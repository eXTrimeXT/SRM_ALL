package com.midea.cloud.srm.sou.req.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.midea.cloud.common.constant.DingTalkConstant;
import com.midea.cloud.common.dingtalks.DingTalkClient;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.meiql.api.function.SFunction;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.meiql.core.util.ResultUtil;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.sou.enums.RecommvendorSourceFromEnum;
import com.midea.cloud.srm.model.sou.enums.SouRecommvendorTypeEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouRecommVendorInfoDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouProjectDto;
import com.midea.cloud.srm.model.sou.req.SouReqApply;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.req.enums.SouReqApplyStatusEnum;
import com.midea.cloud.srm.model.sou.req.vo.ApplyInfoVO;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouRecommendedVendor;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.sup.association.entity.ExtSupAssociation;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementStatusEnum;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.recommvendor.service.ExtSouRecommVendorService;
import com.midea.cloud.srm.sou.req.mapper.SouReqApplyMapper;
import com.midea.cloud.srm.sou.req.mapper.SouReqHeadMapper;
import com.midea.cloud.srm.sou.req.service.SouReqApplyService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 寻源需求单报名表
 *
 * @author xiaym13 xiaym13@meicloud.com
 * @since 1.0.0 2023-10-04
 */
@Service
@AllArgsConstructor
@Slf4j
public class SouReqApplyServiceImpl extends BaseServiceImpl<SouReqApplyMapper, SouReqApply> implements SouReqApplyService {
    @Autowired
    private ExtSouRecommVendorService souRecommVendorService;
    @Autowired
    protected QlService qlService;
    @Autowired
    private BaseClient baseClient;
    @Autowired
    private QlOpenClient qlOpenClient;

    @Autowired
    private SouReqHeadMapper souReqHeadMapper;

    @Autowired
    private RbacClient rbacClient;

    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    @Override
    public ApiExtSouRecommVendorInfoDTO createVendorRecommend(QlQueryAction queryAction) {
        List<Record> records = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        List<Long> ids = records.stream().map(record -> record.get(SouReqApply::getReqHeadId)).collect(Collectors.toList());
        //报名数据
        List<SouReqApply> reqHeadList = qlService.queryByWrapper(QlWrappers.query(MqlType.SOU_REQ_APPLY_BUYER).in(SouReqApply::getReqHeadId, ids).eq(SouReqApply::getApplyStatus, SouReqApplyStatusEnum.SUCCESS_SIGNUP.getCode()), SouReqApply.class);
        //寻源单头表
        SouReqHead reqHead = qlService.readByKey(MqlType.SOU_REQ_HEAD_BUYER, ids.get(0), SouReqHead.class);
        //初始化请求保存要请供应商参数
        ApiExtSouRecommVendorInfoDTO param = new ApiExtSouRecommVendorInfoDTO();
        ExtSouProjectDto project = new ExtSouProjectDto();
        //标的物信息
        ExtSouRecommendedVendor recommendedVendor = new ExtSouRecommendedVendor();
        recommendedVendor.setSourceFrom(RecommvendorSourceFromEnum.SOU.name());
        recommendedVendor.setRcommendType(SouRecommvendorTypeEnum.RECOMM.getCode());
        recommendedVendor.setPublishFlag(reqHead.getIsPublic());
        recommendedVendor.setSouRequirementId(reqHead.getReqHeadId());
        recommendedVendor.setSouRequirementNo(reqHead.getReqHeadNo());
        //项目概况与招标范围
        recommendedVendor.setProjectRemark(reqHead.getProjectScope());
        //供应商资质要求
        recommendedVendor.setVendorFlairAdjure(reqHead.getVendorQualReq());
        //业绩要求
        recommendedVendor.setVendorBizAdjure(reqHead.getPerformanceReq());
        //是否公示
        recommendedVendor.setPublishFlag(reqHead.getIsPublic());
        //板块，取申请单
        project.setExtOrgBuName(reqHead.getOrgBuName());
        project.setExtOrgBuCode(reqHead.getOrgBuCode());
        project.setExtOrgBuId(reqHead.getOrgBuId());
        //公司名称,取申请单
        project.setExtOrgOuName(reqHead.getOrgName());
        project.setExtOrgOuCode(reqHead.getOrgCode());
        project.setExtOrgOuId(reqHead.getOrgId());
        //需求部门
        project.setExtApplicantDepart(reqHead.getReqDepartment());
        //寻源单名号,申请单号带出
        project.setSouNo(reqHead.getReqHeadNo());
        //创建人
        project.setCreatedFullName(reqHead.getCreatedFullName());
        //创建时间
        project.setCreationDate(reqHead.getCreationDate());
        //最后更新时间
        project.setLastUpdateDate(reqHead.getLastUpdateDate());
        //是否公示
        //技术负责人
        project.setExtTechPrincipal(reqHead.getTechnicalUserName());
        //电话
        project.setTel(reqHead.getTechPhone());
        project.setProjectStatus("DRAFT");
        //招标负责人
        project.setExtSouPrincipal(reqHead.getSouPersonUserName());
        //来源单据号
        project.setSourceFromNo(reqHead.getReqHeadNo());
        //来源单据ID
        project.setSourceFromId(reqHead.getReqHeadId());
        //需求来源,来源类型
        project.setSourceFromType(reqHead.getRequireFrom());
        //预算（万元）
        project.setExtBudget(reqHead.getTotalAmountByTenKilo());
        //品类ID
        project.setExtCategoryId(reqHead.getCategoryId());
        //品类编码
        project.setExtCategoryCode(reqHead.getCategoryCode());
        //品类
        project.setExtCategoryName(reqHead.getCategoryName());
        //规模数量
        project.setExtScaleQuantity(reqHead.getRequireQuantity());
        //项目名称，取计划
        project.setSouName(reqHead.getProjectName());
        //合并申请单号
        project.setApplicantNo(ObjectUtils.defaultIfNull(reqHead.getRequirementHeadNoList(), "").replaceAll(",", ";"));
        //投标意向金
        project.setExtEarnestAmount(reqHead.getDepositAmount());
        List<ExtSouVendor> recommVendorList = new ArrayList<>();
        for (int i = 0; i < reqHeadList.size(); i++) {
            SouReqApply reqApply = reqHeadList.get(i);
            ExtSouVendor extSouVendor = new ExtSouVendor();
            extSouVendor.setVendorId(reqApply.getVendorId());
            extSouVendor.setVendorCode(reqApply.getVendorCode());
            extSouVendor.setVendorName(reqApply.getVendorName());
            extSouVendor.setLinkmanName(reqApply.getApplyContactName());
            extSouVendor.setPhone(reqApply.getApplyPhone());
            extSouVendor.setEmail(reqApply.getApplyEmail());
            extSouVendor.setSortIndex(i + 1);
            recommVendorList.add(extSouVendor);
        }
        //赋值
        param.setProject(project);
        param.setSouRecommendedVendor(recommendedVendor);
        param.setSouVendor(recommVendorList);
        Long projectId = souRecommVendorService.editRecommVendor(param, false, SouTypeEnum.recomm.name());
        param.getProject().setProjectId(projectId);
        //
        this.afterhandle(param.getProject(), reqHead);
        return param;
    }

    private void afterhandle(ExtSouProjectDto project, SouReqHead reqHead) {
        //回写寻源单
        qlService.updateByWrapper(QlWrappers.update(MqlType.SOU_REQ_HEAD_BUYER)
                .set(SouReqHead::getIsRecommend, Enable.Y.name())
                .eq(SouReqHead::getReqHeadId, reqHead.getReqHeadId()));
        //回写招标书
        qlOpenClient.update(ContextPath.SUP_CE, QlOpenWrappers.update(ExtPrSouRequirementHead.class)
                .set(ExtPrSouRequirementHead::getHasCreateVendorRecommend, Enable.Y)
                .set(ExtPrSouRequirementHead::getRecommendVendorBillId, project.getProjectId())
                .set(ExtPrSouRequirementHead::getRecommendVendorBillNo, project.getExtRecommendNo())
                .in(ExtPrSouRequirementHead::getRequirementHeadId, Arrays.asList(reqHead.getRequirementHeadIdList().split(","))));
    }

    @Override
    public QlResult getApplyInfo(QlQueryAction queryAction) {
        ApplyInfoVO applyInfoVO = new ApplyInfoVO();
        Record record = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords().get(0);
        //查询头表数据
        SouReqHead souReqHead = qlService.readByKey(MqlType.SOU_REQ_HEAD_BUYER, record.get(SouReqApply::getReqHeadId), SouReqHead.class);
        Assert.isTrue(ObjectUtil.isNotEmpty(souReqHead), "寻源单数据为空，请检查");
        applyInfoVO.setReqHeadId(souReqHead.getReqHeadId());
        //查询头表附件
        souReqHead.setFileUploads(baseClient.listSceneFileBatch(Collections.singletonList(souReqHead.getReqHeadId())));
        //赋值
        applyInfoVO.setSouReqHead(souReqHead);
        //查询报名数据
        List<SouReqApply> applyList = qlService.queryPageByWrapper(QlWrappers.query(MqlType.SOU_REQ_APPLY_BUYER)
                .eq(SouReqApply::getReqHeadId, record.get(SouReqApply::getReqHeadId))
                .eq(ObjectUtil.isNotEmpty(record.get(SouReqApply::getApplyId)), SouReqApply::getApplyId, record.get(SouReqApply::getApplyId))
                .eq(ObjectUtil.isEmpty(record.get(SouReqApply::getApplyId)), SouReqApply::getVendorId, AppUserUtil.getLoginAppUser().getCompanyId()), 1L, 1L, SouReqApply.class).getRecords();
        //如果未暂存过报名，则查询寻源单内容返回
        if (ObjectUtil.isNotEmpty(applyList)) {
            SouReqApply souReqApply = applyList.get(0);
            List<SceneFile> fileUploads = baseClient.listSceneFileBatch(Collections.singletonList(souReqApply.getApplyId()));
            souReqApply.setFileUploads(fileUploads);
            applyInfoVO.setSouReqApply(souReqApply);
        }
        return ResultUtil.build(queryAction, "reqHeadId", Collections.singletonList(applyInfoVO), false);
    }

    @Override
    public Map<String, Object> countRecomm(Map<String, Object> param) {
        param.put("userId", AppUserUtil.getLoginAppUser().getUserId());

        Integer recommPublic = souReqHeadMapper.countRecommPublic(param);

        Integer recommWithoutPublic = souReqHeadMapper.countRecommWithoutPublic(param);

        Map<String, Object> resultMap = new HashMap<>(50);
        resultMap.put("recommPublic", recommPublic);
        resultMap.put("recommWithoutPublic", recommWithoutPublic);
        return resultMap;
    }

    @Override
    public void dingTalkNotice(Record souReqApply) {
        //查询寻原需求单的其他供应商
        List<SouReqApply> souReqApplyList = qlService.queryByWrapper(QlWrappers.query(MqlType.SOU_REQ_APPLY)
                .eq(SouReqApply::getReqHeadId, souReqApply.get(SouReqApply::getReqHeadId))
                .notEq(SouReqApply::getVendorId, souReqApply.get(SouReqApply::getVendorId)), SouReqApply.class);

        if(CollectionUtils.isEmpty(souReqApplyList)) {
            log.info(MessageFormat.format("dingTalkNoticeSouReqVendorSignUp本次报名供应商{0}参与项目{1}无其他报名供应商", souReqApply.get(SouReqApply::getVendorId), souReqApply.get(SouReqApply::getReqHeadId)));
            return;
        }

        //查询关联供应商
        String vendorName = souReqApply.get(SouReqApply::getVendorName);
        List<String> otherVendorNameList = souReqApplyList.stream().filter(a -> !vendorName.equals(a.getVendorName())).map(a -> a.getVendorName()).distinct().collect(Collectors.toList());

        //查询关联供应商
        Boolean relationFlag = false;

        Set<String> relationVendorName = new HashSet<>(16);
        relationFlag = checkRelationSupplier(relationFlag, vendorName, otherVendorNameList, ExtSupAssociation::getVendorNameA, ExtSupAssociation::getVendorNameB, relationVendorName);

        relationFlag = checkRelationSupplier(relationFlag, vendorName, otherVendorNameList, ExtSupAssociation::getVendorNameB, ExtSupAssociation::getVendorNameA, relationVendorName);

        if(!relationFlag) {
            log.info(MessageFormat.format("dingTalkNoticeSouReqVendorSignUp本次报名供应商{0}参与项目{1}与其他报名供应商无关联关系", souReqApply.get(SouReqApply::getVendorId), souReqApply.get(SouReqApply::getReqHeadId)));
            return;
        }

        //存在关联关系，通知供应商负责人
        Record souReqHead = qlService.readByKey(MqlType.SOU_REQ_HEAD_BUYER, souReqApply.get(SouReqApply::getReqHeadId), Record.class);
        Long responsibilityUserId = souReqHead.get(SouReqHead::getResponsibilityUserId);
        //查询供应商负责人工号
        User user = rbacClient.getUserByIdAnon(responsibilityUserId);
        //发送钉钉 [${projectName}][${vendorName}]报名参与的项目与已有供应商[${existVendorName}]存在关联关系
        Map<String, String> var = new HashMap<>(15);
        var.put("${projectName}", souReqHead.get(SouReqHead::getProjectName));
        var.put("${vendorName}", souReqApply.get(SouReqApply::getVendorName));
        var.put("${existVendorName}", relationVendorName.stream().filter(v -> !souReqApply.get(SouReqApply::getVendorName).equals(v)).collect(Collectors.joining(SrmConstant.SIG_3)));

        DingTalkClient.newInstance(baseClient, pjProjectExtClient).sendDingTalk(Arrays.asList(user.getUsername()), DingTalkConstant.SOU_REQ_VENDOR_SIGNUP, var);

        log.info(MessageFormat.format("dingTalkNoticeSouReqVendorSignUp本次报名供应商{0}参与项目{1}与其他报名供应商({2})存在关联关系，已通过钉钉消息通知招标负责人{2}", souReqApply.get(SouReqApply::getVendorId), souReqApply.get(SouReqApply::getReqHeadId), user.getUsername()));
    }

    private <F> Boolean checkRelationSupplier(Boolean relationFlag, String vendorName, List<String> otherVendorNameList, SFunction<F, ?> eq, SFunction<F, ?> in, Set<String> relationVendorName) {

        List<RecordDTO> relationSuppliers = qlOpenClient.query(ContextPath.SUP, QlOpenWrappers.query(MqlType.SOU_RELATION_SUP_BUYER)
                .and(a -> a.eq(eq, vendorName).in(in, otherVendorNameList))
        );
        if(CollectionUtils.isNotEmpty(relationSuppliers)) {
            relationSuppliers.stream().forEach(s -> {
                relationVendorName.add(s.get(ExtSupAssociation::getVendorNameA));
                relationVendorName.add(s.get(ExtSupAssociation::getVendorNameB));
            });
            relationFlag = true;
        }
        return relationFlag;
    }
}
