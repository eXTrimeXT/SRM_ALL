package com.midea.cloud.srm.biz.pj.scoreconfig.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.biz.pj.scoreconfig.mapper.SccPjSouScoreConfigMapper;
import com.midea.cloud.srm.biz.pj.scoreconfig.service.ISccPjSouScoreConfigService;
import com.midea.cloud.srm.biz.pj.scoreconfigdetail.service.ISccPjSouScoreConfigDetailService;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.pj.enums.DocRulerEnum;
import com.midea.cloud.srm.model.pj.enums.SouScoreConfigItemEnum;
import com.midea.cloud.srm.model.pj.enums.SourcePubconfigStatusEnum;
import com.midea.cloud.srm.model.pj.scoreconfig.dto.SccPjSouScoreConfigDto;
import com.midea.cloud.srm.model.pj.scoreconfig.entity.SccPjSouScoreConfig;
import com.midea.cloud.srm.model.pj.scoreconfigdetails.entity.SccPjSouScoreConfigDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author panmq
 * @description
 * @date 2023-09-21
 */
@Slf4j
@Service
public class ISccPjSouScoreConfigServiceImpl extends ServiceImpl<SccPjSouScoreConfigMapper, SccPjSouScoreConfig> implements ISccPjSouScoreConfigService {

    private static final String TYPE_SAVE = "SAVE";

/**    100满分制 */
    private static final Integer FULL_SCORE = 100;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private ISccPjSouScoreConfigDetailService iSccPjSouScoreConfigDetailService;

    @Override
    public PageInfo<SccPjSouScoreConfig> queryPage(SccPjSouScoreConfig sccPjSouScoreConfig) {

        PageUtil.startPage(sccPjSouScoreConfig.getPageNum(), sccPjSouScoreConfig.getPageSize());
        LambdaQueryWrapper<SccPjSouScoreConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(sccPjSouScoreConfig.getScoreTempName()), SccPjSouScoreConfig::getScoreTempName, sccPjSouScoreConfig.getScoreTempName());
        queryWrapper.eq(StringUtils.isNotBlank(sccPjSouScoreConfig.getStatus()), SccPjSouScoreConfig::getStatus, sccPjSouScoreConfig.getStatus());

        queryWrapper.orderByDesc(SccPjSouScoreConfig::getScoreConfigId);
        return new PageInfo<>(this.list(queryWrapper));
    }

    @Override
    public SccPjSouScoreConfigDto saveScoreConfig(SccPjSouScoreConfigDto sccPjSouScoreConfigDto) {
        SccPjSouScoreConfig souScoreConfig = sccPjSouScoreConfigDto.getSouScoreConfig();
        List<SccPjSouScoreConfigDetail> souScoreConfigDetailList = sccPjSouScoreConfigDto.getSouScoreConfigDetailList();
        checkBeforeSave(sccPjSouScoreConfigDto);

        if (Objects.isNull(souScoreConfig.getScoreConfigId())) {
            souScoreConfig.setScoreConfigId(IdGenrator.generate());
            souScoreConfig.setConfigVer(0L);
            souScoreConfig.setConfigNumber(baseClient.seqGenForAnon(DocRulerEnum.SOURCE_SCORE_CONFIG.getCode()));
        }
        if (TYPE_SAVE.equals(sccPjSouScoreConfigDto.getType())) {
            if (!SourcePubconfigStatusEnum.INVALID.getCode().equals(souScoreConfig.getStatus())) {
                souScoreConfig.setStatus(SourcePubconfigStatusEnum.DRAFT.getCode());
            }
        } else {
            souScoreConfig.setStatus(SourcePubconfigStatusEnum.VALID.getCode());
        }

        this.saveOrUpdate(souScoreConfig);

        /*处理行信息 */
        if (CollectionUtils.isNotEmpty(souScoreConfigDetailList)) {
            souScoreConfigDetailList.stream().forEach(sccPjSouScoreConfigDetail -> {
                sccPjSouScoreConfigDetail.setScoreConfigId(souScoreConfig.getScoreConfigId());
                if (Objects.isNull(sccPjSouScoreConfigDetail.getConfigDetailId())) {
                    sccPjSouScoreConfigDetail.setConfigDetailId(IdGenrator.generate());
                }
            });
        }

        /*删除 */
        LambdaQueryWrapper<SccPjSouScoreConfigDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SccPjSouScoreConfigDetail::getScoreConfigId, souScoreConfig.getScoreConfigId());
        queryWrapper.notIn(CollectionUtils.isNotEmpty(souScoreConfigDetailList), SccPjSouScoreConfigDetail::getConfigDetailId, souScoreConfigDetailList.stream().map(SccPjSouScoreConfigDetail::getConfigDetailId).distinct().collect(Collectors.toList()));
        iSccPjSouScoreConfigDetailService.remove(queryWrapper);


        /*保存 */
        if (CollectionUtils.isNotEmpty(souScoreConfigDetailList)) {
            iSccPjSouScoreConfigDetailService.saveOrUpdateBatch(souScoreConfigDetailList);
        }

        return sccPjSouScoreConfigDto;
    }

    /**
     * 保存校验接口，提交时校验技术评审最高分值总和等于100
     *
     * @param souScoreConfigDto
     */
    private void checkBeforeSave(SccPjSouScoreConfigDto souScoreConfigDto) {
        if (TYPE_SAVE.equals(souScoreConfigDto.getType())) {
            return;
        }

        List<String> errorList = new ArrayList<>();
        /*分组 */
        Map<String, List<SccPjSouScoreConfigDetail>> detailGroup = souScoreConfigDto.getSouScoreConfigDetailList().stream().collect(Collectors.groupingBy(SccPjSouScoreConfigDetail::getScoreItem));
        if (detailGroup.containsKey(SouScoreConfigItemEnum.TEH_REVIEW.getCode())) {
            /*maxScore */
            Integer maxScoreSum = detailGroup.get(SouScoreConfigItemEnum.TEH_REVIEW.getCode()).stream().map(s -> toInteger(s.getMaxScore())).reduce(0, Integer::sum);
            if (Integer.compare(maxScoreSum, FULL_SCORE) != 0) {
                errorList.add("技术评审项最高分值之和不等于100，请重新维护数据后再进行操作");
            }
        }
        if (CollectionUtils.isNotEmpty(errorList)) {
            throw new BaseException(errorList.stream().collect(Collectors.joining("; ")));
        }
    }

    private Integer toInteger(String value) {
        try {
            return Integer.valueOf(value);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public SccPjSouScoreConfigDto queryScoreConfig(Long scoreConfigId) {
        SccPjSouScoreConfigDto sccPjSouScoreConfigDto = new SccPjSouScoreConfigDto();
        SccPjSouScoreConfig sccPjSouScoreConfig = this.getById(scoreConfigId);
        LambdaQueryWrapper<SccPjSouScoreConfigDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SccPjSouScoreConfigDetail::getScoreConfigId, scoreConfigId);
        List<SccPjSouScoreConfigDetail> souScoreConfigDetailList = iSccPjSouScoreConfigDetailService.list(queryWrapper);
        sccPjSouScoreConfigDto.setSouScoreConfig(sccPjSouScoreConfig);
        sccPjSouScoreConfigDto.setSouScoreConfigDetailList(souScoreConfigDetailList);
        return sccPjSouScoreConfigDto;
    }

    @Override
    public void invalidScoreConfig(List<Long> scoreConfigIdList) {
        if (CollectionUtils.isEmpty(scoreConfigIdList)) {
            return;
        }
        LambdaQueryWrapper<SccPjSouScoreConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SccPjSouScoreConfig::getScoreConfigId, scoreConfigIdList);
        List<SccPjSouScoreConfig> sccPjSouScoreConfigList = this.list(queryWrapper);

        if (CollectionUtils.isEmpty(sccPjSouScoreConfigList)) {
            return;
        }

        List<String> errorList = new ArrayList<>();
        sccPjSouScoreConfigList.stream().forEach(sccPjSouScoreConfig -> {
            if (!SourcePubconfigStatusEnum.VALID.getCode().equals(sccPjSouScoreConfig.getStatus())) {
                errorList.add(sccPjSouScoreConfig.getScoreTempName());
            }
            sccPjSouScoreConfig.setStatus(SourcePubconfigStatusEnum.INVALID.getCode());
        });
        if (CollectionUtils.isNotEmpty(errorList)) {
            throw new BaseException("以下模板非生效状态，请核对数据后再重新操作：" + errorList.stream().collect(Collectors.joining("、")));
        }

        this.updateBatchById(sccPjSouScoreConfigList);
    }

    @Override
    public void delScoreConfig(List<Long> scoreConfigIdList) {
        if (CollectionUtils.isEmpty(scoreConfigIdList)) {
            return;
        }
        LambdaQueryWrapper<SccPjSouScoreConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SccPjSouScoreConfig::getScoreConfigId, scoreConfigIdList);
        List<SccPjSouScoreConfig> sccPjSouScoreConfigList = this.list(queryWrapper);

        if (CollectionUtils.isEmpty(sccPjSouScoreConfigList)) {
            return;
        }

        List<String> errorList = new ArrayList<>();
        sccPjSouScoreConfigList.stream().forEach(sccPjSouScoreConfig -> {
            if (SourcePubconfigStatusEnum.VALID.getCode().equals(sccPjSouScoreConfig.getStatus())) {
                errorList.add(sccPjSouScoreConfig.getScoreTempName());
            }
        });
        if (CollectionUtils.isNotEmpty(errorList)) {
            throw new BaseException("以下模板为生效状态，不允许删除，请核对数据后再重新操作：" + errorList.stream().collect(Collectors.joining("、")));
        }

        this.removeByIds(scoreConfigIdList);
    }

    @Override
    public List<SccPjSouScoreConfig> listValidScoreConfig() {
        LambdaQueryWrapper<SccPjSouScoreConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SccPjSouScoreConfig::getStatus, SourcePubconfigStatusEnum.VALID.getCode());
        queryWrapper.orderByDesc(SccPjSouScoreConfig::getScoreConfigId);

        List<SccPjSouScoreConfig> configs = this.list(queryWrapper);
        return configs;
    }
}

