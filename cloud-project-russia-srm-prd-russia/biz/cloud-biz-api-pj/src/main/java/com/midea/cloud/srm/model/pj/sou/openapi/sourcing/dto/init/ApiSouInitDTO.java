package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouInitScoreInfoDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouProjectInfoDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouRequireInfoDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouVendorInfoDTO;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 寻源openAPI - 立项信息保存
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/14
 */
@SuppressWarnings("ALL")
@Data
@ApiModel(description = "立项信息保存")
@EqualsAndHashCode(callSuper = true)
public class ApiSouInitDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    protected Long projectId;
    @ApiModelProperty("寻源单编号")
    protected String souNo;
    @ApiModelProperty("项目信息")
    protected ApiSouProjectInfoDTO projectInfo;
    @ApiModelProperty("项目需求")
    protected ApiSouRequireInfoDTO requireInfo;
    @ApiModelProperty("邀请供应商")
    protected ApiSouVendorInfoDTO vendorInfo;
    @ApiModelProperty("评分规则")
    protected ApiSouInitScoreInfoDTO scoreInfo;
    @ApiModelProperty(value = "保存步骤", required = true)
    protected CreateStep createStep;
    @ApiModelProperty("true-暂存/false-提交")
    protected boolean isTempSave;
    @ApiModelProperty("是否用于复制单据情况(true-基本放通所有校验)")
    protected boolean isCopy;
    @ApiModelProperty("当前用户ID(可为空)")
    protected Long currentUserId;

    /**
     *
     */
    @AllArgsConstructor
    public enum CreateStep {
        /**
         * 项目信息
         */
        projectInfo(1),
        /**
         * 项目需求
         */
        requireInfo(2),
        /**
         * 邀请供应商
         */
        inviteVendor(3),
        /**
         * 评分规则
         */
        scoreRule(4);

        @Getter
        private final Integer index;
    }

}

