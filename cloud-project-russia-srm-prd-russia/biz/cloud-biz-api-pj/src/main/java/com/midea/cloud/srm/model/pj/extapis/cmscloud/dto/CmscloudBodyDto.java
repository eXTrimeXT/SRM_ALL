package com.midea.cloud.srm.model.pj.extapis.cmscloud.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: panmq
 * @Date: 2024/03/04/ $
 * @Description: 财务共享-付款结果回推-请求体参数实体类
 */
@Data
@ApiModel("财务共享-付款结果回推-请求体参数实体类")
public class CmscloudBodyDto<T> {


    @ApiModelProperty("接口流水号")
    private String serialNum;

    @ApiModelProperty("接口结果编码")
    private String resultCode;

    @ApiModelProperty("接口结果信息")
    private String resultMsg;

    @ApiModelProperty("参数")
    private T data;
}
