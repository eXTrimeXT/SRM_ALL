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
 * @Description: for srm 关联关系和黑名单
 *
 * @author srm
 * @date 2024-05-18
 */
@Slf4j
public class VendorRiskPreTreatmentRelation extends AbstractRiskPretreatment {
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

            //供应商名称查询条件，循环查询，不断击穿关联供应商名字进行查询，例如 A->B 关联， B-C 关联，但是未维护A->C关联，循环查询之后会认为A->C关联
            List<String> queryVendorNameList = new ArrayList<>(vendorNameList);
            //已查询过的供应商名称集合，用于排除重复查询，防止死循环查询，例如 A->B关联，也维护了B->A关联，就有可能出现死循环查询
            Set<String> allQueryVendorNameSet = new HashSet<>(50);
            //当供应商名称查询条件列表不为空的时候，进入循环
            while (CollectionUtils.isNotEmpty(queryVendorNameList)) {
                //每次循环，将查询的供应商名称加入到已查询的供应商名称集合里，进入下一次循环时，将会排除掉这个结合中供应商名称
                allQueryVendorNameSet.addAll(queryVendorNameList);
                /** A供应商作为查询条件查询关联供应商 */
                Set<String> queryList = new HashSet<>(50);
                //以A供应商名称查询关联关系表
                List<ExtSupAssociation> nameAassociationsList = RiskComponent.getInstance().getQlOpenClient().query(ContextPath.SUP, QlOpenWrappers.query(MqlType.SOU_RELATION_SUP_BUYER)
                        .select(ExtSupAssociation::getVendorNameA, ExtSupAssociation::getVendorNameB)
                        .in(ExtSupAssociation::getVendorNameA, queryVendorNameList), ExtSupAssociation.class);
                //判断是否存在关联关系
                if(CollectionUtils.isNotEmpty(nameAassociationsList)) {
                    //存在关联供应商，那么A供应商作为本供应商，B供应商作为关联供应商，并将关联关系写入缓存对象relationMap
                    appendRelationMap(relationMap, nameAassociationsList.stream().map(a -> assocationKey(a.getVendorNameA(), a.getVendorNameB())).collect(Collectors.toSet()));
                    //将关联供应商B的名字记录到下一次循环查询的供应商名字集合里
                    queryList.addAll(nameAassociationsList.stream().map(a -> a.getVendorNameB()).collect(Collectors.toSet()));
                }

                /** B供应商作为查询条件查询关联供应商 */
                List<ExtSupAssociation> nameBassociationsList = RiskComponent.getInstance().getQlOpenClient().query(ContextPath.SUP, QlOpenWrappers.query(MqlType.SOU_RELATION_SUP_BUYER)
                        .select(ExtSupAssociation::getVendorNameA, ExtSupAssociation::getVendorNameB)
                        .in(ExtSupAssociation::getVendorNameB, queryVendorNameList), ExtSupAssociation.class);
                //判断是否存在关联关系
                if(CollectionUtils.isNotEmpty(nameBassociationsList)) {
                    //存在关联供应商，那么B供应商作为本供应商，A供应商作为关联供应商，并将关联关系写入缓存对象relationMap
                    appendRelationMap(relationMap, nameBassociationsList.stream().map(a -> assocationKey(a.getVendorNameA(), a.getVendorNameB())).collect(Collectors.toSet()));
                    //将关联供应商A的名字记录到下一次循环查询的供应商名字集合里
                    queryList.addAll(nameBassociationsList.stream().map(a -> a.getVendorNameA()).collect(Collectors.toSet()));
                }

                //下一次循环查询的供应商名称，需要排除已查询过的供应商名称
                queryList.removeAll(allQueryVendorNameSet);
                //将下一次循环查询的供应商名称集合，赋值到变量queryVendorNameList
                queryVendorNameList = new ArrayList<>(queryList);
            }

            //根据关联关系缓存对象relationMap，查询供应商信息以及供应商联系人信息，分别缓存到对象relCompanyInfoMap和relCompanyContactInfoMap中
            vendorRelationCompanyInfo(relationMap, relCompanyInfoMap, relCompanyContactInfoMap);
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

    /**
     * 查询关联供应商信息
     * @param relationMap
     * @param relCompanyInfoMap
     * @param relCompanyContactInfoMap
     */
    private void vendorRelationCompanyInfo(Map<String, Set<String>> relationMap, Map<String, Object>relCompanyInfoMap, Map<String, List<ContactInfoDto>>relCompanyContactInfoMap) {

        //所有涉及的供应商名称集合
        Set<String> allvendorNameSet = new HashSet<>(50);

        //从缓存对象中加载所有的供应商名称
        for(Set<String> value : relationMap.values()) {
            //将每一个缓存对象的value供应商名称集合添加到总的供应商名称集合对象中
            allvendorNameSet.addAll(new HashSet<>(value));
        }

        //供应商信息列表定义
        List<RecordDTO> companyInfoList = null;
        //判断是否存在供应商名称
        if(CollectionUtils.isNotEmpty(allvendorNameSet)) {
            //存在供应商名称时，查询供应商主表数据，按供应商名称构造Meiql的查询条件
            QlOpenQueryWrapper qlOpenQueryWrapper = QlOpenWrappers.query(MqlType.SUPPLIER).in(CompanyInfo::getCompanyName, new ArrayList<>(allvendorNameSet));
            //查询sup数据库
            companyInfoList = RiskComponent.getInstance().getQlOpenClient().query(ContextPath.SUP, qlOpenQueryWrapper);
        }
        /** 供应商联系人 */
        if(CollectionUtils.isNotEmpty(companyInfoList)) {
            //存在供应商主数据时，根据供应商ID查询供应商联系人信息
            List<ContactInfoDto> vendorContactList = RiskComponent.getInstance().getQlOpenClient().query(ContextPath.SUP, QlOpenWrappers.query(MqlType.CONTACTINFO).in(ContactInfoDto::getCompanyId, companyInfoList.stream().map(c->c.get(CompanyInfo::getCompanyId)).collect(Collectors.toList())), ContactInfoDto.class);
            //记录供应商ID和供应商信息Map对象，key-value，key为供应商ID， value为供应商信息对象
            Map<Long, RecordDTO> companyInfoMap = new HashMap<>(15);
            //遍历供应商信息
            companyInfoList.stream().forEach(c -> {
                //将供应商信息写入缓存对象relCompanyInfoMap，key-value，key为供应商名称，value为供应商信息，Object对应的类为RecordDTO
                relCompanyInfoMap.put(c.get(CompanyInfo::getCompanyName), c);
                //将供应商信息写入缓存对象relCompanyInfoMap，key-value，key为供应商ID，value为供应商信息
                companyInfoMap.put(c.get(CompanyInfo::getCompanyId), c);
            });
            //联系人信息是否为空
            if(CollectionUtils.isNotEmpty(vendorContactList)) {
                //联系人信息不为空时遍历
                vendorContactList.stream().forEach(c -> {
                    //根据供应商ID获取供应商信息
                    RecordDTO companyInfo = companyInfoMap.get(c.getCompanyId());
                    //获取供应商名字
                    String companyName = companyInfo.get(CompanyInfo::getCompanyName);
                    //判断缓存对象relCompanyContactInfoMap是否包含Key
                    if(!relCompanyContactInfoMap.containsKey(companyName)) {
                        //不包含key值时，写入key值，value值并初始化一个空集合
                        relCompanyContactInfoMap.put(companyName, new ArrayList<>());
                    }
                    //根据key值取出集合并添加元素，元素为供应商联系人对象
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
        //两个供应商名字的关联字段，用下划线拼接
        return StringUtils.joinWith(SrmConstant.UNDER_LINE, assocationKey(nameA), assocationKey(nameB));
    }

    /**
     * 特殊处理供应商名字，供应商名字包含下划线情况
     * @param name
     * @return
     */
    private static String assocationKey(String name) {
        //特殊处理供应商名字，拼接效果为 @<供应商名字>
        return StringUtils.joinWith("", SrmConstant.SIG_4, SrmConstant.LEFT_BRACE, name, SrmConstant.RIGHT_BRACE);
    }

    /**
     * 关联键解码
     * @param assocationKey
     * @return
     */
    public static String[] decodeAssocationKey(String assocationKey) {
        //解码特殊处理的供应商名字, 按 >_@< 字符串进行解码，例如 @<供应商1>_@<供应商2> 拆分得到 @<供应商1 和 供应商2>
        String[] decode = assocationKey.split(StringUtils.joinWith("", SrmConstant.RIGHT_BRACE, SrmConstant.UNDER_LINE, SrmConstant.SIG_4, SrmConstant.LEFT_BRACE));

        //拆分的第一个字符串，从第二字符开始截取得到原来的供应商名字，例如 @<供应商1 得到 供应商1
        decode[0] = decode[0].substring(2);
        //拆分的第二个字符串，截取到倒数1位得到原来的供应商名字，例如 供应商2> 得到 供应商2
        decode[1] = decode[1].substring(0, decode[1].length()-1);
        //返回关联关系A名称和B名称的数组，2个元素
        return decode;
    }

    /**
     * 添加关联关系到缓存relationMap，key为供应商名称，value为关联的供应商名称集合（包含本身供应商名称）
     * assocationKeySet 为特殊处理的关联关系组合key
     * @param relationMap
     * @param assocationKeySet
     */
    private void appendRelationMap(Map<String, Set<String>> relationMap, Set<String> assocationKeySet) {
        /** 关联KEY key-value， key为供应商名称，value为关联供应商名称集合  例如 A->B 那么 key为A， value为 [B]*/
        Map<String, Set<String>> assocationKey = new HashMap<>(50);
        /** 反向关联KEY key-value， key为供应商名称，value为关联供应商名称集合  例如 A->B 那么 key为B， value为 [A]*/
        Map<String, Set<String>> reverseAssocationKey = new HashMap<>(50);

        //循环特殊的关联组合key
        assocationKeySet.forEach(key -> {
            //对特殊处理的关联key解码，得到关联供应商A和B的名字数组
            String[] relationName = decodeAssocationKey(key);
            //关联关系A作为Key，写入缓存assocationKey
            appendMapValue(assocationKey, relationName[0], relationName[1]);
            //关联关系B作为Key，写入缓存reverseAssocationKey
            appendMapValue(reverseAssocationKey, relationName[1], relationName[0]);
        });

        /** 关联对比KEY */
        Set<String> compareKeySet = new HashSet<>(assocationKey.keySet());
        /** 反向关联对比KEY */
        Set<String> reverseCompareKeySet = new HashSet<>(reverseAssocationKey.keySet());

        //遍历关联关系key值
        for(String key : relationMap.keySet()) {
            /** 关联键  初始化：key对应的 value值 */
            Set<String> relKey = new HashSet<>(relationMap.get(key));
            /** 反向关联键  初始化：key对应的 value值 */
            Set<String> reverseRelKey = new HashSet<>(relationMap.get(key));
            //取交集，当A的关联供应商集合（包含A本身）与关联键 有交集时，证明关联键对应的关联供应商属于关联供应商
            relKey.retainAll(compareKeySet);
            //取交集，当A的关联供应商集合（包含A本身）与反向关联键 有交集时，证明反向关联键对应的关联供应商属于关联供应商
            reverseRelKey.retainAll(reverseCompareKeySet);
            /** 正向合并 将关联供应商写入到 relationMap的value中*/
            merge(relationMap.get(key), relKey, assocationKey);
            /** 反向合并 将关联供应商写入到 relationMap的value中*/
            merge(relationMap.get(key), reverseRelKey, reverseAssocationKey);
        }
    }

    private void merge(Set<String> value, Set<String> relKey, Map<String, Set<String>> relMap) {
        //判断关联件 relKey 不为空
        if(CollectionUtils.isNotEmpty(relKey)) {
            //遍历关联件 relKey，并将关联供应商relMap的value集合添加到 value中，value为原关联关系缓存对象relationMap 某个key值下的value集合
            relKey.forEach(key -> value.addAll(relMap.get(key)));
        }
    }


    /**
     * 添加Map缓存对象的key - value
     * @param map
     * @param key
     * @param value
     */
    private void appendMapValue(Map<String, Set<String>> map, String key, String value) {
        //判空处理
        if(StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
            return;
        }
        //判断是否存在key
        if(!map.containsKey(key)) {
            //不存在key时，初始化key值，并初始化value
            map.put(key, new HashSet<>());
        }
        //获取key值，添加value元素
        map.get(key).add(value);
    }

}
