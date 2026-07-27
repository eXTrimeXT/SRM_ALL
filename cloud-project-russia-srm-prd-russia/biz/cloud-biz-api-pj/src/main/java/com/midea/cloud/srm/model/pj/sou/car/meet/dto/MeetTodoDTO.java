package com.midea.cloud.srm.model.pj.sou.car.meet.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.pj.sou.car.meet.entity.MeetTodo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * <pre>
 *      会议管理-待办DTO
 * </pre>
 *
 * @author ex_nongtb@partner.midea.com
 * @version 1.00.00
 *
 * <pre>
 * 修改记录
 * 修改后版本:
 * 修改人: ex_nongtb
 * 修改日期: 2022/6/15 13:56
 * 修改内容:
 * </pre>
 */
@ApiModel(description = "会议管理-待办DTO")
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetTodoDTO extends MeetTodo {

    @ApiModelProperty("会议管理-待办查询-创建时间查询条件-起始时间")
    @TableField(exist = false)
    private Date startDate;

    @ApiModelProperty("会议管理-待办查询-创建时间查询条件-起始时间")
    @TableField(exist = false)
    private Date endDate;
}
