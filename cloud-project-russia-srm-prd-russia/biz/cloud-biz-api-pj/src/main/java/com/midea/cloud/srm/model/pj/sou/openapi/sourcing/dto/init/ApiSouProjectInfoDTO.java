package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.*;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouFileEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouGroupEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouProjectEditDTO;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 寻源openAPI - 项目信息保存
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/14
 */
@SuppressWarnings("ALL")
@Data
@ApiModel(description = "项目信息保存")
@EqualsAndHashCode(callSuper = true)
public class ApiSouProjectInfoDTO extends BaseObjectX {

    @ApiModelProperty(value = "寻源单", required = true)
    protected ApiSouProjectEditDTO project;
    @ApiModelProperty("工作小组")
    protected List<ApiSouGroupEditDTO> groupList;
    @ApiModelProperty("可用币种")
    protected List<ApiSouCurrencyEditDTO> currencyList;
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

    public void formatParams() {
        if (project == null) {
            throw new IllegalArgumentException("缺少project数据");
        }
        sequenceCode = StringUtils.trimToNull(sequenceCode);
        if (sequenceCode == null) {
            throw new IllegalArgumentException("缺少sequenceCode参数");
        }
    }

}
