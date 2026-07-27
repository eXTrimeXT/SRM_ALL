package com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.plugin.event.importexcel;

import com.midea.cloud.common.enums.FileUploadType;
import com.midea.cloud.common.sdkplugin.SdkPluginProxy;
import com.midea.cloud.common.utils.EasyExcelUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.dto.ExtPrSouProjectPlanImportExcelDTO;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.importexcel.IPrSouProjectImportPlanPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.importexcel.IPrSouProjectImportPlanValidatePlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.importexcel.PrSouProjectImportPlanContext;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 招标计划 - 项目计划 - 导入插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/06
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultPrSouProjectImportPlanPlugin implements IPrSouProjectImportPlanPlugin {

    @Autowired
    private QlService qlService;
    @Autowired
    private FileCenterClient fileCenterClient;

    @Override
    @ApiOperation("前置处理")
    public PrSouProjectImportPlanContext beforeImportPlan(PrSouProjectImportPlanContext context) {
        return SdkPluginProxy.proxy(IPrSouProjectImportPlanValidatePlugin.class, context).execute(context);
    }

    @Override
    @ApiOperation("执行处理")
    public PrSouProjectImportPlanContext executeImportPlan(PrSouProjectImportPlanContext context) {
        if (CollectionUtils.isNotEmpty(context.getProjectPlanEntityList())) {
            qlService.save(context.getProjectPlanEntityList());
        }
        return context;
    }

    @Override
    @ApiOperation("后置处理")
    public PrSouProjectImportPlanContext afterImportPlan(PrSouProjectImportPlanContext context) {
        // 如果存在错误信息，则返回错误excel信息
        boolean hasErrMsg = context.getImportDataList().stream().allMatch(data -> data.getErrMsg() != null);
        if (hasErrMsg) {
            Fileupload fileupload = new Fileupload()
                    .setUploadType(FileUploadType.DEF.name())
                    .setSourceType("WEB_APP")
                    .setFileModular("sou")
                    .setFileFunction("招标计划-项目计划导入")
                    .setFileType("images");
            Fileupload errorFileupload = EasyExcelUtil.uploadErrorFile(fileCenterClient, fileupload, context.getImportDataList(),
                    ExtPrSouProjectPlanImportExcelDTO.class, context.getImportFile());
            context.setResult(errorFileupload.getFileuploadId());
        }

        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
