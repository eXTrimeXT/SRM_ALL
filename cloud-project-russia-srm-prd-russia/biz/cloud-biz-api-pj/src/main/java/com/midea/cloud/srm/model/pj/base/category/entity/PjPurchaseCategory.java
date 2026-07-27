package com.midea.cloud.srm.model.pj.base.category.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.meiql.api.annotation.QlMatchType;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author huangbf3
 */
@Data
@TableName("scc_base_purchase_category")
@ApiModel(description = "品类维护")
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@QlMatchType("PurchaseCategory")
public class PjPurchaseCategory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId("CATEGORY_ID")
    private Long categoryId;

    @ApiModelProperty(value = "分类编码")
    @TableField("CATEGORY_CODE")
    private String categoryCode;

    @ApiModelProperty(value = "分类名称")
    @TableField("CATEGORY_NAME")
    private String categoryName;

    @ApiModelProperty(value = "分类别名")
    @TableField("ALIAS")
    private String alias;

    @ApiModelProperty(value = "分类级别(0根节点,1一级节点,2二级节点 3三级节点...n级节点)")
    @TableField(value="LEVEL",keepGlobalFormat = true)
    private Integer level;

    @ApiModelProperty(value = "父级分类ID(指向本表ID),根节点该值为null")
    @TableField("PARENT_ID")
    private Long parentId;

    @ApiModelProperty(value = "排序(同分类级别值越大,排名越靠前)")
    @TableField("CORDER")
    private Integer corder;

    @ApiModelProperty(value = "分类结构,该值以父分类节点ID+中划线+分类节点ID值组合而成(比如该分类ID:2,其父分类节点ID为1,则该字段值为1_2)")
    @TableField("STRUCT")
    private String struct;

    @ApiModelProperty(value = "生效日期(YYYY-MM-DD)")
    @TableField("START_DATE")
    private Date startDate;

    @ApiModelProperty(value = "失效日期(YYYY-MM-DD)")
    @TableField("END_DATE")
    private Date endDate;

    @ApiModelProperty(value = "是否启用(Y启用 N未启用)")
    @TableField("ENABLED")
    private String enabled;


    @ApiModelProperty(value = "父品类名称")
    @TableField(exist = false)
    private String parentName;

    @ApiModelProperty(value = "父品类编码")
    @TableField(exist = false)
    private String parentCode;

    @ApiModelProperty(value = "全路径品类名称")
    @TableField(exist = false)
    private String categoryFullName;

    @ApiModelProperty(value = "主材（数据字典）")
    @TableField("MAIN_MATERIAL")
    private String mainMaterial;

    @ApiModelProperty(value = "是否需同步ERP(是-Y，否-N)")
    @TableField("CEEA_ENABLE_SYN_ERP")
    private String ceeaEnableSynErp;

    @ApiModelProperty(value = "锁定周期(天数，整数)")
    @TableField(value = "CEEA_LOCK_PERIOD", updateStrategy = FieldStrategy.IGNORED)
    private Integer ceeaLockPeriod;

    @ApiModelProperty(value = "是否用于执行到货计划（Y：是，N：否，默认N）")
    @TableField("CEEA_IF_DELIVER_PLAN")
    private String ceeaIfDeliverPlan;

    @ApiModelProperty(value = "是否允许超计划发货（Y：是，N：否，默认N）")
    @TableField("CEEA_IF_BEYOND_DELIVER")
    private String ceeaIfBeyondDeliver;

    @ApiModelProperty(value = "是否强控品类供应商上限（Y：是，N：否，默认N）")
    @TableField("SUPPLIER_COUNT_LIMIT_FLAG")
    private String supplierCountLimitFlag;

    @ApiModelProperty(value = "品类供应商上限")
    @TableField(value = "SUPPLIER_COUNT_LIMIT", updateStrategy = FieldStrategy.IGNORED)
    private Integer supplierCountLimit;

    @ApiModelProperty(value = "是否启用送货预约（Y：是，N：否，默认N）")
    @TableField("DELIVERY_SUBSCRIBE_FLAG")
    private String deliverySubscribeFlag;

    @ApiModelProperty(value = "是否启用送货单（Y：是，N：否，默认N）")
    @TableField("DELIVERY_ORDER_FLAG")
    private String deliveryOrderFlag;

    @ApiModelProperty(value = "存在的绿牌供应商数量")
    @TableField(exist = false)
    private Integer existCountOfCompany;

    @ApiModelProperty(value = "是否末级(Y：是，N：否)")
    @TableField("LAST_LEVEL_FLAG")
    private Enable lastLevelFlag;

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof PjPurchaseCategory)) {
            return false;
        } else {
            PjPurchaseCategory other = (PjPurchaseCategory) o;
            if (!other.canEqual(this)) {
                return false;
            } else if (!super.equals(o)) {
                return false;
            } else {
                label301:
                {
                    Object thisCategoryId = this.getCategoryId();
                    Object otherCategoryId = other.getCategoryId();
                    if (thisCategoryId == null) {
                        if (otherCategoryId == null) {
                            break label301;
                        }
                    } else if (thisCategoryId.equals(otherCategoryId)) {
                        break label301;
                    }

                    return false;
                }

                label294:
                {
                    Object thisCategoryCode = this.getCategoryCode();
                    Object otherCategoryCode = other.getCategoryCode();
                    if (thisCategoryCode == null) {
                        if (otherCategoryCode == null) {
                            break label294;
                        }
                    } else if (thisCategoryCode.equals(otherCategoryCode)) {
                        break label294;
                    }

                    return false;
                }

                Object thisCategoryName = this.getCategoryName();
                Object otherCategoryName = other.getCategoryName();
                if (thisCategoryName == null) {
                    if (otherCategoryName != null) {
                        return false;
                    }
                } else if (!thisCategoryName.equals(otherCategoryName)) {
                    return false;
                }

                label280:
                {
                    Object thisAlias = this.getAlias();
                    Object otherAlias = other.getAlias();
                    if (thisAlias == null) {
                        if (otherAlias == null) {
                            break label280;
                        }
                    } else if (thisAlias.equals(otherAlias)) {
                        break label280;
                    }

                    return false;
                }

                Object thisLevel = this.getLevel();
                Object otherLevel = other.getLevel();
                if (thisLevel == null) {
                    if (otherLevel != null) {
                        return false;
                    }
                } else if (!thisLevel.equals(otherLevel)) {
                    return false;
                }

                label266:
                {
                    Object thisParentId = this.getParentId();
                    Object otherParentId = other.getParentId();
                    if (thisParentId == null) {
                        if (otherParentId == null) {
                            break label266;
                        }
                    } else if (thisParentId.equals(otherParentId)) {
                        break label266;
                    }

                    return false;
                }

                Object thisCorder = this.getCorder();
                Object otherCorder = other.getCorder();
                if (thisCorder == null) {
                    if (otherCorder != null) {
                        return false;
                    }
                } else if (!thisCorder.equals(otherCorder)) {
                    return false;
                }

                Object thisStruct = this.getStruct();
                Object otherStruct = other.getStruct();
                if (thisStruct == null) {
                    if (otherStruct != null) {
                        return false;
                    }
                } else if (!thisStruct.equals(otherStruct)) {
                    return false;
                }

                Object thisStartDate = this.getStartDate();
                Object otherStartDate = other.getStartDate();
                if (thisStartDate == null) {
                    if (otherStartDate != null) {
                        return false;
                    }
                } else if (!thisStartDate.equals(otherStartDate)) {
                    return false;
                }

                label238:
                {
                    Object thisEndDate = this.getEndDate();
                    Object otherEndDate = other.getEndDate();
                    if (thisEndDate == null) {
                        if (otherEndDate == null) {
                            break label238;
                        }
                    } else if (thisEndDate.equals(otherEndDate)) {
                        break label238;
                    }

                    return false;
                }

                label231:
                {
                    Object thisEnabled = this.getEnabled();
                    Object otherEnabled = other.getEnabled();
                    if (thisEnabled == null) {
                        if (otherEnabled == null) {
                            break label231;
                        }
                    } else if (thisEnabled.equals(otherEnabled)) {
                        break label231;
                    }

                    return false;
                }

                Object thisCreatedId = this.getCreatedId();
                Object otherCreatedId = other.getCreatedId();
                if (thisCreatedId == null) {
                    if (otherCreatedId != null) {
                        return false;
                    }
                } else if (!thisCreatedId.equals(otherCreatedId)) {
                    return false;
                }

                label217:
                {
                    Object thisCreatedBy = this.getCreatedBy();
                    Object otherCreatedBy = other.getCreatedBy();
                    if (thisCreatedBy == null) {
                        if (otherCreatedBy == null) {
                            break label217;
                        }
                    } else if (thisCreatedBy.equals(otherCreatedBy)) {
                        break label217;
                    }

                    return false;
                }

                label210:
                {
                    Object thisCreationDate = this.getCreationDate();
                    Object otherCreationDate = other.getCreationDate();
                    if (thisCreationDate == null) {
                        if (otherCreationDate == null) {
                            break label210;
                        }
                    } else if (thisCreationDate.equals(otherCreationDate)) {
                        break label210;
                    }

                    return false;
                }

                Object thisCreatedByIp = this.getCreatedByIp();
                Object otherCreatedByIp = other.getCreatedByIp();
                if (thisCreatedByIp == null) {
                    if (otherCreatedByIp != null) {
                        return false;
                    }
                } else if (!thisCreatedByIp.equals(thisCreatedByIp)) {
                    return false;
                }

                Object thisLastUpdatedId = this.getLastUpdatedId();
                Object otherLastUpdatedId = other.getLastUpdatedId();
                if (thisLastUpdatedId == null) {
                    if (otherLastUpdatedId != null) {
                        return false;
                    }
                } else if (!thisLastUpdatedId.equals(otherLastUpdatedId)) {
                    return false;
                }

                label189:
                {
                    Object thisLastUpdatedBy = this.getLastUpdatedBy();
                    Object otherLastUpdatedBy = other.getLastUpdatedBy();
                    if (thisLastUpdatedBy == null) {
                        if (otherLastUpdatedBy == null) {
                            break label189;
                        }
                    } else if (thisLastUpdatedBy.equals(otherLastUpdatedBy)) {
                        break label189;
                    }

                    return false;
                }

                label182:
                {
                    Object thisLastUpdateDate = this.getLastUpdateDate();
                    Object otherLastUpdateDate = other.getLastUpdateDate();
                    if (thisLastUpdateDate == null) {
                        if (otherLastUpdateDate == null) {
                            break label182;
                        }
                    } else if (thisLastUpdateDate.equals(otherLastUpdateDate)) {
                        break label182;
                    }

                    return false;
                }

                Object thisLastUpdatedByIp = this.getLastUpdatedByIp();
                Object otherLastUpdatedByIp = other.getLastUpdatedByIp();
                if (thisLastUpdatedByIp == null) {
                    if (otherLastUpdatedByIp != null) {
                        return false;
                    }
                } else if (!thisLastUpdatedByIp.equals(otherLastUpdatedByIp)) {
                    return false;
                }

                label168:
                {
                    Object thisVersion = this.getVersion();
                    Object otherVersion = other.getVersion();
                    if (thisVersion == null) {
                        if (otherVersion == null) {
                            break label168;
                        }
                    } else if (thisVersion.equals(otherVersion)) {
                        break label168;
                    }

                    return false;
                }

                Object thisTenantId = this.getTenantId();
                Object otheTenantId = other.getTenantId();
                if (thisTenantId == null) {
                    if (otheTenantId != null) {
                        return false;
                    }
                } else if (!thisTenantId.equals(otheTenantId)) {
                    return false;
                }

                label154:
                {
                    Object thisParentName = this.getParentName();
                    Object otherParentName = other.getParentName();
                    if (thisParentName == null) {
                        if (otherParentName == null) {
                            break label154;
                        }
                    } else if (thisParentName.equals(otherParentName)) {
                        break label154;
                    }

                    return false;
                }

                Object thisParentCode = this.getParentCode();
                Object otherParentCode = other.getParentCode();
                if (thisParentCode == null) {
                    if (otherParentCode != null) {
                        return false;
                    }
                } else if (!thisParentCode.equals(otherParentCode)) {
                    return false;
                }

                Object thisCategoryFullName = this.getCategoryFullName();
                Object otherCategoryFullName = other.getCategoryFullName();
                if (thisCategoryFullName == null) {
                    if (otherCategoryFullName != null) {
                        return false;
                    }
                } else if (!thisCategoryFullName.equals(otherCategoryFullName)) {
                    return false;
                }

                return true;
            }
        }
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof PjPurchaseCategory;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        Object categoryId = this.getCategoryId();
        result = result * 59 + (categoryId == null ? 43 : categoryId.hashCode());
        Object categoryCode = this.getCategoryCode();
        result = result * 59 + (categoryCode == null ? 43 : categoryCode.hashCode());
        Object categoryName = this.getCategoryName();
        result = result * 59 + (categoryName == null ? 43 : categoryName.hashCode());
        Object alias = this.getAlias();
        result = result * 59 + (alias == null ? 43 : alias.hashCode());
        Object level = this.getLevel();
        result = result * 59 + (level == null ? 43 : level.hashCode());
        Object parentId = this.getParentId();
        result = result * 59 + (parentId == null ? 43 : parentId.hashCode());
        Object corder = this.getCorder();
        result = result * 59 + (corder == null ? 43 : corder.hashCode());
        Object struct = this.getStruct();
        result = result * 59 + (struct == null ? 43 : struct.hashCode());
        Object startDate = this.getStartDate();
        result = result * 59 + (startDate == null ? 43 : startDate.hashCode());
        Object endDate = this.getEndDate();
        result = result * 59 + (endDate == null ? 43 : endDate.hashCode());
        Object enabled = this.getEnabled();
        result = result * 59 + (enabled == null ? 43 : enabled.hashCode());
        Object createdId = this.getCreatedId();
        result = result * 59 + (createdId == null ? 43 : createdId.hashCode());
        Object createdBy = this.getCreatedBy();
        result = result * 59 + (createdBy == null ? 43 : createdBy.hashCode());
        Object creationDate = this.getCreationDate();
        result = result * 59 + (creationDate == null ? 43 : creationDate.hashCode());
        Object createdByIp = this.getCreatedByIp();
        result = result * 59 + (createdByIp == null ? 43 : createdByIp.hashCode());
        Object lastUpdatedId = this.getLastUpdatedId();
        result = result * 59 + (lastUpdatedId == null ? 43 : lastUpdatedId.hashCode());
        Object lastUpdatedBy = this.getLastUpdatedBy();
        result = result * 59 + (lastUpdatedBy == null ? 43 : lastUpdatedBy.hashCode());
        Object lastUpdateDate = this.getLastUpdateDate();
        result = result * 59 + (lastUpdateDate == null ? 43 : lastUpdateDate.hashCode());
        Object lastUpdatedByIp = this.getLastUpdatedByIp();
        result = result * 59 + (lastUpdatedByIp == null ? 43 : lastUpdatedByIp.hashCode());
        Object version = this.getVersion();
        result = result * 59 + (version == null ? 43 : version.hashCode());
        Object tenantId = this.getTenantId();
        result = result * 59 + (tenantId == null ? 43 : tenantId.hashCode());
        Object parentName = this.getParentName();
        result = result * 59 + (parentName == null ? 43 : parentName.hashCode());
        Object parentCode = this.getParentCode();
        result = result * 59 + (parentCode == null ? 43 : parentCode.hashCode());
        Object categoryFullName = this.getCategoryFullName();
        result = result * 59 + (categoryFullName == null ? 43 : categoryFullName.hashCode());
        return result;
    }
}
