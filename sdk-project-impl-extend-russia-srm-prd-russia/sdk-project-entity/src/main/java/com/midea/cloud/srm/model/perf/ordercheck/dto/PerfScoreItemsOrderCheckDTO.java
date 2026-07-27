package com.midea.cloud.srm.model.perf.ordercheck.dto;

import com.midea.cloud.srm.model.perf.ordercheck.entity.PerfScoreItemsOrderCheckDetail;
import com.midea.cloud.srm.model.perf.scoreproject.entity.PerfScoreItemsOrderCheck;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *  绩效评分项目供应商表 模型
 * </pre>
 *
 * @author luxc18@meiCloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020-06-06 15:10:37
 *  修改内容:
 * </pre>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(description = "订单化绩效复核dto")
public class PerfScoreItemsOrderCheckDTO extends PerfScoreItemsOrderCheck {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty(value = "复核明细")
    private List<PerfScoreItemsOrderCheckDetail> detailList = new ArrayList<>();

    @ApiModelProperty(value = "驳回原因")
    private String rejectRemark;
}
