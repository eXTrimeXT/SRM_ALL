package com.midea.cloud.srm.supcooperate.catalogonshelves.mapper;

import com.midea.cloud.srm.model.supcooperate.ext.catalogonshelvess.dto.OnShelvesDto;
import com.midea.cloud.srm.model.supcooperate.ext.catalogonshelvess.dto.ShopCartModelDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/7/9
 */
@Mapper
public interface ExtShopCartMapper {
    /**
     * getTempList
     * @param list
     * @return
     */
    public List<ShopCartModelDto> getTempList(List<ShopCartModelDto>list);

    /**
     * getList
     * @param orgId
     * @param extAreaCode
     * @param list
     * @return
     */
    public List<ShopCartModelDto>getList(String orgId,String extAreaCode,List<String>list);

    /**
     * extAdd
     * @param query
     * @return
     */
    public OnShelvesDto extAdd(Map<String,Object>query);
}
