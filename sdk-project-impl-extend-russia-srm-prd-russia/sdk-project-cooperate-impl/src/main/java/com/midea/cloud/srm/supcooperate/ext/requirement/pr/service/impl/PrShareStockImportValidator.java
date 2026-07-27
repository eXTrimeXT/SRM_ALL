package com.midea.cloud.srm.supcooperate.ext.requirement.pr.service.impl;

import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.srm.sies.pojo.SiesData;
import com.midea.cloud.srm.sies.pojo.SiesImportParam;
import com.midea.cloud.srm.sies.pojo.SiesImportResult;
import com.midea.cloud.srm.sies.pojo.SiesMediator;
import com.midea.cloud.srm.sies.validator.AbstractImportValidator;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zenghx2
 */
@Component
public class PrShareStockImportValidator extends AbstractImportValidator {

    @Autowired
    private QlService qlService;

    @Override
    public SiesImportResult doValidate(String iesTaskId, SiesImportParam param, SiesMediator curMediator, int sheetNo, String sheetName, Integer batchNo, List<SiesData> data) {
        List<Record> records = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            SiesData row = data.get(i);
            Record record = new Record();
            row.forEach((k, v) -> {
                record.put(k, v);
            });
            records.add(record);
        }

        if (CollectionUtils.isNotEmpty(records)) {
            qlService.create("PrShareStock", records);
        }

        return new SiesImportResult();
    }

}
