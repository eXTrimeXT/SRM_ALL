package com.midea.cloud.srm.supcooperate.mtmapping.service;

import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.supcooperate.ext.ExternalMaterial;

import java.util.List;


/**
 * 外部物料与系统物料映射表
 *
 * @author xiaym13 xiaym13@meicloud.com
 * @since 1.0.0 2024-02-26
 */
public interface ExternalMaterialService extends BaseService<ExternalMaterial> {

    /**
     * synchExternalMaterial
     */
    void synchExternalMaterial();

    /**
     * 拉去商品图片
     *
     * @param MaterialCode
     * @return
     */
    List<Fileupload> getSkuImage(String MaterialCode);

    /**
     * getMaterialType
     * @param materialCode
     * @return
     */
    String getMaterialType(String materialCode);

    /**
     * 京东商品判断，如果时京东商品，则需要判断商品状态和库存
     *
     * @param requirementHead
     * @param shopCart
     */
    void checkStateByMaterialType(Record requirementHead, Record shopCart);
}
