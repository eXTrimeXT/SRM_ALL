package com.midea.cloud.srm.biz.pj.sou.metadata.model.dto;

import com.midea.cloud.srm.biz.pj.sou.metadata.constants.MetadataKey;
import lombok.Data;

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
 *  修改日期: 2022/6/25 16:26
 *  修改内容:
 * </pre>
 */
@Data
public class MetadataQueryDTO extends MetadataDataDTO {

    public Boolean getConvertAttr() {
        if (containsKey(MetadataKey.KEY_CONVERT_ATTR)) {
            return getBoolean(MetadataKey.KEY_CONVERT_ATTR);
        }
        return null;
    }

    public void setConvertAttr(Boolean convertAttr) {
        put(MetadataKey.KEY_CONVERT_ATTR, convertAttr);
    }

    public Integer getPageNum() {
        if (containsKey(MetadataKey.KEY_PAGE_NUM)) {
            return getInteger(MetadataKey.KEY_PAGE_NUM);
        }
        return null;
    }

    public void setPageNum(Integer pageNum) {
        put(MetadataKey.KEY_PAGE_NUM, pageNum);
    }

    public Integer getPageSize() {
        if (containsKey(MetadataKey.KEY_PAGE_SIZE)) {
            return getInteger(MetadataKey.KEY_PAGE_SIZE);
        }
        return null;
    }

    public void setPageSize(Integer pageSize) {
        put(MetadataKey.KEY_PAGE_SIZE, pageSize);
    }

    public String getFixSelect() {
        if (containsKey(MetadataKey.KEY_FIX_SELECT)) {
            return getString(MetadataKey.KEY_FIX_SELECT);
        }
        return null;
    }

    public void setFixSelect(String fixSelect) {
        put(MetadataKey.KEY_FIX_SELECT, fixSelect);
    }
}
