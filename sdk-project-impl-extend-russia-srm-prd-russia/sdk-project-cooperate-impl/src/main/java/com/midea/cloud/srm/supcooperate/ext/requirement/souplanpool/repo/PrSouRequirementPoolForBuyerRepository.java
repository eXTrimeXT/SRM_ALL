package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.repo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.sdkplugin.ISdkPlugin;
import com.midea.cloud.common.sou.SouUserTypeCheckUtils;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.util.ResultUtil;
import com.midea.cloud.srm.constant.SouConstant;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouRecommVendorInfoDTO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.dto.ExtPrSouRequirementHeadDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.vo.ExtPrSouRequirementPoolQueryVO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.dto.ExtPrSouRequirementPoolAssignDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.dto.ExtPrSouRequirementPoolCreateSouDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.dto.ExtPrSouRequirementPoolQueryDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancel;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancelLine;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.enums.PrSouRequirementCancelStatusEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.vo.ExtPrSouRequirementCreateSouVO;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.service.PrSouRequirementPoolEventService;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.service.PrSouRequirementPoolQueryService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * mql - 招标计划 - 项目计划
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/05
 */
@Component
@Slf4j
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class PrSouRequirementPoolForBuyerRepository extends CrudRepository {

    @Autowired
    private PrSouRequirementPoolQueryService prSouRequirementPoolQueryService;
    @Autowired
    private PrSouRequirementPoolEventService prSouRequirementPoolEventService;
    @Autowired
    private QlService qlService;
    @Autowired
    private PrSouRequirementController prSouRequirementController;
    @Autowired
    private RedisUtil redisUtil;

    public PrSouRequirementPoolForBuyerRepository() {
        super();
        // 业务查询
        this.register("querySouPool", this::querySouPool, false, "招标需求池列表查询");
        this.register("checkCancelCondition", this::checkCancelCondition, false, "判断是否可以进行取消操作");
        this.register("queryOneSouPoolInfo", this::queryOneSouPoolInfo, false, "查询指定的招标计划信息");

        // 业务事件
        this.register("batchAssign", this::batchAssign, true, "批量分配/转办");
        this.register("changeSouPlan", this::changeSouPlan, true, "变更计划单据");
        this.register("createVendorRecommend", this::createVendorRecommend, true, "供应商推荐");
        this.register("createSouReq", this::createSouReq, true, "创建寻源需求");
        this.register("createSou", this::createSou, true, "创建寻源单");
        this.register("createBidSou", this::createBidSou, true, "创建竞价寻源单");
    }

    @ApiOperation("招标需求池列表查询")
    private QlResult querySouPool(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        ExtPrSouRequirementPoolQueryDTO param = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), ExtPrSouRequirementPoolQueryDTO.class);
        // 处理数据权限问题
        List<ExtPrSouRequirementPoolQueryVO> result = prSouRequirementController.querySouPrPool(param);
        //年份-取申请时间的年
        result.forEach(s -> {
            LocalDate applyDate = s.getApplyDate();
            s.setProjectYear(applyDate.getYear());
            s.setProjectDate(s.getProjectYear()+"-"+s.getProjectMonth());
        });

        return ResultUtil.build(queryAction, "requirementHeadId", new PageInfo<>(result), false);
    }

    @ApiOperation("判断是否可以进行取消操作")
    private QlResult checkCancelCondition(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        List<ExtPrSouRequirementHeadDTO> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtPrSouRequirementHeadDTO>>() {});
        AssertUtils.notEmpty(params, "请选择数据");
        AssertUtils.isTrue(params.size() == 1, "只能选择单个数据");
        AssertUtils.notNull(params.get(0).getRequirementHeadId(), "缺少requirementHeadId参数");

        ExtPrSouRequirementHead souPrHead = qlService.readByKey(ExtPrSouRequirementHead.class.getSimpleName(),
                params.get(0).getRequirementHeadId(), ExtPrSouRequirementHead.class);
        AssertUtils.notNull(souPrHead, "招标计划[{0}]不存在", params.get(0).getRequirementHeadId());

        List<ExtPrSouRequirementCancelLine> cancelLineList = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementCancelLine.class)
                .eq(ExtPrSouRequirementCancelLine::getRequirementHeadId, params.get(0).getRequirementHeadId()), ExtPrSouRequirementCancelLine.class);
        if (!cancelLineList.isEmpty()) {
            Set<Long> cancelIds = cancelLineList.stream().map(ExtPrSouRequirementCancelLine::getRequirementCancelId).collect(Collectors.toSet());
            List<ExtPrSouRequirementCancel> cancelList = qlService.readByKeys(ExtPrSouRequirementCancel.class.getSimpleName(), new ArrayList<>(cancelIds), ExtPrSouRequirementCancel.class)
                    .stream().filter(e -> !PrSouRequirementCancelStatusEnum.ABANDONED.name().equals(e.getCancelStatus())).collect(Collectors.toList());
            AssertUtils.isTrue(cancelList.isEmpty(), "招标计划已被取消单[{0}]引用", cancelList.get(0).getRequirementCancelNo());
        }

        return QlResult.empty();
    }

    @ApiOperation("查询指定的招标计划信息")
    private QlResult queryOneSouPoolInfo(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        ExtPrSouRequirementPoolQueryDTO param = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), ExtPrSouRequirementPoolQueryDTO.class);
        AssertUtils.notNull(param.getRequirementHeadNum(), "招标计划编号不存在");
        // 处理数据权限问题
        List<ExtPrSouRequirementPoolQueryVO> result = prSouRequirementController.querySouPrPool(param);
        if (result.isEmpty()) {
            return QlResult.empty();
        } else {
            return ResultUtil.build(queryAction, "requirementHeadId", Collections.singletonList(result.get(0)), false);
        }
    }

    @ApiOperation("批量分配/转办")
    private QlResult batchAssign(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        ExtPrSouRequirementPoolAssignDTO param; {
            List<ExtPrSouRequirementPoolAssignDTO> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtPrSouRequirementPoolAssignDTO>>() {});
            AssertUtils.notEmpty(params, "缺少数据");
            param = params.get(0);
            param.setSceneType(ISdkPlugin.DEFAULT_SCENE);
        }

        prSouRequirementPoolEventService.batchAssign(param);
        return QlResult.empty();
    }

    @ApiOperation("变更计划单据")
    private QlResult changeSouPlan(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        ExtPrSouRequirementHeadDTO param; {
            List<ExtPrSouRequirementHeadDTO> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtPrSouRequirementHeadDTO>>() {});
            AssertUtils.notEmpty(params, "缺少数据");
            param = params.get(0);
        }

        param = prSouRequirementPoolEventService.changeSouPlan(param);
        return ResultUtil.build(queryAction, "requirementHeadId", Collections.singletonList(param), false);
    }

    @ApiOperation("供应商推荐")
    private QlResult createVendorRecommend(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        try {
            List<ExtPrSouRequirementHeadDTO> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtPrSouRequirementHeadDTO>>() {});
            AssertUtils.notEmpty(params, "缺少数据");

            ApiExtSouRecommVendorInfoDTO result = prSouRequirementPoolEventService.createVendorRecommend(params);
            if (result != null) {
                Record record = SouObjectXUtil.convertTargetObj(result, Record.class);
                record.put("projectId", result.getProject().getProjectId());
                log.info("createVendorRecommend success");
                return ResultUtil.build(queryAction, "projectId", Collections.singletonList(record), false);
            } else {
                log.info("createVendorRecommend warn");
                return QlResult.empty();
            }
        } catch (Exception e) {
            log.error("createVendorRecommend", e);
            throw new BaseException(e.getMessage());
        }
    }

    @ApiOperation("创建寻源需求")
    private QlResult createSouReq(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        List<ExtPrSouRequirementHeadDTO> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtPrSouRequirementHeadDTO>>() {});
        AssertUtils.notEmpty(params, "缺少数据");

        RecordDTO recordDTO = prSouRequirementPoolEventService.createSouReq(params);
        return ResultUtil.build(queryAction, "reqHeadId", Collections.singletonList(recordDTO), false);
    }

    @ApiOperation("创建寻源单")
    private QlResult createSou(QlQueryAction queryAction) {
        //加锁---防止重复点击
        String lockKey = StringUtils.joinWith(SouConstant.UNDER_LINE, SouConstant.PR_CREATE_SOU_LOCK, AppUserUtil.getUserName());
        if(redisUtil.hasLock(lockKey)) {
            throw new BaseException(SouConstant.PR_CREATE_SOU_LOCK_MESSAGE);
        }
        //加锁操作
        if(redisUtil.tryLock(lockKey, 1, TimeUnit.MINUTES)) {
            try {
                SouUserTypeCheckUtils.checkIsBuyer();

                List<ExtPrSouRequirementPoolCreateSouDTO> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtPrSouRequirementPoolCreateSouDTO>>() {});
                AssertUtils.notEmpty(params, "缺少数据");

                ExtPrSouRequirementCreateSouVO result = prSouRequirementPoolEventService.createSou(params.get(0));
                return ResultUtil.build(queryAction, "souType", Collections.singletonList(result), false);
            } catch (Exception e) {
                log.error("createSou Exception", e);
                throw new BaseException(e.getMessage());
            } finally {
                //释放锁
                redisUtil.unLock(lockKey);
            }
        } else {
            throw new BaseException(SouConstant.PR_CREATE_SOU_LOCK_FAIL);
        }
    }

    @ApiOperation("创建竞价寻源单")
    private QlResult createBidSou(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        List<ExtPrSouRequirementPoolCreateSouDTO> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtPrSouRequirementPoolCreateSouDTO>>() {});
        AssertUtils.notEmpty(params, "缺少数据");

        ExtPrSouRequirementCreateSouVO result = prSouRequirementPoolEventService.createBidSou(params.get(0));
        return ResultUtil.build(queryAction, "souType", Collections.singletonList(result), false);
    }

}
