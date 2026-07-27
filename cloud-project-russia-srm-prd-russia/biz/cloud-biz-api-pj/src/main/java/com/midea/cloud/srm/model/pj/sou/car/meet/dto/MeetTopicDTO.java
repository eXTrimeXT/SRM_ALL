package com.midea.cloud.srm.model.pj.sou.car.meet.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.pj.sou.car.meet.entity.MeetTopic;
import com.midea.cloud.srm.model.pj.sou.car.meet.entity.MeetTopicMember;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <pre>
 * 功能名称
 * </pre>
 *
 * @author ex_nongtb@partner.midea.com
 * @version 1.00.00
 *
 * <pre>
 * 修改记录
 * 修改后版本:
 * 修改人: ex_nongtb
 * 修改日期: 2022/6/16 15:18
 * 修改内容:
 * </pre>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetTopicDTO extends MeetTopic {
    /**
     * 议题成员
     */
    @ApiModelProperty(value = "议题成员")
    @TableField(exist = false)
    private List<MeetTopicMember> meetTopicMemberList;

    /**
     * 会议管理-议题附件
     */
    @ApiModelProperty(value = "会议管理-议题附件")
    private List<SceneFile> topicFiles;
}
