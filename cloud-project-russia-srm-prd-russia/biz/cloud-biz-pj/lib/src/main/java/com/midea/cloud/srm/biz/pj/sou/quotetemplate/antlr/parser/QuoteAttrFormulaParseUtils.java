package com.midea.cloud.srm.biz.pj.sou.quotetemplate.antlr.parser;

import com.midea.cloud.srm.biz.pj.sou.quotetemplate.antlr.core.QuoteAttrFormulaLexer;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.antlr.core.QuoteAttrFormulaParser;
import com.midea.cloud.srm.model.sou.quotetemplate.dto.antlr.QuoteFormula;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 * 报价属性 - 公式解析工具
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/08/02
 */
public class QuoteAttrFormulaParseUtils {

    private QuoteAttrFormulaParseUtils() {}

    public static QuoteFormula parseFormula(String formula) {
        QuoteAttrFormulaLexer lexer = new QuoteAttrFormulaLexer(CharStreams.fromString(formula));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        QuoteAttrFormulaParser parser = new QuoteAttrFormulaParser(tokens);
        ParseTree tree = parser.line();

        ParseTreeWalker walker = new ParseTreeWalker();
        QuoteAttrFormulaCustomListener listener = new QuoteAttrFormulaCustomListener();
        walker.walk(listener, tree);

        QuoteFormula f = new QuoteFormula();
        f.setExecutableFormula(listener.getFormula());
        f.setFunctions(listener.getFunctions());

        return f;
    }

}
