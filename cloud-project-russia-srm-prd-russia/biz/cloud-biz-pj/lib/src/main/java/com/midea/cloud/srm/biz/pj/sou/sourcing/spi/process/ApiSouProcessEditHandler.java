package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.process;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProcessConfigDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouProcessConfigStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouPublishScopeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 寻源openAPI - 流程配置保存服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/29
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ApiSouProcessEditHandler implements ISouSpiBean {

    @Autowired
    private SouProcessConfigDAOImpl souProcessConfigDao;

    public SouProcessConfig formatValidateAndConvert(SouProcessConfig param, boolean isTempSave) {
        // 1: 数据格式化及校验
        this.formatAndValidate(param, isTempSave);
        // 2: 数据转换
        return this.convert(param, isTempSave);
    }

    /**
     * 入参格式化及校验
     * @param param
     * @param isTempSave
     */
    protected void formatAndValidate(SouProcessConfig param, boolean isTempSave) {
        // 1: ID(略)
        // 2: 寻源类型
        AssertUtils.notNull(param.getSouType(), "请选择寻源类型");
        // 3: 流程配置名称
        param.setProcessConfigName(StringUtils.trimToNull(param.getProcessConfigName()));
        AssertUtils.notNull(param.getProcessConfigName(), "请输入流程配置名称");
        AssertUtils.isTrue(param.getProcessConfigName().length() <= 50, "流程配置名称的输入长度不能超过50");
        long existCount = souProcessConfigDao.lambdaQuery()
                .ne(param.getProcessConfigId() != null, SouProcessConfig::getProcessConfigId, param.getProcessConfigId())
                .eq(SouProcessConfig::getProcessConfigName, param.getProcessConfigName())
                .eq(SouProcessConfig::getSouType, param.getSouType())
                .count();
        AssertUtils.isTrue(existCount <= 0, "同一寻源类型下流程配置名称不能重复");
        // 4: 状态(置空 - 后端处理)
        param.setProcessStatus(null);
        // 5: 发布范围
        AssertUtils.isTrue(param.getPublishScope() != null || isTempSave, "请选择发布范围");
        // 6: 评选方式
        AssertUtils.isTrue(param.getScoreRuleType() != null || isTempSave, "请选择评选方式");
        // 7: 节点-项目信息
        param.setProjectInfo(Enable.Y);
        // 8: 节点-项目需求
        param.setRequireInfo(Enable.Y);
        // 9: 节点-邀请供应商
        param.setInviteVendor(SouPublishScopeEnum.INVITE_TENDER.equals(param.getPublishScope()) ? Enable.Y : Enable.N);
        // 10: 节点-评分规则
        param.setScoreRule(SouScoreRuleTypeEnum.COMPOSITE_PRICE.equals(param.getScoreRuleType()) ? Enable.Y : Enable.N);
        // 11: 节点-立项审批
        if (param.getCreateApproval() == null) {
            param.setCreateApproval(Enable.N);
        }
        // 12: 节点-报名管理
        if (param.getSignUpManagement() == null) {
            param.setSignUpManagement(Enable.N);
        }
        // 12: 节点-投标控制
        param.setBidingControl(Enable.Y);
        // 13: 节点-技术标管理
        if (param.getScoreRuleType() != null) {
            if (SouScoreRuleTypeEnum.COMPOSITE_PRICE.equals(param.getScoreRuleType())) {
                param.setTechManagement(Enable.Y);
            } else {
                param.setTechManagement(Enable.N);
            }
        } else {
            if (param.getTechManagement() == null) {
                param.setTechManagement(Enable.N);
            }
        }
        // 14: 节点-商务标管理
        param.setBusinessManagement(Enable.Y);
        // 15: 节点-评选
        param.setEvaluation(Enable.Y);
        // 16: 备注
        param.setRemark(StringUtils.trimToNull(param.getRemark()));
        if (param.getRemark() != null) {
            AssertUtils.isTrue(param.getRemark().length() <= 300, "备注输入长度不能超过300");
        }
    }

    /**
     * 数据转换
     * @param param
     * @param isTempSave
     * @return
     */
    protected SouProcessConfig convert(SouProcessConfig param, boolean isTempSave) {
        // ID
        boolean isNew = param.getProcessConfigId() == null;
        if (param.getProcessConfigId() == null) {
            param.setProcessConfigId(IdGenrator.generate());
        }
        // 状态
        param.setProcessStatus(isTempSave ? SouProcessConfigStatusEnum.DRAFT : SouProcessConfigStatusEnum.VALID);

        SouProcessConfig entity;
        if (isNew) {
            entity = new SouProcessConfig();
        } else {
            entity = souProcessConfigDao.getById(param.getProcessConfigId());
        }
        BaseEntity.copyProperties(param, entity);
        if (entity.getProcessConfigId() == null) {
            entity.setProcessConfigId(IdGenrator.generate());
        }
        BeanUtils.copyProperties(entity, param);
        return entity;
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
