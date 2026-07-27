package com.midea.cloud.srm.model.pj.sou.quotetemplate.dto.antlr;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author huangbf3
 */
@Data
public class QuoteFormula {

    /** 可执行公式 */
    private String executableFormula;
    /** 元素集合 */
    private Map<String, QuoteFunction> functions = new HashMap<>(32);

}
