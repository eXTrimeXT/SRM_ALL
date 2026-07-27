package com.midea.cloud.srm.supcooperate.ext.storagereturns.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.mall.request.base.BaseRequestDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: for srm 京东申请售后请求DTO
 *
 * @author srm
 * @date 2024-05-20
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@Data
public class JDAfsApplyRequestDTO extends BaseRequestDTO {

    @ApiModelProperty("请求的参数:参数用&拼接（param是json字符串）")
    private String param;

    /**
     * 请求参数，仅记录用
     */
    @ApiModelProperty("京东PIN")
    private String customerPin;

    @ApiModelProperty("京东子订单号")
    private String orderId;

    @ApiModelProperty("申请批次号，同一子订单下不可重复（长度最大20）")
    private String thirdApplyId;

    @ApiModelProperty("是否有发票")
    private Boolean isHasInvoice;

    @ApiModelProperty("用户信息")
    private AfsCustomerInfo customerInfo;

    @ApiModelProperty("取件信息，即原商品如何返回京东或者卖家")
    private AfsPickupWareInfo pickwareInfo;

    @ApiModelProperty("返件信息，商品如何返回客户手中。当售后类型为换货、维修时，表示商品如何返回客户手中；当售后类型为退货时，表示退货失败商品返回客户手中的地址。")
    private AfsReturnWareInfo returnWareInfo;

    @ApiModelProperty("取件信息，即原商品如何返回京东或者卖家")
    private List<AfsApplyInfoItem> afsApplyInfoItemList;


    /**
     * 申请时用户信息
     */
    @Data
    public static class AfsCustomerInfo {

        @ApiModelProperty("用户名")
        private String customerName;

        @ApiModelProperty("联系人")
        private String customerContactName;

        @ApiModelProperty("联系电话")
        private String customerTel;

        @ApiModelProperty("手机号")
        private String customerMobilePhone;

        @ApiModelProperty("Email")
        private String customerEmail;

        @ApiModelProperty("邮编")
        private String customerPostcode;

        public JSONObject toJSON() {
            String jsonStr = JSON.toJSONString(this);
            return JSON.parseObject(jsonStr);
        }
    }

    /**
     * 申请时取件信息
     */
    @Data
    public static class AfsPickupWareInfo {

        @ApiModelProperty("取件方式。4上门取件7客户送货， 40客户发货。")
        private Integer pickwareType;

        @ApiModelProperty("取件省")
        private Integer pickWareProvince;

        @ApiModelProperty("取件市")
        private Integer pickWareCity;

        @ApiModelProperty("取件县")
        private Integer pickWareCounty;

        @ApiModelProperty("取件乡镇")
        private Integer pickWareVillage;

        @ApiModelProperty("取件街道地址")
        private String pickWareAddress;

        @ApiModelProperty("预约取件开始时间。开始时间不可早于当前时间+2小时。格式：2014-09-23 09:00:00")
        private String reserveDateBegin;

        @ApiModelProperty("预约取件结束时间。格式：2014-09-23 19:00:00")
        private String reserveDateEnd;

        public JSONObject toJSON() {
            String jsonStr = JSON.toJSONString(this);
            return JSON.parseObject(jsonStr);
        }
    }

    /**
     * 申请时返件信息
     */
    @Data
    public static class AfsReturnWareInfo {

        @ApiModelProperty("返件方式。10自营配送，20第三方配送")
        private Integer returnWareType;

        @ApiModelProperty("返件省")
        private Integer returnWareProvince;

        @ApiModelProperty("返件市")
        private Integer returnWareCity;

        @ApiModelProperty("返件县")
        private Integer returnWareCountry;

        @ApiModelProperty("返件乡镇")
        private Integer returnWareVillage;

        @ApiModelProperty("返件街道地址")
        private String returnWareAddress;

        public JSONObject toJSON() {
            String jsonStr = JSON.toJSONString(this);
            return JSON.parseObject(jsonStr);
        }
    }

    /**
     * 申请时申请条目
     */
    @Data
    public static class AfsApplyInfoItem {

        @ApiModelProperty("客户期望售后类型。10退货，20换货，30维修，80补货")
        private Integer customerExpect;

        @ApiModelProperty("商品描述信息")
        private WareDescInfo wareDescInfo;

        @ApiModelProperty("商品明细")
        private WareDetailInfo wareDetailInfo;

        public JSONObject toJSON() {
            String jsonStr = JSON.toJSONString(this);
            return JSON.parseObject(jsonStr);
        }
    }

    /**
     *
     */
    @Data
    public static class WareDescInfo {

        @ApiModelProperty("是否需要检测报告")
        private Boolean isNeedDetectionReport;

        @ApiModelProperty("是否有防损吊牌。")
        private Boolean lossPreventionTagFlag;

        @ApiModelProperty("是否有包装")
        private Boolean isHasPackage;

        @ApiModelProperty("包装描述")
        private Integer packageDesc;

        @ApiModelProperty("问题描述文字")
        private String questionDesc;

        @ApiModelProperty("问题描述图片链接。多个图片以“；”分隔")
        private String questionPic;

        public JSONObject toJSON() {
            String jsonStr = JSON.toJSONString(this);
            return JSON.parseObject(jsonStr);
        }
    }

    /**
     * 商品及其数量
     */
    @Data
    public static class WareDetailInfo {

        @ApiModelProperty("商品编号")
        private Long wareId;

        @ApiModelProperty("主商品编号")
        private Long mainWareId;

        @ApiModelProperty("商品名称")
        private String wareName;

        @ApiModelProperty("商品申请数量")
        private Integer wareNum;

        @ApiModelProperty("附件描述")
        private String wareDescribe;

        @ApiModelProperty("支付金额，即“商品单价*数量”")
        private BigDecimal payPrice;

        @ApiModelProperty("商品类型。10主商品，20赠品。")
        private Integer wareType;

        public JSONObject toJSON() {
            String jsonStr = JSON.toJSONString(this);
            return JSON.parseObject(jsonStr);
        }
    }
}
