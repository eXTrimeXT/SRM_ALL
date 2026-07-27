package com.midea.cloud.srm.model.extapi.sou.inq.dto;

import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtInqSouItem;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtInqSouProject;
import com.midea.cloud.srm.model.sou.openapi.inq.dto.init.ApiInqSouProjectQueryDTO;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouItem;
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
public class ExtInqSouProjectQueryDTO extends ApiInqSouProjectQueryDTO {

    /** @see SouItem#getItemCode */
    @ApiModelProperty("物料编码")
    private String itemCode;

    /** @see SouItem#getItemDesc */
    @ApiModelProperty("物料名称")
    private String itemDesc;

    /** @see ExtInqSouItem#getExtMaterialModel */
    @ApiModelProperty("物料规格")
    private String extMaterialModel;

    /** @see ExtInqSouProject#getCreateUserOrgOuName */
    @ApiModelProperty("创建人所在公司")
    private String createUserOrgOuName;

    @Override
    public void formatParams() {
        super.formatParams();
        itemCode = StringUtils.trimToNull(itemCode);
        itemDesc = StringUtils.trimToNull(itemDesc);
        extMaterialModel = StringUtils.trimToNull(extMaterialModel);
        createUserOrgOuName = StringUtils.trimToNull(createUserOrgOuName);
    }

}
