package com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.order;

import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouOrderItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItemHis;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 竞价openAPI - 报价行历史信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiCompOrderItemHisInfoVO extends SouOrderItem {

    /** @see CompSouOrderItem#getFormulaResult */
    @ApiModelProperty("供应商填写的公式报价json")
    private String formulaResult;

    /** @see CompSouOrderItem#getSubmitById */
    @ApiModelProperty("提交人ID")
    private Long submitById;

    /** @see CompSouOrderItem#getSubmitBy */
    @ApiModelProperty("提交人账号")
    private String submitBy;

    /** @see CompSouOrderItem#getSubmitByIp */
    @ApiModelProperty("提交人IP")
    private String submitByIp;

    /** @see CompSouOrderItem#getSubmitFullName */
    @ApiModelProperty("提交人昵称")
    private String submitFullName;

    /** @see CompSouOrderItem#getSubmitTime */
    @ApiModelProperty("提交时间")
    private Date submitTime;

    @ApiModelProperty("阶梯报价")
    private List<SouOrderItemHis> ladderPriceList;

}
