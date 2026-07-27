package com.midea.cloud.srm.sou.sourcing.init.service;

import com.midea.cloud.meiql.api.function.SFunction;
import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouPlan;
/**
 * 备注
 * @author huangbf3
 */
public interface IExtSouPlanService extends IService<ExtSouPlan> {

    /**
     * 更新节点值
     * @param projectId 参数
     * @param value 参数
     * @param sFunction 参数
     * @param <T> 参数
     */
    public <T> void applyAtualPoint(Long projectId, T value, SFunction<ExtSouPlan, T> sFunction);

    /**
     * 更新节点值
     * @param projectId
     * @param value
     * @param sFunction
     * @param ignoreNotNull
     * @param <T>
     */
    public <T> void applyAtualPoint(Long projectId, T value, SFunction<ExtSouPlan, T> sFunction, Boolean ignoreNotNull);
}
