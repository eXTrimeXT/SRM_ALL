package com.midea.cloud.srm.model.pj.todowithbpm.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: panmq
 * @Date: 2024/03/19/ $
 * @Description: 待办记录表实体类
 */
@Data
@ApiModel("待办记录表实体类")
public class SccPjTodowithbpmDto extends BaseDTO {

   @ApiModelProperty("主键")
   private Long todoId;
   @ApiModelProperty("业务单据id")
   private Long businessId;
   @ApiModelProperty("流程模板编码")
   private String businessType;
   @ApiModelProperty("标题")
   private String processTitle;
   @ApiModelProperty("待办人账号")
   private String todoUsername;
   @ApiModelProperty("状态：todo-待办 havedone-已办")
   private String todoStatus;
}
