package com.midea.cloud.srm.sou.fixprice.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.sou.fixprice.dto.ExtFixPriceInqOrderItemsQueryDTO;
import com.midea.cloud.srm.model.sou.fixprice.dto.ExtFixPriceQueryDTO;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceHead;
import com.midea.cloud.srm.model.sou.fixprice.vo.ExtFixPriceInqOrderItemsQueryVO;

import java.util.List;
import java.util.Map;

/**
 * 备注
 * @author huangbf3
 */
public interface ExtFixPriceHeadMapper extends BaseMapper<ExtFixPriceHead> {

    /**
     * 定价列表查询
     * @param queryParam 参数
     * @return 返回
     */
    List<ExtFixPriceHead> listFixPrices(ExtFixPriceQueryDTO queryParam);

    /**
     * 询比价中标信息列表查询
     * @param queryParam 参数
     * @return 返回
     */
    List<ExtFixPriceInqOrderItemsQueryVO> listSouInqOrderItemsOrderCount(Map<String, Object> queryParam);

    /**
     * 询比价中标信息列表查询
     * @param queryParam 参数
     * @return 返回
     */
    List<ExtFixPriceInqOrderItemsQueryVO> listSouInqOrderItems(ExtFixPriceInqOrderItemsQueryDTO queryParam);

    /**
     * 查询协议价格
     * @param param
     * @return
     */
    List<Map<String, Object>> queryAgrrementInfo(Map<String, Object> param);

}
