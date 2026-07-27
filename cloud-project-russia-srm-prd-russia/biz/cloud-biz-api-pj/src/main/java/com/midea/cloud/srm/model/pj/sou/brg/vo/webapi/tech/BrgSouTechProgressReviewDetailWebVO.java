package com.midea.cloud.srm.model.pj.sou.brg.vo.webapi.tech;

import com.midea.cloud.srm.model.pj.sou.openapi.brg.vo.init.ApiBrgSouProjectVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.tech.ApiSouTechProgressReviewDetailVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.tech.ApiSouTechProgressReviewVendorDetailVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouGroup;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 项目式询价 - 工作小组查看技术评分详情
 * PS: 参考{@link ApiSouTechProgressReviewDetailVO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/26
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BrgSouTechProgressReviewDetailWebVO extends BaseObjectX {

    @ApiModelProperty("寻源单")
    private ApiBrgSouProjectVO project;
    @ApiModelProperty("工作小组")
    private SouGroup group;
    @ApiModelProperty("技术评分详情")
    private List<ApiSouTechProgressReviewVendorDetailVO> vendorScoreDetailList;

}
