package com.midea.cloud.srm.sou.sourcing.init.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouProjectDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.init.ApiExtSouProcessConfigVo;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProcessConfig;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouProcessNodeEnum;
/**
 * 备注
 * @author huangbf3
 */
public interface IExtSouProcessConfigService extends IService<ExtSouProcessConfig> {

    /**
     * 生成招标单默认流程
     * @param projectId
     * @return
     */
    public Long generateDefaultProcessConfig(Long projectId);

    /**
     * 更新节点状态， isTempSave 为false时才会更新
     * @param projectId
     * @param nodeEnum
     * @param nodeStatus
     * @param isTempSave
     * @return
     */
    public Long updateNodeStatus(Long projectId, SouProcessNodeEnum nodeEnum, Enable nodeStatus, Boolean isTempSave);

    /**
     * 查询流程配置信息
     * @param processConfigId
     * @return
     */
    public ApiExtSouProcessConfigVo listSouProcessConfig(Long processConfigId);

    /**
     * 修正非材招标流程节点
     * @param project
     */
    public void fixNpmProcessAndNode(ExtSouProjectDto project);
}
