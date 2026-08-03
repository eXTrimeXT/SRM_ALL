package com.midea.cloud.srm.biz.pj.sou.quotetemplate.antlr.parser;

import com.midea.cloud.srm.biz.pj.sou.quotetemplate.antlr.core.QuoteAttrFormulaBaseListener;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.antlr.core.QuoteAttrFormulaParser;
import com.midea.cloud.srm.model.sou.quotetemplate.dto.antlr.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 报价属性 - 公式定义解析器
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/08/02
 */
public class QuoteAttrFormulaCustomListener extends QuoteAttrFormulaBaseListener {

    private final StringBuilder formula;
    private final Map<String, QuoteFunction> functionMap;
    /** variableName  functionType/functionVarType  var */
    private final Map<String, Map<String, String>> variableMap;
    private final VarGenerator varGenerator;
    /** className@objIdentity symbol */
    private final Map<String, String> objSymbolMap;
    /** className@objIdentity */
    private final Map<String, QuoteFunction> functionIdentityMap;
    /** className@objIdentity */
    private final Map<String, QuoteParam> exprFragMap;
    /** className@objIdentity  tempSymbol */
    private final Map<String, String> tempObjSymbolMap;

    private static final int TWO = 2;
    private static final int THREE = 3;
    private static final int FOUR = 4;
    private static final int FIVE = 5;

    public QuoteAttrFormulaCustomListener() {
        formula = new StringBuilder(350);
        functionMap = new HashMap<>(32);
        variableMap = new HashMap<>(32);
        varGenerator = new VarGenerator();
        objSymbolMap = new HashMap<>(32);
        functionIdentityMap = new HashMap<>(32);
        exprFragMap = new HashMap<>(32);
        tempObjSymbolMap = new HashMap<>(32);
    }

    public Map<String/* var */, QuoteFunction> getFunctions() {
        return Collections.unmodifiableMap(functionMap);
    }

    @Override
    public void enterExprGroup(QuoteAttrFormulaParser.Expr_groupContext ctx) {
        this.writeSymbol(ctx);
    }

    @Override
    public void enterExprFun(QuoteAttrFormulaParser.Expr_funContext ctx) {
        this.writeSymbol(ctx);
    }

    @Override
    public void exitExprFun(QuoteAttrFormulaParser.Expr_funContext ctx) {
        this.writeSymbol(ctx);
    }

    @Override
    public void enterExprOperate(QuoteAttrFormulaParser.Expr_operateContext ctx) {
        // expr ( '*' | '/' ) expr
        // expr ( '+' | '-' ) expr
        this.writeSymbol(ctx);
        List<ParseTree> tree = ctx.children;
        // 3个节点，中间节点是运算符
        if (tree.size() < THREE || tree.size() > FIVE) {
            throw new IllegalArgumentException("分析错误");
        }
        TerminalNode terminalNode;
        {
            ParseTree treeNode;
            if (tree.size() == THREE) {
                treeNode = tree.get(1);
            } else if (tree.size() == FOUR) {
                treeNode = tree.get(1);
                if (StringUtils.isBlank(treeNode.getText())) {
                    treeNode = tree.get(2);
                }
            } else {
                treeNode = tree.get(2);
            }
            if (treeNode instanceof TerminalNode) {
                terminalNode = (TerminalNode) treeNode;
            } else {
                throw new IllegalArgumentException("非法的");
            }
        }
        this.putSymbol(tree.get(tree.size() - 1), terminalNode.getText());
    }

    @Override
    public void enterExprNormal(QuoteAttrFormulaParser.Expr_normalContext ctx) {
        this.writeSymbol(ctx);
        formula.append("(");
    }

    @Override
    public void exitExprNormal(QuoteAttrFormulaParser.Expr_normalContext ctx) {
        formula.append(")");
    }

    @Override
    public void enterExprVariable(QuoteAttrFormulaParser.Expr_variableContext ctx) {
        this.writeSymbol(ctx);
        String vName = ctx.VARIABLE().getText();
        vName = vName.substring(2, vName.length() - 1);
        String var = null;
        {
            Map<String/* type */, String/* var */> vars = variableMap.get(vName);
            if (vars != null) {
                var = vars.get(QuoteFunctionType.VAR.name());
            }
        }
        if (var == null) {
            var = varGenerator.generate(QuoteFunctionType.VAR.name());
            variableMap.computeIfAbsent(vName, k -> new HashMap<>(16)).put(QuoteFunctionType.VAR.name(), var);
            QuoteFunction func = new QuoteFunction();
            {
                func.setVariable(var);
                func.setType(QuoteFunctionType.VAR);
                func.setName(vName);
            }
            functionMap.put(var, func);
        }
        formula.append(var);
    }

    @Override
    public void enterExprDecimal(QuoteAttrFormulaParser.Expr_decimalContext ctx) {
        this.writeSymbol(ctx);
        formula.append(ctx.getText());
    }

    @Override
    public void enterExprRef(QuoteAttrFormulaParser.Expr_refContext ctx) {
        this.writeSymbol(ctx);
    }

    @Override
    public void enterRef(QuoteAttrFormulaParser.RefContext ctx) {
        // KEY_REF '.' VARIABLE_NAME '(' ref_select ')'
        this.writeSymbol(ctx);
        String var = varGenerator.generate(QuoteFunctionType.REF.name());
        formula.append(var);

        QuoteFunction func = new QuoteFunction();
        {
            func.setVariable(var);
            func.setType(QuoteFunctionType.REF);
            func.setName(ctx.children.get(2).getText());
            functionIdentityMap.put(getIdentity(ctx), func);
        }
        functionMap.put(var, func);
    }

    @Override
    public void enterRefSelectError1(QuoteAttrFormulaParser.Ref_select_error1Context ctx) {
        // 追溯父级节点，找function记录
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteFunction func = functionIdentityMap.get(parentIdentity);
        while (func == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            func = functionIdentityMap.get(parentIdentity);
        }
        if (func == null) {
            throw new IllegalArgumentException("追溯不到上游记录");
        }
        throw new IllegalArgumentException("[ref." + func.getName() + "]函数缺少 return 语句");
    }

    @Override
    public void enterRefSelectRrror2(QuoteAttrFormulaParser.Ref_select_error2Context ctx) {
        // 追溯父级节点，找function记录
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteFunction func = functionIdentityMap.get(parentIdentity);
        while (func == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            func = functionIdentityMap.get(parentIdentity);
        }
        if (func == null) {
            throw new IllegalArgumentException("追溯不到上游记录");
        }
        throw new IllegalArgumentException("[ref." + func.getName() + "]函数存在多个 return 语句");
    }

    @Override
    public void enterRefSelectFragS(QuoteAttrFormulaParser.Ref_select_frag_sContext ctx) {
        // VARIABLE_REF ( '=' | '!=' | KEY_LIKE) ref_select_expr_s
        // 追溯父级节点，找function记录
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteFunction func = functionIdentityMap.get(parentIdentity);
        while (func == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            func = functionIdentityMap.get(parentIdentity);
        }
        if (func == null) {
            throw new IllegalArgumentException("追溯不到上游记录");
        }
        String key = ctx.children.get(0).getText();
        key = key.substring(2, key.length() - 1);
        String keyVar;
        {
            keyVar = null;
            Map<String/* type */, String/* var */> vars = variableMap.get(key);
            if (vars != null) {
                keyVar = vars.get(QuoteFunctionVarType.FIELD.name());
            }
            if (keyVar == null) {
                keyVar = varGenerator.generate(QuoteFunctionVarType.FIELD.name());
                variableMap.computeIfAbsent(key, k -> new HashMap<>(16)).put(QuoteFunctionVarType.FIELD.name(), keyVar);
            }
        }
        QuoteParam param = new QuoteParam();
        {
            param.setKey(keyVar);
            {
                String operator;
                if (ctx.children.size() == THREE) {
                    operator = ctx.children.get(1).getText();
                } else if (ctx.children.size() == FOUR) {
                    operator = StringUtils.trimToNull(ctx.children.get(1).getText());
                    if (operator == null) {
                        operator = ctx.children.get(2).getText();
                    }
                } else if (ctx.children.size() == FIVE) {
                    operator = ctx.children.get(2).getText();
                } else {
                    throw new IllegalArgumentException("ref_select_frag_s识别异常");
                }
                param.setType(getParamType(operator));
            }
            param.setValue("");
            exprFragMap.put(getIdentity(ctx), param);
        }
        func.getParams().add(param);
        func.getVariables().put(keyVar, new QuoteFunctionVariable(keyVar, key, QuoteFunctionVarType.FIELD));
    }

    @Override
    public void enterRefSelectFragNs(QuoteAttrFormulaParser.Ref_select_frag_nsContext ctx) {
        // VARIABLE_REF ( '=' | '!=' | '>' | '>=' | '<' | '<=' | KEY_LIKE ) ref_select_expr_ns
        // 追溯父级节点，找function记录
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteFunction func = functionIdentityMap.get(parentIdentity);
        while (func == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            func = functionIdentityMap.get(parentIdentity);
        }
        if (func == null) {
            throw new IllegalArgumentException("追溯不到上游记录");
        }
        String key = ctx.children.get(0).getText();
        key = key.substring(2, key.length() - 1);
        String keyVar;
        {
            keyVar = null;
            Map<String/* type */, String/* var */> vars = variableMap.get(key);
            if (vars != null) {
                keyVar = vars.get(QuoteFunctionVarType.FIELD.name());
            }
            if (keyVar == null) {
                keyVar = varGenerator.generate(QuoteFunctionVarType.FIELD.name());
                variableMap.computeIfAbsent(key, k -> new HashMap<>(16)).put(QuoteFunctionVarType.FIELD.name(), keyVar);
            }
        }
        QuoteParam param = new QuoteParam();
        {
            param.setKey(keyVar);
            {
                String operator;
                if (ctx.children.size() == THREE) {
                    operator = ctx.children.get(1).getText();
                } else if (ctx.children.size() == FOUR) {
                    operator = StringUtils.trimToNull(ctx.children.get(1).getText());
                    if (operator == null) {
                        operator = ctx.children.get(2).getText();
                    }
                } else if (ctx.children.size() == FIVE) {
                    operator = ctx.children.get(2).getText();
                } else {
                    throw new IllegalArgumentException("ref_select_frag_ns识别异常");
                }
                param.setType(getParamType(operator));
            }
            param.setValue("");
            exprFragMap.put(getIdentity(ctx), param);
        }
        func.getParams().add(param);
        func.getVariables().put(keyVar, new QuoteFunctionVariable(keyVar, key, QuoteFunctionVarType.FIELD));
    }

    @Override
    public void enterRefSelectFragError1(QuoteAttrFormulaParser.Ref_select_frag_error1Context ctx) {
        // 追溯父级节点，找function记录
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteFunction func = functionIdentityMap.get(parentIdentity);
        while (func == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            func = functionIdentityMap.get(parentIdentity);
        }
        if (func == null) {
            throw new IllegalArgumentException("追溯不到上游记录");
        }
        throw new IllegalArgumentException("[ref." + func.getName() + "]函数的过滤条件的键必须定义为&{xx}: " + ctx.getText());
    }

    @Override
    public void enterRefSelectFragError2(QuoteAttrFormulaParser.Ref_select_frag_error2Context ctx) {
        // 追溯父级节点，找function记录
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteFunction func = functionIdentityMap.get(parentIdentity);
        while (func == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            func = functionIdentityMap.get(parentIdentity);
        }
        if (func == null) {
            throw new IllegalArgumentException("追溯不到上游记录");
        }
        throw new IllegalArgumentException("[ref." + func.getName() + "]函数的过滤条件的键必须定义为&{xx}: " + ctx.getText());
    }

    @Override
    public void enterRefFeturnFrag(QuoteAttrFormulaParser.Ref_return_fragContext ctx) {
        // VARIABLE_NAME ( '=' | '!=' | '>' | '>=' | '<' | '<=' | KEY_LIKE ) ref_select_expr_ns
        // 追溯父级节点，找function记录
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteFunction func = functionIdentityMap.get(parentIdentity);
        while (func == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            func = functionIdentityMap.get(parentIdentity);
        }
        if (func == null) {
            throw new IllegalArgumentException("追溯不到上游记录");
        }
        QuoteParam param = new QuoteParam();
        {
            param.setKey("return");
            {
                String operator;
                if (ctx.children.size() == THREE) {
                    operator = ctx.children.get(1).getText();
                } else if (ctx.children.size() == FOUR) {
                    operator = StringUtils.trimToNull(ctx.children.get(1).getText());
                    if (operator == null) {
                        operator = ctx.children.get(2).getText();
                    }
                } else if (ctx.children.size() == FIVE) {
                    operator = ctx.children.get(2).getText();
                } else {
                    throw new IllegalArgumentException("ref_return_frag识别异常");
                }
                param.setType(getParamType(operator));
            }
            param.setValue("");
            exprFragMap.put(getIdentity(ctx), param);
        }
        func.getParams().add(param);
    }

    @Override
    public void enterRefSelectExprsTerminal(QuoteAttrFormulaParser.Ref_select_expr_s_terminalContext ctx) {
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteParam param = exprFragMap.get(parentIdentity);
        while (param == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            param = exprFragMap.get(parentIdentity);
        }
        if (param == null) {
            throw new IllegalArgumentException("追溯不到上游param记录");
        }

        String tempSymbol = tempObjSymbolMap.get(getIdentity(ctx));
        if (tempSymbol != null) {
            param.setValue(param.getValue() + tempSymbol);
        }

        param.setValue(param.getValue() + ctx.getText());
    }

    @Override
    public void enterRefSelectExprsAdd(QuoteAttrFormulaParser.Ref_select_expr_s_addContext ctx) {
        // ref_select_expr_s '+' ref_select_expr_s
        // ref_select_expr_s '+' ref_select_expr_ns
        // ref_select_expr_ns '+' ref_select_expr_s
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteParam param = exprFragMap.get(parentIdentity);
        while (param == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            param = exprFragMap.get(parentIdentity);
        }
        if (param == null) {
            throw new IllegalArgumentException("追溯不到上游param记录");
        }

        String tempSymbol = tempObjSymbolMap.get(getIdentity(ctx));
        if (tempSymbol != null) {
            param.setValue(param.getValue() + tempSymbol);
        }
        String operator;
        {
            if (ctx.children.size() == THREE) {
                operator = ctx.children.get(1).getText();
            } else if (ctx.children.size() == FOUR) {
                operator = StringUtils.trimToNull(ctx.children.get(1).getText());
                if (operator == null) {
                    operator = ctx.children.get(2).getText();
                }
            } else if (ctx.children.size() == FIVE) {
                operator = ctx.children.get(2).getText();
            } else {
                throw new IllegalArgumentException("ref_select_expr_s_add识别异常");
            }
        }
        tempObjSymbolMap.put(getIdentity(ctx.children.get(ctx.children.size() - 1)), operator);
    }

    @Override
    public void enterRefSelectExprsNormal(QuoteAttrFormulaParser.Ref_select_expr_s_normalContext ctx) {
        // '(' ref_select_expr_s ')'
        // 追溯父级节点，找function记录
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteParam param = exprFragMap.get(parentIdentity);
        while (param == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            param = exprFragMap.get(parentIdentity);
        }
        if (param == null) {
            throw new IllegalArgumentException("追溯不到上游param记录");
        }

        String tempSymbol = tempObjSymbolMap.get(getIdentity(ctx));
        if (tempSymbol != null) {
            param.setValue(param.getValue() + tempSymbol);
        }

        param.setValue(param.getValue() + "(");
    }

    @Override
    public void exitRefSelectExprsNormal(QuoteAttrFormulaParser.Ref_select_expr_s_normalContext ctx) {
        // '(' ref_select_expr_s ')'
        // 追溯父级节点，找function记录
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteParam param = exprFragMap.get(parentIdentity);
        while (param == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            param = exprFragMap.get(parentIdentity);
        }
        if (param == null) {
            throw new IllegalArgumentException("追溯不到上游param记录");
        }
        param.setValue(param.getValue() + ")");
    }

    @Override
    public void enterRefSelectExprNsTerminal(QuoteAttrFormulaParser.Ref_select_expr_ns_terminalContext ctx) {
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteParam param = exprFragMap.get(parentIdentity);
        while (param == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            param = exprFragMap.get(parentIdentity);
        }
        if (param == null) {
            throw new IllegalArgumentException("追溯不到上游param记录");
        }

        String tempSymbol = tempObjSymbolMap.get(getIdentity(ctx));
        if (tempSymbol != null) {
            param.setValue(param.getValue() + tempSymbol);
        }

        param.setValue(param.getValue() + ctx.getText());
    }

    @Override
    public void enterRefSelectExprNsField(QuoteAttrFormulaParser.Ref_select_expr_ns_fieldContext ctx) {
        QuoteParam param;
        {
            ParserRuleContext parentNode = ctx.getParent();
            String parentIdentity = getIdentity(parentNode);
            param = exprFragMap.get(parentIdentity);
            while (param == null) {
                parentNode = parentNode.getParent();
                if (parentNode == null) {
                    break;
                }
                parentIdentity = getIdentity(parentNode);
                param = exprFragMap.get(parentIdentity);
            }
            if (param == null) {
                throw new IllegalArgumentException("追溯不到上游param记录");
            }
        }
        QuoteFunction func;
        {
            ParserRuleContext parentNode = ctx.getParent().getParent();
            String parentIdentity = getIdentity(parentNode);
            func = functionIdentityMap.get(parentIdentity);
            while (func == null) {
                parentNode = parentNode.getParent();
                if (parentNode == null) {
                    break;
                }
                parentIdentity = getIdentity(parentNode);
                func = functionIdentityMap.get(parentIdentity);
            }
            if (func == null) {
                throw new IllegalArgumentException("追溯不到上游func记录");
            }
        }

        String tempSymbol = tempObjSymbolMap.get(getIdentity(ctx));
        if (tempSymbol != null) {
            param.setValue(param.getValue() + tempSymbol);
        }

        String vName = ctx.getText();
        vName = vName.substring(2, vName.length() - 1);
        String var = null;
        {
            Map<String/* type */, String/* var */> vars = variableMap.get(vName);
            if (vars != null) {
                var = vars.get(QuoteFunctionVarType.FIELD.name());
            }
        }
        if (var == null) {
            var = varGenerator.generate(QuoteFunctionVarType.FIELD.name());
            variableMap.computeIfAbsent(vName, k -> new HashMap<>(16)).put(QuoteFunctionVarType.FIELD.name(), var);
        }

        param.setValue(param.getValue() + var);
        func.getVariables().put(var, new QuoteFunctionVariable(var, vName, QuoteFunctionVarType.FIELD));
    }

    @Override
    public void enterRefSelectExprNsVariable(QuoteAttrFormulaParser.Ref_select_expr_ns_variableContext ctx) {
        QuoteParam param;
        {
            ParserRuleContext parentNode = ctx.getParent();
            String parentIdentity = getIdentity(parentNode);
            param = exprFragMap.get(parentIdentity);
            while (param == null) {
                parentNode = parentNode.getParent();
                if (parentNode == null) {
                    break;
                }
                parentIdentity = getIdentity(parentNode);
                param = exprFragMap.get(parentIdentity);
            }
            if (param == null) {
                throw new IllegalArgumentException("追溯不到上游param记录");
            }
        }
        QuoteFunction func;
        {
            ParserRuleContext parentNode = ctx.getParent().getParent();
            String parentIdentity = getIdentity(parentNode);
            func = functionIdentityMap.get(parentIdentity);
            while (func == null) {
                parentNode = parentNode.getParent();
                if (parentNode == null) {
                    break;
                }
                parentIdentity = getIdentity(parentNode);
                func = functionIdentityMap.get(parentIdentity);
            }
            if (func == null) {
                throw new IllegalArgumentException("追溯不到上游func记录");
            }
        }

        String tempSymbol = tempObjSymbolMap.get(getIdentity(ctx));
        if (tempSymbol != null) {
            param.setValue(param.getValue() + tempSymbol);
        }

        String vName = ctx.getText();
        vName = vName.substring(2, vName.length() - 1);
        String var = null;
        {
            Map<String/* type */, String/* var */> vars = variableMap.get(vName);
            if (vars != null) {
                var = vars.get(QuoteFunctionType.VAR.name());
            }
        }
        if (var == null) {
            var = varGenerator.generate(QuoteFunctionType.VAR.name());
            variableMap.computeIfAbsent(vName, k -> new HashMap<>(16)).put(QuoteFunctionType.VAR.name(), var);
            QuoteFunction f = new QuoteFunction();
            {
                f.setVariable(var);
                f.setType(QuoteFunctionType.VAR);
                f.setName(vName);
            }
            functionMap.put(var, f);
        }

        param.setValue(param.getValue() + var);
        func.getVariables().put(var, new QuoteFunctionVariable(var, vName, QuoteFunctionVarType.VAR));
    }

    @Override
    public void enterRefSelectExprNsNormal(QuoteAttrFormulaParser.Ref_select_expr_ns_normalContext ctx) {
        // '(' ref_select_expr_ns ')'
        // 追溯父级节点，找function记录
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteParam param = exprFragMap.get(parentIdentity);
        while (param == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            param = exprFragMap.get(parentIdentity);
        }
        if (param == null) {
            throw new IllegalArgumentException("追溯不到上游param记录");
        }

        String tempSymbol = tempObjSymbolMap.get(getIdentity(ctx));
        if (tempSymbol != null) {
            param.setValue(param.getValue() + tempSymbol);
        }

        param.setValue(param.getValue() + "(");
    }

    @Override
    public void exitRefSelectExprNsNormal(QuoteAttrFormulaParser.Ref_select_expr_ns_normalContext ctx) {
        // '(' ref_select_expr_ns ')'
        // 追溯父级节点，找function记录
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteParam param = exprFragMap.get(parentIdentity);
        while (param == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            param = exprFragMap.get(parentIdentity);
        }
        if (param == null) {
            throw new IllegalArgumentException("追溯不到上游param记录");
        }
        param.setValue(param.getValue() + ")");
    }

    @Override
    public void enterRefSelectExprNsOperate(QuoteAttrFormulaParser.Ref_select_expr_ns_operateContext ctx) {
        // ref_select_expr_ns ( '*' | '/' | '%' ) ref_select_expr_ns
        // ref_select_expr_ns ( '+' | '-' ) ref_select_expr_ns
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteParam param = exprFragMap.get(parentIdentity);
        while (param == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            param = exprFragMap.get(parentIdentity);
        }
        if (param == null) {
            throw new IllegalArgumentException("追溯不到上游param记录");
        }

        String tempSymbol = tempObjSymbolMap.get(getIdentity(ctx));
        if (tempSymbol != null) {
            param.setValue(param.getValue() + tempSymbol);
        }
        String operator;
        {
            if (ctx.children.size() == THREE) {
                operator = ctx.children.get(1).getText();
            } else if (ctx.children.size() == FOUR) {
                operator = StringUtils.trimToNull(ctx.children.get(1).getText());
                if (operator == null) {
                    operator = ctx.children.get(2).getText();
                }
            } else if (ctx.children.size() == FIVE) {
                operator = ctx.children.get(2).getText();
            } else {
                throw new IllegalArgumentException("ref_select_expr_ns_operate识别异常");
            }
        }
        tempObjSymbolMap.put(getIdentity(ctx.children.get(ctx.children.size() - 1)), operator);
    }

    @Override
    public void enterRefSelectExprNsError1(QuoteAttrFormulaParser.Ref_select_expr_ns_error1Context ctx) {
        // 追溯父级节点，找function记录
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteFunction func = functionIdentityMap.get(parentIdentity);
        while (func == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            func = functionIdentityMap.get(parentIdentity);
        }
        if (func == null) {
            throw new IllegalArgumentException("追溯不到上游记录");
        }
        throw new IllegalArgumentException(MessageFormat.format("[ref.{0}]函数存在无法识别的变量[{1}]", func.getName(), ctx.getText()));
    }

    @Override
    public void enterRefSelectExprNsError2(QuoteAttrFormulaParser.Ref_select_expr_ns_error2Context ctx) {
        // 追溯父级节点，找function记录
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteFunction func = functionIdentityMap.get(parentIdentity);
        while (func == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            func = functionIdentityMap.get(parentIdentity);
        }
        if (func == null) {
            throw new IllegalArgumentException("追溯不到上游记录");
        }
        throw new IllegalArgumentException(MessageFormat.format("[ref.{0}]函数存在数字格式错误[{1}]", func.getName(), ctx.getText()));
    }

    @Override
    public void enterRefReturnExprTerminal(QuoteAttrFormulaParser.Ref_return_expr_terminalContext ctx) {
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteParam param = exprFragMap.get(parentIdentity);
        while (param == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            param = exprFragMap.get(parentIdentity);
        }
        if (param == null) {
            throw new IllegalArgumentException("追溯不到上游param记录");
        }

        String tempSymbol = tempObjSymbolMap.get(getIdentity(ctx));
        if (tempSymbol != null) {
            param.setValue(param.getValue() + tempSymbol);
        }

        param.setValue(param.getValue() + ctx.getText());
    }

    @Override
    public void enterRefReturnExprVariable(QuoteAttrFormulaParser.Ref_return_expr_variableContext ctx) {
        QuoteParam param;
        {
            ParserRuleContext parentNode = ctx.getParent();
            String parentIdentity = getIdentity(parentNode);
            param = exprFragMap.get(parentIdentity);
            while (param == null) {
                parentNode = parentNode.getParent();
                if (parentNode == null) {
                    break;
                }
                parentIdentity = getIdentity(parentNode);
                param = exprFragMap.get(parentIdentity);
            }
            if (param == null) {
                throw new IllegalArgumentException("追溯不到上游param记录");
            }
        }
        QuoteFunction func;
        {
            ParserRuleContext parentNode = ctx.getParent().getParent();
            String parentIdentity = getIdentity(parentNode);
            func = functionIdentityMap.get(parentIdentity);
            while (func == null) {
                parentNode = parentNode.getParent();
                if (parentNode == null) {
                    break;
                }
                parentIdentity = getIdentity(parentNode);
                func = functionIdentityMap.get(parentIdentity);
            }
            if (func == null) {
                throw new IllegalArgumentException("追溯不到上游func记录");
            }
        }

        String tempSymbol = tempObjSymbolMap.get(getIdentity(ctx));
        if (tempSymbol != null) {
            param.setValue(param.getValue() + tempSymbol);
        }

        String vName = ctx.getText();
        vName = vName.substring(2, vName.length() - 1);
        String var = null;
        {
            Map<String/* type */, String/* var */> vars = variableMap.get(vName);
            if (vars != null) {
                var = vars.get(QuoteFunctionType.VAR.name());
            }
        }
        if (var == null) {
            var = varGenerator.generate(QuoteFunctionType.VAR.name());
            variableMap.computeIfAbsent(vName, k -> new HashMap<>(16)).put(QuoteFunctionType.VAR.name(), var);
            QuoteFunction f = new QuoteFunction();
            {
                f.setVariable(var);
                f.setType(QuoteFunctionType.VAR);
                f.setName(vName);
            }
            functionMap.put(var, f);
        }

        param.setValue(param.getValue() + var);
        func.getVariables().put(var, new QuoteFunctionVariable(var, vName, QuoteFunctionVarType.VAR));
    }

    @Override
    public void enterRefReturnExprField(QuoteAttrFormulaParser.Ref_return_expr_fieldContext ctx) {
        QuoteParam param;
        {
            ParserRuleContext parentNode = ctx.getParent();
            String parentIdentity = getIdentity(parentNode);
            param = exprFragMap.get(parentIdentity);
            while (param == null) {
                parentNode = parentNode.getParent();
                if (parentNode == null) {
                    break;
                }
                parentIdentity = getIdentity(parentNode);
                param = exprFragMap.get(parentIdentity);
            }
            if (param == null) {
                throw new IllegalArgumentException("追溯不到上游param记录");
            }
        }
        QuoteFunction func;
        {
            ParserRuleContext parentNode = ctx.getParent().getParent();
            String parentIdentity = getIdentity(parentNode);
            func = functionIdentityMap.get(parentIdentity);
            while (func == null) {
                parentNode = parentNode.getParent();
                if (parentNode == null) {
                    break;
                }
                parentIdentity = getIdentity(parentNode);
                func = functionIdentityMap.get(parentIdentity);
            }
            if (func == null) {
                throw new IllegalArgumentException("追溯不到上游func记录");
            }
        }

        String tempSymbol = tempObjSymbolMap.get(getIdentity(ctx));
        if (tempSymbol != null) {
            param.setValue(param.getValue() + tempSymbol);
        }

        String vName = ctx.getText();
        vName = vName.substring(2, vName.length() - 1);
        String var = null;
        {
            Map<String/* type */, String/* var */> vars = variableMap.get(vName);
            if (vars != null) {
                var = vars.get(QuoteFunctionVarType.FIELD.name());
            }
        }
        if (var == null) {
            var = varGenerator.generate(QuoteFunctionVarType.FIELD.name());
            variableMap.computeIfAbsent(vName, k -> new HashMap<>(16)).put(QuoteFunctionVarType.FIELD.name(), var);
        }

        param.setValue(param.getValue() + var);
        func.getVariables().put(var, new QuoteFunctionVariable(var, vName, QuoteFunctionVarType.FIELD));
    }

    @Override
    public void enterRefReturnExprNormal(QuoteAttrFormulaParser.Ref_return_expr_normalContext ctx) {
        // 追溯父级节点，找function记录
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteParam param = exprFragMap.get(parentIdentity);
        while (param == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            param = exprFragMap.get(parentIdentity);
        }
        if (param == null) {
            throw new IllegalArgumentException("追溯不到上游param记录");
        }

        String tempSymbol = tempObjSymbolMap.get(getIdentity(ctx));
        if (tempSymbol != null) {
            param.setValue(param.getValue() + tempSymbol);
        }

        param.setValue(param.getValue() + "(");
    }

    @Override
    public void exitRefReturnExprNormal(QuoteAttrFormulaParser.Ref_return_expr_normalContext ctx) {
        // 追溯父级节点，找function记录
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteParam param = exprFragMap.get(parentIdentity);
        while (param == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            param = exprFragMap.get(parentIdentity);
        }
        if (param == null) {
            throw new IllegalArgumentException("追溯不到上游param记录");
        }
        param.setValue(param.getValue() + ")");
    }

    @Override
    public void enterRefReturnExprOperate(QuoteAttrFormulaParser.Ref_return_expr_operateContext ctx) {
        // ref_return_expr ( '*' | '/' | '%' ) ref_return_expr
        // ref_return_expr ( '+' | '-' ) ref_return_expr
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteParam param = exprFragMap.get(parentIdentity);
        while (param == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            param = exprFragMap.get(parentIdentity);
        }
        if (param == null) {
            throw new IllegalArgumentException("追溯不到上游param记录");
        }

        String tempSymbol = tempObjSymbolMap.get(getIdentity(ctx));
        if (tempSymbol != null) {
            param.setValue(param.getValue() + tempSymbol);
        }
        String operator;
        {
            if (ctx.children.size() == THREE) {
                operator = ctx.children.get(1).getText();
            } else if (ctx.children.size() == FOUR) {
                operator = StringUtils.trimToNull(ctx.children.get(1).getText());
                if (operator == null) {
                    operator = ctx.children.get(2).getText();
                }
            } else if (ctx.children.size() == FIVE) {
                operator = ctx.children.get(2).getText();
            } else {
                throw new IllegalArgumentException("ref_return_expr_operate识别异常");
            }
        }
        tempObjSymbolMap.put(getIdentity(ctx.children.get(ctx.children.size() - 1)), operator);
    }

    @Override
    public void enterRefReturnExprError1(QuoteAttrFormulaParser.Ref_return_expr_error1Context ctx) {
        // 追溯父级节点，找function记录
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteFunction func = functionIdentityMap.get(parentIdentity);
        while (func == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            func = functionIdentityMap.get(parentIdentity);
        }
        if (func == null) {
            throw new IllegalArgumentException("追溯不到上游记录");
        }
        throw new IllegalArgumentException(MessageFormat.format("[ref.{0}]函数存在无法识别的变量[{1}]", func.getName(), ctx.getText()));
    }

    @Override
    public void enterRefReturnExprError2(QuoteAttrFormulaParser.Ref_return_expr_error2Context ctx) {
        // 追溯父级节点，找function记录
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteFunction func = functionIdentityMap.get(parentIdentity);
        while (func == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            func = functionIdentityMap.get(parentIdentity);
        }
        if (func == null) {
            throw new IllegalArgumentException("追溯不到上游记录");
        }
        throw new IllegalArgumentException(MessageFormat.format("[ref.{0}]函数存在数字格式错误[{1}]", func.getName(), ctx.getText()));
    }

    @Override
    public void enterFun(QuoteAttrFormulaParser.FunContext ctx) {
        // KEY_FUN '.' VARIABLE_NAME '(' fun_param ')'
        this.writeSymbol(ctx);
        String var = varGenerator.generate(QuoteFunctionType.FUN.name());
        formula.append(var);

        QuoteFunction func = new QuoteFunction();
        {
            func.setVariable(var);
            func.setType(QuoteFunctionType.FUN);
            func.setName(ctx.children.get(2).getText());
            functionIdentityMap.put(getIdentity(ctx), func);
        }
        functionMap.put(var, func);
    }

    @Override
    public void enterFunParamFragS(QuoteAttrFormulaParser.Fun_param_frag_sContext ctx) {
        // VARIABLE_NAME ('=' | '=' fun_param_expr_s)?
        // 追溯父级节点，找function记录
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteFunction func = functionIdentityMap.get(parentIdentity);
        while (func == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            func = functionIdentityMap.get(parentIdentity);
        }
        if (func == null) {
            throw new IllegalArgumentException("追溯不到上游记录");
        }
        QuoteParam param = new QuoteParam();
        List<String> tags = new ArrayList<>();
        {
            for (ParseTree child : ctx.children) {
                String tag = StringUtils.trimToNull(child.getText());
                if (tag != null) {
                    tags.add(tag);
                }
            }
        }
        if (tags.size() <= TWO) {
            param.setKey(ctx.children.get(0).getText());
        } else {
            param.setKey(ctx.children.get(0).getText());
            param.setValue("");
            exprFragMap.put(getIdentity(ctx), param);
        }
        func.getParams().add(param);
    }

    @Override
    public void enterFunParamFragNs(QuoteAttrFormulaParser.Fun_param_frag_nsContext ctx) {
        // VARIABLE_NAME ('=' | '=' fun_param_expr_ns)?
        // 追溯父级节点，找function记录
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteFunction func = functionIdentityMap.get(parentIdentity);
        while (func == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            func = functionIdentityMap.get(parentIdentity);
        }
        if (func == null) {
            throw new IllegalArgumentException("追溯不到上游记录");
        }
        QuoteParam param = new QuoteParam();
        List<String> tags = new ArrayList<>();
        {
            for (ParseTree child : ctx.children) {
                String tag = StringUtils.trimToNull(child.getText());
                if (tag != null) {
                    tags.add(tag);
                }
            }
        }
        if (tags.size() <= TWO) {
            param.setKey(ctx.children.get(0).getText());
        } else {
            param.setKey(ctx.children.get(0).getText());
            param.setValue("");
            exprFragMap.put(getIdentity(ctx), param);
        }
        func.getParams().add(param);
    }

    @Override
    public void enterFunParamExprsNormal(QuoteAttrFormulaParser.Fun_param_expr_s_normalContext ctx) {
        // 追溯父级节点，找function记录
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteParam param = exprFragMap.get(parentIdentity);
        while (param == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            param = exprFragMap.get(parentIdentity);
        }
        if (param == null) {
            throw new IllegalArgumentException("追溯不到上游param记录");
        }

        String tempSymbol = tempObjSymbolMap.get(getIdentity(ctx));
        if (tempSymbol != null) {
            param.setValue(param.getValue() + tempSymbol);
        }

        param.setValue(param.getValue() + "(");
    }

    @Override
    public void exitFunParamExprsNormal(QuoteAttrFormulaParser.Fun_param_expr_s_normalContext ctx) {
        // 追溯父级节点，找function记录
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteParam param = exprFragMap.get(parentIdentity);
        while (param == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            param = exprFragMap.get(parentIdentity);
        }
        if (param == null) {
            throw new IllegalArgumentException("追溯不到上游param记录");
        }
        param.setValue(param.getValue() + ")");
    }

    @Override
    public void enterFunParamExprsOperate(QuoteAttrFormulaParser.Fun_param_expr_s_operateContext ctx) {
        // fun_param_expr_s '+' fun_param_expr_s
        // fun_param_expr_s '+' fun_param_expr_ns
        // fun_param_expr_ns '+' fun_param_expr_s
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteParam param = exprFragMap.get(parentIdentity);
        while (param == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            param = exprFragMap.get(parentIdentity);
        }
        if (param == null) {
            throw new IllegalArgumentException("追溯不到上游param记录");
        }

        String tempSymbol = tempObjSymbolMap.get(getIdentity(ctx));
        if (tempSymbol != null) {
            param.setValue(param.getValue() + tempSymbol);
        }
        String operator;
        {
            if (ctx.children.size() == THREE) {
                operator = ctx.children.get(1).getText();
            } else if (ctx.children.size() == FOUR) {
                operator = StringUtils.trimToNull(ctx.children.get(1).getText());
                if (operator == null) {
                    operator = ctx.children.get(2).getText();
                }
            } else if (ctx.children.size() == FIVE) {
                operator = ctx.children.get(2).getText();
            } else {
                throw new IllegalArgumentException("fun_param_expr_s_operate识别异常");
            }
        }
        tempObjSymbolMap.put(getIdentity(ctx.children.get(ctx.children.size() - 1)), operator);
    }

    @Override
    public void enterFunParamExprsTerminal(QuoteAttrFormulaParser.Fun_param_expr_s_terminalContext ctx) {
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteParam param = exprFragMap.get(parentIdentity);
        while (param == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            param = exprFragMap.get(parentIdentity);
        }
        if (param == null) {
            throw new IllegalArgumentException("追溯不到上游param记录");
        }

        String tempSymbol = tempObjSymbolMap.get(getIdentity(ctx));
        if (tempSymbol != null) {
            param.setValue(param.getValue() + tempSymbol);
        }

        param.setValue(param.getValue() + ctx.getText());
    }

    @Override
    public void enterFunParamExprNsNormal(QuoteAttrFormulaParser.Fun_param_expr_ns_normalContext ctx) {
        // 追溯父级节点，找function记录
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteParam param = exprFragMap.get(parentIdentity);
        while (param == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            param = exprFragMap.get(parentIdentity);
        }
        if (param == null) {
            throw new IllegalArgumentException("追溯不到上游param记录");
        }

        String tempSymbol = tempObjSymbolMap.get(getIdentity(ctx));
        if (tempSymbol != null) {
            param.setValue(param.getValue() + tempSymbol);
        }

        param.setValue(param.getValue() + "(");
    }

    @Override
    public void exitFunParamExprNsNormal(QuoteAttrFormulaParser.Fun_param_expr_ns_normalContext ctx) {
        // 追溯父级节点，找function记录
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteParam param = exprFragMap.get(parentIdentity);
        while (param == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            param = exprFragMap.get(parentIdentity);
        }
        if (param == null) {
            throw new IllegalArgumentException("追溯不到上游param记录");
        }
        param.setValue(param.getValue() + ")");
    }

    @Override
    public void enterFunParamExprNsOperate(QuoteAttrFormulaParser.Fun_param_expr_ns_operateContext ctx) {
        // fun_param_expr_ns ( '*' | '/' | '%' ) fun_param_expr_ns
        // fun_param_expr_ns ( '+' | '-' ) fun_param_expr_ns
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteParam param = exprFragMap.get(parentIdentity);
        while (param == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            param = exprFragMap.get(parentIdentity);
        }
        if (param == null) {
            throw new IllegalArgumentException("追溯不到上游param记录");
        }

        String tempSymbol = tempObjSymbolMap.get(getIdentity(ctx));
        if (tempSymbol != null) {
            param.setValue(param.getValue() + tempSymbol);
        }
        String operator;
        {
            if (ctx.children.size() == THREE) {
                operator = ctx.children.get(1).getText();
            } else if (ctx.children.size() == FOUR) {
                operator = StringUtils.trimToNull(ctx.children.get(1).getText());
                if (operator == null) {
                    operator = ctx.children.get(2).getText();
                }
            } else if (ctx.children.size() == FIVE) {
                operator = ctx.children.get(2).getText();
            } else {
                throw new IllegalArgumentException("fun_param_expr_ns_operate识别异常");
            }
        }
        tempObjSymbolMap.put(getIdentity(ctx.children.get(ctx.children.size() - 1)), operator);
    }

    @Override
    public void enterFunParamExprNsTerminal(QuoteAttrFormulaParser.Fun_param_expr_ns_terminalContext ctx) {
        ParserRuleContext parentNode = ctx.getParent();
        String parentIdentity = getIdentity(parentNode);
        QuoteParam param = exprFragMap.get(parentIdentity);
        while (param == null) {
            parentNode = parentNode.getParent();
            if (parentNode == null) {
                break;
            }
            parentIdentity = getIdentity(parentNode);
            param = exprFragMap.get(parentIdentity);
        }
        if (param == null) {
            throw new IllegalArgumentException("追溯不到上游param记录");
        }

        String tempSymbol = tempObjSymbolMap.get(getIdentity(ctx));
        if (tempSymbol != null) {
            param.setValue(param.getValue() + tempSymbol);
        }

        param.setValue(param.getValue() + ctx.getText());
    }

    @Override
    public void enterFunParamExprNsVariable(QuoteAttrFormulaParser.Fun_param_expr_ns_variableContext ctx) {
        QuoteParam param;
        {
            ParserRuleContext parentNode = ctx.getParent();
            String parentIdentity = getIdentity(parentNode);
            param = exprFragMap.get(parentIdentity);
            while (param == null) {
                parentNode = parentNode.getParent();
                if (parentNode == null) {
                    break;
                }
                parentIdentity = getIdentity(parentNode);
                param = exprFragMap.get(parentIdentity);
            }
            if (param == null) {
                throw new IllegalArgumentException("追溯不到上游param记录");
            }
        }
        QuoteFunction func;
        {
            ParserRuleContext parentNode = ctx.getParent().getParent();
            String parentIdentity = getIdentity(parentNode);
            func = functionIdentityMap.get(parentIdentity);
            while (func == null) {
                parentNode = parentNode.getParent();
                if (parentNode == null) {
                    break;
                }
                parentIdentity = getIdentity(parentNode);
                func = functionIdentityMap.get(parentIdentity);
            }
            if (func == null) {
                throw new IllegalArgumentException("追溯不到上游func记录");
            }
        }

        String tempSymbol = tempObjSymbolMap.get(getIdentity(ctx));
        if (tempSymbol != null) {
            param.setValue(param.getValue() + tempSymbol);
        }

        String vName = ctx.getText();
        vName = vName.substring(2, vName.length() - 1);
        String var = null;
        {
            Map<String/* type */, String/* var */> vars = variableMap.get(vName);
            if (vars != null) {
                var = vars.get(QuoteFunctionType.VAR.name());
            }
        }
        if (var == null) {
            var = varGenerator.generate(QuoteFunctionType.VAR.name());
            variableMap.computeIfAbsent(vName, k -> new HashMap<>(16)).put(QuoteFunctionType.VAR.name(), var);
            QuoteFunction f = new QuoteFunction();
            {
                f.setVariable(var);
                f.setType(QuoteFunctionType.VAR);
                f.setName(vName);
            }
            functionMap.put(var, f);
        }

        param.setValue(param.getValue() + var);
        func.getVariables().put(var, new QuoteFunctionVariable(var, vName, QuoteFunctionVarType.VAR));
    }

    @Override
    public void enterGroupFunc(QuoteAttrFormulaParser.Group_funcContext ctx) {
        this.writeSymbol(ctx);
    }

    @Override
    public void enterSum(QuoteAttrFormulaParser.SumContext ctx) {
        this.writeSymbol(ctx);
        formula.append("sum(");
    }

    @Override
    public void exitSum(QuoteAttrFormulaParser.SumContext ctx) {
        formula.append(")");
    }

    @Override
    public void enterMax(QuoteAttrFormulaParser.MaxContext ctx) {
        this.writeSymbol(ctx);
        formula.append("max(");
    }

    @Override
    public void exitMax(QuoteAttrFormulaParser.MaxContext ctx) {
        formula.append(")");
    }

    @Override
    public void enterMin(QuoteAttrFormulaParser.MinContext ctx) {
        this.writeSymbol(ctx);
        formula.append("min(");
    }

    @Override
    public void exitMin(QuoteAttrFormulaParser.MinContext ctx) {
        formula.append(")");
    }

    @Override
    public void enterAvg(QuoteAttrFormulaParser.AvgContext ctx) {
        this.writeSymbol(ctx);
        formula.append("avg(");
    }

    @Override
    public void exitAvg(QuoteAttrFormulaParser.AvgContext ctx) {
        formula.append(")");
    }

    @Override
    public void enterGroupFragRef(QuoteAttrFormulaParser.Group_frag_refContext ctx) {
        this.writeSymbol(ctx);
    }

    @Override
    public void enterGroupFragFun(QuoteAttrFormulaParser.Group_frag_funContext ctx) {
        this.writeSymbol(ctx);
    }

    private void putSymbol(Object o, String symbol) {
        String identity = getIdentity(o);
        if (objSymbolMap.containsKey(identity)) {
            throw new IllegalArgumentException("已存在，不能重复添加");
        } else {
            objSymbolMap.put(identity, symbol);
        }
    }

    private void writeSymbol(Object o) {
        String identity = getIdentity(o);
        String symbol = objSymbolMap.remove(identity);
        if (symbol != null) {
            formula.append(symbol);
        }
    }

    public String getFormula() {
        return formula.toString();
    }

    private static String getIdentity(Object o) {
        return o.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(o));
    }

    private static QuoteParamType getParamType(String s) {
        QuoteParamType type;
        s = s.toUpperCase();
        switch (s) {
            case "=":
                type = QuoteParamType.EQ;
                break;
            case "!=":
                type = QuoteParamType.NE;
                break;
            case ">":
                type = QuoteParamType.GT;
                break;
            case ">=":
                type = QuoteParamType.GE;
                break;
            case "<":
                type = QuoteParamType.LT;
                break;
            case "<=":
                type = QuoteParamType.LE;
                break;
            case "LIKE":
                type = QuoteParamType.LIKE;
                break;
            default:
                throw new IllegalArgumentException("无法识别的操作符" + s);
        }
        return type;
    }

    private static class VarGenerator {
        private final Map<String/* functionType/functionVarType */, AtomicInteger> nextIndexMap;

        public VarGenerator() {
            nextIndexMap = new HashMap<>(32);
        }

        public String generate(String type) {
            AtomicInteger atomicInteger = nextIndexMap.get(type);
            if (atomicInteger == null) {
                atomicInteger = new AtomicInteger(0);
            }
            int nextIndex = atomicInteger.getAndIncrement();
            nextIndexMap.put(type, atomicInteger);
            return type + nextIndex;
        }
    }

}
