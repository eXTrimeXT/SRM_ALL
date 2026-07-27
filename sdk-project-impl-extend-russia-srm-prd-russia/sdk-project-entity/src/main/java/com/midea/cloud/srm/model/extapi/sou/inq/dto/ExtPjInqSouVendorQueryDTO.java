package com.midea.cloud.srm.model.extapi.sou.inq.dto;

import com.midea.cloud.srm.model.common.BasePage;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPJInqSouVendorDel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/**
 * 长城 - 询比价 - 邀请供应商删除信息列表查询条件
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtPjInqSouVendorQueryDTO extends BasePage {

    /** @see ExtPJInqSouVendorDel#getProjectId */
    @ApiModelProperty("询价单ID(必填)")
    private Long projectId;

    /** @see ExtPJInqSouVendorDel#getVendorCode */
    @ApiModelProperty("供应商编码(模糊查询)")
    private String vendorCode;

    /** @see ExtPJInqSouVendorDel#getVendorName */
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
