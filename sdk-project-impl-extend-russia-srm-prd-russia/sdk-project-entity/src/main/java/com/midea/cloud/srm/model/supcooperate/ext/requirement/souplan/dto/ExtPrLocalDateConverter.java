package com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.dto;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.mideacloud.common.util.DateUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @Author: panmq
 * @Date: 2024/05/08/ $
 * @Description:
 */
public class ExtPrLocalDateConverter implements Converter<LocalDate> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return LocalDate.class;
    }

    @Override
    public WriteCellData<String> convertToExcelData(WriteConverterContext<LocalDate> context) throws Exception {
        LocalDate localDate = context.getValue();
        if(Objects.isNull(localDate)) {
            return null;
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String value = localDate.format(df);
        return new WriteCellData<>(value);
    }
}
