package com.midea.cloud.srm.biz.pj.sou.sourcing.init.service;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouProcessNodeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.springframework.lang.Nullable;

/**
 * 寻源 - 立项 - 业务事件服务
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/19
 */
public interface SouInitEventService {

    /**
     * 编辑/提交寻源基本信息
     * @param param 寻源基本信息
     * @param isCopy true-单据复制场景
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void editProject(ApiSouProjectInfoDTO param, boolean isCopy, String souType);

    /**
     * 编辑/提交寻源需求信息
     * @param param 物料需求信息
     * @param isCopy true-单据复制场景
     * @param userId 用户ID
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void editRequires(ApiSouRequireInfoDTO param, boolean isCopy, @Nullable Long userId, String souType);

    /**
     * 编辑/提交邀请供应商信息
     * @param param 供应商信息
     * @param isCopy true-单据复制场景
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void editVendors(ApiSouVendorInfoDTO param, boolean isCopy, String souType);

    /**
     * 选定评分规则
     * @param param 评分规则信息
     * @param isCopy true-单据复制场景
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void editScoreRule(ApiSouInitScoreInfoDTO param, boolean isCopy, String souType);

    /**
     * 立项信息整体暂存/提交
     * @param param 立项信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void editInitInfo(ApiSouInitDTO param, String souType);

    /**
     * 删除寻源单
     * @param projectId 寻源单ID
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return
     */
    SouProject removeSou(long projectId, String souType);

    /**
     * 作废寻源单
     * @param param 作废信息
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return
     */
    SouProject cancelSou(ApiSouCancelDTO param, String souType);

    /**
     * 复制寻源单
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return
     */
    long/* projectId */ copySou(long projectId, String souType);

    /**
     * 未开启立项审批时，自动提交审批通过
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param processNode 流程节点，说明当前处于哪个节点步骤中
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void autoSubmitPass(long projectId, SouProcessNodeEnum processNode, String souType);

    /**
     * 立项审批提交后的回调处理
     * @param projectId {@link SouProject#getProjectId}
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void callbackAfterApprovalSubmit(long projectId, String souType);

    /**
     * 立项审批通过后的回调处理
     * @param projectId {@link SouProject#getProjectId}
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void callbackAfterApprovalPass(long projectId, String souType);

    /**
     * 立项审批未通过后的回调处理
     * @param param 回调参数
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void callbackAfterApprovalUnPass(ApiSouCreateApprovalUnPassDTO param, String souType);

}
