package com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.plugin.event.importexcel;

import com.alibaba.excel.EasyExcel;
import com.midea.cloud.common.listener.AnalysisEventListenerImpl;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.dto.ExtPrSouProjectPlanImportExcelDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.entity.ExtPrSouProjectPlan;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.enums.ExtPrSouProjectPlanStatusEnum;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.importexcel.IPrSouProjectImportPlanValidatePlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.importexcel.PrSouProjectImportPlanContext;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 招标计划 - 项目计划 - 导入校验插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/06
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultPrSouProjectImportPlanValidatePlugin implements IPrSouProjectImportPlanValidatePlugin {

    @Autowired
    private BaseClient baseClient;

    @Override
    @ApiModelProperty("解析导入文件")
    public PrSouProjectImportPlanContext parseImportFile(PrSouProjectImportPlanContext context) {
        try (InputStream is = context.getImportFile().getInputStream()) {
            AnalysisEventListenerImpl<ExtPrSouProjectPlanImportExcelDTO> listener = new AnalysisEventListenerImpl<>();
            EasyExcel.read(is, ExtPrSouProjectPlanImportExcelDTO.class, listener)
                    .sheet(0)
                    .doRead();
            List<ExtPrSouProjectPlanImportExcelDTO> dataList = listener.getDatas();
            context.setImportDataList(dataList);
        } catch (IOException e) {
            throw new IllegalArgumentException("导入文件解析失败");
        }
        return context;
    }

    @Override
    @ApiModelProperty("校验及转化项目计划")
    public PrSouProjectImportPlanContext validateAndConvertProjectPlans(PrSouProjectImportPlanContext context) {
        // 1: 数据校验
        List<ExtPrSouProjectPlanImportExcelDTO> dataList = context.getImportDataList(); {
            AssertUtils.notEmpty(dataList, "导入文件缺少数据");
            dataList.forEach(data -> data.setErrMsg(null));
            StringBuilder errSb;
            for (ExtPrSouProjectPlanImportExcelDTO data : dataList) {
                errSb = new StringBuilder();
                // 1.1: 项目名称
                data.setProjectName(StringUtils.trimToNull(data.getProjectName()));
                if (data.getProjectName() == null) {
                    errSb.append("请填写项目名称;");
                } else if (data.getProjectName().length() > 80) {
                    errSb.append("项目名称的输入长度不能超过80;");
                }
                // 1.2: 立项时间
                if (data.getInitDate() == null) {
                    errSb.append("请输入立项时间;");
                }
                // 1.3: 投资部门
                data.setDepartmentId(StringUtils.trimToNull(data.getDepartmentId()));
                data.setDepartmentName(StringUtils.trimToNull(data.getDepartmentName()));
                if (data.getDepartmentId() == null || data.getDepartmentName() == null) {
                    errSb.append("请输入投资部门ID;");
                }
                // 1.4: 金额
                if (data.getInitAmount() == null) {
                    errSb.append("请输入金额;");
                } else if (data.getInitAmount().compareTo(BigDecimal.ZERO) <= 0) {
                    errSb.append("金额必须大于0;");
                }
                // 1.5: 计划编号
                data.setPlanNo(StringUtils.trimToNull(data.getPlanNo()));
                if (data.getPlanNo() == null) {
                    errSb.append("请输入计划编号;");
                } else if (data.getPlanNo().length() >= 80) {
                    errSb.append("计划编号的输入长度不能超过80;");
                }
                // 1.6: 投资地点
                data.setPlanAddress(StringUtils.trimToNull(data.getPlanAddress()));
                if (data.getPlanAddress() == null) {
                    errSb.append("请输入投资地点;");
                } else if (data.getPlanAddress().length() >= 255) {
                    errSb.append("投资地点的输入长度不能超过255;");
                }
                // 1.7: 项目级别
                data.setPlanLevel(StringUtils.trimToNull(data.getPlanLevel()));
                if (data.getPlanLevel() == null) {
                    errSb.append("请输入项目级别;");
                } else if (data.getPlanLevel().length() >= 30) {
                    errSb.append("项目级别的输入长度不能超过30;");
                }

                if (errSb.length() > 0) {
                    data.setErrMsg(errSb.toString());
                }
            }
            boolean noError = dataList.stream().allMatch(data -> data.getErrMsg() == null);
            if (!noError) { return context; }
        }
        // 2: 数据转化
        List<ExtPrSouProjectPlan> entityList = new ArrayList<>(dataList.size()); {
            for (ExtPrSouProjectPlanImportExcelDTO data : dataList) {
                ExtPrSouProjectPlan entity = new ExtPrSouProjectPlan();
                entityList.add(entity);

                BeanUtils.copyProperties(data, entity);
                // 2.1: ID
                entity.setProjectPlanId(IdGenrator.generate());
                // 2.2: 应用场景
                entity.setSceneType(context.getSceneType());
                // 2.3: 项目状态
                entity.setPlanStatus(ExtPrSouProjectPlanStatusEnum.NORMAL.name());
            }
        }

        context.setProjectPlanEntityList(entityList);
        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
