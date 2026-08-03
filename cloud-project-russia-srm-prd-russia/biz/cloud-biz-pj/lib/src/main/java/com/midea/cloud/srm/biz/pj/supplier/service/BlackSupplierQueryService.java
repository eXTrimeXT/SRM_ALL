package com.midea.cloud.srm.biz.pj.supplier.service;

import com.midea.cloud.srm.model.pj.supplier.dto.BlackCompanyDto;
import com.midea.cloud.srm.model.pj.supplier.dto.BlackCompanyResponseDto;
import com.midea.cloud.srm.model.pj.supplier.dto.BlackCompanyResultDto;

/**
 * <pre>
 *  接收供应商失信名单接口
 * </pre>
 *
 * @author fubiao
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2024-06-06 11:28:48
 *  修改内容:
 * </pre>
 */
public interface BlackSupplierQueryService {

    /**
     * 分页查询失信名单
     * @param blackCompanyDto 参数
     * @return 结果
     */
    BlackCompanyResponseDto<BlackCompanyResultDto> blackCompanyQuery(BlackCompanyDto blackCompanyDto);
}
