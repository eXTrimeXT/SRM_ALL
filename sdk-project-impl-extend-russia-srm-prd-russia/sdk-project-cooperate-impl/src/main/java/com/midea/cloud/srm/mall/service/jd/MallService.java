package com.midea.cloud.srm.mall.service.jd;

import com.midea.cloud.srm.mall.request.base.BaseRequestDTO;
import com.midea.cloud.srm.mall.result.jd.Order.*;
import com.midea.cloud.srm.mall.result.jd.afs.AfsApplyResultDTO;
import com.midea.cloud.srm.mall.result.jd.common.AddressResultDTO;
import com.midea.cloud.srm.mall.result.jd.common.DelMessageResultDTO;
import com.midea.cloud.srm.mall.result.jd.common.GetMessageResultDTO;
import com.midea.cloud.srm.mall.result.jd.goods.*;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
public interface MallService {
    /**
     * 地址详情转换京东地址编码
     * @param baseRequestDTO
     * @return
     */
    AddressResultDTO getAddressFromAddress(BaseRequestDTO baseRequestDTO);

    /**
     * 查询物流信息
     * @param baseRequestDTO
     * @return
     */
    DeliveryResultDTO queryDeliveryInfo(BaseRequestDTO baseRequestDTO);

    /**
     * queryOrderTetailInfo
     * @param baseRequestDTO
     * @return
     */
    OrderDetailResultDTO queryOrderTetailInfo(BaseRequestDTO baseRequestDTO);

    /**
     * 确认收货
     *
     * @param baseRequestDTO
     * @return
     */
    ReceiveConfirmResultDTO receiveConfirm(BaseRequestDTO baseRequestDTO);

    /**
     * 批量确认收货
     *
     * @param baseRequestDTO
     * @return
     */
    BatchReceiveConfirmResultDTO batchReceiveConfirm(BaseRequestDTO baseRequestDTO);

    /**
     * queryPromiseTis
     * @param baseRequestDTO
     * @return
     */
    PromiseTisResultDTO queryPromiseTis(BaseRequestDTO baseRequestDTO);

    /**
     * submitOrder
     * @param baseRequestDTO
     * @return
     */
    OrderSubmitResultDTO submitOrder(BaseRequestDTO baseRequestDTO);

    /**
     * 查询商品池编号
     *
     * @param baseRequestDTO
     * @return
     */
    ProductPageNumResultDTO getProductPageNum(BaseRequestDTO baseRequestDTO);
    /**
     * 查询商品库存
     *
     * @param baseRequestDTO
     * @return
     */
    ProductStockResultDTO getProductStock(BaseRequestDTO baseRequestDTO);

    /**
     * 查询池内商品编号
     * @param baseRequestDTO
     * @return
     */
    QuerySkuResultDTO querySkuByPage(BaseRequestDTO baseRequestDTO);

    /**
     * 查询商品详情
     * @param baseRequestDTO
     * @return
     */
    SkuDetailResultDTO querySkuDetail(BaseRequestDTO baseRequestDTO);

    /**
     * 查询商品图片
     * @param baseRequestDTO
     * @return
     */
    SkuImgResultDTO querySkuImage(BaseRequestDTO baseRequestDTO);

    /**
     * 商品可采校验
     * @param baseRequestDTO
     * @return
     */
    TotalCheckNewResultDTO totalCheckNew(BaseRequestDTO baseRequestDTO);

    /**
     * 查询推送信息
     * @param baseRequestDTO
     * @return
     */
    GetMessageResultDTO getMessage(BaseRequestDTO baseRequestDTO);

    /**
     * 删除推送信息
     * @param baseRequestDTO
     * @return
     */
    DelMessageResultDTO delMessage(BaseRequestDTO baseRequestDTO);

    /**
     * 申请售后
     * @param baseRequestDTO
     * @return
     */
    AfsApplyResultDTO createAfsApply(BaseRequestDTO baseRequestDTO);

    /**
     * 更新采购单号（跟京东沟通过，现在这接口用来推送送货单给京东那边）
     * @param baseRequestDTO
     * @return
     */
    UpdatePoNoResultDTO saveOrUpdatePoNo(BaseRequestDTO baseRequestDTO);

    /**
     * 查询商品售卖价
     * @param baseRequestDTO
     * @return
     */
    SellPriceResultDTO getSellPrice(BaseRequestDTO baseRequestDTO);
}
