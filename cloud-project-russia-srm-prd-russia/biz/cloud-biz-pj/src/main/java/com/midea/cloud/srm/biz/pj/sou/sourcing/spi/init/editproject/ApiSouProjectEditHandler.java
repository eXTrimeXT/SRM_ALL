package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.init.editproject;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.comp.init.dao.CompSouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProcessConfigDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouScoreRuleDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.biz.pj.sou.sourcing.util.SouUtils;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCurrency;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.common.enums.UserType;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouProject;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.*;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.score.enums.SouScoreDimensionCodeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.*;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 寻源openAPI - 寻源基本信息保存校验转换处理
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/29
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ApiSouProjectEditHandler implements ISouSpiBean {

    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private SouProcessConfigDAOImpl souProcessConfigDao;
    @Autowired
    private SouScoreRuleDAOImpl souScoreRuleConfigDAO;
    @Autowired
    private BaseClient baseClient;
    @Autowired
    private RbacClient rbacClient;
    @Autowired
    private CompSouProjectDAOImpl compSouProjectDao;

    public SouProjectEditPO formatValidateAndConvert(ApiSouProjectInfoDTO param, boolean isCopy, String souType) {
        if (isCopy) {
            param.setTempSave(true);
        }

        /* 1: 数据格式化及校验 */
        this.formatAndValidate(param, param.isTempSave(), isCopy, souType);
        /* 2: 数据转换 */
        return this.convert(param, param.isTempSave(), souType);
    }

    /**
     * 入参格式化及校验
     * @param param
     * @param isTempSave
     * @param isCopy
     * @param souType
     */
    protected void formatAndValidate(ApiSouProjectInfoDTO param, boolean isTempSave, boolean isCopy, String souType) {

        /* 格式化及校验基本信息 */
        this.formatAndValidateProject(param.getProject(), null, isTempSave, isCopy, souType);
        /* 格式化及校验内/外部附件 */
        this.formatAndValidateInnerOuterFiles(param, null, isTempSave, isCopy);
    }

    /**
     * 数据转换
     * @param param
     * @param isTempSave
     * @param souType
     * @return
     */
    protected SouProjectEditPO convert(ApiSouProjectInfoDTO param, boolean isTempSave, String souType) {
        AssertUtils.notNull(param.getSequenceCode(), "缺少寻源单号生成规则");

        SouProjectEditPO po = new SouProjectEditPO();
        po.setSouProject(this.doConvertProject(param.getProject(), isTempSave, param.getSequenceCode(), souType));
        po.setCompSouProject(this.doConvertCompProject(param.getProject()));
        po.setGroupList(this.doConvertGroups(param.getProject().getProjectId(), param.getGroupList()));
        po.setCurrencyList(this.doConvertCurrencies(po.getSouProject().getProjectId(), param.getCurrencyList()));
        po.setFileConfigList(this.doConvertFileConfigs(po.getSouProject().getProjectId(), param.getFileConfigList()));
        List<SouFile> fileList = this.doConvertFiles(po.getSouProject().getProjectId(), param.getInnerFileList(), param.getOuterFileList());
        po.setSouFileList(fileList);

        return po;
    }

    protected void formatAndValidateProject(ApiSouProjectEditDTO param, Map<String/* currencyCode */, PurchaseCurrency> currencyMap,
                                            boolean isTempSave, boolean isCopy, String souType) {
        /* 2: 单据号(置空 - 后续处理) */
        param.setSouNo(null);
        /* 3: 单据名称 */
        param.setSouName(StringUtils.trimToNull(param.getSouName()));
        /* 7: 本位币 */
        param.setStandardCurrency(StringUtils.trimToNull(param.getStandardCurrency()));
        /* 19: 报名开始时间(置空) */
        param.setSignUpStartTime(null);
        boolean hasSignUpNode = true;
        if (hasSignUpNode) {
            if (param.getSignUpEndTime() == null) {
                AssertUtils.isTrue(isTempSave, "请输入报名截止时间");
            } else {
                AssertUtils.isTrue(param.getSignUpEndTime().after(new Date()), "报名截止时间必须晚于当前时间");
                if (param.getSignUpStartTime() != null) {
                    AssertUtils.isTrue(param.getSignUpStartTime().before(param.getSignUpEndTime()), "报名开始时间必须早于报名截止时间");
                }
            }
        } else {
            param.setSignUpStartTime(null);
        }
        /* 21: 报价开始时间 */
        if (param.getOrderStartTime() == null) {
            AssertUtils.isTrue(isTempSave, "请选择报价开始时间");
        } else {
            if (hasSignUpNode && param.getOrderEndTime() != null) {
                AssertUtils.isTrue(param.getOrderEndTime().after(param.getOrderStartTime()), "报价开始时间必须晚于报价截止时间");
            }
        }
        AssertUtils.isTrue(isTempSave || param.getOrderStartTime() != null, "请选择报价开始时间");
        /* 22: 报价截止时间 */
        AssertUtils.isTrue(isTempSave || param.getOrderStartTime() != null, "请选择报价截止时间");
        if (param.getOrderStartTime() != null && param.getOrderEndTime() != null) {
            AssertUtils.isTrue(param.getOrderStartTime().before(param.getOrderEndTime()), "报价开始时间必须早于报价截止时间");
        }
        /* 28: 是否采用未税价报价 */
        if (param.getIsPriceNotax() == null) {
            param.setIsPriceNotax(Enable.Y);
        }
        /* 29: 来源类型 */
        AssertUtils.notNull(param.getSourceFromType(), "请选择来源类型");
        AssertUtils.isTrue(param.getSourceFromType().length() <= 20, "来源类型的输入长度不能超过20");
        if (SouSourceFromTypeEnum.HAND_MAKE.name().equals(param.getSourceFromType())
                || SouSourceFromTypeEnum.PURCHASE_REQ.name().equals(param.getSourceFromType())) {
            param.setSourceFromId(null);
            param.setSourceFromNo(null);
        } else if (StringUtils.equals(SouSourceFromTypeEnum.SOU_REQ.name(), param.getSourceFromType())) {
            AssertUtils.notNull(param.getSourceFromId(), "请输入来源单据ID");
            AssertUtils.notNull(param.getSourceFromNo(), "请输入来源单据号");
            AssertUtils.isTrue(param.getSourceFromNo().length() <= 80, "来源单据号的输入长度不能超过80");
        } else {
            throw new IllegalArgumentException("寻源类型有误！");
        }
    }

    @Nullable
    protected String formatAndValidateGroupAuth(@Nullable String operateAuth, Set<String> availableOperateAuths, int index) {
        operateAuth = StringUtils.trimToNull(operateAuth);
        if (operateAuth == null) {
            return null;
        }
        String[] tempAuths = operateAuth.split(",");
        StringBuilder sb = new StringBuilder(100);
        for (String tempAuth : tempAuths) {
            tempAuth = StringUtils.trimToNull(tempAuth);
            if (tempAuth != null) {
                AssertUtils.isTrue(availableOperateAuths.contains(tempAuth), LocaleHandler.getLocaleMsg("工作小组第") + "{0}" + LocaleHandler.getLocaleMsg("行权限定义错误") + ":" + LocaleHandler.getLocaleMsg("非法的权限") + "[{1}]", index, tempAuth);
                sb.append(tempAuth);
                sb.append(",");
            }
        }
        return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : null;
    }

    protected void formatAndValidateCurrency(ApiSouProjectInfoDTO param, Map<String/* currencyCode */, PurchaseCurrency> currencyMap,
                                             boolean isTempSave, boolean isCopy) {
        if (param.getCurrencyList() == null) {
            param.setCurrencyList(new ArrayList<>());
        }

        boolean containsStandardCurrency = false;
        int index = 0;
        /*重复校验 */
        Set<String> set = new HashSet<>();
        List<ApiSouCurrencyEditDTO> list = new ArrayList<>();
        for (ApiSouCurrencyEditDTO c : param.getCurrencyList()) {
            index++;
            /* ID(略) */
            /* 币种编码 */
            c.setCurrencyCode(StringUtils.trimToNull(c.getCurrencyCode()));
            AssertUtils.isTrue(c.getCurrencyCode() != null || isTempSave, LocaleHandler.getLocaleMsg("可用币种第") + "{0}" + LocaleHandler.getLocaleMsg("行请选择币种"), index);
            if (c.getCurrencyCode() != null) {
                AssertUtils.isTrue(currencyMap.containsKey(c.getCurrencyCode()), LocaleHandler.getLocaleMsg("可用币种第") + "{0}" + LocaleHandler.getLocaleMsg("行定义错误") + ": " + LocaleHandler.getLocaleMsg("币种") + "[{1}]" + LocaleHandler.getLocaleMsg("不存在"), index, c.getCurrencyCode());
                if (c.getCurrencyCode().equals(param.getProject().getStandardCurrency())) {
                    containsStandardCurrency = true;
                }
            }
            /* 价格精确度 */
            AssertUtils.isTrue(c.getPricePrecision() != null || isTempSave, LocaleHandler.getLocaleMsg("可用币种第") + "{0}" + LocaleHandler.getLocaleMsg("行请选择价格精确度"), index);
            if (c.getPricePrecision() != null) {
                AssertUtils.isTrue(c.getPricePrecision() >= 0 && c.getPricePrecision() <= 8,
                        "可用币种第{0}行价格精确度必须是0~8之间");
            }
            /* 排序 */
            c.setSortIndex(index);
            if (Objects.nonNull(c.getCurrencyCode())) {
                if (set.contains(c.getCurrencyCode())) {
                    /*重复 */
                    if (!c.getCurrencyCode().equals(param.getProject().getStandardCurrency())) {
                        /*不是本位币，报错。 本位币忽略 */
                        throw new IllegalArgumentException(
                                String.format("报价币种设置不能重复:[%s]", currencyMap.get(c.getCurrencyCode()).getCurrencyName()));
                    }
                } else {
                    /*自动去重 */
                    set.add(c.getCurrencyCode());
                    list.add(c);
                }
            }
        }
        /*设置1个本位币 */
        if (!containsStandardCurrency && param.getProject().getStandardCurrency() != null
                && !set.contains(param.getProject().getStandardCurrency())) {
            ApiSouCurrencyEditDTO c = new ApiSouCurrencyEditDTO();
            c.setCurrencyCode(param.getProject().getStandardCurrency());
            c.setPricePrecision(param.getProject().getPricePrecision());
            c.setSortIndex(100);
            param.getCurrencyList().add(c);
            set.add(c.getCurrencyCode());
            list.add(c);
        }
        param.setCurrencyList(list);
    }


    protected void formatAndValidateInnerOuterFiles(ApiSouProjectInfoDTO param, @Nullable SouProcessConfig processConfig, boolean isTempSave, boolean isCopy) {
        if (CollectionUtils.isNotEmpty(param.getInnerFileList())) {
            int index = 0;
            for (ApiSouFileEditDTO souFile : param.getInnerFileList()) {
                index++;
                /* 文件ID */
                AssertUtils.notNull(souFile.getSouDocId(), LocaleHandler.getLocaleMsg("内部附件列表第") + "{0}" + LocaleHandler.getLocaleMsg("行请上传附件"), index);
                /* 文件名称 */
                souFile.setSouFileName(StringUtils.trimToNull(souFile.getSouFileName()));
                AssertUtils.notNull(souFile.getSouFileName(), LocaleHandler.getLocaleMsg("内部附件列表第") + "{0}" + LocaleHandler.getLocaleMsg("行请上传附件"), index);
                AssertUtils.isTrue(souFile.getSouFileName().length() <= 150, LocaleHandler.getLocaleMsg("内部附件列表第") + "{0}" + LocaleHandler.getLocaleMsg("行附件名称长度不能超过150"), index);
                souFile.setFileType(SouFileTypeEnum.INNER);
                /* 备注 */
                souFile.setSouRemark(StringUtils.trimToNull(souFile.getSouRemark()));
                if (souFile.getSouRemark() != null) {
                    AssertUtils.isTrue(souFile.getSouRemark().length() <= 300, LocaleHandler.getLocaleMsg("内部附件列表第") + "{0}" + LocaleHandler.getLocaleMsg("行备注长度不能超过300"), index);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(param.getOuterFileList())) {
            int index = 0;
            for (ApiSouFileEditDTO souFile : param.getOuterFileList()) {
                index++;
                /* 文件ID */
                AssertUtils.notNull(souFile.getSouDocId(), LocaleHandler.getLocaleMsg("外部附件列表第") + "{0}" + LocaleHandler.getLocaleMsg("行请上传附件"), index);
                /* 文件名称 */
                souFile.setSouFileName(StringUtils.trimToNull(souFile.getSouFileName()));
                AssertUtils.notNull(souFile.getSouFileName(), LocaleHandler.getLocaleMsg("外部附件列表第") + "{0}" + LocaleHandler.getLocaleMsg("行请上传附件"), index);
                AssertUtils.isTrue(souFile.getSouFileName().length() <= 150, LocaleHandler.getLocaleMsg("外部附件列表第") + "{0}" + LocaleHandler.getLocaleMsg("行附件名称长度不能超过150"), index);
                souFile.setFileType(SouFileTypeEnum.OUTER);
                /* 备注 */
                souFile.setSouRemark(StringUtils.trimToNull(souFile.getSouRemark()));
                if (souFile.getSouRemark() != null) {
                    AssertUtils.isTrue(souFile.getSouRemark().length() <= 300, LocaleHandler.getLocaleMsg("外部附件列表第") + "{0}" + LocaleHandler.getLocaleMsg("行备注长度不能超过300"), index);
                }
            }
        }
    }

    /** 格式化及校验供方必须上传附件 */
    protected void doValidateFileConfigs(ApiSouProjectInfoDTO param, @Nullable SouProcessConfig processConfig, boolean isTempSave, boolean isCopy) {
        if (param.getFileConfigList() == null || param.getFileConfigList().isEmpty()) {
            if (isTempSave) {
                return;
            } else {
                param.setFileConfigList(new ArrayList<>());
            }
        }

        int index = 0;
        for (ApiSouFileConfigEditDTO fileConfig : param.getFileConfigList()) {
            index++;
            /* ID(略) */
            /* 附件类型 */
            if (fileConfig.getFileType() == null) {
                throw new IllegalArgumentException(MessageFormat.format("供方必须上传附件第{0}行请选择文件类型", index));
            }
            /* 文档ID */
            if (fileConfig.getRequireDocId() == null) {
                throw new IllegalArgumentException(MessageFormat.format("供方必须上传附件第{0}行请上传参考文件", index));
            }
            /* 文件名 */
            fileConfig.setRequireFileName(SouUtils.validateStringNullAndLength(isTempSave, fileConfig.getRequireFileName(), null,
                    50,
                    MessageFormat.format("供方必须上传附件第{0}行请上传参考文件", index),
                    MessageFormat.format("供方必须上传附件第{0}行文件名长度不能超过50", index)));
            /* 资料要求 */
            fileConfig.setFileRequire(SouUtils.validateStringNullAndLength(isTempSave, fileConfig.getFileRequire(), null,
                    120, MessageFormat.format("供方必须上传附件第{0}行请输入资料要求", index),
                    MessageFormat.format("供方必须上传附件第{0}行资料要求长度不能超过120", index)));
            /* 备注 */
            fileConfig.setRequireRemark(SouUtils.validateStringNullAndLength(true, fileConfig.getRequireRemark(), null,
                    300, null, MessageFormat.format("供方必须上传附件第{0}行备注长度不能超过300", index)));
            /* 设置排序 */
            fileConfig.setSortIndex(index);
        }
    }

    /** 转换得到寻源信息 */
    protected SouProject doConvertProject(ApiSouProjectEditDTO project, boolean isTempSave, String sequenceCode, String souType) {
        SouProject entity;
        if (project.getProjectId() != null) {
            entity = souProjectDao.getById(project.getProjectId());
            SouObjectXUtil.mergePropertiesOnlySpecified(project, entity,
                    //寻源单名称
                    SouProject::getSouName,
                    //流程配置ID
                    SouProject::getProcessConfigId,
                    //评选方式
                    SouProject::getScoreRuleType,
                    //评分模板ID
                    SouProject::getScoreTemplateId,
                    //评分模板名称
                    SouProject::getScoreTemplateName,
                    //本位币
                    SouProject::getStandardCurrency,
                    //价格精度
                    SouProject::getPricePrecision,
                    //是否密封报价
                    SouProject::getNeedEncryptPrice,
                    //预计报价地点
                    SouProject::getOrderSite,
                    //是否同步至价格库
                    SouProject::getIsSyncToPriceLibrary,
                    //需要密码解密的操作
                    SouProject::getNeedPwdOperations,
                    //是否允许物料变更
                    SouProject::getAllowItemChange,
                    //是否允许追加供应商
                    SouProject::getAllowNewVendors,
                    //是否允许代理报价
                    SouProject::getAllowProxyOrder,
                    //价格有效期开始时间
                    SouProject::getPriceStartTime,
                    //价格有效期截止时间
                    SouProject::getPriceEndTime,
                    //报名开始时间
                    SouProject::getSignUpStartTime,
                    //报名截止时间
                    SouProject::getSignUpEndTime,
                    //报价开始时间
                    SouProject::getOrderStartTime,
                    //报价截止时间
                    SouProject::getOrderEndTime,
                    //最早开标时间
                    SouProject::getEarliestBusinessOpenTime,
                    //发布范围
                    SouProject::getPublishScope,
                    //报价方式
                    SouProject::getOrderWay,
                    //报价类型
                    SouProject::getOrderType,
                    //是否允许撤回报价
                    SouProject::getAllowWithdraw,
                    //是否允许部分报价
                    SouProject::getAllowPartPrice,
                    //是否使用未税报价
                    SouProject::getIsPriceNotax,
                    //联系人
                    SouProject::getLinkman,
                    //电话
                    SouProject::getTel,
                    //邮箱
                    SouProject::getEmail,
                    //竞价规则
                    SouProject::getSouRules,
                    //公开规则
                    SouProject::getPublicRules,
                    //每项物资报价次数
                    SouProject::getOrderNum,
                    //报价上限
                    SouProject::getQuoteCap,
                    //备注
                    SouProject::getRemark);
        } else {
            entity = new SouProject();
            BeanUtils.copyProperties(project, entity);
            //ID
            entity.setProjectId(IdGenrator.generate());
            //寻源类型
            entity.setSouType(souType);
            //本轮已报价数量/待报价数量
            entity.setInviteCount(null);
            entity.setOrderCount(null);
            String souNo = baseClient.seqGen(sequenceCode);
            entity.setSouNo(souNo);
            project.setSouNo(souNo);
            project.setProjectId(entity.getProjectId());
        }
        //项目状态
        entity.setProjectStatus(SouProjectStatusEnum.DRAFT);
        //立项审核状态
        entity.setCreateApprovalStatus(SouApprovalStatusEnum.DRAFT);
        //当前轮次
        if (!isTempSave) {
            entity.setCurrentRound(1);
        }

        return entity;
    }

    protected CompSouProject doConvertCompProject(ApiSouProjectEditDTO project) {
        CompSouProject entity;
        if (project.getProjectId() != null) {
            entity = compSouProjectDao.getById(project.getProjectId());
            if(Objects.isNull(entity)) {
                entity = new CompSouProject();
                BeanUtils.copyProperties(project, entity);
            } else {
                SouObjectXUtil.mergePropertiesOnlySpecified(project, entity,
                        //寻源单ID
                        CompSouProject::getProjectId,
                        //预算金额
                        CompSouProject::getBudgetAmount,
                        //最小涨/跌幅百分比
                        CompSouProject::getMinPercent,
                        //最小涨/跌金额
                        CompSouProject::getMinAmount,
                        //截止至前几分钟
                        CompSouProject::getExtendTrigger,
                        //延长多少分钟
                        CompSouProject::getExtendMinute,
                        //中标供应商数量
                        CompSouProject::getMaxWinVendorCount,
                        //汇率类型[字典值: EXCHANGE_RATE_TYPE
                        CompSouProject::getExchangeRateType,
                        //汇率类型[币种转换日期
                        CompSouProject::getCurrencyExchangeDate,
                        //商务要求 -- 保证金金额
                        CompSouProject::getBondAmount,
                        //商务要求 -- 保证金说明
                        CompSouProject::getBondDesc,
                        //商务要求 -- 保证金提交方式[字典值: BID_BOND_SUBMISSION
                        CompSouProject::getBondMethod,
                        //商务要求 -- 保证金提交截止时间
                        CompSouProject::getBondEndTime,
                        //商务要求 -- 保证金缴纳账号
                        CompSouProject::getBankAccountNum,
                        //商务要求 -- 账户名称
                        CompSouProject::getBankAccountName,
                        //商务要求 -- 开户支行
                        CompSouProject::getBankBranchName,
                        //需求简述
                        CompSouProject::getRequireDesc);
            }
        } else {
            entity = new CompSouProject();
            BeanUtils.copyProperties(project, entity);
        }
        return entity;
    }


    protected List<SouGroup> doConvertGroups(long projectId, @Nullable List<ApiSouGroupEditDTO> groupList) {
        if (CollectionUtils.isEmpty(groupList)) {
            return new ArrayList<>();
        }

        Set<String> usernames = groupList.stream().map(ApiSouGroupEditDTO::getUserName).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<String/* username */, User> userMap = rbacClient.listByUserNames(usernames)
                .stream().collect(Collectors.toMap(User::getUsername, Function.identity()));

        List<SouGroup> entityList = new ArrayList<>(groupList.size());
        for (ApiSouGroupEditDTO group : groupList) {
            SouGroup groupEntity = SouObjectXUtil.convertTargetObj(group, SouGroup.class);
            /* ID */
            if (groupEntity.getGroupId() == null) {
                groupEntity.setGroupId(IdGenrator.generate());
            }
            /* 寻源单ID */
            groupEntity.setProjectId(projectId);
            /* 用户信息 */
            User user = userMap.get(group.getUserName());
            groupEntity.setUserId(user.getUserId());
            groupEntity.setFullName(user.getNickname());

            entityList.add(groupEntity);
            SouObjectXUtil.mergeProperties(groupEntity, group);
        }
        return entityList;
    }

    protected List<SouFile> doConvertFiles(long projectId, @Nullable List<ApiSouFileEditDTO> innerFiles, @Nullable List<ApiSouFileEditDTO> outerFiles) {
        List<SouFile> fileList = new ArrayList<>();
        addSouFile(projectId, innerFiles, fileList);

        addSouFile(projectId, outerFiles, fileList);
        return fileList;
    }

    private void addSouFile(long projectId, @Nullable List<ApiSouFileEditDTO> outerFiles, List<SouFile> fileList) {
        if (CollectionUtils.isEmpty(outerFiles)) {
            return;
        }
        if (!CollectionUtils.isEmpty(outerFiles)) {
            for (ApiSouFileEditDTO outerFile : outerFiles) {
                SouFile entity = SouObjectXUtil.convertTargetObj(outerFile, SouFile.class);
                if (entity.getSouFileId() == null) {
                    entity.setSouFileId(IdGenrator.generate());
                }
                entity.setProjectId(projectId);

                fileList.add(entity);
                SouObjectXUtil.mergeProperties(entity, outerFile);
            }
        }
    }

    protected List<SouFileConfig> doConvertFileConfigs(long projectId, @Nullable List<ApiSouFileConfigEditDTO> fileConfigList) {
        if (CollectionUtils.isEmpty(fileConfigList)) {
            return new ArrayList<>();
        }
        List<SouFileConfig> entityList = new ArrayList<>(fileConfigList.size());

        for (ApiSouFileConfigEditDTO fileConfig : fileConfigList) {
            SouFileConfig entity = SouObjectXUtil.convertTargetObj(fileConfig, SouFileConfig.class);
            if (entity.getSouFileConfigId() == null) {
                entity.setSouFileConfigId(IdGenrator.generate());
            }
            entity.setProjectId(projectId);

            entityList.add(entity);
            SouObjectXUtil.mergeProperties(entity, fileConfig);
        }
        return entityList;
    }

    protected List<SouCurrency> doConvertCurrencies(long projectId, @Nullable List<ApiSouCurrencyEditDTO> currencyList) {
        if (CollectionUtils.isEmpty(currencyList)) {
            return new ArrayList<>();
        }
        List<SouCurrency> entityList = new ArrayList<>(currencyList.size());
        for (ApiSouCurrencyEditDTO currency : currencyList) {
            SouCurrency entity = SouObjectXUtil.convertTargetObj(currency, SouCurrency.class);
            /* ID */
            if (entity.getSouCurrencyId() == null) {
                entity.setSouCurrencyId(IdGenrator.generate());
            }
            /* 寻源单ID */
            entity.setProjectId(projectId);

            entityList.add(entity);
            SouObjectXUtil.mergeProperties(entity, currency);
        }
        return entityList;
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
