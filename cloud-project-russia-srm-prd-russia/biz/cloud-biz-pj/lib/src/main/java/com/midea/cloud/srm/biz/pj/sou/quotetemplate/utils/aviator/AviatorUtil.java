package com.midea.cloud.srm.biz.pj.sou.quotetemplate.utils.aviator;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.utils.aviator.functions.*;

/**
 * 工具
 * @author: hesl41
 * @Date: 2022/9/23 20:09
 */
public class AviatorUtil {

    public static AviatorEvaluatorInstance getInstance(String script) {
        AviatorEvaluatorInstance instance = AviatorEvaluator.getInstance();
        //添加自定义函数
        instance.addFunction(new AvgFunction());
        instance.addFunction(new MaxFunction());
        instance.addFunction(new MinFunction());
        instance.addFunction(new SumFunction());
        instance.addFunction(new AvgFunction2());
        instance.addFunction(new MaxFunction2());
        instance.addFunction(new MinFunction2());
        instance.addFunction(new SumFunction2());
        // 语法校验
        instance.validate(script);
        return instance;
    }
}
