package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.control.vendoradd;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendorAuth;
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
public class SouVendorAddPO extends BaseObjectX {

    @ApiModelProperty("更新的供应商记录")
    private List<SouVendorRecord> updateRecordList;
    @ApiModelProperty("新增的供应商")
    private List<SouVendor> saveVendorList;
    @ApiModelProperty("新增的报价权限")
    private List<SouVendorAuth> saveAuthList;

}
