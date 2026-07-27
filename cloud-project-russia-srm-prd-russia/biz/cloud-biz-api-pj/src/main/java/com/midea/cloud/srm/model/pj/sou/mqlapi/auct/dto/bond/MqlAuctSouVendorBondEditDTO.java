package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.dto.bond;

import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity.AuctSouVendorBond;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/**
 * 竞价 MQL - 缴纳保证金
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/24
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlAuctSouVendorBondEditDTO extends AuctSouVendorBond {

    private String souType;

    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("true-暂存/false-提交")
    private Boolean tempSave = false;

    /**
     * 入参格式化及校验
     */
    public void formatParams() {
        if (getProjectId() == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (getVendorId() == null) {
            throw new IllegalArgumentException("缺少vendorId参数");
        }

        setPayFileName(StringUtils.trimToNull(getPayFileName()));
        int length = 100;
        if (getPayFileName() == null) {
            throw new IllegalArgumentException("请上传缴纳文件证明");
        } else if (getPayFileName().length() > length) {
            throw new IllegalArgumentException("缴纳文件证明的名称长度不能超过100");
        }
    }

}
