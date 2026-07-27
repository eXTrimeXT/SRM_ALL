package com.midea.cloud.srm.sou.sourcing.spi.init;

import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.srm.model.sou.enums.ExtOrderTypeEnum;
import com.midea.cloud.srm.model.sou.enums.ExtSouGroupRoleEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouProjectQueryDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouTechManageQueryRespDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtNpmSouOpenBidRecordDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtNpmSouOpenBidRecord;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouGroup;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.bid.openrecords.service.IExtNpmSouOpenBidRecordService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouGroupService;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Service
public class ApiExtSouTechManagementQueryHandler implements ISouSpiBean {

    @Autowired
    private IExtNpmSouOpenBidRecordService openBidRecordService;

    @Autowired
    private IExtSouGroupService groupService;

    @ApiOperation("技术标管理查询的后置处理")
    public void doHandlerAfterGetTechManagement(Long projectId, String souType, ApiExtSouTechManageQueryRespDto resp) {
        //确认评标标识
        resp.setExtConfirmFlag(YesOrNo.NO.getValue());
        if(CollectionUtils.isNotEmpty(resp.getEvaTechScoreList())) {
            boolean confirmFlag = resp.getEvaTechScoreList().stream().filter(s -> YesOrNo.YES.getValue().equals(s.getExtConfirmFlag())).findAny().isPresent();
            if(confirmFlag) {
                resp.setExtConfirmFlag(YesOrNo.YES.getValue());
            }
        }
        //查询开标处理人
        List<ExtNpmSouOpenBidRecordDto> techOpenUserList = openBidRecordService.queryTechOpenRecord(projectId);
        resp.setOpenUserList(techOpenUserList);
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
