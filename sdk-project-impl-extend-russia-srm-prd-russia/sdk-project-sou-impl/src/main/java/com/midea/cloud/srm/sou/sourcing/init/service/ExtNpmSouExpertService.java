package com.midea.cloud.srm.sou.sourcing.init.service;

import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouExpertRandomExtractDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouExpertRecord;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouGroup;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
public interface ExtNpmSouExpertService {

    /**
     * 查询专家库
     * @param param
     * @param project
     * @param groupList
     * @return
     */
    List<ExtSouExpertRecord> queryExpert(ApiExtSouExpertRandomExtractDto param, ExtSouProject project, List<ExtSouGroup> groupList);
}
