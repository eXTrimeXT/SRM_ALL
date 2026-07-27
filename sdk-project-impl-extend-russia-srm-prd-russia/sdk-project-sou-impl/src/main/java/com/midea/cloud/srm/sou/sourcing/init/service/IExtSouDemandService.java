package com.midea.cloud.srm.sou.sourcing.init.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouDemand;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouPlan;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
public interface IExtSouDemandService extends IService<ExtSouDemand> {

    /**
     * 生成包名
     * @param demandList 参数
     * @return 返回
     */
    List<ExtSouDemand> generatePackName(List<ExtSouDemand> demandList);
}
