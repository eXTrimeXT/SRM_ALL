package com.midea.cloud.srm.model.pj.sou.openapi.inq.dto.init;

import com.midea.cloud.srm.model.pj.sou.openapi.inq.dto.init.ApiInqSouProjectInfoDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.inq.dto.init.ApiInqSouRequireInfoDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouInitDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouInitScoreInfoDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouVendorInfoDTO;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 询比价openAPI - 立项保存信息
 * PS: 参考 {@link ApiSouInitDTO}
 *
 * @author zhangwk12@meicloud.com, www.gdzwk.com
 * @since 2022/8/31
 */
@SuppressWarnings("ALL")
@Data
@ApiModel(description = "创建时保存询价单的数据结构")
@EqualsAndHashCode(callSuper = true)
public class ApiInqSouInitDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    private Long projectId;
    @ApiModelProperty("寻源单编号")
    private String souNo;
    @ApiModelProperty("项目信息")
    private ApiInqSouProjectInfoDTO projectInfo;
    @ApiModelProperty("项目需求")
    private ApiInqSouRequireInfoDTO requireInfo;
    @ApiModelProperty("邀请供应商")
    private ApiSouVendorInfoDTO vendorInfo;
    @ApiModelProperty("评分规则")
    protected ApiSouInitScoreInfoDTO scoreInfo;
    @ApiModelProperty(value = "保存步骤", required = true)
    private ApiSouInitDTO.CreateStep createStep;
    @ApiModelProperty("true-暂存/false-提交")
    private boolean isTempSave;
    @ApiModelProperty("是否用于复制单据情况(true-基本放通所有校验)")
    private boolean isCopy;
    @ApiModelProperty("当前用户ID(可为空)")
    private Long currentUserId;

}
