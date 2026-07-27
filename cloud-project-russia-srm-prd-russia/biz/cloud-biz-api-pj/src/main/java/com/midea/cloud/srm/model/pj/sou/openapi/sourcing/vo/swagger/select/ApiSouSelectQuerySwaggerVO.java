package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.swagger.select;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select.ApiSouSelectQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.swagger.order.ApiSouOrderItemSwaggerVO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;

/**
 * 评选列表信息 (swagger接口专用)
 * PS: 由于目前的寻源结构（ObjectX + SPI），很难描述不同寻源场景对入参的需求差别，
 *     因此用一个专有的类来装所有的信息。
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/01/05
 */
@Data
@ApiModel(description = "评选列表信息")
@EqualsAndHashCode(callSuper = true)
public class ApiSouSelectQuerySwaggerVO extends ApiSouOrderItemSwaggerVO {

    /** @see SouProject#getStandardCurrency */
    @ApiModelProperty("本位币")
    private String standardCurrency;

    /** @see SouProject#getScoreRuleType  */
    @ApiModelProperty("评分规则")
    private SouScoreRuleTypeEnum scoreRuleType;

    public static PageInfo<ApiSouSelectQuerySwaggerVO> convert(PageInfo<ApiSouSelectQueryVO> page) {
        PageInfo<ApiSouSelectQuerySwaggerVO> pageInfo = new PageInfo<>();
        pageInfo.setList(new ArrayList<>(page.getList().size())); {
            page.getList().forEach(vo -> pageInfo.getList().add(SouObjectXUtil.convertTargetObj(vo, ApiSouSelectQuerySwaggerVO.class)));
        }
        pageInfo.setPageNum(page.getPageNum());
        pageInfo.setPageSize(page.getPageSize());
        pageInfo.setTotal(page.getTotal());
        return pageInfo;
    }

}
