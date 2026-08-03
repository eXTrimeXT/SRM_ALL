package com.midea.cloud.srm.biz.pj.sourcepubconfig.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.biz.pj.sourcepubconfig.mapper.SccPjSourcePubconfigMapper;
import com.midea.cloud.srm.biz.pj.sourcepubconfig.service.ISccPjSourcePubconfigService;
import com.midea.cloud.srm.biz.pj.sourcepubconfigver.service.ISccPjSourcePubconfigVerService;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.pj.enums.DocRulerEnum;
import com.midea.cloud.srm.model.pj.enums.SourcePubconfigStatusEnum;
import com.midea.cloud.srm.model.pj.sourcepubconfig.entity.SccPjSourcePubconfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @author huangbf3
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ISccPjSourcePubconfigServiceImpl extends ServiceImpl<SccPjSourcePubconfigMapper, SccPjSourcePubconfig> implements ISccPjSourcePubconfigService {
    @Autowired
    private ISccPjSourcePubconfigVerService iSccPjSourcePubconfigVerService;
    @Autowired
    private BaseClient baseClient;

/**    保存  */
    private static final String TYPE_SAVE = "SAVE";

    /**
     * 保存接口
     * @param sourcePubconfig
     * @return
     */
    @Override
    public SccPjSourcePubconfig savePubconfig(SccPjSourcePubconfig sourcePubconfig, String type) {
        String status = TYPE_SAVE.equals(type) ? SourcePubconfigStatusEnum.DRAFT.getCode() : SourcePubconfigStatusEnum.VALID.getCode();
        if(SourcePubconfigStatusEnum.DRAFT.getCode().equals(status) && !SourcePubconfigStatusEnum.DRAFT.getCode().equals(sourcePubconfig.getStatus())) {
//            失效状态不更新状态
            status = sourcePubconfig.getStatus();
        }
        sourcePubconfig.setStatus(status);
        if(Objects.isNull(sourcePubconfig.getPubconfigId())) {
            sourcePubconfig.setPubconfigId(IdGenrator.generate());
            sourcePubconfig.setConfigNumber(baseClient.seqGenForAnon(DocRulerEnum.SOURCE_PUBCONFIG.getCode()));
            /*设置版本号 */
            sourcePubconfig.setConfigVer(0L);
            this.save(sourcePubconfig);
        } else {
            this.updateById(sourcePubconfig);
        }
        /*保存版本关联关系 */
        iSccPjSourcePubconfigVerService.saveSccPjSourcePubconfigVer(sourcePubconfig);
        return sourcePubconfig;
    }

    @Override
    public void delPubconfig(List<Long> pubconfigIdList) {
        if(CollectionUtils.isEmpty(pubconfigIdList)) {
            throw new BaseException("请求参数不能为空");
        }
        LambdaQueryWrapper<SccPjSourcePubconfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SccPjSourcePubconfig::getPubconfigId, pubconfigIdList);
        List<SccPjSourcePubconfig> sccPjSourcePubconfigList = this.list(queryWrapper);

        if(CollectionUtils.isEmpty(sccPjSourcePubconfigList)) {
            return;
        }

        StringBuffer errorBuffer = new StringBuffer();
        sccPjSourcePubconfigList.stream().forEach(sourcePubconfig -> {
            if(SourcePubconfigStatusEnum.VALID.getCode().equals(sourcePubconfig.getStatus())) {
                errorBuffer.append("寻源公示模板名称：").append(sourcePubconfig.getPubconfigName()).append(" 为生效状态不允许删除；");
            }
        });
        if(errorBuffer.length() > 0) {
            throw new BaseException(errorBuffer.toString());
        }
        this.removeByIds(pubconfigIdList);
    }

    @Override
    public void invalidPubconfig(List<Long> pubconfigIdList) {
        if(CollectionUtils.isEmpty(pubconfigIdList)) {
            throw new BaseException("请求参数不能为空");
        }
        LambdaQueryWrapper<SccPjSourcePubconfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SccPjSourcePubconfig::getPubconfigId, pubconfigIdList);
        List<SccPjSourcePubconfig> sccPjSourcePubconfigList = this.list(queryWrapper);

        if(CollectionUtils.isEmpty(sccPjSourcePubconfigList)) {
            return;
        }

        StringBuffer errorBuffer = new StringBuffer();
        sccPjSourcePubconfigList.stream().forEach(sourcePubconfig -> {
            if(!SourcePubconfigStatusEnum.VALID.getCode().equals(sourcePubconfig.getStatus())) {
                errorBuffer.append("寻源公示模板名称：").append(sourcePubconfig.getPubconfigName()).append(" 非生效状态不允许失效；");
            }
        });
        if(errorBuffer.length() > 0) {
            throw new BaseException(errorBuffer.toString());
        }

        /*失效 */
        sccPjSourcePubconfigList.stream().forEach(sourcePubconfig -> {
            sourcePubconfig.setStatus(SourcePubconfigStatusEnum.INVALID.getCode());
        });
        this.updateBatchById(sccPjSourcePubconfigList);

        /*生成一条失效数据，保留原有生效数据，版本号加一 */
        sccPjSourcePubconfigList.stream().forEach(sourcePubconfig -> {
            sourcePubconfig.setPubconfigId(IdGenrator.generate());
            sourcePubconfig.setConfigVer(sourcePubconfig.getConfigVer() + 1);
            sourcePubconfig.setStatus(SourcePubconfigStatusEnum.INVALID.getCode());
        });
        this.saveBatch(sccPjSourcePubconfigList);
        /*生成关联关系 */
        iSccPjSourcePubconfigVerService.saveSccPjSourcePubconfigVerBatch(sccPjSourcePubconfigList);
    }

    @Override
    public void validPubconfig(List<Long> pubconfigIdList) {
        if(CollectionUtils.isEmpty(pubconfigIdList)) {
            throw new BaseException("请求参数不能为空");
        }
        LambdaQueryWrapper<SccPjSourcePubconfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SccPjSourcePubconfig::getPubconfigId, pubconfigIdList);
        List<SccPjSourcePubconfig> sccPjSourcePubconfigList = this.list(queryWrapper);

        if(CollectionUtils.isEmpty(sccPjSourcePubconfigList)) {
            return;
        }

        StringBuffer errorBuffer = new StringBuffer();
        sccPjSourcePubconfigList.stream().forEach(sourcePubconfig -> {
            if(SourcePubconfigStatusEnum.VALID.getCode().equals(sourcePubconfig.getStatus())) {
                errorBuffer.append("寻源公示模板名称：").append(sourcePubconfig.getPubconfigName()).append(" 已经是生效状态不允许重复操作；");
            }
        });
        if(errorBuffer.length() > 0) {
            throw new BaseException(errorBuffer.toString());
        }

        /*状态更新为生效 */
        sccPjSourcePubconfigList.stream().forEach(sourcePubconfig -> {
            sourcePubconfig.setStatus(SourcePubconfigStatusEnum.VALID.getCode());
        });

        this.updateBatchById(sccPjSourcePubconfigList);

    }

    @Override
    public PageInfo<SccPjSourcePubconfig> queryPage(SccPjSourcePubconfig sourcePubconfig) {
        PageUtil.startPage(sourcePubconfig.getPageNum(), sourcePubconfig.getPageSize());
        QueryWrapper<SccPjSourcePubconfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("c.PUBCONFIG_ID");
        queryWrapper.like(StringUtils.isNotBlank(sourcePubconfig.getPubconfigName()), "c.PUBCONFIG_NAME", sourcePubconfig.getPubconfigName());
        queryWrapper.eq(StringUtils.isNotBlank(sourcePubconfig.getStatus()), "c.`STATUS`", sourcePubconfig.getStatus());

        List<SccPjSourcePubconfig> sccPjSourcePubconfigList = this.getBaseMapper().queryPage(queryWrapper);
        return new PageInfo<>(sccPjSourcePubconfigList);
    }

    @Override
    public SccPjSourcePubconfig queryPubconfig(Long pubconfigId) {
        return this.getBaseMapper().selectById(pubconfigId);
    }
}
