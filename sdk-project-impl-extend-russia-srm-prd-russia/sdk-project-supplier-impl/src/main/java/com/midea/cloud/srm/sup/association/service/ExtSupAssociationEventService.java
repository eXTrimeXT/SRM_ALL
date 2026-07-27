package com.midea.cloud.srm.sup.association.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.sup.association.dto.ApiExtSupAssociationDTO;
import com.midea.cloud.srm.model.sup.association.entity.ExtSupAssociation;

/**
 * @author luxc18
 */
public interface ExtSupAssociationEventService extends IService<ExtSupAssociation> {

    /**
     * 编辑/提交寻源基本信息
     * @param param
     * @return
     */
    Long editProject(ApiExtSupAssociationDTO param);


    /**
     * 调整投标截止时间
     *
     * @param projectId
     * @param souType
     * @return
     */
   // Long delProject(Long projectId);

}
