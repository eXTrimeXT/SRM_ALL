package com.midea.cloud.srm.sup.info.job;

/**
 * <pre>
 *  定时获取呆滞供应商任务
 * </pre>
 *
 * @author luxc18
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/10/14 17:21
 *  修改内容:
 * </pre>
 */

import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.common.result.ResultCode;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.quartz.bind.Job;
import com.midea.cloud.quartz.handler.ExecuteableJob;
import com.midea.cloud.srm.emun.PjCompanyStatusEmun;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.sup.info.mapper.VendorInformationPjMapper;
import com.midea.cloud.srm.utils.MqlType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Job("pjCompanyStatusJob")
@Slf4j
public class PjCompanyStatusJob implements ExecuteableJob {

    @Autowired
    private QlService qlService;

    @Autowired
    private VendorInformationPjMapper vendorInformationPjMapper;

    @Override
    public BaseResult executeJob(Map<String, String> params) {
        //查找供应商一年内没有参与过项目（招标，竞价，询比价）的逻辑
        //    scc_sou_vendor 没有数据，代表没参加过 pj和sou都会
        try {
            List<CompanyInfo> extCompanyInfoDtos = vendorInformationPjMapper.listSluggishSupplier();
            if (CollectionUtils.isNotEmpty(extCompanyInfoDtos)) {
                List<Long> companyIdList = extCompanyInfoDtos.stream().map(CompanyInfo::getCompanyId).collect(Collectors.toList());
                qlService.updateByWrapper(QlWrappers.update(MqlType.SUPPLIER)
                        .in("companyId", companyIdList)
                        .set("pjCompanyStatus", PjCompanyStatusEmun.SLUGGISH_SUPPLIER.name()));
            }
        } catch (Exception e) {
            log.error("定时获取呆滞供应商任务异常", e);
            return BaseResult.build(ResultCode.UNKNOWN_ERROR, e);
        }
        return BaseResult.build(ResultCode.SUCCESS);
    }

}
