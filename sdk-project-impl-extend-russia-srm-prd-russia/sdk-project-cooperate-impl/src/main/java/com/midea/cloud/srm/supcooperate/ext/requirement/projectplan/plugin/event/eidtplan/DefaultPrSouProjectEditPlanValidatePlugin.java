package com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.plugin.event.eidtplan;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dept.entity.Dept;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.entity.ExtPrSouProjectPlan;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.enums.ExtPrSouProjectPlanStatusEnum;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.editplan.IPrSouProjectEditPlanValidatePlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.editplan.PrSouProjectEditPlanContext;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 招标计划 - 项目计划 - 编辑校验插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/04
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultPrSouProjectEditPlanValidatePlugin implements IPrSouProjectEditPlanValidatePlugin {

    @Autowired
    private BaseClient baseClient;

    @Override
    @ApiOperation("校验及构造项目计划")
    public PrSouProjectEditPlanContext validateAndContextProjectPlan(PrSouProjectEditPlanContext context) {
        // 1: 数据校验
        ExtPrSouProjectPlan param = context.getParam(); {
            // 1.1: ID
            if (param.getProjectPlanId() != null) {
                AssertUtils.notNull(context.getExistProjectPlan(), "项目计划[{0}]不存在", context.getParam().getProjectPlanId());
            }
            // 1.2: 项目名称
            param.setProjectName(StringUtils.trimToNull(param.getProjectName()));
            AssertUtils.notNull(param.getProjectName(), "项目名称不能为空");
            AssertUtils.isTrue(param.getProjectName().length() <= 80, "项目名称长度不能超过80");
            // 1.3: 应用场景
            param.setSceneType(StringUtils.trimToNull(param.getSceneType()));
            AssertUtils.notNull(param.getSceneType(), "缺少应用场景");
            // 1.4: 立项事件
            AssertUtils.notNull(param.getInitDate(), "请选择立项时间");
            // 1.5: 投资部门
            param.setDepartmentId(StringUtils.trimToNull(param.getDepartmentId()));
            param.setDepartmentName(StringUtils.trimToNull(param.getDepartmentName()));
            AssertUtils.notNull(param.getDepartmentId(), "请选择投资部门");
            AssertUtils.notNull(param.getDepartmentName(), "请选择投资部门");
            // 1.6: 立项金额
            AssertUtils.notNull(param.getInitAmount(), "请输入立项金额");
            AssertUtils.isTrue(param.getInitAmount().compareTo(BigDecimal.ZERO) > 0, "立项金额必须大于0");
            param.setInitAmount(param.getInitAmount().setScale(6, RoundingMode.HALF_UP).stripTrailingZeros());
            // 1.7: 计划编号
            param.setPlanNo(StringUtils.trimToNull(param.getPlanNo()));
            AssertUtils.notNull(param.getPlanNo(), "请输入计划编号");
            AssertUtils.isTrue(param.getPlanNo().length() <= 80, "计划编号长度不能超过80");
            // 1.8: 投资地点
            param.setPlanAddress(StringUtils.trimToNull(param.getPlanAddress()));
            AssertUtils.notNull(param.getPlanAddress(), "请输入投资地点");
            AssertUtils.isTrue(param.getPlanAddress().length() <= 255, "投资地点长度不能超过255");
            // 1.9: 项目级别
            param.setPlanLevel(StringUtils.trimToNull(param.getPlanLevel()));
            AssertUtils.notNull(param.getPlanLevel(), "请输入项目级别");
            AssertUtils.isTrue(param.getPlanLevel().length() <= 30, "项目级别长度不能超过30");
            // 1.10: 项目状态
            param.setPlanStatus(StringUtils.trimToNull(param.getPlanStatus()));
            AssertUtils.notNull(param.getPlanStatus(), "请选择项目状态");
            AssertUtils.isTrue(ExtPrSouProjectPlanStatusEnum.NORMAL.name().equals(param.getPlanStatus())
                    || ExtPrSouProjectPlanStatusEnum.CANCEL.name().equals(param.getPlanStatus()), "选择的项目状态只能是正常或者取消");
            // 1.11: 被引用的招标计划(置空)
            param.setRequirementHeadId(null);
        }
        // 2: 数据构造
        ExtPrSouProjectPlan entity; {
            if (context.getExistProjectPlan() != null) {
                entity = context.getExistProjectPlan();
            } else {
                entity = new ExtPrSouProjectPlan();
            }

            //noinspection unchecked
            SouObjectXUtil.mergePropertiesIgnoreFields(param, entity,
                    ExtPrSouProjectPlan::getProjectPlanId,
                    ExtPrSouProjectPlan::getCreatedId,
                    ExtPrSouProjectPlan::getCreatedBy,
                    ExtPrSouProjectPlan::getCreatedByIp,
                    ExtPrSouProjectPlan::getCreationDate,
                    ExtPrSouProjectPlan::getCreatedFullName,
                    ExtPrSouProjectPlan::getCreatedUserName,
                    ExtPrSouProjectPlan::getTenantId,
                    ExtPrSouProjectPlan::getVersion);
            if (context.getExistProjectPlan() == null) {
                entity.setProjectPlanId(IdGenrator.generate());
            }

            SouObjectXUtil.mergeProperties(entity, param);
        }
        context.setProjectPlanEntity(entity);

        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
