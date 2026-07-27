package com.midea.cloud.srm.model.sup.orgcategory.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.supplier.info.entity.OrgCategory;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *  功能名称
 * </pre>
 *
 * @author luxc18
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/10/5 15:14
 *  修改内容:
 * </pre>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "品类关系二开实体类")
@TableName("scc_sup_org_category")
public class PjOrgCategory extends OrgCategory {

    @ApiModelProperty("二开-组织状态")
    @TableField("PJ_ORG_STATUS")
    private String pjOrgStatus;

    @ApiModelProperty("二开-品类状态")
    @TableField("PJ_CATEGORY_STATUS")
    private String pjCategoryStatus;

    @ApiModelProperty("组织id查询条件")
    @TableField(exist = false)
    private List<Long> orgIdList = new ArrayList<>();

}
