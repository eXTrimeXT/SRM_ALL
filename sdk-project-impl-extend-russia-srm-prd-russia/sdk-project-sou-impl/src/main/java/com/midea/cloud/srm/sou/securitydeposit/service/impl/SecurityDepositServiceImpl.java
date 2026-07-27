package com.midea.cloud.srm.sou.securitydeposit.service.impl;

import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.meiql.api.spec.ql.QlQueryFieldWrapper;
import com.midea.cloud.srm.model.sou.sourcing.dto.SecurityDepositDto;
import com.midea.cloud.srm.model.supcooperate.report.purchase.dto.PurchaseOrderProcessDto;
import com.midea.cloud.srm.sou.securitydeposit.mapper.SecurityDepositMapper;
import com.midea.cloud.srm.sou.securitydeposit.service.SecurityDepositService;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/24
 */
@Service
public class SecurityDepositServiceImpl implements SecurityDepositService {
    @Autowired
    SecurityDepositMapper securityDepositMapper;
    @Override
    public List<SecurityDepositDto> list(Map<String,Object> query) {
        /**
         * 多次扣款
         *  分页查询-底表数据
         */
        PageUtil.startPage(MapUtils.getInteger(query, QlQueryFieldWrapper.field(PurchaseOrderProcessDto::getPageNum).getFieldName(), 1),
        MapUtils.getInteger(query, QlQueryFieldWrapper.field(PurchaseOrderProcessDto::getPageSize).getFieldName(), 15));
        List<SecurityDepositDto> list=  securityDepositMapper.list(query);
        return list;
    }
}
