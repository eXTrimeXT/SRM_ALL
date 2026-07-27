package com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.excelhandler;

import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.entity.ExtPrSouProjectPlan;
import com.midea.cloud.srm.sies.pojo.SiesData;
import com.midea.cloud.srm.sies.pojo.SiesImportParam;
import com.midea.cloud.srm.sies.pojo.SiesImportResult;
import com.midea.cloud.srm.sies.pojo.SiesMediator;
import com.midea.cloud.srm.sies.processor.AbstractImportProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 招标计划 - 项目计划导入转化插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/08
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class PrSouProjectPlanImportProcessor extends AbstractImportProcessor {

    @Autowired
    private QlService qlService;

    @Override
    public SiesImportResult doImport(String iesTaskId, SiesImportParam param, SiesMediator curMediator, int sheetNo, String sheetName, Integer batchNo, List<SiesData> data) {
        SiesImportResult importResult = new SiesImportResult();
        List<ExtPrSouProjectPlan> entityList = SouObjectXUtil.convertList(param.getExtData().getAllManualData().get(ExtPrSouProjectPlan.class.getSimpleName()),
                ExtPrSouProjectPlan.class);

        if (!entityList.isEmpty()) {
            qlService.create(entityList);
        }
        return importResult;
    }

}
