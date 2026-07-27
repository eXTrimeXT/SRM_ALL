package com.midea.cloud.srm.sou.expert.repo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.midea.cloud.common.sou.SouUserTypeCheckUtils;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.util.ResultUtil;
import com.midea.cloud.srm.model.sou.expert.dto.ExtSouExpertApplyDTO;
import com.midea.cloud.srm.model.sou.expert.dto.ExtSouExpertApplyUnPassDTO;
import com.midea.cloud.srm.model.sou.expert.dto.ExtSouExpertLatestApplyQueryDTO;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertApply;
import com.midea.cloud.srm.model.sou.expert.enums.ExtSouExpertApplyFromTypeEnum;
import com.midea.cloud.srm.model.sou.expert.vo.ExtSouExpertApplyVO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.sou.expert.service.ExtSouExpertEventService;
import com.midea.cloud.srm.sou.expert.service.ExtSouExpertQueryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * 寻源 - 专家申请
 * PS: 专用于导入导出功能
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/12
 */
@RestController
@RequestMapping("/npm/sou-expert")
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtSouExpertApplyForBuyerRepository extends CrudRepository {

    @Autowired
    private ExtSouExpertQueryService extSouExpertQueryService;
    @Autowired
    private ExtSouExpertEventService extSouExpertEventService;

    public ExtSouExpertApplyForBuyerRepository() {
        super();
        // 业务查询
        this.register("getApplyInfoByApplyId", this::getApplyInfoByApplyId, false, "查询专家申请详情");
        this.register("getLatestApplyInfoByUserId", this::getLatestApplyInfoByUserId, false, "根据用户ID查询最新的专家申请详情");
        // 业务事件
        this.register("tempSaveApply", this::tempSaveApply, true, "暂存专家申请");
        this.register("submitApply", this::submitApply, true, "提交专家申请");
        this.register("removeApply", this::removeApply, true, "删除专家申请");
        this.register("callbackAfterApplyApprovalSubmit", this::callbackAfterApplyApprovalSubmit, true, "专家申请审批提交后的回调处理");
        this.register("callbackAfterApplyApprovalPass", this::callbackAfterApplyApprovalPass, true, "专家申请审批通过后的回调处理");
        this.register("callbackAfterApplyApprovalUnPass", this::callbackAfterApplyApprovalUnPass, true, "专家申请审批未通过后的回调处理");
        this.register("callbackAfterUpgradeApprovalSubmit", this::callbackAfterUpgradeApprovalSubmit, true, "专家升级审批提交后的回调处理");
        this.register("callbackAfterUpgradeApprovalPass", this::callbackAfterUpgradeApprovalPass, true, "专家升级审批通过后的回调处理");
        this.register("callbackAfterUpgradeApprovalUnPass", this::callbackAfterUpgradeApprovalUnPass, true, "专家升级审批未通过后的回调处理");
        this.register("callbackAfterChangeApprovalSubmit", this::callbackAfterChangeApprovalSubmit, true, "专家变更审批提交后的回调处理");
        this.register("callbackAfterChangeApprovalPass", this::callbackAfterChangeApprovalPass, true, "专家变更审批通过后的回调处理");
        this.register("callbackAfterChangeApprovalUnPass", this::callbackAfterChangeApprovalUnPass, true, "专家变更审批未通过后的回调处理");
        this.register("callbackAfterGreenApprovalSubmit", this::callbackAfterGreenApprovalSubmit, true, "专家申请绿色通道审批提交后的回调处理");
        this.register("callbackAfterGreenApprovalPass", this::callbackAfterGreenApprovalPass, true, "专家申请绿色通道审批通过后的回调处理");
        this.register("callbackAfterGreenApprovalUnPass", this::callbackAfterGreenApprovalUnPass, true, "专家申请绿色通道审批未通过后的回调处理");
    }

    @ApiOperation("查询专家申请详情")
    private QlResult getApplyInfoByApplyId(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        long expertApplyId; {
            List<ExtSouExpertApply> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtSouExpertApply>>() {});
            AssertUtils.notEmpty(params, "缺少数据");
            ExtSouExpertApply param = params.get(0);
            AssertUtils.notNull(param.getExpertApplyId(), "缺少expertApplyId参数");
            expertApplyId = param.getExpertApplyId();
        }

        ExtSouExpertApplyVO result = extSouExpertQueryService.getApplyInfoByApplyId(expertApplyId);
        if (result != null) {
            return ResultUtil.build(queryAction, "expertApplyId", Collections.singletonList(result), false);
        } else {
            result = new ExtSouExpertApplyVO();
            result.setExpertApplyId(-1L);
            return ResultUtil.build(queryAction, "expertApplyId", Collections.singletonList(result), false);
        }
    }

    @ApiOperation("根据用户ID查询最新的专家申请详情")
    @SuppressWarnings("UnnecessaryBoxing")
    private QlResult getLatestApplyInfoByUserId(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        ExtSouExpertLatestApplyQueryDTO param; {
            List<ExtSouExpertLatestApplyQueryDTO> paramList = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtSouExpertLatestApplyQueryDTO>>() {});
            AssertUtils.notEmpty(paramList, "缺少数据");
            param = paramList.get(0);
        }

        ExtSouExpertApplyVO result = extSouExpertQueryService.getLatestApplyInfoByUserId(param);
        if (result != null) {
            return ResultUtil.build(queryAction, "expertApplyId", Collections.singletonList(result), false);
        } else {
            result = new ExtSouExpertApplyVO();
            result.setExpertApplyId(-1L);
            return ResultUtil.build(queryAction, "expertApplyId", Collections.singletonList(result), false);
        }
    }

    @ApiOperation("暂存专家申请")
    private QlResult tempSaveApply(QlQueryAction queryAction) {
        return this.editApply(queryAction, true);
    }

    @ApiOperation("提交专家申请")
    private QlResult submitApply(QlQueryAction queryAction) {
        return this.editApply(queryAction, false);
    }

    @ApiOperation("编辑专家申请")
    private QlResult editApply(QlQueryAction queryAction, boolean isTempSave) {
        SouUserTypeCheckUtils.checkIsBuyer();

        ExtSouExpertApplyDTO param; {
            List<ExtSouExpertApplyDTO> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtSouExpertApplyDTO>>() {});
            AssertUtils.notEmpty(params, "缺少数据");
            param = params.get(0);
            param.setTempSave(isTempSave);
            if (param.getApplyFromType() != null) {
                if (ExtSouExpertApplyFromTypeEnum.INDEPENDENT.name().equals(param.getApplyFromType())
                        || ExtSouExpertApplyFromTypeEnum.UPGRADE.name().equals(param.getApplyFromType())) {
                    // 自主申请/升级申请
                    param.setApplyById(AppUserUtil.getLoginAppUser().getUserId());
                    param.setApplyBy(AppUserUtil.getLoginAppUser().getUsername());
                    param.setApplyByCode(AppUserUtil.getLoginAppUser().getCeeaEmpNo());
                    param.setApplyByNickname(AppUserUtil.getLoginAppUser().getNickname());
                } else if (ExtSouExpertApplyFromTypeEnum.GREEN_CHANNEL.name().equals(param.getApplyFromType())) {
                    // 绿色通道
                    AssertUtils.notNull(param.getApplyById(), "缺少applyById参数");
                    AssertUtils.notNull(param.getApplyBy(), "缺少applyBy参数");
                    AssertUtils.notNull(param.getApplyByNickname(), "缺少applyByNickname参数");
                    AssertUtils.notNull(param.getApplyByCode(), "缺少applyByCode参数");
                }
            }
        }

        param = extSouExpertEventService.editApply(param);
        return ResultUtil.build(queryAction, "expertApplyId", Collections.singletonList(param), false);
    }

    @ApiOperation("删除专家申请")
    private QlResult removeApply(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        long expertApplyId; {
            List<ExtSouExpertApply> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtSouExpertApply>>() {});
            AssertUtils.notEmpty(params, "缺少数据");
            ExtSouExpertApply param = params.get(0);
            AssertUtils.notNull(param.getExpertApplyId(), "缺少expertApplyId参数");
            expertApplyId = param.getExpertApplyId();
        }

        ExtSouExpertApplyVO result = extSouExpertEventService.removeApply(expertApplyId);
        if (result != null) {
            return ResultUtil.build(queryAction, "expertApplyId", Collections.singletonList(result), false);
        } else {
            result = new ExtSouExpertApplyVO();
            result.setExpertApplyId(-1L);
            return ResultUtil.build(queryAction, "expertApplyId", Collections.singletonList(result), false);
        }
    }

    @ApiOperation("专家申请审批提交后的回调处理")
    private QlResult callbackAfterApplyApprovalSubmit(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        long expertApplyId; {
            List<ExtSouExpertApply> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtSouExpertApply>>() {});
            AssertUtils.notEmpty(params, "缺少数据");
            ExtSouExpertApply param = params.get(0);
            AssertUtils.notNull(param.getExpertApplyId(), "缺少expertApplyId参数");
            expertApplyId = param.getExpertApplyId();
        }

        extSouExpertEventService.callbackAfterApplyApprovalSubmit(expertApplyId);
        return QlResult.empty();
    }

    @ApiOperation("专家申请审批通过后的回调处理")
    private QlResult callbackAfterApplyApprovalPass(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        long expertApplyId; {
            List<ExtSouExpertApply> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtSouExpertApply>>() {});
            AssertUtils.notEmpty(params, "缺少数据");
            ExtSouExpertApply param = params.get(0);
            AssertUtils.notNull(param.getExpertApplyId(), "缺少expertApplyId参数");
            expertApplyId = param.getExpertApplyId();
        }

        extSouExpertEventService.callbackAfterApplyApprovalPass(expertApplyId);
        return QlResult.empty();
    }

    @ApiOperation("专家申请未审批通过后的回调处理")
    private QlResult callbackAfterApplyApprovalUnPass(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        ExtSouExpertApplyUnPassDTO param; {
            List<ExtSouExpertApplyUnPassDTO> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtSouExpertApplyUnPassDTO>>() {});
            AssertUtils.notEmpty(params, "缺少数据");
            param = params.get(0);
        }

        extSouExpertEventService.callbackAfterApplyApprovalUnPass(param);
        return QlResult.empty();
    }

    @ApiOperation("专家升级审批提交后的回调处理")
    private QlResult callbackAfterUpgradeApprovalSubmit(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        long expertApplyId; {
            List<ExtSouExpertApply> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtSouExpertApply>>() {});
            AssertUtils.notEmpty(params, "缺少数据");
            ExtSouExpertApply param = params.get(0);
            AssertUtils.notNull(param.getExpertApplyId(), "缺少expertApplyId参数");
            expertApplyId = param.getExpertApplyId();
        }

        extSouExpertEventService.callbackAfterUpgradeApprovalSubmit(expertApplyId);
        return QlResult.empty();
    }

    @ApiOperation("专家升级审批通过后的回调处理")
    private QlResult callbackAfterUpgradeApprovalPass(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        long expertApplyId; {
            List<ExtSouExpertApply> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtSouExpertApply>>() {});
            AssertUtils.notEmpty(params, "缺少数据");
            ExtSouExpertApply param = params.get(0);
            AssertUtils.notNull(param.getExpertApplyId(), "缺少expertApplyId参数");
            expertApplyId = param.getExpertApplyId();
        }

        extSouExpertEventService.callbackAfterUpgradeApprovalPass(expertApplyId);
        return QlResult.empty();
    }

    @ApiOperation("专家升级未审批通过后的回调处理")
    private QlResult callbackAfterUpgradeApprovalUnPass(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        ExtSouExpertApplyUnPassDTO param; {
            List<ExtSouExpertApplyUnPassDTO> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtSouExpertApplyUnPassDTO>>() {});
            AssertUtils.notEmpty(params, "缺少数据");
            param = params.get(0);
        }

        extSouExpertEventService.callbackAfterUpgradeApprovalUnPass(param);
        return QlResult.empty();
    }

    @ApiOperation("专家变更审批提交后的回调处理")
    private QlResult callbackAfterChangeApprovalSubmit(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        long expertApplyId; {
            List<ExtSouExpertApply> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtSouExpertApply>>() {});
            AssertUtils.notEmpty(params, "缺少数据");
            ExtSouExpertApply param = params.get(0);
            AssertUtils.notNull(param.getExpertApplyId(), "缺少expertApplyId参数");
            expertApplyId = param.getExpertApplyId();
        }

        extSouExpertEventService.callbackAfterChangeApprovalSubmit(expertApplyId);
        return QlResult.empty();
    }

    @ApiOperation("专家变更审批通过后的回调处理")
    private QlResult callbackAfterChangeApprovalPass(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        long expertApplyId; {
            List<ExtSouExpertApply> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtSouExpertApply>>() {});
            AssertUtils.notEmpty(params, "缺少数据");
            ExtSouExpertApply param = params.get(0);
            AssertUtils.notNull(param.getExpertApplyId(), "缺少expertApplyId参数");
            expertApplyId = param.getExpertApplyId();
        }

        extSouExpertEventService.callbackAfterChangeApprovalPass(expertApplyId);
        return QlResult.empty();
    }

    @ApiOperation("专家变更未审批通过后的回调处理")
    private QlResult callbackAfterChangeApprovalUnPass(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        ExtSouExpertApplyUnPassDTO param; {
            List<ExtSouExpertApplyUnPassDTO> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtSouExpertApplyUnPassDTO>>() {});
            AssertUtils.notEmpty(params, "缺少数据");
            param = params.get(0);
        }

        extSouExpertEventService.callbackAfterChangeApprovalUnPass(param);
        return QlResult.empty();
    }

    @ApiOperation("专家申请绿色通道审批提交后的回调处理")
    private QlResult callbackAfterGreenApprovalSubmit(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        long expertApplyId; {
            List<ExtSouExpertApply> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtSouExpertApply>>() {});
            AssertUtils.notEmpty(params, "缺少数据");
            ExtSouExpertApply param = params.get(0);
            AssertUtils.notNull(param.getExpertApplyId(), "缺少expertApplyId参数");
            expertApplyId = param.getExpertApplyId();
        }

        extSouExpertEventService.callbackAfterGreenApprovalSubmit(expertApplyId);
        return QlResult.empty();
    }

    @ApiOperation("专家申请绿色通道审批通过后的回调处理")
    private QlResult callbackAfterGreenApprovalPass(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        long expertApplyId; {
            List<ExtSouExpertApply> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtSouExpertApply>>() {});
            AssertUtils.notEmpty(params, "缺少数据");
            ExtSouExpertApply param = params.get(0);
            AssertUtils.notNull(param.getExpertApplyId(), "缺少expertApplyId参数");
            expertApplyId = param.getExpertApplyId();
        }

        extSouExpertEventService.callbackAfterGreenApprovalPass(expertApplyId);
        return QlResult.empty();
    }

    @ApiOperation("专家申请绿色通道未审批通过后的回调处理")
    private QlResult callbackAfterGreenApprovalUnPass(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        ExtSouExpertApplyUnPassDTO param; {
            List<ExtSouExpertApplyUnPassDTO> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtSouExpertApplyUnPassDTO>>() {});
            AssertUtils.notEmpty(params, "缺少数据");
            param = params.get(0);
        }

        extSouExpertEventService.callbackAfterGreenApprovalUnPass(param);
        return QlResult.empty();
    }

}
