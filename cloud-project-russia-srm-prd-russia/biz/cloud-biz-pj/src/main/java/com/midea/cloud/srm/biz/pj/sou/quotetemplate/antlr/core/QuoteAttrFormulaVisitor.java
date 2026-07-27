package com.midea.cloud.srm.biz.pj.sou.quotetemplate.antlr.core;// Generated from QuoteAttrFormula.g4 by ANTLR 4.9.3
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * @author huangbf3
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link QuoteAttrFormulaParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface QuoteAttrFormulaVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link QuoteAttrFormulaParser#line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLine(QuoteAttrFormulaParser.LineContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expr_group}
	 * labeled alternative in {@link QuoteAttrFormulaParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprGroup(QuoteAttrFormulaParser.Expr_groupContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expr_fun}
	 * labeled alternative in {@link QuoteAttrFormulaParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprFun(QuoteAttrFormulaParser.Expr_funContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expr_operate}
	 * labeled alternative in {@link QuoteAttrFormulaParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprOperate(QuoteAttrFormulaParser.Expr_operateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expr_normal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprNormal(QuoteAttrFormulaParser.Expr_normalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expr_variable}
	 * labeled alternative in {@link QuoteAttrFormulaParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprVariable(QuoteAttrFormulaParser.Expr_variableContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expr_decimal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprDecimal(QuoteAttrFormulaParser.Expr_decimalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expr_ref}
	 * labeled alternative in {@link QuoteAttrFormulaParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprRef(QuoteAttrFormulaParser.Expr_refContext ctx);
	/**
	 * Visit a parse tree produced by {@link QuoteAttrFormulaParser#ref}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRef(QuoteAttrFormulaParser.RefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_select_valid1}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefSelectValid1(QuoteAttrFormulaParser.Ref_select_valid1Context ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_select_valid2}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefSelectValid2(QuoteAttrFormulaParser.Ref_select_valid2Context ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_select_valid3}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefSelectValid3(QuoteAttrFormulaParser.Ref_select_valid3Context ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_select_valid4}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefSelectValid4(QuoteAttrFormulaParser.Ref_select_valid4Context ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_select_error1}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefSelectError1(QuoteAttrFormulaParser.Ref_select_error1Context ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_select_error2}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefSelectError2(QuoteAttrFormulaParser.Ref_select_error2Context ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_select_frag_s}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_frag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefSelectFragS(QuoteAttrFormulaParser.Ref_select_frag_sContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_select_frag_ns}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_frag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefSelectFragNs(QuoteAttrFormulaParser.Ref_select_frag_nsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_select_frag_error1}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_frag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefSelectFragError1(QuoteAttrFormulaParser.Ref_select_frag_error1Context ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_select_frag_error2}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_frag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefSelectFragError2(QuoteAttrFormulaParser.Ref_select_frag_error2Context ctx);
	/**
	 * Visit a parse tree produced by {@link QuoteAttrFormulaParser#ref_return_frag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefReturnFrag(QuoteAttrFormulaParser.Ref_return_fragContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_select_expr_s_terminal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_expr_s}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefSelectExprsTerminal(QuoteAttrFormulaParser.Ref_select_expr_s_terminalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_select_expr_s_add}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_expr_s}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefSelectExprsAdd(QuoteAttrFormulaParser.Ref_select_expr_s_addContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_select_expr_s_normal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_expr_s}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefSelectExprsNormal(QuoteAttrFormulaParser.Ref_select_expr_s_normalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_select_expr_ns_terminal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_expr_ns}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefSelectExprNsTerminal(QuoteAttrFormulaParser.Ref_select_expr_ns_terminalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_select_expr_ns_normal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_expr_ns}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefSelectExprNsNormal(QuoteAttrFormulaParser.Ref_select_expr_ns_normalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_select_expr_ns_field}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_expr_ns}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefSelectExprNsField(QuoteAttrFormulaParser.Ref_select_expr_ns_fieldContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_select_expr_ns_error1}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_expr_ns}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefSelectExprNsError1(QuoteAttrFormulaParser.Ref_select_expr_ns_error1Context ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_select_expr_ns_error2}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_expr_ns}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefSelectExprNsError2(QuoteAttrFormulaParser.Ref_select_expr_ns_error2Context ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_select_expr_ns_variable}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_expr_ns}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefSelectExprNsVariable(QuoteAttrFormulaParser.Ref_select_expr_ns_variableContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_select_expr_ns_operate}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_expr_ns}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefSelectExprNsOperate(QuoteAttrFormulaParser.Ref_select_expr_ns_operateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_return_expr_variable}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_return_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefReturnExprVariable(QuoteAttrFormulaParser.Ref_return_expr_variableContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_return_expr_terminal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_return_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefReturnExprTerminal(QuoteAttrFormulaParser.Ref_return_expr_terminalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_return_expr_field}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_return_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefReturnExprField(QuoteAttrFormulaParser.Ref_return_expr_fieldContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_return_expr_normal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_return_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefReturnExprNormal(QuoteAttrFormulaParser.Ref_return_expr_normalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_return_expr_error2}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_return_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefReturnExprError2(QuoteAttrFormulaParser.Ref_return_expr_error2Context ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_return_expr_error1}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_return_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefReturnExprError1(QuoteAttrFormulaParser.Ref_return_expr_error1Context ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_return_expr_operate}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_return_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefReturnExprOperate(QuoteAttrFormulaParser.Ref_return_expr_operateContext ctx);
	/**
	 * Visit a parse tree produced by {@link QuoteAttrFormulaParser#ref_where_frag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefWhereFrag(QuoteAttrFormulaParser.Ref_where_fragContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_where_frag_mini_normal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_where_frag_mini}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefWhereFragMiniNormal(QuoteAttrFormulaParser.Ref_where_frag_mini_normalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_where_frag_mini_and}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_where_frag_mini}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefWhereFragMiniAnd(QuoteAttrFormulaParser.Ref_where_frag_mini_andContext ctx);
	/**
	 * Visit a parse tree produced by {@link QuoteAttrFormulaParser#ref_where_frag_mini_frag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefWhereFragMiniFrag(QuoteAttrFormulaParser.Ref_where_frag_mini_fragContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_where_expr_normal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_where_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefWhereExprNormal(QuoteAttrFormulaParser.Ref_where_expr_normalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_where_expr_null}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_where_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefWhereExprNull(QuoteAttrFormulaParser.Ref_where_expr_nullContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_where_expr_field}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_where_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefWhereExprField(QuoteAttrFormulaParser.Ref_where_expr_fieldContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_where_expr_operate}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_where_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefWhereExprOperate(QuoteAttrFormulaParser.Ref_where_expr_operateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_where_expr_terminal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_where_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefWhereExprTerminal(QuoteAttrFormulaParser.Ref_where_expr_terminalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ref_where_expr_variable}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_where_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRefWhereExprVariable(QuoteAttrFormulaParser.Ref_where_expr_variableContext ctx);
	/**
	 * Visit a parse tree produced by {@link QuoteAttrFormulaParser#fun}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFun(QuoteAttrFormulaParser.FunContext ctx);
	/**
	 * Visit a parse tree produced by {@link QuoteAttrFormulaParser#fun_param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunParam(QuoteAttrFormulaParser.Fun_paramContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fun_param_frag_s}
	 * labeled alternative in {@link QuoteAttrFormulaParser#fun_param_frag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunParamFragS(QuoteAttrFormulaParser.Fun_param_frag_sContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fun_param_frag_ns}
	 * labeled alternative in {@link QuoteAttrFormulaParser#fun_param_frag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunParamFragNs(QuoteAttrFormulaParser.Fun_param_frag_nsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fun_param_expr_s_normal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#fun_param_expr_s}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunParamExprsNormal(QuoteAttrFormulaParser.Fun_param_expr_s_normalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fun_param_expr_s_operate}
	 * labeled alternative in {@link QuoteAttrFormulaParser#fun_param_expr_s}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunParamExprsOperate(QuoteAttrFormulaParser.Fun_param_expr_s_operateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fun_param_expr_s_terminal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#fun_param_expr_s}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunParamExprsTerminal(QuoteAttrFormulaParser.Fun_param_expr_s_terminalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fun_param_expr_ns_terminal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#fun_param_expr_ns}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunParamExprNsTerminal(QuoteAttrFormulaParser.Fun_param_expr_ns_terminalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fun_param_expr_ns_normal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#fun_param_expr_ns}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunParamExprNsNormal(QuoteAttrFormulaParser.Fun_param_expr_ns_normalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fun_param_expr_ns_variable}
	 * labeled alternative in {@link QuoteAttrFormulaParser#fun_param_expr_ns}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunParamExprNsVariable(QuoteAttrFormulaParser.Fun_param_expr_ns_variableContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fun_param_expr_ns_operate}
	 * labeled alternative in {@link QuoteAttrFormulaParser#fun_param_expr_ns}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunParamExprNsOperate(QuoteAttrFormulaParser.Fun_param_expr_ns_operateContext ctx);
	/**
	 * Visit a parse tree produced by {@link QuoteAttrFormulaParser#group_func}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroupFunc(QuoteAttrFormulaParser.Group_funcContext ctx);
	/**
	 * Visit a parse tree produced by {@link QuoteAttrFormulaParser#sum}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSum(QuoteAttrFormulaParser.SumContext ctx);
	/**
	 * Visit a parse tree produced by {@link QuoteAttrFormulaParser#max}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMax(QuoteAttrFormulaParser.MaxContext ctx);
	/**
	 * Visit a parse tree produced by {@link QuoteAttrFormulaParser#min}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMin(QuoteAttrFormulaParser.MinContext ctx);
	/**
	 * Visit a parse tree produced by {@link QuoteAttrFormulaParser#avg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAvg(QuoteAttrFormulaParser.AvgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code group_frag_ref}
	 * labeled alternative in {@link QuoteAttrFormulaParser#group_frag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroupFragRef(QuoteAttrFormulaParser.Group_frag_refContext ctx);
	/**
	 * Visit a parse tree produced by the {@code group_frag_fun}
	 * labeled alternative in {@link QuoteAttrFormulaParser#group_frag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroupFragFun(QuoteAttrFormulaParser.Group_frag_funContext ctx);
}