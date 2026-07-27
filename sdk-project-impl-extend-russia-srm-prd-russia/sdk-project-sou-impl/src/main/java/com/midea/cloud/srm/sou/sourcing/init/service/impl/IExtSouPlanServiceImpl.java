package com.midea.cloud.srm.sou.sourcing.init.service.impl;

import cn.hutool.Hutool;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.meiql.api.function.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.model.sou.enums.SouBidPlanTypeEnum;
import com.midea.cloud.srm.sou.common.ExtSouBidComponent;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouPlanMapper;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouPlanService;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouPlan;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.reflection.property.PropertyNamer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Objects;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class IExtSouPlanServiceImpl extends ServiceImpl<ExtSouPlanMapper, ExtSouPlan> implements IExtSouPlanService {

    @Override
    public <T> void applyAtualPoint(Long projectId, T value, SFunction<ExtSouPlan, T> sFunction) {
        applyAtualPoint(projectId, value, sFunction, true);
    }

    @Override
    public <T> void applyAtualPoint(Long projectId, T value, SFunction<ExtSouPlan, T> sFunction, Boolean ignoreNotNull) {
        LambdaQueryWrapper<ExtSouPlan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouPlan::getProjectId, projectId);
        queryWrapper.eq(ExtSouPlan::getPlanType, SouBidPlanTypeEnum.ACTUAL.getCode());
        List<ExtSouPlan> actualList = this.list(queryWrapper);
        ExtSouPlan extSouPlan = new ExtSouPlan();
        if(CollectionUtils.isNotEmpty(actualList)) {
            extSouPlan = actualList.get(0);
        } else {
            extSouPlan.setPlanId(IdGenrator.generate());
            extSouPlan.setProjectId(projectId);
            extSouPlan.setPlanType(SouBidPlanTypeEnum.ACTUAL.getCode());
        }

        //更新实际情况的值
        String fieldName = ExtSouBidComponent.fieldName(sFunction);
        String fieldNameFisrtUpper = fieldName.replaceAll("\\b\\w", String.valueOf(Character.toUpperCase(fieldName.charAt(0))));

        if(!ignoreNotNull) {
            Method getMethod = ReflectionUtils.findMethod(ExtSouPlan.class, StringUtils.join("get", fieldNameFisrtUpper));
            Object getValue = ReflectionUtils.invokeMethod(getMethod, extSouPlan);
            if(!Objects.isNull(getValue)) {
                log.info("applyAtualPoint ignoreNotNull: " + projectId + " fieldName: " + fieldName);
                return;
            }
        }

        Method method = ReflectionUtils.findMethod(ExtSouPlan.class, StringUtils.join("set", fieldNameFisrtUpper), ReflectionUtils.findField(ExtSouPlan.class, fieldName).getType());
        ReflectionUtils.invokeMethod(method, extSouPlan, value);

        if(CollectionUtils.isNotEmpty(actualList)) {
            this.updateById(extSouPlan);
        } else {
            this.save(extSouPlan);
        }
    }
}
