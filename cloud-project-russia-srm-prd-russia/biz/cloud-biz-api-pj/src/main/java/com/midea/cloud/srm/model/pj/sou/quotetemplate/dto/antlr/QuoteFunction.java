package com.midea.cloud.srm.model.pj.sou.quotetemplate.dto.antlr;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author huangbf3
 */
@Data
public class QuoteFunction {

    public static final String RETURN_KEY = "return";
    public static final String GLOBAL_KEY = "global";

    /** 变量名 */
    private String variable;
    /** 变量类型 */
    private QuoteFunctionType type;
    /** 名称 */
    private String name;
    /** 函数参数 */
    private List<QuoteParam> params = new ArrayList<>(8);
    /** 函数中包含的(变量 - 原值信息对比)/* var */
    private Map<String, QuoteFunctionVariable> variables = new HashMap<>(16);

    /**
     * 当 {@link #type} == REF 时，指实际可执行sql
     * 当 {@link #type} == FUN 时，指实际可发送的json数据
     */
    private String template;

    public QuoteParam getParamsByName(String name) {
        for (QuoteParam param : params) {
            if (param.getKey().equals(name)) {
                return param;
            }
        }
        return null;
    }

}
