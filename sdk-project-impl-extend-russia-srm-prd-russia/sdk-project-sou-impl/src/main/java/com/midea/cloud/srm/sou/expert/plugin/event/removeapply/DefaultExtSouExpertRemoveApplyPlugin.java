package com.midea.cloud.srm.sou.expert.plugin.event.removeapply;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.sou.expert.entity.*;
import com.midea.cloud.srm.model.sou.expert.enums.ExtSouExpertApplyStatusEnum;
import com.midea.cloud.srm.model.sou.expert.vo.ExtSouExpertApplyVO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.sou.expert.spi.event.removeapply.ExtSouExpertRemoveApplyContext;
import com.midea.cloud.srm.sou.expert.spi.event.removeapply.IExtSouExpertRemoveApplyPlugin;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * 专家库 - 删除专家申请插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/13
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultExtSouExpertRemoveApplyPlugin implements IExtSouExpertRemoveApplyPlugin {

    @Autowired
    private QlService qlService;
    @Autowired
    private BaseClient baseClient;

    @Override
    @ApiOperation("校验操作条件/权限")
    public ExtSouExpertRemoveApplyContext judgeRemoveApplyAuth(ExtSouExpertRemoveApplyContext context) {
        ExtSouExpertApply expertApply = qlService.readByKey(ExtSouExpertApply.class.getSimpleName(), context.getExpertApplyId(), ExtSouExpertApply.class);
        context.setExpertApply(expertApply);
        if (expertApply != null) {
            AssertUtils.isTrue(ExtSouExpertApplyStatusEnum.DRAFT.name().equals(expertApply.getApplyStatus()), "非拟定状态申请单，不能删除");
        }

        return context;
    }

    @Override
    @ApiOperation("执行处理")
    public ExtSouExpertRemoveApplyContext executeRemoveApply(ExtSouExpertRemoveApplyContext context) {
        if (context.getExpertApply() == null) { return context; }
        // 1: 查询数据
        ExtSouExpertApplyVO result = SouObjectXUtil.convertTargetObj(context.getExpertApply(), ExtSouExpertApplyVO.class);
        context.setResult(result); {
            // 1.1: 查询申请附件
            result.setAttachList(baseClient.listSceneFileBatch(Collections.singletonList(context.getExpertApply().getExpertApplyId())));
            // 1.2: 查询适用品类
            //noinspection unchecked
            result.setCategoryList(qlService.queryByWrapper(QlWrappers.query(ExtSouExpertCategoryRelation.class)
                    .eq(ExtSouExpertCategoryRelation::getExpertApplyId, context.getExpertApplyId())
                    .orderByAsc(ExtSouExpertCategoryRelation::getSortIndex), ExtSouExpertCategoryRelation.class));
            // 1.3: 查询学历
            //noinspection unchecked
            result.setEducationList(qlService.queryByWrapper(QlWrappers.query(ExtSouExpertEducation.class)
                    .eq(ExtSouExpertEducation::getExpertApplyId, context.getExpertApplyId())
                    .orderByAsc(ExtSouExpertEducation::getSortIndex), ExtSouExpertEducation.class));
            // 1.4: 查询适用组织
            //noinspection unchecked
            result.setOrgList(qlService.queryByWrapper(QlWrappers.query(ExtSouExpertOrgRelation.class)
                    .eq(ExtSouExpertOrgRelation::getExpertApplyId, context.getExpertApplyId())
                    .orderByAsc(ExtSouExpertOrgRelation::getSortIndex), ExtSouExpertOrgRelation.class));
            // 1.5: 查询工作经历
            //noinspection unchecked
            result.setWorkList(qlService.queryByWrapper(QlWrappers.query(ExtSouExpertWork.class)
                    .eq(ExtSouExpertWork::getExpertApplyId, context.getExpertApplyId())
                    .orderByAsc(ExtSouExpertWork::getSortIndex), ExtSouExpertWork.class));
        }
        // 2: 删除数据
        qlService.deleteByKeys(ExtSouExpertApply.class.getSimpleName(), Collections.singletonList(context.getExpertApplyId()));
        qlService.deleteByWrapper(QlWrappers.update(ExtSouExpertCategoryRelation.class).eq(ExtSouExpertCategoryRelation::getExpertApplyId, context.getExpertApplyId()));
        qlService.deleteByWrapper(QlWrappers.update(ExtSouExpertEducation.class).eq(ExtSouExpertEducation::getExpertApplyId, context.getExpertApplyId()));
        qlService.deleteByWrapper(QlWrappers.update(ExtSouExpertOrgRelation.class).eq(ExtSouExpertOrgRelation::getExpertApplyId, context.getExpertApplyId()));
        qlService.deleteByWrapper(QlWrappers.update(ExtSouExpertWork.class).eq(ExtSouExpertWork::getExpertApplyId, context.getExpertApplyId()));
        baseClient.removeBusinessIdBatch(ExtSouExpertApply.ATTACH_FILE_SCENE_TYPE, Collections.singletonList(context.getExpertApplyId()));

        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
