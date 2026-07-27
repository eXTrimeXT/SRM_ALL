package com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.init;

import com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.init.ApiCompSouCurrencyEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.init.ApiCompSouProjectEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouFileConfigEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouFileEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouGroupEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouProjectInfoDTO;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 竞价openAPI - 项目信息保存
 * PS: 参考 {@link ApiSouProjectInfoDTO}，变量名保持一致
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/12
 */
@SuppressWarnings("ALL")
@Data
@ApiModel("竞价立项保存信息")
@EqualsAndHashCode(callSuper = true)
public class ApiCompSouProjectInfoDTO extends BaseObjectX {

    @ApiModelProperty("寻源单基本信息")
    private ApiCompSouProjectEditDTO project;
    @ApiModelProperty("工作小组")
    private List<ApiSouGroupEditDTO> groupList;
    @ApiModelProperty("可用币种")
    private List<ApiCompSouCurrencyEditDTO> currencyList;
    @ApiModelProperty("内部查看附件")
    private List<ApiSouFileEditDTO> innerFileList;
    @ApiModelProperty("外部查看附件")
    private List<ApiSouFileEditDTO> outerFileList;
    @ApiModelProperty("供方必须上传附件")
    private List<ApiSouFileConfigEditDTO> fileConfigList;
    @ApiModelProperty(value = "寻源单据号生成规则", required = true)
    protected String sequenceCode = "SEQ_SOU_COMP_NO";
    @ApiModelProperty("true-暂存/false-提交")
    private boolean isTempSave = true;

}
