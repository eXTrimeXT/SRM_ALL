package com.midea.cloud.srm.model.extapi.sou.purinq.vo.select;

import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouOrder;
import com.midea.cloud.srm.model.sou.inq.vo.webapi.select.InqSouOrderTrackingWebVO;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrderFile;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author 100014337
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtPurInqSouOrderTrackingVO extends InqSouOrderTrackingWebVO {

    @ApiModelProperty("报价附件")
    private List<SouOrderFile> orderFileList;

    /** @see ExtPurInqSouOrder#getOrderByNickname */
    @ApiModelProperty("报价人")
    private String orderByNickname;

    /** @see ExtPurInqSouOrder#getOrderPhone */
    @ApiModelProperty("报价联系方式")
    private String orderPhone;

}