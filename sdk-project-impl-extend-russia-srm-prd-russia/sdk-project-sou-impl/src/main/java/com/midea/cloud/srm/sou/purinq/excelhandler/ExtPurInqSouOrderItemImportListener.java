package com.midea.cloud.srm.sou.purinq.excelhandler;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.component.context.container.SpringContextHolder;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseTax;
import com.midea.cloud.srm.model.extapi.sou.purinq.dto.order.ApiPurInqSouOrderItemDTO;
import com.midea.cloud.srm.model.extapi.sou.purinq.dto.order.ExtPurInqSouOrderItemImportDTO;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouOrder;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouOrderItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrder;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.sou.purinq.dao.ExtPurInqSouOrderDAO;
import com.midea.cloud.srm.sou.purinq.dao.ExtPurInqSouOrderItemDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProjectDAO;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderDAO;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderItemDAO;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
public class ExtPurInqSouOrderItemImportListener extends AnalysisEventListener<ExtPurInqSouOrderItemImportDTO> {

    private final static int NUM300=300;
    /**
     * 导入数据
     */
    @Getter
    private final List<ExtPurInqSouOrderItemImportDTO> dtoList = new ArrayList<>(200);
    /**
     * 转换后的数据
     */
    @Getter
    private final List<ApiPurInqSouOrderItemDTO> resultList = new ArrayList<>(200);
    /**
     * 导入数据的解析过程中是否存在异常
     */
    @Getter
    private boolean hasError = false;
    private int rowIndex = 0;
    /**
     * 错误文件ID
     */
    @Getter
    @Setter
    private Long errFileDocId;
    /**
     * 本轮次已有的报价明细数据
     */
    @Nullable
    @Getter
    private ExtPurInqSouOrder existInqOrder;

    private Map<Long, SouOrderItem> currentRoundExistOrderItemMap = Collections.emptyMap();

    private Map<Long, ExtPurInqSouOrderItem> currentRoundExistInqOrderItemMap = Collections.emptyMap();

    private Map<String, BigDecimal> taxMap = Collections.emptyMap();

    private Map<String, String> invokeTypeDictMap = Collections.emptyMap();

    public ExtPurInqSouOrderItemImportListener(long projectId, Integer round, long vendorId) {
        SouProject souProject = SpringContextHolder.getBean(SouProjectDAO.class).getById(projectId);
        round = round != null ? round : souProject.getCurrentRound();
        // 查询本轮次的该供应商报价信息
        SouOrder order = SpringContextHolder.getBean(SouOrderDAO.class).lambdaQuery()
                .eq(SouOrder::getProjectId, projectId)
                .eq(SouOrder::getVendorId, vendorId)
                .eq(SouOrder::getRound, round)
                .one();
        if (order != null) {
            this.existInqOrder = SpringContextHolder.getBean(ExtPurInqSouOrderDAO.class).getById(order.getOrderId());
        }
        if (order != null) {
            currentRoundExistOrderItemMap = SpringContextHolder.getBean(SouOrderItemDAO.class)
                    .list(SouOrderItem::getOrderId, order.getOrderId())
                    .stream().collect(Collectors.toMap(SouOrderItem::getSouItemId, Function.identity()));
            currentRoundExistInqOrderItemMap = SpringContextHolder.getBean(ExtPurInqSouOrderItemDAO.class)
                    .list(ExtPurInqSouOrderItem::getOrderId, order.getOrderId())
                    .stream().collect(Collectors.toMap(ExtPurInqSouOrderItem::getOrderItemId, Function.identity()));
        }
        // 查询税率相关
        taxMap = SpringContextHolder.getBean(BaseClient.class).listTaxAll().stream().collect(Collectors.toMap(PurchaseTax::getTaxKey, PurchaseTax::getTaxCode));
        // 查询字典
        invokeTypeDictMap = SpringContextHolder.getBean(BaseClient.class).listAllByDictCode("EXT_SOU_PURINQ_ORDER_INVOICE_TYPE")
                .stream().collect(Collectors.toMap(DictItemDTO::getDictItemName, DictItemDTO::getDictItemCode, (a, b) -> a));
    }

    @Override
    public void invoke(ExtPurInqSouOrderItemImportDTO data, AnalysisContext context) {
        StringBuilder errMsg = new StringBuilder(200);
        this.doInvoke(data, errMsg);
        if (errMsg.length() > 0) {
            data.setErrMsg(errMsg.toString());
        }
    }

    private void doInvoke(ExtPurInqSouOrderItemImportDTO data, StringBuilder errMsg) {
        this.rowIndex++;

        this.dtoList.add(data);
        ApiPurInqSouOrderItemDTO dto = new ApiPurInqSouOrderItemDTO();
        this.resultList.add(dto);

        SouOrderItem orderItem = currentRoundExistOrderItemMap.get(existInqOrder != null ? existInqOrder.getOrderId() : null);

        // 1: souItemId
        String tempV = StringUtils.trimToNull(data.getSouItemId());
        if (tempV == null) {
            errMsg.append("ID为空，禁止修改该字段;");
            return;
        } else {
            try {
                dto.setSouItemId(Long.valueOf(tempV));
            } catch (NumberFormatException e) {
                errMsg.append("ID格式错误，禁止修改该字段;");
            }
        }
        // 2: orderItemId
        if (orderItem != null) {
            dto.setOrderItemId(orderItem.getOrderItemId());
        }
        // 3: 税率
        tempV = StringUtils.trimToNull(data.getTaxKey());
        if (tempV == null) {
            errMsg.append("请选择税率;");
        } else {
            dto.setTaxKey(tempV);
            dto.setTaxRate(taxMap.get(dto.getTaxKey()));
            if (dto.getTaxRate() == null) {
                errMsg.append("税率不存在;");
            }
        }
        // 4: 发票类型
        tempV = StringUtils.trimToNull(data.getInvoiceType());
        dto.setInvoiceType(tempV);
        if (dto.getInvoiceType() != null) {
            dto.setInvoiceType(invokeTypeDictMap.get(dto.getInvoiceType()));
            if (dto.getInvoiceType() == null) {
                errMsg.append("发票类型不存在;");
            }
        }
        // 5: 未税单价
        tempV = StringUtils.trimToNull(data.getOrderNotaxPrice());
        if (tempV == null) {
            errMsg.append("缺少未税单价;");
        } else {
            try {
                dto.setOrderNotaxPrice(new BigDecimal(tempV).stripTrailingZeros());
            } catch (NumberFormatException e) {
                errMsg.append("未税单价不是数字;");
            }
        }
        // 6: 到货周期(自然日)
        tempV = StringUtils.trimToNull(data.getExtLeadTime());
        if (tempV != null) {
            try {
                dto.setExtLeadTime(Integer.valueOf(tempV));
            } catch (NumberFormatException e) {
                errMsg.append("到货周期(自然日)不是正整数;");
            }
        }
        // 7: 质保期(自然日)
        tempV = StringUtils.trimToNull(data.getExtWarrantyPeriod());
        if (tempV != null) {
            try {
                dto.setExtWarrantyPeriod(Integer.valueOf(tempV));
            } catch (NumberFormatException e) {
                errMsg.append("质保期(自然日)不是正整数;");
            }
        }
        // 8: 备注
        tempV = StringUtils.trimToNull(data.getOrderRemark());
        if (tempV != null) {
            dto.setOrderRemark(tempV);
            if (dto.getOrderRemark().length() > NUM300) {
                errMsg.append("备注的输入长度不能超过300;");
            }
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        AssertUtils.notEmpty(dtoList, "文件为空，无法读取到有效数据");
        this.hasError = dtoList.stream().map(ExtPurInqSouOrderItemImportDTO::getErrMsg).anyMatch(Objects::nonNull);
    }

}
