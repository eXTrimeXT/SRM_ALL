package com.midea.cloud.srm.model.pj.changchengapi.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author huangbf3
 * 长城品类接口数据对象
 */
@Data
public class CategoryDTO {

    @ApiModelProperty("更新人所属部门名称")
    private String updateUserDept;

    @ApiModelProperty("创建人所属部门名称")
    private String createUserDept;

    @ApiModelProperty("描述")
    private String describe;

    @ApiModelProperty("品类状态")
    private String categoryState;

    @ApiModelProperty("父级品类编码")
    private String parentCategoryCode;

    @ApiModelProperty("品类级别")
    private Integer categoryLevel;

    @ApiModelProperty("品类名称")
    private String categoryName;

    @ApiModelProperty("品类编码")
    private String categoryCode;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("更新时间")
    private String updateTime;

    @ApiModelProperty("顺序号")
    private Integer sortNo;

    @ApiModelProperty("创建人工号")
    private String createUserCode;

    @ApiModelProperty("创建人姓名")
    private String createUserName;

    @ApiModelProperty("更新人工号")
    private String updateUserCode;

    @ApiModelProperty("更新人姓名")
    private String updateUserName;

    @ApiModelProperty("删除标识")
    private Integer deleteFlag;

    @ApiModelProperty("启用/禁用标识")
    private Integer activeFlag;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("启用/禁用标识")
    private Integer version;

    @ApiModelProperty("第几页")
    private Integer page;

    @ApiModelProperty("页数")
    private Integer size;
}
