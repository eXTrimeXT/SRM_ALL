package com.midea.cloud.srm.model.extapi.sou.inq.dto;

import com.midea.cloud.srm.model.common.BasePage;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtInqOrderItemHisQueryDTO extends BasePage {

    /** @see SouProject#getSouNo */
    @ApiModelProperty("询价单号(模糊查询)")
    private String souNo;

    /** @see SouItem#getItemCode */
    @ApiModelProperty("物料编码(模糊查询)")
    private String itemCode;

    /** @see SouItem#getItemDesc */
    @ApiModelProperty("物料名称(模糊查询)")
    private String itemDesc;

    @ApiModelProperty("供应商ID")
    private Long vendorId;

    @ApiModelProperty("供应商名称")
    private String vendorName;

    @ApiModelProperty("品牌")
    private String extBrand;

    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("是否采购商端")
    private Boolean forBuyer = false;

    public void formatParams() {
        souNo = StringUtils.trimToNull(souNo);
        itemCode = StringUtils.trimToNull(itemCode);
        itemDesc = StringUtils.trimToNull(itemDesc);
        vendorName = StringUtils.trimToNull(vendorName);
        extBrand = StringUtils.trimToNull(extBrand);
    }

}
