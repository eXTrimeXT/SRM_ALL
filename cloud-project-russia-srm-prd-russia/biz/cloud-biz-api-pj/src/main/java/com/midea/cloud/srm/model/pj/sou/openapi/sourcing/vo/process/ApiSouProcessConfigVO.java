package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.process;

import com.github.pagehelper.Page;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 寻源openAPI - 流程配置
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/20
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouProcessConfigVO extends SouProcessConfig {

    @SuppressWarnings("rawtypes")
    public static List<ApiSouProcessConfigVO> convertApiVO(List<SouProcessConfig> processConfigList) {
        if (processConfigList.isEmpty()) { return Collections.emptyList(); }
        List<ApiSouProcessConfigVO> voList;
        if (processConfigList instanceof Page) {
            voList = new Page<>();
            ((Page)voList).setTotal(((Page)processConfigList).getTotal());
            ((Page)voList).setPageNum(((Page)processConfigList).getPageNum());
            ((Page)voList).setPageSize(((Page)processConfigList).getPageSize());
        } else {
            voList = new ArrayList<>(processConfigList.size());
        }

        ApiSouProcessConfigVO vo;
        for (SouProcessConfig processConfig : processConfigList) {
            vo = new ApiSouProcessConfigVO();
            voList.add(vo);
            BeanUtils.copyProperties(processConfig, vo);
        }
        return voList;
    }

}
