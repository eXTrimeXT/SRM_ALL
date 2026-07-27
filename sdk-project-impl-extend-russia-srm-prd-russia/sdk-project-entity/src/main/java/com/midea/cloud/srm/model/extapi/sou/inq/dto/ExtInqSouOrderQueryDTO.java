package com.midea.cloud.srm.model.extapi.sou.inq.dto;

import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtInqSouProject;
import com.midea.cloud.srm.model.sou.openapi.inq.dto.order.ApiInqSouOrderQueryDTO;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
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
public class ExtInqSouOrderQueryDTO extends ApiInqSouOrderQueryDTO {

    /** @see ExtInqSouProject#getCreateUserOrgOuName */
    @ApiModelProperty("公司名称")
    private String createUserOrgOuName;


    /** @see SouProject#getCreatedFullName */
    @ApiModelProperty("采购员名称")
    private String buyerNickName;


    @Override
    public void formatParams() {
        super.formatParams();
        createUserOrgOuName = StringUtils.trimToNull(createUserOrgOuName);
    }
}
