package com.midea.cloud.srm.model.extapi.sou.purinq.dto.init;

import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouCurrency;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiSouFileConfigEditDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiSouFileEditDTO;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiPurInqSouProjectInfoDTO extends BaseObjectX {

    @ApiModelProperty("寻源单")
    protected ApiPurInqSouProjectEditDTO project;
    @ApiModelProperty("可用币种")
    protected List<ExtPurInqSouCurrency> currencyList;
    @ApiModelProperty("外部查看附件")
    protected List<ApiSouFileEditDTO> outerFileList;
    @ApiModelProperty("内部查看附件")
    protected List<ApiSouFileEditDTO> innerFileList;
    @ApiModelProperty("供方必须上传附件")
    protected List<ApiSouFileConfigEditDTO> fileConfigList;
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty(value = "寻源单据号生成规则", required = true)
    protected String sequenceCode = "SEQ_SOU_PURINQ_NO";
    @SuppressWarnings({"AlibabaBooleanPropertyShouldNotStartWithIs", "AlibabaPojoMustUsePrimitiveField", "AlibabaPojoNoDefaultValue"})
    @ApiModelProperty("true-暂存/false-提交")
    protected boolean isTempSave = true;

}
