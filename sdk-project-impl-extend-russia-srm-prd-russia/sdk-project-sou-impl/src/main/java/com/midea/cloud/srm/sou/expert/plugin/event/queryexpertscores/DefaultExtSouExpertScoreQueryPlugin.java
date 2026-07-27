package com.midea.cloud.srm.sou.expert.plugin.event.queryexpertscores;

import com.github.pagehelper.page.PageMethod;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpert;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertScoreLine;
import com.midea.cloud.srm.model.sou.expert.enums.ExtSouExpertScoreGroupTypeEnum;
import com.midea.cloud.srm.model.sou.expert.vo.ExtSouExpertScoreQueryVO;
import com.midea.cloud.srm.model.sou.expert.vo.ExtSouExpertScoreVO;
import com.midea.cloud.srm.sou.expert.mapper.ExtSouExpertScoreMapper;
import com.midea.cloud.srm.sou.expert.spi.query.queryexpertscores.ExtSouExpertScoreQueryContext;
import com.midea.cloud.srm.sou.expert.spi.query.queryexpertscores.IExtSouExpertScoreQueryPlugin;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 专家评审 - 列表查询插件
 *
 * @author zhangwk12@meicloud.com
 */
@Slf4j
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultExtSouExpertScoreQueryPlugin implements IExtSouExpertScoreQueryPlugin {

    @Autowired
    private QlService qlService;
    @Autowired
    private ExtSouExpertScoreMapper extSouExpertScoreMapper;

    @Override
    @ApiOperation("执行处理")
    public ExtSouExpertScoreQueryContext executeQueryExpertScores(ExtSouExpertScoreQueryContext context) {
        try{
        context.getParam().formatParams();
        // 1: 查询数据
        if (context.getParam().getPageNum() != null && context.getParam().getPageSize() != null) {
            PageMethod.startPage(context.getParam().getPageNum(), context.getParam().getPageSize());
        }
        log.info("executeQueryExpertScores->1");
        List<ExtSouExpertScoreQueryVO> resultList = extSouExpertScoreMapper.queryExpertScores(context.getParam());
        // 2: 查询额外信息
        if (!resultList.isEmpty()) {
            log.info("executeQueryExpertScores->2");
            Map<Long/* expertScoreId */, List<ExtSouExpertScoreLine>> scoreLineMap = qlService.queryByWrapper(QlWrappers.query(ExtSouExpertScoreLine.class)
                    .in(ExtSouExpertScoreLine::getExpertScoreId, resultList.stream().map(ExtSouExpertScoreQueryVO::getExpertScoreId).collect(Collectors.toSet())), ExtSouExpertScoreLine.class)
                    .stream().collect(Collectors.groupingBy(ExtSouExpertScoreLine::getExpertScoreId));
            resultList.forEach(r -> {
                List<ExtSouExpertScoreLine> lineList = scoreLineMap.get(r.getExpertScoreId());
                r.setScoreLineList(lineList);
                if (lineList != null) {
                    ExtSouExpertScoreLine leaderInfo = lineList.stream().filter(e -> ExtSouExpertScoreGroupTypeEnum.SOU_LEADER.name().equals(e.getGroupType())).findAny().orElse(null);
                    if (leaderInfo != null) {
                        r.setLeaderUserId(leaderInfo.getUserId());
                        r.setLeaderUsername(leaderInfo.getUsername());
                        r.setLeaderNickname(leaderInfo.getNickname());
                    }
                    ExtSouExpertScoreLine managerInfo = lineList.stream().filter(e -> ExtSouExpertScoreGroupTypeEnum.SOU_MANAGER.name().equals(e.getGroupType())).findAny().orElse(null);
                    if (managerInfo != null) {
                        r.setManagerUserId(managerInfo.getUserId());
                        r.setManagerUsername(managerInfo.getUsername());
                        r.setManagerNickname(managerInfo.getNickname());
                    }
                }
            });
            log.info("executeQueryExpertScores->3");
            Map<Long/* expertId */, ExtSouExpert> expertMap = qlService.queryByWrapper(QlWrappers.query(ExtSouExpert.class)
                    .in(ExtSouExpert::getExpertId, resultList.stream().map(ExtSouExpertScoreQueryVO::getExpertId).collect(Collectors.toSet())), ExtSouExpert.class)
                    .stream().collect(Collectors.toMap(ExtSouExpert::getExpertId, Function.identity()));
            resultList.forEach(r -> {
                ExtSouExpert expert = expertMap.get(r.getExpertId());
                if (expert != null) {
                    r.setJobStatus(expert.getJobStatus());
                    r.setHasQuite(expert.getHasQuite());
                }
            });
        }

        context.setResult(resultList);
        }catch (Exception e){
            log.error("executeQueryExpertScores异常",e);
            e.printStackTrace();
            throw new BaseException("executeQueryExpertScores异常,"+e.getMessage());
        }
        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
