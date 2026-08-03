package com.midea.cloud.srm.biz.pj.sou.quotetemplate.utils.aviator.functions;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorDecimal;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.midea.cloud.common.utils.AssertUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-17
 */
public class MinFunction extends AbstractFunction {

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        Object o = arg1.getValue(env);
        AssertUtils.notNull(o, "min的参数不能为null");
        if (o instanceof Collection) {
            if (((Collection) o).isEmpty()) {
                return new AviatorDecimal(BigDecimal.ZERO);
            } else {
                BigDecimal min = null;
                try {
                    for (Object o1 : ((Collection) o)) {
                        if (min == null) {
                            min = new BigDecimal(o1.toString());
                        } else {
                            BigDecimal val = new BigDecimal(o1.toString());
                            if (val.compareTo(min) < 0) {
                                min = val;
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("min参数异常，非数字");
                }
                if(min == null){
                    throw new IllegalArgumentException("min为空");
                }else {
                    return new AviatorDecimal(min.stripTrailingZeros());
                }
            }
        } else if (o instanceof BigDecimal) {
            return new AviatorDecimal((BigDecimal) o);
        } else {
            try {
                return new AviatorDecimal(new BigDecimal(o.toString()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("min参数异常，非数字");
            }
        }
    }

    @Override
    public AviatorObject throwArity(int n) {
        throw new IllegalArgumentException("min函数只支持传递一个参数");
    }

    @Override
    public String getName() {
        return "min";
    }

}
