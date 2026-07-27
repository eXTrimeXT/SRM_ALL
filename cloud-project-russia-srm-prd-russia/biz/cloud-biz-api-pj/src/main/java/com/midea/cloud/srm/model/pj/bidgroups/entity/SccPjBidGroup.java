package com.midea.cloud.srm.model.pj.bidgroups.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @description scc_pj_bid_group
 * @author panmq
 * @date 2023-09-25
 */
@Data
@ApiModel("scc_pj_bid_group")
public class SccPjBidGroup extends BaseEntity {
 @TableId
 /**
  * 主键ID
  */
 private Long groupId;
 /**
  * 关联招标基本信息主键ID
  */
 private Long projectId;
 /**
  * 成员账号ID
  */
 private Long userId;
 /**
  * 成员账号
  */
 private String userName;
 /**
  * 成员姓名
  */
 private String fullName;
 /**
  * 电话
  */
 private String phone;
 /**
  * 电子邮箱
  */
 private String email;
 /**
  * 岗位
  */
 private String position;
 /**
  * 角色
  */
 private String groupRole;
 /**
  * 操作权限
  */
 private String operateAuth;
 /**
  * 专家等级
  */
 private String expertLevel;
 /**
  * 评分权限
  */
 private String scoreAuth;

}

