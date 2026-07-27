package com.midea.cloud.srm.model.pj.changchengapi.bpm.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * bpm审批历史
 * @author huangbf3
 * @date 2023-09-25
 */
@Data
@Accessors(chain = true)
@TableName("scc_pj_bpm_task_history")
public class BpmTaskHistory extends BaseEntity<BpmTaskHistory> {
 /**
  * ID
  */
 @TableId
 private Long bpmTaskHistoryId;

 /**
  * 业务单据ID
  */
 private Long orderId;

 /**
  * 备注
  */
 private String msg;

 /**
  * 流程实例ID
  */
 private String processInstId;

 /**
  * 节点处理人
  */
 private String activityName;

 /**
  * 处理时间
  */
 private String createDate;

 /**
  * 操作类型
  */
 private String actionName;

 /**
  * 审批工号
  */
 private String createUser;

 /**
  * 审批人姓名
  */
 private String createUserName;
}

