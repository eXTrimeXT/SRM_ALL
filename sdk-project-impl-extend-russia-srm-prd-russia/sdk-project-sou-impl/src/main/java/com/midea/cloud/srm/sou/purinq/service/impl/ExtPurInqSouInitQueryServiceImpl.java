package com.midea.cloud.srm.sou.purinq.service.impl;

import com.github.pagehelper.page.PageMethod;
import com.midea.cloud.srm.model.extapi.sou.purinq.dto.init.ExtPurInqSouVendorQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouVendorDel;
import com.midea.cloud.srm.sou.purinq.dao.ExtPurInqSouVendorDelDAO;
import com.midea.cloud.srm.sou.purinq.service.ExtPurInqSouInitQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtPurInqSouInitQueryServiceImpl implements ExtPurInqSouInitQueryService {

    @Autowired
    private ExtPurInqSouVendorDelDAO extPurInqSouVendorDelDAO;

    /**
     * 查看被删除的邀请供应商
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public List<ExtPurInqSouVendorDel> queryVendorDel(ExtPurInqSouVendorQueryDTO queryParam) {
        queryParam.formatParams();
        // 1: 查询数据
        if (queryParam.getPageNum() != null && queryParam.getPageSize() != null) {
            PageMethod.startPage(queryParam.getPageNum(), queryParam.getPageSize());
        }
        return extPurInqSouVendorDelDAO.lambdaQuery()
                .eq(ExtPurInqSouVendorDel::getProjectId, queryParam.getProjectId())
                .like(queryParam.getVendorCode() != null, ExtPurInqSouVendorDel::getVendorCode, queryParam.getVendorCode())
                .like(queryParam.getVendorName() != null, ExtPurInqSouVendorDel::getVendorName, queryParam.getVendorName())
                .orderByDesc(ExtPurInqSouVendorDel::getCreationDate)
                .list();
    }

}
