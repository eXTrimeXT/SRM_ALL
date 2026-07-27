package com.midea.cloud.srm.biz.pj.sou.quotetemplate.service;

import com.midea.cloud.srm.biz.pj.sou.metadata.model.vo.MetadataDataVO;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempFormula;
import com.midea.cloud.srm.model.sou.quotetemplate.dto.antlr.QuoteFunction;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 报价属性 - 数据读写服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/08/31
 */
public interface ISouQuoteTempAttrDataService {

    /**
     *
     * 通过可执行sql读数据
     * @param executableSql 可执行公式
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<MetadataDataVO> readByExecutableSql(String executableSql, @Nullable Integer pageNum, @Nullable Integer pageSize);

    /**
     * 根据界面定义的函数，生成可执行SQL模板
     * 例如界面定义 "ref.xx属性(a=1,b=2,c=${变量},return=a+b)"
     * 则根据属性/字段与表的映射关系，生成SQL模板 "select col_1 + col_2 from t_1 where col_1 = 1 and col_2 = 2 and col_3 = ${变量}"
     * @param func
     * @param env
     * @return
     */
    String/* sql */ generateExecutableSqlForSingleResult(QuoteFunction func, Set<String> env);

    /**
     * 专门用于进行基于报价属性的动态表的查询，通过 "ref.xx属性(a=1,b=2)" 来定义过滤条件，但是会忽略return形参信息，自动把报价属性中所有字段返回
     *
     * 公共表:   "select id, a as fieldId1, b as fieldId2 from table where ..."
     * 非公共表: "select id, a as fieldId1, b as fieldId2 from table where ..."
     * @param ref
     * @param env
     * @return
     */
    String/* sql */ generateExecutableSqlForAttrQuery(QuoteFunction ref, Set<String> env);

    /**
     * 专门用于进行API查询，通过 "fun.xx函数(a=1,b=2)" 来定义查询信息
     * @param func
     * @param env
     * @return
     */
    String/* json */ generateJsonForApiQuery(QuoteFunction func, Set<String> env);

    /**
     * 报价属性动态表数据分析处理
     * @param attrId
     * @param businessId
     * @param dataList
     * @param needValidRequired
     */
    void analysisAttrDataList(final long attrId, @Nullable String businessId, List<Map<String/* fieldId */, Object>> dataList, boolean needValidRequired);

    /**
     * 写数据
     * @param attrId 报价模型ID
     * @param businessId 业务单据ID
     * @param dataList 数据
     * @param needValidRequired 是否按照报价模板字段要求校验必填
     * @param deleteOthers 删掉除了 dataList 以外的其他数据(如果是非全局表，则删除范围仅限同一businessId下)
     */
    void writeAttrs(final long attrId, @Nullable String businessId, List<Map<String/* fieldId */, Object>> dataList,
                    boolean needValidRequired, boolean deleteOthers);

    /**
     * 执行单一函数(包括自定义函数、其他报价属性)
     * 包括: 自定义函数"fun.xxx(...)"、其他报价属性"ref.xxx(...)"
     * @param func 函数信息
     * @param rowData 当前行数据
     * @return
     */
    @Nullable
    Object executeFunction(QuoteFunction func, Map<String/* fieldName */, Object> rowData);

    /**
     * 执行公式
     * @param formula 公式信息
     * @param rowData 当前的行数据
     * @param rowIndex 当前计算的是第几行数据
     * @return 公式计算结果(如果为null，则说明不满足应用条件)
     */
    BigDecimal executeFormula(SouQuoteTempFormula formula, Map<String/* fieldName */, Object> rowData, int rowIndex);

    /**
     * 写数据 -- 仅更新
     * @param attrId
     * @param businessId
     * @param dataList
     * @param needValidRequired
     * @param userId
     */
    void writeAttrsOnlyUpdate(final long attrId, @Nullable String businessId, List<Map<String/* fieldId */, Object>> dataList,
                              boolean needValidRequired,Long userId);

}
