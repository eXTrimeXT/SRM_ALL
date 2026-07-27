package com.midea.cloud.srm.sou.meiql.ca.repo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.func.LambdaUtil;
import com.alibaba.fastjson.JSON;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.meiql.api.action.DefaultAction;
import com.midea.cloud.meiql.api.service.QlCondition;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.ProxyQlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.api.spec.result.RepoRecMap;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.meiql.core.repository.jooq.support.QueryParam;
import com.midea.cloud.meiql.core.util.ResultUtil;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.sou.bidprices.dto.BidPriceDto;
import com.midea.cloud.srm.model.sou.ca.dto.*;
import com.midea.cloud.srm.model.sou.ca.enums.CaStatusEnum;
import com.midea.cloud.srm.model.sou.ca.enums.CaTypeEnum;
import com.midea.cloud.srm.model.sou.enums.CaTenderTimeTypeEnum;
import com.midea.cloud.srm.model.sou.enums.SouBidPlanTypeEnum;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.enums.TypeEnum;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouDemand;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouPlan;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.sup.orgcategory.entity.PjOrgCategory;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.model.supplier.info.entity.OrgCategory;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.pojo.RiskCompanyInfo;
import com.midea.cloud.srm.sou.sourcing.init.service.ExtSouInitEventService;
import com.midea.cloud.srm.sou.sourcing.init.service.ExtSouInitQueryService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouPlanService;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
@Slf4j
@Component
public class CaRepository extends CrudRepository {

    @Autowired
    private ExtSouInitEventService extSouInitEventService;

    @Autowired
    private ExtSouInitQueryService extSouInitQueryService;

    @Autowired
    private QlService qlService;

    @Autowired
    private QlOpenClient qlOpenClient;

    @Autowired
    private IExtSouPlanService planService;
    @Autowired
    private BaseClient baseClient;


    public CaRepository() {
        //注册action
        this.register("submit",this::submit,true,"提交");
        this.register("abandon",this::abandon,true,"废弃");
        this.register("selectWinCheck",this::selectWinCheck,true,"选择中标供应商校验");
    }

    private QlResult selectWinCheck(QlQueryAction queryAction) {
        String check = checkSelectWinVendor(queryAction);
        List<Record> recs = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        Long caId = recs.get(0).get(CaDTO::getCaId);

        QlResult qlResult = new QlResult();
        qlResult.setType(queryAction.getType());
        qlResult.setPayload(check);
        List<Record> records = new ArrayList<>(16);
        Record record = new Record();
        records.add(record);
        record.put(CaDTO::getCaId, caId);
        record.put("code", StringUtils.isBlank(check) ? 0 : 1);
        record.put("msg", check);
        qlResult = ResultUtil.build(queryAction, LambdaUtil.getFieldName(CaDTO::getCaId), records, false);
        return qlResult;
    }

    private QlResult submit(QlQueryAction queryAction) {
        this.initeCaNegotiate(queryAction);
        this.initeCaPrice(queryAction);
        List<Record> recs = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        this.initValues(recs);
        return super.doSave(ProxyQlQueryAction.proxy(queryAction, DefaultAction.SAVE.value()),recs);
    }

    /**
     * 初始化值
     * @param recs
     */
    private void initValues(List<Record> recs) {
        //初始化状态为拟定，类型为定标申请
        for (Record rec : recs) {
            if (StringUtils.isEmpty(rec.get(CaDTO::getStatus))) {
                rec.put(CaDTO::getStatus, CaStatusEnum.DRAFT.getCode());
                rec.put(CaDTO::getType, CaTypeEnum.APPLY.getCode());
            }
        }
    }

    private void initeCaPrice(QlQueryAction queryAction) {
        String type = queryAction.getType();
        List<Record> recs = PayloadWrapper.of(type, queryAction.getPayload()).asRecords();
        recs.forEach(rec -> {
            if(Arrays.asList(CaStatusEnum.DRAFT.getCode(), CaStatusEnum.REJECTED.getCode(), CaStatusEnum.WITHDRAW.getCode()).contains(StringUtils.defaultIfBlank(rec.get(CaDTO::getStatus), CaStatusEnum.DRAFT.getCode()))) {
                if(CollectionUtils.isNotEmpty(rec.get(CaDTO::getHistoryPriceList))) {
                    rec.put(CaDTO::getCaPrices, toCaPriceDto(rec.get(CaDTO::getCaId), JSON.parseArray(JSON.toJSONString(rec.get(CaDTO::getHistoryPriceList)), BidPriceDto.class)));
                } else {
                    rec.put(CaDTO::getCaPrices, null);
                    clearCaPrice(rec.get(CaDTO::getCaId));
                }
            }
        });
        queryAction.setPayload(recs);
    }

    private String checkSelectWinVendor(QlQueryAction queryAction) {
        String type = queryAction.getType();
        List<Record> recs = PayloadWrapper.of(type, queryAction.getPayload()).asRecords();
        List<String> errorList = new ArrayList<>(16);
        List<String> warningList = new ArrayList<>(16);
        recs.forEach(rec -> {
            if(CaStatusEnum.APPROVING.getCode().equals(rec.get(CaDTO::getStatus))) {
                List<CaSelectionResultDTO>  caSelectionResults = JSON.parseArray(JSON.toJSONString(rec.get(CaDTO::getCaSelectionResults)), CaSelectionResultDTO.class);

                List<Long> winVendorIdList = caSelectionResults.stream().filter(r -> YesOrNo.YES.getValue().equals(r.getIsWin())).map(r -> r.getVendorId()).collect(Collectors.toList());
                if(CollectionUtils.isEmpty(winVendorIdList)) {
                    return;
                }
                //定标申请，一级领导定标，
                //  如果定标供应商是黑名单，弹窗提醒，不允许提交
                //   如果定标供应商是重点关注，弹窗提醒，允许提交
                //   如果定标供应商是品类受限（某单位下）、单位受限、时间受限，不允许提交
                //查询供应商主表
                List<RecordDTO> companyList = qlOpenClient.query(ContextPath.SUP, QlOpenWrappers.query(MqlType.SUPPLIER).in(CompanyInfo::getCompanyId, winVendorIdList));
                Map<Long, RecordDTO> companyMap = companyList.stream().collect(Collectors.toMap(r -> r.get(CompanyInfo::getCompanyId), Function.identity(), (k1, k2) -> k2));

                List<RecordDTO> orgCategoryList = qlOpenClient.query(ContextPath.SUP, QlOpenWrappers.query(MqlType.ORG_CATEGORY)
                        .in(OrgCategory::getCompanyId, winVendorIdList)
                        .eq(OrgCategory::getOrgId, rec.get(CaDTO::getExtOrgOuId))
                        .eq(OrgCategory::getCategoryId, rec.get(CaDTO::getExtCategoryId)));

                companyList.stream().forEach(company -> {
                    if(YesOrNo.YES.getValue().equals(company.get(CompanyInfo::getIsBacklist))) {
                        errorList.add(MessageFormat.format("{0}为黑名单", company.get(CompanyInfo::getCompanyName)));
                    }
                    if(YesOrNo.YES.getValue().equals(company.get(RiskCompanyInfo::getTimeLimitFlag))) {
                        errorList.add(MessageFormat.format("{0}为时间受限", company.get(CompanyInfo::getCompanyName)));
                    }
                    if(YesOrNo.YES.getValue().equals(company.get(RiskCompanyInfo::getFocusFlag))) {
                        warningList.add(MessageFormat.format("{0}为重点关注", company.get(CompanyInfo::getCompanyName)));
                    }
                });

                if(CollectionUtils.isNotEmpty(orgCategoryList)) {
                    orgCategoryList.stream().forEach(orgcategory -> {
                        RecordDTO company = companyMap.get(orgcategory.get(OrgCategory::getCompanyId));
                        if(Objects.isNull(company)) {
                            return;
                        }
                        if(YesOrNo.NO.getValue().equals(orgcategory.get(PjOrgCategory::getPjCategoryStatus))) {
                            errorList.add(MessageFormat.format("{0}为品类受限", company.get(CompanyInfo::getCompanyName)));
                        }
                        if(YesOrNo.NO.getValue().equals(orgcategory.get(PjOrgCategory::getPjOrgStatus))) {
                            errorList.add(MessageFormat.format("{0}为单位受限", company.get(CompanyInfo::getCompanyName)));
                        }
                    });

                }

            }
        });

        if(CollectionUtils.isNotEmpty(errorList)) {
            throw new BaseException(MessageFormat.format("选择中标供应商校验不通过：{0}", errorList.stream().distinct().collect(Collectors.joining(SrmConstant.SIG_3))));
        }

        if(CollectionUtils.isNotEmpty(warningList)) {
            return MessageFormat.format("选择中标供应商校验存在风险：{0}", warningList.stream().distinct().collect(Collectors.joining(SrmConstant.SIG_3)));
        }
        return "";
    }

    private void clearCaPrice(Long caId) {
        if(Objects.isNull(caId)) {
            return;
        }
        qlService.deleteByWrapper(QlWrappers.update(TypeEnum.CaPrice.getCode()).eq(CaPriceDto::getCaId, caId));
    }

    /**
     * 构造历史价格数据
     * @param caId
     * @param historyPriceList
     * @return
     */
    private Collection<Record> toCaPriceDto(Long caId, List<BidPriceDto> historyPriceList) {
        List<CaPriceDto> caPrices = new ArrayList<>();
        Set<Long> bidPriceIdSet = new HashSet<>();
        List<DictItem> items = baseClient.listDictItemByDictCode("REGION");
        /*Optional<DictItem> dictItem = items.stream()
                .filter(item -> yearData.getAreaName().equals(item.getDictItemName()))
                .findFirst();*/
        if(CollectionUtils.isNotEmpty(historyPriceList)) {
            historyPriceList.stream().forEach(data -> {
                CaPriceDto priceDto = new CaPriceDto();
                priceDto.setCaId(caId);
                priceDto.setBidPriceId(data.getBidPriceId());

                if(data.getBidPriceId() == 0L){
                    priceDto.setPriceTax(data.getPriceTax());
                    priceDto.setFixedPriceTax(data.getFixedPriceTax());
                    priceDto.setBrand(data.getBrand());
                    priceDto.setItemDesc(data.getItemDesc());
                    priceDto.setProjectNo(data.getProjectNo());
                    for(DictItem dictItem:items){
                        if(data.getRegion().equals(dictItem.getDictItemCode())){
                            priceDto.setRegion(dictItem.getDictItemName());
                            break;
                        }else if(data.getRegion().equals(dictItem.getDictItemName())){
                            priceDto.setRegion(dictItem.getDictItemName());
                            break;
                        }
                    }
                    priceDto.setSouName(data.getSouName());
                    priceDto.setSouPrincipal(data.getSouPrincipal());
                    priceDto.setSouPrincipalUserName(data.getSouPrincipalUserName());
                    priceDto.setSpecification(data.getSpecification());
                    priceDto.setSouPrincipalUserId(data.getSouPrincipalUserId());
                    priceDto.setBidDate(data.getCreationDate());
                    priceDto.setPriceSumTax(data.getPriceSumTax());
                    caPrices.add(priceDto);
                }else if(!bidPriceIdSet.contains(priceDto.getBidPriceId())) {
                    bidPriceIdSet.add(priceDto.getBidPriceId());
                    caPrices.add(priceDto);
                }
            });
        }



        if(!Objects.isNull(caId)) {
            List<CaPriceDto> priceDtoList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.CaPrice.getCode()).eq(CaPriceDto::getCaId, caId), CaPriceDto.class);
            Map<Long, Long> priceDtoMap = priceDtoList.stream().filter(e -> e.getBidPriceId() > 0L).collect(Collectors.toMap(k -> k.getBidPriceId(), v -> v.getCaPriceId(), (k1, k2)->k2));
            priceDtoList = priceDtoList.stream().filter(p -> !bidPriceIdSet.contains(p.getBidPriceId())).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(priceDtoList)) {
                qlService.deleteByKeys(TypeEnum.CaPrice.getCode(), priceDtoList.stream().map(k->k.getCaPriceId()).collect(Collectors.toList()));
            }
            qlService.deleteByKeys(TypeEnum.CaPrice.getCode(), priceDtoList.stream().filter(e -> e.getBidPriceId() == 0L).map(k->k.getCaPriceId()).collect(Collectors.toList()));

            caPrices.stream().forEach(caPriceDto -> {
                if(caPriceDto.getBidPriceId()>0L){
                    caPriceDto.setCaPriceId(priceDtoMap.get(caPriceDto.getBidPriceId()));
                }
            });
        }

        Collection<Record> records = MeiQl.toListValue(caPrices, Record.class);
        return records;
    }

    private void initeCaNegotiate(QlQueryAction queryAction) {
        String type = queryAction.getType();
        List<Record> recs = PayloadWrapper.of(type, queryAction.getPayload()).asRecords();
        recs.forEach(rec -> {
            if(Arrays.asList(CaStatusEnum.DRAFT.getCode(), CaStatusEnum.REJECTED.getCode(), CaStatusEnum.WITHDRAW.getCode()).contains(StringUtils.defaultIfBlank(rec.get(CaDTO::getStatus), CaStatusEnum.DRAFT.getCode()))) {
                clearCaNegotiateDto(rec);
                if(ObjectUtils.allNotNull(rec.get(CaDTO::getCaNegotiateExtend))) {
                    rec.put(CaDTO::getCaNegotiates, toCaNegotiateDto(JSON.parseObject(JSON.toJSONString(rec.get(CaDTO::getCaNegotiateExtend)), CaNegotiateExtendDto.class)));
                }
            }
        });
        queryAction.setPayload(recs);
    }

    @Override
    public QlResult save(QlQueryAction queryAction) {

        this.initeCaNegotiate(queryAction);
        this.initeCaPrice(queryAction);

        return super.save(queryAction);
    }

    @Override
    public QlResult doSave(QlQueryAction queryAction,List<Record> recs) {
        this.initValues(recs);
        return super.doSave(queryAction,recs);
    }

    private QlResult abandon(QlQueryAction queryAction) {
        List<Record> recs = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        for (Record rec : recs) {
            rec.put(CaDTO::getStatus, CaStatusEnum.ABANDON.getCode());
        }
        //修改寻源单据状态
        this.updateSouStatusByRecord(recs);
        return super.update(ProxyQlQueryAction.proxy(queryAction, DefaultAction.UPDATE.value(),recs));
    }

    @Override
    protected QlCondition beforeQuery(QlQueryAction queryAction, QueryParam payload) {
        QlCondition qlCondition = super.beforeQuery(queryAction, payload);
        if (null == qlCondition) {
            qlCondition = MeiQl.newCondition();
            qlCondition.eq(CaDTO::getType,CaTypeEnum.APPLY.getCode());
        }
        return qlCondition;
    }

    @Override
    public QlResult delete(QlQueryAction queryAction) {
        List<Record> recs = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        //修改寻源状态
        updateSouStatusByRecord(recs);
        return super.delete(queryAction);
    }


    private void updateSouStatusByRecord(List<Record> recs) {
        //修改寻源单据状态
        List<Long> caIds = recs.stream().map(r -> r.get(CaDTO::getCaId)).collect(Collectors.toList());
        List<CaDTO> caDtos = qlService.readByKeys(TypeEnum.Ca.getCode(),caIds,CaDTO.class);
        if (null != caDtos && !caDtos.isEmpty()) {
            for (CaDTO ca : caDtos) {
                //修改招标单据状态
                if (null != ca.getProjectId() && SouTypeEnum.bid.name().equals(ca.getSouType())) {
                    extSouInitEventService.updateSouBidStatus(ca.getProjectId(),SouBiddingProStatusEnum.BUS_BID_OPEN);
                }
            }
        }
    }

    @Override
    public QlResult read(QlQueryAction queryAction) {
        QlResult result = super.read(queryAction);
        //供应商总体情况排序
        RepoRecMap rr = result.getRef().get("CaSupplier");
        List<Long> pa = new ArrayList<>();
        if (rr != null) {
            List<CaSupplierDTO> caSupList = new ArrayList<>();
            for (Record value : rr.values()) {
                CaSupplierDTO sd = new CaSupplierDTO();
                BeanUtil.copyProperties(value, sd);
                caSupList.add(sd);
            }
            caSupList.sort(Comparator.comparing(CaSupplierDTO::getCompositeScore, Comparator.nullsFirst(BigDecimal::compareTo)).thenComparing(CaSupplierDTO::getTechScore, Comparator.nullsFirst(BigDecimal::compareTo)).reversed());
            pa.addAll(caSupList.stream().map(CaSupplierDTO::getCaSupplierId).collect(Collectors.toList()));
        }
        result.getRefValues(TypeEnum.Ca.getCode()).forEach(record -> {
            record.put(CaDTO::getCaSuppliers, pa);
            //构造谈判内容
            record.put(CaDTO::getCaNegotiateExtend, buildCaNegotiateExtend(record, new ArrayList<>(result.getRefValues(TypeEnum.CaSupplier.getCode()))));
            //构造价格历史数据
            record.put(CaDTO::getHistoryPriceList, buildBidPriceList(new ArrayList<>(result.getRefValues(TypeEnum.CaPrice.getCode()))));
            //刷新定标申请周期数据
            caTenderTimesAutoReflash(record, result);
        });
        return result;
    }

    private void caTenderTimesAutoReflash(Record ca, QlResult result) {
        Collection<Record> tenderTimesList = result.getRefValues(TypeEnum.CaTenderTime.getCode());
        if(CollectionUtils.isEmpty(tenderTimesList)) {
            return;
        }
        String decideBidLeader = ca.get(CaDTO::getApprovalNickname);
        AtomicReference<Boolean> relfashFlag = new AtomicReference<>(false);

        tenderTimesList.forEach(record -> {
            if(CaTenderTimeTypeEnum.BUS_ANALYSIS.getCode().equals(record.get(CaTenderTimeDto::getType))) {
                if(Objects.isNull(record.get(CaTenderTimeDto::getActualTime))) {
                    relfashFlag.set(true);
                }
            }
            if(CaTenderTimeTypeEnum.DECIDE_BID.getCode().equals(record.get(CaTenderTimeDto::getType))) {
                if(Objects.isNull(record.get(CaTenderTimeDto::getActualTime))) {
                    relfashFlag.set(true);
                }
                record.put(CaTenderTimeDto::getDutyOfficer, MessageFormat.format("{0}: {1}", "定标领导", decideBidLeader));
            }
        });

        if(relfashFlag.get()) {
            List<ExtSouPlan> actualPalnsList = planService.lambdaQuery().eq(ExtSouPlan::getProjectId, ca.get(CaDTO::getProjectId))
                    .eq(ExtSouPlan::getPlanType, SouBidPlanTypeEnum.ACTUAL.getCode()).list();
            if(CollectionUtils.isNotEmpty(actualPalnsList)) {
                ExtSouPlan actual = actualPalnsList.get(0);
                tenderTimesList.forEach(record -> {
                    if(CaTenderTimeTypeEnum.BUS_ANALYSIS.getCode().equals(record.get(CaTenderTimeDto::getType))) {
                        if(Objects.isNull(record.get(CaTenderTimeDto::getActualTime))) {
                            record.put(CaTenderTimeDto::getActualTime, actual.getSumReportTime());
                        }
                    }
                    if(CaTenderTimeTypeEnum.DECIDE_BID.getCode().equals(record.get(CaTenderTimeDto::getType))) {
                        if(Objects.isNull(record.get(CaTenderTimeDto::getActualTime))) {
                            record.put(CaTenderTimeDto::getActualTime, actual.getPicketageTime());
                        }
                    }
                });
            }
        }
    }

    @Override
    public void afterRead(QlQueryAction queryAction, Collection<Record> records) {
        super.afterRead(queryAction, records);

        records.forEach(record->{
            String applicantNo =  extSouInitQueryService.getApplicantNo(record.get(CaDTO::getProjectId));
            String applicantId =  extSouInitQueryService.getApplicantId(applicantNo);
            record.put(CaDTO::getApplicantNo, applicantNo);
            record.put(CaDTO::getApplicantId, applicantId);
        });
    }

    /**
     * 移除谈判内容数据
     * @param ca
     */
    private void clearCaNegotiateDto(Record ca) {
        if(ObjectUtils.anyNull(ca.get(CaDTO::getCaId))) {
            return;
        }
        qlService.deleteByWrapper(QlWrappers.update(TypeEnum.CaNegotiate.getCode()).eq(CaNegotiateDto::getCaId, ca.get(CaDTO::getCaId)));
    }


    /**
     * 构造谈判内容数据
     * @param extendDto
     * @return
     */
    private Collection<Record> toCaNegotiateDto(CaNegotiateExtendDto extendDto) {
        List<CaNegotiateDto> caNegotiateDtos = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(extendDto.getTitle()) && CollectionUtils.isNotEmpty(extendDto.getData())) {
            AtomicInteger lineNum = new AtomicInteger(0);
            extendDto.getData().forEach(data -> {
                AtomicReference<Integer> index = new AtomicReference<>(0);
                lineNum.getAndAdd(1);
                extendDto.getTitle().forEach(caNegotiateDto -> {
                    CaNegotiateDto dto = new CaNegotiateDto();
                    BeanCopyUtil.copyProperties(dto, caNegotiateDto);
                    dto.setSortIndex(lineNum.get());
                    dto.setNegotiate(data.get(index.getAndSet(index.get() + 1)));
                    caNegotiateDtos.add(dto);
                });
            });
        }

        Collection<Record> records = MeiQl.toListValue(caNegotiateDtos, Record.class);
        return records;
    }

    private List<BidPriceDto> buildBidPriceList(List<Record> caPrices) {

        if(CollectionUtils.isEmpty(caPrices)) {
            return new ArrayList<>(50);
        }

        List<BidPriceDto> historyPriceList = qlService.readByKeys(MqlType.BID_PRICE, caPrices.stream().map(o -> o.get(CaPriceDto::getBidPriceId)).collect(Collectors.toList()), BidPriceDto.class);
        //查询caprice表，拼接结果
        List<Long> caIds = caPrices.stream().map(r -> r.get(CaDTO::getCaId)).collect(Collectors.toList());
        List<CaPriceDto> priceDtoList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.CaPrice.getCode()).eq(CaPriceDto::getCaId,caIds.get(0) ).eq(CaPriceDto::getBidPriceId,0L), CaPriceDto.class);
        if(priceDtoList.size()>0){
            for(int i=0;i<priceDtoList.size();i++){
                BidPriceDto bidPriceDto=new BidPriceDto();
                bidPriceDto.setBidPriceId(priceDtoList.get(i).getBidPriceId());
                bidPriceDto.setSouName(priceDtoList.get(i).getSouName());
                bidPriceDto.setProjectNo(priceDtoList.get(i).getProjectNo());
                bidPriceDto.setItemDesc(priceDtoList.get(i).getItemDesc());
                bidPriceDto.setSpecification(priceDtoList.get(i).getSpecification());
                bidPriceDto.setBrand(priceDtoList.get(i).getBrand());
                if(priceDtoList.get(i).getPriceTax()!=null){
                    bidPriceDto.setPriceTax(priceDtoList.get(i).getPriceTax());
                }
                if(priceDtoList.get(i).getFixedPriceTax()!=null){
                    bidPriceDto.setFixedPriceTax(priceDtoList.get(i).getFixedPriceTax());
                }
                bidPriceDto.setRegion(priceDtoList.get(i).getRegion());
                bidPriceDto.setSouPrincipalUserId(priceDtoList.get(i).getSouPrincipalUserId());
                bidPriceDto.setSouPrincipalUserName(priceDtoList.get(i).getSouPrincipalUserName());
                bidPriceDto.setSouPrincipal(priceDtoList.get(i).getSouPrincipal());
                bidPriceDto.setCreationDate(priceDtoList.get(i).getBidDate());
                bidPriceDto.setPriceSumTax(priceDtoList.get(i).getPriceSumTax());
                historyPriceList.add(bidPriceDto);
            }
        }
        return historyPriceList;
    }

    private CaNegotiateExtendDto buildCaNegotiateExtend(Record ca, List<Record> caSuppliers) {

        CaNegotiateExtendDto caNegotiateExtendDto = new CaNegotiateExtendDto();
        //构造列头
        caNegotiateExtendDto.setTitle(new ArrayList<>());
        //构造数据
        caNegotiateExtendDto.setData(new ArrayList<>());

        if(CollectionUtils.isEmpty(caSuppliers)) {
            return caNegotiateExtendDto;
        }

        //查询记录
        List<CaNegotiateDto> caNegotiateDtos = qlService.queryByWrapper(QlWrappers.query(TypeEnum.CaNegotiate.getCode()).eq(CaNegotiateDto::getCaId, ca.get(CaDTO::getCaId)).orderByAsc(CaNegotiateDto::getSortIndex), CaNegotiateDto.class);
        //key : vendorId + sortIndex
        Map<String, CaNegotiateDto> caNegotiateDtoMap = caNegotiateDtos.stream().collect(Collectors.toMap(k -> StringUtils.joinWith("_", k.getVendorId(), ObjectUtils.defaultIfNull(k.getSortIndex(), 1)), Function.identity(), (k1, k2) -> k2));

        //行号列表
        List<Integer> lineList = caNegotiateDtos.stream().map(i -> ObjectUtils.defaultIfNull(i.getSortIndex(), 1)).distinct().sorted(Comparator.comparingInt(i->i)).collect(Collectors.toList());

        //第一列固定列表头
        caNegotiateExtendDto.getTitle().add(buildCaNegotiateFixedFirstCol(ca.get(CaDTO::getCaId)));
        //动态表头
        caSuppliers.forEach(caSupplier -> {
            caNegotiateExtendDto.getTitle().add(buildCaNegotiate(caSupplier));
        });

        //动态行数据
        lineList.stream().forEach(line -> {
            List<String> data = new ArrayList<>(caNegotiateExtendDto.getTitle().size());
            caNegotiateExtendDto.getTitle().stream().forEach(title -> {
                String key = StringUtils.joinWith("_", title.getVendorId(), ObjectUtils.defaultIfNull(line, 1));
                String value = ObjectUtils.defaultIfNull(caNegotiateDtoMap.getOrDefault(key, new CaNegotiateDto()).getNegotiate(), "");
                data.add(value);
            });
            caNegotiateExtendDto.getData().add(data);
        });

        return caNegotiateExtendDto;
    }

    /**
     * 构造供应商谈判动态列头
     * @param record
     * @return
     */
    private CaNegotiateDto buildCaNegotiate(Record record) {
        CaNegotiateDto caNegotiateDto = new CaNegotiateDto();
        caNegotiateDto.setCaId(record.get(CaSupplierDTO::getCaId));
        caNegotiateDto.setVendorId(record.get(CaSupplierDTO::getVendorId));
        caNegotiateDto.setVendorCode(record.get(CaSupplierDTO::getVendorCode));
        caNegotiateDto.setVendorName(record.get(CaSupplierDTO::getVendorName));
        return caNegotiateDto;

    }

    /**
     * 构造供应商谈判动态列头
     * @param caId
     * @return
     */
    private CaNegotiateDto buildCaNegotiateFixedFirstCol(Long caId) {
        CaNegotiateDto caNegotiateDto = new CaNegotiateDto();
        caNegotiateDto.setCaId(caId);
        caNegotiateDto.setVendorId(SrmConstant.LONG_MINUS_ONE);
        caNegotiateDto.setVendorCode(SrmConstant.VIRTUAL_VENDOR_CODE);
        caNegotiateDto.setVendorName(SrmConstant.CA_NEGOTIATE_FIRST_COL);
        return caNegotiateDto;

    }

}
