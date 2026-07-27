package com.midea.cloud.srm.biz.pj.sou.metadata.model.dto;

import com.midea.cloud.srm.biz.pj.sou.metadata.enums.ConditionType;
import com.midea.cloud.srm.biz.pj.sou.metadata.enums.JoinType;
import lombok.Data;

import java.io.Serializable;
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
 *  修改日期: 2022/6/25 16:42
 *  修改内容:
 * </pre>
 */
@Data
public class MetadataQueryDetailDTO implements Serializable {

    private String fieldName;

    private ConditionType conditionType;

    private Object fieldValue;

    private JoinType joinType;

    private List<MetadataQueryDetailDTO> subConditions;

    public MetadataQueryDetailDTO() {

    }

    public MetadataQueryDetailDTO(String fieldName, ConditionType conditionType, Object fieldValue, JoinType joinType) {
        this.fieldName = fieldName;
        this.conditionType = conditionType;
        this.fieldValue = fieldValue;
        this.joinType = joinType;
    }

    public MetadataQueryDetailDTO(List<MetadataQueryDetailDTO> subConditions, JoinType joinType) {
        this.subConditions = subConditions;
        this.joinType = joinType;
    }
}
