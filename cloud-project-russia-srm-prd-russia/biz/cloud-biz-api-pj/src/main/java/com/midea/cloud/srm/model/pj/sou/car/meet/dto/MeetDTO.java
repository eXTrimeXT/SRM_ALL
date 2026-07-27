package com.midea.cloud.srm.model.pj.sou.car.meet.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.pj.sou.car.meet.entity.MeetTodo;
import com.midea.cloud.srm.model.pj.sou.car.meet.entity.MeetTopic;
import com.midea.cloud.srm.model.pj.sou.car.meet.entity.Meet;
import com.midea.cloud.srm.model.pj.sou.car.meet.entity.MeetMember;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
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
 * 修改日期: 2022/6/16 16:48
 * 修改内容:
 * </pre>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetDTO extends Meet {

    /**
     * 会议成员数据列表
     */
    @TableField(exist = false)
    private List<MeetMember> meetMemberList;
    /**
     * 议题数据列表
     */
    @TableField(exist = false)
    private List<MeetTopic> meetTopicList;
    /**
     * 待办数据列表列表
     */
    @TableField(exist = false)
    private List<MeetTodo> meetTodoList;
    /**
     * 创建时间-开始日期
     */
    @TableField(exist = false)
    private Date createdStartDate;
    /**
     * 创建-结束日期
     */
    @TableField(exist = false)
    private Date createdEndDate;
    /**
     * 会议开始日期
     */
    @TableField(exist = false)
    private Date meetStartDate;
    /**
     * 会议结束日期
     */
    @TableField(exist = false)
    private Date meetEndDate;
    /**
     * 是否是这个会议中议题的决策人
     */
    @TableField(exist = false)
    private Boolean enableMaker;
}
