package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.tech;

import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.tech.ApiSouTechScoreDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Service;

/**
 * 寻源openAPI - 技术标业务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/05
 */
@Service
public class ApiSouTechEventHandler implements ISouSpiBean {

    @ApiOperation("技术评分后的额外处理")
    public void doHandlerAfterTechScore(ApiSouTechScoreDTO param, String souType) {
    }

    @ApiOperation("技术开标后的额外处理")
    public void doHandlerAfterOpenTech(long projectId, String souType) {
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
