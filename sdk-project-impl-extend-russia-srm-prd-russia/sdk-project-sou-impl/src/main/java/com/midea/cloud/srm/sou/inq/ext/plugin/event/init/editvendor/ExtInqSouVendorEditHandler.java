package com.midea.cloud.srm.sou.inq.ext.plugin.event.init.editvendor;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPJInqSouVendor;
import com.midea.cloud.srm.model.extapi.sou.inq.enums.ExtPjInqSouVendorSourceFromTypeEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiSouVendorDTO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.inq.ext.dao.ExtPJInqSouVendorDAO;
import com.midea.cloud.srm.sou.sourcing.spi.init.editvendor.ApiSouVendorEditHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.editvendor.SouVendorEditPO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 长城 - 询比价 - 立项保存邀请供应商信息
 * @author huangbf3
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtInqSouVendorEditHandler extends ApiSouVendorEditHandler {

    @Autowired
    private ExtPJInqSouVendorDAO extPjInqSouVendorDao;
    @Override
    protected SouVendorEditPO convert(long projectId, List<ApiSouVendorDTO> params, boolean isTempSave, boolean isCopy) {
        SouVendorEditPO po = super.convert(projectId, params, isTempSave, isCopy);
        // 处理额外的询价供应商信息
        if (CollectionUtils.isNotEmpty(params)) {
            List<ExtPJInqSouVendor> inqVendorList = new ArrayList<>(po.getVendorList().size());
            for (ApiSouVendorDTO param : params) {
                ExtPJInqSouVendor entity = new ExtPJInqSouVendor();
                inqVendorList.add(entity);

                entity.setSouVendorId(param.getSouVendorId());
                entity.setProjectId(projectId);
                String sourceFromType = param.getX(SouObjectXUtil.getFieldByLambda(ExtPJInqSouVendor::getSourceFromType));
                AssertUtils.notNull(sourceFromType, "缺少sourceFromType参数");
                try {
                    entity.setSourceFromType(ExtPjInqSouVendorSourceFromTypeEnum.valueOf(sourceFromType));
                } catch (Exception e) {
                    throw new IllegalArgumentException("非法的sourceFromType参数:" + sourceFromType);
                }
                // 是否新供应商
                entity.setNewVendorTag(Enable.N);
            }

            extPjInqSouVendorDao.saveOrUpdate(projectId, inqVendorList, ExtPJInqSouVendor::getProjectId);
        }

        return po;
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.inq.name();
    }

    @Override
    public int getOrder() {
        return 10;
    }

}
