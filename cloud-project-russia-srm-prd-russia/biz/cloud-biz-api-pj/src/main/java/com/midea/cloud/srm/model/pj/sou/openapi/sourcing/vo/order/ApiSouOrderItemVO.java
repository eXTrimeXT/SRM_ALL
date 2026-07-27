package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order;

import com.github.pagehelper.Page;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 寻源openAPI - 报价明细
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouOrderItemVO extends SouOrderItem {

    @ApiModelProperty("阶梯报价")
    private List<SouOrderItemHis> ladderPriceList;

    // --------------------------------------------------- 冗余物料需求信息、供应商信息 -------------------------------------------------
    /** @see SouItem#getOrgOuId */
    @ApiModelProperty("业务实体ID")
    private Long orgOuId;

    /** @see SouItem#getOrgOuCode */
    @ApiModelProperty("业务实体编码")
    private String orgOuCode;

    /** @see SouItem#getOrgOuName */
    @ApiModelProperty("业务实体名称")
    private String orgOuName;

    /** @see SouItem#getOrgInvId */
    @ApiModelProperty("库存组织ID")
    private Long orgInvId;

    /** @see SouItem#getOrgInvCode */
    @ApiModelProperty("库存组织编码")
    private String orgInvCode;

    /** @see SouItem#getOrgInvName */
    @ApiModelProperty("库存组织名称")
    private String orgInvName;

    /** @see SouItem#getItemGroup */
    @ApiModelProperty("物料组合(用于组合报价场景)")
    private String itemGroup;

    /** @see SouItem#getNoCodeItem */
    @ApiModelProperty("是否无料号物料(Y/N)")
    private Enable noCodeItem;

    /** @see SouItem#getRequireDate */
    @ApiModelProperty("需求时间")
    private Date requireDate;

    /** @see SouItem#getIsLadder */
    @ApiModelProperty("是否阶梯报价")
    private Enable isLadder;

    /** @see SouItem#getBuyAmount */
    @ApiModelProperty("预计采购金额")
    private BigDecimal buyAmount;

    /** @see SouItem#getSourceFromType */
    @ApiModelProperty("来源类型(冗余字段) [字典：SOU_SOURCE_FROM_TYPE]")
    private String sourceFromType;

    /** @see SouItem#getSourceFromId */
    @ApiModelProperty("来源单据ID(冗余字段)")
    private Long sourceFromId;

    /** @see SouItem#getSourceFromNo */
    @ApiModelProperty("来源单据号(冗余字段)")
    private String sourceFromNo;

    /** @see SouItem#getSourceFromLineId */
    @ApiModelProperty("来源单据行ID")
    private Long sourceFromLineId;

    /** @see SouItem#getSourceFromLineNo */
    @ApiModelProperty("来源单据行号")
    private Long sourceFromLineNo;

    /** @see SouVendor#getVendorCode */
    @ApiModelProperty("供应商编码")
    private String vendorCode;

    /** @see SouVendor#getVendorName */
    @ApiModelProperty("供应商名称")
    private String vendorName;

    /** @see SouOrder#getSubmitById */
    @ApiModelProperty("提交人ID")
    private Long submitById;

    /** @see SouOrder#getSubmitBy */
    @ApiModelProperty("提交人账号")
    private String submitBy;

    /** @see SouOrder#getSubmitByIp */
    @ApiModelProperty("提交人IP")
    private String submitByIp;

    /** @see SouOrder#getSubmitFullName */
    @ApiModelProperty("提交人昵称")
    private String submitFullName;

    /** @see SouOrder#getSubmitTime */
    @ApiModelProperty("提交时间")
    private Date submitTime;

    @SuppressWarnings("rawtypes")
    public static List<ApiSouOrderItemVO> convertApiVO(List<SouOrderItem> orderItemList,
                                                       List<SouItem> itemList,
                                                       List<SouOrder> orderList,
                                                       List<SouVendor> vendorList) {
        List<ApiSouOrderItemVO> voList;
        if (orderItemList instanceof Page) {
            voList = new Page<>();
            ((Page)voList).setTotal(((Page)orderItemList).getTotal());
            ((Page)voList).setPageSize(((Page)orderItemList).getPageSize());
            ((Page)voList).setPageNum(((Page)orderItemList).getPageNum());
        } else {
            voList = new ArrayList<>(orderItemList.size());
        }

        Map<Long/* souItemId */, SouItem> itemMap = itemList.stream().collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));
        Map<Long/* orderId */, SouOrder> orderMap = orderList.stream().collect(Collectors.toMap(SouOrder::getOrderId, Function.identity()));
        Map<Long/* vendorId */, SouVendor> vendorMap = vendorList.stream().collect(Collectors.toMap(SouVendor::getVendorId, Function.identity()));

        orderItemList.forEach(oi -> {
            ApiSouOrderItemVO vo = SouObjectXUtil.convertTargetObj(oi, ApiSouOrderItemVO.class);
            voList.add(vo);

            SouItem item = itemMap.get(vo.getSouItemId());
            BeanUtils.copyProperties(item, vo);

            SouOrder order = orderMap.get(vo.getOrderId());
            BeanUtils.copyProperties(order, vo);

            SouVendor vendor = vendorMap.get(vo.getVendorId());
            BeanUtils.copyProperties(vendor, vo);
        });
        return voList;
    }

}
