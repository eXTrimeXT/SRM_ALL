package com.midea.cloud.srm.sou.sourcing.spi.init;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouProjectMapper;
import com.midea.cloud.srm.sou.sourcing.spi.init.editproject.ExtSouProjectEditPO;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouProjectInfoDTO;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouApprovalStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class ApiExtSouInitJudgeHandler implements ISouSpiBean {

    @Autowired
    private ExtSouProjectMapper projectMapper;

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public ExtSouProject judgeEditProjectAuth(Long projectId, String souType) {
        if(Objects.isNull(projectId)) {
            return null;
        }
        ExtSouProject project = projectMapper.selectById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源信息")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), projectId);
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型")+"[{0}]"+LocaleHandler.getLocaleMsg("不匹配"), souType);
        AssertUtils.isTrue(SouBiddingProStatusEnum.DRAW_UP.getCode().equals(project.getProjectStatus()), "非拟定状态，禁止修改寻源单");


        if(StringUtils.isBlank(project.getCreateApprovalStatus())) {
            project.setCreateApprovalStatus(SouApprovalStatusEnum.DRAFT.name());
        }
        SouApprovalStatusEnum souApprovalStatusEnum = SouApprovalStatusEnum.valueOf(project.getCreateApprovalStatus());
        switch (souApprovalStatusEnum) {
            case DRAFT:
                // 拟定
            case REJECTED:
                // 已驳回
            case WITHDRAW:
                // 已撤回
            case ABANDONED:
                // 已作废
                break;
            case SUBMITTED:
                // 已提交
                throw new IllegalArgumentException("寻源单已提交审批，禁止修改");
            case APPROVED:
                // 已审批
                throw new IllegalArgumentException("寻源单已审批通过，禁止修改");
            default:;
        }
        return project;
    }

    @ApiOperation("项目信息保存前的额外处理")
    public void doHandlerBeforeEditProject(ApiExtSouProjectInfoDTO param, boolean isCopy, String souType) {
    }

    @ApiOperation("项目信息保存后的额外处理")
    public void doHandlerAfterEditProject(ApiExtSouProjectInfoDTO param, boolean isCopy, String souType, ExtSouProjectEditPO po) {
    }

}
