package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.swagger.order;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouCurrencyEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouInitDetailVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderDetailVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderFileVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.swagger.init.ApiSouInitDetailSwaggerVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.swagger.order.ApiSouOrderItemSwaggerVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 供应商报价信息查看 (swagger接口专用)
 * PS: 由于目前的寻源结构（ObjectX + SPI），很难描述不同寻源场景对入参的需求差别，
 *     因此用一个专有的类来装所有的信息。
 * PS: 来源于 {@link ApiSouOrderDetailVO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/01/05
 */
@Data
@ApiModel(description = "供应商报价信息查看")
@EqualsAndHashCode(callSuper = true)
public class ApiSouOrderDetailSwaggerVO extends BaseObjectX {

    @ApiModelProperty("寻源立项信息")
    private ApiSouInitDetailSwaggerVO initInfo;
    @ApiModelProperty("指定轮次报价单")
    private SouOrder order;
    @ApiModelProperty("物料需求")
    private List<ApiSouOrderItemSwaggerVO> itemList;
    @ApiModelProperty("报价附件")
    private List<ApiSouOrderFileVO> orderFileList;

}
