package com.midea.cloud.srm.model.pj.sou.mqlapi.inq.vo.select;

import com.mideacloud.common.objectx.BaseObjectX;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlInqSouSelectCountWinningWebVO extends BaseObjectX {

    private Long categoryId;

    private Long vendorId;

}
