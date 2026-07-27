package com.midea.cloud.srm.supcooperate.mtmapping.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.supcooperate.ext.ExternalMaterial;
import org.apache.ibatis.annotations.Mapper;

/**
* 外部物料与系统物料映射表
*
* @author xiaym13 xiaym13@meicloud.com
* @since 1.0.0 2024-02-26
*/
@Mapper
public interface ExternalMaterialMapper extends BaseMapper<ExternalMaterial> {

}
