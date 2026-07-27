package com.midea.cloud.srm.sou.sourcing.vendor.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.*;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.PjSouClient;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDTO;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDetailDTO;
import com.midea.cloud.srm.model.sou.ca.enums.CaStatusEnum;
import com.midea.cloud.srm.model.sou.ca.enums.CaTypeEnum;
import com.midea.cloud.srm.model.sou.enums.*;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouPriceTemplateDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouProjectQueryDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouMarginDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouProjectDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.*;
import com.midea.cloud.srm.model.sou.sourcing.dto.MarginRecordVo;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.sou.bid.init.service.ExtBidSouInitQueryWebService;
import com.midea.cloud.srm.sou.bid.ipmonitors.IpMonitoryCompent;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouFileMapper;
import com.midea.cloud.srm.sou.sourcing.init.service.*;
import com.midea.cloud.srm.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.sou.sourcing.vendor.mapper.ExtSouOrderMapper;
import com.midea.cloud.srm.sou.sourcing.vendor.service.ExtBidSouForVendorQueryService;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderItemService;
import com.midea.cloud.srm.sou.sourcing.vendor.spi.ApiExtSouMarginVendorQueryHandler;
import com.midea.cloud.srm.sou.sourcing.vendor.spi.ApiExtSouOrderItemVendorQueryHandler;
import com.midea.cloud.srm.sou.sourcing.vendor.spi.ApiExtSouProjectVendorQueryHandler;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 备注
 * @author huangbf3
 */
@Api("寻源核心-供应商接口实现类")
@Service
@Slf4j
public class ExtBidSouForVendorQueryServiceImpl implements ExtBidSouForVendorQueryService {
    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private ExtSouOrderMapper extSouOrderMapper;

    @Autowired
    private ExtBidSouInitQueryWebService bidSouInitQueryWebService;

    @Autowired
    private IExtSouOrderItemService souOrderItemService;

    @Autowired
    private IExtSouOrderFileService orderFileService;

    @Autowired
    private IExtSouItemService itemService;

    @Autowired
    private ExtSouFileMapper souFileMapper;

    @Autowired
    private IExtSouMarginService marginService;

    @Autowired
    private QlService qlService;

    @Autowired
    private IExtSouVendorService vendorService;

    @Autowired
    private PjSouClient pjSouClient;

    @Autowired
    private IExtSouRoundService roundService;

    @Autowired
    private IExtSouDemandService demandService;

    @Autowired
    private IExtNpmSouOrderService extNpmSouOrderService;

    @Autowired
    private IpMonitoryCompent ipMonitoryCompent;

    @Autowired
    IExtSouPlanService planService;


    @Override
    public List<MarginRecordVo> getSouMarginRecord(Long projectId, Long companyId) {
        return extSouOrderMapper.getSouMarginRecord(projectId,companyId);
    }

    @Override
    public PageInfo<ExtSouOrderDto> getPage(ApiExtSouProjectQueryDTO query, String souType) {

        //行业包前置处理
        QueryWrapper queryWrapper = SouActiveBeanUtils.getActiveBean(souType, ApiExtSouProjectVendorQueryHandler.class).doHandlerBeforePageProjects(query, souType);
        queryWrapper.eq("p.sou_type", souType);
        queryWrapper.eq("o.vendor_id", AppUserUtil.getLoginAppUser().getCompanyId());
        queryWrapper.orderByDesc("p.CREATION_DATE");
        PageUtil.startPage(query.getPageNum(), query.getPageSize());

        List<ExtSouOrderDto> souOrderDtoList = extSouOrderMapper.listOrder(queryWrapper);

        PageInfo pageInfo = new PageInfo(souOrderDtoList);

        souOrderDtoList.stream().forEach(order -> {
            order.setCurrentRoundFlag(YesOrNo.YES.getValue());
            if(Integer.compare(ObjectUtils.defaultIfNull(order.getRound(), 1), ObjectUtils.defaultIfNull(order.getCurrentRound(), 1)) != 0) {
                order.setCurrentRoundFlag(YesOrNo.NO.getValue());
            } else {
                //轮次相等,第一轮区分技术标和商务标
                if(Integer.compare(ObjectUtils.defaultIfNull(order.getRound(), 1), 1) == 0) {
                    //如果是技术标类型，判断状态是技术投标中
                    if(ExtOrderTypeEnum.TECH.getCode().equals(order.getExtOrderType())) {
                        if(!Arrays.asList(SouBiddingProStatusEnum.TECH_BID.getCode()).contains(order.getProjectStatus())) {
                            order.setCurrentRoundFlag(YesOrNo.NO.getValue());
                        }
                    }
                }
            }
        });

        //行业包后置处理
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouProjectVendorQueryHandler.class).doHandlerAfterPageProjects(query, souType, souOrderDtoList);

        return pageInfo;
    }

    private ApiExtSouOrderDetailDto getDetail(Long orderId, Boolean currentRoundFlag, String souType) {
        ExtSouOrder souOrder = extSouOrderMapper.selectById(orderId);
        AssertUtils.notNull(souOrder, "供应商报价单不存在");

        ExtSouProject project = projectService.getById(souOrder.getProjectId());
        ExtSouProjectDto souProjectDto = new ExtSouProjectDto();
        BeanCopyUtil.copyProperties(souProjectDto, project);

        LambdaQueryWrapper<ExtSouRound> roundQuery = new LambdaQueryWrapper<>();
        roundQuery.eq(ExtSouRound::getProjectId, project.getProjectId());
        roundQuery.eq(ExtSouRound::getRound, souOrder.getRound());
        List<ExtSouRound> roundList = roundService.list(roundQuery);

        //投标截止时间
        if(CollectionUtils.isNotEmpty(roundList)) {
            souProjectDto.setOrderEndTime(roundList.get(0).getOrderEndTime());
            souProjectDto.setOrderStartTime(roundList.get(0).getOrderStartTime());
        }
        //技术标截止时间
        if(ExtOrderTypeEnum.TECH.getCode().equals(souOrder.getExtOrderType())) {
            List<ExtSouPlan> planList = planService.lambdaQuery().eq(ExtSouPlan::getProjectId, souOrder.getProjectId())
                    .eq(ExtSouPlan::getPlanType, SouBidPlanTypeEnum.PLAN.getCode()).list();
            planList.stream().forEach(p -> souProjectDto.setOrderEndTime(Objects.isNull(p.getTechEndFixTime())?p.getTechEndTime():p.getTechEndFixTime()));
        }

        Integer round = null;
        if(currentRoundFlag) {
            round = project.getCurrentRound();
        }

        ApiExtSouOrderDetailDto detailDto = new ApiExtSouOrderDetailDto();
        //技术标文件
        detailDto.setTechOrderFileList(orderFileService.getScoreTechOrderFile(souOrder.getProjectId(), souOrder.getOrderId()));
        //商务标文件
        detailDto.setBusOrderFileList(orderFileService.getBusOrderFile(souOrder.getProjectId(), souOrder.getOrderId()));
        //报价模板
        ApiExtSouPriceTemplateDto templateDto = bidSouInitQueryWebService.listPriceTemplate(souOrder.getProjectId());
        detailDto.setPriceTemplateList(templateDto.getSelectedList());
        //报价信息
        LambdaQueryWrapper<ExtSouOrderItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouOrderItem::getProjectId, souOrder.getProjectId());
        queryWrapper.eq(ExtSouOrderItem::getOrderId, souOrder.getOrderId());
        queryWrapper.eq(ObjectUtils.allNotNull(round), ExtSouOrderItem::getRound, round);
        queryWrapper.orderByDesc(ExtSouOrderItem::getCreationDate);
        List<ExtSouOrderItem> orderItemList = souOrderItemService.list(queryWrapper);

        //查询报价字段
        LambdaQueryWrapper<ExtSouItem> itemQueryWrapper = new LambdaQueryWrapper<>();
        itemQueryWrapper.eq(ExtSouItem::getProjectId, souOrder.getProjectId());
        List<ExtSouItem> souItemList = itemService.list(itemQueryWrapper);

        Map<Long, ExtSouItem> souItemMap = souItemList.stream().collect(Collectors.toMap(ExtSouItem::getSouItemId, Function.identity()));

        List<ApiExtSouOrderItemDto> orderItemDtoList = JSON.parseArray(JSON.toJSONString(orderItemList), ApiExtSouOrderItemDto.class);
        orderItemDtoList = orderItemDtoList.stream().filter(o -> souItemMap.containsKey(o.getSouItemId())).collect(Collectors.toList());
        orderItemDtoList.stream().forEach(itemDto -> {
            try {
                ExtSouItem item = souItemMap.getOrDefault(itemDto.getSouItemId(), new ExtSouItem());
                //清理报价模板供应商填写字段
                item.cleanupVendorFiled(templateDto.getSelectedList().stream().filter(s -> "VENDOR".equals(s.getColumnType())).collect(Collectors.toList()));
                BeanCopyUtil.copyProperties(itemDto, item, true);
                itemDto.setSubmitTime(souOrder.getSubmitTime());
            } catch (Exception e) {
                log.error("getOrderDetail Exception", e);
            }
            itemDto.coverItemFields();
        });

        detailDto.setProjectId(souOrder.getProjectId());
        detailDto.setOrderId(souOrder.getOrderId());

        detailDto.setOrderItemList(orderItemDtoList);

        //重新处理技术标文件
        if(currentRoundFlag) {
            detailDto.setTechOrderFileList(filterByRound(detailDto.getTechOrderFileList(), round));
            detailDto.setBusOrderFileList(filterByRound(detailDto.getBusOrderFileList(), round));
        }

        detailDto.setProject(souProjectDto);

        //行业包后置处理
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouOrderItemVendorQueryHandler.class).doHandlerAfterQueryOrderItem(orderId, souType, detailDto);

        return detailDto;
    }

    private List<ExtSouOrderFile> filterByRound(List<ExtSouOrderFile> orderFileList, Integer round) {
        if(CollectionUtils.isEmpty(orderFileList)) {
            return orderFileList;
        }
        Map<Integer, List<ExtSouOrderFile>> groupFile = orderFileList.stream().collect(Collectors.groupingBy(ExtSouOrderFile::getRound));
        if(groupFile.containsKey(round)) {
            return groupFile.get(round);
        }
        List<ExtSouOrderFile> orderFiles = null;
        /** 取消自动取上一轮附件的逻辑
        if(Integer.compare(round, SrmConstant.NUM_ONE) == 1) {
            for(int i = round -1; i > SrmConstant.NUM_ZERO; i--) {
                //获取上一轮次的招标附件
                orderFiles = groupFile.get(i);
                if(CollectionUtils.isNotEmpty(orderFiles)) {
                    orderFiles.stream().forEach(file -> {
                        file.setOrderFileId(null);
                        file.setExtSubmitTime(null);
                    });
                    break;
                }
            }
        } */

        return orderFiles;
    }

    @Override
    public ApiExtSouOrderDetailDto getOrderDetail(Long orderId, String souType) {
        return getDetail(orderId, false, souType);
    }

    @Override
    public ApiExtSouOrderDetailDto getTenderDetail(Long orderId, String souType) {
        return getDetail(orderId, true, souType);
    }

    @Override
    public ApiExtSouBidFileDto getBidSouFileList(Long projectId, String souType) {

        ApiExtSouBidFileDto fileDto = new ApiExtSouBidFileDto();

        //操作权限
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouProjectVendorQueryHandler.class).doHandlerVendorAuth(projectId);

        LambdaQueryWrapper<ExtSouFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouFile::getProjectId, projectId);
        queryWrapper.in(ExtSouFile::getFileType, Arrays.asList(SouBidAttachmentTypeEnum.BID.getCode()));
        queryWrapper.orderByAsc(ExtSouFile::getSouFileId);
        List<ExtSouFile> bidAttachmentList = souFileMapper.selectList(queryWrapper);

        fileDto.setFileList(bidAttachmentList);

        LambdaQueryWrapper<ExtSouDemand> demand = new LambdaQueryWrapper<>();
        demand.eq(ExtSouDemand::getProjectId, projectId);
        demand.eq(ExtSouDemand::getStatus, SrmConstant.NUM_ZERO);
        fileDto.setMergeFlag(Integer.compare((int) demandService.count(demand), 0) == 1);

        extNpmSouOrderService.updateReadBid(projectId);

        ipMonitor(projectId, AppUserUtil.getLoginAppUser().getCompanyId(), "查看招标文件");
        return fileDto;
    }

    /**
     * 监控IP地址
     * @param projectId 参数
     * @param vendorId 参数
     * @param souce 参数
     */
    private void ipMonitor(Long projectId, Long vendorId, String souce) {
        List<ExtSouVendor> vendorList = vendorService.lambdaQuery().eq(ExtSouVendor::getProjectId, projectId)
                .eq(ExtSouVendor::getVendorId, vendorId).list();
        if(CollectionUtils.isNotEmpty(vendorList)) {
            ExtSouVendor vendor = vendorList.get(0);
            ipMonitoryCompent.ipMonitory(IpMonitoryCompent.buildParam(projectId, vendor.getVendorId(), vendor.getVendorCode(), vendor.getVendorName(), souce));
        }
    }

    @Override
    public ExtSouMarginDto getMargin(Long projectId, Long vendorId, String souType) {

        //操作权限
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouProjectVendorQueryHandler.class).doHandlerVendorAuth(projectId);

        //查询项目信息
        ExtSouProject souProject = projectService.getById(projectId);
        //查询保证金
        LambdaQueryWrapper<ExtSouMargin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouMargin::getProjectId, projectId);
        queryWrapper.eq(ExtSouMargin::getVendorId, AppUserUtil.getLoginAppUser().getCompanyId());

        List<ExtSouMargin> souMarginList = marginService.list(queryWrapper);
        marginService.copyYearMarginInfo(souMarginList);

        ExtSouMargin souMargin = CollectionUtils.isEmpty(souMarginList) ? new ExtSouMarginDto() : souMarginList.get(0);

        ExtSouMarginDto extSouMarginDto = new ExtSouMarginDto();
        BeanCopyUtil.copyProperties(extSouMarginDto, souMargin);

        //保证金缴纳金额
        extSouMarginDto.setExtEarnestAmount(souProject.getExtEarnestAmount());
        //开户银行
        extSouMarginDto.setExtBankName(souProject.getExtBankName());
        //银行联行号
        extSouMarginDto.setExtBankNumber(souProject.getExtBankNumber());
        //开户账号
        extSouMarginDto.setExtBankAccount(souProject.getExtBankAccount());
        //开户户名
        extSouMarginDto.setExtBankAccountName(souProject.getExtBankAccountName());

        //行业包后置处理
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouMarginVendorQueryHandler.class).doHandlerAfterQueryMargin(projectId, vendorId, souType, extSouMarginDto);

        return extSouMarginDto;
    }

    @Override
    public ApiExtSouNoticeDto getBidNoticeDetail(Long projectId, String souType) {

        List<BidNoticeDTO> noticeDTOList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.BidNotice.getCode())
                        .eq(BidNoticeDTO::getProjectId, projectId)
                        .eq(BidNoticeDTO::getType, CaTypeEnum.APPLY.getCode())
                        .eq(BidNoticeDTO::getStatus, CaStatusEnum.APPROVED.getCode())
                , BidNoticeDTO.class);

        if(CollectionUtils.isEmpty(noticeDTOList)) {
            return new ApiExtSouNoticeDto();
        }

        List<BidNoticeDetailDTO> noticeDetailDTOList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.BidNoticeDetail.getCode())
                        .in(BidNoticeDetailDTO::getBidNoticeId, noticeDTOList.stream().map(BidNoticeDTO::getBidNoticeId).collect(Collectors.toList()))
                        .eq(BidNoticeDetailDTO::getVendorId, AppUserUtil.getLoginAppUser().getCompanyId())
                        .eq(BidNoticeDetailDTO::getIsSend, YesOrNo.YES.getValue())
                , BidNoticeDetailDTO.class);

        ipMonitor(projectId, AppUserUtil.getLoginAppUser().getCompanyId(), "查看结果");

        return ApiExtSouNoticeDto.buildNoticeResult(noticeDetailDTOList);
    }

    /**
     * 保存并过滤需要推送电子签章的附件
     * @param orderFileList
     * @return
     */
    protected List<ExtSouOrderFile> saveAndFilterSignUpFile(ExtSouOrder souOrder, List<ExtSouOrderFile> orderFileList, String orderType) {
        List<ExtSouOrderFile> saveFileList = new ArrayList<>();
        orderFileList.stream().filter(file -> StringUtils.isNotBlank(file.getOrderFileName()) && !Objects.isNull(file.getOrderDocId())).forEach(file -> {
            if(ObjectUtils.anyNull(file.getOrderFileId())) {
                file.setOrderFileId(IdGenrator.generate());
            }
            file.setOrderId(souOrder.getOrderId());
            file.setProjectId(souOrder.getProjectId());
            file.setVendorId(souOrder.getVendorId());
            if(ObjectUtils.anyNull(file.getSouFileConfigId())) {
                file.setSouFileConfigId(IdGenrator.generate());
            }
            file.setRound(souOrder.getRound());
            if(StringUtils.isBlank(file.getExtSignStatus())) {
                file.setExtSignStatus(BidSignStatusEnum.NOT_SIGN.getCode());
            }
            file.formattingPackageListToName();
            saveFileList.add(file);
        });
        List<String> fileTypeList = Arrays.asList(ExtSouFileConfigTypeEnum.TECH_BID.getCode(), ExtSouFileConfigTypeEnum.TECH_SOLUTION_BID.getCode(), ExtSouFileConfigTypeEnum.TECH_OTHER.getCode(), ExtSouFileConfigTypeEnum.TECH_QUA_PERF.getCode());
        if(ApiExtSouSignEditDto.TYPE_BID_BUSINESS.equals(orderType)) {
            fileTypeList = Arrays.asList(ExtSouFileConfigTypeEnum.BUS_BID.getCode(), ExtSouFileConfigTypeEnum.BUS_OTHER.getCode());
        }
        orderFileService.remove(new LambdaQueryWrapper<ExtSouOrderFile>()
                .eq(ExtSouOrderFile::getProjectId, souOrder.getProjectId())
                .eq(ExtSouOrderFile::getOrderId, souOrder.getOrderId())
                .eq(ExtSouOrderFile::getRound, souOrder.getRound())
                .in(ExtSouOrderFile::getFileType, fileTypeList)
                .notIn(CollectionUtils.isNotEmpty(saveFileList), ExtSouOrderFile::getOrderFileId, saveFileList.stream().map(o -> o.getOrderFileId()).collect(Collectors.toList()))
        );
        if(CollectionUtils.isNotEmpty(saveFileList)) {
            orderFileService.saveOrUpdateBatch(saveFileList);
        }
        //签署文件：技术标和报价文件
        return saveFileList.stream().filter(file -> Arrays.asList(ExtSouFileConfigTypeEnum.TECH_BID.getCode(), ExtSouFileConfigTypeEnum.BUS_BID.getCode()).contains(file.getFileType()) && !BidSignStatusEnum.SIGN.getCode().equals(file.getExtSignStatus())).collect(Collectors.toList());
    }

    @Override
    public ApiExtSignDto getSign(ApiExtSouSignEditDto param, String souType) {
        ExtSouOrder souOrder = extSouOrderMapper.selectById(param.getOrderId());
        AssertUtils.notNull(souOrder, "投标信息为空");

        if(CollectionUtils.isEmpty(param.getSignFileList())) {
            throw new BaseException("签署文件为空");
        }

        if(Long.compare(souOrder.getVendorId(), AppUserUtil.getLoginAppUser().getCompanyId()) != 0) {
            throw new BaseException("禁止操作其他供应商投标文件");
        }

        LambdaQueryWrapper<ExtSouVendor> vendorQuery = new LambdaQueryWrapper<>();
        vendorQuery.eq(ExtSouVendor::getProjectId, souOrder.getProjectId());
        vendorQuery.eq(ExtSouVendor::getVendorId, souOrder.getVendorId());
        List<ExtSouVendor> vendorList = vendorService.list(vendorQuery);
        if(CollectionUtils.isEmpty(vendorList)) {
            throw new BaseException("供应商信息缺失");
        }

        List<ExtSouOrderFile> signUpFileList = saveAndFilterSignUpFile(souOrder, param.getSignFileList(),param.getOrderType());
        if(CollectionUtils.isEmpty(signUpFileList)) {
            throw new BaseException("缺少签署文件[文件类型为投标文件或报价文件]！");
        }

        ExtSouVendor vendor = vendorList.get(0);

        ExtSouOrderFile signFile = buildSignFile(param, souOrder);
        orderFileService.save(signFile);

        ApiExtSignDto signDto = new ApiExtSignDto();
        signDto.setFileIdList(signUpFileList.stream().map(f->f.getOrderDocId()).collect(Collectors.toList()));
        if(ApiExtSouSignEditDto.TYPE_BID_TECH.equals(param.getOrderType())) {
            signDto.setTitle(StringUtils.joinWith("_", "投标文件线上签署", souOrder.getOrderNo()));
        } else {
            signDto.setTitle(StringUtils.joinWith("_", "商务报价线上签署", souOrder.getOrderNo()));
        }

        signDto.setOrderType(param.getOrderType());
        signDto.setOrderId(signFile.getOrderFileId());
        signDto.setSignatoryList(new ArrayList<>());
        signDto.getSignatoryList().add(new ApiExtSignatoryDto());
        signDto.getSignatoryList().get(0).setTenantName(vendor.getVendorName());
        signDto.getSignatoryList().get(0).setContact(AppUserUtil.getLoginAppUser().getUsername());

        return signDto;
    }

    protected ExtSouOrderFile buildSignFile(ApiExtSouSignEditDto param, ExtSouOrder souOrder) {
        ExtSouOrderFile extSouOrderFile = new ExtSouOrderFile();
        extSouOrderFile.setOrderFileId(IdGenrator.generate());
        extSouOrderFile.setFileType(ExtSouFileConfigTypeEnum.SIGN_TODO.getCode());
        extSouOrderFile.setProjectId(souOrder.getProjectId());
        extSouOrderFile.setVendorId(souOrder.getVendorId());
        extSouOrderFile.setOrderId(souOrder.getOrderId());
        extSouOrderFile.setSouFileConfigId(IdGenrator.generate());
        extSouOrderFile.setOrderDocId(-1L);
        extSouOrderFile.setOrderFileName("null");
        extSouOrderFile.setRound(souOrder.getRound());
        extSouOrderFile.setExtSignStatus(BidSignStatusEnum.NOT_SIGN.getCode());
        return extSouOrderFile;
    }

    @Override
    public String pushSgin(ApiExtSouSignEditDto param, String souType) {
        ApiExtSignDto signDto = getSign(param, souType);
        return pjSouClient.contractSigningByUrl(signDto.toJsonObject());
    }
}
