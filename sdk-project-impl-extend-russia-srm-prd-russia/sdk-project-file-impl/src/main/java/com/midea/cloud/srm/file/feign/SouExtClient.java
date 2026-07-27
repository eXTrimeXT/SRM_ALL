package com.midea.cloud.srm.file.feign;

import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouTechScoreLineQueryDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
}
