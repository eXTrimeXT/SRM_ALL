package com.midea.cloud.srm.sup.association.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.sup.association.dto.ApiExtSupAssociationDTO;
import com.midea.cloud.srm.model.sup.association.dto.ApiExtSupAssociationQueryDTO;
import com.midea.cloud.srm.model.sup.association.entity.ExtSupAssociation;
import com.midea.cloud.srm.sup.association.mapper.ExtSupAssociationMapper;
import com.midea.cloud.srm.sup.association.service.ExtSupAssociationQueryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
/**
 * @author luxc18
 */
@Service
@Slf4j
public class ExtSupAssociationQueryServiceImpl extends ServiceImpl<ExtSupAssociationMapper, ExtSupAssociation> implements ExtSupAssociationQueryService {


    @Autowired
    private BaseClient baseClient;

    @Resource
    private ExtSupAssociationMapper supAssociationMapper;


    @Override
    public List<ExtSupAssociation> listProjects(ApiExtSupAssociationQueryDTO souProjectQuery) {
        //格式化请求参数
        souProjectQuery.formatParams();
        //分页参数
        if (!Objects.isNull(souProjectQuery.getPageNum()) && !Objects.isNull(souProjectQuery.getPageSize())) {
            PageUtil.startPage(souProjectQuery.getPageNum(), souProjectQuery.getPageSize());
        }
        //查询条件
        LambdaQueryWrapper<ExtSupAssociation> queryWrapper = new LambdaQueryWrapper<>();
        if (!ObjectUtils.isEmpty(souProjectQuery.getAssociationType())) {
            queryWrapper.eq(ExtSupAssociation::getAssociationType, souProjectQuery.getAssociationType());
        }
        //供应商A
        queryWrapper.like(StringUtils.isNotBlank(souProjectQuery.getVendorCodeA()), ExtSupAssociation::getVendorCodeA, souProjectQuery.getVendorCodeA());
        //供应商B
        queryWrapper.like(StringUtils.isNotBlank(souProjectQuery.getVendorCodeB()), ExtSupAssociation::getVendorCodeB, souProjectQuery.getVendorCodeB());
        //创建人
        queryWrapper.like(StringUtils.isNotBlank(souProjectQuery.getCreatedFullName()), ExtSupAssociation::getCreatedFullName, souProjectQuery.getCreatedFullName());
        //创建日期从
        queryWrapper.gt(!Objects.isNull(souProjectQuery.getCreationDateFrom()), ExtSupAssociation::getCreationDate, souProjectQuery.getCreationDateFrom());
        //创建日期至
        queryWrapper.lt(!Objects.isNull(souProjectQuery.getCreationDateTo()), ExtSupAssociation::getCreationDate, souProjectQuery.getCreationDateTo());

        queryWrapper.orderByDesc(ExtSupAssociation::getAssociationId);
        List<ExtSupAssociation> souProjectList = supAssociationMapper.selectList(queryWrapper);
        return souProjectList;
    }

    @Override
    public ApiExtSupAssociationDTO getProjectInfo(Long projectId) {
        ApiExtSupAssociationDTO apiExtSupAssociationDTO = new ApiExtSupAssociationDTO();
        BeanUtils.copyProperties(this.getById(projectId), apiExtSupAssociationDTO);
        return apiExtSupAssociationDTO;
    }
}
