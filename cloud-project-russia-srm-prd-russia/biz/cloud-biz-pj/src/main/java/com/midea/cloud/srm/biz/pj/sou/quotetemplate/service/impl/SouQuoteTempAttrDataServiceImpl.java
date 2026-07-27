package com.midea.cloud.srm.biz.pj.sou.quotetemplate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.metadata.enums.ConditionType;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.dto.MetadataDataDTO;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.dto.MetadataQueryDTO;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.dto.MetadataQueryDetailDTO;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.vo.MetadataDataVO;
import com.midea.cloud.srm.biz.pj.sou.metadata.service.MetadataDataService;
import com.midea.cloud.srm.biz.pj.sou.metadata.utils.SqlConvertUtil;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.dao.SouQuoteTempApiRepositoryImpl;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.dao.SouQuoteTempAttrRepositoryImpl;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.dao.SouQuoteTempAttrTableRepositoryImpl;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.dao.SouQuoteTempFieldRepositoryImpl;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.service.ISouQuoteTempApiService;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.service.ISouQuoteTempAttrDataService;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.utils.FormulaUtils;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.validator.attr.SouQuoteTempAttrValidator;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.*;
import com.midea.cloud.srm.model.bid.quotetemplate.enums.SouQuoteTempApiStatusEnum;
import com.midea.cloud.srm.model.bid.quotetemplate.enums.SouQuoteTempAttrStatusEnum;
import com.midea.cloud.srm.model.bid.quotetemplate.enums.SouQuoteTempFieldTypeEnum;
import com.midea.cloud.srm.model.bid.quotetemplate.vo.SouQuoteTempApiDetailVO;
import com.midea.cloud.srm.model.bid.quotetemplate.vo.SouQuoteTempAttrTableColumnVO;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.quotetemplate.dto.antlr.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 报价属性 - 数据读写服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/08/31
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class SouQuoteTempAttrDataServiceImpl implements ISouQuoteTempAttrDataService {

    @Autowired
    private MetadataDataService metadataDataService;
    @Autowired
    private SouQuoteTempAttrRepositoryImpl souQuoteTempAttrRepository;
    @Autowired
    private SouQuoteTempFieldRepositoryImpl souQuoteTempFieldRepository;
    @Autowired
    private SouQuoteTempAttrTableRepositoryImpl souQuoteTempAttrTableRepository;
    @Autowired
    private SouQuoteTempApiRepositoryImpl souQuoteTempApiRepository;
    @Autowired
    private ISouQuoteTempAttrDataService souQuoteTempAttrDataService;
    @Autowired
    private ISouQuoteTempApiService souQuoteTempApiService;
    @Autowired
    private BaseClient baseClient;

    /**
     * 通过可执行sql读数据
     * @param executableSql 可执行公式
     */
    @Override
    public List<MetadataDataVO> readByExecutableSql(String executableSql, @Nullable Integer pageNum, @Nullable Integer pageSize) {
        MetadataQueryDTO queryParam = SqlConvertUtil.buildQueryDtoBySql(executableSql);
        if (pageNum != null && pageSize != null) {
            queryParam.setPageNum(pageNum);
            queryParam.setPageSize(pageSize);
            PageInfo<MetadataDataVO> pageInfo = metadataDataService.getListByPage(queryParam);
            Page<MetadataDataVO> page = new Page<>(pageNum, pageSize);
            page.setTotal(pageInfo.getTotal());
            page.addAll(pageInfo.getList());
            return page;
        } else {
            return metadataDataService.getList(queryParam);
        }
    }

    /**
     * 根据界面定义的函数，生成可执行SQL模板
     * 例如界面定义 "ref.xx属性(a=1,b=2,c=${变量},return=a+b)"
     * 则根据属性/字段与表的映射关系，生成SQL模板 "select col_1 + col_2 as myresult from t_1 where col_1 = 1 and col_2 = 2 and col_3 = ${变量}"
     */
    @Override
    public String/* sql */ generateExecutableSqlForSingleResult(QuoteFunction func, Set<String> env) {
        AssertUtils.isTrue(QuoteFunctionType.REF.equals(func.getType()), "错误的方法调用，不支持对自定义函数的查询");
        String funMsg = JSON.toJSONString(func);

        SouQuoteTempAttr attr = souQuoteTempAttrRepository.lambdaQuery()
                .eq(SouQuoteTempAttr::getAttrName, func.getName())
                .one();
        AssertUtils.notNull(attr, LocaleHandler.getLocaleMsg("报价模型[{0}]不存在"), func.getName());
        AssertUtils.isTrue(SouQuoteTempAttrStatusEnum.VALID.equals(attr.getAttrStatus()), LocaleHandler.getLocaleMsg("报价模型[{0}]不是生效状态"), attr.getAttrName());
        Map<String/* fieldName */, SouQuoteTempField> fieldMap = souQuoteTempFieldRepository.lambdaQuery()
                .eq(SouQuoteTempField::getAttrId, attr.getAttrId())
                .list()
                .stream().collect(Collectors.toMap(SouQuoteTempField::getFieldName, Function.identity()));
        SouQuoteTempAttrTable tableInfo = souQuoteTempAttrTableRepository.lambdaQuery()
                .eq(SouQuoteTempAttrTable::getAttrId, attr.getAttrId())
                .one();

        /* 确保ref中使用的均是在报价属性中可以找到的 */
        func.getVariables().forEach((name, var) -> AssertUtils.isTrue(fieldMap.containsKey(var.getOriginName()), "报价属性[{0}]中不存在[{1}]字段", attr.getAttrName(), var.getOriginName()));

        /* 处理函数形参部分 */
        String whereSql; {
            StringBuilder sb = new StringBuilder(" 1 = 1 ");
            for (QuoteParam param : func.getParams()) {
                if (QuoteFunction.RETURN_KEY.equals(param.getKey())) { continue; }
                sb.append(" and ");
                sb.append(param.getKey());
                switch (param.getType()) {
                    case EQ:
                        sb.append("=");
                        break;
                    case GE:
                        sb.append(">=");
                        break;
                    case GT:
                        sb.append(">");
                        break;
                    case LE:
                        sb.append("<=");
                        break;
                    case LT:
                        sb.append(">");
                        break;
                    case NE:
                        sb.append("!=");
                        break;
                    case LIKE:
                        sb.append(" like ");
                        break;
                    default:
                        throw new IllegalArgumentException("无法识别的符号" + param.getType().name());
                }
                sb.append(param.getValue());
            }
            whereSql = sb.toString();
            for (QuoteFunctionVariable var : func.getVariables().values()) {
                if (!QuoteFunctionVarType.FIELD.equals(var.getType())) { continue; }

                whereSql = whereSql.replaceAll(var.getVarName(), tableInfo.getTableColumns().get(var.getOriginName()).getFieldName());
            }
        }
        String selectSql; {
            QuoteParam param = func.getParamsByName(QuoteFunction.RETURN_KEY);
            AssertUtils.notNull(param, "未定义return语句");
            selectSql = param.getValue();
            AssertUtils.notNull(selectSql, "未定义return语句");
            for (QuoteFunctionVariable var : func.getVariables().values()) {
                if (!QuoteFunctionVarType.FIELD.equals(var.getType())) { continue; }

                selectSql = selectSql.replaceAll(var.getVarName(), tableInfo.getTableColumns().get(var.getOriginName()).getFieldName());
            }
        }
        boolean needGlobalSearch = false;

        /* "select 单价 * 数量 from t where a = 1 and b = 2" */
        /* 分析SQL，并进一步解析转化select语句部分 */
        String sql = MessageFormat.format("select {0} as {1} from {2} where {3}", selectSql, SouQuoteTempAttrTableColumnVO.SELECT_COLUMN, tableInfo.getTableName(), whereSql);
        if (!Enable.Y.equals(attr.getIsGlobal()) && !needGlobalSearch) {
            /* 对于非全局性表，如果要求搜索范围不是全局性，则必须要设定businessId，避免查询到非当前业务范围的数据 */
            sql = sql + MessageFormat.format(" and {0}='''${'{0}'}'''", SouQuoteTempAttrTableColumnVO.BUSINESS_ID);
        }

        return sql;
    }

    /**
     * 专门用于进行基于报价属性的动态表的查询，通过 "ref.xx属性(a=1,b=2)" 来定义过滤条件，但是会忽略return形参信息，自动把报价属性中所有字段返回
     *
     * 公共表:   "select id, a as fieldId1, b as fieldId2 from table where ..."
     * 非公共表: "select id, a as fieldId1, b as fieldId2 from table where ..."
     */
    @Override
    public String/* sql */ generateExecutableSqlForAttrQuery(QuoteFunction ref, Set<String> env) {
        AssertUtils.isTrue(QuoteFunctionType.REF.equals(ref.getType()), "错误的方法调用，不支持对自定义函数的查询");
        String refMsg = JSON.toJSONString(ref);

        SouQuoteTempAttr attr = souQuoteTempAttrRepository.lambdaQuery()
                .eq(SouQuoteTempAttr::getAttrName, ref.getName())
                .one();
        AssertUtils.notNull(attr, LocaleHandler.getLocaleMsg("报价模型[{0}]不存在"), ref.getName());
        AssertUtils.isTrue(SouQuoteTempAttrStatusEnum.VALID.equals(attr.getAttrStatus()), LocaleHandler.getLocaleMsg("报价模型[{0}]不是生效状态"), attr.getAttrName());
        Map<String/* fieldName */, SouQuoteTempField> fieldMap = souQuoteTempFieldRepository.lambdaQuery()
                .eq(SouQuoteTempField::getAttrId, attr.getAttrId())
                .list()
                .stream().collect(Collectors.toMap(SouQuoteTempField::getFieldName, Function.identity()));
        SouQuoteTempAttrTable tableInfo = souQuoteTempAttrTableRepository.lambdaQuery()
                .eq(SouQuoteTempAttrTable::getAttrId, attr.getAttrId())
                .one();

        /* 处理函数形参部分 */
        StringBuilder whereSql = new StringBuilder("1 = 1");
        boolean needGlobalSearch = false;
        String selectSql; {
            StringBuilder sb = new StringBuilder(100);
            sb.append(SouQuoteTempAttrTableColumnVO.TABLE_ID + ",");
            if (Enable.N.equals(attr.getIsGlobal())) {
                sb.append(SouQuoteTempAttrTableColumnVO.BUSINESS_ID + ",");
            }
            fieldMap.forEach((fieldName, field) -> {
                sb.append(MessageFormat.format("{0} as {1},", tableInfo.getTableColumns().get(fieldName).getFieldName(), field.getFieldId()));
            });
            selectSql = sb.substring(0, sb.length() - 1);
        }
        for (QuoteParam param : ref.getParams()) {
            if (QuoteFunction.RETURN_KEY.equals(param.getKey())) {
                selectSql = param.getValue();
            } else {
                whereSql.append(" and ");
                whereSql.append(param.getKey());
                switch (param.getType()) {
                    case EQ:
                        whereSql.append("=");
                        break;
                    case GE:
                        whereSql.append(">=");
                        break;
                    case GT:
                        whereSql.append(">");
                        break;
                    case LE:
                        whereSql.append("<=");
                        break;
                    case LT:
                        whereSql.append(">");
                        break;
                    case NE:
                        whereSql.append("!=");
                        break;
                    case LIKE:
                        whereSql.append(" like ");
                        break;
                    default:
                        throw new IllegalArgumentException("无法识别的符号" + param.getType().name());
                }
                whereSql.append(param.getValue());
            }
        }

        /* "select 单价 * 数量 from t where a = 1 and b = 2" */
        /* 分析SQL，并进一步解析转化select语句部分 */
        String sql = MessageFormat.format("select {0} from {1} where {2}", selectSql, tableInfo.getTableName(), whereSql);
/*        selectSql = this.analysisAndConvertSelectSql(sql, refMsg, attr, fieldMap, tableInfo, env); */
        sql = MessageFormat.format("select {0} from {1} where {2}", selectSql, tableInfo.getTableName(), whereSql);
        if (!Enable.Y.equals(attr.getIsGlobal()) && !needGlobalSearch) {
            /* 对于非全局性表，如果要求搜索范围不是全局性，则必须要设定businessId，避免查询到非当前业务范围的数据 */
            sql = sql + MessageFormat.format(" and {0}='''${'{0}'}'''", SouQuoteTempAttrTableColumnVO.BUSINESS_ID);
        }

        return sql;
    }

    /**
     * 专门用于进行API查询，通过 "fun.xx函数(a=1,b=2)" 来定义查询信息
     */
    @Override
    public String/* json */ generateJsonForApiQuery(QuoteFunction func, Set<String> env) {
        AssertUtils.isTrue(QuoteFunctionType.FUN.equals(func.getType()), "错误的方法调用，不支持对自定义函数的查询");

        SouQuoteTempApi api = souQuoteTempApiRepository.lambdaQuery()
                .eq(SouQuoteTempApi::getApiName, func.getName())
                .one();
        AssertUtils.notNull(api, LocaleHandler.getLocaleMsg("自定义函数[{0}]不存在"), func.getName());
        AssertUtils.isTrue(SouQuoteTempApiStatusEnum.VALID.equals(api.getApiStatus()), LocaleHandler.getLocaleMsg("自定义函数[{0}]不是生效状态"), api.getApiName());
        if (CollectionUtils.isNotEmpty(api.getApiDetails())) {
            Map<String, String> kvs = new HashMap<>(api.getApiDetails().size());
            Map<String/* key */, QuoteParam> paramMap = func.getParams().stream().collect(Collectors.toMap(QuoteParam::getKey, Function.identity()));
            for (SouQuoteTempApiDetailVO detail : api.getApiDetails()) {
                QuoteParam param = paramMap.get(detail.getArgName());
                if (param == null) {
                    AssertUtils.isTrue(Enable.N.equals(detail.getRequired()), LocaleHandler.getLocaleMsg("自定义函数[{0}]缺少参数[{1}]"), api.getApiName(), detail.getArgDesc());
                    kvs.put(detail.getArgName(), null);
                } else {
                    kvs.put(param.getKey(), param.getValue());
                }
            }
            return JSON.toJSONString(kvs);
        } else {
            return "{}";
        }
    }

    /**
     * 报价属性动态表数据分析处理
     */
    @Override
    public void analysisAttrDataList(final long attrId, @Nullable String businessId,
                                     List<Map<String/* fieldId */, Object>> dataList,
                                     boolean needValidRequired) {
        List<Map<String/* fieldId */, Object>> rowList = new ArrayList<>(500);

        SouQuoteTempAttr attr = souQuoteTempAttrRepository.getById(attrId);
        AssertUtils.notNull(attr, LocaleHandler.getLocaleMsg("报价模型")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), attrId);
        AssertUtils.isTrue(SouQuoteTempAttrStatusEnum.VALID.equals(attr.getAttrStatus()), LocaleHandler.getLocaleMsg("报价模型")+"[{0}]"+LocaleHandler.getLocaleMsg("不是生效状态"));
        if (Enable.Y.equals(attr.getIsGlobal())) {
            businessId = null;
        }
        List<SouQuoteTempField> fieldList = souQuoteTempFieldRepository.lambdaQuery()
                .eq(SouQuoteTempField::getAttrId, attrId)
                .list();
        Map<String/* dictCode */, Set<String/* itemCode */>> dictMap; {
            Set<String> dictCodes = fieldList.stream()
                    .filter(field -> SouQuoteTempFieldTypeEnum.DICT.equals(field.getFieldType()))
                    .map(SouQuoteTempField::getFieldValue).collect(Collectors.toSet());
            if (dictCodes.isEmpty()) {
                dictMap = Collections.emptyMap();
            } else {
                dictMap = baseClient.listByDictCode(new ArrayList<>(dictCodes)).stream()
                        .collect(Collectors.groupingBy(DictItemDTO::getDictCode, Collectors.mapping(DictItemDTO::getDictItemCode, Collectors.toSet())));
            }
        }

        Map<String/* fieldName */, Set<String>> fieldEnums = new HashMap<>(50);
        int index = 0;
        for (Map<String/* fieldId */, Object> data : dataList) {
            index++;
            Object id = data.get(SouQuoteTempAttrTableColumnVO.TABLE_ID);
            data.put(SouQuoteTempAttrTableColumnVO.TABLE_ID, id != null ? Long.parseLong(id.toString()) : null);
            if (!Enable.Y.equals(attr.getIsGlobal())) {
                data.put(SouQuoteTempAttrTableColumnVO.BUSINESS_ID, businessId);
            }
            for (SouQuoteTempField field : fieldList) {
                Object var;
                if (Enable.Y.equals(field.getForbidEdit()) && !SouQuoteTempFieldTypeEnum.FORMULA.equals(field.getFieldType())) {
                    /* 禁止编辑修改 */
                    var = field.getDefaultValue();
                } else {
                    var = data.get(field.getFieldId().toString());
                    if (var == null) {
                        if (!SouQuoteTempFieldTypeEnum.FORMULA.equals(field.getFieldType())) {
                            if (Enable.Y.equals(field.getRequired())) {
                                AssertUtils.isFalse(needValidRequired, LocaleHandler.getLocaleMsg("第")+"{0}"+LocaleHandler.getLocaleMsg("行字段")+"[{1}]"+LocaleHandler.getLocaleMsg("必填"), index, field.getFieldName());
                            }
                        }
                    } else {
                        if (var instanceof String) {
                            var = StringUtils.trimToNull((String) var);
                            if (var != null) {
                                AssertUtils.isTrue(((String)var).length() <= 200, LocaleHandler.getLocaleMsg("第")+"{0}"+LocaleHandler.getLocaleMsg("行字段")+"[{1}]"+LocaleHandler.getLocaleMsg("长度不能超过200"), index, field.getFieldName());
                            }
                        }
                    }
                }
                /* 字段的数据类型转化 */
                if (var != null) {
                    switch (field.getFieldType()) {
//                        文本
                        case TEXT:
                            break;
//                        文本枚举
                        case ENUM_TEXT:
                            Set<String> enums = fieldEnums.get(field.getFieldName());
                            if (enums == null) {
                                enums = SouQuoteTempAttrValidator.formatAndValidateEnums(field.getFieldValue(), false, "", false);
                                AssertUtils.notNull(enums, LocaleHandler.getLocaleMsg("报价模板")+"[{0}]"+LocaleHandler.getLocaleMsg("的字段")+"[{1}]"+LocaleHandler.getLocaleMsg("定义错误，没有可用的枚举值"), attr.getAttrName(), field.getFieldName());
                                fieldEnums.put(field.getFieldName(), enums);
                            }
                            AssertUtils.isTrue(enums.contains(var.toString()), LocaleHandler.getLocaleMsg("第")+"{0}"+LocaleHandler.getLocaleMsg("行字段")+"[{1}]"+LocaleHandler.getLocaleMsg("不在允许范围内"), index, field.getFieldName());
                            break;
//                        字典
                        case DICT:
                            enums = dictMap.get(field.getFieldValue());
                            if (enums != null) {
                                AssertUtils.isTrue(enums.contains(var.toString()), LocaleHandler.getLocaleMsg("第")+"{0}"+LocaleHandler.getLocaleMsg("行字段")+"[{1}]"+LocaleHandler.getLocaleMsg("不在字典值允许范围内")+":{2}", index, field.getFieldName(), var);
                            }
                            break;
//                        数字
                        case DECIMAL:
//                            公式
                        case FORMULA:
                            try {
                                var = new BigDecimal(new BigDecimal(var.toString()).setScale(SouQuoteTempAttr.DECIMAL_SCALE, RoundingMode.HALF_UP)
                                        .stripTrailingZeros().toPlainString());
                            } catch (NumberFormatException e) {
                                throw new IllegalArgumentException(MessageFormat.format("第{0}行字段[{1}]不是数字", index, field.getFieldName()));
                            }
                            break;
//                        数字枚举
                        case ENUM_DECIMAL:
                            enums = fieldEnums.get(field.getFieldName());
                            if (enums == null) {
                                enums = SouQuoteTempAttrValidator.formatAndValidateEnums(field.getFieldValue(), false, "", false);
                                AssertUtils.notNull(enums, LocaleHandler.getLocaleMsg("报价模板")+"[{0}]"+LocaleHandler.getLocaleMsg("的字段")+"[{1}]"+LocaleHandler.getLocaleMsg("定义错误，没有可用的枚举值"), attr.getAttrName(), field.getFieldName());
                                fieldEnums.put(field.getFieldName(), enums);
                            }
                            try {
                                var = new BigDecimal(new BigDecimal(var.toString()).setScale(SouQuoteTempAttr.DECIMAL_SCALE, RoundingMode.HALF_UP)
                                        .stripTrailingZeros().toPlainString());
                                AssertUtils.isTrue(enums.contains(var.toString()), LocaleHandler.getLocaleMsg("第")+"{0}"+LocaleHandler.getLocaleMsg("行字段")+"[{1}]"+LocaleHandler.getLocaleMsg("不在允许范围内"), index, field.getFieldName());
                            } catch (NumberFormatException e) {
                                throw new IllegalArgumentException(MessageFormat.format("第{0}行字段[{1}]不是数字", index, field.getFieldName()));
                            }
                            break;
//                        日期
                        case DATE:
                            if (!(var instanceof Date)) {
                                var = Date.from(LocalDateTime.parse(var.toString(), SouQuoteTempAttrValidator.DATE_TIME_FORMATTER)
                                        .atZone(ZoneId.systemDefault()).toInstant());
                            }
                            break;
                        default:
                            throw new IllegalArgumentException("报价模板[{0}]存在暂不支持的字段类型:" + field.getFieldType());
                    }
                }
                data.put(field.getFieldId().toString(), var);
            }
        }
    }

    /**
     * 写数据
     * @param attrId 报价模型ID
     * @param businessId 业务单据ID
     * @param dataList 数据
     * @param needValidRequired 是否按照报价模板字段要求校验必填
     * @param deleteOthers 删掉除了 dataList 以外的其他数据(如果是非全局表，则删除范围仅限同一businessId下)
     */
    @Override
    public void writeAttrs(final long attrId, @Nullable String businessId, List<Map<String/* fieldId */, Object>> dataList,
                           boolean needValidRequired, boolean deleteOthers) {
        SouQuoteTempAttr attr = souQuoteTempAttrRepository.getById(attrId);
        AssertUtils.notNull(attr, LocaleHandler.getLocaleMsg("报价模型")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), attrId);
        AssertUtils.isTrue(SouQuoteTempAttrStatusEnum.VALID.equals(attr.getAttrStatus()), LocaleHandler.getLocaleMsg("报价模型")+"[{0}]"+LocaleHandler.getLocaleMsg("不是生效状态"));
        if (Enable.N.equals(attr.getIsGlobal())) {
            AssertUtils.notNull(businessId, LocaleHandler.getLocaleMsg("错误的方法调用，报价模型")+"[{0}]"+LocaleHandler.getLocaleMsg("不是全局表，写数据时必须传递businessId信息"));
        } else {
            businessId = null;
        }
        Map<Long/* fieldId */, String/* fieldName */> fieldIdNameMap = souQuoteTempFieldRepository.lambdaQuery()
                .eq(SouQuoteTempField::getAttrId, attrId)
                .list()
                .stream().collect(Collectors.toMap(SouQuoteTempField::getFieldId, SouQuoteTempField::getFieldName));
        SouQuoteTempAttrTable tableInfo = souQuoteTempAttrTableRepository.lambdaQuery()
                .eq(SouQuoteTempAttrTable::getAttrId, attrId)
                .one();
        /** columnName */
        List<Map<String, Object>> rowList = new ArrayList<>(dataList.size()); {
            this.analysisAttrDataList(attrId, businessId, dataList, needValidRequired);
            /* fieldId - value */
            dataList.forEach(row -> {
                /* columnName */
                Map<String, Object> newRow = new HashMap<>(row.size());
                row.forEach((fieldId, value) -> {
                    if (value instanceof Date) {
                        value = SouQuoteTempAttrValidator.DATE_TIME_FORMATTER.format(((Date)value).toInstant().atZone(ZoneId.systemDefault()));
                    }
                    if (fieldId.equals(SouQuoteTempAttrTableColumnVO.TABLE_ID) || fieldId.equals(SouQuoteTempAttrTableColumnVO.BUSINESS_ID)) {
                        newRow.put(fieldId, value);
                    } else {
                        String columnName = tableInfo.getTableColumns().get(fieldIdNameMap.get(Long.valueOf(fieldId))).getFieldName();
                        newRow.put(columnName, value);
                    }
                });

                if (newRow.get(SouQuoteTempAttrTableColumnVO.TABLE_ID) == null) {
                    newRow.put(SouQuoteTempAttrTableColumnVO.TABLE_ID, IdGenrator.generate());
                }
                rowList.add(newRow);
            });
        }

        /* 删除旧数据 */
        if (deleteOthers) {
            MetadataDataDTO deleteParam = new MetadataDataDTO();
            deleteParam.setTableName(tableInfo.getTableName());
            if (businessId != null) {
                deleteParam.setConditions(Collections.singletonList(new MetadataQueryDetailDTO())); {
                    MetadataQueryDetailDTO condition = deleteParam.getConditions().get(0);
                    condition.setFieldName(SouQuoteTempAttrTableColumnVO.BUSINESS_ID);
                    condition.setConditionType(ConditionType.EQ);
                    condition.setFieldValue(businessId);
                }
            }
            try {
                metadataDataService.delete(deleteParam);
            } catch (Exception e) {
                throw new IllegalArgumentException("清理旧数据失败", e);
            }
        }
        /* 新增数据 */
        if (!rowList.isEmpty()) {
            MetadataDataDTO addParam = new MetadataDataDTO();
            addParam.setTableName(tableInfo.getTableName());
            rowList.forEach(row -> {
                MetadataDataDTO data = new MetadataDataDTO();
                data.putAll(row);
                addParam.addDetail(data);
            });
            try {
                metadataDataService.batchAdd(addParam);
            } catch (Exception e) {
                throw new IllegalArgumentException("新增数据失败", e);
            }
        }
    }

    @Override
    public void writeAttrsOnlyUpdate(final long attrId, @Nullable String businessId, List<Map<String/* fieldId */, Object>> dataList,
                                     boolean needValidRequired ,Long userId) {
        SouQuoteTempAttr attr = souQuoteTempAttrRepository.getById(attrId);
        AssertUtils.notNull(attr, LocaleHandler.getLocaleMsg("报价模型")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), attrId);
        AssertUtils.isTrue(SouQuoteTempAttrStatusEnum.VALID.equals(attr.getAttrStatus()), LocaleHandler.getLocaleMsg("报价模型")+"[{0}]"+LocaleHandler.getLocaleMsg("不是生效状态"));
        if (Enable.N.equals(attr.getIsGlobal())) {
            AssertUtils.notNull(businessId, LocaleHandler.getLocaleMsg("错误的方法调用，报价模型")+"[{0}]"+LocaleHandler.getLocaleMsg("不是全局表，写数据时必须传递businessId信息"));
        } else {
            businessId = null;
        }
        Map<Long/* fieldId */, String/* fieldName */> fieldIdNameMap = souQuoteTempFieldRepository.lambdaQuery()
                .eq(SouQuoteTempField::getAttrId, attrId)
                .list()
                .stream().collect(Collectors.toMap(SouQuoteTempField::getFieldId, SouQuoteTempField::getFieldName));
        SouQuoteTempAttrTable tableInfo = souQuoteTempAttrTableRepository.lambdaQuery()
                .eq(SouQuoteTempAttrTable::getAttrId, attrId)
                .one();
        List<Map<String/* columnName */, Object>> rowList = new ArrayList<>(dataList.size()); {
            this.analysisAttrDataList(attrId, businessId, dataList, needValidRequired);
            /* fieldId - value */
            dataList.forEach(row -> {
                /* columnName */
                Map<String, Object> newRow = new HashMap<>(row.size());
                row.forEach((fieldId, value) -> {
                    if (value instanceof Date) {
                        value = SouQuoteTempAttrValidator.DATE_TIME_FORMATTER.format(((Date)value).toInstant().atZone(ZoneId.systemDefault()));
                    }
                    if (fieldId.equals(SouQuoteTempAttrTableColumnVO.TABLE_ID) || fieldId.equals(SouQuoteTempAttrTableColumnVO.BUSINESS_ID)) {
                        newRow.put(fieldId, value);
                    } else {
                        String columnName = tableInfo.getTableColumns().get(fieldIdNameMap.get(Long.valueOf(fieldId))).getFieldName();
                        newRow.put(columnName, value);
                    }
                });

                if (newRow.get(SouQuoteTempAttrTableColumnVO.TABLE_ID) == null) {
                    newRow.put(SouQuoteTempAttrTableColumnVO.TABLE_ID, IdGenrator.generate());
                }
                rowList.add(newRow);
            });
        }

        /* 更新数据 */
        if (!rowList.isEmpty()) {
            MetadataDataDTO updateParam = new MetadataDataDTO();
            updateParam.setCurrentUserId(userId);
            updateParam.setTableName(tableInfo.getTableName());
            rowList.forEach(row -> {
                MetadataDataDTO data = new MetadataDataDTO();
                data.putAll(row);
                updateParam.addDetail(data);
            });
            try {
                metadataDataService.batchUpdateById(updateParam);
            } catch (Exception e) {
                throw new IllegalArgumentException("新增数据失败", e);
            }
        }
    }

    /**
     * 执行单一函数(包括自定义函数、其他报价属性)
     * 包括: 自定义函数"fun.xxx(...)"、其他报价属性"ref.xxx(...)"
     * @param func 函数信息
     * @param rowData 当前行数据
     */
    @Nullable
    @Override
    public Object executeFunction(QuoteFunction func, Map<String/* fieldName */, Object> rowData) {
        Map<String, Object> env = new HashMap<>(rowData);

        if (QuoteFunctionType.REF.equals(func.getType())) {
            /* ref */
            String sql; {
                sql = func.getTemplate();
                for (Map.Entry<String, Object> entry : env.entrySet()) {
                    if (entry.getValue() != null) {
                        if (SouQuoteTempAttrTableColumnVO.BUSINESS_ID.equals(entry.getKey())) {
                            sql = sql.replaceAll("\\$\\{" + SouQuoteTempAttrTableColumnVO.BUSINESS_ID + "}", FormulaUtils.formatRegex(entry.getValue().toString()));
                        } else if (SouQuoteTempAttrTableColumnVO.TABLE_ID.equals(entry.getKey())) {
                            sql = sql.replaceAll("\\$\\{" + SouQuoteTempAttrTableColumnVO.TABLE_ID + "}", FormulaUtils.formatRegex(entry.getValue().toString()));
                        } else {
                            QuoteFunctionVariable variable = null; {
                                for (QuoteFunctionVariable var : func.getVariables().values()) {
                                    if (var.getOriginName().equals(entry.getValue())) {
                                        variable = var;
                                        break;
                                    }
                                }
                            }
                            if (variable != null) {
                                sql = sql.replaceAll(variable.getVarName(), FormulaUtils.formatRegex(entry.getValue().toString()));
                            }
                        }
                    }
                }
            }
            List<BigDecimal> decimalList = new ArrayList<>(100); {
                List<MetadataDataVO> dataList = souQuoteTempAttrDataService.readByExecutableSql(sql, null, null);
                for (MetadataDataVO data : dataList) {
                    Object val = data.get(SouQuoteTempAttrTableColumnVO.SELECT_COLUMN);
                    if (val == null) {
                        decimalList.add(BigDecimal.ZERO);
                    } else {
                        try {
                            decimalList.add(new BigDecimal(val.toString()).stripTrailingZeros());
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException(MessageFormat.format("报价模板[{0}]查询结果不符合预期(非数字):{1}", func.getName(), sql));
                        }
                    }
                }
            }
            if (CollectionUtils.isEmpty(decimalList)){
                decimalList.add(BigDecimal.ZERO);
            }
            return decimalList;
        } else if (QuoteFunctionType.FUN.equals(func.getType())) {
            /* fun */
            String params; {
                params = func.getTemplate();
                for (Map.Entry<String, Object> entry : env.entrySet()) {
                    if (entry.getValue() != null) {
                        QuoteFunctionVariable variable = null; {
                            for (QuoteFunctionVariable var : func.getVariables().values()) {
                                if (var.getOriginName().equals(entry.getValue())) {
                                    variable = var;
                                    break;
                                }
                            }
                        }
                        if (variable != null) {
                            params = params.replaceAll(variable.getVarName(), FormulaUtils.formatRegex(entry.getValue().toString()));
                        }
                    }
                }
            }
            return souQuoteTempApiService.executeApi(func.getName(),
                    JSON.parseObject(params, new TypeReference<Map<String, Object>>() {}), false);
        } else if (QuoteFunctionType.VAR.equals(func.getType())) {
            /* var */
            return env.get(func.getName());
        } else {
            throw new IllegalArgumentException("暂不支持的公式变量类型:" + func.getType());
        }
    }

    /**
     * 执行公式
     * @param formula 公式信息
     * @param rowData 当前的行数据
     * @param rowIndex 当前计算的是第几行数据
     * @return 公式计算结果(如果为null，则说明不满足应用条件)
     */
    @Override
    public BigDecimal executeFormula(SouQuoteTempFormula formula, Map<String/* fieldName */, Object> rowData, int rowIndex) {
        /* 执行应用条件 */
        boolean condition; {
            Map<String/* fieldName */, Object> env = new HashMap<>(rowData);
            if (formula.getConditionVars() != null && !formula.getConditionVars().getDateArgMap().isEmpty()) {
                env.putAll(formula.getConditionVars().getDateArgMap());
            }
            String envMsg = JSON.toJSONString(env);
            condition = FormulaUtils.executeConditionFormulaPlus(formula.getConditionValue(), env,
                    "第" + rowIndex + "行字段[{0}]缺少数据，无法执行字段[" + formula.getFieldName() + "]应用条件:" + formula.getConditionValue(),
                    MessageFormat.format("第{0}行字段[{1}]应用条件【{2}】执行异常。执行变量:{3}", rowIndex, formula.getFieldName(), formula.getConditionValue(), envMsg),
                    MessageFormat.format("第{0}行字段[{1}]应用条件【{2}】执行异常(分母为0)。执行变量:{3}", rowIndex, formula.getFieldName(), formula.getConditionValue(), envMsg),
                    MessageFormat.format("第{0}行字段[{1}]应用条件【{2}】执行结果错误。执行变量:{3}", rowIndex, formula.getFieldName(), formula.getConditionValue(), envMsg));
        }
        if (!condition) { return null; }
        /* 执行公式计算 */
        /* 需要添加额外的变量上下文 */
        Map<String/* var */, Object> env = new HashMap<>(16); {
            /* 写入本行的其他字段 */
            formula.getFormulaVars().forEach((var, varInfo) -> {
                /* 对于公式中的每个变量，需要单独进行计算 */
                Object functionResult = this.executeFunction(varInfo, rowData);
                AssertUtils.notNull(functionResult, LocaleHandler.getLocaleMsg("第")+"{0}"+LocaleHandler.getLocaleMsg("行字段")+"[{1}]"+LocaleHandler.getLocaleMsg("的公式中")+"[{2}]"+LocaleHandler.getLocaleMsg("计算返回空值")+":{3} \n{4}",
                        rowIndex, formula.getFieldName(), varInfo.getName(), JSON.toJSONString(formula), JSON.toJSONString(env));
                if (functionResult instanceof BigDecimal) {
                    env.put(var, functionResult);
                } else if (functionResult instanceof Collection) {
                    List<BigDecimal> list = new ArrayList<>(((Collection<?>)functionResult).size());
                    ((Collection<?>) functionResult).forEach(e -> {
                        if (e instanceof BigDecimal) {
                            list.add((BigDecimal) e);
                        } else {
                            try {
                                list.add(new BigDecimal(e.toString()));
                            } catch (NumberFormatException ex) {
                                throw new IllegalArgumentException(MessageFormat.format("第{0}行字段[{1}]的公式中[{2}]计算返回值非法:{3}",
                                        rowIndex, formula.getFieldName(), varInfo.getName(),
                                        JSON.toJSONString(functionResult)));
                            }
                        }
                    });
                    env.put(var, list);
                } else {
                    try {
                        env.put(var, new BigDecimal(functionResult.toString()));
                    } catch (NumberFormatException ex) {
                        throw new IllegalArgumentException(MessageFormat.format("第{0}行字段[{1}]的公式中[{2}]计算返回值非法:{3}",
                                rowIndex, formula.getFieldName(), varInfo.getName(),
                                JSON.toJSONString(functionResult)));
                    }
                }
            });
        }
        String rowEnvMsgPlus = JSON.toJSONString(env);
        Expression expression = AviatorEvaluator.compile(formula.getExecutableFormula());
        Object result = expression.execute(env);
        if (result != null) {
            if (result instanceof BigDecimal) {
                return new BigDecimal(((BigDecimal) result).setScale(SouQuoteTempAttr.DECIMAL_SCALE, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
            } else {
                try {
                    return new BigDecimal(result.toString()).setScale(SouQuoteTempAttr.DECIMAL_SCALE, RoundingMode.HALF_UP)
                            .stripTrailingZeros();
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(MessageFormat.format("第{0}行字段[{1}]价格公式【{2}】执行结果错误。执行变量:{3}",
                            rowIndex, formula.getFieldName(), formula.getFormulaValue(), rowEnvMsgPlus));
                }
            }
        } else {
            return BigDecimal.ZERO;
        }
    }

}
