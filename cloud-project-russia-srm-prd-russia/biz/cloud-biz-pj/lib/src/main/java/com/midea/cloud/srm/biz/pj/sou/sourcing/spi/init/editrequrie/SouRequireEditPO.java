package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.init.editrequrie;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItemLadder;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 寻源 - 物料需求 - 保存数据
 *
 * @author zhangwk12@midea.com
 * @since 2022/11/29
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SouRequireEditPO extends BaseObjectX {

    @ApiModelProperty("项目基础信息")
    private SouProject project;
    @ApiModelProperty("物料需求信息")
    private List<SouItem> souItemList;
    @ApiModelProperty("阶梯价模板信息")
    private List<SouItemLadder> ladderList;
    @ApiModelProperty("true-暂存/false-提交")
    protected boolean isTempSave;

}
