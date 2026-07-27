package com.midea.cloud.srm.model.pj.sou.openapi.inq.vo.order;

import com.github.pagehelper.Page;
import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouProject;
import com.midea.cloud.srm.model.pj.sou.inq.enums.InqSouProjectStatusEnum;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 简易询价openAPI - 报价列表查询数据
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiInqSouOrderQueryVO extends ApiSouOrderQueryVO {

    /** @see InqSouProject#getExtProjectStatus */
    @ApiModelProperty("寻源状态")
    private InqSouProjectStatusEnum extProjectStatus;

    @SuppressWarnings("rawtypes")
    public static List<ApiInqSouOrderQueryVO> convertInqVO(List<ApiSouOrderQueryVO> voList) {
        if (voList.isEmpty()) { return Collections.emptyList(); }
        List<ApiInqSouOrderQueryVO> orderList; {
            if (voList instanceof Page) {
                orderList = new Page<>();
                ((Page)orderList).setTotal(((Page)voList).getTotal());
                ((Page)orderList).setPageSize(((Page)voList).getPageSize());
                ((Page)orderList).setPageNum(((Page)voList).getPageNum());
            } else {
                orderList = new ArrayList<>(voList.size());
            }
            voList.forEach(vo -> orderList.add(SouObjectXUtil.convertTargetObj(vo, ApiInqSouOrderQueryVO.class)));
        }
        return orderList;
    }

}
