package com.midea.cloud.srm.model.pj.sou.technicalexchange.dto;

import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.common.BasePage;
import com.midea.cloud.srm.model.pj.sou.technicalexchange.dto.SouTechnicalExchangeItemDTO;
import com.midea.cloud.srm.model.pj.sou.technicalexchange.entity.SouTechnicalExchangeVendor;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 技术交流dto
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
 * 修改日期: 2022/4/27 16:54
 * 修改内容:
 * </pre>
 * @date 2022/04/27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class SouTechnicalExchangeDTO extends BasePage implements Serializable {

    private static final long serialVersionUID = -4962893519194433947L;


    /**
     * 技术交流数据ID
     */
    @ApiModelProperty("技术交流数据ID")
    private Long technicalExchangeId;
    /**
     * 交流单号
     */
    @ApiModelProperty("交流单号")
    private String technicalExchangeFormCode;
    /**
     * 交流标题
     */
    @ApiModelProperty("交流标题")
    private String technicalExchangeTitle;
    /**
     * 业务实体ID
     */
    @ApiModelProperty("业务实体ID")
    private Long orgOuId;
    /**
     * 业务实体编码
     */
    @ApiModelProperty("业务实体编码")
    private String orgOuCode;

    @ApiModelProperty("业务实体名称")
    private String orgOuName;
    /**
     * 交流类型
     */
    @ApiModelProperty("交流类型")
    private String technicalExchangeType;
    /**
     * 预计开始时间
     */
    @ApiModelProperty("预计开始时间")
    private Date technicalExchangeStartTime;
    /**
     * 预计结束时间
     */
    @ApiModelProperty("预计结束时间")
    private Date technicalExchangeEndTime;
    /**
     * 采购方联系人
     */
    @ApiModelProperty("采购方联系人")
    private String linkMan;
    /**
     * 采购方联系电话
     */
    @ApiModelProperty("采购方联系电话")
    private String phone;
    /**
     * 采购方联系邮箱
     */
    @ApiModelProperty("采购方联系邮箱")
    private String email;
    /**
     * 单据状态
     */
    @ApiModelProperty("单据状态：拟定/已发布/已结束/已取消")
    private String technicalExchangeFormStatus;
    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    private Date creationDate;

    @ApiModelProperty("发布时间")
    private Date technicalExchangeReleaseTime;

    @ApiModelProperty(value = "创建人ID")
    private Long createdId;

    @ApiModelProperty(value = "创建人")
    private String createdBy;

    @ApiModelProperty(value = "创建人姓名")
    private String createdFullName;

    @ApiModelProperty(value = "采购商-技术要求附件")
    private List<SceneFile> tecExcFiles;

    @ApiModelProperty(value = "采购商-技术要求-物料需求")
    private List<SouTechnicalExchangeItemDTO> tecExcMaterialItems;

    @ApiModelProperty(value = "采购商-技术要求-邀请的供应商")
    private List<SouTechnicalExchangeVendor> tecExcVendors;

    @ApiModelProperty("反馈状态")
    private String feedbackStatus;

    @ApiModelProperty("反馈比例")
    private String feedbackRatio;

    /**
     * 技术交流-供应商反馈ID
     */
    @ApiModelProperty("技术交流-供应商反馈ID")
    private Long technicalExchangeFeedbackId;
}
