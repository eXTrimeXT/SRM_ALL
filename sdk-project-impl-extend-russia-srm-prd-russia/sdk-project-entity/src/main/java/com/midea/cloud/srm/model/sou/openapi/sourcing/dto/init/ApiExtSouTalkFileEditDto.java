package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouFile;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode
@ApiModel("商务标管理-谈判资料")
public class ApiExtSouTalkFileEditDto extends BaseObjectX {

    @ApiModelProperty("招标单ID")
    private Long projectId;

    @ApiModelProperty("谈判资料")
    List<ExtSouFile> talkFileList;

    public List<ExtSouFile> getTalkFileList() {
        if(Objects.isNull(talkFileList)) {
            talkFileList = new ArrayList<>();
        }
        return talkFileList;
    }
}
