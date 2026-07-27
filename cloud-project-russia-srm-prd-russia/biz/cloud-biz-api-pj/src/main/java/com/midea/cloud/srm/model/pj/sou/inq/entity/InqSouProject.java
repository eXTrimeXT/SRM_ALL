package com.midea.cloud.srm.model.pj.sou.inq.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtInqSouProject;
import com.midea.cloud.srm.model.pj.sou.inq.enums.InqSouProjectStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <pre>
 *  询价-询价信息头表 模型
 * </pre>
 *
 * @author zhongbh
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人: zhangwk12@meicloud.com, www.gdzwk.com(重构)
 *  修改日期: 2020-03-14 17:32:14
 *  修改内容:
 * </pre>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_sou_inq_project")
@ApiModel(description = "简易询价头信息")
public class InqSouProject extends ExtInqSouProject {

    private static final long serialVersionUID = 1L;

    /** @see SouProject#getProjectId */
    @TableId("PROJECT_ID")
    @ApiModelProperty("ID(同寻源单ID)")
    private Long projectId;

    @TableField("EXT_PROJECT_STATUS")
    @ApiModelProperty("寻源状态")
    private InqSouProjectStatusEnum extProjectStatus;

    @TableField("INQUIRY_TYPE")
    @ApiModelProperty("询价类型[字典值: SOU_INQUIRY_TYPE]")
    private String inquiryType;

    @TableField("EXCHANGE_RATE_TYPE")
    @ApiModelProperty("汇率类型[字典值: EXCHANGE_RATE_TYPE]")
    private String exchangeRateType;

    @TableField("CURRENCY_EXCHANGE_DATE")
    @ApiModelProperty("币种转换日期")
    private Date currencyExchangeDate;

    @TableField("IS_TARGET_PRICE_OK")
    @ApiModelProperty("是否已设定目标价(Y/N)")
    private Enable isTargetPriceOk;

    @TableField("EXCLUDE_BLACK_VENDORS")
    @ApiModelProperty("推荐控制 -- 是否排除黑名单供应商(Y/N)")
    private Enable excludeBlackVendors;

    @TableField("EXCLUDE_NO_CURRENT_ORG_VENDORS")
    @ApiModelProperty("推荐控制 -- 是否排除非本业务实体供应商(Y/N)")
    private Enable excludeNoCurrentOrgVendors;

    @TableField("EXCLUDE_ORG_QUIT_VENDORS")
    @ApiModelProperty("推荐控制 -- 是否排除业务实体退出供应商(Y/N)")
    private Enable excludeOrgQuitVendors;

    @TableField("EXCLUDE_ORG_CATEGORY_STATUS")
    @ApiModelProperty("推荐控制 -- 需要排除指定品类状态的供应商")
    private String excludeOrgCategoryStatus;

}

