package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.signup;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProcessConfigDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouVendorDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouPublishScopeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 寻源openAPI - 报名接口校验
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/02
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ApiSouSignUpJudgeHandler implements ISouSpiBean {

    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private SouProcessConfigDAOImpl souProcessConfigDao;
    @Autowired
    private SouVendorDAOImpl souVendorDao;

    @ApiOperation("当前是否可以查看供应商报名信息")
    public SouProject judgeGetVendorSignUpInfoAuth(long projectId, String souType) {
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), projectId);
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型")+"[{0}]"+LocaleHandler.getLocaleMsg("不匹配"), souType);
        SouProcessConfig processConfig = souProcessConfigDao.getById(project.getProcessConfigId());
        return project;
    }

    @Nullable
    @ApiOperation("当前是否可以查看供应商报名详情")
    public SouVendor judgeGetVendorSignUpDetailAuth(long projectId, long vendorId, String souType) {
        SouProject project = this.judgeGetVendorSignUpInfoAuth(projectId, souType);
        if (SouPublishScopeEnum.INVITE_TENDER.equals(project.getPublishScope())) {
            /* 邀请 */
            SouVendor vendor = souVendorDao.lambdaQuery()
                    .eq(SouVendor::getProjectId, projectId)
                    .eq(SouVendor::getVendorId, vendorId)
                    .one();
            AssertUtils.notNull(vendor, "供应商不在受邀范围内");
            return vendor;
        }
        return null;
    }

    @ApiOperation("当前是否可以进行供应商报名")
    public SouProject judgeVendorSignUpAuth(long projectId, long vendorId, String souType) {
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), projectId);
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型")+"[{0}]"+LocaleHandler.getLocaleMsg("不匹配"), souType);
        AssertUtils.notNull(project.getSignUpEndTime(), "报名截止时间为空，禁止操作");
        AssertUtils.isTrue(new Date().before(project.getSignUpEndTime()), "报名时间已截止，禁止操作");
        SouVendor vendor = souVendorDao.lambdaQuery()
                .eq(SouVendor::getProjectId, projectId)
                .eq(SouVendor::getVendorId, vendorId)
                .one();
        if (SouPublishScopeEnum.INVITE_TENDER.equals(project.getPublishScope())) {
            /* 邀请 */
            AssertUtils.notNull(vendor, "非受邀供应商，禁止操作");
        }
        if (vendor != null) {
            switch (vendor.getSignUpStatus()) {
                //未报名
                case NO_SIGN_UP:
                    //已驳回
                case REJECTED:
                    break;
                    //确认中
                case CONFIRM_ING:
                    throw new IllegalArgumentException("请勿重复提交报名信息");
                case SIGN_UP_DONE:
                    throw new IllegalArgumentException("已报名，请勿重复操作");
                default:
                    throw new IllegalArgumentException("不支持的报名状态:" + vendor.getSignUpStatus());
            }
        }
        return project;
    }

    @ApiOperation("当前是否可以确认/驳回供应商报名")
    public void judgeConformVendorSignUpAuth(long projectId, long vendorId, String souType) {
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), projectId);
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型")+"[{0}]"+LocaleHandler.getLocaleMsg("不匹配"), souType);
        SouVendor vendor = souVendorDao.lambdaQuery()
                .eq(SouVendor::getProjectId, projectId)
                .eq(SouVendor::getVendorId, vendorId)
                .one();
        AssertUtils.notNull(vendor, LocaleHandler.getLocaleMsg("供应商信息")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), vendorId);
        switch (vendor.getSignUpStatus()) {
            //未报名
            case NO_SIGN_UP:
                throw new IllegalArgumentException("供应商未报名");
                //已驳回
            case REJECTED:
                throw new IllegalArgumentException("供应商报名已驳回");
                //已报名
            case SIGN_UP_DONE:
                throw new IllegalArgumentException("供应商已报名通过");
                //确认中
            case CONFIRM_ING:
                break;
            default:
                throw new IllegalArgumentException("不支持的供应商报名状态" + vendor.getSignUpStatus());
        }
    }

    @ApiOperation("当前是否可以立即截止/延长报名")
    public SouProject judgeChangeSignUpEndTimeAuth(long projectId, String souType) {
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), projectId);
        AssertUtils.isTrue(project.getSouType().equals(souType), "寻源类型不匹配");
        switch (project.getProjectStatus()) {
            //接收报名中
            case ACCEPT_SIGN_UP:
                //报名已截止
            case SIGN_UP_END:
                break;
            default:
                throw new IllegalArgumentException("当前单据状态禁止该操作");
        }
        return project;
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
