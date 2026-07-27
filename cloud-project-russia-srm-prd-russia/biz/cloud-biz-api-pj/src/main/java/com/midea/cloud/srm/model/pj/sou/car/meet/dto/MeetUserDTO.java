package com.midea.cloud.srm.model.pj.sou.car.meet.dto;


import com.midea.cloud.srm.model.pj.sou.car.meet.enums.MeetBusinessType;
import com.midea.cloud.srm.model.pj.sou.car.meet.enums.MeetUserType;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ex_nongtb
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MeetUserDTO extends User {

    /**
     * 会议主持人
     */
    private MeetUserType meetUserType;

    private MeetBusinessType meetBusinessType;
}
