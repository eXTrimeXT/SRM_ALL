package com.midea.cloud.srm.model.extapi.sou.inq.vo;

import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtInqSouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtInqSouItemQueryVO extends SouItem {

    /** @see SouProject#getSouNo */
    @ApiModelProperty("询价单号")
    private String souNo;

    @ApiModelProperty("物料报价次数(询比价当前轮次)")
    private Integer orderCount;

    /** @see ExtInqSouItem#getExtMaterialModel */
    @ApiModelProperty("规格型号")
    private String extMaterialModel;

    /** @see SouProject#getCreatedBy */
    @ApiModelProperty("采购员昵称")
    private String buyerUsername;

    /** @see SouProject#getCreatedFullName */
    @ApiModelProperty("采购员名称")
    private String buyerNickName;

    /** @see SouProject#getCreationDate */
    @ApiModelProperty("询比价创建时间")
    private Date souCreationDate;

    /** @see SouProject#getOrderEndTime */
    @ApiModelProperty("询比价报价截止时间")
    private Date orderEndTime;

    /** @see SouProject#getProjectStatus()  */
    @ApiModelProperty("项目状态")
    private String projectStatus;

    @ApiModelProperty("当前轮次")
    private Integer currentRound;

    @ApiModelProperty("轮次")
    private Integer round;

}
