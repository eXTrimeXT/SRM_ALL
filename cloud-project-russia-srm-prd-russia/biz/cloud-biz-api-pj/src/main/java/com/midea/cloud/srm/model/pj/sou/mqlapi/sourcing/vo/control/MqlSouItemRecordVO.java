package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.control;

import com.github.pagehelper.Page;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItemRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * MQL - 物料变更记录
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouItemRecordVO extends SouItemRecord {

    @SuppressWarnings("rawtypes")
    public static List<MqlSouItemRecordVO> convertMqlVO(List<SouItemRecord> recordList) {
        if (recordList.isEmpty()) { return Collections.emptyList(); }
        List<MqlSouItemRecordVO> voList;
        if (recordList instanceof Page) {
            voList = new Page<>();
            ((Page)voList).setTotal(((Page)recordList).getTotal());
            ((Page)voList).setPageNum(((Page)recordList).getPageNum());
            ((Page)voList).setPageSize(((Page)recordList).getPageSize());
        } else {
            voList = new ArrayList<>(recordList.size());
        }

        for (SouItemRecord record : recordList) {
            MqlSouItemRecordVO vo = SouObjectXUtil.convertTargetObj(record, MqlSouItemRecordVO.class);
            voList.add(vo);
        }
        return voList;
    }

}