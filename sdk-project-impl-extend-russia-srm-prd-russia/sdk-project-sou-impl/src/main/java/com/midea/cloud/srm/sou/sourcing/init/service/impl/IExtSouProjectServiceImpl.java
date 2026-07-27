package com.midea.cloud.srm.sou.sourcing.init.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.midea.cloud.srm.model.base.organization.entity.OrganizationRelation;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouDemand;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrderItem;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouProjectMapper;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class IExtSouProjectServiceImpl extends ServiceImpl<ExtSouProjectMapper, ExtSouProject> implements IExtSouProjectService {
    @Resource
    private IExtSouProjectService projectService;
    @Override
    public List<Map<String, Object>> statisticalBidTimes(List<Long> vendorIdList, Long extCategoryId) {
        Map<String, Object> param = new HashMap<>(15);
        param.put("vendorIdList", vendorIdList);
        param.put("extCategoryId", extCategoryId);
        return this.baseMapper.statisticalBidTimes(param);
    }
    @Override
    public List<ExtSouProject> queryByProjectNo(ExtSouProject extSouProject) {
        LambdaQueryWrapper<ExtSouProject> projectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        projectLambdaQueryWrapper.eq(ExtSouProject::getExtProjectNo, extSouProject.getExtProjectNo());
        List<ExtSouProject> extSouProjectList = projectService.list(projectLambdaQueryWrapper);
        return extSouProjectList;
    }
}
