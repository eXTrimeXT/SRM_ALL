package com.midea.cloud.srm.sou.expert.plugin.query.queryexperts;

import com.github.pagehelper.page.PageMethod;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.sou.expert.entity.*;
import com.midea.cloud.srm.model.sou.expert.vo.ExtSouExpertQueryVO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.sou.expert.mapper.ExtSouExpertMapper;
import com.midea.cloud.srm.sou.expert.spi.query.queryexperts.ExtSouExpertQueryContext;
import com.midea.cloud.srm.sou.expert.spi.query.queryexperts.IExtSouExpertQueryPlugin;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 专家库 - 列表查询插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/21
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultExtSouExpertQueryPlugin implements IExtSouExpertQueryPlugin {

    @Autowired
    private QlService qlService;
    @Autowired
    private ExtSouExpertMapper extSouExpertMapper;

    @Override
    @ApiOperation("执行处理")
    public ExtSouExpertQueryContext executeQueryExperts(ExtSouExpertQueryContext context) {
        // 1: 查询数据
        context.getParam().formatParams();
        if (context.getParam().getPageSize() != null && context.getParam().getPageNum() != null) {
            PageMethod.startPage(context.getParam().getPageNum(), context.getParam().getPageSize());
        }
        List<ExtSouExpertQueryVO> resultList = SouObjectXUtil.convertList(extSouExpertMapper.queryExperts(context.getParam()), ExtSouExpertQueryVO.class);
        // 2: 填补其他信息
        Set<Long> expertApplyIds = resultList.stream().map(ExtSouExpertQueryVO::getExpertApplyId).collect(Collectors.toSet());
        // 2.1: 查询申请信息
        Map<Long/* expertApplyId */, ExtSouExpertApply> applyMap = qlService.readByKeys(ExtSouExpertApply.class.getSimpleName(), new ArrayList<>(expertApplyIds), ExtSouExpertApply.class)
                .stream().collect(Collectors.toMap(ExtSouExpertApply::getExpertApplyId, Function.identity()));
        resultList.stream().filter(result -> applyMap.containsKey(result.getExpertApplyId())).forEach(result -> BeanUtils.copyProperties(applyMap.get(result.getExpertApplyId()), result));
        // 2.2: 查询申请学历
        //noinspection unchecked
        Map<Long/* expertApplyId */, List<ExtSouExpertEducation>> educationMap = qlService.queryByWrapper(QlWrappers.query(ExtSouExpertEducation.class)
                .in(ExtSouExpertEducation::getExpertApplyId, expertApplyIds)
                .orderByAsc(ExtSouExpertEducation::getSortIndex), ExtSouExpertEducation.class)
                .stream().collect(Collectors.groupingBy(ExtSouExpertEducation::getExpertApplyId));
        resultList.forEach(result -> {
            List<ExtSouExpertEducation> educationList = educationMap.get(result.getExpertApplyId());
            if (CollectionUtils.isNotEmpty(educationList)) {
                BeanUtils.copyProperties(educationList.get(0), result);
            }
        });

        context.setResult(resultList);
        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
