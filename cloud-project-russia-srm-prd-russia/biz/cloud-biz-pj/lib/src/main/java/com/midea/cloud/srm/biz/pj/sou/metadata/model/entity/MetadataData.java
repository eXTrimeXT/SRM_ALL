package com.midea.cloud.srm.biz.pj.sou.metadata.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.biz.pj.sou.metadata.constants.MetadataKey;
import io.swagger.annotations.ApiModel;

import java.util.HashMap;

/**
 * <pre>
 *
 * </pre>
 *
 * @author huangyq154@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/6/24 12:05
 *  修改内容:
 * </pre>
 */
@TableName("${tableName}")
@ApiModel(description = "可扩展实体数据")
public class MetadataData extends HashMap {

    public MetadataData() {
    }

    public String getTableName() {
        if (containsKey(MetadataKey.KEY_TABLE_NAME)) {
            return get(MetadataKey.KEY_TABLE_NAME).toString();
        }
        return null;
    }

    public void setTableName(String tableName) {
        put(MetadataKey.KEY_TABLE_NAME, tableName);
    }

    public String getIdField() {
        if (containsKey(MetadataKey.KEY_ID_FIELD)) {
            return get(MetadataKey.KEY_ID_FIELD).toString();
        }
        return null;
    }

    public void setIdField(String idField) {
        put(MetadataKey.KEY_ID_FIELD, idField);
    }

    public Object getId() {
        if (containsKey(MetadataKey.KEY_ID_FIELD)) {
            return get(get(MetadataKey.KEY_ID_FIELD).toString());
        }
        return null;
    }

    public void setId(Object id) {
        if (containsKey(MetadataKey.KEY_ID_FIELD)) {
            put(get(MetadataKey.KEY_ID_FIELD).toString(), id);
        }
    }
}
