package com.midea.cloud.srm.supcooperate.ext.requirement.pr.service;

import java.util.Map;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/5
 */
public interface EdmattachSyncService {

    /**
     * 添加附件同步任务
     * @param requirementHeadId
     */
    public void addSyncTask(Long requirementHeadId);

    /**
     * 同步附件
     * @param param
     */
    public void syncAttach(Map<String, Object> param);
}
