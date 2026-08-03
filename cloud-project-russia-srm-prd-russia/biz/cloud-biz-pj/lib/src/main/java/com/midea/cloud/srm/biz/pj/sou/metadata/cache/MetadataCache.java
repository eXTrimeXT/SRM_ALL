package com.midea.cloud.srm.biz.pj.sou.metadata.cache;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.component.filter.HttpServletHolder;
import com.midea.cloud.srm.biz.pj.sou.metadata.utils.DatabaseUtil;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.metadata.vo.MetadataVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

/**
 * <pre>
 * 可扩展字段缓存
 * </pre>
 *
 * @author huangyq154@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/6/24 14:02
 *  修改内容:
 * </pre>
 */
@Component
@Slf4j
public class MetadataCache {
    private static final String METADATA_KEY = "scc.metadata.";

    /**
     * 主数据库名，初次调用生成
     */
    private static String CURRENT_DB_NAME = null;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 按表名获取执行时的缓存配置
     */
    public MetadataVO getByTableName(String tableName) throws SQLException {
        String token = getToken();
        String key = getCacheKey(getDbName(), tableName);
        MetadataVO config = redisUtil.get(key);
        if (null == config && null != token) {
            config = baseClient.getMetadataByTableName(getDbName(), tableName);
            if (null == config) {
                // 查询为空生成个dummy对象，防止每次都为空时都远程调用
                // 若新增实体，维护端会清除缓存
                config = new MetadataVO();
            }
            redisUtil.set(key, config, 7200);
        }
        if (null != config && (null == config.getMetadataId() || CollectionUtils.isEmpty(config.getDetails()))) {
            return null;
        }
        return config;
    }

    public boolean isMetadata(String tableName) throws SQLException {
        String key = getCacheKey(getDbName(), tableName);
        return redisUtil.get(key) != null;
    }

    /**
     * 清除指定表名的缓存
     */
    public void cleanByTableName(String dbName, String tableName) {
        if (null == dbName) {
            dbName = getDbName();
        }
        String key = getCacheKey(dbName, tableName);
        redisUtil.del(key);
    }

    private String getCacheKey(String dbName, String tableName) {
        return new StringBuilder(METADATA_KEY).append(dbName).append(".").append(tableName).toString();
    }

    private String getDbName() {
        if (null == CURRENT_DB_NAME) {
            try {
                CURRENT_DB_NAME = DatabaseUtil.getCurrentDbName();
            } catch (SQLException ex) {
                //暂时不抛出
            }
            if (null == CURRENT_DB_NAME) {
                throw new BaseException("当前服务并未指定数据源");
            }
        }
        return CURRENT_DB_NAME;
    }

    public String getToken() {
        HttpServletRequest request = HttpServletHolder.getRequest();
        return request.getHeader("Authorization");
    }
}
