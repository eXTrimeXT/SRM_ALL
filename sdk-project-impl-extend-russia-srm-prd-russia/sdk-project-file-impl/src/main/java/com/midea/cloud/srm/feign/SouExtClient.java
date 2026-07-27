package com.midea.cloud.srm.feign;

import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouTechScoreLineQueryDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 备注
 * @author huangbf3
 */
@FeignClient(value = "cloud-biz-sou", contextId = "SouExtClient", path = "/api-sou")
public interface SouExtClient {

    /**
     * 归档评分文件数据查询
     *
     * @param query
     */
    @PostMapping("/ext/buyer/bid/init/exportScoreExcelForArchivist")
    @ApiOperation("归档评分文件数据查询")
    public List<Map<String, Object>> exportScoreExcelForArchivist(@RequestBody ApiExtSouTechScoreLineQueryDTO query);

    /**
     * 下载商务附件
     * @param projectId
     * @return
     */
    @GetMapping("/ext/buyer/bid/init/listDownloadBusinessFile")
    @ApiOperation("下载商务附件")
    Map<String, Object> listDownloadBusinessFile(@RequestParam(value = "projectId") Long projectId);

    /**
     * 下载技术附件
     * @param projectId
     * @return
     */
    @GetMapping("/ext/buyer/bid/init/listDownloadTechPlanFile")
    @ApiOperation("下载技术附件")
    Map<String, Object> listDownloadTechPlanFile(@RequestParam(value = "projectId") Long projectId);
}
