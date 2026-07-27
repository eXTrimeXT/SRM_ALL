package com.midea.cloud.srm.model.pj.changchengapi.bpm.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 法务-法人公司主数据
 * @author huangbf3
 * @date 2023-09-25
 */
@Data
@TableName("scc_pj_bpm_incorporated_company")
public class BpmIncorporatedCompany extends BaseEntity<BpmIncorporatedCompany> {
 /**
  * ID
  */
 @TableId
 private Long bpmIncorporatedCompanyId;
 /**
  * 公司代码
  */
  private String companyNo;
 /**
  * 法定代表人/负责人
  */
  private String operName;
 /**
  * 成立日期
  */
  private Date startDate;
 /**
  * 经营期限始
  */
  private String termStart;
 /**
  * 经营期限至
  */
  private String teamEnd;
 /**
  * 注册资本(万元)
  */
  private String registCapital;
 /**
  * 实缴资本(万元)
  */
  private String recCap;
 /**
  * 登记状态
  */
  private String registStatus;
 /**
  * 公司类型
  */
  private String companyType;
 /**
  * 所属版块
  */
  private String belongSection;
 /**
  * 发照日期
  */
  private Date checkDate;
 /**
  * 注销日期
  */
  private Date endDate;
 /**
  * 统一社会信用代码
  */
  private String creditCode;
 /**
  * 受益所有人
  */
  private String beneficialOwner;
 /**
  * 公司名称
  */
  private String companyName;
 /**
  * 注册地址
  */
  private String corporationAddress;
 /**
  * 经营范围
  */
  private String scope;
 /**
  * 创建时间
  */
  private Date createTime;
 /**
  * 更新时间
  */
  private Date updateTime;
 /**
  * 顺序号
  */
 private Integer sortNo;
 /**
  * 创建人工号
  */
 private String createUserCode;
 /**
  * 创建人姓名
  */
 private String createUserName;
 /**
  * 更新人工号
  */
 private String updateUserCode;
 /**
  * 更新人姓名
  */
 private String updateUserName;
 /**
  * 删除标识
  */
 private Integer deleteFlag;
 /**
  * 启用/禁用标识
  */
 private Integer activeFlag;
 /**
  * 备注
  */
 private String remark;
}

