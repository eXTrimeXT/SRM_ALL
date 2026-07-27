package com.midea.cloud.srm.biz.pj.contract.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.biz.pj.contract.mapper.PJContractSealMapper;
import com.midea.cloud.srm.biz.pj.contract.service.PJContractSealService;
import com.midea.cloud.srm.model.pj.contract.ContractSeal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PJContractSealServiceImpl extends ServiceImpl<PJContractSealMapper, ContractSeal> implements PJContractSealService {
    @Override
    public PageInfo<ContractSeal> listPage(ContractSeal contractSeal) {
        PageUtil.startPage(contractSeal.getPageNum(), contractSeal.getPageSize());
        List<ContractSeal> contractSealList = getContractSealList(contractSeal);
        return new PageInfo<>(contractSealList);
    }

    private List<ContractSeal> getContractSealList(ContractSeal contractSeal) {
        String signCompanyName = contractSeal.getSignCompanyName();
        String sealName = contractSeal.getSealName();
        String sealId = contractSeal.getSealId();
        final List<ContractSeal> list = lambdaQuery()
                .like(StringUtils.isNotEmpty(signCompanyName), ContractSeal::getSignCompanyName, signCompanyName)
                .like(StringUtils.isNotEmpty(sealName), ContractSeal::getSealName, sealName)
                .like(StringUtils.isNotBlank(sealId), ContractSeal::getSealId, sealId)
                .list();
        return list;
    }
}
