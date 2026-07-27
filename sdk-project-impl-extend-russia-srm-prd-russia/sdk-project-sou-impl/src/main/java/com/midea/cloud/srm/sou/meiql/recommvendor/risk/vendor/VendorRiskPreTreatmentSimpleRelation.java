package com.midea.cloud.srm.sou.meiql.recommvendor.risk.vendor;

import com.alibaba.fastjson.JSON;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorDto;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sup.association.entity.ExtSupAssociation;
import com.midea.cloud.srm.model.supplier.bpm.dto.ContactInfoDto;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenQueryWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.abstracts.AbstractRiskPretreatment;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.compent.RiskComponent;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.pojo.RiskRequest;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.pojo.RiskResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: for srm 关联关系和黑名单---关联关系简单版
 *
 * @author srm
 * @date 2024-05-18
 */
@Slf4j
public class VendorRiskPreTreatmentSimpleRelation extends AbstractRiskPretreatment {
    @Override
    public RiskResponse todo(RiskRequest riskRequest) {
        log.info("riskService VendorRiskPreTreatmentRelation start...");
        //关联关系缓存Map对象，key-value，key为供应商名称，value为Set集合，集合元素为包括本身供应商名称在内的关联供应商名称，当只有本身供应商名称一个元素时，表明无关联供应商
        Map<String, Set<String>> relationMap = new HashMap<>(50);
        //供应商信息缓存Map对象，key-value，key为供应商名称，value为供应商信息，Object对应的class为RecordDTO
        Map<String, Object> relCompanyInfoMap = new HashMap<>(50);
        //供应商联系人缓存Map对象，key-value，key为供应商名称，value为供应商联系人信息列表
        Map<String, List<ContactInfoDto>> relCompanyContactInfoMap = new HashMap<>(50);

        //判断是否存在供应商风险底表数据
        if(CollectionUtils.isNotEmpty(riskRequest.getVendorRiskList())) {
            //取出供应商风险底表数据列表
            List<RecommvendorDto> recommvendorDtoList = riskRequest.getVendorRiskList();
            //从供应商风险底表数据列表获取供应商名称
            List<String> vendorNameList = recommvendorDtoList.stream().map(r -> r.getVendorName()).distinct().collect(Collectors.toList());

            //初始化relationMap，将供应商名称写入key-value中，key为供应商名字，value为Set结合，并初始化第一个元素为本身供应商名称
            vendorNameList.stream().forEach(vendorName -> relationMap.put(vendorName, new HashSet<>(Arrays.asList(vendorName))));

            //供应商名称查询条件，循环查询，不断击穿关联供应商名字进行查询
            List<String> queryVendorNameList = new ArrayList<>(vendorNameList);

            /** A供应商作为查询条件查询关联供应商 */
            //以A供应商名称查询关联关系表
            List<ExtSupAssociation> nameAassociationsList = RiskComponent.getInstance().getQlOpenClient().query(ContextPath.SUP, QlOpenWrappers.query(MqlType.SOU_RELATION_SUP_BUYER)
                    .select(ExtSupAssociation::getVendorNameA, ExtSupAssociation::getVendorNameB)
                    .in(ExtSupAssociation::getVendorNameA, queryVendorNameList), ExtSupAssociation.class);

            //判断是否存在关联关系
            if(CollectionUtils.isNotEmpty(nameAassociationsList)) {
                //存在关联供应商，那么A供应商作为本供应商，B供应商作为关联供应商，并将关联关系写入缓存对象relationMap
                appendSimpleRelationMap(relationMap, nameAassociationsList.stream().collect(Collectors.toMap(k -> k.getVendorNameA(),v -> v.getVendorNameB(), (k1, k2) -> k2)));
            }

            /** B供应商作为查询条件查询关联供应商 */
            List<ExtSupAssociation> nameBassociationsList = RiskComponent.getInstance().getQlOpenClient().query(ContextPath.SUP, QlOpenWrappers.query(MqlType.SOU_RELATION_SUP_BUYER)
                    .select(ExtSupAssociation::getVendorNameA, ExtSupAssociation::getVendorNameB)
                    .in(ExtSupAssociation::getVendorNameB, queryVendorNameList), ExtSupAssociation.class);
            //判断是否存在关联关系
            if(CollectionUtils.isNotEmpty(nameBassociationsList)) {
                //存在关联供应商，那么B供应商作为本供应商，A供应商作为关联供应商，并将关联关系写入缓存对象relationMap
                appendSimpleRelationMap(relationMap, nameAassociationsList.stream().collect(Collectors.toMap(k -> k.getVendorNameB(),v -> v.getVendorNameA(), (k1, k2) -> k2)));
            }

        }
        //返回数据对象，key-value，key为变量名，value为上述缓存对象。
        Map<String, Object> data = new HashMap<>(50);
        //返回缓存对象，关联关系缓存Map对象，key-value，key为供应商名称，value为Set集合，集合元素为包括本身供应商名称在内的关联供应商名称，当只有本身供应商名称一个元素时，表明无关联供应商
        data.put("relationMap", relationMap);
        //返回缓存对象，供应商信息缓存Map对象，key-value，key为供应商名称，value为供应商信息，Object对应的class为RecordDTO
        data.put("relCompanyInfoMap", relCompanyInfoMap);
        //返回缓存对象，供应商联系人缓存Map对象，key-value，key为供应商名称，value为供应商联系人信息列表
        data.put("relCompanyContactInfoMap", relCompanyContactInfoMap);
        log.info("riskService VendorRiskPreTreatmentRelation end...");
        return new RiskResponse(data);
    }

    private void appendSimpleRelationMap(Map<String, Set<String>> relationMap, Map<String, String> relMap) {
        for(String key : relMap.keySet()) {
            //关联关系写入缓存
            relationMap.getOrDefault(key, new HashSet<>(16)).addAll(new ArrayList<>(Arrays.asList(key, relMap.get(key))));
        }
    }



    private void vendorRelationCompanyInfo(Map<String, Set<String>> relationMap, Map<String, Object>relCompanyInfoMap, Map<String, List<ContactInfoDto>>relCompanyContactInfoMap) {

        Set<String> allvendorNameSet = new HashSet<>(50);

        for(Set<String> value : relationMap.values()) {
            allvendorNameSet.addAll(new HashSet<>(value));
        }

        List<RecordDTO> companyInfoList = null;
        if(CollectionUtils.isNotEmpty(allvendorNameSet)) {
            QlOpenQueryWrapper qlOpenQueryWrapper = QlOpenWrappers.query(MqlType.SUPPLIER).in(CompanyInfo::getCompanyName, new ArrayList<>(allvendorNameSet));
            companyInfoList = RiskComponent.getInstance().getQlOpenClient().query(ContextPath.SUP, qlOpenQueryWrapper);
        }
        /** 供应商联系人 */
        if(CollectionUtils.isNotEmpty(companyInfoList)) {
            List<ContactInfoDto> vendorContactList = RiskComponent.getInstance().getQlOpenClient().query(ContextPath.SUP, QlOpenWrappers.query(MqlType.CONTACTINFO).in(ContactInfoDto::getCompanyId, companyInfoList.stream().map(c->c.get(CompanyInfo::getCompanyId)).collect(Collectors.toList())), ContactInfoDto.class);
            Map<Long, RecordDTO> companyInfoMap = new HashMap<>(15);
            companyInfoList.stream().forEach(c -> {
                relCompanyInfoMap.put(c.get(CompanyInfo::getCompanyName), c);
                companyInfoMap.put(c.get(CompanyInfo::getCompanyId), c);
            });
            if(CollectionUtils.isNotEmpty(vendorContactList)) {
                vendorContactList.stream().forEach(c -> {
                    RecordDTO companyInfo = companyInfoMap.get(c.getCompanyId());
                    String companyName = companyInfo.get(CompanyInfo::getCompanyName);
                    if(!relCompanyContactInfoMap.containsKey(companyName)) {
                        relCompanyContactInfoMap.put(companyName, new ArrayList<>());
                    }
                    relCompanyContactInfoMap.get(companyName).add(c);
                });
            }
        }
    }

    /**
     * 关联键
     * @param nameA
     * @param nameB
     * @return
     */
    public static String assocationKey(String nameA, String nameB) {
        return StringUtils.joinWith(SrmConstant.UNDER_LINE, assocationKey(nameA), assocationKey(nameB));
    }

    private static String assocationKey(String name) {
        return StringUtils.joinWith("", SrmConstant.SIG_4, SrmConstant.LEFT_BRACE, name, SrmConstant.RIGHT_BRACE);
    }

    /**
     * 关联键解码
     * @param assocationKey
     * @return
     */
    public static String[] decodeAssocationKey(String assocationKey) {
        String[] decode = assocationKey.split(StringUtils.joinWith("", SrmConstant.RIGHT_BRACE, SrmConstant.UNDER_LINE, SrmConstant.SIG_4, SrmConstant.LEFT_BRACE));
        decode[0] = decode[0].substring(2);
        decode[1] = decode[1].substring(0, decode[1].length()-1);
        return decode;
    }

    private void appendRelationMap(Map<String, Set<String>> relationMap, Set<String> assocationKeySet) {
        /** 关联KEY */
        Map<String, Set<String>> assocationKey = new HashMap<>(50);
        /** 反向关联KEY */
        Map<String, Set<String>> reverseAssocationKey = new HashMap<>(50);

        assocationKeySet.forEach(key -> {
            String[] relationName = decodeAssocationKey(key);
            appendMapValue(assocationKey, relationName[0], relationName[1]);
            appendMapValue(reverseAssocationKey, relationName[1], relationName[0]);
        });

        /** 关联对比KEY */
        Set<String> compareKeySet = new HashSet<>(assocationKey.keySet());
        /** 反向关联对比KEY */
        Set<String> reverseCompareKeySet = new HashSet<>(reverseAssocationKey.keySet());
        for(String key : relationMap.keySet()) {
            /** 关联键 */
            Set<String> relKey = new HashSet<>(relationMap.get(key));
            /** 反向关联键 */
            Set<String> reverseRelKey = new HashSet<>(relationMap.get(key));
            relKey.retainAll(compareKeySet);
            reverseRelKey.retainAll(reverseCompareKeySet);
            /** 正向合并 */
            merge(relationMap.get(key), relKey, assocationKey);
            /** 反向合并 */
            merge(relationMap.get(key), reverseRelKey, reverseAssocationKey);
        }
    }

    private void merge(Set<String> value, Set<String> relKey, Map<String, Set<String>> relMap) {
        if(CollectionUtils.isNotEmpty(relKey)) {
            relKey.forEach(key -> value.addAll(relMap.get(key)));
        }
    }

    private void mergeAll(Map<String, Set<String>> relationMap) {
        List<String> keySet = new ArrayList<>(relationMap.keySet());
        for(int i = 0 ; i < keySet.size(); i++) {
            String key = keySet.get(0);
            Set<String> value = relationMap.get(key);
            keySet.stream().filter(otherKey -> !otherKey.equals(key)).forEach(otherKey -> {
                Set<String> otherValue = relationMap.get(otherKey);

                Set<String> compare = new HashSet<>(value);
                compare.retainAll(otherValue);

                if(CollectionUtils.isNotEmpty(compare)) {
                    //两两关联
                    value.addAll(otherValue);
                    otherValue.addAll(value);
                }
            });
        }
    }

    public static void main(String[] args) {
        List<String> vendorNameList = new ArrayList<>();
        vendorNameList.add("A");
        vendorNameList.add("B");
        vendorNameList.add("C");
        Map<String, Set<String>> relationMap = new HashMap<>(50);
        vendorNameList.stream().forEach(vendorName -> relationMap.put(vendorName, new HashSet<>(Arrays.asList(vendorName))));

        VendorRiskPreTreatmentSimpleRelation relation = new VendorRiskPreTreatmentSimpleRelation();
        List<ExtSupAssociation> nameAassociationsList = new ArrayList<>();
        nameAassociationsList.add(new ExtSupAssociation());
        nameAassociationsList.add(new ExtSupAssociation());
        nameAassociationsList.add(new ExtSupAssociation());
        nameAassociationsList.add(new ExtSupAssociation());
        nameAassociationsList.get(0).setVendorNameA("A");
        nameAassociationsList.get(0).setVendorNameB("E");
        nameAassociationsList.get(1).setVendorNameA("A");
        nameAassociationsList.get(1).setVendorNameB("F");
        nameAassociationsList.get(2).setVendorNameA("B");
        nameAassociationsList.get(2).setVendorNameB("X");
        nameAassociationsList.get(3).setVendorNameA("B");
        nameAassociationsList.get(3).setVendorNameB("Y");
        relation.appendRelationMap(relationMap, nameAassociationsList.stream().map(o -> assocationKey(o.getVendorNameA(), o.getVendorNameB())).collect(Collectors.toSet()));


        List<ExtSupAssociation> nameBassociationsList = new ArrayList<>();
        nameBassociationsList.add(new ExtSupAssociation());
        nameBassociationsList.add(new ExtSupAssociation());
        nameBassociationsList.get(0).setVendorNameA("X");
        nameBassociationsList.get(0).setVendorNameB("A");
        nameBassociationsList.get(1).setVendorNameA("S");
        nameBassociationsList.get(1).setVendorNameB("B");
        relation.appendRelationMap(relationMap, nameBassociationsList.stream().map(o -> assocationKey(o.getVendorNameB(), o.getVendorNameA())).collect(Collectors.toSet()));

        relation.mergeAll(relationMap);
        JSON.toJSONString(relationMap);


        List<ExtSupAssociation> nameAassociations2List = new ArrayList<>();
        nameAassociations2List.add(new ExtSupAssociation());
        nameAassociations2List.add(new ExtSupAssociation());
        nameAassociations2List.get(0).setVendorNameA("E");
        nameAassociations2List.get(0).setVendorNameB("C");
        nameAassociations2List.get(1).setVendorNameA("F");
        nameAassociations2List.get(1).setVendorNameB("Q");

        relation.appendRelationMap(relationMap, nameAassociations2List.stream().map(o -> assocationKey(o.getVendorNameA(), o.getVendorNameB())).collect(Collectors.toSet()));


        List<ExtSupAssociation> nameBassociations2List = new ArrayList<>();
        nameBassociations2List.add(new ExtSupAssociation());
        nameBassociations2List.add(new ExtSupAssociation());
        nameBassociations2List.get(0).setVendorNameA("E");
        nameBassociations2List.get(0).setVendorNameB("F");
        nameBassociations2List.get(1).setVendorNameA("X");
        nameBassociations2List.get(1).setVendorNameB("F");
        relation.appendRelationMap(relationMap, nameBassociations2List.stream().map(o -> assocationKey(o.getVendorNameB(), o.getVendorNameA())).collect(Collectors.toSet()));

        relation.mergeAll(relationMap);
        JSON.toJSONString(relationMap);
    }

    private void appendMapValue(Map<String, Set<String>> map, String key, String value) {
        if(StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
            return;
        }
        if(!map.containsKey(key)) {
            map.put(key, new HashSet<>());
        }
        map.get(key).add(value);
    }

}
