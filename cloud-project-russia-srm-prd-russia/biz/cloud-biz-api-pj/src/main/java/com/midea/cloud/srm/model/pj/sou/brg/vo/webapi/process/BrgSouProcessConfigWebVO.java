package com.midea.cloud.srm.model.pj.sou.brg.vo.webapi.process;

import com.github.pagehelper.Page;
import com.midea.cloud.srm.model.pj.sou.brg.entity.BrgSouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.openapi.brg.vo.process.ApiBrgSouProcessConfigVO;
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
 * 项目式询价 - 流程配置
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BrgSouProcessConfigWebVO extends ApiBrgSouProcessConfigVO {

    public static BrgSouProcessConfigWebVO convertBrgVO(ApiSouProcessConfigVO souProcessConfig,
                                                        BrgSouProcessConfig brgSouProcessConfig) {
        BrgSouProcessConfigWebVO vo = new BrgSouProcessConfigWebVO();
        BeanUtils.copyProperties(souProcessConfig, vo);
        BeanUtils.copyProperties(brgSouProcessConfig, vo);
        return vo;
    }

    @SuppressWarnings("rawtypes")
    public static List<BrgSouProcessConfigWebVO> convertBrgVO(List<ApiSouProcessConfigVO> souProcessConfigList,
                                                              List<BrgSouProcessConfig> brgProcessConfigList) {
        if (souProcessConfigList.isEmpty()) { return Collections.emptyList(); }

        Map<Long/* processConfigId */, BrgSouProcessConfig> brgProcessConfigMap = brgProcessConfigList.stream()
                .collect(Collectors.toMap(BrgSouProcessConfig::getProcessConfigId, Function.identity()));

        List<BrgSouProcessConfigWebVO> voList;
        if (souProcessConfigList instanceof Page) {
            voList = new Page<>();
            ((Page)voList).setTotal(((Page)souProcessConfigList).getTotal());
            ((Page)voList).setPageNum(((Page)souProcessConfigList).getPageNum());
            ((Page)voList).setPageSize(((Page)souProcessConfigList).getPageSize());
        } else {
            voList = new ArrayList<>(souProcessConfigList.size());
        }

        for (ApiSouProcessConfigVO processConfig : souProcessConfigList) {
            voList.add(convertBrgVO(processConfig, brgProcessConfigMap.get(processConfig.getProcessConfigId())));
        }
        return voList;
    }

}
