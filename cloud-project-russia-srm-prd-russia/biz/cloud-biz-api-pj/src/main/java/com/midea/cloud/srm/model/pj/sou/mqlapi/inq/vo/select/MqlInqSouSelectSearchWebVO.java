package com.midea.cloud.srm.model.pj.sou.mqlapi.inq.vo.select;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MQL - 评选弹窗 - 筛选条件下拉框值
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/14
 */
@Data
@ApiModel("评选弹窗 - 筛选条件下拉框值")
public class MqlInqSouSelectSearchWebVO {
/**    itemCode itemDesc */
    @ApiModelProperty("物料")
    private Map<String, String> items;
/**    vendorId vendorName */
    @ApiModelProperty("供应商")
    private Map<Long, String> vendors;
    @ApiModelProperty("最新轮次")
    private Integer currentRound;

    public static MqlInqSouSelectSearchWebVO convert(int round, List<SouItem> itemList, List<SouVendor> vendorList) {
        MqlInqSouSelectSearchWebVO vo = new MqlInqSouSelectSearchWebVO();
        vo.items = new HashMap<>(itemList.size());
        vo.vendors = new HashMap<>(vendorList.size());
        vo.currentRound = round;

        itemList.forEach(item -> vo.items.put(item.getItemCode(), item.getItemDesc()));
        vendorList.forEach(vendor -> vo.vendors.put(vendor.getVendorId(), vendor.getVendorName()));

        return vo;
    }

}
