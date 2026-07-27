package com.midea.cloud.srm.model.pj.changchengapi.bpm.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * bpm审批发起记录表
 * @author huangbf3
 * @date 2023-09-25
 */
@Data
@Accessors(chain = true)
@TableName("scc_bpm_start_record")
public class BpmStartRecord extends BaseEntity<BpmStartRecord> {
 /**
  * ID
  */
 @TableId
 private Long bpmStartRecordId;

 /**
  * 传输内容
  */
 private String serviceInfo;

 /**
  * 返回信息
  */
 private String returnInfo;

 /**
  * 返回信息
  */
 private Long businessId;

 /**
  * 审批流模板
  */
 private String bussinessType;
}

