package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.service;

import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.vo.ExtPrSouRequirementPoolQueryVO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.dto.ExtPrSouRequirementPoolQueryDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancel;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.vo.ExtPrSouRequirementCancelVO;

import java.util.List;

/**
 * 招标计划池 - 查询服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/07
 */
public interface PrSouRequirementPoolQueryService {

    /**
     * 招标需求池列表查询
     * @param queryParam 参数
     * @return 返回
     */
    List<ExtPrSouRequirementPoolQueryVO> querySouPrPool(ExtPrSouRequirementPoolQueryDTO queryParam);

    /**
     * 查询招标计划取消单据信息
     * @param requirementCancelId {@link ExtPrSouRequirementCancel#getRequirementCancelId}
     * @return 返回
     */
    ExtPrSouRequirementCancelVO getCancelInfo(long requirementCancelId);

}
