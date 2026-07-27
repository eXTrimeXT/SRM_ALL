package com.midea.cloud.srm.sou.sourcing.vendor.spi;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.bid.enums.BidProjectStatusEnum;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDTO;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDetailDTO;
import com.midea.cloud.srm.model.sou.bidnotices.enums.BidNoticeStatusEnum;
import com.midea.cloud.srm.model.sou.ca.enums.CaTypeEnum;
import com.midea.cloud.srm.model.sou.enums.*;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.enums.ExtOrderTypeEnum;
import com.midea.cloud.srm.model.sou.enums.MarginHanderModeEnum;
import com.midea.cloud.srm.model.sou.enums.SouBidPlanTypeEnum;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouProjectQueryDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouMarginDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ExtSouOrderDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.sourcing.fixstatus.service.SouFixedProjectStatusService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouDemandService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouMarginService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouPlanService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderDAO;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Service
public class ApiExtSouProjectVendorQueryHandler implements ISouSpiBean {

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private SouFixedProjectStatusService fixedProjectStatusService;

    @Autowired
    private IExtSouMarginService marginService;

    @Autowired
    private SouOrderDAO souOrderDAO;

    @Autowired
    private IExtSouDemandService demandService;

    @Autowired
    private IExtSouPlanService planService;

    @Autowired
    private QlService qlService;

    public void doHandlerVendorAuth(Long projectId) {
        ExtSouProject project = projectService.getById(projectId);

        AssertUtils.notNull(project, "寻源项目信息不存在！");

        LambdaQueryWrapper<SouOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SouOrder::getProjectId, projectId);
        queryWrapper.eq(SouOrder::getVendorId, AppUserUtil.getLoginAppUser().getCompanyId());
        int count = (int) souOrderDAO.count(queryWrapper);
        if(Integer.compare(count, 0) <= 0) {
            throw new BaseException("当前供应商账号无该寻源项目的查看权限");
        }
    }

    @ApiOperation("寻源分页查询的前置处理")
    public QueryWrapper doHandlerBeforePageProjects(ApiExtSouProjectQueryDTO queryParam, String souType) {
        //刷新招标单状态
        fixedProjectStatusService.fixedProjectStatusAll(souType);

        QueryWrapper queryWrapper = new QueryWrapper();
        queryParam.formatParams();
        queryWrapper.like(StringUtils.isNotBlank(queryParam.getSouNo()), "p.sou_no", queryParam.getSouNo());
        queryWrapper.like(StringUtils.isNotBlank(queryParam.getExtProjectNo()), "p.ext_project_no", queryParam.getExtProjectNo());
        queryWrapper.like(StringUtils.isNotBlank(queryParam.getSouName()), "p.sou_name", queryParam.getSouName());
        queryWrapper.eq(StringUtils.isNotBlank(queryParam.getProjectStatus()), "p.project_status", queryParam.getProjectStatus());
        queryWrapper.ge(ObjectUtils.allNotNull(queryParam.getCreationDateFrom()), "p.CREATION_DATE", queryParam.getCreationDateFrom());
        queryWrapper.le(ObjectUtils.allNotNull(queryParam.getCreationDateTo()), "p.CREATION_DATE", queryParam.getCreationDateTo());
        queryWrapper.ge(ObjectUtils.allNotNull(queryParam.getPublishTimeFrom()), "p.publish_time", queryParam.getPublishTimeFrom());
        queryWrapper.le(ObjectUtils.allNotNull(queryParam.getPublishTimeTo()), "p.publish_time", queryParam.getPublishTimeTo());
        queryWrapper.eq(StringUtils.isNotBlank(queryParam.getOrderStatus()), "o.order_status", queryParam.getOrderStatus());
        queryWrapper.like(StringUtils.isNotBlank(queryParam.getCreatedFullName()), "p.CREATED_FULL_NAME", queryParam.getCreatedFullName());


        return queryWrapper;
    }

    @ApiOperation("寻源分页查询的后置处理")
    public List<ExtSouOrderDto> doHandlerAfterPageProjects(ApiExtSouProjectQueryDTO queryParam, String souType, List<ExtSouOrderDto> souOrderDtoList) {

        if(CollectionUtils.isNotEmpty(souOrderDtoList)) {
            LambdaQueryWrapper<ExtSouMargin> queryWrapper = new LambdaQueryWrapper<>();
            List<Long> projectIdList = souOrderDtoList.stream().map(ExtSouOrderDto::getProjectId).distinct().collect(Collectors.toList());
            queryWrapper.in(ExtSouMargin::getProjectId, projectIdList);
            queryWrapper.eq(ExtSouMargin::getVendorId, AppUserUtil.getLoginAppUser().getCompanyId());
            List<ExtSouMargin> souMarginList = marginService.list(queryWrapper);
            marginService.copyYearMarginInfo(souMarginList);
            Map<Long, ExtSouMargin> souMarginMap = souMarginList.stream().collect(Collectors.toMap(o -> o.getProjectId(), Function.identity(), (k1, k2)->k2));

            //是否发送
            Map<Long, String> sendMap = bidNoticeSendInfo(projectIdList);

            //处理是否合并招标标识
            LambdaQueryWrapper<ExtSouDemand> demanQuery = new LambdaQueryWrapper<>();
            demanQuery.in(ExtSouDemand::getProjectId, souOrderDtoList.stream().map(o->o.getProjectId()).distinct().collect(Collectors.toList()));
            demanQuery.eq(ExtSouDemand::getStatus, SrmConstant.NUM_ZERO);
            List<ExtSouDemand> demandList = demandService.list(demanQuery);
            Map<Long, List<ExtSouDemand>> demandGroup = demandList.stream().collect(Collectors.groupingBy(ExtSouDemand::getProjectId));

            //修正截止时间
            List<ExtSouPlan> extSouPlans = planService.lambdaQuery().in(ExtSouPlan::getProjectId, souOrderDtoList.stream().map(o -> o.getProjectId()).distinct().collect(Collectors.toList()))
                            .eq(ExtSouPlan::getPlanType, SouBidPlanTypeEnum.PLAN.getCode()).list();

            Map<Long, ExtSouPlan> extSouPlanMap = extSouPlans.stream().collect(Collectors.toMap(p -> p.getProjectId(), Function.identity(), (k1, k2)->k2));
            souOrderDtoList.stream().forEach(o -> {
                ExtSouMargin margin = souMarginMap.getOrDefault(o.getProjectId(), new ExtSouMargin());
                o.setMarginStatus(margin.getMarginStatus());
                o.setHanderMode(margin.getHanderMode());
                o.setCauseDesc(margin.getCauseDesc());
                o.setMarginId(margin.getMarginId());
                o.setMergeFlag(Integer.compare(demandGroup.getOrDefault(o.getProjectId(), new ArrayList<>()).size(), 1) == 1);

                if(ExtOrderTypeEnum.TECH.getCode().equals(o.getExtOrderType())) {
                    ExtSouPlan plan = extSouPlanMap.getOrDefault(o.getProjectId(), new ExtSouPlan());
                    o.setOrderEndTime(ObjectUtils.anyNull(plan.getTechEndFixTime())?plan.getTechEndTime():plan.getTechEndFixTime());
                }
                //仅未缴纳可选择允许不缴纳，允许后，供应商不显示缴纳保证金按钮。
                //创建标书是否缴纳保证金：Y
                //显示缴纳按钮。
                //允许不缴纳：Y->N
                //不需要缴纳保证金：不显示缴纳按钮。

                //前端这个标识等于Y时，不显示缴纳保证金按钮
                String extEarnestFlag = YesOrNo.NO.getValue();//供应商协同，该标识传N时默认校验保证金投标
                if(YesOrNo.YES.getValue().equals(o.getExtEarnestFlag())) {
                    if(MarginHanderModeEnum.CAN_NOTPAY.getCode().equals(margin.getHanderMode())) {
                        extEarnestFlag = YesOrNo.YES.getValue();//供应商协同，该标识传Y时允许不校验保证金投标
                    }
                } else {
                    extEarnestFlag = YesOrNo.YES.getValue();//供应商协同，该标识传Y时允许不校验保证金投标
                }
                o.setExtEarnestFlag(extEarnestFlag);
                o.setIsSend(MapUtils.getString(sendMap, o.getProjectId(), YesOrNo.NO.getValue()));
            });

        }

        return souOrderDtoList;
    }

    private Map<Long, String> bidNoticeSendInfo(List<Long> projectIdList) {
        Map<Long, String> sendMap = new HashMap<>(50);
        if(CollectionUtils.isNotEmpty(projectIdList)) {
            List<BidNoticeDTO> bidNoticeList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.BidNotice.getCode()).in(BidNoticeDTO::getProjectId, projectIdList).eq(BidNoticeDTO::getType, CaTypeEnum.APPLY.getCode()).eq(BidNoticeDTO::getStatus, BidNoticeStatusEnum.APPROVED.getCode()), BidNoticeDTO.class);
            if(CollectionUtils.isNotEmpty(bidNoticeList)) {
                List<BidNoticeDetailDTO> bidNoticeDetailList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.BidNoticeDetail.getCode()).in(BidNoticeDetailDTO::getBidNoticeId, bidNoticeList.stream().map(BidNoticeDTO::getBidNoticeId).collect(Collectors.toList())).eq(BidNoticeDetailDTO::getIsSend, YesOrNo.YES.getValue()).eq(BidNoticeDetailDTO::getVendorId, AppUserUtil.getLoginAppUser().getCompanyId()), BidNoticeDetailDTO.class);
                if(CollectionUtils.isNotEmpty(bidNoticeDetailList)) {
                    Map<Long, BidNoticeDTO> bidNoticeMap = bidNoticeList.stream().collect(Collectors.toMap(BidNoticeDTO::getBidNoticeId, Function.identity(), (k1, k2)->k2));
                    bidNoticeDetailList.stream().forEach(detail -> {
                        sendMap.put(bidNoticeMap.get(detail.getBidNoticeId()).getProjectId(), YesOrNo.YES.getValue());
                    });
                }
            }
        }
        return sendMap;
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
