package com.midea.cloud.srm.sou.extsourcings.spi.process;

import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouProjectDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.init.ApiExtSouProcessConfigVo;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.init.ApiSouProcessNodeVO;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProcessConfig;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouProcessNodeEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProcessConfigService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.spi.process.ApiSouProcessQueryHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Service
@ApiModel("寻源非核心包下，招标流程节点后置处理（二开增加了流程节点）")
public class ApiExtSouBidProcessQueryHandler extends ApiSouProcessQueryHandler {

    @Autowired
    private IExtSouProcessConfigService processConfigService;

    @Autowired
    private IExtSouProjectService projectService;

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.bid.name();
    }

    @Override
    @ApiOperation("查询流程节点信息后的额外处理")
    public List<ApiSouProcessNodeVO> doHandlerAfterListProcessNodes(long projectId, String souType, List<ApiSouProcessNodeVO> voList) {
        //重新修正节点状态
        if(SouTypeEnum.bid.name().equals(souType) && CollectionUtils.isNotEmpty(voList)) {

            ExtSouProcessConfig processConfig = processConfigService.getById(voList.get(0).getProcessConfigId());
            voList.stream().forEach(vo -> {
                if(SouProcessNodeEnum.bidReuslt.name().equals(vo.getProcessNode())) {
                    vo.setEnabled(processConfig.getExtBidReuslt());
                    return;
                }
                if(SouProcessNodeEnum.bidWinOrLoss.name().equals(vo.getProcessNode())) {
                    vo.setEnabled(processConfig.getExtBidWinOrLoss());
                    return;
                }
                if(SouProcessNodeEnum.bidArchive.name().equals(vo.getProcessNode())) {
                    vo.setEnabled(processConfig.getExtBidArchive());
                    return;
                }
                if(SouProcessNodeEnum.bondManagement.name().equals(vo.getProcessNode())) {
                    vo.setEnabled(processConfig.getExtBondManagement());
                    return;
                }
            });

            ApiExtSouProcessConfigVo vo = new ApiExtSouProcessConfigVo();
            BeanCopyUtil.copyProperties(vo, processConfig);

            ExtSouProject project = projectService.getById(projectId);

            //修正节点
            ExtSouProjectDto extSouProjectDto = new ExtSouProjectDto();
            BeanCopyUtil.copyProperties(extSouProjectDto, project);
            extSouProjectDto.setProcessConfig(vo);
            extSouProjectDto.setProcessNodeList(voList);
            processConfigService.fixNpmProcessAndNode(extSouProjectDto);
        }

        return voList;
    }
}
