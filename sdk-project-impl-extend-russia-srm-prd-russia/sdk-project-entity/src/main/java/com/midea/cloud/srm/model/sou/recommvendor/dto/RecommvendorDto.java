package com.midea.cloud.srm.model.sou.recommvendor.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel(description = "供应商推荐供应商表DTO")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class RecommvendorDto extends BaseDTO {
    @ApiModelProperty("ID")
    private Long souVendorId;
    @ApiModelProperty("寻源单ID")
    private Long projectId;
    @ApiModelProperty("供应商ID")
    private Long vendorId;
    @ApiModelProperty("供应商编码")
    private String vendorCode;
    @ApiModelProperty("供应商名称")
    private String vendorName;
    @ApiModelProperty("加入轮次")
    private Integer joinRound;
    @ApiModelProperty("报名状态")
    private String signUpStatus;
    @ApiModelProperty("报名时间")
    private Date signUpTime;
    @ApiModelProperty("报名驳回原因")
    private String signUpRejectReason;
    @ApiModelProperty("联系人名称")
    private String linkmanName;
    @ApiModelProperty("电话")
    private String phone;
    @ApiModelProperty("邮箱")
    private String email;
    @ApiModelProperty("排序")
    private Integer sortIndex;
    @ApiModelProperty("是否查阅标书")
    private String extReadBidFlag;
    @ApiModelProperty("注册资金")
    private String extRegisterFund;
    @ApiModelProperty("成立时间")
    private Date extFounded;
    @ApiModelProperty("gscp")
    private String extGscp;
    @ApiModelProperty("是否重点关注")
    private String extIsMainPoint;
    @ApiModelProperty("公司地址")
    private String extCompanyAddr;
    @ApiModelProperty("资质")
    private String extAptitude;
    @ApiModelProperty("品牌")
    private String extBrand;
    @ApiModelProperty("供应商")
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
    @ApiModelProperty("推荐供应商单号")
    private String extRecommendNo;

    @ApiModelProperty("时间受限")
    private String timeLimitFlag;
    @ApiModelProperty("是否单位受限")
    private String positionLimitFlag;
    @ApiModelProperty("是否品类受限")
    private String categoryLimitFlag;
    @ApiModelProperty("是否重点监督")
    private String keySupervisionFlag;

    @ApiModelProperty("是否集团黑名单")
    private String groupBlacklistFlag;
    @ApiModelProperty("联系人是否重复")
    private String contackRepeatFlag;
    @ApiModelProperty("联系人电话是否重复")
    private String telRepeatFlag;
    @ApiModelProperty("联系人邮箱是否重复")
    private String emailRepeatFlag;
    @ApiModelProperty("法人是否重复")
    private String legalRepeatFlag;
    @ApiModelProperty("股东是否重复")
    private String holderRepeatFlag;
    @ApiModelProperty("关联关系供应商是否黑名单")
    private String relBlacklistRepeatFlag;
    @ApiModelProperty("主要人员是否重复")
    private String mainPeopleRepeatFlag;

    @ApiModelProperty("来源类型")
    private String sourceType;

    @ApiModelProperty("是否从标前交流获取")
    private String extPreBid;

}
