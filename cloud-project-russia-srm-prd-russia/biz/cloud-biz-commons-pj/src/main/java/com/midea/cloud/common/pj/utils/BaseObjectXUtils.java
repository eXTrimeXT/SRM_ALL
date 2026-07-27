package com.midea.cloud.common.pj.utils;

import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.srm.model.objectx.dto.ConditionDTO;
import com.midea.cloud.srm.model.objectx.dto.ObjectXDTO;
import com.mideacloud.common.objectx.ObjectX;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <pre>
 *  描述
 * </pre>
 *
 * @author Sam
 * @version 1.0
 * @date 2022/11/16 11:40
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class BaseObjectXUtils {
    private BaseObjectXUtils() {
    }

    public static void setCondition(ObjectXDTO objectXdto, String field, String operator, Object value) {
        List<ConditionDTO> extendConditions = objectXdto.getExtendConditions();
        if (extendConditions == null) {
            extendConditions = new ArrayList<>();
        }
        ConditionDTO conditionDTO = new ConditionDTO();
        conditionDTO.setField(field);
        conditionDTO.setOperator(operator);
        conditionDTO.setValue(value);
        extendConditions.add(conditionDTO);
        objectXdto.setExtendConditions(extendConditions);
    }

    public static <T extends ObjectX> String getStringX(T objectX, String field) {
        return BaseObjectXUtils.getX(objectX, field, String.class);
    }

    public static <T extends ObjectX> Long getLongX(T objectX, String field) {
        return BaseObjectXUtils.getX(objectX, field, Long.class);
    }

    public static <T extends ObjectX> BigDecimal getBigDecimalX(T objectX, String field) {
        return BaseObjectXUtils.getX(objectX, field, BigDecimal.class);
    }

    public static <T extends ObjectX, B> B getX(T objectX, String field, Class<B> clazz) {
        Class<? extends ObjectX> aClass = objectX.getClass();
        try {
            Object returnValue = objectX.getX(field);
            if (returnValue == null) {
                return null;
            }
            if (clazz.isAssignableFrom(BigDecimal.class)) {
                B bObject = (B) new BigDecimal(returnValue.toString());
                return bObject;
            } else if (clazz.isAssignableFrom(Long.class)) {
                B bObject = (B) Long.valueOf(returnValue.toString());
                return bObject;
            } else if (clazz.isAssignableFrom(Integer.class)) {
                B bObject = (B) Integer.valueOf(returnValue.toString());
                return bObject;
            } else if (clazz.isAssignableFrom(Date.class)) {
                B bObject = (B) DateUtil.parseDate(returnValue.toString());
                return bObject;
            } else if (clazz.isAssignableFrom(String.class)) {
                return (B) returnValue.toString();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
