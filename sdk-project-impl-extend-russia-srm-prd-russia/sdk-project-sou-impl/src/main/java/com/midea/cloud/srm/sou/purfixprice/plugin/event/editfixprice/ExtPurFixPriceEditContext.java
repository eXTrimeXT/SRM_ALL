package com.midea.cloud.srm.sou.purfixprice.plugin.event.editfixprice;

import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouProject;
import com.midea.cloud.srm.model.sou.purfixprice.dto.ExtPurFixPriceEditDTO;
import com.midea.cloud.srm.model.sou.purfixprice.entity.ExtPurFixPriceFile;
import com.midea.cloud.srm.model.sou.purfixprice.entity.ExtPurFixPriceHead;
import com.midea.cloud.srm.model.sou.purfixprice.entity.ExtPurFixPriceLine;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ExtPurFixPriceEditContext extends BaseObjectX {

    @ApiModelProperty("入参")
    private ExtPurFixPriceEditDTO param;

    @Nullable
    @ApiModelProperty("现有的定价单")
    private ExtPurFixPriceHead existFixPriceHead;

    @ApiModelProperty("待保存的定价实体")
    private ExtPurFixPriceHead priceHeadEntity;
    @ApiModelProperty("待保存的定价明细实体")
    private List<ExtPurFixPriceLine> priceLineListEntity = new ArrayList<>(16);
    @ApiModelProperty("待保存的定价附件实体")
    private List<ExtPurFixPriceFile> priceFileListEntity = new ArrayList<>(16);

    public ExtPurFixPriceEditContext(ExtPurFixPriceEditDTO param) {
        this.param = param;
    }

}
