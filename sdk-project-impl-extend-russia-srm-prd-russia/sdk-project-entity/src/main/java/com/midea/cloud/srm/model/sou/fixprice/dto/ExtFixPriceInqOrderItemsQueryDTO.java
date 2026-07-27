package com.midea.cloud.srm.model.sou.fixprice.dto;

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
public class ExtFixPriceInqOrderItemsQueryDTO extends BasePage {

    /** @see SouProject#getSouNo */
    @ApiModelProperty("询价单号")
    private String souNo;

    /** @see SouItem#getItemDesc */
    @ApiModelProperty("物料编码")
    private String itemCode;

    /** @see SouProject#getCreatedBy */
    @ApiModelProperty("采购员")
    private String buyerUsername;

    @ApiModelProperty("采购部门ID")
    private Long orgOuId;

    @ApiModelProperty("创建人所在公司ID")
    private Long createUserOrgOuId;

    public void formatParams() {
        souNo = StringUtils.trimToNull(souNo);
        itemCode = StringUtils.trimToNull(itemCode);
        buyerUsername = StringUtils.trimToNull(buyerUsername);
    }

}
