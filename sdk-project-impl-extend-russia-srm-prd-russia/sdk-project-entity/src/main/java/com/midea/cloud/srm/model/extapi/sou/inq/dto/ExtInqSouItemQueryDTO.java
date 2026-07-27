package com.midea.cloud.srm.model.extapi.sou.inq.dto;

import com.midea.cloud.srm.model.common.BasePage;
import com.midea.cloud.srm.model.extapi.sou.inq.vo.ExtInqSouItemQueryVO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouProjectQueryDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtInqSouItemQueryDTO extends BasePage {

    /** @see ExtInqSouItemQueryVO#getSouNo */
    @ApiModelProperty("询价单号(模糊查询)")
    private String souNo;

    /** @see ExtInqSouItemQueryVO#getBuyerUsername */
    @ApiModelProperty("创建人账号(等值查询)")
    private String buyerUsername;

    /** @see ExtInqSouItemQueryVO#getItemCode */
    @ApiModelProperty("物料编码(模糊查询)")
    private String itemCode;

    /** @see ExtInqSouItemQueryVO#getItemDesc */
    @ApiModelProperty("物料名称(模糊查询)")
    private String itemDesc;

    /** @see ExtInqSouItemQueryVO#getExtMaterialModel */
    @ApiModelProperty("规格型号(模糊查询)")
    private String extMaterialModel;

    /** @see ExtInqSouItemQueryVO#getSouCreationDate */
    @ApiModelProperty("询价创建时间范围")
    private Date souCreationDateFrom;
    private Date souCreationDateTo;

    /** @see ExtInqSouItemQueryVO#getOrderEndTime */
    @ApiModelProperty("询价报价截止时间范围")
    private Date orderEndTimeFrom;
    private Date orderEndTimeTo;

    /** @see ExtInqSouItemQueryVO#getOrderCount */
    @ApiModelProperty("报价次数")
    private Integer orderCount;

    /** @see ExtInqSouItemQueryVO#getProjectStatus()  */
    @ApiModelProperty("项目状态")
    private String projectStatus;

    /** 入参格式化 */
    public void formatParams() {
        souNo = StringUtils.trimToNull(souNo);
        buyerUsername = StringUtils.trimToNull(buyerUsername);
        itemCode = StringUtils.trimToNull(itemCode);
        itemDesc = StringUtils.trimToNull(itemDesc);
        extMaterialModel = StringUtils.trimToNull(extMaterialModel);
        if (souCreationDateFrom != null) {
            souCreationDateFrom = ApiExtSouProjectQueryDTO.getStartTimeOfDate(souCreationDateFrom);
        }
        if (souCreationDateTo != null) {
            souCreationDateTo = ApiExtSouProjectQueryDTO.getEndTimeOfDay(souCreationDateTo);
        }
        if (orderEndTimeFrom != null) {
            orderEndTimeFrom = ApiExtSouProjectQueryDTO.getStartTimeOfDate(orderEndTimeFrom);
        }
        if (orderEndTimeTo != null) {
            orderEndTimeTo = ApiExtSouProjectQueryDTO.getEndTimeOfDay(orderEndTimeTo);
        }
    }

}
