package com.midea.cloud.srm.model.pj.sou.inq.dto.webapi.select;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 简易询价模块 - 比价信息查询条件
 *
 * @author zhangwk12@midea.com
 * @since 2022/04/19
 */
@Data
@ApiModel("比价查询条件")
public class InqSouPriceCompareQueryWebDTO {

    /**
     * 询价单ID(必填)
     *
     * @see SouProject#getProjectId()
     */
    @ApiModelProperty(value = "询价单ID", required = true)
    private Long projectId;

    /**
     * 轮次(为空时默认最新轮次)
     *
     * @see SouProject#getCurrentRound()
     */
    @ApiModelProperty("轮次(为空时默认最新轮次)")
    private Integer round;

    /**
     * 物料编码
     * PS: 等值查询
     *
     * @see SouItem#getItemCode()
     */
    @ApiModelProperty("物料编码(等值查询)")
    private String itemCode;

    /**
     * 物料描述
     * PS: 模糊查询
     *
     * @see SouItem#getItemDesc
     */
    @ApiModelProperty("物料描述")
    private String itemDesc;

    /**
     * 入参格式化
     */
    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少inquiryId参数");
        }
        itemCode = StringUtils.trimToNull(itemCode);
        itemDesc = StringUtils.trimToNull(itemDesc);
    }

}
