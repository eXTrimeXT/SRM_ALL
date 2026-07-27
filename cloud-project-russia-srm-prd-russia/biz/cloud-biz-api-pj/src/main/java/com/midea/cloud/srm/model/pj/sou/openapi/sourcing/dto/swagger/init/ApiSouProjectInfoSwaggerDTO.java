package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.swagger.init;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.*;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.swagger.init.ApiSouCurrencyEditSwaggerDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.swagger.init.ApiSouProjectEditSwaggerDTO;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 项目信息保存 (swagger接口专用)
 * PS: 由于目前的寻源结构（ObjectX + SPI），很难描述不同寻源场景对入参的需求差别，
 *     因此用一个专有的类来装所有的信息。
 * PS: 来源于 {@link ApiSouProjectInfoDTO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/01/05
 */
@SuppressWarnings("ALL")
@Data
@ApiModel(description = "项目信息保存")
@EqualsAndHashCode(callSuper = true)
public class ApiSouProjectInfoSwaggerDTO extends BaseObjectX {

    @ApiModelProperty(value = "寻源单", required = true)
    protected ApiSouProjectEditSwaggerDTO project;
    @ApiModelProperty("工作小组")
    protected List<ApiSouGroupEditDTO> groupList;
    @ApiModelProperty("可用币种")
    protected List<ApiSouCurrencyEditSwaggerDTO> currencyList;
    @ApiModelProperty("外部查看附件")
    protected List<ApiSouFileEditDTO> outerFileList;
    @ApiModelProperty("内部查看附件")
    protected List<ApiSouFileEditDTO> innerFileList;
    @ApiModelProperty("供方必须上传附件")
    protected List<ApiSouFileConfigEditDTO> fileConfigList;
    @ApiModelProperty(value = "寻源单据号生成规则", required = true)
    protected String sequenceCode;
    @ApiModelProperty("true-暂存/false-提交")
    protected boolean isTempSave = true;

}
