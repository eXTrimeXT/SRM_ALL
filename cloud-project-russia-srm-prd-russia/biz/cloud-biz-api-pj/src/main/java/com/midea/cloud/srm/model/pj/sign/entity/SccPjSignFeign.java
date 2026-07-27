package com.midea.cloud.srm.model.pj.sign.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 契约回调模块feign路径配置
 * @author huangbf3
 * @date 2023-09-21
 */
@Data
@TableName("scc_pj_sign_feign")
public class SccPjSignFeign extends BaseEntity {

 @ApiModelProperty("主键ID")
 @TableId("SIGN_FEIGN_ID")
 private Long signFeignId;

 @ApiModelProperty("FEIGN的url前缀值")
 @TableField("FEIGN_URL_PATH")
 private String feignUrlPath;

 @ApiModelProperty("FEIGN的文件全路径值")
 @TableField("FEIGN_CLASS_PATH")
 private String feignClassPath;
}

