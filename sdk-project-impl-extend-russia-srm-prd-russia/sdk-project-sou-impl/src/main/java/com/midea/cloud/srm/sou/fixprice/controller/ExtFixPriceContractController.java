package com.midea.cloud.srm.sou.fixprice.controller;

import com.midea.cloud.srm.model.sou.fixprice.dto.ExtFixPriceContractSignDTO;
import com.midea.cloud.srm.model.sou.fixprice.dto.ExtFixPriceHeadDTO;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceLine;
import com.midea.cloud.srm.sou.fixprice.dao.ExtFixPriceLineDAO;
import com.mideacloud.common.util.BeanUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 100014336
 */
@RestController
@RequestMapping("/npm/fix-price/contract")
public class ExtFixPriceContractController {

    @Autowired
    private ExtFixPriceLineDAO extFixPriceLineDAO;

    /**
     * 更新合同相关信息
     */
    @PostMapping("/updateContractSign")
    @ApiOperation("更新合同相关信息")
    public void updateContractSign(@RequestBody ExtFixPriceContractSignDTO extFixPriceContractSignDTO) {
        ExtFixPriceLine extFixPriceLine = BeanUtil.copyProperties(extFixPriceContractSignDTO,ExtFixPriceLine.class);
        extFixPriceLineDAO.updateById(extFixPriceLine);
    }

    /**
     * 批量更新合同相关信息
     */
    @PostMapping("/updateTempProcureContractSigns")
    @ApiOperation("批量更新合同相关信息")
    public void updateTempProcureContractSigns(@RequestBody List<ExtFixPriceContractSignDTO> extFixPriceContractSignDTOs) {
        List<ExtFixPriceLine> extFixPriceLines = new ArrayList<>();
        for (ExtFixPriceContractSignDTO extFixPriceContractSignDTO:extFixPriceContractSignDTOs){
            ExtFixPriceLine extFixPriceLine = BeanUtil.copyProperties(extFixPriceContractSignDTO,ExtFixPriceLine.class);
            extFixPriceLines.add(extFixPriceLine);
        }
        extFixPriceLineDAO.updateBatchById(extFixPriceLines);
    }

    /**
     *
     */
    @PostMapping("/fixPriceLineList")
    @ApiOperation("通过id查找priceLine")
    public List<ExtFixPriceLine> fixPriceLineList(@RequestBody List<String> fixPriceLineIds){
        return extFixPriceLineDAO.listByIds(fixPriceLineIds);
    }

}
