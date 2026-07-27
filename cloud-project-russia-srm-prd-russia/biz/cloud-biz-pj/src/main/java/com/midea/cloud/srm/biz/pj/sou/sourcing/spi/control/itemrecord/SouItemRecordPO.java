package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.control.itemrecord;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItemRecord;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 寻源核心 - 物料变更记录存储
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SouItemRecordPO extends BaseObjectX {

    @ApiModelProperty("新增变更记录")
    private List<SouItemRecord> saveRecordList;
    @ApiModelProperty("现有变更记录")
    private List<SouItemRecord> updateRecordList;

}
