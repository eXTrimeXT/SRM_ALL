package com.midea.cloud.srm.biz.pj.utils;

import com.google.common.collect.Lists;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.meiql.api.component.paging.Page;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenQueryWrapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <pre>
 *  QlOpenClient 工具
 * </pre>
 *
 * @author luxc18@meiCloud.com
 * @version 1.00.00
 *
 * <pre>
 * 修改记录修改后版本:
 * 修改人:
 * 修改日期: 2023/8/10 13:53、
 * 修改内容:
 * </pre>
 */
@Component
public class PjQlOpenClientUtil {


    @Autowired
    private QlOpenClient qlOpenClient;

    /**
     * 条件查询数据，每次查询100条，然后汇总
     *
     * @param contextPath
     * @param wrapper
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> List<T> queryDataByPage(ContextPath contextPath, QlOpenQueryWrapper wrapper, Class<T> tClass) {
        return this.queryDataByPage(contextPath, wrapper, tClass, 100L);
    }

    /**
     * 条件查询数据，每次查询pageSize（ 0 < pageSize <= 1000）条，然后汇总
     * @param contextPath
     * @param wrapper
     * @param tClass
     * @param pageSize
     * @return
     * @param <T>
     */
    public <T> List<T> queryDataByPage(ContextPath contextPath, QlOpenQueryWrapper wrapper, Class<T> tClass, Long pageSize) {
        int maxSixe = 1000;
        if (pageSize == null || pageSize > maxSixe) {
            throw new BaseException("queryDataByPage - 参数不满足 0 < pageSize <= 1000");
        }
        Long pageNum = 1L;
        List<T> dataList = Lists.newArrayList();
        Page<T> pageData = qlOpenClient.query(contextPath, wrapper, pageNum, pageSize, tClass);
        long total = pageData.getTotal();
        if (total <= 0) {
            return dataList;
        } else if (total <= pageData.getRecords().size()) {
            dataList.addAll(pageData.getRecords());
            return dataList;
        } else {
            dataList.addAll(pageData.getRecords());
        }
        while (true) {
            pageNum++;
            Page<T> tempData = qlOpenClient.query(contextPath, wrapper, pageNum, pageSize, tClass);
            if (CollectionUtils.isNotEmpty(tempData.getRecords())) {
                dataList.addAll(tempData.getRecords());
            }
            if (dataList.size() >= tempData.getTotal()) {
                break;
            }
        }
        return dataList;
    }


    /**
     * 根据id获取单个记录*
     * @param contextPath
     * @param type
     * @param id
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> T queryOneById(ContextPath contextPath,String type,Long id, Class<T> tClass) {
        return qlOpenClient.read(contextPath, type, id, tClass);
    }

}
