package com.midea.cloud.srm.feign;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.feign.workflow.FlowBusinessCallbackClient;
import com.midea.cloud.srm.model.perf.ordercheck.dto.PjPerfTemplateHeaderQueryDTO;
import com.midea.cloud.srm.model.perf.projectscoreitem.entity.ProjectScoreItems;
import com.midea.cloud.srm.model.perf.projectscoreman.entity.ProjectScoreMan;
import com.midea.cloud.srm.model.perf.template.entity.PerfTemplateHeader;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 100014336 ganyh19
 */
@FeignClient(
        value = "${cloud.scc.feign-name-mapping.cloud-biz-performance:cloud-biz-performance}",
        path = "${cloud.scc.feign-name-mapping.cloud-biz-performance-path:/api-pef}",
        contextId = "cloud-biz-performance-ext"
)
public interface ContractPerformanceExtClient extends FlowBusinessCallbackClient {

    /**
     * Description 分页查询绩效模型头表信息
     *
     * @return
     * @throws
     * @param  queryDTO 绩效模型查询dto
     * @Date 2020.05.28
     **/
    @PostMapping("/pj/template/listPefTemplateHeaderPage")
    PageInfo<PerfTemplateHeader> listPefTemplateHeaderPage(@RequestBody PjPerfTemplateHeaderQueryDTO queryDTO);

    /**
     * updateProjectScoreHeader
     * @param contractNo
     */
    @PostMapping("/projectScoreHeader/update")
    void updateProjectScoreHeader(@RequestBody String contractNo);

    /**
     * updateProjectScoreItems
     * @param contractNo
     */
    @PostMapping("/pj/projectScoreItems/update")
    void updateProjectScoreItems(@RequestBody String contractNo);

    /**
     * updateProjectScoreMan
     * @param contractNo
     */
    @PostMapping("/projectScoreMan/update")
    void updateProjectScoreMan(@RequestBody String contractNo);
}
