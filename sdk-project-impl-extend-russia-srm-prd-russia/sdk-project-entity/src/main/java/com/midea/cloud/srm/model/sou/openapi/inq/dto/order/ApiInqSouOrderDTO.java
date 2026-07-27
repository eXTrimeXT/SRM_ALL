package com.midea.cloud.srm.model.sou.openapi.inq.dto.order;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPjInqSouOrder;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiSouOrderEditDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiSouOrderFileDTO;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiInqSouOrderDTO extends ApiSouOrderEditDTO {

    @ApiModelProperty("报价明细")
    protected List<ApiInqSouOrderItemDTO> orderItemList;
    @ApiModelProperty("报价附件")
    @Nullable
    protected List<ApiSouOrderFileDTO> orderFileList;
    /** @see SouOrder#getOrderNo */
    @ApiModelProperty("用于生成报价单号-参考SequenceCodeConstant")
    protected String orderNoGenerateCode;
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("true-暂存/false-提交")
    protected Boolean isTempSave = true;

    // ------------------ 长城询比价报价单额外字段 ------------------------
    /** @see ExtPjInqSouOrder#getPriceActiveDay */
    @ApiModelProperty("报价有效期(自然日)")
    private BigDecimal priceActiveDay;

    /** @see ExtPjInqSouOrder#getExtOrderByNickname */
    @ApiModelProperty("报价人")
    private String extOrderByNickname;

    /** @see ExtPjInqSouOrder#getExtOrderPhone */
    @ApiModelProperty("报价联系方式")
    private String extOrderPhone;

}
