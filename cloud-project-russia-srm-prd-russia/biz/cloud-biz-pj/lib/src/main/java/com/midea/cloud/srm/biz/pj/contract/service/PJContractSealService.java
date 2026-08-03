package com.midea.cloud.srm.biz.pj.contract.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.model.pj.contract.ContractSeal;

public interface PJContractSealService extends IService<ContractSeal> {
    PageInfo<ContractSeal> listPage(ContractSeal contractSeal);
}
