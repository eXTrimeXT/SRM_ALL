package com.midea.cloud.srm.model.pj.sou.inq.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtInqSouQuotePriceCompare;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 简易询价 - 评选比价信息(缓存)
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/01/19
 */
@ApiModel(description = "简易询价 - 评选比价信息(缓存)")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_sou_inq_price_compare")
public class InqSouQuotePriceCompare extends ExtInqSouQuotePriceCompare {

    /**
     * 简易询价 - 评选比价信息(缓存)ID
     */
    @ApiModelProperty("简易询价 - 评选比价信息(缓存)ID")
    @TableId("SOU_ORDER_PRICE_COMPARE_ID")
    private Long souOrderPriceCompareId;

    /**
     * 寻源核心-询价单ID
     *
     * @see SouProject#getProjectId()
     */
    @ApiModelProperty("寻源核心-询价单ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    /**
     * 寻源核心-需求物料ID
     *
     * @see SouItem#getSouItemId()
     */
    @ApiModelProperty("寻源核心-需求物料ID")
    @TableField("SOU_ITEM_ID")
    private Long souItemId;

    /**
     * 轮次
     * PS: 不同轮次，数据可能不一样
     */
    @ApiModelProperty("轮次 PS: 不同轮次，数据可能不一样")
    @TableField("ROUND")
    private Integer round;

    /**
     * 缓存的比价信息
     */
    @ApiModelProperty("缓存的比价信息")
    @TableField("JSON_VALUE")
    private String jsonValue;

}
