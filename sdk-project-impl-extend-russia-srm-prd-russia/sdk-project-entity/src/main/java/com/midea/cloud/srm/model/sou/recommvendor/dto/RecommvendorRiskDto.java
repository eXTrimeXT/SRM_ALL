package com.midea.cloud.srm.model.sou.recommvendor.dto;

import com.midea.cloud.srm.model.sou.recommvendor.enums.RiskItemType;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel("供应商风险")
public class RecommvendorRiskDto extends BaseObjectX {

    private static final String[] RISK_ITEM = {RiskItemType.LINKMAN_NAME.getType(), RiskItemType.LINKMAN_TEL.getType(), RiskItemType.LINKMAN_MAIL.getType(), RiskItemType.LEGAL.getType(),
            RiskItemType.BLACKLIST.getType(), RiskItemType.HOLDER.getType(), RiskItemType.MAIN_PERSON.getType(), RiskItemType.RELATIONS_VENDOR.getType()};

    @ApiModelProperty("供应商风险")
    private List<RecommvendorDto> vendorRiskList;

    /**
     * 风险项
     */
    List<RiskItem> riskItemList;

    @ApiModelProperty("供应商风险数")
    private Integer riskCount;

    /**
     * 统计异常数量
     * @param add
     */
    public void count(Integer add) {
        //累加异常统计数量
        this.riskCount = ObjectUtils.defaultIfNull(this.riskCount, 0) + ObjectUtils.defaultIfNull(add, 0);
    }

    /**
     * 添加风险项
     * @param riskIndex
     * @param description
     * @return
     */
    public RecommvendorRiskDto addRiskItem(Integer riskIndex, String description) {
        if(Objects.isNull(riskItemList)) {
            riskItemList = new ArrayList<>();
        }
        //添加风险项
        riskItemList.add(new RiskItem(RISK_ITEM[riskIndex], description));
        return this;
    }

    /**
     * 添加风险项
     * @param riskItemType
     * @param description
     * @return
     */
    public RecommvendorRiskDto addRiskItem(RiskItemType riskItemType, String description) {
        if(Objects.isNull(riskItemList)) {
            riskItemList = new ArrayList<>();
        }
        //添加风险项
        riskItemList.add(new RiskItem(riskItemType.getType(), description));
        return this;
    }

    /**
     * 扩展风险项
     * @param riskIndex
     * @param description
     * @return
     */
    public RecommvendorRiskDto appendRiskItem(Integer riskIndex, String description) {
        if(Objects.isNull(riskItemList)) {
            riskItemList = new ArrayList<>();
        }
        //从已有的风险项中过滤指定风险项，并将风险描述进行追加，用分号分隔，返回过滤是否匹配结果，布尔值，false时说明未匹配到，需要增加风险项
        Boolean appendFlag = riskItemList.stream().filter(r -> RISK_ITEM[riskIndex].equals(r.getType())).peek(r -> {
            //判断是否存在风险描述
            if(StringUtils.isBlank(r.getDescription())) {
                //不存在风险描述时，直接写入
                r.setDescription(description);
            } else {
                //存在风险描述时，用分号分隔并追加到末尾
                r.setDescription(StringUtils.joinWith(";", r.getDescription(), description));
            }
        }).findAny().isPresent();
        //判断是否匹配上现有的风险项
        if(!appendFlag) {
            //现有风险项中未匹配上当前风险项时，添加到风险项中去
            return addRiskItem(riskIndex, description);
        }
        return this;
    }

    /**
     * 构建一个空描述的风险项信息
     * @param riskIndex
     * @return
     */
    public RiskItem buildRiskItem(Integer riskIndex) {
        return new RiskItem(RISK_ITEM[riskIndex], null);
    }


    /**
     * @Description: 内部类，供应商风险项信息
     *
     * @author srm
     * @date 2024-06-07
     */
    @Data
    public class RiskItem {
        /**
         * 风险项类型
         */
        private String type;
        /**
         * 风险描述
         */
        private String description;

        /**
         * 内部类构造方法
         * @param type
         * @param description
         */
        RiskItem(String type, String description) {
            this.type = type;
            this.description = description;
        }

    }
}
