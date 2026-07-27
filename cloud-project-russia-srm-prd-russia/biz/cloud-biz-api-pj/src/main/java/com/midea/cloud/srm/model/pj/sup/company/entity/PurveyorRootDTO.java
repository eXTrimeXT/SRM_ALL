package com.midea.cloud.srm.model.pj.sup.company.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author huangbf3
 * 根据社会信用代码查询供应商信息接口返回结果
 */
@Data
public class PurveyorRootDTO<T> {

    @ApiModelProperty("响应码")
    private String code;

    @ApiModelProperty("描述信息")
    private String message;

    @ApiModelProperty("返回数据")
    private List<PurveyorResultList> result;
}
