package com.midea.cloud.srm.model.pj.sou.brg.dto.webapi.bond;

import com.midea.cloud.srm.model.pj.sou.brg.entity.BrgSouVendorBond;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * 项目式询价 - 供应商缴纳保证金
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/27
 */
@Data
public class BrgSouVendorBondEditWebDTO {

    /** @see BrgSouVendorBond#getProjectId */
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    /** @see BrgSouVendorBond#getPayDate */
    @ApiModelProperty("缴纳时间")
    private Date payDate;

    /** @see BrgSouVendorBond#getPayDocId */
    @ApiModelProperty("缴纳文件ID")
    private Long payDocId;

    /** @see BrgSouVendorBond#getPayFileName */
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
        int num = 100;
        if (payFileName == null) {
            throw new IllegalArgumentException("请上传缴纳文件证明");
        } else if (payFileName.length() > num) {
            throw new IllegalArgumentException("缴纳文件证明的名称长度不能超过100");
        }
    }

}
