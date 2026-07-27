package com.midea.cloud.srm.sou.sourcing.spi.init.editpricetemplates;

import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.model.sou.enums.SouMarginRecordTypeEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouMarginRecordDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouPriceTemplateDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouMarginRecord;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouPriceTemplate;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouMarginRecordService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouPriceTemplateService;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.sou.sourcing.spi.init.editmargins.ExtSouMarginRecordPo;
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
public class ApiExtSouPriceTemplateEditHandler implements ISouSpiBean {

    @Autowired
    private IExtSouPriceTemplateService priceTemplateService;

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public ExtSouPriceTemplatePo formatValidateAndConvert(ApiExtSouPriceTemplateDto param, String souType) {
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
    protected void formatAndValidate(ApiExtSouPriceTemplateDto param, String souType) {

    }

    /**
     * 数据转换
     * @param param 参数
     * @param souType 参数
     * @return 返回
     */
    protected ExtSouPriceTemplatePo convert(ApiExtSouPriceTemplateDto param, String souType) {
        ExtSouPriceTemplatePo po = new ExtSouPriceTemplatePo();
        po.setPriceTemplateList(this.doConvertProject(param, souType));
        return po;
    }

    /**
     * 转换得到寻源信息
     * @param param 参数
     * @param souType 参数
     * @return 返回
     */
    protected List<ExtSouPriceTemplate> doConvertProject(ApiExtSouPriceTemplateDto param, String souType) {
        List<ExtSouPriceTemplate> priceTemplateList = param.getSelectedList();
        if(Objects.isNull(priceTemplateList)) {
            priceTemplateList = new ArrayList<>();
        }

        AtomicInteger index = new AtomicInteger(1);
        priceTemplateList.stream().forEach(item -> {
            item.setProjectId(param.getProjectId());
            item.setColumnDefault(YesOrNo.YES.getValue());
            item.setColnmnSort(index.getAndAdd(1));
            if(Objects.isNull(item.getTemplateId()) || Long.compare(item.getProjectId(), param.getProjectId()) != 0) {
                item.setTemplateId(IdGenrator.generate());
            }
        });
        return priceTemplateList;
    }

    @ApiOperation("项目信息保存前的额外处理")
    public void doHandlerBeforeEditProject(ApiExtSouPriceTemplateDto param, String souType) {
    }

    @ApiOperation("项目信息保存后的额外处理")
    public void doHandlerAfterEditProject(ApiExtSouPriceTemplateDto param, String souType, ExtSouPriceTemplatePo po) {
    }


}
