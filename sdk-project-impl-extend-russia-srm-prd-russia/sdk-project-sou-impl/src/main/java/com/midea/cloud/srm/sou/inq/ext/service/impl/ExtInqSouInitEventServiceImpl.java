package com.midea.cloud.srm.sou.inq.ext.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtPjInqSouVendorCheckDTO;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtPjInqSouVendorDelDTO;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPJInqSouVendor;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPJInqSouVendorDel;
import com.midea.cloud.srm.model.extapi.sou.inq.enums.ExtPjInqSouVendorSourceFromTypeEnum;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouProjectStatusEnum;
import com.midea.cloud.srm.model.sup.association.entity.ExtSupAssociation;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.inq.ext.dao.ExtPJInqSouVendorDAO;
import com.midea.cloud.srm.sou.inq.ext.dao.ExtPJInqSouVendorDelDAO;
import com.midea.cloud.srm.sou.inq.ext.service.ExtInqSouInitEventService;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.compent.RiskComponent;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProjectDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouVendorDAO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 长城 - 询比价 - 立项 - 事件服务
 * @author huangbf3
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtInqSouInitEventServiceImpl implements ExtInqSouInitEventService {

    @Autowired
    private SouVendorDAO souVendorDAO;
    @Autowired
    private SouProjectDAO souProjectDAO;
    @Autowired
    private ExtPJInqSouVendorDAO extPjInqSouVendorDao;
    @Autowired
    private ExtPJInqSouVendorDelDAO extPjInqSouVendorDelDao;

    /**
     * 删除新增邀请供应商
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void removeVendor(ExtPjInqSouVendorDelDTO param) {
        // 1: 校验操作条件/权限
        if (param.getSouVendorId() != null) {
            AssertUtils.notNull(param.getSouVendorId(), "缺少souVendorId参数");
            SouVendor souVendor = souVendorDAO.getById(param.getSouVendorId());
            ExtPJInqSouVendor inqSouVendor = extPjInqSouVendorDao.getById(param.getSouVendorId());
            if (souVendor == null) { return; }
            SouProject souProject = souProjectDAO.getById(souVendor.getProjectId());
            AssertUtils.notNull(souProject, "询比价[{0}]不存在", souVendor.getProjectId());
            AssertUtils.isTrue(SouProjectStatusEnum.DRAFT.equals(souProject.getProjectStatus()), "询比价非拟定状态，禁止该操作");
            // 2: 删除数据
            souVendorDAO.removeById(param.getSouVendorId());
            extPjInqSouVendorDao.removeById(param.getSouVendorId());
            // 3: 构造数据，写入删除记录表
            ExtPJInqSouVendorDel delEntity = new ExtPJInqSouVendorDel(); {
                BeanUtils.copyProperties(souVendor, delEntity);
                BeanUtils.copyProperties(inqSouVendor, delEntity);

                delEntity.setSouVendorDelId(IdGenrator.generate());
                delEntity.setDelReason(StringUtils.trimToNull(param.getDelReason()));
            }
            extPjInqSouVendorDelDao.save(delEntity);
        } else {
            AssertUtils.notNull(param.getProjectId(), "缺少projectId参数");
            SouProject souProject = souProjectDAO.getById(param.getProjectId());
            AssertUtils.notNull(souProject, "询比价[{0}]不存在", param.getProjectId());
            AssertUtils.isTrue(SouProjectStatusEnum.DRAFT.equals(souProject.getProjectStatus()), "询比价非拟定状态，禁止该操作");
            // 3: 构造数据，写入删除记录表
            ExtPJInqSouVendorDel delEntity = new ExtPJInqSouVendorDel(); {
                BeanUtils.copyProperties(param, delEntity);
                delEntity.setSourceFromType(ExtPjInqSouVendorSourceFromTypeEnum.HAND_MAKE);

                delEntity.setSouVendorDelId(IdGenrator.generate());
                delEntity.setDelReason(StringUtils.trimToNull(param.getDelReason()));
            }
            extPjInqSouVendorDelDao.save(delEntity);
        }
    }

    @Override
    public Boolean checkVendor(ExtPjInqSouVendorCheckDTO param) {
        AssertUtils.notNull(param.getProjectId(), "缺少projectId参数");

        if(CollectionUtils.isEmpty(param.getVendorIds())) {
            return true;
        }

        LambdaQueryWrapper<SouVendor> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SouVendor::getProjectId, param.getProjectId());
        queryWrapper.notIn(CollectionUtils.isNotEmpty(param.getVendorIds()), SouVendor::getVendorId, param.getVendorIds());
        List<SouVendor> souVendorList = souVendorDAO.list(queryWrapper);
        Set<Long> existsSet = souVendorList.stream().map(SouVendor::getVendorId).collect(Collectors.toSet());
        if(existsSet.isEmpty()) {
            //没有供应商， 不用查关联关系
            return true;
        }

        if(CollectionUtils.isNotEmpty(param.getVendorIds())) {
            //查询新增供应商关联供应商
            List<ExtSupAssociation> nameAassociationsAList = RiskComponent.getInstance().getQlOpenClient().query(ContextPath.SUP, QlOpenWrappers.query(MqlType.SOU_RELATION_SUP_BUYER)
                            .select(ExtSupAssociation::getVendorIdA, ExtSupAssociation::getVendorIdB)
                            .in(ExtSupAssociation::getVendorIdA, param.getVendorIds())
                    , ExtSupAssociation.class);
            Set<Long> associationBSet = nameAassociationsAList.stream().map(ExtSupAssociation::getVendorIdB).collect(Collectors.toSet());
            List<ExtSupAssociation> nameAassociationsBList = RiskComponent.getInstance().getQlOpenClient().query(ContextPath.SUP, QlOpenWrappers.query(MqlType.SOU_RELATION_SUP_BUYER)
                            .select(ExtSupAssociation::getVendorIdA, ExtSupAssociation::getVendorIdB)
                            .in(ExtSupAssociation::getVendorIdB, param.getVendorIds())
                    , ExtSupAssociation.class);
            Set<Long> associationASet = nameAassociationsBList.stream().map(ExtSupAssociation::getVendorIdA).collect(Collectors.toSet());
            associationBSet.addAll(associationASet);
            existsSet.retainAll(associationBSet);
        }

        //集合为空，没有交集， 即供应商没有关联
        return existsSet.isEmpty();
    }

}
