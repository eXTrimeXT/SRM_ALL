package com.midea.cloud.srm.feign;

import com.midea.cloud.srm.cm.contract.model.dto.MilestoneHasCreatePefDto;
import com.midea.cloud.srm.feign.workflow.FlowBusinessCallbackClient;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.Serializable;
import java.util.List;

/**
 * <pre>
 *
 * </pre>
 *
 * @author luxc18
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2024/01/26 11:36:28
 *  修改内容:
 * </pre>
 */
@FeignClient(value = "cloud-biz-contract", contextId = "contractPerfClient", path = "/api-cm")
public interface ContractExtClient extends FlowBusinessCallbackClient {

    /**
     * 写入是否已经创建项目
     * @param milestoneHasCreatePefDto
     * @return
     */
    @ApiOperation("写入是否已经创建项目")
    @PostMapping("/perPlan/ext/setHasCreatePerf")
    public List<Serializable> setHasCreatePerf(@RequestBody MilestoneHasCreatePefDto milestoneHasCreatePefDto);

    }
