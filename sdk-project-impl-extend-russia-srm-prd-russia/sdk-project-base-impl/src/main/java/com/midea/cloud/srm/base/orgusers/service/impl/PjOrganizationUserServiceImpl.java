package com.midea.cloud.srm.base.orgusers.service.impl;

import com.alibaba.fastjson.JSON;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.base.orgusers.mapper.OrgUserMapper;
import com.midea.cloud.srm.base.orgusers.service.PjOrganizationUserService;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.enums.OrganizationUserSourceEnum;
import com.midea.cloud.srm.model.base.organization.entity.OrganizationRelation;
import com.midea.cloud.srm.model.base.organization.entity.OrganizationUser;
import com.midea.cloud.srm.model.base.pjorganizationusers.dto.PjOrganizationUserDto;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 备注
 * @author huangbf3
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class PjOrganizationUserServiceImpl extends BaseServiceImpl<OrgUserMapper, OrganizationUser> implements PjOrganizationUserService {

    @Autowired
    private QlService qlService;

    @Autowired
    private BaseClient baseClient;

    @Override
    public Long autoAuthorizationOrganizationUser(List<PjOrganizationUserDto> pjOrganizationUserDtoList) {
        if(CollectionUtils.isEmpty(pjOrganizationUserDtoList)) {
            return SrmConstant.LONG_MINUS_ONE;
        }
        List<Long> userIdList = pjOrganizationUserDtoList.stream().map(PjOrganizationUserDto::getUserId).distinct().collect(Collectors.toList());

        //查询组织架构树
        List<OrganizationRelation> organizationRelationList = baseClient.allTree();
        Map<Long, OrganizationRelation> fullPathIdMap = new HashMap<>(16);
        //组织全路径
        List<String> fullpathList = new ArrayList<>();
        //递归解析组织架构
        getFullPathId(organizationRelationList, fullPathIdMap, ObjectUtils.toString(SrmConstant.LONG_MINUS_ONE, () -> ""), fullpathList);

        //向上、向下展开
        Map<Long, Set<Long>> includeOrgnanizationMap = new HashMap<>(50);
        //匹配目标组织
        Set<Long> targetOrgnizationSet = pjOrganizationUserDtoList.stream().map(PjOrganizationUserDto::getOrganizationId).collect(Collectors.toSet());
        //遍历全路径
        fullpathList.stream().forEach(fullpath -> {
            Set<Long> matchingOrganizationIdSet = Arrays.stream(fullpath.split(SrmConstant.SIG_1)).map(id -> Long.valueOf(id)).filter(id -> Long.compare(SrmConstant.LONG_MINUS_ONE, id) != 0).collect(Collectors.toSet());
            Set<Long> addToMapSet = matchingOrganizationId(targetOrgnizationSet, matchingOrganizationIdSet);
            if(CollectionUtils.isNotEmpty(addToMapSet)) {
                addToMapSet.stream().forEach(id -> {
                    addToMap(includeOrgnanizationMap, id, matchingOrganizationIdSet);
                });
            }
        });

        List<PjOrganizationUserDto> saveList = new ArrayList<>();
        pjOrganizationUserDtoList.stream().forEach(pjOrganizationUserDto -> {
            if(includeOrgnanizationMap.containsKey(pjOrganizationUserDto.getOrganizationId())) {
                //存在组织架构树 && 不等于集团
                includeOrgnanizationMap.get(pjOrganizationUserDto.getOrganizationId()).stream().filter(id -> fullPathIdMap.containsKey(id) && Long.compare(SrmConstant.LONG_MINUS_ONE, fullPathIdMap.get(id).getParentOrganizationId()) != 0).forEach(id -> {
                    saveList.add(buildPjOrganizationUserDto(pjOrganizationUserDto, fullPathIdMap.get(id)));
                });
            }
        });

        //查询数据库中的数据
        List<PjOrganizationUserDto> existList = qlService.queryByWrapper(QlWrappers.query(MqlType.ORGANIZATION_USER).in(PjOrganizationUserDto::getUserId, userIdList), PjOrganizationUserDto.class);
        Map<String, PjOrganizationUserDto> existMap = existList.stream().collect(Collectors.toMap(k -> StringUtils.joinWith(SrmConstant.SIG_1, k.getUserId(), k.getOrganizationId()), Function.identity(), (k1, k2)->k2));
        //移除的组织权限
        Set<Long> removeId = existList.stream().filter(e -> OrganizationUserSourceEnum.HR.name().equals(e.getSourceFrom())).map(e -> e.getOrganizationUserRelId()).collect(Collectors.toSet());

        //需要插入的数据
        List<PjOrganizationUserDto> insertList = new ArrayList<>();
        //需要更新的数据
        List<PjOrganizationUserDto> updateList = new ArrayList<>();

        List<PjOrganizationUserDto> finalInsertList = insertList;
        saveList.stream().forEach(pjOrganizationUserDto -> {
            String key = StringUtils.joinWith(SrmConstant.SIG_1, pjOrganizationUserDto.getUserId(), pjOrganizationUserDto.getOrganizationId());
            if(existMap.containsKey(key)) {
                PjOrganizationUserDto existDto = existMap.get(key);
                //不删除数据
                removeId.remove(existDto.getOrganizationUserRelId());

                //判断授权是否失效
                if(ObjectUtils.defaultIfNull(existDto.getEndDate(), new Date()).before(new Date())) {
                    //重新生效
                    existDto.setEndDate(pjOrganizationUserDto.getEndDate());
                    updateList.add(existDto);
                }
            } else {
                finalInsertList.add(pjOrganizationUserDto);
            }
        });

        //插入
        if(CollectionUtils.isNotEmpty(insertList)) {
            insertList = insertList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(s -> StringUtils.joinWith(SrmConstant.SIG_1, s.getUserId(), s.getOrganizationId())))), ArrayList::new));
            qlService.create(MqlType.ORGANIZATION_USER, insertList);
        }

        //更新
        if(CollectionUtils.isNotEmpty(updateList)) {
            qlService.update(MqlType.ORGANIZATION_USER, updateList);
        }

        //删除
        if(CollectionUtils.isNotEmpty(removeId)) {
            qlService.deleteByKeys(MqlType.ORGANIZATION_USER, new ArrayList<>(removeId));
        }
        return userIdList.stream().max(Comparator.comparingLong(s->s)).get();
    }

    /**
     * 构造组织权限实体
     * @param pjOrganizationUserDto
     * @param organizationRelation
     * @return
     */
    protected PjOrganizationUserDto buildPjOrganizationUserDto(PjOrganizationUserDto pjOrganizationUserDto, OrganizationRelation organizationRelation) {
        PjOrganizationUserDto dto = new PjOrganizationUserDto();
        dto.setUserId(pjOrganizationUserDto.getUserId());
        dto.setOrganizationId(organizationRelation.getOrganizationId());
        dto.setSourceFrom(OrganizationUserSourceEnum.HR.name());
        dto.setStartDate(new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 2);
        dto.setEndDate(calendar.getTime());
        dto.setFullPathId(organizationRelation.getFullPathId());
        return dto;
    }

    /**
     * 添加值
     * @param targetMap
     * @param key
     * @param value
     */
    protected void addToMap(Map<Long, Set<Long>> targetMap, Long key, Set<Long> value) {
        if(!targetMap.containsKey(key)) {
            targetMap.put(key, new HashSet<>());
        }
        if(CollectionUtils.isNotEmpty(value)) {
            targetMap.get(key).addAll(value);
        }
    }

    /**
     * 匹配交集
     * @param targetIdSet
     * @param matchIdSet
     * @return
     */
    protected Set<Long> matchingOrganizationId(Set<Long> targetIdSet, Set<Long> matchIdSet) {
        Set<Long> set1 = new HashSet<>(targetIdSet);
        Set<Long> set2 = new HashSet<>(matchIdSet);
        set1.retainAll(set2);
        return set1;
    }

    protected void getFullPathId(List<OrganizationRelation> organizationRelationList, Map<Long, OrganizationRelation> fullPathIdMap, String path, List<String> fullPathList) {
        for (OrganizationRelation organizationRelation : organizationRelationList) {
            fullPathIdMap.put(organizationRelation.getOrganizationId(), organizationRelation);
            String currentPath = StringUtils.joinWith(SrmConstant.SIG_1, path, organizationRelation.getOrganizationId());
            if (CollectionUtils.isNotEmpty(organizationRelation.getChildOrganRelation())) {
                getFullPathId(organizationRelation.getChildOrganRelation(), fullPathIdMap, currentPath, fullPathList);
            } else {
                fullPathList.add(currentPath);
            }
        }
    }
}
