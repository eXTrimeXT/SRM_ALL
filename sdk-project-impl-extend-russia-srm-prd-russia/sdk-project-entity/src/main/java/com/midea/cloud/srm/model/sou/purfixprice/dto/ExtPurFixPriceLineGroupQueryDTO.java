package com.midea.cloud.srm.model.sou.purfixprice.dto;

import com.midea.cloud.srm.model.common.BasePage;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtPurFixPriceLineGroupQueryDTO extends BasePage {

    private Long projectId;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (getPageNum() == null) {
            throw new IllegalArgumentException("缺少pageNum参数");
        }
        if (getPageSize() == null) {
            throw new IllegalArgumentException("缺少pageSize参数");
        }
    }

}
