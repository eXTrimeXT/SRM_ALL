package com.midea.cloud.srm.model.pj.sou.openapi.bid.vo.process;

import com.midea.cloud.srm.model.pj.sou.openapi.bid.vo.process.ApiBidSouTempSelectDetailDataVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select.ApiSouTempSelectVO;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *  功能名称
 * </pre>
 *
 * @author haibo1.huang@meiCloud.com
 * @version 1.00.00
 *
 * <pre>
 * 修改记录修改后版本:
 * 修改人:
 * 修改日期: 2023/8/14 15:32、
 * 修改内容:
 * </pre>
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel("招标的模板报价的比价数据模型－分项维度")
public class ApiBidSouTempSelectDataVO extends BaseObjectX {

    @ApiModelProperty("寻源核心 - 寻源单id")
    private Long projectId;
    @ApiModelProperty("寻源核心 - 物料需求行ID")
    private Long souItemId;
    @ApiModelProperty("物料ID")
    private Long itemId;
    @ApiModelProperty("物料编码")
    private String itemCode;
    @ApiModelProperty("物料名称")
    private String itemDesc;
    @ApiModelProperty("需求数量")
    private BigDecimal requireQuantity;
    @ApiModelProperty("轮次")
    private Integer round;
    @ApiModelProperty("属性id")
    private Long attrId;
    @ApiModelProperty("分项名称（报价属性字段的名称）")
    private String quoteFieldName;
    @ApiModelProperty("分项ID（报价属性字段的id）")
    private Long quoteFieldId;
    @ApiModelProperty("分项动态列数据")
    private Map<String, Object> dynamicColMap;
    @ApiModelProperty("分项明细数据")
    private List<ApiBidSouTempSelectDetailDataVO> souTempSelectDetailDataVOList;
    @ApiModelProperty("分项明细动态列")
    private List<ApiSouTempSelectVO.DynamicCol> souTempSelectDetailDynamicColList;
    @ApiModelProperty("异常提示")
    private String errorMessage;
    @ApiModelProperty("拦标价")
    private BigDecimal targetPrice;

}
