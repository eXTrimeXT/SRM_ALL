package com.midea.cloud.srm.sou.purinq.plugin.event.control;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.inq.enums.ExtPurInqSouTypeEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.control.ApiSouDecryptPriceDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.control.ApiSouStartNewRoundDTO;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouRound;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouProjectStatusEnum;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProjectDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouRoundDAO;
import com.midea.cloud.srm.sou.sourcing.select.service.SouSelectEventService;
import com.midea.cloud.srm.sou.sourcing.spi.control.ApiSouControlJudgeHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtPurInqSouControlJudgeHandler extends ApiSouControlJudgeHandler {

    @Autowired
    private SouSelectEventService souSelectEventService;
    @Autowired
    private SouProjectDAO souProjectDAO;
    @Autowired
    private SouRoundDAO souRoundDAO;

    @Override
    @ApiOperation("当前是否可以进行商务开标")
    public SouProject judgeBusinessOpenAuth(long projectId, @Nullable Long currentUserId, String souType) {
        SouProject project = souProjectDAO.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单[{0}]不存在"), projectId);
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型[{0}]不匹配"), souType);
        switch (project.getProjectStatus()) {
            // 报价未开始
            case ORDER_NOT_START:
                throw new IllegalArgumentException("未完成供应商报价环节，禁止商务开标");
                // 接收报价中
            case ACCEPT_ORDER:
                throw new IllegalArgumentException("报价未截止，禁止商务开标");
                // 报价已截止
            case ORDER_END:
                // 技术开标
            case TECH_EVAL:
                break;
            // 商务开标
            case BUSINESS_EVAL:
                // 评选中
            case EVALUATING:
                throw new IllegalArgumentException("当前已开标，请勿重复操作");
            default:
                throw new IllegalArgumentException("当前单据状态禁止操作");
        }
        return project;
    }

    @Override
    @ApiOperation("当前是否可以报价解密")
    public SouProject judgeDecryptPriceAuth(ApiSouDecryptPriceDTO param, String souType) {
        SouProject project = souProjectDAO.getById(param.getProjectId());
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单[{0}]不存在"), param.getProjectId());
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型[{0}]不匹配"), souType);
        AssertUtils.isTrue(Enable.Y.equals(project.getNeedEncryptPrice()), "无需报价解密");
        switch (project.getProjectStatus()) {
            // 报价截止
            case ORDER_END:
                throw new IllegalArgumentException("未商务开标，禁止操作");
                // 商务开标
            case BUSINESS_EVAL:
                // 技术开标
            case TECH_EVAL:
                break;
            // 评选中
            case EVALUATING:
                // 定价中
            case PRICING:
                // 定价驳回
            case PRICE_REJECT:
                // 已定价
            case PRICE_END:
                throw new IllegalArgumentException("已报价解密，无需重复操作");
            default:
                throw new IllegalArgumentException("当前单据状态禁止操作");
        }
        SouRound currentRound = souRoundDAO.lambdaQuery()
                .eq(SouRound::getProjectId, param.getProjectId())
                .eq(SouRound::getRound, project.getCurrentRound())
                .one();
        if (SouProjectStatusEnum.TECH_EVAL.equals(project.getProjectStatus())) {
            // 技术开标，判断是否已进行商务开标
            AssertUtils.isTrue(Enable.Y.equals(currentRound.getBusinessOpen()), "未商务开标，禁止操作");
        }
        return project;
    }

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
        return ExtPurInqSouTypeEnum.ext_pur_inq.name();
    }

    @Override
    public int getOrder() {
        return 100;
    }

}
