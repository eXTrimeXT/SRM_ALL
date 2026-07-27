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
public class MaxFunction extends AbstractFunction {

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        Object o = arg1.getValue(env);
        AssertUtils.notNull(o, "max的参数不能为null");
        if (o instanceof Collection) {
            if (((Collection) o).isEmpty()) {
                return new AviatorDecimal(BigDecimal.ZERO);
            } else {
                BigDecimal max = null;
                try {
                    for (Object o1 : ((Collection) o)) {
                        if (max == null) {
                            max = new BigDecimal(o1.toString());
                        } else {
                            BigDecimal val = new BigDecimal(o1.toString());
                            if (val.compareTo(max) > 0) {
                                max = val;
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("max参数异常，非数字");
                }
                if(max == null){
                    throw new IllegalArgumentException("max为空");
                }else {
                    return new AviatorDecimal(max.stripTrailingZeros());
                }
            }
        } else if (o instanceof BigDecimal) {
            return new AviatorDecimal((BigDecimal) o);
        } else {
            try {
                return new AviatorDecimal(new BigDecimal(o.toString()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("max参数异常，非数字");
            }
        }
    }

    @Override
    public AviatorObject throwArity(int n) {
        throw new IllegalArgumentException("max函数只支持传递一个参数");
    }

    @Override
    public String getName() {
        return "max";
    }

}
