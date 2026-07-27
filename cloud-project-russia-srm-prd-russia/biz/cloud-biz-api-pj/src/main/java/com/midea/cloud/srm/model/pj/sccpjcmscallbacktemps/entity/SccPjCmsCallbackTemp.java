package com.midea.cloud.srm.model.pj.sccpjcmscallbacktemps.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: panmq
 * @Date: 2024/03/04/ $
 * @Description:
 */

@Data
@ApiModel("财务共享-回推接口表")
@TableName(value = "scc_pj_cms_callback_temp")
public class SccPjCmsCallbackTemp extends BaseEntity {
    @TableId
    /**
     * 主键
     */
    @ApiModelProperty("主键")
    private Long cmsCallbackTempId;
    /**
     * 支付行项目唯一标识
     */
    @ApiModelProperty("支付行项目唯一标识")
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
    /**
     * 处理序号
     */
    @ApiModelProperty("处理序号")
    private String processSerialNum;
    /**
     * 处理状态，PENDING：未处理，COMPLETED：处理完成，PROCESSING：处理中，ERROR：处理错误，RETRY：需重试
     */
    @ApiModelProperty("处理状态，PENDING：未处理，COMPLETED：处理完成，PROCESSING：处理中，ERROR：处理错误，RETRY：需重试")
    private String processStatus;
    /**
     * 处理信息
     */
    @ApiModelProperty("处理信息")
    private String processMessage;
    /**
     * 处理时间
     */
    @ApiModelProperty("处理时间")
    private Date processDate;
    /**
     * 处理批次号
     */
    @ApiModelProperty("处理批次号")
    private Long processGroupId;
}
