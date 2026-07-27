package com.midea.cloud.srm.sou.sourcing.spi.init;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.process.ApiSouProcessNodeStatusChangeDTO;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProcessConfig;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProcessNode;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouProcessConfigStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProcessConfigDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProcessNodeDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProjectDAO;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProcessConfigService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.sou.sourcing.spi.process.ApiSouProcessEditHandler;
import com.midea.cloud.srm.sou.sourcing.spi.process.ApiSouProcessJudgeHandler;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
/**
 * 备注
 * @author huangbf3
 */
@Service
public class ApiExtSouProcessJudgeHandler extends ApiSouProcessJudgeHandler {

    @Autowired
    private SouProcessConfigDAO souProcessConfigDAO;
    @Autowired
    private IExtSouProjectService projectService;
    @Autowired
    private SouProcessNodeDAO souProcessNodeDAO;

    @Autowired
    private IExtSouProcessConfigService processConfigService;

    @Override
    @ApiOperation("当前是否可以查询指定的流程配置信息")
    public SouProcessConfig judgeGetProcessConfigAuth(long processConfigId, @Nullable Long vendorId) {
        SouProcessConfig processConfig = (SouProcessConfig)this.souProcessConfigDAO.getById(processConfigId);
        AssertUtils.notNull(processConfig, LocaleHandler.getLocaleMsg("流程配置信息") + "[{0}]" + LocaleHandler.getLocaleMsg("不存在"), new Object[]{processConfigId});
        if (vendorId != null) {
            AssertUtils.isFalse(SouProcessConfigStatusEnum.DRAFT.equals(processConfig.getProcessStatus()), "禁止查询拟定状态的流程配置信息", new Object[0]);
        }

        return processConfig;
    }

    @Override
    @Nullable
    @ApiOperation("当前是否可以访问寻源单据关联的流程节点信息")
    public Long judgeListProcessNodesAuth(long projectId, String souType) {
        souType = StringUtils.trimToNull(souType);
        AssertUtils.notNull(souType, "缺少souType参数", new Object[0]);
        ExtSouProject project = projectService.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源项目") + "[{0}]" + LocaleHandler.getLocaleMsg("不存在"), new Object[]{projectId});
        return project.getProcessConfigId();
    }

    @Override
    @ApiOperation("当前是否可以新增/修改寻源配置信息")
    public void judgeEditProcessConfigAuth(@Nullable Long processConfigId) {
        if (processConfigId != null) {
            SouProcessConfig processConfig = (SouProcessConfig)this.souProcessConfigDAO.getById(processConfigId);
            AssertUtils.notNull(processConfig, LocaleHandler.getLocaleMsg("寻源配置信息") + "[{0}]" + LocaleHandler.getLocaleMsg("不存在"), new Object[]{processConfigId});
            AssertUtils.isTrue(SouProcessConfigStatusEnum.DRAFT.equals(processConfig.getProcessStatus()), "寻源配置非拟定状态，不能修改", new Object[0]);
        }

    }

    @Override
    @ApiOperation("当前是否可以生效寻源配置信息")
    public void judgeValidProcessConfigAuth(long processConfigId) {
        SouProcessConfig processConfig = (SouProcessConfig)this.souProcessConfigDAO.getById(processConfigId);
        AssertUtils.notNull(processConfig, LocaleHandler.getLocaleMsg("寻源配置信息") + "[{0}]" + LocaleHandler.getLocaleMsg("不存在"), new Object[]{processConfigId});
        if (SouProcessConfigStatusEnum.DRAFT.equals(processConfig.getProcessStatus())) {
            ((ApiSouProcessEditHandler) SouActiveBeanUtils.getActiveBean(SouTypeEnum.DEFAULT.name(), ApiSouProcessEditHandler.class)).formatValidateAndConvert(processConfig, false);
        }

    }

    @Override
    @ApiOperation("当前是否可以失效寻源配置信息")
    public void judgeInvalidProcessConfigAuth(long processConfigId) {
        SouProcessConfig processConfig = (SouProcessConfig)this.souProcessConfigDAO.getById(processConfigId);
        AssertUtils.notNull(processConfig, LocaleHandler.getLocaleMsg("寻源配置信息") + "[{0}]" + LocaleHandler.getLocaleMsg("不存在"), new Object[]{processConfigId});
        AssertUtils.isFalse(SouProcessConfigStatusEnum.DRAFT.equals(processConfig.getProcessStatus()), "拟定状态的寻源配置信息不能失效", new Object[0]);
    }

    @Override
    @ApiOperation("当前是否可以删除寻源配置信息")
    public SouProcessConfig judgeRemoveProcessConfigAuth(long processConfigId) {
        SouProcessConfig processConfig = (SouProcessConfig)this.souProcessConfigDAO.getById(processConfigId);
        AssertUtils.notNull(processConfig, LocaleHandler.getLocaleMsg("寻源配置信息") + "[{0}]" + LocaleHandler.getLocaleMsg("不存在"), new Object[]{processConfigId});
        AssertUtils.isTrue(SouProcessConfigStatusEnum.DRAFT.equals(processConfig.getProcessStatus()), "仅能删除拟定状态的寻源配置信息", new Object[0]);
        return processConfig;
    }

    @Override
    @ApiOperation("当前是否可以创建寻源单关联的流程节点")
    public void judgeCreateProcessNodesAuth(long processConfigId, long projectId) {
        SouProcessConfig processConfig = (SouProcessConfig)this.souProcessConfigDAO.getById(processConfigId);
        AssertUtils.notNull(processConfig, LocaleHandler.getLocaleMsg("寻源配置信息") + "[{0}]" + LocaleHandler.getLocaleMsg("不存在"), new Object[]{processConfigId});
        AssertUtils.isTrue(SouProcessConfigStatusEnum.VALID.equals(processConfig.getProcessStatus()), "寻源配置不是生效状态，不能使用", new Object[0]);
        long existCount = ((LambdaQueryChainWrapper)this.souProcessNodeDAO.lambdaQuery().eq(SouProcessNode::getProjectId, projectId)).count();
        AssertUtils.isTrue(existCount <= 0, "寻源单已绑定寻源配置，禁止修改", new Object[0]);
    }

    @Override
    @ApiOperation("当前是否可以更新寻源单关联的流程节点状态")
    public void judgeUpdateProcessNodeStatusAuth(ApiSouProcessNodeStatusChangeDTO param, String souType) {
        ExtSouProject project = projectService.getById(param.getProjectId());
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单") + "[{0}]" + LocaleHandler.getLocaleMsg("不存在"), new Object[]{param.getProjectId()});
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型") + "[{0}]" + LocaleHandler.getLocaleMsg("不匹配"), new Object[]{souType});
        long existCount = ((LambdaQueryChainWrapper)this.souProcessNodeDAO.lambdaQuery().eq(SouProcessNode::getProjectId, param.getProjectId())).count();
        AssertUtils.isTrue(existCount > 0, "寻源单未绑定寻源配置", new Object[0]);
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
