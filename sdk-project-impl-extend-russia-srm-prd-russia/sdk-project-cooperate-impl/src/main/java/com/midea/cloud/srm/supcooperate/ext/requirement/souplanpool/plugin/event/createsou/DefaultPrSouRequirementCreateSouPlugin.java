package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.plugin.event.createsou;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryWrapper;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.InviteTendersExtClient;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.entity.PrRequirementHead;
import com.midea.cloud.srm.model.pm.pr.requirement.enums.RequirementApproveStatus;
import com.midea.cloud.srm.model.sou.pool.dto.SouBidRequirementPoolDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouDemand;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.dto.ExtPrSouRequirementHeadDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementStatusEnum;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.service.PrSouRequirementCreateValidService;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.createsou.IPrSouRequirementCreateSouPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.createsou.PrSouRequirementCreateSouContext;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 招标计划池 - 创建寻源单插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/11
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultPrSouRequirementCreateSouPlugin implements IPrSouRequirementCreateSouPlugin {

    @Autowired
    private QlService qlService;

    @Autowired
    private PrSouRequirementCreateValidService prSouRequirementCreateValidService;

    @Override
    @ApiOperation("校验操作条件/权限")
    public PrSouRequirementCreateSouContext judgeCreateSouAuth(PrSouRequirementCreateSouContext context) {
        context.getParam().formatParams();
        context.getParam().setReqHeadList(context.getParam().getReqHeadList().stream().filter(e -> e.getRequirementHeadId() != null).collect(Collectors.toList()));
        AssertUtils.notEmpty(context.getParam().getReqHeadList(), "请选择需要操作的数据");
        List<ExtPrSouRequirementHead> souPrHeadList = qlService.readByKeys(ExtPrSouRequirementHead.class.getSimpleName(),
                new ArrayList<>(context.getParam().getReqHeadList().stream().map(ExtPrSouRequirementHeadDTO::getRequirementHeadId).collect(Collectors.toSet())), ExtPrSouRequirementHead.class);
        AssertUtils.notEmpty(context.getParam().getReqHeadList(), "请选择需要操作的招标计划数据");
        List<PrRequirementHead> prHeadList = qlService.readByKeys(PrRequirementHead.class.getSimpleName(),
                new ArrayList<>(souPrHeadList.stream().map(ExtPrSouRequirementHead::getRequirementHeadId).collect(Collectors.toSet())), PrRequirementHead.class);
        Map<Long, PrRequirementHead> prHeadMap = prHeadList.stream().collect(Collectors.toMap(PrRequirementHead::getRequirementHeadId, Function.identity()));
        boolean isRequirementOk = prHeadList.stream().allMatch(e -> RequirementApproveStatus.APPROVED.equals(e.getAuditStatus()));
        AssertUtils.isTrue(isRequirementOk, "只能操作已审批的招标计划");
        souPrHeadList.forEach(prHead -> AssertUtils.isFalse(PrSouRequirementStatusEnum.CANCEL.name().equals(prHead.getSouReqStatus()),
                "招标计划[{0}]已取消", prHeadMap.get(prHead.getRequirementHeadId()).getRequirementHeadNum()));
        souPrHeadList.forEach(e -> AssertUtils.isTrue(Enable.N.equals(e.getHasCreateSou()), "招标计划[{0}]已生成标书", prHeadMap.get(e.getRequirementHeadId()).getRequirementHeadNum()));

        checkForCreate(context);

        context.setSouPrHeadList(souPrHeadList);
        context.setPrHeadList(prHeadList);
        return context;
    }

    protected void checkForCreate(PrSouRequirementCreateSouContext context) {
        prSouRequirementCreateValidService.requirementCreateValid(context.getParam().getReqHeadList().stream().map(r->r.getRequirementHeadId()).collect(Collectors.toList()), context.getParam().getSouType());
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
