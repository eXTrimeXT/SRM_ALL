package com.midea.cloud.srm.sou.sourcing.init.service;

import com.midea.cloud.srm.model.sou.enums.ExtOrderTypeEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtNpmSouOpenBidRecord;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;

import java.util.List;

/**
 * @Author: panmq
 * @Date: 2024/03/20/ $
 * @Description:
 */
public interface ExtNpmSouOpenTodoService {

    /**
     * 发送待办
     * @param souProjectList
     */
    public void sendTodo(List<ExtSouProject> souProjectList);

    /**
     * 转已办
     * @param projectId
     */
    public void havedone(Long projectId);

    /**
     * 转已办
     * @param projectId
     * @param record
     */
    public void havedone(Long projectId, ExtNpmSouOpenBidRecord record);
}
