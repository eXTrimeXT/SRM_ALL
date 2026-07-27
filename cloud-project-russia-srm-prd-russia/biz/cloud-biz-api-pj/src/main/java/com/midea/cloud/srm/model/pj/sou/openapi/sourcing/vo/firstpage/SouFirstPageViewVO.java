package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.firstpage;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouSignUpStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 寻源 - 门户.寻源信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/01/19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SouFirstPageViewVO extends SouProject {

    @ApiModelProperty(value = "距离截止时间天数")
    private Integer sinceLastDay;
    @ApiModelProperty("品类全称")
    private String categoryFullNames;
    @ApiModelProperty("已报名供应商数")
    private Long signUpVendorCount;

    @SuppressWarnings("rawtypes")
    public static PageInfo<SouFirstPageViewVO> convertPage(List<SouProject> projectList,
                                                           Map<Long/* projectId */, List<SouVendor>> vendorCountMap,
                                                           Map<Long/* projectId */, Set<String/* fullCategoryName */>> fullCategoryMap) {
        if (projectList.isEmpty()) { return new PageInfo<>(); }

        List<SouFirstPageViewVO> voList;
        if (projectList instanceof Page) {
            voList = new Page<>();
            ((Page)voList).setTotal(((Page)projectList).getTotal());
            ((Page)voList).setPageSize(((Page)projectList).getPageSize());
            ((Page)voList).setPageNum(((Page)projectList).getPageNum());
        } else {
            voList = new ArrayList<>(projectList.size());
        }
        Date now = new Date();
        projectList.forEach(project -> {
            SouFirstPageViewVO vo = SouObjectXUtil.convertTargetObj(project, SouFirstPageViewVO.class);
            voList.add(vo);

            List<SouVendor> vendorList = vendorCountMap.get(project.getProjectId());
            vo.setSignUpVendorCount(CollectionUtils.isEmpty(vendorList) ? 0L : vendorList.stream()
                    .filter(vendor -> SouSignUpStatusEnum.SIGN_UP_DONE.equals(vendor.getSignUpStatus()))
                    .count());

            // 距离截止时间天数
            if (project.getOrderEndTime() != null && project.getOrderEndTime().after(now)) {
                int sinceLastDay = Math.max(daysBetween(now, project.getOrderEndTime()), 0);
                vo.setSinceLastDay(sinceLastDay);
            } else {
                vo.setSinceLastDay(0);
            }

            StringBuilder sb = new StringBuilder(300); {
                Set<String> categoryNames = fullCategoryMap.get(project.getProjectId());
                if (!org.springframework.util.CollectionUtils.isEmpty(categoryNames)) {
                    categoryNames.forEach(categoryName -> {
                        sb.append(categoryName);
                        sb.append(",");
                    });
                }
            }
            vo.setCategoryFullNames(sb.length() > 0 ? sb.substring(0, sb.length() - 2) : null);
        });

        return new PageInfo<>(voList);
    }

    public static int daysBetween(Date beginDate, Date endDate) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        try {
            beginDate =sdf.parse(sdf.format(beginDate));
            endDate =sdf.parse(sdf.format(endDate));
        } catch (ParseException e) {
            throw new IllegalArgumentException("日期转换错误");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(beginDate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(endDate);
        long time2 = cal.getTimeInMillis();
        long betweenDays=(time2-time1)/(1000*3600*24);

        return Integer.parseInt(String.valueOf(betweenDays));
    }

}
