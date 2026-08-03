package com.midea.cloud.srm.biz.pj.sou.metadata.utils;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.DbType;
import com.midea.cloud.component.context.container.SpringContextHolder;
import com.midea.cloud.srm.biz.pj.sou.metadata.config.DynamicProperties;
import com.midea.cloud.srm.model.common.enums.dynamic.Module;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Iterator;
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
 *  修改日期: 2022/7/1 9:32
 *  修改内容:
 * </pre>
 */
public class DatabaseUtil {

    public static DbType getDbType() throws SQLException {
        JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
        DatabaseMetaData metadata = jdbcTemplate.getDataSource().getConnection().getMetaData();
        String dbTypeName = metadata.getDatabaseProductName().toUpperCase();
        String mysql = "MYSQL";
        if (dbTypeName.indexOf(mysql) == 0) {
            return DbType.MYSQL;
        }
 /*       else if (dbTypeName.startsWith("ORACLE")) {
            return DbType.ORACLE;
        }*/
        return null;
    }

    public static String getCurrentDbName() throws SQLException {
        Connection connection = null;
        try {
            SqlSessionFactory sqlSessionFactory =
                    SpringContextHolder.getBean("sqlSessionFactory", SqlSessionFactory.class);
            DataSource dataSource = sqlSessionFactory.getConfiguration().getEnvironment().getDataSource();
            connection = dataSource.getConnection();
            return connection.getCatalog();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    public static Module getModuleByDbName(String dbName){
        DynamicProperties dynamicProperties =
                SpringContextHolder.getBean("dynamicProperties", DynamicProperties.class);
        Map<String, JSONObject>  datasources = dynamicProperties.getDatasouces();
        if (null != datasources) {
            Iterator<Map.Entry<String, JSONObject>> itDatasources = datasources.entrySet().iterator();
            while (itDatasources.hasNext()) {
                Map.Entry<String, JSONObject> datasource = itDatasources.next();
                String url = datasource.getValue().getString("url");
                if (url.contains("/" + dbName + "?")) {
                    return Module.get(datasource.getKey());
                }
            }
        }
        return null;
    }
}
