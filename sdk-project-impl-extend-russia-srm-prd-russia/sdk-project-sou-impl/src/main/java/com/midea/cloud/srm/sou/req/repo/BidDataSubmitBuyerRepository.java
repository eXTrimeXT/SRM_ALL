package com.midea.cloud.srm.sou.req.repo;

import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.util.ObjectUtil;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.meiql.api.action.DefaultAction;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.ProxyQlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.enums.SouBidProccessEnum;
import com.midea.cloud.srm.model.sou.req.BidDataSubmit;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.req.enums.BidDataSubmitFileTypeEnum;
import com.midea.cloud.srm.model.sou.req.enums.BidDataSubmitStatusEnum;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenUpdateWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.*;

/**
 * <pre>
 *  招标资料递交
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/10/8 16:18
 *  修改内容:
 * </pre>
 */
@Slf4j
@Component
public class BidDataSubmitBuyerRepository extends CrudRepository {
    @Autowired
    protected QlService qlService;
    @Autowired
    private QlOpenClient qlOpenClient;
    @Autowired
    private BaseClient baseClient;

    public BidDataSubmitBuyerRepository() {
        //注册action
        this.register("submit", this::submit, true, "提交");
        this.register("saveOrUpdate", this::saveOrUpdate, true, "暂存");
    }

    private QlResult saveOrUpdate(QlQueryAction queryAction) {
        List<Record> records = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        //查询申请单头表数据
        List<Record> purchaseRequirementHeads = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query(MqlType.PURCHASE_REQUIREMENT_HEAD)
                .eq("requirementHeadNum", records.get(0).get(BidDataSubmit::getRequirementHeadNum)), Record.class);
        Assert.isTrue(ObjectUtil.isNotEmpty(purchaseRequirementHeads), "申请单数据为空");
        //查询扩展表数据
        Record extPrSouRequirementHead = qlOpenClient.read(ContextPath.SUP_CE, MqlType.EXT_PR_SOU_REQUIREMENT_HEAD, purchaseRequirementHeads.get(0).getLong("requirementHeadId"));
        String hasSendSouProfile = "hasSendSouProfile";
        if (ObjectUtil.isEmpty(records.get(0).get(BidDataSubmit::getDataSubmitId)) && extPrSouRequirementHead.getString(hasSendSouProfile).equals(Enable.Y.name())) {
            throw new RuntimeException("申请单已被占用");
        }
        QlResult qlResult = super.save(queryAction);
        //回写申请单
        QlOpenUpdateWrapper up = QlOpenWrappers.update(MqlType.EXT_PR_SOU_REQUIREMENT_HEAD)
                .set("hasSendSouProfile", Enable.Y.name())
                .set("sendSouProfileStatus", BidDataSubmitStatusEnum.DRAFT)
                .eq("requirementHeadId", purchaseRequirementHeads.get(0).getLong("requirementHeadId"));
        qlOpenClient.update(ContextPath.SUP_CE, up);
        return qlResult;
    }


    private QlResult submit(QlQueryAction queryAction) {
        List<Record> records = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        //提交前校验
        Record purchaseRequirementHead = beforeSubmitHandle(records.get(0));
        //状态赋值
        if(Objects.isNull(records.get(0).get(BidDataSubmit::getStatus))) {
            records.get(0).set(BidDataSubmit::getStatus, BidDataSubmitStatusEnum.DRAFT.name());
        }

        //执行提交
        QlResult qlResult = super.save(ProxyQlQueryAction.proxy(queryAction, DefaultAction.SAVE.value(), records));
        return qlResult;
    }

    private Record beforeSubmitHandle(Record record) {
        //查询申请单头表数据
        List<Record> purchaseRequirementHeads = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query(MqlType.PURCHASE_REQUIREMENT_HEAD)
                .eq("requirementHeadNum", record.get(BidDataSubmit::getRequirementHeadNum)), Record.class);
        Assert.isTrue(ObjectUtil.isNotEmpty(purchaseRequirementHeads), "申请单数据为空");
        //查询扩展表数据
        Record extPrSouRequirementHead = qlOpenClient.read(ContextPath.SUP_CE, MqlType.EXT_PR_SOU_REQUIREMENT_HEAD, purchaseRequirementHeads.get(0).getLong("requirementHeadId"));
        String hasSendSouProfile = "hasSendSouProfile";
        if (ObjectUtil.isEmpty(record.get(BidDataSubmit::getDataSubmitId)) && extPrSouRequirementHead.getString(hasSendSouProfile).equals(Enable.Y.name())) {
            throw new RuntimeException("申请单已被占用");
        }
        List<Record> sceneFileList = record.getSubRecords(LambdaUtil.getFieldName(BidDataSubmit::getFileUploads));
        String jingjia = "JINGJIA";
        if (jingjia.equals(record.get(BidDataSubmit::getBidFlow))) {
            boolean hasTechnologyTask = sceneFileList.stream().anyMatch(r -> BidDataSubmitFileTypeEnum.OTHER.name().equals(r.get(SceneFile::getAttachmentType)));
            Assert.isTrue(hasTechnologyTask, "提交失败：需要上传”其它“类型的附件");
        } else {
            /*专家抽取标准：
        ①预算（万元）<500万，评标组人员数量3个及以上（含评标组长），高级专家>=1;
        ②500万<=预算（万元）<1000万，评标组人员数量3个及以上（含评标组长），高级专家>=2;
        ③预算（万元）>1000万，评标组人员数量5个及以上（含评标组长），高级>=3*/
            //预算金额
            BigDecimal totalBudget = record.get(BidDataSubmit::getTotalBudget);
            if (ObjectUtil.isNotEmpty(record.get(BidDataSubmit::getBidFlow)) && !record.get(BidDataSubmit::getBidFlow).equals(SouBidProccessEnum.INQUIRY.getCode())) {
                Assert.isTrue(ObjectUtil.isNotEmpty(record.get(BidDataSubmit::getBidEvaluatorNum)) && ObjectUtil.isNotEmpty(record.get(BidDataSubmit::getAskSeniorExpertNum)), "评审总人数和要求高级专家人数不能为空");
                Integer num1 = 500;
                Integer num2 = 1000;
                if (totalBudget.compareTo(BigDecimal.valueOf(num1)) < 0) {
                    Assert.isTrue(record.get(BidDataSubmit::getBidEvaluatorNum) >= 3 && record.get(BidDataSubmit::getAskSeniorExpertNum) >= 1, "提交失败：需要评标组人员数量3个及以上（含评标组长），高级专家大于等于1");
                }
                if (totalBudget.compareTo(BigDecimal.valueOf(num1)) >= 0 && totalBudget.compareTo(BigDecimal.valueOf(num2)) < 0) {
                    Assert.isTrue(record.get(BidDataSubmit::getBidEvaluatorNum) >= 3 && record.get(BidDataSubmit::getAskSeniorExpertNum) >= 2, "提交失败：需要评标组人员数量3个及以上（含评标组长），高级专家大于等于2");
                }
                if (totalBudget.compareTo(BigDecimal.valueOf(num2)) > 0) {
                    Assert.isTrue(record.get(BidDataSubmit::getBidEvaluatorNum) >= 5 && record.get(BidDataSubmit::getAskSeniorExpertNum) >= 3, "提交失败：需要评标组人员数量5个及以上（含评标组长），高级专家大于等于3");
                }
            }
            //附件合规校验
        /*1、属于招标范围且金额小于10万
        2、不属于招标范围
        3、特殊招标
        以上条件只要有一项满足，就需要上传”技术任务书及附件“附件类型，其他不必填。
        属于招标范围且金额大于10万，所有附件类型均必填。
        招*/
            PurchaseCategory category = new PurchaseCategory();
            try {
                category.setCategoryId(record.get(BidDataSubmit::getCategoryId));
                category = baseClient.getPurchaseCategoryByParm(category);
            } catch (BaseException e) {
                throw new RuntimeException("请求中台数据异常");
            }
//招标范围
            boolean ifBid = ObjectUtil.isNotEmpty(category) &&
                    ObjectUtil.isNotEmpty(category.getExtensions().get("ifBid")) &&
                    YesOrNo.YES.getValue().equals(category.getExtensions().get("ifBid").toString());
            Integer num3 = 10;
            boolean pass = (ifBid && record.get(BidDataSubmit::getTotalBudget).compareTo(BigDecimal.valueOf(num3)) < 0) ||
                    !ifBid || "SPECIAL_SOU".equals(record.get(BidDataSubmit::getSourceFrom));
            if (pass) {
                boolean hasTechnologyTask = sceneFileList.stream().anyMatch(r -> BidDataSubmitFileTypeEnum.TECHNOLOGY_TASK.name().equals(r.get(SceneFile::getAttachmentType)));
                Assert.isTrue(hasTechnologyTask, "提交失败：需要上传”技术任务书及附件“类型的附件");
            } else {
                Set<String> allTypes = new HashSet<>(Arrays.asList(
                        BidDataSubmitFileTypeEnum.TECHNOLOGY_TASK.name(),
                        BidDataSubmitFileTypeEnum.CONTRACT_MODEL.name(),
                        BidDataSubmitFileTypeEnum.PROJECT_APPLICATION.name(),
                        BidDataSubmitFileTypeEnum.QUOTATION_TEMPLATE.name()
                ));
                //取差集，如果没有，则表示全部都有
                allTypes.removeIf(type -> sceneFileList.stream().anyMatch(r -> r.get(SceneFile::getAttachmentType).equals(type)));
                Assert.isTrue(allTypes.isEmpty(), "所有附件类型均需要必填");
            }
        }
        return purchaseRequirementHeads.get(0);
    }

    @Override
    public QlResult delete(QlQueryAction queryAction) {
        List<Record> keys = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        for (Record key : keys) {
            Record record = qlService.readByKey(MqlType.SUBMIT_BUYER, key.get(BidDataSubmit::getDataSubmitId), Record.class);
            if (record != null && ObjectUtil.isNotEmpty(record.get(BidDataSubmit::getRequirementHeadNum))) {
                //查询申请单头表数据
                List<Record> purchaseRequirementHeads = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query(MqlType.PURCHASE_REQUIREMENT_HEAD)
                        .eq("requirementHeadNum", record.get(BidDataSubmit::getRequirementHeadNum)), Record.class);
                if (ObjectUtil.isNotEmpty(purchaseRequirementHeads)) {
                    Record purchaseRequirementHead = purchaseRequirementHeads.get(0);
                    //回写申请单状态
                    QlOpenUpdateWrapper up = QlOpenWrappers.update(MqlType.EXT_PR_SOU_REQUIREMENT_HEAD)
                            .set("hasSendSouProfile", Enable.N.name())
                            .set("sendSouProfileStatus", null)
                            .eq("requirementHeadId", purchaseRequirementHead.getLong("requirementHeadId"));
                    qlOpenClient.update(ContextPath.SUP_CE, up);
                }
            }
        }
        return super.delete(queryAction);
    }
}
