package com.midea.cloud.srm.biz.pj.sou.quotetemplate.antlr.core;// Generated from QuoteAttrFormula.g4 by ANTLR 4.9.3

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class QuoteAttrFormulaParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		KEY_LIKE=18, KEY_RETURN=19, KEY_WHERE=20, KEY_IS=21, KEY_NULL=22, KEY_NOT=23, 
		KEY_AND=24, KEY_SUM=25, KEY_MAX=26, KEY_MIN=27, KEY_AVG=28, KEY_REF=29, 
		KEY_FUN=30, KEY_WORD=31, DECIMAL=32, DIGIT=33, VARIABLE=34, VARIABLE_REF=35, 
		VARIABLE_NAME=36, STRING=37, AVAILABLE_CHAR=38, ERROR_VAR_COIN_BEGIN1=39, 
		WS=40;
	public static final int
		RULE_line = 0, RULE_expr = 1, RULE_ref = 2, RULE_ref_select = 3, RULE_ref_select_frag = 4, 
		RULE_ref_return_frag = 5, RULE_ref_select_expr_s = 6, RULE_ref_select_expr_ns = 7, 
		RULE_ref_return_expr = 8, RULE_ref_where_frag = 9, RULE_ref_where_frag_mini = 10, 
		RULE_ref_where_frag_mini_frag = 11, RULE_ref_where_expr = 12, RULE_fun = 13, 
		RULE_fun_param = 14, RULE_fun_param_frag = 15, RULE_fun_param_expr_s = 16, 
		RULE_fun_param_expr_ns = 17, RULE_group_func = 18, RULE_sum = 19, RULE_max = 20, 
		RULE_min = 21, RULE_avg = 22, RULE_group_frag = 23;
	private static String[] makeRuleNames() {
		return new String[] {
			"line", "expr", "ref", "ref_select", "ref_select_frag", "ref_return_frag", 
			"ref_select_expr_s", "ref_select_expr_ns", "ref_return_expr", "ref_where_frag", 
			"ref_where_frag_mini", "ref_where_frag_mini_frag", "ref_where_expr", 
			"fun", "fun_param", "fun_param_frag", "fun_param_expr_s", "fun_param_expr_ns", 
			"group_func", "sum", "max", "min", "avg", "group_frag"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "' '", "'('", "')'", "'*'", "'/'", "'%'", "'+'", "'-'", "'.'", 
			"','", "'='", "'!='", "'>'", "'>='", "'<'", "'<='", "'\"'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, "KEY_LIKE", "KEY_RETURN", "KEY_WHERE", 
			"KEY_IS", "KEY_NULL", "KEY_NOT", "KEY_AND", "KEY_SUM", "KEY_MAX", "KEY_MIN", 
			"KEY_AVG", "KEY_REF", "KEY_FUN", "KEY_WORD", "DECIMAL", "DIGIT", "VARIABLE", 
			"VARIABLE_REF", "VARIABLE_NAME", "STRING", "AVAILABLE_CHAR", "ERROR_VAR_COIN_BEGIN1", 
			"WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "QuoteAttrFormula.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public QuoteAttrFormulaParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class LineContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode EOF() { return getToken(QuoteAttrFormulaParser.EOF, 0); }
		public LineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_line; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitLine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitLine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LineContext line() throws RecognitionException {
		LineContext _localctx = new LineContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_line);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(48);
			expr(0);
			setState(49);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprContext extends ParserRuleContext {
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
	 
		public ExprContext() { }
		public void copyFrom(ExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class Expr_groupContext extends ExprContext {
		public Group_funcContext group_func() {
			return getRuleContext(Group_funcContext.class,0);
		}
		public Expr_groupContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterExprGroup(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitExprGroup(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitExprGroup(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Expr_funContext extends ExprContext {
		public FunContext fun() {
			return getRuleContext(FunContext.class,0);
		}
		public Expr_funContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterExprFun(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitExprFun(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitExprFun(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Expr_operateContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public Expr_operateContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterExprOperate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitExprOperate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitExprOperate(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Expr_normalContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public Expr_normalContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterExprNormal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitExprNormal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitExprNormal(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Expr_variableContext extends ExprContext {
		public TerminalNode VARIABLE() { return getToken(QuoteAttrFormulaParser.VARIABLE, 0); }
		public Expr_variableContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterExprVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitExprVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitExprVariable(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Expr_decimalContext extends ExprContext {
		public TerminalNode DECIMAL() { return getToken(QuoteAttrFormulaParser.DECIMAL, 0); }
		public Expr_decimalContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterExprDecimal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitExprDecimal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitExprDecimal(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Expr_refContext extends ExprContext {
		public RefContext ref() {
			return getRuleContext(RefContext.class,0);
		}
		public Expr_refContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterExprRef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitExprRef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitExprRef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		return expr(0);
	}

	private ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState);
		ExprContext _prevctx = _localctx;
		int _startState = 2;
		enterRecursionRule(_localctx, 2, RULE_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(144);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				{
				_localctx = new Expr_normalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(55);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(52);
					match(T__0);
					}
					}
					setState(57);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(58);
				match(T__1);
				setState(62);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
				while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(59);
						match(T__0);
						}
						} 
					}
					setState(64);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
				}
				setState(65);
				expr(0);
				setState(69);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(66);
					match(T__0);
					}
					}
					setState(71);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(72);
				match(T__2);
				setState(76);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
				while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(73);
						match(T__0);
						}
						} 
					}
					setState(78);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
				}
				}
				break;
			case 2:
				{
				_localctx = new Expr_refContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(82);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(79);
					match(T__0);
					}
					}
					setState(84);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(85);
				ref();
				setState(89);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
				while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(86);
						match(T__0);
						}
						} 
					}
					setState(91);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
				}
				}
				break;
			case 3:
				{
				_localctx = new Expr_funContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(95);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(92);
					match(T__0);
					}
					}
					setState(97);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(98);
				fun();
				setState(102);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
				while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(99);
						match(T__0);
						}
						} 
					}
					setState(104);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
				}
				}
				break;
			case 4:
				{
				_localctx = new Expr_groupContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(108);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(105);
					match(T__0);
					}
					}
					setState(110);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(111);
				group_func();
				setState(115);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
				while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(112);
						match(T__0);
						}
						} 
					}
					setState(117);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
				}
				}
				break;
			case 5:
				{
				_localctx = new Expr_decimalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(121);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(118);
					match(T__0);
					}
					}
					setState(123);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(124);
				match(DECIMAL);
				setState(128);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
				while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(125);
						match(T__0);
						}
						} 
					}
					setState(130);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
				}
				}
				break;
			case 6:
				{
				_localctx = new Expr_variableContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(134);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(131);
					match(T__0);
					}
					}
					setState(136);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(137);
				match(VARIABLE);
				setState(141);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
				while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(138);
						match(T__0);
						}
						} 
					}
					setState(143);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
				}
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(178);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(176);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
					case 1:
						{
						_localctx = new Expr_operateContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(146);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(150);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==T__0) {
							{
							{
							setState(147);
							match(T__0);
							}
							}
							setState(152);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(153);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__3) | (1L << T__4) | (1L << T__5))) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)== Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(157);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
						while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
							if ( _alt==1 ) {
								{
								{
								setState(154);
								match(T__0);
								}
								} 
							}
							setState(159);
							_errHandler.sync(this);
							_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
						}
						setState(160);
						expr(8);
						}
						break;
					case 2:
						{
						_localctx = new Expr_operateContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(161);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(165);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==T__0) {
							{
							{
							setState(162);
							match(T__0);
							}
							}
							setState(167);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(168);
						_la = _input.LA(1);
						if ( !(_la==T__6 || _la==T__7) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)== Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(172);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
						while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
							if ( _alt==1 ) {
								{
								{
								setState(169);
								match(T__0);
								}
								} 
							}
							setState(174);
							_errHandler.sync(this);
							_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
						}
						setState(175);
						expr(7);
						}
						break;
					}
					} 
				}
				setState(180);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class RefContext extends ParserRuleContext {
		public TerminalNode KEY_REF() { return getToken(QuoteAttrFormulaParser.KEY_REF, 0); }
		public TerminalNode VARIABLE_NAME() { return getToken(QuoteAttrFormulaParser.VARIABLE_NAME, 0); }
		public Ref_selectContext ref_select() {
			return getRuleContext(Ref_selectContext.class,0);
		}
		public RefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ref; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RefContext ref() throws RecognitionException {
		RefContext _localctx = new RefContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_ref);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(181);
			match(KEY_REF);
			setState(182);
			match(T__8);
			setState(183);
			match(VARIABLE_NAME);
			setState(184);
			match(T__1);
			setState(188);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(185);
				match(T__0);
				}
				}
				setState(190);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(191);
			ref_select();
			setState(195);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(192);
				match(T__0);
				}
				}
				setState(197);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(198);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Ref_selectContext extends ParserRuleContext {
		public Ref_selectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ref_select; }
	 
		public Ref_selectContext() { }
		public void copyFrom(Ref_selectContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class Ref_select_error2Context extends Ref_selectContext {
		public List<Ref_return_fragContext> ref_return_frag() {
			return getRuleContexts(Ref_return_fragContext.class);
		}
		public Ref_return_fragContext ref_return_frag(int i) {
			return getRuleContext(Ref_return_fragContext.class,i);
		}
		public List<Ref_select_fragContext> ref_select_frag() {
			return getRuleContexts(Ref_select_fragContext.class);
		}
		public Ref_select_fragContext ref_select_frag(int i) {
			return getRuleContext(Ref_select_fragContext.class,i);
		}
		public List<Ref_where_fragContext> ref_where_frag() {
			return getRuleContexts(Ref_where_fragContext.class);
		}
		public Ref_where_fragContext ref_where_frag(int i) {
			return getRuleContext(Ref_where_fragContext.class,i);
		}
		public Ref_select_error2Context(Ref_selectContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefSelectRrror2(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefSelectError2(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefSelectError2(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Ref_select_error1Context extends Ref_selectContext {
		public List<Ref_select_fragContext> ref_select_frag() {
			return getRuleContexts(Ref_select_fragContext.class);
		}
		public Ref_select_fragContext ref_select_frag(int i) {
			return getRuleContext(Ref_select_fragContext.class,i);
		}
		public List<Ref_where_fragContext> ref_where_frag() {
			return getRuleContexts(Ref_where_fragContext.class);
		}
		public Ref_where_fragContext ref_where_frag(int i) {
			return getRuleContext(Ref_where_fragContext.class,i);
		}
		public Ref_select_error1Context(Ref_selectContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefSelectError1(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefSelectRrror1(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefSelectError1(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Ref_select_valid4Context extends Ref_selectContext {
		public Ref_return_fragContext ref_return_frag() {
			return getRuleContext(Ref_return_fragContext.class,0);
		}
		public Ref_where_fragContext ref_where_frag() {
			return getRuleContext(Ref_where_fragContext.class,0);
		}
		public Ref_select_valid4Context(Ref_selectContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefSelectValid4(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefSelectValid4(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefSelectValid4(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Ref_select_valid3Context extends Ref_selectContext {
		public Ref_where_fragContext ref_where_frag() {
			return getRuleContext(Ref_where_fragContext.class,0);
		}
		public Ref_return_fragContext ref_return_frag() {
			return getRuleContext(Ref_return_fragContext.class,0);
		}
		public Ref_select_valid3Context(Ref_selectContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefSelectValid3(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefSelectValid3(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefSelectValid3(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Ref_select_valid2Context extends Ref_selectContext {
		public Ref_return_fragContext ref_return_frag() {
			return getRuleContext(Ref_return_fragContext.class,0);
		}
		public List<Ref_select_fragContext> ref_select_frag() {
			return getRuleContexts(Ref_select_fragContext.class);
		}
		public Ref_select_fragContext ref_select_frag(int i) {
			return getRuleContext(Ref_select_fragContext.class,i);
		}
		public Ref_select_valid2Context(Ref_selectContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefSelectValid2(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefSelectValid2(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefSelectValid2(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Ref_select_valid1Context extends Ref_selectContext {
		public Ref_return_fragContext ref_return_frag() {
			return getRuleContext(Ref_return_fragContext.class,0);
		}
		public List<Ref_select_fragContext> ref_select_frag() {
			return getRuleContexts(Ref_select_fragContext.class);
		}
		public Ref_select_fragContext ref_select_frag(int i) {
			return getRuleContext(Ref_select_fragContext.class,i);
		}
		public Ref_select_valid1Context(Ref_selectContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefSelectValid1(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefSelectValid1(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefSelectValid1(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Ref_selectContext ref_select() throws RecognitionException {
		Ref_selectContext _localctx = new Ref_selectContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_ref_select);
		int _la;
		try {
			int _alt;
			setState(545);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,84,_ctx) ) {
			case 1:
				_localctx = new Ref_select_valid1Context(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(203);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__9) {
					{
					{
					setState(200);
					match(T__9);
					}
					}
					setState(205);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(226);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARIABLE) | (1L << VARIABLE_REF) | (1L << VARIABLE_NAME) | (1L << ERROR_VAR_COIN_BEGIN1))) != 0)) {
					{
					{
					setState(206);
					ref_select_frag();
					setState(210);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==T__0) {
						{
						{
						setState(207);
						match(T__0);
						}
						}
						setState(212);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(220); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(213);
						match(T__9);
						setState(217);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==T__0) {
							{
							{
							setState(214);
							match(T__0);
							}
							}
							setState(219);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						}
						}
						setState(222); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==T__9 );
					}
					}
					setState(228);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(229);
				ref_return_frag();
				setState(239);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__9) {
					{
					{
					setState(230);
					match(T__9);
					setState(234);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,28,_ctx);
					while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1 ) {
							{
							{
							setState(231);
							match(T__0);
							}
							} 
						}
						setState(236);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,28,_ctx);
					}
					}
					}
					setState(241);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				_localctx = new Ref_select_valid2Context(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(245);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__9) {
					{
					{
					setState(242);
					match(T__9);
					}
					}
					setState(247);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(248);
				ref_return_frag();
				setState(252);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(249);
					match(T__0);
					}
					}
					setState(254);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(262); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(255);
					match(T__9);
					setState(259);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==T__0) {
						{
						{
						setState(256);
						match(T__0);
						}
						}
						setState(261);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
					}
					setState(264); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__9 );
				setState(286);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,37,_ctx);
				while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(266);
						ref_select_frag();
						setState(270);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==T__0) {
							{
							{
							setState(267);
							match(T__0);
							}
							}
							setState(272);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(280); 
						_errHandler.sync(this);
						_la = _input.LA(1);
						do {
							{
							{
							setState(273);
							match(T__9);
							setState(277);
							_errHandler.sync(this);
							_la = _input.LA(1);
							while (_la==T__0) {
								{
								{
								setState(274);
								match(T__0);
								}
								}
								setState(279);
								_errHandler.sync(this);
								_la = _input.LA(1);
							}
							}
							}
							setState(282); 
							_errHandler.sync(this);
							_la = _input.LA(1);
						} while ( _la==T__9 );
						}
						} 
					}
					setState(288);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,37,_ctx);
				}
				setState(289);
				ref_select_frag();
				setState(299);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__9) {
					{
					{
					setState(290);
					match(T__9);
					setState(294);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,38,_ctx);
					while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1 ) {
							{
							{
							setState(291);
							match(T__0);
							}
							} 
						}
						setState(296);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,38,_ctx);
					}
					}
					}
					setState(301);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 3:
				_localctx = new Ref_select_valid3Context(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(305);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__9) {
					{
					{
					setState(302);
					match(T__9);
					}
					}
					setState(307);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(308);
				ref_where_frag();
				setState(312);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(309);
					match(T__0);
					}
					}
					setState(314);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(322); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(315);
					match(T__9);
					setState(319);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==T__0) {
						{
						{
						setState(316);
						match(T__0);
						}
						}
						setState(321);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
					}
					setState(324); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__9 );
				setState(326);
				ref_return_frag();
				setState(336);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__9) {
					{
					{
					setState(327);
					match(T__9);
					setState(331);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,44,_ctx);
					while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1 ) {
							{
							{
							setState(328);
							match(T__0);
							}
							} 
						}
						setState(333);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,44,_ctx);
					}
					}
					}
					setState(338);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 4:
				_localctx = new Ref_select_valid4Context(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(342);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__9) {
					{
					{
					setState(339);
					match(T__9);
					}
					}
					setState(344);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(345);
				ref_return_frag();
				setState(349);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(346);
					match(T__0);
					}
					}
					setState(351);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(359); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(352);
					match(T__9);
					setState(356);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==T__0) {
						{
						{
						setState(353);
						match(T__0);
						}
						}
						setState(358);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
					}
					setState(361); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__9 );
				setState(363);
				ref_where_frag();
				setState(373);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__9) {
					{
					{
					setState(364);
					match(T__9);
					setState(368);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,50,_ctx);
					while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1 ) {
							{
							{
							setState(365);
							match(T__0);
							}
							} 
						}
						setState(370);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,50,_ctx);
					}
					}
					}
					setState(375);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 5:
				_localctx = new Ref_select_error1Context(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(379);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__9) {
					{
					{
					setState(376);
					match(T__9);
					}
					}
					setState(381);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(405);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,57,_ctx);
				while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(384);
						_errHandler.sync(this);
						switch (_input.LA(1)) {
						case VARIABLE:
						case VARIABLE_REF:
						case VARIABLE_NAME:
						case ERROR_VAR_COIN_BEGIN1:
							{
							setState(382);
							ref_select_frag();
							}
							break;
						case KEY_WHERE:
							{
							setState(383);
							ref_where_frag();
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						setState(389);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==T__0) {
							{
							{
							setState(386);
							match(T__0);
							}
							}
							setState(391);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(399); 
						_errHandler.sync(this);
						_la = _input.LA(1);
						do {
							{
							{
							setState(392);
							match(T__9);
							setState(396);
							_errHandler.sync(this);
							_la = _input.LA(1);
							while (_la==T__0) {
								{
								{
								setState(393);
								match(T__0);
								}
								}
								setState(398);
								_errHandler.sync(this);
								_la = _input.LA(1);
							}
							}
							}
							setState(401); 
							_errHandler.sync(this);
							_la = _input.LA(1);
						} while ( _la==T__9 );
						}
						} 
					}
					setState(407);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,57,_ctx);
				}
				setState(410);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case VARIABLE:
				case VARIABLE_REF:
				case VARIABLE_NAME:
				case ERROR_VAR_COIN_BEGIN1:
					{
					setState(408);
					ref_select_frag();
					}
					break;
				case KEY_WHERE:
					{
					setState(409);
					ref_where_frag();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(421);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__9) {
					{
					{
					setState(412);
					match(T__9);
					setState(416);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,59,_ctx);
					while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1 ) {
							{
							{
							setState(413);
							match(T__0);
							}
							} 
						}
						setState(418);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,59,_ctx);
					}
					}
					}
					setState(423);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 6:
				_localctx = new Ref_select_error2Context(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(427);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__9) {
					{
					{
					setState(424);
					match(T__9);
					}
					}
					setState(429);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(453);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << KEY_WHERE) | (1L << VARIABLE) | (1L << VARIABLE_REF) | (1L << VARIABLE_NAME) | (1L << ERROR_VAR_COIN_BEGIN1))) != 0)) {
					{
					{
					setState(432);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case VARIABLE:
					case VARIABLE_REF:
					case VARIABLE_NAME:
					case ERROR_VAR_COIN_BEGIN1:
						{
						setState(430);
						ref_select_frag();
						}
						break;
					case KEY_WHERE:
						{
						setState(431);
						ref_where_frag();
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(437);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==T__0) {
						{
						{
						setState(434);
						match(T__0);
						}
						}
						setState(439);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(447); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(440);
						match(T__9);
						setState(444);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==T__0) {
							{
							{
							setState(441);
							match(T__0);
							}
							}
							setState(446);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						}
						}
						setState(449); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==T__9 );
					}
					}
					setState(455);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(474); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(456);
						ref_return_frag();
						setState(460);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==T__0) {
							{
							{
							setState(457);
							match(T__0);
							}
							}
							setState(462);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(470); 
						_errHandler.sync(this);
						_la = _input.LA(1);
						do {
							{
							{
							setState(463);
							match(T__9);
							setState(467);
							_errHandler.sync(this);
							_la = _input.LA(1);
							while (_la==T__0) {
								{
								{
								setState(464);
								match(T__0);
								}
								}
								setState(469);
								_errHandler.sync(this);
								_la = _input.LA(1);
							}
							}
							}
							setState(472); 
							_errHandler.sync(this);
							_la = _input.LA(1);
						} while ( _la==T__9 );
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(476); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,70,_ctx);
				} while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER );
				setState(501);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << KEY_WHERE) | (1L << VARIABLE) | (1L << VARIABLE_REF) | (1L << VARIABLE_NAME) | (1L << ERROR_VAR_COIN_BEGIN1))) != 0)) {
					{
					{
					setState(480);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case VARIABLE:
					case VARIABLE_REF:
					case VARIABLE_NAME:
					case ERROR_VAR_COIN_BEGIN1:
						{
						setState(478);
						ref_select_frag();
						}
						break;
					case KEY_WHERE:
						{
						setState(479);
						ref_where_frag();
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(485);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==T__0) {
						{
						{
						setState(482);
						match(T__0);
						}
						}
						setState(487);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(495); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(488);
						match(T__9);
						setState(492);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==T__0) {
							{
							{
							setState(489);
							match(T__0);
							}
							}
							setState(494);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						}
						}
						setState(497); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==T__9 );
					}
					}
					setState(503);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(531);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,81,_ctx) ) {
				case 1:
					{
					setState(522); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(504);
						ref_return_frag();
						setState(508);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==T__0) {
							{
							{
							setState(505);
							match(T__0);
							}
							}
							setState(510);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(518); 
						_errHandler.sync(this);
						_la = _input.LA(1);
						do {
							{
							{
							setState(511);
							match(T__9);
							setState(515);
							_errHandler.sync(this);
							_la = _input.LA(1);
							while (_la==T__0) {
								{
								{
								setState(512);
								match(T__0);
								}
								}
								setState(517);
								_errHandler.sync(this);
								_la = _input.LA(1);
							}
							}
							}
							setState(520); 
							_errHandler.sync(this);
							_la = _input.LA(1);
						} while ( _la==T__9 );
						}
						}
						setState(524); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==KEY_RETURN );
					setState(528);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case VARIABLE:
					case VARIABLE_REF:
					case VARIABLE_NAME:
					case ERROR_VAR_COIN_BEGIN1:
						{
						setState(526);
						ref_select_frag();
						}
						break;
					case KEY_WHERE:
						{
						setState(527);
						ref_where_frag();
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					}
					break;
				case 2:
					{
					{
					setState(530);
					ref_return_frag();
					}
					}
					break;
				}
				setState(542);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__9) {
					{
					{
					setState(533);
					match(T__9);
					setState(537);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,82,_ctx);
					while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1 ) {
							{
							{
							setState(534);
							match(T__0);
							}
							} 
						}
						setState(539);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,82,_ctx);
					}
					}
					}
					setState(544);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Ref_select_fragContext extends ParserRuleContext {
		public Ref_select_fragContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ref_select_frag; }
	 
		public Ref_select_fragContext() { }
		public void copyFrom(Ref_select_fragContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class Ref_select_frag_nsContext extends Ref_select_fragContext {
		public TerminalNode VARIABLE_REF() { return getToken(QuoteAttrFormulaParser.VARIABLE_REF, 0); }
		public Ref_select_expr_nsContext ref_select_expr_ns() {
			return getRuleContext(Ref_select_expr_nsContext.class,0);
		}
		public TerminalNode KEY_LIKE() { return getToken(QuoteAttrFormulaParser.KEY_LIKE, 0); }
		public Ref_select_frag_nsContext(Ref_select_fragContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefSelectFragNs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefSelectFragNs(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefSelectFragNs(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Ref_select_frag_error2Context extends Ref_select_fragContext {
		public List<TerminalNode> VARIABLE_NAME() { return getTokens(QuoteAttrFormulaParser.VARIABLE_NAME); }
		public TerminalNode VARIABLE_NAME(int i) {
			return getToken(QuoteAttrFormulaParser.VARIABLE_NAME, i);
		}
		public Ref_select_expr_sContext ref_select_expr_s() {
			return getRuleContext(Ref_select_expr_sContext.class,0);
		}
		public Ref_select_expr_nsContext ref_select_expr_ns() {
			return getRuleContext(Ref_select_expr_nsContext.class,0);
		}
		public TerminalNode KEY_LIKE() { return getToken(QuoteAttrFormulaParser.KEY_LIKE, 0); }
		public Ref_select_frag_error2Context(Ref_select_fragContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefSelectFragError2(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefSelectFragError2(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefSelectFragError2(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Ref_select_frag_sContext extends Ref_select_fragContext {
		public TerminalNode VARIABLE_REF() { return getToken(QuoteAttrFormulaParser.VARIABLE_REF, 0); }
		public Ref_select_expr_sContext ref_select_expr_s() {
			return getRuleContext(Ref_select_expr_sContext.class,0);
		}
		public TerminalNode KEY_LIKE() { return getToken(QuoteAttrFormulaParser.KEY_LIKE, 0); }
		public Ref_select_frag_sContext(Ref_select_fragContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefSelectFragS(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefSelectFragS(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefSelectFragS(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Ref_select_frag_error1Context extends Ref_select_fragContext {
		public TerminalNode VARIABLE() { return getToken(QuoteAttrFormulaParser.VARIABLE, 0); }
		public TerminalNode ERROR_VAR_COIN_BEGIN1() { return getToken(QuoteAttrFormulaParser.ERROR_VAR_COIN_BEGIN1, 0); }
		public Ref_select_expr_sContext ref_select_expr_s() {
			return getRuleContext(Ref_select_expr_sContext.class,0);
		}
		public Ref_select_expr_nsContext ref_select_expr_ns() {
			return getRuleContext(Ref_select_expr_nsContext.class,0);
		}
		public TerminalNode KEY_LIKE() { return getToken(QuoteAttrFormulaParser.KEY_LIKE, 0); }
		public Ref_select_frag_error1Context(Ref_select_fragContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefSelectFragError1(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefSelectFragError1(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefSelectFragError1(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Ref_select_fragContext ref_select_frag() throws RecognitionException {
		Ref_select_fragContext _localctx = new Ref_select_fragContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_ref_select_frag);
		int _la;
		try {
			int _alt;
			setState(626);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,97,_ctx) ) {
			case 1:
				_localctx = new Ref_select_frag_sContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(547);
				match(VARIABLE_REF);
				{
				setState(551);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(548);
					match(T__0);
					}
					}
					setState(553);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(554);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__10) | (1L << T__11) | (1L << KEY_LIKE))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)== Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(558);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(555);
					match(T__0);
					}
					}
					setState(560);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				setState(561);
				ref_select_expr_s(0);
				}
				break;
			case 2:
				_localctx = new Ref_select_frag_nsContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(562);
				match(VARIABLE_REF);
				{
				setState(566);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(563);
					match(T__0);
					}
					}
					setState(568);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(569);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__10) | (1L << T__11) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << KEY_LIKE))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)== Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(573);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(570);
					match(T__0);
					}
					}
					setState(575);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				setState(576);
				ref_select_expr_ns(0);
				}
				break;
			case 3:
				_localctx = new Ref_select_frag_error1Context(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(577);
				_la = _input.LA(1);
				if ( !(_la==VARIABLE || _la==ERROR_VAR_COIN_BEGIN1) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)== Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				{
				setState(581);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(578);
					match(T__0);
					}
					}
					setState(583);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(584);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__10) | (1L << T__11) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << KEY_LIKE))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)== Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(588);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(585);
					match(T__0);
					}
					}
					setState(590);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				setState(593);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,91,_ctx) ) {
				case 1:
					{
					setState(591);
					ref_select_expr_s(0);
					}
					break;
				case 2:
					{
					setState(592);
					ref_select_expr_ns(0);
					}
					break;
				}
				}
				break;
			case 4:
				_localctx = new Ref_select_frag_error2Context(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				{
				setState(604);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,93,_ctx);
				while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(595);
						match(VARIABLE_NAME);
						setState(599);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==T__0) {
							{
							{
							setState(596);
							match(T__0);
							}
							}
							setState(601);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						}
						} 
					}
					setState(606);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,93,_ctx);
				}
				setState(607);
				match(VARIABLE_NAME);
				}
				{
				setState(612);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(609);
					match(T__0);
					}
					}
					setState(614);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(615);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__10) | (1L << T__11) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << KEY_LIKE))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)== Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(619);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(616);
					match(T__0);
					}
					}
					setState(621);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				setState(624);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,96,_ctx) ) {
				case 1:
					{
					setState(622);
					ref_select_expr_s(0);
					}
					break;
				case 2:
					{
					setState(623);
					ref_select_expr_ns(0);
					}
					break;
				}
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Ref_return_fragContext extends ParserRuleContext {
		public TerminalNode KEY_RETURN() { return getToken(QuoteAttrFormulaParser.KEY_RETURN, 0); }
		public Ref_return_exprContext ref_return_expr() {
			return getRuleContext(Ref_return_exprContext.class,0);
		}
		public Ref_return_fragContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ref_return_frag; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefFeturnFrag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefReturnFrag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefReturnFrag(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Ref_return_fragContext ref_return_frag() throws RecognitionException {
		Ref_return_fragContext _localctx = new Ref_return_fragContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_ref_return_frag);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(628);
			match(KEY_RETURN);
			setState(632);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(629);
				match(T__0);
				}
				}
				setState(634);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(635);
			match(T__10);
			setState(639);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(636);
				match(T__0);
				}
				}
				setState(641);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(642);
			ref_return_expr(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Ref_select_expr_sContext extends ParserRuleContext {
		public Ref_select_expr_sContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ref_select_expr_s; }
	 
		public Ref_select_expr_sContext() { }
		public void copyFrom(Ref_select_expr_sContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class Ref_select_expr_s_terminalContext extends Ref_select_expr_sContext {
		public TerminalNode STRING() { return getToken(QuoteAttrFormulaParser.STRING, 0); }
		public Ref_select_expr_s_terminalContext(Ref_select_expr_sContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefSelectExprsTerminal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefSelectExprsTerminal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefSelectExprsTerminal(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Ref_select_expr_s_addContext extends Ref_select_expr_sContext {
		public Ref_select_expr_nsContext ref_select_expr_ns() {
			return getRuleContext(Ref_select_expr_nsContext.class,0);
		}
		public List<Ref_select_expr_sContext> ref_select_expr_s() {
			return getRuleContexts(Ref_select_expr_sContext.class);
		}
		public Ref_select_expr_sContext ref_select_expr_s(int i) {
			return getRuleContext(Ref_select_expr_sContext.class,i);
		}
		public Ref_select_expr_s_addContext(Ref_select_expr_sContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefSelectExprsAdd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefSelectExprsAdd(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefSelectExprsAdd(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Ref_select_expr_s_normalContext extends Ref_select_expr_sContext {
		public Ref_select_expr_sContext ref_select_expr_s() {
			return getRuleContext(Ref_select_expr_sContext.class,0);
		}
		public Ref_select_expr_s_normalContext(Ref_select_expr_sContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefSelectExprsNormal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefSelectExprsNormal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefSelectExprsNormal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Ref_select_expr_sContext ref_select_expr_s() throws RecognitionException {
		return ref_select_expr_s(0);
	}

	private Ref_select_expr_sContext ref_select_expr_s(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Ref_select_expr_sContext _localctx = new Ref_select_expr_sContext(_ctx, _parentState);
		Ref_select_expr_sContext _prevctx = _localctx;
		int _startState = 12;
		enterRecursionRule(_localctx, 12, RULE_ref_select_expr_s, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(678);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,104,_ctx) ) {
			case 1:
				{
				_localctx = new Ref_select_expr_s_normalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(645);
				match(T__1);
				setState(649);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(646);
					match(T__0);
					}
					}
					setState(651);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(652);
				ref_select_expr_s(0);
				setState(656);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(653);
					match(T__0);
					}
					}
					setState(658);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(659);
				match(T__2);
				}
				break;
			case 2:
				{
				_localctx = new Ref_select_expr_s_addContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(661);
				ref_select_expr_ns(0);
				setState(665);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(662);
					match(T__0);
					}
					}
					setState(667);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(668);
				match(T__6);
				setState(672);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(669);
					match(T__0);
					}
					}
					setState(674);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(675);
				ref_select_expr_s(2);
				}
				break;
			case 3:
				{
				_localctx = new Ref_select_expr_s_terminalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(677);
				match(STRING);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(712);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,110,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(710);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,109,_ctx) ) {
					case 1:
						{
						_localctx = new Ref_select_expr_s_addContext(new Ref_select_expr_sContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_ref_select_expr_s);
						setState(680);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(684);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==T__0) {
							{
							{
							setState(681);
							match(T__0);
							}
							}
							setState(686);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(687);
						match(T__6);
						setState(691);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==T__0) {
							{
							{
							setState(688);
							match(T__0);
							}
							}
							setState(693);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(694);
						ref_select_expr_s(5);
						}
						break;
					case 2:
						{
						_localctx = new Ref_select_expr_s_addContext(new Ref_select_expr_sContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_ref_select_expr_s);
						setState(695);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(699);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==T__0) {
							{
							{
							setState(696);
							match(T__0);
							}
							}
							setState(701);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(702);
						match(T__6);
						setState(706);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==T__0) {
							{
							{
							setState(703);
							match(T__0);
							}
							}
							setState(708);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(709);
						ref_select_expr_ns(0);
						}
						break;
					}
					} 
				}
				setState(714);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,110,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Ref_select_expr_nsContext extends ParserRuleContext {
		public Ref_select_expr_nsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ref_select_expr_ns; }
	 
		public Ref_select_expr_nsContext() { }
		public void copyFrom(Ref_select_expr_nsContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class Ref_select_expr_ns_terminalContext extends Ref_select_expr_nsContext {
		public TerminalNode DECIMAL() { return getToken(QuoteAttrFormulaParser.DECIMAL, 0); }
		public Ref_select_expr_ns_terminalContext(Ref_select_expr_nsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefSelectExprNsTerminal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefSelectExprNsTerminal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefSelectExprNsTerminal(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Ref_select_expr_ns_normalContext extends Ref_select_expr_nsContext {
		public Ref_select_expr_nsContext ref_select_expr_ns() {
			return getRuleContext(Ref_select_expr_nsContext.class,0);
		}
		public Ref_select_expr_ns_normalContext(Ref_select_expr_nsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefSelectExprNsNormal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefSelectExprNsNormal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefSelectExprNsNormal(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Ref_select_expr_ns_fieldContext extends Ref_select_expr_nsContext {
		public TerminalNode VARIABLE_REF() { return getToken(QuoteAttrFormulaParser.VARIABLE_REF, 0); }
		public Ref_select_expr_ns_fieldContext(Ref_select_expr_nsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefSelectExprNsField(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefSelectExprNsField(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefSelectExprNsField(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Ref_select_expr_ns_error1Context extends Ref_select_expr_nsContext {
		public TerminalNode VARIABLE_NAME() { return getToken(QuoteAttrFormulaParser.VARIABLE_NAME, 0); }
		public Ref_select_expr_ns_error1Context(Ref_select_expr_nsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefSelectExprNsError1(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefSelectExprNsError1(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefSelectExprNsError1(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Ref_select_expr_ns_error2Context extends Ref_select_expr_nsContext {
		public List<TerminalNode> DIGIT() { return getTokens(QuoteAttrFormulaParser.DIGIT); }
		public TerminalNode DIGIT(int i) {
			return getToken(QuoteAttrFormulaParser.DIGIT, i);
		}
		public Ref_select_expr_ns_error2Context(Ref_select_expr_nsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefSelectExprNsError2(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefSelectExprNsError2(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefSelectExprNsError2(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Ref_select_expr_ns_variableContext extends Ref_select_expr_nsContext {
		public TerminalNode VARIABLE() { return getToken(QuoteAttrFormulaParser.VARIABLE, 0); }
		public Ref_select_expr_ns_variableContext(Ref_select_expr_nsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefSelectExprNsVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefSelectExprNsVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefSelectExprNsVariable(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Ref_select_expr_ns_operateContext extends Ref_select_expr_nsContext {
		public List<Ref_select_expr_nsContext> ref_select_expr_ns() {
			return getRuleContexts(Ref_select_expr_nsContext.class);
		}
		public Ref_select_expr_nsContext ref_select_expr_ns(int i) {
			return getRuleContext(Ref_select_expr_nsContext.class,i);
		}
		public Ref_select_expr_ns_operateContext(Ref_select_expr_nsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefSelectExprNsOperate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefSelectExprNsOperate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefSelectExprNsOperate(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Ref_select_expr_nsContext ref_select_expr_ns() throws RecognitionException {
		return ref_select_expr_ns(0);
	}

	private Ref_select_expr_nsContext ref_select_expr_ns(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Ref_select_expr_nsContext _localctx = new Ref_select_expr_nsContext(_ctx, _parentState);
		Ref_select_expr_nsContext _prevctx = _localctx;
		int _startState = 14;
		enterRecursionRule(_localctx, 14, RULE_ref_select_expr_ns, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(773);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__1:
				{
				_localctx = new Ref_select_expr_ns_normalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(716);
				match(T__1);
				setState(720);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(717);
					match(T__0);
					}
					}
					setState(722);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(723);
				ref_select_expr_ns(0);
				setState(727);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(724);
					match(T__0);
					}
					}
					setState(729);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(730);
				match(T__2);
				}
				break;
			case DECIMAL:
				{
				_localctx = new Ref_select_expr_ns_terminalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(732);
				match(DECIMAL);
				}
				break;
			case VARIABLE:
				{
				_localctx = new Ref_select_expr_ns_variableContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(733);
				match(VARIABLE);
				}
				break;
			case VARIABLE_REF:
				{
				_localctx = new Ref_select_expr_ns_fieldContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(734);
				match(VARIABLE_REF);
				}
				break;
			case VARIABLE_NAME:
				{
				_localctx = new Ref_select_expr_ns_error1Context(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(735);
				match(VARIABLE_NAME);
				}
				break;
			case T__8:
			case DIGIT:
				{
				_localctx = new Ref_select_expr_ns_error2Context(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(771);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,119,_ctx) ) {
				case 1:
					{
					{
					setState(737); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(736);
						match(DIGIT);
						}
						}
						setState(739); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==DIGIT );
					setState(741);
					match(T__8);
					}
					}
					break;
				case 2:
					{
					{
					setState(742);
					match(T__8);
					setState(744); 
					_errHandler.sync(this);
					_alt = 1;
					do {
						switch (_alt) {
						case 1:
							{
							{
							setState(743);
							match(DIGIT);
							}
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						setState(746); 
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,114,_ctx);
					} while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER );
					}
					}
					break;
				case 3:
					{
					{
					setState(749); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(748);
						match(DIGIT);
						}
						}
						setState(751); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==DIGIT );
					setState(753);
					match(T__8);
					setState(767); 
					_errHandler.sync(this);
					_alt = 1;
					do {
						switch (_alt) {
						case 1:
							{
							{
							setState(757);
							_errHandler.sync(this);
							_la = _input.LA(1);
							while (_la==DIGIT) {
								{
								{
								setState(754);
								match(DIGIT);
								}
								}
								setState(759);
								_errHandler.sync(this);
								_la = _input.LA(1);
							}
							setState(760);
							match(T__8);
							setState(764);
							_errHandler.sync(this);
							_alt = getInterpreter().adaptivePredict(_input,117,_ctx);
							while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
								if ( _alt==1 ) {
									{
									{
									setState(761);
									match(DIGIT);
									}
									} 
								}
								setState(766);
								_errHandler.sync(this);
								_alt = getInterpreter().adaptivePredict(_input,117,_ctx);
							}
							}
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						setState(769); 
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,118,_ctx);
					} while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER );
					}
					}
					break;
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(807);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,126,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(805);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,125,_ctx) ) {
					case 1:
						{
						_localctx = new Ref_select_expr_ns_operateContext(new Ref_select_expr_nsContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_ref_select_expr_ns);
						setState(775);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(779);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==T__0) {
							{
							{
							setState(776);
							match(T__0);
							}
							}
							setState(781);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(782);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__3) | (1L << T__4) | (1L << T__5))) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)== Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(786);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==T__0) {
							{
							{
							setState(783);
							match(T__0);
							}
							}
							setState(788);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(789);
						ref_select_expr_ns(8);
						}
						break;
					case 2:
						{
						_localctx = new Ref_select_expr_ns_operateContext(new Ref_select_expr_nsContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_ref_select_expr_ns);
						setState(790);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(794);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==T__0) {
							{
							{
							setState(791);
							match(T__0);
							}
							}
							setState(796);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(797);
						_la = _input.LA(1);
						if ( !(_la==T__6 || _la==T__7) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)== Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(801);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==T__0) {
							{
							{
							setState(798);
							match(T__0);
							}
							}
							setState(803);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(804);
						ref_select_expr_ns(7);
						}
						break;
					}
					} 
				}
				setState(809);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,126,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Ref_return_exprContext extends ParserRuleContext {
		public Ref_return_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ref_return_expr; }
	 
		public Ref_return_exprContext() { }
		public void copyFrom(Ref_return_exprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class Ref_return_expr_variableContext extends Ref_return_exprContext {
		public TerminalNode VARIABLE() { return getToken(QuoteAttrFormulaParser.VARIABLE, 0); }
		public Ref_return_expr_variableContext(Ref_return_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefReturnExprVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefReturnExprVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefReturnExprVariable(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Ref_return_expr_terminalContext extends Ref_return_exprContext {
		public TerminalNode DECIMAL() { return getToken(QuoteAttrFormulaParser.DECIMAL, 0); }
		public Ref_return_expr_terminalContext(Ref_return_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefReturnExprTerminal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefReturnExprTerminal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefReturnExprTerminal(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Ref_return_expr_fieldContext extends Ref_return_exprContext {
		public TerminalNode VARIABLE_REF() { return getToken(QuoteAttrFormulaParser.VARIABLE_REF, 0); }
		public Ref_return_expr_fieldContext(Ref_return_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefReturnExprField(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefReturnExprField(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefReturnExprField(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Ref_return_expr_normalContext extends Ref_return_exprContext {
		public Ref_return_exprContext ref_return_expr() {
			return getRuleContext(Ref_return_exprContext.class,0);
		}
		public Ref_return_expr_normalContext(Ref_return_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefReturnExprNormal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefReturnExprNormal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefReturnExprNormal(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Ref_return_expr_error2Context extends Ref_return_exprContext {
		public List<TerminalNode> DIGIT() { return getTokens(QuoteAttrFormulaParser.DIGIT); }
		public TerminalNode DIGIT(int i) {
			return getToken(QuoteAttrFormulaParser.DIGIT, i);
		}
		public Ref_return_expr_error2Context(Ref_return_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefReturnExprError2(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefReturnExprError2(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefReturnExprError2(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Ref_return_expr_error1Context extends Ref_return_exprContext {
		public TerminalNode VARIABLE_NAME() { return getToken(QuoteAttrFormulaParser.VARIABLE_NAME, 0); }
		public Ref_return_expr_error1Context(Ref_return_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefReturnExprError1(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefReturnExprError1(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefReturnExprError1(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Ref_return_expr_operateContext extends Ref_return_exprContext {
		public List<Ref_return_exprContext> ref_return_expr() {
			return getRuleContexts(Ref_return_exprContext.class);
		}
		public Ref_return_exprContext ref_return_expr(int i) {
			return getRuleContext(Ref_return_exprContext.class,i);
		}
		public Ref_return_expr_operateContext(Ref_return_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefReturnExprOperate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefReturnExprOperate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefReturnExprOperate(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Ref_return_exprContext ref_return_expr() throws RecognitionException {
		return ref_return_expr(0);
	}

	private Ref_return_exprContext ref_return_expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Ref_return_exprContext _localctx = new Ref_return_exprContext(_ctx, _parentState);
		Ref_return_exprContext _prevctx = _localctx;
		int _startState = 16;
		enterRecursionRule(_localctx, 16, RULE_ref_return_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(868);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__1:
				{
				_localctx = new Ref_return_expr_normalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(811);
				match(T__1);
				setState(815);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(812);
					match(T__0);
					}
					}
					setState(817);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(818);
				ref_return_expr(0);
				setState(822);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(819);
					match(T__0);
					}
					}
					setState(824);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(825);
				match(T__2);
				}
				break;
			case DECIMAL:
				{
				_localctx = new Ref_return_expr_terminalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(827);
				match(DECIMAL);
				}
				break;
			case VARIABLE:
				{
				_localctx = new Ref_return_expr_variableContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(828);
				match(VARIABLE);
				}
				break;
			case VARIABLE_REF:
				{
				_localctx = new Ref_return_expr_fieldContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(829);
				match(VARIABLE_REF);
				}
				break;
			case VARIABLE_NAME:
				{
				_localctx = new Ref_return_expr_error1Context(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(830);
				match(VARIABLE_NAME);
				}
				break;
			case T__8:
			case DIGIT:
				{
				_localctx = new Ref_return_expr_error2Context(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(866);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,135,_ctx) ) {
				case 1:
					{
					{
					setState(832); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(831);
						match(DIGIT);
						}
						}
						setState(834); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==DIGIT );
					setState(836);
					match(T__8);
					}
					}
					break;
				case 2:
					{
					{
					setState(837);
					match(T__8);
					setState(839); 
					_errHandler.sync(this);
					_alt = 1;
					do {
						switch (_alt) {
						case 1:
							{
							{
							setState(838);
							match(DIGIT);
							}
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						setState(841); 
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,130,_ctx);
					} while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER );
					}
					}
					break;
				case 3:
					{
					{
					setState(844); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(843);
						match(DIGIT);
						}
						}
						setState(846); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==DIGIT );
					setState(848);
					match(T__8);
					setState(862); 
					_errHandler.sync(this);
					_alt = 1;
					do {
						switch (_alt) {
						case 1:
							{
							{
							setState(852);
							_errHandler.sync(this);
							_la = _input.LA(1);
							while (_la==DIGIT) {
								{
								{
								setState(849);
								match(DIGIT);
								}
								}
								setState(854);
								_errHandler.sync(this);
								_la = _input.LA(1);
							}
							setState(855);
							match(T__8);
							setState(859);
							_errHandler.sync(this);
							_alt = getInterpreter().adaptivePredict(_input,133,_ctx);
							while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
								if ( _alt==1 ) {
									{
									{
									setState(856);
									match(DIGIT);
									}
									} 
								}
								setState(861);
								_errHandler.sync(this);
								_alt = getInterpreter().adaptivePredict(_input,133,_ctx);
							}
							}
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						setState(864); 
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,134,_ctx);
					} while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER );
					}
					}
					break;
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(902);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,142,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(900);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,141,_ctx) ) {
					case 1:
						{
						_localctx = new Ref_return_expr_operateContext(new Ref_return_exprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_ref_return_expr);
						setState(870);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(874);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==T__0) {
							{
							{
							setState(871);
							match(T__0);
							}
							}
							setState(876);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(877);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__3) | (1L << T__4) | (1L << T__5))) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)== Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(881);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==T__0) {
							{
							{
							setState(878);
							match(T__0);
							}
							}
							setState(883);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(884);
						ref_return_expr(8);
						}
						break;
					case 2:
						{
						_localctx = new Ref_return_expr_operateContext(new Ref_return_exprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_ref_return_expr);
						setState(885);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(889);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==T__0) {
							{
							{
							setState(886);
							match(T__0);
							}
							}
							setState(891);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(892);
						_la = _input.LA(1);
						if ( !(_la==T__6 || _la==T__7) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)== Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(896);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==T__0) {
							{
							{
							setState(893);
							match(T__0);
							}
							}
							setState(898);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(899);
						ref_return_expr(7);
						}
						break;
					}
					} 
				}
				setState(904);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,142,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Ref_where_fragContext extends ParserRuleContext {
		public TerminalNode KEY_WHERE() { return getToken(QuoteAttrFormulaParser.KEY_WHERE, 0); }
		public Ref_where_frag_miniContext ref_where_frag_mini() {
			return getRuleContext(Ref_where_frag_miniContext.class,0);
		}
		public Ref_where_fragContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ref_where_frag; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefWhereFrag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefWhereFrag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefWhereFrag(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Ref_where_fragContext ref_where_frag() throws RecognitionException {
		Ref_where_fragContext _localctx = new Ref_where_fragContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_ref_where_frag);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(905);
			match(KEY_WHERE);
			setState(906);
			match(T__10);
			setState(907);
			match(T__16);
			setState(908);
			ref_where_frag_mini();
			setState(909);
			match(T__16);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Ref_where_frag_miniContext extends ParserRuleContext {
		public Ref_where_frag_miniContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ref_where_frag_mini; }
	 
		public Ref_where_frag_miniContext() { }
		public void copyFrom(Ref_where_frag_miniContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class Ref_where_frag_mini_normalContext extends Ref_where_frag_miniContext {
		public Ref_where_frag_miniContext ref_where_frag_mini() {
			return getRuleContext(Ref_where_frag_miniContext.class,0);
		}
		public Ref_where_frag_mini_normalContext(Ref_where_frag_miniContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefWhereFragMiniNormal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefWhereFragMiniNormal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefWhereFragMiniNormal(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Ref_where_frag_mini_andContext extends Ref_where_frag_miniContext {
		public List<Ref_where_frag_mini_fragContext> ref_where_frag_mini_frag() {
			return getRuleContexts(Ref_where_frag_mini_fragContext.class);
		}
		public Ref_where_frag_mini_fragContext ref_where_frag_mini_frag(int i) {
			return getRuleContext(Ref_where_frag_mini_fragContext.class,i);
		}
		public List<TerminalNode> KEY_AND() { return getTokens(QuoteAttrFormulaParser.KEY_AND); }
		public TerminalNode KEY_AND(int i) {
			return getToken(QuoteAttrFormulaParser.KEY_AND, i);
		}
		public Ref_where_frag_mini_andContext(Ref_where_frag_miniContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefWhereFragMiniAnd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefWhereFragMiniAnd(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefWhereFragMiniAnd(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Ref_where_frag_miniContext ref_where_frag_mini() throws RecognitionException {
		Ref_where_frag_miniContext _localctx = new Ref_where_frag_miniContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_ref_where_frag_mini);
		int _la;
		try {
			setState(923);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__1:
				_localctx = new Ref_where_frag_mini_normalContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(911);
				match(T__1);
				setState(912);
				ref_where_frag_mini();
				setState(913);
				match(T__2);
				}
				break;
			case T__2:
			case T__16:
			case KEY_AND:
			case VARIABLE_REF:
				_localctx = new Ref_where_frag_mini_andContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(915);
				ref_where_frag_mini_frag();
				setState(920);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==KEY_AND) {
					{
					{
					setState(916);
					match(KEY_AND);
					setState(917);
					ref_where_frag_mini_frag();
					}
					}
					setState(922);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Ref_where_frag_mini_fragContext extends ParserRuleContext {
		public TerminalNode VARIABLE_REF() { return getToken(QuoteAttrFormulaParser.VARIABLE_REF, 0); }
		public Ref_where_exprContext ref_where_expr() {
			return getRuleContext(Ref_where_exprContext.class,0);
		}
		public TerminalNode KEY_LIKE() { return getToken(QuoteAttrFormulaParser.KEY_LIKE, 0); }
		public Ref_where_frag_mini_fragContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ref_where_frag_mini_frag; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefWhereFragMiniFrag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefWhereFragMiniFrag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefWhereFragMiniFrag(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Ref_where_frag_mini_fragContext ref_where_frag_mini_frag() throws RecognitionException {
		Ref_where_frag_mini_fragContext _localctx = new Ref_where_frag_mini_fragContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_ref_where_frag_mini_frag);
		int _la;
		try {
			setState(929);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__2:
			case T__16:
			case KEY_AND:
				enterOuterAlt(_localctx, 1);
				{
				}
				break;
			case VARIABLE_REF:
				enterOuterAlt(_localctx, 2);
				{
				setState(926);
				match(VARIABLE_REF);
				setState(927);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__10) | (1L << T__11) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << KEY_LIKE))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)== Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(928);
				ref_where_expr(0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Ref_where_exprContext extends ParserRuleContext {
		public Ref_where_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ref_where_expr; }
	 
		public Ref_where_exprContext() { }
		public void copyFrom(Ref_where_exprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class Ref_where_expr_normalContext extends Ref_where_exprContext {
		public Ref_where_exprContext ref_where_expr() {
			return getRuleContext(Ref_where_exprContext.class,0);
		}
		public Ref_where_expr_normalContext(Ref_where_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefWhereExprNormal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefWhereExprNormal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefWhereExprNormal(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Ref_where_expr_nullContext extends Ref_where_exprContext {
		public TerminalNode VARIABLE_REF() { return getToken(QuoteAttrFormulaParser.VARIABLE_REF, 0); }
		public TerminalNode KEY_IS() { return getToken(QuoteAttrFormulaParser.KEY_IS, 0); }
		public TerminalNode KEY_NULL() { return getToken(QuoteAttrFormulaParser.KEY_NULL, 0); }
		public TerminalNode KEY_NOT() { return getToken(QuoteAttrFormulaParser.KEY_NOT, 0); }
		public Ref_where_expr_nullContext(Ref_where_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefWhereExprNull(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefWhereExprNull(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefWhereExprNull(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Ref_where_expr_fieldContext extends Ref_where_exprContext {
		public TerminalNode VARIABLE_REF() { return getToken(QuoteAttrFormulaParser.VARIABLE_REF, 0); }
		public Ref_where_expr_fieldContext(Ref_where_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefWhereExprField(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefWhereExprField(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefWhereExprField(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Ref_where_expr_operateContext extends Ref_where_exprContext {
		public List<Ref_where_exprContext> ref_where_expr() {
			return getRuleContexts(Ref_where_exprContext.class);
		}
		public Ref_where_exprContext ref_where_expr(int i) {
			return getRuleContext(Ref_where_exprContext.class,i);
		}
		public Ref_where_expr_operateContext(Ref_where_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefWhereExprOperate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefWhereExprOperate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefWhereExprOperate(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Ref_where_expr_terminalContext extends Ref_where_exprContext {
		public TerminalNode DECIMAL() { return getToken(QuoteAttrFormulaParser.DECIMAL, 0); }
		public Ref_where_expr_terminalContext(Ref_where_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefWhereExprTerminal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefWhereExprTerminal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefWhereExprTerminal(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Ref_where_expr_variableContext extends Ref_where_exprContext {
		public TerminalNode VARIABLE() { return getToken(QuoteAttrFormulaParser.VARIABLE, 0); }
		public Ref_where_expr_variableContext(Ref_where_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterRefWhereExprVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitRefWhereExprVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitRefWhereExprVariable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Ref_where_exprContext ref_where_expr() throws RecognitionException {
		return ref_where_expr(0);
	}

	private Ref_where_exprContext ref_where_expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Ref_where_exprContext _localctx = new Ref_where_exprContext(_ctx, _parentState);
		Ref_where_exprContext _prevctx = _localctx;
		int _startState = 24;
		enterRecursionRule(_localctx, 24, RULE_ref_where_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(947);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,147,_ctx) ) {
			case 1:
				{
				_localctx = new Ref_where_expr_normalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(932);
				match(T__1);
				setState(933);
				ref_where_expr(0);
				setState(934);
				match(T__2);
				}
				break;
			case 2:
				{
				_localctx = new Ref_where_expr_nullContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(936);
				match(VARIABLE_REF);
				setState(942);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,146,_ctx) ) {
				case 1:
					{
					{
					setState(937);
					match(KEY_IS);
					setState(938);
					match(KEY_NULL);
					}
					}
					break;
				case 2:
					{
					{
					setState(939);
					match(KEY_IS);
					setState(940);
					match(KEY_NOT);
					setState(941);
					match(KEY_NULL);
					}
					}
					break;
				}
				}
				break;
			case 3:
				{
				_localctx = new Ref_where_expr_terminalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(944);
				match(DECIMAL);
				}
				break;
			case 4:
				{
				_localctx = new Ref_where_expr_variableContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(945);
				match(VARIABLE);
				}
				break;
			case 5:
				{
				_localctx = new Ref_where_expr_fieldContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(946);
				match(VARIABLE_REF);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(954);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,148,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Ref_where_expr_operateContext(new Ref_where_exprContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_ref_where_expr);
					setState(949);
					if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
					setState(950);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7))) != 0)) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)== Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(951);
					ref_where_expr(6);
					}
					} 
				}
				setState(956);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,148,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class FunContext extends ParserRuleContext {
		public TerminalNode KEY_FUN() { return getToken(QuoteAttrFormulaParser.KEY_FUN, 0); }
		public TerminalNode VARIABLE_NAME() { return getToken(QuoteAttrFormulaParser.VARIABLE_NAME, 0); }
		public Fun_paramContext fun_param() {
			return getRuleContext(Fun_paramContext.class,0);
		}
		public FunContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fun; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterFun(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitFun(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitFun(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunContext fun() throws RecognitionException {
		FunContext _localctx = new FunContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_fun);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(957);
			match(KEY_FUN);
			setState(958);
			match(T__8);
			setState(959);
			match(VARIABLE_NAME);
			setState(960);
			match(T__1);
			setState(964);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,149,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(961);
					match(T__0);
					}
					} 
				}
				setState(966);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,149,_ctx);
			}
			setState(967);
			fun_param();
			setState(971);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(968);
				match(T__0);
				}
				}
				setState(973);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(974);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fun_paramContext extends ParserRuleContext {
		public List<Fun_param_fragContext> fun_param_frag() {
			return getRuleContexts(Fun_param_fragContext.class);
		}
		public Fun_param_fragContext fun_param_frag(int i) {
			return getRuleContext(Fun_param_fragContext.class,i);
		}
		public Fun_paramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fun_param; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterFunParam(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitFunParam(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitFunParam(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Fun_paramContext fun_param() throws RecognitionException {
		Fun_paramContext _localctx = new Fun_paramContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_fun_param);
		int _la;
		try {
			int _alt;
			setState(1027);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,160,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(985);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__9) {
					{
					{
					setState(976);
					match(T__9);
					setState(980);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,151,_ctx);
					while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1 ) {
							{
							{
							setState(977);
							match(T__0);
							}
							} 
						}
						setState(982);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,151,_ctx);
					}
					}
					}
					setState(987);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(989);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VARIABLE_NAME) {
					{
					setState(988);
					fun_param_frag();
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(1009); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(991);
						fun_param_frag();
						setState(995);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==T__0) {
							{
							{
							setState(992);
							match(T__0);
							}
							}
							setState(997);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(1005); 
						_errHandler.sync(this);
						_la = _input.LA(1);
						do {
							{
							{
							setState(998);
							match(T__9);
							setState(1002);
							_errHandler.sync(this);
							_la = _input.LA(1);
							while (_la==T__0) {
								{
								{
								setState(999);
								match(T__0);
								}
								}
								setState(1004);
								_errHandler.sync(this);
								_la = _input.LA(1);
							}
							}
							}
							setState(1007); 
							_errHandler.sync(this);
							_la = _input.LA(1);
						} while ( _la==T__9 );
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(1011); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,157,_ctx);
				} while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER );
				setState(1013);
				fun_param_frag();
				}
				setState(1024);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__9) {
					{
					{
					setState(1015);
					match(T__9);
					setState(1019);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,158,_ctx);
					while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1 ) {
							{
							{
							setState(1016);
							match(T__0);
							}
							} 
						}
						setState(1021);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,158,_ctx);
					}
					}
					}
					setState(1026);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fun_param_fragContext extends ParserRuleContext {
		public Fun_param_fragContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fun_param_frag; }
	 
		public Fun_param_fragContext() { }
		public void copyFrom(Fun_param_fragContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class Fun_param_frag_nsContext extends Fun_param_fragContext {
		public TerminalNode VARIABLE_NAME() { return getToken(QuoteAttrFormulaParser.VARIABLE_NAME, 0); }
		public Fun_param_expr_nsContext fun_param_expr_ns() {
			return getRuleContext(Fun_param_expr_nsContext.class,0);
		}
		public Fun_param_frag_nsContext(Fun_param_fragContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterFunParamFragNs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitFunParamFragNs(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitFunParamFragNs(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Fun_param_frag_sContext extends Fun_param_fragContext {
		public TerminalNode VARIABLE_NAME() { return getToken(QuoteAttrFormulaParser.VARIABLE_NAME, 0); }
		public Fun_param_expr_sContext fun_param_expr_s() {
			return getRuleContext(Fun_param_expr_sContext.class,0);
		}
		public Fun_param_frag_sContext(Fun_param_fragContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterFunParamFragS(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitFunParamFragS(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitFunParamFragS(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Fun_param_fragContext fun_param_frag() throws RecognitionException {
		Fun_param_fragContext _localctx = new Fun_param_fragContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_fun_param_frag);
		int _la;
		try {
			int _alt;
			setState(1065);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,167,_ctx) ) {
			case 1:
				_localctx = new Fun_param_frag_sContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(1029);
				match(VARIABLE_NAME);
				setState(1033);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,161,_ctx);
				while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(1030);
						match(T__0);
						}
						} 
					}
					setState(1035);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,161,_ctx);
				}
				setState(1045);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,163,_ctx) ) {
				case 1:
					{
					setState(1036);
					match(T__10);
					}
					break;
				case 2:
					{
					setState(1037);
					match(T__10);
					setState(1041);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==T__0) {
						{
						{
						setState(1038);
						match(T__0);
						}
						}
						setState(1043);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(1044);
					fun_param_expr_s(0);
					}
					break;
				}
				}
				break;
			case 2:
				_localctx = new Fun_param_frag_nsContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(1047);
				match(VARIABLE_NAME);
				setState(1051);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,164,_ctx);
				while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(1048);
						match(T__0);
						}
						} 
					}
					setState(1053);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,164,_ctx);
				}
				setState(1063);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,166,_ctx) ) {
				case 1:
					{
					setState(1054);
					match(T__10);
					}
					break;
				case 2:
					{
					setState(1055);
					match(T__10);
					setState(1059);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==T__0) {
						{
						{
						setState(1056);
						match(T__0);
						}
						}
						setState(1061);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(1062);
					fun_param_expr_ns(0);
					}
					break;
				}
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fun_param_expr_sContext extends ParserRuleContext {
		public Fun_param_expr_sContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fun_param_expr_s; }
	 
		public Fun_param_expr_sContext() { }
		public void copyFrom(Fun_param_expr_sContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class Fun_param_expr_s_normalContext extends Fun_param_expr_sContext {
		public Fun_param_expr_sContext fun_param_expr_s() {
			return getRuleContext(Fun_param_expr_sContext.class,0);
		}
		public Fun_param_expr_s_normalContext(Fun_param_expr_sContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterFunParamExprsNormal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitFunParamExprsNormal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitFunParamExprsNormal(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Fun_param_expr_s_operateContext extends Fun_param_expr_sContext {
		public Fun_param_expr_nsContext fun_param_expr_ns() {
			return getRuleContext(Fun_param_expr_nsContext.class,0);
		}
		public List<Fun_param_expr_sContext> fun_param_expr_s() {
			return getRuleContexts(Fun_param_expr_sContext.class);
		}
		public Fun_param_expr_sContext fun_param_expr_s(int i) {
			return getRuleContext(Fun_param_expr_sContext.class,i);
		}
		public Fun_param_expr_s_operateContext(Fun_param_expr_sContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterFunParamExprsOperate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitFunParamExprsOperate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitFunParamExprsOperate(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Fun_param_expr_s_terminalContext extends Fun_param_expr_sContext {
		public TerminalNode STRING() { return getToken(QuoteAttrFormulaParser.STRING, 0); }
		public Fun_param_expr_s_terminalContext(Fun_param_expr_sContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterFunParamExprsTerminal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitFunParamExprsTerminal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitFunParamExprsTerminal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Fun_param_expr_sContext fun_param_expr_s() throws RecognitionException {
		return fun_param_expr_s(0);
	}

	private Fun_param_expr_sContext fun_param_expr_s(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Fun_param_expr_sContext _localctx = new Fun_param_expr_sContext(_ctx, _parentState);
		Fun_param_expr_sContext _prevctx = _localctx;
		int _startState = 32;
		enterRecursionRule(_localctx, 32, RULE_fun_param_expr_s, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1101);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,172,_ctx) ) {
			case 1:
				{
				_localctx = new Fun_param_expr_s_normalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(1068);
				match(T__1);
				setState(1072);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(1069);
					match(T__0);
					}
					}
					setState(1074);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1075);
				fun_param_expr_s(0);
				setState(1079);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(1076);
					match(T__0);
					}
					}
					setState(1081);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1082);
				match(T__2);
				}
				break;
			case 2:
				{
				_localctx = new Fun_param_expr_s_operateContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1084);
				fun_param_expr_ns(0);
				setState(1088);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(1085);
					match(T__0);
					}
					}
					setState(1090);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1091);
				match(T__6);
				setState(1095);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(1092);
					match(T__0);
					}
					}
					setState(1097);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1098);
				fun_param_expr_s(2);
				}
				break;
			case 3:
				{
				_localctx = new Fun_param_expr_s_terminalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1100);
				match(STRING);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(1135);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,178,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(1133);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,177,_ctx) ) {
					case 1:
						{
						_localctx = new Fun_param_expr_s_operateContext(new Fun_param_expr_sContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_fun_param_expr_s);
						setState(1103);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(1107);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==T__0) {
							{
							{
							setState(1104);
							match(T__0);
							}
							}
							setState(1109);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(1110);
						match(T__6);
						setState(1114);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==T__0) {
							{
							{
							setState(1111);
							match(T__0);
							}
							}
							setState(1116);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(1117);
						fun_param_expr_s(5);
						}
						break;
					case 2:
						{
						_localctx = new Fun_param_expr_s_operateContext(new Fun_param_expr_sContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_fun_param_expr_s);
						setState(1118);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(1122);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==T__0) {
							{
							{
							setState(1119);
							match(T__0);
							}
							}
							setState(1124);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(1125);
						match(T__6);
						setState(1129);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==T__0) {
							{
							{
							setState(1126);
							match(T__0);
							}
							}
							setState(1131);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(1132);
						fun_param_expr_ns(0);
						}
						break;
					}
					} 
				}
				setState(1137);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,178,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Fun_param_expr_nsContext extends ParserRuleContext {
		public Fun_param_expr_nsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fun_param_expr_ns; }
	 
		public Fun_param_expr_nsContext() { }
		public void copyFrom(Fun_param_expr_nsContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class Fun_param_expr_ns_terminalContext extends Fun_param_expr_nsContext {
		public TerminalNode DECIMAL() { return getToken(QuoteAttrFormulaParser.DECIMAL, 0); }
		public Fun_param_expr_ns_terminalContext(Fun_param_expr_nsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterFunParamExprNsTerminal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitFunParamExprNsTerminal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitFunParamExprNsTerminal(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Fun_param_expr_ns_normalContext extends Fun_param_expr_nsContext {
		public Fun_param_expr_nsContext fun_param_expr_ns() {
			return getRuleContext(Fun_param_expr_nsContext.class,0);
		}
		public Fun_param_expr_ns_normalContext(Fun_param_expr_nsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterFunParamExprNsNormal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitFunParamExprNsNormal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitFunParamExprNsNormal(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Fun_param_expr_ns_variableContext extends Fun_param_expr_nsContext {
		public TerminalNode VARIABLE() { return getToken(QuoteAttrFormulaParser.VARIABLE, 0); }
		public Fun_param_expr_ns_variableContext(Fun_param_expr_nsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterFunParamExprNsVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitFunParamExprNsVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitFunParamExprNsVariable(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Fun_param_expr_ns_operateContext extends Fun_param_expr_nsContext {
		public List<Fun_param_expr_nsContext> fun_param_expr_ns() {
			return getRuleContexts(Fun_param_expr_nsContext.class);
		}
		public Fun_param_expr_nsContext fun_param_expr_ns(int i) {
			return getRuleContext(Fun_param_expr_nsContext.class,i);
		}
		public Fun_param_expr_ns_operateContext(Fun_param_expr_nsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterFunParamExprNsOperate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitFunParamExprNsOperate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitFunParamExprNsOperate(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Fun_param_expr_nsContext fun_param_expr_ns() throws RecognitionException {
		return fun_param_expr_ns(0);
	}

	private Fun_param_expr_nsContext fun_param_expr_ns(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Fun_param_expr_nsContext _localctx = new Fun_param_expr_nsContext(_ctx, _parentState);
		Fun_param_expr_nsContext _prevctx = _localctx;
		int _startState = 34;
		enterRecursionRule(_localctx, 34, RULE_fun_param_expr_ns, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1157);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__1:
				{
				_localctx = new Fun_param_expr_ns_normalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(1139);
				match(T__1);
				setState(1143);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(1140);
					match(T__0);
					}
					}
					setState(1145);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1146);
				fun_param_expr_ns(0);
				setState(1150);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(1147);
					match(T__0);
					}
					}
					setState(1152);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1153);
				match(T__2);
				}
				break;
			case DECIMAL:
				{
				_localctx = new Fun_param_expr_ns_terminalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1155);
				match(DECIMAL);
				}
				break;
			case VARIABLE:
				{
				_localctx = new Fun_param_expr_ns_variableContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1156);
				match(VARIABLE);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(1191);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,187,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(1189);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,186,_ctx) ) {
					case 1:
						{
						_localctx = new Fun_param_expr_ns_operateContext(new Fun_param_expr_nsContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_fun_param_expr_ns);
						setState(1159);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(1163);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==T__0) {
							{
							{
							setState(1160);
							match(T__0);
							}
							}
							setState(1165);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(1166);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__3) | (1L << T__4) | (1L << T__5))) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)== Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1170);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==T__0) {
							{
							{
							setState(1167);
							match(T__0);
							}
							}
							setState(1172);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(1173);
						fun_param_expr_ns(5);
						}
						break;
					case 2:
						{
						_localctx = new Fun_param_expr_ns_operateContext(new Fun_param_expr_nsContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_fun_param_expr_ns);
						setState(1174);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(1178);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==T__0) {
							{
							{
							setState(1175);
							match(T__0);
							}
							}
							setState(1180);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(1181);
						_la = _input.LA(1);
						if ( !(_la==T__6 || _la==T__7) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)== Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1185);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==T__0) {
							{
							{
							setState(1182);
							match(T__0);
							}
							}
							setState(1187);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(1188);
						fun_param_expr_ns(4);
						}
						break;
					}
					} 
				}
				setState(1193);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,187,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Group_funcContext extends ParserRuleContext {
		public SumContext sum() {
			return getRuleContext(SumContext.class,0);
		}
		public MaxContext max() {
			return getRuleContext(MaxContext.class,0);
		}
		public MinContext min() {
			return getRuleContext(MinContext.class,0);
		}
		public AvgContext avg() {
			return getRuleContext(AvgContext.class,0);
		}
		public Group_funcContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_group_func; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterGroupFunc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitGroupFunc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitGroupFunc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Group_funcContext group_func() throws RecognitionException {
		Group_funcContext _localctx = new Group_funcContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_group_func);
		try {
			setState(1198);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case KEY_SUM:
				enterOuterAlt(_localctx, 1);
				{
				setState(1194);
				sum();
				}
				break;
			case KEY_MAX:
				enterOuterAlt(_localctx, 2);
				{
				setState(1195);
				max();
				}
				break;
			case KEY_MIN:
				enterOuterAlt(_localctx, 3);
				{
				setState(1196);
				min();
				}
				break;
			case KEY_AVG:
				enterOuterAlt(_localctx, 4);
				{
				setState(1197);
				avg();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SumContext extends ParserRuleContext {
		public TerminalNode KEY_SUM() { return getToken(QuoteAttrFormulaParser.KEY_SUM, 0); }
		public Group_fragContext group_frag() {
			return getRuleContext(Group_fragContext.class,0);
		}
		public SumContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sum; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterSum(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitSum(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitSum(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SumContext sum() throws RecognitionException {
		SumContext _localctx = new SumContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_sum);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1200);
			match(KEY_SUM);
			setState(1201);
			match(T__1);
			setState(1205);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(1202);
				match(T__0);
				}
				}
				setState(1207);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1208);
			group_frag();
			setState(1212);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(1209);
				match(T__0);
				}
				}
				setState(1214);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1215);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MaxContext extends ParserRuleContext {
		public TerminalNode KEY_MAX() { return getToken(QuoteAttrFormulaParser.KEY_MAX, 0); }
		public Group_fragContext group_frag() {
			return getRuleContext(Group_fragContext.class,0);
		}
		public MaxContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_max; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterMax(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitMax(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitMax(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MaxContext max() throws RecognitionException {
		MaxContext _localctx = new MaxContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_max);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1217);
			match(KEY_MAX);
			setState(1218);
			match(T__1);
			setState(1222);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(1219);
				match(T__0);
				}
				}
				setState(1224);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1225);
			group_frag();
			setState(1229);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(1226);
				match(T__0);
				}
				}
				setState(1231);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1232);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MinContext extends ParserRuleContext {
		public TerminalNode KEY_MIN() { return getToken(QuoteAttrFormulaParser.KEY_MIN, 0); }
		public Group_fragContext group_frag() {
			return getRuleContext(Group_fragContext.class,0);
		}
		public MinContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_min; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterMin(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitMin(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitMin(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MinContext min() throws RecognitionException {
		MinContext _localctx = new MinContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_min);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1234);
			match(KEY_MIN);
			setState(1235);
			match(T__1);
			setState(1239);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(1236);
				match(T__0);
				}
				}
				setState(1241);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1242);
			group_frag();
			setState(1246);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(1243);
				match(T__0);
				}
				}
				setState(1248);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1249);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AvgContext extends ParserRuleContext {
		public TerminalNode KEY_AVG() { return getToken(QuoteAttrFormulaParser.KEY_AVG, 0); }
		public Group_fragContext group_frag() {
			return getRuleContext(Group_fragContext.class,0);
		}
		public AvgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_avg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterAvg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitAvg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitAvg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AvgContext avg() throws RecognitionException {
		AvgContext _localctx = new AvgContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_avg);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1251);
			match(KEY_AVG);
			setState(1252);
			match(T__1);
			setState(1256);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(1253);
				match(T__0);
				}
				}
				setState(1258);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1259);
			group_frag();
			setState(1263);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(1260);
				match(T__0);
				}
				}
				setState(1265);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1266);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Group_fragContext extends ParserRuleContext {
		public Group_fragContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_group_frag; }
	 
		public Group_fragContext() { }
		public void copyFrom(Group_fragContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class Group_frag_refContext extends Group_fragContext {
		public RefContext ref() {
			return getRuleContext(RefContext.class,0);
		}
		public Group_frag_refContext(Group_fragContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterGroupFragRef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitGroupFragRef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitGroupFragRef(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Group_frag_funContext extends Group_fragContext {
		public FunContext fun() {
			return getRuleContext(FunContext.class,0);
		}
		public Group_frag_funContext(Group_fragContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).enterGroupFragFun(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuoteAttrFormulaListener) ((QuoteAttrFormulaListener)listener).exitGroupFragFun(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuoteAttrFormulaVisitor) return ((QuoteAttrFormulaVisitor<? extends T>)visitor).visitGroupFragFun(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Group_fragContext group_frag() throws RecognitionException {
		Group_fragContext _localctx = new Group_fragContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_group_frag);
		try {
			setState(1270);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case KEY_REF:
				_localctx = new Group_frag_refContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(1268);
				ref();
				}
				break;
			case KEY_FUN:
				_localctx = new Group_frag_funContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(1269);
				fun();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 1:
			return expr_sempred((ExprContext)_localctx, predIndex);
		case 6:
			return ref_select_expr_s_sempred((Ref_select_expr_sContext)_localctx, predIndex);
		case 7:
			return ref_select_expr_ns_sempred((Ref_select_expr_nsContext)_localctx, predIndex);
		case 8:
			return ref_return_expr_sempred((Ref_return_exprContext)_localctx, predIndex);
		case 12:
			return ref_where_expr_sempred((Ref_where_exprContext)_localctx, predIndex);
		case 16:
			return fun_param_expr_s_sempred((Fun_param_expr_sContext)_localctx, predIndex);
		case 17:
			return fun_param_expr_ns_sempred((Fun_param_expr_nsContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 7);
		case 1:
			return precpred(_ctx, 6);
		}
		return true;
	}
	private boolean ref_select_expr_s_sempred(Ref_select_expr_sContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return precpred(_ctx, 4);
		case 3:
			return precpred(_ctx, 3);
		}
		return true;
	}
	private boolean ref_select_expr_ns_sempred(Ref_select_expr_nsContext _localctx, int predIndex) {
		switch (predIndex) {
		case 4:
			return precpred(_ctx, 7);
		case 5:
			return precpred(_ctx, 6);
		}
		return true;
	}
	private boolean ref_return_expr_sempred(Ref_return_exprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 6:
			return precpred(_ctx, 7);
		case 7:
			return precpred(_ctx, 6);
		}
		return true;
	}
	private boolean ref_where_expr_sempred(Ref_where_exprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 8:
			return precpred(_ctx, 5);
		}
		return true;
	}
	private boolean fun_param_expr_s_sempred(Fun_param_expr_sContext _localctx, int predIndex) {
		switch (predIndex) {
		case 9:
			return precpred(_ctx, 4);
		case 10:
			return precpred(_ctx, 3);
		}
		return true;
	}
	private boolean fun_param_expr_ns_sempred(Fun_param_expr_nsContext _localctx, int predIndex) {
		switch (predIndex) {
		case 11:
			return precpred(_ctx, 4);
		case 12:
			return precpred(_ctx, 3);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3*\u04fb\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\3\2\3\2\3\2\3\3\3\3\7\38\n\3\f\3\16\3;\13\3\3\3\3\3\7\3?\n\3\f\3\16\3"+
		"B\13\3\3\3\3\3\7\3F\n\3\f\3\16\3I\13\3\3\3\3\3\7\3M\n\3\f\3\16\3P\13\3"+
		"\3\3\7\3S\n\3\f\3\16\3V\13\3\3\3\3\3\7\3Z\n\3\f\3\16\3]\13\3\3\3\7\3`"+
		"\n\3\f\3\16\3c\13\3\3\3\3\3\7\3g\n\3\f\3\16\3j\13\3\3\3\7\3m\n\3\f\3\16"+
		"\3p\13\3\3\3\3\3\7\3t\n\3\f\3\16\3w\13\3\3\3\7\3z\n\3\f\3\16\3}\13\3\3"+
		"\3\3\3\7\3\u0081\n\3\f\3\16\3\u0084\13\3\3\3\7\3\u0087\n\3\f\3\16\3\u008a"+
		"\13\3\3\3\3\3\7\3\u008e\n\3\f\3\16\3\u0091\13\3\5\3\u0093\n\3\3\3\3\3"+
		"\7\3\u0097\n\3\f\3\16\3\u009a\13\3\3\3\3\3\7\3\u009e\n\3\f\3\16\3\u00a1"+
		"\13\3\3\3\3\3\3\3\7\3\u00a6\n\3\f\3\16\3\u00a9\13\3\3\3\3\3\7\3\u00ad"+
		"\n\3\f\3\16\3\u00b0\13\3\3\3\7\3\u00b3\n\3\f\3\16\3\u00b6\13\3\3\4\3\4"+
		"\3\4\3\4\3\4\7\4\u00bd\n\4\f\4\16\4\u00c0\13\4\3\4\3\4\7\4\u00c4\n\4\f"+
		"\4\16\4\u00c7\13\4\3\4\3\4\3\5\7\5\u00cc\n\5\f\5\16\5\u00cf\13\5\3\5\3"+
		"\5\7\5\u00d3\n\5\f\5\16\5\u00d6\13\5\3\5\3\5\7\5\u00da\n\5\f\5\16\5\u00dd"+
		"\13\5\6\5\u00df\n\5\r\5\16\5\u00e0\7\5\u00e3\n\5\f\5\16\5\u00e6\13\5\3"+
		"\5\3\5\3\5\7\5\u00eb\n\5\f\5\16\5\u00ee\13\5\7\5\u00f0\n\5\f\5\16\5\u00f3"+
		"\13\5\3\5\7\5\u00f6\n\5\f\5\16\5\u00f9\13\5\3\5\3\5\7\5\u00fd\n\5\f\5"+
		"\16\5\u0100\13\5\3\5\3\5\7\5\u0104\n\5\f\5\16\5\u0107\13\5\6\5\u0109\n"+
		"\5\r\5\16\5\u010a\3\5\3\5\7\5\u010f\n\5\f\5\16\5\u0112\13\5\3\5\3\5\7"+
		"\5\u0116\n\5\f\5\16\5\u0119\13\5\6\5\u011b\n\5\r\5\16\5\u011c\7\5\u011f"+
		"\n\5\f\5\16\5\u0122\13\5\3\5\3\5\3\5\7\5\u0127\n\5\f\5\16\5\u012a\13\5"+
		"\7\5\u012c\n\5\f\5\16\5\u012f\13\5\3\5\7\5\u0132\n\5\f\5\16\5\u0135\13"+
		"\5\3\5\3\5\7\5\u0139\n\5\f\5\16\5\u013c\13\5\3\5\3\5\7\5\u0140\n\5\f\5"+
		"\16\5\u0143\13\5\6\5\u0145\n\5\r\5\16\5\u0146\3\5\3\5\3\5\7\5\u014c\n"+
		"\5\f\5\16\5\u014f\13\5\7\5\u0151\n\5\f\5\16\5\u0154\13\5\3\5\7\5\u0157"+
		"\n\5\f\5\16\5\u015a\13\5\3\5\3\5\7\5\u015e\n\5\f\5\16\5\u0161\13\5\3\5"+
		"\3\5\7\5\u0165\n\5\f\5\16\5\u0168\13\5\6\5\u016a\n\5\r\5\16\5\u016b\3"+
		"\5\3\5\3\5\7\5\u0171\n\5\f\5\16\5\u0174\13\5\7\5\u0176\n\5\f\5\16\5\u0179"+
		"\13\5\3\5\7\5\u017c\n\5\f\5\16\5\u017f\13\5\3\5\3\5\5\5\u0183\n\5\3\5"+
		"\7\5\u0186\n\5\f\5\16\5\u0189\13\5\3\5\3\5\7\5\u018d\n\5\f\5\16\5\u0190"+
		"\13\5\6\5\u0192\n\5\r\5\16\5\u0193\7\5\u0196\n\5\f\5\16\5\u0199\13\5\3"+
		"\5\3\5\5\5\u019d\n\5\3\5\3\5\7\5\u01a1\n\5\f\5\16\5\u01a4\13\5\7\5\u01a6"+
		"\n\5\f\5\16\5\u01a9\13\5\3\5\7\5\u01ac\n\5\f\5\16\5\u01af\13\5\3\5\3\5"+
		"\5\5\u01b3\n\5\3\5\7\5\u01b6\n\5\f\5\16\5\u01b9\13\5\3\5\3\5\7\5\u01bd"+
		"\n\5\f\5\16\5\u01c0\13\5\6\5\u01c2\n\5\r\5\16\5\u01c3\7\5\u01c6\n\5\f"+
		"\5\16\5\u01c9\13\5\3\5\3\5\7\5\u01cd\n\5\f\5\16\5\u01d0\13\5\3\5\3\5\7"+
		"\5\u01d4\n\5\f\5\16\5\u01d7\13\5\6\5\u01d9\n\5\r\5\16\5\u01da\6\5\u01dd"+
		"\n\5\r\5\16\5\u01de\3\5\3\5\5\5\u01e3\n\5\3\5\7\5\u01e6\n\5\f\5\16\5\u01e9"+
		"\13\5\3\5\3\5\7\5\u01ed\n\5\f\5\16\5\u01f0\13\5\6\5\u01f2\n\5\r\5\16\5"+
		"\u01f3\7\5\u01f6\n\5\f\5\16\5\u01f9\13\5\3\5\3\5\7\5\u01fd\n\5\f\5\16"+
		"\5\u0200\13\5\3\5\3\5\7\5\u0204\n\5\f\5\16\5\u0207\13\5\6\5\u0209\n\5"+
		"\r\5\16\5\u020a\6\5\u020d\n\5\r\5\16\5\u020e\3\5\3\5\5\5\u0213\n\5\3\5"+
		"\5\5\u0216\n\5\3\5\3\5\7\5\u021a\n\5\f\5\16\5\u021d\13\5\7\5\u021f\n\5"+
		"\f\5\16\5\u0222\13\5\5\5\u0224\n\5\3\6\3\6\7\6\u0228\n\6\f\6\16\6\u022b"+
		"\13\6\3\6\3\6\7\6\u022f\n\6\f\6\16\6\u0232\13\6\3\6\3\6\3\6\7\6\u0237"+
		"\n\6\f\6\16\6\u023a\13\6\3\6\3\6\7\6\u023e\n\6\f\6\16\6\u0241\13\6\3\6"+
		"\3\6\3\6\7\6\u0246\n\6\f\6\16\6\u0249\13\6\3\6\3\6\7\6\u024d\n\6\f\6\16"+
		"\6\u0250\13\6\3\6\3\6\5\6\u0254\n\6\3\6\3\6\7\6\u0258\n\6\f\6\16\6\u025b"+
		"\13\6\7\6\u025d\n\6\f\6\16\6\u0260\13\6\3\6\3\6\3\6\7\6\u0265\n\6\f\6"+
		"\16\6\u0268\13\6\3\6\3\6\7\6\u026c\n\6\f\6\16\6\u026f\13\6\3\6\3\6\5\6"+
		"\u0273\n\6\5\6\u0275\n\6\3\7\3\7\7\7\u0279\n\7\f\7\16\7\u027c\13\7\3\7"+
		"\3\7\7\7\u0280\n\7\f\7\16\7\u0283\13\7\3\7\3\7\3\b\3\b\3\b\7\b\u028a\n"+
		"\b\f\b\16\b\u028d\13\b\3\b\3\b\7\b\u0291\n\b\f\b\16\b\u0294\13\b\3\b\3"+
		"\b\3\b\3\b\7\b\u029a\n\b\f\b\16\b\u029d\13\b\3\b\3\b\7\b\u02a1\n\b\f\b"+
		"\16\b\u02a4\13\b\3\b\3\b\3\b\5\b\u02a9\n\b\3\b\3\b\7\b\u02ad\n\b\f\b\16"+
		"\b\u02b0\13\b\3\b\3\b\7\b\u02b4\n\b\f\b\16\b\u02b7\13\b\3\b\3\b\3\b\7"+
		"\b\u02bc\n\b\f\b\16\b\u02bf\13\b\3\b\3\b\7\b\u02c3\n\b\f\b\16\b\u02c6"+
		"\13\b\3\b\7\b\u02c9\n\b\f\b\16\b\u02cc\13\b\3\t\3\t\3\t\7\t\u02d1\n\t"+
		"\f\t\16\t\u02d4\13\t\3\t\3\t\7\t\u02d8\n\t\f\t\16\t\u02db\13\t\3\t\3\t"+
		"\3\t\3\t\3\t\3\t\3\t\6\t\u02e4\n\t\r\t\16\t\u02e5\3\t\3\t\3\t\6\t\u02eb"+
		"\n\t\r\t\16\t\u02ec\3\t\6\t\u02f0\n\t\r\t\16\t\u02f1\3\t\3\t\7\t\u02f6"+
		"\n\t\f\t\16\t\u02f9\13\t\3\t\3\t\7\t\u02fd\n\t\f\t\16\t\u0300\13\t\6\t"+
		"\u0302\n\t\r\t\16\t\u0303\5\t\u0306\n\t\5\t\u0308\n\t\3\t\3\t\7\t\u030c"+
		"\n\t\f\t\16\t\u030f\13\t\3\t\3\t\7\t\u0313\n\t\f\t\16\t\u0316\13\t\3\t"+
		"\3\t\3\t\7\t\u031b\n\t\f\t\16\t\u031e\13\t\3\t\3\t\7\t\u0322\n\t\f\t\16"+
		"\t\u0325\13\t\3\t\7\t\u0328\n\t\f\t\16\t\u032b\13\t\3\n\3\n\3\n\7\n\u0330"+
		"\n\n\f\n\16\n\u0333\13\n\3\n\3\n\7\n\u0337\n\n\f\n\16\n\u033a\13\n\3\n"+
		"\3\n\3\n\3\n\3\n\3\n\3\n\6\n\u0343\n\n\r\n\16\n\u0344\3\n\3\n\3\n\6\n"+
		"\u034a\n\n\r\n\16\n\u034b\3\n\6\n\u034f\n\n\r\n\16\n\u0350\3\n\3\n\7\n"+
		"\u0355\n\n\f\n\16\n\u0358\13\n\3\n\3\n\7\n\u035c\n\n\f\n\16\n\u035f\13"+
		"\n\6\n\u0361\n\n\r\n\16\n\u0362\5\n\u0365\n\n\5\n\u0367\n\n\3\n\3\n\7"+
		"\n\u036b\n\n\f\n\16\n\u036e\13\n\3\n\3\n\7\n\u0372\n\n\f\n\16\n\u0375"+
		"\13\n\3\n\3\n\3\n\7\n\u037a\n\n\f\n\16\n\u037d\13\n\3\n\3\n\7\n\u0381"+
		"\n\n\f\n\16\n\u0384\13\n\3\n\7\n\u0387\n\n\f\n\16\n\u038a\13\n\3\13\3"+
		"\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\7\f\u0399\n\f\f\f"+
		"\16\f\u039c\13\f\5\f\u039e\n\f\3\r\3\r\3\r\3\r\5\r\u03a4\n\r\3\16\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\5\16\u03b1\n\16\3\16\3\16"+
		"\3\16\5\16\u03b6\n\16\3\16\3\16\3\16\7\16\u03bb\n\16\f\16\16\16\u03be"+
		"\13\16\3\17\3\17\3\17\3\17\3\17\7\17\u03c5\n\17\f\17\16\17\u03c8\13\17"+
		"\3\17\3\17\7\17\u03cc\n\17\f\17\16\17\u03cf\13\17\3\17\3\17\3\20\3\20"+
		"\7\20\u03d5\n\20\f\20\16\20\u03d8\13\20\7\20\u03da\n\20\f\20\16\20\u03dd"+
		"\13\20\3\20\5\20\u03e0\n\20\3\20\3\20\7\20\u03e4\n\20\f\20\16\20\u03e7"+
		"\13\20\3\20\3\20\7\20\u03eb\n\20\f\20\16\20\u03ee\13\20\6\20\u03f0\n\20"+
		"\r\20\16\20\u03f1\6\20\u03f4\n\20\r\20\16\20\u03f5\3\20\3\20\3\20\3\20"+
		"\7\20\u03fc\n\20\f\20\16\20\u03ff\13\20\7\20\u0401\n\20\f\20\16\20\u0404"+
		"\13\20\5\20\u0406\n\20\3\21\3\21\7\21\u040a\n\21\f\21\16\21\u040d\13\21"+
		"\3\21\3\21\3\21\7\21\u0412\n\21\f\21\16\21\u0415\13\21\3\21\5\21\u0418"+
		"\n\21\3\21\3\21\7\21\u041c\n\21\f\21\16\21\u041f\13\21\3\21\3\21\3\21"+
		"\7\21\u0424\n\21\f\21\16\21\u0427\13\21\3\21\5\21\u042a\n\21\5\21\u042c"+
		"\n\21\3\22\3\22\3\22\7\22\u0431\n\22\f\22\16\22\u0434\13\22\3\22\3\22"+
		"\7\22\u0438\n\22\f\22\16\22\u043b\13\22\3\22\3\22\3\22\3\22\7\22\u0441"+
		"\n\22\f\22\16\22\u0444\13\22\3\22\3\22\7\22\u0448\n\22\f\22\16\22\u044b"+
		"\13\22\3\22\3\22\3\22\5\22\u0450\n\22\3\22\3\22\7\22\u0454\n\22\f\22\16"+
		"\22\u0457\13\22\3\22\3\22\7\22\u045b\n\22\f\22\16\22\u045e\13\22\3\22"+
		"\3\22\3\22\7\22\u0463\n\22\f\22\16\22\u0466\13\22\3\22\3\22\7\22\u046a"+
		"\n\22\f\22\16\22\u046d\13\22\3\22\7\22\u0470\n\22\f\22\16\22\u0473\13"+
		"\22\3\23\3\23\3\23\7\23\u0478\n\23\f\23\16\23\u047b\13\23\3\23\3\23\7"+
		"\23\u047f\n\23\f\23\16\23\u0482\13\23\3\23\3\23\3\23\3\23\5\23\u0488\n"+
		"\23\3\23\3\23\7\23\u048c\n\23\f\23\16\23\u048f\13\23\3\23\3\23\7\23\u0493"+
		"\n\23\f\23\16\23\u0496\13\23\3\23\3\23\3\23\7\23\u049b\n\23\f\23\16\23"+
		"\u049e\13\23\3\23\3\23\7\23\u04a2\n\23\f\23\16\23\u04a5\13\23\3\23\7\23"+
		"\u04a8\n\23\f\23\16\23\u04ab\13\23\3\24\3\24\3\24\3\24\5\24\u04b1\n\24"+
		"\3\25\3\25\3\25\7\25\u04b6\n\25\f\25\16\25\u04b9\13\25\3\25\3\25\7\25"+
		"\u04bd\n\25\f\25\16\25\u04c0\13\25\3\25\3\25\3\26\3\26\3\26\7\26\u04c7"+
		"\n\26\f\26\16\26\u04ca\13\26\3\26\3\26\7\26\u04ce\n\26\f\26\16\26\u04d1"+
		"\13\26\3\26\3\26\3\27\3\27\3\27\7\27\u04d8\n\27\f\27\16\27\u04db\13\27"+
		"\3\27\3\27\7\27\u04df\n\27\f\27\16\27\u04e2\13\27\3\27\3\27\3\30\3\30"+
		"\3\30\7\30\u04e9\n\30\f\30\16\30\u04ec\13\30\3\30\3\30\7\30\u04f0\n\30"+
		"\f\30\16\30\u04f3\13\30\3\30\3\30\3\31\3\31\5\31\u04f9\n\31\3\31\2\t\4"+
		"\16\20\22\32\"$\32\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60"+
		"\2\b\3\2\6\b\3\2\t\n\4\2\r\16\24\24\4\2\r\22\24\24\4\2$$))\3\2\6\n\2\u05c6"+
		"\2\62\3\2\2\2\4\u0092\3\2\2\2\6\u00b7\3\2\2\2\b\u0223\3\2\2\2\n\u0274"+
		"\3\2\2\2\f\u0276\3\2\2\2\16\u02a8\3\2\2\2\20\u0307\3\2\2\2\22\u0366\3"+
		"\2\2\2\24\u038b\3\2\2\2\26\u039d\3\2\2\2\30\u03a3\3\2\2\2\32\u03b5\3\2"+
		"\2\2\34\u03bf\3\2\2\2\36\u0405\3\2\2\2 \u042b\3\2\2\2\"\u044f\3\2\2\2"+
		"$\u0487\3\2\2\2&\u04b0\3\2\2\2(\u04b2\3\2\2\2*\u04c3\3\2\2\2,\u04d4\3"+
		"\2\2\2.\u04e5\3\2\2\2\60\u04f8\3\2\2\2\62\63\5\4\3\2\63\64\7\2\2\3\64"+
		"\3\3\2\2\2\659\b\3\1\2\668\7\3\2\2\67\66\3\2\2\28;\3\2\2\29\67\3\2\2\2"+
		"9:\3\2\2\2:<\3\2\2\2;9\3\2\2\2<@\7\4\2\2=?\7\3\2\2>=\3\2\2\2?B\3\2\2\2"+
		"@>\3\2\2\2@A\3\2\2\2AC\3\2\2\2B@\3\2\2\2CG\5\4\3\2DF\7\3\2\2ED\3\2\2\2"+
		"FI\3\2\2\2GE\3\2\2\2GH\3\2\2\2HJ\3\2\2\2IG\3\2\2\2JN\7\5\2\2KM\7\3\2\2"+
		"LK\3\2\2\2MP\3\2\2\2NL\3\2\2\2NO\3\2\2\2O\u0093\3\2\2\2PN\3\2\2\2QS\7"+
		"\3\2\2RQ\3\2\2\2SV\3\2\2\2TR\3\2\2\2TU\3\2\2\2UW\3\2\2\2VT\3\2\2\2W[\5"+
		"\6\4\2XZ\7\3\2\2YX\3\2\2\2Z]\3\2\2\2[Y\3\2\2\2[\\\3\2\2\2\\\u0093\3\2"+
		"\2\2][\3\2\2\2^`\7\3\2\2_^\3\2\2\2`c\3\2\2\2a_\3\2\2\2ab\3\2\2\2bd\3\2"+
		"\2\2ca\3\2\2\2dh\5\34\17\2eg\7\3\2\2fe\3\2\2\2gj\3\2\2\2hf\3\2\2\2hi\3"+
		"\2\2\2i\u0093\3\2\2\2jh\3\2\2\2km\7\3\2\2lk\3\2\2\2mp\3\2\2\2nl\3\2\2"+
		"\2no\3\2\2\2oq\3\2\2\2pn\3\2\2\2qu\5&\24\2rt\7\3\2\2sr\3\2\2\2tw\3\2\2"+
		"\2us\3\2\2\2uv\3\2\2\2v\u0093\3\2\2\2wu\3\2\2\2xz\7\3\2\2yx\3\2\2\2z}"+
		"\3\2\2\2{y\3\2\2\2{|\3\2\2\2|~\3\2\2\2}{\3\2\2\2~\u0082\7\"\2\2\177\u0081"+
		"\7\3\2\2\u0080\177\3\2\2\2\u0081\u0084\3\2\2\2\u0082\u0080\3\2\2\2\u0082"+
		"\u0083\3\2\2\2\u0083\u0093\3\2\2\2\u0084\u0082\3\2\2\2\u0085\u0087\7\3"+
		"\2\2\u0086\u0085\3\2\2\2\u0087\u008a\3\2\2\2\u0088\u0086\3\2\2\2\u0088"+
		"\u0089\3\2\2\2\u0089\u008b\3\2\2\2\u008a\u0088\3\2\2\2\u008b\u008f\7$"+
		"\2\2\u008c\u008e\7\3\2\2\u008d\u008c\3\2\2\2\u008e\u0091\3\2\2\2\u008f"+
		"\u008d\3\2\2\2\u008f\u0090\3\2\2\2\u0090\u0093\3\2\2\2\u0091\u008f\3\2"+
		"\2\2\u0092\65\3\2\2\2\u0092T\3\2\2\2\u0092a\3\2\2\2\u0092n\3\2\2\2\u0092"+
		"{\3\2\2\2\u0092\u0088\3\2\2\2\u0093\u00b4\3\2\2\2\u0094\u0098\f\t\2\2"+
		"\u0095\u0097\7\3\2\2\u0096\u0095\3\2\2\2\u0097\u009a\3\2\2\2\u0098\u0096"+
		"\3\2\2\2\u0098\u0099\3\2\2\2\u0099\u009b\3\2\2\2\u009a\u0098\3\2\2\2\u009b"+
		"\u009f\t\2\2\2\u009c\u009e\7\3\2\2\u009d\u009c\3\2\2\2\u009e\u00a1\3\2"+
		"\2\2\u009f\u009d\3\2\2\2\u009f\u00a0\3\2\2\2\u00a0\u00a2\3\2\2\2\u00a1"+
		"\u009f\3\2\2\2\u00a2\u00b3\5\4\3\n\u00a3\u00a7\f\b\2\2\u00a4\u00a6\7\3"+
		"\2\2\u00a5\u00a4\3\2\2\2\u00a6\u00a9\3\2\2\2\u00a7\u00a5\3\2\2\2\u00a7"+
		"\u00a8\3\2\2\2\u00a8\u00aa\3\2\2\2\u00a9\u00a7\3\2\2\2\u00aa\u00ae\t\3"+
		"\2\2\u00ab\u00ad\7\3\2\2\u00ac\u00ab\3\2\2\2\u00ad\u00b0\3\2\2\2\u00ae"+
		"\u00ac\3\2\2\2\u00ae\u00af\3\2\2\2\u00af\u00b1\3\2\2\2\u00b0\u00ae\3\2"+
		"\2\2\u00b1\u00b3\5\4\3\t\u00b2\u0094\3\2\2\2\u00b2\u00a3\3\2\2\2\u00b3"+
		"\u00b6\3\2\2\2\u00b4\u00b2\3\2\2\2\u00b4\u00b5\3\2\2\2\u00b5\5\3\2\2\2"+
		"\u00b6\u00b4\3\2\2\2\u00b7\u00b8\7\37\2\2\u00b8\u00b9\7\13\2\2\u00b9\u00ba"+
		"\7&\2\2\u00ba\u00be\7\4\2\2\u00bb\u00bd\7\3\2\2\u00bc\u00bb\3\2\2\2\u00bd"+
		"\u00c0\3\2\2\2\u00be\u00bc\3\2\2\2\u00be\u00bf\3\2\2\2\u00bf\u00c1\3\2"+
		"\2\2\u00c0\u00be\3\2\2\2\u00c1\u00c5\5\b\5\2\u00c2\u00c4\7\3\2\2\u00c3"+
		"\u00c2\3\2\2\2\u00c4\u00c7\3\2\2\2\u00c5\u00c3\3\2\2\2\u00c5\u00c6\3\2"+
		"\2\2\u00c6\u00c8\3\2\2\2\u00c7\u00c5\3\2\2\2\u00c8\u00c9\7\5\2\2\u00c9"+
		"\7\3\2\2\2\u00ca\u00cc\7\f\2\2\u00cb\u00ca\3\2\2\2\u00cc\u00cf\3\2\2\2"+
		"\u00cd\u00cb\3\2\2\2\u00cd\u00ce\3\2\2\2\u00ce\u00e4\3\2\2\2\u00cf\u00cd"+
		"\3\2\2\2\u00d0\u00d4\5\n\6\2\u00d1\u00d3\7\3\2\2\u00d2\u00d1\3\2\2\2\u00d3"+
		"\u00d6\3\2\2\2\u00d4\u00d2\3\2\2\2\u00d4\u00d5\3\2\2\2\u00d5\u00de\3\2"+
		"\2\2\u00d6\u00d4\3\2\2\2\u00d7\u00db\7\f\2\2\u00d8\u00da\7\3\2\2\u00d9"+
		"\u00d8\3\2\2\2\u00da\u00dd\3\2\2\2\u00db\u00d9\3\2\2\2\u00db\u00dc\3\2"+
		"\2\2\u00dc\u00df\3\2\2\2\u00dd\u00db\3\2\2\2\u00de\u00d7\3\2\2\2\u00df"+
		"\u00e0\3\2\2\2\u00e0\u00de\3\2\2\2\u00e0\u00e1\3\2\2\2\u00e1\u00e3\3\2"+
		"\2\2\u00e2\u00d0\3\2\2\2\u00e3\u00e6\3\2\2\2\u00e4\u00e2\3\2\2\2\u00e4"+
		"\u00e5\3\2\2\2\u00e5\u00e7\3\2\2\2\u00e6\u00e4\3\2\2\2\u00e7\u00f1\5\f"+
		"\7\2\u00e8\u00ec\7\f\2\2\u00e9\u00eb\7\3\2\2\u00ea\u00e9\3\2\2\2\u00eb"+
		"\u00ee\3\2\2\2\u00ec\u00ea\3\2\2\2\u00ec\u00ed\3\2\2\2\u00ed\u00f0\3\2"+
		"\2\2\u00ee\u00ec\3\2\2\2\u00ef\u00e8\3\2\2\2\u00f0\u00f3\3\2\2\2\u00f1"+
		"\u00ef\3\2\2\2\u00f1\u00f2\3\2\2\2\u00f2\u0224\3\2\2\2\u00f3\u00f1\3\2"+
		"\2\2\u00f4\u00f6\7\f\2\2\u00f5\u00f4\3\2\2\2\u00f6\u00f9\3\2\2\2\u00f7"+
		"\u00f5\3\2\2\2\u00f7\u00f8\3\2\2\2\u00f8\u00fa\3\2\2\2\u00f9\u00f7\3\2"+
		"\2\2\u00fa\u00fe\5\f\7\2\u00fb\u00fd\7\3\2\2\u00fc\u00fb\3\2\2\2\u00fd"+
		"\u0100\3\2\2\2\u00fe\u00fc\3\2\2\2\u00fe\u00ff\3\2\2\2\u00ff\u0108\3\2"+
		"\2\2\u0100\u00fe\3\2\2\2\u0101\u0105\7\f\2\2\u0102\u0104\7\3\2\2\u0103"+
		"\u0102\3\2\2\2\u0104\u0107\3\2\2\2\u0105\u0103\3\2\2\2\u0105\u0106\3\2"+
		"\2\2\u0106\u0109\3\2\2\2\u0107\u0105\3\2\2\2\u0108\u0101\3\2\2\2\u0109"+
		"\u010a\3\2\2\2\u010a\u0108\3\2\2\2\u010a\u010b\3\2\2\2\u010b\u0120\3\2"+
		"\2\2\u010c\u0110\5\n\6\2\u010d\u010f\7\3\2\2\u010e\u010d\3\2\2\2\u010f"+
		"\u0112\3\2\2\2\u0110\u010e\3\2\2\2\u0110\u0111\3\2\2\2\u0111\u011a\3\2"+
		"\2\2\u0112\u0110\3\2\2\2\u0113\u0117\7\f\2\2\u0114\u0116\7\3\2\2\u0115"+
		"\u0114\3\2\2\2\u0116\u0119\3\2\2\2\u0117\u0115\3\2\2\2\u0117\u0118\3\2"+
		"\2\2\u0118\u011b\3\2\2\2\u0119\u0117\3\2\2\2\u011a\u0113\3\2\2\2\u011b"+
		"\u011c\3\2\2\2\u011c\u011a\3\2\2\2\u011c\u011d\3\2\2\2\u011d\u011f\3\2"+
		"\2\2\u011e\u010c\3\2\2\2\u011f\u0122\3\2\2\2\u0120\u011e\3\2\2\2\u0120"+
		"\u0121\3\2\2\2\u0121\u0123\3\2\2\2\u0122\u0120\3\2\2\2\u0123\u012d\5\n"+
		"\6\2\u0124\u0128\7\f\2\2\u0125\u0127\7\3\2\2\u0126\u0125\3\2\2\2\u0127"+
		"\u012a\3\2\2\2\u0128\u0126\3\2\2\2\u0128\u0129\3\2\2\2\u0129\u012c\3\2"+
		"\2\2\u012a\u0128\3\2\2\2\u012b\u0124\3\2\2\2\u012c\u012f\3\2\2\2\u012d"+
		"\u012b\3\2\2\2\u012d\u012e\3\2\2\2\u012e\u0224\3\2\2\2\u012f\u012d\3\2"+
		"\2\2\u0130\u0132\7\f\2\2\u0131\u0130\3\2\2\2\u0132\u0135\3\2\2\2\u0133"+
		"\u0131\3\2\2\2\u0133\u0134\3\2\2\2\u0134\u0136\3\2\2\2\u0135\u0133\3\2"+
		"\2\2\u0136\u013a\5\24\13\2\u0137\u0139\7\3\2\2\u0138\u0137\3\2\2\2\u0139"+
		"\u013c\3\2\2\2\u013a\u0138\3\2\2\2\u013a\u013b\3\2\2\2\u013b\u0144\3\2"+
		"\2\2\u013c\u013a\3\2\2\2\u013d\u0141\7\f\2\2\u013e\u0140\7\3\2\2\u013f"+
		"\u013e\3\2\2\2\u0140\u0143\3\2\2\2\u0141\u013f\3\2\2\2\u0141\u0142\3\2"+
		"\2\2\u0142\u0145\3\2\2\2\u0143\u0141\3\2\2\2\u0144\u013d\3\2\2\2\u0145"+
		"\u0146\3\2\2\2\u0146\u0144\3\2\2\2\u0146\u0147\3\2\2\2\u0147\u0148\3\2"+
		"\2\2\u0148\u0152\5\f\7\2\u0149\u014d\7\f\2\2\u014a\u014c\7\3\2\2\u014b"+
		"\u014a\3\2\2\2\u014c\u014f\3\2\2\2\u014d\u014b\3\2\2\2\u014d\u014e\3\2"+
		"\2\2\u014e\u0151\3\2\2\2\u014f\u014d\3\2\2\2\u0150\u0149\3\2\2\2\u0151"+
		"\u0154\3\2\2\2\u0152\u0150\3\2\2\2\u0152\u0153\3\2\2\2\u0153\u0224\3\2"+
		"\2\2\u0154\u0152\3\2\2\2\u0155\u0157\7\f\2\2\u0156\u0155\3\2\2\2\u0157"+
		"\u015a\3\2\2\2\u0158\u0156\3\2\2\2\u0158\u0159\3\2\2\2\u0159\u015b\3\2"+
		"\2\2\u015a\u0158\3\2\2\2\u015b\u015f\5\f\7\2\u015c\u015e\7\3\2\2\u015d"+
		"\u015c\3\2\2\2\u015e\u0161\3\2\2\2\u015f\u015d\3\2\2\2\u015f\u0160\3\2"+
		"\2\2\u0160\u0169\3\2\2\2\u0161\u015f\3\2\2\2\u0162\u0166\7\f\2\2\u0163"+
		"\u0165\7\3\2\2\u0164\u0163\3\2\2\2\u0165\u0168\3\2\2\2\u0166\u0164\3\2"+
		"\2\2\u0166\u0167\3\2\2\2\u0167\u016a\3\2\2\2\u0168\u0166\3\2\2\2\u0169"+
		"\u0162\3\2\2\2\u016a\u016b\3\2\2\2\u016b\u0169\3\2\2\2\u016b\u016c\3\2"+
		"\2\2\u016c\u016d\3\2\2\2\u016d\u0177\5\24\13\2\u016e\u0172\7\f\2\2\u016f"+
		"\u0171\7\3\2\2\u0170\u016f\3\2\2\2\u0171\u0174\3\2\2\2\u0172\u0170\3\2"+
		"\2\2\u0172\u0173\3\2\2\2\u0173\u0176\3\2\2\2\u0174\u0172\3\2\2\2\u0175"+
		"\u016e\3\2\2\2\u0176\u0179\3\2\2\2\u0177\u0175\3\2\2\2\u0177\u0178\3\2"+
		"\2\2\u0178\u0224\3\2\2\2\u0179\u0177\3\2\2\2\u017a\u017c\7\f\2\2\u017b"+
		"\u017a\3\2\2\2\u017c\u017f\3\2\2\2\u017d\u017b\3\2\2\2\u017d\u017e\3\2"+
		"\2\2\u017e\u0197\3\2\2\2\u017f\u017d\3\2\2\2\u0180\u0183\5\n\6\2\u0181"+
		"\u0183\5\24\13\2\u0182\u0180\3\2\2\2\u0182\u0181\3\2\2\2\u0183\u0187\3"+
		"\2\2\2\u0184\u0186\7\3\2\2\u0185\u0184\3\2\2\2\u0186\u0189\3\2\2\2\u0187"+
		"\u0185\3\2\2\2\u0187\u0188\3\2\2\2\u0188\u0191\3\2\2\2\u0189\u0187\3\2"+
		"\2\2\u018a\u018e\7\f\2\2\u018b\u018d\7\3\2\2\u018c\u018b\3\2\2\2\u018d"+
		"\u0190\3\2\2\2\u018e\u018c\3\2\2\2\u018e\u018f\3\2\2\2\u018f\u0192\3\2"+
		"\2\2\u0190\u018e\3\2\2\2\u0191\u018a\3\2\2\2\u0192\u0193\3\2\2\2\u0193"+
		"\u0191\3\2\2\2\u0193\u0194\3\2\2\2\u0194\u0196\3\2\2\2\u0195\u0182\3\2"+
		"\2\2\u0196\u0199\3\2\2\2\u0197\u0195\3\2\2\2\u0197\u0198\3\2\2\2\u0198"+
		"\u019c\3\2\2\2\u0199\u0197\3\2\2\2\u019a\u019d\5\n\6\2\u019b\u019d\5\24"+
		"\13\2\u019c\u019a\3\2\2\2\u019c\u019b\3\2\2\2\u019d\u01a7\3\2\2\2\u019e"+
		"\u01a2\7\f\2\2\u019f\u01a1\7\3\2\2\u01a0\u019f\3\2\2\2\u01a1\u01a4\3\2"+
		"\2\2\u01a2\u01a0\3\2\2\2\u01a2\u01a3\3\2\2\2\u01a3\u01a6\3\2\2\2\u01a4"+
		"\u01a2\3\2\2\2\u01a5\u019e\3\2\2\2\u01a6\u01a9\3\2\2\2\u01a7\u01a5\3\2"+
		"\2\2\u01a7\u01a8\3\2\2\2\u01a8\u0224\3\2\2\2\u01a9\u01a7\3\2\2\2\u01aa"+
		"\u01ac\7\f\2\2\u01ab\u01aa\3\2\2\2\u01ac\u01af\3\2\2\2\u01ad\u01ab\3\2"+
		"\2\2\u01ad\u01ae\3\2\2\2\u01ae\u01c7\3\2\2\2\u01af\u01ad\3\2\2\2\u01b0"+
		"\u01b3\5\n\6\2\u01b1\u01b3\5\24\13\2\u01b2\u01b0\3\2\2\2\u01b2\u01b1\3"+
		"\2\2\2\u01b3\u01b7\3\2\2\2\u01b4\u01b6\7\3\2\2\u01b5\u01b4\3\2\2\2\u01b6"+
		"\u01b9\3\2\2\2\u01b7\u01b5\3\2\2\2\u01b7\u01b8\3\2\2\2\u01b8\u01c1\3\2"+
		"\2\2\u01b9\u01b7\3\2\2\2\u01ba\u01be\7\f\2\2\u01bb\u01bd\7\3\2\2\u01bc"+
		"\u01bb\3\2\2\2\u01bd\u01c0\3\2\2\2\u01be\u01bc\3\2\2\2\u01be\u01bf\3\2"+
		"\2\2\u01bf\u01c2\3\2\2\2\u01c0\u01be\3\2\2\2\u01c1\u01ba\3\2\2\2\u01c2"+
		"\u01c3\3\2\2\2\u01c3\u01c1\3\2\2\2\u01c3\u01c4\3\2\2\2\u01c4\u01c6\3\2"+
		"\2\2\u01c5\u01b2\3\2\2\2\u01c6\u01c9\3\2\2\2\u01c7\u01c5\3\2\2\2\u01c7"+
		"\u01c8\3\2\2\2\u01c8\u01dc\3\2\2\2\u01c9\u01c7\3\2\2\2\u01ca\u01ce\5\f"+
		"\7\2\u01cb\u01cd\7\3\2\2\u01cc\u01cb\3\2\2\2\u01cd\u01d0\3\2\2\2\u01ce"+
		"\u01cc\3\2\2\2\u01ce\u01cf\3\2\2\2\u01cf\u01d8\3\2\2\2\u01d0\u01ce\3\2"+
		"\2\2\u01d1\u01d5\7\f\2\2\u01d2\u01d4\7\3\2\2\u01d3\u01d2\3\2\2\2\u01d4"+
		"\u01d7\3\2\2\2\u01d5\u01d3\3\2\2\2\u01d5\u01d6\3\2\2\2\u01d6\u01d9\3\2"+
		"\2\2\u01d7\u01d5\3\2\2\2\u01d8\u01d1\3\2\2\2\u01d9\u01da\3\2\2\2\u01da"+
		"\u01d8\3\2\2\2\u01da\u01db\3\2\2\2\u01db\u01dd\3\2\2\2\u01dc\u01ca\3\2"+
		"\2\2\u01dd\u01de\3\2\2\2\u01de\u01dc\3\2\2\2\u01de\u01df\3\2\2\2\u01df"+
		"\u01f7\3\2\2\2\u01e0\u01e3\5\n\6\2\u01e1\u01e3\5\24\13\2\u01e2\u01e0\3"+
		"\2\2\2\u01e2\u01e1\3\2\2\2\u01e3\u01e7\3\2\2\2\u01e4\u01e6\7\3\2\2\u01e5"+
		"\u01e4\3\2\2\2\u01e6\u01e9\3\2\2\2\u01e7\u01e5\3\2\2\2\u01e7\u01e8\3\2"+
		"\2\2\u01e8\u01f1\3\2\2\2\u01e9\u01e7\3\2\2\2\u01ea\u01ee\7\f\2\2\u01eb"+
		"\u01ed\7\3\2\2\u01ec\u01eb\3\2\2\2\u01ed\u01f0\3\2\2\2\u01ee\u01ec\3\2"+
		"\2\2\u01ee\u01ef\3\2\2\2\u01ef\u01f2\3\2\2\2\u01f0\u01ee\3\2\2\2\u01f1"+
		"\u01ea\3\2\2\2\u01f2\u01f3\3\2\2\2\u01f3\u01f1\3\2\2\2\u01f3\u01f4\3\2"+
		"\2\2\u01f4\u01f6\3\2\2\2\u01f5\u01e2\3\2\2\2\u01f6\u01f9\3\2\2\2\u01f7"+
		"\u01f5\3\2\2\2\u01f7\u01f8\3\2\2\2\u01f8\u0215\3\2\2\2\u01f9\u01f7\3\2"+
		"\2\2\u01fa\u01fe\5\f\7\2\u01fb\u01fd\7\3\2\2\u01fc\u01fb\3\2\2\2\u01fd"+
		"\u0200\3\2\2\2\u01fe\u01fc\3\2\2\2\u01fe\u01ff\3\2\2\2\u01ff\u0208\3\2"+
		"\2\2\u0200\u01fe\3\2\2\2\u0201\u0205\7\f\2\2\u0202\u0204\7\3\2\2\u0203"+
		"\u0202\3\2\2\2\u0204\u0207\3\2\2\2\u0205\u0203\3\2\2\2\u0205\u0206\3\2"+
		"\2\2\u0206\u0209\3\2\2\2\u0207\u0205\3\2\2\2\u0208\u0201\3\2\2\2\u0209"+
		"\u020a\3\2\2\2\u020a\u0208\3\2\2\2\u020a\u020b\3\2\2\2\u020b\u020d\3\2"+
		"\2\2\u020c\u01fa\3\2\2\2\u020d\u020e\3\2\2\2\u020e\u020c\3\2\2\2\u020e"+
		"\u020f\3\2\2\2\u020f\u0212\3\2\2\2\u0210\u0213\5\n\6\2\u0211\u0213\5\24"+
		"\13\2\u0212\u0210\3\2\2\2\u0212\u0211\3\2\2\2\u0213\u0216\3\2\2\2\u0214"+
		"\u0216\5\f\7\2\u0215\u020c\3\2\2\2\u0215\u0214\3\2\2\2\u0216\u0220\3\2"+
		"\2\2\u0217\u021b\7\f\2\2\u0218\u021a\7\3\2\2\u0219\u0218\3\2\2\2\u021a"+
		"\u021d\3\2\2\2\u021b\u0219\3\2\2\2\u021b\u021c\3\2\2\2\u021c\u021f\3\2"+
		"\2\2\u021d\u021b\3\2\2\2\u021e\u0217\3\2\2\2\u021f\u0222\3\2\2\2\u0220"+
		"\u021e\3\2\2\2\u0220\u0221\3\2\2\2\u0221\u0224\3\2\2\2\u0222\u0220\3\2"+
		"\2\2\u0223\u00cd\3\2\2\2\u0223\u00f7\3\2\2\2\u0223\u0133\3\2\2\2\u0223"+
		"\u0158\3\2\2\2\u0223\u017d\3\2\2\2\u0223\u01ad\3\2\2\2\u0224\t\3\2\2\2"+
		"\u0225\u0229\7%\2\2\u0226\u0228\7\3\2\2\u0227\u0226\3\2\2\2\u0228\u022b"+
		"\3\2\2\2\u0229\u0227\3\2\2\2\u0229\u022a\3\2\2\2\u022a\u022c\3\2\2\2\u022b"+
		"\u0229\3\2\2\2\u022c\u0230\t\4\2\2\u022d\u022f\7\3\2\2\u022e\u022d\3\2"+
		"\2\2\u022f\u0232\3\2\2\2\u0230\u022e\3\2\2\2\u0230\u0231\3\2\2\2\u0231"+
		"\u0233\3\2\2\2\u0232\u0230\3\2\2\2\u0233\u0275\5\16\b\2\u0234\u0238\7"+
		"%\2\2\u0235\u0237\7\3\2\2\u0236\u0235\3\2\2\2\u0237\u023a\3\2\2\2\u0238"+
		"\u0236\3\2\2\2\u0238\u0239\3\2\2\2\u0239\u023b\3\2\2\2\u023a\u0238\3\2"+
		"\2\2\u023b\u023f\t\5\2\2\u023c\u023e\7\3\2\2\u023d\u023c\3\2\2\2\u023e"+
		"\u0241\3\2\2\2\u023f\u023d\3\2\2\2\u023f\u0240\3\2\2\2\u0240\u0242\3\2"+
		"\2\2\u0241\u023f\3\2\2\2\u0242\u0275\5\20\t\2\u0243\u0247\t\6\2\2\u0244"+
		"\u0246\7\3\2\2\u0245\u0244\3\2\2\2\u0246\u0249\3\2\2\2\u0247\u0245\3\2"+
		"\2\2\u0247\u0248\3\2\2\2\u0248\u024a\3\2\2\2\u0249\u0247\3\2\2\2\u024a"+
		"\u024e\t\5\2\2\u024b\u024d\7\3\2\2\u024c\u024b\3\2\2\2\u024d\u0250\3\2"+
		"\2\2\u024e\u024c\3\2\2\2\u024e\u024f\3\2\2\2\u024f\u0253\3\2\2\2\u0250"+
		"\u024e\3\2\2\2\u0251\u0254\5\16\b\2\u0252\u0254\5\20\t\2\u0253\u0251\3"+
		"\2\2\2\u0253\u0252\3\2\2\2\u0254\u0275\3\2\2\2\u0255\u0259\7&\2\2\u0256"+
		"\u0258\7\3\2\2\u0257\u0256\3\2\2\2\u0258\u025b\3\2\2\2\u0259\u0257\3\2"+
		"\2\2\u0259\u025a\3\2\2\2\u025a\u025d\3\2\2\2\u025b\u0259\3\2\2\2\u025c"+
		"\u0255\3\2\2\2\u025d\u0260\3\2\2\2\u025e\u025c\3\2\2\2\u025e\u025f\3\2"+
		"\2\2\u025f\u0261\3\2\2\2\u0260\u025e\3\2\2\2\u0261\u0262\7&\2\2\u0262"+
		"\u0266\3\2\2\2\u0263\u0265\7\3\2\2\u0264\u0263\3\2\2\2\u0265\u0268\3\2"+
		"\2\2\u0266\u0264\3\2\2\2\u0266\u0267\3\2\2\2\u0267\u0269\3\2\2\2\u0268"+
		"\u0266\3\2\2\2\u0269\u026d\t\5\2\2\u026a\u026c\7\3\2\2\u026b\u026a\3\2"+
		"\2\2\u026c\u026f\3\2\2\2\u026d\u026b\3\2\2\2\u026d\u026e\3\2\2\2\u026e"+
		"\u0272\3\2\2\2\u026f\u026d\3\2\2\2\u0270\u0273\5\16\b\2\u0271\u0273\5"+
		"\20\t\2\u0272\u0270\3\2\2\2\u0272\u0271\3\2\2\2\u0273\u0275\3\2\2\2\u0274"+
		"\u0225\3\2\2\2\u0274\u0234\3\2\2\2\u0274\u0243\3\2\2\2\u0274\u025e\3\2"+
		"\2\2\u0275\13\3\2\2\2\u0276\u027a\7\25\2\2\u0277\u0279\7\3\2\2\u0278\u0277"+
		"\3\2\2\2\u0279\u027c\3\2\2\2\u027a\u0278\3\2\2\2\u027a\u027b\3\2\2\2\u027b"+
		"\u027d\3\2\2\2\u027c\u027a\3\2\2\2\u027d\u0281\7\r\2\2\u027e\u0280\7\3"+
		"\2\2\u027f\u027e\3\2\2\2\u0280\u0283\3\2\2\2\u0281\u027f\3\2\2\2\u0281"+
		"\u0282\3\2\2\2\u0282\u0284\3\2\2\2\u0283\u0281\3\2\2\2\u0284\u0285\5\22"+
		"\n\2\u0285\r\3\2\2\2\u0286\u0287\b\b\1\2\u0287\u028b\7\4\2\2\u0288\u028a"+
		"\7\3\2\2\u0289\u0288\3\2\2\2\u028a\u028d\3\2\2\2\u028b\u0289\3\2\2\2\u028b"+
		"\u028c\3\2\2\2\u028c\u028e\3\2\2\2\u028d\u028b\3\2\2\2\u028e\u0292\5\16"+
		"\b\2\u028f\u0291\7\3\2\2\u0290\u028f\3\2\2\2\u0291\u0294\3\2\2\2\u0292"+
		"\u0290\3\2\2\2\u0292\u0293\3\2\2\2\u0293\u0295\3\2\2\2\u0294\u0292\3\2"+
		"\2\2\u0295\u0296\7\5\2\2\u0296\u02a9\3\2\2\2\u0297\u029b\5\20\t\2\u0298"+
		"\u029a\7\3\2\2\u0299\u0298\3\2\2\2\u029a\u029d\3\2\2\2\u029b\u0299\3\2"+
		"\2\2\u029b\u029c\3\2\2\2\u029c\u029e\3\2\2\2\u029d\u029b\3\2\2\2\u029e"+
		"\u02a2\7\t\2\2\u029f\u02a1\7\3\2\2\u02a0\u029f\3\2\2\2\u02a1\u02a4\3\2"+
		"\2\2\u02a2\u02a0\3\2\2\2\u02a2\u02a3\3\2\2\2\u02a3\u02a5\3\2\2\2\u02a4"+
		"\u02a2\3\2\2\2\u02a5\u02a6\5\16\b\4\u02a6\u02a9\3\2\2\2\u02a7\u02a9\7"+
		"\'\2\2\u02a8\u0286\3\2\2\2\u02a8\u0297\3\2\2\2\u02a8\u02a7\3\2\2\2\u02a9"+
		"\u02ca\3\2\2\2\u02aa\u02ae\f\6\2\2\u02ab\u02ad\7\3\2\2\u02ac\u02ab\3\2"+
		"\2\2\u02ad\u02b0\3\2\2\2\u02ae\u02ac\3\2\2\2\u02ae\u02af\3\2\2\2\u02af"+
		"\u02b1\3\2\2\2\u02b0\u02ae\3\2\2\2\u02b1\u02b5\7\t\2\2\u02b2\u02b4\7\3"+
		"\2\2\u02b3\u02b2\3\2\2\2\u02b4\u02b7\3\2\2\2\u02b5\u02b3\3\2\2\2\u02b5"+
		"\u02b6\3\2\2\2\u02b6\u02b8\3\2\2\2\u02b7\u02b5\3\2\2\2\u02b8\u02c9\5\16"+
		"\b\7\u02b9\u02bd\f\5\2\2\u02ba\u02bc\7\3\2\2\u02bb\u02ba\3\2\2\2\u02bc"+
		"\u02bf\3\2\2\2\u02bd\u02bb\3\2\2\2\u02bd\u02be\3\2\2\2\u02be\u02c0\3\2"+
		"\2\2\u02bf\u02bd\3\2\2\2\u02c0\u02c4\7\t\2\2\u02c1\u02c3\7\3\2\2\u02c2"+
		"\u02c1\3\2\2\2\u02c3\u02c6\3\2\2\2\u02c4\u02c2\3\2\2\2\u02c4\u02c5\3\2"+
		"\2\2\u02c5\u02c7\3\2\2\2\u02c6\u02c4\3\2\2\2\u02c7\u02c9\5\20\t\2\u02c8"+
		"\u02aa\3\2\2\2\u02c8\u02b9\3\2\2\2\u02c9\u02cc\3\2\2\2\u02ca\u02c8\3\2"+
		"\2\2\u02ca\u02cb\3\2\2\2\u02cb\17\3\2\2\2\u02cc\u02ca\3\2\2\2\u02cd\u02ce"+
		"\b\t\1\2\u02ce\u02d2\7\4\2\2\u02cf\u02d1\7\3\2\2\u02d0\u02cf\3\2\2\2\u02d1"+
		"\u02d4\3\2\2\2\u02d2\u02d0\3\2\2\2\u02d2\u02d3\3\2\2\2\u02d3\u02d5\3\2"+
		"\2\2\u02d4\u02d2\3\2\2\2\u02d5\u02d9\5\20\t\2\u02d6\u02d8\7\3\2\2\u02d7"+
		"\u02d6\3\2\2\2\u02d8\u02db\3\2\2\2\u02d9\u02d7\3\2\2\2\u02d9\u02da\3\2"+
		"\2\2\u02da\u02dc\3\2\2\2\u02db\u02d9\3\2\2\2\u02dc\u02dd\7\5\2\2\u02dd"+
		"\u0308\3\2\2\2\u02de\u0308\7\"\2\2\u02df\u0308\7$\2\2\u02e0\u0308\7%\2"+
		"\2\u02e1\u0308\7&\2\2\u02e2\u02e4\7#\2\2\u02e3\u02e2\3\2\2\2\u02e4\u02e5"+
		"\3\2\2\2\u02e5\u02e3\3\2\2\2\u02e5\u02e6\3\2\2\2\u02e6\u02e7\3\2\2\2\u02e7"+
		"\u0306\7\13\2\2\u02e8\u02ea\7\13\2\2\u02e9\u02eb\7#\2\2\u02ea\u02e9\3"+
		"\2\2\2\u02eb\u02ec\3\2\2\2\u02ec\u02ea\3\2\2\2\u02ec\u02ed\3\2\2\2\u02ed"+
		"\u0306\3\2\2\2\u02ee\u02f0\7#\2\2\u02ef\u02ee\3\2\2\2\u02f0\u02f1\3\2"+
		"\2\2\u02f1\u02ef\3\2\2\2\u02f1\u02f2\3\2\2\2\u02f2\u02f3\3\2\2\2\u02f3"+
		"\u0301\7\13\2\2\u02f4\u02f6\7#\2\2\u02f5\u02f4\3\2\2\2\u02f6\u02f9\3\2"+
		"\2\2\u02f7\u02f5\3\2\2\2\u02f7\u02f8\3\2\2\2\u02f8\u02fa\3\2\2\2\u02f9"+
		"\u02f7\3\2\2\2\u02fa\u02fe\7\13\2\2\u02fb\u02fd\7#\2\2\u02fc\u02fb\3\2"+
		"\2\2\u02fd\u0300\3\2\2\2\u02fe\u02fc\3\2\2\2\u02fe\u02ff\3\2\2\2\u02ff"+
		"\u0302\3\2\2\2\u0300\u02fe\3\2\2\2\u0301\u02f7\3\2\2\2\u0302\u0303\3\2"+
		"\2\2\u0303\u0301\3\2\2\2\u0303\u0304\3\2\2\2\u0304\u0306\3\2\2\2\u0305"+
		"\u02e3\3\2\2\2\u0305\u02e8\3\2\2\2\u0305\u02ef\3\2\2\2\u0306\u0308\3\2"+
		"\2\2\u0307\u02cd\3\2\2\2\u0307\u02de\3\2\2\2\u0307\u02df\3\2\2\2\u0307"+
		"\u02e0\3\2\2\2\u0307\u02e1\3\2\2\2\u0307\u0305\3\2\2\2\u0308\u0329\3\2"+
		"\2\2\u0309\u030d\f\t\2\2\u030a\u030c\7\3\2\2\u030b\u030a\3\2\2\2\u030c"+
		"\u030f\3\2\2\2\u030d\u030b\3\2\2\2\u030d\u030e\3\2\2\2\u030e\u0310\3\2"+
		"\2\2\u030f\u030d\3\2\2\2\u0310\u0314\t\2\2\2\u0311\u0313\7\3\2\2\u0312"+
		"\u0311\3\2\2\2\u0313\u0316\3\2\2\2\u0314\u0312\3\2\2\2\u0314\u0315\3\2"+
		"\2\2\u0315\u0317\3\2\2\2\u0316\u0314\3\2\2\2\u0317\u0328\5\20\t\n\u0318"+
		"\u031c\f\b\2\2\u0319\u031b\7\3\2\2\u031a\u0319\3\2\2\2\u031b\u031e\3\2"+
		"\2\2\u031c\u031a\3\2\2\2\u031c\u031d\3\2\2\2\u031d\u031f\3\2\2\2\u031e"+
		"\u031c\3\2\2\2\u031f\u0323\t\3\2\2\u0320\u0322\7\3\2\2\u0321\u0320\3\2"+
		"\2\2\u0322\u0325\3\2\2\2\u0323\u0321\3\2\2\2\u0323\u0324\3\2\2\2\u0324"+
		"\u0326\3\2\2\2\u0325\u0323\3\2\2\2\u0326\u0328\5\20\t\t\u0327\u0309\3"+
		"\2\2\2\u0327\u0318\3\2\2\2\u0328\u032b\3\2\2\2\u0329\u0327\3\2\2\2\u0329"+
		"\u032a\3\2\2\2\u032a\21\3\2\2\2\u032b\u0329\3\2\2\2\u032c\u032d\b\n\1"+
		"\2\u032d\u0331\7\4\2\2\u032e\u0330\7\3\2\2\u032f\u032e\3\2\2\2\u0330\u0333"+
		"\3\2\2\2\u0331\u032f\3\2\2\2\u0331\u0332\3\2\2\2\u0332\u0334\3\2\2\2\u0333"+
		"\u0331\3\2\2\2\u0334\u0338\5\22\n\2\u0335\u0337\7\3\2\2\u0336\u0335\3"+
		"\2\2\2\u0337\u033a\3\2\2\2\u0338\u0336\3\2\2\2\u0338\u0339\3\2\2\2\u0339"+
		"\u033b\3\2\2\2\u033a\u0338\3\2\2\2\u033b\u033c\7\5\2\2\u033c\u0367\3\2"+
		"\2\2\u033d\u0367\7\"\2\2\u033e\u0367\7$\2\2\u033f\u0367\7%\2\2\u0340\u0367"+
		"\7&\2\2\u0341\u0343\7#\2\2\u0342\u0341\3\2\2\2\u0343\u0344\3\2\2\2\u0344"+
		"\u0342\3\2\2\2\u0344\u0345\3\2\2\2\u0345\u0346\3\2\2\2\u0346\u0365\7\13"+
		"\2\2\u0347\u0349\7\13\2\2\u0348\u034a\7#\2\2\u0349\u0348\3\2\2\2\u034a"+
		"\u034b\3\2\2\2\u034b\u0349\3\2\2\2\u034b\u034c\3\2\2\2\u034c\u0365\3\2"+
		"\2\2\u034d\u034f\7#\2\2\u034e\u034d\3\2\2\2\u034f\u0350\3\2\2\2\u0350"+
		"\u034e\3\2\2\2\u0350\u0351\3\2\2\2\u0351\u0352\3\2\2\2\u0352\u0360\7\13"+
		"\2\2\u0353\u0355\7#\2\2\u0354\u0353\3\2\2\2\u0355\u0358\3\2\2\2\u0356"+
		"\u0354\3\2\2\2\u0356\u0357\3\2\2\2\u0357\u0359\3\2\2\2\u0358\u0356\3\2"+
		"\2\2\u0359\u035d\7\13\2\2\u035a\u035c\7#\2\2\u035b\u035a\3\2\2\2\u035c"+
		"\u035f\3\2\2\2\u035d\u035b\3\2\2\2\u035d\u035e\3\2\2\2\u035e\u0361\3\2"+
		"\2\2\u035f\u035d\3\2\2\2\u0360\u0356\3\2\2\2\u0361\u0362\3\2\2\2\u0362"+
		"\u0360\3\2\2\2\u0362\u0363\3\2\2\2\u0363\u0365\3\2\2\2\u0364\u0342\3\2"+
		"\2\2\u0364\u0347\3\2\2\2\u0364\u034e\3\2\2\2\u0365\u0367\3\2\2\2\u0366"+
		"\u032c\3\2\2\2\u0366\u033d\3\2\2\2\u0366\u033e\3\2\2\2\u0366\u033f\3\2"+
		"\2\2\u0366\u0340\3\2\2\2\u0366\u0364\3\2\2\2\u0367\u0388\3\2\2\2\u0368"+
		"\u036c\f\t\2\2\u0369\u036b\7\3\2\2\u036a\u0369\3\2\2\2\u036b\u036e\3\2"+
		"\2\2\u036c\u036a\3\2\2\2\u036c\u036d\3\2\2\2\u036d\u036f\3\2\2\2\u036e"+
		"\u036c\3\2\2\2\u036f\u0373\t\2\2\2\u0370\u0372\7\3\2\2\u0371\u0370\3\2"+
		"\2\2\u0372\u0375\3\2\2\2\u0373\u0371\3\2\2\2\u0373\u0374\3\2\2\2\u0374"+
		"\u0376\3\2\2\2\u0375\u0373\3\2\2\2\u0376\u0387\5\22\n\n\u0377\u037b\f"+
		"\b\2\2\u0378\u037a\7\3\2\2\u0379\u0378\3\2\2\2\u037a\u037d\3\2\2\2\u037b"+
		"\u0379\3\2\2\2\u037b\u037c\3\2\2\2\u037c\u037e\3\2\2\2\u037d\u037b\3\2"+
		"\2\2\u037e\u0382\t\3\2\2\u037f\u0381\7\3\2\2\u0380\u037f\3\2\2\2\u0381"+
		"\u0384\3\2\2\2\u0382\u0380\3\2\2\2\u0382\u0383\3\2\2\2\u0383\u0385\3\2"+
		"\2\2\u0384\u0382\3\2\2\2\u0385\u0387\5\22\n\t\u0386\u0368\3\2\2\2\u0386"+
		"\u0377\3\2\2\2\u0387\u038a\3\2\2\2\u0388\u0386\3\2\2\2\u0388\u0389\3\2"+
		"\2\2\u0389\23\3\2\2\2\u038a\u0388\3\2\2\2\u038b\u038c\7\26\2\2\u038c\u038d"+
		"\7\r\2\2\u038d\u038e\7\23\2\2\u038e\u038f\5\26\f\2\u038f\u0390\7\23\2"+
		"\2\u0390\25\3\2\2\2\u0391\u0392\7\4\2\2\u0392\u0393\5\26\f\2\u0393\u0394"+
		"\7\5\2\2\u0394\u039e\3\2\2\2\u0395\u039a\5\30\r\2\u0396\u0397\7\32\2\2"+
		"\u0397\u0399\5\30\r\2\u0398\u0396\3\2\2\2\u0399\u039c\3\2\2\2\u039a\u0398"+
		"\3\2\2\2\u039a\u039b\3\2\2\2\u039b\u039e\3\2\2\2\u039c\u039a\3\2\2\2\u039d"+
		"\u0391\3\2\2\2\u039d\u0395\3\2\2\2\u039e\27\3\2\2\2\u039f\u03a4\3\2\2"+
		"\2\u03a0\u03a1\7%\2\2\u03a1\u03a2\t\5\2\2\u03a2\u03a4\5\32\16\2\u03a3"+
		"\u039f\3\2\2\2\u03a3\u03a0\3\2\2\2\u03a4\31\3\2\2\2\u03a5\u03a6\b\16\1"+
		"\2\u03a6\u03a7\7\4\2\2\u03a7\u03a8\5\32\16\2\u03a8\u03a9\7\5\2\2\u03a9"+
		"\u03b6\3\2\2\2\u03aa\u03b0\7%\2\2\u03ab\u03ac\7\27\2\2\u03ac\u03b1\7\30"+
		"\2\2\u03ad\u03ae\7\27\2\2\u03ae\u03af\7\31\2\2\u03af\u03b1\7\30\2\2\u03b0"+
		"\u03ab\3\2\2\2\u03b0\u03ad\3\2\2\2\u03b1\u03b6\3\2\2\2\u03b2\u03b6\7\""+
		"\2\2\u03b3\u03b6\7$\2\2\u03b4\u03b6\7%\2\2\u03b5\u03a5\3\2\2\2\u03b5\u03aa"+
		"\3\2\2\2\u03b5\u03b2\3\2\2\2\u03b5\u03b3\3\2\2\2\u03b5\u03b4\3\2\2\2\u03b6"+
		"\u03bc\3\2\2\2\u03b7\u03b8\f\7\2\2\u03b8\u03b9\t\7\2\2\u03b9\u03bb\5\32"+
		"\16\b\u03ba\u03b7\3\2\2\2\u03bb\u03be\3\2\2\2\u03bc\u03ba\3\2\2\2\u03bc"+
		"\u03bd\3\2\2\2\u03bd\33\3\2\2\2\u03be\u03bc\3\2\2\2\u03bf\u03c0\7 \2\2"+
		"\u03c0\u03c1\7\13\2\2\u03c1\u03c2\7&\2\2\u03c2\u03c6\7\4\2\2\u03c3\u03c5"+
		"\7\3\2\2\u03c4\u03c3\3\2\2\2\u03c5\u03c8\3\2\2\2\u03c6\u03c4\3\2\2\2\u03c6"+
		"\u03c7\3\2\2\2\u03c7\u03c9\3\2\2\2\u03c8\u03c6\3\2\2\2\u03c9\u03cd\5\36"+
		"\20\2\u03ca\u03cc\7\3\2\2\u03cb\u03ca\3\2\2\2\u03cc\u03cf\3\2\2\2\u03cd"+
		"\u03cb\3\2\2\2\u03cd\u03ce\3\2\2\2\u03ce\u03d0\3\2\2\2\u03cf\u03cd\3\2"+
		"\2\2\u03d0\u03d1\7\5\2\2\u03d1\35\3\2\2\2\u03d2\u03d6\7\f\2\2\u03d3\u03d5"+
		"\7\3\2\2\u03d4\u03d3\3\2\2\2\u03d5\u03d8\3\2\2\2\u03d6\u03d4\3\2\2\2\u03d6"+
		"\u03d7\3\2\2\2\u03d7\u03da\3\2\2\2\u03d8\u03d6\3\2\2\2\u03d9\u03d2\3\2"+
		"\2\2\u03da\u03dd\3\2\2\2\u03db\u03d9\3\2\2\2\u03db\u03dc\3\2\2\2\u03dc"+
		"\u03df\3\2\2\2\u03dd\u03db\3\2\2\2\u03de\u03e0\5 \21\2\u03df\u03de\3\2"+
		"\2\2\u03df\u03e0\3\2\2\2\u03e0\u0406\3\2\2\2\u03e1\u03e5\5 \21\2\u03e2"+
		"\u03e4\7\3\2\2\u03e3\u03e2\3\2\2\2\u03e4\u03e7\3\2\2\2\u03e5\u03e3\3\2"+
		"\2\2\u03e5\u03e6\3\2\2\2\u03e6\u03ef\3\2\2\2\u03e7\u03e5\3\2\2\2\u03e8"+
		"\u03ec\7\f\2\2\u03e9\u03eb\7\3\2\2\u03ea\u03e9\3\2\2\2\u03eb\u03ee\3\2"+
		"\2\2\u03ec\u03ea\3\2\2\2\u03ec\u03ed\3\2\2\2\u03ed\u03f0\3\2\2\2\u03ee"+
		"\u03ec\3\2\2\2\u03ef\u03e8\3\2\2\2\u03f0\u03f1\3\2\2\2\u03f1\u03ef\3\2"+
		"\2\2\u03f1\u03f2\3\2\2\2\u03f2\u03f4\3\2\2\2\u03f3\u03e1\3\2\2\2\u03f4"+
		"\u03f5\3\2\2\2\u03f5\u03f3\3\2\2\2\u03f5\u03f6\3\2\2\2\u03f6\u03f7\3\2"+
		"\2\2\u03f7\u03f8\5 \21\2\u03f8\u0402\3\2\2\2\u03f9\u03fd\7\f\2\2\u03fa"+
		"\u03fc\7\3\2\2\u03fb\u03fa\3\2\2\2\u03fc\u03ff\3\2\2\2\u03fd\u03fb\3\2"+
		"\2\2\u03fd\u03fe\3\2\2\2\u03fe\u0401\3\2\2\2\u03ff\u03fd\3\2\2\2\u0400"+
		"\u03f9\3\2\2\2\u0401\u0404\3\2\2\2\u0402\u0400\3\2\2\2\u0402\u0403\3\2"+
		"\2\2\u0403\u0406\3\2\2\2\u0404\u0402\3\2\2\2\u0405\u03db\3\2\2\2\u0405"+
		"\u03f3\3\2\2\2\u0406\37\3\2\2\2\u0407\u040b\7&\2\2\u0408\u040a\7\3\2\2"+
		"\u0409\u0408\3\2\2\2\u040a\u040d\3\2\2\2\u040b\u0409\3\2\2\2\u040b\u040c"+
		"\3\2\2\2\u040c\u0417\3\2\2\2\u040d\u040b\3\2\2\2\u040e\u0418\7\r\2\2\u040f"+
		"\u0413\7\r\2\2\u0410\u0412\7\3\2\2\u0411\u0410\3\2\2\2\u0412\u0415\3\2"+
		"\2\2\u0413\u0411\3\2\2\2\u0413\u0414\3\2\2\2\u0414\u0416\3\2\2\2\u0415"+
		"\u0413\3\2\2\2\u0416\u0418\5\"\22\2\u0417\u040e\3\2\2\2\u0417\u040f\3"+
		"\2\2\2\u0417\u0418\3\2\2\2\u0418\u042c\3\2\2\2\u0419\u041d\7&\2\2\u041a"+
		"\u041c\7\3\2\2\u041b\u041a\3\2\2\2\u041c\u041f\3\2\2\2\u041d\u041b\3\2"+
		"\2\2\u041d\u041e\3\2\2\2\u041e\u0429\3\2\2\2\u041f\u041d\3\2\2\2\u0420"+
		"\u042a\7\r\2\2\u0421\u0425\7\r\2\2\u0422\u0424\7\3\2\2\u0423\u0422\3\2"+
		"\2\2\u0424\u0427\3\2\2\2\u0425\u0423\3\2\2\2\u0425\u0426\3\2\2\2\u0426"+
		"\u0428\3\2\2\2\u0427\u0425\3\2\2\2\u0428\u042a\5$\23\2\u0429\u0420\3\2"+
		"\2\2\u0429\u0421\3\2\2\2\u0429\u042a\3\2\2\2\u042a\u042c\3\2\2\2\u042b"+
		"\u0407\3\2\2\2\u042b\u0419\3\2\2\2\u042c!\3\2\2\2\u042d\u042e\b\22\1\2"+
		"\u042e\u0432\7\4\2\2\u042f\u0431\7\3\2\2\u0430\u042f\3\2\2\2\u0431\u0434"+
		"\3\2\2\2\u0432\u0430\3\2\2\2\u0432\u0433\3\2\2\2\u0433\u0435\3\2\2\2\u0434"+
		"\u0432\3\2\2\2\u0435\u0439\5\"\22\2\u0436\u0438\7\3\2\2\u0437\u0436\3"+
		"\2\2\2\u0438\u043b\3\2\2\2\u0439\u0437\3\2\2\2\u0439\u043a\3\2\2\2\u043a"+
		"\u043c\3\2\2\2\u043b\u0439\3\2\2\2\u043c\u043d\7\5\2\2\u043d\u0450\3\2"+
		"\2\2\u043e\u0442\5$\23\2\u043f\u0441\7\3\2\2\u0440\u043f\3\2\2\2\u0441"+
		"\u0444\3\2\2\2\u0442\u0440\3\2\2\2\u0442\u0443\3\2\2\2\u0443\u0445\3\2"+
		"\2\2\u0444\u0442\3\2\2\2\u0445\u0449\7\t\2\2\u0446\u0448\7\3\2\2\u0447"+
		"\u0446\3\2\2\2\u0448\u044b\3\2\2\2\u0449\u0447\3\2\2\2\u0449\u044a\3\2"+
		"\2\2\u044a\u044c\3\2\2\2\u044b\u0449\3\2\2\2\u044c\u044d\5\"\22\4\u044d"+
		"\u0450\3\2\2\2\u044e\u0450\7\'\2\2\u044f\u042d\3\2\2\2\u044f\u043e\3\2"+
		"\2\2\u044f\u044e\3\2\2\2\u0450\u0471\3\2\2\2\u0451\u0455\f\6\2\2\u0452"+
		"\u0454\7\3\2\2\u0453\u0452\3\2\2\2\u0454\u0457\3\2\2\2\u0455\u0453\3\2"+
		"\2\2\u0455\u0456\3\2\2\2\u0456\u0458\3\2\2\2\u0457\u0455\3\2\2\2\u0458"+
		"\u045c\7\t\2\2\u0459\u045b\7\3\2\2\u045a\u0459\3\2\2\2\u045b\u045e\3\2"+
		"\2\2\u045c\u045a\3\2\2\2\u045c\u045d\3\2\2\2\u045d\u045f\3\2\2\2\u045e"+
		"\u045c\3\2\2\2\u045f\u0470\5\"\22\7\u0460\u0464\f\5\2\2\u0461\u0463\7"+
		"\3\2\2\u0462\u0461\3\2\2\2\u0463\u0466\3\2\2\2\u0464\u0462\3\2\2\2\u0464"+
		"\u0465\3\2\2\2\u0465\u0467\3\2\2\2\u0466\u0464\3\2\2\2\u0467\u046b\7\t"+
		"\2\2\u0468\u046a\7\3\2\2\u0469\u0468\3\2\2\2\u046a\u046d\3\2\2\2\u046b"+
		"\u0469\3\2\2\2\u046b\u046c\3\2\2\2\u046c\u046e\3\2\2\2\u046d\u046b\3\2"+
		"\2\2\u046e\u0470\5$\23\2\u046f\u0451\3\2\2\2\u046f\u0460\3\2\2\2\u0470"+
		"\u0473\3\2\2\2\u0471\u046f\3\2\2\2\u0471\u0472\3\2\2\2\u0472#\3\2\2\2"+
		"\u0473\u0471\3\2\2\2\u0474\u0475\b\23\1\2\u0475\u0479\7\4\2\2\u0476\u0478"+
		"\7\3\2\2\u0477\u0476\3\2\2\2\u0478\u047b\3\2\2\2\u0479\u0477\3\2\2\2\u0479"+
		"\u047a\3\2\2\2\u047a\u047c\3\2\2\2\u047b\u0479\3\2\2\2\u047c\u0480\5$"+
		"\23\2\u047d\u047f\7\3\2\2\u047e\u047d\3\2\2\2\u047f\u0482\3\2\2\2\u0480"+
		"\u047e\3\2\2\2\u0480\u0481\3\2\2\2\u0481\u0483\3\2\2\2\u0482\u0480\3\2"+
		"\2\2\u0483\u0484\7\5\2\2\u0484\u0488\3\2\2\2\u0485\u0488\7\"\2\2\u0486"+
		"\u0488\7$\2\2\u0487\u0474\3\2\2\2\u0487\u0485\3\2\2\2\u0487\u0486\3\2"+
		"\2\2\u0488\u04a9\3\2\2\2\u0489\u048d\f\6\2\2\u048a\u048c\7\3\2\2\u048b"+
		"\u048a\3\2\2\2\u048c\u048f\3\2\2\2\u048d\u048b\3\2\2\2\u048d\u048e\3\2"+
		"\2\2\u048e\u0490\3\2\2\2\u048f\u048d\3\2\2\2\u0490\u0494\t\2\2\2\u0491"+
		"\u0493\7\3\2\2\u0492\u0491\3\2\2\2\u0493\u0496\3\2\2\2\u0494\u0492\3\2"+
		"\2\2\u0494\u0495\3\2\2\2\u0495\u0497\3\2\2\2\u0496\u0494\3\2\2\2\u0497"+
		"\u04a8\5$\23\7\u0498\u049c\f\5\2\2\u0499\u049b\7\3\2\2\u049a\u0499\3\2"+
		"\2\2\u049b\u049e\3\2\2\2\u049c\u049a\3\2\2\2\u049c\u049d\3\2\2\2\u049d"+
		"\u049f\3\2\2\2\u049e\u049c\3\2\2\2\u049f\u04a3\t\3\2\2\u04a0\u04a2\7\3"+
		"\2\2\u04a1\u04a0\3\2\2\2\u04a2\u04a5\3\2\2\2\u04a3\u04a1\3\2\2\2\u04a3"+
		"\u04a4\3\2\2\2\u04a4\u04a6\3\2\2\2\u04a5\u04a3\3\2\2\2\u04a6\u04a8\5$"+
		"\23\6\u04a7\u0489\3\2\2\2\u04a7\u0498\3\2\2\2\u04a8\u04ab\3\2\2\2\u04a9"+
		"\u04a7\3\2\2\2\u04a9\u04aa\3\2\2\2\u04aa%\3\2\2\2\u04ab\u04a9\3\2\2\2"+
		"\u04ac\u04b1\5(\25\2\u04ad\u04b1\5*\26\2\u04ae\u04b1\5,\27\2\u04af\u04b1"+
		"\5.\30\2\u04b0\u04ac\3\2\2\2\u04b0\u04ad\3\2\2\2\u04b0\u04ae\3\2\2\2\u04b0"+
		"\u04af\3\2\2\2\u04b1\'\3\2\2\2\u04b2\u04b3\7\33\2\2\u04b3\u04b7\7\4\2"+
		"\2\u04b4\u04b6\7\3\2\2\u04b5\u04b4\3\2\2\2\u04b6\u04b9\3\2\2\2\u04b7\u04b5"+
		"\3\2\2\2\u04b7\u04b8\3\2\2\2\u04b8\u04ba\3\2\2\2\u04b9\u04b7\3\2\2\2\u04ba"+
		"\u04be\5\60\31\2\u04bb\u04bd\7\3\2\2\u04bc\u04bb\3\2\2\2\u04bd\u04c0\3"+
		"\2\2\2\u04be\u04bc\3\2\2\2\u04be\u04bf\3\2\2\2\u04bf\u04c1\3\2\2\2\u04c0"+
		"\u04be\3\2\2\2\u04c1\u04c2\7\5\2\2\u04c2)\3\2\2\2\u04c3\u04c4\7\34\2\2"+
		"\u04c4\u04c8\7\4\2\2\u04c5\u04c7\7\3\2\2\u04c6\u04c5\3\2\2\2\u04c7\u04ca"+
		"\3\2\2\2\u04c8\u04c6\3\2\2\2\u04c8\u04c9\3\2\2\2\u04c9\u04cb\3\2\2\2\u04ca"+
		"\u04c8\3\2\2\2\u04cb\u04cf\5\60\31\2\u04cc\u04ce\7\3\2\2\u04cd\u04cc\3"+
		"\2\2\2\u04ce\u04d1\3\2\2\2\u04cf\u04cd\3\2\2\2\u04cf\u04d0\3\2\2\2\u04d0"+
		"\u04d2\3\2\2\2\u04d1\u04cf\3\2\2\2\u04d2\u04d3\7\5\2\2\u04d3+\3\2\2\2"+
		"\u04d4\u04d5\7\35\2\2\u04d5\u04d9\7\4\2\2\u04d6\u04d8\7\3\2\2\u04d7\u04d6"+
		"\3\2\2\2\u04d8\u04db\3\2\2\2\u04d9\u04d7\3\2\2\2\u04d9\u04da\3\2\2\2\u04da"+
		"\u04dc\3\2\2\2\u04db\u04d9\3\2\2\2\u04dc\u04e0\5\60\31\2\u04dd\u04df\7"+
		"\3\2\2\u04de\u04dd\3\2\2\2\u04df\u04e2\3\2\2\2\u04e0\u04de\3\2\2\2\u04e0"+
		"\u04e1\3\2\2\2\u04e1\u04e3\3\2\2\2\u04e2\u04e0\3\2\2\2\u04e3\u04e4\7\5"+
		"\2\2\u04e4-\3\2\2\2\u04e5\u04e6\7\36\2\2\u04e6\u04ea\7\4\2\2\u04e7\u04e9"+
		"\7\3\2\2\u04e8\u04e7\3\2\2\2\u04e9\u04ec\3\2\2\2\u04ea\u04e8\3\2\2\2\u04ea"+
		"\u04eb\3\2\2\2\u04eb\u04ed\3\2\2\2\u04ec\u04ea\3\2\2\2\u04ed\u04f1\5\60"+
		"\31\2\u04ee\u04f0\7\3\2\2\u04ef\u04ee\3\2\2\2\u04f0\u04f3\3\2\2\2\u04f1"+
		"\u04ef\3\2\2\2\u04f1\u04f2\3\2\2\2\u04f2\u04f4\3\2\2\2\u04f3\u04f1\3\2"+
		"\2\2\u04f4\u04f5\7\5\2\2\u04f5/\3\2\2\2\u04f6\u04f9\5\6\4\2\u04f7\u04f9"+
		"\5\34\17\2\u04f8\u04f6\3\2\2\2\u04f8\u04f7\3\2\2\2\u04f9\61\3\2\2\2\u00c8"+
		"9@GNT[ahnu{\u0082\u0088\u008f\u0092\u0098\u009f\u00a7\u00ae\u00b2\u00b4"+
		"\u00be\u00c5\u00cd\u00d4\u00db\u00e0\u00e4\u00ec\u00f1\u00f7\u00fe\u0105"+
		"\u010a\u0110\u0117\u011c\u0120\u0128\u012d\u0133\u013a\u0141\u0146\u014d"+
		"\u0152\u0158\u015f\u0166\u016b\u0172\u0177\u017d\u0182\u0187\u018e\u0193"+
		"\u0197\u019c\u01a2\u01a7\u01ad\u01b2\u01b7\u01be\u01c3\u01c7\u01ce\u01d5"+
		"\u01da\u01de\u01e2\u01e7\u01ee\u01f3\u01f7\u01fe\u0205\u020a\u020e\u0212"+
		"\u0215\u021b\u0220\u0223\u0229\u0230\u0238\u023f\u0247\u024e\u0253\u0259"+
		"\u025e\u0266\u026d\u0272\u0274\u027a\u0281\u028b\u0292\u029b\u02a2\u02a8"+
		"\u02ae\u02b5\u02bd\u02c4\u02c8\u02ca\u02d2\u02d9\u02e5\u02ec\u02f1\u02f7"+
		"\u02fe\u0303\u0305\u0307\u030d\u0314\u031c\u0323\u0327\u0329\u0331\u0338"+
		"\u0344\u034b\u0350\u0356\u035d\u0362\u0364\u0366\u036c\u0373\u037b\u0382"+
		"\u0386\u0388\u039a\u039d\u03a3\u03b0\u03b5\u03bc\u03c6\u03cd\u03d6\u03db"+
		"\u03df\u03e5\u03ec\u03f1\u03f5\u03fd\u0402\u0405\u040b\u0413\u0417\u041d"+
		"\u0425\u0429\u042b\u0432\u0439\u0442\u0449\u044f\u0455\u045c\u0464\u046b"+
		"\u046f\u0471\u0479\u0480\u0487\u048d\u0494\u049c\u04a3\u04a7\u04a9\u04b0"+
		"\u04b7\u04be\u04c8\u04cf\u04d9\u04e0\u04ea\u04f1\u04f8";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}