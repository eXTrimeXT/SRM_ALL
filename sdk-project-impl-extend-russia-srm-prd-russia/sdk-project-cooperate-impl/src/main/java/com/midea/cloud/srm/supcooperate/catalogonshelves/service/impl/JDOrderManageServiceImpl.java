package com.midea.cloud.srm.supcooperate.catalogonshelves.service.impl;

import com.meicloud.paas.audit.util.JsonUtils;
import com.midea.cloud.srm.mall.request.jd.common.JDAddressRequestDTO;
import com.midea.cloud.srm.mall.request.jd.goods.JDGoodsStockRequestDTO;
import com.midea.cloud.srm.mall.result.jd.common.AddressResultDTO;
import com.midea.cloud.srm.mall.result.jd.goods.ProductStockResultDTO;
import com.midea.cloud.srm.mall.service.jd.MallService;
import com.midea.cloud.srm.model.supcooperate.enums.MallTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <pre>
 *  JD商城业务处理类入口
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@Service
@Slf4j
public class JDOrderManageServiceImpl {

    @Autowired
    private MallService goodsService;

    // 数据库操作
    // private Mapper mapper;

    public AddressResultDTO testAddress() {
        // 业务逻辑代码处理 待完善
        JDAddressRequestDTO jdAddressRequestDTO = new JDAddressRequestDTO();
        jdAddressRequestDTO.setAddress("地址");
        jdAddressRequestDTO.setMallType(MallTypeEnum.JD.getCode());
        // 发起接口调用
        log.info("发起接口getSkuInfo调用:{}", JsonUtils.toJsonString(jdAddressRequestDTO));
        return goodsService.getAddressFromAddress(jdAddressRequestDTO);
    }

    public void testStock() {
        // 业务逻辑代码处理 待完善
        JDGoodsStockRequestDTO jdGoodsStockRequestDTO = new JDGoodsStockRequestDTO();
        jdGoodsStockRequestDTO.setArea("13_1000_4277_0 ");
        jdGoodsStockRequestDTO.setSkuNums("[{skuId: 569172,num:101}]");
        jdGoodsStockRequestDTO.setMallType(MallTypeEnum.JD.getCode());
        ProductStockResultDTO productStockResultDTO= goodsService.getProductStock(jdGoodsStockRequestDTO);
    }
}

