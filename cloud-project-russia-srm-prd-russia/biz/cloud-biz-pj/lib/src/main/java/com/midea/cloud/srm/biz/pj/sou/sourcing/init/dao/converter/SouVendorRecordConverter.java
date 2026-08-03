package com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.converter;

import com.alibaba.fastjson.JSON;
import com.midea.cloud.meiql.api.annotation.SchemaKey;
import com.midea.cloud.meiql.api.repository.exec.FieldConverter;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.schema.QlField;
import com.midea.cloud.meiql.api.spec.schema.QlFieldConverter;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author huangbf3
 */
@SchemaKey(key = "souVendorRecordConverter")
@Component
public class SouVendorRecordConverter implements FieldConverter {

    private static final String HIS_INFO = "vendorInfo";

    @Override
    public void fillInsert(String type, QlField field, QlFieldConverter converter, Record record) {
        this.beforeSaveDb(record);
    }

    @Override
    public void fillUpdate(String type, QlField field, QlFieldConverter converter, Record rec) {
        this.beforeSaveDb(rec);
    }

    @Override
    public Object fetchDb(String type, Object value, QlField field, QlFieldConverter converter) {
        if (HIS_INFO.equals(field.getName())) {
            if (value != null) {
                String v = StringUtils.trimToNull(value.toString());
                if (v != null) {
                    return JSON.parseObject(v, SouVendor.class);
                }
            }
        }
        return value;
    }

    private void beforeSaveDb(Record record) {
        Object v = record.get(HIS_INFO);
        if (v != null) {
            record.put(HIS_INFO, JSON.toJSONString(v));
        }
    }

}
