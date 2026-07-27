package com.midea.cloud.srm.mall.api;

import com.midea.cloud.srm.mall.common.ResultCode;
import com.midea.cloud.srm.mall.request.base.BaseRequestDTO;
import com.midea.cloud.srm.mall.result.CommonResultDTO;
import com.midea.cloud.srm.mall.result.MallResult;

public interface GoodsApi {

    /**
     *  商品详情查询
     * @param baseRequestDTO
     * @return
     */
    MallResult<ResultCode, CommonResultDTO> querySkuDetail(BaseRequestDTO baseRequestDTO);

    /**
     * 查询商品图片
     * @param baseRequestDTO
     * @return
     */
    MallResult<ResultCode, CommonResultDTO> querySkuImage(BaseRequestDTO baseRequestDTO);

    /**
     * 商品池编号查询
     * @param baseRequestDTO
     * @return
     */
    MallResult<ResultCode, CommonResultDTO> getProductPageNum(BaseRequestDTO baseRequestDTO);

    /**
     * 查询池内商品编号- 翻页
     * @param baseRequestDTO
     * @return
     */
    MallResult<ResultCode, CommonResultDTO> querySkuByPage(BaseRequestDTO baseRequestDTO);

    /**
     * 查询商品上下架状态
     * @param baseRequestDTO
     * @return
     */
    MallResult<ResultCode, CommonResultDTO> getSkuState(BaseRequestDTO baseRequestDTO);

    /**
     * 查询商品库存
     * @param baseRequestDTO
     * @return
     */
    MallResult<ResultCode, CommonResultDTO> getProductStockById(BaseRequestDTO baseRequestDTO);

    /**
     * 商品可采校验
     *
     * @return
     */
    MallResult<ResultCode, CommonResultDTO> totalCheckNew(BaseRequestDTO baseRequestDTO);

    /**
     * 查询商品售卖价
     *
     * @return
     */
    MallResult<ResultCode, CommonResultDTO> getSellPrice(BaseRequestDTO baseRequestDTO);
}
