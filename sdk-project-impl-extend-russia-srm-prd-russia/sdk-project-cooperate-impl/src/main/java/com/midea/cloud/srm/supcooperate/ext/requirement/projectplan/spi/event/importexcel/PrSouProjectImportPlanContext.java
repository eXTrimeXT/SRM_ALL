package com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.importexcel;

import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.dto.ExtPrSouProjectPlanImportExcelDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.entity.ExtPrSouProjectPlan;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 招标计划 - 项目计划 - 导入上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PrSouProjectImportPlanContext extends SdkPluginContext {

    @ApiModelProperty("入参: 导入文件")
    private MultipartFile importFile;

    @ApiModelProperty("入参: 应用场景")
    private String sceneType;

    @ApiModelProperty("导入解析数据(IPrSouProjectImportPlanValidatePlugin环节填补)")
    private List<ExtPrSouProjectPlanImportExcelDTO> importDataList;

    @ApiModelProperty("项目计划实体")
    private List<ExtPrSouProjectPlan> projectPlanEntityList;
    /** 错误文件ID */
    @Nullable
    @ApiModelProperty("错误文件ID")
    private Long result;

    public PrSouProjectImportPlanContext(MultipartFile importFile, String sceneType) {
        this.importFile = importFile;
        this.sceneType = sceneType;
    }

}
