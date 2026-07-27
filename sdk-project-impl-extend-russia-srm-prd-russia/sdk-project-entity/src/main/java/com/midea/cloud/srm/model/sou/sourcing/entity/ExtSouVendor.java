package com.midea.cloud.srm.model.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel("寻源核心-供应商表")
@TableName(value = "scc_sou_vendor")
public class ExtSouVendor extends SouVendor{

    /**
     * 推荐供应商单号
     */
    private String extRecommendNo;

    @ApiModelProperty("是否查阅标书")
    private String extReadBidFlag;

    @ApiModelProperty("注册资金")
    private String extRegisterFund;

    @ApiModelProperty("成立时间")
    private Date extFounded;

    @ApiModelProperty("GSCP")
    @TableField("ext_gscp")
    private String extGscp;

    @ApiModelProperty("是否重点关注")
    private String extIsMainPoint;

    @ApiModelProperty("公司地址")
    private String extCompanyAddr;

    @ApiModelProperty("资质")
    private String extAptitude;

    @ApiModelProperty("品牌")
    private String extBrand;

    @ApiModelProperty("供应商属性")
    private String extVendorAttr;

    @ApiModelProperty("是否新供应商")
    private String extIsNewVendor;

    @ApiModelProperty("备注")
    private String extRemark;

    @ApiModelProperty("是否失信")
    private String extIsDishonesty;

    @ApiModelProperty("是否经营异常")
    private String extIsBizAnomaly;

    @ApiModelProperty("法人")
    private String extLegal;

    @ApiModelProperty("主要人员")
    private String extMainPeople;

    @ApiModelProperty("主要股东")
    private String extStockholder;

    @ApiModelProperty("是否追加供应商")
    private String extIsAddVendor;

    @ApiModelProperty("发送短信标识")
    private String extSendMsmFlag;

    @ApiModelProperty("是否从标前交流获取")
    private String extPreBid;

}
