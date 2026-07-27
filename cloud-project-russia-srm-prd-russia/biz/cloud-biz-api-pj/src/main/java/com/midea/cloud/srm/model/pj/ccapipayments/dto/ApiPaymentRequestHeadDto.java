package com.midea.cloud.srm.model.pj.ccapipayments.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: panmq
 * @Date: 2024/04/02/ $
 * @Description: 批量付款保存及自动提交接口-请求头实体类
 */
@ApiModel("批量付款保存及自动提交接口-请求头实体类")
@Data
public class ApiPaymentRequestHeadDto extends BaseDTO {

    @ApiModelProperty("创建者工号")
    private String creator;
    @ApiModelProperty("系统标识")
    private String sysFlag;
    @ApiModelProperty("公司代码")
    private String corporationCode;
    @ApiModelProperty("公司名称")
    private String currencyName;
    @ApiModelProperty("时间 格式：yyyy-MM-dd HH:mm:ss")
    private String paymentDate;
    @ApiModelProperty("申请说明")
    private String requestNote;
    @ApiModelProperty("组织代码")
    private String organizationCode;
    @ApiModelProperty("业务货币代码")
    private String currencyCode;
    @ApiModelProperty("对接文件 默认：true")
    private Boolean fileFlag;
    @ApiModelProperty("单据类型 默认：BATCH_IMPORT_PAYMNET")
    private String paymentType;
    @ApiModelProperty("资金预算部门代码")
    private String budgetDepartmentCode;
    @ApiModelProperty("资金预算部门名称")
    private String budgetDepartmentName;


}
