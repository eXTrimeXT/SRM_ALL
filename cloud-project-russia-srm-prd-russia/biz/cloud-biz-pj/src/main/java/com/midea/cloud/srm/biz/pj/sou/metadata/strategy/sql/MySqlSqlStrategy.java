package com.midea.cloud.srm.biz.pj.sou.metadata.strategy.sql;

import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.srm.biz.pj.sou.metadata.context.MetadataDdlParamContext;
import com.midea.cloud.srm.biz.pj.sou.metadata.enums.MySqlKeyword;
import com.midea.cloud.srm.biz.pj.sou.metadata.result.MetadataPermissionHandler;
import com.midea.cloud.srm.biz.pj.sou.metadata.result.MetadataResultSetHandler;
import com.midea.cloud.srm.biz.pj.sou.metadata.utils.CompareUtil;
import com.midea.cloud.srm.biz.pj.sou.metadata.utils.EntityUtil;
import com.midea.cloud.srm.model.base.metadata.entity.Metadata;
import com.midea.cloud.srm.model.base.metadata.entity.MetadataDetail;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <pre>
 * MySQL表结构脚本生成策略
 * </pre>
 *
 * @author huangyq154@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/7/6 11:04
 *  修改内容:
 * </pre>
 */
public class MySqlSqlStrategy implements MetadataSqlStrategy {
    private static List<String> MYSQL_KEYWORDS = MySqlKeyword.getList();

    @Override
    public String getTableCountSql() {
        return "select count(table_name) from information_schema.tables where table_name = ? and table_schema = ?";
    }

    @Override
    public String getCreateTableSql(Metadata extend) {
        StringBuilder sql = new StringBuilder("create table " + extend.getTableName() + " ");
        sql.append(" ( ");
        StringBuilder cols = new StringBuilder();
        for (int i = 0; i < extend.getDetails().size(); i++) {
            MetadataDetail detail = extend.getDetails().get(i);
            StringBuilder col = getColSql(detail);
            cols.append(",").append(col);
        }
        sql.append(cols.substring(1));
        /*主键标识 */
        if (null != extend.getPrimaryKeyDetail()) {
            sql.append(" ,primary key(").append(extend.getPrimaryKeyDetail().getFieldName()).append(") using btree ");
        }
        sql.append(" ) ");
        /*表备注 */
        if (StringUtils.isNotEmpty(extend.getEntityDesc())) {
            MetadataDdlParamContext.add(extend.getEntityDesc());
            sql.append(" comment = ? ");
        }
        return sql.toString();
    }

    @Override
    public String getRenameTableSql(String oldTableName, String newTableName) {
        return "rename table " + oldTableName + " to " + newTableName;
    }

    @Override
    public String getAlterTableSql(Metadata existMetadata, Metadata extend, List<String> defaultFieldNames) {
        StringBuilder cols = new StringBuilder();
        List<MetadataDetail> existDetails = existMetadata.getDetails();
        /*按ID生成的map，用于列修改判断 */
        Map<Long, MetadataDetail> detailsById = extend.getDetails().stream()
                .collect(Collectors.toMap(MetadataDetail::getMetadataDetailId, d -> d, (k1, k2) -> k2));
        /*按顺序生成的map，用于列顺序修改判断 */
        Map<Integer, String> fieldNamesByOrder = extend.getDetails().stream()
                .collect(Collectors.toMap(MetadataDetail::getFieldOrder, MetadataDetail::getFieldName, (k1, k2) -> k2));
        for (int i = 0; i < existDetails.size(); i++) {
            MetadataDetail existDetail = existDetails.get(i);
            if (defaultFieldNames.contains(existDetail.getFieldName())) {
                detailsById.remove(existDetail.getMetadataDetailId());
                /*忽略默认列 */
                continue;
            }
            MetadataDetail detail = detailsById.get(existDetail.getMetadataDetailId());
            if (null != detail) {
                boolean hasModifyField = hasModifyField(existDetail, detail);
                boolean hasModifyOrder = hasModifyOrder(existDetail, detail);
                /*若已保存列和传入列都存在，代表列编辑 */
                if (hasModifyField || hasModifyOrder) {
                    StringBuilder modifySql = new StringBuilder();
                    StringBuilder col = getColSql(detail);
                    if (hasModifyFieldName(existDetail, detail)) {
                        modifySql.append(" change ").append(existDetail.getFieldName()).append(" ").append(col);
                    } else {
                        modifySql.append(" modify column ").append(col);
                    }
                    /*更新列顺序 */
                    if (hasModifyOrder && i > 0) {
                        String lastFieldName = fieldNamesByOrder.get(i - 1);
                        if (null != lastFieldName) {
                            modifySql.append(" after ").append(lastFieldName);
                        }
                    }
                    cols.append(" ,").append(modifySql);
                }
                detailsById.remove(existDetail.getMetadataDetailId());
            } else {
                /*若已保存列存在而传入列不存在，代表列删除 */
                StringBuilder dropSql = new StringBuilder(" drop column " + existDetail.getFieldName());
                cols.append(" ,").append(dropSql);
            }
        }
        if (!detailsById.isEmpty()) {
            for (MetadataDetail detail : detailsById.values()) {
                /*若已保存列不存在而传入列存在，代表列新增 */
                StringBuilder col = getColSql(detail);
                StringBuilder addSql = new StringBuilder(" add ");
                addSql.append(col);
                /*指定插入列位置 */
                String lastFieldName = fieldNamesByOrder.get(detail.getFieldOrder() - 1);
                if (null != lastFieldName) {
                    addSql.append(" after ").append(lastFieldName);
                }
                cols.append(" ,").append(addSql);
            }
        }
        if (cols.length() > 0) {
            StringBuilder sql = new StringBuilder("alter table " + extend.getTableName());
            sql.append(cols.substring(2));
            return sql.toString();
        }
        return null;
    }

    @Override
    public String getDataCountSql(String tableName) {
        return "select count(1) from " + tableName;
    }

    @Override
    public String getDropCountSql(String tableName) {
        return "drop table " + tableName;
    }

    @Override
    public String getStructSql(String tableName) {
        return "desc " + tableName;
    }

    @Override
    public String getUpdateFieldOrderSql(Metadata existMetadata, Metadata extend, List<String> defaultFieldNames) {
        /*mysql策略在更新表结构中处理 */
        return null;
    }

    @Override
    public String getPermissionSql() {
        return "show grants";
    }

    @Override
    public MetadataResultSetHandler getStructResultHandler() {
        return rs -> {
            MetadataDetail detail = new MetadataDetail();
            String fieldName = rs.getString("Field");
            String fieldType = rs.getString("Type");
            String nullable = rs.getString("Null");
            String keyType = rs.getString("Key");
            String defaultValue = rs.getString("Default");
            String text1 = "(";
            String text2 = ")";
            int num0 = 0;
            int num1 = 1;
            int num2 = 2;

            if (fieldType.contains(text1) && fieldType.contains(text2)) {
                String[] typeElms = fieldType.replace(")", "").split("\\(");
                detail.setDataType(typeElms[num0].toUpperCase());
                String[] lengthElms = typeElms[num1].split(",");
                if (lengthElms.length == num1) {
                    detail.setDataLength(Integer.valueOf(lengthElms[num0]));
                } else if (lengthElms.length == num2) {
                    detail.setDataLength(Integer.valueOf(lengthElms[0]));
                    detail.setDataPrecision(Integer.valueOf(lengthElms[1]));
                }
            } else {
                detail.setDataType(fieldType);
            }
            detail.setFieldName(fieldName);
            detail.setFieldAttr(EntityUtil.getAttrByName(fieldName));
            detail.setRequiredFlag("YES".equals(nullable) ? YesOrNo.YES.getValue() : null);
            detail.setPrimaryKeyFlag("PRI".equals(keyType) ? YesOrNo.YES.getValue() : null);
            detail.setDefaultValue(defaultValue);
            return detail;
        };
    }

    @Override
    public MetadataPermissionHandler getPermissionHandler() {
        return new MetadataPermissionHandler() {
            @Override
            public boolean handleCheck(String dbName, ResultSet rs) throws SQLException {
                String allText = "ALL PRIVILEGES";
                String createText = "CREATE";
                String alterText = "ALTER";
                String dropText = "DROP";
                String grantInfo = rs.getString(1).replaceAll("\\\\", "");
                if (grantInfo.contains(allText) || (grantInfo.contains(createText) && grantInfo.contains(alterText) && grantInfo.contains(dropText))) {
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    public List<String> getKeywordList() {
        return MYSQL_KEYWORDS;
    }

    private StringBuilder getColSql(MetadataDetail detail) {
        StringBuilder col = new StringBuilder();
        col.append(detail.getFieldName()).append(" ").append(detail.getDataType()).append(" ");
        /*数据长度+数据精度 */
        if (null != detail.getDataLength() && null != detail.getDataPrecision()) {
            col.append(" (").append(detail.getDataLength()).append(",").append(detail.getDataPrecision()).append(") ");
        } else if (null != detail.getDataLength() && null == detail.getDataPrecision()) {
            col.append(" (").append(detail.getDataLength()).append(") ");
        }
        /*必填标识 */
        if (YesOrNo.YES.getValue().equals(detail.getPrimaryKeyFlag()) || YesOrNo.YES.getValue().equals(detail.getRequiredFlag())) {
            col.append(" not null ");
        } else if (!YesOrNo.YES.getValue().equals(detail.getPrimaryKeyFlag()) && !YesOrNo.YES.getValue().equals(detail.getRequiredFlag())) {
            col.append(" null ");
        }
        /*默认值 */
        if (StringUtils.isNotEmpty(detail.getDefaultValue())) {
            MetadataDdlParamContext.add(detail.getDefaultValue());
            col.append(" default ? ");
        }
        /*列备注 */
        if (StringUtils.isNotEmpty(detail.getFieldDesc())) {
            MetadataDdlParamContext.add(detail.getFieldDesc());
            col.append(" comment ? ");
        }
        return col;
    }

    private boolean hasModifyFieldName(MetadataDetail existDetail, MetadataDetail detail) {
        return !CompareUtil.compareString(existDetail.getFieldName(), detail.getFieldName());
    }

    private boolean hasModifyOrder(MetadataDetail existDetail, MetadataDetail detail) {
        int eOrder = existDetail.getFieldOrder() != null ? existDetail.getFieldOrder() : -1;
        int nOrder = detail.getFieldOrder() != null ? detail.getFieldOrder() : -1;
//        判断较大顺序为准
        return !CompareUtil.compareInteger(eOrder, nOrder) && nOrder > eOrder;
    }

    private boolean hasModifyField(MetadataDetail existDetail, MetadataDetail detail) {
        boolean fieldNameChanged = hasModifyFieldName(existDetail, detail);
        boolean dataTypeChanged = !CompareUtil.compareString(existDetail.getDataType(), detail.getDataType());
        boolean dataLengthChanged = !CompareUtil.compareInteger(existDetail.getDataLength(), detail.getDataLength());
        boolean dataPrecisionChanged = !CompareUtil.compareInteger(existDetail.getDataPrecision(), detail.getDataPrecision());
        boolean primaryKeyFlagChanged = !CompareUtil.compareString(existDetail.getPrimaryKeyFlag(), detail.getPrimaryKeyFlag());
        boolean requiredFlagChanged = !CompareUtil.compareString(existDetail.getRequiredFlag(), detail.getRequiredFlag());
        boolean defaultValueChanged = !CompareUtil.compareString(existDetail.getDefaultValue(), detail.getDefaultValue());
        boolean fieldDescChanged = !CompareUtil.compareString(existDetail.getFieldDesc(), detail.getFieldDesc());
        return fieldNameChanged || dataTypeChanged || dataLengthChanged || dataPrecisionChanged || primaryKeyFlagChanged || requiredFlagChanged || defaultValueChanged || fieldDescChanged;
    }
}
