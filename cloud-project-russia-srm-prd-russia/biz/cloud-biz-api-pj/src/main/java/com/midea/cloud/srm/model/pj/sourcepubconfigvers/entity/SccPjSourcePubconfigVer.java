package com.midea.cloud.srm.model.pj.sourcepubconfigvers.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 寻源公示配置-最新版本表
 * @author zhengkai.blog.csdn.net
 * @date 2023-09-20
 */
@Data
@ApiModel("寻源公示配置-最新版本表")
public class SccPjSourcePubconfigVer extends BaseEntity {

    @TableId
    /**
     * 主键
     */
    @ApiModelProperty("主键")
    private Long pubconfigVerId;

    /**
     * 模板单号  collate utf8mb4_general_ci
     */
    @ApiModelProperty("模板单号  collate utf8mb4_general_ci")
    private String configNumber;

    /**
     * 版本号
     */
    @ApiModelProperty("版本号")
    private Long configVer;
}
