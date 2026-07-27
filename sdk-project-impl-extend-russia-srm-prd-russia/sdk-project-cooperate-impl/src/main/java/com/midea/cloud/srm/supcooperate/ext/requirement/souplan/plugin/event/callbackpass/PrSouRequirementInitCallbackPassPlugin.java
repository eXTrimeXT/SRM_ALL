package com.midea.cloud.srm.supcooperate.ext.requirement.souplan.plugin.event.callbackpass;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.constant.NumConstant;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.entity.PrRequirementHead;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.entity.PrRequirementLine;
import com.midea.cloud.srm.model.pm.pr.division.entity.DivisionCategory;
import com.midea.cloud.srm.model.pm.pr.requirement.enums.RequirementApproveStatus;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementGroupTypeEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementStatusEnum;
import com.midea.cloud.srm.pr.division.service.IDivisionCategoryService;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.callbackpass.IRequirementInitCallbackPassPlugin;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.callbackpass.RequirementInitCallbackPassContext;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 采购申请 - 立项通过回调插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/06/02
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class PrSouRequirementInitCallbackPassPlugin implements IRequirementInitCallbackPassPlugin {

    @Autowired
    private QlService qlService;
    @Autowired
    private IDivisionCategoryService divisionCategoryService;
    @Autowired
    private RbacClient rbacClient;
    @Override
    @ApiOperation("校验操作条件/权限")
    public RequirementInitCallbackPassContext judgeCallbackPassAuth(RequirementInitCallbackPassContext context) {
        PrRequirementHead prHead = qlService.readByKey(PrRequirementHead.class.getSimpleName(), context.getRequirementHeadId(), PrRequirementHead.class);
        AssertUtils.notNull(prHead, LocaleHandler.getLocaleMsg("采购申请单[{0}]不存在"), new Object[]{context.getRequirementHeadId()});
        AssertUtils.isTrue(RequirementApproveStatus.APPROVING.equals(prHead.getAuditStatus()), "非审批中状态，不能立项审批通过", new Object[0]);
        return context;
    }

    @Override
    @ApiOperation("更新数据")
    public RequirementInitCallbackPassContext executeCallbackPass(RequirementInitCallbackPassContext context) {
        PrRequirementHead prHead = qlService.readByKey(PrRequirementHead.class.getSimpleName(), context.getRequirementHeadId(), PrRequirementHead.class);
        ExtPrSouRequirementHead prSouHead = qlService.readByKey(ExtPrSouRequirementHead.class.getSimpleName(), context.getRequirementHeadId(), ExtPrSouRequirementHead.class);
        // 2: 更新状态
        qlService.updateByWrapper(QlWrappers.update(PrRequirementHead.class)
                .set(PrRequirementHead::getAuditStatus, RequirementApproveStatus.APPROVED)
                .eq(PrRequirementHead::getRequirementHeadId, context.getRequirementHeadId()));

        List<ExtPrSouRequirementGroup> souGroupList = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementGroup.class)
                .eq(ExtPrSouRequirementGroup::getRequirementHeadId, context.getRequirementHeadId())
                .orderByAsc(ExtPrSouRequirementGroup::getSortIndex), ExtPrSouRequirementGroup.class);
        String SOU="SOU";
        boolean haveSou=false;
        for(ExtPrSouRequirementGroup extPrSouRequirementGroup:souGroupList){
            if(SOU.equals(extPrSouRequirementGroup.getGroupType())){
                haveSou= true;
            }
        }
        Enable enable=Enable.Y;
        if(!haveSou) {
            enable = Enable.N;
        }
        qlService.updateByWrapper(QlWrappers.update(ExtPrSouRequirementHead.class)
                    .set(ExtPrSouRequirementHead::getSouReqStatus, PrSouRequirementStatusEnum.EXECUTING)
                    .set(ExtPrSouRequirementHead::getHasAssigned, enable)
                    .set(ExtPrSouRequirementHead::getApprovalPassTime, LocalDateTime.now())
                    .eq(ExtPrSouRequirementHead::getRequirementHeadId, context.getRequirementHeadId()));
        return context;
    }

    @Override
    public int getOrder() {
        return 10;
    }

}
