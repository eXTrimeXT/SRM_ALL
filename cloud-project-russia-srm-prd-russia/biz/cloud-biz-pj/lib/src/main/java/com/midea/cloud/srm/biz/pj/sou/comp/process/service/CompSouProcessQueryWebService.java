package com.midea.cloud.srm.biz.pj.sou.comp.process.service;

import com.midea.cloud.srm.model.pj.sou.comp.vo.webapi.process.CompSouProcessConfigWebVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.process.ApiSouProcessConfigQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouProcessNodeVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 竞价 - 流程配置
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/15
 */
public interface CompSouProcessQueryWebService {

    /**
     * 流程配置列表查询
     * @param queryParam
     * @return
     */
    List<CompSouProcessConfigWebVO> listProcessConfigs(ApiSouProcessConfigQueryDTO queryParam);

    /**
     * 查询指定流程配置信息
     * @param processConfigId
     * @param vendorId
     * @return
     */
    CompSouProcessConfigWebVO getProcessConfig(long processConfigId, @Nullable Long vendorId);

    /**
     * 根据寻源单ID查询流程节点信息
     * @param projectId {@link SouProject#getProjectId}
     * @param projectId
     * @return
     */
    List<ApiSouProcessNodeVO> listProcessNodes(long projectId);

}