package com.midea.cloud.srm.sou.fixprice.plugin.event.edit;

import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementLine;
import com.midea.cloud.srm.model.sou.fixprice.dto.ExtFixPriceHeadDTO;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceFile;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceHead;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceLine;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouItem;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouOrderItem;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouVendor;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.*;

/**
 * @author 100014337
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ExtFixPriceEditContext extends BaseObjectX {

    @ApiModelProperty("入参: 编辑信息")
    private ExtFixPriceHeadDTO param;

    @Nullable
    @ApiModelProperty("现有的定价单")
    private ExtFixPriceHead existExtFixPriceHead;
    @ApiModelProperty("现有的定价明细")
    private List<ExtFixPriceLine> existExtFixPriceLineList;

    @ApiModelProperty("询价单报价明细")
    private Map<Long, SouOrderItem> orderItemMap = Collections.emptyMap();
    @ApiModelProperty("询价单报价明细")
    private Map<Long, InqSouOrderItem> inqOrderItemMap = Collections.emptyMap();
    @ApiModelProperty("询价单报价物料")
    private Map<Long, SouItem> souItemMap = Collections.emptyMap();
    @ApiModelProperty("询价单报价物料")
    private Map<Long, InqSouItem> inqSouItemMap = Collections.emptyMap();
    @ApiModelProperty("询价单")
    private Map<Long, SouProject> souProjectMap = Collections.emptyMap();
    @ApiModelProperty("询价单")
    private Map<Long, InqSouProject> inqProjectMap = Collections.emptyMap();
    @ApiModelProperty("询比价供应商")
    private Map<String, SouVendor> souVendorMap = Collections.emptyMap();
    @ApiModelProperty("询比价物料需求与采购需求明细的绑定关系")
    private Map<Long, Set<Long>> orderItemReqLineMap = Collections.emptyMap();
    @ApiModelProperty("采购申请明细")
    private Map<Long, RequirementLine> reqLineMap = Collections.emptyMap();
    @ApiModelProperty("采购申请")
    private Map<Long, RequirementHead> reqHeadMap = Collections.emptyMap();

    @ApiModelProperty("定价单实体")
    private ExtFixPriceHead fixPriceHeadEntity;
    @ApiModelProperty("物料明细实体")
    private List<ExtFixPriceLine> fixPriceLineEntityList = new ArrayList<>(32);
    @ApiModelProperty("附件实体")
    private List<ExtFixPriceFile> fixPriceFileEntityList;

    public ExtFixPriceEditContext(ExtFixPriceHeadDTO param) {
        this.param = param;
    }

}
