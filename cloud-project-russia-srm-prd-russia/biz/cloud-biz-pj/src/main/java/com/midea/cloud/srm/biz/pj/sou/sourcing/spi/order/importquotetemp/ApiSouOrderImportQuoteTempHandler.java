package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order.importquotetemp;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.dao.SouQuoteTempAttrRepositoryImpl;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.dao.SouQuoteTempFieldRepositoryImpl;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.dao.SouQuoteTempLineRepositoryImpl;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.service.ISouQuoteTempService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouItemDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order.editorder.SouOrderDtoContext;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempAttr;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempField;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempLine;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * @author huangbf3
 */
@Component
@Slf4j
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ApiSouOrderImportQuoteTempHandler implements ISouSpiBean {

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

    /**
     * 下载excel
     */
    public void execute(long projectId, long vendorId, long souItemId, @Nullable Integer round, boolean isBuyer, String souType, MultipartFile file) {
        // 1: 构造业务所需的上下文数据，并保存到上下文中
        SouOrderImportQuoteTempDtoContext.setContextHolder(this.buildContextData(projectId, vendorId, souItemId, round, isBuyer, souType, file));
        try {
            // 2: 读取excel文件
            Map<Long/* attrId */, List<Map<String/* fieldId */, Object>>> dataMap = this.readData();
            // 3: 数据处理与保存
            String businessId = projectId + "_" + SouOrderImportQuoteTempDtoContext.getContextHolder().getSearchRound() + "_" + vendorId + "_" + souItemId;
            souQuoteTempService.computeTempData(
                    SouOrderImportQuoteTempDtoContext.getContextHolder().getSouProject().getQuoteTempId(),
                    businessId,
                    dataMap,
                    true, false);
        } finally {
            // 4: 清除业务上下文
            SouOrderDtoContext.remove();
        }
    }

    protected SouOrderImportQuoteTempDtoContext buildContextData(long projectId, long vendorId, long souItemId, @Nullable Integer round, boolean isBuyer,
                                                                 String souType, MultipartFile file) {
        SouOrderImportQuoteTempDtoContext context = new SouOrderImportQuoteTempDtoContext();
        context.setVendorId(vendorId);
        context.setFile(file);
        context.setBuyer(isBuyer);
        // 查询寻源单
        SouProject souProject = souProjectDao.getById(projectId);
        context.setSouProject(souProject);
        context.setSearchRound(round != null ? round : souProject.getCurrentRound());
        // 查询物料需求
        SouItem souItem = souItemDao.getById(souItemId);
        AssertUtils.notNull(souItem, "物料需求行[{0}]不存在", souItemId);
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
        Map<Long/* attrId */, List<SouQuoteTempField>> attrFieldMap = souQuoteTempFieldRepository.lambdaQuery()
                .in(SouQuoteTempField::getAttrId, attrMap.keySet())
                .list().stream().sorted(Comparator.comparing(SouQuoteTempField::getSortIndex)).collect(Collectors.groupingBy(SouQuoteTempField::getAttrId));
        context.setAttrFieldMap(attrFieldMap);

        return context;
    }

    public static void main(String[] args) {
        Map<String,String> map = new HashMap<>(1);
        map.put("a","a");
        map.put("b","b");
        map.put("c","c");
        log.info(map.size()+"");
    }

    protected Map<Long/* attrId */, List<Map<String/* fieldId */, Object>>> readData() {
        Map<Long/* attrId */, List<Map<String/* fieldId */, Object>>> dataMap = new HashMap<>(50);
        ExcelReader reader = null;
        try {
            AnalysisEventListenerImpl listener = new AnalysisEventListenerImpl();
            reader = EasyExcel.read(SouOrderImportQuoteTempDtoContext.getContextHolder().getFile().getInputStream(), listener).build();

            int index = -1;
            for (SouQuoteTempLine tempLine : SouOrderImportQuoteTempDtoContext.getContextHolder().getTempLineList()) {
                index++;
                SouQuoteTempAttr attr = SouOrderImportQuoteTempDtoContext.getContextHolder().getAttrMap().get(tempLine.getAttrId());
                List<SouQuoteTempField> attrFieldList = SouOrderImportQuoteTempDtoContext.getContextHolder().getAttrFieldMap().get(tempLine.getAttrId());

                dataMap.put(attr.getAttrId(), this.readSheet(reader, listener, tempLine, attr, attrFieldList, index));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("获取io失败");
        } finally {
            if (reader != null) {
                reader.finish();
                reader.close();
            }
        }
        return dataMap;
    }

    @ApiOperation("处理单个sheet")
    protected List<Map<String/* fieldId */, Object>> readSheet(ExcelReader reader, AnalysisEventListenerImpl listener, SouQuoteTempLine tempLine, SouQuoteTempAttr attr, List<SouQuoteTempField> fieldList, int index) {
        List<LinkedHashMap<Integer, Object>> dataList; {
            ReadSheet readSheet = EasyExcel.readSheet(attr.getAttrName()).build();
            reader.read(readSheet);
            dataList = listener.getDatas();
            listener.setDatas(new ArrayList<>());
        }

        List<Map<String/* fieldId */, Object>> resultList = new ArrayList<>(dataList.size());
        for (LinkedHashMap<Integer, Object> data : dataList) {
            Map<String/* fieldId */, Object> result = new HashMap<>(32);
            resultList.add(result);

            int columnIndex = -1;
            for (SouQuoteTempField field : fieldList) {
                columnIndex++;
                result.put(field.getFieldId().toString(), data.get(columnIndex));
            }
        }
        if (Enable.Y.equals(tempLine.getIsTotal()) && dataList.isEmpty()) {
            // 用于处理汇总数据(避免不计算)
            resultList.add(new HashMap<>(50));
        }

        return resultList;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    protected static class AnalysisEventListenerImpl extends AnalysisEventListener<LinkedHashMap<Integer, Object>> {

        private List<LinkedHashMap<Integer, Object>> datas = new ArrayList<>();

        @Override
        public void invoke(LinkedHashMap<Integer, Object> data, AnalysisContext context) {
            datas.add(data);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {

        }

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
