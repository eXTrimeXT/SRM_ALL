package com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.select;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.ApiSouSelectFileDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouSelectFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouApprovalStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouSelectStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouWinStatusEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;


/**
 * @author ex_yipeng
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouSelectResultVO extends BaseObjectX {

    private Long orderResultId;

    private Long projectId;

    private Long orderId;

    private Long souItemId;

    private String itemDesc;

    private String affiliatedUnit;

    private BigDecimal monthlyProduction;

    @ApiModelProperty("评选附件")
    protected List<SouSelectFile> selectFileList;

    @ApiModelProperty("最高价供应商名称")
    private String maxVendorName;

    @ApiModelProperty("最高价供应商Id")
    private Long maxVendorId;

    @ApiModelProperty("最高价金额")
    private BigDecimal maxPrice;

    @ApiModelProperty("次高价供应商名称")
    private String secondVendorName;

    @ApiModelProperty("次高价供应商Id")
    private Long secondVendorId;

    @ApiModelProperty("次高价金额")
    private BigDecimal secondPrice;

    @ApiModelProperty("第三高价供应商名称")
    private String thirdVendorName;

    @ApiModelProperty("第三高价供应商Id")
    private Long thirdVendorId;

    @ApiModelProperty("第三高价金额")
    private BigDecimal thirdPrice;

    /**
     * 通过物料名称查询最近一次的竞价中标供应商
     */
    @ApiModelProperty("上期中标供应商名称")
    private String periodVendorName;

    /**
     * 通过物料名称查询最近一次的竞价中标供应商
     */
    @ApiModelProperty("上期中标供应商Id")
    private Long periodVendorId;

    @ApiModelProperty("上期中标金额")
    private BigDecimal periodPrice;

    /**
     * 本期最高价-上期中标单价）上期中标单价
     */
    @ApiModelProperty("价格差异率")
    private BigDecimal differenceRate;

    /**
     * 单价*月产量
     */
    @ApiModelProperty("月总金额")
    private BigDecimal monthTotalAmount;

    @ApiModelProperty("中标供应商供应商Id")
    private Long winVendorId;

    @ApiModelProperty("中标供应商名称")
    private String winVendorName;

    @ApiModelProperty("中标金额")
    private BigDecimal winVendorPrice;

    @ApiModelProperty("中标结果")
    private SouSelectStatusEnum selectStatus;

    @ApiModelProperty("计量单位")
    private String meteringUnit;

    /**报价次数*/
    private BigDecimal orderRound;

    private SouApprovalStatusEnum resultStatus;

    private String selectRemark;

    private String orderRemark;

    private String winNoticeRemark;

    private SouApprovalStatusEnum winNoticeStatus;

    @ApiModelProperty(value = "中标原因")
    private String winReason;

    @ApiModelProperty(value = "流标原因")
    private String failureReason;

    @ApiModelProperty(value = "是否流标")
    private String failureBidFlag;

}
