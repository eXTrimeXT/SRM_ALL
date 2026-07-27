package com.midea.cloud.srm.model.pj.sou.inq.vo.webapi.select;

import com.mideacloud.common.objectx.BaseObjectX;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InqSouSelectCountWinningWebVO extends BaseObjectX {

    private Long categoryId;

    private Long vendorId;

}
