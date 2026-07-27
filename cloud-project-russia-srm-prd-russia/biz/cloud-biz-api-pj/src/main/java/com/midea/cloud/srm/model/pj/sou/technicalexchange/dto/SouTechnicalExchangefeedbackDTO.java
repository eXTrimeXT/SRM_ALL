package com.midea.cloud.srm.model.pj.sou.technicalexchange.dto;

import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <pre>
 * 功能名称
 * </pre>
 *
 * @author ex_nongtb@partner.midea.com
 * @version 1.00.00
 *
 * <pre>
 * 修改记录
 * 修改后版本:
 * 修改人: ex_nongtb
 * 修改日期: 2022/4/29 11:21
 * 修改内容:
 * </pre>
 * @date 2022/04/29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SouTechnicalExchangefeedbackDTO {

    @ApiModelProperty("技术交流-供应商反馈ID")
    private Long technicalExchangeFeedbackId;

    @ApiModelProperty("技术交流单据-数据ID")
    private Long technicalExchangeId;

    @ApiModelProperty(value = "供应商-反馈技术附件")
    private List<SceneFile> tecExcFiles;

}
