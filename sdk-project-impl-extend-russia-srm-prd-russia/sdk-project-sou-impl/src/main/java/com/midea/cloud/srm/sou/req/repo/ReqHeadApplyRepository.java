package com.midea.cloud.srm.sou.req.repo;

import cn.hutool.core.util.ObjectUtil;
import com.midea.cloud.meiql.api.action.DefaultAction;
import com.midea.cloud.meiql.api.service.QlCondition;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.ProxyQlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.meiql.core.repository.jooq.support.QueryParam;
import com.midea.cloud.srm.model.common.enums.CompanyStatusEnum;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.sou.req.SouReqApply;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.req.enums.SouReqApplyStatusEnum;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.model.supplier.info.entity.OrgCategory;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.constants.SouConstant;
import com.midea.cloud.srm.sou.deposit.service.FinanceService;
import com.midea.cloud.srm.sou.req.service.SouReqApplyService;
import com.midea.cloud.srm.sou.req.service.SouReqHeadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Date;
import javax.annotation.Resource;
import java.util.List;

/**
 * <pre>
 *  功能名称
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
public class ReqHeadApplyRepository extends CrudRepository {
    @Autowired
    protected QlService qlService;
    @Autowired
    private QlOpenClient qlOpenClient;
    @Autowired
    private SouReqApplyService souReqApplyService;
    @Autowired
    private SouReqHeadService souReqHeadService;
    @Resource
    private FinanceService financeService;

    private static final String Z003 = "Z003";

    public ReqHeadApplyRepository() {
        //注册action
        this.register("apply", this::apply, true, "报名");
        this.register("getApplyInfo", this::getApplyInfo, false, "获取报名详情信息");
        this.register("withdraw", this::withdraw, true, "撤回报名");
    }

    private QlResult withdraw(QlQueryAction queryAction) {
        Record records = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords().get(0);
        qlService.updateByWrapper(QlWrappers.update(MqlType.SOU_REQ_APPLY)
                .set(SouReqApply::getApplyStatus, SouReqApplyStatusEnum.WITHDRAW.getCode())
                .set(SouReqApply::getWithdrawReason, records.get(SouReqApply::getWithdrawReason))
                .eq(SouReqApply::getApplyId, records.get(SouReqApply::getApplyId)));
        return new QlResult();
    }

    private QlResult getApplyInfo(QlQueryAction queryAction) {
        return souReqApplyService.getApplyInfo(queryAction);
    }

    /**
     * 2.在报名时会做组织品类退出限制，如果该供应商的组织品类与寻源公示的组织品类相同，则不允许报名。
     * 3.无MDM正式供应商编码时，不可报名。
     *
     * @param queryAction
     * @return
     */
    private QlResult apply(QlQueryAction queryAction) {
        souReqHeadService.handleSignupDone();
        List<Record> records = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        Assert.isTrue(ObjectUtil.isNotEmpty(records), "报名数据不能为空");
        this.checkParams(records.get(0));
        //是否内部供应商: 供应商库的账户组如果是Z003，则显示是，否则显示否
        RecordDTO companyInfo = qlOpenClient.read(ContextPath.SUP, "CompanyInfo", records.get(0).get(SouReqApply::getVendorId));
        String accountGroup = companyInfo.getString("accountGroup");
        if(Z003.equals(accountGroup)) {
            records.get(0).set(SouReqApply::getIsInternalVendor, "Y");
        } else {
            records.get(0).set(SouReqApply::getIsInternalVendor, "N");
        }
        //初始化参数
        records.get(0).set(SouReqApply::getApplyStatus, SouReqApplyStatusEnum.CONFIRMING_SIGNUP);
        records.get(0).set(SouReqApply::getSignupTime, new Date());
        QlResult qlResult = super.save(ProxyQlQueryAction.proxy(queryAction, DefaultAction.SAVE.value(), records));
        //意向金缴款
        Record record = records.get(0);
        SouReqHead souReqHead = qlService.readByKey(MqlType.SOU_REQ_HEAD_BUYER, record.get(SouReqApply::getReqHeadId), SouReqHead.class);
        boolean flag = financeService.existsFinanceUseRecord(souReqHead.getReqHeadNo(),record.get(SouReqApply::getVendorCode));
        if(!flag) {
            financeService.dealIntentionalDepositPayment(record.getLong("reqHeadId"), record.getString("vendorBankAccount"), record.getLong("vendorId"));
        }
        //存在供应商关联关系时，钉钉消息通知供应商负责人
        souReqApplyService.dingTalkNotice(records.get(0));
        return qlResult;
    }

    private void checkParams(Record record) {
        //查询主单数据
        Record souReqHead = qlService.readByKey(MqlType.SOU_REQ_HEAD_BUYER, record.get(SouReqApply::getReqHeadId), Record.class);
        //检查是否超过截止时间
        Assert.isTrue(souReqHead.get(SouReqHead::getPublicEndTime).getTime() >= System.currentTimeMillis(), "报名异常：超过报名截止时间");
        RecordDTO companyInfo = qlOpenClient.read(ContextPath.SUP, "CompanyInfo", record.get(SouReqApply::getVendorId));
        List<RecordDTO> categoryList = qlOpenClient.query(ContextPath.SUP, QlOpenWrappers.query(OrgCategory.class)
                //供应商的组织品类与寻源公示的组织品类需要一样
                .eq(OrgCategory::getCategoryId, souReqHead.get(SouReqHead::getCategoryId))
                .eq(OrgCategory::getCompanyId, record.get(SouReqApply::getVendorId))
                .orderByDesc(OrgCategory::getCreationDate));
        Assert.isTrue(ObjectUtil.isNotEmpty(categoryList), "您还未添加此服务范围，请在您公司基本信息中添加。");
        RecordDTO category = categoryList.get(0);
        //黑名单
        if (companyInfo.get(CompanyInfo::getIsBacklist).equals(Enable.Y.name())
                //限制时间
                || companyInfo.get("timeLimitFlag").equals(Enable.Y.name())
                //组织状态
                || category.get("pjOrgStatus").equals(Enable.N.name())
                //品类状态
                || category.get("pjCategoryStatus").equals(Enable.N.name())
                //供应商品类状态
                || category.get(OrgCategory::getCompanyStatus).equals(CompanyStatusEnum.TERMINATION))
        {
            throw new RuntimeException("您公司已被列入我司异常名录供应商，请联系报名联系人。");
        }
        String pjCompanyStatus = "pjCompanyStatus";
        String potentialSupplier = "POTENTIAL_SUPPLIER";
        if (companyInfo.get(pjCompanyStatus).equals(potentialSupplier)) {
            throw new RuntimeException("您的资审信息还未被审核，请联系报名联系人。");
        }
    }

    @Override
    public QlCondition beforeQuery(QlQueryAction queryAction, QueryParam payload) {
        souReqHeadService.handleSignupDone();
        return MeiQl.newCondition();
    }
}
