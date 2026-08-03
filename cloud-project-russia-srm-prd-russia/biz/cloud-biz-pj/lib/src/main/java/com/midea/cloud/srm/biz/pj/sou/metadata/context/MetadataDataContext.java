package com.midea.cloud.srm.biz.pj.sou.metadata.context;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.component.context.container.SpringContextHolder;
import com.midea.cloud.srm.biz.pj.sou.metadata.cache.MetadataCache;
import com.midea.cloud.srm.model.base.metadata.vo.MetadataVO;
import org.apache.commons.collections4.CollectionUtils;

/**
 * <pre>
 * 可扩展实体数据查询上下文
 * </pre>
 *
 * @author huangyq154@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/6/25 11:37
 *  修改内容:
 * </pre>
 */
public class MetadataDataContext {
    private static ThreadLocal<MetadataVO> currentDataCache = new ThreadLocal<>();

    public static boolean isExecuting() {
        return currentDataCache.get() != null;
    }

    public static void load(String tableName) throws Exception {
        if (!isExecuting()) {
            MetadataCache metadataCache = SpringContextHolder.getApplicationContext().getBean(MetadataCache.class);
            MetadataVO config = metadataCache.getByTableName(tableName);
            if (null == config) {
                throw new BaseException("表" + tableName + "不存在");
            }
            if (CollectionUtils.isEmpty(config.getDetails())) {
                throw new BaseException("表" + config.getTableName() + "没有配置字段");
            }
            currentDataCache.set(config);
        }
    }

    public static MetadataVO get() {
        MetadataVO config = currentDataCache.get();
        if (null == config) {
            throw new BaseException("当前请求并未加载可扩展字段设置缓存");
        }
        return config;
    }

    public static void destroy() {
        if (isExecuting()) {
            currentDataCache.remove();
        }
    }

    /**
     * 是否为有主键的实体
     *
     * @return
     */
    public static boolean isPkEntity() {
        if (isExecuting()) {
            MetadataVO config = currentDataCache.get();
            return null != config.getPrimaryKeyDetail();
        }
        return false;
    }
}
