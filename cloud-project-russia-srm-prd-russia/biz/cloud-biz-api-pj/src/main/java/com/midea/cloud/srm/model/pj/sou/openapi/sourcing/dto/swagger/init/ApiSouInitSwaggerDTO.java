package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.swagger.init;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.*;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.swagger.init.ApiSouProjectInfoSwaggerDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.swagger.init.ApiSouRequireInfoSwaggerDTO;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 立项信息保存 (swagger接口专用)
 * PS: 由于目前的寻源结构（ObjectX + SPI），很难描述不同寻源场景对入参的需求差别，
 *     因此用一个专有的类来装所有的信息。
 * PS: 来源于 {@link ApiSouInitDTO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/01/05
 */
@SuppressWarnings("ALL")
@Data
@ApiModel(description = "立项信息保存")
@EqualsAndHashCode(callSuper = true)
public class ApiSouInitSwaggerDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    protected Long projectId;
    @ApiModelProperty("寻源单编号")
    protected String souNo;
    @ApiModelProperty("项目信息")
    protected ApiSouProjectInfoSwaggerDTO projectInfo;
    @ApiModelProperty("项目需求")
    protected ApiSouRequireInfoSwaggerDTO requireInfo;
    @ApiModelProperty("邀请供应商")
    protected ApiSouVendorInfoDTO vendorInfo;
    @ApiModelProperty("评分规则")
    protected ApiSouInitScoreInfoDTO scoreInfo;
    @ApiModelProperty(value = "保存步骤", required = true)
    protected ApiSouInitDTO.CreateStep createStep;
    @ApiModelProperty("true-暂存/false-提交")
    protected boolean isTempSave;
    @ApiModelProperty("是否用于复制单据情况(true-基本放通所有校验)")
    protected boolean isCopy;
    @ApiModelProperty("当前用户ID(可为空)")
    protected Long currentUserId;

}
