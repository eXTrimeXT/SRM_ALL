package com.midea.cloud.srm.supauth.review.service.flow.dingtalk;

import com.alibaba.fastjson.JSON;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.BeanMapUtils;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.feign.supplierauth.SupplierAuthClient;
import com.midea.cloud.srm.model.file.upload.dto.FileuploadDTO;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.supplierauth.entry.entity.FileRecord;
import com.midea.cloud.srm.model.supplierauth.review.dto.ReviewFormDTO;
import com.midea.cloud.srm.model.supplierauth.review.entity.CateJournal;
import com.midea.cloud.srm.model.supplierauth.review.entity.ReviewForm;
import com.midea.cloud.srm.model.third.dingtalk.v1_0.dto.ProcessInstanceCreateRequest;
import com.midea.cloud.srm.model.third.dingtalk.v1_0.dto.process.Approver;
import com.midea.cloud.srm.model.third.dingtalk.v1_0.dto.process.FormComponentValueItem;
import com.midea.cloud.srm.model.third.dingtalk.v1_0.dto.process.ProcessFileInfo;
import com.midea.cloud.srm.model.third.dingtalk.v1_0.dto.storage.CommitFileResponseDentry;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @description:
 * @date: 2023/7/31 20:54
 * @author 100014323
 */
@Service
public class ReviewFormDingtalkFlowServiceImpl implements IFlowBusinessCallbackService {

    @Resource(name = "reviewFormFlowServiceImpl")
    private IFlowBusinessCallbackService reviewFormFlowServiceImpl;

    @Autowired
    private SupplierAuthClient supplierAuthClient;

    @Autowired
    private RbacClient rbacClient;

    @Autowired
    public FileCenterClient fileCenterClient;

    @Override
    public void submitFlow(Long businessId, String param) throws Exception {
        reviewFormFlowServiceImpl.submitFlow(businessId, param);
    }

    @Override
    public void passFlow(Long businessId, String param) throws Exception {
        reviewFormFlowServiceImpl.passFlow(businessId, param);
    }

    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {
        reviewFormFlowServiceImpl.rejectFlow(businessId, param);
    }

    @Override
    public void withdrawFlow(Long businessId, String param) throws Exception {
        reviewFormFlowServiceImpl.withdrawFlow(businessId, param);
    }

    @Override
    public void destoryFlow(Long businessId, String param) throws Exception {
        reviewFormFlowServiceImpl.destoryFlow(businessId, param);
    }

    @Override
    public String getVariableFlow(Long businessId, String param) throws Exception {
        return reviewFormFlowServiceImpl.getVariableFlow(businessId, param);
    }

    /**
     * 1、发起用户，如果不存在，则提示失败，让先绑定用户
     *      用户支持方式：
     *      1.1、手机号获取：手机号获取设置有效期，在指定时间段内不重新获取，或者通过清理第三方绑定用户表
     *      1.2、iam获取：iam使用实时获取方式-feign接口。需要在IAM上已经绑定此用户
     * 2、上传附件，新增第三方附件绑定表，上传成功后，更新第三方附件表
     * 3、流程编码：根据不同流程，二开固定或者根据指定规则进行自定义配置
     * 4、审批用户，同发起用户，同时获取
     * @param businessId
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public String getDataPushFlow(Long businessId, String param) throws Exception {
        ReviewFormDTO reviewFormDTO = supplierAuthClient.getReviewFormDTO(businessId);
        ReviewForm reviewForm = reviewFormDTO.getReviewForm();
        if (reviewForm == null) {
            throw new BaseException("单据不存在：" + businessId);
        }

        Long createdUserId = reviewForm.getCreatedId();
        List<Long> approveUserIdList = new ArrayList<>();
        approveUserIdList.add(createdUserId);
        Map<Long, String> userId2OpenIdMap = rbacClient.listUserId2DingtalkOpenId(approveUserIdList);

        ProcessInstanceCreateRequest processInstanceCreateRequest = new ProcessInstanceCreateRequest();

        String originatorUserId = userId2OpenIdMap.get(createdUserId);
        processInstanceCreateRequest.setOriginatorUserId(originatorUserId);
        processInstanceCreateRequest.setProcessCode("PROC-DEE616DA-8ECD-41C7-9D89-8AC438F06D79");

        List<FormComponentValueItem> formComponentValues = new ArrayList<>();
        {
            FormComponentValueItem formComponentValueItem = new FormComponentValueItem();
            formComponentValueItem.setName("资质审查类型");
            formComponentValueItem.setValue(reviewForm.getQuaReviewType());
            formComponentValues.add(formComponentValueItem);
        }
        {
            FormComponentValueItem formComponentValueItem = new FormComponentValueItem();
            formComponentValueItem.setName("资质审查单号");
            formComponentValueItem.setValue(reviewForm.getReviewFormNumber());
            formComponentValues.add(formComponentValueItem);
        }
        {
            FormComponentValueItem formComponentValueItem = new FormComponentValueItem();
            formComponentValueItem.setName("资质审查说明");
            formComponentValueItem.setValue(reviewForm.getReviewExplain());
            formComponentValues.add(formComponentValueItem);
        }

        {
            List<CateJournal> cateJournalList = reviewFormDTO.getCateJournals();
            if (cateJournalList != null && !cateJournalList.isEmpty()) {
                FormComponentValueItem formComponentValueItem = new FormComponentValueItem();
                formComponentValueItem.setName("引入品类");

                List<List<FormComponentValueItem>> tableList = new ArrayList<>();
                for (CateJournal cateJournal: cateJournalList) {
                    List<FormComponentValueItem> lineList = new ArrayList<>();

                    FormComponentValueItem detailItem = new FormComponentValueItem();
                    detailItem.setName("品类");
                    detailItem.setValue(cateJournal.getCategoryName());
                    lineList.add(detailItem);

                    tableList.add(lineList);
                }

                formComponentValueItem.setValue(JSON.toJSONString(tableList));

                formComponentValues.add(formComponentValueItem);
            }
        }

        extracted(reviewFormDTO, formComponentValues);

        processInstanceCreateRequest.setFormComponentValues(formComponentValues);

        List<Approver> approvers = new ArrayList<>();
        Approver approver = new Approver();
        approver.setActionType("NONE");
        approver.setUserIds(Collections.singletonList(originatorUserId));
        approvers.add(approver);
        processInstanceCreateRequest.setApprovers(approvers);

        return JSON.toJSONString(processInstanceCreateRequest);
    }

    /**
     * 附件组装
     * @param reviewFormDTO
     * @param formComponentValues
     * @throws Exception
     */
    private void extracted(ReviewFormDTO reviewFormDTO, List<FormComponentValueItem> formComponentValues) throws Exception {
        List<FileRecord> fileRecordList = reviewFormDTO.getFileRecords();
        if (fileRecordList != null && !fileRecordList.isEmpty()) {
            List<Long> fileIdList = fileRecordList.stream().map(FileRecord::getFileId).filter(Objects::nonNull).collect(Collectors.toList());
            FileuploadDTO fileuploadDTO = new FileuploadDTO();
            fileuploadDTO.setFileIds(fileIdList);
            List<Fileupload> fileUploadList = fileCenterClient.uploadThirdBatch(fileuploadDTO);
            Map<Long, String> fileuploadMap = BeanMapUtils.list2Map(fileUploadList, Fileupload::getFileuploadId, Fileupload::getThirdFileInfo);

            FormComponentValueItem formComponentValueItem = new FormComponentValueItem();
            formComponentValueItem.setName("表格");

            List<List<FormComponentValueItem>> tableList = new ArrayList<>();
            for (FileRecord fileRecord: fileRecordList) {
                List<FormComponentValueItem> lineList = new ArrayList<>();
                {
                    FormComponentValueItem detailItem = new FormComponentValueItem();
                    detailItem.setName("附件");

                    String thirdFileInfo = fileuploadMap.get(fileRecord.getFileId());
                    CommitFileResponseDentry commitFileResponseDentry = JSON.parseObject(thirdFileInfo, CommitFileResponseDentry.class);
                    ProcessFileInfo processFileInfo = new ProcessFileInfo(commitFileResponseDentry);
                    List<ProcessFileInfo> processFileInfoList = Arrays.asList(processFileInfo);
                    detailItem.setValue(JSON.toJSONString(processFileInfoList));
                    lineList.add(detailItem);
                }
                {
                    FormComponentValueItem scoreItem = new FormComponentValueItem();
                    scoreItem.setName("得分");
                    scoreItem.setValue(fileRecord.getScore() != null ? fileRecord.getScore() + "" : "");

                    lineList.add(scoreItem);
                }

                tableList.add(lineList);
            }

            formComponentValueItem.setValue(JSON.toJSONString(tableList));

            formComponentValues.add(formComponentValueItem);

        }
    }

}
