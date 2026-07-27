package com.midea.cloud.srm.sou.bid.event;

import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.pj.aihelper.BidReviewDto;
import com.midea.cloud.srm.model.pj.enums.BidReviewEnum;
import com.midea.cloud.srm.model.sou.answer.dto.AnswerDTO;
import com.midea.cloud.srm.model.sou.answer.dto.AnswerVendorDTO;
import com.midea.cloud.srm.model.sou.answer.dto.ReplayFileDTO;
import com.midea.cloud.srm.model.sou.answer.enums.AnswerConfirmStatusEnum;
import com.midea.cloud.srm.model.sou.enums.ScoreConfigItemEnum;
import com.midea.cloud.srm.model.sou.enums.TypeEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouOrderFileDto;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.sou.bid.enums.ReviewFileTypeEnum;
import com.midea.cloud.srm.sou.sourcing.init.service.ExtSouInitQueryService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouOrderFileService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.mideacloud.common.event.Event;
import com.mideacloud.common.event.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author GW00302625
 */
@Slf4j
@Service
public class ExtBidSouEvent {

    @Resource
    private EventPublisher eventPublisher;
    @Autowired
    private IExtSouProjectService projectService;
    @Autowired
    private ExtSouInitQueryService extSouInitQueryService;
    @Autowired
    private IExtSouOrderFileService souOrderFileService;
    @Autowired
    private QlService qlService;

    public void pushTechEvent(Long projectId) {
        try {
            ExtSouProject souProject = projectService.getById(projectId);
            if(YesOrNo.YES.getValue().equals(souProject.getExtHideKeyInfo())) {
                return;
            }
            this.onOpenTech(projectId);
        } catch (Exception e) {
            log.error("ExtBidSouEvent pushTechEvent error", e);
        }
    }

    public void pushAnswerEvent(Long answerId) {
        try {
            AnswerDTO answerDTO = qlService.readByKey(MqlType.ANSWER, answerId,AnswerDTO.class);
            if(!AnswerConfirmStatusEnum.COMFIRMED.getCode().equals(answerDTO.getAnswerStatus())) {
                return;
            }
            ExtSouProject souProject = projectService.getById(answerDTO.getProjectId());
            if(YesOrNo.YES.getValue().equals(souProject.getExtHideKeyInfo())) {
                return;
            }
            this.onAnswerConfirm(souProject.getProjectId(),answerId);
        } catch (Exception e) {
            log.error("ExtBidSouEvent pushAnswerEvent error", e);
        }
    }

    private void onOpenTech(Long projectId) {
        BidReviewDto bidReviewDto = new BidReviewDto();
        fillCompany(projectId, bidReviewDto);
        fillReviewItem(projectId, bidReviewDto);
        if(CollectionUtils.isEmpty(bidReviewDto.getCompanyList())){
            return;
        }
        Event<BidReviewDto> event = new Event<>();
        event.setDomainName("SRM_PROJECT").setBusinessName("REVIEW_TOPIC").setBusinessAction(BidReviewEnum.TECH.name()).setId(projectId.toString()).setOrderly(false).setHashKey("the-key").setSynchronous(true).setData(bidReviewDto);
        this.eventPublisher.publish(event);
    }

    private void onAnswerConfirm(Long projectId, Long answerId) {
        BidReviewDto bidReviewDto = new BidReviewDto();
        fillCompany(projectId, answerId, bidReviewDto);
        if(CollectionUtils.isEmpty(bidReviewDto.getCompanyList())){
            return;
        }
        Event<BidReviewDto> event = new Event<>();
        event.setDomainName("SRM_PROJECT").setBusinessName("REVIEW_TOPIC").setBusinessAction(BidReviewEnum.ANSWER.name()).setId(projectId.toString()).setOrderly(false).setHashKey("the-key").setSynchronous(true).setData(bidReviewDto);
        this.eventPublisher.publish(event);
    }


    private void fillCompany(Long projectId, BidReviewDto bidReviewDto) {
        bidReviewDto.setProjectId(projectId);
        bidReviewDto.setCompanyList(new ArrayList<>());
        //获取供应商文件
        souOrderFileService.getTechPlan(projectId).getOrderFileList().stream()
                .filter(e -> ReviewFileTypeEnum.isValidReviewFileType(e.getOrderFileName()))
                .collect(Collectors.groupingBy(ExtSouOrderFileDto::getVendorId, Collectors.toList()))
                .forEach((vendorId, fileList) -> {
                    BidReviewDto.Company company = new BidReviewDto.Company();
                    company.setCompanyId(vendorId);
                    company.setCompanyName(fileList.get(0).getVendorName());
                    List<BidReviewDto.File> techList = fileList.stream().map(e -> {
                        BidReviewDto.File file = new BidReviewDto.File();
                        file.setFileId(e.getOrderDocId());
                        file.setFileName(e.getOrderFileName());
                        file.setFileType(BidReviewEnum.TECH.name());
                        return file;
                    }).collect(Collectors.toList());
                    company.setFileList(techList);
                    bidReviewDto.getCompanyList().add(company);
                });
    }
    private void fillCompany(Long projectId, Long answerId, BidReviewDto bidReviewDto) {
        bidReviewDto.setProjectId(projectId);
        bidReviewDto.setCompanyList(new ArrayList<>());
        //获取供应商文件
        List<AnswerVendorDTO> answerVendorDTOS = qlService.queryByWrapper(QlWrappers.query(TypeEnum.AnswerVendor.getCode())
                .eq(AnswerVendorDTO::getAnswerId,answerId)
                .eq(AnswerVendorDTO::getConfirmStatus,AnswerConfirmStatusEnum.COMFIRMED.getCode())
                .isNotNull(AnswerVendorDTO::getReplayId),AnswerVendorDTO.class);
        if(CollectionUtils.isEmpty(answerVendorDTOS)){
            return;
        }
        List<ReplayFileDTO> replayFileDtos = qlService.queryByWrapper(QlWrappers.query(TypeEnum.ReplayFile.getCode())
                .in(ReplayFileDTO::getReplayId,answerVendorDTOS.stream().map(AnswerVendorDTO::getReplayId).collect(Collectors.toList()))
                .eq(ReplayFileDTO::getIsDelete,YesOrNo.NO.getValue()),ReplayFileDTO.class);
        if(CollectionUtils.isEmpty(replayFileDtos)){
            return;
        }
        Map<Long, List<ReplayFileDTO>> replayFileMap = replayFileDtos.stream()
                .filter(e -> ReviewFileTypeEnum.isValidReviewFileType(e.getFileName()))
                .collect(Collectors.groupingBy(ReplayFileDTO::getReplayId, Collectors.toList()));

        answerVendorDTOS.stream()
                .filter(e -> replayFileMap.containsKey(e.getReplayId()))
                .forEach(e -> {
                    BidReviewDto.Company company = new BidReviewDto.Company();
                    company.setCompanyId(e.getVendorId());
                    company.setCompanyName(e.getVendorName());
                    List<BidReviewDto.File> fileList = replayFileMap.get(e.getReplayId()).stream().map(r -> {
                        BidReviewDto.File file = new BidReviewDto.File();
                        file.setFileId(r.getFileId());
                        file.setFileName(r.getFileName());
                        file.setFileType(BidReviewEnum.ANSWER.name());
                        return file;
                    }).collect(Collectors.toList());
                    company.setFileList(fileList);
                    bidReviewDto.getCompanyList().add(company);
                });

    }

    private void fillReviewItem(Long projectId, BidReviewDto bidReviewDto) {
        List<BidReviewDto.ReviewItem> reviewItemList = extSouInitQueryService.getScoreRule(projectId).stream()
                .filter(e -> Arrays.asList(ScoreConfigItemEnum.QUA_REVIEW.getCode(), ScoreConfigItemEnum.BUS_REVIEW.getCode(), ScoreConfigItemEnum.TEH_REVIEW.getCode()).contains(e.getScoreItem()))
                .map(e -> {
                    BidReviewDto.ReviewItem reviewItem = new BidReviewDto.ReviewItem();
                    reviewItem.setItemId(e.getScoreRuleId());
                    reviewItem.setItemName(e.getReviewItem());
                    reviewItem.setItemDescript(e.getScoreDesc());
                    return reviewItem;
                }).collect(Collectors.toList());
        bidReviewDto.setReviewItemList(reviewItemList);
    }




}
