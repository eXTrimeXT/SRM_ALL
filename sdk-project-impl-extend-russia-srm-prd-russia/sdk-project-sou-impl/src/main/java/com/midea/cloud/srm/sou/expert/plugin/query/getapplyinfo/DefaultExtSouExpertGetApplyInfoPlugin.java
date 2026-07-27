package com.midea.cloud.srm.sou.expert.plugin.query.getapplyinfo;

import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.sou.expert.entity.*;
import com.midea.cloud.srm.model.sou.expert.vo.ExtSouExpertApplyVO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.sou.expert.spi.query.getapplyinfo.ExtSouExpertGetApplyInfoContext;
import com.midea.cloud.srm.sou.expert.spi.query.getapplyinfo.IExtSouExpertGetApplyInfoPlugin;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * 专家库 - 查询专家申请详情插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/12
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultExtSouExpertGetApplyInfoPlugin implements IExtSouExpertGetApplyInfoPlugin {

    @Autowired
    private QlService qlService;
    @Autowired
    private BaseClient baseClient;

    @Override
    @ApiOperation("校验操作条件/权限")
    public ExtSouExpertGetApplyInfoContext judgeGetApplyInfoAuth(ExtSouExpertGetApplyInfoContext context) {
        context.setExpertApply(qlService.readByKey(ExtSouExpertApply.class.getSimpleName(), context.getExpertApplyId(), ExtSouExpertApply.class));

        return context;
    }

    @Override
    @ApiOperation("执行处理")
    public ExtSouExpertGetApplyInfoContext executeGetApplyInfo(ExtSouExpertGetApplyInfoContext context) {
        if (context.getExpertApply() == null) { return context; }
        ExtSouExpertApplyVO result = SouObjectXUtil.convertTargetObj(context.getExpertApply(), ExtSouExpertApplyVO.class);
        context.setResult(result);
        // 1: 查询适用组织
        //noinspection unchecked
        result.setOrgList(qlService.queryByWrapper(QlWrappers.query(ExtSouExpertOrgRelation.class)
                .eq(ExtSouExpertOrgRelation::getExpertApplyId, context.getExpertApplyId())
                .orderByAsc(ExtSouExpertOrgRelation::getSortIndex), ExtSouExpertOrgRelation.class));
        // 2: 查询学历
        //noinspection unchecked
        result.setEducationList(qlService.queryByWrapper(QlWrappers.query(ExtSouExpertEducation.class)
                .eq(ExtSouExpertEducation::getExpertApplyId, context.getExpertApplyId())
                .orderByAsc(ExtSouExpertEducation::getSortIndex), ExtSouExpertEducation.class));
        // 3: 查询适用品类
        //noinspection unchecked
        result.setCategoryList(qlService.queryByWrapper(QlWrappers.query(ExtSouExpertCategoryRelation.class)
                .eq(ExtSouExpertCategoryRelation::getExpertApplyId, context.getExpertApplyId())
                .orderByAsc(ExtSouExpertCategoryRelation::getSortIndex), ExtSouExpertCategoryRelation.class));
        // 4: 查询工作经历
        //noinspection unchecked
        result.setWorkList(qlService.queryByWrapper(QlWrappers.query(ExtSouExpertWork.class)
                .eq(ExtSouExpertWork::getExpertApplyId, context.getExpertApplyId())
                .orderByAsc(ExtSouExpertWork::getSortIndex), ExtSouExpertWork.class));
        // 5: 查询亲属工作经历
        //noinspection unchecked
        result.setWorkRelationList(qlService.queryByWrapper(QlWrappers.query(ExtSouExpertWorkRelation.class)
                .eq(ExtSouExpertWorkRelation::getExpertApplyId, context.getExpertApplyId())
                .orderByAsc(ExtSouExpertWorkRelation::getSortIndex), ExtSouExpertWorkRelation.class));
        // 6: 查询附件
        result.setAttachList(baseClient.listSceneFileBatch(Collections.singletonList(context.getExpertApplyId())));

        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
