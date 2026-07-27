package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select;

import com.midea.cloud.srm.model.inq.price.entity.PriceLibrary;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;
/**
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouOrderPriceCompareVO extends BaseObjectX {
    /** vendorName */
    @ApiModelProperty("节点信息")
    private Map<String, List<SouOrderPriceCompareNode>> priceNodes;

    @Data
    @ApiModel("比价节点信息")
    public static class SouOrderPriceCompareNode {
        @ApiModelProperty("未税单价")
        private BigDecimal notaxPrice;
        @ApiModelProperty("含税单价")
        private BigDecimal taxPrice;
        @ApiModelProperty("供应商编码")
        private String vendorCode;
        @ApiModelProperty("供应商名称")
        private String vendorName;
        @ApiModelProperty("时间")
        private Date date;
    }

    /**
     * 便捷方法
     */
    public static ApiSouOrderPriceCompareVO convert(List<PriceLibrary> priceLibraryList,
                                               List<CompanyInfo> companyInfos,
                                               Map<String/* fromCurrency */, BigDecimal/* rate */> rateMap,
                                               String standardCurrency) {
        if (priceLibraryList.isEmpty()) {
            return new ApiSouOrderPriceCompareVO();
        }

        ApiSouOrderPriceCompareVO vo = new ApiSouOrderPriceCompareVO();

        Map<Long/* vendorId */, String/* vendorName */> vendorIdNames = companyInfos.stream()
                .filter(e -> Objects.nonNull(e.getCompanyId()) && Objects.nonNull(e.getCompanyName()))
                .collect(Collectors.toMap(CompanyInfo::getCompanyId, CompanyInfo::getCompanyName));

        Map<String/* vendorName */, List<PriceLibrary>> priceLibraryMap = new HashMap<>(32);
        priceLibraryList.forEach(priceLibrary ->
                priceLibraryMap.computeIfAbsent(vendorIdNames.get(priceLibrary.getVendorId()), k -> new ArrayList<>(128))
                        .add(priceLibrary)
        );
        // 排序
        priceLibraryMap.values().forEach(pls -> pls.sort(Comparator.comparing(PriceLibrary::getLastUpdateDate)));
        // 3. 填充数据(物料历史价格)
        if (!priceLibraryMap.isEmpty()) {
            vo.setPriceNodes(new HashMap<>(priceLibraryMap.size()));

            priceLibraryMap.forEach((vendorName, plList) -> {
                plList.forEach(pl -> {
                    SouOrderPriceCompareNode node = new SouOrderPriceCompareNode();
                    BigDecimal rate;
                    if (pl.getCurrencyCode().equals(standardCurrency)) {
                        rate = BigDecimal.ONE;
                    } else {
                        rate = rateMap.get(pl.getCurrencyCode());
                        if (rate == null) {
                            throw new IllegalArgumentException(MessageFormat.format("缺少[{0} -> {1}]的汇率", pl.getCurrencyCode(), standardCurrency));
                        }
                    }
                    node.setNotaxPrice(pl.getNotaxPrice().multiply(rate).setScale(10, RoundingMode.HALF_UP).stripTrailingZeros());
                    node.setTaxPrice(pl.getTaxPrice().multiply(rate).setScale(10, RoundingMode.HALF_UP).stripTrailingZeros());
                    node.setVendorCode(pl.getVendorCode());
                    node.setVendorName(pl.getVendorName());
                    node.setDate(pl.getLastUpdateDate());

                    vo.getPriceNodes()
                            .computeIfAbsent(vendorName, k -> new ArrayList<>(128))
                            .add(node);
                });
            });
        }

        return vo;
    }

}
