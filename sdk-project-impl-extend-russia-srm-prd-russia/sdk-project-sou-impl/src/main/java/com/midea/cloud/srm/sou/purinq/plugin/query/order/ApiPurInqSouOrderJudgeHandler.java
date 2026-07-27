package com.midea.cloud.srm.sou.purinq.plugin.query.order;

import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.model.extapi.sou.inq.enums.ExtPurInqSouTypeEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.sou.sourcing.spi.order.ApiSouOrderJudgeHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Component
public class ApiPurInqSouOrderJudgeHandler extends ApiSouOrderJudgeHandler {

    @Override
    @ApiOperation("当前是否可以查看供应商报价信息")
    public SouProject judgeGetOrderInfoAuth(long projectId, long vendorId, @Nullable Integer round, boolean isBuyer, String souType) {
        AssertUtils.isTrue(AppUserUtil.getLoginAppUser().getCompanyId().equals(vendorId), "禁止查看他人的报价信息!");
        return super.judgeGetOrderInfoAuth(projectId, vendorId, round, isBuyer, souType);
    }

    @Override
    @ApiOperation("是否可以报价")
    public void judgeOrderAuth(long projectId, long vendorId, boolean isBuyer, String souType) {
        AssertUtils.isTrue(AppUserUtil.getLoginAppUser().getCompanyId().equals(vendorId), "禁止查看他人的报价信息!");
        super.judgeOrderAuth(projectId, vendorId, isBuyer, souType);
    }

    @Override
    @ApiOperation("校验是否能撤回指定的报价单")
    public SouProject judgeRollbackAuth(long projectId, long vendorId, String souType) {
        AssertUtils.isTrue(AppUserUtil.getLoginAppUser().getCompanyId().equals(vendorId), "禁止查看他人的报价信息!");
        return super.judgeRollbackAuth(projectId, vendorId, souType);
    }

    @Override
    public String matchModule() {
        return ExtPurInqSouTypeEnum.ext_pur_inq.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
