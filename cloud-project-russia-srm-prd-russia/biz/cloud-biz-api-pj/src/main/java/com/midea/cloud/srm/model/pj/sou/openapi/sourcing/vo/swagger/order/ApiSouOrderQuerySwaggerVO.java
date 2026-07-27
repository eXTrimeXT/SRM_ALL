package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.swagger.order;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.inq.enums.InqSouProjectStatusEnum;
import com.midea.cloud.srm.model.pj.sou.openapi.bid.vo.order.ApiBidSouOrderQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.brg.vo.order.ApiBrgSouOrderQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.order.ApiCompSouOrderQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.inq.vo.order.ApiInqSouOrderQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Date;

/**
 * 供应商报价列表查询数据 (swagger接口专用)
 * PS: 由于目前的寻源结构（ObjectX + SPI），很难描述不同寻源场景对入参的需求差别，
 *     因此用一个专有的类来装所有的信息。
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/01/05
 */
@Data
@ApiModel(description = "供应商报价列表查询数据")
@EqualsAndHashCode(callSuper = true)
public class ApiSouOrderQuerySwaggerVO extends ApiSouOrderQueryVO {

    /** @see ApiInqSouOrderQueryVO#getExtProjectStatus */
    @ApiModelProperty("寻源状态(仅用于简易询价-inq)")
    private InqSouProjectStatusEnum extProjectStatus;

    /**
     * @see ApiBidSouOrderQueryVO#getBondEndTime
     * @see ApiBrgSouOrderQueryVO#getBondEndTime
     * @see ApiCompSouOrderQueryVO#getBondEndTime
     */
    @ApiModelProperty("保证金提交截止时间(仅用于招投标-bid/项目式询价-brg/竞价-comp)")
    private Date bondEndTime;

    /**
     * @see ApiBidSouOrderQueryVO#getHasBondNode
     * @see ApiBrgSouOrderQueryVO#getHasBondNode
     * @see ApiCompSouOrderQueryVO#getHasBondNode
     */
    @ApiModelProperty("是否有保证金节点(仅用于招投标-bid/项目式询价-brg/竞价-comp)")
    private Enable hasBondNode;

    public static PageInfo<ApiSouOrderQuerySwaggerVO> convert(PageInfo<ApiSouOrderQueryVO> page) {
        PageInfo<ApiSouOrderQuerySwaggerVO> pageInfo = new PageInfo<>();
        pageInfo.setList(new ArrayList<>(page.getList().size())); {
            page.getList().forEach(vo -> pageInfo.getList().add(SouObjectXUtil.convertTargetObj(vo, ApiSouOrderQuerySwaggerVO.class)));
        }
        pageInfo.setPageNum(page.getPageNum());
        pageInfo.setPageSize(page.getPageSize());
        pageInfo.setTotal(page.getTotal());
        return pageInfo;
    }

}
