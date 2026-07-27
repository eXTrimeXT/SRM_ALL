package com.midea.cloud.srm.model.contract.dto;

import com.midea.cloud.srm.model.cm.perform.entity.PerAcceptance;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 100014336 ganyh19
 */
@Data
public class PerAcceptanceExt extends PerAcceptance {

    /**
     * 创建人所属板块代码
     */
    @ApiModelProperty("创建人所属板块代码")
    private String extHrSectorCode;

    /**
     * 创建人所属板块ID
     */
    @ApiModelProperty("创建人所属板块ID")
    private Long extHrSectorId;

    /**
     * 创建人所属板块名称
     */
    @ApiModelProperty("创建人所属板块名称")
    private String extHrSectorName;

    /**
     * 创建人所属部门名称
     */
    @ApiModelProperty("创建人所属部门名称")
    private String extHrDeptName;

    /**
     * 创建人所属部门Id
     */
    @ApiModelProperty("创建人所属部门Id")
    private Long extHrDeptId;

    /**
     * 创建人所属部门代码
     */
    @ApiModelProperty("创建人所属部门代码")
    private String extHrDeptCode;

    /**
     * 创建人所属公司名称
     *
     */
    @ApiModelProperty("创建人所属公司名称")
    private String extHrCompanyName;

    /**
     * 创建人所属公司Id
     */
    @ApiModelProperty("创建人所属公司Id")
    private Long extHrCompanyId;

    /**
     * 创建人所属公司代码
     */
    @ApiModelProperty("创建人所属公司代码")
    private String extHrCompanyCode;

    /**
     * bpm发起人账号
     */
    private String startBpmUsername;

    /**
     * bpm发起人名称
     */
    private String startBpmNickname;

}
