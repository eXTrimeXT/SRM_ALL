package com.midea.cloud.srm.biz.pj.sou.quotetemplate.utils;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.ast.SpelNodeImpl;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * 用于修改spel ast树的节点
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/29
 */
public class SpelNodeReplaceWrapper extends SpelNodeImpl {

    private SpelNodeImpl node;
    private final int pos;
    private SpelNodeImpl[] children = new SpelNodeImpl[0];

    public SpelNodeReplaceWrapper(SpelNode node) {
        super(0, 0);
        this.node = (SpelNodeImpl) node;
        try {
            Field childField = SpelNodeImpl.class.getDeclaredField("children");
            ReflectionUtils.makeAccessible(childField);
            this.children = (SpelNodeImpl[]) childField.get(node);

            Field posField = SpelNodeImpl.class.getDeclaredField("pos");
            ReflectionUtils.makeAccessible(posField);
            this.pos = (int) posField.get(node);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void replaceChildNode(int arrayIndex, SpelNodeImpl childNode) {
        this.children[arrayIndex] = childNode;
    }

    @Override
    public TypedValue getValueInternal(ExpressionState expressionState) throws EvaluationException {
        return node.getValueInternal(expressionState);
    }

    @Override
    public String toStringAST() {
        return node.toStringAST();
    }

    public static SpelNodeReplaceWrapper build(SpelNode node) {
        return new SpelNodeReplaceWrapper(node);
    }

}
