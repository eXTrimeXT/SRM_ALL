package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.plugin.query.getcancelinfo;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.enums.ExtPrRequirementGroupTypeEnum;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.vo.ExtPrSouRequirementHeadVO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancel;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancelAttach;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancelLine;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.vo.ExtPrSouRequirementCancelLineVO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.vo.ExtPrSouRequirementCancelVO;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.query.getcancelinfo.IPrSouRequirementGetCancelInfoPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.query.getcancelinfo.PrSouRequirementGetCancelInfoContext;
import com.midea.cloud.srm.supcooperate.meiql.requirement.core.init.service.MqlPrRequirementInitQueryService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 招标计划池 - 取消单据查询插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/11
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultPrSouRequirementGetCancelInfoPlugin implements IPrSouRequirementGetCancelInfoPlugin {

    @Autowired
    private QlService qlService;
    @Autowired
    private MqlPrRequirementInitQueryService mqlPrRequirementInitQueryService;

    @Override
    @ApiOperation("校验操作条件/权限")
    public PrSouRequirementGetCancelInfoContext judgeGetCancelInfoAuth(PrSouRequirementGetCancelInfoContext context) {
        ExtPrSouRequirementCancel cancel = qlService.readByKey(ExtPrSouRequirementCancel.class.getSimpleName(), context.getRequirementCancelId(), ExtPrSouRequirementCancel.class);
        AssertUtils.notNull(cancel, "计划取消单据[{0}]不存在", context.getRequirementCancelId());

        context.setResult(SouObjectXUtil.convertTargetObj(cancel, ExtPrSouRequirementCancelVO.class));
        return context;
    }

    @Override
    @ApiOperation("执行处理")
    public PrSouRequirementGetCancelInfoContext executeGetCancelInfo(PrSouRequirementGetCancelInfoContext context) {
        // 1: 查询取消明细
        context.getResult().setCancelLineList(SouObjectXUtil.convertList(qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementCancelLine.class)
                .eq(ExtPrSouRequirementCancelLine::getRequirementCancelId, context.getRequirementCancelId()), ExtPrSouRequirementCancelLine.class), ExtPrSouRequirementCancelLineVO.class));
        queryRequirementGroup(context);
        // 2: 查询取消附件
        context.getResult().setCancelAttachList(qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementCancelAttach.class)
                .eq(ExtPrSouRequirementCancelAttach::getRequirementCancelId, context.getRequirementCancelId()), ExtPrSouRequirementCancelAttach.class));
        // 3: 查询关联的招标计划信息
        context.getResult().getCancelLineList().forEach(line -> line.setReqHead(SouObjectXUtil.convertTargetObj(
                mqlPrRequirementInitQueryService.getRequirementInfo(line.getRequirementHeadId()), ExtPrSouRequirementHeadVO.class)));

        return context;
    }

    /**
     * 查询申请单负责人
     * @param context
     */
    private void queryRequirementGroup(PrSouRequirementGetCancelInfoContext context) {
        if(CollectionUtils.isEmpty(context.getResult().getCancelLineList())) {
            return;
        }
        List<Long> requirementHeadId = context.getResult().getCancelLineList().stream().map(l -> l.getRequirementHeadId()).distinct().collect(Collectors.toList());
        List<ExtPrSouRequirementGroup> groupList = qlService.queryByWrapper(QlWrappers.query(MqlType.EXT_PR_SOU_REQUIREMENT_GROUP).in(ExtPrSouRequirementGroup::getRequirementHeadId, requirementHeadId), ExtPrSouRequirementGroup.class);
        if(CollectionUtils.isNotEmpty(groupList)) {
            Map<String, ExtPrSouRequirementGroup> groupMap = groupList.stream().collect(Collectors.toMap(k -> StringUtils.joinWith(SrmConstant.UNDER_LINE, k.getRequirementHeadId(), k.getGroupType()), Function.identity(), (k1, k2)->k2));
            context.getResult().getCancelLineList().stream().forEach(cancelLine -> {
                /** 招标负责人 */
                ExtPrSouRequirementGroup souGroup = groupMap.getOrDefault(StringUtils.joinWith(SrmConstant.UNDER_LINE, cancelLine.getRequirementHeadId(), ExtPrRequirementGroupTypeEnum.SOU.getCode()), new ExtPrSouRequirementGroup());
                cancelLine.setSouGroupUserId(souGroup.getUserId());
                cancelLine.setSouGroupFullName(souGroup.getFullName());
                cancelLine.setSouGroupUsername(souGroup.getUsername());
                /** 技术负责人 */
                ExtPrSouRequirementGroup techGroup = groupMap.getOrDefault(StringUtils.joinWith(SrmConstant.UNDER_LINE, cancelLine.getRequirementHeadId(), ExtPrRequirementGroupTypeEnum.TECH.getCode()), new ExtPrSouRequirementGroup());
                cancelLine.setTechGroupUserId(techGroup.getUserId());
                cancelLine.setTechGroupFullName(techGroup.getFullName());
                cancelLine.setTechGroupUsername(techGroup.getUsername());
                /** 供应商负责人 */
                ExtPrSouRequirementGroup vendorGroup = groupMap.getOrDefault(StringUtils.joinWith(SrmConstant.UNDER_LINE, cancelLine.getRequirementHeadId(), ExtPrRequirementGroupTypeEnum.VENDOR.getCode()), new ExtPrSouRequirementGroup());
                cancelLine.setVendorGroupUserId(vendorGroup.getUserId());
                cancelLine.setVendorGroupFullName(vendorGroup.getFullName());
                cancelLine.setVendorGroupUsername(vendorGroup.getUsername());
            });
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
