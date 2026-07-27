package com.midea.cloud.srm.model.pj.sou.quotetemplate.dto.antlr;

import com.midea.cloud.srm.model.pj.sou.quotetemplate.dto.antlr.QuoteFunctionVarType;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangbf3
 */
@Data
@NoArgsConstructor
public class QuoteFunctionVariable {

    /** 变量名 */
    private String varName;

    /** 原始名称 */
    private String originName;

    /** 变量类型 */
    private com.midea.cloud.srm.model.pj.sou.quotetemplate.dto.antlr.QuoteFunctionVarType type;

    public QuoteFunctionVariable(String varName, String originName, QuoteFunctionVarType type) {
        this.varName = varName;
        this.originName = originName;
        this.type = type;
    }

}
