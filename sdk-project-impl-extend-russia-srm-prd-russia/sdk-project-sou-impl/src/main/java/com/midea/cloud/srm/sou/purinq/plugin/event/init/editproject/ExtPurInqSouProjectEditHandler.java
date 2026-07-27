package com.midea.cloud.srm.sou.purinq.plugin.event.init.editproject;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCurrency;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.inq.enums.ExtPurInqSouTypeEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiSouProjectEditDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiSouProjectInfoDTO;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProcessConfig;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.*;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProcessConfigDAO;
import com.midea.cloud.srm.sou.sourcing.spi.init.editproject.ApiSouProjectEditHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtPurInqSouProjectEditHandler extends ApiSouProjectEditHandler {

    @Autowired
    private SouProcessConfigDAO souProcessConfigDAO;

    @Override
    protected void formatAndValidateProject(ApiSouProjectEditDTO param, Map<String/* currencyCode */, PurchaseCurrency> currencyMap,
                                            boolean isTempSave, boolean isCopy, String souType) {
        // 设置默认的流程配置
        SouProcessConfig processConfig = souProcessConfigDAO.lambdaQuery()
                .eq(SouProcessConfig::getSouType, ExtPurInqSouTypeEnum.ext_pur_inq)
                .eq(SouProcessConfig::getProcessStatus, SouProcessConfigStatusEnum.VALID)
                .one();
        AssertUtils.notNull(processConfig, "集采询比价缺少一个默认的流程配置");
        param.setProcessConfigId(processConfig.getProcessConfigId());
        param.setOrderType(SouOrderTypeEnum.SIMPLE);
        param.setOrderWay(SouOrderWayEnum.SINGLE);
        param.setPublishScope(SouPublishScopeEnum.INVITE_TENDER);
        param.setSourceFromType(SouSourceFromTypeEnum.SOU_REQ.name());

        super.formatAndValidateProject(param, currencyMap, isTempSave, isCopy, souType);
        param.setSourceFromType("DESIGN_PLAN");
    }

    @Override
    protected void formatAndValidateGroups(ApiSouProjectInfoDTO param, boolean isTempSave, boolean isCopy) {
        param.setGroupList(Collections.emptyList());
    }

    @Override
    @ApiOperation("转换得到寻源信息")
    protected SouProject doConvertProject(ApiSouProjectEditDTO project, boolean isTempSave, String sequenceCode, String souType) {
        SouProject souProject = super.doConvertProject(project, isTempSave, sequenceCode, souType);

        souProject.setCurrentRound(1);
        souProject.setAllowPartPrice(Enable.Y);
        return souProject;
    }

    @Override
    public String matchModule() {
        return ExtPurInqSouTypeEnum.ext_pur_inq.name();
    }

    @Override
    public int getOrder() {
        return 100;
    }

}
