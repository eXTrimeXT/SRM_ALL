package com.midea.cloud.srm.sou.purfixprice.controller;

import cn.hutool.core.util.ObjectUtil;
import com.midea.cloud.srm.model.sou.fixprice.dto.ExtFixPriceContractSignDTO;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceLine;
import com.midea.cloud.srm.model.sou.purfixprice.dto.ExtPurFixPriceContractSignDTO;
import com.midea.cloud.srm.model.sou.purfixprice.entity.ExtPurFixPriceHead;
import com.midea.cloud.srm.model.sou.purfixprice.entity.ExtPurFixPriceLine;
import com.midea.cloud.srm.model.sou.purfixprice.entity.ExtPurFixPriceLineContractDTO;
import com.midea.cloud.srm.sou.fixprice.dao.ExtFixPriceLineDAO;
import com.midea.cloud.srm.sou.purfixprice.dao.ExtPurFixPriceHeadDAO;
import com.midea.cloud.srm.sou.purfixprice.dao.ExtPurFixPriceLineDAO;
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
@RequestMapping("/npm/pur-fix-price/contract")
public class ExtPurFixPriceContractController {

    @Autowired
    private ExtPurFixPriceLineDAO extPurFixPriceLineDAO;

    @Autowired
    private ExtPurFixPriceHeadDAO extPurFixPriceHeadDAO;


    /**
     * 更新合同相关信息
     */
    @PostMapping("/purFixPriceLineList")
    @ApiOperation("通过id查找priceLine")
    public List<ExtPurFixPriceLineContractDTO> purFixPriceLineList(@RequestBody List<String> fixPriceLineIds){
        List<ExtPurFixPriceLine> extPurFixPriceLines =  extPurFixPriceLineDAO.listByIds(fixPriceLineIds);
        List<ExtPurFixPriceLineContractDTO> extPurFixPriceLineContractDTOS = new ArrayList<>();
        if(ObjectUtil.isNotEmpty(extPurFixPriceLines)){
            Long purFixPriceHeadId = extPurFixPriceLines.get(0).getPurFixPriceHeadId();
            ExtPurFixPriceHead head = extPurFixPriceHeadDAO.getById(purFixPriceHeadId);
            if(ObjectUtil.isNotNull(head)){
                extPurFixPriceLines.stream().forEach(e->
                        extPurFixPriceLineContractDTOS.add(ExtPurFixPriceLineContractDTO.makeExtPurFixPriceLineContractDTO(e,head)));
            }
        }
        return extPurFixPriceLineContractDTOS;
    }

    /**
     * 更新合同相关信息
     */
    @PostMapping("/updateContractSign")
    @ApiOperation("更新合同相关信息")
    public void updateContractSign(@RequestBody ExtPurFixPriceContractSignDTO extPurFixPriceContractSignDto) {
        ExtPurFixPriceLine extPurFixPriceLine = BeanUtil.copyProperties(extPurFixPriceContractSignDto,ExtPurFixPriceLine.class);
        extPurFixPriceLineDAO.updateById(extPurFixPriceLine);
    }

    /**
     * 更新合同相关信息
     */
    @PostMapping("/updateCentPurchaseContractSigns")
    @ApiOperation("更新合同相关信息")
    public void updateCentPurchaseContractSigns(@RequestBody List<ExtPurFixPriceContractSignDTO> extPurFixPriceContractSignDtos){
        List<ExtPurFixPriceLine> extPurFixPriceLines = new ArrayList<>();
        for (ExtPurFixPriceContractSignDTO extPurFixPriceLineContractDto:extPurFixPriceContractSignDtos){
            ExtPurFixPriceLine extPurFixPriceLine = BeanUtil.copyProperties(extPurFixPriceLineContractDto,ExtPurFixPriceLine.class);
            extPurFixPriceLines.add(extPurFixPriceLine);
        }
        extPurFixPriceLineDAO.updateBatchById(extPurFixPriceLines);
    }




}
