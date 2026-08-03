package com.midea.cloud.srm.biz.pj.sou.sourcing.init.service;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.process.ApiSouProcessNodeStatusChangeDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouProcessNodeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;

/**
 * 寻源 - 流程信息 - 业务事件服务
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/18
 */
public interface SouProcessEventService {

    /**
     * 编辑/提交流程配置
     * @param param 编辑信息
     * @param isTempSave true-暂存/false-提交
     * @return
     */
    long/* processConfigId */ editProcessConfig(SouProcessConfig param, boolean isTempSave);

    /**
     * 生效流程配置
     * @param processConfigId 流程配置ID
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void validProcessConfig(long processConfigId, String souType);

    /**
     * 失效流程配置
     * @param processConfigId 流程配置ID
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void invalidProcessConfig(long processConfigId, String souType);

    /**
     * 采购商端: 删除流程配置
     * @param processConfigId 流程配置ID
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return
     */
    SouProcessConfig removeProcessConfig(long processConfigId, String souType);

    /**
     * 采购商端: 创建寻源单关联的流程节点
     * @param processConfigId 寻源配置ID
     * @param projectId 寻源单ID
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void createProcessNodes(long processConfigId, long projectId, String souType);

    /**
     * 更新寻源单关联的流程节点状态(仅更新指定的节点)
     * @param param 节点信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void updateProcessNodeStatus(ApiSouProcessNodeStatusChangeDTO param, String souType);

    /**
     * 采购商端: 更新寻源单关联的流程节点状态(仅更新指定的节点)
     * @param projectId 寻源单ID
     * @param nodeName 需要更新状态的节点{@link SouProcessNodeEnum}
     * @param flag 更新状态
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void updateProcessNodeStatus(long projectId, String nodeName, Enable flag, String souType);

    /**
     * 采购商端: 专用于寻源立项阶段的节点更新
     * PS: 例如要更新'项目信息'节点，则'物料需求'、'邀请供应商'、'评分规则'等节点均置为灰色
     * @param projectId 寻源单ID
     * @param nodeName 需要更新状态的节点{@link SouProcessNodeEnum}
     * @param flag 更新状态
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void updateProcessNodeStatusForInit(long projectId, String nodeName, Enable flag, String souType);

}
