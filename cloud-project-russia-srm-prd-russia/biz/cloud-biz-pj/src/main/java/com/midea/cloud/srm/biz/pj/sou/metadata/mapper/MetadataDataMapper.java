package com.midea.cloud.srm.biz.pj.sou.metadata.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.entity.MetadataData;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <pre>
 *
 * </pre>
 *
 * @author huangyq154@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/6/24 14:07
 *  修改内容:
 * </pre>
 */
@Repository
public interface MetadataDataMapper extends BaseMapper<MetadataData> {
    /**
     * 重载默认方法，传入tableName
     *
     * @param tableName
     * @param qw
     * @return
     */
    List<MetadataData> selectList(@Param("tableName") String tableName, @Param(Constants.WRAPPER) Wrapper qw);

    /**
     * 备注
     * @param tableName
     * @param entity
     * @return
     */
    int insertData(@Param("tableName") String tableName, @Param(Constants.ENTITY) MetadataData entity);

    /**
     * 备注
     * @param tableName
     * @param entity
     * @param qw
     * @return
     */
    int updateData(@Param("tableName") String tableName, @Param(Constants.ENTITY) MetadataData entity, @Param(Constants.WRAPPER) Wrapper qw);

    /**
     * 备注
     * @param tableName
     * @param qw
     * @return
     */
    int deleteData(@Param("tableName") String tableName, @Param(Constants.WRAPPER) Wrapper qw);
}
