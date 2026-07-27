package com.midea.cloud.srm.model.sou.req;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 邀请供应商行表
 *
 * @author xiaym13 xiaym13@meicloud.com
 * @since 1.0.0 2023-10-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_npm_sou_invite_item")
public class SouInviteItem extends BaseEntity {
    /**
     * 主键
     */
    @TableId
    private Long inviteItemId;
    /**
     * 邀请供应商头表主键
     */
    private Long inviteHeadId;

    /**
     * 项目编号
     */
    private String projectNo;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 板块ID
     */
    private Long orgBuId;

    /**
     * 板块编码
     */
    private String orgBuCode;

    /**
     * 板块名称
     */
    private String orgBuName;

    /**
     * 公司ID(对应产品的业务实体id)
     */
    private Long orgId;

    /**
     * 公司编码(对应产品的业务实体编码)
     */
    private String orgCode;

    /**
     * 公司名称(对应产品的业务实体名称)
     */
    private String orgName;

    /**
     * 创建时间
     */
    private Date bidCreationDate;

    /**
     * 品类ID
     */
    private Long categoryId;

    /**
     * 品类编码
     */
    private String categoryCode;

    /**
     * 品类
     */
    private String categoryName;

    /**
     * 是否投标（Y是，N否）
     */
    private String isBid;

    /**
     * 是否废标（Y是，N否）
     */
    private String isInvalidBid;

    /**
     * 是否中标（Y是，N否）
     */
    private String isSuccBid;

    /**
     * 技术得分
     */
    private BigDecimal techScore;

    /**
     * 综合得分
     */
    private BigDecimal totalScore;

    /**
     * 绩效得分
     */
    private BigDecimal performanceScore;

    /**
     * 不参与的原因
     */
    private String notParticipatingReason;
    /**
     * 供应商id
     */
    @TableField(exist = false)
    private Long vendorId;
}
