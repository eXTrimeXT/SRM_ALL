package com.midea.cloud.srm.sou.meiql.recommvendor.risk.pojo;

import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorDto;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Data
public class RiskRequest<T> {

    private T data;
    /**
     * 供应商ID
     */
    private List<Long> vendorIdList;
    /**
     * 是否开启阳光诚信接口
     */
    private Boolean sunshineCreditFlag;
    /**
     * 合并申请单号
     */
    private String applicantNo;
    /**
     * 供应商风险列表
     */
    private List<RecommvendorDto> vendorRiskList;
    /**
     * 供应商风险启用接口字典
     */
    private List<DictItem> recommvenorServiceDictItems;
    /**
     * 联系人名字列表
     */
    private List<String> contactNameList;
    /**
     * 联系人电话列表
     */
    private List<String> ceeaContactMethodList;
    /**
     * 联系人邮箱列表
     */
    private List<String> emailList;
    /**
     * 字典缓存HashMap，key-value，key为字典编码，value为字典条目列表
     */
    private Map<String, List<DictItemDTO>> dictMap;
}
