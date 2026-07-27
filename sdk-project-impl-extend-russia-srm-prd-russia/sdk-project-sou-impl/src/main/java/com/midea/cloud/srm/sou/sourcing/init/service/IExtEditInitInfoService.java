package com.midea.cloud.srm.sou.sourcing.init.service;

import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiSouInitDTO;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
public interface IExtEditInitInfoService {

    /**
     * 立项信息整体暂存/提交
     *
     * @param param   立项信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void editInitInfo(ApiSouInitDTO param, String souType);
}
