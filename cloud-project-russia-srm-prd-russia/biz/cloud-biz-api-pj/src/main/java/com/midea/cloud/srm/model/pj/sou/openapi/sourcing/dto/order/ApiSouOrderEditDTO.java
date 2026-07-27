package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源openAPI - 报价单
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/28
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouOrderEditDTO extends BaseObjectX {

    /** @see SouOrder#getOrderId */
    @ApiModelProperty("寻源核心-供应商报价头ID")
    protected Long orderId;

    /** @see SouOrder#getProjectId */
    @ApiModelProperty("寻源单ID")
    protected Long projectId;

    /** @see SouOrder#getOrderNo */
    @ApiModelProperty("供应商报价单号")
    protected String orderNo;

    /** @see SouOrder#getVendorId */
    @ApiModelProperty("供应商ID")
    protected Long vendorId;

    // ----------------------------------------------------------- 提交人信息 --------------------------------------------------------------
    /** @see SouOrder#getSubmitById */
    @ApiModelProperty("提交人ID")
    protected Long submitById;

    /** @see SouOrder#getSubmitBy */
    @ApiModelProperty("提交人账号")
    protected String submitBy;

    /** @see SouOrder#getSubmitByIp */
    @ApiModelProperty("提交人IP")
    protected String submitByIp;

    /** @see SouOrder#getSubmitFullName */
    @ApiModelProperty("提交人昵称")
    protected String submitFullName;

    // ----------------------------------------------------------- 代理信息 ----------------------------------------------------------------
    /** @see SouOrder#getIsProxy */
    @ApiModelProperty("是否代理报价")
    protected Enable isProxy;

    /** @see SouOrder#getProxyDocId */
    @ApiModelProperty("代理授权文件ID")
    protected Long proxyDocId;

    /** @see SouOrder#getProxyFileName */
    @ApiModelProperty("代理授权文件名称")
    protected String proxyFileName;

    /** @see SouOrder#getProxyRemark */
    @ApiModelProperty("代理授权说明")
    protected String proxyRemark;

}
