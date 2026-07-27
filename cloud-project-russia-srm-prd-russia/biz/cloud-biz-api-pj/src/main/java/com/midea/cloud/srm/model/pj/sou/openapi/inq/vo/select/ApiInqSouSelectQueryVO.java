package com.midea.cloud.srm.model.pj.sou.openapi.inq.vo.select;

import com.github.pagehelper.Page;
import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouCurrency;
import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouItem;
import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouOrderItem;
import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouOrderItemPayment;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select.ApiSouSelectQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 简易询价openAPI - 评选列表查询数据
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiInqSouSelectQueryVO extends ApiSouSelectQueryVO {

    /** @see InqSouItem#getLadderType */
    @ApiModelProperty("阶梯价类型(standard-标准阶梯价、sum-累计阶梯价)")
    private String ladderType;

    /** @see InqSouItem#getIsFormula */
    @ApiModelProperty("是否公式报价(Y/N)")
    private Enable isFormula;

    /** @see InqSouItem#getMaterialFormulaRelateId */
    @ApiModelProperty("物料价格公式关联ID")
    private Long materialFormulaRelateId;

    /** @see InqSouItem#getFormulaId */
    @ApiModelProperty("公式ID")
    private Long formulaId;

    /** @see InqSouItem#getItemType */
    @ApiModelProperty("行类型[字典值: DMAND_LINE_TYPE]")
    private String itemType;

    /** @see InqSouItem#getFormulaName */
    @ApiModelProperty("公式名称")
    private String formulaName;

    /** @see InqSouItem#getFormulaValue */
    @ApiModelProperty("公式值")
    private String formulaValue;

    /** @see InqSouItem#getNotaxTargetPrice */
    @ApiModelProperty("未税目标价（报价阶段设定目标价时才使用）")
    private BigDecimal notaxTargetPrice;

    /** @see InqSouOrderItem#getFormulaAttrValues */
    @ApiModelProperty("供应商填写的公式报价信息")
    private String formulaAttrValues;

    /** @see InqSouCurrency#getPriceTax */
    @ApiModelProperty("汇率")
    private BigDecimal priceTax;

    @ApiModelProperty("供应商本位币未税报价与目标价价差")
    private BigDecimal priceDifference;

    @ApiModelProperty("附件")
    private List<SceneFile> itemFiles;

    @ApiModelProperty("账期信息")
    private List<InqSouOrderItemPayment> paymentList;

    @SuppressWarnings("rawtypes")
    public static List<ApiInqSouSelectQueryVO> convertInqVO(List<ApiSouSelectQueryVO> selectList) {
        if (selectList.isEmpty()) { return Collections.emptyList(); }
        List<ApiInqSouSelectQueryVO> voList; {
            if (selectList instanceof Page) {
                voList = new Page<>();
                ((Page)voList).setTotal(((Page)selectList).getTotal());
                ((Page)voList).setPageSize(((Page)selectList).getPageSize());
                ((Page)voList).setPageNum(((Page)selectList).getPageNum());
            } else {
                voList = new ArrayList<>(selectList.size());
            }
        }
        selectList.forEach(select -> voList.add(SouObjectXUtil.convertTargetObj(select, ApiInqSouSelectQueryVO.class)));
        return voList;
    }

}
