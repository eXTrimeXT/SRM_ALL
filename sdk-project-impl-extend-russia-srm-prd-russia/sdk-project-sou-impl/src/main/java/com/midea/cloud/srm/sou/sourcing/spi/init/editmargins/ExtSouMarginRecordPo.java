package com.midea.cloud.srm.sou.sourcing.spi.init.editmargins;

import com.alibaba.fastjson.JSON;
import com.midea.cloud.srm.model.sou.enums.SouMarginRecordTypeEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouMarginRecordDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouMargin;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouMarginRecord;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * 备注
 * @author huangbf3
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ExtSouMarginRecordPo extends BaseObjectX {

    @ApiModelProperty("保证金扣款、罚款提交")
    private List<ExtSouMarginRecord> marginRecordList;

    private List<ExtSouMarginRecordDto> marginRecordDtoList;

    private Map<Long, ExtSouMargin> marginMap;

    private Map<Long, ExtSouMargin> yearMarginMap;


    public List<ExtSouMarginRecordDto> getMarginRecordDtoList() {
        if(CollectionUtils.isNotEmpty(marginRecordList)) {
            marginRecordDtoList = JSON.parseArray(JSON.toJSONString(marginRecordList), ExtSouMarginRecordDto.class);
            marginRecordDtoList.stream().forEach(m -> {
                if(SouMarginRecordTypeEnum.REFUND.getCode().equals(m.getType())) {
                    m.setRefundAmount(m.getAmount());
                }
            });
        }
        return marginRecordDtoList;
    }
}
