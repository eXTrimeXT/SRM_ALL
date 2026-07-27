package com.midea.cloud.srm.sou.purinq.plugin.event.init.editvendor;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.inq.enums.ExtPurInqSouTypeEnum;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouVendor;
import com.midea.cloud.srm.model.extapi.sou.purinq.enums.ExtPurInqSouVendorSourceFromTypeEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiSouVendorDTO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.sou.purinq.dao.ExtPurInqSouVendorDAO;
import com.midea.cloud.srm.sou.sourcing.spi.init.editvendor.ApiSouVendorEditHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.editvendor.SouVendorEditPO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: for srm集采询比价 - 立项保存邀请供应商信息
 *
 * @author srm
 * @date 2024-05-18
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtPurInqSouVendorEditHandler extends ApiSouVendorEditHandler {

    @Autowired
    private ExtPurInqSouVendorDAO extPurInqSouVendorDAO;

    @Override
    protected SouVendorEditPO convert(long projectId, List<ApiSouVendorDTO> params, boolean isTempSave, boolean isCopy) {
        SouVendorEditPO po = super.convert(projectId, params, isTempSave, isCopy);
        // 处理额外的询价供应商信息
        if (CollectionUtils.isNotEmpty(params)) {
            List<ExtPurInqSouVendor> inqVendorList = new ArrayList<>(po.getVendorList().size());
            for (ApiSouVendorDTO param : params) {
                ExtPurInqSouVendor entity = new ExtPurInqSouVendor();
                inqVendorList.add(entity);

                entity.setSouVendorId(param.getSouVendorId());
                entity.setProjectId(projectId);
                entity.setVendorId(param.getVendorId());
                String sourceFromType = param.getX(SouObjectXUtil.getFieldByLambda(ExtPurInqSouVendor::getSourceFromType));
                AssertUtils.notNull(sourceFromType, "缺少sourceFromType参数");
                try {
                    entity.setSourceFromType(ExtPurInqSouVendorSourceFromTypeEnum.valueOf(sourceFromType));
                } catch (Exception e) {
                    throw new IllegalArgumentException("非法的sourceFromType参数:" + sourceFromType);
                }
                // 是否新供应商
                entity.setNewVendorTag(Enable.N);
            }

            extPurInqSouVendorDAO.saveOrUpdate(projectId, inqVendorList, ExtPurInqSouVendor::getProjectId);
        }

        return po;
    }

    @Override
    public String matchModule() {
        return ExtPurInqSouTypeEnum.ext_pur_inq.name();
    }

    @Override
    public int getOrder() {
        return 10;
    }

}
