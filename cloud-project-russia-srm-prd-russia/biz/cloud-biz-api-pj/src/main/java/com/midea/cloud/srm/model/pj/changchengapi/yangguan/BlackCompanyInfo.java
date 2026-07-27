package com.midea.cloud.srm.model.pj.changchengapi.yangguan;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author huangbf3
 * 阳光诚信系统 黑名单
 */
@Data
public class BlackCompanyInfo {

    @ApiModelProperty("公司全称")
    private String companyName;

    @ApiModelProperty("公司类别：禁止合作，重点关注")
    private String companyType;

    @ApiModelProperty("问题类别：向我司员工行贿")
    private String question;

    @ApiModelProperty("信息来源：腐败黑名单、合同黑名单、招标黑名单，默认为空")
    private String messageFrom;

    @ApiModelProperty("法人")
    private String legalRepresent;

    @ApiModelProperty("股东")
    private String shareholder;

    @ApiModelProperty("业务人员")
    private String bizPerson;

    @ApiModelProperty("停止合作日期；生效日期  格式： yyyy-MM-dd")
    private String endTime;

    @ApiModelProperty("扣除违约金")
    private String result;

    @ApiModelProperty("统一社会信用代码")
    private String taxCode;
}
