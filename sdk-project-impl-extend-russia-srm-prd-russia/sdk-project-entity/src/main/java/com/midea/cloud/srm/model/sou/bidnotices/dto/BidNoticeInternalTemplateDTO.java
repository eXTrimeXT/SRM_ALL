package com.midea.cloud.srm.model.sou.bidnotices.dto;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.base.utils.DictUtil;
import com.midea.cloud.srm.model.sou.bidnotices.NumberFormatUtils;
import com.midea.cloud.srm.model.sou.bidnotices.NumberToChinese;
import com.midea.cloud.srm.model.sou.ca.dto.CaDTO;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouGroup;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrder;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 100014336
 */
@Data
public class BidNoticeInternalTemplateDTO {

    @ApiModelProperty(value = "需求部门名称", example = "需求部门1", required = true)
    private String demandDepartmentName;

    @ApiModelProperty(value = "项目名称", example = "项目1")
    private String souName;

    @ApiModelProperty(value = "招标项目编号", example = "001")
    private String extProjectNo;

    @ApiModelProperty(value = "小写金额", example = "12,000")
    private String smallWinAmount;

    @ApiModelProperty(value = "大写金额", example = "人民币壹佰贰拾万元整")
    private String bigWinAmount;

    @ApiModelProperty(value = "合同周期", example = "2022-01-01至2023-01-01")
    private String contractPeriod;

    @ApiModelProperty(value = "招标负责人姓名", example = "张三")
    private String souPrincipal;

    @ApiModelProperty("投标联系电话")
    private String extTenderPhone;

    @ApiModelProperty(value = "供应商名称", example = "供应商1")
    private String vendorName;

    @ApiModelProperty(value = "质保期", example = "1年", required = true)
    private String warrantyPeriod;

    @ApiModelProperty(value = "付款要求", example = "付款要求")
    private String paymentRequirements;

    /**
     * 投资编号
     */
    private String extInvestNo;

    /**
     * 发票类型
     */
    private String extInvoiceType;
    /**
     * 税率（%）
     */
    private String extTaxRate;

    /**
     * 模板初始化
     * @param bidNoticeDTO
     * @param bidNoticeInternalDTO
     * @param bidNoticeDetailDTO
     * @param group
     * @param extSouOrder
     * @param caDTO
     * @param extSouItem
     * @return
     */
    public static BidNoticeInternalTemplateDTO create(BidNoticeDTO bidNoticeDTO, BidNoticeInternalDTO bidNoticeInternalDTO, BidNoticeDetailDTO bidNoticeDetailDTO, ExtSouGroup group, ExtSouProject extSouProject, ExtSouOrder extSouOrder, CaDTO caDTO, ExtSouItem extSouItem,List<DictItemDTO> invoiceTypeItems){
        BidNoticeInternalTemplateDTO templateDTO = new BidNoticeInternalTemplateDTO();
        //发送对象
        templateDTO.setDemandDepartmentName(getEmptyStrIfNull(bidNoticeInternalDTO.getDemandDepartmentName()));
        //中标商相关
        if(ObjectUtil.isNotEmpty(bidNoticeDetailDTO)){
           if(ObjectUtil.isNotNull(bidNoticeDetailDTO.getWinAmount())){
               templateDTO.setBigWinAmount(NumberToChinese.convertNumberToChineseAmountWithWan(bidNoticeDetailDTO.getWinAmount()));
               templateDTO.setSmallWinAmount(NumberFormatUtils.wanToThousand(bidNoticeDetailDTO.getWinAmount()));
           } else {
               templateDTO.setBigWinAmount("");
               templateDTO.setSmallWinAmount("");
           }
           templateDTO.setVendorName(getEmptyStrIfNull(bidNoticeDetailDTO.getVendorName()));
           templateDTO.setContractPeriod(getEmptyStrIfNull(bidNoticeDetailDTO.getContractPeriod()));
        }
        //投标人相关
        if(ObjectUtil.isNotNull(extSouOrder)){
            templateDTO.setExtTenderPhone(getEmptyStrIfNull(extSouOrder.getExtTenderPhone()));
        }
        //支付相关
        if(ObjectUtil.isNotNull(caDTO)){
            templateDTO.setPaymentRequirements(getEmptyStrIfNull(caDTO.getPaymentRequirements()));
            templateDTO.setWarrantyPeriod(getEmptyStrIfNull(caDTO.getWarrantyPeriod()));

        }
        //项目相关
        templateDTO.setExtProjectNo(getEmptyStrIfNull(bidNoticeDTO.getExtProjectNo()));
        templateDTO.setSouName(getEmptyStrIfNull(bidNoticeDTO.getSouName()));
        //招标相关
        if(ObjectUtil.isNotNull(group)){
            templateDTO.setSouPrincipal(getEmptyStrIfNull(group.getFullName()));
        }
        //发票和税率
        if(ObjectUtil.isNotNull(extSouItem)){
            //查询字典获取中文名
            String invoiceTypeDesc = getDictName(invoiceTypeItems,extSouItem.getExtInvoiceType());
            templateDTO.setExtInvoiceType(getEmptyStrIfNull(invoiceTypeDesc));
            templateDTO.setExtTaxRate(ObjectUtil.isNotEmpty(extSouItem.getExtTaxRate())?extSouItem.getExtTaxRate().toString()+"%":"XXXX%");
        }
        //寻源相关
        if(ObjectUtil.isNotNull(extSouProject)){
            //投资编码
            templateDTO.setExtInvestNo(getEmptyStrIfNull(extSouProject.getExtInvestNo()));
        }

        return templateDTO;
    }

    private static String getDictName(List<DictItemDTO> invoiceTypeItems, String extInvoiceType) {
        String dictItemName = null;
        if(CollUtil.isNotEmpty(invoiceTypeItems)){
            List<String> names = invoiceTypeItems.stream().filter(e->e.getDictItemCode().equals(extInvoiceType)).map(DictItemDTO::getDictItemName).collect(Collectors.toList());
            if(CollUtil.isNotEmpty(names)){
                dictItemName = names.get(0);
            }
        }
        return dictItemName;
    }

    private static String getEmptyStrIfNull(String str){
        return ObjectUtil.isEmpty(str)?"XXXX":str;
    }


    /**
     * 转化为模板参数
     * @return
     */
    public Map<String,Object> toTemplateParams(){
        Field[] fields  = ReflectUtil.getFields(BidNoticeInternalTemplateDTO.class);
        Map<String,Object> params = new HashMap<>(16);
        for (Field field:fields){
            String name = field.getName();
            Object value = ReflectUtil.getFieldValue(this,field);
            if(ObjectUtil.isNotNull(value)){
                params.put(name,value);
            }
        }
        return params;
    }

}
