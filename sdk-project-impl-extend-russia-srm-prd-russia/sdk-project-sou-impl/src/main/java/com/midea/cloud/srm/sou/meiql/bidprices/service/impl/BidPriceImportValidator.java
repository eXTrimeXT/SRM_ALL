package com.midea.cloud.srm.sou.meiql.bidprices.service.impl;

import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.srm.sies.pojo.SiesData;
import com.midea.cloud.srm.sies.pojo.SiesImportParam;
import com.midea.cloud.srm.sies.pojo.SiesImportResult;
import com.midea.cloud.srm.sies.pojo.SiesMediator;
import com.midea.cloud.srm.sies.validator.AbstractImportValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author
 */
@Component
public class BidPriceImportValidator extends AbstractImportValidator {

    @Autowired
    private QlService qlService;

    @Override
    public SiesImportResult doValidate(String iesTaskId, SiesImportParam param, SiesMediator curMediator, int sheetNo, String sheetName, Integer batchNo, List<SiesData> data) {
        SiesImportResult importResult = new SiesImportResult();
        data.stream().map(row -> new SiesImportResult.SuccessRow(row, false)).forEach(importResult::addSuccessRow);
        return importResult;
    }

}
