package com.midea.cloud.srm.sou.sourcing.spi.init.editproject;

import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 * 备注
 * @author huangbf3
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ExtSouProjectEditPO extends BaseObjectX {

    @ApiModelProperty("项目基本信息")
    private ExtSouProject project;

    @ApiModelProperty("招标工作小组")
    private List<ExtSouGroup> groupList;

    @ApiModelProperty("招标附件（申请附件和招标附件）")
    private List<ExtSouFile> souFileList;

    @ApiModelProperty("招标计划")
    private List<ExtSouPlan> planList;

    private List<ExtSouDemand> souDemands;

    public List<ExtSouGroup> getGroupList() {
        if(Objects.isNull(groupList)) {
            groupList = new ArrayList<>();
        }
        return groupList;
    }

    public List<ExtSouFile> getSouFileList() {
        if(Objects.isNull(souFileList)) {
            souFileList = new ArrayList<>();
        }
        return souFileList;
    }

    public List<ExtSouDemand> getSouDemands() {
        if(Objects.isNull(souDemands)) {
            souDemands = new ArrayList<>();
        }
        return souDemands;
    }

    public ExtSouProject getProject() {
        if(Objects.isNull(planList)) {
            planList = new ArrayList<>();
        }
        return project;
    }
}
