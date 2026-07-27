package com.midea.cloud.srm.supcooperate.ext.requirement.pr.service.impl;

import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.meiql.api.spec.ql.QlQueryFieldWrapper;
import com.midea.cloud.srm.model.supcooperate.report.purchase.dto.PurchaseOrderProcessDto;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.ExtFixPriceTimelinessRatioDetail;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.ExtFixPriceTimelinessRatioHead;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.mapper.ExtFixPriceTimelinessRatioMapper;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.service.ExtFixPriceTimelinessRatioService;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/7/5
 */
@Service
public class ExtFixPriceTimelinessRatioServiceImpl implements ExtFixPriceTimelinessRatioService {
    @Autowired
    ExtFixPriceTimelinessRatioMapper extFixPriceTimelinessRatioMapper;
    @Override
    public List<ExtFixPriceTimelinessRatioDetail> list(Map<String, Object> query) {
        PageUtil.startPage(MapUtils.getInteger(query, QlQueryFieldWrapper.field(PurchaseOrderProcessDto::getPageNum).getFieldName(), 1),
                MapUtils.getInteger(query, QlQueryFieldWrapper.field(PurchaseOrderProcessDto::getPageSize).getFieldName(), 15));
        return extFixPriceTimelinessRatioMapper.list(query);
    }
    @Override
    public List<ExtFixPriceTimelinessRatioDetail> listById(Map<String, Object> query) {
        return extFixPriceTimelinessRatioMapper.listById(query);
    }

    @Override
    public List<ExtFixPriceTimelinessRatioHead> get(Map<String, Object> query) {
        PageUtil.startPage(MapUtils.getInteger(query, QlQueryFieldWrapper.field(PurchaseOrderProcessDto::getPageNum).getFieldName(), 1),
                MapUtils.getInteger(query, QlQueryFieldWrapper.field(PurchaseOrderProcessDto::getPageSize).getFieldName(), 15));
        return extFixPriceTimelinessRatioMapper.get(query);
    }
}
