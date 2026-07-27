package com.midea.cloud.srm.sou.expert.repo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.sou.SouUserTypeCheckUtils;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.util.ResultUtil;
import com.midea.cloud.srm.model.sou.expert.dto.ExtSouExpertDoScoreDTO;
import com.midea.cloud.srm.model.sou.expert.dto.ExtSouExpertScoreCreateDTO;
import com.midea.cloud.srm.model.sou.expert.dto.ExtSouExpertScoreQueryDTO;
import com.midea.cloud.srm.model.sou.expert.vo.ExtSouExpertScoreQueryVO;
import com.midea.cloud.srm.model.sou.expert.vo.ExtSouExpertScoreVO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.sou.expert.service.ExtSouExpertEventService;
import com.midea.cloud.srm.sou.expert.service.ExtSouExpertQueryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 寻源 - 专家评审
 * PS: 专用于导入导出功能
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/12
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtSouExpertScoreForBuyerRepository extends CrudRepository {

    @Autowired
    private ExtSouExpertQueryService extSouExpertQueryService;
    @Autowired
    private ExtSouExpertEventService extSouExpertEventService;
    private final static String PAGE = "page";
    private final static String FILTER = "filter";
    public ExtSouExpertScoreForBuyerRepository() {
        super();
        // 业务查询
        this.register("queryExpertScores", this::queryExpertScores, false, "专家评审列表查询");
        // 业务事件
        this.register("createExpertScores", this::createExpertScores, true, "创建专家评审信息");
        this.register("expertDoScore", this::expertDoScore, true, "专家评分");
    }

    @ApiOperation("专家评审列表查询")
    private QlResult queryExpertScores(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        ExtSouExpertScoreQueryDTO param = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<ExtSouExpertScoreQueryDTO>() {}); {
            if (param.getX(PAGE) != null) {
                Map<String, Object> pageInfo = param.getX("page");
                param.setPageNum(pageInfo.get("pageNum") != null ? Integer.valueOf(pageInfo.get("pageNum").toString()) : null);
                param.setPageSize(pageInfo.get("pageSize") != null ? Integer.valueOf(pageInfo.get("pageSize").toString()) : null);
            }
            if (param.getX(FILTER) != null) {
                SouObjectXUtil.mergeProperties(param.getX("filter"), param);
            }
        }
        List<ExtSouExpertScoreQueryVO> resultList = extSouExpertQueryService.queryExpertScores(param);
        return ResultUtil.build(queryAction, "expertScoreId", new PageInfo<>(resultList), false);
    }

    @ApiOperation("创建专家评审信息")
    private QlResult createExpertScores(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        List<ExtSouExpertScoreCreateDTO> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtSouExpertScoreCreateDTO>>() {});
        AssertUtils.notEmpty(params, "缺少数据");

        extSouExpertEventService.createExpertScores(params);
        return QlResult.empty();
    }

    @ApiOperation("专家评分")
    private QlResult expertDoScore(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        List<ExtSouExpertDoScoreDTO> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtSouExpertDoScoreDTO>>() {});
        AssertUtils.notEmpty(params, "缺少数据");
        params.forEach(p -> p.setCurrentUserId(AppUserUtil.getLoginAppUser().getUserId()));

        extSouExpertEventService.expertDoScore(params);
        return QlResult.empty();
    }

}
