package com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.order;

import com.github.pagehelper.Page;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouProject;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 竞价openAPI - 供应商端列表查询
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiCompSouOrderQueryVO extends ApiSouOrderQueryVO {

    /** @see CompSouProject#getBondEndTime */
    @ApiModelProperty("保证金提交截止时间")
    private Date bondEndTime;

    /** @see CompSouProcessConfig#getBondManagement */
    @ApiModelProperty("是否有保证金节点")
    private Enable hasBondNode;

    @SuppressWarnings("rawtypes")
    public static List<ApiCompSouOrderQueryVO> convertCompVO(List<ApiSouOrderQueryVO> souVOList) {
        if (souVOList.isEmpty()) { return Collections.emptyList(); }
        List<ApiCompSouOrderQueryVO> compVOList;
        if (souVOList instanceof Page) {
            compVOList = new Page<>();
            ((Page)compVOList).setTotal(((Page)souVOList).getTotal());
            ((Page)compVOList).setPageSize(((Page)souVOList).getPageSize());
            ((Page)compVOList).setPageNum(((Page)souVOList).getPageNum());
        } else {
            compVOList = new ArrayList<>(souVOList.size());
        }
        souVOList.forEach(vo -> compVOList.add(SouObjectXUtil.convertTargetObj(vo, ApiCompSouOrderQueryVO.class)));
        return compVOList;
    }

}
