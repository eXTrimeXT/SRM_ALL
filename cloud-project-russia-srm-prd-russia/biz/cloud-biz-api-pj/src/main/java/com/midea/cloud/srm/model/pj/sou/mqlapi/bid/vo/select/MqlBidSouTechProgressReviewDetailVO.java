package com.midea.cloud.srm.model.pj.sou.mqlapi.bid.vo.select;

import com.midea.cloud.srm.model.pj.sou.mqlapi.bid.vo.init.MqlBidSouProjectVO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.tech.MqlSouTechProgressReviewDetailVO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.tech.MqlSouTechProgressReviewVendorDetailVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouGroup;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 招投标 - 工作小组查看技术评分详情
 * PS: 参考{@link MqlSouTechProgressReviewDetailVO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlBidSouTechProgressReviewDetailVO extends BaseObjectX {

    @ApiModelProperty("寻源单")
    private MqlBidSouProjectVO project;
    @ApiModelProperty("工作小组")
    private SouGroup group;
    @ApiModelProperty("技术评分详情")
    private List<MqlSouTechProgressReviewVendorDetailVO> vendorScoreDetailList;

}
