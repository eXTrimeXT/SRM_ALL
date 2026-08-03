package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.process;

import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Service;

/**
 * 寻源openAPI - 流程配置业务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/05
 */
@Service
public class ApiSouProcessEventHandler implements ISouSpiBean {

    @ApiOperation("编辑/提交流程配置前的额外处理")
    public void doHandlerBeforeEditProcessConfig(SouProcessConfig param, boolean isTempSave) {
    }

    @ApiOperation("编辑/提交流程配置后的额外处理")
    public void doHandlerAfterEditProcessConfig(SouProcessConfig param, boolean isTempSave) {
    }

    @ApiOperation("生效流程配置前的额外操作")
    public void doHandlerBeforeValidProcessConfig(long processConfigId, String souType) {
    }

    @ApiOperation("生效流程配置后的额外操作")
    public void doHandlerAfterValidProcessConfig(long processConfigId, String souType) {
    }

    @ApiOperation("失效流程配置前的额外操作")
    public void doHandlerBeforeInvalidProcessConfig(long processConfigId, String souType) {
    }

    @ApiOperation("失效流程配置后的额外操作")
    public void doHandlerAfterInvalidProcessConfig(long processConfigId, String souType) {
    }

    @ApiOperation("删除流程配置前的额外处理")
    public void doHandlerBeforeRemoveProcessConfig(long processConfigId, String souType) {
    }

    @ApiOperation("删除流程配置后的额外处理")
    public SouProcessConfig doHandlerAfterRemoveProcessConfig(long processConfigId, String souType, SouProcessConfig souProcessConfig) {
        return souProcessConfig;
    }

    @ApiOperation("创建寻源单相关流程节点后的额外处理")
    public void doHandlerAfterCreateProcessNodes(long processConfigId, long projectId, String souType) {
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
