package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouMarginRecord;
import com.mideacloud.common.objectx.BaseObjectX;
import lombok.Data;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
public class ApiExtSouMarginRecordDto extends BaseObjectX {

    private List<ExtSouMarginRecordDto> marginRecordList;

    private ExtSouMarginRecordDto marginRecord;

    private String type;

    /**
     * 关联保证金缴纳记录表ID
     */
    private Long marginId;
    /**
     * 关联招标基本信息主键ID
     */
    private Long projectId;

}
