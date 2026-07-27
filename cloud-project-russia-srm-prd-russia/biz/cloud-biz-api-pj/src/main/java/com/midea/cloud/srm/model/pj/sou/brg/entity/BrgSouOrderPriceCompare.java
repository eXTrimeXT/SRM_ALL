package com.midea.cloud.srm.model.pj.sou.brg.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.extapi.sou.brg.entity.ExtBrgSouOrderPriceCompare;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 项目式询价.评选比价信息(缓存)
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/01/19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_sou_brg_price_compare")
@ApiModel("项目式询价.评选比价信息(缓存)")
public class BrgSouOrderPriceCompare extends ExtBrgSouOrderPriceCompare {

    @TableId("order_price_compare_id")
    @ApiModelProperty("主键ID")
    private Long orderPriceCompareId;

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源单ID")
    @TableField("project_id")
    private Long projectId;

    /** @see SouItem#getSouItemId */
    @TableField("sou_item_id")
    @ApiModelProperty("需求物料ID")
    private Long souItemId;

    @TableField("round")
    @ApiModelProperty("轮次")
    private Integer round;

    @TableField("json_value")
    @ApiModelProperty("缓存的比价信息")
    private String jsonValue;

}
