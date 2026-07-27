package com.midea.cloud.srm.sou.sourcing.spi.init.edittechscores;

import com.alibaba.fastjson.JSON;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.model.sou.enums.TechScoreStatusEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtScoreRuleDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouTechScoreDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouTechScoreHeadDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouTechScoreHead;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouTechScoreHeadService;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import com.mideacloud.common.objectx.BaseObjectX;
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
public class ApiExtSouTechScoreHeadJudgeHandler implements ISouSpiBean {

    @Autowired
    private IExtSouTechScoreHeadService scoreHeadService;

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public ExtSouTechScorePO formatValidateAndConvert(ExtSouTechScoreHeadDto param, String souType) {
        // 1: 数据格式化及校验
        this.formatAndValidate(param, souType);
        // 2: 数据转换
        return this.convert(param, souType);
    }

    /**
     * 入参格式化及校验
     * @param param 参数
     * @param souType 参数
     */
    protected void formatAndValidate(ExtSouTechScoreHeadDto param, String souType) {
        //校验数据
        ExtSouTechScoreHead head = scoreHeadService.getById(param.getTechScoreHeadId());
        if(YesOrNo.YES.getValue().equals(head.getExtConfirmFlag())) {
            throw new BaseException("已确认评标，不允许退回评标！");
        }

    }

    /**
     * 数据转换
     * @param param 参数
     * @param souType 参数
     * @return 返回
     */
    protected ExtSouTechScorePO convert(ExtSouTechScoreHeadDto param, String souType) {
        ExtSouTechScorePO po = new ExtSouTechScorePO();
        po.setScoreHead(this.doConvertScoreHead(param, souType));
        return po;
    }

    /**
     * 转换得到评分头
     * @param param 参数
     * @param souType 参数
     * @return 返回
     */
    protected ExtSouTechScoreHead doConvertScoreHead(ExtSouTechScoreHeadDto param, String souType) {

        ExtSouTechScoreHead head = JSON.parseObject(JSON.toJSONString(param), ExtSouTechScoreHead.class);
        head.setScoreStatus(TechScoreStatusEnum.REJECT.getCode());
        return head;
    }

    @ApiOperation("退回评分前的额外处理")
    public void doHandlerBeforeReject(ExtSouTechScoreHeadDto param, String souType) {
    }

    @ApiOperation("退回评分后的额外处理")
    public void doHandlerAfterEditReject(ExtSouTechScoreHeadDto param, String souType, ExtSouTechScorePO po) {
    }
}
