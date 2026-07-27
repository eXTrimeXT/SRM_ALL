package com.midea.cloud.srm.sou.expert.service;

import com.midea.cloud.srm.model.sou.expert.dto.*;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertApply;
import com.midea.cloud.srm.model.sou.expert.vo.ExtSouExpertApplyVO;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 专家库 - 事件服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/12
 */
public interface ExtSouExpertEventService {

    /**
     * 编辑专家申请
     * @param param 参数
     * @return 返回
     */
    ExtSouExpertApplyDTO editApply(ExtSouExpertApplyDTO param);

    /**
     * 删除专家申请
     * @param expertApplyId {@link ExtSouExpertApply#getExpertApplyId}
     * @return
     */
    @Nullable
    ExtSouExpertApplyVO removeApply(long expertApplyId);

    /**
     * 冻结专家
     * @param param 参数
     */
    void frozenExpert(ExtSouExpertFrozenDTO param);

    /**
     * 确认冻结专家
     * @param expertId 参数
     */
    void frozenExpertConfirm(long expertId);

    /**
     * 拒绝冻结专家
     * @param param 参数
     */
    void frozenExpertReject(ExtSouExpertFrozenRejectDTO param);

    /**
     * 解冻专家
     * @param param 参数
     */
    void unFrozenExpert(ExtSouExpertUnFrozenDTO param);

    /**
     * 确认解冻专家
     * @param expertId 参数
     */
    void unfrozenExpertConfirm(long expertId);

    /**
     * 拒绝解冻专家
     * @param param 参数
     */
    void unfrozenExpertReject(ExtSouExpertFrozenRejectDTO param);

    /**
     * 专家退出
     * @param param 参数
     */
    void quiteExpert(ExtSouExpertQuiteDTO param);

    /**
     * 专家申请审批提交后的回调处理
     * @param expertApplyId {@link ExtSouExpertApply#getExpertApplyId}
     */
    void callbackAfterApplyApprovalSubmit(long expertApplyId);

    /**
     * 专家申请审批通过后的回调处理
     * @param expertApplyId {@link ExtSouExpertApply#getExpertApplyId}
     */
    void callbackAfterApplyApprovalPass(long expertApplyId);

    /**
     * 专家申请审批未通过后的回调处理
     * @param param 回调参数
     */
    void callbackAfterApplyApprovalUnPass(ExtSouExpertApplyUnPassDTO param);

    /**
     * 专家升级审批提交后的回调处理
     * @param expertApplyId {@link ExtSouExpertApply#getExpertApplyId}
     */
    void callbackAfterUpgradeApprovalSubmit(long expertApplyId);

    /**
     * 专家升级审批通过后的回调处理
     * @param expertApplyId {@link ExtSouExpertApply#getExpertApplyId}
     */
    void callbackAfterUpgradeApprovalPass(long expertApplyId);

    /**
     * 专家升级审批未通过后的回调处理
     * @param param 回调参数
     */
    void callbackAfterUpgradeApprovalUnPass(ExtSouExpertApplyUnPassDTO param);

    /**
     * 专家变更审批提交后的回调处理
     * @param expertApplyId {@link ExtSouExpertApply#getExpertApplyId}
     */
    void callbackAfterChangeApprovalSubmit(long expertApplyId);

    /**
     * 专家变更审批通过后的回调处理
     * @param expertApplyId {@link ExtSouExpertApply#getExpertApplyId}
     */
    void callbackAfterChangeApprovalPass(long expertApplyId);

    /**
     * 专家变更审批未通过后的回调处理
     * @param param 回调参数
     */
    void callbackAfterChangeApprovalUnPass(ExtSouExpertApplyUnPassDTO param);

    /**
     * 专家申请绿色通道审批提交后的回调处理
     * @param expertApplyId {@link ExtSouExpertApply#getExpertApplyId}
     */
    void callbackAfterGreenApprovalSubmit(long expertApplyId);

    /**
     * 专家申请绿色通道审批通过后的回调处理
     * @param expertApplyId {@link ExtSouExpertApply#getExpertApplyId}
     */
    void callbackAfterGreenApprovalPass(long expertApplyId);

    /**
     * 专家申请绿色通道审批未通过后的回调处理
     * @param param 回调参数
     */
    void callbackAfterGreenApprovalUnPass(ExtSouExpertApplyUnPassDTO param);

    /**
     * 批量创建专家评审信息
     * @param params 参数
     */
    void createExpertScores(List<ExtSouExpertScoreCreateDTO> params);

    /**
     * 专家评分
     * @param params 参数
     */
    void expertDoScore(List<ExtSouExpertDoScoreDTO> params);

}
