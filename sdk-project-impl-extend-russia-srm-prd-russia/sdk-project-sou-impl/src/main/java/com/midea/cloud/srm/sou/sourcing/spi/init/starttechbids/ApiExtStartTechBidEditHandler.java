package com.midea.cloud.srm.sou.sourcing.spi.init.starttechbids;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.constant.SmsConstant;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.sms.SmsClient;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Payload;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.core.core.QlDispatcher;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.base.service.NoticeSendGlobalClientService;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.base.noticetemplate.dto.NoticeSendDTO;
import com.midea.cloud.srm.model.base.noticetemplate.entity.NoticeTemplate;
import com.midea.cloud.srm.model.base.noticetemplate.enums.NoticeTemplateCodeEnum;
import com.midea.cloud.srm.model.base.noticetemplate.enums.NoticeTemplateModeEnum;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.rbac.ExtUserPermissionDTO;
import com.midea.cloud.srm.model.sou.enums.*;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouMarginDto;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorDto;
import com.midea.cloud.srm.model.sou.req.SouInviteHead;
import com.midea.cloud.srm.model.sou.req.SouInviteItem;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.req.enums.IntDepositRefundStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTechScoreStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouWinStatusEnum;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.req.repo.InviteHeadBuyerRepository;
import com.midea.cloud.srm.sou.sourcing.init.service.*;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderItemService;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class ApiExtStartTechBidEditHandler implements ISouSpiBean {

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private IExtSouOrderService orderService;

    @Autowired
    private IExtSouTechScoreHeadService techScoreHeadService;

    @Autowired
    private IExtSouTechScoreLineService techScoreLineService;

    @Autowired
    private IExtSouVendorService vendorService;

    @Autowired
    private IExtSouGroupService groupService;

    @Autowired
    private IExtSouItemService itemService;

    @Autowired
    private IExtSouPlanService planService;

    @Autowired
    private IExtSouRoundService roundService;

    @Autowired
    private IExtSouOrderItemService orderItemService;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private IExtSouScoreRuleService scoreRuleService;

    @Autowired
    private IExtSouMarginService marginService;

    @Autowired
    private QlOpenClient qlOpenClient;

    @Autowired
    private QlService qlService;

    @Autowired
    private QlDispatcher qlDispatcher;

    @Autowired
    private IExtNpmSouOrderService extNpmSouOrderService;

    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    @Autowired
    private NoticeSendGlobalClientService noticeSendGlobalClientService;


    private static final String SEQ_NO = "SEQ_INQ_QUOTENO_CODE";

    @Value("${global.srm.register-address:没有配置地址}")
    private String cloudUrl;

    /**
     * 【有价废弃物】/【固定资产处置】
     */
    private static final List<String> MARGIN_CATEGORY = Arrays.asList("有价废弃物", "固定资产处置");
    /**
     * 内部供应商
     */
    private static final String INNER_VENDOR_ACCOUNT_GROUP = "J003";

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * 校验和转换技术标PO类
     *
     * @param projectId
     * @param souType
     * @return
     */
    public ExtStartTechBidEditPO doFormatevalidAndConvert(Long projectId, String souType) {
        //校验
        this.doFormatevalid(projectId, souType);
        //转化PO类
        return this.doConvert(projectId, souType);
    }

    protected void doFormatevalid(Long projectId, String souType) {

    }

    public ExtStartTechBidEditPO doExtendAndValidVendor(ExtSouProject project, List<RecommvendorDto> recommvendorDtoList, String souType) {
        ExtStartTechBidEditPO po = new ExtStartTechBidEditPO();
        po.setSouProject(project);
        if(doExtendValid(project)) {
           this.doExtendVendor(project, recommvendorDtoList, souType, po);
        }
        return po;
    }

    protected void doExtendVendor(ExtSouProject project, List<RecommvendorDto> recommvendorDtoList, String souType, ExtStartTechBidEditPO po) {
        if(CollectionUtils.isEmpty(recommvendorDtoList)) {
            return;
        }
        //查询供应商
        List<ExtSouVendor> extSouVendorList = vendorService.lambdaQuery().eq(ExtSouVendor::getProjectId, project.getProjectId())
                .list();
        List<Long> vendorIdList = extSouVendorList.stream().map(v -> v.getVendorId()).distinct().collect(Collectors.toList());
        Integer maxIndex = extSouVendorList.stream().map(ExtSouVendor::getSortIndex).max(Comparator.comparingInt(o -> o)).get();
        AtomicInteger sortIndex = new AtomicInteger(maxIndex+1);
        //生成供应商数据
        List<ExtSouVendor> appendVendorList = new ArrayList<>();
        recommvendorDtoList.stream().forEach(recommvendorDto -> {
            if(vendorIdList.contains(recommvendorDto.getVendorId())) {
                return;
            }
            vendorIdList.add(recommvendorDto.getVendorId());
            ExtSouVendor extSouVendor = new ExtSouVendor();
            BeanCopyUtil.copyProperties(extSouVendor, recommvendorDto);
            extSouVendor.setSouVendorId(IdGenrator.generate());
            extSouVendor.setProjectId(project.getProjectId());
            extSouVendor.setSortIndex(ObjectUtils.defaultIfNull(sortIndex.getAndAdd(1), 0));

            appendVendorList.add(extSouVendor);
        });

        po.setVendorList(appendVendorList);

        //生成投标主表
        po.setSouOrderList(this.doConvertSouOrder(project, appendVendorList, souType));

        //生成投标明细
        List<ExtSouItem> itemList = itemService.lambdaQuery().eq(ExtSouItem::getProjectId, project.getProjectId()).list();
        po.setSouOrderItemList(this.doConvertSouOrderItem(project.getProjectId(), po.getSouOrderList(), itemList));

        //生成保证金
        po.setSouMarginList(this.doConvertMargin(project, appendVendorList));

    }

    protected boolean doExtendValid(ExtSouProject project) {

        //大于第一轮，不允许追加供应商
        if(Integer.compare(ObjectUtils.defaultIfNull(project.getCurrentRound(), 1), 1) == 1) {
            return false;
        }

        //投标中的状态才允许追加供应商
        if(!Arrays.asList(SouBiddingProStatusEnum.TECH_BID.getCode(), SouBiddingProStatusEnum.TECH_BID_END.getCode(),
                SouBiddingProStatusEnum.BUS_BID.getCode(), SouBiddingProStatusEnum.BUS_BID_END.getCode()).contains(project.getProjectStatus())) {
            return false;
        }
        //第一轮技术标开标前，或者第一轮商务标开标前
        //商务标中，判断是否存在技术标，如果存在技术标，则不允许追加供应商
        if(Arrays.asList(SouBiddingProStatusEnum.BUS_BID.getCode(), SouBiddingProStatusEnum.BUS_BID_END.getCode()).equals(project.getProjectStatus())) {
            //查询投标表
            List<ExtSouOrder> orderList = orderService.lambdaQuery().eq(ExtSouOrder::getProjectId, project.getProjectId()).list();
            //存在技术标
            Boolean techFlag = orderList.stream().filter(o -> YesOrNo.YES.getValue().equals(o.getExtTechFlag())).findAny().isPresent();
            //已经开始商务标
            Boolean busFlag = orderList.stream().filter(o -> ExtOrderTypeEnum.BUS.getCode().equals(o.getExtOrderType())).findAny().isPresent();
            if(techFlag && busFlag) {
                //已进行过技术开标，不允许追加供应商
                return false;
            }
        }

        return true;
    }

    protected ExtStartTechBidEditPO doConvert(Long projectId, String souType) {
        ExtStartTechBidEditPO po = new ExtStartTechBidEditPO();

        ExtSouProject souProject = projectService.getById(projectId);
        AssertUtils.notNull(souProject, "项目信息不存在");

        //标准招标 且 先收技术标后收商务标
        if (SouBidProccessEnum.STANDARD.getCode().equals(souProject.getExtSouProcess()) && SouBidSouModeEnum.TECH_THEN_BUS.getCode().equals(souProject.getExtSouMode())) {
            souProject.setProjectStatus(SouBiddingProStatusEnum.TECH_BID.getCode());
        } else {
            //其他情况都是投标中
            souProject.setProjectStatus(SouBiddingProStatusEnum.BUS_BID.getCode());
        }

        souProject.setCurrentRound(1);

        po.setSouProject(souProject);

        //查询邀请的供应商
        LambdaQueryWrapper<ExtSouVendor> vendorQuery = new LambdaQueryWrapper<>();
        vendorQuery.eq(ExtSouVendor::getProjectId, projectId);
        vendorQuery.orderByAsc(ExtSouVendor::getVendorId);
        List<ExtSouVendor> vendorList = vendorService.list(vendorQuery);

        //查询报价信息
        LambdaQueryWrapper<ExtSouItem> itemQuery = new LambdaQueryWrapper<>();
        itemQuery.eq(ExtSouItem::getProjectId, projectId);
        List<ExtSouItem> itemList = itemService.list(itemQuery);

        //查询计划
        LambdaQueryWrapper<ExtSouPlan> planQuery = new LambdaQueryWrapper<>();
        planQuery.eq(ExtSouPlan::getProjectId, projectId);
        planQuery.eq(ExtSouPlan::getPlanType, SouBidPlanTypeEnum.PLAN.getCode());

        List<ExtSouPlan> souPlanList = planService.list(planQuery);
        ExtSouPlan plan = new ExtSouPlan();
        if (CollectionUtils.isNotEmpty(souPlanList)) {
            plan = souPlanList.get(0);
        }

        //生成轮次
        po.setRound(this.doConvertSouRound(projectId, souType));
        po.getRound().setOrderStartTime(new Date());

        if (SouBiddingProStatusEnum.TECH_BID.getCode().equals(souProject.getProjectStatus())) {
            po.getRound().setOrderEndTime(plan.getTechEndTime());
        } else {
            po.getRound().setOrderEndTime(plan.getBusEndTime());
        }


        //生成报价单
        po.setSouOrderList(this.doConvertSouOrder(souProject, vendorList, souType));

        //生成报价明细
        po.setSouOrderItemList(this.doConvertSouOrderItem(projectId, po.getSouOrderList(), itemList));

        //生成技术标
        //生成保证金
        po.setSouMarginList(this.doConvertMargin(po.getSouProject(), vendorList));

        return po;
    }

    //供应商账户组 accountGroup

    /**
     * 是否缴纳：供应商缴纳保证金后资金系统核实后，返回缴纳状态。
     * 手工发起退回。
     * 列表数据：默认所有推荐的供应商清单。
     * 已退款：资金系统退款成功返回状态。
     * 退款失败：资金系统返回的退款失败状态。
     * 未退款：缴纳保证金成功后状态。
     * 不交保证金，默认不涉及。
     * 可退款=缴纳金额-扣款金额。
     * 如果是内部供应商，可以不需要缴纳保证金，默认否，但可以直接投标。（判断内部供应商：供应商主数据的账户组字段值为J003）
     * 年度保证金：检查品类是否为：【有价废弃物】/【固定资产处置】两个品类，如果是，则所有投标的供应商直接查年度保证金功能数据，若有【未退回】，默认为年度保证金供应商，可不缴纳保证金，直接投标。
     * 若供应商缴纳保证金品类为【有价废弃物】/【固定资产处置】这两个品类，且缴纳成功时，自动将该保证金自动添加至年度保证金中。
     * 【有价废弃物】/【固定资产处置】这两个品类的招标项目，不可退回保证金，需在年度保证金中退回。
     *
     * @return
     */
    protected List<ExtSouMargin> doConvertMargin(ExtSouProject souProject, List<ExtSouVendor> vendorList) {
        if (CollectionUtils.isEmpty(vendorList)) {
            return new ArrayList<>();
        }
        /** 查询年度保证金字典配置信息 */
        List<DictItem> yearMarginList = baseClient.listDictItemByDictCode(DictCodeEnum.SOU_BID_YEAR_MARGIN.getCode());

        Map<String, DictItem> yearMarginMap = new HashMap<>(16);
        if(CollectionUtils.isNotEmpty(yearMarginList)) {
            yearMarginList.stream().forEach(dict -> yearMarginMap.put(dict.getDictItemCode(), dict));
        }

        //List<Record> companyInfos = qlService.queryByWrapper(QlWrappers.query(MqlType.SUPPLIER)
        //                .eq("companyId", companyId), Record.class)
        List<RecordDTO> vendorRecordDtoList = qlOpenClient.query(ContextPath.SUP, QlOpenWrappers.query(MqlType.SUPPLIER).in("companyId", vendorList.stream().map(v -> v.getVendorId()).collect(Collectors.toList())));
        Map<Long, RecordDTO> vendorRecordMap = vendorRecordDtoList.stream().collect(Collectors.toMap(r -> r.getLong("companyId"), Function.identity(), (k1, k2) -> k2));

        LambdaQueryWrapper<ExtSouMargin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouMargin::getProjectId, souProject.getProjectId());
        List<ExtSouMargin> existList = marginService.list(queryWrapper);
        Map<Long, ExtSouMargin> existMap = existList.stream().collect(Collectors.toMap(e -> e.getVendorId(), Function.identity(), (k1, k2) -> k2));

        //年度保证金
        String yearFlag = YesOrNo.NO.getValue();
        Map<String, ExtSouMargin> yearMargin = new HashMap<>(16);
        if (yearMarginMap.containsKey(souProject.getExtCategoryCode())) {
            yearFlag = YesOrNo.YES.getValue();
            yearMargin = queryYearMargin(yearMarginMap, souProject, vendorList);
        }

        List<ExtSouMargin> souMarginList = new ArrayList<>();
        if (YesOrNo.YES.getValue().equals(souProject.getExtEarnestFlag())) {
            String finalYearFlag = yearFlag;
            Map<String, ExtSouMargin> finalYearMargin = yearMargin;
            vendorList.stream().forEach(v -> {
                ExtSouMargin souMargin = new ExtSouMargin();
                if (existMap.containsKey(v.getVendorId())) {
                    souMargin = existMap.get(v.getVendorId());
                } else {
                    souMargin.setMarginId(IdGenrator.generate());
                }
                souMargin.setVendorId(v.getVendorId());
                souMargin.setVendorCode(v.getVendorCode());
                souMargin.setVendorName(v.getVendorName());
                souMargin.setProjectId(souProject.getProjectId());
                souMargin.setSourceProjectId(souProject.getProjectId());
                souMargin.setPayFlag(YesOrNo.NO.getValue());
                souMargin.setCategoryId(souProject.getExtCategoryId());
                souMargin.setCategoryCode(souProject.getExtCategoryCode());
                souMargin.setCategoryName(souProject.getExtCategoryName());
                souMargin.setYearFlag(finalYearFlag);
                //内部供应商
                String innerFlag = YesOrNo.NO.getValue();
                String accountGroup = "accountGroup";
                if (vendorRecordMap.containsKey(v.getVendorId()) && INNER_VENDOR_ACCOUNT_GROUP.equals(vendorRecordMap.get(v.getVendorId()).getString(accountGroup))) {
                    innerFlag = YesOrNo.YES.getValue();
                }
                souMargin.setInnerFlag(innerFlag);

                souMargin.setMarginStatus(SouBidMarginStatusEnum.NOT_PAY.getCode());

                souMargin.setRelYearMarginId(SrmConstant.LONG_MINUS_ONE);

                extendYearMargin(souMargin, finalYearMargin, yearMarginMap.get(souProject.getExtCategoryCode()), souMarginList);
                souMarginList.add(souMargin);
            });
        }
        return souMarginList;
    }

    private void extendYearMargin(ExtSouMargin souMargin, Map<String, ExtSouMargin> yearMarginMap, DictItem yearMarginDict, List<ExtSouMargin> souMarginList) {
        if(!YesOrNo.YES.getValue().equals(souMargin.getYearFlag())) {
            return;
        }
        String key = yearMarginKey(souMargin);
        if(yearMarginMap.containsKey(key)) {
            souMargin.setRelYearMarginId(yearMarginMap.get(key).getMarginId());
        } else {
            ExtSouMargin yearSouMargin = new ExtSouMarginDto();
            yearSouMargin.setMarginId(IdGenrator.generate());
            yearSouMargin.setRelYearMarginId(SrmConstant.LONG_MINUS_ONE);
            souMargin.setRelYearMarginId(yearSouMargin.getMarginId());
            yearSouMargin.setProjectId(SrmConstant.LONG_MINUS_ONE);
            yearSouMargin.setSourceProjectId(souMargin.getProjectId());
            yearSouMargin.setVendorId(souMargin.getVendorId());
            yearSouMargin.setVendorCode(souMargin.getVendorCode());
            yearSouMargin.setVendorName(souMargin.getVendorName());
            yearSouMargin.setCategoryId(souMargin.getCategoryId());
            yearSouMargin.setCategoryCode(souMargin.getCategoryCode());
            yearSouMargin.setCategoryName(souMargin.getCategoryName());
            yearSouMargin.setPayFlag(YesOrNo.NO.getValue());
            yearSouMargin.setInnerFlag(souMargin.getInnerFlag());
            yearSouMargin.setMarginStatus(SouBidMarginStatusEnum.NOT_PAY.getCode());
            yearSouMargin.setYearFlag(YesOrNo.YES.getValue());

            /*if(!Objects.isNull(yearMarginDict)) {
                yearSouMargin.setPayAmount(strToBigDecimal(yearMarginDict.getItemDescription()));
                yearSouMargin.setRefundAmount(yearSouMargin.getPayAmount());
            }*/

            yearMarginMap.put(key, yearSouMargin);
            souMarginList.add(yearSouMargin);
        }
    }

    private BigDecimal strToBigDecimal(String decimalStr) {
        if(StringUtils.isNotBlank(decimalStr)) {
            try {
                return new BigDecimal(decimalStr);
            } catch (Exception e) {
                log.error("strToBigDecimal Exception", e);
            }
        }
        return null;
    }

    private String yearMarginKey(ExtSouMargin margin) {
        return StringUtils.joinWith(SrmConstant.UNDER_LINE, margin.getCategoryCode(), margin.getVendorId());
    }

    private Map<String, ExtSouMargin> queryYearMargin(Map<String, DictItem> yearMarginMap, ExtSouProject souProject, List<ExtSouVendor> vendorList) {
        if(MapUtils.isEmpty(yearMarginMap) || CollectionUtils.isEmpty(vendorList)) {
            return new HashMap<>(16);
        }
        LambdaQueryWrapper<ExtSouMargin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouMargin::getProjectId, SrmConstant.LONG_MINUS_ONE);
        queryWrapper.eq(ExtSouMargin::getYearFlag, YesOrNo.YES.getValue());
        queryWrapper.in(ExtSouMargin::getVendorId, vendorList.stream().map(v -> v.getVendorId()).distinct().collect(Collectors.toList()));
        queryWrapper.eq(ExtSouMargin::getCategoryCode, souProject.getExtCategoryCode());
        queryWrapper.and(a -> a.isNull(ExtSouMargin::getRefundStatus).or(o -> o.notIn(ExtSouMargin::getRefundStatus, Arrays.asList(IntDepositRefundStatusEnum.REFUNDING.getCode(), IntDepositRefundStatusEnum.REFUNDED.getCode()))));
        queryWrapper.orderByDesc(ExtSouMargin::getMarginId);
        List<ExtSouMargin> souMarginList = marginService.list(queryWrapper);

        Map<String, ExtSouMargin> yearMargin = new HashMap<>(16);
        souMarginList.stream().forEach(margin -> {
            String key = yearMarginKey(margin);
            if(!yearMargin.containsKey(key)) {
                yearMargin.put(key, margin);
            }
        });

        return yearMargin;
    }

    protected List<ExtSouTechScoreHead> doConvertTechScoreHead(Long projectId, List<ExtSouOrder> orderList, List<ExtSouGroup> groupList) {
        //校验是否有维护评分规则，无评分规则时不生成评审单据
        LambdaQueryWrapper<ExtScoreRule> ruleQuery = new LambdaQueryWrapper<>();
        ruleQuery.eq(ExtScoreRule::getProjectId, projectId);
        Integer count = Math.toIntExact(scoreRuleService.count(ruleQuery));
        if (Integer.compare(count, 0) <= 0) {
            return new ArrayList<>();
        }

        //查询技术投标头表
        LambdaQueryWrapper<ExtSouTechScoreHead> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouTechScoreHead::getProjectId, projectId);

        List<ExtSouTechScoreHead> techScoreHeadList = techScoreHeadService.list(queryWrapper);

        Map<String, ExtSouTechScoreHead> techScoreHeadMap = techScoreHeadList.stream().collect(Collectors.toMap(t -> StringUtils.joinWith("_", t.getVendorId(), t.getGroupId()), Function.identity(), (k1, k2) -> k2));

        List<ExtSouTechScoreHead> saveList = new ArrayList<>();
        groupList.stream().filter(g -> SouScoreDimensionCodeEnum.SOU_TECH.getCode().equals(g.getScoreAuth())).forEach(g -> {
            String key = StringUtils.joinWith("_", -1L, g.getGroupId());
            ExtSouTechScoreHead techScoreHead = techScoreHeadMap.get(key);
            if (Objects.isNull(techScoreHead)) {
                techScoreHead = new ExtSouTechScoreHead();
                techScoreHead.setTechScoreHeadId(IdGenrator.generate());
                techScoreHead.setProjectId(projectId);
                techScoreHead.setVendorId(-1L);
                techScoreHead.setGroupId(g.getGroupId());
                techScoreHead.setIsProxy(Enable.N.name());
            }
            techScoreHead.setOrderId(-1L);
            techScoreHead.setScoreStatus(SouTechScoreStatusEnum.UNFINISHED.name());
            saveList.add(techScoreHead);
        });
        return saveList;
    }

    protected ExtSouRound doConvertSouRound(Long projectId, String souType) {
        LambdaQueryWrapper<ExtSouRound> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouRound::getProjectId, projectId);
        queryWrapper.eq(ExtSouRound::getRound, 1);
        queryWrapper.orderByDesc(ExtSouRound::getRound);
        List<ExtSouRound> roundList = roundService.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(roundList)) {
            return roundList.get(0);
        }
        ExtSouRound souRound = new ExtSouRound();
        souRound.setProjectId(projectId);
        souRound.setRoundId(IdGenrator.generate());
        souRound.setRound(1);
        souRound.setHasPublishResult(Enable.N);
        souRound.setBusinessOpen(Enable.N);
        souRound.setPriceDecrypt(Enable.N);

        return souRound;
    }

    protected List<ExtSouOrderItem> doConvertSouOrderItem(Long projectId, List<ExtSouOrder> souOrderList, List<ExtSouItem> itemList) {
        LambdaQueryWrapper<ExtSouOrderItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouOrderItem::getProjectId, projectId);
        queryWrapper.eq(ExtSouOrderItem::getRound, 1);

        List<ExtSouOrderItem> orderItemList = orderItemService.list(queryWrapper);
        Map<String, ExtSouOrderItem> orderItemMap = orderItemList.stream().collect(Collectors.toMap(i -> StringUtils.joinWith("_", i.getVendorId(), i.getSouItemId()), Function.identity(), (k1, k2) -> k2));

        List<ExtSouOrderItem> saveList = new ArrayList<>();
        souOrderList.stream().forEach(o -> {
            itemList.stream().forEach(i -> {
                String key = StringUtils.joinWith("_", o.getVendorId(), i.getSouItemId());

                if (orderItemMap.containsKey(key)) {
                    ExtSouOrderItem orderItem = orderItemMap.get(key);
                    orderItem.setOrderStatus(o.getOrderStatus());
                    saveList.add(orderItem);
                } else {
                    ExtSouOrderItem orderItem = new ExtSouOrderItem();
                    BeanCopyUtil.copyProperties(orderItem, i);
                    if (Objects.isNull(orderItem.getNoCodeItem())) {
                        orderItem.setNoCodeItem(Enable.Y);
                    }
                    if (Objects.isNull(orderItem.getIsProxy())) {
                        orderItem.setIsProxy(Enable.N);
                    }
                    if (Objects.isNull(orderItem.getWinStatus())) {
                        orderItem.setWinStatus(SouWinStatusEnum.D);
                    }

                    if (Objects.isNull(orderItem.getOrderItemId())) {
                        orderItem.setOrderItemId(IdGenrator.generate());
                    }
                    orderItem.setOrderId(o.getOrderId());
                    orderItem.setOrderStatus(o.getOrderStatus());
                    orderItem.setVendorId(o.getVendorId());
                    orderItem.setProjectId(o.getProjectId());
                    orderItem.setRound(o.getRound());

                    saveList.add(orderItem);
                }
            });
        });

        return saveList;
    }

    protected List<ExtSouOrder> doConvertSouOrder(ExtSouProject project, List<ExtSouVendor> vendorList, String souType) {
        LambdaQueryWrapper<ExtSouOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouOrder::getProjectId, project.getProjectId());

        List<ExtSouOrder> orderList = orderService.list(queryWrapper);
        Map<Long, ExtSouOrder> orderMap = orderList.stream().collect(Collectors.toMap(o -> o.getVendorId(), Function.identity(), (k1, k2) -> k2));

        String techFlag = YesOrNo.NO.getValue();

        if (Arrays.asList(SouBiddingProStatusEnum.TECH_BID.getCode(), SouBiddingProStatusEnum.TECH_BID_END.getCode(), SouBiddingProStatusEnum.TECH_BID_OPEN.getCode(),
                SouBiddingProStatusEnum.TECH_BID_EVA.getCode(), SouBiddingProStatusEnum.TECH_BID_EVA_DONE.getCode()).contains(project.getProjectStatus())) {
            techFlag = YesOrNo.YES.getValue();
        }

        List<ExtSouOrder> souOrderList = new ArrayList<>();
        String finalTechFlag = techFlag;
        vendorList.stream().forEach(v -> {
            if (orderMap.containsKey(v.getVendorId())) {
                ExtSouOrder souOrder = orderMap.get(v.getVendorId());
                souOrder.setOrderStatus(SouOrderStatusEnum.DRAFT);
                if (StringUtils.isBlank(souOrder.getExtTechFlag())) {
                    souOrder.setExtTechFlag(finalTechFlag);
                }
                if(YesOrNo.YES.getValue().equals(finalTechFlag)) {
                    souOrder.setExtOrderType(ExtOrderTypeEnum.TECH.getCode());
                } else {
                    souOrder.setExtOrderType(ExtOrderTypeEnum.BUS.getCode());
                }
                souOrderList.add(souOrder);
            } else {
                ExtSouOrder souOrder = new ExtSouOrder();
                souOrder.setProjectId(project.getProjectId());
                souOrder.setRound(1);
                souOrder.setVendorId(v.getVendorId());
                //创建ID
                souOrder.setOrderId(IdGenrator.generate());
                //创建单号
                souOrder.setOrderNo(baseClient.seqGen(SEQ_NO));
                //报价单状态：字典 SOU_ORDER_STATUS
                souOrder.setOrderStatus(SouOrderStatusEnum.DRAFT);
                souOrder.setIsProxy(Enable.N);
                if (StringUtils.isBlank(souOrder.getExtTechFlag())) {
                    souOrder.setExtTechFlag(finalTechFlag);
                }
                if(YesOrNo.YES.getValue().equals(finalTechFlag)) {
                    souOrder.setExtOrderType(ExtOrderTypeEnum.TECH.getCode());
                } else {
                    souOrder.setExtOrderType(ExtOrderTypeEnum.BUS.getCode());
                }
                souOrderList.add(souOrder);
            }
        });


        return souOrderList;
    }

    protected Boolean sendMsg(ExtSouProject souProject, ExtSouVendor souVendor, SmsClient smsClient) {
        try {
            if(StringUtils.isBlank(souVendor.getPhone()) || YesOrNo.YES.getValue().equals(souVendor.getExtSendMsmFlag())) {
                return false;
            }
            Map<String, String> var = new HashMap<>(15);

            var.put("${souProject.souName}",souProject.getSouName());
            var.put("${souProject.souNo}",souProject.getExtProjectNo());
            var.put("${souProject.linkMan}",souProject.getLinkman());
            var.put("${souProject.tel}",souProject.getTel());
            return smsClient.sendSms(souVendor.getPhone(), SmsConstant.SOU_PROJECT_PUBLIC, var);
        } catch (Exception e) {
            log.error("sendMsg Exception", e);
        }
        return false;
    }

    /**
     * 给供应商发送短信
     * @param souProject
     * @param vendorList
     */
    protected void sendMsmToVendor(ExtSouProject souProject, List<ExtSouVendor> vendorList) {
        //已发送短信的供应商
        List<ExtSouVendor> vendorSendForUpdateList = new ArrayList<>();

        SmsClient smsClient = SmsClient.newInstance(baseClient, pjProjectExtClient);

        for(ExtSouVendor vendor : vendorList) {
            if(sendMsg(souProject, vendor, smsClient)) {
                vendor.setExtSendMsmFlag(YesOrNo.YES.getValue());
                vendorSendForUpdateList.add(vendor);
            }
        }

        if(CollectionUtils.isNotEmpty(vendorSendForUpdateList)) {
            vendorService.updateBatchById(vendorSendForUpdateList);
        }

    }

    /**
     * 给供应商发送邮件
     * @param souProject
     * @param vendorList
     */
    protected void sendEmailToVendor(ExtSouProject souProject, List<ExtSouVendor> vendorList) {
        for(ExtSouVendor vendor : vendorList) {
            NoticeSendDTO noticeSendDTO = new NoticeSendDTO();
            noticeSendDTO.setMsgTemplateCode("PJ_BIDDING_PASS_VENDOR_NOTICE");
            noticeSendDTO.setMsgUuid(UUID.randomUUID().toString());
            Map<String, Object> msgParams = new HashMap(15);
            msgParams.put(NoticeSendDTO.NOTICE_RECEIVER_INFO, vendor.getEmail());
            msgParams.put("projectName", souProject.getSouName());
            msgParams.put("baseUrl", cloudUrl);
            noticeSendDTO.setMsgParams(msgParams);
            noticeSendGlobalClientService.send(noticeSendDTO);
        }
    }

    @SneakyThrows(value = {Exception.class})
    public void doHandlerSouProjectAsAfterFlowPass(Long projectId, String souType, ExtStartTechBidEditPO po) {
        //生成投标扩展表信息
        extNpmSouOrderService.extendSouOrder(po.getSouOrderList());

        List<ExtSouVendor> vendorList = vendorService.lambdaQuery().eq(ExtSouVendor::getProjectId, projectId).groupBy(ExtSouVendor::getVendorId).list();
        if (CollectionUtils.isEmpty(vendorList)) {
            return;
        }
//        String SOU_INVITE_ITEM_BUYER = MqlType.SOU_INVITE_ITEM_BUYER;
        List<SouInviteHead> inviteHeadList = qlService.queryByWrapper(QlWrappers.query(MqlType.SOU_INVITE_HEAD_BUYER).in(SouInviteHead::getVendorId, vendorList.stream().map(v -> v.getVendorId()).collect(Collectors.toList())), SouInviteHead.class);
        Map<Long, SouInviteHead> inviteHeadMap = inviteHeadList.stream().collect(Collectors.toMap(SouInviteHead::getVendorId, Function.identity(), (k1, k2)->k2));

        List<SouInviteHead> saveOrUpdateList = new ArrayList<>();
        for(ExtSouVendor souVendor : vendorList) {
            SouInviteHead inviteHead = builderSouInviteHead(souVendor);
            if(inviteHeadMap.containsKey(inviteHead.getVendorId())) {
                SouInviteHead souInviteHead = new SouInviteHead();
                BeanCopyUtil.copyProperties(souInviteHead, inviteHead, true);
                saveOrUpdateList.add(souInviteHead);
            } else {
                saveOrUpdateList.add(inviteHead);
            }
        }

        QlQueryAction queryAction = QlQueryAction.builder().build();
        queryAction.setType(MqlType.SOU_INVITE_HEAD_BUYER);
        queryAction.setAction("saveOrUpdate");
        queryAction.setPayload(saveOrUpdateList);
        queryAction.setDoProxy(true);

        qlDispatcher.execute(queryAction);

        //生成明细
        this.createInviteItem(po.getSouProject(), vendorList);

        //标书发布且发送短信
        sendMsmToVendor(po.getSouProject(), vendorList);
        //标书发布且发送邮件
        sendEmailToVendor(po.getSouProject(), vendorList);
    }

    public void createInviteItem(ExtSouProject souProject, List<ExtSouVendor> vendorList) throws Exception {
        // com.midea.cloud.srm.model.sou.req.constants.MqlType.SOU_INVITE_ITEM_BUYER
        List<SouInviteHead> inviteHeadList = qlService.queryByWrapper(QlWrappers.query(MqlType.SOU_INVITE_HEAD_BUYER).in(SouInviteHead::getVendorId, vendorList.stream().map(v -> v.getVendorId()).collect(Collectors.toList())), SouInviteHead.class);
        Map<Long, SouInviteHead> inviteHeadMap = inviteHeadList.stream().collect(Collectors.toMap(SouInviteHead::getVendorId, Function.identity(), (k1, k2)->k2));

        //查询明细---
        List<SouInviteItem> inviteItemExistisList = qlService.queryByWrapper(QlWrappers.query(MqlType.SOU_INVITE_ITEM_BUYER)
                .in(SouInviteItem::getInviteHeadId, inviteHeadList.stream().map(h -> h.getInviteHeadId()).collect(Collectors.toList()))
                .eq(SouInviteItem::getProjectNo, souProject.getExtProjectNo()), SouInviteItem.class);
        Map<Long, SouInviteItem> itemMap = inviteItemExistisList.stream().collect(Collectors.toMap(k -> k.getInviteHeadId(), Function.identity(), (k1, k2)->k2));


        List<SouInviteItem> inviteItemList = new ArrayList<>();
        for(ExtSouVendor vendor : vendorList) {
            SouInviteItem inviteItem = new SouInviteItem();
            SouInviteHead head = inviteHeadMap.get(vendor.getVendorId());
            if(Objects.isNull(head)) {
                continue;
            }
            //已经生成过
            if(itemMap.containsKey(head.getInviteHeadId())) {
                continue;
            }
            //     * 邀请供应商头表主键
            inviteItem.setInviteHeadId(head.getInviteHeadId());

            //     * 项目编号
            inviteItem.setProjectNo(souProject.getExtProjectNo());

            //     * 项目名称
            inviteItem.setProjectName(souProject.getSouName());

            inviteItem.setOrgBuId(souProject.getExtOrgBuId());

            //     * 板块编码
            inviteItem.setOrgBuCode(souProject.getExtOrgBuCode());

            //     * 板块名称
            inviteItem.setOrgBuName(souProject.getExtOrgBuName());
            //     * 公司ID(对应产品的业务实体id)
            inviteItem.setOrgId(souProject.getExtOrgOuId());

            //     * 公司编码(对应产品的业务实体编码)
            inviteItem.setOrgCode(souProject.getExtOrgOuCode());

            //     * 公司名称(对应产品的业务实体名称)
            inviteItem.setOrgName(souProject.getExtOrgOuName());

            //     * 创建时间
            inviteItem.setBidCreationDate(souProject.getCreationDate());

            //     * 品类ID
            inviteItem.setCategoryId(souProject.getExtCategoryId());

            //     * 品类编码
            inviteItem.setCategoryCode(souProject.getExtCategoryCode());

            //     * 品类
            inviteItem.setCategoryName(souProject.getExtCategoryName());

            //供应商ID
            inviteItem.setVendorId(vendor.getVendorId());

            //     * 是否投标（Y是，N否）
            inviteItem.setIsBid(YesOrNo.NO.getValue());

            //     * 是否废标（Y是，N否）
            inviteItem.setIsInvalidBid(YesOrNo.NO.getValue());
            //     * 是否中标（Y是，N否）
            inviteItem.setIsSuccBid(YesOrNo.NO.getValue());

            inviteItemList.add(inviteItem);

        }

        if(CollectionUtils.isNotEmpty(inviteItemList)) {
            QlQueryAction queryAction = QlQueryAction.builder().build();
            queryAction.setType(MqlType.SOU_INVITE_ITEM_BUYER);
            queryAction.setAction("create");
            queryAction.setPayload(inviteItemList);
            queryAction.setDoProxy(true);

            qlDispatcher.execute(queryAction);
        }
    }


    protected SouInviteHead builderSouInviteHead(ExtSouVendor souVendor) {
        SouInviteHead inviteHead = new SouInviteHead();
        inviteHead.setVendorId(souVendor.getVendorId());
        inviteHead.setVendorCode(souVendor.getVendorCode());
        inviteHead.setVendorName(souVendor.getVendorName());
        inviteHead.setContactName(souVendor.getLinkmanName());
        inviteHead.setPhone(souVendor.getPhone());
        inviteHead.setEmail(souVendor.getEmail());

        return inviteHead;
    }
}
