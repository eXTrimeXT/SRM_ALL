package com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.excelhandler;

import com.meicloud.paas.ies.model.ImportResultModel;
import com.midea.cloud.common.sdkplugin.ISdkPlugin;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dept.entity.Dept;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.dto.ExtPrSouProjectPlanImportExcelDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.entity.ExtPrSouProjectPlan;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.enums.ExtPrSouProjectPlanStatusEnum;
import com.midea.cloud.srm.sies.pojo.SiesData;
import com.midea.cloud.srm.sies.pojo.SiesImportParam;
import com.midea.cloud.srm.sies.pojo.SiesImportResult;
import com.midea.cloud.srm.sies.pojo.SiesMediator;
import com.midea.cloud.srm.sies.validator.AbstractImportValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * 招标计划 - 项目计划导入校验插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/08
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class PrSouProjectPlanImportValidator extends AbstractImportValidator {

    @Autowired
    private BaseClient baseClient;
    private static final DateTimeFormatter LOCAL_DATE_PATTERN = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final DateTimeFormatter LOCAL_DATE_PATTERN2 = DateTimeFormatter.ofPattern("yyyy/MM/d");

    @Override
    public SiesImportResult doValidate(String iesTaskId, SiesImportParam param, SiesMediator curMediator, int sheetNo, String sheetName, Integer batchNo, List<SiesData> data) {
        AssertUtils.notEmpty(data, "导入文件缺少数据");
        SiesImportResult importResult = new SiesImportResult();

        int index = -1;
        for (SiesData d : data) {
            index++;
            ExtPrSouProjectPlanImportExcelDTO dto = SouObjectXUtil.convertTargetObj(d, ExtPrSouProjectPlanImportExcelDTO.class);
            StringBuilder errSb = new StringBuilder(100);

            // 1: 项目名称
            dto.setProjectName(StringUtils.trimToNull(dto.getProjectName()));
            if (dto.getProjectName() == null) {
                errSb.append("项目名称不能为空;");
            } else if (dto.getProjectName().length() > 80) {
                errSb.append("项目名称的输入长度不能超过80;");
            }
            d.put(ExtPrSouProjectPlanImportExcelDTO::getProjectName, dto.getProjectName());
            d.put(ExtPrSouProjectPlan::getSceneType, ISdkPlugin.DEFAULT_SCENE);
            // 2: 立项时间
            if (dto.getInitDate() == null) {
                errSb.append("立项时间不能为空;");
            } else {
                LocalDate localDate = null;
                try {
                    localDate = LocalDate.parse(dto.getInitDate(), LOCAL_DATE_PATTERN);
                } catch (DateTimeParseException e) {
                    try {
                        localDate = LocalDate.parse(dto.getInitDate(), LOCAL_DATE_PATTERN2);
                    } catch (DateTimeParseException ex) {
                        errSb.append("立项时间必须满足类似( 2023/01/01 )格式;");
                    }
                }
                if (localDate != null) {
                    d.put(ExtPrSouProjectPlanImportExcelDTO::getInitDate, localDate);
                }
            }
            // 3: 投资部门ID
            dto.setDepartmentId(StringUtils.trimToNull(dto.getDepartmentId()));
            if (dto.getDepartmentId() == null) {
                errSb.append("投资部门ID不能为空;");
            }
            d.put(ExtPrSouProjectPlanImportExcelDTO::getDepartmentId, dto.getDepartmentId());
            // 4: 投资部门名称
            dto.setDepartmentName(StringUtils.trimToNull(dto.getDepartmentName()));
            if (dto.getDepartmentName() == null) {
                errSb.append("投资部门名称不能为空;");
            }
            d.put(ExtPrSouProjectPlanImportExcelDTO::getDepartmentName, dto.getDepartmentName());
            // 5: 立项金额
            if (dto.getInitAmount() == null) {
                errSb.append("立项金额不能为空;");
            } else {
                dto.setInitAmount(dto.getInitAmount().setScale(6, RoundingMode.HALF_UP).stripTrailingZeros());
            }
            d.put(ExtPrSouProjectPlanImportExcelDTO::getInitAmount, dto.getInitAmount());
            // 6: 计划编号
            dto.setPlanNo(StringUtils.trimToNull(dto.getPlanNo()));
            if (dto.getPlanNo() == null) {
                errSb.append("计划编号不能为空;");
            } else if (dto.getPlanNo().length() > 80) {
                errSb.append("计划编号的输入长度不能超过80;");
            }
            d.put(ExtPrSouProjectPlanImportExcelDTO::getPlanNo, dto.getPlanNo());
            // 7: 投资地点
            dto.setPlanAddress(StringUtils.trimToNull(dto.getPlanAddress()));
            if (dto.getPlanAddress() == null) {
                errSb.append("投资地点不能为空;");
            } else if (dto.getPlanAddress().length() > 255) {
                errSb.append("投资地点的输入长度不能超过255;");
            }
            d.put(ExtPrSouProjectPlanImportExcelDTO::getPlanAddress, dto.getPlanAddress());
            // 8: 项目级别
            dto.setPlanLevel(StringUtils.trimToNull(dto.getPlanLevel()));
            if (dto.getPlanLevel() == null) {
                errSb.append("项目级别不能为空;");
            } else if (dto.getPlanLevel().length() > 30) {
                errSb.append("项目级别的输入长度不能超过30;");
            }
            d.put(ExtPrSouProjectPlanImportExcelDTO::getPlanLevel, dto.getPlanLevel());
            // 9: 项目状态
            d.put(ExtPrSouProjectPlanImportExcelDTO::getPlanStatus, ExtPrSouProjectPlanStatusEnum.NORMAL.name());

            if (errSb.length() > 0) {
                importResult.addErrorRow(new ImportResultModel.ErrorRow(index, errSb.toString()));
            } else {
                importResult.addSuccessRow(new SiesImportResult.SuccessRow(d, false));
            }
        }

        return importResult;
    }

}
