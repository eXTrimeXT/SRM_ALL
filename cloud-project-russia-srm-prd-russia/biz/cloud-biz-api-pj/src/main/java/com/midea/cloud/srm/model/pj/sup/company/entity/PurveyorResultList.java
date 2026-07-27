package com.midea.cloud.srm.model.pj.sup.company.entity;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.util.List;

/**
 * @author huangbf3
 */
@Data
@ApiOperation("供应商返回结果集")
public class PurveyorResultList <T>{

    @ApiModelProperty("社会信用代码/税号/身份证号，唯一")
    private String taxCode;

    @ApiModelProperty("供应商具体信息")
    @JSONField(name = "SupplierInfoList")
    private List<SupplierInfo> supplierInfoList;



}
