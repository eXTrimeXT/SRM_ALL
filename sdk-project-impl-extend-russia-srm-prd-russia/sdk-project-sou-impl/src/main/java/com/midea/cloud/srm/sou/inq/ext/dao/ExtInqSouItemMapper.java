package com.midea.cloud.srm.sou.inq.ext.dao;

import com.midea.cloud.component.mphelper.mapper.CustomMapper;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtInqSouItem;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.PjInqSouItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/12
 */
@Mapper
public interface ExtInqSouItemMapper extends CustomMapper<PjInqSouItem> {
}
