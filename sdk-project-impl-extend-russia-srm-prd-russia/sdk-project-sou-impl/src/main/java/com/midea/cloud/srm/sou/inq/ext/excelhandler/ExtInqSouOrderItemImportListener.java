package com.midea.cloud.srm.sou.inq.ext.excelhandler;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.component.context.container.SpringContextHolder;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseTax;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtInqSouOrderItemImportDTO;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPjInqSouOrder;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouOrderItem;
import com.midea.cloud.srm.model.sou.openapi.inq.dto.order.ApiInqSouOrderItemDTO;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrder;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.sou.inq.ext.dao.ExtPjInqSouOrderDAO;
import com.midea.cloud.srm.sou.inq.order.dao.InqSouOrderItemDAO;
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
 * 备注
 * @author huangbf3
 */
public class ExtInqSouOrderItemImportListener extends AnalysisEventListener<ExtInqSouOrderItemImportDTO> {

    /**
     * 导入数据
     */
    @Getter
    private final List<ExtInqSouOrderItemImportDTO> dtoList = new ArrayList<>(200);
    /**
     * 转换后的数据
     */
    @Getter
    private final List<ApiInqSouOrderItemDTO> resultList = new ArrayList<>(200);
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
    private ExtPjInqSouOrder existInqOrder;
    private Map<Long, SouOrderItem> currentRoundExistOrderItemMap = Collections.emptyMap();
    private Map<Long, InqSouOrderItem> currentRoundExistInqOrderItemMap = Collections.emptyMap();
    private Map<String, BigDecimal> taxMap = Collections.emptyMap();
    private Map<String, String> invokeTypeDictMap = Collections.emptyMap();

    public ExtInqSouOrderItemImportListener(long projectId, Integer round, long vendorId) {
        SouProject souProject = SpringContextHolder.getBean(SouProjectDAO.class).getById(projectId);
        round = round != null ? round : souProject.getCurrentRound();
        // 查询本轮次的该供应商报价信息
        SouOrder order = SpringContextHolder.getBean(SouOrderDAO.class).lambdaQuery()
                .eq(SouOrder::getProjectId, projectId)
                .eq(SouOrder::getVendorId, vendorId)
                .eq(SouOrder::getRound, round)
                .one();
        if (order != null) {
            this.existInqOrder = SpringContextHolder.getBean(ExtPjInqSouOrderDAO.class).getById(order.getOrderId());
        }
        if (order != null) {
            currentRoundExistOrderItemMap = SpringContextHolder.getBean(SouOrderItemDAO.class)
                    .list(SouOrderItem::getOrderId, order.getOrderId())
                    .stream().collect(Collectors.toMap(SouOrderItem::getSouItemId, Function.identity()));
            currentRoundExistInqOrderItemMap = SpringContextHolder.getBean(InqSouOrderItemDAO.class)
                    .list(InqSouOrderItem::getOrderId, order.getOrderId())
                    .stream().collect(Collectors.toMap(InqSouOrderItem::getOrderItemId, Function.identity()));
        }
        // 查询税率相关
        taxMap = SpringContextHolder.getBean(BaseClient.class).listTaxAll().stream().collect(Collectors.toMap(PurchaseTax::getTaxKey, PurchaseTax::getTaxCode));
        // 查询字典
        invokeTypeDictMap = SpringContextHolder.getBean(BaseClient.class).listAllByDictCode("EXT_SOU_INQ_ORDER_INVOICE_TYPE")
                .stream().collect(Collectors.toMap(DictItemDTO::getDictItemName, DictItemDTO::getDictItemCode, (a, b) -> a));
    }

    @Override
    public void invoke(ExtInqSouOrderItemImportDTO data, AnalysisContext context) {
        StringBuilder errMsg = new StringBuilder(200);
        this.doInvoke(data, errMsg);
        if (errMsg.length() > 0) {
            data.setErrMsg(errMsg.toString());
        }
    }

    private void doInvoke(ExtInqSouOrderItemImportDTO data, StringBuilder errMsg) {
        this.rowIndex++;

        this.dtoList.add(data);
        ApiInqSouOrderItemDTO dto = new ApiInqSouOrderItemDTO();
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
                BigDecimal noTaxPrice = new BigDecimal(tempV).stripTrailingZeros();
                if(BigDecimal.ZERO.compareTo(noTaxPrice) == 0){
                    errMsg.append("未税单价不能为0;");
                }else{
                    dto.setOrderNotaxPrice(noTaxPrice);
                }
            } catch (NumberFormatException e) {
                errMsg.append("未税单价不是数字;");
            }
        }
        extracted(data, errMsg, dto);
    }

    /**
     * 拆分
     * @param data 参数
     * @param errMsg 参数
     * @param dto 参数
     */
    private static void extracted(ExtInqSouOrderItemImportDTO data, StringBuilder errMsg, ApiInqSouOrderItemDTO dto) {
        String tempV;
        // 6: 预付款说明
        tempV = StringUtils.trimToNull(data.getAdvancePaymentRemark());
        if (tempV == null) {
            dto.setAdvancePaymentRemark(Enable.N);
        } else {
            switch (tempV) {
                case "是":
                    dto.setAdvancePaymentRemark(Enable.Y);
                    break;
                case "否":
                    dto.setAdvancePaymentRemark(Enable.N);
                    break;
                default:
                    errMsg.append("预付款说明填写错误");
                    break;
            }
        }
        // 7: 到货周期(自然日)
        tempV = StringUtils.trimToNull(data.getExtLeadTime());
        if (tempV != null) {
            try {
                dto.setExtLeadTime(Integer.valueOf(tempV));
            } catch (NumberFormatException e) {
                errMsg.append("到货周期(自然日)不是正整数;");
            }
        }
        // 8: 质保期(自然日)
        tempV = StringUtils.trimToNull(data.getExtWarrantyPeriod());
        if (tempV != null) {
            try {
                dto.setExtWarrantyPeriod(Integer.valueOf(tempV));
            } catch (NumberFormatException e) {
                errMsg.append("质保期(自然日)不是正整数;");
            }
        }
        // 9: 备注
        tempV = StringUtils.trimToNull(data.getOrderRemark());
        if (tempV != null) {
            dto.setOrderRemark(tempV);
            int num300 = 300;
            if (dto.getOrderRemark().length() > num300) {
                errMsg.append("备注的输入长度不能超过300;");
            }
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        AssertUtils.notEmpty(dtoList, "文件为空，无法读取到有效数据");
        this.hasError = dtoList.stream().map(ExtInqSouOrderItemImportDTO::getErrMsg).anyMatch(Objects::nonNull);
    }

}
