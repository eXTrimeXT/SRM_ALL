package com.midea.cloud.srm.biz.pj.sou.quotetemplate.utils;

import com.googlecode.aviator.exception.ExpressionRuntimeException;
import com.googlecode.aviator.exception.ExpressionSyntaxErrorException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.Assert.*;

/**
 * @author zhangwk12@midea.com
 * @since 2022/0331
 */
@Slf4j
public class FormulaUtilsTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    /**
     * 测试去除公式(条件公式、计算公式)中多余的空格
     * PS: 保留属性中的空格
     */
    @Test
    public void testDeleteWhitespace() {
        String formula = "[螺 丝 钉] > 1";
        String result = FormulaUtils.deleteWhitespace(formula);
        assertEquals(result, "[螺 丝 钉]>1");
        formula = "[ 螺 丝 钉 ] > 1";
        result = FormulaUtils.deleteWhitespace(formula);
        assertEquals(result, "[螺 丝 钉]>1");
        formula = "[ 螺 丝 钉] > 1";
        result = FormulaUtils.deleteWhitespace(formula);
        assertEquals(result, "[螺 丝 钉]>1");
        formula = "[螺 丝 钉 ] > 1";
        result = FormulaUtils.deleteWhitespace(formula);
        assertEquals(result, "[螺 丝 钉]>1");
        formula = "[螺 丝 钉 ] > 1 并且 [ a a ] > 2";
        result = FormulaUtils.deleteWhitespace(formula);
        assertEquals(result, "[螺 丝 钉]>1并且[a a]>2");
    }

    /**
     * 测试根据应用条件名称生成对应的应用条件公式
     * PS: 不能获取前端传递的应用条件公式，以防止由于前端错误导致应用条件名称与应用条件公式的语义出现差异
     */
    @Test
    public void testConvertConditionNameSuccess() {
        String result = FormulaUtils.getFormulaFromConditionName("");
        assertTrue(StringUtils.isEmpty(result));

        result = FormulaUtils.getFormulaFromConditionName("[a] 大于 1");
        assertEquals("[a] > 1", result);

        result = FormulaUtils.getFormulaFromConditionName("[a]大于1");
        assertEquals("[a]>1", result);

        result = FormulaUtils.getFormulaFromConditionName("[a] 大于等于 1");
        assertEquals("[a] >= 1", result);

        result = FormulaUtils.getFormulaFromConditionName("[a]大于等于1");
        assertEquals("[a]>=1", result);

        result = FormulaUtils.getFormulaFromConditionName("[a] 小于 1");
        assertEquals("[a] < 1", result);

        result = FormulaUtils.getFormulaFromConditionName("[a] 小于等于 1");
        assertEquals("[a] <= 1", result);

        result = FormulaUtils.getFormulaFromConditionName("[a] 大于 1 并且 [a] 小于5");
        assertEquals("[a] > 1 && [a] <5", result);

        result = FormulaUtils.getFormulaFromConditionName("[a]大于1并且[a]小于5");
        assertEquals("[a]>1&&[a]<5", result);

        /* fixed 当公式字符串里包含 "大于" 等字符串文本时，不应该被转换成符号 */
        result = FormulaUtils.getFormulaFromConditionName("[a] 等于 '大于1'");
        assertEquals("[a] == '大于1'", result);
        result = FormulaUtils.getFormulaFromConditionName("[a] 等于 \"大于1\"");
        assertEquals("[a] == \"大于1\"", result);
    }

    /**
     * 测试获取公式中的变量
     * PS: 这里说的变量指的是"[xxx]"，对于公式"[属性1] * [属性2] * a"，仅返回 "[属性1]"、"[属性2]"
     */
    @Test
    public void testGetFormulaAttrs() {
        /* 正常的属性定义 */
        Set<String> attrSet = FormulaUtils.getFormulaAttr(
                "[属性1] * [属性2] * a", false, null);
        assertEquals(attrSet.size(), 2);
        assertTrue(attrSet.contains("属性1"));
        assertTrue(attrSet.contains("属性2"));

        /* 不正常的属性定义(抑制错误检测) */
        attrSet = FormulaUtils.getFormulaAttr(
                "[属性1] * [属性2]] * a", false, null);
        assertEquals(attrSet.size(), 2);
        assertTrue(attrSet.contains("属性1"));
        assertTrue(attrSet.contains("属性2"));

        /* 不正常的属性定义(抑制错误检测) */
        attrSet = FormulaUtils.getFormulaAttr(
                "[属性1] * [[属性2] * a", false, null);
        assertEquals(attrSet.size(), 2);
        assertTrue(attrSet.contains("属性1"));
        assertTrue(attrSet.contains("[属性2"));

        /* 不正常的属性定义(不抑制错误检测) */
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("公式格式错误");
        FormulaUtils.getFormulaAttr(
                "[属性1] * [属性2]] * a", true, "公式格式错误");
    }

    /**
     * 简单校验公式格式
     * 1. 变量的定义 "[xxx]",不能存在 "[[xxx]"、"[xxx]]"、"[xxx" 等情况
     * 2. 合理使用"()"括号
     * PS: 其余更细致的，通过表达式引擎编译来验证
     */
    @Test
    public void testSimpleValidateFormulaSuccess() {
        FormulaUtils.simpleValidateFormula("[属性1（元）] * 100 * [属性2]", "");

        FormulaUtils.simpleValidateFormula("([属性1] * ([属性2] * 1 / [属性1]))", "");
    }

    /**
     * 简单校验公式格式
     * 1. 变量的定义 "[xxx]",不能存在 "[[xxx]"、"[xxx]]"、"[xxx" 等情况
     * 2. 合理使用"()"括号
     * PS: 其余更细致的，通过表达式引擎编译来验证
     */
    @Test
    public void testSimpleValidateFormulaErrorBracket() {
        /* 测试不合理定义变量"[xxx]" */
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("公式格式错误");
        FormulaUtils.simpleValidateFormula("[[属性1] * 1", "公式格式错误");
    }

    /**
     * 简单校验公式格式
     * 1. 变量的定义 "[xxx]",不能存在 "[[xxx]"、"[xxx]]"、"[xxx" 等情况
     * 2. 合理使用"()"括号
     * PS: 其余更细致的，通过表达式引擎编译来验证
     */
    @Test
    public void testSimpleValidateFormulaErrorCurlyBrace() {
        /* 测试不合理定义大括号 */
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("公式格式错误");
        FormulaUtils.simpleValidateFormula("([属性1] * 1", "公式格式错误");
    }

    /**
     * 简单校验公式格式
     * 1. 不能存在单引号引用错误情况，即公式内字符串定义错误
     * PS: 其余更细致的，通过表达式引擎编译来验证
     */
    @Test
    public void testSimpleValidateFormulaErrorSingleQuote() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("公式格式错误");
        FormulaUtils.simpleValidateFormula("[属性1] == 'a", "公式格式错误");
    }

    /**
     * 简单校验公式格式
     * 1. 不能存在双引号引用错误情况，即公式内字符串定义错误
     * PS: 其余更细致的，通过表达式引擎编译来验证
     */
    @Test
    public void testSimpleValidateFormulaErrorDoubleQuote() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("公式格式错误");
        FormulaUtils.simpleValidateFormula("[属性1] == \"a", "公式格式错误");
    }

    /**
     * 测试验证条件表达式
     * PS: 条件表达式的结果一定是boolean值，并且不能恒为false
     */
    @Test
    public void testAnalysisConditionFormulaSuccess() {
        Set<String> attrNames = new HashSet<>();
        attrNames.add("a");
        attrNames.add("b");
        attrNames.add("c");

        /* 测试文字表达式 */
        FormulaUtils.analysisConditionFormula("1 + 1 > 1", null, attrNames, null);
        FormulaUtils.analysisConditionFormula("1 > 0 && 1 < 2", null, attrNames, null);

        /* 测试动态表达式 */
        FormulaUtils.analysisConditionFormula("[a] > 0", null, attrNames, null);
        FormulaUtils.analysisConditionFormula("(([a] + 100.003) + 999) != (1/3 + 55 * [b]) + [c]", null, attrNames, null);
        FormulaUtils.analysisConditionFormula("[a] != 0 && [c] == 'abc'", null, attrNames, null);
    }

    /**
     * 测试验证条件表达式
     * PS: 当存在未定义的变量(这里的变量指的是表达式引擎可识别变量)
     *     对于核价业务上的变量"[xxx]"，应该在前置步骤中通过调用
     *     {@link FormulaUtils#getFormulaAttr(String, boolean, String)}进行验证
     */
    @Test
    public void testAnalysisConditionFormulaErrorUndefinedArgs() {
        Set<String> attrNames = new HashSet<>();
        attrNames.add("a");
        attrNames.add("b");
        attrNames.add("c");

        try {
            /* 变量"宽"不属于声明的可识别变量 */
            FormulaUtils.analysisConditionFormula("[a] > b", null, attrNames, null);
        } catch (IllegalArgumentException e) {
            /* 断言非表达式引擎抛出的异常 */
            assertNull(e.getCause());
            assertTrue(e.getMessage().contains("存在未定义的变量"));
        }
    }

    /**
     * 测试验证条件表达式(静态表达式)
     * PS: 条件表达式的值只能是boolean
     */
    @Test
    public void testAnalysisConditionFormulaErrorStaticNotBoolean() {
        try {
            /* 尝试传递文本计算表达式，测试能否通过条件表达式验证函数 */
            FormulaUtils.analysisConditionFormula("1 + 1", null, Collections.emptySet(), null);
        } catch (IllegalArgumentException e) {
            /* 断言非表达式引擎抛出的异常 */
            assertNull(e.getCause());
            assertTrue(e.getMessage().contains("非条件表达式"));
        }
    }

    /**
     * 测试验证条件表达式(动态表达式)
     * PS: 条件表达式的值只能是boolean
     */
    @Test
    public void testAnalysisConditionFormulaErrorDynamicNotBoolean() {
        Set<String> attrNames = new HashSet<>();
        attrNames.add("a");
        attrNames.add("b");
        attrNames.add("c");

        try {
            /* 尝试传递动态计算表达式，测试能否通过条件表达式验证函数 */
            FormulaUtils.analysisConditionFormula("[a] + [b]", null, attrNames, null);
        } catch (IllegalArgumentException e) {
            /* 断言非表达式引擎抛出的异常 */
            assertNull(e.getCause());
            assertTrue(e.getMessage().contains("非条件表达式"));
        }
    }

    /**
     * 测试验证条件表达式
     * PS: 条件表达式的值不能恒为false
     */
    @Test
    public void testAnalysisConditionFormulaErrorAlwaysFalse() {
        Set<String> attrNames = new HashSet<>();
        attrNames.add("a");
        attrNames.add("b");
        attrNames.add("c");

        try {
            FormulaUtils.analysisConditionFormula("1 > 2", null, attrNames, null);
        } catch (IllegalArgumentException e) {
            /* 断言非表达式引擎抛出的异常 */
            assertNull(e.getCause());
            assertTrue(e.getMessage().contains("不存在满足的情况"));
        }
    }

    /**
     * 测试条件表达式对语法异常的检测
     */
    @Test
    public void testAnalysisConditionFormulaErrorSyntax() {
        try {
            FormulaUtils.analysisConditionFormula("1 >< 1", null, Collections.emptySet(), null);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getCause() instanceof ExpressionSyntaxErrorException);
            assertTrue(e.getMessage().contains("语法错误"));
        }
    }

    /**
     * 测试条件表达式对参数类型的检测
     * PS: 就核价模块来说参数类型就两种：String、BigDecimal
     */
    @Test
    public void testAnalysisConditionFormulaErrorArgsType() {
    }

    /**
     * 测试条件表达式对除零异常的检测
     * PS: 字面表达式，且分母为简单的0，不是复杂的计算值0
     */
    @Test
    public void testAnalysisConditionFormulaErrorStaticSimpleZero() {
        try {
            FormulaUtils.analysisConditionFormula("1 / 0 > 0", null, Collections.emptySet(), null);
        } catch (IllegalArgumentException e) {
            /* 断言表达式引擎抛出的异常 */
            assertTrue(e.getCause() instanceof ArithmeticException);
            assertTrue(e.getMessage().contains("0不能作为分母"));
        }
    }

    /**
     * 测试条件表达式对除零异常的检测
     * PS: 字面表达式，且分母为复杂的计算值0
     * PS: 常规情况下，无论是aviator还是spel，对于 "1 / (1 - 0.01 * 100)"的计算结果都为Infinity(无穷大)。
     *     因为其计算方式为：double b = 1 / (1 - 0.01 * 100);
     *     但就核价的业务来讲，这是不合适的，我们需要能告诉aviator库，这里面所有的数字类型变量，都应该是BigDecimal，
     *     以便进行精确计算。而aviator库本身是通过 "0.01M" 的后置"M"来表示BigDecimal类型。对客户来说是不现实的。
     * PS: 因此工具类中采用了MyAviatorEvaluator，修改了源码，默认情况下使用BigDecimal进行数值运算。
     */
    @Test
    public void testAnalysisConditionFormulaErrorStaticComplexZero() {
        try {
            FormulaUtils.analysisConditionFormula("1 / (1 - (0.01 * 100)) > 0", null, Collections.emptySet(), null);
        } catch (IllegalArgumentException e) {
            /* 断言表达式引擎抛出的异常 */
            assertTrue(e.getCause() instanceof ArithmeticException);
            assertTrue(e.getMessage().contains("0不能作为分母"));
        }
    }

    /**
     * 测试条件表达式对除零异常的检测
     * PS: 动态表达式，且分母为简单或复杂的计算值0
     * PS: 常规情况下，无论是aviator还是spel，对于 "1 / (1 - 0.01 * 100)"的计算结果都为Infinity(无穷大)。
     *     因为其计算方式为：double b = 1 / (1 - 0.01 * 100);
     *     但就核价的业务来讲，这是不合适的，我们需要能告诉aviator库，这里面所有的数字类型变量，都应该是BigDecimal，
     *     以便进行精确计算。而aviator库本身是通过 "0.01M" 的后置"M"来表示BigDecimal类型。对客户来说是不现实的。
     * PS: 因此工具类中采用了MyAviatorEvaluator，修改了源码，默认情况下使用BigDecimal进行数值运算。
     * PS: 相比于字面表达式，对动态表达式进行除零校验更加复杂。但还是能通过ast划分表达式的子集(子表达式，找到其中的字面
     *     表达式，【好像太复杂了，先略过】)
     */
    @Test
    public void testAnalysisConditionFormulaErrorDynamicZero() {
    }

    @Test
    public void tsetsfsafaf() {
        String s = "(java_Args0 > 1)";
        String regex = "java_Args0";
        log.info(s.replaceAll(regex, "[a\\*]"));
    }

    /**
     * 测试条件表达式
     */
    @Test
    public void testAnalysisConditionFormulaPlusSuccess() {
        String formula = "";
//        所有变量
        Set<String> attrNames = new HashSet<>();
//        文本变量
        Set<String> textAttrs = new HashSet<>();
//        数值变量
        Set<String> decimalAttrs = new HashSet<>();
//        日期变量
        Set<String> dateAttrs = new HashSet<>();
//        字符串日期容器
        Map<String, Date> dateMap = new HashMap<>(50);
//        nil变量容器
        Set<String> nilMap = new HashSet<>();
//        nil变量容器
        Set<String> noNilMap = new HashSet<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        /* 1: 最基本的条件判断 */
        formula = "[a] > 1";
        attrNames.add("a");
        FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                dateMap, nilMap, noNilMap, formatter, null);
        attrNames.clear();
        /* 1.1: 变量名包含正则:"\"、"^"、"$"、"*"、"+"、"?"、"("、")"、"{"、"}"、"["、"]"、"-"、"|"、"." */
        formula = "[a\\] > 1";
        attrNames.add("a\\");
        FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                dateMap, nilMap, noNilMap, formatter, null);
        attrNames.clear();
        formula = "[a\\b] > 1";
        attrNames.add("a\\b");
        FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                dateMap, nilMap, noNilMap, formatter, null);
        attrNames.clear();
        formula = "[a^] > 1";
        attrNames.add("a^");
        FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                dateMap, nilMap, noNilMap, formatter, null);
        attrNames.clear();
        formula = "[a^2] > 1";
        attrNames.add("a^2");
        FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                dateMap, nilMap, noNilMap, formatter, null);
        attrNames.clear();
        formula = "[a$] > 1";
        attrNames.add("a$");
        FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                dateMap, nilMap, noNilMap, formatter, null);
        attrNames.clear();
        formula = "[a$2] > 1";
        attrNames.add("a$2");
        FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                dateMap, nilMap, noNilMap, formatter, null);
        attrNames.clear();
        formula = "[a*] > 1";
        attrNames.add("a*");
        FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                dateMap, nilMap, noNilMap, formatter, null);
        attrNames.clear();
        formula = "[a*2] > 1";
        attrNames.add("a*2");
        FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                dateMap, nilMap, noNilMap, formatter, null);
        attrNames.clear();
        formula = "[a+] > 1";
        attrNames.add("a+");
        FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                dateMap, nilMap, noNilMap, formatter, null);
        attrNames.clear();
        formula = "[a+1] > 1";
        attrNames.add("a+1");
        FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                dateMap, nilMap, noNilMap, formatter, null);
        attrNames.clear();
        formula = "[a?] > 1";
        attrNames.add("a?");
        FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                dateMap, nilMap, noNilMap, formatter, null);
        attrNames.clear();
        formula = "[a?1] > 1";
        attrNames.add("a?1");
        FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                dateMap, nilMap, noNilMap, formatter, null);
        attrNames.clear();
        formula = "[a)] > 1";
        attrNames.add("a)");
        FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                dateMap, nilMap, noNilMap, formatter, null);
        attrNames.clear();
        formula = "[a)1] > 1";
        attrNames.add("a)1");
        FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                dateMap, nilMap, noNilMap, formatter, null);
        attrNames.clear();
        formula = "[a(] > 1";
        attrNames.add("a(");
        FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                dateMap, nilMap, noNilMap, formatter, null);
        attrNames.clear();
        formula = "[a(1] > 1";
        attrNames.add("a(1");
        FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                dateMap, nilMap, noNilMap, formatter, null);
        attrNames.clear();
        formula = "[a{] > 1";
        attrNames.add("a{");
        FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                dateMap, nilMap, noNilMap, formatter, null);
        attrNames.clear();
        formula = "[a{1] > 1";
        attrNames.add("a{1");
        FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                dateMap, nilMap, noNilMap, formatter, null);
        attrNames.clear();
        formula = "[a}] > 1";
        attrNames.add("a}");
        FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                dateMap, nilMap, noNilMap, formatter, null);
        attrNames.clear();
        formula = "[a}1] > 1";
        attrNames.add("a}1");
        FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                dateMap, nilMap, noNilMap, formatter, null);
        attrNames.clear();
        formula = "[a[] > 1";
        attrNames.add("a[");
        FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                dateMap, nilMap, noNilMap, formatter, null);
        attrNames.clear();
        formula = "[a[1] > 1";
        attrNames.add("a[1");
        FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                dateMap, nilMap, noNilMap, formatter, null);
        attrNames.clear();
        formula = "[a]] > 1";
        attrNames.add("a]");
        FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                dateMap, nilMap, noNilMap, formatter, null);
        attrNames.clear();
        formula = "[a]1] > 1";
        attrNames.add("a]1");
        FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                dateMap, nilMap, noNilMap, formatter, null);
        attrNames.clear();
        formula = "[a-] > 1";
        attrNames.add("a-");
        FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                dateMap, nilMap, noNilMap, formatter, null);
        attrNames.clear();
        formula = "[a|] > 1";
        attrNames.add("a|");
        FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                dateMap, nilMap, noNilMap, formatter, null);
        attrNames.clear();
        formula = "[a.] > 1";
        attrNames.add("a.");
        FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                dateMap, nilMap, noNilMap, formatter, null);
        attrNames.clear();
    }

    /**
     * 测试条件表达式: 文本类型变量
     */
    @Test
    public void testAnalysisConditionFormulaPlusErrorTextArr() {
        String formula = "";
//        所有变量
        Set<String> attrNames = new HashSet<>();
//        文本变量
        Set<String> textAttrs = new HashSet<>();
//        数值变量
        Set<String> decimalAttrs = new HashSet<>();
//        日期变量
        Set<String> dateAttrs = new HashSet<>();
//        字符串日期容器
        Map<String, Date> dateMap = new HashMap<>(50);
//        nil变量容器
        Set<String> nilMap = new HashSet<>();
//        非nil变量容器
        Set<String> noNilMap = new HashSet<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        /* 1: 与数字类型的变量进行比较 */
        formula = "[a] != [b]";
        attrNames.add("a");
        attrNames.add("b");
        textAttrs.add("a");
        decimalAttrs.add("b");
        try {
            FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                    dateMap, nilMap, noNilMap, formatter, null);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("数字类型的变量不能与非数字的变量进行比较"));
        }
        attrNames.clear();
        textAttrs.clear();
        decimalAttrs.clear();
        /* 2: 与日期类型的变量进行比较 */
        formula = "[a] != [b]";
        attrNames.add("a");
        attrNames.add("b");
        textAttrs.add("a");
        dateAttrs.add("b");
        try {
            FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                    dateMap, nilMap, noNilMap, formatter, null);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("日期类型的变量不能与非日期的变量进行比较"));
        }
        attrNames.clear();
        textAttrs.clear();
        dateAttrs.clear();
        /* 3: 与文本类型的变量进行比较 */
        formula = "[a] > [b]";
        attrNames.add("a");
        attrNames.add("b");
        textAttrs.add("a");
        textAttrs.add("b");
        try {
            FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                    dateMap, nilMap, noNilMap, formatter, null);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("文本类型比较只能使用==、!="));
        }
        attrNames.clear();
        textAttrs.clear();
        /* 4: 与数字常量比较 */
        formula = "[a] > -1";
        attrNames.add("a");
        textAttrs.add("a");
        try {
            FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                    dateMap, nilMap, noNilMap, formatter, null);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("文本类型比较只能使用==、!="));
        }
        attrNames.clear();
        textAttrs.clear();
    }

    /**
     * 测试条件表达式: 数字类型变量
     */
    @Test
    public void testAnalysisConditionFormulaPlusErroDecimalArr() {
        String formula = "";
//        所有变量
        Set<String> attrNames = new HashSet<>();
//        文本变量
        Set<String> textAttrs = new HashSet<>();
//        数值变量
        Set<String> decimalAttrs = new HashSet<>();
//        日期变量
        Set<String> dateAttrs = new HashSet<>();
//        字符串日期容器
        Map<String, Date> dateMap = new HashMap<>(50);
//        nil变量容器
        Set<String> nilMap = new HashSet<>();
//        非nil变量容器
        Set<String> noNilMap = new HashSet<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        /* 1: 与日期类型的变量进行比较 */
        formula = "[a] != [b]";
        attrNames.add("a");
        attrNames.add("b");
        decimalAttrs.add("a");
        dateAttrs.add("b");
        try {
            FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                    dateMap, nilMap, noNilMap, formatter, null);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("日期类型的变量不能与非日期的变量进行比较"));
        }
        attrNames.clear();
        decimalAttrs.clear();
        dateAttrs.clear();
        /* 2: 与文本类型的变量进行比较 */
        formula = "[a] > [b]";
        attrNames.add("a");
        attrNames.add("b");
        decimalAttrs.add("a");
        textAttrs.add("b");
        try {
            FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                    dateMap, nilMap, noNilMap, formatter, null);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("数字类型的变量不能与非数字的变量进行比较"));
        }
        attrNames.clear();
        textAttrs.clear();
        /* 3: 与文本常量的比较 */
        formula = "[a] >= 'a'";
        attrNames.add("a");
        decimalAttrs.add("a");
        try {
            FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                    dateMap, nilMap, noNilMap, formatter, null);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("数值类型变量不能与非数字进行比较"));
        }
        attrNames.clear();
        decimalAttrs.clear();
    }

    /**
     * 测试条件表达式: 日期类型变量
     */
    @Test
    public void testAnalysisConditionFormulaPlusErrorDateArr() {
        String formula = "";
//        所有变量
        Set<String> attrNames = new HashSet<>();
//        文本变量
        Set<String> textAttrs = new HashSet<>();
//        数值变量
        Set<String> decimalAttrs = new HashSet<>();
//        日期变量
        Set<String> dateAttrs = new HashSet<>();
//        字符串日期容器
        Map<String, Date> dateMap = new HashMap<>(50);
//        nil变量容器
        Set<String> nilMap = new HashSet<>();
//        非nil变量容器
        Set<String> noNilMap = new HashSet<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        /* 1: 与文本类型的变量进行比较 */
        formula = "[a] != [b]";
        attrNames.add("a");
        attrNames.add("b");
        dateAttrs.add("a");
        textAttrs.add("b");
        try {
            FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                    dateMap, nilMap, noNilMap, formatter, null);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("日期类型的变量不能与非日期的变量进行比较"));
        }
        attrNames.clear();
        textAttrs.clear();
        dateAttrs.clear();
        /* 2: 与数值类型的变量进行比较 */
        formula = "[a] > [b]";
        attrNames.add("a");
        attrNames.add("b");
        dateAttrs.add("a");
        decimalAttrs.add("b");
        try {
            FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                    dateMap, nilMap, noNilMap, formatter, null);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("日期类型的变量不能与非日期的变量进行比较"));
        }
        attrNames.clear();
        dateAttrs.clear();
        decimalAttrs.clear();
        /* 3: 与文本常量的比较 */
        formula = "[a] >= 'a'";
        attrNames.add("a");
        dateAttrs.add("a");
        try {
            FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                    dateMap, nilMap, noNilMap, formatter, null);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("'a'不是合法的日期格式数据"));
        }
        formula = "[a] >= '2022-08-08 00/00/00'";
        try {
            FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                    dateMap, nilMap, noNilMap, formatter, null);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("'2022-08-08 00/00/00'不是合法的日期格式数据"));
        }
        attrNames.clear();
        dateAttrs.clear();
        /* 4: 与数字常量的比较 */
        formula = "[a] >= 1";
        attrNames.add("a");
        dateAttrs.add("a");
        try {
            FormulaUtils.analysisConditionFormulaPlus(formula, null, attrNames, textAttrs, decimalAttrs, dateAttrs,
                    dateMap, nilMap, noNilMap, formatter, null);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("日期类型变量[a]不能用于此操作"));
        }
        attrNames.clear();
        dateAttrs.clear();
    }

    /**
     * 测试验证计算表达式
     */
    @Test
    public void testAnalysisComputeFormulaSuccess() {
        Set<String> attrNames = new HashSet<>();
        attrNames.add("a");
        attrNames.add("b");
        attrNames.add("c");

        /* 测试文字表达式(简单数字) */
        FormulaUtils.analysisComputeFormula("max(1)", null, attrNames, null);
        FormulaUtils.analysisComputeFormula("min(-1)", null, attrNames, null);

        /* 测试文字表达式(简单运算) */
        FormulaUtils.analysisComputeFormula("avg(1 + 1)", null, attrNames, null);
        FormulaUtils.analysisComputeFormula("1 * 1 / 1213", null, attrNames, null);

        /* 测试动态表达式 */
        FormulaUtils.analysisComputeFormula("[a] * 1", null, attrNames, null);
    }

    /**
     * 测试验证计算表达式(静态表达式)
     * PS: 计算表达式的值只能是BigDecimal
     */
    @Test
    public void testAnalysisComputeFormulaErrorStaticNotNumber() {
        try {
            /* 尝试传递文本计算表达式，测试能否通过计算表达式验证函数 */
            FormulaUtils.analysisComputeFormula("1 + 1 != 1", null, Collections.emptySet(), null);
        } catch (IllegalArgumentException e) {
            /* 断言非表达式引擎抛出的异常 */
            assertNull(e.getCause());
            assertTrue(e.getMessage().contains("非计算表达式"));
        }
    }

    /**
     * 测试验证计算表达式(动态表达式)
     */
    @Test
    public void testAnalysisComputeFormulaErrorDynamicNotNumber() {
        Set<String> attrNames = new HashSet<>();
        attrNames.add("a");
        attrNames.add("b");
        attrNames.add("c");

        try {
            /* 尝试传递文本计算表达式，测试能否通过计算表达式验证函数 */
            FormulaUtils.analysisComputeFormula("[a] + [c] > [b]", null, attrNames, null);
        } catch (IllegalArgumentException e) {
            /* 断言非表达式引擎抛出的异常 */
            assertNull(e.getCause());
            assertTrue(e.getMessage().contains("非计算表达式"));
        }
    }

    /**
     * 测试验证计算表达式
     * PS: 当表达式存在语法错误时，需要正确提示错误
     */
    @Test
    public void testAnalysisComputeFormulaErrorSyntax() {
        /* aviator能解析 "1 +- 1" 等同于 "1 + (-1)" */
        try {
            FormulaUtils.analysisComputeFormula("1 /+ 1", null, Collections.emptySet(), null);
        } catch (IllegalArgumentException e) {
            /* 断言表达式引擎抛出的异常 */
            assertTrue(e.getCause() instanceof ExpressionSyntaxErrorException);
            assertTrue(e.getMessage().contains("语法错误"));
        }
    }

    /**
     * 测试计算表达式对参数类型的检测
     * PS: 就核价模块来说参数类型就两种：String、BigDecimal
     */
    @Test
    public void testAnalysisComputeFormulaErrorArgsType() {
    }

    /**
     * 测试计算表达式对除零异常的检测
     * PS: 字面表达式，且分母为简单的0，不是复杂的计算值0
     */
    @Test
    public void testAnalysisComputeFormulaErrorStaticSimpleZero() {
        try {
            FormulaUtils.analysisConditionFormula("1 / 0", null, Collections.emptySet(), null);
        } catch (IllegalArgumentException e) {
            /* 断言表达式引擎抛出的异常 */
            assertTrue(e.getCause() instanceof ArithmeticException);
            assertTrue(e.getMessage().contains("0不能作为分母"));
        }
    }

    /**
     * 测试计算表达式对除零异常的检测
     * PS: 字面表达式，且分母为复杂的计算值0
     * PS: 常规情况下，无论是aviator还是spel，对于 "1 / (1 - 0.01 * 100)"的计算结果都为Infinity(无穷大)。
     *     因为其计算方式为：double b = 1 / (1 - 0.01 * 100);
     *     但就核价的业务来讲，这是不合适的，我们需要能告诉aviator库，这里面所有的数字类型变量，都应该是BigDecimal，
     *     以便进行精确计算。而aviator库本身是通过 "0.01M" 的后置"M"来表示BigDecimal类型。对客户来说是不现实的。
     * PS: 因此工具类中采用了MyAviatorEvaluator，修改了源码，默认情况下使用BigDecimal进行数值运算。
     */
    @Test
    public void testAnalysisComputeFormulaErrorStaticComplexZero() {
        try {
            FormulaUtils.analysisConditionFormula("1 / 0", null, Collections.emptySet(), null);
        } catch (IllegalArgumentException e) {
            /* 断言非表达式引擎抛出的异常 */
            assertTrue(e.getCause() instanceof ArithmeticException);
            assertTrue(e.getMessage().contains("0不能作为分母"));
        }
    }

    /**
     * 测试计算表达式对除零异常的检测
     * PS: 动态表达式，且分母为简单或复杂的计算值0
     * PS: 常规情况下，无论是aviator还是spel，对于 "1 / (1 - 0.01 * 100)"的计算结果都为Infinity(无穷大)。
     *     因为其计算方式为：double b = 1 / (1 - 0.01 * 100);
     *     但就核价的业务来讲，这是不合适的，我们需要能告诉aviator库，这里面所有的数字类型变量，都应该是BigDecimal，
     *     以便进行精确计算。而aviator库本身是通过 "0.01M" 的后置"M"来表示BigDecimal类型。对客户来说是不现实的。
     * PS: 因此工具类中采用了MyAviatorEvaluator，修改了源码，默认情况下使用BigDecimal进行数值运算。
     * PS: 相比于字面表达式，对动态表达式进行除零校验更加复杂。但还是能通过ast划分表达式的子集(子表达式，找到其中的字面
     *     表达式，【好像太复杂了，先略过】)
     */
    @Test
    public void testAnalysisComputeFormulaErrorDynamicZero() {
    }

    /**
     * 测试执行条件表达式
     */
    @Test
    public void testExecuteConditionFormulaSuccess() {
        Map<String, Object> attrValueMap = new HashMap<>(50);
        attrValueMap.put("螺丝", new BigDecimal(5));
        attrValueMap.put("箱子", new BigDecimal(30));
        Map<String, String> attrArgNameMap = new HashMap<>(50);
        attrArgNameMap.put("螺丝", "a");
        attrArgNameMap.put("箱子", "b");

        /* 静态条件表达式 */
        boolean flag = FormulaUtils.executeConditionFormula(
                "1 > 0 + 0.5",
                attrValueMap,
                attrArgNameMap,
                null, null, null, null);
        assertTrue(flag);

        /* 动态条件表达式 */
        flag = FormulaUtils.executeConditionFormula(
                "[螺丝] > 1 && [箱子] < 10",
                attrValueMap,
                attrArgNameMap,
                null, null, null, null);
        assertFalse(flag);
    }

    /**
     * 测试条件表达式执行
     * PS: 实际执行中，可能存在用户未填写必要的参数，且程序漏校验的情况。
     *     需给与合理的提示。
     */
    @Test
    public void testExecuteConditionFormulaErrorNoArgs() {
        Map<String, Object> attrValueMap = new HashMap<>(50);
        attrValueMap.put("螺丝", null);
        attrValueMap.put("箱子", new BigDecimal(30));
        Map<String, String> attrArgNameMap = new HashMap<>(50);
        attrArgNameMap.put("螺丝", "a");
        attrArgNameMap.put("箱子", "b");

        try {
            FormulaUtils.executeConditionFormula(
                    "[螺丝] * 1 > [箱子] * 10",
                    attrValueMap,
                    attrArgNameMap,
                    "参数[{0}]未填写", null, null, null);
        } catch (IllegalArgumentException e) {
            assertNull(e.getCause());
            assertTrue(e.getMessage().contains("参数[螺丝]未填写"));
        }
    }

    /**
     * 测试执行条件表达式
     * PS: 一般情况下执行过程不会抛出异常，除非入参类型错误等。
     */
    @Test
    public void testExecuteConditionFormulaErrorExecute() {
        Map<String, Object> attrValueMap = new HashMap<>(50);
//        错误的入参类型
        attrValueMap.put("螺丝", "abc");
        attrValueMap.put("箱子", new BigDecimal(30));
        Map<String, String> attrArgNameMap = new HashMap<>(50);
        attrArgNameMap.put("螺丝", "a");
        attrArgNameMap.put("箱子", "b");

        try {
            FormulaUtils.executeConditionFormula(
                    "[螺丝] * 1 > [箱子] * 10",
                    attrValueMap,
                    attrArgNameMap,
                    null, "执行异常", null, null);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getCause() instanceof ExpressionRuntimeException);
            assertTrue(e.getMessage().contains("执行异常"));
        }
    }

    /**
     * 测试执行条件表达式
     * PS: 当动态条件表达式执行出现除零情况时，需要给与合理的提示
     */
    @Test
    public void testExecuteConditionFormulaErrorDynamicZero() {
        Map<String, Object> attrValueMap = new HashMap<>(50);
        attrValueMap.put("螺丝", new BigDecimal(5));
        attrValueMap.put("箱子", new BigDecimal(20));
        Map<String, String> attrArgNameMap = new HashMap<>(50);
        attrArgNameMap.put("螺丝", "a");
        attrArgNameMap.put("箱子", "b");

        try {
            FormulaUtils.executeConditionFormula(
                    "[螺丝] / 0",
                    attrValueMap,
                    attrArgNameMap,
                    null, null, "公式执行出错,0不能作为分母:{0}", null);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getCause() instanceof ArithmeticException);
            assertTrue(e.getMessage().contains("公式执行出错,0不能作为分母"));
        }
    }

    /**
     * 测试执行条件表达式
     * PS: 一般情况下，执行结果都是Boolean类型，除非前置的公式校验没做
     */
    @Test
    public void testExecuteConditionFormulaErrorResultType() {
        Map<String, Object> attrValueMap = new HashMap<>(50);
        attrValueMap.put("螺丝", new BigDecimal(5));
        attrValueMap.put("箱子", new BigDecimal(30));
        Map<String, String> attrArgNameMap = new HashMap<>(50);
        attrArgNameMap.put("螺丝", "a");
        attrArgNameMap.put("箱子", "b");

        try {
            FormulaUtils.executeConditionFormula(
                    "[螺丝] * 1 + [箱子] * 10",
                    attrValueMap,
                    attrArgNameMap,
                    null, null, null, "公式执行结果有误:{0}");
        } catch (IllegalArgumentException e) {
            assertNull(e.getCause());
            assertTrue(e.getMessage().contains("公式执行结果有误"));
        }
    }

    /**
     * 测试执行计算表达式
     */
    @Test
    public void testExecuteComputeFormulaSuccess() {
        Map<String, Object> attrValueMap = new HashMap<>(50);
        attrValueMap.put("螺丝", new BigDecimal(5));
        attrValueMap.put("箱子", new BigDecimal(20));
        Map<String, String> attrArgNameMap = new HashMap<>(50);
        attrArgNameMap.put("螺丝", "a");
        attrArgNameMap.put("箱子", "b");

        /* 静态条件表达式 */
        BigDecimal result = FormulaUtils.executeComputeFormula(
                "1 * 0 + 0.5",
                attrValueMap,
                attrArgNameMap,
                null, null, null, null, 8);
        assertEquals(result, new BigDecimal("0.5").setScale(8, RoundingMode.HALF_UP).stripTrailingZeros());

        /* 动态条件表达式 */
        result = FormulaUtils.executeComputeFormula(
                "[螺丝] * 10 / [箱子]",
                attrValueMap,
                attrArgNameMap,
                null, null, null, null, 8);
        assertEquals(result, new BigDecimal("2.5").setScale(8, RoundingMode.HALF_UP).stripTrailingZeros());
    }

    /**
     * 测试计算表达式执行
     * PS: 实际执行中，可能存在用户未填写必要的参数，且程序漏校验的情况。
     *     需给与合理的提示。
     */
    @Test
    public void testExecuteComputeFormulaErrorNoArgs() {
        Map<String, Object> attrValueMap = new HashMap<>(50);
        attrValueMap.put("螺丝", null);
        attrValueMap.put("箱子", new BigDecimal(30));
        Map<String, String> attrArgNameMap = new HashMap<>(50);
        attrArgNameMap.put("螺丝", "a");
        attrArgNameMap.put("箱子", "b");

        try {
            FormulaUtils.executeComputeFormula(
                    "[螺丝] * 1 + [箱子] * 10",
                    attrValueMap,
                    attrArgNameMap,
                    "参数[{0}]未填写", null, null, null, 8);
        } catch (IllegalArgumentException e) {
            assertNull(e.getCause());
            assertTrue(e.getMessage().contains("参数[螺丝]未填写"));
        }
    }

    @Test
    public void sfadfsasf() {
        Map<String, Object> env = new HashMap<>(50);
        env.put("单价", new BigDecimal(10));
        env.put("数量", new BigDecimal(9));
        env.put("生产日期", LocalDateTime.now());
        env.put("java_date_1", LocalDateTime.now());
        env.put("总价（元）", null);

        FormulaUtils.executeConditionFormulaPlus("(([数量] <= 10) && ([生产日期] < java_date_1))",
                env, "", "", "", "");
    }

    /**
     * 测试执行计算表达式
     * PS: 一般情况下执行过程不会抛出异常，除非入参类型错误等。
     */
    @Test
    public void testExecuteComputeFormulaErrorExecute() {
        Map<String, Object> attrValueMap = new HashMap<>(50);
//        错误的入参类型
        attrValueMap.put("螺丝", "abc");
        attrValueMap.put("箱子", new BigDecimal(30));
        Map<String, String> attrArgNameMap = new HashMap<>(50);
        attrArgNameMap.put("螺丝", "a");
        attrArgNameMap.put("箱子", "b");

        try {
            FormulaUtils.executeComputeFormula(
                    "max([螺丝] * 1 + [箱子] * 10)",
                    attrValueMap,
                    attrArgNameMap,
                    null, "执行异常", null, null, 8);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getCause() instanceof ExpressionRuntimeException);
            assertTrue(e.getMessage().contains("执行异常"));
        }
    }

    /**
     * 测试执行计算表达式
     * PS: 当动态条件表达式执行出现除零情况时，需要给与合理的提示
     */
    @Test
    public void testExecuteComputeFormulaErrorDynamicZero() {
        Map<String, Object> attrValueMap = new HashMap<>(50);
        attrValueMap.put("螺丝", new BigDecimal(5));
        attrValueMap.put("箱子", new BigDecimal(20));
        Map<String, String> attrArgNameMap = new HashMap<>(50);
        attrArgNameMap.put("螺丝", "a");
        attrArgNameMap.put("箱子", "b");

        try {
            FormulaUtils.executeComputeFormula(
                    "[螺丝] / 0",
                    attrValueMap,
                    attrArgNameMap,
                    null, null, "公式执行出错,0不能作为分母:{0}", null, 8);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getCause() instanceof ArithmeticException);
            assertTrue(e.getMessage().contains("公式执行出错,0不能作为分母"));
        }
    }

    /**
     * 测试执行计算表达式
     * PS: 一般情况下，执行结果都是BigDecimal类型，除非前置的公式校验没做
     */
    @Test
    public void testExecuteComputeFormulaErrorResultType() {
        Map<String, Object> attrValueMap = new HashMap<>(50);
        attrValueMap.put("螺丝", new BigDecimal(5));
        attrValueMap.put("箱子", new BigDecimal(30));
        Map<String, String> attrArgNameMap = new HashMap<>(50);
        attrArgNameMap.put("螺丝", "a");
        attrArgNameMap.put("箱子", "b");

        try {
            FormulaUtils.executeConditionFormula(
                    "[螺丝] * 1 > [箱子] * 10",
                    attrValueMap,
                    attrArgNameMap,
                    null, null, null, "公式执行结果有误:{0}");
        } catch (IllegalArgumentException e) {
            assertNull(e.getCause());
            assertTrue(e.getMessage().contains("公式执行结果有误"));
        }
    }

}
