package com.midea.cloud.srm.sou.approve.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.sou.approve.entity.SouApproveUser;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;

import java.util.List;
import java.util.Map;

/**
 * @description scc_npm_sou_approve_user
 * @author panmq2
 * @date 2023-10-23
 */
public interface ISouApproveUserService extends IService<SouApproveUser> {

    /**
     * 添加审批人
     * @param businessId
     * @param userId
     * @param userName
     * @param fullName
     * @return
     */
    public SouApproveUser addApproveUser(Long businessId, Long userId, String userName, String fullName);

    /**
     * 修改审批人信息
     * @param user
     */
    public void modifyApproveUser(SouApproveUser user);

    /**
     * 获取当前审批人
     * @param businessId
     * @return
     */
    public SouApproveUser getCurrentApproveUser(Long businessId);

    /**
     * 获取最新审批人
     * @param businessId
     * @return
     */
    public SouApproveUser getNewestApproveUser(Long businessId);

    /**
     * 批量获取最新审批人
     * @param businessIdList
     * @return
     */
    public Map<Long, SouApproveUser> getNewestApproveUserMap(List<Long> businessIdList);
}

