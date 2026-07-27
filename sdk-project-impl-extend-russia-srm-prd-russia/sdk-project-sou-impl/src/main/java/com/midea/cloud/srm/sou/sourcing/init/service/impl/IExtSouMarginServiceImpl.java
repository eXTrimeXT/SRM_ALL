package com.midea.cloud.srm.sou.sourcing.init.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.enums.SouBidMarginStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouMargin;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouMarginMapper;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouMarginService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @description scc_npm_sou_margin
 * @author panmq
 * @date 2023-10-07
 */
@Slf4j
@Service
public class IExtSouMarginServiceImpl extends ServiceImpl<ExtSouMarginMapper, ExtSouMargin> implements IExtSouMarginService {
    @Override
    public void copyYearMarginInfo(List<ExtSouMargin> souMarginList) {
        if(CollectionUtils.isEmpty(souMarginList)) {
            return;
        }
        List<Long> relYearMarginIdList = new ArrayList<>(16);
        souMarginList.stream().forEach(souMargin -> {
            if(YesOrNo.YES.getValue().equals(souMargin.getYearFlag())) {
                relYearMarginIdList.add(souMargin.getRelYearMarginId());
            }
        });

        if(CollectionUtils.isEmpty(relYearMarginIdList)) {
            return;
        }
        List<ExtSouMargin> yearMarginList = this.listByIds(relYearMarginIdList);
        Map<Long, ExtSouMargin> yearMarginMap = yearMarginList.stream().collect(Collectors.toMap(k -> k.getMarginId(), Function.identity(), (k1, k2) -> k2));
        souMarginList.stream().filter(k -> yearMarginMap.containsKey(k.getRelYearMarginId())).forEach(souMargin -> {
            if(YesOrNo.YES.getValue().equals(souMargin.getYearFlag())) {
                copyMargin(souMargin, yearMarginMap.get(souMargin.getRelYearMarginId()));
            }
        });
    }

    private void copyMargin(ExtSouMargin souMargin, ExtSouMargin yearMargin) {
        /** 需还原的字段 */
        Long projectId = souMargin.getProjectId();
        Long marginId = souMargin.getMarginId();
        Long relYearMarginId = souMargin.getRelYearMarginId();
        String handerMode = souMargin.getHanderMode();
        String causeDesc = souMargin.getCauseDesc();
        String marginStatus = souMargin.getMarginStatus();

        BeanCopyUtil.copyProperties(souMargin, yearMargin);
        souMargin.setProjectId(projectId);
        souMargin.setMarginId(marginId);
        souMargin.setRelYearMarginId(relYearMarginId);


        /** 如果线上年度保证金处于未支付状态时，存在线下处理，则取线下处理的年度保证金状态 */
        if(StringUtils.isNotBlank(handerMode) && !SouBidMarginStatusEnum.PAY.getCode().equals(yearMargin.getMarginStatus())) {
            souMargin.setMarginStatus(marginStatus);
            souMargin.setHanderMode(handerMode);
            souMargin.setCauseDesc(causeDesc);
        }
    }

    @Override
    public Map<Long, ExtSouMargin> queryYearMarginInfo(List<ExtSouMargin> souMarginList) {
        Map<Long, ExtSouMargin> yearMarginMap = new HashMap<>();
        List<Long> marginIdList = new ArrayList<>(15);
        List<ExtSouMargin> extSouMarginList = new ArrayList<>(15);
        ((List<ExtSouMargin>)ObjectUtils.defaultIfNull(souMarginList, new ArrayList<>(15)))
                .stream().filter(f -> YesOrNo.YES.getValue().equals(f.getYearFlag())).forEach(f -> {
                    if(SrmConstant.LONG_MINUS_ONE.compareTo(f.getRelYearMarginId()) == 0) {
                        extSouMarginList.add(f);
                    } else {
                        marginIdList.add(f.getRelYearMarginId());
                    }
                });
        if(CollectionUtils.isNotEmpty(marginIdList)) {
             extSouMarginList.addAll(this.listByIds(marginIdList));
        }
        yearMarginMap = extSouMarginList.stream().collect(Collectors.toMap(k -> k.getMarginId(), Function.identity(), (k1, k2) -> k2));
        return yearMarginMap;
    }
}

