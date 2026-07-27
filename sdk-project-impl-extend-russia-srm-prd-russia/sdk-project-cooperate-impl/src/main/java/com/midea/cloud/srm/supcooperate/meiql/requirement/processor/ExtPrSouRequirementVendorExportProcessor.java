package com.midea.cloud.srm.supcooperate.meiql.requirement.processor;

import com.alibaba.fastjson.JSONObject;
import com.meicloud.paas.ies.core.model.ExcelExportRequest;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.BeanMapUtils;
import com.midea.cloud.common.utils.EasyExcelUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.dto.ExtPrSouRequirementVendorExcelDto;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementVendor;
import com.midea.cloud.srm.sies.pojo.SiesExecuteParam;
import com.midea.cloud.srm.sies.pojo.SiesExportParam;
import com.midea.cloud.srm.sies.pojo.SiesExportResult;
import com.midea.cloud.srm.sies.pojo.SiesMediator;
import com.midea.cloud.srm.sies.processor.AbstractExportProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: panmq
 * @Date: 2024/03/06/ $
 * @Description: 招标计划池-推荐单位导出
 */
@Component
@Slf4j
public class ExtPrSouRequirementVendorExportProcessor extends AbstractExportProcessor {
    @Autowired
    private QlService qlService;

    @Override
    public boolean isSync(ExcelExportRequest<SiesExportParam> exportRequest, SiesExecuteParam executeParam, SiesExportParam exportParam) {
        return true;
    }

    @Override
    public SiesExportResult<JSONObject> doExport(String iesTaskId, SiesExportParam exportParam, SiesMediator curMediator, int sheetNo, String sheetName, Integer batchNo, Integer batchSize) {
        ExtPrSouRequirementVendor params = (ExtPrSouRequirementVendor) exportParam.getExtParam();
        List<ExtPrSouRequirementVendor> vendorList = qlService.queryByWrapper(QlWrappers.query(MqlType.EXT_PR_SOU_REQUIREMENT_VENDOR).eq(ExtPrSouRequirementVendor::getRequirementHeadId, params.getRequirementHeadId()), ExtPrSouRequirementVendor.class);

        SiesExportResult<JSONObject> siesExportResult = new SiesExportResult<>();
        siesExportResult.setData(new ArrayList<>(16));
        if(CollectionUtils.isNotEmpty(vendorList)) {
            vendorList.stream().forEach(vendor -> {
                JSONObject object = new JSONObject(BeanMapUtils.beanToMap(vendor));
                siesExportResult.getData().add(object);
            });
        }
        siesExportResult.setTotalCount(siesExportResult.getData().size());
        return siesExportResult;
    }

    public void doExportAsEasyExcel(ExtPrSouRequirementVendor params, HttpServletResponse response) {

        RequirementHead requirementHead = qlService.readByKey(MqlType.PURCHASE_REQUIREMENT_HEAD, params.getRequirementHeadId(), RequirementHead.class);
        if(ObjectUtils.anyNull(requirementHead)) {
            throw new BaseException("招标计划单据不存在");
        }

        List<ExtPrSouRequirementVendorExcelDto> vendorList = qlService.queryByWrapper(QlWrappers.query(MqlType.EXT_PR_SOU_REQUIREMENT_VENDOR).eq(ExtPrSouRequirementVendor::getRequirementHeadId, params.getRequirementHeadId()), ExtPrSouRequirementVendorExcelDto.class);

        try {
            OutputStream outputStream = EasyExcelUtil.getServletOutputStream(response, "招标计划["+requirementHead.getRequirementHeadNum()+"]-推荐单位导出");
            EasyExcelUtil.writeExcelWithModel(outputStream, vendorList, ExtPrSouRequirementVendorExcelDto.class, "推荐单位", new ExtCellWriteHandler());
        } catch (Exception e) {
            log.error("doExportAsEasyExcel Exception", e);
            throw new BaseException(e.getMessage());
        }
    }
}
