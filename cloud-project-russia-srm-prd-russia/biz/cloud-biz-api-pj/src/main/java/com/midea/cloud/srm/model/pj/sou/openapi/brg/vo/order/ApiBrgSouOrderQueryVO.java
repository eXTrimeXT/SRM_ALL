package com.midea.cloud.srm.model.pj.sou.openapi.brg.vo.order;

import com.github.pagehelper.Page;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.brg.entity.BrgSouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.brg.entity.BrgSouProject;
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
 * 项目式询价openAPI - 供应商端列表查询
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/23
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiBrgSouOrderQueryVO extends ApiSouOrderQueryVO {

    /** @see BrgSouProject#getBondEndTime */
    @ApiModelProperty("保证金提交截止时间")
    private Date bondEndTime;

    /** @see BrgSouProcessConfig#getBondManagement */
    @ApiModelProperty("是否有保证金节点")
    private Enable hasBondNode;

    @SuppressWarnings("rawtypes")
    public static List<ApiBrgSouOrderQueryVO> convertBrgVO(List<ApiSouOrderQueryVO> souVOList) {
        if (souVOList.isEmpty()) { return Collections.emptyList(); }
        List<ApiBrgSouOrderQueryVO> brgVOList;
        if (souVOList instanceof Page) {
            brgVOList = new Page<>();
            ((Page)brgVOList).setTotal(((Page)souVOList).getTotal());
            ((Page)brgVOList).setPageSize(((Page)souVOList).getPageSize());
            ((Page)brgVOList).setPageNum(((Page)souVOList).getPageNum());
        } else {
            brgVOList = new ArrayList<>(souVOList.size());
        }
        souVOList.forEach(vo -> brgVOList.add(SouObjectXUtil.convertTargetObj(vo, ApiBrgSouOrderQueryVO.class)));
        return brgVOList;
    }

}
