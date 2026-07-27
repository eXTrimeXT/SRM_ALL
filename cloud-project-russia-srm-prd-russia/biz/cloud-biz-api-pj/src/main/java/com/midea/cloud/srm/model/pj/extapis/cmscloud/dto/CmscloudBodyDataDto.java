package com.midea.cloud.srm.model.pj.extapis.cmscloud.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: panmq
 * @Date: 2024/03/04/ $
 * @Description: 财务共享-付款结果回推 请求体数据
 */
@Data
@ApiModel("财务共享-付款结果回推 请求体数据")
public class CmscloudBodyDataDto {

    @ApiModelProperty("请求ID")
    private String requestItemId;

    /**
     * 共享单号
     */
    @ApiModelProperty("共享单号")
    private String businessNo;
    /**
     * 付款性质名称
     */
    @ApiModelProperty("付款性质名称")
    private String paymentNatureName;
    /**
     * 序号
     */
    @ApiModelProperty("序号")
    private Integer cmsIndex;
    /**
     * 付款时间, 格式：YYYY-_M_M-DD _H_H:MM:SS
     */
    @ApiModelProperty("付款时间, 格式：YYYY-_M_M-DD _H_H:MM:SS")
    private String paymentDate;
    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private String status;
    /**
     * 付款描述
     */
    @ApiModelProperty("付款描述")
    private String paymentDesc;
    /**
     * 供应商编码
     */
    @ApiModelProperty("供应商编码")
    private String supplyCode;
    /**
     * 回单文件URL
     */
    @ApiModelProperty("回单文件URL")
    private String receiptFileUrl;
    /**
     * 回单文件名称
     */
    @ApiModelProperty("回单文件名称")
    private String receiptFileName;

    @ApiModelProperty("单据处理结果编码")
    private String businessResultCode;

    @ApiModelProperty("单据处理结果信息")
    private String businessResultMsg;
}
