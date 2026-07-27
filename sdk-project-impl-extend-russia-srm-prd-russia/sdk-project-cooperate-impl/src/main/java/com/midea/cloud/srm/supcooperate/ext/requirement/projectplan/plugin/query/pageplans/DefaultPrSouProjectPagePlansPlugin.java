package com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.plugin.query.pageplans;

import com.midea.cloud.meiql.api.component.paging.Page;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.ql.QlQueryWrapper;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.entity.ExtPrSouProjectPlan;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.query.pageplans.IPrSouProjectPagePlansPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.query.pageplans.PrSouProjectPagePlansContext;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 招标计划 - 项目计划 - 列表查询插件s
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/04
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultPrSouProjectPagePlansPlugin implements IPrSouProjectPagePlansPlugin {

    @Autowired
    private QlService qlService;

    @Override
    @ApiOperation("执行查询")
    @SuppressWarnings("rawtypes")
    public PrSouProjectPagePlansContext executePagePlans(PrSouProjectPagePlansContext context) {
        // 1: 入参格式化
        context.getQueryParam().formatParams();
        // 2: 查询数据
        QlQueryWrapper wrapper = QlWrappers.query(ExtPrSouProjectPlan.class)
                // 项目名称
                .contains(context.getQueryParam().getProjectName() != null, ExtPrSouProjectPlan::getProjectName, context.getQueryParam().getProjectName())
                // 创建人账号
                .eq(context.getQueryParam().getCreatedBy() != null, ExtPrSouProjectPlan::getCreatedBy, context.getQueryParam().getCreatedBy())
                // 项目状态
                .eq(context.getQueryParam().getPlanStatus() != null, ExtPrSouProjectPlan::getPlanStatus, context.getQueryParam().getPlanStatus());
        List<ExtPrSouProjectPlan> planList;
        if (context.getQueryParam().getPageNum() != null && context.getQueryParam().getPageSize() != null) {
            Page<ExtPrSouProjectPlan> page = qlService.queryPageByWrapper(wrapper, (long)context.getQueryParam().getPageNum(), (long)context.getQueryParam().getPageSize(), ExtPrSouProjectPlan.class);
            planList = new com.github.pagehelper.Page<>();
            planList.addAll(page.getRecords());
            ((com.github.pagehelper.Page)planList).setPageNum(context.getQueryParam().getPageNum());
            ((com.github.pagehelper.Page)planList).setPageSize(context.getQueryParam().getPageSize());
            ((com.github.pagehelper.Page)planList).setTotal(page.getTotal());
        } else {
            planList = qlService.queryByWrapper(wrapper, ExtPrSouProjectPlan.class);
        }

        context.setResult(planList);
        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
