package com.midea.cloud.srm.model.pj.sou.openapi.bid.dto.init;

import com.midea.cloud.srm.model.pj.sou.openapi.bid.dto.init.ApiBidSouCurrencyEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.bid.dto.init.ApiBidSouProjectEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouProjectInfoDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFileConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouGroup;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 招投标openAPI - 项目信息保存
 * PS: 参考 {@link ApiSouProjectInfoDTO}，变量名保持一致
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/21
 */
@SuppressWarnings("ALL")
@Data
@ApiModel("招投标立项保存信息")
@EqualsAndHashCode(callSuper = true)
public class ApiBidSouProjectInfoDTO extends BaseObjectX {

    @ApiModelProperty("寻源单基本信息")
    private ApiBidSouProjectEditDTO project;
    @ApiModelProperty("工作小组")
    private List<SouGroup> groupList;
    @ApiModelProperty("供方必须上传附件")
    private List<SouFileConfig> fileConfigList;
    @ApiModelProperty("内部查看附件")
    private List<SouFile> innerFileList = new ArrayList<>();
    @ApiModelProperty("外部查看附件")
    private List<SouFile> outerFileList = new ArrayList<>();
    @ApiModelProperty("可用币种")
    private List<ApiBidSouCurrencyEditDTO> currencyList = new ArrayList<>();
    @ApiModelProperty(value = "寻源单据号生成规则", required = true)
    protected String sequenceCode = "SEQ_SOU_BID_NO";
    @ApiModelProperty("true-暂存/false-提交")
    private boolean isTempSave = true;

}
