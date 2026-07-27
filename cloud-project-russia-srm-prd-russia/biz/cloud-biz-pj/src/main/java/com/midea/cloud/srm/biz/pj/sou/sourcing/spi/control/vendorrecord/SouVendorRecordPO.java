package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.control.vendorrecord;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendorRecord;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 寻源核心 - 追加供应商实体
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/24
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SouVendorRecordPO extends BaseObjectX {

    @ApiModelProperty("新增的供应商记录")
    private List<SouVendorRecord> saveRecordList;
    @ApiModelProperty("更新的供应商记录")
    private List<SouVendorRecord> updateRecordList;

}
