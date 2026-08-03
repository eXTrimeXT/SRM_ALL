package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.process;

import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.process.ApiSouProcessConfigQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouProcessNodeVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.process.ApiSouProcessConfigVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import io.swagger.annotations.ApiOperation;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 寻源openAPI - 流程配置查询
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/05
 */
@Service
public class ApiSouProcessQueryHandler implements ISouSpiBean {

    @ApiOperation("流程配置列表查询")
    public List<ApiSouProcessConfigVO> doHandlerAfterListProcessConfigs(ApiSouProcessConfigQueryDTO queryParam, List<ApiSouProcessConfigVO> voList) {
        return voList;
    }

    @ApiOperation("查询指定流程配置后的额外处理")
    public SouProcessConfig doHandlerAfterGetProcessConfig(long processConfigId, @Nullable Long vendorId, String souType, SouProcessConfig souProcessConfig) {
        return souProcessConfig;
    }

    @ApiOperation("查询流程节点信息后的额外处理")
    public List<ApiSouProcessNodeVO> doHandlerAfterListProcessNodes(long projectId, String souType, List<ApiSouProcessNodeVO> voList) {
        return voList;
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
