package com.midea.cloud.srm.model.sou.req;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 邀请供应商历史表
 *
 * @author xiaym13 xiaym13@meicloud.com
 * @since 1.0.0 2023-10-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_npm_sou_invite_history")
public class SouInviteHistory extends BaseEntity {
    /**
     * 主键
     */
    @TableId
    private Long inviteHistoryId;
    /**
     * 邀请供应商头ID
     */
    private Long inviteHeadId;
    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 来源单类型(寻源单RFP,申请单PR)
     */
    private String souType;

    /**
     * 来源单主键ID
     */
    private Long souId;

    /**
     * 来源单号
     */
    private String souNo;

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
     * 电话
     */
    private String phone;
}
