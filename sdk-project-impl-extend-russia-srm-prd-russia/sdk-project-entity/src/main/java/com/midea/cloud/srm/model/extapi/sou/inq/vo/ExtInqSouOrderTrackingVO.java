package com.midea.cloud.srm.model.extapi.sou.inq.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPjInqSouOrder;
import com.midea.cloud.srm.model.sou.inq.vo.webapi.select.InqSouOrderTrackingWebVO;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrderFile;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtInqSouOrderTrackingVO extends InqSouOrderTrackingWebVO {

    @ApiModelProperty("报价附件")
    private List<SouOrderFile> orderFileList;

    /** @see ExtPjInqSouOrder#getExtOrderByNickname */
    @ApiModelProperty("报价人")
    private String extOrderByNickname;

    /** @see ExtPjInqSouOrder#getExtOrderPhone */
    @ApiModelProperty("报价联系方式")
    private String extOrderPhone;

    @ApiModelProperty("原因")
    private String reason;

}
