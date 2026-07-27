package com.midea.cloud.srm.biz.pj.sou.metadata.strategy.sql;

import com.baomidou.mybatisplus.annotation.DbType;
import com.midea.cloud.dynamicds.bind.CheckModuleHolder;
import com.midea.cloud.srm.biz.pj.sou.metadata.context.MetadataDdlParamContext;
import com.midea.cloud.srm.biz.pj.sou.metadata.executor.MetadataSqlExecutor;
import com.midea.cloud.srm.biz.pj.sou.metadata.utils.DatabaseUtil;
import com.midea.cloud.srm.biz.pj.sou.metadata.utils.EntityUtil;
import com.midea.cloud.srm.model.base.metadata.entity.Metadata;
import com.midea.cloud.srm.model.base.metadata.entity.MetadataDetail;
import com.midea.cloud.srm.model.base.metadata.vo.MetadataDetailVO;
import com.midea.cloud.srm.model.common.enums.dynamic.Module;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <pre>
 * DDL执行器
 * 避免数据源切换失败，需开启@Transactional(propagation = Propagation.NOT_SUPPORTED)剔除事务
 * 利用方法异常通知主事务回滚，请在方法最后调用本类内容
 * </pre>
 *
 * @author huangyq154@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/7/6 11:05
 *  修改内容:
 * </pre>
 */
@ConditionalOnProperty(value = "spring.application.name", havingValue = "cloud-biz-base")
@Component
@Slf4j
public class MetadataSqlHandler {
    @Resource
    @Qualifier("sqlSessionFactory")
    private SqlSessionFactory sqlSessionFactory;

    private Map<String, MetadataSqlStrategy> strategies;

    public MetadataSqlHandler() {
        this.strategies = new HashMap<>(50);
        this.strategies.put(DbType.MYSQL.getDb(), new MySqlSqlStrategy());
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public long getTableCount(String tableName, Module module) throws Exception {
        try {
            CheckModuleHolder.checkout(module);
            MetadataDdlParamContext.init();
            MetadataSqlStrategy strategy = getStrategy();
            long result = 0;
            if (null != strategy) {
                MetadataDdlParamContext.add(tableName);
                String sql = strategy.getTableCountSql();
                result = MetadataSqlExecutor.getInstance().executeTableCount(sql);
            }
            return result;
        } finally {
            CheckModuleHolder.release();
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public int createTable(Metadata extend) throws Exception {
        try {
            CheckModuleHolder.checkout(extend.getModule());
            MetadataDdlParamContext.init();
            MetadataSqlStrategy strategy = getStrategy();
            int result = 0;
            if (null != strategy) {
                String sql = strategy.getCreateTableSql(extend);
                result = MetadataSqlExecutor.getInstance().executeDdl(sql);
            }
            return result;
        } finally {
            CheckModuleHolder.release();
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public int renameTable(String oldTableName, String newTableName, Module module) throws Exception {
        try {
            CheckModuleHolder.checkout(module);
            MetadataDdlParamContext.init();
            MetadataSqlStrategy strategy = getStrategy();
            int result = 0;
            if (null != strategy) {
                String sql = strategy.getRenameTableSql(oldTableName, newTableName);
                result = MetadataSqlExecutor.getInstance().executeDdl(sql);
            }
            return result;
        } finally {
            CheckModuleHolder.release();
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public int alterTable(Metadata existExtend, Metadata extend) throws Exception {
        try {
            CheckModuleHolder.checkout(extend.getModule());
            MetadataDdlParamContext.init();
            MetadataSqlStrategy strategy = getStrategy();
            int result = 0;
            if (null != strategy) {
                List<String> defaultFieldNames = EntityUtil.getDefaultDetailVos().stream().map(MetadataDetailVO::getFieldName).collect(Collectors.toList());
                String sql = strategy.getAlterTableSql(existExtend, extend, defaultFieldNames);
                if (null != sql) {
                    result = MetadataSqlExecutor.getInstance().executeDdl(sql);
                }
            }
            return result;
        } finally {
            CheckModuleHolder.release();
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public int updateFieldOrder(Metadata existExtend, Metadata extend) throws Exception {
        //考虑同时支持mysql和oracle的列顺序修改，暂时独立执行
        try {
            CheckModuleHolder.checkout(extend.getModule());
            MetadataDdlParamContext.init();
            MetadataSqlStrategy strategy = getStrategy();
            int result = 0;
            if (null != strategy) {
                List<String> defaultFieldNames = EntityUtil.getDefaultDetailVos().stream().map(MetadataDetailVO::getFieldName).collect(Collectors.toList());
                String sql = strategy.getUpdateFieldOrderSql(existExtend, extend, defaultFieldNames);
                if (null != sql) {
                    result = MetadataSqlExecutor.getInstance().executeDdl(sql);
                }
            }
            return result;
        } finally {
            CheckModuleHolder.release();
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public long getDataCount(String tableName, Module module) throws Exception {
        try {
            CheckModuleHolder.checkout(module);
            MetadataDdlParamContext.init();
            MetadataSqlStrategy strategy = getStrategy();
            long result = 0;
            if (null != strategy) {
                String sql = strategy.getDataCountSql(tableName);
                result = MetadataSqlExecutor.getInstance().executeSelectCount(sql);
            }
            return result;
        } finally {
            CheckModuleHolder.release();
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public int dropTable(String tableName, Module module) throws Exception {
        try {
            CheckModuleHolder.checkout(module);
            MetadataSqlStrategy strategy = getStrategy();
            int result = 0;
            if (null != strategy) {
                String sql = strategy.getDropCountSql(tableName);
                result = MetadataSqlExecutor.getInstance().executeDdl(sql);
            }
            return result;
        } finally {
            CheckModuleHolder.release();
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<MetadataDetail> getTableStruct(String tableName, Module module) throws Exception {
        try {
            CheckModuleHolder.checkout(module);
            MetadataSqlStrategy strategy = getStrategy();
            List<MetadataDetail> structDetails = new ArrayList<>();
            if (null != strategy) {
                String sql = strategy.getStructSql(tableName);
                try {
                    structDetails = MetadataSqlExecutor.getInstance().executeStruct(sql, strategy.getStructResultHandler());
                } catch (Exception ex) {
                    //执行报错认为查询不出表结构
                }
            }
            return structDetails;
        } finally {
            CheckModuleHolder.release();
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public boolean hasStructPermission(Module module) throws Exception {
        try {
            CheckModuleHolder.checkout(module);
            MetadataSqlStrategy strategy = getStrategy();
            boolean result = false;
            if (null != strategy) {
                String sql = strategy.getPermissionSql();
                result = MetadataSqlExecutor.getInstance().executePermissionCheck(sql, strategy.getPermissionHandler());
            }
            return result;
        } finally {
            CheckModuleHolder.release();
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<String> getKeywordList(Module module) throws Exception {
        try {
            CheckModuleHolder.checkout(module);
            MetadataSqlStrategy strategy = getStrategy();
            if (null != strategy) {
                List<String> keywords = strategy.getKeywordList();
                return keywords;
            }
            return null;
        } finally {
            CheckModuleHolder.release();
        }
    }

    private MetadataSqlStrategy getStrategy() throws SQLException {
        DbType dbType = DatabaseUtil.getDbType();
        if (null != dbType) {
            return strategies.get(dbType.getDb());
        }
        return null;
    }
}
