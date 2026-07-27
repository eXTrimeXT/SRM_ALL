package com.midea.cloud.srm.model.pj.sou.bid.vo.webapi.process;

import com.github.pagehelper.Page;
import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.openapi.bid.vo.process.ApiBidSouProcessConfigVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.process.ApiSouProcessConfigVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 招投标 - 流程配置
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BidSouProcessConfigWebVO extends ApiBidSouProcessConfigVO {

    public static BidSouProcessConfigWebVO convertBidVO(ApiSouProcessConfigVO souProcessConfig,
                                                        BidSouProcessConfig bidSouProcessConfig) {
        BidSouProcessConfigWebVO vo = new BidSouProcessConfigWebVO();
        BeanUtils.copyProperties(souProcessConfig, vo);
        BeanUtils.copyProperties(bidSouProcessConfig, vo);
        return vo;
    }

    @SuppressWarnings("rawtypes")
    public static List<BidSouProcessConfigWebVO> convertBidVO(List<ApiSouProcessConfigVO> souProcessConfigList,
                                                              List<BidSouProcessConfig> bidProcessConfigList) {
        if (souProcessConfigList.isEmpty()) { return Collections.emptyList(); }

        Map<Long/* processConfigId */, BidSouProcessConfig> bidProcessConfigMap = bidProcessConfigList.stream()
                .collect(Collectors.toMap(BidSouProcessConfig::getProcessConfigId, Function.identity()));

        List<BidSouProcessConfigWebVO> voList;
        if (souProcessConfigList instanceof Page) {
            voList = new Page<>();
            ((Page)voList).setTotal(((Page)souProcessConfigList).getTotal());
            ((Page)voList).setPageNum(((Page)souProcessConfigList).getPageNum());
            ((Page)voList).setPageSize(((Page)souProcessConfigList).getPageSize());
        } else {
            voList = new ArrayList<>(souProcessConfigList.size());
        }

        for (ApiSouProcessConfigVO processConfig : souProcessConfigList) {
            voList.add(convertBidVO(processConfig, bidProcessConfigMap.get(processConfig.getProcessConfigId())));
        }
        return voList;
    }

}
