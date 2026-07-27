package com.midea.cloud.srm.sou.sourcing.spi.init.editscorerules;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouScoreRuleDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouVendorDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtScoreRule;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouScoreRuleService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouVendorService;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.sou.sourcing.spi.init.editinvitesuppliers.ExtSouVendorEditPO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class ApiExtSouScoreRuleEditHandler implements ISouSpiBean {

    @Autowired
    private IExtSouScoreRuleService scoreRuleService;

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

    public ExtSouScoreRuleEditPO formatValidateAndConvert(ApiExtSouScoreRuleDto param, String souType) {
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
    protected void formatAndValidate(ApiExtSouScoreRuleDto param, String souType) {

    }

    /**
     * 数据转换
     * @param param 参数
     * @param souType 参数
     * @return 返回
     */
    protected ExtSouScoreRuleEditPO convert(ApiExtSouScoreRuleDto param, String souType) {
        ExtSouScoreRuleEditPO po = new ExtSouScoreRuleEditPO();
        po.setScoreRuleList(this.doConvertScoreRule(param, souType));
        return po;
    }

    /**
     * 转换得到寻源信息
     * @param param 参数
     * @param souType 参数
     * @return 返回
     */
    protected List<ExtScoreRule> doConvertScoreRule(ApiExtSouScoreRuleDto param, String souType) {

        List<ExtScoreRule> scoreRuleList = param.getScoreRuleList();
        if(Objects.isNull(scoreRuleList)) {
            scoreRuleList = new ArrayList<>();
        }

        scoreRuleList.stream().forEach(item -> {
            if(Objects.isNull(item.getScoreRuleId())) {
                item.setScoreRuleId(IdGenrator.generate());
            }
            item.setProjectId(param.getProjectId());
        });
        return scoreRuleList;
    }

    @ApiOperation("评分规则信息保存前的额外处理")
    public void doHandlerBeforeEditProject(ApiExtSouScoreRuleDto param, String souType) {
    }

    @ApiOperation("评分规则信息保存后的额外处理")
    public void doHandlerAfterEditProject(ApiExtSouScoreRuleDto param, String souType, ExtSouScoreRuleEditPO po) {
    }


}
