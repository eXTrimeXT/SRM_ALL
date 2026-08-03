package com.midea.cloud.srm.biz.pj.sou.metadata.model;

import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.ExtendHandlerType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * <pre>
 * 扩展实体查询信息
 * </pre>
 *
 * @author huangyq154@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/7/22 11:16
 *  修改内容:
 * </pre>
 */
@Setter
@Getter
public class EntityExtendDTO implements Serializable {
    private Class<? extends BaseEntity> entityClass;

    private String entityIdAttr;

    private Class entityIdType;

    private List<String> entityAttrs;

    private String tableName;

    private String extendTableName;

    private String extendIdAttr;

    private String extendReferenceAttr;

    private String extendAttr;

    private ExtendHandlerType extendHandlerType;
    /**
     * 关联主模型的字段
     */
    private String foreignKeyAttrs;
    @Override
    public String toString() {
        return super.toString();
    }
}
