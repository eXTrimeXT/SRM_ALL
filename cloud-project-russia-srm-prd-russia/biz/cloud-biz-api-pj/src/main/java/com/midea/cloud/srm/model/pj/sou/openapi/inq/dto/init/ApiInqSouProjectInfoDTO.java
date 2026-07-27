package com.midea.cloud.srm.model.pj.sou.openapi.inq.dto.init;

import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouCurrency;
import com.midea.cloud.srm.model.pj.sou.openapi.inq.dto.init.ApiInqSouProjectEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouCurrencyEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouFileConfigEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouFileEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouGroupEditDTO;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 简易询价openAPI - 立项基本信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/05
 */
@SuppressWarnings("ALL")
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiInqSouProjectInfoDTO extends BaseObjectX {

    @ApiModelProperty(value = "寻源单", required = true)
    protected ApiInqSouProjectEditDTO project;
    @ApiModelProperty("可用币种")
    protected List<InqSouCurrency> currencyList;
    @ApiModelProperty("外部查看附件")
    protected List<ApiSouFileEditDTO> outerFileList;
    @ApiModelProperty("内部查看附件")
    protected List<ApiSouFileEditDTO> innerFileList;
    @ApiModelProperty("供方必须上传附件")
    protected List<ApiSouFileConfigEditDTO> fileConfigList;
    @ApiModelProperty(value = "寻源单据号生成规则", required = true)
    protected String sequenceCode = "SEQ_SOU_INQ_NO";
    @ApiModelProperty("true-暂存/false-提交")
    protected boolean isTempSave = true;

}
