package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel(description = "项目信息保存")
@EqualsAndHashCode(callSuper = true)
public class ApiExtSouProjectInfoDTO extends BaseObjectX {

    /**
     * 项目信息
     */
    private ExtSouProjectDto project;

    /**
     * 评分小组
     */
    private List<ExtSouGroup> groupList;

    /**
     * 申请资料
     */
    private List<ExtSouFile> applyFileList;

    /**
     * 招标文件
     */
    private List<ExtSouFile> bidFileList;

    /**
     * 招标计划
     */
    private List<ExtSouPlan> planList;



    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("true-暂存/false-提交")
    protected Boolean tempSave = true;
}
