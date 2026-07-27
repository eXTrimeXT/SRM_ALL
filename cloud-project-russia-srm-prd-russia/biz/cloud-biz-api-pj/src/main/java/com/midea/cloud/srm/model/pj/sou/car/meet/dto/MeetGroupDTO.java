package com.midea.cloud.srm.model.pj.sou.car.meet.dto;


import com.midea.cloud.srm.model.pj.sou.car.meet.entity.MeetGroup;
import com.midea.cloud.srm.model.pj.sou.car.meet.entity.MeetGroupMember;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 满足集团dto
 *
 * @author ex_nongtb
 * @date 2022/05/31
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MeetGroupDTO extends MeetGroup {
    private static final long serialVersionUID = 170602L;

    /**
     * 分组用户
     */
    private List<MeetGroupMember> groupMemberList;
}