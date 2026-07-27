package com.midea.cloud.srm.supcooperate.ext.requirement.pr.service;

import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.dto.ExtPrSouRequirementLineExportRequestDto;
import com.midea.cloud.srm.model.suppliercooperate.order.dto.OrderSaveRequestDTO;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.ExtPurchaseRequirementCreateSouDTO;
import com.midea.cloud.srm.model.sou.fixprice.dto.ExtFixPriceHeadDTO;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.suppliercooperate.order.enums.PurchaseOrderEnum;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.ExtPurchaseRequirementCreateSouDTO;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.OrderPriceParams;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.PurchaseRequirementHeadDTO;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.PurchaseRequirementLineDTO;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author zenghx2
 */
public interface PurchaseRequirementService {
    /**
     * 备注
     * @param requirementHead 参数
     * @param pushUserCode 参数
     * @param pushUserName 参数
     */
    void pushPool(PurchaseRequirementHeadDTO requirementHead, String pushUserCode, String pushUserName);

    /**
     * 备注
     * @param requirementLines 参数
     * @return 返回
     */
    Map<String, OrderPriceParams> getPriceMap(List<PurchaseRequirementLineDTO> requirementLines);

    /**
     * 备注
     *
     * @param items              参数
     * @param fromPriceAgreement 参数
     * @param priceLibraryMap    参数
     * @param orderQtyMap        参数
     * @param status             参数
     * @return 返回
     */
    OrderSaveRequestDTO createOrder(List<PurchaseRequirementLineDTO> items,
                                    Boolean fromPriceAgreement,
                                    Map<String, OrderPriceParams> priceLibraryMap,
                                    Map<Long, BigDecimal> orderQtyMap,
                                    PurchaseOrderEnum status);

    /**
     * 分组创建订单
     * @param items
     * @param fromPriceAgreement
     * @param priceMap
     * @param orderQtyMap
     * @param status
     * @return
     */
    List<OrderSaveRequestDTO> groupCreateOrder(List<PurchaseRequirementLineDTO> items,
                                               Boolean fromPriceAgreement,
                                               Map<String, OrderPriceParams> priceMap,
                                               Map<Long, BigDecimal> orderQtyMap,
                                               PurchaseOrderEnum status);


    /**
     * 非招创建寻源
     * @param param
     * @return
     */
    SouProject createSou(ExtPurchaseRequirementCreateSouDTO param);

    /**
     * 创建定价订单
     * @param extFixPriceHeadDTO
     */
    void createOrderByFixPrice(ExtFixPriceHeadDTO extFixPriceHeadDTO);

    /**
     * 勾选导出明细行
     * @param params
     * @param response
     * @throws Exception
     */
    void exportRequirementLine(ExtPrSouRequirementLineExportRequestDto params, HttpServletResponse response) throws Exception;

}
