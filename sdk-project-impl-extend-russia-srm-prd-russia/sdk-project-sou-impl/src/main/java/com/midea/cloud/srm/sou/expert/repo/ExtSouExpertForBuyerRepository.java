package com.midea.cloud.srm.sou.expert.repo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.sou.SouUserTypeCheckUtils;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.util.ResultUtil;
import com.midea.cloud.srm.model.sou.expert.dto.*;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpert;
import com.midea.cloud.srm.model.sou.expert.vo.ExtSouExpertQueryVO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.sou.expert.service.ExtSouExpertEventService;
import com.midea.cloud.srm.sou.expert.service.ExtSouExpertQueryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 寻源 - 专家库
 * PS: 专用于导入导出功能
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/12
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtSouExpertForBuyerRepository extends CrudRepository {

    @Autowired
    private ExtSouExpertQueryService extSouExpertQueryService;
    @Autowired
    private ExtSouExpertEventService extSouExpertEventService;

    public ExtSouExpertForBuyerRepository() {
        super();
        // 业务查询
        this.register("queryExperts", this::queryExperts, false, "专家库列表查询");
        // 业务事件
        this.register("frozenExpert", this::frozenExpert, true, "冻结专家");
        this.register("frozenExpertConfirm", this::frozenExpertConfirm, true, "确认冻结专家");
        this.register("frozenExpertReject", this::frozenExpertReject, true, "拒绝冻结专家");
        this.register("unFrozenExpert", this::unFrozenExpert, true, "解冻专家");
        this.register("unfrozenExpertConfirm", this::unfrozenExpertConfirm, true, "确认解冻专家");
        this.register("unfrozenExpertReject", this::unfrozenExpertReject, true, "拒绝解冻专家");
        this.register("quiteExpert", this::quiteExpert, true, "专家退出");
    }

    @ApiOperation("专家库列表查询")
    private QlResult queryExperts(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        ExtSouExpertQueryDTO param = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), ExtSouExpertQueryDTO.class);

        List<ExtSouExpertQueryVO> voList = extSouExpertQueryService.queryExperts(param);
        return ResultUtil.build(queryAction, "expertId", new PageInfo<>(voList), false);
    }

    @ApiOperation("冻结专家")
    private QlResult frozenExpert(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        ExtSouExpertFrozenDTO param; {
            List<ExtSouExpertFrozenDTO> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtSouExpertFrozenDTO>>() {});
            AssertUtils.notEmpty(params, "缺少数据");
            param = params.get(0);
        }

        extSouExpertEventService.frozenExpert(param);
        return QlResult.empty();
    }

    @ApiOperation("确认冻结专家")
    private QlResult frozenExpertConfirm(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        long expertId; {
            List<ExtSouExpert> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtSouExpert>>() {});
            AssertUtils.notEmpty(params, "缺少数据");
            ExtSouExpert param = params.get(0);
            AssertUtils.notNull(param.getExpertId(), "缺少expertId参数");
            expertId = param.getExpertId();
        }

        extSouExpertEventService.frozenExpertConfirm(expertId);
        return QlResult.empty();
    }

    @ApiOperation("拒绝冻结专家")
    private QlResult frozenExpertReject(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        ExtSouExpertFrozenRejectDTO param; {
            List<ExtSouExpertFrozenRejectDTO> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtSouExpertFrozenRejectDTO>>() {});
            AssertUtils.notEmpty(params, "缺少数据");
            param = params.get(0);
        }

        extSouExpertEventService.frozenExpertReject(param);
        return QlResult.empty();
    }

    @ApiOperation("解冻专家")
    private QlResult unFrozenExpert(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        ExtSouExpertUnFrozenDTO param; {
            List<ExtSouExpertUnFrozenDTO> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtSouExpertUnFrozenDTO>>() {});
            AssertUtils.notEmpty(params, "缺少数据");
            param = params.get(0);
        }

        extSouExpertEventService.unFrozenExpert(param);
        return QlResult.empty();
    }

    @ApiOperation("确认解冻专家")
    private QlResult unfrozenExpertConfirm(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        long expertId; {
            List<ExtSouExpert> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtSouExpert>>() {});
            AssertUtils.notEmpty(params, "缺少数据");
            ExtSouExpert param = params.get(0);
            AssertUtils.notNull(param.getExpertId(), "缺少expertId参数");
            expertId = param.getExpertId();
        }

        extSouExpertEventService.unfrozenExpertConfirm(expertId);
        return QlResult.empty();
    }

    @ApiOperation("拒绝解冻专家")
    private QlResult unfrozenExpertReject(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        ExtSouExpertFrozenRejectDTO param; {
            List<ExtSouExpertFrozenRejectDTO> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtSouExpertFrozenRejectDTO>>() {});
            AssertUtils.notEmpty(params, "缺少数据");
            param = params.get(0);
        }

        extSouExpertEventService.unfrozenExpertReject(param);
        return QlResult.empty();
    }

    @ApiOperation("专家退出")
    private QlResult quiteExpert(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        ExtSouExpertQuiteDTO param; {
            List<ExtSouExpertQuiteDTO> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtSouExpertQuiteDTO>>() {});
            AssertUtils.notEmpty(params, "缺少数据");
            param = params.get(0);
        }

        extSouExpertEventService.quiteExpert(param);
        return QlResult.empty();
    }

}
