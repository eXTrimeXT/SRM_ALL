package com.midea.cloud.srm.model.pj.changchengapi.sign.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: panmq
 * @Date: 2024/05/08/ $
 * @Description: 契约锁节点回调请求参数
 */
@Data
@ApiModel("契约锁节点回调请求参数")
public class NodeCallbackRequestParamDto {

    @ApiModelProperty("合同ID")
    String contractId;
    @ApiModelProperty("签署方编号")
    String tenantId;
    @ApiModelProperty("签署方名称")
    String tenantName;
    @ApiModelProperty("文件自定义编号")
    String sn;
    @ApiModelProperty("回调类型")
    String type;
    @ApiModelProperty("联系方式")
    String contact;
    @ApiModelProperty("操作人名字")
    String operatorName;
    @ApiModelProperty("操作人手机")
    String operatorMobile;
    @ApiModelProperty("操作人员工编号")
    String operatorNumber;
    @ApiModelProperty("经办人员工编号")
    String receiverNumber;
    @ApiModelProperty("文件状态")
    String status;
}
