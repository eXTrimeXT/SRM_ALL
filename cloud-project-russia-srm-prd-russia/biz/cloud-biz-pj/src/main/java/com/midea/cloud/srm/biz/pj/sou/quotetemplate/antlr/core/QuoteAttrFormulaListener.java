package com.midea.cloud.srm.biz.pj.sou.quotetemplate.antlr.core;// Generated from QuoteAttrFormula.g4 by ANTLR 4.9.3
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * @author huangbf3
 * This interface defines a complete listener for a parse tree produced by
 * {@link QuoteAttrFormulaParser}.
 */
public interface QuoteAttrFormulaListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link QuoteAttrFormulaParser#line}.
	 * @param ctx the parse tree
	 */
	void enterLine(QuoteAttrFormulaParser.LineContext ctx);
	/**
	 * Exit a parse tree produced by {@link QuoteAttrFormulaParser#line}.
	 * @param ctx the parse tree
	 */
	void exitLine(QuoteAttrFormulaParser.LineContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expr_group}
	 * labeled alternative in {@link QuoteAttrFormulaParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExprGroup(QuoteAttrFormulaParser.Expr_groupContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expr_group}
	 * labeled alternative in {@link QuoteAttrFormulaParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExprGroup(QuoteAttrFormulaParser.Expr_groupContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expr_fun}
	 * labeled alternative in {@link QuoteAttrFormulaParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExprFun(QuoteAttrFormulaParser.Expr_funContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expr_fun}
	 * labeled alternative in {@link QuoteAttrFormulaParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExprFun(QuoteAttrFormulaParser.Expr_funContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expr_operate}
	 * labeled alternative in {@link QuoteAttrFormulaParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExprOperate(QuoteAttrFormulaParser.Expr_operateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expr_operate}
	 * labeled alternative in {@link QuoteAttrFormulaParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExprOperate(QuoteAttrFormulaParser.Expr_operateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expr_normal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExprNormal(QuoteAttrFormulaParser.Expr_normalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expr_normal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExprNormal(QuoteAttrFormulaParser.Expr_normalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expr_variable}
	 * labeled alternative in {@link QuoteAttrFormulaParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExprVariable(QuoteAttrFormulaParser.Expr_variableContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expr_variable}
	 * labeled alternative in {@link QuoteAttrFormulaParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExprVariable(QuoteAttrFormulaParser.Expr_variableContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expr_decimal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExprDecimal(QuoteAttrFormulaParser.Expr_decimalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expr_decimal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExprDecimal(QuoteAttrFormulaParser.Expr_decimalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expr_ref}
	 * labeled alternative in {@link QuoteAttrFormulaParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExprRef(QuoteAttrFormulaParser.Expr_refContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expr_ref}
	 * labeled alternative in {@link QuoteAttrFormulaParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExprRef(QuoteAttrFormulaParser.Expr_refContext ctx);
	/**
	 * Enter a parse tree produced by {@link QuoteAttrFormulaParser#ref}.
	 * @param ctx the parse tree
	 */
	void enterRef(QuoteAttrFormulaParser.RefContext ctx);
	/**
	 * Exit a parse tree produced by {@link QuoteAttrFormulaParser#ref}.
	 * @param ctx the parse tree
	 */
	void exitRef(QuoteAttrFormulaParser.RefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_select_valid1}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select}.
	 * @param ctx the parse tree
	 */
	void enterRefSelectValid1(QuoteAttrFormulaParser.Ref_select_valid1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_select_valid1}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select}.
	 * @param ctx the parse tree
	 */
	void exitRefSelectValid1(QuoteAttrFormulaParser.Ref_select_valid1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_select_valid2}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select}.
	 * @param ctx the parse tree
	 */
	void enterRefSelectValid2(QuoteAttrFormulaParser.Ref_select_valid2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_select_valid2}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select}.
	 * @param ctx the parse tree
	 */
	void exitRefSelectValid2(QuoteAttrFormulaParser.Ref_select_valid2Context ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_select_valid3}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select}.
	 * @param ctx the parse tree
	 */
	void enterRefSelectValid3(QuoteAttrFormulaParser.Ref_select_valid3Context ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_select_valid3}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select}.
	 * @param ctx the parse tree
	 */
	void exitRefSelectValid3(QuoteAttrFormulaParser.Ref_select_valid3Context ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_select_valid4}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select}.
	 * @param ctx the parse tree
	 */
	void enterRefSelectValid4(QuoteAttrFormulaParser.Ref_select_valid4Context ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_select_valid4}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select}.
	 * @param ctx the parse tree
	 */
	void exitRefSelectValid4(QuoteAttrFormulaParser.Ref_select_valid4Context ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_select_error1}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select}.
	 * @param ctx the parse tree
	 */
	void enterRefSelectError1(QuoteAttrFormulaParser.Ref_select_error1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_select_error1}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select}.
	 * @param ctx the parse tree
	 */
	void exitRefSelectRrror1(QuoteAttrFormulaParser.Ref_select_error1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_select_error2}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select}.
	 * @param ctx the parse tree
	 */
	void enterRefSelectRrror2(QuoteAttrFormulaParser.Ref_select_error2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_select_error2}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select}.
	 * @param ctx the parse tree
	 */
	void exitRefSelectError2(QuoteAttrFormulaParser.Ref_select_error2Context ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_select_frag_s}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_frag}.
	 * @param ctx the parse tree
	 */
	void enterRefSelectFragS(QuoteAttrFormulaParser.Ref_select_frag_sContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_select_frag_s}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_frag}.
	 * @param ctx the parse tree
	 */
	void exitRefSelectFragS(QuoteAttrFormulaParser.Ref_select_frag_sContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_select_frag_ns}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_frag}.
	 * @param ctx the parse tree
	 */
	void enterRefSelectFragNs(QuoteAttrFormulaParser.Ref_select_frag_nsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_select_frag_ns}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_frag}.
	 * @param ctx the parse tree
	 */
	void exitRefSelectFragNs(QuoteAttrFormulaParser.Ref_select_frag_nsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_select_frag_error1}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_frag}.
	 * @param ctx the parse tree
	 */
	void enterRefSelectFragError1(QuoteAttrFormulaParser.Ref_select_frag_error1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_select_frag_error1}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_frag}.
	 * @param ctx the parse tree
	 */
	void exitRefSelectFragError1(QuoteAttrFormulaParser.Ref_select_frag_error1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_select_frag_error2}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_frag}.
	 * @param ctx the parse tree
	 */
	void enterRefSelectFragError2(QuoteAttrFormulaParser.Ref_select_frag_error2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_select_frag_error2}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_frag}.
	 * @param ctx the parse tree
	 */
	void exitRefSelectFragError2(QuoteAttrFormulaParser.Ref_select_frag_error2Context ctx);
	/**
	 * Enter a parse tree produced by {@link QuoteAttrFormulaParser#ref_return_frag}.
	 * @param ctx the parse tree
	 */
	void enterRefFeturnFrag(QuoteAttrFormulaParser.Ref_return_fragContext ctx);
	/**
	 * Exit a parse tree produced by {@link QuoteAttrFormulaParser#ref_return_frag}.
	 * @param ctx the parse tree
	 */
	void exitRefReturnFrag(QuoteAttrFormulaParser.Ref_return_fragContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_select_expr_s_terminal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_expr_s}.
	 * @param ctx the parse tree
	 */
	void enterRefSelectExprsTerminal(QuoteAttrFormulaParser.Ref_select_expr_s_terminalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_select_expr_s_terminal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_expr_s}.
	 * @param ctx the parse tree
	 */
	void exitRefSelectExprsTerminal(QuoteAttrFormulaParser.Ref_select_expr_s_terminalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_select_expr_s_add}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_expr_s}.
	 * @param ctx the parse tree
	 */
	void enterRefSelectExprsAdd(QuoteAttrFormulaParser.Ref_select_expr_s_addContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_select_expr_s_add}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_expr_s}.
	 * @param ctx the parse tree
	 */
	void exitRefSelectExprsAdd(QuoteAttrFormulaParser.Ref_select_expr_s_addContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_select_expr_s_normal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_expr_s}.
	 * @param ctx the parse tree
	 */
	void enterRefSelectExprsNormal(QuoteAttrFormulaParser.Ref_select_expr_s_normalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_select_expr_s_normal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_expr_s}.
	 * @param ctx the parse tree
	 */
	void exitRefSelectExprsNormal(QuoteAttrFormulaParser.Ref_select_expr_s_normalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_select_expr_ns_terminal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_expr_ns}.
	 * @param ctx the parse tree
	 */
	void enterRefSelectExprNsTerminal(QuoteAttrFormulaParser.Ref_select_expr_ns_terminalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_select_expr_ns_terminal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_expr_ns}.
	 * @param ctx the parse tree
	 */
	void exitRefSelectExprNsTerminal(QuoteAttrFormulaParser.Ref_select_expr_ns_terminalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_select_expr_ns_normal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_expr_ns}.
	 * @param ctx the parse tree
	 */
	void enterRefSelectExprNsNormal(QuoteAttrFormulaParser.Ref_select_expr_ns_normalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_select_expr_ns_normal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_expr_ns}.
	 * @param ctx the parse tree
	 */
	void exitRefSelectExprNsNormal(QuoteAttrFormulaParser.Ref_select_expr_ns_normalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_select_expr_ns_field}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_expr_ns}.
	 * @param ctx the parse tree
	 */
	void enterRefSelectExprNsField(QuoteAttrFormulaParser.Ref_select_expr_ns_fieldContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_select_expr_ns_field}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_expr_ns}.
	 * @param ctx the parse tree
	 */
	void exitRefSelectExprNsField(QuoteAttrFormulaParser.Ref_select_expr_ns_fieldContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_select_expr_ns_error1}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_expr_ns}.
	 * @param ctx the parse tree
	 */
	void enterRefSelectExprNsError1(QuoteAttrFormulaParser.Ref_select_expr_ns_error1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_select_expr_ns_error1}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_expr_ns}.
	 * @param ctx the parse tree
	 */
	void exitRefSelectExprNsError1(QuoteAttrFormulaParser.Ref_select_expr_ns_error1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_select_expr_ns_error2}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_expr_ns}.
	 * @param ctx the parse tree
	 */
	void enterRefSelectExprNsError2(QuoteAttrFormulaParser.Ref_select_expr_ns_error2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_select_expr_ns_error2}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_expr_ns}.
	 * @param ctx the parse tree
	 */
	void exitRefSelectExprNsError2(QuoteAttrFormulaParser.Ref_select_expr_ns_error2Context ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_select_expr_ns_variable}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_expr_ns}.
	 * @param ctx the parse tree
	 */
	void enterRefSelectExprNsVariable(QuoteAttrFormulaParser.Ref_select_expr_ns_variableContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_select_expr_ns_variable}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_expr_ns}.
	 * @param ctx the parse tree
	 */
	void exitRefSelectExprNsVariable(QuoteAttrFormulaParser.Ref_select_expr_ns_variableContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_select_expr_ns_operate}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_expr_ns}.
	 * @param ctx the parse tree
	 */
	void enterRefSelectExprNsOperate(QuoteAttrFormulaParser.Ref_select_expr_ns_operateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_select_expr_ns_operate}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_select_expr_ns}.
	 * @param ctx the parse tree
	 */
	void exitRefSelectExprNsOperate(QuoteAttrFormulaParser.Ref_select_expr_ns_operateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_return_expr_variable}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_return_expr}.
	 * @param ctx the parse tree
	 */
	void enterRefReturnExprVariable(QuoteAttrFormulaParser.Ref_return_expr_variableContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_return_expr_variable}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_return_expr}.
	 * @param ctx the parse tree
	 */
	void exitRefReturnExprVariable(QuoteAttrFormulaParser.Ref_return_expr_variableContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_return_expr_terminal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_return_expr}.
	 * @param ctx the parse tree
	 */
	void enterRefReturnExprTerminal(QuoteAttrFormulaParser.Ref_return_expr_terminalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_return_expr_terminal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_return_expr}.
	 * @param ctx the parse tree
	 */
	void exitRefReturnExprTerminal(QuoteAttrFormulaParser.Ref_return_expr_terminalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_return_expr_field}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_return_expr}.
	 * @param ctx the parse tree
	 */
	void enterRefReturnExprField(QuoteAttrFormulaParser.Ref_return_expr_fieldContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_return_expr_field}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_return_expr}.
	 * @param ctx the parse tree
	 */
	void exitRefReturnExprField(QuoteAttrFormulaParser.Ref_return_expr_fieldContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_return_expr_normal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_return_expr}.
	 * @param ctx the parse tree
	 */
	void enterRefReturnExprNormal(QuoteAttrFormulaParser.Ref_return_expr_normalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_return_expr_normal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_return_expr}.
	 * @param ctx the parse tree
	 */
	void exitRefReturnExprNormal(QuoteAttrFormulaParser.Ref_return_expr_normalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_return_expr_error2}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_return_expr}.
	 * @param ctx the parse tree
	 */
	void enterRefReturnExprError2(QuoteAttrFormulaParser.Ref_return_expr_error2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_return_expr_error2}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_return_expr}.
	 * @param ctx the parse tree
	 */
	void exitRefReturnExprError2(QuoteAttrFormulaParser.Ref_return_expr_error2Context ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_return_expr_error1}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_return_expr}.
	 * @param ctx the parse tree
	 */
	void enterRefReturnExprError1(QuoteAttrFormulaParser.Ref_return_expr_error1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_return_expr_error1}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_return_expr}.
	 * @param ctx the parse tree
	 */
	void exitRefReturnExprError1(QuoteAttrFormulaParser.Ref_return_expr_error1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_return_expr_operate}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_return_expr}.
	 * @param ctx the parse tree
	 */
	void enterRefReturnExprOperate(QuoteAttrFormulaParser.Ref_return_expr_operateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_return_expr_operate}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_return_expr}.
	 * @param ctx the parse tree
	 */
	void exitRefReturnExprOperate(QuoteAttrFormulaParser.Ref_return_expr_operateContext ctx);
	/**
	 * Enter a parse tree produced by {@link QuoteAttrFormulaParser#ref_where_frag}.
	 * @param ctx the parse tree
	 */
	void enterRefWhereFrag(QuoteAttrFormulaParser.Ref_where_fragContext ctx);
	/**
	 * Exit a parse tree produced by {@link QuoteAttrFormulaParser#ref_where_frag}.
	 * @param ctx the parse tree
	 */
	void exitRefWhereFrag(QuoteAttrFormulaParser.Ref_where_fragContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_where_frag_mini_normal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_where_frag_mini}.
	 * @param ctx the parse tree
	 */
	void enterRefWhereFragMiniNormal(QuoteAttrFormulaParser.Ref_where_frag_mini_normalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_where_frag_mini_normal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_where_frag_mini}.
	 * @param ctx the parse tree
	 */
	void exitRefWhereFragMiniNormal(QuoteAttrFormulaParser.Ref_where_frag_mini_normalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_where_frag_mini_and}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_where_frag_mini}.
	 * @param ctx the parse tree
	 */
	void enterRefWhereFragMiniAnd(QuoteAttrFormulaParser.Ref_where_frag_mini_andContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_where_frag_mini_and}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_where_frag_mini}.
	 * @param ctx the parse tree
	 */
	void exitRefWhereFragMiniAnd(QuoteAttrFormulaParser.Ref_where_frag_mini_andContext ctx);
	/**
	 * Enter a parse tree produced by {@link QuoteAttrFormulaParser#ref_where_frag_mini_frag}.
	 * @param ctx the parse tree
	 */
	void enterRefWhereFragMiniFrag(QuoteAttrFormulaParser.Ref_where_frag_mini_fragContext ctx);
	/**
	 * Exit a parse tree produced by {@link QuoteAttrFormulaParser#ref_where_frag_mini_frag}.
	 * @param ctx the parse tree
	 */
	void exitRefWhereFragMiniFrag(QuoteAttrFormulaParser.Ref_where_frag_mini_fragContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_where_expr_normal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_where_expr}.
	 * @param ctx the parse tree
	 */
	void enterRefWhereExprNormal(QuoteAttrFormulaParser.Ref_where_expr_normalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_where_expr_normal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_where_expr}.
	 * @param ctx the parse tree
	 */
	void exitRefWhereExprNormal(QuoteAttrFormulaParser.Ref_where_expr_normalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_where_expr_null}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_where_expr}.
	 * @param ctx the parse tree
	 */
	void enterRefWhereExprNull(QuoteAttrFormulaParser.Ref_where_expr_nullContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_where_expr_null}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_where_expr}.
	 * @param ctx the parse tree
	 */
	void exitRefWhereExprNull(QuoteAttrFormulaParser.Ref_where_expr_nullContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_where_expr_field}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_where_expr}.
	 * @param ctx the parse tree
	 */
	void enterRefWhereExprField(QuoteAttrFormulaParser.Ref_where_expr_fieldContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_where_expr_field}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_where_expr}.
	 * @param ctx the parse tree
	 */
	void exitRefWhereExprField(QuoteAttrFormulaParser.Ref_where_expr_fieldContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_where_expr_operate}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_where_expr}.
	 * @param ctx the parse tree
	 */
	void enterRefWhereExprOperate(QuoteAttrFormulaParser.Ref_where_expr_operateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_where_expr_operate}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_where_expr}.
	 * @param ctx the parse tree
	 */
	void exitRefWhereExprOperate(QuoteAttrFormulaParser.Ref_where_expr_operateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_where_expr_terminal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_where_expr}.
	 * @param ctx the parse tree
	 */
	void enterRefWhereExprTerminal(QuoteAttrFormulaParser.Ref_where_expr_terminalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_where_expr_terminal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_where_expr}.
	 * @param ctx the parse tree
	 */
	void exitRefWhereExprTerminal(QuoteAttrFormulaParser.Ref_where_expr_terminalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ref_where_expr_variable}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_where_expr}.
	 * @param ctx the parse tree
	 */
	void enterRefWhereExprVariable(QuoteAttrFormulaParser.Ref_where_expr_variableContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ref_where_expr_variable}
	 * labeled alternative in {@link QuoteAttrFormulaParser#ref_where_expr}.
	 * @param ctx the parse tree
	 */
	void exitRefWhereExprVariable(QuoteAttrFormulaParser.Ref_where_expr_variableContext ctx);
	/**
	 * Enter a parse tree produced by {@link QuoteAttrFormulaParser#fun}.
	 * @param ctx the parse tree
	 */
	void enterFun(QuoteAttrFormulaParser.FunContext ctx);
	/**
	 * Exit a parse tree produced by {@link QuoteAttrFormulaParser#fun}.
	 * @param ctx the parse tree
	 */
	void exitFun(QuoteAttrFormulaParser.FunContext ctx);
	/**
	 * Enter a parse tree produced by {@link QuoteAttrFormulaParser#fun_param}.
	 * @param ctx the parse tree
	 */
	void enterFunParam(QuoteAttrFormulaParser.Fun_paramContext ctx);
	/**
	 * Exit a parse tree produced by {@link QuoteAttrFormulaParser#fun_param}.
	 * @param ctx the parse tree
	 */
	void exitFunParam(QuoteAttrFormulaParser.Fun_paramContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fun_param_frag_s}
	 * labeled alternative in {@link QuoteAttrFormulaParser#fun_param_frag}.
	 * @param ctx the parse tree
	 */
	void enterFunParamFragS(QuoteAttrFormulaParser.Fun_param_frag_sContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fun_param_frag_s}
	 * labeled alternative in {@link QuoteAttrFormulaParser#fun_param_frag}.
	 * @param ctx the parse tree
	 */
	void exitFunParamFragS(QuoteAttrFormulaParser.Fun_param_frag_sContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fun_param_frag_ns}
	 * labeled alternative in {@link QuoteAttrFormulaParser#fun_param_frag}.
	 * @param ctx the parse tree
	 */
	void enterFunParamFragNs(QuoteAttrFormulaParser.Fun_param_frag_nsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fun_param_frag_ns}
	 * labeled alternative in {@link QuoteAttrFormulaParser#fun_param_frag}.
	 * @param ctx the parse tree
	 */
	void exitFunParamFragNs(QuoteAttrFormulaParser.Fun_param_frag_nsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fun_param_expr_s_normal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#fun_param_expr_s}.
	 * @param ctx the parse tree
	 */
	void enterFunParamExprsNormal(QuoteAttrFormulaParser.Fun_param_expr_s_normalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fun_param_expr_s_normal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#fun_param_expr_s}.
	 * @param ctx the parse tree
	 */
	void exitFunParamExprsNormal(QuoteAttrFormulaParser.Fun_param_expr_s_normalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fun_param_expr_s_operate}
	 * labeled alternative in {@link QuoteAttrFormulaParser#fun_param_expr_s}.
	 * @param ctx the parse tree
	 */
	void enterFunParamExprsOperate(QuoteAttrFormulaParser.Fun_param_expr_s_operateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fun_param_expr_s_operate}
	 * labeled alternative in {@link QuoteAttrFormulaParser#fun_param_expr_s}.
	 * @param ctx the parse tree
	 */
	void exitFunParamExprsOperate(QuoteAttrFormulaParser.Fun_param_expr_s_operateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fun_param_expr_s_terminal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#fun_param_expr_s}.
	 * @param ctx the parse tree
	 */
	void enterFunParamExprsTerminal(QuoteAttrFormulaParser.Fun_param_expr_s_terminalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fun_param_expr_s_terminal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#fun_param_expr_s}.
	 * @param ctx the parse tree
	 */
	void exitFunParamExprsTerminal(QuoteAttrFormulaParser.Fun_param_expr_s_terminalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fun_param_expr_ns_terminal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#fun_param_expr_ns}.
	 * @param ctx the parse tree
	 */
	void enterFunParamExprNsTerminal(QuoteAttrFormulaParser.Fun_param_expr_ns_terminalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fun_param_expr_ns_terminal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#fun_param_expr_ns}.
	 * @param ctx the parse tree
	 */
	void exitFunParamExprNsTerminal(QuoteAttrFormulaParser.Fun_param_expr_ns_terminalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fun_param_expr_ns_normal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#fun_param_expr_ns}.
	 * @param ctx the parse tree
	 */
	void enterFunParamExprNsNormal(QuoteAttrFormulaParser.Fun_param_expr_ns_normalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fun_param_expr_ns_normal}
	 * labeled alternative in {@link QuoteAttrFormulaParser#fun_param_expr_ns}.
	 * @param ctx the parse tree
	 */
	void exitFunParamExprNsNormal(QuoteAttrFormulaParser.Fun_param_expr_ns_normalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fun_param_expr_ns_variable}
	 * labeled alternative in {@link QuoteAttrFormulaParser#fun_param_expr_ns}.
	 * @param ctx the parse tree
	 */
	void enterFunParamExprNsVariable(QuoteAttrFormulaParser.Fun_param_expr_ns_variableContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fun_param_expr_ns_variable}
	 * labeled alternative in {@link QuoteAttrFormulaParser#fun_param_expr_ns}.
	 * @param ctx the parse tree
	 */
	void exitFunParamExprNsVariable(QuoteAttrFormulaParser.Fun_param_expr_ns_variableContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fun_param_expr_ns_operate}
	 * labeled alternative in {@link QuoteAttrFormulaParser#fun_param_expr_ns}.
	 * @param ctx the parse tree
	 */
	void enterFunParamExprNsOperate(QuoteAttrFormulaParser.Fun_param_expr_ns_operateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fun_param_expr_ns_operate}
	 * labeled alternative in {@link QuoteAttrFormulaParser#fun_param_expr_ns}.
	 * @param ctx the parse tree
	 */
	void exitFunParamExprNsOperate(QuoteAttrFormulaParser.Fun_param_expr_ns_operateContext ctx);
	/**
	 * Enter a parse tree produced by {@link QuoteAttrFormulaParser#group_func}.
	 * @param ctx the parse tree
	 */
	void enterGroupFunc(QuoteAttrFormulaParser.Group_funcContext ctx);
	/**
	 * Exit a parse tree produced by {@link QuoteAttrFormulaParser#group_func}.
	 * @param ctx the parse tree
	 */
	void exitGroupFunc(QuoteAttrFormulaParser.Group_funcContext ctx);
	/**
	 * Enter a parse tree produced by {@link QuoteAttrFormulaParser#sum}.
	 * @param ctx the parse tree
	 */
	void enterSum(QuoteAttrFormulaParser.SumContext ctx);
	/**
	 * Exit a parse tree produced by {@link QuoteAttrFormulaParser#sum}.
	 * @param ctx the parse tree
	 */
	void exitSum(QuoteAttrFormulaParser.SumContext ctx);
	/**
	 * Enter a parse tree produced by {@link QuoteAttrFormulaParser#max}.
	 * @param ctx the parse tree
	 */
	void enterMax(QuoteAttrFormulaParser.MaxContext ctx);
	/**
	 * Exit a parse tree produced by {@link QuoteAttrFormulaParser#max}.
	 * @param ctx the parse tree
	 */
	void exitMax(QuoteAttrFormulaParser.MaxContext ctx);
	/**
	 * Enter a parse tree produced by {@link QuoteAttrFormulaParser#min}.
	 * @param ctx the parse tree
	 */
	void enterMin(QuoteAttrFormulaParser.MinContext ctx);
	/**
	 * Exit a parse tree produced by {@link QuoteAttrFormulaParser#min}.
	 * @param ctx the parse tree
	 */
	void exitMin(QuoteAttrFormulaParser.MinContext ctx);
	/**
	 * Enter a parse tree produced by {@link QuoteAttrFormulaParser#avg}.
	 * @param ctx the parse tree
	 */
	void enterAvg(QuoteAttrFormulaParser.AvgContext ctx);
	/**
	 * Exit a parse tree produced by {@link QuoteAttrFormulaParser#avg}.
	 * @param ctx the parse tree
	 */
	void exitAvg(QuoteAttrFormulaParser.AvgContext ctx);
	/**
	 * Enter a parse tree produced by the {@code group_frag_ref}
	 * labeled alternative in {@link QuoteAttrFormulaParser#group_frag}.
	 * @param ctx the parse tree
	 */
	void enterGroupFragRef(QuoteAttrFormulaParser.Group_frag_refContext ctx);
	/**
	 * Exit a parse tree produced by the {@code group_frag_ref}
	 * labeled alternative in {@link QuoteAttrFormulaParser#group_frag}.
	 * @param ctx the parse tree
	 */
	void exitGroupFragRef(QuoteAttrFormulaParser.Group_frag_refContext ctx);
	/**
	 * Enter a parse tree produced by the {@code group_frag_fun}
	 * labeled alternative in {@link QuoteAttrFormulaParser#group_frag}.
	 * @param ctx the parse tree
	 */
	void enterGroupFragFun(QuoteAttrFormulaParser.Group_frag_funContext ctx);
	/**
	 * Exit a parse tree produced by the {@code group_frag_fun}
	 * labeled alternative in {@link QuoteAttrFormulaParser#group_frag}.
	 * @param ctx the parse tree
	 */
	void exitGroupFragFun(QuoteAttrFormulaParser.Group_frag_funContext ctx);
}