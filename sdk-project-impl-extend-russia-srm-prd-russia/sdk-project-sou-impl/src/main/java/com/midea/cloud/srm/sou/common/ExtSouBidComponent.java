package com.midea.cloud.srm.sou.common;

import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.meiql.api.function.SFunction;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.meiql.api.function.SFunction;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryFieldWrapper;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.rbac.role.entity.Role;
import com.midea.cloud.srm.model.rbac.role.entity.RoleUser;
import com.midea.cloud.srm.model.rbac.user.dto.RoleDto;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouDemand;
import com.midea.cloud.srm.model.supcooperate.enums.ExtRequireFromEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Component
@Slf4j
public class ExtSouBidComponent {

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private QlService qlService;

    @Autowired
    private QlOpenClient qlOpenClient;

    private static ExtSouBidComponent extSouBidComponent;

    @PostConstruct
    private void inite() {
        extSouBidComponent = this;
    }

    private ExtSouBidComponent() {

    }

    public static ExtSouBidComponent getInstance() {
        return extSouBidComponent;
    }

    /**
     * 判断是否：10万以上且在招标范围内
     * @param categoryId
     * @param budget
     * @return
     */
    public Boolean isUpperHundredThousandAsBid(Long categoryId, BigDecimal budget) {
        if(ObjectUtils.anyNull(categoryId, budget)) {
            return false;
        }
        //10万以下
        if(BigDecimal.TEN.compareTo(budget) == 1) {
            return false;
        }
        return isBid(categoryId);
    }

    /**
     * 判断是否招标范围
     * @param categoryId
     * @return
     */
    public Boolean isBid(Long categoryId) {
        //查询品类
        PurchaseCategory category = new PurchaseCategory();
        category.setCategoryId(categoryId);
        category = baseClient.getPurchaseCategoryByParm(category);
        String ifBid = (String) category.getExtensions().get("ifBid");
        //属于招标范围
        if(YesOrNo.YES.getValue().equals(ifBid)) {
            return true;
        }
        return false;
    }

    /**
     * 是否特殊招标
     * @param projectId
     * @return
     */
    public Boolean isSpecialSou(Long projectId) {
        List<Record> souDemandList = qlService.queryByWrapper(QlWrappers.query(MqlType.NPM_SOU_DEMAND).eq(ExtSouDemand::getProjectId, projectId), Record.class);
        if(CollectionUtils.isEmpty(souDemandList)) {
            log.info(MessageFormat.format("是否特殊招标{0}结果为：{1}", projectId, "否"));
            return false;
        }

        List<RecordDTO> requirementHeadList = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query(MqlType.PURCHASE_REQUIREMENT_HEAD).in(RequirementHead::getRequirementHeadNum, souDemandList.stream().map(r -> r.get(ExtSouDemand::getApplicantNo)).collect(Collectors.toList())));
        if(CollectionUtils.isEmpty(requirementHeadList)) {
            log.info(MessageFormat.format("是否特殊招标{0}结果为：{1}", projectId, "否"));
            return false;
        }


        List<RecordDTO> extRequirementHeadList = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query(MqlType.EXT_PR_SOU_REQUIREMENT_HEAD).in(ExtPrSouRequirementHead::getRequirementHeadId, requirementHeadList.stream().map(r -> r.get(RequirementHead::getRequirementHeadId)).collect(Collectors.toList())).eq(ExtPrSouRequirementHead::getRequireFrom, ExtRequireFromEnum.SPECIAL_SOU.getCode()));
        if(CollectionUtils.isNotEmpty(extRequirementHeadList)) {
            log.info(MessageFormat.format("是否特殊招标{0}结果为：{1}", projectId, "是"));
            return true;
        }

        log.info(MessageFormat.format("是否特殊招标{0}结果为：{1}", projectId, "否"));
        return false;
    }

    public static <F> String fieldName(SFunction<F, ?> sFunction) {
        return QlQueryFieldWrapper.field(sFunction).getFieldName();
    }

    public Boolean checkIsGroupBidding(Long userId) {
        List<RecordDTO> roleList = qlOpenClient.query(ContextPath.RBAC, QlOpenWrappers.query(MqlType.SCC_RBAC_ROLE).eq(RoleDto::getRoleCode, SrmConstant.RESPONSIBLE_PERSON_OF_GROUP_BIDDING));
        if(CollectionUtils.isEmpty(roleList)) {
            return false;
        }
        List<RecordDTO> userRoleList = qlOpenClient.query(ContextPath.RBAC, QlOpenWrappers.query(MqlType.SCC_RBAC_ROLE_USER).eq(RoleUser::getUserId, userId).in(RoleUser::getRoleId, roleList.stream().map(r -> r.get(Role::getRoleId)).collect(Collectors.toList())));
        if(CollectionUtils.isEmpty(userRoleList)) {
            return false;
        }
        return true;
    }

    @SneakyThrows(value = {Exception.class})
    public static LocalDate formateLocalDate(RecordDTO record, String fieldName) {
        Object value = record.get(fieldName);
        if(Objects.isNull(value)) {
            return null;
        }
        log.info("formateLocalDate {0}, {1}", value, value.getClass().getSimpleName());
        if(value instanceof String) {
            return DateUtil.dateToLocalDate(DateUtil.parseDate((String) value));
        }
        if(value instanceof Date) {
            return DateUtil.dateToLocalDate((Date) value);
        }
        if(value instanceof LocalDate) {
            return (LocalDate) value;
        }
        return DateUtil.dateToLocalDate(DateUtil.parseDate((String) value));
    }

}
