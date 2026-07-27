package com.midea.cloud.srm.model.supcooperate.historyprices.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: panmq
 * @Date: 2024/03/28/ $
 * @Description: 历史价格接口接收表Dto实体类
 */
@ApiModel("历史价格接口接收表Dto实体类")
@Data
public class SccScHistoryPriceTempDto extends BaseDTO {

    @ApiModelProperty("主键")
    private Long rowId;
    @ApiModelProperty("物料编码")
    private String materialCode;
    @ApiModelProperty("物料名称")
    private String materialName;
    @ApiModelProperty("区域(名称)")
    private String areaCode;
    @ApiModelProperty("品牌")
    private String brand;
    @ApiModelProperty("物料描述")
    private String materialDescribe;
    @ApiModelProperty("业务实体编码")
    private String orgCode;
    @ApiModelProperty("业务实体名称")
    private String orgName;
    @ApiModelProperty("税率")
    private String taxRate;
    @ApiModelProperty("未税价格")
    private String noTaxPrice;
    @ApiModelProperty("到货周期")
    private Date leadTime;
    @ApiModelProperty("供应商编码")
    private String supCode;
    @ApiModelProperty("供应商名称")
    private String supName;
    @ApiModelProperty("供应商联系方式")
    private String supTel;
    @ApiModelProperty("订单日期")
    private Date orderDate;
    @ApiModelProperty("处理序号")
    private String processSerialNum;
    @ApiModelProperty("处理状态，PENDING：未处理，COMPLETED：处理完成，PROCESSING：处理中，ERROR：处理错误，RETRY：需重试")
    private String processStatus;
    @ApiModelProperty("处理信息")
    private String processMessage;
    @ApiModelProperty("处理时间")
    private Date processDate;
    @ApiModelProperty("处理批次号")
    private Long processGroupId;

}
