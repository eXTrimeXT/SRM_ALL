package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.control.itemrefresh;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItemRecord;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendorAuth;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 寻源核心 - 物料变更处理结果存储
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SouItemRefreshPO extends BaseObjectX {

    @ApiModelProperty("新增的物料集合")
    private List<SouItem> saveItemList = new ArrayList<>();
    @ApiModelProperty("更新的物料集合")
    private List<SouItem> updateItemList = new ArrayList<>();
    @ApiModelProperty("供应商报价权限(针对新物料)")
    private List<SouVendorAuth> saveAuthList = new ArrayList<>();
    @ApiModelProperty("供应商报价权限(针对现有有效物料)")
    private List<SouVendorAuth> updateAuthList = new ArrayList<>();
    @ApiModelProperty("供应商报价信息(对物料冗余字段的更新)")
    private List<SouOrderItem> updateOrderItemList = new ArrayList<>();
    @ApiModelProperty("需更新的记录")
    private List<SouItemRecord> updateRecordList = new ArrayList<>();

}
