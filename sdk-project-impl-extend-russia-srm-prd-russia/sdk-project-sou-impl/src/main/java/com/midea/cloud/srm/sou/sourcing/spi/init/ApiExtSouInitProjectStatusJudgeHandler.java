package com.midea.cloud.srm.sou.sourcing.spi.init;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouProjectInfoDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouProjectModifyDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouApprovalStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouProjectMapper;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.sou.sourcing.spi.init.editprojectstatuss.ApiProjectStatusFactory;
import com.midea.cloud.srm.sou.sourcing.spi.init.editprojectstatuss.ApiProjectStatusRangeVo;
import com.midea.cloud.srm.sou.sourcing.spi.init.editproject.ExtSouProjectEditPO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Objects;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class ApiExtSouInitProjectStatusJudgeHandler implements ISouSpiBean {

    @Autowired
    private IExtSouProjectService projectService;

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }


    @ApiOperation("项目信息保存前的额外处理")
    public void doHandlerBeforeEditProject(ApiExtSouProjectModifyDto param, boolean isCopy, String souType) {
    }

    public ExtSouProjectEditPO doHandlerValidAndConvertEditProject(ApiExtSouProjectModifyDto param, boolean isCopy, String souType) {
        ExtSouProject project = projectService.getById(param.getProjectId());
        AssertUtils.notNull(project, "单据信息不存在！");

        ApiProjectStatusRangeVo<SouBiddingProStatusEnum> vo = ApiProjectStatusFactory.projectStatus(project.getProjectStatus());
        AssertUtils.notNull(vo, "当前单据状态存在异常！");

        SouBiddingProStatusEnum proStatusEnum = SouBiddingProStatusEnum.valueOf(param.getProjectStatus());
        AssertUtils.notNull(proStatusEnum, "请求参数单据状态存在异常！");
        Boolean check = false;

        //状态不一样
        if(!proStatusEnum.getCode().equals(project.getProjectStatus())) {
            //允许状态回滚
            if(!Objects.isNull(vo.getPreStatus()) && vo.getPreStatus().contains(proStatusEnum)) {
                check = true;
            }

            //允许状态进入下一步
            if(!Objects.isNull(vo.getNextStatus()) && vo.getNextStatus().contains(proStatusEnum)) {
                check = true;
            }

            AssertUtils.isTrue(check, MessageFormat.format("当前单据状态[{0}]不允许修改成状态[{1}]！", vo.getStatus().getName(), proStatusEnum.getName()));

            project.setProjectStatus(param.getProjectStatus());
        }

        ExtSouProjectEditPO po = new ExtSouProjectEditPO();
        po.setProject(project);
        return po;
    }

    @ApiOperation("项目信息保存后的额外处理")
    public void doHandlerAfterEditProject(ApiExtSouProjectModifyDto param, boolean isCopy, String souType, ExtSouProjectEditPO po) {
    }

}
