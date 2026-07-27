package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 寻源openAPI - 供应商报名信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/02
 */
@Data
@ApiModel(description = "供应商报名信息")
@EqualsAndHashCode(callSuper = true)
public class ApiSouPlaceOnFileDTO extends BaseObjectX {

    @ApiModelProperty("竞价单ID")
    protected Long projectId;

    @ApiModelProperty("报价行信息")
    protected List<ApiSouChangeSelectStatusItemDTO> selects;

    @ApiModelProperty("归档附件")
    protected List<SouPlaceOnFileDTO> placeOnFileList;


    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (CollectionUtils.isEmpty(placeOnFileList)) {
            throw new IllegalArgumentException("请上传归档附件");
        }
    }

}
