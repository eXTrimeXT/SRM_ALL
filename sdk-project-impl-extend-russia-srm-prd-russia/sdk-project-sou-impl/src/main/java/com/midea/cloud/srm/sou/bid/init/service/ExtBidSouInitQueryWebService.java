package com.midea.cloud.srm.sou.bid.init.service;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouMarginQueryDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouPriceTemplateDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouMarginDto;

import javax.servlet.http.HttpServletResponse;
/**
 * 备注
 * @author huangbf3
 */
public interface ExtBidSouInitQueryWebService {

    /**
     * 查询报价模板
     * @param projectId
     * @return
     */
    ApiExtSouPriceTemplateDto listPriceTemplate(Long projectId);

    /**
     * 导出博报价模板
     * @param projectId 参数
     * @param response 参数
     * @throws Exception
     */
    void exportPriceExcelTemplate(Long projectId, HttpServletResponse response) throws Exception;

    /**
     * 年度保证金查询
     * @param param 参数
     * @return 返回
     */
    PageInfo<ExtSouMarginDto> listYearlyMargin(ApiExtSouMarginQueryDto param);

    /**
     * 附件压缩包下载
     * @param response 参数
     * @throws Exception
     */
    void testZip(HttpServletResponse response) throws Exception;

}
