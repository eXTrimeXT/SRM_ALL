package com.midea.cloud.srm.model.pj.supplier.rev.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author huangbf3
 */
@Data
public class BpmReviewFormDto implements Serializable {

    /**
     * 资质审查类型
     */
    private String zzsclx;

    /**
     * 供应商名称
     */
    private String gysmc;

    /**
     * 资质审查单号
     */
    private String zzscdh;

    /**
     * 审核状态
     */
    private String shzt;

    /**
     * 创建人
     */
    private String cjr;

    /**
     * 部门
     */
    private String bm;

    /**
     * 创建时间
     */
    private String cjsj;

    /**
     * 是否招标
     */
    private String sfzb;

    /**
     * 单据说明
     */
    private String djsm;

}
