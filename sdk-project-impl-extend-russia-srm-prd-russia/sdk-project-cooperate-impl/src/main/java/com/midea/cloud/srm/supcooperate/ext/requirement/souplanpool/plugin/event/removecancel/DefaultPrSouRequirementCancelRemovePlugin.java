package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.plugin.event.removecancel;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancel;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancelAttach;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancelLine;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.enums.PrSouRequirementCancelStatusEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.vo.ExtPrSouRequirementCancelLineVO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.vo.ExtPrSouRequirementCancelVO;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.removecancel.IPrSouRequirementCancelRemovePlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.removecancel.PrSouRequirementCancelRemoveContext;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 招标计划 - 计划取消删除插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/09
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultPrSouRequirementCancelRemovePlugin implements IPrSouRequirementCancelRemovePlugin {

    @Autowired
    private QlService qlService;

    @Override
    @ApiOperation("校验操作条件/权限")
    public PrSouRequirementCancelRemoveContext judgeRemoveCancelAuth(PrSouRequirementCancelRemoveContext context) {
        ExtPrSouRequirementCancel reqCancel = qlService.readByKey(ExtPrSouRequirementCancel.class.getSimpleName(), context.getRequirementCancelId(), ExtPrSouRequirementCancel.class);
        if (reqCancel == null) { return context; }
        AssertUtils.isTrue(PrSouRequirementCancelStatusEnum.DRAFT.name().equals(reqCancel.getCancelStatus()), "非拟定状态，不能删除");
        context.setReqCancel(reqCancel);
        return context;
    }

    @Override
    @ApiOperation("执行处理")
    public PrSouRequirementCancelRemoveContext executeRemoveCancel(PrSouRequirementCancelRemoveContext context) {
        // 1: 先查询需要删除的数据(用于组装返回)
        ExtPrSouRequirementCancelVO result = SouObjectXUtil.convertTargetObj(context.getReqCancel(), ExtPrSouRequirementCancelVO.class);
        result.setCancelLineList(SouObjectXUtil.convertList(qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementCancelLine.class)
                .eq(ExtPrSouRequirementCancelLine::getRequirementCancelId, context.getRequirementCancelId()), ExtPrSouRequirementCancelLine.class), ExtPrSouRequirementCancelLineVO.class));
        result.setCancelAttachList(qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementCancelAttach.class)
                .eq(ExtPrSouRequirementCancelAttach::getRequirementCancelId, context.getRequirementCancelId()), ExtPrSouRequirementCancelAttach.class));
        context.setResult(result);

        // 2: 删除数据
        qlService.deleteByWrapper(QlWrappers.update(ExtPrSouRequirementCancel.class).eq(ExtPrSouRequirementCancel::getRequirementCancelId, context.getRequirementCancelId()));
        qlService.deleteByWrapper(QlWrappers.update(ExtPrSouRequirementCancelLine.class).eq(ExtPrSouRequirementCancelLine::getRequirementCancelId, context.getRequirementCancelId()));
        qlService.deleteByWrapper(QlWrappers.update(ExtPrSouRequirementCancelAttach.class).eq(ExtPrSouRequirementCancelAttach::getRequirementCancelId, context.getRequirementCancelId()));

        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
