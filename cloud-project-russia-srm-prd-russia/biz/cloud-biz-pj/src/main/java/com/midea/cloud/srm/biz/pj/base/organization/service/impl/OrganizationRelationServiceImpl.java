package com.midea.cloud.srm.biz.pj.base.organization.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.midea.cloud.common.constants.SysConstant;
import com.midea.cloud.common.utils.EncryptUtil;
import com.midea.cloud.common.utils.ObjectUtil;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.meiql.api.component.paging.Page;
import com.midea.cloud.srm.biz.pj.base.noitce.mapper.PjNoticeMapper;
import com.midea.cloud.srm.biz.pj.base.organization.service.IOrganizationRelationService;
import com.midea.cloud.srm.model.pj.base.organization.dto.TreeNew;
import com.midea.cloud.srm.model.pj.base.organization.entity.Organization;
import com.midea.cloud.srm.model.pj.base.organization.entity.OrganizationRelation;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenQueryWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author huangbf3
 */
@Slf4j
@Service
public class OrganizationRelationServiceImpl implements IOrganizationRelationService {

    @Resource
    private QlOpenClient qlOpenClient;

    @Resource
    private PjNoticeMapper pjNoticeMapper;
    @Resource
    private RedisUtil redisUtil;
    /**
     * 组织树缓存时间
     */
    public static final String TREE_NEW_CASH_TIME = "TREE_NEW_CASH_TIME";

    /**
     * 组织树缓存数据
     */
    public static final String TREE_NEW_CASH_DATA = "TREE_NEW_CASH_DATA";


    /**
     * 组织树缓存时间
     */
    public static final Long TREE_NEW_CASH_SECONDS = 432000L;

    /**
     * 查询所有组织树
     * @return 所有组织树列表
     */
    private List<TreeNew> findAllTreeNew(){
        Page page = new Page();
        page.setPageNum(1);
        page.setPageSize(5000);
        List<TreeNew> treeNews = new ArrayList<>();
        QlOpenQueryWrapper queryWrapper = QlOpenWrappers.query("Organization");
        Page<TreeNew> orgPageResult = qlOpenClient.query(ContextPath.BASE, queryWrapper, (long) page.getPageNum(), (long) page.getPageSize(), TreeNew.class);

        treeNews.addAll(orgPageResult.getRecords());
        while (orgPageResult.getRecords()!=null&&orgPageResult.getRecords().size()>0
                &&orgPageResult.getTotal()>page.getPageSize()*page.getPageNum()){
            page.setPageNum(page.getPageNum()+1);
            orgPageResult = qlOpenClient.query(ContextPath.BASE, queryWrapper, (long) page.getPageNum(), (long) page.getPageSize(), TreeNew.class);
            treeNews.addAll(orgPageResult.getRecords());
        }
        return treeNews;
    }

    /**
     * 查询所有组织关系数据
     * @return 所有组织关系列表
     */
    private List<OrganizationRelation> findAllOrganizationRelation(){
        Page page = new Page();
        page.setPageNum(1);
        page.setPageSize(5000);
        List<OrganizationRelation> organizationRelations = new ArrayList<>();
        QlOpenQueryWrapper queryWrapper = QlOpenWrappers.query("OrganizationRelation");
        Page<OrganizationRelation> orgRelResult = qlOpenClient.query(ContextPath.BASE, queryWrapper, (long) page.getPageNum(), (long) page.getPageSize(), OrganizationRelation.class);

        organizationRelations.addAll(orgRelResult.getRecords());
        while (orgRelResult.getRecords()!=null&&orgRelResult.getRecords().size()>0
                &&orgRelResult.getTotal()>page.getPageSize()*page.getPageNum()){
            page.setPageNum(page.getPageNum()+1);
            orgRelResult = qlOpenClient.query(ContextPath.BASE, queryWrapper, (long) page.getPageNum(), (long) page.getPageSize(), OrganizationRelation.class);
            organizationRelations.addAll(orgRelResult.getRecords());
        }
        return organizationRelations;
    }

    /**
     * 查询组织树鑫方法
     * @param organizationRelation
     * @return
     */
    @Override
    public List<TreeNew> assembleTreeByParentNew(TreeNew organizationRelation) {
        Long cashTime = redisUtil.get(TREE_NEW_CASH_TIME);
        Date date = pjNoticeMapper.getOrgRelLastUpdateDate();
        if(cashTime!=null&&date!=null){
            if(cashTime>date.getTime()){
                String cashData = redisUtil.get(TREE_NEW_CASH_DATA);
                return JSONArray.parseArray(cashData,TreeNew.class);
            }
        }
        log.info(date+"");
        List<OrganizationRelation> organizationRelationList = findAllOrganizationRelation();
        Map<Long,List<OrganizationRelation>> orgIdParentIdListMap = organizationRelationList.stream().collect(Collectors.groupingBy(OrganizationRelation::getOrganizationId));
        List<TreeNew> endList = findAllTreeNew();
        for (TreeNew relation : endList) {
            if(orgIdParentIdListMap.containsKey(relation.getOrganizationId())){
                OrganizationRelation organizationRelation1 = orgIdParentIdListMap.get(relation.getOrganizationId()).get(0);
                relation.setParentOrganizationId(organizationRelation1.getParentOrganizationId());
                relation.setRelId(organizationRelation1.getRelId());
            }
        }
        if (organizationRelation.getParentOrganizationId() == null) {
            organizationRelation.setParentOrganizationId(SysConstant.TREE_PARENT_ID);
            Long parentId = organizationRelation.getOrganizationId();
            setFullPathId(organizationRelation, String.valueOf(organizationRelation.getParentOrganizationId()), parentId);
        }
        Map<Long,List<TreeNew>> treeMap = endList.stream().filter(item->Objects.nonNull(item.getParentOrganizationId())).collect(Collectors.groupingBy(TreeNew::getParentOrganizationId));
        List<TreeNew> treeNews = assembleNew(treeMap, organizationRelation.getParentOrganizationId(), String.valueOf(organizationRelation.getParentOrganizationId()));
        redisUtil.set(TREE_NEW_CASH_TIME,System.currentTimeMillis(),TREE_NEW_CASH_SECONDS);
        redisUtil.set(TREE_NEW_CASH_DATA,JSONArray.toJSONString(treeNews),TREE_NEW_CASH_SECONDS);
        return treeNews;
    }

    private String setFullPathId(TreeNew organizationRelation, String parentSignId, Long currentId) {
        String md5ParentOrganizationId = EncryptUtil.getMD5(parentSignId + currentId);
        organizationRelation.setFullPathId(md5ParentOrganizationId);
        return md5ParentOrganizationId;
    }

    private List<TreeNew> assembleNew(Map<Long,List<TreeNew>> treeMap, Long parentOrganizationId, String parentSignId) {
        List<TreeNew> endPointOrganizationRelationTree = new ArrayList<>();
        // 保存当前节点的组织信息
        List<TreeNew> organizationRelations = treeMap.get(parentOrganizationId);
        if(organizationRelations!=null){
            organizationRelations.forEach(organizationRelation -> {
                Long parentId = organizationRelation.getOrganizationId();
                //添加子节点全路径唯一ID
                String currentSignId = setFullPathId(organizationRelation, parentSignId, parentId);
                // 添加子节点组织信息
                List<TreeNew> childOrganizationRelation = assembleNew(treeMap,parentId, currentSignId);
                childOrganizationRelation = (List<TreeNew>) ObjectUtil.deepCopy(childOrganizationRelation);
                organizationRelation.setChildOrganRelation(childOrganizationRelation);
                organizationRelation = (TreeNew) ObjectUtil.deepCopy(organizationRelation);
                endPointOrganizationRelationTree.add(organizationRelation);
            });
        }
        return endPointOrganizationRelationTree;
    }

    /**
     * 查询集团-板块-公司结构
     */
    @Override
    public List<TreeNew> treeNewAllGroupBuOu() {
        // 1: 查询集团/板块/公司类型的组织
        List<TreeNew> groupBuOuList; {
            List<String> code = new ArrayList<>();
            code.add("GROUP");
            code.add("BU");
            code.add("OU");
            groupBuOuList = qlOpenClient.query(ContextPath.BASE, QlOpenWrappers.query("Organization").in(Organization::getOrganizationTypeCode, code), TreeNew.class);
        }

        // 2: 查询组织关系
        List<OrganizationRelation> organizationRelationList; {
            Function<List<Long>, Collection<OrganizationRelation>> groupFunction = (a) -> qlOpenClient.query(ContextPath.BASE, QlOpenWrappers.query("OrganizationRelation")
                    .in(OrganizationRelation::getOrganizationId, a), OrganizationRelation.class);
            organizationRelationList = com.midea.cloud.common.utils.CollectionUtils.getListResultByGroup(
                    groupBuOuList.stream().map(TreeNew::getOrganizationId).collect(Collectors.toList()),
                    500, groupFunction);
        }
        // 3: 搜集组织父子关系
        Map<Long/* orgId(父) */, Set<Long/* orgId(子) */>> orgFatherChildMap = organizationRelationList.stream()
                .filter(e -> e.getParentOrganizationId() != null)
                .collect(Collectors.groupingBy(OrganizationRelation::getParentOrganizationId, Collectors.mapping(OrganizationRelation::getOrganizationId, Collectors.toSet())));
        // 4: 遍历组织集合，建立父子联系
        Map<Long/* orgId */, TreeNew> groupBuOuMap = groupBuOuList.stream().collect(Collectors.toMap(TreeNew::getOrganizationId, Function.identity()));
        for (TreeNew org : groupBuOuList) {
            org.setChildOrganRelation(new ArrayList<>(20));
            Set<Long/* orgId(子) */> childOrgIds = orgFatherChildMap.get(org.getOrganizationId());
            if (CollectionUtils.isNotEmpty(childOrgIds)) {
                childOrgIds.forEach(childOrgId -> {
                    TreeNew childOrg = groupBuOuMap.get(childOrgId);
                    if (childOrg != null) {
                        boolean matchParentChild = ("GROUP".equals(org.getOrganizationTypeCode()) && "BU".equals(childOrg.getOrganizationTypeCode()))
                                || ("BU".equals(org.getOrganizationTypeCode()) && "OU".equals(childOrg.getOrganizationTypeCode()));
                        if (matchParentChild) {
                            org.getChildOrganRelation().add(childOrg);

                            childOrg.setParentOrganizationId(org.getOrganizationId());
                        }
                    }
                });
            }
        }
        groupBuOuList.forEach(org -> {
            org.setFullPathId(org.getOrganizationId().toString());
        });

        return groupBuOuList.stream().filter(e -> "GROUP".equals(e.getOrganizationTypeCode())).collect(Collectors.toList());
    }

}
