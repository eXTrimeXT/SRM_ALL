package com.midea.cloud.srm.model.pj.sou.car.meet.param;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.pj.sou.car.meet.entity.MeetTopic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 满足主题参数
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
 * 修改日期: 2022/5/23 15:56
 * 修改内容:
 * </pre>
 * @date 2022/05/23
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetTopicParam extends MeetTopic {

    private static final long serialVersionUID = 2933144795252720778L;


    /**
     * 创建时间查询参数-时间开始
     */
    @TableField(exist = false)
    private Date startDate;

    /**
     * 创建时间查询参数-时间开始
     */
    @TableField(exist = false)
    private Date endDate;

    /**
     * 完成时查询参数-时间开始
     */
    private Date completeStartDate;

    /**
     * 完成时查询参数-时间结束
     */
    private Date completeEndDate;

}
