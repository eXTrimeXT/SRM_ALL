package com.midea.cloud.srm.sou.securitydeposit.service;

import com.midea.cloud.srm.model.sou.sourcing.dto.SecurityDepositDto;

import java.util.List;
import java.util.Map;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/24
 */
public interface SecurityDepositService {
    /**
     * list
     * @param query
     * @return
     */
    List<SecurityDepositDto> list(Map<String,Object> query);
}
