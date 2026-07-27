package com.midea.cloud.srm.sup.info.job;

/**
 * <pre>
 *  定时更新品类关系-品类状态为失效
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
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.model.supplier.info.entity.OrgCategory;
import com.midea.cloud.srm.sup.info.mapper.VendorInformationPjMapper;
import com.midea.cloud.srm.utils.MqlType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Job("pjCategoryStatusJob")
@Slf4j
public class PjCategoryStatusJob implements ExecuteableJob {

    @Autowired
    private QlService qlService;

    @Autowired
    private VendorInformationPjMapper vendorInformationPjMapper;

    @Override
    public BaseResult executeJob(Map<String, String> params) {
        //2年内，供应商品类库对应的品类和组织没参加过项目
        //   供应商、组织、品类在下面表
        //   scc_sou_vendor、scc_sou_item， scc_sou_project（project_id）
        try {
            List<OrgCategory> orgCategories = vendorInformationPjMapper.listUnusedOrgCategory();
            if (CollectionUtils.isNotEmpty(orgCategories)) {
                List<Long> idList = orgCategories.stream().map(OrgCategory::getOrgCategoryId).collect(Collectors.toList());
                qlService.updateByWrapper(QlWrappers.update(MqlType.ORGCATEGORY)
                        .in("orgCategoryId", idList)
                        .set("pjCategoryStatus", Enable.N.name()));
            }
        } catch (Exception e) {
            log.error("定时更新品类关系品类状态异常", e);
            return BaseResult.build(ResultCode.UNKNOWN_ERROR, e);
        }
        return BaseResult.build(ResultCode.SUCCESS);
    }

}
