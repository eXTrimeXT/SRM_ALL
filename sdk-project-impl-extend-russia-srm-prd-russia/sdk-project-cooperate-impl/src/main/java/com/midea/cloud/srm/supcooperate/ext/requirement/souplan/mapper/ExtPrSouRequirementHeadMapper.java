package com.midea.cloud.srm.supcooperate.ext.requirement.souplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.entity.PrRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.vo.ExtPrSouRequirementHeadVO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.vo.ExtPrSouRequirementPoolQueryVO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.dto.ExtPrSouRequirementPoolQueryDTO;

import java.util.List;

/**
 * 招标计划 - 头表
 * @author huangbf3
 */
public interface ExtPrSouRequirementHeadMapper extends BaseMapper<ExtPrSouRequirementHead> {

    /**
     * 招标需求池列表查询
     * @param queryParam 参数
     * @return 返回
     */
    List<ExtPrSouRequirementPoolQueryVO> querySouPrPool(ExtPrSouRequirementPoolQueryDTO queryParam);

}
