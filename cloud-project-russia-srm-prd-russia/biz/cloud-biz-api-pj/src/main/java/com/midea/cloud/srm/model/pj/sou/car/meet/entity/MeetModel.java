package com.midea.cloud.srm.model.pj.sou.car.meet.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <pre>
 *  会议管理-议题-议题模板
 * </pre>
 *
 * @author lianjh7@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: May 10, 2022 10:56:19 AM
 *  修改内容:
 * </pre>
 */

@ApiModel(description = "会议管理-议题-议题模板")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_car_meet_model")
public class MeetModel extends BaseEntity {

    private static final long serialVersionUID = -7782881073075598375L;
    /**
     * ID
     */
    @ApiModelProperty("ID")
    @TableId("MODEL_ID")
    private Long modelId;
    /**
     * 上会类型
     */
    @ApiModelProperty("上会类型")
    @TableField("TOPIC_TYPE")
    private String topicType;
    /**
     * 议题模型编号
     */
    @ApiModelProperty("议题模型编号（单据编码生成规则 ：SequenceCodeConstant.SEQ_MEET_MODEL_CODE）")
    @TableField("MODEL_CODE")
    private String modelCode;
    /**
     * 议题模型名称
     */
    @ApiModelProperty("议题模型名称")
    @TableField("MODEL_NAME")
    private String modelName;
    /**
     * 状态
     */
    @ApiModelProperty("状态：生效-Y / 失效-N")
    @TableField("STATUS")
    private Enable status;
}