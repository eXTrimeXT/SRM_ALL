package com.midea.cloud.srm.model.pj.sou.openapi.inq.vo.init;

import com.github.pagehelper.Page;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouProject;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouRound;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 简易询价openAPI - 寻源单
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiInqSouProjectVO extends SouProject {

    /** @see InqSouProject#getInquiryType */
    @ApiModelProperty("询价类型[字典值: SOU_INQUIRY_TYPE]")
    private String inquiryType;

    /** @see InqSouProject#getExchangeRateType */
    @ApiModelProperty("汇率类型[字典值: EXCHANGE_RATE_TYPE]")
    private String exchangeRateType;

    /** @see InqSouProject#getCurrencyExchangeDate */
    @ApiModelProperty("币种转换日期")
    private Date currencyExchangeDate;

    /** @see InqSouProject#getIsTargetPriceOk */
    @ApiModelProperty("是否已设定目标价(Y/N)")
    private Enable isTargetPriceOk;

    /** @see InqSouProject#getExcludeBlackVendors */
    @ApiModelProperty("推荐控制 -- 是否排除黑名单供应商(Y/N)")
    private Enable excludeBlackVendors;

    /** @see InqSouProject#getExcludeNoCurrentOrgVendors */
    @ApiModelProperty("推荐控制 -- 是否排除非本业务实体供应商(Y/N)")
    private Enable excludeNoCurrentOrgVendors;

    /** @see InqSouProject#getExcludeOrgQuitVendors */
    @ApiModelProperty("推荐控制 -- 是否排除业务实体退出供应商(Y/N)")
    private Enable excludeOrgQuitVendors;

    /** @see InqSouProject#getExcludeOrgCategoryStatus */
    @ApiModelProperty("推荐控制 -- 需要排除指定品类状态的供应商")
    private String excludeOrgCategoryStatus;

    /** @see SouRound#getInviteCount */
    @ApiModelProperty("本轮应报价供应商数量")
    private Integer inviteCount;

    /** @see SouRound#getOrderCount */
    @ApiModelProperty("本轮已报价供应商数量")
    private Integer orderCount;

    @SuppressWarnings("rawtypes")
    public static List<ApiInqSouProjectVO> convertWebVO(List<SouProject> souProjectList) {
        if (souProjectList.isEmpty()) { return Collections.emptyList(); }
        List<ApiInqSouProjectVO> voList; {
            if (souProjectList instanceof Page) {
                voList = new Page<>();
                ((Page)voList).setTotal(((Page)souProjectList).getTotal());
                ((Page)voList).setPageSize(((Page)souProjectList).getPageSize());
                ((Page)voList).setPageNum(((Page)souProjectList).getPageNum());
            } else {
                voList = new ArrayList<>(souProjectList.size());
            }
        }
        souProjectList.forEach(project -> voList.add(SouObjectXUtil.convertTargetObj(project, ApiInqSouProjectVO.class)));
        return voList;
    }

}
