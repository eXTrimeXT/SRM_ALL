package com.midea.cloud.srm.biz.pj.hrusertemp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjHrUserInfo;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.pj.hrusertemps.entity.SccPjUserTemp;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author huangbf3
 */
public interface ISccPjUserTempService extends IService<SccPjUserTemp> {
    /**
     * 备注
     * @param param
     * @param groupId
     * @return
     */
    public List<SccPjUserTemp> syncHrUser(Map<String, Object> param, Long groupId);

    /**
     * 备注
     * @param param
     * @return
     */
    public List<SccPjUserTemp> syncAllHrUser(Map<String, Object> param);

    /**
     * 备注
     * @param groupId
     * @return
     */
    public List<SccPjUser> doPending(Long groupId);

    /**
     * 备注
     * @return
     */
    public List<SccPjUser> doAllPending();

    /**
     * 根据员工工号查询员工信息
     * @param personnelNo
     * @return
     */
    @Nullable
    SccPjHrUserInfo getHrUserInfo(String personnelNo);

    /**
     * 根据员工工号批量查询员工信息
     * @param personnelNos
     * @return
     */
    Map<String/* 工号 */, SccPjHrUserInfo> listHrUserInfos(Set<String> personnelNos);

}
