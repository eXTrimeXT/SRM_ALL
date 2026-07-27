package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.control;

import com.github.pagehelper.Page;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItemRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 寻源核心 - 物料变更记录
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/16
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouItemRecordVO extends SouItemRecord {

    @SuppressWarnings("rawtypes")
    public static List<ApiSouItemRecordVO> convertApiVO(List<SouItemRecord> recordList) {
        if (recordList.isEmpty()) { return Collections.emptyList(); }
        List<ApiSouItemRecordVO> voList;
        if (recordList instanceof Page) {
            voList = new Page<>();
            ((Page)voList).setTotal(((Page)recordList).getTotal());
            ((Page)voList).setPageNum(((Page)recordList).getPageNum());
            ((Page)voList).setPageSize(((Page)recordList).getPageSize());
        } else {
            voList = new ArrayList<>(recordList.size());
        }

        for (SouItemRecord record : recordList) {
            ApiSouItemRecordVO vo = SouObjectXUtil.convertTargetObj(record, ApiSouItemRecordVO.class);
            voList.add(vo);
        }
        return voList;
    }

}
