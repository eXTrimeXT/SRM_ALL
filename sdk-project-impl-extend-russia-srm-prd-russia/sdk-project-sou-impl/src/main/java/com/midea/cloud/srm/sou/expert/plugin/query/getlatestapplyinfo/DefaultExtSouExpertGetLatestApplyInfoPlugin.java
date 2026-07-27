package com.midea.cloud.srm.sou.expert.plugin.query.getlatestapplyinfo;

import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.sou.expert.entity.*;
import com.midea.cloud.srm.model.sou.expert.enums.ExtSouExpertApplyStatusEnum;
import com.midea.cloud.srm.model.sou.expert.vo.ExtSouExpertApplyVO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.sou.expert.spi.query.getlatestapplyinfo.ExtSouExpertGetLatestApplyInfoContext;
import com.midea.cloud.srm.sou.expert.spi.query.getlatestapplyinfo.IExtSouExpertGetLatestApplyInfoPlugin;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 专家库 - 查询专家申请详情插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/12
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultExtSouExpertGetLatestApplyInfoPlugin implements IExtSouExpertGetLatestApplyInfoPlugin {

    @Autowired
    private QlService qlService;
    @Autowired
    private BaseClient baseClient;

    @Override
    @ApiOperation("校验操作条件/权限")
    public ExtSouExpertGetLatestApplyInfoContext judgeGetLatestApplyInfoAuth(ExtSouExpertGetLatestApplyInfoContext context) {
        context.getParam().formatParams();

        //noinspection unchecked
        List<ExtSouExpertApply> applyList = qlService.queryByWrapper(QlWrappers.query(ExtSouExpertApply.class)
                .eq(ExtSouExpertApply::getApplyById, context.getParam().getUserId())
                .orderByDesc(ExtSouExpertApply::getCreationDate), ExtSouExpertApply.class);
        if (!applyList.isEmpty()) {
            if (context.getParam().getPriorityApprovalPass()) {
                // 优先返回已审批的
                int num2 = 2;
                if (ExtSouExpertApplyStatusEnum.APPROVED.name().equals(applyList.get(0).getApplyStatus())) {
                    context.setExpertApply(applyList.get(0));
                } else if (applyList.size() >= num2) {
                    context.setExpertApply(applyList.get(1));

                    context.setNoApprovalPassExpertApplyId(applyList.get(0).getExpertApplyId());
                } else {
                    context.setExpertApply(applyList.get(0));
                }
            } else {
                context.setExpertApply(applyList.get(0));
            }
        }

        return context;
    }

    @Override
    @ApiOperation("执行处理")
    public ExtSouExpertGetLatestApplyInfoContext executeGetLatestApplyInfo(ExtSouExpertGetLatestApplyInfoContext context) {
        if (context.getExpertApply() == null) { return context; }
        ExtSouExpertApplyVO result = SouObjectXUtil.convertTargetObj(context.getExpertApply(), ExtSouExpertApplyVO.class);
        context.setResult(result);
        // 1: 查询适用组织
        //noinspection unchecked
        result.setOrgList(qlService.queryByWrapper(QlWrappers.query(ExtSouExpertOrgRelation.class)
                .eq(ExtSouExpertOrgRelation::getExpertApplyId, context.getExpertApply().getExpertApplyId())
                .orderByAsc(ExtSouExpertOrgRelation::getSortIndex), ExtSouExpertOrgRelation.class));
        // 2: 查询学历
        //noinspection unchecked
        result.setEducationList(qlService.queryByWrapper(QlWrappers.query(ExtSouExpertEducation.class)
                .eq(ExtSouExpertEducation::getExpertApplyId, context.getExpertApply().getExpertApplyId())
                .orderByAsc(ExtSouExpertEducation::getSortIndex), ExtSouExpertEducation.class));
        // 3: 查询适用品类
        //noinspection unchecked
        result.setCategoryList(qlService.queryByWrapper(QlWrappers.query(ExtSouExpertCategoryRelation.class)
                .eq(ExtSouExpertCategoryRelation::getExpertApplyId, context.getExpertApply().getExpertApplyId())
                .orderByAsc(ExtSouExpertCategoryRelation::getSortIndex), ExtSouExpertCategoryRelation.class));
        // 4: 查询工作经历
        //noinspection unchecked
        result.setWorkList(qlService.queryByWrapper(QlWrappers.query(ExtSouExpertWork.class)
                .eq(ExtSouExpertWork::getExpertApplyId, context.getExpertApply().getExpertApplyId())
                .orderByAsc(ExtSouExpertWork::getSortIndex), ExtSouExpertWork.class));
        // 5: 查询亲属工作经历
        //noinspection unchecked
        result.setWorkRelationList(qlService.queryByWrapper(QlWrappers.query(ExtSouExpertWorkRelation.class)
                .eq(ExtSouExpertWorkRelation::getExpertApplyId, context.getExpertApply().getExpertApplyId())
                .orderByAsc(ExtSouExpertWorkRelation::getSortIndex), ExtSouExpertWorkRelation.class));
        // 6: 查询附件
        result.setAttachList(baseClient.listSceneFileBatch(Collections.singletonList(context.getExpertApply().getExpertApplyId())));

        result.putX("noApprovalPassExpertApplyId", context.getNoApprovalPassExpertApplyId());

        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
