package com.midea.cloud.srm.sou.sourcing.init.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;

import java.util.List;
import java.util.Map;

/**
 * 备注
 * @author huangbf3
 */
public interface IExtSouVendorService extends IService<ExtSouVendor> {

    /**
     *  盲审时屏蔽供应商名称，对招标负责人展示原供应商名称
     * @param souProject
     * @return
     */
    public List<ExtSouVendor> listVendorInfoAsShieldVendorName(ExtSouProject souProject);

    /**
     *  盲审时屏蔽供应商名称，对招标负责人展示原供应商名称
     * @param souProjectList
     * @return
     */
    public Map<String, ExtSouVendor> listVendorInfoAsShieldVendorNameBatch(List<ExtSouProject> souProjectList);
}
