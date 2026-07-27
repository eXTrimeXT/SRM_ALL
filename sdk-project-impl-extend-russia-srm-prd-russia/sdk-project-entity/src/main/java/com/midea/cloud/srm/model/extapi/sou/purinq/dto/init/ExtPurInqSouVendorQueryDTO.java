package com.midea.cloud.srm.model.extapi.sou.purinq.dto.init;

import com.midea.cloud.srm.model.common.BasePage;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouVendorDel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtPurInqSouVendorQueryDTO extends BasePage {

    /** @see ExtPurInqSouVendorDel#getProjectId */
    @ApiModelProperty("询价单ID(必填)")
    private Long projectId;

    /** @see ExtPurInqSouVendorDel#getVendorCode */
    @ApiModelProperty("供应商编码(模糊查询)")
    private String vendorCode;

    /** @see ExtPurInqSouVendorDel#getVendorName */
    @ApiModelProperty("供应商名称(模糊查询)")
    private String vendorName;

    /**
     * 入参格式化
     */
    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        vendorCode = StringUtils.trimToNull(vendorCode);
        vendorName = StringUtils.trimToNull(vendorName);
    }

}
