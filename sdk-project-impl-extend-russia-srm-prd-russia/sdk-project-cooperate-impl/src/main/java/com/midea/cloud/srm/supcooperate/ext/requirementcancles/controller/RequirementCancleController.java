package com.midea.cloud.srm.supcooperate.ext.requirementcancles.controller;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.supcooperate.ext.requirementcancles.service.ReuirementCancleCommonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@RestController
@Slf4j
@RequestMapping("/ext/requiremnetCancle")
@Api("申请单取消控制类")
public class RequirementCancleController extends BaseController {

    @Autowired
    private ReuirementCancleCommonService reuirementCancleCommonService;

    @ApiOperation("查询申请单未终止和未废弃的合同")
    @GetMapping("/queryReuiremnetAsWithContract")
    public List<RecordDTO> queryReuiremnetAsWithContract(@RequestParam("requirementHeadNum") String requirementHeadNum) {
        try {
            return reuirementCancleCommonService.queryReuiremnetAsWithContract(requirementHeadNum);
        } catch (Exception e) {
            log.error("", e);
            throw new BaseException(e.getMessage());
        }
    }

}
