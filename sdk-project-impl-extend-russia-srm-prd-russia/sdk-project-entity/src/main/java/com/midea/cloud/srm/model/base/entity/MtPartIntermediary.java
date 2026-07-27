package com.midea.cloud.srm.model.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import lombok.Data;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@TableName("mt_part_intermediary")
public class MtPartIntermediary extends BaseEntity {

    /**
     * id
     */
    @TableId("id")
    private Long id;

    /**
     * 物资分类编码
     */
    @TableField("CATEGORY_CODE")
    private String categoryCode;

    /**
     * 物资分类名称
     */
    @TableField("CATEGORY_NAME")
    private String categoryName;

    /**
     * 物料编码
     */
    @TableField("ITEM_CODE")
    private String itemCode;

    /**
     * 物料名称
     */
    @TableField("ITEM_NAME")
    private String itemName;

    /**
     * 物料描述
     */
    @TableField("MATERIAL_DESCRIPTION")
    private String materialDescription;

    /**
     * 计量单位
     */
    @TableField("UNIT")
    private String unit;

    /**
     * 规格型号
     */
    @TableField("SPECIFICATION")
    private String specification;

    /**
     * 申请人（工号+姓名）
     */
    @TableField("APPLY_PERSON")
    private String applyPerson;

    /**
     * 申请时间
     */
    @TableField("APPLY_TIME")
    private String applyTime;

    /**
     * 标识
     */
    @TableField("LOGO")
    private String logo;

    /**
     * 状态
     */
    @TableField("STATUS")
    private String status;

    /**
     * 是否同步
     */
    @TableField("IS_SYNC")
    private String isSync;
}
