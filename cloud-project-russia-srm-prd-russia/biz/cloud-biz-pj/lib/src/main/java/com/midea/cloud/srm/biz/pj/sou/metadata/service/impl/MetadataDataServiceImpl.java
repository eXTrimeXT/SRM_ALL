package com.midea.cloud.srm.biz.pj.sou.metadata.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.biz.pj.sou.metadata.context.MetadataDataContext;
import com.midea.cloud.srm.biz.pj.sou.metadata.enums.ConditionType;
import com.midea.cloud.srm.biz.pj.sou.metadata.enums.JoinType;
import com.midea.cloud.srm.biz.pj.sou.metadata.enums.MetadataType;
import com.midea.cloud.srm.biz.pj.sou.metadata.mapper.MetadataDataMapper;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.dto.MetadataDataDTO;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.dto.MetadataQueryDTO;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.entity.MetadataData;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.vo.MetadataDataVO;
import com.midea.cloud.srm.biz.pj.sou.metadata.service.MetadataDataService;
import com.midea.cloud.srm.biz.pj.sou.metadata.utils.DataUtil;
import com.midea.cloud.srm.biz.pj.sou.metadata.utils.WrapperUtil;
import com.midea.cloud.srm.model.base.metadata.vo.MetadataVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
 *  修改日期: 2022/6/24 16:56
 *  修改内容:
 * </pre>
 */
@Lazy
@Service
public class MetadataDataServiceImpl extends BaseServiceImpl<MetadataDataMapper, MetadataData> implements MetadataDataService {
    @Autowired
    private MetadataDataMapper dataMapper;

    @Override
    public MetadataDataVO getSingle(MetadataQueryDTO queryDto) {
        MetadataVO config = MetadataDataContext.get();
        QueryWrapper<MetadataData> qw = WrapperUtil.buildQueryWrapper(queryDto);
        //固定查询列，可用于匹配聚合函数
        String fixSelect = queryDto.getFixSelect();
        if (StringUtils.isNotEmpty(fixSelect)) {
            qw.select(fixSelect);
        } else {
            qw.select(config.getFieldNames().toArray(new String[0]));
        }
        PageMethod.startPage(1, 1);
        List<MetadataData> entities = dataMapper.selectList(config.getTableName(), qw);
        if (CollectionUtils.isNotEmpty(entities)) {
            return DataUtil.convertEntityToVo(entities.get(0), StringUtils.isNotEmpty(fixSelect));
        }
        return null;
    }

    @Override
    public MetadataDataVO getSingleById(MetadataQueryDTO queryDto) {
        MetadataVO config = MetadataDataContext.get();
        DataUtil.checkPk(config.getTableName());
        Object id = DataUtil.getIdByCheck(queryDto.getActualId());
        queryDto.addCondition(config.getPrimaryKeyAttr(), ConditionType.EQ, id, JoinType.AND);
        return getSingle(queryDto);
    }

    @Override
    public List<MetadataDataVO> getList(MetadataQueryDTO queryDto) {
        MetadataVO config = MetadataDataContext.get();
        QueryWrapper<MetadataData> qw = WrapperUtil.buildQueryWrapper(queryDto);
        //固定查询列，可用于匹配聚合函数
        String fixSelect = queryDto.getFixSelect();
        if (StringUtils.isNotEmpty(fixSelect)) {
            qw.select(fixSelect);
        } else {
            qw.select(config.getFieldNames().toArray(new String[0]));
        }
        List<MetadataData> entities = dataMapper.selectList(config.getTableName(), qw);
        return DataUtil.convertEntitiesToVo(entities, StringUtils.isNotEmpty(fixSelect));
    }

    @Override
    public PageInfo<MetadataDataVO> getListByPage(MetadataQueryDTO queryDto) {
        MetadataVO config = MetadataDataContext.get();
        PageMethod.startPage(queryDto.getPageNum(), queryDto.getPageSize());
        QueryWrapper<MetadataData> qw = WrapperUtil.buildQueryWrapper(queryDto);
        //固定查询列，可用于匹配聚合函数
        String fixSelect = queryDto.getFixSelect();
        if (StringUtils.isNotEmpty(fixSelect)) {
            qw.select(fixSelect);
        } else {
            qw.select(config.getFieldNames().toArray(new String[0]));
        }
        List<MetadataData> entities = dataMapper.selectList(config.getTableName(), qw);
        return DataUtil.convertEntitiesToPage(new PageInfo<>(entities), StringUtils.isNotEmpty(fixSelect));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object add(MetadataDataDTO dataDto) throws Exception {
        MetadataVO config = MetadataDataContext.get();
        MetadataData entity = DataUtil.convertDtoToEntity(dataDto);
        if (null == entity.getId()) {
            entity.setId(IdGenrator.generate());
        }
        if (!MetadataType.EXT.toString().equals(config.getMetadataType())) {
            DataUtil.addCreateAttr(entity);
        }
        dataMapper.insertData(config.getTableName(), entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Object> batchAdd(MetadataDataDTO batchDataDto) throws Exception {
        MetadataVO config = MetadataDataContext.get();
        List<Object> ids = new ArrayList<>();
        List<MetadataData> entities = new ArrayList<>();
        for (MetadataDataDTO dataDto : batchDataDto.getDetails()) {
            MetadataData entity = DataUtil.convertDtoToEntity(dataDto);
            if (null == entity.getId()) {
                entity.setId(IdGenrator.generate());
            }
            if (!MetadataType.EXT.toString().equals(config.getMetadataType())) {
                DataUtil.addCreateAttr(entity);
            }
            entities.add(entity);
        }

        //在一个sqlsession里提交一条多套参数的语句，减少逐条执行的性能消耗
        executeBatch(entities, DEFAULT_BATCH_SIZE, (sqlSession, entity) -> {
            sqlSession.getMapper(MetadataDataMapper.class).insertData(config.getTableName(), entity);
            ids.add(entity.getId());
        });
        return ids;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(MetadataDataDTO dataDto) throws Exception {
        MetadataVO config = MetadataDataContext.get();
        MetadataData entity = DataUtil.convertDtoToEntity(dataDto);
        if (!MetadataType.EXT.toString().equals(config.getMetadataType())) {
            DataUtil.addUpdateAttr(entity);
        }
        UpdateWrapper<MetadataData> qw = WrapperUtil.buildUpdateWrapper(dataDto.getConditions());
        return dataMapper.updateData(config.getTableName(), entity, qw) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(MetadataDataDTO dataDto) throws Exception {
        MetadataVO config = MetadataDataContext.get();
        DataUtil.checkPk(config.getTableName());
        Object id = DataUtil.getIdByCheck(dataDto.getActualId());
        dataDto.addCondition(config.getPrimaryKeyAttr(), ConditionType.EQ, id, JoinType.AND);
        return update(dataDto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateById(MetadataDataDTO batchDataDto) throws Exception {
        MetadataVO config = MetadataDataContext.get();
        DataUtil.checkPk(config.getTableName());
        List<MetadataData> entities = new ArrayList<>();
        for (MetadataDataDTO dataDto : batchDataDto.getDetails()) {
            MetadataData entity = DataUtil.convertDtoToEntity(dataDto);
            DataUtil.getIdByCheck(dataDto.getActualId());
            if (!MetadataType.EXT.toString().equals(config.getMetadataType())) {
                DataUtil.addUpdateAttr(entity);
            }
            entities.add(entity);
        }
        //在一个sqlsession里提交一条多套参数的语句，减少逐条执行的性能消耗
        executeBatch(entities, DEFAULT_BATCH_SIZE, (sqlSession, entity) -> {
            UpdateWrapper<MetadataData> qw = Wrappers.update(entity).eq(config.getPrimaryKeyName(), entity.getId());
            sqlSession.getMapper(MetadataDataMapper.class).updateData(config.getTableName(), entity, qw);
        });
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(MetadataDataDTO dataDto) throws Exception {
        MetadataVO config = MetadataDataContext.get();
        UpdateWrapper<MetadataData> qw = WrapperUtil.buildUpdateWrapper(dataDto.getConditions());
        return dataMapper.deleteData(config.getTableName(), qw) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(MetadataDataDTO dataDto) throws Exception {
        MetadataVO config = MetadataDataContext.get();
        DataUtil.checkPk(config.getTableName());
        Object id = DataUtil.getIdByCheck(dataDto.getActualId());
        dataDto.addCondition(config.getPrimaryKeyAttr(), ConditionType.EQ, id, JoinType.AND);
        return delete(dataDto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteById(MetadataDataDTO batchDataDto) throws Exception {
        MetadataVO config = MetadataDataContext.get();
        DataUtil.checkPk(config.getTableName());
        List<Object> ids = batchDataDto.getActualIds();
        if (CollectionUtils.isNotEmpty(ids)) {
            throw new BaseException("主键值列表不能为空");
        }
        MetadataDataDTO deleteDto = new MetadataDataDTO();
        deleteDto.addCondition(config.getPrimaryKeyAttr(), ConditionType.IN, ids, JoinType.AND);
        UpdateWrapper<MetadataData> qw = WrapperUtil.buildUpdateWrapper(deleteDto.getConditions());
        return dataMapper.deleteData(config.getTableName(), qw) > 0;
    }
}
