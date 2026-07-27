package com.midea.cloud.srm.biz.pj.supplier.service.impl;

import com.alibaba.fastjson.JSON;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.biz.pj.common.OpenClientConstant;
import com.midea.cloud.srm.biz.pj.common.OpenClientUtils;
import com.midea.cloud.srm.biz.pj.supplier.service.BlackSupplierQueryService;
import com.midea.cloud.srm.model.pj.supplier.dto.BlackCompanyDto;
import com.midea.cloud.srm.model.pj.supplier.dto.BlackCompanyResponseDto;
import com.midea.cloud.srm.model.pj.supplier.dto.BlackCompanyResultDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * <pre>
 *  接收供应商失信名单接口 服务实现类
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
@Lazy
@Service
@Slf4j
public class BlackSupplierQueryServiceImpl implements BlackSupplierQueryService {

    @Override
    public BlackCompanyResponseDto<BlackCompanyResultDto> blackCompanyQuery(BlackCompanyDto blackCompanyDto) {
        try {
            String result = OpenClientUtils.sendHttpPost(OpenClientUtils.TYPE.SYN_BLACK_COMPANY, JSON.toJSONString(blackCompanyDto), "application/json");
            BlackCompanyResponseDto<BlackCompanyResultDto> blackCompanyResultDto = BlackCompanyResponseDto.buildResp(result);

            if (OpenClientConstant.CODE_SUCCESS_INT.compareTo(blackCompanyResultDto.getCode()) != 0) {
                throw new BaseException("请求MDM失信名单接口返回异常：" + blackCompanyResultDto.getMessage());
            }
            return blackCompanyResultDto;
        } catch (Exception e) {
            log.error("synSupplierViewToSap Exception", e);
            throw new BaseException(e.getMessage());
        }
    }
}
