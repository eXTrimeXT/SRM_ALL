package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.init.editproject;

import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 寻源 - 项目信息 - 保存数据
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/20
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SouProjectEditPO extends BaseObjectX {

    @ApiModelProperty("项目基本信息")
    private SouProject souProject;
    @ApiModelProperty("保证金信息")
    private CompSouProject compSouProject;
    @ApiModelProperty("工作小组")
    private List<SouGroup> groupList;
    @ApiModelProperty("可用币种")
    private List<SouCurrency> currencyList;
    @ApiModelProperty("附件")
    private List<SouFile> souFileList;
    @ApiModelProperty("供方必须上传附件")
    private List<SouFileConfig> fileConfigList;

}
