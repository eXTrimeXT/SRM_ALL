package com.midea.cloud.srm.model.extapi.sou.purinq.dto.init;

import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiSouInitDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiSouInitScoreInfoDTO;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtPurInqSouInitDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    protected Long projectId;
    @ApiModelProperty("寻源单编号")
    protected String souNo;
    @ApiModelProperty("项目信息")
    protected ApiPurInqSouProjectInfoDTO projectInfo;
    @ApiModelProperty("项目需求")
    protected ApiPurInqSouRequireInfoDTO requireInfo;
    @ApiModelProperty("邀请供应商")
    protected ApiPurInqSouVendorInfoDTO vendorInfo;
    @ApiModelProperty(value = "保存步骤", required = true)
    protected ApiSouInitDTO.CreateStep createStep;
    @SuppressWarnings({"AlibabaBooleanPropertyShouldNotStartWithIs", "AlibabaPojoMustUsePrimitiveField"})
    @ApiModelProperty("true-暂存/false-提交")
    protected boolean isTempSave;
    @SuppressWarnings({"AlibabaBooleanPropertyShouldNotStartWithIs", "AlibabaPojoMustUsePrimitiveField"})
    @ApiModelProperty("是否用于复制单据情况(true-基本放通所有校验)")
    protected boolean isCopy;
    @ApiModelProperty("当前用户ID(可为空)")
    protected Long currentUserId;
    protected ApiSouInitScoreInfoDTO scoreInfo;

}
