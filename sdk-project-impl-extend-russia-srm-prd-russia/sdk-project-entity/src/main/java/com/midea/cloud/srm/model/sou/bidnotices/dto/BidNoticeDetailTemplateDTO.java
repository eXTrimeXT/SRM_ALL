package com.midea.cloud.srm.model.sou.bidnotices.dto;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.midea.cloud.srm.model.sou.bidnotices.NumberFormatUtils;
import com.midea.cloud.srm.model.sou.bidnotices.NumberToChinese;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouGroup;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 100014336
 */
@Data
public class BidNoticeDetailTemplateDTO {

    @ApiModelProperty(value = "中标供应商名称", example = "供应商1")
    private String vendorName;

    @ApiModelProperty(value = "项目名称", example = "项目1")
    private String souName;

    @ApiModelProperty(value = "招标项目编号", example = "001")
    private String extProjectNo;

    @ApiModelProperty(value = "小写金额", example = "12,000")
    private String smallWinAmount;

    @ApiModelProperty(value = "大写金额", example = "人民币壹佰贰拾万元整")
    private String bigWinAmount;

    @ApiModelProperty(value = "合同签署单位名称集合,逗号分割", example = "公司A")
    private String contractSignUnit;

    @ApiModelProperty(value = "招标技术负责人", example = "李四")
    private String extTechPrincipal;

    @ApiModelProperty(value = "技术负责人联系电话", example = "13800138000")
    private String extTechPhone;

    @ApiModelProperty(value = "招标负责人姓名", example = "张三")
    private String souPrincipal;

    @ApiModelProperty(value = "招标负责人联系电话", example = "张三")
    private String souPrincipalPhone;

    @ApiModelProperty(value = "合同周期", example = "2022-01-01至2023-01-01")
    private String contractPeriod;




    public static BidNoticeDetailTemplateDTO createBidNoticeDetail(BidNoticeDTO bidNoticeDTO, BidNoticeDetailDTO noticeDetailDTO, ExtSouGroup group){
        BidNoticeDetailTemplateDTO bidNoticeDetailTemplateDTO = new BidNoticeDetailTemplateDTO();
        bidNoticeDetailTemplateDTO.setSouName(getEmptyStrIfNull(bidNoticeDTO.getSouName()));
        bidNoticeDetailTemplateDTO.setExtTechPhone(getEmptyStrIfNull(bidNoticeDTO.getExtTechPhone()));
        bidNoticeDetailTemplateDTO.setExtTechPrincipal(getEmptyStrIfNull(bidNoticeDTO.getExtTechPrincipal()));
        bidNoticeDetailTemplateDTO.setExtProjectNo(getEmptyStrIfNull(bidNoticeDTO.getExtProjectNo()));
        bidNoticeDetailTemplateDTO.setSouPrincipal(getEmptyStrIfNull(group.getFullName()));
        bidNoticeDetailTemplateDTO.setSouPrincipalPhone(getEmptyStrIfNull(group.getPhone()));

        if(noticeDetailDTO.getWinAmount()!=null){
            bidNoticeDetailTemplateDTO.setBigWinAmount(NumberToChinese.convertNumberToChineseAmountWithWan(noticeDetailDTO.getWinAmount()));
            bidNoticeDetailTemplateDTO.setSmallWinAmount(NumberFormatUtils.wanToThousand(noticeDetailDTO.getWinAmount()));
        } else {
            bidNoticeDetailTemplateDTO.setBigWinAmount("XXXX");
            bidNoticeDetailTemplateDTO.setSmallWinAmount("XXXX");
        }
        bidNoticeDetailTemplateDTO.setContractPeriod(getEmptyStrIfNull(noticeDetailDTO.getContractPeriod()));
        bidNoticeDetailTemplateDTO.setContractSignUnit(getEmptyStrIfNull(noticeDetailDTO.getContractSignUnit()));
        bidNoticeDetailTemplateDTO.setVendorName(getEmptyStrIfNull(noticeDetailDTO.getVendorName()));
        return bidNoticeDetailTemplateDTO;
    }

    /**
     * 转化为模板参数
     * @return
     */
    public  Map<String,Object> toTemplateParams(){
       Field[] fields  = ReflectUtil.getFields(BidNoticeDetailTemplateDTO.class);
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

    private static String getEmptyStrIfNull(String str){
        return ObjectUtil.isEmpty(str)?"XXXX":str;
    }




}
