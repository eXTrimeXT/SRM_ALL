package com.midea.cloud.srm.model.pj.hruser.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * @author huangbf3
 */
@Data
@TableName("scc_pj_user")
public class SccPjUser extends BaseEntity {

    @TableId
    private Long rowId;
    private Long id;
    private String personnelNo;
    private Integer isFormal;
    private Integer isForeign;
    private String chineseName;
    private Integer sex;
    private Long groupId;
    private String englishName;
    private String duty;
    private String position;
    private Integer state;
    private Date updateTime;
    private String groupName;
    private String dutyName;
    private String positionName;
    private String isDelete;

}
