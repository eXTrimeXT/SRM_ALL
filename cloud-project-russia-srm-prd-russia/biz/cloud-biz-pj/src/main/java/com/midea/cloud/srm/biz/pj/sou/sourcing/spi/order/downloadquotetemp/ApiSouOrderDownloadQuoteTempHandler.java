package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order.downloadquotetemp;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.EasyExcelUtil;
import com.midea.cloud.common.utils.easyexcel.SpinnerWriteHandler2;
import com.midea.cloud.component.filter.HttpServletHolder;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.dao.SouQuoteTempAttrRepositoryImpl;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.dao.SouQuoteTempFieldRepositoryImpl;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.dao.SouQuoteTempLineRepositoryImpl;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.service.ISouQuoteTempService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouItemDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order.editorder.SouOrderDtoContext;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempAttr;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempField;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempLine;
import com.midea.cloud.srm.model.bid.quotetemplate.enums.SouQuoteTempFieldTypeEnum;
import com.midea.cloud.srm.model.bid.quotetemplate.vo.SouQuoteTempDataVO;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderItemQuoteTempDownloadDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * @author huangbf3
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ApiSouOrderDownloadQuoteTempHandler implements ISouSpiBean {

    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private SouItemDAOImpl souItemDao;
    @Autowired
    private SouQuoteTempLineRepositoryImpl souQuoteTempLineRepository;
    @Autowired
    private SouQuoteTempAttrRepositoryImpl souQuoteTempAttrRepository;
    @Autowired
    private SouQuoteTempFieldRepositoryImpl souQuoteTempFieldRepository;
    @Autowired
    private ISouQuoteTempService souQuoteTempService;
    @Autowired
    private BaseClient baseClient;

    /**
     * 下载excel
     */
    public void download(ApiSouOrderItemQuoteTempDownloadDTO param, boolean isBuyer) {
        // 1: 构造业务所需的上下文数据，并保存到上下文中
        SouOrderDownloadQuoteTempDtoContext.setContextHolder(this.buildContextData(param, isBuyer));
        try {
            try (OutputStream outputStream = EasyExcelUtil.getServletOutputStream(HttpServletHolder.getResponse(), "寻源报价物料-料费分离导入模板")) {
                ExcelWriter writer = EasyExcel.write(outputStream).build();

                int index = -1;
                List<SouQuoteTempLine> tempLineList = SouOrderDownloadQuoteTempDtoContext.getContextHolder().getTempLineList();
                for (SouQuoteTempLine tempLine : tempLineList) {
                    index++;
                    if (tempLineList.size() > 1 && Enable.Y.equals(tempLine.getIsTotal())) { continue; }
                    SouQuoteTempAttr attr = SouOrderDownloadQuoteTempDtoContext.getContextHolder().getAttrMap().get(tempLine.getAttrId());
                    List<SouQuoteTempField> attrFieldList = SouOrderDownloadQuoteTempDtoContext.getContextHolder().getAttrFieldMap().get(tempLine.getAttrId());

                    this.handlerOneSheet(writer, tempLine, attr, attrFieldList, index);
                }
                writer.finish();
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("获取io异常");
        } finally {
            // 4: 清除业务上下文
            SouOrderDtoContext.remove();
        }
    }

    protected SouOrderDownloadQuoteTempDtoContext buildContextData(ApiSouOrderItemQuoteTempDownloadDTO param, boolean isBuyer) {
        SouOrderDownloadQuoteTempDtoContext context = new SouOrderDownloadQuoteTempDtoContext();
        context.setParam(param);
        // 查询寻源单
        SouProject souProject = souProjectDao.getById(param.getProjectId());
        context.setSouProject(souProject);
        if (param.getRound() == null) { param.setRound(souProject.getCurrentRound()); }
        // 查询物料需求
        SouItem souItem = souItemDao.getById(param.getSouItemId());
        AssertUtils.notNull(souItem, "物料需求行[{0}]不存在", param.getSouItemId());
        context.setSouItem(souItem);
        // 查询报价模板明细
        List<SouQuoteTempLine> tempLineList = souQuoteTempLineRepository.list(SouQuoteTempLine::getTempId, souProject.getQuoteTempId())
                .stream().sorted(Comparator.comparing(SouQuoteTempLine::getSortIndex)).collect(Collectors.toList());
        context.setTempLineList(tempLineList);
        // 查询报价属性
        Map<Long/* attrId */, SouQuoteTempAttr> attrMap = souQuoteTempAttrRepository.listByIds(tempLineList.stream().map(SouQuoteTempLine::getAttrId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(SouQuoteTempAttr::getAttrId, Function.identity()));
        context.setAttrMap(attrMap);
        // 查询报价属性字段
        List<SouQuoteTempField> attrFieldList = souQuoteTempFieldRepository.lambdaQuery()
                .in(SouQuoteTempField::getAttrId, attrMap.keySet())
                .list();
        Map<Long/* attrId */, List<SouQuoteTempField>> attrFieldMap = attrFieldList.stream()
                .sorted(Comparator.comparing(SouQuoteTempField::getSortIndex)).collect(Collectors.groupingBy(SouQuoteTempField::getAttrId));
        context.setAttrFieldMap(attrFieldMap);
        // 查询模板报价数据
        String businessId = souProject.getProjectId() + "_" + param.getRound() + "_" + param.getVendorId() + "_" + param.getSouItemId();
        SouQuoteTempDataVO quoteData = souQuoteTempService.queryTempData(souProject.getQuoteTempId(), businessId, true);
        context.setQuoteData(quoteData);
        // 查询字典数据
        Map<String/* dictCode */, List<DictItemDTO>> dictMap; {
            Set<String> dictCodes = attrFieldList.stream().filter(e -> SouQuoteTempFieldTypeEnum.DICT.equals(e.getFieldType()))
                    .map(SouQuoteTempField::getFieldValue).collect(Collectors.toSet());
            if (dictCodes.isEmpty()) {
                dictMap = Collections.emptyMap();
            } else {
                dictMap = baseClient.listByDictCode(new ArrayList<>(dictCodes)).stream()
                        .collect(Collectors.groupingBy(DictItemDTO::getDictCode));
            }
        }
        context.setDictMap(dictMap);

        return context;
    }

    @ApiOperation("处理单个sheet")
    protected void handlerOneSheet(ExcelWriter writer, SouQuoteTempLine tempLine, SouQuoteTempAttr attr, List<SouQuoteTempField> fieldList, int index) {
        List<List<String>> heads = new ArrayList<>(fieldList.size()); {
            fieldList.forEach(field -> heads.add(Collections.singletonList(field.getFieldName())));
        }
        Map<Integer/* columnIndex */, String[]> arrMap = new HashMap<>(fieldList.size()); {
            for (int i = 0; i < fieldList.size(); i++) {
                SouQuoteTempField field = fieldList.get(i);
                boolean isEnum = SouQuoteTempFieldTypeEnum.ENUM_DECIMAL.equals(field.getFieldType()) || SouQuoteTempFieldTypeEnum.ENUM_TEXT.equals(field.getFieldType());
                if (isEnum) {
                    arrMap.put(i, field.getFieldValue().split(","));
                } else if (SouQuoteTempFieldTypeEnum.DICT.equals(field.getFieldType())) {
                    List<DictItemDTO> dictItemList = SouOrderDownloadQuoteTempDtoContext.getContextHolder().getDictMap().get(field.getFieldValue());
                    if (CollectionUtils.isNotEmpty(dictItemList)) {
                        arrMap.put(i, dictItemList.stream().map(DictItemDTO::getDictItemName).toArray(String[]::new));
                    }
                }
            }
        }


        WriteSheet sheet = EasyExcel
                .writerSheet(index, attr.getAttrName())
                .head(heads)
                .registerWriteHandler(new SpinnerWriteHandler2(arrMap))
                .build();
        writer.write(this.handlerQuoteData(tempLine, attr, fieldList, index), sheet);
    }

    protected List<List<Object>> handlerQuoteData(SouQuoteTempLine tempLine, SouQuoteTempAttr attr, List<SouQuoteTempField> fieldList, int index) {
        List<Map<String/* fieldId */, Object>> quoteDataList = SouOrderDownloadQuoteTempDtoContext.getContextHolder().getQuoteData().getPriceData().getData().get(attr.getAttrId());
        if (CollectionUtils.isEmpty(quoteDataList)) { return Collections.emptyList(); }
        // 字典值编码转字典名称
        for (Map<String/* fieldId */, Object> data : quoteDataList) {
            fieldList.forEach(field -> {
                if (SouQuoteTempFieldTypeEnum.DICT.equals(field.getFieldType())) {
                    List<DictItemDTO> dictItemList = SouOrderDownloadQuoteTempDtoContext.getContextHolder().getDictMap().get(field.getFieldValue());
                    if (CollectionUtils.isNotEmpty(dictItemList)) {
                        Map<String/* dictItemCode */, String/* dictItemName */> dictItemMap = dictItemList.stream()
                                .collect(Collectors.toMap(DictItemDTO::getDictItemCode, DictItemDTO::getDictItemName));
                        data.put(field.getFieldId().toString(), dictItemMap.get(data.get(field.getFieldId().toString())));
                    }
                }
            });
        }
        List<List<Object>> dataList = new ArrayList<>(quoteDataList.size());
        for (Map<String/* fieldId */, Object> quoteData : quoteDataList) {
            List<Object> data = new ArrayList<>(fieldList.size());
            dataList.add(data);
            for (SouQuoteTempField field : fieldList) {
                data.add(quoteData.get(field.getFieldId().toString()));
            }
        }
        return dataList;
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
