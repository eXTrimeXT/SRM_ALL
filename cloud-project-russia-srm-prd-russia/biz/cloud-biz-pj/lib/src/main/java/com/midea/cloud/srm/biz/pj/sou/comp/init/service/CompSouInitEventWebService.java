package com.midea.cloud.srm.biz.pj.sou.comp.init.service;

import com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.init.*;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouCancelDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pm.pr.requirement.dto.RequirementManageDTO;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

/**
 * 竞价 - 立项业务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/12
 */
public interface CompSouInitEventWebService {

    /**
     * 暂存/提交项目信息
     * @param param 参数
     * @param isCopy 复制
     * @return
     */
    long/* projectId */ editProjectInfo(ApiCompSouProjectInfoDTO param, boolean isCopy);

    /**
     * 需求池创建寻源单
     * @param reqParams 参数
     * @return
     */
    SouProject tempSaveCompFromReq(List<RequirementManageDTO> reqParams);

    /**
     * 暂存/提交物料需求
     * @param param 参数
     * @param isCopy 复制
     * @param currentUserId 用户id
     */
    void editRequireInfo(ApiCompSouRequireInfoDTO param, boolean isCopy, @Nullable Long currentUserId);

    /**
     * 暂存/提交邀请供应商
     * @param param 参数
     * @param isCopy 复制
     */
    void editInviteSupplier(ApiCompSouVendorInfoDTO param, boolean isCopy);

    /**
     * 暂存/提交评分规则
     * @param param 参数
     */
    void editScoreRule(ApiCompSouScoreInfoDTO param);

    /**
     * 删除询价单
     * @param projectId id
     */
    void removeComp(long projectId);

    /**
     * 作废寻源单
     * @param param 参数
     */
    void cancelComp(ApiSouCancelDTO param);

    /**
     * 备注
     * @param params 参数
     * @return
     */
    SouProject editSouBidInfo(Map<String, Object> params);
}
