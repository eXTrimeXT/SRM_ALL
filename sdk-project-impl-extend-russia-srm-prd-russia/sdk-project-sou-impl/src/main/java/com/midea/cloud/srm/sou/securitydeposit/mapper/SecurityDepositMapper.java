package com.midea.cloud.srm.sou.securitydeposit.mapper;

import com.midea.cloud.srm.model.sou.sourcing.dto.SecurityDepositDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/24
 */
@Mapper
public interface SecurityDepositMapper {
    /**
     * list
     * @param query
     * @return
     */
    List<SecurityDepositDto> list(Map<String,Object> query);
}
