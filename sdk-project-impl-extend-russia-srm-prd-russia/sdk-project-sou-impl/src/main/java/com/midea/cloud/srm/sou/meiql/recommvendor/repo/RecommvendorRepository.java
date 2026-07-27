package com.midea.cloud.srm.sou.meiql.recommvendor.repo;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.function.SFunction;
import com.midea.cloud.meiql.api.service.QlCondition;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.*;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.api.spec.schema.QlType;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.meiql.core.repository.jooq.support.QueryParam;
import com.midea.cloud.meiql.core.util.ResultUtil;
import com.midea.cloud.meiql.core.util.SchemaUtil;
import com.midea.cloud.srm.feign.SouExtPerformanceClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.common.enums.ProcessStatusEnum;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.perf.enums.PerformanceCodeEnum;
import com.midea.cloud.srm.model.perf.projectscore.entity.ProjectScoreHeader;
import com.midea.cloud.srm.model.sou.enums.ExtOrderTypeEnum;
import com.midea.cloud.srm.model.sou.enums.SouRecommvendorStatusEnum;
import com.midea.cloud.srm.model.sou.enums.SouRecommvendorTypeEnum;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.recommvendor.dto.*;
import com.midea.cloud.srm.model.sou.recommvendor.enums.RecommType;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.bid.openrecords.service.IExtNpmSouOpenBidRecordService;
import com.midea.cloud.srm.sou.constants.SouConstant;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.compent.RiskComponent;
import com.midea.cloud.srm.sou.meiql.recommvendor.service.SouRecommvendorQuitSearchService;
import com.midea.cloud.srm.sou.meiql.recommvendor.service.SouRecommvendorRiskService;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouVendorMapper;
import com.midea.cloud.srm.sou.sourcing.init.service.*;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Api("供应商推荐自定义组件")
@Component
@Slf4j
public class RecommvendorRepository extends CrudRepository {

    @Autowired
    private ExtSouInitQueryService extSouInitQueryService;

    @Autowired
    private SouRecommvendorRiskService souRecommvendorRiskService;

    @Autowired
    private SouRecommvendorQuitSearchService souRecommvendorQuitSearchService;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private QlService qlService;

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private IExtSouOrderService extSouOrderService;

    @Autowired
    private IExtSouDemandService demandService;

    @Autowired
    private IExtNpmSouOrderService extNpmSouOrderService;

    @Autowired
    private IExtNpmSouOpenBidRecordService openBidRecordService;

    @Autowired
    private ExtSouVendorMapper vendorMapper;

    private static final String SEQ_RECOMMVENDOR = "SEQ_RECOMMVENDOR";

    private static final List<String> VENDOR_GSCP_NOT_PASS = Arrays.asList("Open", "Changed");

    @Autowired
    private IExtSouOrderService orderService;
    @Autowired
    private SouExtPerformanceClient souExtPerformanceClient;

    private QlResult submit(QlQueryAction queryAction) {
        return this.save(queryAction);
    }

    @Override
    public QlResult doSave(QlQueryAction queryAction, List<Record> recs) {

        return super.doSave(queryAction, recs);
    }

    private void checkAddAsNewestRecommvendor(Long originalProjectId, Long currentProjectId) {
        if(Objects.isNull(originalProjectId)) {
            return;
        }
        //逆向找到原单
        RecommvendorProjectExtendDto extendDto = null;
        Long projectId = originalProjectId;
        while (true) {
            List<RecommvendorProjectExtendDto> extendDtoList = qlService.queryByWrapper(QlWrappers.query(RecommType.RecommvendorProjectExtend.name())
                    .eq(RecommvendorProjectExtendDto::getProjectId, projectId), RecommvendorProjectExtendDto.class);
            if(CollectionUtils.isNotEmpty(extendDtoList)) {
                extendDto = extendDtoList.get(0);
                projectId = extendDto.getOriginalProjectId();
                if(SouRecommvendorTypeEnum.RECOMM.getCode().equals(extendDto.getRcommendType())) {
                    break;
                }
            } else {
                break;
            }
        }
        if(Objects.isNull(extendDto)) {
            return;
        }

        //基于原单找最新的追加单
        RecommvendorProjectExtendDto add = null;
        List<Long> projectIdList = new ArrayList<>();
        projectIdList.add(extendDto.getProjectId());
        while (true) {
            QlQueryWrapper queryWrapper = QlWrappers.query(RecommType.RecommvendorProjectExtend.name(), "e")
                    .in(RecommvendorProjectExtendDto::getOriginalProjectId, projectIdList).orderByDesc(RecommvendorProjectExtendDto::getCreationDate);
            if(!Objects.isNull(currentProjectId)) {
                //排除当前
                queryWrapper.notEq(RecommvendorProjectExtendDto::getProjectId, currentProjectId);
            }
            queryWrapper.exists(RecommType.RecommvendorProject.name(), "p", ql -> {
                ql.eq(QlQueryFieldWrapper.field("p", RecommvendorProjectDto::getProjectId), QlQueryFieldWrapper.field("e", RecommvendorProjectExtendDto::getProjectId));
            });
            List<RecommvendorProjectExtendDto> extendDtoList = qlService.queryByWrapper(queryWrapper, RecommvendorProjectExtendDto.class);
            if(CollectionUtils.isEmpty(extendDtoList)) {
                break;
            }
            projectIdList = extendDtoList.stream().map(RecommvendorProjectExtendDto::getProjectId).distinct().collect(Collectors.toList());
            add = extendDtoList.get(0);
        }
        //如果追加单不是基于最新的追加单进行追加，则校验不通过
        AssertUtils.isTrue(Objects.isNull(add) || originalProjectId.compareTo(add.getProjectId()) == 0, "只能基于供应商推荐单最新的追加单[{0}]进行追加！", Objects.isNull(add)? "" : add.getExtRecommendNo());
    }

    private void checkAddRecommvendor(Long originalProjectId) {
        if(Objects.isNull(originalProjectId)) {
            return;
        }
        List<Long> recommvendorProjectIdList = new ArrayList<>();
        Long projectId = originalProjectId;
        while (true) {
            List<RecommvendorProjectExtendDto> extendDtoList = qlService.query(RecommType.RecommvendorProjectExtend.name(),
                    MeiQl.newCondition().eq(RecommvendorProjectExtendDto::getProjectId, projectId), RecommvendorProjectExtendDto.class);
            if(CollectionUtils.isNotEmpty(extendDtoList)) {
                recommvendorProjectIdList.add(extendDtoList.get(0).getProjectId());
                if(Objects.isNull(extendDtoList.get(0).getOriginalProjectId())) {
                    break;
                }
                projectId = extendDtoList.get(0).getOriginalProjectId();
            } else {
                break;
            }
        }
        if(CollectionUtils.isEmpty(recommvendorProjectIdList)) {
            return;
        }
        //查询关联的申请单号
        List<ExtSouDemand> recommvendoDemandList = demandService.lambdaQuery().in(ExtSouDemand::getProjectId, recommvendorProjectIdList).eq(ExtSouDemand::getStatus, SrmConstant.NUM_ZERO).list();
        if(CollectionUtils.isEmpty(recommvendoDemandList)) {
            return;
        }
        //查询关联的招标单据
        List<ExtSouDemand> bidDemandList = demandService.lambdaQuery().in(ExtSouDemand::getApplicantNo, recommvendoDemandList.stream().map(ExtSouDemand::getApplicantNo).distinct().collect(Collectors.toList())).eq(ExtSouDemand::getStatus, SrmConstant.NUM_ZERO).list();
        List<ExtSouProject> projectList = projectService.lambdaQuery()
                .eq(ExtSouProject::getSouType, SouTypeEnum.bid.name())
                .in(ExtSouProject::getProjectId, bidDemandList.stream().map(ExtSouDemand::getProjectId).distinct().collect(Collectors.toList()))
                .orderByDesc(ExtSouProject::getCreationDate).list();
        if(CollectionUtils.isEmpty(projectList)) {
            return;
        }

        //开标记录
        ExtSouProject project = projectList.get(0);
        List<ExtNpmSouOpenBidRecord> openBidRecords = openBidRecordService.lambdaQuery().eq(ExtNpmSouOpenBidRecord::getProjectId, project.getProjectId()).ne(ExtNpmSouOpenBidRecord::getOpenStatus, ProcessStatusEnum.PENDING.getCode()).orderByDesc(ExtNpmSouOpenBidRecord::getCreationDate).list();
        if(CollectionUtils.isNotEmpty(openBidRecords)) {
            throw new BaseException(MessageFormat.format("招标单{0}{1}已开标，不允许追加供应商", project.getSouNo(), ExtOrderTypeEnum.TECH.getCode().equals(openBidRecords.get(0).getOpenType()) ? ExtOrderTypeEnum.TECH.getName() : ExtOrderTypeEnum.BUS.getName()));
        }

    }

    protected  <F> String fieldName(SFunction<F, ?> lambdaObj) {
        return QlQueryFieldWrapper.field(lambdaObj).getFieldName();
    }

    @Override
    public QlResult save(QlQueryAction queryAction) {

        if (RecommType.RecommvendorProject.name().equals(queryAction.getType())) {

            Collection<Record> records = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();

            //GSCP卡流程供应商列表
            List<String> vendorNameGscpNotPassList = new ArrayList<>();

            //受限供应商
            List<String> vendorNameTimeLimitList = new ArrayList<>();

            records.forEach(record -> {
                //状态 SouRecommvendorStatusEnum
                String projectStatus = record.get(RecommvendorProjectDto::getProjectStatus);
                if(Arrays.asList(SouRecommvendorStatusEnum.APPROVED.getCode(), SouRecommvendorStatusEnum.APPROVING.getCode()).contains(projectStatus)) {
                    return;
                }

                Long projectId = record.get(RecommvendorProjectDto::getProjectId);

                //追加类型，如果已开标，不允许追加
                Long originalProjectId = null;
                if (record.containsKey(SouConstant.RE_COM_MV_EN_DOR_PROJECT_EXTEND) && CollectionUtils.isNotEmpty((Collection) record.get(SouConstant.RE_COM_MV_EN_DOR_PROJECT_EXTEND))) {
                    List<Map<String, Object>> recommvendorProjectExtendList = (List<Map<String, Object>>) record.get(SouConstant.RE_COM_MV_EN_DOR_PROJECT_EXTEND);
                    originalProjectId = MapUtils.getLong(recommvendorProjectExtendList.get(0), fieldName(RecommvendorProjectExtendDto::getOriginalProjectId));
                    String originalExtRecommendNo = MapUtils.getString(recommvendorProjectExtendList.get(0), fieldName(RecommvendorProjectExtendDto::getOriginalExtRecommendNo));
                    if(StringUtils.isNotBlank(originalExtRecommendNo)) {
                        List<RecommvendorProjectDto> extendDtoList = qlService.query(RecommType.RecommvendorProject.name(), MeiQl.newCondition().eq(RecommvendorProjectDto::getSouNo, originalExtRecommendNo), RecommvendorProjectDto.class);
                        if(CollectionUtils.isNotEmpty(extendDtoList)) {
                            originalProjectId = extendDtoList.get(0).getProjectId();
                            recommvendorProjectExtendList.get(0).put(fieldName(RecommvendorProjectExtendDto::getOriginalProjectId), originalProjectId);
                        }
                    }
                }
                //已开标，不允许追加供应商
                checkAddRecommvendor(originalProjectId);
                //只能基于原单对应的最新的追加单进行追加
                checkAddAsNewestRecommvendor(originalProjectId, projectId);
                //追加供应商，把合并申请单号也一并追加
                if(ObjectUtils.allNotNull(originalProjectId)) {
                    List<Long> projectIdList = new ArrayList<>();
                    projectIdList.add(originalProjectId);
                    if(!Objects.isNull(projectId)) {
                        projectIdList.add(projectId);
                    }
                    List<Record> demandList = qlService.query(RecommType.RecommvendorDemand.name(), MeiQl.newCondition().in(ExtSouDemand::getProjectId, projectIdList), Record.class);
                    boolean pass = !demandList.stream().filter(d -> !Objects.isNull(projectId) && Long.compare(d.get(ExtSouDemand::getProjectId), projectId) == 0).findAny().isPresent();
                    if(CollectionUtils.isNotEmpty(demandList) && pass) {
                        demandList.stream().forEach(r -> {
                            r.put(ExtSouDemand::getDemandId, null);
                            r.put(ExtSouDemand::getProjectId, null);
                        });
                        record.put(fieldName(RecommvendorProjectDto::getRecommvendorDemandList), demandList);
                    }
                }

                //供应商排序
                AtomicInteger index = new AtomicInteger(1);
                if (record.containsKey(SouConstant.RE_COMM_VEN_DOR_LIST) && CollectionUtils.isNotEmpty((Collection) record.get(SouConstant.RE_COMM_VEN_DOR_LIST))) {
                    List<Map<String, Object>> recommvendorList = (List<Map<String, Object>>) record.get(SouConstant.RE_COMM_VEN_DOR_LIST);
                    List<Long> souVendorIdList = new ArrayList<>();
//                    Long projectId = record.getLong("projectId");
                    //供应商不允许重复
                    Set<Long> vendorIdSet = new HashSet<>();
                    Set<String> errorList = new HashSet<>();
                    recommvendorList.stream().forEach(vendor -> {
                        vendor.put(fieldName(RecommvendorDto::getSortIndex), index.getAndAdd(1));
                        Long souVendorId = MapUtils.getLong(vendor, fieldName(RecommvendorDto::getSouVendorId));
                        if(ObjectUtils.allNotNull(souVendorId)) {
                            souVendorIdList.add(souVendorId);
                        }
                        Long vendorId = MapUtils.getLong(vendor, fieldName(RecommvendorDto::getVendorId));
                        if(vendorIdSet.contains(vendorId)) {
                            errorList.add(MapUtils.getString(vendor, fieldName(RecommvendorDto::getVendorName)));
                        }
                        vendorIdSet.add(vendorId);

                        if(VENDOR_GSCP_NOT_PASS.contains(vendor.get(SouConstant.EXTGSCP))) {
                            vendorNameGscpNotPassList.add(MapUtils.getString(vendor, fieldName(RecommvendorDto::getVendorName)));
                        }
                        //时间受限
                        if(YesOrNo.YES.getValue().equals(MapUtils.getString(vendor, fieldName(RecommvendorDto::getTimeLimitFlag)))) {
                            vendorNameTimeLimitList.add(MapUtils.getString(vendor, fieldName(RecommvendorDto::getVendorName)));
                        }

                    });
                    AssertUtils.isTrue(CollectionUtils.isEmpty(errorList), MessageFormat.format("存在以下重复供应商，不允许操作：{0}", errorList.stream().collect(Collectors.joining("，"))));
                    if(ObjectUtils.allNotNull(projectId)) {
                        QlUpdateWrapper updateWrapper = QlWrappers.update(RecommType.Recommvendor.name()).eq(RecommvendorDto::getProjectId, projectId);
                        if(CollectionUtils.isNotEmpty(souVendorIdList)) {
                            updateWrapper.notIn(RecommvendorDto::getSouVendorId, souVendorIdList);
                        }
                        qlService.deleteByWrapper(updateWrapper);

                    }

                } else {
                    if(ObjectUtils.allNotNull(projectId)) {
                        QlUpdateWrapper updateWrapper = QlWrappers.update(RecommType.Recommvendor.name()).eq(RecommvendorDto::getProjectId, projectId);
                        qlService.deleteByWrapper(updateWrapper);

                    }
                }

                List<Long> souFileIdList = new ArrayList<>();
                index.set(1);
                if (record.containsKey(SouConstant.RECOMMVENDORFILELIST) && CollectionUtils.isNotEmpty((Collection) record.get(SouConstant.RECOMMVENDORFILELIST))) {
                    List<Map<String, Object>> recommvendorFileList = (List<Map<String, Object>>) record.get(SouConstant.RECOMMVENDORFILELIST);
                    recommvendorFileList.stream().forEach(vendor -> {
                        vendor.put(fieldName(RecommvendorFileDto::getSortIndex), index.getAndAdd(1));
                        Long souFileId = MapUtils.getLong(vendor, fieldName(RecommvendorFileDto::getSouFileId));
                        if(ObjectUtils.allNotNull(souFileId)) {
                            souFileIdList.add(souFileId);
                        }
                    });
                }
                if(ObjectUtils.allNotNull(projectId)) {
                    QlUpdateWrapper updateWrapper = QlWrappers.update(RecommType.RecommvendorFile.name()).eq(RecommvendorDto::getProjectId, projectId);
                    if(CollectionUtils.isNotEmpty(souFileIdList)) {
                        updateWrapper.notIn(RecommvendorFileDto::getSouFileId, souFileIdList);
                    }
                    qlService.deleteByWrapper(updateWrapper);
                }


                if (StringUtils.isNotBlank(record.get(RecommvendorProjectDto::getSouNo))) {
                    return;
                }
                String docNum = baseClient.seqGen(SEQ_RECOMMVENDOR);
                record.put(RecommvendorProjectDto::getSouNo, docNum);
                record.put(fieldName(RecommvendorProjectDto::getExtRecommendNo), docNum);
                if (record.containsKey(SouConstant.RE_COM_MV_EN_DOR_PROJECT_EXTEND) && CollectionUtils.isNotEmpty((Collection) record.get(SouConstant.RE_COM_MV_EN_DOR_PROJECT_EXTEND))) {
                    List<Map<String, Object>> recommvendorProjectExtendList = (List<Map<String, Object>>) record.get(SouConstant.RE_COM_MV_EN_DOR_PROJECT_EXTEND);
                    recommvendorProjectExtendList.forEach(extend -> {
                        extend.put(fieldName(RecommvendorProjectExtendDto::getExtRecommendNo), docNum);
                        extend.put(fieldName(RecommvendorProjectExtendDto::getRecommendedVendorId), null);
                        extend.put(fieldName(RecommvendorProjectExtendDto::getProjectId), null);
                    });
                }
                if (record.containsKey(SouConstant.RE_COMM_VEN_DOR_LIST) && CollectionUtils.isNotEmpty((Collection) record.get(SouConstant.RE_COMM_VEN_DOR_LIST))) {
                    List<Map<String, Object>> recommvendorList = (List<Map<String, Object>>) record.get(SouConstant.RE_COMM_VEN_DOR_LIST);
                    recommvendorList.forEach(extend -> {
                        extend.put(fieldName(RecommvendorDto::getSouVendorId), null);
                        extend.put(fieldName(RecommvendorDto::getProjectId), null);
                    });
                }
                if (record.containsKey(SouConstant.RECOMMVENDORFILELIST) && CollectionUtils.isNotEmpty((Collection) record.get(SouConstant.RECOMMVENDORFILELIST))) {
                    List<Map<String, Object>> recommvendorFileList = (List<Map<String, Object>>) record.get(SouConstant.RECOMMVENDORFILELIST);
                    recommvendorFileList.forEach(extend -> {
                        extend.put(fieldName(RecommvendorFileDto::getSouFileId), null);
                        extend.put(fieldName(RecommvendorFileDto::getProjectId), null);
                    });
                }
            });
            queryAction.setPayload(records);
            if(CollectionUtils.isNotEmpty(vendorNameGscpNotPassList)) {
                throw new BaseException(MessageFormat.format("存在以下供应商GSCP校验不通过（{0}）: {1}", VENDOR_GSCP_NOT_PASS, vendorNameGscpNotPassList.stream().collect(Collectors.joining("、"))));
            }
            if(CollectionUtils.isNotEmpty(vendorNameTimeLimitList)) {
                throw new BaseException(MessageFormat.format("存在以下时间受限供应商校验不通过: {0}", vendorNameTimeLimitList.stream().collect(Collectors.joining("、"))));
            }
        }


        return super.save(queryAction);
    }

    @Override
    protected QlCondition beforeQuery(QlQueryAction queryAction, QueryParam payload) {
        QlCondition qlCondition = super.beforeQuery(queryAction, payload);
        if (null == qlCondition) {
            qlCondition = MeiQl.newCondition();
        }
        if (RecommType.RecommvendorProject.name().equals(queryAction.getType())) {
            //供应商推荐
            qlCondition.eq(RecommvendorProjectDto::getSouType, SouTypeEnum.recomm.name());
            //供应商地址
            if (payload.getFilter().containsKey(SouConstant.EXTCOMPANYADD) && StringUtils.isNotBlank((String) payload.getFilter().getValueWithoutOperator(SouConstant.EXTCOMPANYADD))) {
                QlType qlType = SchemaUtil.getType(queryAction.getType());
                qlCondition.exists(RecommType.Recommvendor.name(), "v", MeiQl.newCondition()
                        .contains(RecommvendorDto::getExtCompanyAddr, (String) payload.getFilter().getValueWithoutOperator(SouConstant.EXTCOMPANYADD))
                        .eq("v", RecommvendorDto::getProjectId, QlQueryFieldWrapper.field(qlType.getTableName(), RecommvendorProjectDto::getProjectId)
                        ))
                ;
            }
        }
        return qlCondition;
    }

    @Override
    protected QlCondition beforeRead(QlQueryAction queryAction, Collection keys) {
        QlCondition condition = super.beforeRead(queryAction, keys);
        return condition;
    }

    @Override
    public QlResult read(QlQueryAction queryAction) {
        QlResult result = super.read(queryAction);

        if(RecommType.RecommvendorProject.name().equals(queryAction.getType())) {

            if(!Objects.isNull(result) && !Objects.isNull(result.getRef()) && result.getRef().containsKey(SouConstant.RECOMMVENDOR)) {
                //明细行按供应商序号排序
                Collection<Record> recomvendorList = result.getRef().get(SouConstant.RECOMMVENDOR).values();
                recomvendorList = recomvendorList.stream().sorted(Comparator.comparingInt(o -> MapUtils.getInteger(o, fieldName(RecommvendorDto::getSortIndex), 1))).collect(Collectors.toList());
                Collection<Record> records = result.getRefValues(RecommType.RecommvendorProject.name());
                if(CollectionUtils.isNotEmpty(records)) {
                    Collection<Record> finalRecomvendorList = recomvendorList;
                    records.forEach(record -> {
                        record.put(RecommvendorProjectDto::getApplicantNo, extSouInitQueryService.getApplicantNo(record.get(RecommvendorProjectDto::getProjectId)));
                        record.put(RecommvendorProjectDto::getApplicantId, extSouInitQueryService.getApplicantId(record.get(RecommvendorProjectDto::getApplicantNo)));
                        record.put(RecommvendorProjectDto::getPartCancle, extSouInitQueryService.partCancle(record.get(RecommvendorProjectDto::getProjectId)));

                        AtomicReference<String> originalExtRecommendNo = new AtomicReference<>(null);//原单ID
                        if(record.containsKey(SouConstant.RE_COM_MV_EN_DOR_PROJECT_EXTEND)) {
                            Collection<Record> recommvendorProjectExtendList = result.getRef().get(RecommType.RecommvendorProjectExtend.name()).values();
                            recommvendorProjectExtendList.stream().forEach(projectExtend -> {
                                originalExtRecommendNo.set(MapUtils.getString(projectExtend, fieldName(RecommvendorProjectExtendDto::getOriginalExtRecommendNo)));
                            });
                        }

                        //查询原单供应商
                        List<RecommvendorDto> originalVendorList = new ArrayList<>();
                        if(StringUtils.isNotBlank(originalExtRecommendNo.get())) {
                            List<RecommvendorProjectDto> recommvendorProjectDtos = qlService.query(RecommType.RecommvendorProject.name(), MeiQl.newCondition().eq(RecommvendorProjectDto::getSouNo, originalExtRecommendNo.get()), RecommvendorProjectDto.class);
                            if(CollectionUtils.isNotEmpty(recommvendorProjectDtos)) {
                                originalVendorList = qlService.query(RecommType.Recommvendor.name(), MeiQl.newCondition().eq(RecommvendorDto::getProjectId ,recommvendorProjectDtos.get(0).getProjectId()), RecommvendorDto.class);
                            }

                        }
                        Set<Long> originalVendorSet = originalVendorList.stream().map(v -> v.getVendorId()).collect(Collectors.toSet());
                        if(record.containsKey(SouConstant.RE_COMM_VEN_DOR_LIST)) {
                            record.put(fieldName(RecommvendorProjectDto::getRecommvendorList), finalRecomvendorList.stream().map(o -> MapUtils.getLong(o, fieldName(RecommvendorDto::getSouVendorId))).collect(Collectors.toList()));
                        }
                        //原单供应商标识
                        finalRecomvendorList.stream().forEach(vendor -> {
                            Long vendorId = MapUtils.getLong(vendor, fieldName(RecommvendorDto::getVendorId));
                            if(StringUtils.isNotBlank(originalExtRecommendNo.get())) {
                                if(originalVendorSet.contains(vendorId)) {
                                    vendor.put(fieldName(RecommvendorDto::getExtIsAddVendor), YesOrNo.NO.getValue());
                                } else {
                                    vendor.put(fieldName(RecommvendorDto::getExtIsAddVendor), YesOrNo.YES.getValue());
                                }
                            } else {
                                vendor.put(fieldName(RecommvendorDto::getExtIsAddVendor), YesOrNo.NO.getValue());
                            }

                        });
                        List<Map<String, Object>> abl = new ArrayList<>();
                        List<RecommvendorDto> vendorList = qlService.query(RecommType.Recommvendor.name(), MeiQl.newCondition().eq(RecommvendorDto::getProjectId, record.get(RecommvendorProjectDto::getProjectId)), RecommvendorDto.class);

                        if (CollectionUtils.isNotEmpty(vendorList) && vendorList.size() > 1) {
                            List<List<RecommvendorDto>> resultList = dealList(vendorList);
                            Set<Long> vendorIdSet = new HashSet<>();
                            for (List<RecommvendorDto> longs : resultList) {
                                vendorIdSet.add(longs.get(0).getVendorId());
                                vendorIdSet.add(longs.get(1).getVendorId());
                            }
                            Long categoryId = record.get(RecommvendorProjectDto::getExtCategoryId);
                            //获取寻源单中的供应商id
                            Map<Long, List<Long>> projectVendorMap = getProjectVendorMap(vendorIdSet, categoryId);

                            for (List<RecommvendorDto> longs : resultList) {
                                // 查询语句放在for循环中，耗时太久会导致请求超时
                                // long vendorCount= vendorMapper.selectVendor(longs.get(0).getVendorId(), longs.get(1).getVendorId(), categoryId);
                                long vendorCount = 0;
                                log.info("开始匹配供应商：{}，与：{}", longs.get(0).getVendorId(), longs.get(1).getVendorId());
                                for(Long projectId : projectVendorMap.keySet()){
                                    List<Long> vendorIds = projectVendorMap.get(projectId);
                                    // 判断两家供应商是否同时在一张寻源单中
                                    if(vendorIds.contains(longs.get(0).getVendorId()) && vendorIds.contains(longs.get(1).getVendorId())){
                                        vendorCount++;
                                    }
                                    //已满足下面的条件（两家供应商同时在一张寻源单中，最少3次），不需要再遍历
                                    if(vendorCount >= 3){
                                        break;
                                    }
                                }
                                if (vendorCount >= 3) {
                                    Map<String, Object> map = new HashMap<>(15);
                                    map.put("beforeVendorId", longs.get(0).getVendorId());
                                    map.put("beforeVendorCode", longs.get(0).getVendorCode());
                                    map.put("beforeVendorName", longs.get(0).getVendorName());
                                    map.put("afterVendorId", longs.get(1).getVendorId());
                                    map.put("afterVendorCode", longs.get(1).getVendorCode());
                                    map.put("afterVendorName", longs.get(1).getVendorName());
                                    abl.add(map);
                                }
                            }
                        }
                        record.put("accompanyBiddingList", abl);
                    });
                }
            }
        }

        return result;
    }

    /**
     * 获取寻源单中的供应商id
     * @param vendorIds
     * @param categoryId
     * @return
     */
    private Map<Long, List<Long>> getProjectVendorMap(Collection<Long> vendorIds, Long categoryId) {
        log.info("根据供应商和品类id，查询寻源id和供应商id");
        Map<Long, List<Long>> projectVendorMap = new HashMap<>();
        //查询供应商信息
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("vendorIds", vendorIds);
        paramMap.put("categoryId", categoryId);
        List<SouVendor> souVendorList = vendorMapper.selectProjectVendor(paramMap);

        if(CollectionUtils.isNotEmpty(souVendorList)){
            log.info("getProjectVendorMap size="+souVendorList.size());
            //根据寻源id进行分组
            Map<Long, List<SouVendor>> souVendorrMap = souVendorList.stream().collect(Collectors.groupingBy(SouVendor::getProjectId));
            for(Long projectId : souVendorrMap.keySet()){
                List<SouVendor> list = souVendorrMap.get(projectId);
                List<Long> vendorIdList = list.stream().map(SouVendor::getVendorId).collect(Collectors.toList());
                projectVendorMap.put(projectId, vendorIdList);
            }
        }
        log.info("获取寻源和供应商id projectVendorMap="+ JSON.toJSONString(projectVendorMap));
        return projectVendorMap;
    }

    private static List<List<RecommvendorDto>> dealList(List<RecommvendorDto> demandList) {
        List<List<RecommvendorDto>> reList = new ArrayList<>();
        for (int i = 0; i < demandList.size(); i++) {
            for (int j = i + 1; j < demandList.size(); j++) {
                List<RecommvendorDto> aa = new ArrayList<>();
                aa.add(demandList.get(i));
                aa.add(demandList.get(j));
                reList.add(aa);
            }
        }
        return reList;
    }

    @Override
    protected void afterRead(QlQueryAction queryAction, Collection<Record> records) {
        super.afterRead(queryAction, records);
    }


    public RecommvendorRepository() {
        super();
        this.register("submit", this::submit, true, "供应商推荐提交");
        this.register("queryRisk", this::queryRisk, true, "供应商风险查看");
        this.register("vendorUpdate", this::vendorUpdate, true, "更新");
        this.register("vendorUpdateAsPreBid", this::vendorUpdateAsPreBid, true, "从标前交流获取供应商");
        this.register("recommvendorQuickQuery", this::recommvendorQuickQuery, true, "供应商推荐快速查询");
        this.register("queryHisScore", this::queryHisScore, true, "供应商履约信息");
    }

    @Override
    protected void beforeDelete(QlQueryAction queryAction, Collection<Record> records) {
        super.beforeDelete(queryAction, records);
        if(RecommType.RecommvendorProject.name().equals(queryAction.getType())) {
            //删除
            List<RecommvendorProjectDto> params = SouObjectXUtil.convertTargetObj(records, new TypeReference<List<RecommvendorProjectDto>>() {
            });

            souRecommvendorRiskService.rollbackPlanPool(params);
        }
    }

    /**
     * 查看风险
     *
     * @param queryAction
     * @return
     */
    public QlResult queryRisk(QlQueryAction queryAction) {

        RecommvendorProjectDto params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<RecommvendorProjectDto>() {
        });
        RecommvendorRiskDto riskDto = souRecommvendorRiskService.vendorRiskNew(params);
        RecommvendorDto recommvendorDto = new RecommvendorDto();
        recommvendorDto.setSouVendorId(1L);
        QlResult qlResult = ResultUtil.build(queryAction, fieldName(RecommvendorDto::getSouVendorId), Collections.singletonList(recommvendorDto), false);
        Collection<Record> records = qlResult.getRefValues(RecommType.Recommvendor.name());
        records.forEach(record -> {
            record.put("vendorRisk", riskDto);
        });
        return qlResult;
    }

    public QlResult vendorUpdate(QlQueryAction queryAction) {
        RecommvendorDto params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), RecommvendorDto.class);
        Long projectId = souRecommvendorRiskService.vendorUpdateAsSouReq(params.getProjectId());
        QlQueryWrapper queryWrapper = QlWrappers.query(RecommType.Recommvendor.name()).eq(RecommvendorDto::getProjectId, projectId);
        QlResult qlResult = MeiQl.toResult(RecommType.Recommvendor.name(), fieldName(RecommvendorDto::getSouVendorId), qlService.queryByWrapper(queryWrapper, Record.class));
        return qlResult;
    }

    public QlResult vendorUpdateAsPreBid(QlQueryAction queryAction) {
        RecommvendorDto params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), RecommvendorDto.class);
        Long projectId = souRecommvendorRiskService.vendorUpdateAsPreBid(params.getProjectId());
        QlQueryWrapper queryWrapper = QlWrappers.query(RecommType.Recommvendor.name()).eq(RecommvendorDto::getProjectId, projectId);
        QlResult qlResult = MeiQl.toResult(RecommType.Recommvendor.name(), fieldName(RecommvendorDto::getSouVendorId), qlService.queryByWrapper(queryWrapper, Record.class));
        return qlResult;
    }

    public QlResult recommvendorQuickQuery(QlQueryAction queryAction) {
        RecommvendorQuickQueryParam param = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), RecommvendorQuickQueryParam.class);
        PageInfo<RecommvendorDto> recommvendorDtoList = souRecommvendorQuitSearchService.queryVendor(param);
        return ResultUtil.build(queryAction, fieldName(RecommvendorDto::getVendorId), recommvendorDtoList, false);
    }

    /**
     * 供应商履约信息
     * @param queryAction
     * @return
     */
    public QlResult queryHisScore(QlQueryAction queryAction) {

        RecommvendorHisScoreQueryParam params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<RecommvendorHisScoreQueryParam>() {
        });
        AssertUtils.notNull(params.getApplicantNo(), "缺少数据");
        AssertUtils.notEmpty(params.getRecommvendorList(),"缺少数据");

        List<ExtSouDemand> souDemandList = qlService.queryByWrapper(
                QlWrappers.query(RecommType.RecommvendorDemand.name()).eq(ExtSouDemand::getApplicantNo, params.getApplicantNo()), ExtSouDemand.class);

        if(CollectionUtils.isEmpty(souDemandList)){
            return QlResult.empty();
        }

        List<RecommvendorProjectDto> recommvendorProjectDtos = qlService.queryByWrapper(
                QlWrappers.query(RecommType.RecommvendorProject.name()).eq(RecommvendorProjectDto::getSouType, SouTypeEnum.recomm.name())
                        .in(RecommvendorProjectDto::getProjectId, souDemandList.stream().map(h -> h.getProjectId()).collect(Collectors.toList())),
                RecommvendorProjectDto.class);

        if(CollectionUtils.isEmpty(recommvendorProjectDtos)){
            return QlResult.empty();
        }

        List<Long> vendorIdList = params.getRecommvendorList().stream().map(RecommvendorDto::getVendorId).collect(Collectors.toList());
        RecommvendorProjectDto projectDto = recommvendorProjectDtos.get(0);
        //构建请求参数，请求 contract 模块
        ProjectScoreHeader projectScoreHeader = new ProjectScoreHeader();
        projectScoreHeader.setCategoryId(projectDto.getExtCategoryId());
        projectScoreHeader.setCompanyIdList(vendorIdList);
        projectScoreHeader.setPerformanceType(PerformanceCodeEnum.PROJECT.getCode());
        projectScoreHeader.setCalcDateStart(params.getCalcDateStart() == null ? LocalDate.now().minusYears(3): params.getCalcDateStart());
        projectScoreHeader.setCalcDateEnd(params.getCalcDateEnd() == null ? LocalDate.now(): params.getCalcDateStart());
        //推荐供应商数量有限,写死分页
        projectScoreHeader.setPageNum(1);
        projectScoreHeader.setPageSize(100);
        PageInfo<ProjectScoreHeader> projectScoreHeaderPageInfo = souExtPerformanceClient.listPage(projectScoreHeader);
        List<ProjectScoreHeader> list = projectScoreHeaderPageInfo.getList();

        RecommvendorDto recommvendorDto = new RecommvendorDto();
        recommvendorDto.setSouVendorId(1L);
        QlResult qlResult = ResultUtil.build(queryAction, fieldName(RecommvendorDto::getSouVendorId), Collections.singletonList(recommvendorDto), false);
        Collection<Record> records = qlResult.getRefValues(RecommType.Recommvendor.name());
        records.forEach(record -> {
            record.put("hisScore", list.stream()
                    .sorted(Comparator.comparingLong(ProjectScoreHeader::getCompanyId)
                            .thenComparing(ProjectScoreHeader::getScore,Comparator.reverseOrder()))
                    .collect(Collectors.toList())
            );
        });

        return qlResult;
    }
}
