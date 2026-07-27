package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.tech;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.tech.ApiSouTechProgressReviewVendorDetailVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouGroup;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouTechScoreHead;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTechScoreStatusEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 寻源openAPI - 工作小组查看技术评分详情
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouTechProgressReviewDetailVO extends BaseObjectX {

    @ApiModelProperty("寻源单")
    private SouProject project;
    @ApiModelProperty("工作小组")
    private SouGroup group;
    @ApiModelProperty("技术评分详情")
    private List<ApiSouTechProgressReviewVendorDetailVO> vendorScoreDetailList;

    public static ApiSouTechProgressReviewDetailVO convertApiVO(SouProject project, SouGroup group,
                                                                List<SouVendor> vendorList,
                                                                List<SouTechScoreHead> techScoreHeadList) {
        ApiSouTechProgressReviewDetailVO vo = new ApiSouTechProgressReviewDetailVO();
        vo.setProject(project);
        vo.setGroup(group);

        Map<Long/* vendorId */, SouTechScoreHead> scoreHeadMap = techScoreHeadList.stream()
                .collect(Collectors.toMap(SouTechScoreHead::getVendorId, Function.identity()));
        vo.setVendorScoreDetailList(new ArrayList<>(vendorList.size()));
        for (SouVendor vendor : vendorList) {
            ApiSouTechProgressReviewVendorDetailVO detail = new ApiSouTechProgressReviewVendorDetailVO();
            vo.getVendorScoreDetailList().add(detail);

            BeanUtils.copyProperties(vendor, detail);

            SouTechScoreHead scoreHead = scoreHeadMap.get(vendor.getVendorId());
            detail.setScoreStatus(scoreHead != null ? scoreHead.getScoreStatus() : SouTechScoreStatusEnum.UNFINISHED);
        }

        return vo;
    }

}
