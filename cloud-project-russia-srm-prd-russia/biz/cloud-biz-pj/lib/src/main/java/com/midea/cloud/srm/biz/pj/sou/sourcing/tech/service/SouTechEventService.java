package com.midea.cloud.srm.biz.pj.sou.sourcing.tech.service;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.tech.ApiSouTechScoreDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;

/**
 * 寻源核心 - 技术标事件服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/26
 */
public interface SouTechEventService {

    /**
     * 技术评分/代理评分
     * @param param 评分信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void techScore(ApiSouTechScoreDTO param, String souType);

    /**
     * 技术开标
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void openTech(long projectId, String souType);

}
