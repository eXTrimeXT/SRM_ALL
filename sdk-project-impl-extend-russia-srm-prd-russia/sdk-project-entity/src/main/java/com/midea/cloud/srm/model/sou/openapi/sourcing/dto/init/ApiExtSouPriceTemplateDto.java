package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.alibaba.fastjson.JSON;
import com.midea.cloud.srm.model.sou.enums.ColumnSourceEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouPriceTemplate;
import com.mideacloud.common.objectx.BaseObjectX;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Data
public class ApiExtSouPriceTemplateDto extends BaseObjectX {

    /**
     * 备选字段
     */
    private List<ExtSouPriceTemplate> alternativeList;

    /**
     * 已选字段（报价模板字段）
     */
    private List<ExtSouPriceTemplate> selectedList;

    /**
     * 关联招标基本信息主键ID, -1为所有字段
     */
    private Long projectId;

    /**
     * 获取已选字段，且属于采购商维护的字段
     */
    public List<ExtSouPriceTemplate> getBuyerAsSelectedList() {
        List<ExtSouPriceTemplate> buyerList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(selectedList)) {
            for(ExtSouPriceTemplate template : selectedList) {
                if(ColumnSourceEnum.BUYER.getCode().equals(template.getColumnSource())) {
                    ExtSouPriceTemplate buyerTemplate = JSON.parseObject(JSON.toJSONString(template), ExtSouPriceTemplate.class);
                    buyerList.add(buyerTemplate);
                }

            }
        }
        return buyerList;
    }


}
