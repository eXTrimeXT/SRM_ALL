package com.midea.cloud.srm.model.sou.req;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 邀请供应商头表
 *
 * @author xiaym13 xiaym13@meicloud.com
 * @since 1.0.0 2023-10-13
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("scc_npm_sou_invite_head")
public class SouInviteHead extends BaseEntity {
    /**
     * 主键
     */
    @TableId
    private Long inviteHeadId;

    /**
     * 供应商id
     */
    private Long vendorId;

    /**
     * 供应商编码/企业标识
     */
    private String vendorCode;

    /**
     * 供应商名称
     */
    private String vendorName;

    /**
     * 是否被推荐（Y是，N否）
     */
    private String isIntelligentBid;

    /**
     * 联系人
     */
    private String contactName;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 主要客户
     */
    private String mainCustomers;
    /**
     * 投标次数
     */
    private Integer bidCount;
    /**
     * 中标次数
     */
    private Integer succBidCount;

    /**
     * 废标次数
     */
    private Integer invalidBidCount;
    /**
     * 资质
     */
    @TableField(exist = false)
    private String pjQualifications;
    /**
     * 品牌
     */
    @TableField(exist = false)
    private String ceeaAgentBrand;
    /**
     * 供应商品类ID
     */
    @TableField(exist = false)
    private Long categoryId;
    /**
     * 供应商品类
     */
    @TableField(exist = false)
    private String categoryName;

    /**
     * 成立年限
     */
    @TableField(exist = false)
    private String companyCreationYear;

    /**
     * 注册资金
     */
    @TableField(exist = false)
    private String registeredCapital;
    /**
     * 注册资金
     */
    @TableField(exist = false)
    private String registCurrency;
    /**
     * 注册资金
     */
    @TableField(exist = false)
    private String registCurrencyName;

    /**
     * 公司地址
     */
    @TableField(exist = false)
    private String companyAddress;
    /**
     * 首次参与投标时间
     */
    @TableField(exist = false)
    private Date bidBeginTime;
    /**
     * 最后一次参与招标的时间
     */
    @TableField(exist = false)
    private Date bidLastTime;
    /**
     * orgBuName
     */
    @TableField(exist = false)
    private String orgBuName;
    /**
     *
     */
    @TableField(exist = false)
    private String orgName;
    /**
     *
     */
    @TableField(exist = false)
    private String projectName;
    /**
     * 注册资金
     */
    @TableField(exist = false)
    private String beginRegisteredCapital;
    /**
     * 注册资金
     */
    @TableField(exist = false)
    private String endRegisteredCapital;
    /**
     * 成立年限
     */
    @TableField(exist = false)
    private String beginCompanyCreationYear;
    /**
     * 成立年限
     */
    @TableField(exist = false)
    private String endCompanyCreationYear;
    @ApiModelProperty("是否智能推荐查询")
    @TableField(exist = false)
    private String isIr;

    @ApiModelProperty("智能推荐")
    @TableField(exist = false)
    private String smartRecomm;

    @ApiModelProperty("智能推荐品类条件拼接")
    @TableField(exist = false)
    private String irCategory;

    @ApiModelProperty("是否重点监督")
    @TableField(exist = false)
    private String keySupervisionFlag;

    @ApiModelProperty("是否时间受限")
    @TableField(exist = false)
    private String timelimitflag;

    @ApiModelProperty("供应商状态")
    @TableField(exist = false)
    private String pjCompanyStatus;
    @ApiModelProperty("供应商品类组织状态")
    @TableField(exist = false)
    private String pjOrgStatus;
    @ApiModelProperty("供应商品类状态")
    @TableField(exist = false)
    private String pjCategoryStatus;
    @ApiModelProperty("供应商品类服务状态")
    @TableField(exist = false)
    private String serviceStatus;
}
