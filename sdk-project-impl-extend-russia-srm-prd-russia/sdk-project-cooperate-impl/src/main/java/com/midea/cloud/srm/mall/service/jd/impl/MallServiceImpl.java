package com.midea.cloud.srm.mall.service.jd.impl;

import com.alibaba.fastjson.JSON;
import com.midea.cloud.srm.mall.api.AddressApi;
import com.midea.cloud.srm.mall.api.AfterSaleApi;
import com.midea.cloud.srm.mall.api.GoodsApi;
import com.midea.cloud.srm.mall.api.OrderApi;
import com.midea.cloud.srm.mall.common.ResultCode;
import com.midea.cloud.srm.mall.config.InstanceFactory;
import com.midea.cloud.srm.mall.request.base.BaseRequestDTO;
import com.midea.cloud.srm.mall.result.CommonResultDTO;
import com.midea.cloud.srm.mall.result.MallResult;
import com.midea.cloud.srm.mall.result.jd.Order.*;
import com.midea.cloud.srm.mall.result.jd.afs.AfsApplyResultDTO;
import com.midea.cloud.srm.mall.result.jd.common.AddressResultDTO;
import com.midea.cloud.srm.mall.result.jd.common.DelMessageResultDTO;
import com.midea.cloud.srm.mall.result.jd.common.GetMessageResultDTO;
import com.midea.cloud.srm.mall.result.jd.goods.*;
import com.midea.cloud.srm.mall.service.jd.MallService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Service
@Slf4j
public class MallServiceImpl implements MallService {

    private final InstanceFactory instanceFactory;

    public MallServiceImpl(InstanceFactory instanceFactory) {
        this.instanceFactory = instanceFactory;
    }

    @Override
    public AddressResultDTO getAddressFromAddress(BaseRequestDTO baseRequestDTO) {
        log.info("京东商城BaseRequestDTO==>"+ JSON.toJSONString(baseRequestDTO));
        AddressApi addressApi = instanceFactory.selectAddressInstance(baseRequestDTO.getMallType());
        log.info("京东商城addressApi==>"+ JSON.toJSONString(addressApi));
        final MallResult<ResultCode, CommonResultDTO> result = addressApi.getAddressFromAddress(baseRequestDTO);
        log.info("京东商城result==>"+ JSON.toJSONString(result));
        AddressResultDTO addressResultDTO = (AddressResultDTO) result.getData();
        Assert.isTrue(addressResultDTO.isSuccess(), "地址详情转换京东地址编码失败：" + addressResultDTO.getResultMessage());
        return addressResultDTO;
    }

    @Override
    public DeliveryResultDTO queryDeliveryInfo(BaseRequestDTO baseRequestDTO) {
        log.info("京东商城BaseRequestDTO==>"+ JSON.toJSONString(baseRequestDTO));
        // 根据商城平台编码，获取对应的服务
        OrderApi orderApi = instanceFactory.selectOrderInstance(baseRequestDTO.getMallType());
        log.info("京东商城orderApi==>"+ JSON.toJSONString(orderApi));
        // 查询物流信息
        final MallResult<ResultCode, CommonResultDTO> result = orderApi.queryOrderTrack(baseRequestDTO);
        log.info("京东商城result==>"+ JSON.toJSONString(result));
        return (DeliveryResultDTO) result.getData();
    }

    @Override
    public OrderDetailResultDTO queryOrderTetailInfo(BaseRequestDTO baseRequestDTO) {
        log.info("京东商城BaseRequestDTO==>"+ JSON.toJSONString(baseRequestDTO));
        // 根据商城平台编码，获取对应的服务
        OrderApi orderApi = instanceFactory.selectOrderInstance(baseRequestDTO.getMallType());
        log.info("京东商城orderApi==>"+ JSON.toJSONString(orderApi));
        // 查询订单详情
        final MallResult<ResultCode, CommonResultDTO> result = orderApi.queryOrderDetail(baseRequestDTO);
        log.info("京东商城result==>"+ JSON.toJSONString(result));
        return (OrderDetailResultDTO) result.getData();
        //后可以根据json里的type=1表示有子单结构OrderDetailHasChildResultDTO，type=2表示无子单的结构OrderDetailNoneChildResultDTO，业务上可以通过JSON.parseObject转成实体类
    }

    @Override
    public ReceiveConfirmResultDTO receiveConfirm(BaseRequestDTO baseRequestDTO) {
        log.info("京东商城BaseRequestDTO==>"+ JSON.toJSONString(baseRequestDTO));
        // 根据商城平台编码，获取对应的服务
        OrderApi orderApi = instanceFactory.selectOrderInstance(baseRequestDTO.getMallType());
        log.info("京东商城orderApi==>"+ JSON.toJSONString(orderApi));
        // 批量收货确认
        final MallResult<ResultCode, CommonResultDTO> result = orderApi.confirmReceived(baseRequestDTO);
        log.info("京东商城result==>"+ JSON.toJSONString(result));
        return (ReceiveConfirmResultDTO) result.getData();
    }

    @Override
    public BatchReceiveConfirmResultDTO batchReceiveConfirm(BaseRequestDTO baseRequestDTO) {
        log.info("京东商城BaseRequestDTO==>"+ JSON.toJSONString(baseRequestDTO));
        // 根据商城平台编码，获取对应的服务
        OrderApi orderApi = instanceFactory.selectOrderInstance(baseRequestDTO.getMallType());
        log.info("京东商城orderApi==>"+ JSON.toJSONString(orderApi));
        // 批量收货确认
        final MallResult<ResultCode, CommonResultDTO> result = orderApi.batchConfirmReceived(baseRequestDTO);
        log.info("京东商城result==>"+ JSON.toJSONString(result));
        return (BatchReceiveConfirmResultDTO) result.getData();
    }

    @Override
    public PromiseTisResultDTO queryPromiseTis(BaseRequestDTO baseRequestDTO) {
        log.info("京东商城BaseRequestDTO==>"+ JSON.toJSONString(baseRequestDTO));
        // 根据商城平台编码，获取对应的服务
        OrderApi orderApi = instanceFactory.selectOrderInstance(baseRequestDTO.getMallType());
        log.info("京东商城orderApi==>"+ JSON.toJSONString(orderApi));
        // 查询商品配送预计送达时间
        final MallResult<ResultCode, CommonResultDTO> result = orderApi.getPromiseTips(baseRequestDTO);
        log.info("京东商城result==>"+ JSON.toJSONString(result));
        return (PromiseTisResultDTO) result.getData();
    }

    @Override
    public OrderSubmitResultDTO submitOrder(BaseRequestDTO baseRequestDTO) {
        log.info("京东商城BaseRequestDTO==>"+ JSON.toJSONString(baseRequestDTO));
        // 根据商城平台编码，获取对应的服务
        OrderApi orderApi = instanceFactory.selectOrderInstance(baseRequestDTO.getMallType());
        log.info("京东商城orderApi==>"+ JSON.toJSONString(orderApi));
        // 提交订单
        final MallResult<ResultCode, CommonResultDTO> result = orderApi.submitOrder(baseRequestDTO);
        log.info("京东商城result==>"+ JSON.toJSONString(result));
        return (OrderSubmitResultDTO) result.getData();
    }

    @Override
    public ProductPageNumResultDTO getProductPageNum(BaseRequestDTO baseRequestDTO) {
        log.info("京东商城BaseRequestDTO==>"+ JSON.toJSONString(baseRequestDTO));
        GoodsApi goodsApi=instanceFactory.selectGoodsInstance(baseRequestDTO.getMallType());
        log.info("京东商城goodsApi==>"+ JSON.toJSONString(goodsApi));
        final MallResult<ResultCode, CommonResultDTO> result =goodsApi.getProductPageNum(baseRequestDTO);
        log.info("京东商城result==>"+ JSON.toJSONString(result));
        return (ProductPageNumResultDTO) result.getData();
    }

    @Override
    public ProductStockResultDTO getProductStock(BaseRequestDTO baseRequestDTO) {
        log.info("京东商城BaseRequestDTO==>"+ JSON.toJSONString(baseRequestDTO));
        GoodsApi goodsApi=instanceFactory.selectGoodsInstance(baseRequestDTO.getMallType());
        log.info("京东商城goodsApi==>"+ JSON.toJSONString(goodsApi));
        final MallResult<ResultCode, CommonResultDTO> result =goodsApi.getProductStockById(baseRequestDTO);
        ProductStockResultDTO productStockResultDTO = (ProductStockResultDTO) result.getData();
        Assert.isTrue(productStockResultDTO.isSuccess(), "查询京东商品库存失败：" + productStockResultDTO.getResultMessage());
        log.info("京东商城result==>"+ JSON.toJSONString(result));
        return productStockResultDTO;
    }

    @Override
    public QuerySkuResultDTO querySkuByPage(BaseRequestDTO baseRequestDTO) {
        log.info("京东商城BaseRequestDTO==>"+ JSON.toJSONString(baseRequestDTO));
        GoodsApi goodsApi = instanceFactory.selectGoodsInstance(baseRequestDTO.getMallType());
        log.info("京东商城goodsApi==>"+ JSON.toJSONString(goodsApi));
        final MallResult<ResultCode, CommonResultDTO> result = goodsApi.querySkuByPage(baseRequestDTO);
        log.info("京东商城result==>"+ JSON.toJSONString(result));
        return (QuerySkuResultDTO) result.getData();
    }

    @Override
    public SkuDetailResultDTO querySkuDetail(BaseRequestDTO baseRequestDTO) {
        log.info("京东商城BaseRequestDTO==>"+ JSON.toJSONString(baseRequestDTO));
        GoodsApi goodsApi = instanceFactory.selectGoodsInstance(baseRequestDTO.getMallType());
        log.info("京东商城goodsApi==>"+ JSON.toJSONString(goodsApi));
        final MallResult<ResultCode, CommonResultDTO> result = goodsApi.querySkuDetail(baseRequestDTO);
        log.info("京东商城result==>"+ JSON.toJSONString(result));
        return (SkuDetailResultDTO) result.getData();
    }

    @Override
    public SkuImgResultDTO querySkuImage(BaseRequestDTO baseRequestDTO) {
        log.info("京东商城BaseRequestDTO==>"+ JSON.toJSONString(baseRequestDTO));
        GoodsApi goodsApi=instanceFactory.selectGoodsInstance(baseRequestDTO.getMallType());
        log.info("京东商城goodsApi==>"+ JSON.toJSONString(goodsApi));
        final MallResult<ResultCode, CommonResultDTO> result =goodsApi.querySkuImage(baseRequestDTO);
        log.info("京东商城result==>"+ JSON.toJSONString(result));
        return (SkuImgResultDTO) result.getData();
    }

    @Override
    public TotalCheckNewResultDTO totalCheckNew(BaseRequestDTO baseRequestDTO) {
        log.info("京东商城BaseRequestDTO==>"+ JSON.toJSONString(baseRequestDTO));
        GoodsApi goodsApi = instanceFactory.selectGoodsInstance(baseRequestDTO.getMallType());
        log.info("京东商城goodsApi==>"+ JSON.toJSONString(goodsApi));
        final MallResult<ResultCode, CommonResultDTO> result = goodsApi.totalCheckNew(baseRequestDTO);
        log.info("京东商城result==>"+ JSON.toJSONString(result));
        return (TotalCheckNewResultDTO) result.getData();
    }

    @Override
    public GetMessageResultDTO getMessage(BaseRequestDTO baseRequestDTO) {
        log.info("京东商城BaseRequestDTO==>"+ JSON.toJSONString(baseRequestDTO));
        OrderApi orderApi = instanceFactory.selectOrderInstance(baseRequestDTO.getMallType());
        log.info("京东商城orderApi==>"+ JSON.toJSONString(orderApi));
        final MallResult<ResultCode, CommonResultDTO> result = orderApi.getMessage(baseRequestDTO);
        GetMessageResultDTO getMessageResultDTO = (GetMessageResultDTO) result.getData();
        Assert.isTrue(getMessageResultDTO.isSuccess(), "拉取消息失败：" + getMessageResultDTO.getResultMessage());
        log.info("京东商城result==>"+ JSON.toJSONString(result));
        return getMessageResultDTO;
    }

    @Override
    public DelMessageResultDTO delMessage(BaseRequestDTO baseRequestDTO) {
        log.info("京东商城BaseRequestDTO==>"+ JSON.toJSONString(baseRequestDTO));
        OrderApi orderApi = instanceFactory.selectOrderInstance(baseRequestDTO.getMallType());
        log.info("京东商城orderApi==>"+ JSON.toJSONString(orderApi));
        final MallResult<ResultCode, CommonResultDTO> result = orderApi.delMessage(baseRequestDTO);
        log.info("京东商城result==>"+ JSON.toJSONString(result));
        DelMessageResultDTO delMessageResultDTO = (DelMessageResultDTO) result.getData();
        Assert.isTrue(delMessageResultDTO.isSuccess(), "删除消息失败：" + delMessageResultDTO.getResultMessage());
        return delMessageResultDTO;
    }

    @Override
    public AfsApplyResultDTO createAfsApply(BaseRequestDTO baseRequestDTO) {
        log.info("京东商城BaseRequestDTO==>"+ JSON.toJSONString(baseRequestDTO));
        AfterSaleApi afterSaleApi = instanceFactory.selectAfterSaleInstance(baseRequestDTO.getMallType());
        log.info("京东商城afterSaleApi==>"+ JSON.toJSONString(afterSaleApi));
        final MallResult<ResultCode, CommonResultDTO> result = afterSaleApi.createAfsApply(baseRequestDTO);
        log.info("京东商城result==>"+ JSON.toJSONString(result));
        AfsApplyResultDTO afsApplyResultDTO = (AfsApplyResultDTO) result.getData();
        Assert.isTrue(afsApplyResultDTO.isSuccess(), "售后申请失败：" + afsApplyResultDTO.getResultMessage());
        return afsApplyResultDTO;
    }

    @Override
    public UpdatePoNoResultDTO saveOrUpdatePoNo(BaseRequestDTO baseRequestDTO) {
        log.info("京东商城BaseRequestDTO==>"+ JSON.toJSONString(baseRequestDTO));
        OrderApi orderApi = instanceFactory.selectOrderInstance(baseRequestDTO.getMallType());
        log.info("京东商城orderApi==>"+ JSON.toJSONString(orderApi));
        final MallResult<ResultCode, CommonResultDTO> result = orderApi.saveOrUpdatePoNo(baseRequestDTO);
        log.info("京东商城result==>"+ JSON.toJSONString(result));
        UpdatePoNoResultDTO resultDTO = (UpdatePoNoResultDTO) result.getData();
        Assert.isTrue(resultDTO.isSuccess(), "更新送货单号失败：" + resultDTO.getResultMessage());
        return resultDTO;
    }

    @Override
    public SellPriceResultDTO getSellPrice(BaseRequestDTO baseRequestDTO) {
        log.info("京东商城BaseRequestDTO==>" + JSON.toJSONString(baseRequestDTO));
        GoodsApi goodsApi = instanceFactory.selectGoodsInstance(baseRequestDTO.getMallType());
        log.info("京东商城orderApi==>" + JSON.toJSONString(goodsApi));
        final MallResult<ResultCode, CommonResultDTO> result = goodsApi.getSellPrice(baseRequestDTO);
        log.info("京东商城result==>" + JSON.toJSONString(result));
        return (SellPriceResultDTO) result.getData();
    }

}
