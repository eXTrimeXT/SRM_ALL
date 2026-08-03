package com.midea.cloud.srm.biz.pj.sou.metadata.utils;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.biz.pj.sou.metadata.context.MetadataDataContext;
import com.midea.cloud.srm.biz.pj.sou.metadata.enums.ConditionType;
import com.midea.cloud.srm.biz.pj.sou.metadata.enums.JoinType;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.dto.MetadataQueryDTO;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.dto.MetadataQueryDetailDTO;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.entity.MetadataData;
import com.midea.cloud.srm.model.base.metadata.vo.MetadataVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
 *  修改日期: 2022/6/28 8:56
 *  修改内容:
 * </pre>
 */
public class WrapperUtil {
    public static QueryWrapper<MetadataData> buildQueryWrapper(MetadataQueryDTO queryDto) {
        MetadataVO config = MetadataDataContext.get();
        Map<String, String> mapping = config.getFieldAttrMapping().entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (k1, k2) -> k2));
        QueryWrapper<MetadataData> qw = new QueryWrapper<>();
        //查询条件
        addConditions(qw, queryDto.getConditions(), queryDto.getConvertAttr(), mapping, null);
        //排序条件
        for (MetadataQueryDetailDTO detail : queryDto.getOrders()) {
            if (StringUtils.isEmpty(detail.getFieldName())) {
                throw new BaseException("不允许空列名作为可扩展字段排序条件");
            }
            String fieldName = mapping.get(detail.getFieldName());
            if (StringUtils.isNotEmpty(fieldName)) {
                qw.orderByAsc(ConditionType.ASC.equals(detail.getConditionType()), fieldName);
                qw.orderByDesc(ConditionType.DESC.equals(detail.getConditionType()), fieldName);
            }
        }
        return qw;
    }

    public static UpdateWrapper<MetadataData> buildUpdateWrapper(List<MetadataQueryDetailDTO> conditions) {
        MetadataVO config = MetadataDataContext.get();
        if (CollectionUtils.isEmpty(conditions)) {
            throw new BaseException("可扩展字段查询条件为空");
        }
        Map<String, String> mapping = config.getFieldAttrMapping().entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (k1, k2) -> k2));
        UpdateWrapper<MetadataData> qw = new UpdateWrapper();
        addConditions(qw, conditions, true, mapping, null);
        if (qw.isEmptyOfWhere()) {
            throw new BaseException("可扩展字段查询条件为空");
        }
        return qw;
    }


    public static void addConditions(AbstractWrapper qw, List<MetadataQueryDetailDTO> conditions, Boolean convertAttr, Map<String, String> mapping, JoinType joinType) {
        for (MetadataQueryDetailDTO detail : conditions) {
            List<MetadataQueryDetailDTO> subConditions = detail.getSubConditions();
            JoinType curJoinType = null != joinType ? joinType : detail.getJoinType();
            if (CollectionUtils.isNotEmpty(subConditions)) {
                if (JoinType.OR.equals(curJoinType)) {
                    qw.or(q -> addConditions((AbstractWrapper) q, subConditions, convertAttr, mapping, detail.getJoinType()));
                } else {
                    qw.and(q -> addConditions((AbstractWrapper) q, subConditions, convertAttr, mapping, detail.getJoinType()));
                }
            } else {
                if (StringUtils.isEmpty(detail.getFieldName())) {
                    throw new BaseException("不允许空列名作为可扩展字段查询条件");
                }
                String fieldName = BooleanUtils.isFalse(convertAttr) ? detail.getFieldName() : mapping.get(detail.getFieldName());
                if (StringUtils.isEmpty(fieldName)) {
                    throw new BaseException("没有找到字段" + detail.getFieldName() + "映射");
                }
                if (JoinType.OR.equals(curJoinType)) {
                    qw.or();
                }
                addCondition(qw, detail.getConditionType(), fieldName, detail.getFieldValue());
            }
        }
    }

    private static void addCondition(AbstractWrapper qw, ConditionType conditionType, String fieldName, Object fieldValue) {
        qw.eq(ConditionType.EQ.equals(conditionType), fieldName, fieldValue);
        qw.like(ConditionType.LIKE.equals(conditionType), fieldName, fieldValue);
        qw.ne(ConditionType.NE.equals(conditionType), fieldName, fieldValue);
        qw.gt(ConditionType.GT.equals(conditionType), fieldName, fieldValue);
        qw.ge(ConditionType.GE.equals(conditionType), fieldName, fieldValue);
        qw.lt(ConditionType.LT.equals(conditionType), fieldName, fieldValue);
        qw.le(ConditionType.LE.equals(conditionType), fieldName, fieldValue);
        qw.isNull(ConditionType.IS_NULL.equals(conditionType), fieldName);
        qw.isNotNull(ConditionType.IS_NOT_NULL.equals(conditionType), fieldName);

        if (ConditionType.IN.equals(conditionType) || ConditionType.NOT_IN.equals(conditionType) || ConditionType.BETWEEN.equals(conditionType)) {
            Object[] arrayValue = getArrayValue(fieldValue);
            qw.in(ConditionType.IN.equals(conditionType) && !ArrayUtils.isEmpty(arrayValue), fieldName, arrayValue);
            qw.notIn(ConditionType.NOT_IN.equals(conditionType) && !ArrayUtils.isEmpty(arrayValue), fieldName, arrayValue);
            int length = 2;
            if (ConditionType.BETWEEN.equals(conditionType) && arrayValue.length == length) {
                qw.between(ConditionType.BETWEEN.equals(conditionType), fieldName, arrayValue[0], arrayValue[1]);
            }
        }
    }

    private static Object[] getArrayValue(Object fieldValue) {
        if (null != fieldValue) {
            if (fieldValue instanceof List) {
                return ((List) fieldValue).toArray(new Object[0]);
            } else if (fieldValue instanceof Array) {
                return (Object[]) fieldValue;
            }
        }
        return new Object[0];
    }


}
