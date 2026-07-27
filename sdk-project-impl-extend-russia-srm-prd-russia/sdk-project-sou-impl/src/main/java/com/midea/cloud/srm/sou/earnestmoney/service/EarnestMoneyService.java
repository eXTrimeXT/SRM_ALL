package com.midea.cloud.srm.sou.earnestmoney.service;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.model.sou.sourcing.dto.EarnestMoneyDto;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/27
 */
public interface EarnestMoneyService {
    /**
     * list
     * @param query
     * @return
     */
    public List<EarnestMoneyDto> list(Map<String,Object> query);
}
