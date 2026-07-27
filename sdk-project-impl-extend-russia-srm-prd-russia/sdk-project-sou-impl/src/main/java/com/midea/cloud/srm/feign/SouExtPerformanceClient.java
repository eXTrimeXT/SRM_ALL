package com.midea.cloud.srm.feign;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.model.perf.projectscore.entity.ProjectScoreHeader;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 100014336 ganyh19 bs
 */
@FeignClient(
        value = "${cloud.scc.feign-name-mapping.cloud-biz-performance:cloud-biz-performance}",
        path = "${cloud.scc.feign-name-mapping.cloud-biz-performance-path:/api-pef}",
        contextId = "SouExtPerformanceClient"
)
public interface SouExtPerformanceClient {

    /**
     * 获取绩效化评分
     * @param queryDTO queryDTO
     * @return PageInfo<ProjectScoreHeader>
     */
    @PostMapping("/projectScoreHeader/listPage")
    PageInfo<ProjectScoreHeader> listPage(@RequestBody ProjectScoreHeader queryDTO);

}
