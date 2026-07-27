package com.midea.cloud.srm.model.pj.pricetax.dto;

import com.midea.cloud.srm.model.pj.pricetax.entity.PriceRate;
import lombok.Data;

import java.util.List;

/**
 * @author huangbf3
 */
@Data
public class ResultInfoDto {

    /**页码 */
    private Integer page;

    /**总数 */
    private Integer total;

    /** 汇率的具体数据 */
    private List<PriceRate> rows;
}
