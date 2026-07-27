package com.midea.cloud.srm.sou.purinq.plugin.event.init.domain;

import com.midea.cloud.srm.model.extapi.sou.purinq.dto.init.ApiPurInqSouItemDTO;
import com.midea.cloud.srm.model.extapi.sou.purinq.dto.init.ApiPurInqSouProjectEditDTO;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouCurrency;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
public interface ExtPurInqSouInitDomainService {

    /**
     * 保存询价额外的基本信息
     * @param projectId
     * @param inqProject
     * @param isTempSave
     */
    void editProjectInfo(long projectId, ApiPurInqSouProjectEditDTO inqProject, boolean isTempSave);

    /**
     * 保存询价额外的币种信息
     * @param inqProject
     * @param currencyList
     */
    void editCurrency(ApiPurInqSouProjectEditDTO inqProject, @Nullable List<ExtPurInqSouCurrency> currencyList);

    /**
     * 保存简易询价额外的物料需求信息
     * @param project
     * @param itemDTOList
     * @param userId
     * @param isTempSave
     */
    void editRequireInfo(SouProject project, List<ApiPurInqSouItemDTO> itemDTOList, @Nullable Long userId, boolean isTempSave);

}
