package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select;

import com.google.common.collect.Lists;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select.ApiSouTempSelectDataVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * <pre>
 *  模板报价的比价数据模型
 * </pre>
 *
 * @author haibo1.huang@meiCloud.com
 * @version 1.00.00
 *
 * <pre>
 * 修改记录修改后版本:
 * 修改人:
 * 修改日期: 2023/8/11 10:20、
 * 修改内容:
 * </pre>
 */
@Data
@Accessors(chain = true)
@ApiModel("模板报价的比价数据模型")
public class ApiSouTempSelectVO extends BaseObjectX {

    @ApiModelProperty("表格数据")
    private List<ApiSouTempSelectDataVO> dataList;

    @ApiModelProperty("动态列数据")
    private List<DynamicCol> dynamicColList;

    /**
     * 动态列
     */
    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DynamicCol {
        private String prop;
        private String label;
        /**
         * 动态列prop的公共部分
         */
        public static final String PROP_COMMON_PART_FIELD_QUOTE_VALUE = "fieldQuoteValue_";
    }

    /**
     * 根据供应商数据设置动态列信息
     * @param souVendorList 寻源供应商数据
     */
    public void setDynamicCol(List<SouVendor> souVendorList) {
        List<DynamicCol> dynamicColList = Lists.newArrayList();
        for (SouVendor souVendor : souVendorList) {
            dynamicColList.add(new DynamicCol(DynamicCol.PROP_COMMON_PART_FIELD_QUOTE_VALUE + souVendor.getVendorCode(), souVendor.getVendorName()));
        }
        this.dynamicColList = dynamicColList;
    }

}
