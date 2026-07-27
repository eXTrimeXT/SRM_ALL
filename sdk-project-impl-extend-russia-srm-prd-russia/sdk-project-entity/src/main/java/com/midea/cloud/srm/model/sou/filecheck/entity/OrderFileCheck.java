package com.midea.cloud.srm.model.sou.filecheck.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <pre>
 *  围串标识别流水
 * </pre>
 *
 * @author huanghb14@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020-03-04 13:39:58
 *  修改内容:
 * </pre>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_sou_order_file_check")
@ApiModel(description = "围串标识别流水表")
public class OrderFileCheck extends BaseEntity {

    private static final long serialVersionUID = 6390591027354729862L;

    @ApiModelProperty(value = "ID")
    @TableId("ORDER_FILE_CHECK_ID")
    private Long orderFileCheckID;

    @ApiModelProperty(value = "寻源单ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    @ApiModelProperty(value = "字数类型(字典AI_CHECK_FILE_PART_TYPE)")
    @TableField("FILE_PART_TYPE")
    private String filePartType;

    @ApiModelProperty(value = "围串标识别流水号(知识大脑返回)")
    @TableField("SERIAL_NUM")
    private String serialNum;
}
