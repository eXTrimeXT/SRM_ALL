package com.midea.cloud.srm.model.pj.sou.openapi.inq.vo.init;

import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouCurrency;
import com.midea.cloud.srm.model.pj.sou.openapi.inq.vo.init.ApiInqSouProjectVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouProcessNodeVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 简易询价openAPI - 项目基本信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiInqSouInitProjectInfoVO extends ApiInqSouProjectVO {

    @ApiModelProperty("流程配置信息")
    private SouProcessConfig processConfig;
    @ApiModelProperty("流程节点信息")
    private List<ApiSouProcessNodeVO> processNodeList;
    @ApiModelProperty("可用币种")
    private List<InqSouCurrency> currencyList;
    @ApiModelProperty("内部/外部附件")
    private List<SouFile> souFileList;
    @ApiModelProperty("供应商必须上传附件")
    private List<SouFileConfig> fileConfigList;
    @ApiModelProperty("工作小组")
    private List<SouGroup> groupList;

}
