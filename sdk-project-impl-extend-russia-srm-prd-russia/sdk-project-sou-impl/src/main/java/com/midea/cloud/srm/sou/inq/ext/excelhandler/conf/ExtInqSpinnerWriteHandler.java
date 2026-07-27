package com.midea.cloud.srm.sou.inq.ext.excelhandler.conf;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtInqExcelPropertyValues;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
public class ExtInqSpinnerWriteHandler implements SheetWriteHandler {

    /**
     * 目前线程安全，整一个给业务使用
     */
    public static final ExtInqSpinnerWriteHandler HANDLER = new ExtInqSpinnerWriteHandler();

    private ExtInqSpinnerWriteHandler() {}

    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {

    }

    private Map<Integer, String[]> getMapDropDown(WriteSheetHolder writeSheetHolder) {
        // 获取EasyExcel.readSheet(..).head(xxx.class) 中head()方法声明的类
        Class<?> clazz = writeSheetHolder.getClazz();
        if (clazz == null) { return Collections.emptyMap(); }

        Field[] fields = clazz.getDeclaredFields();

        Map<Integer/* index */, String[]> dropDownMap = new HashMap<>(fields.length + fields.length >>> 1 + 1);

        ExcelProperty excelProperty;
        ExtInqExcelPropertyValues excelPropertyValues;
        int anonIndex = -1;
        for (Field field : fields) {
            excelProperty = field.getDeclaredAnnotation(ExcelProperty.class);
            if (excelProperty == null) {
                continue;
            }

            anonIndex++;

            excelPropertyValues = field.getDeclaredAnnotation(ExtInqExcelPropertyValues.class);
            if (excelPropertyValues == null) {
                continue;
            }
            if (!excelPropertyValues.forSpinner()) {
                continue;
            }

            if (excelPropertyValues.values().length > 0) {
                dropDownMap.put(
                        excelProperty.index() >= 0 ? excelProperty.index() : anonIndex,
                        excelPropertyValues.values());
            } else if(StringUtils.isNotBlank(excelPropertyValues.dictCode())) {
                // ExtInqSpinnerWriteHandler / ExtInqEnumsCellWriteHandler 使用同一个字典缓存
                List<DictItemDTO> dictItemList = ExtInqEnumsCellWriteHandler.getDictInfoByCode(excelPropertyValues.dictCode().trim());
                dropDownMap.put(
                        excelProperty.index() >= 0 ? excelProperty.index() : anonIndex,
                        dictItemList.stream().map(DictItemDTO::getDictItemName).collect(Collectors.toSet()).toArray(new String[0]));
            } else if (excelPropertyValues.useUnit()) {
                // 处理单位
                List<DictItemDTO> dictItemList = ExtInqEnumsCellWriteHandler.getUnitInfos();
                dropDownMap.put(
                        excelProperty.index() >= 0 ? excelProperty.index() : anonIndex,
                        dictItemList.stream().map(DictItemDTO::getDictItemName).collect(Collectors.toSet()).toArray(new String[0]));
            } else if (excelPropertyValues.useTax()) {
                // 处理税率
                List<DictItemDTO> dictItemList = ExtInqEnumsCellWriteHandler.getTaxInfos();
                dropDownMap.put(
                        excelProperty.index() >= 0 ? excelProperty.index() : anonIndex,
                        dictItemList.stream().map(DictItemDTO::getDictItemName).collect(Collectors.toSet()).toArray(new String[0]));
            }
        }
        return dropDownMap;
    }

    private Set<Integer> setHiddenSheetForBigDictCodes(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder, Map<Integer,String []> mapDropDown) {
        // 判断是否需要设置隐藏sheet页
        Set<Integer> hiddenColSet = new HashSet<>();
        String[] colIndex = {"A","B","C","D","E","F","G","H","I","G","K","L","M","N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
                "AA","AB","AC","AD","AE","AF","AG","AH","AI","AG","AK","AL","AM","AN", "AO", "AP", "AQ", "AR", "AS", "AT", "AU", "AV", "AW", "AX", "AY", "AZ",
                "BA","BB","BC","BD","BE","BF","BG","BH","BI","BG","BK","BL","BM","BN", "BO", "BP", "BQ", "BR", "BS", "BT", "BU", "BV", "BW", "BX", "BY", "BZ",};
        Sheet hidden = writeWorkbookHolder.getWorkbook().getSheet("_hidden_");
        if (hidden == null) {
            hidden = writeWorkbookHolder.getWorkbook().createSheet("_hidden_");
        }
        {
            int hiddenIndex = -1;
            Iterator<Sheet> sheetIterator = writeWorkbookHolder.getWorkbook().sheetIterator();
            while (sheetIterator.hasNext()) {
                hiddenIndex++;
                String hiddenText = "_hidden_";
                if (sheetIterator.next().getSheetName().equals(hiddenText)) { break; }
            }
            if (hiddenIndex >= 0) {
                writeWorkbookHolder.getWorkbook().setSheetHidden(hiddenIndex, true);
            }
        }

        Map<Integer, Row> rowMap = new HashMap<>(50);
        for (Map.Entry<Integer, String[]> entry : mapDropDown.entrySet()) {
            Integer index = entry.getKey();
            String[] dictItems = entry.getValue();

            StringBuilder sb = new StringBuilder(300);
            for (String arr : dictItems) {
                sb.append(arr);
            }
            if (sb.length() >= 160) {
                hiddenColSet.add(index);
                for (int i = 0; i < dictItems.length; i++) {
                    Row row = rowMap.get(i);
                    if (row == null) {
                        row = hidden.createRow(i);
                        rowMap.put(i, row);
                    }
                    row.createCell(index).setCellValue(dictItems[i]);
                }
                Name refName = writeWorkbookHolder.getWorkbook().createName();
                refName.setNameName("ref_hidden_" + index);
                // 设置名称引用公式
                refName.setRefersToFormula("_hidden_!" + "$"+colIndex[index]+"$1:$"+colIndex[index]+"$"+dictItems.length);
                // 获取上文名称内数据
                DataValidationHelper helper = writeSheetHolder.getSheet().getDataValidationHelper();
                DataValidationConstraint constraint = helper.createFormulaListConstraint("ref_hidden_" + index);
                // 设置下拉框位置
                CellRangeAddressList addressList = new CellRangeAddressList(1, 1500, index, index);
                DataValidation dataValidation = helper.createValidation(constraint, addressList);
                writeSheetHolder.getSheet().addValidationData(dataValidation);
            }
        }

        return hiddenColSet;
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        // 获取字典列信息
        Map<Integer,String []> mapDropDown = this.getMapDropDown(writeSheetHolder);
        // 处理超大下拉框，用隐藏sheet
        Set<Integer> hiddenIndexSet = this.setHiddenSheetForBigDictCodes(writeWorkbookHolder, writeSheetHolder, mapDropDown);

        Sheet sheet = writeSheetHolder.getSheet();
        ///开始设置下拉框 设置下拉框
        DataValidationHelper helper = sheet.getDataValidationHelper();
        for (Map.Entry<Integer, String[]> entry : mapDropDown.entrySet()) {
            if (hiddenIndexSet.contains(entry.getKey())) { continue; }
            /***起始行、终止行、起始列、终止列**/
            CellRangeAddressList addressList = new CellRangeAddressList(1, 2000, entry.getKey(), entry.getKey());
            /***设置下拉框数据**/
            DataValidationConstraint constraint = helper.createExplicitListConstraint(entry.getValue());
            DataValidation dataValidation = helper.createValidation(constraint, addressList);
            /***处理Excel兼容性问题**/
            if (dataValidation != null) {
                if (dataValidation instanceof XSSFDataValidation) {
                    dataValidation.setSuppressDropDownArrow(true);
                    dataValidation.setShowErrorBox(true);
                } else {
                    dataValidation.setSuppressDropDownArrow(false);
                }
                sheet.addValidationData(dataValidation);
            }
        }
    }

}
