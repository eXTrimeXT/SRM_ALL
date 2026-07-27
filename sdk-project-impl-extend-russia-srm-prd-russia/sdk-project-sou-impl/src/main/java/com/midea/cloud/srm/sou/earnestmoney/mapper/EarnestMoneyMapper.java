package com.midea.cloud.srm.sou.earnestmoney.mapper;

import com.midea.cloud.srm.model.sou.sourcing.dto.EarnestMoneyDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/27
 */
@Mapper
public interface EarnestMoneyMapper {
    /**
     * list
     * @param query
     * @return
     */
    List<EarnestMoneyDto> list (Map<String,Object> query);
}
