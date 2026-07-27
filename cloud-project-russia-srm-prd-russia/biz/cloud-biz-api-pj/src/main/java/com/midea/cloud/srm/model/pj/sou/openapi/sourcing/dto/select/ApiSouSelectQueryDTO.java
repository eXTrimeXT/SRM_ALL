package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.common.BasePage;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouSelectStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouWinStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 寻源openAPI - 评选列表查询条件
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/03
 */
@Data
@ApiModel(description = "评选列表查询条件")
@EqualsAndHashCode(callSuper = true)
public class ApiSouSelectQueryDTO extends BasePage {

    /**
     * @see SouOrderItem#getProjectId
     */
    @ApiModelProperty("寻源单ID(必填)")
    private Long projectId;

    /**
     * @see SouOrderItem#getItemCode
     */
    @ApiModelProperty("物料编码")
    private String itemCode;

    /**
     * @see SouOrderItem#getItemDesc
     */
    @ApiModelProperty("物料名称")
    private String itemDesc;

    @ApiModelProperty("所属单位")
    private String affiliatedUnit;

    @ApiModelProperty("组合")
    private String itemGroup;

    /**
     * @see SouVendor#getVendorId
     */
    @ApiModelProperty("供应商ID")
    private Long vendorId;

    /**
     * @see SouVendor#getVendorName
     */
    @ApiModelProperty("供应商名称")
    private String vendorName;

    @ApiModelProperty("报价时间从")
    private Date quoteTimeFrom;

    /** @see SouProject#getPublishTime */
    @ApiModelProperty("报价时间到")
    private Date quoteTimeTo;

    @ApiModelProperty("报价从")
    private BigDecimal quoteFrom;

    /** @see SouProject#getPublishTime */
    @ApiModelProperty("报价到")
    private BigDecimal quoteTo;


    @ApiModelProperty("业务实体编码")
    private String orgOuCode;

    @ApiModelProperty("业务实体名称")
    private String orgOuName;

    @ApiModelProperty("库存组装编码")
    private String orgInvCode;

    @ApiModelProperty("库存组装名称")
    private String orgInvName;

    @ApiModelProperty("本轮入围情况")
    private SouWinStatusEnum winStatus;

    @ApiModelProperty("评选结果")
    private SouSelectStatusEnum selectStatus;

    @ApiModelProperty("轮次")
    private Integer round;

    @ApiModelProperty("排名")
    private Integer ranking;

    @ApiModelProperty("只查询已报价的供应商")
    private Enable onlySubmitOrder;

    @ApiModelProperty("是否排除最新轮次数据")
    private Enable excludeCurrentRound;

    /**
     * 入参格式化
     */
    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        // 物料编码
        itemCode = StringUtils.trimToNull(itemCode);
        // 物料名称
        itemDesc = StringUtils.trimToNull(itemDesc);
        // 业务实体编码
        orgOuCode = StringUtils.trimToNull(orgOuCode);
        // 业务实体名称
        orgOuName = StringUtils.trimToNull(orgOuName);
        // 库存组装编码
        orgInvCode = StringUtils.trimToNull(orgInvCode);
        // 库存组织名称
        orgInvName = StringUtils.trimToNull(orgInvName);
        // 组合
        itemGroup = StringUtils.trimToNull(itemGroup);
        // 供应商名称
        vendorName = StringUtils.trimToNull(vendorName);
    }

}
