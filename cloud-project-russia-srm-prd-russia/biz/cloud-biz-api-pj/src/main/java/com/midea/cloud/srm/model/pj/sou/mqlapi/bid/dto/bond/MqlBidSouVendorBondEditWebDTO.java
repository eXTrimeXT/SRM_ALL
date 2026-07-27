package com.midea.cloud.srm.model.pj.sou.mqlapi.bid.dto.bond;

import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouVendorBond;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * 招投标MQL - 供应商缴纳保证金
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/30
 */
@Data
public class MqlBidSouVendorBondEditWebDTO {

    /** @see BidSouVendorBond#getProjectId */
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    /** @see BidSouVendorBond#getPayDate */
    @ApiModelProperty("缴纳时间")
    private Date payDate;

    /** @see BidSouVendorBond#getPayDocId */
    @ApiModelProperty("缴纳文件ID")
    private Long payDocId;

    /** @see BidSouVendorBond#getPayFileName */
    @ApiModelProperty("缴纳文件名称")
    private String payFileName;

    /**
     * 入参格式化及校验
     */
    public void formatAndValidate() {
        if (payDocId == null) {
            throw new IllegalArgumentException("请上传缴纳文件证明");
        }
        if (payDate == null) {
            throw new IllegalArgumentException("请输入缴纳时间");
        } else if (payDate.after(new Date())) {
            throw new IllegalArgumentException("缴纳时间不能晚于当前时间");
        }
        payFileName = StringUtils.trimToNull(payFileName);
        int length = 100;
        if (payFileName == null) {
            throw new IllegalArgumentException("请上传缴纳文件证明");
        } else if (payFileName.length() > length) {
            throw new IllegalArgumentException("缴纳文件证明的名称长度不能超过100");
        }
    }

}
