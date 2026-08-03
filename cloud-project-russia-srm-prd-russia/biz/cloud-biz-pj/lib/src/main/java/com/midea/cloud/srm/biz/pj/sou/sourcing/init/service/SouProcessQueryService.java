package com.midea.cloud.srm.biz.pj.sou.sourcing.init.service;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.process.ApiSouProcessConfigQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouProcessNodeVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.process.ApiSouProcessConfigVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 寻源 - 流程控制 - 信息查询服务
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/18
 */
public interface SouProcessQueryService {

    /**
     * 流程配置列表查询
     * @param queryParam
     * @return
     */
    List<ApiSouProcessConfigVO> listProcessConfigs(ApiSouProcessConfigQueryDTO queryParam);

    /**
     * 查询指定流程配置信息
     * @param processConfigId
     * @param vendorId
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return
     */
    SouProcessConfig getProcessConfig(long processConfigId, @Nullable Long vendorId, String souType);

    /**
     * 根据寻源单ID查询流程节点信息
     * @param projectId {@link SouProject#getProjectId}
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return
     */
    List<ApiSouProcessNodeVO> listProcessNodes(long projectId, String souType);

}
