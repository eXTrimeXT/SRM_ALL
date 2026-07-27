package com.midea.cloud.srm.sup.info.job;

/**
 * <pre>
 *  定时刷新时间受限
 * </pre>
 *
 * @author luxc18
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/10/12 17:21
 *  修改内容:
 * </pre>
 */

import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.common.result.ResultCode;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.quartz.bind.Job;
import com.midea.cloud.quartz.handler.ExecuteableJob;
import com.midea.cloud.srm.emun.PjSupplierControlType;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.utils.MqlType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Job("supplierLimitDateJob")
@Slf4j
public class SupplierLimitDateJob implements ExecuteableJob {

    @Autowired
    private QlService qlService;

    @Override
    public BaseResult executeJob(Map<String, String> params) {
        //定时器扫描供应商清单库，查询是否限制时间为是的数据并且限制时间小于当前时间，
        try {
            List<CompanyInfo> companyInfoList = qlService.queryByWrapper(QlWrappers.query(MqlType.SUPPLIER)
                    .eq("timeLimitFlag", Enable.Y.name())
                    .lt("limitDate", LocalDate.now()), CompanyInfo.class);

            if (CollectionUtils.isNotEmpty(companyInfoList)) {
                // 把是否限制时间改为否,供应商异常信息对应的信息逻辑删除
                List<Long> companyIdList = companyInfoList.stream().map(CompanyInfo::getCompanyId).collect(Collectors.toList());
                qlService.updateByWrapper(QlWrappers.update(MqlType.SUPPLIER)
                        .in("companyId", companyIdList)
                        .set("timeLimitFlag", Enable.N.name())
                        .set("limitDate", null));
                qlService.updateByWrapper(QlWrappers.update(MqlType.NPM_COMPANY_EXCEPTION_INFO)
                        .in("companyId", companyIdList)
                        .eq("exceptionType", PjSupplierControlType.TIME_LIMIT_FLAG)
                        .set("deleteFlag", Enable.Y.name()));
            }
        } catch (Exception e) {
            log.error("定时器扫描供应商清单库更新时间受限异常", e);
            return BaseResult.build(ResultCode.UNKNOWN_ERROR, e);
        }
        return BaseResult.build(ResultCode.SUCCESS);
    }

}
