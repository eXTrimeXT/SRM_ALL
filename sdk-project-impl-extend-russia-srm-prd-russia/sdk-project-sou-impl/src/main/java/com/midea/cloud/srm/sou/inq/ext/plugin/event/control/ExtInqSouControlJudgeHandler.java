package com.midea.cloud.srm.sou.inq.ext.plugin.event.control;

import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.control.ApiSouStartNewRoundDTO;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.inq.spi.control.InqSouControlJudgeHandler;
import com.midea.cloud.srm.sou.sourcing.select.service.SouSelectEventService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/**
 * 备注
 * @author huangbf3
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtInqSouControlJudgeHandler extends InqSouControlJudgeHandler {

    @Autowired
    private SouSelectEventService souSelectEventService;

    @Override
    @ApiOperation("当前是否可以发起新一轮")
    public SouProject judgeStartNewRoundAuth(ApiSouStartNewRoundDTO param, String souType) {
        // 1: 先公开本轮结果
        souSelectEventService.openResult(param.getProjectId(), souType);
        // 2: 调用核心功能
        return super.judgeStartNewRoundAuth(param, souType);
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.inq.name();
    }

    @Override
    public int getOrder() {
        return 100;
    }

}
