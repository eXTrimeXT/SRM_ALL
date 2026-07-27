package com.midea.cloud.srm.sou.report.service;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;
import java.util.Map;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Api("报表接口定义")
public interface ExtReportService<T> {

    /**
     * 报表分页查询
     * @param query
     * @return
     */
    @ApiOperation("报表分页查询")
    public PageInfo<T> listPage(Map<String, Object> query);

    /**
     * 报表填充数据
     * @param dataList
     */
    @ApiOperation("报表填充数据")
    public void fillReportData(List<T> dataList);
}
