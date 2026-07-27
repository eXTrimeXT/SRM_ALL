package com.midea.cloud.srm.biz.pj.sou.comp.process.service.impl;

import com.midea.cloud.srm.biz.pj.sou.comp.process.dao.CompSouProcessConfigDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.comp.process.service.CompSouProcessQueryWebService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.service.SouProcessQueryService;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.comp.vo.webapi.process.CompSouProcessConfigWebVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.process.ApiSouProcessConfigQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouProcessNodeVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.process.ApiSouProcessConfigVO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 竞价 - 流程配置
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/15
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class CompSouProcessQueryWebServiceImpl implements CompSouProcessQueryWebService {

    @Autowired
    private SouProcessQueryService souProcessQueryService;
    @Autowired
    private CompSouProcessConfigDAOImpl compSouProcessConfigDao;

    /**
     * 采购商端: 流程配置列表查询
     */
    @Override
    public List<CompSouProcessConfigWebVO> listProcessConfigs(ApiSouProcessConfigQueryDTO queryParam) {
        // 1: 查询核心数据
        queryParam.setSouType(SouTypeEnum.comp.name());
        List<ApiSouProcessConfigVO> souProcessConfigList = souProcessQueryService.listProcessConfigs(queryParam);
        // 2: 查询额外数据
        List<CompSouProcessConfig> compProcessConfigList; {
            if (souProcessConfigList.isEmpty()) {
                compProcessConfigList = Collections.emptyList();
            } else {
                compProcessConfigList = compSouProcessConfigDao.lambdaQuery()
                        .in(CompSouProcessConfig::getProcessConfigId, souProcessConfigList.stream()
                                .map(SouProcessConfig::getProcessConfigId).collect(Collectors.toSet()))
                        .list();
            }
        }
        // 3: 组装数据
        return CompSouProcessConfigWebVO.convertCompVO(souProcessConfigList, compProcessConfigList);
    }

    /**
     * 查询指定流程配置信息
     */
    @Override
    public CompSouProcessConfigWebVO getProcessConfig(long processConfigId, @Nullable Long vendorId) {
        // 1: 查询核心数据
        SouProcessConfig souProcessConfig = souProcessQueryService.getProcessConfig(processConfigId, vendorId, SouTypeEnum.comp.name());
        // 2: 数据转化
        return SouObjectXUtil.convertTargetObj(souProcessConfig, CompSouProcessConfigWebVO.class);
    }

    /**
     * 根据寻源单ID查询流程节点信息
     * @param projectId {@link SouProject#getProjectId}
     */
    @Override
    public List<ApiSouProcessNodeVO> listProcessNodes(long projectId) {
        // 1: 查询核心流程数据
        return souProcessQueryService.listProcessNodes(projectId, SouTypeEnum.comp.name());
    }

}
