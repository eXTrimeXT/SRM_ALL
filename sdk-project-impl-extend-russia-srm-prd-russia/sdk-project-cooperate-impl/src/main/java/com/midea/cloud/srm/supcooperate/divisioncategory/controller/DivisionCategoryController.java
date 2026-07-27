package com.midea.cloud.srm.supcooperate.divisioncategory.controller;

import com.midea.cloud.srm.supcooperate.divisioncategory.service.IPjDivisionCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(value = "/divisionCategory/ext", tags = {"品类分工二开"})
@RestController
@RequestMapping("/divisionCategory/ext")
public class DivisionCategoryController {
    @Autowired
    private IPjDivisionCategoryService iPjDivisionCategoryService;

    @ApiOperation(value = "通过用户ID获取品类分工的品类ID", notes = "通过用户ID获取品类分工的品类ID")
    @GetMapping("/getUserDivisionCategoryId")
    List<Long> getUserDivisionCategoryId(@RequestParam("personInChargeUserId")Long personInChargeUserId){
        return iPjDivisionCategoryService.getUserDivisionCategoryId(personInChargeUserId);
    }
}
