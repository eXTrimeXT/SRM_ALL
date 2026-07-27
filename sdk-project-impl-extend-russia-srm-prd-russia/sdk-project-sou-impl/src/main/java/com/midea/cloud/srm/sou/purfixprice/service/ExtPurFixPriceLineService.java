package com.midea.cloud.srm.sou.purfixprice.service;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.model.sou.designplans.entity.SccSouChDemandAgreement;

/**
 * @description:
 * @author: 100014337
 * @create: 2023-12-22 16:59
 * @version 1.0
 **/
public interface ExtPurFixPriceLineService {
    /**
     * 根据提报策划新增协议明细
     * @date: 2023/12/22
     * @param obj
     * @return
     **/
    PageInfo<SccSouChDemandAgreement> getAgreement(SccSouChDemandAgreement obj);
}
