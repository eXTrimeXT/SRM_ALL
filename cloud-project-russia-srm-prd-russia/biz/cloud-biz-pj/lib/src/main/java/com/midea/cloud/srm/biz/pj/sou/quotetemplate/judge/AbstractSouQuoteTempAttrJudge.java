package com.midea.cloud.srm.biz.pj.sou.quotetemplate.judge;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.dao.SouQuoteTempAttrRepositoryImpl;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempAttr;
import com.midea.cloud.srm.model.bid.quotetemplate.enums.SouQuoteTempAttrStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;

/**
 * 寻源 - 模型报价模板 - 报价属性 - 接口校验服务
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/27
 */
public abstract class AbstractSouQuoteTempAttrJudge {

    @Autowired
    private SouQuoteTempAttrRepositoryImpl souQuoteTempAttrRepository;

    /**
     * 当前人是否可以查看报价属性详情信息
     */
    public SouQuoteTempAttr judgeGetAttrAuth(long attrId) {
        SouQuoteTempAttr attr = souQuoteTempAttrRepository.getById(attrId);
        AssertUtils.notNull(attr, LocaleHandler.getLocaleMsg("报价属性[{0}]不存在"), attrId);
        return attr;
    }

    /**
     * 当前登录人是否可以编辑报价属性信息
     */
    public void judgeEditAttrAuth(@Nullable Long attrId) {
        if (attrId != null) {
            SouQuoteTempAttr attr = souQuoteTempAttrRepository.getById(attrId);
            AssertUtils.notNull(attr, LocaleHandler.getLocaleMsg("报价属性[{0}]不存在"), attrId);
            AssertUtils.isTrue(SouQuoteTempAttrStatusEnum.DRAFT.equals(attr.getAttrStatus()), "非拟定状态禁止编辑");
        }
    }

    /**
     * 当前登录人是否可以删除报价属性
     */
    public void judgeRemoveAttrAuth(long attrId) {
        SouQuoteTempAttr attr = souQuoteTempAttrRepository.getById(attrId);
        AssertUtils.notNull(attr, LocaleHandler.getLocaleMsg("报价属性[{0}]不存在"), attrId);
        AssertUtils.isTrue(SouQuoteTempAttrStatusEnum.DRAFT.equals(attr.getAttrStatus()), "非拟定状态，不能删除");
    }

    /**
     * 当前登录人是否可以生效报价属性
     */
    public SouQuoteTempAttr judgeValidAttrAuth(long attrId) {
        SouQuoteTempAttr attr = souQuoteTempAttrRepository.getById(attrId);
        AssertUtils.notNull(attr, LocaleHandler.getLocaleMsg("报价属性[{0}]不存在"), attrId);
        return attr;
    }

    /**
     * 当前登录人是否可以生效报价属性
     */
    public void judgeInvalidAttrAuth(long attrId) {
        SouQuoteTempAttr attr = souQuoteTempAttrRepository.getById(attrId);
        AssertUtils.notNull(attr, LocaleHandler.getLocaleMsg("报价属性[{0}]不存在"), attrId);
        AssertUtils.isTrue(SouQuoteTempAttrStatusEnum.VALID.equals(attr.getAttrStatus()), "非生效状态，不能失效");
    }

    /**
     * 当前登录人是否可以复制报价属性
     */
    public void judgeCopyAttrAuth(long attrId) {
        SouQuoteTempAttr attr = souQuoteTempAttrRepository.getById(attrId);
        AssertUtils.notNull(attr, LocaleHandler.getLocaleMsg("报价属性[{0}]不存在"), attrId);
    }

}
