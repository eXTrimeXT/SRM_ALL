package com.midea.cloud.srm.model.sou.designplans.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * 备注
 * @author huangbf3
 */
@Data
public class PullQueryDto {

    /**
     * 主id
     */
    private Long designId;

    /**
     * 1、上年订单数据。2、上上年订单数据
     */
    private Integer type;

    /**
     * 分类
     */
    private List<PullQueDto> oneLevel;

    /** 二级分类 */
    //private List<ThreeInfo> twoLevel;

    /** 供应商名称编码 */
    private List<String> supList;

    /** 申请单位编码 */
    private List<String> orgList;

    /** 采购员id */
    private List<String> personList;

    /** 品牌 */
    private String brand;

    /** 物资名称,物料编码 */
    private List<String> materialList;

    /** 订单状态编码 */
    private List<String> orderStatusList;

    /** 单项物资订单数 传（> 1或 <= 2等） */
    private String orderNum;

    /** 单项物资采购金额（未税） */
    private String buyMoney;

    /** 上年订单日期从 */
    private LocalDate lastYearOrderDateStart;

    /** 上年订单日期到 */
    private LocalDate lastYearOrderDateEnd;

    /** 上上年订单日期从 */
    private LocalDate lastLastYearOrderDateStart;

    /** 上上年订单日期到 */
    private LocalDate lastLastYearOrderDateEnd;

    /**
     *品类id
     **/
    Set<Long> categoryIds;
    /**
     *区域编码
     **/
    private List<String> areaCodes;
}
