package com.midea.cloud.srm.biz.pj.changchengapi.material.service;

import com.midea.cloud.srm.model.pj.changchengapi.material.MaterialParam;
import com.midea.cloud.srm.model.pj.changchengapi.material.MaterialResultDto;
import com.midea.cloud.srm.model.pj.changchengapi.yangguan.ResultDTO;

/**
 * @Description: for srm 非生产物料接口定义类
 *
 * @author srm
 * @date 2024-05-17
 */
public interface IMaterialService {

    /**
     * 非生产物料接口查询
     * @param materialParam
     * @return
     */
    ResultDTO<MaterialResultDto> page(MaterialParam materialParam);
}
