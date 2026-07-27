package com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.select;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.pj.sou.comp.entity.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 竞价openAPI - 评选列表查询结果
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/16
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouSelectItemQueryVO extends SouOrderItem {


    /** @see CompSouOrderItem#getFormulaResult */
    @ApiModelProperty("提交人ID")
    private Long submitById;

    /** @see CompSouOrderItem#getFormulaResult */
    @ApiModelProperty("提交人账号")
    private String submitBy;

    /** @see CompSouOrderItem#getFormulaResult */
    @ApiModelProperty("提交人IP")
    private String submitByIp;

    /** @see CompSouOrderItem#getFormulaResult */
    @ApiModelProperty("提交人昵称")
    private String submitFullName;

    /** @see CompSouOrderItem#getFormulaResult */
    @ApiModelProperty("提交时间")
    private Date submitTime;

    private String vendorName;

    private String vendorCode;

    //----------------- 原型字段 --------------------------
    /*@ApiModelProperty("计算供应商及金额")
    private ApiSouSelectVendorPriceDTO souSelectVendorPriceDTO;

    @ApiModelProperty("月总金额")//单价*月产量
    private BigDecimal monthlyTotalAmount;*/

}
