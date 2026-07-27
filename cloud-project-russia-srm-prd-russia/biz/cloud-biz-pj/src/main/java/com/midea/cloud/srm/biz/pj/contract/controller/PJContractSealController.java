package com.midea.cloud.srm.biz.pj.contract.controller;

import cn.hutool.core.lang.Assert;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.biz.pj.contract.service.PJContractSealService;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.pj.contract.ContractSeal;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(value = "合同印章维护",tags = "合同印章维护")
@RestController
@Slf4j
@RequestMapping("/contract/seal")
public class PJContractSealController extends BaseController {

    @Resource
    private PJContractSealService contractSealService;


    @ApiOperation("根据ID获取详情")
    @GetMapping("/get")
    public ContractSeal get(Long id) {
        Assert.notNull(id, "id不能为空");
        return contractSealService.getById(id);
    }

    @ApiOperation("新增")
    @PostMapping("/add")
    public void add(@RequestBody ContractSeal contractSeal) {
        Long id = IdGenrator.generate();
        contractSeal.setContractSealId(id);
        contractSealService.save(contractSeal);
    }

    @ApiOperation("修改")
    @PostMapping("/update")
    public void modify(@RequestBody ContractSeal contractSeal) {
        contractSealService.updateById(contractSeal);
    }

    @ApiOperation("删除")
    @GetMapping("/delete")
    public void delete(Long id) {
        Assert.notNull(id, "id不能为空");
        contractSealService.removeById(id);
    }

    @ApiOperation("分页查询")
    @PostMapping("/listPage")
    public PageInfo<ContractSeal> listPage(@RequestBody ContractSeal contractSeal) {
        return contractSealService.listPage(contractSeal);
    }
    
}
