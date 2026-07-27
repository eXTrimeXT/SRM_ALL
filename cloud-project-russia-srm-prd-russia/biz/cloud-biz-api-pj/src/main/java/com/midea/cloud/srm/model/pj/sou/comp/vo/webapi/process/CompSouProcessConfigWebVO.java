package com.midea.cloud.srm.model.pj.sou.comp.vo.webapi.process;

import com.github.pagehelper.Page;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.process.ApiCompSouProcessConfigVO;
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
 * 竞价 - 流程配置查询
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CompSouProcessConfigWebVO extends ApiCompSouProcessConfigVO {

    public static CompSouProcessConfigWebVO convertCompVO(ApiSouProcessConfigVO souProcessConfig,
                                                          CompSouProcessConfig compSouProcessConfig) {
        CompSouProcessConfigWebVO vo = new CompSouProcessConfigWebVO();
        BeanUtils.copyProperties(souProcessConfig, vo);
        BeanUtils.copyProperties(compSouProcessConfig, vo);
        return vo;
    }

    @SuppressWarnings("rawtypes")
    public static List<CompSouProcessConfigWebVO> convertCompVO(List<ApiSouProcessConfigVO> souProcessConfigList,
                                                               List<CompSouProcessConfig> compProcessConfigList) {
        if (souProcessConfigList.isEmpty()) { return Collections.emptyList(); }

        Map<Long/* processConfigId */, CompSouProcessConfig> compProcessConfigMap = compProcessConfigList.stream()
                .collect(Collectors.toMap(CompSouProcessConfig::getProcessConfigId, Function.identity()));

        List<CompSouProcessConfigWebVO> voList;
        if (souProcessConfigList instanceof Page) {
            voList = new Page<>();
            ((Page)voList).setTotal(((Page)souProcessConfigList).getTotal());
            ((Page)voList).setPageNum(((Page)souProcessConfigList).getPageNum());
            ((Page)voList).setPageSize(((Page)souProcessConfigList).getPageSize());
        } else {
            voList = new ArrayList<>(souProcessConfigList.size());
        }

        for (ApiSouProcessConfigVO processConfig : souProcessConfigList) {
            voList.add(convertCompVO(processConfig, compProcessConfigMap.get(processConfig.getProcessConfigId())));
        }
        return voList;
    }

}
