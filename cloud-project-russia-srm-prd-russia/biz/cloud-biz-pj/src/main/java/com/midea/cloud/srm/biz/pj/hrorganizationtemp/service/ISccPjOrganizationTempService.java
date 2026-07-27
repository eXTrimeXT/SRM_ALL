package com.midea.cloud.srm.biz.pj.hrorganizationtemp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.pj.hrorganization.SccPjOrganization;
import com.midea.cloud.srm.model.pj.hrorganizationtemp.SccPjOrganizationTemp;

import java.util.List;
import java.util.Map;

/**
 * @author huangbf3
 */
public interface ISccPjOrganizationTempService extends IService<SccPjOrganizationTemp> {

    /**
     * 同步HR组织信息
     * @param param
     * @param groupId
     * @return
     */
    public List<SccPjOrganizationTemp> syncHrOrganization(Map<String, Object> param, Long groupId);

    /**
     * 全量同步HR组织信息
     * @param param
     * @return
     */
    public List<SccPjOrganizationTemp> syncAllHrOrganization(Map<String, Object> param);

    /**
     * 处理临时表数据
     * @param level
     * @return
     */
    public List<SccPjOrganization> doPending(Integer level);

    /**
     * 处理临时表数据
     * @return
     */
    public List<SccPjOrganization> doAllPending();
}
