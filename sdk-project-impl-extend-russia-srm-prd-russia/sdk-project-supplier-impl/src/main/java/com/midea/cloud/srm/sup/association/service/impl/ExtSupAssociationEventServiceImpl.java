package com.midea.cloud.srm.sup.association.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.sup.association.dto.ApiExtSupAssociationDTO;
import com.midea.cloud.srm.model.sup.association.entity.ExtSupAssociation;
import com.midea.cloud.srm.sup.association.mapper.ExtSupAssociationMapper;
import com.midea.cloud.srm.sup.association.service.ExtSupAssociationEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author ex_yipeng
 */
@Slf4j
@Service
public class ExtSupAssociationEventServiceImpl extends ServiceImpl<ExtSupAssociationMapper, ExtSupAssociation> implements ExtSupAssociationEventService {


    @Autowired
    private BaseClient baseClient;

    @Resource
    private ExtSupAssociationMapper supAssociationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long editProject(ApiExtSupAssociationDTO param) {
        // 入参校验+转换处理
        ExtSupAssociation po = this.doConvertAssociation(param);
        //保存项目信息
        this.saveOrUpdate(po);
        return po.getAssociationId();
    }

    protected ExtSupAssociation doConvertAssociation(ApiExtSupAssociationDTO projectInfo) {

        ExtSupAssociation extSupAssociation = new ExtSupAssociation();
        if (Objects.isNull(projectInfo.getAssociationId())) {
            projectInfo.setAssociationId(IdGenrator.generate());
        }
        BeanUtils.copyProperties(projectInfo, extSupAssociation);
        return extSupAssociation;
    }
}
