package com.midea.cloud.srm.sou.purinq.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.extapi.sou.purinq.dto.init.ApiPurInqSouProjectQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;

import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
public interface ExtPurInqSouProjectMapper extends BaseMapper<ExtPurInqSouProject> {

    /**
     * 询价列表查询
     * @param queryParam
     * @return
     */
    List<SouProject> listPurInqProjects(ApiPurInqSouProjectQueryDTO queryParam);

}
