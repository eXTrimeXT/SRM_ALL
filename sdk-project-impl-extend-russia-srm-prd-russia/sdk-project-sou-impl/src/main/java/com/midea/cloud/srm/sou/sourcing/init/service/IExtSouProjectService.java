package com.midea.cloud.srm.sou.sourcing.init.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;

import java.util.List;
import java.util.Map;

/**
 * 备注
 * @author huangbf3
 */
public interface IExtSouProjectService extends IService<ExtSouProject> {

    /**
     * 统计某个供应商某个品类的投标次数和中标次数
     * @param vendorIdList
     * @param extCategoryId
     * @return
     */
    public List<Map<String, Object>> statisticalBidTimes(List<Long> vendorIdList, Long extCategoryId);
    /**
     * 根据招标项目编号查询名称
     * @param extSouProject
     * @return
     */
    public List<ExtSouProject> queryByProjectNo(ExtSouProject extSouProject);
}
