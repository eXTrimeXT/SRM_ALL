package com.midea.cloud.srm.model.perf.pjquicksearch.dto;

import com.midea.cloud.srm.model.perf.scoreproject.entity.PerfScoreItems;
import lombok.Data;

/**
 * <pre>
 *  功能名称
 * </pre>
 *
 * @author luxc18
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/12/14 15:00
 *  修改内容:
 * </pre>
 */
@Data
public class PerfScoreItemsQueryDto extends PerfScoreItems {
    private String vendorName;
    private String vendorCode;

}
