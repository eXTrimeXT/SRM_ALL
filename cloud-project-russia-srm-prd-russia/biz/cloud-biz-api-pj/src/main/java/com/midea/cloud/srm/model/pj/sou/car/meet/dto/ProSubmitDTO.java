package com.midea.cloud.srm.model.pj.sou.car.meet.dto;

import com.midea.cloud.srm.model.pj.sou.car.meet.entity.MeetTopic;
import lombok.AllArgsConstructor;
import lombok.Data;
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
 * 修改日期: 2022/5/17 17:24
 * 修改内容:
 * </pre>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProSubmitDTO {

    /**
     * 会议id
     */
    private Long meetingId;

    /**
     * 参与议题决议的人员列表
     */
    private List<MeetTopic> meetTopics;
}
