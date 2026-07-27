package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.control;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.control.ApiSouItemRecordVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItemRecord;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouItemRefreshTypeEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 寻源核心 - 最新的物料变更情况
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouItemRecordLatestVO extends BaseObjectX {

    @ApiModelProperty("新增数量")
    private Integer addCnt;
    @ApiModelProperty("删除数量")
    private Integer deleteCnt;
    @ApiModelProperty("最新的物料变更信息")
    private List<ApiSouItemRecordVO> recordList;

    public static ApiSouItemRecordLatestVO convertApiVO(List<SouItemRecord> recordList) {
        ApiSouItemRecordLatestVO vo = new ApiSouItemRecordLatestVO();

        recordList.sort(Comparator.comparing(SouItemRecord::getBatchNo).reversed());
        if (recordList.isEmpty()) {
            vo.setAddCnt(0);
            vo.setDeleteCnt(0);
            vo.setRecordList(Collections.emptyList());
        } else {
            String latestBatchNo = recordList.get(0).getBatchNo();
            recordList = recordList.stream().filter(r -> r.getBatchNo().equals(latestBatchNo)).collect(Collectors.toList());

            vo.setRecordList(ApiSouItemRecordVO.convertApiVO(recordList));
            vo.setAddCnt((int)recordList.stream()
                    .map(SouItemRecord::getRefreshType)
                    .filter(SouItemRefreshTypeEnum.NEW::equals)
                    .count());
            vo.setDeleteCnt((int)recordList.stream()
                    .map(SouItemRecord::getRefreshType)
                    .filter(SouItemRefreshTypeEnum.DELETE::equals)
                    .count());
        }
        return vo;
    }

}
