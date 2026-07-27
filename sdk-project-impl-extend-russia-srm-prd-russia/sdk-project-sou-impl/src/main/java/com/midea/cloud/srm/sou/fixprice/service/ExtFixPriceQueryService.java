package com.midea.cloud.srm.sou.fixprice.service;

import com.github.pagehelper.page.PageMethod;
import com.midea.cloud.srm.model.sou.fixprice.dto.ExtFixPriceInqOrderItemsQueryDTO;
import com.midea.cloud.srm.model.sou.fixprice.dto.ExtFixPriceQueryDTO;
import com.midea.cloud.srm.model.sou.fixprice.dto.ExtFixPriceReqLinesQueryDTO;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceHead;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceLine;
import com.midea.cloud.srm.model.sou.fixprice.vo.*;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouItem;
import org.springframework.lang.Nullable;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
public interface ExtFixPriceQueryService {

    /**
     * 定价单列表查询
     * @param queryParam 参数
     * @return 返回
     */
    List<ExtFixPriceHead> listFixPrices(ExtFixPriceQueryDTO queryParam);

    /**
     * 查看定价单详情
     * @param fixPriceHeadId 参数
     * @return 返回
     */
    @Nullable
    ExtFixPriceHeadVO getFixPrice(long fixPriceHeadId);

    /**
     * 询比价中标信息列表查询
     * @param queryParam 参数
     * @return 返回
     */
    List<ExtFixPriceInqOrderItemsQueryVO> listSouInqOrderItems(ExtFixPriceInqOrderItemsQueryDTO queryParam);

    /**
     * 近期采购列表查询
     * @param queryParam 参数
     * @return 返回
     */
    List<ExtFixPriceReqLinesQueryVO> listReqLines(ExtFixPriceReqLinesQueryDTO queryParam);

    /**
     * 供应商报价明细查询
     * @param orderItemId 参数
     * @return 返回
     */
    List<ExtFixPriceInqOrderItemVO> listSouInqOrderItemsForPriceLine(long orderItemId);


    /**
     * 导出定价单列表
     * @param queryParam 参数
     * @return
     */
    List<ExtFixPriceExportVO> exportFixPrices(ExtFixPriceQueryDTO queryParam);

    /**
     * 导出明细
     * @param extFixPriceId
     * @return
     */
    List<ExtFixPriceExportVO> exportFixPriceLine(Long extFixPriceId);


    /**
     * 判断定价单明细是否存在
     * @param itemCode 物料号
     * @param sourceFromNo 询价单号
     * @return
     */
    List<ExtFixPriceLine> queryLines(String itemCode, String sourceFromNo);

    /**
     * 通过询价单查询定价单
     * @param inqSouItems
     * @return
     */
    List<ExtFixPriceLine> queryLines(List<SouItem> inqSouItems);
}
