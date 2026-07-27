package com.midea.cloud.srm.model.pj.sou.comp.vo.webapi.tech;

import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.init.ApiCompSouProjectVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.tech.ApiSouTechProgressReviewDetailVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.tech.ApiSouTechProgressReviewVendorDetailVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouGroup;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 竞价 - 工作小组查看技术评分详情
 * PS: 参考{@link ApiSouTechProgressReviewDetailVO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/16
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CompSouTechProgressReviewDetailWebVO extends BaseObjectX {

    @ApiModelProperty("寻源单")
    private ApiCompSouProjectVO project;
    @ApiModelProperty("工作小组")
    private SouGroup group;
    @ApiModelProperty("技术评分详情")
    private List<ApiSouTechProgressReviewVendorDetailVO> vendorScoreDetailList;

}
