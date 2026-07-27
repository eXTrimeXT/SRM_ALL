package com.midea.cloud.srm.supcooperate.catalogonshelves.job;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.meiql.api.enums.QlQueryFeature;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.quartz.bind.Job;
import com.midea.cloud.quartz.handler.ExecuteableJob;
import com.midea.cloud.srm.model.pm.pr.catalogonshelves.entity.CatalogOnShelves;
import com.midea.cloud.srm.model.supcooperate.ext.catalogonshelvess.ExtCatalogOnShelvesStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *  功能名称
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
@Job("scheduledShelvesJob")
public class ScheduledShelvesJob implements ExecuteableJob {
    @Autowired
    protected QlService qlService;

    /**
     * 每天刷新
     * 1、如果这个价格开始时间到了，状态为待上架，则自动上架
     * 2、如果这个价格结束时间到了，状态为已上架，则自动下架
     * 3、如果这个价格开始时间到了，状态为已定时上架，则自动上架
     *
     * @param params
     * @return
     */
    @Override
    public BaseResult executeJob(Map<String, String> params) {
        Date now = new Date();
        //上架
        List<Record> upRecords = qlService.queryByWrapper(QlWrappers.query(CatalogOnShelves.class)
                .in(CatalogOnShelves::getStatus, new String[]{
                        ExtCatalogOnShelvesStatusEnum.SCHEDULED_SHELVES.name(),
                        ExtCatalogOnShelvesStatusEnum.TO_BE_ON_SHELVES.name()
                })
                .le(CatalogOnShelves::getEffectiveDate, DateUtil.beginOfDay(now))
                .gt(CatalogOnShelves::getExpirationDate, DateUtil.beginOfDay(now)), Record.class);
        if (ObjectUtil.isNotEmpty(upRecords)) {
            upRecords.forEach(record -> record.set(CatalogOnShelves::getStatus, ExtCatalogOnShelvesStatusEnum.ON_SHELVES));
            qlService.update("CatalogOnShelves", upRecords, QlQueryFeature.EXCLUDE_NULL);
        }
        //定时下架
        List<Record> downRecords = qlService.queryByWrapper(QlWrappers.query(CatalogOnShelves.class)
                .eq(CatalogOnShelves::getStatus, ExtCatalogOnShelvesStatusEnum.ON_SHELVES)
                .le(CatalogOnShelves::getExpirationDate, DateUtil.beginOfDay(now)), Record.class);
        if (ObjectUtil.isNotEmpty(downRecords)) {
            for (Record record : downRecords) {
                record.set(CatalogOnShelves::getStatus, ExtCatalogOnShelvesStatusEnum.OFF_SHELVES);
                record.set(CatalogOnShelves::getOffShelvesReason, "价格到期自动下架");
                record.set(CatalogOnShelves::getOffShelvesDate, now);
            }
            qlService.update("CatalogOnShelves", downRecords, QlQueryFeature.EXCLUDE_NULL);
        }
        return BaseResult.buildSuccess("执行成功！");
    }
}
