package com.midea.cloud.srm.biz.pj.sou.metadata.model.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.midea.cloud.srm.biz.pj.sou.metadata.constants.MetadataKey;
import com.midea.cloud.srm.biz.pj.sou.metadata.context.MetadataDataContext;
import com.midea.cloud.srm.biz.pj.sou.metadata.enums.ConditionType;
import com.midea.cloud.srm.biz.pj.sou.metadata.enums.JoinType;
import com.midea.cloud.srm.model.base.metadata.vo.MetadataVO;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

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
public class MetadataDataDTO extends JSONObject {
    public Long getCurrentUserId() {
        if (containsKey(MetadataKey.KEY_CURRENT_USER_ID)) {
            return getLong(MetadataKey.KEY_CURRENT_USER_ID);
        }
        return null;
    }

    public void setCurrentUserId(Long currentUserId) {
        put(MetadataKey.KEY_CURRENT_USER_ID, currentUserId);
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

    /**
     * 获取参数中id
     *
     * @return
     */
    public Object getId() {
        if (containsKey(MetadataKey.KEY_ID_FIELD)) {
            return get(get(MetadataKey.KEY_ID_FIELD).toString());
        }
        return null;
    }

    /**
     * 根据配置获取真实表中的id
     *
     * @return
     */
    public Object getActualId() {
        if (MetadataDataContext.isPkEntity()) {
            MetadataVO config = MetadataDataContext.get();
            String fieldAttr = config.getPrimaryKeyAttr();
            return get(fieldAttr);
        }
        return null;
    }

    public List<Object> getActualIds() {
        if (MetadataDataContext.isPkEntity()) {
            MetadataVO config = MetadataDataContext.get();
            String fieldAttr = config.getPrimaryKeyAttr() + "Batch";
            JSONArray ids = getJSONArray(fieldAttr);
            return JSONObject.parseArray(ids.toJSONString(), Object.class);
        }
        return null;
    }

    public List<MetadataDataDTO> getDetails() {
        JSONArray details = getJSONArray(MetadataKey.KEY_DETAILS);
        if (null != details) {
            return JSONObject.parseArray(details.toJSONString(), MetadataDataDTO.class);
        } else {
            return new ArrayList<>();
        }
    }

    public List<MetadataDataDTO> getIncludedNullDetails() {
        JSONArray details = getJSONArray(MetadataKey.KEY_DETAILS);
        if (null != details) {
            return JSONObject.parseArray(JSON.toJSONString(details, SerializerFeature.WriteMapNullValue), MetadataDataDTO.class);
        } else {
            return new ArrayList<>();
        }
    }

    public void setId(Object id) {
        put(get(MetadataKey.KEY_ID_FIELD).toString(), id);
    }

    public List<MetadataQueryDetailDTO> getConditions() {
        if (containsKey(MetadataKey.KEY_CONDITIONS)) {
            return getJSONArray(MetadataKey.KEY_CONDITIONS).toJavaList(MetadataQueryDetailDTO.class);
        }
        return new ArrayList<>();
    }

    public void setConditions(List<MetadataQueryDetailDTO> conditions) {
        put(MetadataKey.KEY_CONDITIONS, conditions);
    }

    public List<MetadataQueryDetailDTO> getOrders() {
        if (containsKey(MetadataKey.KEY_ORDERS)) {
            return getJSONArray(MetadataKey.KEY_ORDERS).toJavaList(MetadataQueryDetailDTO.class);
        }
        return new ArrayList<>();
    }

    public void setOrders(List<MetadataQueryDetailDTO> conditions) {
        put(MetadataKey.KEY_ORDERS, conditions);
    }

    public void addCondition(String fieldName, ConditionType conditionType, Object conditionValue, JoinType joinType) {
        List<MetadataQueryDetailDTO> conditions = getConditions();
        conditions.add(new MetadataQueryDetailDTO(fieldName, conditionType, conditionValue, joinType));
        setConditions(conditions);
    }

    public void addSubCondition(List<MetadataQueryDetailDTO> subConditions, JoinType joinType) {
        if (CollectionUtils.isNotEmpty(subConditions)) {
            List<MetadataQueryDetailDTO> conditions = getConditions();
            conditions.add(new MetadataQueryDetailDTO(subConditions, joinType));
            setConditions(conditions);
        }
    }

    public void addOrder(String fieldName, ConditionType orderType) {
        List<MetadataQueryDetailDTO> orders = getOrders();
        orders.add(new MetadataQueryDetailDTO(fieldName, orderType, null, null));
        setOrders(orders);
    }

    public void addDetail(MetadataDataDTO dataDto) {
        List<MetadataDataDTO> dataDtos = getDetails();
        dataDtos.add(dataDto);
        put(MetadataKey.KEY_DETAILS, dataDtos);
    }
}
