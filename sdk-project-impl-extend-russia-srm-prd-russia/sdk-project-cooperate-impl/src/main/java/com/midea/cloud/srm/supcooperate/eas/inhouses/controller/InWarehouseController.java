package com.midea.cloud.srm.supcooperate.eas.inhouses.controller;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.meicloud.paas.audit.util.JsonUtils;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.ql.QlQueryWrapper;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.mall.request.jd.common.JDAddressRequestDTO;
import com.midea.cloud.srm.mall.result.jd.Order.ReceiveConfirmResultDTO;
import com.midea.cloud.srm.mall.result.jd.afs.AfsApplyResultDTO;
import com.midea.cloud.srm.mall.result.jd.common.AddressResultDTO;
import com.midea.cloud.srm.mall.service.jd.MallService;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.supcooperate.enums.MallTypeEnum;
import com.midea.cloud.srm.model.supcooperate.ext.ExternalMaterial;
import com.midea.cloud.srm.model.suppliercooperate.order.entry.WarehousingReturnDetail;
import com.midea.cloud.srm.model.suppliercooperate.storagereturn.openapi.dto.StorageReturnAddDTO;
import com.midea.cloud.srm.model.suppliercooperate.storagereturn.openapi.dto.StorageReturnBatchAddDTO;
import com.midea.cloud.srm.model.suppliercooperate.storagereturn.openapi.enums.StorageReturnSourceEnum;
import com.midea.cloud.srm.model.suppliercooperate.storagereturn.openapi.enums.StorageReturnTypeEnum;
import com.midea.cloud.srm.supcooperate.eas.entity.CeeaStorageReturn;
import com.midea.cloud.srm.supcooperate.eas.entity.InWarehouseInfo;
import com.midea.cloud.srm.supcooperate.eas.entity.ReceiveInfo;
import com.midea.cloud.srm.supcooperate.eas.inhouses.service.InWarehouseService;
import com.midea.cloud.srm.supcooperate.ext.deliverynotes.dto.ExtDeliveryNote;
import com.midea.cloud.srm.supcooperate.ext.deliverynotes.enums.ExtDeliveryNoteStatusEnum;
import com.midea.cloud.srm.supcooperate.ext.order.dto.ExtOrder;
import com.midea.cloud.srm.supcooperate.ext.order.dto.JDOrderDetailRequestDTO;
import com.midea.cloud.srm.supcooperate.ext.order.service.ExtOrderService;
import com.midea.cloud.srm.supcooperate.ext.storagereturns.dto.JDAfsApplyRequestDTO;
import com.midea.cloud.srm.supcooperate.mtmapping.service.ExternalMaterialService;
import com.midea.cloud.srm.supcooperate.openapi.storagereturn.service.StorageReturnOpenApiService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author aaa
 */
@Slf4j
@RestController
@RequestMapping("/sup/coop/inWarehouse")
public class InWarehouseController {
    private static final Integer NUM = 100;
    private static final String STR0000 = "0000";


    @Resource
    private StorageReturnOpenApiService storageReturnOpenApiService;

    @Resource
    private BaseClient baseClient;

    @Resource
    private InWarehouseService inWarehouseService;

    @Autowired
    private ExtOrderService extOrderService;

    @Autowired
    private MallService mallService;

    @Autowired
    protected QlService qlService;

    @Autowired
    private ExternalMaterialService externalMaterialService;

    @ApiOperation(value = "获取入库信息")
    @PostMapping("/getGoodsInfo")
    public Map<String, Object> getGoodsInfo(@RequestBody List<InWarehouseInfo> infoList) {
        Map<String, Object> resultMes = new HashMap<>(50);
        if (CollectionUtils.isEmpty(infoList)) {
            resultMes.put("code", "1");
            resultMes.put("message", "参数为空！");
            return resultMes;
        }
        try {
            Collection<Map<String, String>> codes = new ArrayList<>();
            infoList.forEach(e -> {
                Map<String, String> m = new HashMap<>(50);
                m.put("orgCode", e.getInventoryOrg());
                m.put("warehousingNum", e.getWarehousingNum());
                m.put("warehousingLineNum", e.getWarehousingLineNum());
                m.put("collectTime", e.getCollectTime());
                codes.add(m);
            });
            List<String> writebackKeys = infoList.stream().filter(e -> YesOrNo.NO.getValue().equals(e.getIsCreateNo()))
                    .map(e -> getStorageKey(e.getWarehousingNum(), e.getWarehousingLineNum())).collect(Collectors.toList());
            if (infoList.size() <= NUM) {
                dealData(inPar(infoList), codes, writebackKeys);
            } else {
                for (int i = 0; i < infoList.size()/NUM + (infoList.size() % NUM == 0 ? 0 : 1); i++) {
                    dealData(inPar(infoList.subList(i*NUM, Math.min(i*NUM + NUM, infoList.size()))), codes, writebackKeys);
                }
            }
            resultMes.put("code", "0");
            resultMes.put("message", "操作成功！");
        } catch (Exception e) {
            log.info("入库信息添加失败=={}=={}" , e.getMessage(), JSONObject.toJSONString(e));
            resultMes.put("code", "1");
            resultMes.put("message", "操作失败！");
            resultMes.put("data", infoList);
        }
        return resultMes;
    }

    @ApiOperation(value = "同步EAS收货信息")
    @PostMapping("/receiveByEas")
    public Map<String, Object> receiveByEas(@RequestBody List<ReceiveInfo> infoList) {
        log.info("receiveByEas param: {}", JSONUtil.toJsonStr(infoList));
        Map<String, Object> resultMes = new HashMap<>(50);
        if (CollectionUtils.isEmpty(infoList)) {
            resultMes.put("code", "1");
            resultMes.put("message", "参数为空！");
            return resultMes;
        }
        try {
            extOrderService.writebackReceiveQty(infoList);

            resultMes.put("code", "0");
            resultMes.put("message", "操作成功！");
        } catch (Exception e) {
            log.error("同步EAS收货信息失败：" + e.getMessage(), e);
            resultMes.put("code", "1");
            resultMes.put("message", "操作失败：" + e.getMessage());
            resultMes.put("data", infoList);
        }
        return resultMes;
    }

    public StorageReturnBatchAddDTO inPar(List<InWarehouseInfo> infoList) {
        StorageReturnBatchAddDTO storageReturnBatchAddDTO = new StorageReturnBatchAddDTO();
        //送货单
        storageReturnBatchAddDTO.setSource(StorageReturnSourceEnum.DELIVERY);
        storageReturnBatchAddDTO.setReturnDeatils(true);
        List<StorageReturnAddDTO> sraList = new ArrayList<>();
        for (InWarehouseInfo e : infoList) {
            StorageReturnAddDTO sr = new StorageReturnAddDTO();
            //事务类型
            sr.setType("01".equals(e.getWorkType()) ? StorageReturnTypeEnum.RECEIVE : StorageReturnTypeEnum.RETURN);
            //接收单号-入库单号
            sr.setReceiveOrderNo(e.getWarehousingNum());
            //接收行号-入库单行号
            sr.setReceiveOrderLineNo(Integer.valueOf(e.getWarehousingLineNum()));
            //入库/退货数量-入库数量
            sr.setReceiveNum(new BigDecimal(e.getCollectNum()));
            //送货单号-送货单号
            sr.setDeliveryNumber(e.getDeliveryNoteNum());
            //送货单号-送货单行号
            sr.setDeliveryLineNum(Integer.valueOf(e.getDeliveryNoteLineNum()));
            sraList.add(sr);
        }
        storageReturnBatchAddDTO.setList(sraList);
        return storageReturnBatchAddDTO;
    }

    public void dealData(StorageReturnBatchAddDTO par, Collection<Map<String, String>> codes, List<String> writebackKeys) throws ParseException {
        List<WarehousingReturnDetail> list = storageReturnOpenApiService.batchAdd(par);

        // 回写订单、送货单
        List<WarehousingReturnDetail> storageList = list.stream().filter(e -> writebackKeys.contains(getStorageKey(e.getReceiveOrderNo(), e.getReceiveOrderLineNo()))).collect(Collectors.toList());
        extOrderService.writebackStorageQty(storageList);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //处理库存组织信息
        Collection<String> col = new HashSet<>();
        codes.forEach(e -> col.add(e.get("orgCode")));
        Map<String, Organization> orgCodeMap = baseClient.getOrganizationsByCodes(col);
        for (WarehousingReturnDetail e : list) {
            for (Map<String, String> a : codes) {
                if (e.getReceiveOrderNo().equals(a.get("warehousingNum")) &&
                        e.getReceiveOrderLineNo().equals(Integer.valueOf(a.get("warehousingLineNum"))) &&
                        orgCodeMap.get(a.get("orgCode")) != null) {
                    LambdaUpdateChainWrapper lambdaUpdate = inWarehouseService.lambdaUpdate().
                            set(CeeaStorageReturn::getOrganizationId, orgCodeMap.get(a.get("orgCode")).getOrganizationId()).
                            set(CeeaStorageReturn::getOrganizationCode, orgCodeMap.get(a.get("orgCode")).getOrganizationCode()).
                            set(CeeaStorageReturn::getOrganizationName, orgCodeMap.get(a.get("orgCode")).getOrganizationName()).
                            set(CeeaStorageReturn::getWarehousingDate, sdf.parse(a.get("collectTime"))).

                            eq(CeeaStorageReturn::getReceiveOrderNo, e.getReceiveOrderNo()).
                            eq(CeeaStorageReturn::getReceiveOrderLineNo, e.getReceiveOrderLineNo());
                            if("RECEIVE".equals(e.getType())) {
                                lambdaUpdate.setSql("DEAL_DATE = RECEIVE_DATE");
                            } else {
                                lambdaUpdate.setSql("DEAL_DATE = RETURN_TO_SUPPLIER_DATE");
                            }

                    lambdaUpdate.update();
                    break;
                }
            }
        }

        /**
         * 处理京东订单
         * 1、当产生供应商为京东的入库单时，调用京东接口，将订单行的入库信息传递给京东
         * 2、当系统产生供应商为京东的退库单时，调用京东的申请售后信息，将该订单行的退库信息传递给京东
         *
         * 判断是入库单还是退库单,RECEIVE("入库", "RECEIVE"),RETURN("退货", "RETURN");
         */

        //todo 其中JD001为暂时设置的京东供应商编码，京东还为引入，具体编码还未知，后续需要更改
        //获取供应商编码为京东的入库明细
        List<WarehousingReturnDetail> receiveDetails = list.stream()
                .filter(detail -> "JD001".equals(detail.getVendorCode()))
                .filter(detail -> "RECEIVE".equals(detail.getType()))
                .collect(Collectors.toList());
        //获取供应商编码为京东的退库明细
        List<WarehousingReturnDetail> returnDetails = list.stream()
                .filter(detail -> "JD001".equals(detail.getVendorCode()))
                .filter(detail -> "RETURN".equals(detail.getType()))
                .collect(Collectors.toList());

        //处理入库单
        if (CollectionUtils.isNotEmpty(receiveDetails)) {
            for (WarehousingReturnDetail receiveDetail : receiveDetails) {

                //获取此入库单对应的送货单，确认送货单是否已完整入库，送货单上记录着京东订单id
                QlQueryWrapper qlQueryWrapper = QlWrappers.query("DeliveryNote")
                        .eq(ExtDeliveryNote::getDeliveryNumber,receiveDetail.getDeliveryNumber());
                List<ExtDeliveryNote> extDeliveryNotes = qlService.queryByWrapper(qlQueryWrapper, ExtDeliveryNote.class);
                //一个送货单号正常只对应一个送货单
                ExtDeliveryNote extDeliveryNote = extDeliveryNotes.get(0);

                //确认送货单是否已完整送货
                if (ExtDeliveryNoteStatusEnum.FINISHED.name().equals(extDeliveryNote.getExtStatus())) {
                    //确认已完整送货，调用京东确认收货接口
                    JDOrderDetailRequestDTO requestDTO = new JDOrderDetailRequestDTO();
                    requestDTO.setJdOrderId(extDeliveryNote.getExtJdOrderId());
                    requestDTO.setMallType(MallTypeEnum.JD.getCode());
                    //调用京东确认收货接口
                    ReceiveConfirmResultDTO confirmResultDTO = mallService.receiveConfirm(requestDTO);
                    if (confirmResultDTO.isSuccess()) {
                        //如果后续还有其他操作可以继续补充
                        log.info("京东订单:"+extDeliveryNote.getExtJdOrderId()+"已确认收货");
                    } else {
                        log.info("京东订单:"+extDeliveryNote.getExtJdOrderId()+"确认收货异常;"+confirmResultDTO.getResultMessage());
                    }
                }
            }

        }

        //处理退库单
        if (CollectionUtils.isNotEmpty(returnDetails)) {
            for (WarehousingReturnDetail returnDetail : returnDetails) {

                //获取此退库单对应的送货单，送货单上记录着京东订单id
                QlQueryWrapper extDeliveryNoteqlQueryWrapper = QlWrappers.query("DeliveryNote")
                        .eq(ExtDeliveryNote::getDeliveryNumber,returnDetail.getDeliveryNumber());
                List<ExtDeliveryNote> extDeliveryNotes = qlService.queryByWrapper(extDeliveryNoteqlQueryWrapper, ExtDeliveryNote.class);
                //一个送货单号正常只对应一个送货单
                ExtDeliveryNote extDeliveryNote = extDeliveryNotes.get(0);

                //获取京东订单对应的采购订单
                QlQueryWrapper qlQueryWrapper = QlWrappers.query("Order")
                        .eq(ExtOrder::getOrderNumber,returnDetail.getOrderNumber());
                List<ExtOrder> extOrderList = qlService.queryByWrapper(qlQueryWrapper, ExtOrder.class);
                //只对应一个订单
                ExtOrder extOrder = extOrderList.get(0);

                //组装售后请求对象
                JDAfsApplyRequestDTO jdAfsApplyRequestDTO = new JDAfsApplyRequestDTO();
                jdAfsApplyRequestDTO.setMallType(MallTypeEnum.JD.getCode());
                //组装json
                jdAfsApplyRequestDTO.setParam(this.getParam(returnDetail,extDeliveryNote,extOrder));

                //向京东推送售后信息
                AfsApplyResultDTO afsApplyResultDTO = mallService.createAfsApply(jdAfsApplyRequestDTO);
                if (afsApplyResultDTO.isSuccess()) {
                    //提交售后信息成功NJJNKNASDN
                    log.info("退库单:"+returnDetail.getWarehousingReturnDetailId()+"向京东提交售后信息成功;京东订单号:"+extDeliveryNote.getExtJdOrderId());
                } else {
                    //提交售后信息失败
                    log.info("退库单:"+returnDetail.getWarehousingReturnDetailId()+"向京东提交售后信息失败;京东订单号:"+extDeliveryNote.getExtJdOrderId());
                }

            }
        }
    }

    private String getStorageKey(String headNo, Object lineNo) {
        return headNo + "-" + lineNo;
    }

    /**
     * 获取推送京东售后信息的params参数
     * @return
     */
    private String getParam(WarehousingReturnDetail returnDetail,ExtDeliveryNote extDeliveryNote,ExtOrder extOrder) {

        List<NameValuePair> params = new ArrayList<>();

        //组装orderId
        params.add(new BasicNameValuePair("orderId", extDeliveryNote.getExtJdOrderId().toString()));

        //组装thirdApplyId
        //thirdApplyId为自定义数据，确保单个京东订单号没有重复即可 ==》以子订单加时间拼接，确保单个子订单不存在重复
        StringBuffer thirdApplyId = new StringBuffer();
        thirdApplyId.append(extDeliveryNote.getExtJdOrderId());
        thirdApplyId.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        params.add(new BasicNameValuePair("thirdApplyId",thirdApplyId.toString()));

        //组装customerInfo    信息使用送货单中的采购员信息
        JDAfsApplyRequestDTO.AfsCustomerInfo afsCustomerInfo = new JDAfsApplyRequestDTO.AfsCustomerInfo();
        afsCustomerInfo.setCustomerName(extDeliveryNote.getExtPurchaserName());
        afsCustomerInfo.setCustomerContactName(extDeliveryNote.getExtPurchaserName());
        afsCustomerInfo.setCustomerMobilePhone(extDeliveryNote.getExtPurchaserPhone());
        params.add(new BasicNameValuePair("customerInfo",JSON.toJSONString(afsCustomerInfo)));

        //组装pickwareInfo    取件地址使用订单中的收货地址
        JDAfsApplyRequestDTO.AfsPickupWareInfo afsPickupWareInfo = new JDAfsApplyRequestDTO.AfsPickupWareInfo();
        //获取京东地址编码
        String receiveAddress = extOrder.getReceiveAddress();
        AddressResultDTO resultDTO = getJdAddress(receiveAddress);
        AddressResultDTO.AddressDetail result = resultDTO.getResult();
        //售后走线下，仅传递信息，设置虚假默认值，可更改; 取件方式。4上门取件7客户送货， 40客户发货。
        afsPickupWareInfo.setPickwareType(4);
        afsPickupWareInfo.setPickWareProvince(result.getProvinceId());
        afsPickupWareInfo.setPickWareCity(result.getCityId());
        afsPickupWareInfo.setPickWareCounty(result.getCountyId());
        afsPickupWareInfo.setPickWareAddress(receiveAddress);
        params.add(new BasicNameValuePair("pickwareInfo",JSON.toJSONString(afsPickupWareInfo)));

        //申请时返件信息，及京东放寄回时的地址，和原来的收货地址保持一直
        JDAfsApplyRequestDTO.AfsReturnWareInfo afsReturnWareInfo = new JDAfsApplyRequestDTO.AfsReturnWareInfo();
        //售后走线下，仅传递信息，设置虚假默认值，可更改; 设置为10 返件方式。10自营配送，20第三方配送
        afsReturnWareInfo.setReturnWareType(10);
        afsReturnWareInfo.setReturnWareProvince(result.getProvinceId());
        afsReturnWareInfo.setReturnWareCity(result.getCityId());
        afsReturnWareInfo.setReturnWareCountry(result.getCountyId());
        afsReturnWareInfo.setReturnWareAddress(receiveAddress);
        params.add(new BasicNameValuePair("returnWareInfo",JSON.toJSONString(afsReturnWareInfo)));

        //组装afsApplyInfoItemList    商品信息(待确认)
        List<JDAfsApplyRequestDTO.AfsApplyInfoItem> afsApplyInfoItems = new ArrayList<>();
        //一个退库单只有一个物料
        JDAfsApplyRequestDTO.AfsApplyInfoItem afsApplyInfoItem = new JDAfsApplyRequestDTO.AfsApplyInfoItem();
        //售后走线下，仅传递信息，设置虚假默认值，可更改; 设置为10；客户期望售后类型。10退货，20换货，30维修，80补货
        afsApplyInfoItem.setCustomerExpect(10);
        //确认商品和数量
        //从外部物料映射中查询京东物料信息
        LambdaQueryWrapper<ExternalMaterial> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExternalMaterial::getMaterialCode,returnDetail.getItemCode());
        queryWrapper.eq(ExternalMaterial::getMaterialType,"JD");
        queryWrapper.eq(ExternalMaterial::getMappingFlag,"Y");
        List<ExternalMaterial> externalMaterials = externalMaterialService.list(queryWrapper);
        //物料应该是一对一的
        ExternalMaterial material = externalMaterials.get(0);
        JDAfsApplyRequestDTO.WareDetailInfo wareDetailInfo = new JDAfsApplyRequestDTO.WareDetailInfo();
        //商品无赠品的情况，商品编号与主商品编号无区别
        wareDetailInfo.setWareId(Long.parseLong(material.getSkuId()));
        wareDetailInfo.setMainWareId(Long.parseLong(material.getSkuId()));
        wareDetailInfo.setWareName(material.getExternalMaterialName());
        wareDetailInfo.setWareNum(Integer.parseInt(returnDetail.getReturnToSupplierNum().toString()));
        wareDetailInfo.setWareDescribe("");
        //售后走线下，仅传递信息，设置虚假默认值，可更改; 商品类型。10主商品，20赠品。
        wareDetailInfo.setWareType(10);
        //添加到列表中
        afsApplyInfoItem.setWareDetailInfo(wareDetailInfo);
        afsApplyInfoItems.add(afsApplyInfoItem);
        params.add(new BasicNameValuePair("afsApplyInfoItemList",JSON.toJSONString(afsApplyInfoItems)));

        return URLEncodedUtils.format(params, "UTF-8");
    }

    /**
     * 转换成京东地址
     * @param receiveAddress 收货地址
     * @return JdResultDTO 响应报文
     */
    @NotNull
    private AddressResultDTO getJdAddress(String receiveAddress) {
        JDAddressRequestDTO jdAddressRequestDTO = new JDAddressRequestDTO();
        jdAddressRequestDTO.setAddress(receiveAddress);
        jdAddressRequestDTO.setMallType(MallTypeEnum.JD.getCode());
        log.info("发起接口getSkuInfo调用:{}", JsonUtils.toJsonString(jdAddressRequestDTO));
        AddressResultDTO resultDTO = mallService.getAddressFromAddress(jdAddressRequestDTO);
        if(!STR0000.equals(resultDTO.getResultCode())){
            throw new BaseException("京东地址转换异常，地址不存在或异常。");
        }
        return resultDTO;
    }
}
