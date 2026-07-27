package com.midea.cloud.srm.biz.pj.sou.metadata.executor;

import com.midea.cloud.component.context.container.SpringContextHolder;
import com.midea.cloud.srm.biz.pj.sou.metadata.context.MetadataDdlParamContext;
import com.midea.cloud.srm.biz.pj.sou.metadata.result.MetadataPermissionHandler;
import com.midea.cloud.srm.biz.pj.sou.metadata.result.MetadataResultSetHandler;
import com.midea.cloud.srm.model.base.metadata.entity.MetadataDetail;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 *  修改日期: 2022/7/8 11:23
 *  修改内容:
 * </pre>
 */

public class MetadataSqlExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataSqlExecutor.class);
    private SqlSession sqlSession;
    private PreparedStatement stmt;

    private MetadataSqlExecutor() {
    }

    public static synchronized MetadataSqlExecutor getInstance() {
        MetadataSqlExecutor executor = new MetadataSqlExecutor();
        return executor;
    }

    public long executeTableCount(String sql) throws SQLException {
        long result = 0L;
        ResultSet rs = null;
        try {
            initStatement(sql);
            String dbName = stmt.getConnection().getCatalog();
            MetadataDdlParamContext.add(dbName);
            bindParams();
            rs = stmt.executeQuery();
            LOGGER.info("执行查询成功：{}", sql);
            if (rs.next()) {
                result = rs.getLong(1);
            }
        } finally {
            if (null != rs) {
                rs.close();
            }
            close();
        }
        return result;
    }

    public long executeSelectCount(String sql) throws SQLException {
        long result = 0L;
        ResultSet rs = null;
        try {
            initStatement(sql);
            bindParams();
            rs = stmt.executeQuery();
            LOGGER.info("执行查询成功：{}", sql);
            if (rs.next()) {
                result = rs.getLong(1);
            }
        } finally {
            if (null != rs) {
                rs.close();
            }
            close();
        }
        return result;
    }

    public int executeDdl(String sql) throws SQLException {
        int result = 0;
        try {
            initStatement(sql);
            bindParams();
            result = stmt.executeUpdate();
            LOGGER.info("执行查询成功：{}", sql);
        } finally {
            close();
        }
        return result;
    }

    public List<MetadataDetail> executeStruct(String sql, MetadataResultSetHandler resultSetHandler) throws SQLException {
        List<MetadataDetail> structDetails = new ArrayList<>();
        ResultSet rs = null;
        try {
            initStatement(sql);
            rs = stmt.executeQuery();
            LOGGER.info("执行查询成功：{}", sql);
            while (rs.next()) {
                structDetails.add(resultSetHandler.handleRow(rs));
            }

        } finally {
            if (null != rs) {
                rs.close();
            }
            close();
        }
        return structDetails;
    }

    public boolean executePermissionCheck(String sql, MetadataPermissionHandler permissionHandler) throws SQLException {
        Map<String, Boolean> results = new HashMap<>(50);
        ResultSet rs = null;
        try {
            initStatement(sql);
            String dbName = stmt.getConnection().getCatalog();
            rs = stmt.executeQuery();
            LOGGER.info("执行查询成功：{}", sql);
            while (rs.next()) {
                if (permissionHandler.handleCheck(dbName, rs)) {
                    return true;
                }
            }
        } finally {
            if (null != rs) {
                rs.close();
            }
            close();
        }
        return false;
    }

    private void initStatement(String sql) throws SQLException {
        SqlSessionFactory sqlSessionFactory =
                SpringContextHolder.getBean("sqlSessionFactory", SqlSessionFactory.class);
        sqlSession = SqlSessionUtils.getSqlSession(sqlSessionFactory);
        Connection connection = sqlSession.getConnection();
        stmt = connection.prepareStatement(sql);
    }

    private void bindParams() throws SQLException {
        List<Object> params = MetadataDdlParamContext.get();
        if (CollectionUtils.isNotEmpty(params)) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
        }
    }

    private void close() throws SQLException {
        if (null != stmt) {
            stmt.close();
        }
        if (null != sqlSession) {
            SqlSessionFactory sqlSessionFactory =
                    SpringContextHolder.getBean("sqlSessionFactory", SqlSessionFactory.class);
            SqlSessionUtils.closeSqlSession(sqlSession, sqlSessionFactory);
        }
    }
}
