package com.midea.cloud.srm.model.pj.sou.car.meet.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.pj.sou.car.meet.entity.MeetModel;
import com.midea.cloud.srm.model.pj.sou.car.meet.entity.MeetModelMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * <pre>
 * 会议管理-议题模板DTO
 * </pre>
 *
 * @author ex_nongtb@partner.midea.com
 * @version 1.00.00
 *
 * <pre>
 * 修改记录
 * 修改后版本:
 * 修改人: ex_nongtb
 * 修改日期: 2022/6/15 15:28
 * 修改内容:
 * </pre>
 * @date 2022/06/15
 */
@ApiModel(description = "会议管理-议题模板DTO")
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetModelDTO extends MeetModel {

    /**
     * 议题模板成员明细行
     */
    @ApiModelProperty("议题模板成员明细行")
    @TableField(exist = false)
    private List<MeetModelMember> meetModelMemberList;

    @ApiModelProperty("会议管理-议题模板查询-创建时间查询条件-起始时间")
    @TableField(exist = false)
    private Date startDate;

    @ApiModelProperty("会议管理-议题模板查询-创建时间查询条件-结束时间")
    @TableField(exist = false)
    private Date endDate;

    @ApiModelProperty(value = "会议管理-议题模板附件")
    private List<SceneFile> modelFiles;
}
