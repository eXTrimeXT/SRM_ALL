package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.swagger.init;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouInitProjectInfoVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouProcessNodeVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.swagger.init.ApiSouCurrencySwaggerVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.swagger.process.ApiSouProcessConfigSwaggerVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 项目信息 (swagger接口专用)
 * PS: 由于目前的寻源结构（ObjectX + SPI），很难描述不同寻源场景对入参的需求差别，
 *     因此用一个专有的类来装所有的信息。
 * PS: 来源于 {@link ApiSouInitProjectInfoVO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/01/05
 */
@Data
@ApiModel(description = "项目信息")
@EqualsAndHashCode(callSuper = true)
public class ApiSouInitProjectInfoSwaggerVO extends BaseObjectX {

    @ApiModelProperty("流程配置信息")
    private ApiSouProcessConfigSwaggerVO processConfig;
    @ApiModelProperty("流程节点信息")
    private List<ApiSouProcessNodeVO> processNodeList;
    @ApiModelProperty("可用币种")
    private List<ApiSouCurrencySwaggerVO> currencyList;
    @ApiModelProperty("内部/外部附件")
    private List<SouFile> souFileList;
    @ApiModelProperty("供应商必须上传附件")
    private List<SouFileConfig> fileConfigList;
    @ApiModelProperty("工作小组")
    private List<SouGroup> groupList;

}
