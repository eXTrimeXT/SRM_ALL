package com.midea.cloud.srm.supcooperate.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.meiql.api.enums.QlQueryFeature;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.quartz.bind.Job;
import com.midea.cloud.quartz.handler.ExecuteableJob;
import com.midea.cloud.srm.model.pm.pr.catalogonshelves.entity.CatalogOnShelves;
import com.midea.cloud.srm.model.supcooperate.ext.ExternalMaterial;
import com.midea.cloud.srm.model.supcooperate.ext.catalogonshelvess.ExtCatalogOnShelvesStatusEnum;
import com.midea.cloud.srm.supcooperate.mtmapping.service.ExternalMaterialService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <pre>
 *  每个月28号晚上11点调用商品上下架接口，同步京东商品的上下架状态并保存；
 * 同步京东商品时，也刷新商品的上下架状态，并更新为最新
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/11/2 15:32
 *  修改内容:
 * </pre>
 */
@Job("externalMaterialJob")
public class ExternalMaterialJob implements ExecuteableJob {
    @Autowired
    protected QlService qlService;
    @Autowired
    private ExternalMaterialService externalMaterialService;

    private static final String STR0 = "0";

    @Override
    public BaseResult executeJob(Map<String, String> params) {
        //更新物料映射表
        externalMaterialService.synchExternalMaterial();
        //查询已上架商品
        List<Record> catalogOnShelves = qlService.queryByWrapper(QlWrappers.query(CatalogOnShelves.class)
                .eq(CatalogOnShelves::getStatus, ExtCatalogOnShelvesStatusEnum.ON_SHELVES), Record.class);
        if (CollectionUtils.isEmpty(catalogOnShelves)) {
            return BaseResult.buildSuccess("执行成功！");
        }
        // 构建MaterialCode集合
        List<String> materialCodes = catalogOnShelves.stream()
                .map(record -> record.get(CatalogOnShelves::getMaterialCode))
                .collect(Collectors.toList());
        // 分批次查询物料映射表，存在的映射关系的数据
        int batchSize = 1000;
        List<ExternalMaterial> externalMaterials = new ArrayList<>();
        for (int i = 0; i < materialCodes.size(); i += batchSize) {
            List<String> batchmaterialCodeList = materialCodes.subList(i, Math.min(i + batchSize, materialCodes.size()));
            List<ExternalMaterial> batchAList = externalMaterialService.list(new LambdaQueryWrapper<ExternalMaterial>().in(ExternalMaterial::getMaterialCode, batchmaterialCodeList));
            externalMaterials.addAll(batchAList);
        }
        if (CollectionUtils.isNotEmpty(externalMaterials)) {
            Date now = new Date();
            List<Record> updateRecords = new ArrayList<>();
            //判断状态，如果京东已下架，则下架
            Map<String, ExternalMaterial> externalMaterialMap = externalMaterials.stream().collect(Collectors.toMap(ExternalMaterial::getMaterialCode, material -> material));
            catalogOnShelves.forEach(r -> {
                ExternalMaterial externalMaterial = externalMaterialMap.get(r.get(CatalogOnShelves::getMaterialCode));
                if (externalMaterial != null && STR0.equals(externalMaterial.getMaterialState())) {
                    r.set(CatalogOnShelves::getStatus, ExtCatalogOnShelvesStatusEnum.OFF_SHELVES);
                    r.set(CatalogOnShelves::getOffShelvesReason, "该商品在京东平台已下架");
                    r.set(CatalogOnShelves::getOffShelvesDate, now);
                    updateRecords.add(r);
                }
            });
            //更新为下架
            qlService.update("CatalogOnShelves", updateRecords, QlQueryFeature.EXCLUDE_NULL);
        }
        return BaseResult.buildSuccess("执行成功！");
    }
}
