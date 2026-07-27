package com.midea.cloud.srm.supcooperate.pjquicksearch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.perf.pjquicksearch.dto.PerfScoreItemsQueryDto;
import com.midea.cloud.srm.model.perf.projectscoreitem.entity.ProjectScoreItems;
import com.midea.cloud.srm.model.perf.scoreproject.entity.PerfScoreItems;
import com.midea.cloud.srm.model.suppliercooperate.order.entry.WarehousingReturnDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author luxc
 */
@Mapper
public interface PjQuickSearchMapper extends BaseMapper<WarehousingReturnDetail> {

    /**
     * 获取供应商信息
     * @param perfScoreItems
     * @return
     */
    List<WarehousingReturnDetail> listForPerf(PerfScoreItemsQueryDto perfScoreItems);

}
