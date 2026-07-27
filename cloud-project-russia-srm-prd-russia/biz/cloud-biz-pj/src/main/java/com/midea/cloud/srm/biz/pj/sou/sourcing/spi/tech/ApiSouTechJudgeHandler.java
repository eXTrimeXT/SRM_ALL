package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.tech;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouGroupDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProcessConfigDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouVendorDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.biz.pj.sou.sourcing.tech.dao.SouTechScoreHeadDAOImpl;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.score.enums.SouScoreDimensionCodeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouSignUpStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTechScoreStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 寻源openAPI - 技术标接口校验
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/05
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ApiSouTechJudgeHandler implements ISouSpiBean {

    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private SouProcessConfigDAOImpl souProcessConfigDao;
    @Autowired
    private SouGroupDAOImpl souGroupDao;
    @Autowired
    private SouVendorDAOImpl souVendorDao;
    @Autowired
    private SouOrderDAOImpl souOrderDao;
    @Autowired
    private SouTechScoreHeadDAOImpl souTechScoreHeadDao;

    @ApiOperation("当前是否可以查看技术评标进度")
    public SouProject judgeQueryTechProgressAuth(long projectId, String souType) {
        SouProject project = souProjectDao.getById(projectId);

        /* 是否有技术标节点 */
        SouProcessConfig processConfig = souProcessConfigDao.getById(project.getProcessConfigId());
        AssertUtils.isTrue(Enable.Y.equals(processConfig.getTechManagement()), "无技术标节点，禁止操作");

        /* 供应商投标未截止，不能访问(因为这时候连技术标附件都没有，何来技术标管理) */
        if (project.getCurrentRound() == 1) {
            switch (project.getProjectStatus()) {
                //拟定
                case DRAFT:
                    //接收报名中
                case ACCEPT_SIGN_UP:
                    //报名截止
                case SIGN_UP_END:
                    //报价未开始
                case ORDER_NOT_START:
                    //接收报价中
                case ACCEPT_ORDER:
                    throw new IllegalArgumentException("首轮报价截止后才可访问");
                default:
                    break;
            }
        }
        return project;
    }

    @ApiOperation("当前是否可以进行技术评分/代理评分")
    public long/* 评委ID */ judgeTechScoreAuth(long projectId, @Nullable Long groupId, long vendorId, @Nullable Long userId) {
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), projectId);
        SouProcessConfig processConfig = souProcessConfigDao.getById(project.getProcessConfigId());
        AssertUtils.isTrue(Enable.Y.equals(processConfig.getTechManagement()), "当前单据无技术标节点，禁止操作");
        AssertUtils.isTrue(Enable.Y.equals(project.getTechOpen()), "当前未技术开标，禁止评分");
        switch (project.getProjectStatus()) {
            //技术评分
            case TECH_EVAL:
                //商务评分
            case BUSINESS_EVAL:
                break;
            default:
                throw new IllegalArgumentException("当前单据状态禁止该操作");
        }
        SouGroup group; {
            if (groupId == null) {
                Optional<SouGroup> groupOptional = souGroupDao.lambdaQuery()
                        .eq(SouGroup::getProjectId, projectId)
                        .eq(SouGroup::getUserId, userId)
                        .list().stream().filter(g -> g.getScoreAuth().contains(SouScoreDimensionCodeEnum.SOU_TECH.name()))
                        .findFirst();
                AssertUtils.isTrue(groupOptional.isPresent(), "非工作小组成员，禁止操作");
                group = groupOptional.get();
                groupId = group.getGroupId();
            } else {
                group = souGroupDao.getById(groupId);
                AssertUtils.notNull(groupId, "缺少groupId参数");
            }
            AssertUtils.isTrue(group.getScoreAuth() != null && group.getScoreAuth().contains(SouScoreDimensionCodeEnum.SOU_TECH.name()),
                    "非技术评委，禁止操作");
        }
        SouVendor vendor = souVendorDao.lambdaQuery()
                .eq(SouVendor::getProjectId, projectId)
                .eq(SouVendor::getVendorId, vendorId)
                .one();
        AssertUtils.notNull(vendor, "不能对该供应商进行技术评分");
        if (Enable.Y.equals(processConfig.getSignUpManagement())) {
            /* 有报名节点 */
            AssertUtils.isTrue(SouSignUpStatusEnum.SIGN_UP_DONE.equals(vendor.getSignUpStatus()), "该供应商未报名通过，不能对该供应商进行技术评分");
        }

        SouOrder order = souOrderDao.lambdaQuery()
                .eq(SouOrder::getProjectId, projectId)
                .eq(SouOrder::getVendorId, vendorId)
                .eq(SouOrder::getRound, 1)
                .eq(SouOrder::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                .one();
        AssertUtils.notNull(order, "该供应商未提交报价，无需对其进行技术评分");

        SouTechScoreHead scoreHead = souTechScoreHeadDao.lambdaQuery()
                .eq(SouTechScoreHead::getProjectId, projectId)
                .eq(SouTechScoreHead::getGroupId, groupId)
                .eq(SouTechScoreHead::getVendorId, vendorId)
                .one();
        if (scoreHead != null) {
            AssertUtils.isFalse(SouTechScoreStatusEnum.FINISHED.equals(scoreHead.getScoreStatus()),
                    "评分已完成，请勿重复操作");
        }

        return groupId;
    }

    @ApiOperation("当前是否可以技术开标")
    public void judgeOpenTechAuth(long projectId, String souType) {
        SouProject project = this.judgeQueryTechProgressAuth(projectId, souType);
        AssertUtils.isTrue(Enable.N.equals(project.getTechOpen()), "已技术开标，勿重复操作");
        AssertUtils.isTrue(project.getCurrentRound() == 1, "非首轮，无需技术开标");
        switch (project.getProjectStatus()) {
            //报价截止
            case ORDER_END:
                //商务评标
            case BUSINESS_EVAL:
                break;
                //技术标
            case TECH_EVAL:
                throw new BaseException("已技术开标，无需重复操作");
            default:
                throw new BaseException("当前单据状态禁止操作");
        }
        long firstRoundSubmitOrderCount = souOrderDao.lambdaQuery()
                .eq(SouOrder::getProjectId, projectId)
                .eq(SouOrder::getRound, 1)
                .eq(SouOrder::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                .count();
        AssertUtils.isTrue(firstRoundSubmitOrderCount > 0, "当前无供应商提交报价，无法技术开标");
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
