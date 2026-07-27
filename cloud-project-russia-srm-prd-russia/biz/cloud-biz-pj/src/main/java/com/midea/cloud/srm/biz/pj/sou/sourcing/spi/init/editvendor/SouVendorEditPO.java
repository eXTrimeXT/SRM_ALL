package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.init.editvendor;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendorAuth;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 寻源 - 项目需求 - 保存数据
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/20
 */
@Data
@NoArgsConstructor
public class SouVendorEditPO {

    /** 供应商信息 */
    private List<SouVendor> vendorList;
    /** 报价权限 */
    private List<SouVendorAuth> authList;

}