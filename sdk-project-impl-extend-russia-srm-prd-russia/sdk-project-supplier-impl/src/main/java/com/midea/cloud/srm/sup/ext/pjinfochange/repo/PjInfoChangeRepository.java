package com.midea.cloud.srm.sup.ext.pjinfochange.repo;

import cn.hutool.core.lang.func.LambdaUtil;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.enums.sup.InfoChangeStatus;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.meiql.api.service.QlCondition;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Payload;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryFieldWrapper;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.api.spec.schema.QlType;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.ProxyRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.meiql.core.repository.jooq.support.QueryParam;
import com.midea.cloud.meiql.core.util.SchemaUtil;
import com.midea.cloud.srm.feign.SCExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.base.systemConfigure.dto.SystemConfigureDTO;
import com.midea.cloud.srm.model.common.enums.UserType;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.pm.pr.division.entity.DivisionCategory;
import com.midea.cloud.srm.model.rbac.role.entity.Role;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.supplier.change.dto.InfoChangeDTO;
import com.midea.cloud.srm.model.supplier.change.entity.InfoChange;
import com.midea.cloud.srm.model.supplierauth.review.entity.CateJournal;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sup.ext.pjinfochange.mapper.PjInfoChangeMapper;
import com.midea.cloud.srm.utils.MqlType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 供应商信息变更二开自定义action
 *
 * @author LUXC18
 * @date 2023/10/08 17:42
 */
@Slf4j
@Component
public class PjInfoChangeRepository extends ProxyRepository {
    @Autowired
    private QlOpenClient qlOpenClient;
    @Autowired
    private QlService qlService;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private RbacClient rbacClient;

    @Autowired
    private PjInfoChangeMapper pjInfoChangeMapper;

    @Autowired
    private SCExtClient scExtClient;

    private final String INFO_CHANGE_ROLE_LIST = "INFO_CHANGE_ROLE_LIST";

    public PjInfoChangeRepository() {
        this.register("queryInfoChangePage", this::queryInfoChangePage, true, "二开-查询信息变更列表");

    }

    /**
     * 信息变更列表查询，增加权限过滤
     *
     * @return
     */
    private QlResult queryInfoChangePage(QlQueryAction qlQueryAction) {
        //查询 供应商清单列表,自定义查询action,做权限过滤
        //服务范围里面对应的二级品类和该登录人员的品类分工的品类对应的二级品类有交集
        List<Long> conditionIdList = new ArrayList<>();

        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();

        // 创建人也能查看
        Long userId = loginAppUser.getUserId();
        // 特定角色人员可以查看全部
        SystemConfigureDTO systemConfigure = baseClient.getSystemConfigure(INFO_CHANGE_ROLE_LIST);
        if (systemConfigure != null) {
            List<Role> rolePermissions = loginAppUser.getRolePermissions();
            String paramValue = systemConfigure.getParamValue();
            for (Role rolePermission : rolePermissions) {
                if (paramValue.contains(rolePermission.getRoleCode())) {
                    return this.query(qlQueryAction);
                }
            }
        }
        List<InfoChange> infoChanges = qlService.queryByWrapper(QlWrappers.query(MqlType.INFO_CHANGE)
                .select(InfoChange::getChangeId)
                .eq(InfoChange::getCreatedId, userId)
                .orderByDesc(InfoChange::getChangeId), InfoChange.class
        );
        if (CollectionUtils.isNotEmpty(infoChanges)) {
            List<Long> changeIdList = infoChanges.stream().map(InfoChange::getChangeId).collect(Collectors.toList());
            conditionIdList.addAll(changeIdList);
        }


        List<String> partNameList = categoryFullNameAsTwoLevel(userId);
        // 没权限查看
        if (CollectionUtils.isEmpty(partNameList)) {
            log.info("---没有品类分工权限");
        } else {
            // 前端传入供应商品类信息,含前2级的全路径名
            List<Record> cateJournalList = qlService.queryByWrapper(QlWrappers.query(MqlType.NPM_CATE_JOURNAL_CHANGE)
                    .select("formId")
                    .in("categoryFullName", partNameList)
                    .groupBy("formId")
                    .orderByDesc("formId")
                    , Record.class);
            // 没权限查看
            if (CollectionUtils.isEmpty(cateJournalList)) {
                log.info("---没有对应品类的供应商");
            } else {
                for (Record record : cateJournalList) {
                    conditionIdList.add(record.getLong("formId"));
                }
            }
        }

        if (CollectionUtils.isEmpty(conditionIdList)) {
            log.info("---没有可以查询的信息变更数据");
            return new QlResult();
        }

        //去重减少数据量
        conditionIdList = conditionIdList.stream().distinct().collect(Collectors.toList());

        QueryParam queryParam = PayloadWrapper.of(qlQueryAction.getType(), qlQueryAction.getPayload()).asQuery();
        queryParam.getFilter().setValue("changeId", "in", conditionIdList);
        qlQueryAction.setPayload(queryParam);
        QlResult qlResult = this.query(qlQueryAction);
        return qlResult;
    }

    private List<String> categoryFullNameAsTwoLevel(Long personInChargeUserId) {
        Map<String, Object> param = new HashMap<>(16);
        param.put("personInChargeUserId", personInChargeUserId);
        List<Map<String, Object>> categoryFullIdObjectList = pjInfoChangeMapper.listTwoLevelWithDivisionCategory(param);

        List<String> categoryFullNameList = new ArrayList<>(16);

        if(CollectionUtils.isEmpty(categoryFullIdObjectList)) {
            return categoryFullNameList;
        }
        List<Long> categoryIdList = new ArrayList<>(16);
        List<Long> finalCategoryIdList = categoryIdList;
        categoryFullIdObjectList.stream().forEach(category -> {
            String categoryFullId = MapUtils.getString(category, LambdaUtil.getFieldName(CateJournal::getCategoryFullId), "");
            String[] categoryIdArrarys = categoryFullId.split(SrmConstant.SHORT_LINE);
            Arrays.stream(categoryIdArrarys).forEach(categoryId -> {
                try {
                    finalCategoryIdList.add(Long.valueOf(categoryId));
                } catch (Exception e) {
                    log.error("categoryFullNameAsTwoLevel Exception", e);
                }
            });
        });
        categoryIdList = categoryIdList.stream().distinct().collect(Collectors.toList());
        if(CollectionUtils.isEmpty(categoryIdList)) {
            return categoryFullNameList;
        }
        List<PurchaseCategory> purchaseCategoryList = baseClient.listCategoryByIds(categoryIdList);
        Map<Long, PurchaseCategory> purchaseCategoryMap = purchaseCategoryList.stream().collect(Collectors.toMap(PurchaseCategory::getCategoryId, Function.identity(), (k1, k2) -> k2));

        categoryFullIdObjectList.stream().forEach(category -> {
            String categoryFullId = MapUtils.getString(category, LambdaUtil.getFieldName(CateJournal::getCategoryFullId), "");
            String[] categoryIdArrarys = categoryFullId.split(SrmConstant.SHORT_LINE);
            List<String> categoryNameList = new ArrayList<>(categoryIdArrarys.length);
            Arrays.stream(categoryIdArrarys).forEach(categoryId -> {
                try {
                    categoryNameList.add(purchaseCategoryMap.getOrDefault(Long.valueOf(categoryId), new PurchaseCategory()).getCategoryName());
                } catch (Exception e) {
                    log.error("categoryFullNameAsTwoLevel Exception", e);
                }
            });
            categoryFullNameList.add(categoryNameList.stream().collect(Collectors.joining(SrmConstant.SHORT_LINE)));
        });
        return categoryFullNameList;
    }

    @Override
    protected QlCondition beforeQuery(QlQueryAction queryAction, QueryParam payload) {
        QlCondition condition = MeiQl.newCondition();
        LoginAppUser user = AppUserUtil.getLoginAppUser();
        QlType qlType = SchemaUtil.getType(queryAction.getType());
        if (!UserType.BUYER.name().equals(user.getUserType())) {
            Long companyId = user.getCompanyId();
            if (companyId == null) {
                User userByUserName = rbacClient.getUserByUserName(user.getUsername());
                if (userByUserName != null) {
                    log.info("特殊处理,获取的用户信息" + JSONObject.toJSONString(userByUserName));
                    companyId = userByUserName.getCompanyId();
                }
            }
            condition.eq("companyId", companyId);
            condition.notExists(queryAction.getType(), "v", MeiQl.newCondition()
                    .eq(InfoChangeDTO::getChangeStatus, InfoChangeStatus.DRAFT.getValue())
                    .eq(InfoChangeDTO::getUserType, UserType.BUYER.name())
                    .eq("v", InfoChangeDTO::getChangeId, QlQueryFieldWrapper.field(qlType.getTableName(), InfoChangeDTO::getChangeId))
            );
        } else {
            condition.notExists(queryAction.getType(), "v", MeiQl.newCondition()
                    .eq(InfoChangeDTO::getChangeStatus, InfoChangeStatus.DRAFT.getValue())
                    .eq(InfoChangeDTO::getUserType, UserType.VENDOR.name())
                    .eq("v", InfoChangeDTO::getChangeId, QlQueryFieldWrapper.field(qlType.getTableName(), InfoChangeDTO::getChangeId))
            );
        }
        return condition;
    }

    @Override
    public QlResult query(QlQueryAction queryAction) {
        return super.query(noProxy(queryAction));
    }
}
