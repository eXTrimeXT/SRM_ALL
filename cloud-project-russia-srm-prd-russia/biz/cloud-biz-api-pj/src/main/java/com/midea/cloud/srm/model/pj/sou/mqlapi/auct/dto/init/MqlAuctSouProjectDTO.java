package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.dto.init;

import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.dto.init.MqlAuctSouCurrencyDTO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.dto.init.MqlAuctSouItemDTO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.dto.init.MqlAuctSouProjectEditDTO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.init.MqlSouVendorDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouInitDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFileConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouGroup;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 竞价MQL - 立项信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlAuctSouProjectDTO extends MqlAuctSouProjectEditDTO {

    @ApiModelProperty("工作小组")
    private List<SouGroup> groupList;

    @ApiModelProperty("内部/外部查看附件")
    private List<SouFile> fileList;

    @ApiModelProperty("供方必须上传配置")
    private List<SouFileConfig> fileConfigList;

    @ApiModelProperty("可用币种")
    private List<MqlAuctSouCurrencyDTO> currencyList;

    @ApiModelProperty("物料需求")
    private List<MqlAuctSouItemDTO> itemList;

    @ApiModelProperty("邀请供应商信息")
    private List<MqlSouVendorDTO> vendorList;

    @ApiModelProperty(value = "保存步骤", required = true)
    protected ApiSouInitDTO.CreateStep createStep;
    @ApiModelProperty("单据号规则")
    protected String sequenceCode;
    @ApiModelProperty("true-暂存/false-提交")
    protected Boolean tempSave;
    @ApiModelProperty("是否用于复制单据情况(true-基本放通所有校验)")
    protected Boolean copy;
    @ApiModelProperty("当前用户ID(可为空)")
    protected Long currentUserId;

}
