package com.midea.cloud.srm.biz.pj.sou.metadata.strategy.sql;

import com.midea.cloud.srm.biz.pj.sou.metadata.result.MetadataPermissionHandler;
import com.midea.cloud.srm.biz.pj.sou.metadata.result.MetadataResultSetHandler;
import com.midea.cloud.srm.model.base.metadata.entity.Metadata;

import java.util.List;

/**
 * <pre>
 * SQL生成策略
 * </pre>
 *
 * @author huangyq154@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/7/6 11:01
 *  修改内容:
 * </pre>
 */
public interface MetadataSqlStrategy {
    /**
     * 备注
     * @return
     */
    String getTableCountSql();

    /**
     * 备注
     * @param extend
     * @return
     */
    String getCreateTableSql(Metadata extend);

    /**
     * 备注
     * @param oldTableName
     * @param newTableName
     * @return
     */
    String getRenameTableSql(String oldTableName, String newTableName);

    /**
     * 备注
     * @param existExtend
     * @param extend
     * @param defaultFieldNames
     * @return
     */
    String getAlterTableSql(Metadata existExtend, Metadata extend, List<String> defaultFieldNames);

    /**
     * 备注
     * @param tableName
     * @return
     */
    String getDataCountSql(String tableName);

    /**
     * 备注
     * @param tableName
     * @return
     */
    String getDropCountSql(String tableName);

    /**
     * 备注
     * @param tableName
     * @return
     */
    String getStructSql(String tableName);

    /**
     * 备注
     * @param existExtend
     * @param extend
     * @param defaultFieldNames
     * @return
     */
    String getUpdateFieldOrderSql(Metadata existExtend, Metadata extend, List<String> defaultFieldNames);

    /**
     * 备注
     * @return
     */
    String getPermissionSql();

    /**
     * 备注
     * @return
     */
    MetadataResultSetHandler getStructResultHandler();

    /**
     * 备注
     * @return
     */
    MetadataPermissionHandler getPermissionHandler();

    /**
     * 备注
     * @return
     */
    List<String> getKeywordList();
}
