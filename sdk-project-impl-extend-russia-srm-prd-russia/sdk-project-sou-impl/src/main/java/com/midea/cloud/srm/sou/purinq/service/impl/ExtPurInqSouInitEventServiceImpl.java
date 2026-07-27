package com.midea.cloud.srm.sou.purinq.service.impl;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.model.extapi.sou.purinq.dto.init.ExtPurInqSouVendorDelDTO;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouVendor;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouVendorDel;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouProjectStatusEnum;
import com.midea.cloud.srm.sou.purinq.dao.ExtPurInqSouVendorDAO;
import com.midea.cloud.srm.sou.purinq.dao.ExtPurInqSouVendorDelDAO;
import com.midea.cloud.srm.sou.purinq.service.ExtPurInqSouInitEventService;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProjectDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouVendorDAO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtPurInqSouInitEventServiceImpl implements ExtPurInqSouInitEventService {

    @Autowired
    private SouVendorDAO souVendorDAO;
    @Autowired
    private ExtPurInqSouVendorDAO extPurInqSouVendorDAO;
    @Autowired
    private SouProjectDAO souProjectDAO;
    @Autowired
    private ExtPurInqSouVendorDelDAO extPurInqSouVendorDelDAO;

    /**
     * 删除新增供应商
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void removeVendor(ExtPurInqSouVendorDelDTO param) {
        AssertUtils.notNull(param.getSouVendorId(), "缺少souVendorId参数");
        // 1: 校验操作条件/权限
        SouVendor souVendor = souVendorDAO.getById(param.getSouVendorId());
        ExtPurInqSouVendor inqSouVendor = extPurInqSouVendorDAO.getById(param.getSouVendorId());
        if (souVendor == null) { return; }
        SouProject souProject = souProjectDAO.getById(souVendor.getProjectId());
        AssertUtils.notNull(souProject, "询比价[{0}]不存在", souVendor.getProjectId());
        AssertUtils.isTrue(SouProjectStatusEnum.DRAFT.equals(souProject.getProjectStatus()), "询比价非拟定状态，禁止该操作");
        // 2: 删除数据
        souVendorDAO.removeById(param.getSouVendorId());
        extPurInqSouVendorDAO.removeById(param.getSouVendorId());
        // 3: 构造数据，写入删除记录表
        ExtPurInqSouVendorDel delEntity = new ExtPurInqSouVendorDel(); {
            BeanUtils.copyProperties(souVendor, delEntity);
            BeanUtils.copyProperties(inqSouVendor, delEntity);

            delEntity.setSouVendorDelId(IdGenrator.generate());
            delEntity.setDelReason(StringUtils.trimToNull(param.getDelReason()));
        }
        extPurInqSouVendorDelDAO.save(delEntity);
    }

}
