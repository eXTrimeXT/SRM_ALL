package com.midea.cloud.srm.base.organizationrelation.service.impl;

import com.midea.cloud.srm.base.organizationrelation.mapper.OrgRelMapper;
import com.midea.cloud.srm.base.organizationrelation.service.OrgRelService;
import com.midea.cloud.srm.model.pj.base.organization.dto.TreeNew;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ex_liuxy46
 */
@Service
public class OrgRelServiceImpl implements OrgRelService {

    @Resource
    private OrgRelMapper orgRelMapper;

    /**
     * treeNew
     *
     * @return list
     */
    @Override
    public List<TreeNew> allTree() {
        return null;
    }
}
