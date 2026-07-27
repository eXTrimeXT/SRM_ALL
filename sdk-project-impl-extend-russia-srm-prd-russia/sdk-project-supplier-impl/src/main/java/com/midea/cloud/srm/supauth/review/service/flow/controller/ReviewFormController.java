package com.midea.cloud.srm.supauth.review.service.flow.controller;

import com.midea.cloud.srm.model.supplierauth.review.entity.ReviewForm;
import com.midea.cloud.srm.supauth.review.service.IReviewFormService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * @author luxc18
 */
@Api(value = "ReviewFormController", tags = {"供应商资质审查信息二开"})
@RestController
@RequestMapping("/pj/supplier")
@Slf4j
public class ReviewFormController {

    @Autowired
    private IReviewFormService reviewFormService;

    @ApiOperation(value = "获取资质审查信息", notes = "获取资质审查信息")
    @GetMapping("/review/form/info")
    public ReviewForm getReviewFormInfo(@RequestParam("businessId") Long businessId) {
        return reviewFormService.getById(businessId);
    }
}
