package com.midea.cloud.srm.biz.pj.sou.metadata.utils;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.srm.biz.pj.sou.metadata.enums.ConditionType;
import com.midea.cloud.srm.biz.pj.sou.metadata.enums.JoinType;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.dto.MetadataQueryDTO;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.dto.MetadataQueryDetailDTO;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.apache.commons.collections4.CollectionUtils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *
 * </pre>
 *
 * @author huangyq154@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/8/16 17:48
 *  修改内容:
 * </pre>
 */
public class SqlConvertUtil {

    public static MetadataQueryDTO buildQueryDtoBySql(String sql) {
        CCJSqlParserManager pm = new CCJSqlParserManager();
        Statement statement = null;
        try {
            statement = pm.parse(new StringReader(sql));
        } catch (JSQLParserException e) {
            throw new BaseException("SQL解析异常，请检查SQL输入是否正确");
        }
        if (!(statement instanceof Select)) {
            throw new BaseException("SQL类型不合法，只接受select语句");
        }
        PlainSelect plainSelect = (PlainSelect) ((Select) statement).getSelectBody();
        if (CollectionUtils.isNotEmpty(plainSelect.getJoins())) {
            throw new BaseException("不接受联表查询");
        }
        List<SelectItem> selectItems = plainSelect.getSelectItems();
        if (CollectionUtils.isEmpty(selectItems)) {
            throw new BaseException("查询列不能为空");
        }
        FromItem fromItem = plainSelect.getFromItem();
        if (!(fromItem instanceof Table)) {
            throw new BaseException("查询对象只能为表");
        }

        MetadataQueryDTO queryDto = new MetadataQueryDTO();
        queryDto.setConvertAttr(false);
        //表名
        queryDto.setTableName(((Table) fromItem).getName());
        //查询列
        StringBuilder fixSelect = new StringBuilder();
        for (SelectItem selectItem : selectItems) {
            fixSelect.append(",").append(selectItem.toString());
        }
        queryDto.setFixSelect(fixSelect.substring(1));
        //查询条件
        Expression whereExpression = plainSelect.getWhere();
        if (null != whereExpression) {
            addConditionByOperator(queryDto, whereExpression);
        }
        return queryDto;
    }

    private static void addConditionByOperator(MetadataQueryDTO queryDto, Expression whereExpression) {
        if (whereExpression instanceof AndExpression) {
            AndExpression and = (AndExpression) whereExpression;
            List<MetadataQueryDetailDTO> subConditions = getSubConditions(and.getLeftExpression(), and.getRightExpression());
            queryDto.addSubCondition(subConditions, JoinType.AND);
        } else if (whereExpression instanceof OrExpression) {
            OrExpression or = (OrExpression) whereExpression;
            List<MetadataQueryDetailDTO> subConditions = getSubConditions(or.getLeftExpression(), or.getRightExpression());
            queryDto.addSubCondition(subConditions, JoinType.OR);
        } else if (whereExpression instanceof Parenthesis) {
            Parenthesis parenthesis = (Parenthesis) whereExpression;
            addConditionByOperator(queryDto, parenthesis.getExpression());
        } else if (whereExpression instanceof EqualsTo) {
            EqualsTo eq = (EqualsTo) whereExpression;
            addConditionByExpression(queryDto, eq.getLeftExpression().toString(), ConditionType.EQ, eq.getRightExpression());
        } else if (whereExpression instanceof LikeExpression) {
            LikeExpression like = (LikeExpression) whereExpression;
            addConditionByExpression(queryDto, like.getLeftExpression().toString(), like.isNot() ? ConditionType.NOT_LIKE : ConditionType.LIKE, like.getRightExpression());
        } else if (whereExpression instanceof NotEqualsTo) {
            NotEqualsTo ne = (NotEqualsTo) whereExpression;
            addConditionByExpression(queryDto, ne.getLeftExpression().toString(), ConditionType.NE, ne.getRightExpression());
        } else if (whereExpression instanceof GreaterThan) {
            GreaterThan gt = (GreaterThan) whereExpression;
            addConditionByExpression(queryDto, gt.getLeftExpression().toString(), ConditionType.GT, gt.getRightExpression());
        } else if (whereExpression instanceof GreaterThanEquals) {
            GreaterThanEquals ge = (GreaterThanEquals) whereExpression;
            addConditionByExpression(queryDto, ge.getLeftExpression().toString(), ConditionType.GE, ge.getRightExpression());
        } else if (whereExpression instanceof InExpression) {
            InExpression in = (InExpression) whereExpression;
            List<Expression> expressions = ((ExpressionList) in.getRightItemsList()).getExpressions();
            addConditionByExpression(queryDto, in.getLeftExpression().toString(), in.isNot() ? ConditionType.NOT_IN : ConditionType.IN,
                    expressions.toArray(new Expression[0]));
        } else if (whereExpression instanceof IsNullExpression) {
            IsNullExpression isNull = (IsNullExpression) whereExpression;
            addConditionByExpression(queryDto, isNull.getLeftExpression().toString(), isNull.isNot() ? ConditionType.IS_NOT_NULL : ConditionType.IS_NULL, null);
        } else if (whereExpression instanceof Between) {
            Between between = (Between) whereExpression;
            addConditionByExpression(queryDto, between.getLeftExpression().toString(), ConditionType.BETWEEN,
                    between.getBetweenExpressionStart(), between.getBetweenExpressionEnd());
        }
    }

    private static List<MetadataQueryDetailDTO> getSubConditions(Expression... whereExpressions) {
        MetadataQueryDTO subQueryDto = new MetadataQueryDTO();
        for (Expression expression : whereExpressions) {
            addConditionByOperator(subQueryDto, expression);
        }
        return subQueryDto.getConditions();
    }

    private static void addConditionByExpression(MetadataQueryDTO queryDto, String fieldName, ConditionType conditionType, Expression... expressions) {
        Object value = null;
        if (ConditionType.NOT_IN.equals(conditionType) || ConditionType.IN.equals(conditionType) || ConditionType.BETWEEN.equals(conditionType)) {
            List<Object> values = new ArrayList<>();
            if (null != expressions && expressions.length > 0) {
                for (Expression expression : expressions) {
                    values.add(getExpressionValue(expression));
                }
            }
            if (CollectionUtils.isNotEmpty(values)) {
                value = values;
            }
        } else if (null != expressions) {
            value = getExpressionValue(expressions[0]);
        }
        if ((ConditionType.IS_NOT_NULL.equals(conditionType) || ConditionType.IS_NULL.equals(conditionType)) || null != value) {
            queryDto.addCondition(fieldName, conditionType, value, null);
        }
    }

    private static Object getExpressionValue(Expression expression) {
        Object value = null;
        if (expression instanceof DateValue) {
            value = DateUtil.format(((DateValue) expression).getValue(), "yyyy-MM-dd HH:mm:ss");
        } else if (expression instanceof DoubleValue) {
            value = ((DoubleValue) expression).getValue();
        } else if (expression instanceof LongValue) {
            value = ((LongValue) expression).getValue();
        } else if (expression instanceof StringValue) {
            value = ((StringValue) expression).getValue();
        }
        return value;
    }
}
