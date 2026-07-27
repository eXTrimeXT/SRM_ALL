package com.midea.cloud.srm.sou.req.repo;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.util.ObjectUtil;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.meiql.api.component.paging.Page;
import com.midea.cloud.meiql.api.service.QlCondition;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryFieldWrapper;
import com.midea.cloud.meiql.api.spec.ql.QlQueryWrapper;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.api.spec.schema.QlType;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.meiql.core.repository.jooq.support.QueryParam;
import com.midea.cloud.meiql.core.util.ResultUtil;
import com.midea.cloud.meiql.core.util.SchemaUtil;
import com.midea.cloud.srm.feign.PjSouClient;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.common.enums.UserType;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.pj.sourcepubconfig.entity.SccPjSourcePubconfig;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.pj.sourcepubconfig.entity.SccPjSourcePubconfig;
import com.midea.cloud.srm.model.sou.req.SouReqApply;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.req.enums.SouReqApplyStatusEnum;
import com.midea.cloud.srm.model.sou.req.enums.SouReqHeadStatusEnum;
import com.midea.cloud.srm.sou.req.service.SouReqHeadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <pre>
 *  功能名称
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/10/8 16:18
 *  修改内容:
 * </pre>
 */
@Slf4j
@Component
public class ReqHeadRepository extends CrudRepository {
    @Autowired
    private PjSouClient pjSouClient;
    @Autowired
    private SouReqHeadService souReqHeadService;
    @Autowired
    private QlService qlService;

    public ReqHeadRepository() {
        this.register("souReqlistPage", this::souReqlistPage, false, "首页-列表查询");
        this.register("souReqgetById", this::souReqgetById, false, "首页-寻源需求详情");
        this.register("publicityListPage", this::publicityListPage, false, "公示报名大厅查询");
    }

    private QlResult souReqgetById(QlQueryAction queryAction) {
        // 获取前端传来的数据
        List<Record> records = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        // 转换对象
        List<String> ids = records.stream().map(e -> e.getString("reqHeadId")).collect(Collectors.toList());
        Long reqHeadId = Convert.toLong(ids.get(0));
        SouReqHead reqHead = qlService.readByKey(MqlType.SOU_REQ_HEAD_BUYER, reqHeadId, SouReqHead.class);
        //报名截止状态判断
        souReqHeadService.handleSignupDone();
        //更新阅读量
        qlService.updateByWrapper(QlWrappers.update(MqlType.SOU_REQ_HEAD_BUYER)
                .set(SouReqHead::getProjectViewsCount, ObjectUtil.isEmpty(reqHead.getProjectViewsCount()) ? 1 : reqHead.getProjectViewsCount() + 1)
                .eq(SouReqHead::getReqHeadId, reqHead.getReqHeadId()));
        return super.read(queryAction);
    }

    @Override
    public QlResult query(QlQueryAction queryAction) {

        if(ObjectUtils.allNotNull(queryAction.getPayload()) && queryAction.getPayload() instanceof QueryParam) {
            QueryParam param = (QueryParam) queryAction.getPayload();
            //判断是否存在待查看项目待办
            String todo = Objects.toString(param.getFilter().getValueWithoutOperator("todo"), "");
            if(StringUtils.isNotBlank(todo) && YesOrNo.YES.getValue().equals(todo))  {
                //移除非表单字段---待办辅助字段
                param.getFilter().remove("todo");
                //查询供应商待查看公示报名大厅待办列表，返回主键ID列表集合
                List<Long> reqHeadIdList = quryTodoReqHeadIdList();
                if(CollectionUtils.isNotEmpty(reqHeadIdList)) {
                    //添加待办ID过滤条件
                    Map<String, List<Long>> var = new HashMap<>(15);
                    var.put("in", reqHeadIdList);
                    param.getFilter().put(LambdaUtil.getFieldName(SouReqHead::getReqHeadId), var);
                }
            }
        }
        return super.query(queryAction);
    }

    /**
     * 待查看公示待办列表
     * @return
     */
    private List<Long> quryTodoReqHeadIdList() {
        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        QlQueryWrapper qlQueryWrapper = QlWrappers.query(MqlType.SOU_REQ_HEAD_BUYER, "req");
        qlQueryWrapper.select(QlQueryFieldWrapper.field("req", SouReqHead::getReqHeadId));
        Long vendorId = loginAppUser.getCompanyId();
        qlQueryWrapper.leftJoin(MqlType.SOU_REQ_APPLY_BUYER, "app", oncondition ->
                oncondition.eq(QlQueryFieldWrapper.field("req", SouReqHead::getReqHeadId), QlQueryFieldWrapper.field("app", SouReqApply::getReqHeadId))
                        .eq(QlQueryFieldWrapper.field("app", SouReqApply::getVendorId), vendorId)
                        .in(QlQueryFieldWrapper.field("app", SouReqApply::getApplyStatus), Arrays.asList(SouReqApplyStatusEnum.SUCCESS_SIGNUP.getCode(), SouReqApplyStatusEnum.FAIL_SIGNUP.getCode(), SouReqApplyStatusEnum.CONFIRMING_SIGNUP.getCode())));

        //截止时间未结束，也就是说截止时间大于当前时间
        qlQueryWrapper.gt(QlQueryFieldWrapper.field("req", SouReqHead::getPublicEndTime), new Date());
        //报名接受中
        qlQueryWrapper.eq(QlQueryFieldWrapper.field("req", SouReqHead::getStatus), SouReqHeadStatusEnum.APPROVED.getCode());
        //寻源公示单未关闭的，也就是关闭原因为null或者空白时
        qlQueryWrapper.and(a -> a.isNull("req", SouReqHead::getClosePublicReason).or(
                o -> o.eq("req", SouReqHead::getClosePublicReason, "")
        ));
        //关联登录账号对应的报名表数据
        qlQueryWrapper.isNull(QlQueryFieldWrapper.field("app", SouReqApply::getApplyId));

        List<Record> headList = qlService.queryByWrapper(qlQueryWrapper, Record.class);
        if(CollectionUtils.isEmpty(headList)) {
            return new ArrayList<>(15);
        }
        return headList.stream().map(record -> record.get(SouReqHead::getReqHeadId)).collect(Collectors.toList());
    }

    private QlResult publicityListPage(QlQueryAction queryAction) {
        QueryParam param = MeiQl.toValue(queryAction.getPayload(), QueryParam.class);

        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        if(Objects.isNull(loginAppUser)) {
            loginAppUser = new LoginAppUser();
        }

        QlQueryWrapper qlQueryWrapper = QlWrappers.query(MqlType.SOU_REQ_HEAD_BUYER, "req");
        qlQueryWrapper.select(QlQueryFieldWrapper.field("req", "*"));


        if(ObjectUtils.allNotNull(param) && ObjectUtils.allNotNull(param.getFilter())) {
            //标题
            String projectName = Objects.toString(param.getFilter().getValueWithoutOperator(LambdaUtil.getFieldName(SouReqHead::getProjectName)), "");
            if(StringUtils.isNotBlank(projectName))  {
                qlQueryWrapper.contains(QlQueryFieldWrapper.field("req", SouReqHead::getProjectName), projectName);
            }
            //单号
            String reqHeadNo = Objects.toString(param.getFilter().getValueWithoutOperator(LambdaUtil.getFieldName(SouReqHead::getReqHeadNo)), "");
            if(StringUtils.isNotBlank(reqHeadNo))  {
                qlQueryWrapper.contains(QlQueryFieldWrapper.field("req", SouReqHead::getReqHeadNo), reqHeadNo);
            }
            //状态
            String status = Objects.toString(param.getFilter().getValueWithoutOperator(LambdaUtil.getFieldName(SouReqHead::getStatus)), "");
            if(StringUtils.isNotBlank(status))  {
                qlQueryWrapper.eq(QlQueryFieldWrapper.field("req", SouReqHead::getStatus), status);
            }
            //创建人
            String createdFullName = Objects.toString(param.getFilter().getValueWithoutOperator(LambdaUtil.getFieldName(SouReqHead::getCreatedFullName)), "");
            if(StringUtils.isNotBlank(createdFullName))  {
                qlQueryWrapper.contains(QlQueryFieldWrapper.field("req", SouReqHead::getCreatedFullName), createdFullName);
            }
            //发布日期
            String releaseDate = Objects.toString(param.getFilter().getValueWithoutOperator(LambdaUtil.getFieldName(SouReqHead::getReleaseDate)), "");
            if(StringUtils.isNotBlank(releaseDate))  {
                String[] releaseDateArrarys = releaseDate.split(SrmConstant.SIG_3);
                qlQueryWrapper.ge(QlQueryFieldWrapper.field("req", SouReqHead::getReleaseDate), releaseDateArrarys[0]);
                qlQueryWrapper.le(QlQueryFieldWrapper.field("req", SouReqHead::getReleaseDate), releaseDateArrarys[1]);
            }

            //供应商账号时关联报名表查询
            if(UserType.VENDOR.name().equals(loginAppUser.getUserType())) {
                Long vendorId = loginAppUser.getCompanyId();
                qlQueryWrapper.leftJoin(MqlType.SOU_REQ_APPLY_BUYER, "app", oncondition ->
                        oncondition.eq(QlQueryFieldWrapper.field("req", SouReqHead::getReqHeadId), QlQueryFieldWrapper.field("app", SouReqApply::getReqHeadId))
                                    .eq(QlQueryFieldWrapper.field("app", SouReqApply::getVendorId), vendorId));

                //待办附加条件
                String todo = Objects.toString(param.getFilter().getValueWithoutOperator("todo"), "");
                if(StringUtils.isNotBlank(todo) && YesOrNo.YES.getValue().equals(todo))  {
                    //截止时间未结束，也就是说截止时间大于当前时间
                    qlQueryWrapper.gt(QlQueryFieldWrapper.field("req", SouReqHead::getPublicEndTime), new Date());
                    //报名接受中
                    qlQueryWrapper.eq(QlQueryFieldWrapper.field("req", SouReqHead::getStatus), SouReqHeadStatusEnum.APPROVED.getCode());
                    //寻源公示单未关闭的，也就是关闭原因为null或者空白时
                    qlQueryWrapper.and(a -> a.isNull("req", SouReqHead::getClosePublicReason).or(
                            o -> o.eq("req", SouReqHead::getClosePublicReason, "")
                    ));
                    //关联登录账号对应的报名表数据
                    qlQueryWrapper.and(a -> a.isNull("app", SouReqApply::getApplyId).or(
                            o -> o.in("app", SouReqApply::getApplyStatus, Arrays.asList(SouReqApplyStatusEnum.NO_SIGNUP.getCode(), SouReqApplyStatusEnum.FAIL_SIGNUP.getCode(), SouReqApplyStatusEnum.WITHDRAW.getCode()))
                    ));
                }
            }


        }

        qlQueryWrapper.orderByDesc(QlQueryFieldWrapper.field("req", SouReqHead::getReqHeadId));
        Page<Record> page = qlService.queryPageByWrapper(qlQueryWrapper, param.getPage(), Record.class);

        if(CollectionUtils.isNotEmpty(page.getRecords())) {
            List<Record> headList = page.getRecords();

            List<Long> reqHeadIdList = headList.stream().map(r -> r.get(SouReqHead::getReqHeadId)).collect(Collectors.toList());

            List<Record> applyList = qlService.queryByWrapper(QlWrappers.query(MqlType.SOU_REQ_APPLY_BUYER)
                    .in(SouReqHead::getReqHeadId, reqHeadIdList).eq(SouReqApply::getVendorId, loginAppUser.getCompanyId()), Record.class);

            Map<Long, List<Record>> applyMap = applyList.stream().collect(Collectors.groupingBy(r -> r.get(SouReqApply::getReqHeadId)));

            headList.stream().forEach(head -> {
                head.put("souReqApplyList", applyMap.getOrDefault(head.get(SouReqHead::getReqHeadId), new ArrayList<>(15)));
            });

        }

        return ResultUtil.build(queryAction, LambdaUtil.getFieldName(SouReqHead::getReqHeadId), page, false);
    }

    private QlResult souReqlistPage(QlQueryAction queryAction) {
        //报名截止状态判断
        souReqHeadService.handleSignupDone();
        QueryParam param = MeiQl.toValue(queryAction.getPayload(), QueryParam.class);
        Map<String, Object> status = new HashMap<>(50);
        status.put("eq", SouReqHeadStatusEnum.APPROVED.getCode());
        Map<String, Object> isPublic = new HashMap<>(50);
        isPublic.put("eq", Enable.Y.name());
        param.getFilter().put("status", status);
        param.getFilter().put("isPublic", isPublic);
        queryAction.setPayload(param);
        return super.query(queryAction);
    }

    @Override
    protected void afterRead(QlQueryAction queryAction, Collection<Record> records) {
        super.afterRead(queryAction, records);
        if (ObjectUtil.isNotEmpty(records)) {
            records.forEach(record -> {
                record.put("pjSourcePubconfig", null);
                if (ObjectUtil.isNotEmpty(record.get(SouReqHead::getPubconfigId))) {
                    //查询寻源公示模板信息
                    SccPjSourcePubconfig pjSourcePubconfig = pjSouClient.queryPubconfig(record.get(SouReqHead::getPubconfigId));
                    record.put("pjSourcePubconfig", pjSourcePubconfig);
                }
            });
        }
    }

    @Override
    public QlCondition beforeQuery(QlQueryAction queryAction, QueryParam payload) {
        souReqHeadService.handleSignupDone();
        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        QlCondition condition = MeiQl.newCondition();
        QlCondition subCondition = MeiQl.newCondition();
        QlType qlType = SchemaUtil.getType(queryAction.getType());
        subCondition.or(MeiQl.newCondition().eq("status", "APPROVED"))
                .or(MeiQl.newCondition()
                        .in("status", Arrays.asList("APPROVED", "SIGNUP_DONE", "CLOSED", "FAIL_SIGNUP"))
                        .exists(MqlType.SOU_REQ_APPLY_BUYER, "h", MeiQl.newCondition()
                                .eq("h", "reqHeadId", QlQueryFieldWrapper.field(qlType.getTableName(), "reqHeadId"))
                                .eq("h", "vendorId", loginAppUser.getCompanyId())
                                .in("h", "applyStatus", Arrays.asList(SouReqApplyStatusEnum.SUCCESS_SIGNUP.getCode(), SouReqApplyStatusEnum.FAIL_SIGNUP.getCode()))
                        )
                );
        condition = condition.and(subCondition);
        return condition;
    }

}
