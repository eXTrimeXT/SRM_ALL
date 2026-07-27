package com.midea.cloud.srm.biz.pj.sou.quotetemplate.validator.attr;

import com.midea.cloud.common.constants.SequenceCodeConstant;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.dto.MetadataDataDTO;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.antlr.parser.QuoteAttrFormulaParseUtils;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.dao.SouQuoteTempApiRepositoryImpl;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.dao.SouQuoteTempAttrRepositoryImpl;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.dao.SouQuoteTempAttrTableRepositoryImpl;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.service.ISouQuoteTempAttrDataService;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.utils.FormulaUtils;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.metadata.dto.MetadataDTO;
import com.midea.cloud.srm.model.base.metadata.dto.MetadataDetailDTO;
import com.midea.cloud.srm.model.bid.quotetemplate.dto.SouQuoteTempAttrEditDTO;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.*;
import com.midea.cloud.srm.model.bid.quotetemplate.enums.SouQuoteTempAttrStatusEnum;
import com.midea.cloud.srm.model.bid.quotetemplate.enums.SouQuoteTempFieldTypeEnum;
import com.midea.cloud.srm.model.bid.quotetemplate.vo.*;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.common.enums.dynamic.Module;
import com.midea.cloud.srm.model.common.utils.RegexUtil;
import com.midea.cloud.srm.model.competition.utils.FormatValidateUtil;
import com.midea.cloud.srm.model.sou.quotetemplate.dto.antlr.QuoteFormula;
import com.midea.cloud.srm.model.sou.quotetemplate.dto.antlr.QuoteFunction;
import com.midea.cloud.srm.model.sou.quotetemplate.dto.antlr.QuoteFunctionType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 寻源 - 模型报价模板 - 报价属性 - 校验服务
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/27
 */
@Slf4j
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class SouQuoteTempAttrValidator {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final BigDecimal B_10_10 = BigDecimal.valueOf(Math.pow(10, 12)).stripTrailingZeros();

    @Autowired
    private SouQuoteTempAttrRepositoryImpl souQuoteTempAttrRepository;
    @Autowired
    private SouQuoteTempAttrTableRepositoryImpl souQuoteTempAttrTableRepository;
    @Autowired
    private SouQuoteTempApiRepositoryImpl souQuoteTempApiRepository;
    @Autowired
    private ISouQuoteTempAttrDataService souQuoteTempAttrDataService;
    @Autowired
    private BaseClient baseClient;

    public SouQuoteTempAttrEditPO formatValidateAndConvert(SouQuoteTempAttrEditDTO param, boolean isTempSave) {
        AssertUtils.isTrue(param.getAttr() != null, "缺少attr参数信息");
        /* 1: 设置上下文 */
        SouQuoteTempAttrEditDtoContext.setContextHolder(this.buildContext(param));

        try {
            /* 2: 数据格式化及校验 */
            this.formatAndValidate(param, isTempSave);
            /* 5: 数据转换 */
            SouQuoteTempAttrEditPO po = this.convert(param, isTempSave);
            /* 6: 校验价格公式中引用自身属性的公式的引用情况，确保不存在循环引用，并记录公式执行顺序 */
            this.validateAttrFormulaRelations(po.getFieldList(), po.getFormulaList());
            /* 7: 校验价格公式中属性引用的关联情况情况，确保不存在循环引用 */
            po.getAttr().setAttrRelate(this.validateAttrRelations(po.getAttr(), po.getFormulaList()));

            return po;
        } finally {
            /* 9: 清空上下文 */
            SouQuoteTempAttrEditDtoContext.remove();
        }
    }

    public SouQuoteTempAttrTable createDynamicTable(SouQuoteTempAttrEditPO po) {
        String tableName = "scc_sou_qt_" + po.getAttr().getAttrId();

        /* 先删除现有的动态表，避免待会新增失败 */
        try {
            long rowCount = baseClient.getMetadataDataCount(Module.SOU, tableName);
            if (rowCount > 0) {
                MetadataDataDTO param = new MetadataDataDTO();
                param.setTableName(tableName);
            }
            MetadataDTO param = new MetadataDTO();
            param.setTableName(tableName);
            param.setModule(Module.SOU.name());
            baseClient.deleteMetadata(param);
        } catch (Exception e) {
            throw new IllegalArgumentException("删除动态表失败", e);
        }

        SouQuoteTempAttrTable table = new SouQuoteTempAttrTable();
        table.setTableId(IdGenrator.generate());
        table.setAttrId(po.getAttr().getAttrId());
        table.setTableColumns(new LinkedHashMap<>(po.getFieldList().size() + 4));

        MetadataDTO meta = new MetadataDTO();
        /* 表名称 */
        meta.setTableName(tableName);
        table.setTableName(tableName);
        /* 所属模块 */
        meta.setModule(Module.SOU.name());
        /* 表类型 */
        meta.setMetadataType("DYNAMIC");
        /* 默认列开关 */
        meta.setNonDefaultField(true);
        /* 列定义 */
        meta.setDetails(new ArrayList<>(po.getFieldList().size() + 4)); {
            /* 1: ID */
            MetadataDetailDTO metaColumn = new MetadataDetailDTO(); {
                meta.getDetails().add(metaColumn);
                metaColumn.setFieldName(SouQuoteTempAttrTableColumnVO.TABLE_ID);
                metaColumn.setDataType("BIGINT");
                metaColumn.setDataLength(20);
                metaColumn.setPrimaryKeyFlag("Y");
                metaColumn.setFieldDesc("主键");
            }
            /* 2: 业务单据ID(特地用字符串类型) */
            if (!Enable.Y.equals(po.getAttr().getIsGlobal())) {
                metaColumn = new MetadataDetailDTO();
                meta.getDetails().add(metaColumn);
                metaColumn.setFieldName(SouQuoteTempAttrTableColumnVO.BUSINESS_ID);
                metaColumn.setDataType("VARCHAR");
                metaColumn.setDataLength(50);
                metaColumn.setFieldDesc("业务单据ID");
            }
            /* 3: 其他字段 */
            int index = 0;
            for (SouQuoteTempField field : po.getFieldList()) {
                index++;
                metaColumn = new MetadataDetailDTO();
                table.getTableColumns().put(field.getFieldName(), metaColumn);
                meta.getDetails().add(metaColumn);
                metaColumn.setFieldName("column" + index);
                switch (field.getFieldType()) {
//                    文本
                    case TEXT:
//                    文本枚举
                    case ENUM_TEXT:
                        metaColumn.setDataType("VARCHAR");
                        metaColumn.setDataLength(200);
                        break;
//                    字典
                    case DICT:
                        metaColumn.setDataType("VARCHAR");
                        metaColumn.setDataLength(50);
                        break;
//                    日期
                    case DATE:
                        metaColumn.setDataType("datetime");
                        break;
                    case DECIMAL:
                    case ENUM_DECIMAL:
//                        公式
                    case FORMULA:
                        metaColumn.setDataType("DECIMAL");
                        metaColumn.setDataLength(24);
                        metaColumn.setDataPrecision(8);
                        break;
                    default:
                        throw new IllegalArgumentException("不支持的字段类型" + field.getFieldType());
                }
            }
        }
        /* 新增表 */
        try {
            baseClient.addMetadata(meta);
        } catch (Exception e) {
            log.error("新增动态表失败", e);
            throw new BaseException(e.getMessage());
        }

        return table;
    }

    /**
     * 校验价格公式中引用自身属性的字段公式的引用情况，确保不存在循环引用，并记录公式执行顺序
     * @param fieldList
     * @param formulaList
     */
    private void validateAttrFormulaRelations(List<SouQuoteTempField> fieldList, List<SouQuoteTempFormula> formulaList) {
        /* 1: 拿到字段直接的关联关系 */
        List<SouQuoteTempFieldRelateVO> relateList = new ArrayList<>(32);
        List<SouQuoteTempFieldRelateTreeVO> noRelateTrees = new ArrayList<>();
        {
            /* 公式字段 */
            Set<String> formulaFields = fieldList.stream()
                    .filter(field -> SouQuoteTempFieldTypeEnum.FORMULA.equals(field.getFieldType()))
                    .map(SouQuoteTempField::getFieldName)
                    .collect(Collectors.toSet());
            for (SouQuoteTempFormula formula : formulaList) {
                Set<String> relateFields; {
                    relateFields = new HashSet<>(32);
                    relateFields.addAll(formula.getFormulaVars().values().stream().filter(e -> QuoteFunctionType.VAR.equals(e.getType()))
                            .map(QuoteFunction::getName).collect(Collectors.toSet()));
                }
                if (CollectionUtils.isEmpty(relateFields)) {
                    noRelateTrees.add(new SouQuoteTempFieldRelateTreeVO(formula.getFieldName()));
                } else {
                    boolean hasRelate = false;
                    for (String field : relateFields) {
                        if (formulaFields.contains(field)) {
                            relateList.add(new SouQuoteTempFieldRelateVO(formula.getFieldName(), field));
                            hasRelate = true;
                        }
                    }
                    if (!hasRelate) {
                        noRelateTrees.add(new SouQuoteTempFieldRelateTreeVO(formula.getFieldName()));
                    }
                }
            }
        }
        /* 2: 记录引用路径 */
        Map<String/* firstField */, List<SouQuoteTempFieldRelateTreeVO>> attrTreeMap = new HashMap<>(50);
        if (!relateList.isEmpty()) {
            relateList.forEach(relate -> {
                SouQuoteTempFieldRelateTreeVO tree = new SouQuoteTempFieldRelateTreeVO();
                tree.addNode(relate.getFieldName());
                tree.addNode(relate.getTargetFieldName());
                attrTreeMap.computeIfAbsent(relate.getFieldName(), k -> new ArrayList<>())
                        .add(tree);
            });
            attrTreeMap.values().forEach(treeList -> {
                Set<String> lastNodes = treeList.stream().map(SouQuoteTempFieldRelateTreeVO::getLastNode).collect(Collectors.toSet());
                lastNodes.forEach(lastNode -> {
                    List<SouQuoteTempFieldRelateTreeVO> trees = attrTreeMap.get(lastNode);
                    if (trees != null && trees.size() > 1) {
                        List<SouQuoteTempFieldRelateTreeVO> ts2 = new ArrayList<>();
                        treeList.forEach(tree -> {
                            for (int i = 0; i < trees.size() - 1; i++) {
                                ts2.add(tree.copyTree());
                            }
                        });
                        treeList.addAll(ts2);
                    }
                });
                /* 开始嫁接树 */
                List<SouQuoteTempFieldRelateTreeVO> tempTrees = new ArrayList<>(32);
                Iterator<SouQuoteTempFieldRelateTreeVO> iterator = treeList.iterator();
                while (iterator.hasNext()) {
                    SouQuoteTempFieldRelateTreeVO tree = iterator.next();
                    List<SouQuoteTempFieldRelateTreeVO> otherTrees = attrTreeMap.get(tree.getLastNode());
                    if (otherTrees != null) {
                        otherTrees.forEach(otherTree -> {
                            SouQuoteTempFieldRelateTreeVO newTree = tree.copyTree();
                            String duplicateNode = newTree.addTree(otherTree);
                            AssertUtils.isNull(duplicateNode, "!!!出现节点循环!!!");
                            tempTrees.add(newTree);
                        });
                        /* 删除现有的 */
                        iterator.remove();
                    }
                }
                treeList.addAll(tempTrees);
            });
        }
        /* 3: 根据树结构，获取推荐的执行顺序 */
        List<SouQuoteTempFieldRelateTreeVO> treeList = new ArrayList<>(attrTreeMap.size() << 2); {
            attrTreeMap.values().forEach(treeList::addAll);
        }
        treeList.addAll(noRelateTrees);
        Map<String/* fieldName */, Integer/* index */> executeIndexMap = SouQuoteTempFieldRelateTreeVO.getExecuteIndex(treeList);
        /* 4: 记录推荐顺序 */
        formulaList.forEach(formula -> {
            formula.setExecuteIndex(executeIndexMap.get(formula.getFieldName()));
            AssertUtils.notNull(formula.getExecuteIndex(), LocaleHandler.getLocaleMsg("公式[{0}]没有分析到执行顺序"), formula.getFieldName());
        });
    }

    protected void formatAndValidate(SouQuoteTempAttrEditDTO param, boolean isTempSave) {
        /* 1: 校验报价属性基本信息 */
        this.doFormatAndValidateAttr(param.getAttr());
        /* 2: 校验字段定义 */
        this.doFormatAndValidateFields(param.getAttr(), param.getFieldList(), isTempSave);
        /* 3: 校验公式定义 */
        this.doFormatAndValidateFormulas(param, isTempSave);
    }

    private SouQuoteTempAttrEditDtoContext buildContext(SouQuoteTempAttrEditDTO param) {
        /* 1: 查询报价属性 */
        Map<Long/* attrId */, SouQuoteTempAttr> attrMap = souQuoteTempAttrRepository.lambdaQuery()
                .eq(SouQuoteTempAttr::getAttrStatus, SouQuoteTempAttrStatusEnum.VALID)
                .list()
                .stream().collect(Collectors.toMap(SouQuoteTempAttr::getAttrId, java.util.function.Function.identity()));
        /* 2: 查询可用报价属性对应的动态表定义 */
        Set<Long> availableAttrIds = attrMap.values().stream()
                .filter(attr -> SouQuoteTempAttrStatusEnum.VALID.equals(attr.getAttrStatus()))
                .map(SouQuoteTempAttr::getAttrId)
                .collect(Collectors.toSet());
        Map<Long/* attrId */, SouQuoteTempAttrTable> attrTableMap = Collections.emptyMap();
        if (!availableAttrIds.isEmpty()) {
            attrTableMap = souQuoteTempAttrTableRepository.lambdaQuery()
                    .in(SouQuoteTempAttrTable::getAttrId, availableAttrIds)
                    .list()
                    .stream().collect(Collectors.toMap(SouQuoteTempAttrTable::getAttrId, java.util.function.Function.identity()));
        }
        /* 3: 查询api定义信息 */
        Map<Long/* apiId */, SouQuoteTempApi> apiMap = souQuoteTempApiRepository.lambdaQuery()
                .list()
                .stream().collect(Collectors.toMap(SouQuoteTempApi::getApiId, java.util.function.Function.identity()));
        /* 4: 查询字典信息 */
        Map<String/* dictCode */, Set<String/* dictItemCode */>> dictMap; {
            Set<String> dictCodes = new HashSet<>(); {
                if (CollectionUtils.isNotEmpty(param.getFieldList())) {
                    param.getFieldList().forEach(field -> {
                        if (field.getFieldType() != null && SouQuoteTempFieldTypeEnum.DICT.equals(field.getFieldType())) {
                            String dictCode = StringUtils.trimToNull(field.getFieldValue());
                            if (dictCode != null) {
                                dictCodes.add(dictCode);
                            }
                        }
                    });
                }
            }
            if (dictCodes.isEmpty()) {
                dictMap = Collections.emptyMap();
            } else {
                dictMap = baseClient.listByDictCode(new ArrayList<>(dictCodes)).stream()
                        .collect(Collectors.groupingBy(DictItemDTO::getDictCode, Collectors.mapping(DictItemDTO::getDictItemCode, Collectors.toSet())));
            }
        }

        return SouQuoteTempAttrEditDtoContext.builder()
                .attrs(attrMap)
                .availableAttrTables(attrTableMap)
                .apiMap(apiMap)
                .dictMap(dictMap)
                .build();
    }

    private void doFormatAndValidateAttr(@Nullable SouQuoteTempAttr attr) {
        AssertUtils.notNull(attr, "缺少attr信息");
        attr.setAttrName(StringUtils.trimToNull(attr.getAttrName()));
        AssertUtils.notNull(attr.getAttrName(), "请输入属性名称");
        AssertUtils.isTrue(attr.getAttrName().length() <= 100, "属性名称的长度不能超过100");
        AssertUtils.isTrue(RegexUtil.REGEX_NORMAL_NAME.matcher(attr.getAttrName()).matches(), "属性名称只能包含中英文数字下划线(中英文开头)");
        if (attr.getIsGlobal() == null) {
            attr.setIsGlobal(Enable.N);
        }
        /* 确保名称唯一 */
        long existCount = souQuoteTempAttrRepository.lambdaQuery()
                .eq(SouQuoteTempAttr::getAttrName, attr.getAttrName())
                .ne(attr.getAttrId() != null, SouQuoteTempAttr::getAttrId, attr.getAttrId())
                .count();
        AssertUtils.isTrue(existCount <= 0, LocaleHandler.getLocaleMsg("报价属性[{0}]已存在，不能重复添加"), attr.getAttrName());
    }

    private void doFormatAndValidateFields(SouQuoteTempAttr attr, List<SouQuoteTempField> fieldList, boolean isTempSave) {
        if (CollectionUtils.isEmpty(fieldList)) {
            AssertUtils.isTrue(isTempSave, "报价属性缺少字段信息");
            return;
        }

        int index = 0;
        boolean containsTotalPrice = false;
        for (SouQuoteTempField field : fieldList) {
            index++;
            /* 格式化及校验字段 */
            this.doFormatAndValidateField(attr, field, isTempSave, index);
            if (Enable.Y.equals(field.getIsTotal())) {
                AssertUtils.isFalse(containsTotalPrice, "只能有一个公式被标记为总价");
                containsTotalPrice = true;
            }
        }
        if (!containsTotalPrice) {
            AssertUtils.isTrue(isTempSave, "请勾选一个公式字段作为总价");
        }
    }

    private void doFormatAndValidateField(SouQuoteTempAttr attr, SouQuoteTempField field, boolean isTempSave, int index) {
        /* 1: ID(略) */
        /* 2: 关联属性表ID(置空 - 后端处理) */
        field.setAttrId(null);
        /* 3: 字段名称 */
        field.setFieldName(StringUtils.trimToNull(field.getFieldName()));
        AssertUtils.notNull(field.getFieldName(), LocaleHandler.getLocaleMsg("字段列表第{0}行请输入字段名称"), index);
        AssertUtils.isTrue(field.getFieldName().length() <= 30, LocaleHandler.getLocaleMsg("字段列表第{0}行字段名称长度不能超过30"), index);
        AssertUtils.isFalse(attr.getAttrName().equals(field.getFieldName()), LocaleHandler.getLocaleMsg("字段列表第{0}行字段名称不能与自身报价属性的名称相同"), index);
        AssertUtils.isTrue(RegexUtil.REGEX_NORMAL_NAME.matcher(field.getFieldName()).matches(),
                "字段列表第{0}行字段名只能包含中英文数字下划线(中英文开头)", index);
        /* 4: 字段类型 */
        AssertUtils.notNull(field.getFieldType(), LocaleHandler.getLocaleMsg("字段列表第{0}行请选择字段类型"));
        /* 5: 字段值 */
        field.setFieldValue(StringUtils.trimToNull(field.getFieldValue()));
        if (field.getFieldValue() != null) {
            AssertUtils.isTrue(field.getFieldValue().length() <= 300, LocaleHandler.getLocaleMsg("字段列表第{0}行字段值的长度不能超过300"), index);
        }
        switch (field.getFieldType()) {
//            文本枚举
            case ENUM_TEXT:
//                数字枚举
            case ENUM_DECIMAL:
//                字典
            case DICT:
                AssertUtils.notNull(field.getFieldValue(), LocaleHandler.getLocaleMsg("字段列表第{0}行请输入枚举的字段值"), index);
                break;
            default:
                field.setFieldValue(null);
                break;
        }
        Set<String> enums = null;
        switch (field.getFieldType()) {
//            文本
            case TEXT:
                AssertUtils.isTrue(field.getFieldValue() == null || field.getFieldValue().length() <= 300,
                        "字段列表第{0}行字段值长度不能超过300", index);
                break;
//            数值
            case DECIMAL:
                if (field.getFieldValue() != null) {
                    try {
                        BigDecimal num = new BigDecimal(field.getFieldValue());
                        AssertUtils.isTrue(num.compareTo(B_10_10) < 0, "输入的数字整数部分不能超过12位");
                        field.setFieldValue(num.setScale(4, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
                    } catch (NumberFormatException e) {
                        AssertUtils.isTrue(isTempSave, LocaleHandler.getLocaleMsg("字段列表第{0}行字段值必须是数字"), index);
                    }
                }
                break;
//            文本枚举
            case ENUM_TEXT:
                enums = formatAndValidateEnums(field.getFieldValue(), false,
                        MessageFormat.format("字段列表第{0}行", index), isTempSave);
                field.setFieldValue(this.collectionToString(enums));
                break;
//            数字枚举
            case ENUM_DECIMAL:
                enums = formatAndValidateEnums(field.getFieldValue(), true,
                        MessageFormat.format("字段列表第{0}行", index), isTempSave);
                field.setFieldValue(this.collectionToString(enums));
                break;
//            字典
            case DICT:
                AssertUtils.notNull(field.getFieldValue(), LocaleHandler.getLocaleMsg("字段列表第{0}行请输入字段值(字典编码)"));
                AssertUtils.isTrue(SouQuoteTempAttrEditDtoContext.getContext().getDictMap().containsKey(field.getFieldValue()),
                        LocaleHandler.getLocaleMsg("字典编码[{0}]不存在，请先维护"), field.getFieldValue());
                break;
//            日期
            case DATE:
                if (field.getFieldValue() != null) {
                    try {
                        DATE_TIME_FORMATTER.parse(field.getFieldValue());
                    } catch (DateTimeParseException e) {
                        AssertUtils.isTrue(isTempSave, LocaleHandler.getLocaleMsg("字段列表第{0}行日期类型的字段值格式错误"), index);
                    }
                }
                break;
//            公式
            case FORMULA:
//                公式类型不支持
                field.setFieldValue(null);
                break;
            default:
                throw new IllegalArgumentException(MessageFormat.format("无法识别的字段类型:{0}", field.getFieldType()));
        }
        /* 6: 默认值 */
        field.setDefaultValue(StringUtils.trimToNull(field.getDefaultValue()));
        if (field.getDefaultValue() != null) {
            AssertUtils.isTrue(field.getDefaultValue().length() <= 100, LocaleHandler.getLocaleMsg("字段列表第{0}行默认值的长度不能超过100"), index);
        }
        if (field.getDefaultValue() != null) {
            switch (field.getFieldType()) {
//                文本
                case TEXT:
                    AssertUtils.isTrue(field.getDefaultValue() == null || field.getDefaultValue().length() <= 100,
                            "字段列表第{0}行默认值长度不能超过100", index);
                    break;
//                数值
                case DECIMAL:
                    if (field.getDefaultValue() != null) {
                        try {
                            BigDecimal num = new BigDecimal(field.getDefaultValue());
                            AssertUtils.isTrue(num.compareTo(B_10_10) < 0, "输入的数字整数部分不能超过12位");
                            field.setFieldValue(num.setScale(4, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
                        } catch (NumberFormatException e) {
                            AssertUtils.isTrue(isTempSave, LocaleHandler.getLocaleMsg("字段列表第{0}行默认必须是数字"), index);
                        }
                    }
                    break;
//                文本枚举
                case ENUM_TEXT:
                    if (enums == null) {
                        field.setDefaultValue(null);
                    } else {
                        AssertUtils.isTrue(enums.contains(field.getDefaultValue()), LocaleHandler.getLocaleMsg("字段列表第")+"{0}"+LocaleHandler.getLocaleMsg("行默认值不在枚举的范围内")+":{1}",
                                index, field.getDefaultValue());
                    }
                    break;
//                数字枚举
                case ENUM_DECIMAL:
                    if (enums == null) {
                        field.setDefaultValue(null);
                    } else {
                        AssertUtils.isTrue(enums.contains(field.getDefaultValue()), LocaleHandler.getLocaleMsg("字段列表第")+"{0}"+LocaleHandler.getLocaleMsg("行默认值不在枚举的范围内")+":{1}",
                                index, field.getDefaultValue());
                        BigDecimal num = new BigDecimal(field.getDefaultValue());
                        AssertUtils.isTrue(num.compareTo(B_10_10) < 0, "输入的数字整数部分不能超过12位");
                        field.setDefaultValue(num.setScale(4, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
                    }
                    break;
//                日期
                case DATE:
                    if (field.getDefaultValue() != null) {
                        try {
                            DATE_TIME_FORMATTER.parse(field.getDefaultValue());
                        } catch (DateTimeParseException e) {
                            AssertUtils.isTrue(isTempSave, LocaleHandler.getLocaleMsg("字段列表第")+"{0}"+LocaleHandler.getLocaleMsg("行日期类型的默认值格式错误")+":{1}", index, field.getDefaultValue());
                        }
                    }
                    break;
//                字典
                case DICT:
                    Set<String> dictItemMap = SouQuoteTempAttrEditDtoContext.getContext().getDictMap().get(field.getFieldValue());
                    if (dictItemMap != null) {
                        AssertUtils.isTrue(dictItemMap.contains(field.getDefaultValue()), "字典[{0}]的默认值[{1}]不存在", field.getFieldValue(), field.getDefaultValue());
                    }
                    break;
//                公式
                case FORMULA:
//                    公式类型不支持
                    field.setDefaultValue(null);
                    break;
                default:
                    throw new IllegalArgumentException(MessageFormat.format("无法识别的字段类型:{0}", field.getFieldType()));
            }
        }
        /* 6: 是否禁止编辑 */
        switch (field.getFieldType()) {
//            文本
            case TEXT:
//                数字
            case DECIMAL:
//                日期
            case DATE:
//                文本枚举
            case ENUM_TEXT:
//                数字枚举
            case ENUM_DECIMAL:
//                字典
            case DICT:
                if (field.getForbidEdit() == null) {
                    field.setForbidEdit(Enable.N);
                } else if (Enable.Y.equals(field.getForbidEdit())) {
                    AssertUtils.isTrue(field.getDefaultValue() != null || isTempSave,
                            "字段列表第{0}行设定为禁止用户编辑，请输入字段值(用于默认展示)", index);
                }
                break;
//            公式
            case FORMULA:
            default:
                field.setForbidEdit(Enable.Y);
                break;
        }
        /* 7: 是否必填 */
        if (field.getRequired() == null) {
            field.setRequired(Enable.N);
        }
        /* 8: 是否总价 */
        if (field.getIsTotal() == null) {
            field.setIsTotal(Enable.N);
        } else if (Enable.Y.equals(field.getIsTotal())) {
            AssertUtils.isTrue(SouQuoteTempFieldTypeEnum.FORMULA.equals(field.getFieldType()),
                    "字段列表第{0}行非公式类型字段不能标记为总价", index);
        }
        /* 9: 是否显示 */
        if (field.getDisplayed() == null) {
            field.setDisplayed(Enable.N);
        }
        /* 10: 排序 */
        field.setSortIndex(index);
    }

    private void doFormatAndValidateFormulas(SouQuoteTempAttrEditDTO param, boolean isTempSave) {
        if (CollectionUtils.isEmpty(param.getFormulaList())) {
            AssertUtils.isTrue(isTempSave, "报价属性缺少公式信息");
            return;
        }
        if (CollectionUtils.isEmpty(param.getFieldList())) {
            param.getFormulaList().clear();
            return;
        }

        /* 收集不同类型的字段 */
        /* 文本/文本枚举/字典类型字段 */
        Set<String> textFields = new HashSet<>(32);
        /* 数字/数字枚举类型字段 */
        Set<String> decimalFields = new HashSet<>(32);
        /* 日期类型字段 */
        Set<String> dateFields = new HashSet<>(16);
        /* 公式类型字段 */
        Set<String> formulaFields = new HashSet<>(32);
        /* 所有的字段 */
        Set<String> allFields = new HashSet<>(64);
        /* 标记为非必填的字段 */
        Set<String> nullableFields = new HashSet<>();
        for (SouQuoteTempField field : param.getFieldList()) {
            allFields.add(field.getFieldName());
            boolean forbidEdit = Enable.Y.equals(field.getForbidEdit());

            switch (field.getFieldType()) {
//                文本
                case TEXT:
//                    文本枚举
                case ENUM_TEXT:
//                    字典
                case DICT:
                    textFields.add(field.getFieldName());
                    if (!Enable.Y.equals(field.getRequired()) && !forbidEdit) {
                        nullableFields.add(field.getFieldName());
                    }
                    break;
//                数字
                case DECIMAL:
//                    数字枚举
                case ENUM_DECIMAL:
                    decimalFields.add(field.getFieldName());
                    if (!Enable.Y.equals(field.getRequired()) && !forbidEdit) {
                        nullableFields.add(field.getFieldName());
                    }
                    break;
//                日期
                case DATE:
                    dateFields.add(field.getFieldName());
                    if (!Enable.Y.equals(field.getRequired()) && !forbidEdit) {
                        nullableFields.add(field.getFieldName());
                    }
                    break;
//                公式
                case FORMULA:
                    formulaFields.add(field.getFieldName());
                    break;
                default:
                    throw new IllegalArgumentException(MessageFormat.format("不支持的字段类型:{0}", field.getFieldType()));
            }
        }

        /* 搜集其他的报价属性 */
        Map<String/* attrName */, SouQuoteTempAttr> otherAttrMap = souQuoteTempAttrRepository.lambdaQuery()
                .ne(SouQuoteTempAttr::getAttrName, param.getAttr().getAttrName())
                .list()
                .stream().collect(Collectors.toMap(SouQuoteTempAttr::getAttrName, java.util.function.Function.identity()));

        /* 校验公式 */
        int index = 0;
        for (SouQuoteTempFormula formula : param.getFormulaList()) {
            index++;
            /* 1: ID(略) */
            /* 2: 关联属性表ID(置空 - 后端处理) */
            formula.setAttrId(null);
            /* 3: 字段名称 */
            formula.setFieldName(StringUtils.trimToNull(formula.getFieldName()));
            AssertUtils.notNull(formula.getFieldName(), LocaleHandler.getLocaleMsg("公式列表第")+"{0}"+LocaleHandler.getLocaleMsg("行请选择字段名称"), index);
            AssertUtils.isTrue(allFields.contains(formula.getFieldName()), LocaleHandler.getLocaleMsg("公式列表第")+"{0}"+LocaleHandler.getLocaleMsg("行字段")+"[{1}]"+LocaleHandler.getLocaleMsg("未定义"), index, formula.getFieldName());
            AssertUtils.isTrue(formulaFields.contains(formula.getFieldName()), LocaleHandler.getLocaleMsg("公式列表第")+"{0}"+LocaleHandler.getLocaleMsg("行字段使用错误")+": "+LocaleHandler.getLocaleMsg("只能选择公式类型的字段"), index);
            /* 4: 应用条件 */
            Set<String> conditionFields;
            Set<String> nilMap = new HashSet<>(8);
            Set<String> noNilMap = new HashSet<>(8);
            formula.setConditionName(StringUtils.trimToNull(formula.getConditionName()));
            if (formula.getConditionName() != null) {
                AssertUtils.isTrue(formula.getConditionName().length() <= 200, LocaleHandler.getLocaleMsg("公式列表第")+"{0}"+LocaleHandler.getLocaleMsg("行应用条件长度不能超过200"), index);
            }
            formula.setConditionNameShow(StringUtils.trimToNull(formula.getConditionNameShow()));
            formula.setConditionNameJson(StringUtils.trimToNull(formula.getConditionNameJson()));
            /* 属性公式/总价公式类型 */
            if (formula.getConditionName() != null) {
                /* 去除公式中多余空格 */
                FormulaUtils.deleteWhitespace(formula.getConditionName());
                /* 获取实际的应用条件公式 */
                String actualFormula = FormulaUtils.getFormulaFromConditionName(formula.getConditionName());
                formula.setConditionValue(actualFormula);
                /* 简单校验公式格式 */
                FormulaUtils.simpleValidateFormula(actualFormula, MessageFormat.format("公式列表第{0}行应用条件格式错误:", index));
                /* 获取公式中的字段 */
                conditionFields = FormulaUtils.getFormulaAttr(actualFormula, false, null);
                /* 确保公式中的字段是存在的(不能使用公式、不能自己引用自己) */
                for (String field : conditionFields) {
                    AssertUtils.isTrue(allFields.contains(field), LocaleHandler.getLocaleMsg("公式列表第")+"{0}"+LocaleHandler.getLocaleMsg("行应用条件定义错误")+": "+LocaleHandler.getLocaleMsg("字段")+"[{1}]"+LocaleHandler.getLocaleMsg("不存在"), index, field);
                    AssertUtils.isFalse(formulaFields.contains(field), LocaleHandler.getLocaleMsg("公式列表第")+"{0}"+LocaleHandler.getLocaleMsg("行应用条件定义错误")+": "+LocaleHandler.getLocaleMsg("字段")+"[{1}]"+LocaleHandler.getLocaleMsg("为公式类型，不能使用"), index, field);
                    AssertUtils.isFalse(field.equals(formula.getFieldName()), LocaleHandler.getLocaleMsg("公式列表第")+"{0}"+LocaleHandler.getLocaleMsg("行应用条件定义错误")+": "+LocaleHandler.getLocaleMsg("公式中不能引用自己"), index);
                }
                /* 分析公式是否正常 */
                Map<String/* argName */, Date> dateArgMap = new HashMap<>(8);
                String conditionValue = FormulaUtils.analysisConditionFormulaPlus(actualFormula, null, conditionFields,
                        textFields, decimalFields, dateFields, dateArgMap, nilMap, noNilMap,
                        FormatValidateUtil.DATE_TIME_FORMATTER, MessageFormat.format("公式列表第{0}行应用条件", index));
                formula.setConditionValue(conditionValue);

                formula.setConditionVars(new SouQuoteTempFormulaConditionVarsVO(dateArgMap, nilMap));
            } else {
                /* 5: 应用条件实际值 */
                formula.setConditionValue("1 == 1");
                /* 6: 应用条件中的变量集合 */
                formula.setConditionVars(null);
            }
            /* 5: 价格公式值 */
            formula.setFormulaValue(StringUtils.trimToNull(formula.getFormulaValue()));
            AssertUtils.isTrue(formula.getFormulaValue() != null || isTempSave, LocaleHandler.getLocaleMsg("公式列表第")+"{0}"+LocaleHandler.getLocaleMsg("行请维护价格公式"), index);
            if (formula.getFormulaValue() != null) {
                AssertUtils.isTrue(formula.getFormulaValue().length() <= 3000, LocaleHandler.getLocaleMsg("公式列表第")+"{0}"+LocaleHandler.getLocaleMsg("行价格公式长度不能超过3000"), index);
            }
            QuoteFormula formulaInfo = null;
            if (formula.getFormulaValue() != null) {
                /* antlr语法解析 */
                formulaInfo = QuoteAttrFormulaParseUtils.parseFormula(formula.getFormulaValue());
                /* 分析处理每个函数 */
                for (QuoteFunction func : formulaInfo.getFunctions().values()) {
                    if (QuoteFunctionType.VAR.equals(func.getType())) {
                        /* 报价属性自身的字段 */
                        AssertUtils.isFalse(func.getName().equals(formula.getFieldName()), "公式列表第{0}行价格公式定义错误:不能在公式中引用自己", index);
                    } else if (QuoteFunctionType.REF.equals(func.getType())) {
                        /* 引用其他报价属性 */
                        func.setTemplate(souQuoteTempAttrDataService.generateExecutableSqlForSingleResult(func, allFields));
                    } else if (QuoteFunctionType.FUN.equals(func.getType())) {
                        /* 引用api */
                        func.setTemplate(souQuoteTempAttrDataService.generateJsonForApiQuery(func, allFields));
                    } else {
                        throw new IllegalArgumentException("暂不支持的自定义函数类型:" + func.getType());
                    }
                }
            }
            /* 8: 可执行公式 */
            formula.setExecutableFormula(formulaInfo != null ? formulaInfo.getExecutableFormula() : null);
            /* 9: 价格公式中的变量集合(置空 - 后端处理) */
            formula.setFormulaVars(formulaInfo != null ? formulaInfo.getFunctions() : Collections.emptyMap());
            /* 10: 排序 */
            formula.setSortIndex(index);
        }

        /* 确保公式类型的字段都进行了公式定义 */
        if (!isTempSave) {
            Set<String> fFieldNames = param.getFormulaList().stream().map(SouQuoteTempFormula::getFieldName).collect(Collectors.toSet());
            formulaFields.forEach(field -> AssertUtils.isTrue(fFieldNames.contains(field),
                    "公式列表未定义[{0}]字段(字段公式)信息", field));
        }
    }

    protected SouQuoteTempAttrEditPO convert(SouQuoteTempAttrEditDTO param, boolean isTempSave) {
        SouQuoteTempAttrEditPO po = new SouQuoteTempAttrEditPO();
        /* 1: 转换得到报价属性信息 */
        po.setAttr(this.doConvertAttr(param, isTempSave));
        /* 2: 转换得到字段集合 */
        po.setFieldList(this.doConvertFields(po.getAttr().getAttrId(), param));
        /* 3: 转换得到公式集合 */
        po.setFormulaList(this.doConvertFormulas(po.getAttr().getAttrId(), param));

        return po;
    }

    public static Set<String> formatAndValidateEnums(@Nullable String value, boolean isDecimal, String errMsg, boolean isTempSave) {
        if (value == null) {
            AssertUtils.isTrue(isTempSave, "{0}"+LocaleHandler.getLocaleMsg("请输入枚举值"), errMsg);
            return Collections.emptySet();
        }
        AssertUtils.isFalse(value.contains("["), "{0}"+LocaleHandler.getLocaleMsg("不能包含")+"'['", errMsg);
        AssertUtils.isFalse(value.contains("]"), "{0}"+LocaleHandler.getLocaleMsg("不能包含")+"'['", errMsg);
        String[] enums = value.split(",");
        Set<String> kSet = new LinkedHashSet<>(enums.length);
        for (String k : enums) {
            k = StringUtils.trimToNull(k);
            if (k == null) { continue; }
            if (isDecimal) {
                try {
                    BigDecimal num = new BigDecimal(k);
                    AssertUtils.isTrue(num.compareTo(B_10_10) < 0, "输入的数字整数部分不能超过12位");
                    kSet.add(num.setScale(4, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
                } catch (NumberFormatException e) {
                    AssertUtils.isTrue(isTempSave, "{0}"+LocaleHandler.getLocaleMsg("存在非数字类型的枚举值")+":{1}", errMsg, k);
                }
            } else {
                kSet.add(k);
            }
        }
        AssertUtils.isTrue(!kSet.isEmpty() || isTempSave, "{0}"+LocaleHandler.getLocaleMsg("请维护字段值"), errMsg);
        return kSet;
    }

    private List<String> splitFormulaAttr(String field) {
        List<String> fields = new ArrayList<>(4); {
            String[] fieldArr = field.split(":");
            for (String s : fieldArr) {
                s = StringUtils.trimToNull(s);
                if (s != null) {
                    fields.add(s);
                }
            }
        }
        return fields;
    }

    private String collectionToString(@Nullable Collection<?> collection) {
        if (collection == null) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder(100);
            for (Object o : collection) {
                sb.append(o.toString());
                sb.append(",");
            }
            return sb.substring(0, sb.length() - 1);
        }
    }

    private SouQuoteTempAttr doConvertAttr(SouQuoteTempAttrEditDTO param, boolean isTempSave) {
        SouQuoteTempAttr entity;
        if (param.getAttr().getAttrId() == null) {
            entity = new SouQuoteTempAttr();
            BeanUtils.copyProperties(param.getAttr(), entity);
            /* ID */
            entity.setAttrId(IdGenrator.generate());
            /* 属性编码 */
            entity.setAttrNo(baseClient.seqGen(SequenceCodeConstant.SOU.SOU_QUOTE_TEMP_ATTR_NO));
        } else {
            entity = souQuoteTempAttrRepository.getById(param.getAttr().getAttrId());
            entity.setAttrName(param.getAttr().getAttrName());
        }
        /* 状态 */
        entity.setAttrStatus(isTempSave ? SouQuoteTempAttrStatusEnum.DRAFT : SouQuoteTempAttrStatusEnum.VALID);
        return entity;
    }

    private List<SouQuoteTempField> doConvertFields(long attrId, SouQuoteTempAttrEditDTO param) {
        if (CollectionUtils.isEmpty(param.getFieldList())) {
            return Collections.emptyList();
        }
        for (SouQuoteTempField field : param.getFieldList()) {
            /* ID */
            field.setFieldId(IdGenrator.generate());
            /* 关联属性表ID */
            field.setAttrId(attrId);
        }
        return param.getFieldList();
    }

    private List<SouQuoteTempFormula> doConvertFormulas(long attrId, SouQuoteTempAttrEditDTO param) {
        if (CollectionUtils.isEmpty(param.getFieldList()) || CollectionUtils.isEmpty(param.getFormulaList())) {
            return Collections.emptyList();
        }

        Map<String/* fieldName */, SouQuoteTempField> fieldMap = param.getFieldList()
                .stream()
                .collect(Collectors.toMap(SouQuoteTempField::getFieldName, java.util.function.Function.identity()));

        for (SouQuoteTempFormula formula : param.getFormulaList()) {
            /* ID */
            formula.setFormulaId(IdGenrator.generate());
            /* 关联属性表ID */
            formula.setAttrId(attrId);
            /* 字段ID */
            SouQuoteTempField field = fieldMap.get(formula.getFieldName());
            formula.setFieldId(field.getFieldId());
            /* 字段类型 */
            formula.setFieldType(field.getFieldType());
        }
        return param.getFormulaList();
    }

    /**
     * 校验价格公式中引用其他属性的关联引用情况，确保不存在循环引用
     * @param attr
     * @param formulaList
     * @return
     */
    private SouQuoteTempAttrRelateVO validateAttrRelations(SouQuoteTempAttr attr, List<SouQuoteTempFormula> formulaList) {
        if (formulaList.isEmpty()) { return new SouQuoteTempAttrRelateVO(); }
        Map<String/* attrName */, String/* tree */> attrTreeMap = new HashMap<>(50);
        /* 1: 确保第一层的target节点是生效且无循环依赖 */
        Set<String> level1AttrNames; {
            level1AttrNames = new HashSet<>(32);
            for (SouQuoteTempFormula formula : formulaList) {
                for (QuoteFunction func : formula.getFormulaVars().values()) {
                    if (QuoteFunctionType.REF.equals(func.getType())) {
                        level1AttrNames.add(func.getName());
                    }
                }
            }
        }
        if (level1AttrNames.isEmpty()) { return new SouQuoteTempAttrRelateVO(); }
        List<SouQuoteTempAttr> level1Attrs = souQuoteTempAttrRepository.lambdaQuery()
                .in(SouQuoteTempAttr::getAttrName, level1AttrNames)
                .list();
        if (level1Attrs.isEmpty()) { return new SouQuoteTempAttrRelateVO(); }
        level1Attrs.forEach(level1Attr -> {
            String tree = MessageFormat.format("[{0}] -> [{1}]", attr.getAttrName(), level1Attr.getAttrName());
            attrTreeMap.put(level1Attr.getAttrName(), tree);
        });
        /* 2: 确保第二层的target节点是生效且无循环依赖 */
        Map<String/* level1AttrName_level2AttrName */, String/* level2AttrName */> level12Relates; {
            level12Relates = new HashMap<>(32);
            for (SouQuoteTempAttr level1Attr : level1Attrs) {
                level1Attr.getAttrRelate().getRelateTree().forEach(tree -> {
                    if (tree.size() >= 2) {
                        String level2Attrname = tree.get(1);
                        level12Relates.put(level1Attr.getAttrName() + "_" + level2Attrname, level2Attrname);
                    }
                });
            }
        }
        if (level12Relates.isEmpty()) { return new SouQuoteTempAttrRelateVO(attrTreeMap.values()); }
        Map<String/* attrName */, SouQuoteTempAttr> level2Attrs = souQuoteTempAttrRepository.lambdaQuery()
                .in(SouQuoteTempAttr::getAttrName, level12Relates.values())
                .list()
                .stream().collect(Collectors.toMap(SouQuoteTempAttr::getAttrName, java.util.function.Function.identity()));
        if (level2Attrs.isEmpty()) { return new SouQuoteTempAttrRelateVO(attrTreeMap.values()); }
        level12Relates.forEach((relation, level2Attrname) -> {
            String parentAttr = relation.split("_")[0];
            String parentTree = attrTreeMap.get(parentAttr);
            String tree = MessageFormat.format("{0} -> [{1}]", parentTree, level2Attrname);
            SouQuoteTempAttr level2Attr = level2Attrs.get(level2Attrname);
            AssertUtils.notNull(level2Attr, LocaleHandler.getLocaleMsg("报价属性依赖关系")+"{0}"+LocaleHandler.getLocaleMsg("存在错误")+": [{1}]"+LocaleHandler.getLocaleMsg("不是生效状态不存在"), tree, level2Attrname);
            AssertUtils.notNull(level2Attr, LocaleHandler.getLocaleMsg("报价属性依赖关系")+"{0}"+LocaleHandler.getLocaleMsg("存在错误")+": [{1}]"+LocaleHandler.getLocaleMsg("不是生效状态"), tree, level2Attrname);
            attrTreeMap.put(level2Attrname, tree);
        });
        /* 3: 确保第三层的target节点是生效且无循环依赖 */
        Map<String/* level2AttrName_level3AttrName */, String/* level3AttrName */> level23Relates; {
            level23Relates = new HashMap<>(32);
            for (SouQuoteTempAttr level2Attr : level2Attrs.values()) {
                level2Attr.getAttrRelate().getRelateTree().forEach(tree -> {
                    if (tree.size() >= 2) {
                        String level3AttrName = tree.get(1);
                        level23Relates.put(level2Attr.getAttrName() + "_" + level3AttrName, level3AttrName);
                    }
                });
            }
        }
        if (level23Relates.isEmpty()) { return new SouQuoteTempAttrRelateVO(attrTreeMap.values()); }
        Map<String/* attrName */, SouQuoteTempAttr> level3Attrs = souQuoteTempAttrRepository.lambdaQuery()
                .in(SouQuoteTempAttr::getAttrName, level23Relates.values())
                .list()
                .stream().collect(Collectors.toMap(SouQuoteTempAttr::getAttrName, java.util.function.Function.identity()));
        if (level3Attrs.isEmpty()) { return new SouQuoteTempAttrRelateVO(attrTreeMap.values()); }
        level23Relates.forEach((relation, level3AttrName) -> {
            String parentAttr = relation.split("_")[0];
            String parentTree = attrTreeMap.get(parentAttr);
            String tree = MessageFormat.format("{0} -> [{1}]", parentTree, level3AttrName);
            SouQuoteTempAttr level3Attr = level3Attrs.get(level3AttrName);
            AssertUtils.notNull(level3Attr, LocaleHandler.getLocaleMsg("报价属性依赖关系")+"{0}"+LocaleHandler.getLocaleMsg("存在错误")+": [{1}]"+LocaleHandler.getLocaleMsg("不是生效状态不存在"), tree, level3AttrName);
            AssertUtils.notNull(level3Attr, LocaleHandler.getLocaleMsg("报价属性依赖关系")+"{0}"+LocaleHandler.getLocaleMsg("存在错误")+": [{1}]"+LocaleHandler.getLocaleMsg("不是生效状态"), tree, level3AttrName);
            attrTreeMap.put(level3AttrName, tree);
        });
        /* 4: 确保第四层的target节点是生效且无循环依赖 */
        Map<String/* level3AttrName_level4AttrName */, String/* level4AttrName */> level34Relates; {
            level34Relates = new HashMap<>(32);
            for (SouQuoteTempAttr level3Attr : level3Attrs.values()) {
                level3Attr.getAttrRelate().getRelateTree().forEach(tree -> {
                    if (tree.size() >= 2) {
                        String level4AttrName = tree.get(1);
                        level34Relates.put(level3Attr.getAttrName() + "_" + level4AttrName, level4AttrName);
                    }
                });
            }
        }
        if (level34Relates.isEmpty()) { return new SouQuoteTempAttrRelateVO(attrTreeMap.values()); }
        Map<String/* attrName */, SouQuoteTempAttr> level4Attrs = souQuoteTempAttrRepository.lambdaQuery()
                .in(SouQuoteTempAttr::getAttrName, level34Relates.values())
                .list()
                .stream().collect(Collectors.toMap(SouQuoteTempAttr::getAttrName, java.util.function.Function.identity()));
        if (level4Attrs.isEmpty()) { return new SouQuoteTempAttrRelateVO(attrTreeMap.values()); }
        level34Relates.forEach((relation, level4AttrName) -> {
            String parentAttr = relation.split("_")[0];
            String parentTree = attrTreeMap.get(parentAttr);
            String tree = MessageFormat.format("{0} -> [{1}]", parentTree, level4AttrName);
            SouQuoteTempAttr level4Attr = level4Attrs.get(level4AttrName);
            AssertUtils.notNull(level4Attr, LocaleHandler.getLocaleMsg("报价属性依赖关系")+"{0}"+LocaleHandler.getLocaleMsg("存在错误")+": [{1}]"+LocaleHandler.getLocaleMsg("不是生效状态不存在"), tree, level4AttrName);
            AssertUtils.notNull(level4Attr, LocaleHandler.getLocaleMsg("报价属性依赖关系")+"{0}"+LocaleHandler.getLocaleMsg("存在错误")+": [{1}]"+LocaleHandler.getLocaleMsg("不是生效状态"), tree, level4AttrName);
            attrTreeMap.put(level4AttrName, tree);
        });
        /* 5: 确保第五层的target节点是生效且无循环依赖 */
        Map<String/* level4AttrName_level5AttrName */, String/* level5AttrName */> level45Relates; {
            level45Relates = new HashMap<>(32);
            for (SouQuoteTempAttr level4Attr : level4Attrs.values()) {
                level4Attr.getAttrRelate().getRelateTree().forEach(tree -> {
                    if (tree.size() >= 2) {
                        String level5AttrName = tree.get(1);
                        level45Relates.put(level4Attr.getAttrName() + "_" + level5AttrName, level5AttrName);
                    }
                });
            }
        }
        if (level45Relates.isEmpty()) { return new SouQuoteTempAttrRelateVO(attrTreeMap.values()); }
        Map<String/* attrName */, SouQuoteTempAttr> level5Attrs = souQuoteTempAttrRepository.lambdaQuery()
                .in(SouQuoteTempAttr::getAttrName, level45Relates.values())
                .list()
                .stream().collect(Collectors.toMap(SouQuoteTempAttr::getAttrName, java.util.function.Function.identity()));
        if (level5Attrs.isEmpty()) { return new SouQuoteTempAttrRelateVO(attrTreeMap.values()); }
        level45Relates.forEach((relation, level5AttrName) -> {
            String parentAttr = relation.split("_")[0];
            String parentTree = attrTreeMap.get(parentAttr);
            String tree = MessageFormat.format("{0} -> [{1}]", parentTree, level5AttrName);
            SouQuoteTempAttr level5Attr = level5Attrs.get(level5AttrName);
            AssertUtils.notNull(level5Attr, LocaleHandler.getLocaleMsg("报价属性依赖关系")+"{0}"+LocaleHandler.getLocaleMsg("存在错误")+": [{1}]"+LocaleHandler.getLocaleMsg("不是生效状态不存在"), tree, level5AttrName);
            AssertUtils.notNull(level5Attr, LocaleHandler.getLocaleMsg("报价属性依赖关系")+"{0}"+LocaleHandler.getLocaleMsg("存在错误")+": [{1}]"+LocaleHandler.getLocaleMsg("不是生效状态"), tree, level5AttrName);
            attrTreeMap.put(level5AttrName, tree);
        });
        /* 6: 暂不支持更深层次的引用了 */
        Map<String/* level5AttrName_level6AttrName */, String/* level6AttrName */> level56Relates; {
            level56Relates = new HashMap<>(32);
            for (SouQuoteTempAttr level5Attr : level5Attrs.values()) {
                level5Attr.getAttrRelate().getRelateTree().forEach(tree -> {
                    if (tree.size() >= 2) {
                        String level6AttrName = tree.get(1);
                        level56Relates.put(level5Attr.getAttrName() + "_" + level6AttrName, level6AttrName);
                    }
                });
            }
        }
        if (level56Relates.isEmpty()) { return new SouQuoteTempAttrRelateVO(attrTreeMap.values()); }
        Map<String/* attrName */, SouQuoteTempAttr> level6Attrs = souQuoteTempAttrRepository.lambdaQuery()
                .in(SouQuoteTempAttr::getAttrName, level56Relates.values())
                .list()
                .stream().collect(Collectors.toMap(SouQuoteTempAttr::getAttrName, java.util.function.Function.identity()));
        if (!level6Attrs.isEmpty()) {
            level56Relates.forEach((relation, level6AttrName) -> {
                String parentAttr = relation.split("_")[0];
                String parentTree = attrTreeMap.get(parentAttr);
                throw new IllegalArgumentException(MessageFormat.format("当前报价属性间的依赖已达到5层，不支持更复杂的依赖情况{0}->[{1}](不支持)",
                        parentTree, level6AttrName));
            });
        }
        return new SouQuoteTempAttrRelateVO(attrTreeMap.values());
    }

}
