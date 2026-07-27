package com.midea.cloud.srm.model.extapi.sou.purinq.vo.init;

import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouCurrency;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.init.ApiSouProcessNodeVO;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
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
public class ApiPurInqSouInitProjectInfoVO extends ApiPurInqSouProjectVO {

    @ApiModelProperty("流程配置信息")
    private SouProcessConfig processConfig;
    @ApiModelProperty("流程节点信息")
    private List<ApiSouProcessNodeVO> processNodeList;
    @ApiModelProperty("可用币种")
    private List<ExtPurInqSouCurrency> currencyList;
    @ApiModelProperty("内部/外部附件")
    private List<SouFile> souFileList;
    @ApiModelProperty("供应商必须上传附件")
    private List<SouFileConfig> fileConfigList;
    @ApiModelProperty("工作小组")
    private List<SouGroup> groupList;

}
