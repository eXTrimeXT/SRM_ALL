package com.midea.cloud.srm.biz.pj.sou.quotetemplate.validator.api;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.dao.SouQuoteTempApiRepositoryImpl;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempApi;
import com.midea.cloud.srm.model.bid.quotetemplate.enums.SouQuoteTempApiStatusEnum;
import com.midea.cloud.srm.model.bid.quotetemplate.enums.SouQuoteTempApiTypeEnum;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.common.utils.RegexUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 寻源-报价模板-api引用验证
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/08/17
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class SouQuoteTempApiValidator {

    @Autowired
    private SouQuoteTempApiRepositoryImpl souQuoteTempApiRepository;

    public SouQuoteTempApi formatValidateAndConvert(SouQuoteTempApi param, boolean isTempSave) {
        /* 1: 入参格式化及校验 */
        this.formatAndValidate(param, isTempSave);
        /* 2: 数据转换 */
        return this.doConvert(param, isTempSave);
    }

    private void formatAndValidate(SouQuoteTempApi param, boolean isTempSave) {
        /* 1:ID(略) */
        /* 2: api名称 */
        param.setApiName(StringUtils.trimToNull(param.getApiName())); {
            AssertUtils.notNull(param.getApiName(), "请输入api名称");
            AssertUtils.isTrue(param.getApiName().length() <= 100, "api名称长度不能超过100");
            /* 确保名称唯一 */
            long existCount = souQuoteTempApiRepository.lambdaQuery()
                    .ne(param.getApiId() != null, SouQuoteTempApi::getApiId, param.getApiId())
                    .eq(SouQuoteTempApi::getApiName, param.getApiName())
                    .count();
            AssertUtils.isTrue(existCount <= 0, "api名称不能重复");
            AssertUtils.isTrue(RegexUtil.REGEX_NORMAL_NAME.matcher(param.getApiName()).matches(), "名称只能包含中英文数字下划线(中英文开头)");
        }
        /* 3: api类型 */
        AssertUtils.notNull(param.getApiType(), "请选择api类型");
        /* 4: api状态(置空 - 后端处理) */
        param.setApiStatus(null);
        /* 5: url */
        if (SouQuoteTempApiTypeEnum.URL.equals(param.getApiType())) {
            param.setApiUrl(StringUtils.trimToNull(param.getApiUrl()));
            AssertUtils.notNull(param.getApiUrl(), "请输入url全路径");
        } else {
            param.setApiUrl(null);
        }
        /* 6: 分流服务类 */
        boolean needModule = SouQuoteTempApiTypeEnum.SERVICE.equals(param.getApiType());
        if (needModule) {
            param.setApiClient(StringUtils.trimToNull(param.getApiClient()));
            AssertUtils.notNull(param.getApiClient(), "请输入分流服务类");
            AssertUtils.isTrue(param.getApiClient().length() <= 100, "分流服务类的输入长度不能超过100");
        } else {
            param.setApiClient(null);
        }
        /* 7: 业务服务类 */
        if (needModule) {
            param.setApiService(StringUtils.trimToNull(param.getApiService()));
            AssertUtils.notNull(param.getApiService(), "请输入业务服务类");
            AssertUtils.isTrue(param.getApiService().length() <= 100, "业务服务类的输入长度不能超过100");
        } else {
            param.setApiService(null);
        }
        /* 8: api参数说明 */
        if (param.getApiDetails() != null) {
            Set<String> argDescSet = new HashSet<>(param.getApiDetails().size());
            param.getApiDetails().forEach(detail -> {
                detail.setArgName(StringUtils.trimToNull(detail.getArgName()));
                AssertUtils.notNull(detail.getArgName(), "缺少argName");
                AssertUtils.isTrue(argDescSet.add(detail.getArgName()), LocaleHandler.getLocaleMsg("参数不能重复：{0}"), detail.getArgName());
                detail.setArgDesc(StringUtils.trimToNull(detail.getArgDesc()));
                AssertUtils.notNull(detail.getArgDesc(), LocaleHandler.getLocaleMsg("参数[{0}]缺少中文说明"), detail.getArgName());
                AssertUtils.isTrue(argDescSet.add(detail.getArgDesc()), LocaleHandler.getLocaleMsg("参数[{0}]的中文说明不能重复"), detail.getArgName());
                AssertUtils.notNull(detail.getArgType(), LocaleHandler.getLocaleMsg("参数[{0}]缺少类型说明"), detail.getArgName());
                if (detail.getRequired() == null) {
                    detail.setRequired(Enable.N);
                }
            });
        } else {
            param.setApiDetails(Collections.emptyList());
        }
    }

    private SouQuoteTempApi doConvert(SouQuoteTempApi param, boolean isTempSave) {
        SouQuoteTempApi entity;
        if (param.getApiId() == null) {
            entity = new SouQuoteTempApi();
            entity.setApiId(IdGenrator.generate());
        } else {
            entity = souQuoteTempApiRepository.getById(param.getApiId());
        }
        BaseEntity.copyFieldValuesOnlySpecified(entity, param,
//                api名称
                SouQuoteTempApi::getApiName,
//                api类型
                SouQuoteTempApi::getApiType,
//                url
                SouQuoteTempApi::getApiUrl,
//                分流服务类
                SouQuoteTempApi::getApiClient,
//                业务服务类
                SouQuoteTempApi::getApiService,
//                api参数说明
                SouQuoteTempApi::getApiDetails);
        entity.setApiStatus(isTempSave ? SouQuoteTempApiStatusEnum.DRAFT : SouQuoteTempApiStatusEnum.VALID);

        return entity;
    }

}
