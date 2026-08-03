package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.process;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProcessConfigDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProcessNodeDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.process.ApiSouProcessNodeStatusChangeDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessNode;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouProcessConfigStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

/**
 * 寻源openAPI - 流程配置接口判断
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/29
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ApiSouProcessJudgeHandler implements ISouSpiBean {

    @Autowired
    private SouProcessConfigDAOImpl souProcessConfigDao;
    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private SouProcessNodeDAOImpl souProcessNodeDao;

    @ApiOperation("当前是否可以查询指定的流程配置信息")
    public SouProcessConfig judgeGetProcessConfigAuth(long processConfigId, @Nullable Long vendorId) {
        SouProcessConfig processConfig = souProcessConfigDao.getById(processConfigId);
        AssertUtils.notNull(processConfig, LocaleHandler.getLocaleMsg("流程配置信息")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), processConfigId);

        if (vendorId != null) {
            AssertUtils.isFalse(SouProcessConfigStatusEnum.DRAFT.equals(processConfig.getProcessStatus()),
                    "禁止查询拟定状态的流程配置信息");
        }
        return processConfig;
    }

    /**
     * 当前是否可以访问寻源单据关联的流程节点信息
     * @param projectId {@link SouProject#getProjectId}
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Nullable
    @ApiOperation("当前是否可以访问寻源单据关联的流程节点信息")
    public Long/* processConfigId */ judgeListProcessNodesAuth(long projectId, String souType) {
        souType = StringUtils.trimToNull(souType);
        AssertUtils.notNull(souType, "缺少souType参数");
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源项目")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), projectId);

        return project.getProcessConfigId();
    }

    @ApiOperation("当前是否可以新增/修改寻源配置信息")
    public void judgeEditProcessConfigAuth(@Nullable Long processConfigId) {
        if (processConfigId != null) {
            SouProcessConfig processConfig = souProcessConfigDao.getById(processConfigId);
            AssertUtils.notNull(processConfig, LocaleHandler.getLocaleMsg("寻源配置信息")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), processConfigId);
            AssertUtils.isTrue(SouProcessConfigStatusEnum.DRAFT.equals(processConfig.getProcessStatus()), "寻源配置非拟定状态，不能修改");
        }
    }

    @ApiOperation("当前是否可以生效寻源配置信息")
    public void judgeValidProcessConfigAuth(long processConfigId) {
        SouProcessConfig processConfig = souProcessConfigDao.getById(processConfigId);
        AssertUtils.notNull(processConfig, LocaleHandler.getLocaleMsg("寻源配置信息")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), processConfigId);
        if (SouProcessConfigStatusEnum.DRAFT.equals(processConfig.getProcessStatus())) {
            // 拟定状态(确保没有漏填的信息)
            SouActiveBeanUtils.getActiveBean(SouTypeEnum.DEFAULT.name(), ApiSouProcessEditHandler.class)
                    .formatValidateAndConvert(processConfig, false);
        }
    }

    @ApiOperation("当前是否可以失效寻源配置信息")
    public void judgeInvalidProcessConfigAuth(long processConfigId) {
        SouProcessConfig processConfig = souProcessConfigDao.getById(processConfigId);
        AssertUtils.notNull(processConfig, LocaleHandler.getLocaleMsg("寻源配置信息")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), processConfigId);
        AssertUtils.isFalse(SouProcessConfigStatusEnum.DRAFT.equals(processConfig.getProcessStatus()), "拟定状态的寻源配置信息不能失效");
    }

    @ApiOperation("当前是否可以删除寻源配置信息")
    public SouProcessConfig judgeRemoveProcessConfigAuth(long processConfigId) {
        SouProcessConfig processConfig = souProcessConfigDao.getById(processConfigId);
        AssertUtils.notNull(processConfig, LocaleHandler.getLocaleMsg("寻源配置信息")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), processConfigId);
        AssertUtils.isTrue(SouProcessConfigStatusEnum.DRAFT.equals(processConfig.getProcessStatus()), "仅能删除拟定状态的寻源配置信息");
        return processConfig;
    }

    /**
     * 当前是否可以创建寻源单关联的流程节点
     * @param processConfigId 寻源配置ID
     * @param projectId 寻源单ID
     */
    @ApiOperation("当前是否可以创建寻源单关联的流程节点")
    public void judgeCreateProcessNodesAuth(long processConfigId, long projectId) {
        SouProcessConfig processConfig = souProcessConfigDao.getById(processConfigId);
        AssertUtils.notNull(processConfig, LocaleHandler.getLocaleMsg("寻源配置信息")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), processConfigId);
        AssertUtils.isTrue(SouProcessConfigStatusEnum.VALID.equals(processConfig.getProcessStatus()), "寻源配置不是生效状态，不能使用");
        long existCount = souProcessNodeDao.lambdaQuery()
                .eq(SouProcessNode::getProjectId, projectId)
                .count();
        AssertUtils.isTrue(existCount <= 0, "寻源单已绑定寻源配置，禁止修改");
    }

    @ApiOperation("当前是否可以更新寻源单关联的流程节点状态")
    public void judgeUpdateProcessNodeStatusAuth(ApiSouProcessNodeStatusChangeDTO param, String souType) {
        SouProject project = souProjectDao.getById(param.getProjectId());
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), param.getProjectId());
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型")+"[{0}]"+LocaleHandler.getLocaleMsg("不匹配"), souType);
        long existCount = souProcessNodeDao.lambdaQuery()
                .eq(SouProcessNode::getProjectId, param.getProjectId())
                .count();
        AssertUtils.isTrue(existCount > 0, "寻源单未绑定寻源配置");
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
