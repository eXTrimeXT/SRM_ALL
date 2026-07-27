package com.midea.cloud.srm.supcooperate.mtmapping.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.enums.FileUploadType;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.component.aop.lock.SyncLock;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.mall.config.UriPropertiesConfiguration;
import com.midea.cloud.srm.mall.enums.JDResultCodeEnum;
import com.midea.cloud.srm.mall.request.base.BaseRequestDTO;
import com.midea.cloud.srm.mall.request.jd.common.JDAddressRequestDTO;
import com.midea.cloud.srm.mall.request.jd.goods.JDGoodsStockRequestDTO;
import com.midea.cloud.srm.mall.request.jd.goods.JDSkuImgRequestDTO;
import com.midea.cloud.srm.mall.request.jd.goods.QuerySkuDetailRequestDTO;
import com.midea.cloud.srm.mall.request.jd.goods.QuerySkuRequestDTO;
import com.midea.cloud.srm.mall.result.jd.common.AddressResultDTO;
import com.midea.cloud.srm.mall.result.jd.goods.*;
import com.midea.cloud.srm.mall.service.jd.MallService;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pm.pr.shopcart.entity.ShopCart;
import com.midea.cloud.srm.model.supcooperate.enums.MallTypeEnum;
import com.midea.cloud.srm.model.supcooperate.ext.ExternalMaterial;
import com.midea.cloud.srm.supcooperate.mtmapping.mapper.ExternalMaterialMapper;
import com.midea.cloud.srm.supcooperate.mtmapping.service.ExternalMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 外部物料与系统物料映射表
 *
 * @author xiaym13 xiaym13@meicloud.com
 * @since 1.0.0 2024-02-26
 */
@Slf4j
@Service
public class ExternalMaterialServiceImpl extends BaseServiceImpl<ExternalMaterialMapper, ExternalMaterial> implements ExternalMaterialService {
    @Autowired
    private MallService goodsService;
    @Autowired
    private ExternalMaterialService externalMaterialService;
    @Resource
    private FileCenterClient fileCenterClient;
    @Autowired
    private UriPropertiesConfiguration uriPropertiesConfiguration;
    private static final String EXT_MATERIAL_TYPE = "extMaterialType";

    @Override
    @SyncLock( // 禁止同时访问该接口
            moduleName = "SYNCH_EXTERNAL_MATERIAL",
            allowInTx = true,
            lockFailureMsg = "正在执行中，请稍后")
    public void synchExternalMaterial() {
        BaseRequestDTO baseRequestDTO = new BaseRequestDTO();
        baseRequestDTO.setMallType(MallTypeEnum.JD.getCode());
        ProductPageNumResultDTO productPageNumResultDTO= goodsService.getProductPageNum(baseRequestDTO);
        log.info("同步京东商品条数：{}", productPageNumResultDTO.getResult().size());
        if (productPageNumResultDTO.isSuccess()) {
        if (ObjectUtil.isNotEmpty(productPageNumResultDTO.getResult())) {
            //根据商品池获取全部商品
            List<ExternalMaterial> externalMaterials = this.getSku(productPageNumResultDTO.getResult());
            //判断是否已存在表，存在则更新，不存在则插入
            this.saveOrUpdateExternalMaterial(externalMaterials);
            //执行更新表
            log.info("同步京东商品执行更新条数：{}", externalMaterials.size());
            externalMaterialService.saveOrUpdateBatch(externalMaterials);
        }
        }else {
            throw new RuntimeException(productPageNumResultDTO.getResultMessage());
        }
    }

    private void saveOrUpdateExternalMaterial(List<ExternalMaterial> externalMaterials) {
        // 每次查询的skuId个数
        int batchSize = 1000;

        // 获取externalMaterials中的所有skuId
        List<String> skuIdList = externalMaterials.stream()
                .map(ExternalMaterial::getSkuId)
                .collect(Collectors.toList());

        // 分批次查询表A
        List<ExternalMaterial> aList = new ArrayList<>();
        for (int i = 0; i < skuIdList.size(); i += batchSize) {
            List<String> batchSkuIdList = skuIdList.subList(i, Math.min(i + batchSize, skuIdList.size()));
            List<ExternalMaterial> batchAList = externalMaterialService.list(new LambdaQueryWrapper<ExternalMaterial>().in(ExternalMaterial::getSkuId, batchSkuIdList));
            aList.addAll(batchAList);
        }
        //如果存在值，则赋值externalMaterials里对象的主键
        externalMaterials.forEach(externalMaterial -> aList.stream()
                .filter(a -> a.getSkuId().equals(externalMaterial.getSkuId()))
                .findFirst()
                .ifPresent(a -> {
                    externalMaterial.setExternalMaterialId(a.getExternalMaterialId());
                }));
    }

    private List<ExternalMaterial> getSku(List<ProductPageNumResultDTO.ProductPageNum> productPageNums) {
        //初始化外部物料与系统物料映射实体集合
        List<ExternalMaterial> externalMaterials = new ArrayList<>();
        Date now = new Date();
        for (ProductPageNumResultDTO.ProductPageNum productPageNum : productPageNums) {
            int pageSize = 200;
            long offset = 0;
            boolean hasNextPage = true;
            while (hasNextPage) {
                // 构造请求参数
                QuerySkuRequestDTO querySkuRequestDTO = QuerySkuRequestDTO.builder()
                        .pageNum(productPageNum.getPage_num())
                        .pageSize(pageSize)
                        .offset(offset)
                        .build();
                querySkuRequestDTO.setMallType(MallTypeEnum.JD.getCode());
                // 查询池内商品编号
                QuerySkuResultDTO productPageNumResultDTO = goodsService.querySkuByPage(querySkuRequestDTO);
                if (productPageNumResultDTO.isSuccess() && productPageNumResultDTO.getResultCode().equals(JDResultCodeEnum.B0000.getCode())) {
                    for (String sku : productPageNumResultDTO.getResult().getSkus()) {
                        ExternalMaterial externalMaterial = ExternalMaterial.builder()
                                .externalMaterialCode(productPageNum.getPage_num())
                                .externalMaterialName(productPageNum.getName())
                                .skuId(sku)
                                .externalMaterialUpdateDate(now)
                                .brand(MallTypeEnum.JD.getMessage())
                                .materialType(MallTypeEnum.JD.getCode())
                                .build();
                        QuerySkuDetailRequestDTO querySkuDetailRequestDTO = QuerySkuDetailRequestDTO.builder()
                                .sku(sku)
                                .build();
                        querySkuDetailRequestDTO.setMallType(MallTypeEnum.JD.getCode());
                        //查询商品详情（京东接口限流，查询商品详情  每秒110次）
                        SkuDetailResultDTO skuDetailResultDTO = goodsService.querySkuDetail(querySkuDetailRequestDTO);
                        if (skuDetailResultDTO.isSuccess()) {
                            //商品名称
                            externalMaterial.setSkuName(skuDetailResultDTO.getResult().getName());
                            //品牌
                            externalMaterial.setBrand(skuDetailResultDTO.getResult().getBrandName());
                            //商品状态
                            externalMaterial.setMaterialState(skuDetailResultDTO.getResult().getState());
                        }
                        externalMaterials.add(externalMaterial);
                    }
                    //赋值偏移量
                    offset = productPageNumResultDTO.getResult().getOffset();
                } else if (productPageNumResultDTO.getResultCode().equals(JDResultCodeEnum.B0010.getCode())) {
                    hasNextPage = false;
                } else {
                    throw new RuntimeException("查询商品池商品编号失败：" + productPageNumResultDTO.getResultMessage());
                }
            }
        }
        return externalMaterials;
    }
    @Override
    public List<Fileupload> getSkuImage(String MaterialCode) {
        ExternalMaterial externalMaterial = this.getOne(new LambdaQueryWrapper<ExternalMaterial>().eq(ExternalMaterial::getMaterialCode, MaterialCode), false);
        if (ObjectUtil.isNotEmpty(externalMaterial)) {
            JDSkuImgRequestDTO jdSkuImgRequestDTO = new JDSkuImgRequestDTO();
            jdSkuImgRequestDTO.setMallType(MallTypeEnum.JD.getCode());
            jdSkuImgRequestDTO.setSku(externalMaterial.getSkuId());
            //查询商品图片数据
            SkuImgResultDTO productPageNumResultDTO= goodsService.querySkuImage(jdSkuImgRequestDTO);
            if (ObjectUtil.isNotEmpty(productPageNumResultDTO.getResult()) && !productPageNumResultDTO.getResult().isEmpty()) {
                //上传到srm附件系统
                List<Fileupload> list = new ArrayList<>();
                for (SkuImgResultDTO.SkuImg skuImg : productPageNumResultDTO.getResult().get(externalMaterial.getSkuId())) {
                    String sourceType = "WEB_APP";
                    String fileType = "images";
                    // 查找最后一个 "/" 的位置
                    int lastIndex = skuImg.getPath().lastIndexOf("/");
                    // 截取子字符串
                    String subString = skuImg.getPath().substring(lastIndex + 1);
                    MultipartFile file = null;
                    try {
                        //下载图片
                        byte[] imageBytes = HttpUtil.downloadBytes(uriPropertiesConfiguration.getImgBaseUrl()+skuImg.getPath());
                        file = new MockMultipartFile("file",subString, fileType, imageBytes);
                    //上传图片
                    Fileupload fileupload = fileCenterClient.feignClientUpload(file, sourceType, FileUploadType.DEF.name(), "fileModular", "commonFile", fileType);
                    list.add(fileupload);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                return list;
            }
        }
        return new ArrayList<>();
    }

    @Override
    public String getMaterialType(String materialCode) {
        String materialType = MallTypeEnum.CC.getCode();
        if (ObjectUtil.isNotEmpty(materialCode)) {
            ExternalMaterial externalMaterial = externalMaterialService.getOne(new LambdaQueryWrapper<ExternalMaterial>()
                    .eq(ExternalMaterial::getMaterialCode, materialCode), false);
            if (externalMaterial != null) {
                materialType = externalMaterial.getMaterialType();
            }
        }
        return materialType;
    }

    @Override
    public void checkStateByMaterialType(Record requirementHead, Record shopCart) {
        if (ObjectUtil.isNotEmpty(shopCart.get(EXT_MATERIAL_TYPE))) {
            ExternalMaterial externalMaterial = externalMaterialService.getOne(new LambdaQueryWrapper<ExternalMaterial>()
                    .eq(ExternalMaterial::getMaterialCode, shopCart.get(ShopCart::getMaterialCode)), false);
            if (externalMaterial != null) {
                //检查商品状态
                QuerySkuDetailRequestDTO querySkuDetailRequestDTO = QuerySkuDetailRequestDTO.builder()
                        .sku(externalMaterial.getSkuId())
                        .build();
                querySkuDetailRequestDTO.setMallType(MallTypeEnum.JD.getCode());
                SkuDetailResultDTO skuDetailResultDTO = goodsService.querySkuDetail(querySkuDetailRequestDTO);
                Assert.isTrue(skuDetailResultDTO.isSuccess(), "校验商品上架状态异常：" + skuDetailResultDTO.getResultMessage());
                requirementHead.put("extIsAbnormal", "0".equals(skuDetailResultDTO.getResult().getState()) ? Enable.Y.name() : Enable.N.name());
                //转换京东地址
                JDAddressRequestDTO jdAddressRequestDTO = new JDAddressRequestDTO();
                jdAddressRequestDTO.setAddress(shopCart.getString("extAddressName"));
                jdAddressRequestDTO.setMallType(MallTypeEnum.JD.getCode());
                AddressResultDTO addressResultDTO = goodsService.getAddressFromAddress(jdAddressRequestDTO);
                //检查商品库存
                JDGoodsStockRequestDTO jdGoodsStockRequestDTO = new JDGoodsStockRequestDTO();
                jdGoodsStockRequestDTO.setArea(addressResultDTO.getResult().getProvinceId() + "_" + addressResultDTO.getResult().getCountyId() + "_" + addressResultDTO.getResult().getTownId() + "_" + addressResultDTO.getResult().getNationId());
                jdGoodsStockRequestDTO.setSkuNums("[{skuId:" + externalMaterial.getSkuId() + ",num:" + shopCart.get(ShopCart::getRequirementNum) + "}]");
                jdGoodsStockRequestDTO.setMallType(MallTypeEnum.JD.getCode());
                ProductStockResultDTO productStockResultDTO = goodsService.getProductStock(jdGoodsStockRequestDTO);
                requirementHead.put("extIsAbnormal", productStockResultDTO != null
                        && productStockResultDTO.getResult() != null
                        && !productStockResultDTO.getResult().isEmpty()
                        && productStockResultDTO.getResult().get(0).getRemainNum() != null
                        && Convert.toBigDecimal(productStockResultDTO.getResult().get(0).getRemainNum()).compareTo(shopCart.get(ShopCart::getRequirementNum)) < 0 ? Enable.Y.name() : Enable.N.name());
            }
        }
    }


}
