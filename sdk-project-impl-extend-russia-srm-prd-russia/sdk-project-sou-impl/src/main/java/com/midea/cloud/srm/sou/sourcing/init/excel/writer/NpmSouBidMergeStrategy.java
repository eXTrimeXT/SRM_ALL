package com.midea.cloud.srm.sou.sourcing.init.excel.writer;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.merge.AbstractMergeStrategy;
import com.midea.cloud.srm.model.constant.SrmConstant;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: panmq
 * @Date: 2024/05/07/ $
 * @Description: 合并列策略
 */
@Data
@Slf4j
public class NpmSouBidMergeStrategy extends AbstractMergeStrategy {

    /**
     * 合并范围
     */
    private Map<String, Integer[]> mergeRange = new HashMap<>(16);

    public void addMergeRange(Integer rowStart, Integer rowEnd, Integer colStart, Integer colEnd) {
        String mergeKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, rowEnd, colEnd);
        mergeRange.put(mergeKey, new Integer[]{rowStart, rowEnd, colStart, colEnd});
    }

    @Override
    protected void merge(Sheet sheet, Cell cell, Head head, Integer integer) {
        //行
        Integer rowIndex = cell.getRowIndex();
        //行
        Integer colIndex = cell.getColumnIndex();

        String mergeKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, rowIndex, colIndex);

        if(mergeRange.containsKey(mergeKey)) {
            log.info("NpmSouBidMergeStrategy合并单元格：" + mergeKey);
            Integer[] range = mergeRange.get(mergeKey);
            CellRangeAddress rangeAddress = new CellRangeAddress(range[0], range[1], range[2], range[3]);
            sheet.addMergedRegion(rangeAddress);
        }

    }
}
