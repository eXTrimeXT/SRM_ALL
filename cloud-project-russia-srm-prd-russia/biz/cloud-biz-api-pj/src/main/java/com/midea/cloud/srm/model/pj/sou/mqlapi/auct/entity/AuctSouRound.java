package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.extapi.sou.auct.entity.ExtAuctSouRound;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouRound;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 竞价 MQL - 轮次
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/15
 */
@Data
@TableName("scc_sou_auct_round")
@EqualsAndHashCode(callSuper = true)
public class AuctSouRound extends ExtAuctSouRound {

    @ApiModelProperty("ID")
    @TableId("ROUND_ID")
    private Long roundId;

    @ApiModelProperty("寻源单ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    @ApiModelProperty("轮次")
    @TableField("ROUND")
    private Integer round;

    /**
     * {@link SouRound#getOrderEndTime} 时间可能会随着竞价延时机制而改变，
     * 而这个字段不会随着竞价延时机制改变
     */
    @ApiModelProperty("原始报价截止时间")
    @TableField("ORIGIN_ORDER_END_TIME")
    private Date originOrderEndTime;

    @TableField("EXTEND_COUNT")
    @ApiModelProperty("触发延时的次数")
    private Integer extendCount;

}
