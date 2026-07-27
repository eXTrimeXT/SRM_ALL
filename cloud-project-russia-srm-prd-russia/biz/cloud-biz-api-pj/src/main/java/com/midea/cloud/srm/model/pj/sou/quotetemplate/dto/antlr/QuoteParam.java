package com.midea.cloud.srm.model.pj.sou.quotetemplate.dto.antlr;

import lombok.Data;

/**
 * @author huangbf3
 */
@Data
public class QuoteParam {

    /** 键 */
    private String key;
    /** 值 */
    private String value;
    /** 符号(默认是=) */
    private QuoteParamType type = QuoteParamType.EQ;
}
