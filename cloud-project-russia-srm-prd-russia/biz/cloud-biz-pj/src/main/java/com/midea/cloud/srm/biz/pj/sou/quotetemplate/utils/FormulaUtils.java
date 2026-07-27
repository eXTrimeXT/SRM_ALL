package com.midea.cloud.srm.biz.pj.sou.quotetemplate.utils;

import com.googlecode.aviator.Expression;
import com.googlecode.aviator.LiteralExpression;
import com.googlecode.aviator.exception.ExpressionRuntimeException;
import com.googlecode.aviator.exception.ExpressionSyntaxErrorException;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.utils.aviator.AviatorUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.ParserContext;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.ast.*;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 本公式处理工具类主要采用 aviator
 *
 * @author zhangwk12@midea.com
 * @since 2022/03/31
 */
public class FormulaUtils {

    private FormulaUtils() {}

    /**
     * 公式中支持的逻辑运算符、关系运算符中英文对照
     * name-condition
     */
    private static final Map<String, String> CONDITION_MAP;
    private static final ExpressionParser SPEL_PARSER = new SpelExpressionParser();
    private static final ParserContext SPEL_PARSER_CONTEXT;
    /** 关系运算符集合 */
    private static final Set<String> RELATION_OPERATORS;
    /** 算术运算符集合 */
    private static final Set<String> ARITHMETIC_OPERATORS;

    private static final String NIL_TEXT = "nil";
    private static final String ZERO_TEXT = "by zero";
    static {
        Map<String/* name */, String/* condition */> tempMap = new LinkedHashMap<>(16);
//        放前面
        tempMap.put("小于等于", "<=");
//        放前面
        tempMap.put("大于等于", ">=");
//        放前面
        tempMap.put("不等于", "!=");
        tempMap.put("等于", "==");
        tempMap.put("大于", ">");
        tempMap.put("小于", "<");
        tempMap.put("并且", "&&");
        tempMap.put("或者", "||");
        tempMap.put("未填写", "== nil");
        tempMap.put("已填写", "!= nil");

        CONDITION_MAP = Collections.unmodifiableMap(tempMap);

        SPEL_PARSER_CONTEXT = new ParserContext() {
            @Override
            public boolean isTemplate() {
                return false;
            }

            @Override
            public String getExpressionPrefix() {
                return null;
            }

            @Override
            public String getExpressionSuffix() {
                return null;
            }
        };

        RELATION_OPERATORS = new HashSet<>(8);
        RELATION_OPERATORS.add(">");
        RELATION_OPERATORS.add(">=");
        RELATION_OPERATORS.add("<");
        RELATION_OPERATORS.add("<=");
        RELATION_OPERATORS.add("==");
        RELATION_OPERATORS.add("!=");
        RELATION_OPERATORS.add("&&");
        RELATION_OPERATORS.add("and");
        RELATION_OPERATORS.add("||");
        RELATION_OPERATORS.add("or");

        ARITHMETIC_OPERATORS = new HashSet<>(8);
        ARITHMETIC_OPERATORS.add("+");
        ARITHMETIC_OPERATORS.add("-");
        ARITHMETIC_OPERATORS.add("*");
        ARITHMETIC_OPERATORS.add("/");
        ARITHMETIC_OPERATORS.add("%");
    }

    /**
     * 去除应用条件、计算公式中多余的空格
     * PS: 注意，属性中如果存在空格，不能去掉。
     *     例如应用条件 "[螺丝钉 长度] > 1"  ---> "[螺丝钉 长度]>1"
     */
    public static String deleteWhitespace(String formula) {
        if (formula == null || formula.length() == 0) {
            return formula;
        }
        final int sz = formula.length();
        final char[] chs = new char[sz];
        int count = 0;
        int bracketCount = 0;
        boolean bracketChar = false;
        int spaceCount = 0;
        char c;
        for (int i = 0; i < sz; i++) {
            c = formula.charAt(i);
            switch (c) {
                case '[':
                    bracketCount++;
                    chs[count++] = c;
                    break;
                case ']':
                    bracketCount--;
                    chs[count++] = c;
                    if (spaceCount > 0) {
                        for (int j = count - 1; j >= count - spaceCount; j--) {
                            chs[j - 1] = chs[j];
                        }
                        count = count - spaceCount;
                    }
                    bracketChar = false;
                    spaceCount = 0;
                    break;
                default:
                    if (bracketCount <= 0) {
                        if (!Character.isWhitespace(c)) {
                            chs[count++] = c;
                        }
                    } else if (bracketCount % 2 == 1) {
                        // 在[]中
                        if (!Character.isWhitespace(c)) {
                            bracketChar = true;
                            spaceCount = 0;
                        }
                        if (bracketChar) {
                            if (Character.isWhitespace(c)) {
                                spaceCount++;
                            }
                            chs[count++] = c;
                        } else {
                            // 进入到[，但没碰到非空字符，因此略过空字符
                        }
                    } else {
                        chs[count++] = c;
                    }
                    break;
            }
        }
        if (count == sz) {
            return formula;
        }
        return new String(chs, 0, count);
    }

    /**
     * 根据表达式名称(中文)生成对应的表达式(公式)
     * PS: 不能获取前端传递的表达式(公式)，以防止由于前端错误导致名称与公式的"语义"出现差异
     * PS: fixed(修复) 如果公式本身含有字符串，那么字符串中的 "大于/小于" 不应被转换为 ">/<"
     * @param conditionName 表达式(名称)
     *                      例如 "( [属性1] 大于 5 并且 [属性1] 小于10 )"  ---> "( [属性1] > 5 && [属性1] <10 )"
     */
    public static String getFormulaFromConditionName(String conditionName) {
        if (StringUtils.isEmpty(conditionName)) {
            return null;
        }
        String conditionCode = conditionName;
        String text1 = "'";
        String text2 = "\"";
        if (conditionCode.contains(text1) || conditionCode.contains(text2)) {
            // 处理单/双引号，公式中定义的字符串不能替换
            String conditionFrag;
            String[] quoteArr = {text1, text2};
            for (String quote : quoteArr) {
                String[] conditionFrags = conditionCode.split(quote, -1);
                if (conditionFrags.length > 1) {
                    StringBuilder sb = new StringBuilder(conditionCode.length());
                    for (int i = 0; i < conditionFrags.length; i++) {
                        conditionFrag = conditionFrags[i];
                        // 单数可以直接替换，如果是双数，只有结尾那个可以替换
                        if (conditionFrag.length() > 0 && (i % 2 == 0 || i == conditionFrags.length - 1)) {
                            for (Map.Entry<String, String> entry : CONDITION_MAP.entrySet()) {
                                conditionFrag = conditionFrag.replaceAll(formatRegex(entry.getKey()), formatRegex(entry.getValue()));
                            }
                        }
                        sb.append(conditionFrag);
                        if (i < conditionFrags.length - 1) {
                            sb.append(quote);
                        }
                    }
                    conditionCode = sb.toString();
                }
            }
        } else {
            // 不包含引号，直接替换
            for (Map.Entry<String, String> entry : CONDITION_MAP.entrySet()) {
                conditionCode = conditionCode.replaceAll(formatRegex(entry.getKey()), formatRegex(entry.getValue()));
            }
        }

        return conditionCode;
    }

    /**
     * 简单校验公式格式
     * 1. 是否存在左右括号不匹配，例如 "( [属性1] * 1"
     * 2. 是否存在变量定义错误，例如 "[[属性1] * 1"、"属性1] * 1"
     * 3. 是否存在由于单引号、双引号使用不规范导致公式内字符串定义错误，例如 "[属性1] == 'a"
     * @param formula 需要校验的公式
     * @param errMsg 校验失败时的错误提示
     */
    public static void simpleValidateFormula(String formula, String errMsg) {
        if (formula == null || formula.length() == 0) { return ; }
        // 中括号数量
        int bracketCount = 0;
        // 大括号数量
        int curlyBraceCount = 0;
        // 单引号数量
        int singleQuoteCount = 0;
        // 双引号数量
        int doubleQuoteCount = 0;
        final int sz = formula.length();
        char c;
        for (int i = 0; i < sz; i++) {
            c = formula.charAt(i);
            switch (c) {
                case '[':
                    if (bracketCount <= 0) {
                        bracketCount++;
                    } else {
                        throw new IllegalArgumentException(errMsg + "变量请使用[...]定义");
                    }
                    break;
                case ']':
                    if (bracketCount > 0) {
                        bracketCount--;
                    } else {
                        throw new IllegalArgumentException(errMsg + "变量请使用[...]定义");
                    }
                    break;
                case '(':
                    curlyBraceCount++;
                    break;
                case ')':
                    if (curlyBraceCount > 0) {
                        curlyBraceCount--;
                    } else {
                        throw new IllegalArgumentException(errMsg + "存在多余的 \")\"");
                    }
                    break;
                case '）':
                case '（':
                    if (bracketCount % 2 == 1) {
                        // 属于变量名称中定义
                    } else {
                        throw new IllegalArgumentException(errMsg + "不能使用中文括号");
                    }
                case '\'':
                    if (singleQuoteCount > 0) {
                        singleQuoteCount--;
                    } else {
                        singleQuoteCount++;
                    }
                    break;
                case '"':
                    if (doubleQuoteCount > 0) {
                        doubleQuoteCount--;
                    } else {
                        doubleQuoteCount++;
                    }
                    break;
                default:
                    break;
            }
        }
        if (bracketCount > 0) {
            throw new IllegalArgumentException(errMsg + "变量请使用[...]定义");
        }
        if (curlyBraceCount > 0) {
            throw new IllegalArgumentException(errMsg + "存在多余的 \"(\"");
        }
        if (singleQuoteCount > 0) {
            throw new IllegalArgumentException(errMsg + "字符串定义错误，存在多余的单引号");
        }
        if (doubleQuoteCount > 0) {
            throw new IllegalArgumentException(errMsg + "字符串定义错误，存在多余的双引号");
        }
    }

    /**
     * 获取公式(应用条件、计算公式)中定义的变量
     * PS: 例如 "[属性1] * 100 * [属性2]"，返回 {"属性1", "属性2"}
     * @param formula 需要校验的公式
     * @param throwErr 当公式格式出现错误时，是否抛出异常(例如"[abc]] * 2")
     * @param formatErrMsg 当 throwErr == true 时，抛出的异常信息
     * @return 公式中定义的变量集合
     */
    public static Set<String> getFormulaAttr(String formula, boolean throwErr, @Nullable String formatErrMsg) {
        if(formatErrMsg == null){
            formatErrMsg = "";
        }
        Set<String> attrNames = new HashSet<>(8);
        StringBuilder sb = new StringBuilder(10);
        int index = -1;
        char c;
        String attr2;
        char[] arr = formula.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            c = arr[i];
            switch (c) {
                case '[':
                    if (index >= 0) {
                        // 存在"[...["格式错误
                        if (throwErr) {
                            throw new IllegalArgumentException(formatErrMsg);
                        } else {
                            sb.append(c);
                        }
                    } else {
                        sb.append(c);
                        index = i;
                    }
                    break;
                case ']':
                    if (index < 0) {
                        // 存在"...]"格式错误
                        if (throwErr) {
                            throw new IllegalArgumentException(formatErrMsg);
                        } else {
                            sb.append(c);
                        }
                    } else if (index == i - 1) {
                        // 存在"[]"，没实际变量，格式错误
                        if (throwErr) {
                            throw new IllegalArgumentException(formatErrMsg);
                        } else {
                            sb.append(c);
                        }
                    } else {
                        sb.append(c);
                        index = -1;

                        attr2 = sb.toString();
                        attrNames.add(attr2.substring(1, attr2.length() - 1));
                        sb.setLength(0);
                    }
                    break;
                default:
                    if (index >= 0) {
                        sb.append(c);
                    }
                    break;
            }
        }
        return attrNames;
    }

    /**
     * 检测条件表达式
     * @param formula 公式
     * @param formulaName 公式名称(可为空)
     * @param attrNameSet 公式中的变量集合，例如 {"属性1", "属性2"}
     * @param extraMsg 额外的错误信息
     */
    public static void analysisConditionFormula(final String formula,
                                                @Nullable String formulaName,
                                                Set<String> attrNameSet,
                                                @Nullable String extraMsg) {
        analysisFormula(formula, formulaName, attrNameSet, true, extraMsg);
    }

    /**
     * 检测条件表达式(增强版)
     * PS: 增加了对日期类型的处理
     * PS: 对于日期、文本、数值类型的变量，进一步校验它们所应用的操作符是否恰当
     * @param formula 公式("[a] > 1")
     * @param formulaName 公式名称
     * @param attrNameSet2 所有的可用变量
     * @param textAttrSet2 文本类型变量
     * @param decimalAttrSet2 数值类型变量
     * @param dateAttrSet2 日期类型变量
     * @param dateArgMap 容器，存储条件公式分析过程中字符串日期值转化的变量
     * @param nilSet 容器，存储条件公式分析过程中存在判空的变量
     * @param dateTimeFormatter 日期转换处理(需要指明处理的日期格式: "yyyy-MM-dd" or "yyyy-MM-dd HH:mm:ss" 等)
     * @param extraMsg 额外的错误信息
     */
    public static String analysisConditionFormulaPlus(final String formula,
                                                      @Nullable String formulaName,
                                                      Set<String> attrNameSet2,
                                                      Set<String> textAttrSet2,
                                                      Set<String> decimalAttrSet2,
                                                      Set<String> dateAttrSet2,
                                                      Map<String/* argName */, Date> dateArgMap, // 将字符串日期转换为变量
                                                      Set<String> nilSet, // 条件公式中 == nil 的变量
                                                      Set<String> noNilSet, // 条件公式中 != nil 的变量
                                                      DateTimeFormatter dateTimeFormatter,
                                                      @Nullable String extraMsg) {
        String formulaName2 = formulaName != null ? formulaName : formula;
        String extraMsg2 = extraMsg != null ? extraMsg : "";

        Set<String> attrNameSet = new HashSet<>(attrNameSet2);
        Set<String> textAttrSet = new HashSet<>(textAttrSet2);
        Set<String> decimalAttrSet = new HashSet<>(decimalAttrSet2);
        Set<String> dateAttrSet = new HashSet<>(dateAttrSet2);

        // 1. "[xx]"变量替换为可解析的实际公式变量"a,b,c"
        Map<String/* argName */, String/* attrName */> argAttrMap = new HashMap<>(attrNameSet.size() + attrNameSet.size() >>> 1 + 1);
        String formatFormula = formula;
        String arg;
        int index = 0;
        for (String attr : attrNameSet) {
            arg = "java_Args" + index++;
            argAttrMap.put(arg, attr);
            if (textAttrSet.contains(attr)) { textAttrSet.add(arg); }
            if (decimalAttrSet.contains(attr)) { decimalAttrSet.add(arg); }
            if (dateAttrSet.contains(attr)) { dateAttrSet.add(arg); }

            formatFormula = formatFormula.replaceAll(formatRegex("[" + attr + "]"), formatRegex(arg));
        }

        // 2. 采用spel解析(针对动态表达式)
        try {
            org.springframework.expression.Expression expression = SPEL_PARSER.parseExpression(formatFormula, SPEL_PARSER_CONTEXT);
            if (expression instanceof SpelExpression) {
                SpelNode root = ((SpelExpression) expression).getAST();
                if (root instanceof Operator) {
                    String operator = ((Operator) root).getOperatorName();
                    // 条件表达式
                    AssertUtils.isTrue(RELATION_OPERATORS.contains(operator), "非条件表达式");
                    // 进一步校验条件表达式(日期、文本、数值所应用的操作符是否恰当)
                    doAnalysisTreeNodes(expression, root, argAttrMap, textAttrSet, decimalAttrSet, dateAttrSet, dateArgMap, nilSet, noNilSet,
                            new AtomicInteger(0), dateTimeFormatter, extraMsg);
                    formatFormula = root.toStringAST();
                    // 处理and/or/
                    formatFormula = formatFormula.replaceAll("and", "&&");
                    formatFormula = formatFormula.replaceAll("or", "||");
                } else if (root instanceof PropertyOrFieldReference) {
                    // 说明该公式仅是简单的 "[属性1]" (仅在计算表达式中才是合法的)
                }
            }
        } catch (ParseException e) {
            // 目前核价模块功能只用到了算术、逻辑、关系运算符。就这三者来说，aviator/spel在绝大部分情况下解析结果都是一样的。
            // 如果出现这个报错，而前面aviator编译环节没有报错，很可能是使用了aviator内置函数等高级功能。针对性调整该处的代码即可。
            // 解析失败，再次使用aviator进行解析
            throw new IllegalArgumentException("公式解析失败:" + formulaName2, e);
        }

        // 3. 采用aviator解析
        try {
            Expression expression = AviatorUtil.getInstance(formatFormula).compile(formatFormula);
            if (expression instanceof LiteralExpression) {
                // 文本表达式(固定值)
                Object result = expression.execute();
                // 条件表达式
                if (result instanceof Boolean) {
                    if (!(boolean)result) {
                        throw new IllegalArgumentException(MessageFormat.format("{0}【{1}】不存在满足的情况", extraMsg2, formulaName2));
                    }
                }
            } else {
                // 获取变量名称
                List<String> attrList = expression.getVariableNames();
                // 判断是否变量都是已定义的(存在于成员属性中)
                if (!attrList.isEmpty()) {
                    attrList.forEach(attr -> {
                        if (!NIL_TEXT.equals(attr) && !argAttrMap.containsKey(attr) && !dateArgMap.containsKey(attr)) {
                            throw new IllegalArgumentException(MessageFormat.format("{0}【{1}】存在未定义的变量:{2}",
                                    extraMsg2, formulaName2, attr));
                        }
                    });
                }
            }
        } catch (ExpressionSyntaxErrorException e) {
            // 存在语法错误(静态判断)
            throw new IllegalArgumentException(MessageFormat.format("{0}解析失败,存在语法错误:{1}", extraMsg2, formulaName2), e);
        } catch (ExpressionRuntimeException e) {
            // 表达式运行时错误
            throw new IllegalArgumentException(MessageFormat.format("{0}运行失败:{1}", extraMsg2, formulaName2), e);
        } catch (ArithmeticException e) {
            String message;
            // 算术异常
            if (e.getMessage().contains(ZERO_TEXT)) {
                message = extraMsg2 + "语法错误,0不能作为分母:" + formulaName2;
            } else {
                message = extraMsg2 + "公式运行失败:" + formulaName2;
            }
            throw new IllegalArgumentException(message, e);
        }
        for (Map.Entry<String/* argName */, String/* attrName */> entry : argAttrMap.entrySet()) {
            formatFormula = formatFormula.replaceAll(formatRegex(entry.getKey()), formatRegex("[" + entry.getValue() + "]"));
        }
        return formatFormula;
    }

    public static String formatRegex(String regex) {
        StringBuilder sb = new StringBuilder(30);
        for (char c : regex.toCharArray()) {
            switch (c) {
                case '$':
                case '(':
                case ')':
                case '\\':
                case '^':
                case '[':
                case ']':
                case '*':
                case '+':
                case '?':
                case '{':
                case '}':
                case '-':
                case '|':
                case '.':
                    sb.append("\\");
                    sb.append(c);
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }

    /**
     * 分析spring SPEL AST树结构，确保日期、文本、数值类型的变量所应用的操作符是恰当的
     */
    private static void doAnalysisTreeNodes(org.springframework.expression.Expression expression,
                                            SpelNode node,
                                            Map<String/* arg */, String/* attr */> argAttrMap,
                                            Set<String> textArgSet,
                                            Set<String> decimalArgSet,
                                            Set<String> dateArgSet,
                                            Map<String/* argName */, Date> dateArgMap, // 将字符串日期转换为变量
                                            Set<String> nilArgSet, // 条件公式中 == nil 的变量
                                            Set<String> noNilArgSet, // 条件公式中 != nil 的变量
                                            AtomicInteger argNameIndex,
                                            DateTimeFormatter dateTimeFormatter,
                                            @Nullable String extraMsg) {
        ExpressionState state = new ExpressionState(((SpelExpression) expression).getEvaluationContext());

        if (node instanceof PropertyOrFieldReference) {
            // 属性/字段引用
        } else if (node instanceof MethodReference) {
            // min/max/sum/avg
            String method = ((MethodReference) node).getName();
            AssertUtils.isTrue("min".equalsIgnoreCase(method) || "max".equalsIgnoreCase(method)
                    || "sum".equalsIgnoreCase(method) || "avg".equalsIgnoreCase(method), "目前仅支持max/min/sum/avg等函数");
            AssertUtils.isTrue(node.getChildCount() == 2, LocaleHandler.getLocaleMsg("{0}...{1}...符号定义错误"), extraMsg, ((Operator) node).getOperatorName());
            for (int i = 0; i < node.getChildCount(); i++) {
                SpelNode node1 = node.getChild(i);
                doAnalysisTreeNodes(expression, node1, argAttrMap, textArgSet, decimalArgSet, dateArgSet, dateArgMap, nilArgSet, noNilArgSet, argNameIndex, dateTimeFormatter, extraMsg);
            }
        }  else if (node instanceof Operator) {
            // 操作符
            if (node instanceof OpOr || node instanceof OpAnd) {
                // or、and
                AssertUtils.isTrue(node.getChildCount() == 2, LocaleHandler.getLocaleMsg("{0}...{1}...符号定义错误"), extraMsg, ((Operator) node).getOperatorName());
                SpelNode node1 = node.getChild(0);
                SpelNode node2 = node.getChild(1);
                doAnalysisTreeNodes(expression, node1, argAttrMap, textArgSet, decimalArgSet, dateArgSet, dateArgMap, nilArgSet, noNilArgSet, argNameIndex, dateTimeFormatter, extraMsg);
                doAnalysisTreeNodes(expression, node2, argAttrMap, textArgSet, decimalArgSet, dateArgSet, dateArgMap, nilArgSet, noNilArgSet, argNameIndex, dateTimeFormatter, extraMsg);
            } else if (node instanceof OperatorBetween || node instanceof OperatorMatches || node instanceof OperatorInstanceof) {
                // between、instanceof(不支持的操作符)
                throw new IllegalArgumentException(MessageFormat.format("{0}公式中存在不支持的操作符:{1}", extraMsg, ((Operator) node).getOperatorName()));
            } else if (node instanceof OperatorPower || node instanceof OpMinus || node instanceof OpMultiply || node instanceof OpDec
                    || node instanceof OpDivide || node instanceof OpPlus || node instanceof OpInc || node instanceof OpModulus) {
                // ^、-、*、--、/、+、++、%
                int count = node.getChildCount();
                for (int i = 0; i < count; i++) {
                    SpelNode nodeI = node.getChild(i);
                    if (nodeI instanceof PropertyOrFieldReference) {
                        String nodeIname = ((PropertyOrFieldReference) nodeI).getName();
                        AssertUtils.isFalse(textArgSet.contains(nodeIname), LocaleHandler.getLocaleMsg("{0}文本类型的变量不能用于 {1} 符号计算"), extraMsg, nodeIname);
                        AssertUtils.isFalse(dateArgSet.contains(nodeIname), LocaleHandler.getLocaleMsg("{0}日期类型的变量不能用于 {1} 符号计算"), extraMsg, nodeIname);
                    } else if (nodeI instanceof Literal) {
                        // TODO
                    }
                    doAnalysisTreeNodes(expression, nodeI, argAttrMap, textArgSet, decimalArgSet, dateArgSet, dateArgMap, nilArgSet, noNilArgSet, argNameIndex, dateTimeFormatter, extraMsg);
                }
            } else if (node instanceof OpEQ || node instanceof OpNE || node instanceof OpGT || node instanceof OpGE
                    || node instanceof OpLT || node instanceof OpLE) {
                // ==、!=、>、>=、<、<= (只会有两个参数)
                AssertUtils.isTrue(node.getChildCount() == 2, LocaleHandler.getLocaleMsg("{0}...{1}...符号定义错误"), extraMsg, ((Operator) node).getOperatorName());
                SpelNode node1 = node.getChild(0);
                SpelNode node2 = node.getChild(1);
                if (node1 instanceof PropertyOrFieldReference && node2 instanceof PropertyOrFieldReference) {
                    // 两个参数都是变量

                    // 搜集判非空的变量"a == nil"、"a != nil"
                    boolean needCheckNilProp = (node instanceof OpEQ || node instanceof OpNE)
                            && (NIL_TEXT.equals(((PropertyOrFieldReference) node1).getName()) || NIL_TEXT.equals(((PropertyOrFieldReference) node2).getName()));
                    if (needCheckNilProp) {
                        if (NIL_TEXT.equals(((PropertyOrFieldReference) node1).getName())) {
                            if (node instanceof OpEQ) {
                                nilArgSet.add(argAttrMap.get(((PropertyOrFieldReference) node2).getName()));
                            } else {
                                noNilArgSet.add(argAttrMap.get(((PropertyOrFieldReference) node2).getName()));
                            }
                        } else {
                            if (node instanceof OpEQ) {
                                nilArgSet.add(argAttrMap.get(((PropertyOrFieldReference) node1).getName()));
                            } else {
                                noNilArgSet.add(argAttrMap.get(((PropertyOrFieldReference) node1).getName()));
                            }
                        }
                    }

                    // 如果有一个变量是日期类型，需要确保另外一个变量也是日期类型
                    boolean needCheckDateProp = dateArgSet.contains(((PropertyOrFieldReference) node1).getName())
                            || dateArgSet.contains(((PropertyOrFieldReference) node2).getName());
                    if (!needCheckNilProp && needCheckDateProp) {
                        if (dateArgSet.contains(((PropertyOrFieldReference) node1).getName())) {
                            AssertUtils.isTrue(dateArgSet.contains(((PropertyOrFieldReference) node2).getName()),
                                    "{0}[{1}] {2} [{3}]定义错误: 日期类型的变量不能与非日期的变量进行比较", extraMsg,
                                    argAttrMap.get(((PropertyOrFieldReference) node1).getName()),
                                    ((Operator) node).getOperatorName(),
                                    argAttrMap.get(((PropertyOrFieldReference) node2).getName()));
                        } else {
                            AssertUtils.isTrue(dateArgSet.contains(((PropertyOrFieldReference) node1).getName()),
                                    "{0}[{1}] {2} [{3}]定义错误: 日期类型的变量不能与非日期的变量进行比较", extraMsg,
                                    argAttrMap.get(((PropertyOrFieldReference) node1).getName()),
                                    ((Operator) node).getOperatorName(),
                                    argAttrMap.get(((PropertyOrFieldReference) node2).getName()));
                        }
                    }
                    // 如果有一个变量是数字类型，需要确保另外一个变量也是数字类型
                    boolean needCheckDecimalProp = decimalArgSet.contains(((PropertyOrFieldReference) node1).getName())
                            || decimalArgSet.contains(((PropertyOrFieldReference) node2).getName());
                    if (!needCheckNilProp && needCheckDecimalProp) {
                        if (decimalArgSet.contains(((PropertyOrFieldReference) node1).getName())) {
                            AssertUtils.isTrue(decimalArgSet.contains(((PropertyOrFieldReference) node2).getName()),
                                    "{0}[{1}] {2} [{3}]定义错误: 数字类型的变量不能与非数字的变量进行比较", extraMsg,
                                    argAttrMap.get(((PropertyOrFieldReference) node1).getName()),
                                    ((Operator) node).getOperatorName(),
                                    argAttrMap.get(((PropertyOrFieldReference) node2).getName()));
                        } else {
                            AssertUtils.isTrue(decimalArgSet.contains(((PropertyOrFieldReference) node1).getName()),
                                    "{0}[{1}] {2} [{3}]定义错误: 数字类型的变量不能与非数字的变量进行比较", extraMsg,
                                    argAttrMap.get(((PropertyOrFieldReference) node1).getName()),
                                    ((Operator) node).getOperatorName(),
                                    argAttrMap.get(((PropertyOrFieldReference) node2).getName()));
                        }
                    }
                    // 能走到这里，基本两个变量都是文本了
                    boolean needCheckTextProp = textArgSet.contains(((PropertyOrFieldReference) node1).getName())
                            && textArgSet.contains(((PropertyOrFieldReference) node2).getName());
                    if (needCheckTextProp) {
                        AssertUtils.isTrue(node instanceof OpEQ || node instanceof OpNE, LocaleHandler.getLocaleMsg("{0}[{1}]{2}[{3}]定义错误: 文本类型比较只能使用==、!="),
                                extraMsg,
                                argAttrMap.get(((PropertyOrFieldReference) node1).getName()),
                                ((Operator) node).getOperatorName(),
                                argAttrMap.get(((PropertyOrFieldReference) node2).getName()));
                    }

                } else if (!(node1 instanceof PropertyOrFieldReference) && !(node2 instanceof PropertyOrFieldReference)) {
                    // 两个参数都不是变量(不作判断处理)
                    if (node1 instanceof StringLiteral) {
                        // 字符串常量
                        AssertUtils.isTrue(node2 instanceof StringLiteral, LocaleHandler.getLocaleMsg("{0}{1} {2} {3}定义错误: 文本类型只能与文本类型进行比较"), extraMsg,
                                node1.toStringAST(), ((Operator) node).getOperatorName(), node2.toStringAST());
                        AssertUtils.isTrue(node instanceof OpEQ || node instanceof OpNE, LocaleHandler.getLocaleMsg("{0}{1} {2} {3}定义错误: 文本类型比较只能使用==、!="),
                                extraMsg, node1.toStringAST(), ((Operator) node).getOperatorName(), node2.toStringAST());
                    } else if (node1 instanceof FloatLiteral || node1 instanceof IntLiteral || node1 instanceof LongLiteral) {
                        // 数字常量
                        AssertUtils.isTrue(node2 instanceof FloatLiteral || node2 instanceof IntLiteral || node2 instanceof LongLiteral,
                                "{0}{1} {2} {3}定义错误: 数字类型只能与数字类型进行比较", extraMsg,
                                node1.toStringAST(), ((Operator) node).getOperatorName(), node2.toStringAST());
                    } else if (node1 instanceof BooleanLiteral) {
                        // 布尔常量
                        AssertUtils.isTrue(node2 instanceof BooleanLiteral, LocaleHandler.getLocaleMsg("{0}{1} {2} {3}定义错误: 布尔类型只能与布尔类型进行比较"), extraMsg,
                                node1.toStringAST(), ((Operator) node).getOperatorName(), node2.toStringAST());
                        AssertUtils.isTrue(node instanceof OpEQ || node instanceof OpNE, LocaleHandler.getLocaleMsg("{0}{1} {2} {3}定义错误: 布尔类型比较只能使用==、!="),
                                extraMsg, node1.toStringAST(), ((Operator) node).getOperatorName(), node2.toStringAST());
                    }
                } else {
                    // 其中一个参数是变量，另外一个参数不是变量

                    // 1: 不同类型的变量，可用的操作符范围不同
                    if (node1 instanceof PropertyOrFieldReference) {
                        // 第一个参数是变量
                        if (textArgSet.contains(((PropertyOrFieldReference) node1).getName())) {
                            // 文本类型变量
                            AssertUtils.isTrue(node instanceof OpEQ || node instanceof OpNE, LocaleHandler.getLocaleMsg("{0}[{1}] {2} {3}定义错误: 文本类型比较只能使用==、!="),
                                    extraMsg,
                                    argAttrMap.get(((PropertyOrFieldReference) node1).getName()),
                                    ((Operator) node).getOperatorName(),
                                    node2.toStringAST());
                            AssertUtils.isTrue(node2 instanceof StringLiteral, LocaleHandler.getLocaleMsg("{0}[{1}] {2} {3}定义错误: 文本类型变量不能与非文本进行比较"), extraMsg,
                                    argAttrMap.get(((PropertyOrFieldReference) node1).getName()),
                                    ((Operator) node).getOperatorName(),
                                    node2.toStringAST());
                        } else if (decimalArgSet.contains(((PropertyOrFieldReference) node1).getName())) {
                            // 数值类型
                            if (node2 instanceof RealLiteral) {
                                TypedValue value = node2.getTypedValue(state);
                                AssertUtils.isTrue(value.getValue() instanceof Number,
                                        "{0}[{1}] {2} {3}定义错误: 数值类型变量不能与非数字进行比较", extraMsg,
                                        argAttrMap.get(((PropertyOrFieldReference) node1).getName()),
                                        ((Operator) node).getOperatorName(),
                                        node2.toStringAST());
                            } else {
                                AssertUtils.isTrue(node2 instanceof FloatLiteral || node2 instanceof IntLiteral || node2 instanceof LongLiteral
                                                || node2 instanceof OpMinus/* 即负数 */,
                                        "{0}[{1}] {2} {3}定义错误: 数值类型变量不能与非数字进行比较", extraMsg,
                                        argAttrMap.get(((PropertyOrFieldReference) node1).getName()),
                                        ((Operator) node).getOperatorName(),
                                        node2.toStringAST());
                            }
                        } else if (dateArgSet.contains(((PropertyOrFieldReference) node1).getName())) {
                            // 日期类型(略 - 后面处理)
                        }
                    } else {
                        // 第二个参数是变量
                        if (textArgSet.contains(((PropertyOrFieldReference) node2).getName())) {
                            // 文本类型变量
                            AssertUtils.isTrue(node instanceof OpEQ || node instanceof OpNE, LocaleHandler.getLocaleMsg("{0}{1} {2} [{3}]定义错误: 文本类型比较只能使用==、!="),
                                    extraMsg,
                                    node1.toStringAST(),
                                    ((Operator) node).getOperatorName(),
                                    argAttrMap.get(((PropertyOrFieldReference) node2).getName()));
                        } else if (decimalArgSet.contains(((PropertyOrFieldReference) node2).getName())) {
                            // 数值类型
                            AssertUtils.isTrue(node1 instanceof FloatLiteral || node1 instanceof IntLiteral || node1 instanceof LongLiteral,
                                    "{0}{1} {2} [{3}]定义错误: 数值类型变量不能与非数字进行比较", extraMsg,
                                    node1.toStringAST(),
                                    ((Operator) node).getOperatorName(),
                                    argAttrMap.get(((PropertyOrFieldReference) node2).getName()));
                        } else if (dateArgSet.contains(((PropertyOrFieldReference) node2).getName())) {
                            // 日期类型(略 - 后面处理)
                        }
                    }

                    // 2: 日期类型的变量处理(常量必须是日期文本/nil)
                    boolean node1Date = (node1 instanceof PropertyOrFieldReference) && dateArgSet.contains(((PropertyOrFieldReference) node1).getName());
                    boolean node2Date = (node2 instanceof PropertyOrFieldReference) && dateArgSet.contains(((PropertyOrFieldReference) node2).getName());
                    if (node1Date) {
                        // 第一个参数是日期类型，第二个参数是常量
                        AssertUtils.isTrue(node2 instanceof StringLiteral, LocaleHandler.getLocaleMsg("{0}[{1}] {2} {3}定义错误: 日期类型变量[{1}]不能用于此操作"), extraMsg,
                                argAttrMap.get(((PropertyOrFieldReference) node1).getName()),
                                ((Operator) node).getOperatorName(),
                                node2.toStringAST());
                        String dateStr = StringUtils.trimToNull(node2.getTypedValue(state).getValue().toString());
                        AssertUtils.notNull(dateStr, LocaleHandler.getLocaleMsg("{0}[{1}] {2} {3}定义错误: 日期类型变量{1}不能用于此操作"), extraMsg,
                                argAttrMap.get(((PropertyOrFieldReference) node1).getName()),
                                ((Operator) node).getOperatorName(),
                                node2.toStringAST());
                        try {
                            String key = "java_date_" + argNameIndex.addAndGet(1);
                            dateArgMap.put(key,
                                    Date.from(LocalDateTime.parse(dateStr, dateTimeFormatter).atZone(ZoneId.systemDefault()).toInstant()));
                            // 替换文本节点为变量节点
                            SpelNodeReplaceWrapper.build(node).replaceChildNode(1, new PropertyOrFieldReference(false, key, 0, 0));
                        } catch (DateTimeParseException e) {
                            throw new IllegalArgumentException(MessageFormat.format("{0}[{1}] {2} {3}定义错误: {3}不是合法的日期格式数据", extraMsg,
                                    argAttrMap.get(((PropertyOrFieldReference) node1).getName()),
                                    ((Operator) node).getOperatorName(),
                                    node2.toStringAST()));
                        }
                    } else if (node2Date) {
                        // 第二个参数是日期类型，第一个参数是常量
                        AssertUtils.isTrue(node1 instanceof StringLiteral, LocaleHandler.getLocaleMsg("{0}{1} {2} [{3}]定义错误: 日期类型变量{3}不能用于此操作"), extraMsg,
                                node1.toStringAST(), ((Operator) node).getOperatorName(), ((PropertyOrFieldReference) node2).getName());
                        String dateStr = StringUtils.trimToNull(((StringLiteral) node1).getOriginalValue());
                        AssertUtils.notNull(dateStr, LocaleHandler.getLocaleMsg("{0}{1} {2} [{3}]定义错误: 日期类型变量{3}不能用于此操作"), extraMsg,
                                node1.toStringAST(), ((Operator) node).getOperatorName(),
                                argAttrMap.get(((PropertyOrFieldReference) node2).getName()));
                        try {
                            String key = "java_date_" + argNameIndex.addAndGet(1);
                            dateArgMap.put(key,
                                    Date.from(LocalDateTime.from(dateTimeFormatter.parse(dateStr)).atZone(ZoneId.systemDefault()).toInstant()));
                            // 替换文本节点为变量节点
                            SpelNodeReplaceWrapper.build(node).replaceChildNode(0, new PropertyOrFieldReference(false, key, 0, 0));
                        } catch (DateTimeParseException e) {
                            throw new IllegalArgumentException(MessageFormat.format("{0}{1} {2} [{3}]定义错误: {1}不是合法的日期格式数据", extraMsg,
                                    node1.toStringAST(),
                                    ((Operator) node).getOperatorName(),
                                    argAttrMap.get(((PropertyOrFieldReference) node2).getName())));
                        }
                    }
                }
                doAnalysisTreeNodes(expression, node1, argAttrMap, textArgSet, decimalArgSet, dateArgSet, dateArgMap, nilArgSet, noNilArgSet, argNameIndex, dateTimeFormatter, extraMsg);
                doAnalysisTreeNodes(expression, node2, argAttrMap, textArgSet, decimalArgSet, dateArgSet, dateArgMap, nilArgSet, noNilArgSet, argNameIndex, dateTimeFormatter, extraMsg);
            } else {
                throw new IllegalArgumentException(MessageFormat.format("{0}无法识别的公式操作符:{1}", extraMsg, ((Operator) node).getOperatorName()));
            }
        } else if (node instanceof Literal) {
            // 常量节点(字符串、布尔、数字等)
        } else {
            // 不做判断了
            throw new IllegalArgumentException(MessageFormat.format("{0}公式中存在不支持的节点信息[{1}]", extraMsg, node.toString()));
        }
    }

    /**
     * 检测计算表达式
     * @param formula 公式
     * @param formulaName 公式名称(可为空)
     * @param attrNameSet 公式中的变量集合，例如 {"属性1", "属性2"}
     * @param extraMsg 额外的错误信息
     */
    public static void analysisComputeFormula(final String formula,
                                              @Nullable String formulaName,
                                              Set<String> attrNameSet,
                                              @Nullable String extraMsg) {
        analysisFormula(formula, formulaName, attrNameSet, false, extraMsg);
    }

    /**
     * 核价中用到的公式就包括两种:
     * 1. 条件表达式(返回值为true/false)
     * 2. 计算表达式(返回值为数字类型)
     * 该方法输入一个给定的表达式，验证是否为条件/计算表达式 。
     *
     * @param formula 需要验证的表达式，例如 "[属性1] * 1"
     * @param formulaName 表达式名称，用于错误提示
     * @param attrNameSet 公式中的变量集合，例如 {"属性1", "属性2"}
     * @param isConditionFormula true-条件表达式、false-计算表达式
     * @param extraMsg 额外的错误信息
     */
    private static void analysisFormula(final String formula,
                                        @Nullable final String formulaName,
                                        Set<String> attrNameSet,
                                        boolean isConditionFormula,
                                        @Nullable String extraMsg) {
        String formulaName2 = formulaName != null ? formulaName : formula;
        String extraMsg2 = extraMsg != null ? extraMsg : "";

        // 1. "[xx]"变量替换为可解析的实际公式变量"a,b,c"
        Set<String> argNames = new HashSet<>(attrNameSet.size() + attrNameSet.size() >>> 1 + 1);
        String formatFormula = formula;
        String arg;
        int index = 0;
        for (String attr : attrNameSet) {
            arg = "java_Args" + index++;
            argNames.add(arg);

            formatFormula = formatFormula.replaceAll(formatRegex("[" + attr + "]"), formatRegex(arg));
        }

        // 2. 采用aviator解析
        boolean hasAnalysis = false;
        try {
            Expression expression = AviatorUtil.getInstance(formatFormula).compile(formatFormula);
            if (expression instanceof LiteralExpression) {
                // 文本表达式(固定值)
                Object result = expression.execute();
                if (isConditionFormula) {
                    // 条件表达式
                    if (result instanceof Boolean) {
                        if (!(boolean)result) {
                            throw new IllegalArgumentException(MessageFormat.format("{0}【{1}】不存在满足的情况", extraMsg2, formulaName2));
                        } else {
                            hasAnalysis = true;
                        }
                    }
                } else {
                    // 计算表达式
                    hasAnalysis = result instanceof Number;
                }
            } else {
                // 获取变量名称
                List<String> attrList = expression.getVariableNames();
                // 判断是否变量都是已定义的(存在于成员属性中)
                if (attrList != null && !attrList.isEmpty()) {
                    attrList.forEach(attr -> {
                        if (!argNames.contains(attr)) {
                            throw new IllegalArgumentException(MessageFormat.format("{0}【{1}】存在未定义的变量:{2}",
                                    extraMsg2, formulaName2, attr));
                        }
                    });
                }
            }
        } catch (ExpressionSyntaxErrorException e) {
            // 存在语法错误(静态判断)
            throw new IllegalArgumentException(MessageFormat.format("{0}解析失败,存在语法错误:{1}", extraMsg2, formulaName2), e);
        } catch (ExpressionRuntimeException e) {
            // 表达式运行时错误
            throw new IllegalArgumentException(MessageFormat.format("{0}运行失败:{1}", extraMsg2, formulaName2), e);
        } catch (ArithmeticException e) {
            String message;
            // 算术异常
            if (e.getMessage().contains(ZERO_TEXT)) {
                message = extraMsg2 + "语法错误,0不能作为分母:" + formulaName2;
            } else {
                message = extraMsg2 + "公式运行失败:" + formulaName2;
            }
            throw new IllegalArgumentException(message, e);
        }

        // 3. 采用spel解析(针对动态表达式)
        if (!hasAnalysis) {
            try {
                org.springframework.expression.Expression expression = SPEL_PARSER.parseExpression(formatFormula, SPEL_PARSER_CONTEXT);
                if (expression instanceof SpelExpression) {
                    SpelNode root = ((SpelExpression) expression).getAST();
                    if (root instanceof Operator) {
                        String operator = ((Operator) root).getOperatorName();
                        if (isConditionFormula) {
                            // 条件表达式
                            hasAnalysis = RELATION_OPERATORS.contains(operator);
                        } else {
                            // 计算表达式
                            hasAnalysis = ARITHMETIC_OPERATORS.contains(operator);
                        }
                    } else if (root instanceof PropertyOrFieldReference) {
                        // 说明该公式仅是简单的 "[属性1]" (仅在计算表达式中才是合法的)
                        hasAnalysis = !isConditionFormula;
                    } else if (root instanceof MethodReference) {
                        String method = ((MethodReference) root).getName();
                        hasAnalysis = "max".equalsIgnoreCase(method) || "min".equalsIgnoreCase(method)
                                || "sum".equalsIgnoreCase(method) || "avg".equalsIgnoreCase(method);
                    }
                }
            } catch (ParseException e) {
                // 目前核价模块功能只用到了算术、逻辑、关系运算符。就这三者来说，aviator/spel在绝大部分情况下解析结果都是一样的。
                // 如果出现这个报错，而前面aviator编译环节没有报错，很可能是使用了aviator内置函数等高级功能。针对性调整该处的代码即可。
                throw new IllegalArgumentException("公式解析失败:" + formulaName2, e);
            }
        }
        if (!hasAnalysis) {
            // 要么要素表达式非常特殊，要么就根本不是条件表达式
            throw new IllegalArgumentException((isConditionFormula ? "公式非条件表达式:" : "公式非计算表达式:") + formulaName2);
        }
    }

    /**
     * 执行条件表达式
     *      * PS: 该方法中不对公式做校验了。仅考虑以下情况:
     *      *     1. 用户没有填写相关变量的值
     *      *     2. 公式执行异常(出现情况极少)
     *      *     3. 结果类型转换异常(出现情况极少)
     * @param originalFormula 需要执行的条件表达式
     * @param attrValueMap 用户输入的属性值集合
     * @param attrArgNameMap 属性名与可执行变量名的映射关系
     * @param noArgsErrMsg 当表达式的执行需要某个属性值，但用户未填写时的报错信息
     * @param executeErrMsg 表达式执行失败时的报错信息
     * @param executeZeroErrMsg 执行时发生除零异常时的报错信息
     * @param resultTypeErrMsg 表达式执行结果转boolean异常时的报错信息
     */
    public static boolean executeConditionFormula(final String originalFormula,
                                                  Map<String/* attrName */, Object/* attrValue */> attrValueMap,
                                                  Map<String/* attrName */, String/* argName */> attrArgNameMap,
                                                  String noArgsErrMsg,
                                                  String executeErrMsg,
                                                  String executeZeroErrMsg,
                                                  String resultTypeErrMsg) {

        String formula = deleteWhitespace(originalFormula);
        // 1. 确保公式中不存在未赋值的变量(需要提醒用户，给某某变量赋值)
        String attr;
        int index;
        for (Map.Entry<String/* attrName */, String/* argName */> entry : attrArgNameMap.entrySet()) {
            attr = entry.getKey();
            index = formula.indexOf("[" + attr + "]");
            if (index >= 0) {
                // 条件表达式中包含该变量
                if (attrValueMap.get(entry.getKey()) == null) {
                    // 用户没有对该变量赋值
                    throw new IllegalArgumentException(MessageFormat.format(noArgsErrMsg, entry.getKey()));
                }
            }
        }

        // 2. 公式变量替换
        Map<String/* argName */, Object/* argValue */> env = new HashMap<>(attrValueMap.size() + attrValueMap.size() >>> 1 + 1);
        env.putAll(attrValueMap);
        String argName;
        for (Map.Entry<String/* attrName */, String/* argName */> entry : attrArgNameMap.entrySet()) {
            attr = entry.getKey();
            argName = attrArgNameMap.get(entry.getKey());

            formula = formula.replaceAll(formatRegex("[" + attr + "]"), formatRegex(argName));
            env.put(argName, attrValueMap.get(entry.getKey()));
        }

        // 3. 执行条件
        try {
            Expression expression = AviatorUtil.getInstance(formula).compile(formula);
            Object result;
            try {
                result = expression.execute(env);
            } catch (ExpressionRuntimeException e) {
                if (e.getCause() != null && e.getCause() instanceof ArithmeticException && e.getCause().getMessage().contains(ZERO_TEXT)) {
                    throw new IllegalArgumentException(MessageFormat.format(executeZeroErrMsg, originalFormula), e.getCause());
                } else {
                    throw new IllegalArgumentException(MessageFormat.format(executeErrMsg + "：" + e.getMessage(), originalFormula), e);
                }
            } catch (ArithmeticException e) {
                throw new IllegalArgumentException(MessageFormat.format(executeZeroErrMsg, originalFormula), e);
            }

            if (result instanceof Boolean) {
                return (boolean)result;
            } else {
                throw new IllegalArgumentException(MessageFormat.format(resultTypeErrMsg, result));
            }

        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                throw e;
            } else {
                throw new IllegalArgumentException("公式执行异常:" + e.getMessage(), e);
            }
        }
    }

    public static boolean executeConditionFormulaPlus(final String originalFormula,
                                                      Map<String/* attrName */, Object/* attrValue */> attrValueMap,
                                                      String noArgsErrMsg,
                                                      String executeErrMsg,
                                                      String executeZeroErrMsg,
                                                      String resultTypeErrMsg) {
        String formula = deleteWhitespace(originalFormula);
        // 1. 确保公式中不存在未赋值的变量(需要提醒用户，给某某变量赋值)
        Set<String> formulaAttrs = getFormulaAttr(formula, false, null);
        for (String attr : formulaAttrs) {
            AssertUtils.isTrue(attrValueMap.containsKey(attr), noArgsErrMsg, attr);
        }

        // 2. 公式变量替换
        Map<String/* argName */, Object/* argValue */> env = new HashMap<>(attrValueMap.size() + attrValueMap.size() >>> 1 + 1);
        env.putAll(attrValueMap);
        int index = 0;
        for (Map.Entry<String/* attrName */, Object/* attrValue */> attr : attrValueMap.entrySet()) {
            String attrName = attr.getKey();
            String argName = "java_arg_" + index++;

            formula = formula.replaceAll(formatRegex("[" + attrName + "]"), formatRegex(argName));
            env.put(argName, attr.getValue());
        }

        // 3. 执行条件
        try {
            Expression expression = AviatorUtil.getInstance(formula).compile(formula);
            Object result;
            try {
                result = expression.execute(env);
            } catch (ExpressionRuntimeException e) {
                if (e.getCause() != null && e.getCause() instanceof ArithmeticException && e.getCause().getMessage().contains(ZERO_TEXT)) {
                    throw new IllegalArgumentException(MessageFormat.format(executeZeroErrMsg, originalFormula), e.getCause());
                } else {
                    throw new IllegalArgumentException(MessageFormat.format(executeErrMsg + "：" + e.getMessage(), originalFormula), e);
                }
            } catch (ArithmeticException e) {
                throw new IllegalArgumentException(MessageFormat.format(executeZeroErrMsg, originalFormula), e);
            }

            if (result instanceof Boolean) {
                return (boolean)result;
            } else {
                throw new IllegalArgumentException(MessageFormat.format(resultTypeErrMsg, result));
            }

        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                throw e;
            } else {
                throw new IllegalArgumentException("公式执行异常:" + e.getMessage(), e);
            }
        }
    }

    /**
     * 执行计算表达式
     *      * PS: 该方法中不对公式做校验了。仅考虑以下情况:
     *      *     1. 用户没有填写相关变量的值
     *      *     2. 公式执行异常(出现情况极少)
     *      *     3. 结果类型转换异常(出现情况极少)
     * @param originalFormula 需要执行的条件表达式
     * @param attrValueMap 用户输入的属性值集合
     * @param attrArgNameMap 属性名与可执行变量名的映射关系
     * @param noArgsErrMsg 当表达式的执行需要某个属性值，但用户未填写时的报错信息
     * @param executeErrMsg 表达式执行失败时的报错信息
     * @param executeZeroErrMsg 执行时发生除零异常时的报错信息
     * @param resultTypeErrMsg 表达式执行结果转BigDecimal异常时的报错信息
     * @param scale BigDecimal的精度
     */
    public static BigDecimal executeComputeFormula(final String originalFormula,
                                                   Map<String/* attrName */, Object/* attrValue */> attrValueMap,
                                                   Map<String/* attrName */, String/* argName */> attrArgNameMap,
                                                   String noArgsErrMsg,
                                                   String executeErrMsg,
                                                   String executeZeroErrMsg,
                                                   String resultTypeErrMsg,
                                                   int scale) {
        String formula = deleteWhitespace(originalFormula);
        // 1. 确保公式中不存在未赋值的变量(需要提醒用户，给某某变量赋值)(公式不同于应用条件，不应该存在"[a] != nil"的情况)
        String attr;
        int index;
        for (Map.Entry<String/* attrName */, String/* argName */> entry : attrArgNameMap.entrySet()) {
            attr = entry.getKey();
            index = formula.indexOf("[" + attr + "]");
            if (index >= 0) {
                // 条件表达式中包含该变量
                if (attrValueMap.get(entry.getKey()) == null) {
                    // 用户没有对该变量赋值
                    throw new IllegalArgumentException(MessageFormat.format(noArgsErrMsg, entry.getKey()));
                }
            }
        }

        // 2. 公式变量替换
        Map<String/* argName */, Object/* argValue */> env = new HashMap<>(attrValueMap.size() + attrValueMap.size() >>> 1 + 1);
        String argName;
        for (Map.Entry<String/* attrName */, String/* argName */> entry : attrArgNameMap.entrySet()) {
            attr = entry.getKey();
            argName = attrArgNameMap.get(entry.getKey());

            formula = formula.replaceAll(formatRegex("[" + attr + "]"), formatRegex(argName));
            env.put(argName, attrValueMap.get(entry.getKey()));
        }

        // 3. 执行条件
        try {
            Expression expression = AviatorUtil.getInstance(formula).compile(formula);
            Object result;
            try {
                result = expression.execute(env);
            } catch (ExpressionRuntimeException e) {
                if (e.getCause() != null && e.getCause() instanceof ArithmeticException && e.getCause().getMessage().contains(ZERO_TEXT)) {
                    throw new IllegalArgumentException(MessageFormat.format(executeZeroErrMsg, originalFormula), e.getCause());
                } else {
                    throw new IllegalArgumentException(MessageFormat.format(executeErrMsg + "：" + e.getMessage(), originalFormula), e);
                }
            } catch (ArithmeticException e) {
                throw new IllegalArgumentException(MessageFormat.format(executeZeroErrMsg, originalFormula), e);
            }

            try {
                BigDecimal dosage = new BigDecimal(result.toString()).setScale(scale, RoundingMode.HALF_UP).stripTrailingZeros();
                return dosage.stripTrailingZeros();
            } catch (ClassCastException e) {
                throw new IllegalArgumentException(MessageFormat.format(resultTypeErrMsg, result));
            }

        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                throw e;
            } else {
                throw new IllegalArgumentException("公式执行异常:" + e.getMessage(), e);
            }
        }
    }

    /**
     * 执行计算表达式
     *      * PS: 该方法中不对公式做校验了。仅考虑以下情况:
     *      *     1. 用户没有填写相关变量的值
     *      *     2. 公式执行异常(出现情况极少)
     *      *     3. 结果类型转换异常(出现情况极少)
     * @param originalFormula 需要执行的条件表达式
     * @param attrValueMap 用户输入的属性值集合
     * @param noArgsErrMsg 当表达式的执行需要某个属性值，但用户未填写时的报错信息
     * @param executeErrMsg 表达式执行失败时的报错信息
     * @param executeZeroErrMsg 执行时发生除零异常时的报错信息
     * @param resultTypeErrMsg 表达式执行结果转BigDecimal异常时的报错信息
     * @param scale BigDecimal的精度
     */
    public static BigDecimal executeComputeFormulaPlus(final String originalFormula,
                                                       Map<String/* attrName */, Object/* attrValue */> attrValueMap,
                                                       String noArgsErrMsg,
                                                       String executeErrMsg,
                                                       String executeZeroErrMsg,
                                                       String resultTypeErrMsg,
                                                       int scale) {
        String formula = deleteWhitespace(originalFormula);
        // 1. 确保公式中不存在未赋值的变量(需要提醒用户，给某某变量赋值)
        Set<String> formulaAttrs = getFormulaAttr(formula, false, null);
        for (String attr : formulaAttrs) {
            AssertUtils.isTrue(attrValueMap.containsKey(attr), noArgsErrMsg, attr);
        }

        // 2. 公式变量替换
        Map<String/* argName */, Object/* argValue */> env = new HashMap<>(attrValueMap.size() + attrValueMap.size() >>> 1 + 1);
        int index = 0;
        for (Map.Entry<String/* attrName */, Object/* attrValue */> attr : attrValueMap.entrySet()) {
            String attrName = attr.getKey();
            String argName = "java_arg_" + index++;

            formula = formula.replaceAll(formatRegex("[" + attrName + "]"), formatRegex(argName));
            env.put(argName, attr.getValue());
        }

        // 3. 执行条件
        try {
            Expression expression = AviatorUtil.getInstance(formula).compile(formula);
            Object result;
            try {
                result = expression.execute(env);
            } catch (ExpressionRuntimeException e) {
                if (e.getCause() != null && e.getCause() instanceof ArithmeticException && e.getCause().getMessage().contains(ZERO_TEXT)) {
                    throw new IllegalArgumentException(MessageFormat.format(executeZeroErrMsg, originalFormula), e.getCause());
                } else {
                    throw new IllegalArgumentException(MessageFormat.format(executeErrMsg + "：" + e.getMessage(), originalFormula), e);
                }
            } catch (ArithmeticException e) {
                throw new IllegalArgumentException(MessageFormat.format(executeZeroErrMsg, originalFormula), e);
            }

            try {
                BigDecimal dosage =new BigDecimal(result.toString()).setScale(scale, RoundingMode.HALF_UP).stripTrailingZeros();
                return dosage.stripTrailingZeros();
            } catch (ClassCastException e) {
                throw new IllegalArgumentException(MessageFormat.format(resultTypeErrMsg, result));
            }

        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                throw e;
            } else {
                throw new IllegalArgumentException("公式执行异常:" + e.getMessage(), e);
            }
        }
    }

}
