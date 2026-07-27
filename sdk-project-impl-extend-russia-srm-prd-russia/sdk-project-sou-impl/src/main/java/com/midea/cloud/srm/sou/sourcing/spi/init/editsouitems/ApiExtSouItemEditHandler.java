package com.midea.cloud.srm.sou.sourcing.spi.init.editsouitems;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouItemDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouPriceTemplateDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouPriceTemplate;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouSourceFromTypeEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouItemService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouPriceTemplateService;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.sou.sourcing.spi.init.editpricetemplates.ExtSouPriceTemplatePo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class ApiExtSouItemEditHandler implements ISouSpiBean {

    @Autowired
    private IExtSouItemService itemService;

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public ExtSouItemEditPO formatValidateAndConvert(ApiExtSouItemDto param, String souType) {
        // 1: 数据格式化及校验
        this.formatAndValidate(param, souType);
        // 2: 数据转换
        return this.convert(param, souType);
    }

    public ExtSouItemEditPO formatValidateAndConvertForImport(ApiExtSouItemDto param, String souType) {
        // 1: 数据格式化及校验
        this.formatAndValidateForImport(param, souType);
        // 2: 数据转换
        return this.convert(param, souType);
    }

    /**
     * 入参格式化及校验
     * @param param 参数
     * @param souType 参数
     */
    protected void formatAndValidateForImport(ApiExtSouItemDto param, String souType) {
        param.setItemList(new ArrayList<>());

        param.getImportList().stream().forEach(data -> {
            ExtSouItem item = JSON.parseObject(JSON.toJSONString(data), ExtSouItem.class);
            param.getItemList().add(item);

            //校验

        });
    }

    protected void errorChek(Map<String, Object> data, String errorMsg, ApiExtSouItemDto param) {
        param.getImportCheck().set(false);
        if (data.containsKey(ApiExtSouItemDto.ERROR_CHECK)) {
            data.put(ApiExtSouItemDto.ERROR_CHECK, StringUtils.joinWith(";", MapUtils.getString(data, ApiExtSouItemDto.ERROR_CHECK), errorMsg));
        } else {
            data.put(ApiExtSouItemDto.ERROR_CHECK, errorMsg);
        }
    }

    /**
     * 入参格式化及校验
     * @param param 参数
     * @param souType 参数
     */
    protected void formatAndValidate(ApiExtSouItemDto param, String souType) {

    }

    /**
     * 数据转换
     * @param param 参数
     * @param souType 参数
     * @return 返回
     */
    protected ExtSouItemEditPO convert(ApiExtSouItemDto param, String souType) {
        ExtSouItemEditPO po = new ExtSouItemEditPO();
        po.setItemList(this.doConvertProject(param, souType));
        return po;
    }

    /**
     * 转换得到寻源信息
     * @param param 参数
     * @param souType 参数
     * @return 返回
     */
    protected List<ExtSouItem> doConvertProject(ApiExtSouItemDto param, String souType) {
        List<ExtSouItem> itemList = param.getItemList();
        if(Objects.isNull(itemList)) {
            itemList = new ArrayList<>();
        }

        AtomicInteger index = new AtomicInteger(1);
        //查询最大的排序
        LambdaQueryWrapper<ExtSouItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouItem::getProjectId, param.getProjectId());
        queryWrapper.orderByDesc(ExtSouItem::getSortIndex);
        PageUtil.startPage(1, 1);
        List<ExtSouItem> souItemList = itemService.list(queryWrapper);
        if(CollectionUtils.isNotEmpty(souItemList)) {
            index.set(souItemList.get(0).getSortIndex());
        }

        itemList.stream().forEach(item -> {
            item.setProjectId(param.getProjectId());
            //无物料号默认是
            if(Objects.isNull(item.getNoCodeItem())) {
                item.setNoCodeItem(Enable.Y);
            }

            if(Objects.isNull(item.getSourceFromType())) {
                item.setSourceFromType(SouSourceFromTypeEnum.SOU_REQ.name());
            }

            //是否阶梯价默认N
            if(Objects.isNull(item.getIsLadder())) {
                item.setIsLadder(Enable.N);
            }

            item.setSortIndex(index.getAndAdd(1));

            if(Objects.isNull(item.getSouItemId())) {
                item.setSouItemId(IdGenrator.generate());
            }
        });
        return itemList;
    }

    @ApiOperation("报价信息保存前的额外处理")
    public void doHandlerBeforeEditProject(ApiExtSouItemDto param, String souType) {
        if(!param.isTempSave()) {
            AssertUtils.isTrue(CollectionUtils.isNotEmpty(param.getItemList()), "报价信息不能为空！");
        }
    }

    @ApiOperation("报价信息保存后的额外处理")
    public void doHandlerAfterEditProject(ApiExtSouItemDto param, String souType, ExtSouItemEditPO po) {
    }


}
