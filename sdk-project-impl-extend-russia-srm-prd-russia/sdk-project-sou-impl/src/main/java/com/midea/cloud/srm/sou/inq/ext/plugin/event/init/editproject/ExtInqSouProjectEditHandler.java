package com.midea.cloud.srm.sou.inq.ext.plugin.event.init.editproject;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiSouProjectEditDTO;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.inq.spi.init.editproject.InqSouProjectEditHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Component;
/**
 * 备注
 * @author huangbf3
 */
@Component
public class ExtInqSouProjectEditHandler extends InqSouProjectEditHandler {

    @Override
    @ApiOperation("转换得到寻源信息")
    protected SouProject doConvertProject(ApiSouProjectEditDTO project, boolean isTempSave, String sequenceCode, String souType) {
        SouProject souProject = super.doConvertProject(project, isTempSave, sequenceCode, souType);

        souProject.setCurrentRound(1);
        souProject.setAllowPartPrice(Enable.Y);
        return souProject;
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.inq.name();
    }

    @Override
    public int getOrder() {
        return 100;
    }

}
