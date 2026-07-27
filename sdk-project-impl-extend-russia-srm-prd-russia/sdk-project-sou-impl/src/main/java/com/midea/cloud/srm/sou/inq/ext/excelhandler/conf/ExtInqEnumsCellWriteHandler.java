package com.midea.cloud.srm.sou.inq.ext.excelhandler.conf;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.midea.cloud.component.context.container.SpringContextHolder;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseTax;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseUnit;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtInqExcelPropertyValues;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Slf4j
public class ExtInqEnumsCellWriteHandler implements CellWriteHandler {

    /**
     * 目前线程安全，整一个给业务使用
     */
    public static final ExtInqEnumsCellWriteHandler HANDLER = new ExtInqEnumsCellWriteHandler();
    /**
     * 使用缓存，防止对多个cell均执行远程访问，消耗资源
     */
    volatile static ConcurrentHashMap<String, List<DictItemDTO>> DICT_MAP = new ConcurrentHashMap<>(64);
    volatile static String UNIT_CODE = "$SRM_UNIT_CODE";
    volatile static String TAX_CODE = "$SRM_TAX_CODE";

    static Timer TIMER = new Timer();

    static {
        TIMER.schedule(new TimerTask() {
            @Override
            public void run() {
                // 清除缓存，防止字典信息过旧
                DICT_MAP.clear();
            }
        }, 10000, 50000);
    }

    private ExtInqEnumsCellWriteHandler() {}

    @Override
    public void beforeCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder,
                                 Row row, Head head, Integer columnIndex, Integer relativeRowIndex, Boolean isHead) {

    }

    @Override
    public void afterCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder,
                                Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {

    }

    @Override
    public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder,
                                 List<WriteCellData<?>> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
        Class<?> clazz = writeSheetHolder.getClazz();
        if (clazz == null) { return; }

        String fieldName = head.getFieldName();
        if (fieldName == null) { return; }

        try {
            Field field = clazz.getDeclaredField(fieldName);
            if (field.getType().equals(String.class)) {
                // 枚举值，只能是String类型
                Annotation[] annotations = field.getDeclaredAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation instanceof ExtInqExcelPropertyValues) {
                        ExtInqExcelPropertyValues e = (ExtInqExcelPropertyValues)annotation;
                        if (e.forOutput()) {
                            if (StringUtils.isNotEmpty(e.dictCode())) {
                                // 变更为字典中文名称
                                this.changeCellValue(e.dictCode(), cell);
                            } else if (e.useUnit()) {
                                // 将单位编码变为单位名称
                                this.changeUnitCodeToName(cell);
                            }
                        }
                    }
                }
            }

        } catch (NoSuchFieldException e) {
            // 忽略
            log.error("导出 - 下拉框处理报错", e);
            return;
        }
    }

    private void changeCellValue(String dictCode, Cell cell) {
        List<DictItemDTO> dictItemList = getDictInfoByCode(dictCode);
        Map<String/* code */, String/* name */> dictItemMap = dictItemList.stream()
                .collect(Collectors.toMap(DictItemDTO::getDictItemCode, DictItemDTO::getDictItemName));

        String value = dictItemMap.get(cell.getStringCellValue());
        if (value != null) {
            cell.setCellValue(value);
        }
    }

    private void changeUnitCodeToName(Cell cell) {
        // 1: 查询单位数据
        List<DictItemDTO> dictItemList = getUnitInfos();
        Map<String/* code */, String/* name */> dictItemMap = dictItemList.stream()
                .collect(Collectors.toMap(DictItemDTO::getDictItemCode, DictItemDTO::getDictItemName));

        String value = dictItemMap.get(cell.getStringCellValue());
        if (value != null) {
            cell.setCellValue(value);
        }
    }

    /**
     * 从base服务获取字典信息，并保存到本地缓存中
     */
    static List<DictItemDTO> getDictInfoByCode(String dictCode) {
        // 远程调用baseClient，获取字典信息(注意，如果是内部访问，请修改这里，使用anon接口)
        List<DictItemDTO> dictItemList = ExtInqEnumsCellWriteHandler.DICT_MAP.get(dictCode);
        if (dictItemList == null) {
            dictItemList = SpringContextHolder.getBean(BaseClient.class).listAllByDictCode(dictCode.trim());
            if (dictItemList == null) {
                dictItemList = Collections.emptyList();
            }
            ExtInqEnumsCellWriteHandler.DICT_MAP.put(dictCode, dictItemList);
        }
        return dictItemList;
    }

    public static List<DictItemDTO> getUnitInfos() {
        List<DictItemDTO> dictList = ExtInqEnumsCellWriteHandler.DICT_MAP.get(UNIT_CODE);
        if (CollectionUtils.isEmpty(dictList)) {
            // 查询数据
            List<PurchaseUnit> unitList = SpringContextHolder.getBean(BaseClient.class).listAllEnablePurchaseUnit()
                    .stream().filter(e -> Enable.Y.name().equals(e.getEnabled())).collect(Collectors.toList());
            dictList = unitList.stream()
                    .map(unit -> {
                        DictItemDTO dict = new DictItemDTO();
                        dict.setDictItemCode(unit.getUnitCode());
                        dict.setDictItemName(unit.getUnitName());
                        return dict;
                    })
                    .collect(Collectors.toList());
            // 将数据放到缓存
            ExtInqEnumsCellWriteHandler.DICT_MAP.put(UNIT_CODE, dictList);
        }
        return dictList;
    }

    public static List<DictItemDTO> getTaxInfos() {
        List<DictItemDTO> dictList = ExtInqEnumsCellWriteHandler.DICT_MAP.get(TAX_CODE);
        if (CollectionUtils.isEmpty(dictList)) {
            // 查询数据
            List<PurchaseTax> taxList = SpringContextHolder.getBean(BaseClient.class).listTaxAll();
            dictList = taxList.stream()
                    .map(unit -> {
                        DictItemDTO dict = new DictItemDTO();
                        dict.setDictItemCode(unit.getTaxKey());
                        dict.setDictItemName(unit.getTaxKey());
                        return dict;
                    })
                    .collect(Collectors.toList());
            // 将数据放到缓存
            ExtInqEnumsCellWriteHandler.DICT_MAP.put(TAX_CODE, dictList);
        }
        return dictList;
    }

}
