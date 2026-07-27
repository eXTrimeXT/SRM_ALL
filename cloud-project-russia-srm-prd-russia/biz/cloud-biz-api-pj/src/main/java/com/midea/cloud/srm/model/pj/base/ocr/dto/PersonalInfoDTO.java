package com.midea.cloud.srm.model.pj.base.ocr.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author huangbf3
 */
@Data
public class PersonalInfoDTO {

    @ApiModelProperty("性别")
    private String sex;

    @ApiModelProperty("民族")
    private String nation;

    @ApiModelProperty("出生日期")
    private String birth;

    @ApiModelProperty("地址")
    private String address;

    @ApiModelProperty("身份证号")
    private String idNum;

    @ApiModelProperty("姓名")
    private String name;
}
