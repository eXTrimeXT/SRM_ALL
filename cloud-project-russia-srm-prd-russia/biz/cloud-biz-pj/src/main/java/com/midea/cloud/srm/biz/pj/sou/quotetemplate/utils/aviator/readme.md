aviator对于浮点运算，在不特殊标识的情况下，是简单使用double进行的。
如果需要进行精确计算，需要在数字后面标识"M"。
```
"1 / (1 - 0.01 * 100)" = double(Infinity)

"1 / (1 - 0.01M * 100)" // 抛出除零异常
```
PS: 如果采用变量的形式 "a * 100"，直接将变量定义为new BigDecimal(...)即可。

就核价业务来说，是需要精确运算的。而让用户在运算公式的数字中特别标识"M"是不现实的。
因此需要程序予以处理。目前思考到的处理方案为:
1. 修改aviator解析过程的源码，改成默认情况下使用BigDecimal进行数值运算。

因此有了`MyAviatorEvaluator`、`MyExpressionLexer`、
`MyExpressionParser`这三个自定义类。分别从`AviatorEvaluator`、`ExpressionLexer`、
`ExpressionParser`复制过来。目前唯一的改造点位于`MyExpressionLexer#scan`(第227行)。

使用方式与原来没有任何区别，只需要将 AviatorEvaluator替换为 MyAviatorEvaluator即可。