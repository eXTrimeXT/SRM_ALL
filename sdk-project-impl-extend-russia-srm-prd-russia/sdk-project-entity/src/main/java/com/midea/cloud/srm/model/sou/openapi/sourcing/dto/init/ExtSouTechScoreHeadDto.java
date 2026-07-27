package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouTechScoreHead;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description scc_sou_tech_score_head
 * @author panmq
 * @date 2023-10-09
 */
@Data
public class ExtSouTechScoreHeadDto extends ExtSouTechScoreHead {

      @ApiModelProperty("招标单号")
      private String souNo;
      /**
       * ext_project_no
       */
      @ApiModelProperty("招标项目编号")
      private String extProjectNo;

      @ApiModelProperty("项目名称")
      private String souName;

      @ApiModelProperty("公司")
      private String extOrgOuName;

      @ApiModelProperty("板块")
      private String extOrgBuName;

      @ApiModelProperty("单据状态")
      private String projectStatus;

      @ApiModelProperty("评标人")
      private String fullName;

      @ApiModelProperty("评标人账号")
      private String userName;

      @ApiModelProperty("评标人ID")
      private Long userId;

      @ApiModelProperty("电话")
      private String phone;

      /**
       * 专家等级
       */
      @ApiModelProperty("专家等级")
      private String extExpertLevel;

      @ApiModelProperty("角色-SouGroupRoleEnum(字典值:SOU_GROUP_ROLE)")
      private String groupRole;

}



