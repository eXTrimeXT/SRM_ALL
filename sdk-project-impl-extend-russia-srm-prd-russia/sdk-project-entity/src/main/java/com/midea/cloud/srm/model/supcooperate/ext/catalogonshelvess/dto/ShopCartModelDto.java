package com.midea.cloud.srm.model.supcooperate.ext.catalogonshelvess.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

/**
 * <pre>
 *  功能名称  购物车EXCEL模型
 * </pre>
 *
 * @author huangbf3@meicloud.com
 * @version 1.00.00
 * <p>
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020/7/24 8:30
 *  修改内容:
 * </pre>
 */
@Data
@ColumnWidth(20)
@HeadRowHeight(-10)
@ApiModel(description = "购物车EXCEL模型")
public class ShopCartModelDto implements Serializable {

    private static final long serialVersionUID = 525681281551782881L;
    /**
     * *汇总人
     */
    @ExcelProperty(value = "*汇总人", index = 0)
    @NotEmpty(message = "*汇总人")
    private String summaryNickname;

    /**
     * *部门领导
     */
    @ExcelProperty(value = "*部门领导", index = 1)
    private String deptLeaderUserNickname;

    /**
     * *需求日期
     */
    @ExcelProperty(value = "*需求日期（≥当前时间+15天）", index = 2)
    @NotEmpty(message = "*需求日期（≥当前时间+15天）")
    private Date requirementDateStr;

    /**
     * *物料编码
     */
    @ExcelProperty(value = "*物料编码", index = 3)
    @NotEmpty(message = "*物料编码")
    private String materialCode;

    /**
     * *需求数量
     */
    @ExcelProperty(value = "*需求数量",index = 4)
    @NotEmpty(message = "*需求数量")
    private BigDecimal requirementNum;

    /**
     * *使用部门
     */
    @ExcelProperty(value = "*使用部门",index = 5)
    @NotEmpty(message = "*使用部门")
    private String extDepartmentcode;

    /**
     * *物料编码
     */
    @ExcelProperty(value = "*使用人联系方式", index = 6)
    @NotEmpty(message = "*使用人联系方式")
    private String extUserPhone;
    /**
     * *参考价
     */
    @ExcelProperty(value = "*参考价",index = 7)
    @NotEmpty(message = "*参考价")
    private BigDecimal extReferencePrice;

    /**
     * *参考价
     */
    @ExcelProperty(value = "*用途",index = 8)
    @NotEmpty(message = "*用途")
    private String extUseTo;

    /**
     * *品牌
     */
    @ExcelProperty(value = "品牌",index = 9)
    @NotEmpty(message = "品牌")
    private String brand;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注",index = 10)
    @NotEmpty(message = "备注")
    private String extBuyTypeComment;
    /**
     * 错误信息提示
     */
    @ExcelProperty( value = "错误信息提示",index = 11)
    private String errorMsg;

    /**
     * *币种 默认人民币
     */
    @ExcelIgnore
    private String currencyCode;

    /**
     * *币种名称
     */
    @ExcelIgnore
    private String currencyName;

    /**
     * 品类编码
     */
    @ExcelIgnore
    private String categoryCode;

    /**
     * 品类ID
     */
    @ExcelIgnore
    private Long categoryId;

    /**
     * 品类名称
     */
    @ExcelIgnore
    private String categoryName;

    /**
     * 部门领导ID
     */
    @ExcelIgnore
    private Long deptLeaderUserId;

    /**
     * 收货地址
     */
    @ExcelIgnore
    private String extAddress;

    /**
     * 收货地址ID
     */
    @ExcelIgnore
    private Long extAddressId;

    /**
     * 收货地址名称
     */
    @ExcelIgnore
    private String extAddressName;

    /**
     * 收货地址编码
     */
    @ExcelIgnore
    private String extAreaCode;

    /**
     * 使用部门ID
     */
    @ExcelIgnore
    private Long extCeeaDeptid;

    /**
     * 使用部门
     */
    @ExcelIgnore
    private String extDepartment;

    /**
     * 使用部门编码
     */
    /*@ExcelIgnore
    private String extDepartmentcode;*/

    /**
     * 是否商品
     */
    @ExcelIgnore
    private String extIsGoods;

    /**
     * 收货人
     */
    @ExcelIgnore
    private String extReceiver;

    /**
     * 收货人联系方式
     */
    @ExcelIgnore
    private String extReceiverContact;

    /**
     * 物料ID
     */
    @ExcelIgnore
    private Long materialId;

    /**
     * 物料名称
     */
    @ExcelIgnore
    private String materialName;

    /**
     * 二级品类编码
     */
    @ExcelIgnore
    private String extSecondCategoryCode;

    /**
     * 二级品类名称
     */
    @ExcelIgnore
    private String extSecondCategoryName;

    /**
     * 二级品类ID
     */
    @ExcelIgnore
    private Long extSecondCategoryId;

    /**
     * 实体编码
     */
    @ExcelIgnore
    private String orgCode;

    /**
     * 实体ID
     */
    @ExcelIgnore
    private Long orgId;

    /**
     * 实体名称
     */
    @ExcelIgnore
    private String orgName;

    /**
     * 规格型号
     */
    @ExcelIgnore
    private String specification;

    /**
     * 汇总人ID
     */
    @ExcelIgnore
    private Long summaryUserId;

    /**
     * 单位编码
     */
    @ExcelIgnore
    private String unit;

    /**
     * 单位名称
     */
    @ExcelIgnore
    private String unitName;

    /**
     * 数据行号
     */
    @ExcelIgnore
    private Integer row;

    @ExcelIgnore
    private LocalDate requirementDate;
    @ExcelIgnore
    private String extOrgIdList;
}
