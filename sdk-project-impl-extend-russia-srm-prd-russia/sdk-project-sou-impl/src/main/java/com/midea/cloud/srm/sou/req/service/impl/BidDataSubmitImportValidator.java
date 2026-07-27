package com.midea.cloud.srm.sou.req.service.impl;

import com.meicloud.paas.ies.model.ImportResultModel;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.req.BidDataSubmitDetails;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.dto.ExtPrSouProjectPlanImportExcelDTO;
import com.midea.cloud.srm.sies.pojo.SiesData;
import com.midea.cloud.srm.sies.pojo.SiesImportParam;
import com.midea.cloud.srm.sies.pojo.SiesImportResult;
import com.midea.cloud.srm.sies.pojo.SiesMediator;
import com.midea.cloud.srm.sies.validator.AbstractImportValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zenghx2
 */
@Component
public class BidDataSubmitImportValidator extends AbstractImportValidator {

    @Autowired
    private QlService qlService;

    @Override
    public SiesImportResult doValidate(String iesTaskId, SiesImportParam param, SiesMediator curMediator, int sheetNo, String sheetName, Integer batchNo, List<SiesData> data) {
        SiesImportResult importResult = new SiesImportResult();
        int index=-1;
        for (SiesData d : data) {
            index++;
            BidDataSubmitDetails dto =
                    SouObjectXUtil.convertTargetObj(d, BidDataSubmitDetails.class);
            StringBuilder errSb = new StringBuilder(100);
            if(!"A".equals(dto.getCombination())&&
                    !"B".equals(dto.getCombination())&&
                    !"C".equals(dto.getCombination())&&
                    !"D".equals(dto.getCombination())&&
                    !"E".equals(dto.getCombination())&&
                    !"F".equals(dto.getCombination())&&
                    !"G".equals(dto.getCombination())&&
                    !"H".equals(dto.getCombination())&&
                    !"J".equals(dto.getCombination())&&
                    !"K".equals(dto.getCombination())
            ){
                errSb.append("组合填写错误");
            }
            if (errSb.length() > 0) {
                importResult.addErrorRow(new ImportResultModel.ErrorRow(index, errSb.toString()));
            } else {
                importResult.addSuccessRow(new SiesImportResult.SuccessRow(d, false));
            }
        }
        data.stream().map(row -> new SiesImportResult.SuccessRow(row, false)).forEach(importResult::addSuccessRow);
        return importResult;
    }

}
