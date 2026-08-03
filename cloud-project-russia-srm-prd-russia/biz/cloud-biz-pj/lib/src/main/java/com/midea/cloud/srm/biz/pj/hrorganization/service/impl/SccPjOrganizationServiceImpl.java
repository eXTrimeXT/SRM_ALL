package com.midea.cloud.srm.biz.pj.hrorganization.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.biz.pj.hrorganization.mapper.SccPjOrganizationMapper;
import com.midea.cloud.srm.biz.pj.hrorganization.service.SccPjOrganizationService;
import com.midea.cloud.srm.biz.pj.hrorganizationtemp.mapper.SccPjOrganizationTempMapper;
import com.midea.cloud.srm.model.pj.hrorganization.SccPjOrganization;
import com.midea.cloud.srm.model.pj.hrorganizationtemp.SccPjOrganizationTemp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author huangbf3
 */
@Slf4j
@Service
public class SccPjOrganizationServiceImpl extends ServiceImpl<SccPjOrganizationMapper, SccPjOrganization> implements SccPjOrganizationService {

    @Autowired
    private SccPjOrganizationTempMapper sccPjOrganizationTempMapper;

    private static Map<Integer, String> LEVEL_TYPE = new HashMap<>(50);

    static {
        LEVEL_TYPE.put(1, "GROUP");
        LEVEL_TYPE.put(2, "BU");
        LEVEL_TYPE.put(3, "COMPANY");
        LEVEL_TYPE.put(4, "OU");
        LEVEL_TYPE.put(5, "ORG");
    }

    @Override
    public List<SccPjOrganization> toSccPjOrganization(List<SccPjOrganizationTemp> sccPjOrganizationTempList) {

        LambdaQueryWrapper<SccPjOrganization> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SccPjOrganization::getId, sccPjOrganizationTempList.stream().map(SccPjOrganizationTemp::getId).distinct().collect(Collectors.toList()));
        List<SccPjOrganization> sccPjOrganizationList = this.list(queryWrapper);

        Map<Long, SccPjOrganization> organizationMap = sccPjOrganizationList.stream().collect(Collectors.toMap(k->k.getId(), Function.identity(), (k1, k2)->k2));

        //设置类型和ID
        List<SccPjOrganization> saveList = new ArrayList<>();
        sccPjOrganizationTempList.stream().forEach(sccPjOrganizationTemp -> {
            SccPjOrganization sccPjOrganization = new SccPjOrganization();
            BeanCopyUtil.copyProperties(sccPjOrganization, sccPjOrganizationTemp);
            if(organizationMap.containsKey(sccPjOrganization.getId())) {
                sccPjOrganization.setOrganizationId(organizationMap.get(sccPjOrganization.getId()).getOrganizationId());
            } else {
                sccPjOrganization.setOrganizationId(sccPjOrganizationTemp.getPreOrganizationId());
            }
            saveList.add(sccPjOrganization);
        });

        //查询父类,有限查同一批次，再查业务表
        Map<Long, SccPjOrganizationTemp> parent = queryParent(sccPjOrganizationTempList);

        saveList.stream().forEach(sccPjOrganization -> {
            List<SccPjOrganizationTemp> path = new ArrayList<>();
            SccPjOrganizationTemp sccPjOrganizationTemp = new SccPjOrganizationTemp();
            BeanCopyUtil.copyProperties(sccPjOrganizationTemp, sccPjOrganization);
            sccPjOrganizationTemp.setPreOrganizationId(sccPjOrganization.getOrganizationId());
            path.add(sccPjOrganizationTemp);
            SccPjOrganizationTemp curentPath = sccPjOrganizationTemp;
            while (true) {
                Long  id = curentPath.getParentId();
                SccPjOrganizationTemp nextPath = parent.get(id);
                if(Objects.isNull(nextPath)) {
                    break;
                }
                path.add(nextPath);
                curentPath = nextPath;
            }

            Collections.reverse(path);
            for(int i = 0; i < path.size(); i++) {
                SccPjOrganizationTemp pjOrganization = path.get(i);
//                pjOrganization.setOrganizationTypeCode(LEVEL_TYPE.getOrDefault(i+1, LEVEL_TYPE.get(5)));
                pjOrganization.setOrganizationTypeCode(dealOrganizationCode(pjOrganization.getGrade()));
//                pjOrganization.setOrganizationCode(StringUtils.joinWith("_", "HR", pjOrganization.getOrganizationTypeCode(), pjOrganization.getId()));
                pjOrganization.setOrganizationCode(StringUtils.joinWith("_", "HR", pjOrganization.getId()));
                if(i == 0) {
                    pjOrganization.setParentOrganizationId(-1L);
                    pjOrganization.setOrgIdPath(pjOrganization.getPreOrganizationId().toString());
                    pjOrganization.setIamParentId(0L);
                    pjOrganization.setOrgNamePath( pjOrganization.getGroupName());
                    pjOrganization.setOrganizationPath(pjOrganization.getOrganizationCode());
                    pjOrganization.setOrgStatusPath( "0");
                } else {
                    pjOrganization.setParentOrganizationId(path.get(i-1).getPreOrganizationId());
                    //与上一级不同类型时
                    if(!pjOrganization.getOrganizationTypeCode().equals(path.get(i-1).getOrganizationTypeCode())) {
                        pjOrganization.setIamParentId(0L);
                    } else {
                        pjOrganization.setIamParentId(pjOrganization.getParentOrganizationId());
                    }
                    //父类路径
                    pjOrganization.setOrgIdPath(StringUtils.joinWith("/", path.get(i-1).getOrgIdPath(), pjOrganization.getPreOrganizationId()));
                    pjOrganization.setOrgNamePath(StringUtils.joinWith("/", path.get(i-1).getOrgNamePath(), pjOrganization.getGroupName()));
                    pjOrganization.setOrganizationPath(StringUtils.joinWith("/", path.get(i-1).getOrganizationPath(), pjOrganization.getOrganizationCode()));
                    pjOrganization.setOrgStatusPath(StringUtils.joinWith("", path.get(i-1).getOrgStatusPath(), "0"));
                }
            }

            SccPjOrganizationTemp pathLast = path.get(path.size() - 1);

            sccPjOrganization.setOrganizationCode(pathLast.getOrganizationCode());
            sccPjOrganization.setOrganizationTypeCode(pathLast.getOrganizationTypeCode());
            sccPjOrganization.setParentOrganizationId(pathLast.getParentOrganizationId());
            sccPjOrganization.setIamParentId(pathLast.getIamParentId());
            sccPjOrganization.setOrgIdPath(pathLast.getOrgIdPath());
            sccPjOrganization.setOrgNamePath( pathLast.getOrgNamePath());
            sccPjOrganization.setOrganizationPath(pathLast.getOrganizationPath());
            sccPjOrganization.setOrgStatusPath( pathLast.getOrgStatusPath());
        });


        //保存数据
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SccPjOrganization::getId, sccPjOrganizationTempList.stream().map(SccPjOrganizationTemp::getId).distinct().collect(Collectors.toList()));
        sccPjOrganizationList = this.list(queryWrapper);

        Map<String, SccPjOrganization> sccPjOrganizationMap = sccPjOrganizationList.stream().collect(Collectors.toMap(k -> StringUtils.joinWith("_", k.getId(), k.getParentId()), Function.identity(), (k1, k2)->k2));

        Map<String, SccPjOrganization> saveMap = new HashMap<>(50);
        saveList.stream().forEach(sccPjOrganization -> {
            String key = StringUtils.joinWith("_", sccPjOrganization.getId(), sccPjOrganization.getParentId());
            SccPjOrganization exists = sccPjOrganizationMap.get(key);
            if(Objects.isNull(exists)) {
                sccPjOrganization.setRowId(IdGenrator.generate());
            } else {
                sccPjOrganization.setRowId(exists.getRowId());
            }
            saveMap.put(key, sccPjOrganization);
        });

        List<SccPjOrganization> list = new ArrayList<>(saveMap.values());
        Set<String> codeSet = list.stream().map(SccPjOrganization::getOrganizationCode).collect(Collectors.toSet());
        LambdaQueryWrapper<SccPjOrganization> codeQw = new LambdaQueryWrapper<>();
        codeQw.in(SccPjOrganization::getOrganizationCode, codeSet);
        List<SccPjOrganization> codeList = this.list(codeQw);
        if (CollectionUtils.isNotEmpty(codeList)) {
            Set<String> set = codeList.stream().map(SccPjOrganization::getOrganizationCode).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(set)) {
                list.removeIf(e -> set.contains(e.getOrganizationCode()));
            }
        }
        this.saveOrUpdateBatch(list);
        return list;
    }

    private static String dealOrganizationCode(String str) {
        if(StringUtils.isBlank(str)) {
            return LEVEL_TYPE.get(5);
        }
        switch (str) {
            case "1":
                return LEVEL_TYPE.get(1);
            case "10":
                 return LEVEL_TYPE.get(4);
            case "20":
            case "30":
                return "DEP";
            default:
                return LEVEL_TYPE.get(5);
        }
    }
    private Map<Long, SccPjOrganizationTemp> queryParent(List<SccPjOrganizationTemp> sccPjOrganizationTempList) {

        //记录父类组织ID缓存
        Map<Long, SccPjOrganizationTemp> parent = new HashMap<>(50);
        //限定同一批次
        Long processGroupId = sccPjOrganizationTempList.get(0).getProcessGroupId();
        List<Long> parentIdList = sccPjOrganizationTempList.stream().map(SccPjOrganizationTemp::getParentId).distinct().collect(Collectors.toList());

        while (true) {
            //查询临时表父类组织
            LambdaQueryWrapper<SccPjOrganizationTemp> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(SccPjOrganizationTemp::getProcessGroupId, processGroupId);
            queryWrapper.in(SccPjOrganizationTemp::getId, parentIdList);
            List<SccPjOrganizationTemp> sccPjOrganizationTemps = sccPjOrganizationTempMapper.selectList(queryWrapper);

            //查询业务表父类组织ID
            LambdaQueryWrapper<SccPjOrganization> idQueryWrapper = new LambdaQueryWrapper<>();
            idQueryWrapper.in(SccPjOrganization::getId, parentIdList);
            List<SccPjOrganization> sccPjOrganizationList = this.list(idQueryWrapper);

            //清空父类组织ID
            parentIdList = new ArrayList<>();

            if(CollectionUtils.isNotEmpty(sccPjOrganizationList)) {
                //以业务表父类组织ID为准
                sccPjOrganizationList.stream().forEach(sccPjOrganization -> {
                    SccPjOrganizationTemp sccPjOrganizationTemp = new SccPjOrganizationTemp();
                    BeanCopyUtil.copyProperties(sccPjOrganizationTemp, sccPjOrganization);
                    sccPjOrganizationTemp.setPreOrganizationId(sccPjOrganization.getOrganizationId());
                    parent.put(sccPjOrganizationTemp.getId(), sccPjOrganizationTemp);
                });
                parentIdList.addAll(sccPjOrganizationList.stream().map(o -> o.getParentId()).distinct().collect(Collectors.toList()));
            }

            if(CollectionUtils.isNotEmpty(sccPjOrganizationTemps)) {
                //已临时表父类组织ID为补充
                sccPjOrganizationTemps.stream().filter(o -> !parent.containsKey(o.getId())).forEach(sccPjOrganizationTemp -> {
                    parent.put(sccPjOrganizationTemp.getId(), sccPjOrganizationTemp);
                });
                parentIdList.addAll(sccPjOrganizationTemps.stream().map(o -> o.getParentId()).distinct().collect(Collectors.toList()));
                parentIdList = parentIdList.stream().distinct().collect(Collectors.toList());
            }

            if(CollectionUtils.isEmpty(parentIdList) || (parentIdList.size() == 1 && Long.compare(0L, parentIdList.get(0)) == 0)) {
                break;
            }
        }
        return parent;
    }
}
