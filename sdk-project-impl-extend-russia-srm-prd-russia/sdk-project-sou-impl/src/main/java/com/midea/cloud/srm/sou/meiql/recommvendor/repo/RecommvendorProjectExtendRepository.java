package com.midea.cloud.srm.sou.meiql.recommvendor.repo;

import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.recommvendor.dto.*;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouRecommendedVendor;
import com.midea.cloud.srm.sou.bid.openrecords.service.IExtNpmSouOpenBidRecordService;
import com.midea.cloud.srm.sou.meiql.recommvendor.mapper.RecommvendorMapper;
import com.midea.cloud.srm.sou.meiql.recommvendor.service.SouRecommvendorQuitSearchService;
import com.midea.cloud.srm.sou.meiql.recommvendor.service.SouRecommvendorRiskService;
import com.midea.cloud.srm.sou.sourcing.init.service.ExtSouInitQueryService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtNpmSouOrderService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouDemandService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 100014337
 */
@Api("供应商推荐自定义组件")
@Component
@Slf4j
public class RecommvendorProjectExtendRepository extends CrudRepository {

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
    private RecommvendorMapper recommvendorMapper;

    private static final String PREINVITE_TENDERS = "preInviteTenders";

    public RecommvendorProjectExtendRepository() {
        super();
    }

    /**
     * 查询
     * @param queryAction 参数
     * @return 返回
     */
    @Override
    public QlResult query(QlQueryAction queryAction) {
        QlResult qlResult = super.query(queryAction);
        return qlResult;
    }

    @Override
    protected void afterQuery(QlQueryAction queryAction, Collection<Record> records) {
        super.afterQuery(queryAction, records);

        List<Map<String, Object>> projectList = new ArrayList<>(16);
        List<Long> projectIdList = new ArrayList<>(16);

        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, -3);

        records.forEach(e -> {
            if (StringUtils.isNotBlank(e.getString(PREINVITE_TENDERS))) {
                return;
            }
            Long pId = e.getLong("projectId");

            if(!Objects.isNull(pId)) {
                projectIdList.add(pId);
            }

//            ExtSouProject extSouProject = projectService.getById(pId);
//            String extCategoryCode = extSouProject.getExtCategoryCode();
//            LambdaQueryWrapper<ExtSouProject> qw = new LambdaQueryWrapper<>();
//            qw.eq(ExtSouProject::getExtCategoryCode, extCategoryCode);
//            qw.in(ExtSouProject::getProjectStatus, "ARCHIVE_TODO", "ARCHIVE_DONE");
//            List<ExtSouProject> pList = projectService.list(qw);
//            List<String> reList = new ArrayList<>();
//            int i = 1;
//            for (ExtSouProject souProject : pList) {
//                if (reList.size() >= 10) {
//                    break;
//                }
//                //查询中落表通知头表
//                List<BidNoticeDTO> noticeDTOList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.BidNotice.getCode())
//                                .eq(BidNoticeDTO::getProjectId, souProject.getProjectId())
//                                .eq(BidNoticeDTO::getStatus, BidNoticeStatusEnum.APPROVED.getCode())
//                                .eq(BidNoticeDTO::getType, CaTypeEnum.APPLY.getCode())
//                                .orderByDesc(BidNoticeDTO::getCreationDate)
//                        , BidNoticeDTO.class);
//
//                if (CollectionUtils.isEmpty(noticeDTOList)) {
//                    break;
//                }
//                BidNoticeDTO bidNoticeDTO = noticeDTOList.get(0);
//                //查看中/落标通知
//
//                List<BidNoticeDetailDTO> noticeDetailDTOList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.BidNoticeDetail.getCode())
//                                .eq(BidNoticeDetailDTO::getBidNoticeId, bidNoticeDTO.getBidNoticeId())
//                                .eq(BidNoticeDetailDTO::getIsWin, "Y")
//                                .ge(BidNoticeDetailDTO::getCreationDate, calendar.getTime())
//                                .orderByAsc(BidNoticeDetailDTO::getVendorId)
//                        , BidNoticeDetailDTO.class);
//                for (BidNoticeDetailDTO bidNoticeDetailDTO : noticeDetailDTOList) {
//                    reList.add(String.format("%d、%S,%S,%S", i++, souProject.getSouName(), souProject.getExtProjectNo(), bidNoticeDetailDTO.getVendorName()));
//                    if (reList.size() >= 10) {
//                        break;
//                    }
//                }
//            }
//            if (CollectionUtils.isNotEmpty(reList)) {
//                e.put(ExtSouRecommendedVendor::getPreInviteTenders, StringUtils.join(reList, ";"));
//            }
        });

        //性能优化
        Map<Long, ExtSouProject> projectMap = new HashMap<>(16);

        if(CollectionUtils.isNotEmpty(projectIdList)) {
            List<ExtSouProject> extSouProjects = projectService.listByIds(projectIdList);
            if(CollectionUtils.isNotEmpty(extSouProjects)) {
                extSouProjects.stream().map(p -> p.getExtCategoryId()).distinct().forEach(categoryId -> {
                    Map<String, Object> query = new HashMap<>(16);
                    query.put("extCategoryId", categoryId);
                    query.put("creationDate", calendar.getTime());
                    projectList.add(query);
                });

                projectMap = extSouProjects.stream().collect(Collectors.toMap(k -> k.getProjectId(), Function.identity(), (k1, k2) -> k2));
            }
        }

        if(CollectionUtils.isNotEmpty(projectList)) {


            Map<String, Object> query = new HashMap<>(16);
            query.put("projectList", projectList);
            List<RecommvendorSouNoticeInfoDto> noticeInfoDtoList = recommvendorMapper.queryNoticeInfo(query);

            Map<Long, List<RecommvendorSouNoticeInfoDto>> noticeInfoMap = noticeInfoDtoList.stream().collect(Collectors.groupingBy(RecommvendorSouNoticeInfoDto::getExtCategoryId));

            Map<Long, ExtSouProject> finalProjectMap = projectMap;
            records.stream().filter(record -> StringUtils.isBlank(record.get(ExtSouRecommendedVendor::getPreInviteTenders))).forEach(record -> {
                Long projectId = record.get(ExtSouRecommendedVendor::getProjectId);
                ExtSouProject project = finalProjectMap.get(projectId);
                if(!Objects.isNull(project)) {
                    List<RecommvendorSouNoticeInfoDto> recommvendorSouNoticeInfoDtoList = noticeInfoMap.getOrDefault(project.getExtCategoryId(), new ArrayList<>(16));
                    AtomicReference<Integer> index = new AtomicReference<>(1);
                    String preInviteTenders = recommvendorSouNoticeInfoDtoList.stream().map(info -> {
                        return String.format("%d、%S,%S,%S", index.getAndSet(index.get() + 1), info.getSouName(), info.getExtProjectNo(), info.getVendorName());
                    }).collect(Collectors.joining(SrmConstant.SIG_1));
                    record.put(ExtSouRecommendedVendor::getPreInviteTenders, preInviteTenders);
                }
            });

        }
    }


    @Override
    public QlResult read(QlQueryAction queryAction) {
        QlResult result = super.read(queryAction);
        return result;
    }
}
