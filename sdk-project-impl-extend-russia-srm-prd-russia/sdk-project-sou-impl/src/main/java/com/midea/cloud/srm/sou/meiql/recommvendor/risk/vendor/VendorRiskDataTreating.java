package com.midea.cloud.srm.sou.meiql.recommvendor.risk.vendor;

import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.pj.changchengapi.dto.CompanyAQCApiDTO;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.sou.recommvendor.dto.ExceptionSupplierDto;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorDto;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorRiskDto;
import com.midea.cloud.srm.model.sou.recommvendor.enums.RiskItemType;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import com.midea.cloud.srm.model.supplier.bpm.dto.ContactInfoDto;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.model.supplierauth.orgcategory.entity.OrgCatForm;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.abstracts.AbstractRiskDataTreating;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.compent.RiskComponent;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.pojo.RiskRequest;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.pojo.RiskResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: for srm供应商风险数据处理类
 *
 * @author srm
 * @date 2024-05-18
 */
@Slf4j
public class VendorRiskDataTreating extends AbstractRiskDataTreating {

    private static final String LEAGAL = "LEAGAL";

    private static final String SHAREHOLDERS = "SHAREHOLDERS";

    private static final String DIRECTORS = "DIRECTORS";

    private static final String VENDOR_CONTACT_MAN = "VENDOR_CONTACT_MAN";

    private static final String VENDOR_CONTACT_PHONE = "VENDOR_CONTACT_PHONE";

    private static final String VENDOR_CONTACT_EMAIL = "VENDOR_CONTACT_EMAIL";

    private static final String VENDOR_RECOMM_MAN = "VENDOR_RECOMM_MAN";

    private static final String VENDOR_RECOMM_PHONE = "VENDOR_RECOMM_PHONE";

    private static final String VENDOR_RECOMM_EMAIL = "VENDOR_RECOMM_EMAIL";

    private static final String EXCEPTION_SUPPLIER = "EXCEPTION_SUPPLIER";

    private static final String EXCEPTION_SUPPLIER_LEAGAL = "EXCEPTION_SUPPLIER_LEAGAL";

    private static final String EXCEPTION_SUPPLIER_PHONE = "EXCEPTION_SUPPLIER_PHONE";

    private static final String EXCEPTION_SUPPLIER_EMAIL = "EXCEPTION_SUPPLIER_EMAIL";

    private static final String RELATION_VENDOR = "RELATION_VENDOR";

    private static final String RELATION_VENDOR_PHONE = "RELATION_VENDOR_PHONE";

    private static final String RELATION_VENDOR_EMAIL = "RELATION_VENDOR_EMAIL";

    @Override
    public RiskResponse todo(RiskRequest riskRequest) {
        log.info("riskService VendorRiskDataTreating start...");
        //获取所有的工厂策略类处理结果集合，key-value，key为简单类名，value为每个实现类返回的结果
        Map<String, RiskResponse> responseMap = (Map<String, RiskResponse>) riskRequest.getData();
        //获取生成供应商风险底表数据策略实现类的返回结果
        RiskResponse response = responseMap.get(VendorRiskDataGenerator.class.getSimpleName());

        //风险项-重复姓名缓存 key-value， key为姓名，value为供应商名称Set集合
        Map<String, Set<String>> riskNameMap = new HashMap<>(50);
        //风险项-重复电话缓存 key-value， key为电话，value为供应商名称Set集合
        Map<String, Set<String>> riskTelMap = new HashMap<>(50);
        //风险项-重复邮箱缓存 key-value， key为邮箱，value为供应商名称Set集合
        Map<String, Set<String>> riskEmailMap = new HashMap<>(50);

        //获取异常名录策略实现类的响应结果，返回的列表为异常名录清单
        List<ExceptionSupplierDto> exceptionSupplierDtoList = (List<ExceptionSupplierDto>) responseMap.get(VendorRiskPreTreatmentExceptionVendor.class.getSimpleName()).getData();
        //异常名录缓存Map对象，key-value，key值名字，电话，邮箱，value值为异常名录列表
        Map<String, List<ExceptionSupplierDto>> exceptionSupplierMap = new HashMap<>(50);
        //遍历异常名录，并写入异常名录缓存Map对象exceptionSupplierMap
        exceptionSupplierDtoList.stream().forEach(exceptionSupplierDto -> {
            //缓存异常名录联系人名字到缓存对象 exceptionSupplierMap
            appendExceptionSupplier(exceptionSupplierMap, exceptionSupplierDto.getContactName(), exceptionSupplierDto);
            //缓存风险项 异常名录联系人名字和异常名录供应商名字 到 riskNameMap
            appendRiskMap(riskNameMap, exceptionSupplierDto.getContactName(), exceptionSupplierDto.getCompanyName());
            //缓存异常名录法人名字到缓存对象 exceptionSupplierMap
            appendExceptionSupplier(exceptionSupplierMap, exceptionSupplierDto.getLegalPerson(), exceptionSupplierDto);
            //缓存风险项 异常名录法人名字和异常名录供应商名字 到 riskNameMap
            appendRiskMap(riskNameMap, exceptionSupplierDto.getLegalPerson(), exceptionSupplierDto.getCompanyName());
            //缓存异常名录联系人电话到缓存对象 exceptionSupplierMap
            appendExceptionSupplier(exceptionSupplierMap, exceptionSupplierDto.getCeeaContactMethod(), exceptionSupplierDto);
            //缓存风险项 异常名录联系人电话和异常名录供应商名字 到 riskNameMap
            appendRiskMap(riskTelMap, exceptionSupplierDto.getCeeaContactMethod(), exceptionSupplierDto.getCompanyName());
            //缓存异常名录联系人邮箱到缓存对象 exceptionSupplierMap
            appendExceptionSupplier(exceptionSupplierMap, exceptionSupplierDto.getEmail(), exceptionSupplierDto);
            //缓存风险项 异常名录联系人邮箱和异常名录供应商名字 到 riskNameMap
            appendRiskMap(riskEmailMap, exceptionSupplierDto.getEmail(), exceptionSupplierDto.getCompanyName());
        });

        /** 法人、股东、主要联系人，主要人员，报名联系人 */
        //获取供应商风险返回对象
        RecommvendorRiskDto recommvendorRiskDto = (RecommvendorRiskDto) response.getData();

        //解析大数据爬虫接口返回结果，key-value, key为供应商名称，value为爬虫接口返回的对象
        Map<String, CompanyAQCApiDTO> qacMap = qacMap(responseMap);
        //解析供应商联系人返回结果，key-value, key为供应商ID，value为供应商联系人集合列表
        Map<Long, List<ContactInfoDto>> vendorContactMap = vendorContactMap(responseMap);
        //解析供应商推荐报名联系人返回结果，key-value, key为供应商ID，value为供应商推荐单据信息
        Map<Long, RecommvendorDto> vendorRecommMap = vendorRecommMap(responseMap);

        /*Map<String, Object> relData = vendorRelationMap(responseMap);*/
        //解析关联关系供应商返回结果，key-value, key等于relationMap时，获取关联缓存对象，key等于relCompanyInfoMap时，获取关联供应商信息，key等于relCompanyContactInfoMap时，获取关联供应商联系人信息
        Map<String, Object> relData = vendorSimpleRelationMap(responseMap);
        //关联关系缓存Map对象，key-value，key为供应商名称，value为Set集合，集合元素为包括本身供应商名称在内的关联供应商名称，当只有本身供应商名称一个元素时，表明无关联供应商
        Map<String, Set<String>> relationMap = (Map<String, Set<String>>) relData.getOrDefault("relationMap", new HashMap<>(50));
        //供应商信息缓存Map对象，key-value，key为供应商名称，value为供应商信息，Object对应的class为RecordDTO
        Map<String, Object> relCompanyInfoMap = (Map<String, Object>) relData.getOrDefault("relCompanyInfoMap", new HashMap<>(50));
        //返回缓存对象，供应商联系人缓存Map对象，key-value，key为供应商名称，value为供应商联系人信息列表
        Map<String, List<ContactInfoDto>> relCompanyContactInfoMap = (Map<String, List<ContactInfoDto>>) relData.getOrDefault("relCompanyContactInfoMap", new HashMap<>(50));

        //关联关系联系人对象， key为联系人名字，value为供应商名称集合
        Map<String, Set<String>> relContactMap = new HashMap<>(50);
        //遍历关联供应商联系人缓存对象的key集合
        for(String companyName : relCompanyContactInfoMap.keySet()) {
            //获取供应商联系人信息
            List<ContactInfoDto> contactInfoDtoList = (List<ContactInfoDto>) relCompanyContactInfoMap.get(companyName);
            //判断联系人集合是否为空
            if(CollectionUtils.isNotEmpty(contactInfoDtoList)) {
                //循环联系人方式
                contactInfoDtoList.stream().forEach(contactInfoDto -> {
                    //添加关联关系 写入relationMap， key写联系人名字，value添加元素为供应商名称
                    appendRiskMap(relationMap, contactInfoDto.getContactName(), companyName);
                    //添加关联关系 写入relationMap， key写联系人电话，value添加元素为供应商名称
                    appendRiskMap(relationMap, contactInfoDto.getCeeaContactMethod(), companyName);
                    //添加关联关系 写入relationMap， key写联系人邮箱，value添加元素为供应商名称
                    appendRiskMap(relationMap, contactInfoDto.getEmail(), companyName);

                    //添加名字重复供应商风险 写入riskNameMap， key写联系人名字，value添加元素为供应商名称
                    appendRiskMap(riskNameMap, contactInfoDto.getContactName(), companyName);
                    //添加电话重复供应商风险 写入riskNameMap， key写联系人电话，value添加元素为供应商名称
                    appendRiskMap(riskTelMap, contactInfoDto.getCeeaContactMethod(), companyName);
                    //添加邮箱重复供应商风险 写入riskNameMap， key写联系人邮箱，value添加元素为供应商名称
                    appendRiskMap(riskEmailMap, contactInfoDto.getEmail(), companyName);
                });
            }
        }

        //供应商风险信息对象，key-value，key为拼接字符串，下划线拼接，一般时供应商名字_XXX,XXX表示法人、主要人员等含义， value为名字或者电话或者邮箱集合
        Map<String, Object> riskVendorInfo = new HashMap<>(50);
        //供应商风险底表数据Map缓存，key-value，key为供应商名称，value为供应商风险对象
        Map<String, RecommvendorDto> vendorMap = new HashMap<>(50);
        //关联供应商链表，元素为供应商名称集合，已经被识别为关联供应商的集合，用于排除供应商，防止重复识别
        Set<String> relationVendorLink = new HashSet<>(50);

        //遍历供应商风险底表数据列表，放到一个Map缓存对象vendorMap中，方便根据供应商名称获取对象
        recommvendorRiskDto.getVendorRiskList().stream().forEach(vendor -> {
            vendorMap.put(vendor.getVendorName(), vendor);
        });

        //遍历供应商风险底表数据列表，识别供应商风险项
        recommvendorRiskDto.getVendorRiskList().stream().forEach(vendor -> {
            //获取供应商名字
            String companyName = vendor.getVendorName();
            //获取供应商ID
            Long companyId = vendor.getVendorId();
            /*缓存供应商风险信息
            *  riskVendorInfo: 供应商风险信息对象，key-value，key为拼接字符串，下划线拼接，一般时供应商名字_XXX,XXX表示法人、主要人员等含义， value为名字或者电话或者邮箱集合
            *  riskNameMap：风险项-重复姓名缓存 key-value， key为姓名，value为供应商名称Set集合
            * riskTelMap：风险项-重复电话缓存 key-value， key为电话，value为供应商名称Set集合
            * riskEmailMap：风险项-重复邮箱缓存 key-value， key为邮箱，value为供应商名称Set集合
            * qacMap：大数据爬虫接口返回结果，key-value, key为供应商名称，value为爬虫接口返回的对象
            * vendorContactMap：供应商联系人返回结果，key-value, key为供应商ID，value为供应商联系人集合列表
            * vendorRecommMap：供应商推荐报名联系人返回结果，key-value, key为供应商ID，value为供应商推荐单据信息
            * */
            cacheRiskVendor(companyId, companyName, riskVendorInfo, riskNameMap, riskTelMap, riskEmailMap, qacMap, vendorContactMap, vendorRecommMap);
            /*
            * 关联关系供应商
            * companyName:供应商名称
            * recommvendorRiskDto：供应商风险返回对象
            * relationMap：关联关系缓存Map对象，key-value，key为供应商名称，value为Set集合，集合元素为包括本身供应商名称在内的关联供应商名称，当只有本身供应商名称一个元素时，表明无关联供应商
            * vendorMap：供应商风险底表数据Map缓存，key-value，key为供应商名称，value为供应商风险对象
            * relationVendorLink：关联供应商链表，元素为供应商名称集合，已经被识别为关联供应商的集合，用于排除供应商，防止重复识别
            * */
            riskRelationVendor(companyName, recommvendorRiskDto, relationMap, vendorMap, relationVendorLink);
            /*
            * 关联关系黑名单
            * vendor：供应商风险底表具体供应商对象
            * recommvendorRiskDto：供应商风险返回对象
            * relationMap：关联关系缓存Map对象，key-value，key为供应商名称，value为Set集合，集合元素为包括本身供应商名称在内的关联供应商名称，当只有本身供应商名称一个元素时，表明无关联供应商
            * relCompanyInfoMap：供应商信息缓存Map对象，key-value，key为供应商名称，value为供应商信息，Object对应的class为RecordDTO
            * vendorMap：供应商风险底表数据Map缓存，key-value，key为供应商名称，value为供应商风险对象
            * */
            riskRelationVendorBlacklist(vendor, recommvendorRiskDto, relationMap, relCompanyInfoMap, vendorMap);

            /**
             * 统计供应商风险项
             * timeLimitFlag：时间受限
             * extIsMainPoint：是否重点关注
             * groupBlacklistFlag：是否集团黑名单
             * positionLimitFlag：是否单位受限
             * categoryLimitFlag：是否品类受限
             * keySupervisionFlag：是否重点监督
             * extIsDishonesty：是否失信
             * extIsBizAnomaly：是否经营异常
             */
            riskItemCount(recommvendorRiskDto, vendor.getTimeLimitFlag(), vendor.getExtIsMainPoint(), vendor.getGroupBlacklistFlag(),
                    vendor.getPositionLimitFlag(), vendor.getCategoryLimitFlag(), vendor.getKeySupervisionFlag(), vendor.getExtIsDishonesty(),
                    vendor.getExtIsBizAnomaly());
        });

        /** 姓名是否重复 */
        for(String key : riskNameMap.keySet()) {
            //取重复名字的供应商名称集合
            List<String> companyNameSet = new ArrayList<>(riskNameMap.get(key));
            /*
            * 处理名字重复的风险
            * recommvendorRiskDto：供应商风险返回对象
            * companyNameSet：重复名字的供应商名称集合
            * riskVendorInfo：供应商风险信息对象，key-value，key为拼接字符串，下划线拼接，一般时供应商名字_XXX,XXX表示法人、主要人员等含义， value为名字或者电话或者邮箱集合
            * key：名字
            * exceptionSupplierMap：异常名录缓存Map对象，key-value，key值名字，电话，邮箱，value值为异常名录列表
            * relContactMap：关联关系联系人对象， key为联系人名字，value为供应商名称集合
            * vendorMap：供应商风险底表数据Map缓存，key-value，key为供应商名称，value为供应商风险对象
            * */
            nameRepeat(recommvendorRiskDto, companyNameSet, riskVendorInfo, key, exceptionSupplierMap, relContactMap, vendorMap);
        }

        /** 股东与供应商名字是否重复 */
        //遍历供应商风险底表数据，过滤存在供应商爬虫数据的风险底表数据
        recommvendorRiskDto.getVendorRiskList().stream().filter(vendor -> qacMap.containsKey(vendor.getVendorName())).forEach(vendor -> {
            //获取供应商的爬虫信息
            CompanyAQCApiDTO companyAqc = qacMap.get(vendor.getVendorName());
            //判断供应商爬虫的主要股东不为空时
            if(ObjectUtils.allNotNull(companyAqc) && ObjectUtils.allNotNull(companyAqc.getData()) && CollectionUtils.isNotEmpty(companyAqc.getData().getShareholdersData())) {
                //供应商名字风险项 vendorMap缓存，key-value，key为供应商名称，value为供应商风险对象
                List<String> vendorNameList = new ArrayList<>(vendorMap.keySet());
                //供应商股东名字列表
                List<String> shareholdersList = new ArrayList<>(companyAqc.getData().getShareholdersData());
                /** 取供应商名称与股东集合交集*/
                shareholdersList.retainAll(vendorNameList);
                /** 排除自身*/
                shareholdersList.remove(vendor.getVendorName());
                //判断主要股东集合是否为空
                if(CollectionUtils.isNotEmpty(shareholdersList)) {
                    //主要股东集合不为空，说明主要股东与报名其他供应商存在重复异常
                    vendor.setHolderRepeatFlag(YesOrNo.YES.getValue());
                    //股东与供应商名称重复异常提示语：供应商名称股东与供应商名称重复，重复内容为:XXX
                    recommvendorRiskDto.addRiskItem(RiskItemType.NAME, StringUtils.joinWith("", vendor.getVendorName(), "股东与供应商名称重复，重复内容为: ", shareholdersList.stream().collect(Collectors.joining(SrmConstant.SIG_3))));
                    //统计的供应商风险数量+1
                    recommvendorRiskDto.count(1);
                }
            }
        });

        //遍历电话重复集合
        for(String key : riskTelMap.keySet()) {
            //电话重复集合中对应的供应商名称列表
            List<String> companyNameSet = new ArrayList<>(riskTelMap.get(key));
            /*
            * 处理电话重复异常
            * recommvendorRiskDto：供应商风险返回对象
            * companyNameSet：电话重复集合中对应的供应商名称列表
            * riskVendorInfo：供应商风险信息对象，key-value，key为拼接字符串，下划线拼接，一般时供应商名字_XXX,XXX表示法人、主要人员等含义， value为名字或者电话或者邮箱集合
            * key：电话
            * exceptionSupplierMap： 异常名录缓存Map对象，key-value，key值名字，电话，邮箱，value值为异常名录列表
            * relContactMap：关联关系联系人对象， key为联系人名字，value为供应商名称集合
            * vendorMap: 供应商风险底表数据Map缓存，key-value，key为供应商名称，value为供应商风险对象
            * */
            telRepeat(recommvendorRiskDto, companyNameSet, riskVendorInfo, key, exceptionSupplierMap, relContactMap, vendorMap);

        }

        //遍历邮箱重复集合
        for(String key : riskEmailMap.keySet()) {
            //邮箱重复集合中对应的供应商名称列表
            List<String> companyNameSet = new ArrayList<>(riskEmailMap.get(key));
            /*
             * 处理邮箱重复异常
             * recommvendorRiskDto：供应商风险返回对象
             * companyNameSet：邮箱重复集合中对应的供应商名称列表
             * riskVendorInfo：供应商风险信息对象，key-value，key为拼接字符串，下划线拼接，一般时供应商名字_XXX,XXX表示法人、主要人员等含义， value为名字或者电话或者邮箱集合
             * key：邮箱
             * exceptionSupplierMap： 异常名录缓存Map对象，key-value，key值名字，电话，邮箱，value值为异常名录列表
             * relContactMap：关联关系联系人对象， key为联系人名字，value为供应商名称集合
             * vendorMap: 供应商风险底表数据Map缓存，key-value，key为供应商名称，value为供应商风险对象
             * */
            emailRepeat(recommvendorRiskDto, companyNameSet, riskVendorInfo, key, exceptionSupplierMap, relContactMap, vendorMap);
        }

        //历史报名信息查重
        //解析历史供应商重复信息 返回结果，key-value, key为 nameRepList phoneRepList emailRepList，value为供应商报名重复集合列表
        Map<String, List<ExtSouVendor>> vendorRiskPreTreatmentRecommHistoryReg = historyRecommReg(responseMap);
        //对比重复项 并添加到风险项中
        appendRecommHistoryReg(recommvendorRiskDto,vendorRecommMap,vendorRiskPreTreatmentRecommHistoryReg);

        //品类受限 详细内容
        List<OrgCatForm> categoryRestrictionList = categoryRestrictionList(responseMap);
        //处理数据-某公司受限单号某品类受限
        appendCategoryRestriction(recommvendorRiskDto,categoryRestrictionList);


        //识别供应商风险管理监控中的异常 responseMap为所有的工厂策略类处理结果集合，key-value，key为简单类名，value为每个实现类返回的结果
        Map<Long, String> monitorMap = monitorMap(responseMap);

        //循环供应商风险管理监控中数据
        for(Long vendorId: monitorMap.keySet()) {
            //添加供应商风险项
            recommvendorRiskDto.addRiskItem(RiskItemType.MONITORING, monitorMap.get(vendorId));
            //风险数量+1
            recommvendorRiskDto.count(1);
        }

        log.info("riskService VendorRiskDataTreating end...");
        return response;
    }

    /**
     * 统计风险数量
     * @param riskDto 参数
     * @param value 参数
     */
    protected void riskItemCount(RecommvendorRiskDto riskDto, String... value) {
        Arrays.stream(value).forEach(v -> {
            if(YesOrNo.YES.getValue().equals(v)) {
                riskDto.count(1);
            }
        });
    }

    /**
     * 关联关系黑名单
     * @param vendor  供应商风险底表具体供应商对象
     * @param recommvendorRiskDto  供应商风险返回对象
     * @param relationMap 关联关系缓存Map对象，key-value，key为供应商名称，value为Set集合，集合元素为包括本身供应商名称在内的关联供应商名称，当只有本身供应商名称一个元素时，表明无关联供应商
     * @param relCompanyInfoMap 供应商信息缓存Map对象，key-value，key为供应商名称，value为供应商信息，Object对应的class为RecordDTO
     * @param vendorMap 供应商风险底表数据Map缓存，key-value，key为供应商名称，value为供应商风险对象
     */
    private void riskRelationVendorBlacklist(RecommvendorDto vendor, RecommvendorRiskDto recommvendorRiskDto, Map<String, Set<String>> relationMap, Map<String, Object> relCompanyInfoMap, Map<String, RecommvendorDto> vendorMap) {

        //获取供应商名称
        String companyName = vendor.getVendorName();
        //判空处理，排除无效供应商
        if(!relationMap.containsKey(companyName) || !vendorMap.containsKey(companyName)) {
            return;
        }
        //获取关联供应商集合
        Set<String> relationsSet = new HashSet<>(relationMap.get(companyName));
        //移除本身
        relationsSet.remove(companyName);
        //判断关联关系集合是否为空
        if(CollectionUtils.isEmpty(relationsSet)) {
            //关联关系为空时，无需处理，无风险
            return;
        }
        //过滤关联供方列表中属于黑名单的供应商
        Set<String> relationsBlacklistSet = relationsSet.stream().filter(vendorName -> relCompanyInfoMap.containsKey(vendorName)).filter(vendorName -> {
            RecordDTO companyInfo = (RecordDTO) relCompanyInfoMap.get(vendorName);
            return YesOrNo.YES.getValue().equals(companyInfo.get(CompanyInfo::getIsBacklist));
        }).collect(Collectors.toSet());

        //判断关联供方黑名单集合是否为空
        if(CollectionUtils.isEmpty(relationsBlacklistSet)) {
            //为空说明关联供方都不是黑名单，无风险
            return;
        }
        //关联供方黑名单集合不为空时，标识关联供方黑名单标识为Y
        vendor.setRelBlacklistRepeatFlag(YesOrNo.YES.getValue());
        //关联供方黑名单集合不为空时，统计风险数量+黑名单关联供方的数量
        recommvendorRiskDto.count(relationsBlacklistSet.size());
        //关联供方黑名单集合不为空时，提示供应商关联关系供应商存在黑名单，黑名单为：XXXX
        recommvendorRiskDto.addRiskItem(RiskItemType.BLACKLIST, StringUtils.joinWith("", companyName, "关联关系供应商存在黑名单，黑名单为：", relationsBlacklistSet.stream().collect(Collectors.joining(SrmConstant.SIG_3))));
    }

    /**
     * 处理关联关系的风险分析
     * @param companyName  供应商名称
     * @param recommvendorRiskDto   供应商风险返回对象
     * @param relationMap  关联关系缓存Map对象，key-value，key为供应商名称，value为Set集合，集合元素为包括本身供应商名称在内的关联供应商名称，当只有本身供应商名称一个元素时，表明无关联供应商
     * @param vendorMap 供应商风险底表数据Map缓存，key-value，key为供应商名称，value为供应商风险对象
     * @param relationVendorLink  关联供应商链表，元素为供应商名称集合，已经被识别为关联供应商的集合，用于排除供应商，防止重复识别
     */
    private void riskRelationVendor(String companyName, RecommvendorRiskDto recommvendorRiskDto, Map<String, Set<String>> relationMap, Map<String, RecommvendorDto> vendorMap, Set<String> relationVendorLink) {

        //判空处理，防止空异常和无效供应商数据
        if(!relationMap.containsKey(companyName) || !vendorMap.containsKey(companyName)) {
            return;
        }
        //根据供应商名字 取关联关系缓存对象relationMap 中的关联供应商
        Set<String> relationsSet = new HashSet<>(relationMap.get(companyName));

        //关联关系与报名的供应商名字集合取交集，如果存在交集证明与报名其他供应商存在关联关系
        relationsSet.retainAll(vendorMap.keySet());
        //判断关联关系集合是否为空
        if(CollectionUtils.isEmpty(relationsSet)) {
            //为空证明不存在关联关系，无风险，无需处理
            return;
        }
        //移除已经被识别为供应商关联关系的集合，防止重复生成风险提示，例如 A与B存在风险时，遍历A时已提示A和B为关联供方，遍历到B时，无需再提示A和B为关联供方
        relationsSet.removeAll(relationVendorLink);
        //移除本身供应商
        relationsSet.remove(companyName);
        //排除一些无需比较的供应商名单后，判断是否关联关系集合为空
        if(CollectionUtils.isEmpty(relationsSet)) {
            //排除一些无需比较的供应商名单后，关联关系集合为空，无风险，无需处理
            return;
        }
        //将本批次关联关系集合添加到已识别的关联关系集合中去
        relationVendorLink.addAll(relationsSet);
        //将本身供应商名字也添加到已识别的关联关系集合中去，因为关联关系relationsSet集合中不包含本身名单
        relationVendorLink.add(companyName);

        //存在多少个关联关系，那么就标识增加多少个风险数量，前端并未使用后端统计的数量，可以不用过于考究
        recommvendorRiskDto.count(relationsSet.size());
        //添加关联关系供应商风险项，提示供应商与XXX,XXXX为关联关系供应商
        recommvendorRiskDto.addRiskItem(RiskItemType.RELATIONS_VENDOR, StringUtils.joinWith("", companyName, "与", relationsSet.stream().collect(Collectors.joining(SrmConstant.SIG_3)), "为关联关系供应商"));
    }

    /**
     *  缓存供应商风险
     * @param companyId
     * @param companyName
     * @param riskVendorInfo： 供应商风险信息对象，key-value，key为拼接字符串，下划线拼接，一般时供应商名字_XXX,XXX表示法人、主要人员等含义， value为名字或者电话或者邮箱集合
     * @param riskNameMap：风险项-重复姓名缓存 key-value， key为姓名，value为供应商名称Set集合
     * @param riskTelMap: 风险项-重复电话缓存 key-value， key为电话，value为供应商名称Set集合
     * @param riskEmailMap: 风险项-重复邮箱缓存 key-value， key为邮箱，value为供应商名称Set集合
     * @param qacMap: 大数据爬虫接口返回结果，key-value, key为供应商名称，value为爬虫接口返回的对象
     * @param vendorContactMap: 供应商联系人返回结果，key-value, key为供应商ID，value为供应商联系人集合列表
     * @param vendorRecommMap: 供应商推荐报名联系人返回结果，key-value, key为供应商ID，value为供应商推荐单据信息
     */
    private void cacheRiskVendor(Long companyId, String companyName, Map<String, Object> riskVendorInfo, Map<String, Set<String>> riskNameMap, Map<String, Set<String>> riskTelMap, Map<String, Set<String>> riskEmailMap, Map<String, CompanyAQCApiDTO> qacMap, Map<Long, List<ContactInfoDto>> vendorContactMap, Map<Long, RecommvendorDto> vendorRecommMap) {
       
        //根据供应商名字获取爬虫数据
        CompanyAQCApiDTO aqc = qacMap.getOrDefault(companyName, new CompanyAQCApiDTO());
        //判空处理，防止空异常
        if(ObjectUtils.anyNull(aqc.getData())) {
            aqc.setData(new CompanyAQCApiDTO.ItemData());
        }
        /** 法人 */
        //缓存名字重复信息 riskVendorInfo的key值为 供应商名称_LEAGAL， vlaue为法人名字集合
        append(riskVendorInfo, StringUtils.joinWith(SrmConstant.UNDER_LINE, companyName, LEAGAL), Collections.singletonList(aqc.getData().getLegalPerson()));
        //缓存风险项，riskNameMap的key为法人名字，value为供应商名称集合
        appendRiskMap(riskNameMap, aqc.getData().getLegalPerson(), companyName);
        /** 主要股东 */
        //缓存名字重复信息 riskVendorInfo的key值为 供应商名称_SHAREHOLDERS， vlaue为主要股东名字集合
        append(riskVendorInfo, StringUtils.joinWith(SrmConstant.UNDER_LINE, companyName, SHAREHOLDERS), aqc.getData().getShareholdersData());
        //缓存风险项，riskNameMap的key为主要股东名字，value为供应商名称集合
        appendRiskMap(riskNameMap, aqc.getData().getShareholdersData(), companyName);
        /** 主要人员 */
        //缓存名字重复信息 riskVendorInfo的key值为 供应商名称_SHAREHOLDERS， vlaue为主要人员名字集合
        append(riskVendorInfo, StringUtils.joinWith(SrmConstant.UNDER_LINE, companyName, DIRECTORS), aqc.getData().getDirectorsData());
        //缓存风险项，riskNameMap的key为主要人员名字，value为供应商名称集合
        appendRiskMap(riskNameMap, aqc.getData().getDirectorsData(), companyName);

        //按供应商ID获取供应商联系人集合信息
        List<ContactInfoDto> contactInfoDtoList = vendorContactMap.getOrDefault(companyId, new ArrayList<>());

        //遍历供应商联系人集合，识别风险信息
        contactInfoDtoList.stream().forEach(contactInfoDto -> {
            /** 供应商联系人 */
            //缓存名字重复信息 riskVendorInfo的key值为 供应商名称_SHAREHOLDERS， vlaue为供应商联系人名字集合
            append(riskVendorInfo, StringUtils.joinWith(SrmConstant.UNDER_LINE, companyName, VENDOR_CONTACT_MAN), Collections.singletonList(contactInfoDto.getContactName()));
            //缓存风险项，riskNameMap的key为供应商联系人名字，value为供应商名称集合
            appendRiskMap(riskNameMap, contactInfoDto.getContactName(), companyName);
            /** 供应商联系电话 */
            //缓存电话重复信息 riskVendorInfo的key值为 供应商名称_SHAREHOLDERS， vlaue为供应商联系电话集合
            append(riskVendorInfo, StringUtils.joinWith(SrmConstant.UNDER_LINE, companyName, VENDOR_CONTACT_PHONE), Collections.singletonList(contactInfoDto.getCeeaContactMethod()));
            //缓存风险项，riskTelMap的key为供应商联系电话，value为供应商名称集合
            appendRiskMap(riskTelMap, contactInfoDto.getCeeaContactMethod(), companyName);
            /** 供应商联系邮箱 */
            //缓存邮箱重复信息 riskVendorInfo的key值为 供应商名称_SHAREHOLDERS， vlaue为供应商联系邮箱集合
            append(riskVendorInfo, StringUtils.joinWith(SrmConstant.UNDER_LINE, companyName, VENDOR_CONTACT_EMAIL), Collections.singletonList(contactInfoDto.getEmail()));
            //缓存风险项，riskEmailMap的key为供应商联系邮箱，value为供应商名称集合
            appendRiskMap(riskEmailMap, contactInfoDto.getEmail(), companyName);
        });

        //根据供应商ID获取供应商推荐报名信息
        RecommvendorDto recommvendorDto = vendorRecommMap.getOrDefault(companyId, new RecommvendorDto());
        /** 推荐供应商报名联系人 */
        //缓存名字重复信息 riskVendorInfo的key值为 供应商名称_SHAREHOLDERS， vlaue为推荐供应商报名联系人名字集合
        append(riskVendorInfo, StringUtils.joinWith(SrmConstant.UNDER_LINE, companyName, VENDOR_RECOMM_MAN), Collections.singletonList(recommvendorDto.getLinkmanName()));
        //缓存风险项，riskNameMap的key为推荐供应商报名联系人邮箱，value为供应商名称集合
        appendRiskMap(riskNameMap, recommvendorDto.getLinkmanName(), companyName);
        /** 推荐供应商报名联系电话 */
        //缓存电话重复信息 riskVendorInfo的key值为 供应商名称_SHAREHOLDERS， vlaue为推荐供应商报名联系人电话集合
        append(riskVendorInfo, StringUtils.joinWith(SrmConstant.UNDER_LINE, companyName, VENDOR_RECOMM_PHONE), Collections.singletonList(recommvendorDto.getPhone()));
        //缓存风险项，riskTelMap的key为推荐供应商报名联系人电话，value为供应商名称集合
        appendRiskMap(riskTelMap, recommvendorDto.getPhone(), companyName);
        /** 推荐供应商报名联系邮箱 */
        //缓存邮箱重复信息 riskVendorInfo的key值为 供应商名称_SHAREHOLDERS， vlaue为推荐供应商报名联系人邮箱集合
        append(riskVendorInfo, StringUtils.joinWith(SrmConstant.UNDER_LINE, companyName, VENDOR_RECOMM_EMAIL), Collections.singletonList(recommvendorDto.getEmail()));
        //缓存风险项，riskEmailMap的key为推荐供应商报名联系人邮箱，value为供应商名称集合
        appendRiskMap(riskEmailMap, recommvendorDto.getEmail(), companyName);
    }

    /**
     * 处理邮箱重复异常
     * @param recommvendorRiskDto 供应商风险返回对象
     * @param companyNameSet 邮箱重复集合中对应的供应商名称列表
     * @param riskVendorInfo 供应商风险信息对象，key-value，key为拼接字符串，下划线拼接，一般时供应商名字_XXX,XXX表示法人、主要人员等含义， value为名字或者电话或者邮箱集合
     * @param key 邮箱
     * @param exceptionSupplierMap 异常名录缓存Map对象，key-value，key值名字，电话，邮箱，value值为异常名录列表
     * @param relContactMap 关联关系联系人对象， key为联系人名字，value为供应商名称集合
     * @param vendorMap 供应商风险底表数据Map缓存，key-value，key为供应商名称，value为供应商风险对象
     */
    private void emailRepeat(RecommvendorRiskDto recommvendorRiskDto, List<String> companyNameSet, Map<String, Object> riskVendorInfo, String key,
                           Map<String, List<ExceptionSupplierDto>> exceptionSupplierMap, Map<String, Set<String>> relContactMap,Map<String, RecommvendorDto> vendorMap ) {
        //判断邮箱重复供应商集合是否小于等于1，小于等于1时代表没有重复的供应商
        if(companyNameSet.size() <= 1) {
            return;
        }
        /** 邮箱重复 key-value，key为供应商名称，value为邮箱重复类型列表集合*/
        Map<String, List<String>> companyTypeMap = new HashMap<>(50);
        //遍历邮箱重复的供应商列表集合
        companyNameSet.stream().forEach(companyName -> {
            //记录每个供应商邮箱重复的类型列表
            companyTypeMap.put(companyName, emailType(riskVendorInfo, companyName, key, exceptionSupplierMap, relContactMap, vendorMap));
        });

        //遍历邮箱重复供应商列表集合，两两比较，第一个供应商
        for(int i =0; i < companyNameSet.size() -1; i++) {
            //遍历邮箱重复供应商列表集合，两两比较，第二个供应商
            for(int j = i+1; j < companyNameSet.size(); j++) {
                //第一个供应商名称
                String companyNameFori = companyNameSet.get(i);
                //第二个供应商名称
                String companyNameForj = companyNameSet.get(j);
                //判断两个供应商名称都属于供应商风险项有效供应商，否则跳出本次循环，继续处理
                if(!vendorMap.containsKey(companyNameFori) && !vendorMap.containsKey(companyNameForj)) {
                    continue;
                }
                //第一个供应商风险项对象
                RecommvendorDto recommvendorDtoFori = vendorMap.get(companyNameFori);
                //第二个供应商风险项对象
                RecommvendorDto recommvendorDtoForj = vendorMap.get(companyNameForj);
                //遍历邮箱重复类型列表，第一个供应商邮箱重复类型
                companyTypeMap.get(companyNameFori).forEach(typeFori -> {
                    //遍历邮箱重复类型列表，第二个供应商邮箱重复类型
                    companyTypeMap.get(companyNameForj).forEach(typeForj -> {
                        //添加邮箱重复风险项，风险提示语解析
                        recommvendorRiskDto.addRiskItem(RiskItemType.EMAIL, repeatCompanyNameWithType(companyNameWithType(companyNameFori, typeFori, recommvendorDtoFori), companyNameWithType(companyNameForj, typeForj, recommvendorDtoForj), StringUtils.joinWith("",  "重复: 重复内容为", key)));
                        //判断是否两个供应商都属于邮箱重复
                        if(ObjectUtils.allNotNull(recommvendorDtoFori, recommvendorDtoForj)) {
                            //两个供应商都属于邮箱重复时，风险项+2
                            recommvendorRiskDto.count(2);
                        } else {
                            //只有一个供应商都属于邮箱重复时，风险项+1
                            recommvendorRiskDto.count(1);
                        }
                    });
                });
            }
        }
    }

    /**
     * 邮箱重复类型识别
     * @param riskVendorInfo 供应商风险信息对象，key-value，key为拼接字符串，下划线拼接，一般时供应商名字_XXX,XXX表示法人、主要人员等含义， value为名字或者电话或者邮箱集合
     * @param companyName 供应商名称
     * @param value 邮箱
     * @param exceptionSupplierMap 异常名录缓存Map对象，key-value，key值名字，电话，邮箱，value值为异常名录列表
     * @param relContactMap 关联关系联系人对象， key为联系人名字，value为供应商名称集合
     * @param vendorMap 供应商风险底表数据Map缓存，key-value，key为供应商名称，value为供应商风险对象
     * @return
     */
    private List<String> emailType(Map<String, Object> riskVendorInfo, String companyName, String value, Map<String, List<ExceptionSupplierDto>> exceptionSupplierMap, Map<String, Set<String>> relContactMap, Map<String, RecommvendorDto> vendorMap) {

        //邮箱重复类型列表
        List<String> typeList = new ArrayList<>();
        //识别并添加邮箱重复类型-供应商联系人邮箱重复
        addList(typeList, decodeTypeCompany(riskVendorInfo, companyName, value, VENDOR_CONTACT_EMAIL));
        //识别并添加邮箱重复类型-推荐供应商报名联系人邮箱重复
        addList(typeList, decodeTypeCompany(riskVendorInfo, companyName, value, VENDOR_RECOMM_EMAIL));

        //供应商为风险项供应商 且 异常名录存在该邮箱
        if(!vendorMap.containsKey(companyName) && exceptionSupplierMap.containsKey(value)) {
            //过滤属于该供应商的异常名录操作
            Optional<ExceptionSupplierDto> exceptionSupplierDtoOptional = exceptionSupplierMap.get(value).stream().filter(s -> companyName.equals(s.getCompanyName())).findAny();
            //判断属于该供应商的异常名录是否存在
            if(exceptionSupplierDtoOptional.isPresent()) {
                //存在供应商异常名录邮箱重复类型
                addList(typeList, EXCEPTION_SUPPLIER_EMAIL);
            }
        }

        //判断供应商是否为风险项供应商 且 关联供方邮箱包含当前邮箱 且 包含了该供应商
        if(!vendorMap.containsKey(companyName) && relContactMap.containsKey(value) && relContactMap.get(value).contains(companyName)) {
            //存在关联供方邮箱重复类型
            addList(typeList, RELATION_VENDOR_EMAIL);
        }
        return typeList;
    }

    /**
     * 处理电话重复异常
     * @param recommvendorRiskDto 供应商风险返回对象
     * @param companyNameSet 电话重复集合中对应的供应商名称列表
     * @param riskVendorInfo 供应商风险信息对象，key-value，key为拼接字符串，下划线拼接，一般时供应商名字_XXX,XXX表示法人、主要人员等含义， value为名字或者电话或者邮箱集合
     * @param key 电话
     * @param exceptionSupplierMap 异常名录缓存Map对象，key-value，key值名字，电话，邮箱，value值为异常名录列表
     * @param relContactMap 关联关系联系人对象， key为联系人名字，value为供应商名称集合
     * @param vendorMap 供应商风险底表数据Map缓存，key-value，key为供应商名称，value为供应商风险对象
     */
    private void telRepeat(RecommvendorRiskDto recommvendorRiskDto, List<String> companyNameSet, Map<String, Object> riskVendorInfo, String key,
                            Map<String, List<ExceptionSupplierDto>> exceptionSupplierMap, Map<String, Set<String>> relContactMap,Map<String, RecommvendorDto> vendorMap ) {

        //一个电话的供应商集合列表小于等于1代表没有重复，直接返回不处理
        if(companyNameSet.size() <= 1) {
            return;
        }
        /** 电话重复 key-value, key为供应商名字， value为列表集合，元素为类型，也就是说电话属于报名联系电话还是供应商联系人电话这些 */
        Map<String, List<String>> companyTypeMap = new HashMap<>(50);
        //遍历重复的供应商集合
        companyNameSet.stream().forEach(companyName -> {
            //将重复供应商的电话类型记录缓存
            companyTypeMap.put(companyName, telType(riskVendorInfo, companyName, key, exceptionSupplierMap, relContactMap, vendorMap));
        });

        //遍历重复供应商列表，两两比较，第一个供应商
        for(int i =0; i < companyNameSet.size() -1; i++) {
            //遍历重复供应商列表，两两比较，第二个供应商
            for(int j = i+1; j < companyNameSet.size(); j++) {
                //第一个供应商名称
                String companyNameFori = companyNameSet.get(i);
                //第二个供应商名称
                String companyNameForj = companyNameSet.get(j);
                //判断是否都属于风险供应商底表数据，如果不都是的话，跳出循环继续处理
                if(!vendorMap.containsKey(companyNameFori) && !vendorMap.containsKey(companyNameForj)) {
                    continue;
                }
                //第一个供应商风险底表数据
                RecommvendorDto recommvendorDtoFori = vendorMap.get(companyNameFori);
                //第二个供应商风险底表数据
                RecommvendorDto recommvendorDtoForj = vendorMap.get(companyNameForj);
                //遍历电话类型，第一个供应商的类型
                companyTypeMap.get(companyNameFori).forEach(typeFori -> {
                    //遍历电话类型，第二个供应商的类型
                    companyTypeMap.get(companyNameForj).forEach(typeForj -> {
                        //添加风险项，提示异常信息
                        recommvendorRiskDto.addRiskItem(RiskItemType.TEL, repeatCompanyNameWithType(companyNameWithType(companyNameFori, typeFori, recommvendorDtoFori), companyNameWithType(companyNameForj, typeForj, recommvendorDtoForj), StringUtils.joinWith("",  "重复: 重复内容为", key)));
                        //判断是否两个供应商风险底表数据都不为空
                        if(ObjectUtils.allNotNull(recommvendorDtoFori, recommvendorDtoForj)) {
                            //两个供应商风险底表数据不为空时风险数量+2
                            recommvendorRiskDto.count(2);
                        } else {
                            //只有一个供应商风险底表数据不为空时风险数量+2
                            recommvendorRiskDto.count(1);
                        }
                    });
                });
            }
        }
    }

    /**
     * 电话类型
     * @param riskVendorInfo 供应商风险信息对象，key-value，key为拼接字符串，下划线拼接，一般时供应商名字_XXX,XXX表示法人、主要人员等含义， value为名字或者电话或者邮箱集合
     * @param companyName 供应商名称
     * @param value 电话
     * @param exceptionSupplierMap 异常名录缓存Map对象，key-value，key值名字，电话，邮箱，value值为异常名录列表
     * @param relContactMap 关联关系联系人对象， key为联系人名字，value为供应商名称集合
     * @param vendorMap 供应商风险底表数据Map缓存，key-value，key为供应商名称，value为供应商风险对象
     * @return
     */
    private List<String> telType(Map<String, Object> riskVendorInfo, String companyName, String value, Map<String, List<ExceptionSupplierDto>> exceptionSupplierMap, Map<String, Set<String>> relContactMap, Map<String, RecommvendorDto> vendorMap) {
        //电话类型列表
        List<String> typeList = new ArrayList<>();
        //添加供应商联系人电话类型，识别存在则增加到typeList
        addList(typeList, decodeTypeCompany(riskVendorInfo, companyName, value, VENDOR_CONTACT_PHONE));
        //添加推荐供应商联系人报名电话类型，识别存在则增加到typeList
        addList(typeList, decodeTypeCompany(riskVendorInfo, companyName, value, VENDOR_RECOMM_PHONE));


        //判断供应商风险底表数据是否包含供应商名称 且  异常名录是否包含电话
        if(!vendorMap.containsKey(companyName) && exceptionSupplierMap.containsKey(value)) {
            //取异常名录的供应商联系人电话列表操作---过滤属于该供应商名称的
            Optional<ExceptionSupplierDto> exceptionSupplierDtoOptional = exceptionSupplierMap.get(value).stream().filter(s -> companyName.equals(s.getCompanyName())).findAny();
            //判断异常名录供应商联系人电话是否存在
            if(exceptionSupplierDtoOptional.isPresent()) {
                //添加异常名录供供应商联系人电话重复类型
                addList(typeList, EXCEPTION_SUPPLIER_PHONE);
            }
        }

        //判断是否存在关联供方联系人电话重复
        if(!vendorMap.containsKey(companyName) && relContactMap.containsKey(value) && relContactMap.get(value).contains(companyName)) {
            //添加关联供应商供应商联系人电话重复类型
            addList(typeList, RELATION_VENDOR_PHONE);
        }
        return typeList;
    }

    /**
     * 处理名字重复的风险
     * @param recommvendorRiskDto 供应商风险返回对象
     * @param companyNameSet 重复名字的供应商名称集合
     * @param riskVendorInfo 供应商风险信息对象，key-value，key为拼接字符串，下划线拼接，一般时供应商名字_XXX,XXX表示法人、主要人员等含义， value为名字或者电话或者邮箱集合
     * @param key 名字
     * @param exceptionSupplierMap 异常名录缓存Map对象，key-value，key值名字，电话，邮箱，value值为异常名录列表
     * @param relContactMap 关联关系联系人对象， key为联系人名字，value为供应商名称集合
     * @param vendorMap 供应商风险底表数据Map缓存，key-value，key为供应商名称，value为供应商风险对象
     */
    private void nameRepeat(RecommvendorRiskDto recommvendorRiskDto, List<String> companyNameSet, Map<String, Object> riskVendorInfo, String key,
                            Map<String, List<ExceptionSupplierDto>> exceptionSupplierMap, Map<String, Set<String>> relContactMap,Map<String, RecommvendorDto> vendorMap ) {
        //如果一个名字里的供应商名称只有一个，证明该名字不重复
        if(companyNameSet.size() <= 1) {
            return;
        }
        /** 名字重复 */
        //记录名字重复的类型， key-value，key为名字，value为类型列表集合，例如元素为法人、主要人员等
        Map<String, List<String>> companyTypeMap = new HashMap<>(50);
        //遍历供应商集合
        companyNameSet.stream().forEach(companyName -> {
            //识别名字类型，写入缓存companyTypeMap
            companyTypeMap.put(companyName, nameType(riskVendorInfo, companyName, key, exceptionSupplierMap, relContactMap, vendorMap));
        });

        //遍历存在重复名字的供应商名字列表
        for(int i =0; i < companyNameSet.size() -1; i++) {
            //二次循环，两两比较
            for(int j = i+1; j < companyNameSet.size(); j++) {
                //第一个供应商名字
                String companyNameFori = companyNameSet.get(i);
                //第二个供应商名字
                String companyNameForj = companyNameSet.get(j);
                //判断两个供应商名字是否属于有效供应商
                if(!vendorMap.containsKey(companyNameFori) && !vendorMap.containsKey(companyNameForj)) {
                    //如果其中一个供应商或者两个供应商都不在报名供应商范围里，则无风险，跳出本次循环
                    continue;
                }
                //第一个供应商风险底表数据
                RecommvendorDto recommvendorDtoFori = vendorMap.get(companyNameFori);
                //第二个供应商风险底表数据
                RecommvendorDto recommvendorDtoForj = vendorMap.get(companyNameForj);
                //遍历第一个供应商类型列表
                companyTypeMap.get(companyNameFori).forEach(typeFori -> {
                    //遍历第二个供应商类型列表
                    companyTypeMap.get(companyNameForj).forEach(typeForj -> {
                        //添加供应商风险项，提示供应商名称1法人与供应商2主要人员名字重复，重复内容为：xxx
                        recommvendorRiskDto.addRiskItem(RiskItemType.NAME, repeatCompanyNameWithType(companyNameWithType(companyNameFori, typeFori, recommvendorDtoFori), companyNameWithType(companyNameForj, typeForj, recommvendorDtoForj), StringUtils.joinWith("",  "重复: 重复内容为", key)));
                        //判断两个供应商风险底表数据都不为空
                        if(ObjectUtils.allNotNull(recommvendorDtoFori, recommvendorDtoForj)) {
                            //都不为空时，风险数量+2
                            recommvendorRiskDto.count(2);
                        } else {
                            //有一个为空时，风险数量+1
                            recommvendorRiskDto.count(1);
                        }
                    });
                });
            }
        }
    }

    /**
     *
     * @param messageFori 第一个供应商风险信息
     * @param messageForj 第一个供应商风险信息
     * @param message 风险提示语
     * @return
     */
    private String repeatCompanyNameWithType(String messageFori, String messageForj, String message) {
        //字符串拼接：第一个供应商风险信息与第一个供应商风险信息风险提示语
        return StringUtils.joinWith("", messageFori, "与", messageForj, message);
    }

    /**
     *
     * @param companyName 供应商名称
     * @param type  类型
     * @param vendor  供应商风险底表数据
     * @return
     */
    private String companyNameWithType(String companyName, String type, RecommvendorDto vendor) {
        //判断供应商风险底表数据不为空
        if(ObjectUtils.anyNull(vendor)) {
            //为空时初始化对象，防止空异常
            vendor = new RecommvendorDto();
        }
        //转换类型
        switch (type) {
            case LEAGAL:
                //法人重复标识设置为Y
                vendor.setLegalRepeatFlag(YesOrNo.YES.getValue());
                //供应商名称法人
                return StringUtils.joinWith("", companyName, "法人");
            case SHAREHOLDERS:
                //主要股东重复标识设置为Y
                vendor.setHolderRepeatFlag(YesOrNo.YES.getValue());
                //供应商名称主要股东
                return StringUtils.joinWith("", companyName, "主要股东");
            case DIRECTORS:
                //主要人员重复标识设置为Y
                vendor.setMainPeopleRepeatFlag(YesOrNo.YES.getValue());
                //供应商名称主要人员
                return StringUtils.joinWith("", companyName, "主要人员");
            case VENDOR_CONTACT_MAN:
                //供应商联系人重复标识设置为Y
                vendor.setContackRepeatFlag(YesOrNo.YES.getValue());
                //供应商名称供应商联系人
                return StringUtils.joinWith("", companyName, "供应商联系人");
            case VENDOR_CONTACT_PHONE:
                //供应商联系电话重复标识设置为Y
                vendor.setTelRepeatFlag(YesOrNo.YES.getValue());
                //供应商名称供应商联系电话
                return StringUtils.joinWith("", companyName, "供应商联系电话");
            case VENDOR_CONTACT_EMAIL:
                //供应商联系邮箱重复标识设置为Y
                vendor.setEmailRepeatFlag(YesOrNo.YES.getValue());
                //供应商名称供应商联系邮箱
                return StringUtils.joinWith("", companyName, "供应商联系邮箱");
            case VENDOR_RECOMM_MAN:
                //报名联系人重复标识设置为Y
                vendor.setContackRepeatFlag(YesOrNo.YES.getValue());
                //供应商名称报名联系人
                return StringUtils.joinWith("", companyName, "报名联系人");
            case VENDOR_RECOMM_PHONE:
                //报名联系电话重复标识设置为Y
                vendor.setTelRepeatFlag(YesOrNo.YES.getValue());
                //供应商名称报名联系电话
                return StringUtils.joinWith("", companyName, "报名联系电话");
            case VENDOR_RECOMM_EMAIL:
                //报名联系邮箱重复标识设置为Y
                vendor.setEmailRepeatFlag(YesOrNo.YES.getValue());
                //供应商名称报名联系邮箱
                return StringUtils.joinWith("", companyName, "报名联系邮箱");
            case EXCEPTION_SUPPLIER:
                //异常名录供应商联系人重复标识设置为Y
                vendor.setTelRepeatFlag(YesOrNo.YES.getValue());
                //异常名录供应商名称供应商联系人
                return StringUtils.joinWith("", "异常名录", companyName, "供应商联系人");
            case EXCEPTION_SUPPLIER_LEAGAL:
                //异常名录法人重复标识设置为Y
                vendor.setLegalRepeatFlag(YesOrNo.YES.getValue());
                //异常名录供应商名称法人
                return StringUtils.joinWith("", "异常名录", companyName, "法人");
            case EXCEPTION_SUPPLIER_PHONE:
                //异常名录供应商联系电话重复标识设置为Y
                vendor.setTelRepeatFlag(YesOrNo.YES.getValue());
                //异常名录供应商名称供应商联系电话
                return StringUtils.joinWith("", "异常名录", companyName, "供应商联系电话");
            case EXCEPTION_SUPPLIER_EMAIL:
                //异常名录供应商联系邮箱重复标识设置为Y
                vendor.setEmailRepeatFlag(YesOrNo.YES.getValue());
                //异常名录供应商名称供应商联系邮箱
                return StringUtils.joinWith("", "异常名录", companyName, "供应商联系邮箱");
            case RELATION_VENDOR:
                //关联供应商供应商联系人重复标识设置为Y
                vendor.setContackRepeatFlag(YesOrNo.YES.getValue());
                //关联供应商供应商名称供应商联系人
                return StringUtils.joinWith("", "关联供应商", companyName, "供应商联系人");
            case RELATION_VENDOR_PHONE:
                //关联供应商供应商联系电话重复标识设置为Y
                vendor.setTelRepeatFlag(YesOrNo.YES.getValue());
                //关联供应商供应商名称供应商联系电话
                return StringUtils.joinWith("", "关联供应商", companyName, "供应商联系电话");
            case RELATION_VENDOR_EMAIL:
                //关联供应商供应商联系邮箱重复标识设置为Y
                vendor.setEmailRepeatFlag(YesOrNo.YES.getValue());
                //关联供应商供应商名称供应商联系邮箱
                return StringUtils.joinWith("", "关联供应商", companyName, "供应商联系邮箱");
            default:
        }
        return companyName;
    }

    /**
     * 识别名字类型
     * @param riskVendorInfo 供应商风险信息对象，key-value，key为拼接字符串，下划线拼接，一般时供应商名字_XXX,XXX表示法人、主要人员等含义， value为名字或者电话或者邮箱集合
     * @param companyName 供应商名称
     * @param value  名字
     * @param exceptionSupplierMap  异常名录缓存Map对象，key-value，key值名字，电话，邮箱，value值为异常名录列表
     * @param relContactMap  关联关系联系人对象， key为联系人名字，value为供应商名称集合
     * @param vendorMap  供应商风险底表数据Map缓存，key-value，key为供应商名称，value为供应商风险对象
     * @return
     */
    private List<String> nameType(Map<String, Object> riskVendorInfo, String companyName, String value, Map<String, List<ExceptionSupplierDto>> exceptionSupplierMap, Map<String, Set<String>> relContactMap, Map<String, RecommvendorDto> vendorMap) {

        //类型列表集合对象
        List<String> typeList = new ArrayList<>();
        //识别法人名字类型
        addList(typeList, decodeTypeCompany(riskVendorInfo, companyName, value, LEAGAL));
        //识别主要股东名字类型
        addList(typeList, decodeTypeCompany(riskVendorInfo, companyName, value, SHAREHOLDERS));
        //识别主要人员名字类型
        addList(typeList, decodeTypeCompany(riskVendorInfo, companyName, value, DIRECTORS));
        //识别供应商联系人名字类型
        addList(typeList, decodeTypeCompany(riskVendorInfo, companyName, value, VENDOR_CONTACT_MAN));
        //识别推荐供应商报名联系人名字类型
        addList(typeList, decodeTypeCompany(riskVendorInfo, companyName, value, VENDOR_RECOMM_MAN));

        //判断有效供应商和异常名录缓存Map对象风险
        if(!vendorMap.containsKey(companyName) && exceptionSupplierMap.containsKey(value)) {
            //过滤异常名录存在名字重复的操作
            Optional<ExceptionSupplierDto> exceptionSupplierDtoOptional = exceptionSupplierMap.get(value).stream().filter(s -> companyName.equals(s.getCompanyName())).findAny();
            //判断是否异常名录存在名字重复
            if(exceptionSupplierDtoOptional.isPresent()) {
                //异常名录存在名字重复，取出异常名录对象
                ExceptionSupplierDto exceptionSupplierDto = exceptionSupplierDtoOptional.get();
                //判断异常名录供应商联系人是否相等
                if(value.equals(exceptionSupplierDto.getContactName())) {
                    //属于供应商联系人相等的风险
                    addList(typeList, EXCEPTION_SUPPLIER);
                    //判断异常名录法人是否相等
                } else if(value.equals(exceptionSupplierDto.getLegalPerson())) {
                    //属于法人相等的风险
                    addList(typeList, EXCEPTION_SUPPLIER_LEAGAL);
                }
            }
        }

        //判断是否存在关联供应商联系人
        if(!vendorMap.containsKey(companyName) && relContactMap.containsKey(value) && relContactMap.get(value).contains(companyName)) {
            //属于关联关系联系人相等的风险
            addList(typeList, RELATION_VENDOR);
        }
        return typeList;
    }


    private void addList(List<String> list, String element) {
        if(StringUtils.isBlank(element) || list.contains(element)) {
            return;
        }
        list.add(element);
    }

    /**
     * 解码类型
     * @param riskVendorInfo  供应商风险信息对象，key-value，key为拼接字符串，下划线拼接，一般时供应商名字_XXX,XXX表示法人、主要人员等含义， value为名字或者电话或者邮箱集合
     * @param companyName  供应商名称
     * @param value  名字
     * @param type  类型
     * @return
     */
    private String decodeTypeCompany(Map<String, Object> riskVendorInfo, String companyName, String value, String type) {
        //拼接字符串，下划线拼接，规则：供应商名称_类型编码
        String key = StringUtils.joinWith(SrmConstant.UNDER_LINE, companyName, type);
        //如果供应商风险信息对象不存在上述key值，说明不属于该类型
        if(!riskVendorInfo.containsKey(key)) {
            return "";
        }
        //取出属于key的所有值集合，元素可能是名字、邮箱、电话
        List valueList = (List<String>)riskVendorInfo.get(key);
        //判断值集合是否包含当前值
        if(valueList.contains(value)) {
            //包含当前值说明属于该类型
            return type;
        }
        //不包含当前值说明不属于该类型
        return "";
    }

    private void appendExceptionSupplier(Map<String, List<ExceptionSupplierDto>> exceptionSupplierMap, String key, ExceptionSupplierDto value) {
        if(!exceptionSupplierMap.containsKey(key)) {
            exceptionSupplierMap.put(key, new ArrayList<>());
        }
        exceptionSupplierMap.get(key).add(value);
    }

    private void appendRiskMap(Map<String, Set<String>> riskMap, List<String> key, String value) {
        if(CollectionUtils.isNotEmpty(key)) {
            key.stream().forEach(k -> appendRiskMap(riskMap, k, value));
        }
    }

    private void appendRiskMap(Map<String, Set<String>> riskMap, String key, String value) {
        if(StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
            return;
        }
        if(!riskMap.containsKey(key)) {
            riskMap.put(key, new HashSet<>());
        }
        riskMap.get(key).add(value);
    }

    private void append(Map<String, Object> map, String key, List<String> value) {
        if(CollectionUtils.isNotEmpty(value)) {
            value = value.stream().filter(s -> StringUtils.isNotBlank(s)).collect(Collectors.toList());
        }
        if(CollectionUtils.isEmpty(value)) {
            return;
        }
        if(!map.containsKey(key)) {
            map.put(key, value);
        } else {
            value.addAll((List<String>) map.get(key));
            map.put(key, value);
        }
    }

    private Map<String, Object> vendorRelationMap(Map<String, RiskResponse> responseMap) {
        RiskResponse response = responseMap.get(VendorRiskPreTreatmentRelation.class.getSimpleName());
        if(!Objects.isNull(response)) {
            Map<String, Object> data = (Map<String, Object>) response.getData();
            return data;
        }
        return new HashMap<>(50);
    }

    private Map<String, Object> vendorSimpleRelationMap(Map<String, RiskResponse> responseMap) {
        RiskResponse response = responseMap.get(VendorRiskPreTreatmentSimpleRelation.class.getSimpleName());
        if(!Objects.isNull(response)) {
            Map<String, Object> data = (Map<String, Object>) response.getData();
            return data;
        }
        return new HashMap<>(50);
    }

    private Map<Long, RecommvendorDto> vendorRecommMap(Map<String, RiskResponse> responseMap) {
        RiskResponse response = responseMap.get(VendorRiskPreTreatmentRecomm.class.getSimpleName());
        if(!Objects.isNull(response)) {
            return (Map<Long, RecommvendorDto>) response.getData();
        }
        return new HashMap<>(50);
    }

    private Map<Long, List<ContactInfoDto>> vendorContactMap(Map<String, RiskResponse> responseMap) {
        RiskResponse response = responseMap.get(VendorRiskPreTreatmentContact.class.getSimpleName());
        if(!Objects.isNull(response)) {
            return (Map<Long, List<ContactInfoDto>>) response.getData();
        }
        return new HashMap<>(50);
    }

    private Map<String, CompanyAQCApiDTO> qacMap(Map<String, RiskResponse> responseMap) {
        RiskResponse response = responseMap.get(VendorRiskPreTreatmentCrawler.class.getSimpleName());
        if(!Objects.isNull(response)) {
            return (Map<String, CompanyAQCApiDTO>) response.getData();
        }
        return new HashMap<>(50);
    }

    /**
     * 解析结果
     * @param responseMap 所有的工厂策略类处理结果集合，key-value，key为简单类名，value为每个实现类返回的结果
     * @return
     */
    private Map<Long, String> monitorMap(Map<String, RiskResponse> responseMap) {
        //获取策略类响应数据
        RiskResponse response = responseMap.get(VendorRiskPreTreatmentMonitor.class.getSimpleName());
        //判断是否存在响应
        if(!Objects.isNull(response)) {
            //返回响应的数据
            return (Map<Long, String>) response.getData();
        }
        return new HashMap<>(50);
    }

    private  List<OrgCatForm> categoryRestrictionList(Map<String, RiskResponse> responseMap) {
        RiskResponse response = responseMap.get(VendorRiskDataCategoryRestriction.class.getSimpleName());
        if(!Objects.isNull(response)) {
            return (List<OrgCatForm>) response.getData();
        }
        return new ArrayList<>();
    }

    private void appendCategoryRestriction(RecommvendorRiskDto recommvendorRiskDto, List<OrgCatForm> categoryRestrictionList){

        categoryRestrictionList.forEach(orgCatForm ->
                orgCatForm.getRangeList().forEach(orgCatFormCategory ->
                        recommvendorRiskDto.addRiskItem(RiskItemType.CATEGORY_RESTRICTION,
                                StringUtils.joinWith("-",
                                        orgCatForm.getVendorName(), orgCatForm.getOrgCatFormNumber(), orgCatFormCategory.getCategoryName()))));

    }

    private Map<String, List<ExtSouVendor>> historyRecommReg(Map<String, RiskResponse> responseMap) {
        RiskResponse response = responseMap.get(VendorRiskPreTreatmentRecommHistoryReg.class.getSimpleName());
        if(!Objects.isNull(response)) {
            return (Map<String, List<ExtSouVendor>>) response.getData();
        }
        return new HashMap<>(50);
    }

    private void appendRecommHistoryReg(RecommvendorRiskDto recommvendorRiskDto,Map<Long, RecommvendorDto> vendorRecommMap,
                                        Map<String, List<ExtSouVendor>> vendorRiskPreTreatmentRecommHistoryReg){

        List<ExtSouVendor> nameRepList = vendorRiskPreTreatmentRecommHistoryReg.get("nameRepList");
        List<ExtSouVendor> phoneRepList = vendorRiskPreTreatmentRecommHistoryReg.get("phoneRepList");
        List<ExtSouVendor> emailRepList = vendorRiskPreTreatmentRecommHistoryReg.get("emailRepList");

        String str1 = "存在重复: 重复内容为";
        String str2 = "，项目为：" ;
        vendorRecommMap.forEach((vendorId, recommVendor) -> {
            nameRepList.stream()
                    .filter(extSouVendor -> !vendorId.equals(extSouVendor.getVendorId()) &&
                            recommVendor.getLinkmanName().equals(extSouVendor.getLinkmanName()) &&
                            !recommVendor.getProjectId().equals(extSouVendor.getProjectId()))
                    .forEach(extSouVendor -> {
                        ExtSouProject extSouProject = RiskComponent.getInstance().getProjectMapper().selectById(extSouVendor.getProjectId());
                        recommvendorRiskDto.addRiskItem(RiskItemType.HIS_LINKNAME,
                                repeatCompanyNameWithType(recommVendor.getVendorName() + "的报名联系人",
                                        extSouVendor.getVendorName() + "历史报名联系人",
                                        StringUtils.joinWith("", str1, extSouVendor.getLinkmanName(), str2 + extSouProject.getSouNo())));
                    });
            phoneRepList.stream()
                    .filter(extSouVendor -> !vendorId.equals(extSouVendor.getVendorId()) &&
                            recommVendor.getPhone().equals(extSouVendor.getPhone()) &&
                            !recommVendor.getProjectId().equals(extSouVendor.getProjectId()))
                    .forEach(extSouVendor -> {
                        ExtSouProject extSouProject = RiskComponent.getInstance().getProjectMapper().selectById(extSouVendor.getProjectId());
                        recommvendorRiskDto.addRiskItem(RiskItemType.HIS_PHONE,
                                repeatCompanyNameWithType(recommVendor.getVendorName() + "的报名联系电话",
                                        extSouVendor.getVendorName() + "历史报名联系电话",
                                        StringUtils.joinWith("", str1, extSouVendor.getPhone(), str2 + extSouProject.getSouNo())));
                    });
            emailRepList.stream()
                    .filter(extSouVendor -> !vendorId.equals(extSouVendor.getVendorId()) &&
                            recommVendor.getEmail().equals(extSouVendor.getEmail()) &&
                            !recommVendor.getProjectId().equals(extSouVendor.getProjectId()))
                    .forEach(extSouVendor -> {
                        ExtSouProject extSouProject = RiskComponent.getInstance().getProjectMapper().selectById(extSouVendor.getProjectId());
                        recommvendorRiskDto.addRiskItem(RiskItemType.HIS_EMAIL,
                                repeatCompanyNameWithType(recommVendor.getVendorName() + "的报名邮箱",
                                        extSouVendor.getVendorName() + "历史报名邮箱",
                                        StringUtils.joinWith("", str1, extSouVendor.getEmail(), str2 + extSouProject.getSouNo())));
                    });
        });



    }


}
