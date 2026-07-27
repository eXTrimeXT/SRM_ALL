package com.midea.cloud.srm.biz.pj.sou.metadata.model.vo;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.biz.pj.sou.metadata.constants.MetadataKey;
import com.mideacloud.common.objectx.ExtensionMap;

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
 *  修改日期: 2022/6/24 13:33
 *  修改内容:
 * </pre>
 */
public class MetadataDataVO extends JSONObject {

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

    public ExtensionMap buildExtensionMap() {
        ExtensionMap extensions = new ExtensionMap();
        extensions.putAll(this);
        extensions.remove(MetadataKey.KEY_TABLE_NAME);
        extensions.remove(MetadataKey.KEY_ID_FIELD);
        return extensions;
    }
}
