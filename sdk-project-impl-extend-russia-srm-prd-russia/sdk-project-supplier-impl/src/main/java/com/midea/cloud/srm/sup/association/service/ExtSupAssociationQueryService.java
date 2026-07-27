package com.midea.cloud.srm.sup.association.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.sup.association.dto.ApiExtSupAssociationDTO;
import com.midea.cloud.srm.model.sup.association.dto.ApiExtSupAssociationQueryDTO;
import com.midea.cloud.srm.model.sup.association.entity.ExtSupAssociation;

import java.util.List;

/**
 * @author luxc18
 */
public interface ExtSupAssociationQueryService extends IService<ExtSupAssociation> {

    /**
     * 主页面查询
     *
     * @param souProjectQuery
     * @param souType
     * @return
     */
    List<ExtSupAssociation> listProjects(ApiExtSupAssociationQueryDTO souProjectQuery);

    /**
     * 基本信息
     *
     * @param projectId
     * @return
     */
    ApiExtSupAssociationDTO getProjectInfo(Long projectId);

}
