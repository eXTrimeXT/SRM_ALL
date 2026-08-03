package com.midea.cloud.srm.biz.pj.sou.quotetemplate.judge;

import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.dao.SouQuoteTempAttrRepositoryImpl;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.dao.SouQuoteTempRepositoryImpl;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTemp;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempAttr;
import com.midea.cloud.srm.model.bid.quotetemplate.enums.SouQuoteTempAttrStatusEnum;
import com.midea.cloud.srm.model.bid.quotetemplate.enums.SouQuoteTempStatusEnum;
import com.midea.cloud.srm.model.common.enums.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 寻源 - 模型报价模板 - 接口校验服务
 *
 * @author zhangwk12@midea.com
 * @since 2022/08/02
 */
public abstract class AbstractSouQuoteTempJudge {

    @Autowired
    private SouQuoteTempRepositoryImpl souQuoteTempRepository;
    @Autowired
    private SouQuoteTempAttrRepositoryImpl souQuoteTempAttrRepository;

    /**
     * 当前登录人是否可以列表查询报价模板数据
     */
    public void judgeListTempsAuth() {
        boolean isBuyer = AppUserUtil.getLoginAppUser() == null || UserType.BUYER.name().equals(AppUserUtil.getLoginAppUser().getUserType());
        AssertUtils.isTrue(isBuyer, "非采购商角色，禁止操作");
    }

    /**
     * 当前操作人是否可以查看报价模板详情
     */
    public SouQuoteTemp judgeGetTempAuth(long tempId) {
        SouQuoteTemp temp = souQuoteTempRepository.getById(tempId);
        AssertUtils.notNull(temp, LocaleHandler.getLocaleMsg("报价模板[{0}]不存在"), tempId);
        return temp;
    }

    /**
     * 当前操作人是否可以校验添加的报价属性是否是完整的
     */
    public List<SouQuoteTempAttr> judgeCheckTempAttrsAuth(Set<Long> attrIds) {
        AssertUtils.notEmpty(attrIds, "请勾选报价属性");
        Map<Long/* attrId */, SouQuoteTempAttr> attrMap = souQuoteTempAttrRepository.listByIds(attrIds)
                .stream().collect(Collectors.toMap(SouQuoteTempAttr::getAttrId, Function.identity()));
        attrIds.forEach(attrId -> AssertUtils.isTrue(attrMap.containsKey(attrId), LocaleHandler.getLocaleMsg("报价属性[{0}]不存在"), attrId));
        attrMap.values().forEach(attr -> AssertUtils.isTrue(SouQuoteTempAttrStatusEnum.VALID.equals(attr.getAttrStatus()),
                "报价属性[{0}]不是生效状态，不能使用", attr.getAttrName()));
        return new ArrayList<>(attrMap.values());
    }

    /**
     * 当前操作人是否可以编辑报价模板信息
     */
    public void judgeEditTempAuth(@Nullable Long tempId) {
        boolean isBuyer = AppUserUtil.getLoginAppUser() == null || UserType.BUYER.name().equals(AppUserUtil.getLoginAppUser().getUserType());
        AssertUtils.isTrue(isBuyer, "非采购商角色，禁止操作");
        if (tempId != null) {
            SouQuoteTemp temp = souQuoteTempRepository.getById(tempId);
            AssertUtils.notNull(temp, LocaleHandler.getLocaleMsg("报价模板[{0}]不存在"), tempId);
            AssertUtils.isTrue(SouQuoteTempStatusEnum.DRAFT.equals(temp.getTempStatus()), "非拟定状态，禁止编辑");
        }
    }

    /**
     * 当前操作人是否可以删除报价模板
     */
    public void judgeRemoveTempAuth(long tempId) {
        boolean isBuyer = AppUserUtil.getLoginAppUser() == null || UserType.BUYER.name().equals(AppUserUtil.getLoginAppUser().getUserType());
        AssertUtils.isTrue(isBuyer, "非采购商角色，禁止操作");

        SouQuoteTemp temp = souQuoteTempRepository.getById(tempId);
        AssertUtils.notNull(temp, LocaleHandler.getLocaleMsg("报价模板[{0}]不存在"), tempId);
        AssertUtils.isTrue(SouQuoteTempStatusEnum.DRAFT.equals(temp.getTempStatus()), "非拟定状态，禁止删除");
    }

    /**
     * 当前操作人是否可以生效报价模板
     */
    public SouQuoteTemp judgeValidTempAuth(long tempId) {
        boolean isBuyer = AppUserUtil.getLoginAppUser() == null || UserType.BUYER.name().equals(AppUserUtil.getLoginAppUser().getUserType());
        AssertUtils.isTrue(isBuyer, "非采购商角色，禁止操作");

        SouQuoteTemp temp = souQuoteTempRepository.getById(tempId);
        AssertUtils.notNull(temp, LocaleHandler.getLocaleMsg("报价模板[{0}]不存在"), tempId);
        return temp;
    }

    /**
     * 当前操作人是否可以失效报价模板
     */
    public void judgeInvalidTempAuth(long tempId) {
        boolean isBuyer = AppUserUtil.getLoginAppUser() == null || UserType.BUYER.name().equals(AppUserUtil.getLoginAppUser().getUserType());
        AssertUtils.isTrue(isBuyer, "非采购商角色，禁止操作");

        SouQuoteTemp temp = souQuoteTempRepository.getById(tempId);
        AssertUtils.notNull(temp, LocaleHandler.getLocaleMsg("报价模板[{0}]不存在"), tempId);
        AssertUtils.isTrue(SouQuoteTempStatusEnum.VALID.equals(temp.getTempStatus()), "非生效状态，不能失效");
    }

    /**
     * 当前操作人是否可以复制报价模板
     */
    public void judgeCopyTempAuth(long tempId) {
        boolean isBuyer = AppUserUtil.getLoginAppUser() == null || UserType.BUYER.name().equals(AppUserUtil.getLoginAppUser().getUserType());
        AssertUtils.isTrue(isBuyer, "非采购商角色，禁止操作");

        SouQuoteTemp temp = souQuoteTempRepository.getById(tempId);
        AssertUtils.notNull(temp, LocaleHandler.getLocaleMsg("报价模板[{0}]不存在"), tempId);
    }

}
