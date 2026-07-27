package com.midea.cloud.srm.model.pj.sapcostcenter;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_base_sap_cost_center")
@ApiModel(description = "SAP成本中心科目信息")
public class CostCenterInfo extends BaseEntity {

    @ApiModelProperty(value = "成本中心科目信息id")
    @TableField("ID")
    private Long id;

    @ApiModelProperty(value = "所属管理单元编码（公司编码）")
    @TableField("ORGID")
    private String orgid;

    @ApiModelProperty(value = "经营体编码")
    @TableField("JYORGNUMBER")
    private String jyorgnumber;

    @ApiModelProperty(value = "经营体名称")
    @TableField("JYORGNAME")
    private String jyorgname;

    @ApiModelProperty(value = "成本中心easid")
    @TableField("COSTID")
    private String costid;

    @ApiModelProperty(value = "成本中心编码")
    @TableField("COSTNUMBER")
    private String costnumber;

    @ApiModelProperty(value = "成本中心名称")
    @TableField("COSTNAME")
    private String costname;

    @ApiModelProperty(value = "成本中心替代组")
    @TableField("FALTGROUP")
    private String faltgroup;

    @ApiModelProperty(value = "0-启用，1-禁用")
    @TableField("STATUS")
    private String status;

    @ApiModelProperty(value = "描述")
    @TableField("DESCRIPTION")
    private String description;

    @ApiModelProperty(value = "创建人编码")
    @TableField("CREATOR")
    private String creator;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATETIME")
    private String createtime;

    @ApiModelProperty(value = "修改人编码")
    @TableField("UPDATOR")
    private String updator;

    @ApiModelProperty(value = "修改时间")
    @TableField("UPDATETIME")
    private String updatetime;
}
