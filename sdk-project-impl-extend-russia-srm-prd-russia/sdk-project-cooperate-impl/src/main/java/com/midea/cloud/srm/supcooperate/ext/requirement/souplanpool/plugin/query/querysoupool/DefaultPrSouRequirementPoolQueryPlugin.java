package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.plugin.query.querysoupool;

import com.github.pagehelper.page.PageMethod;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.entity.PrRequirementHead;
import com.midea.cloud.srm.model.pm.pr.division.dto.DivisionCategoryQueryDTO;
import com.midea.cloud.srm.model.pm.pr.division.entity.DivisionCategory;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementAttach;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementVendor;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementGroupTypeEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.vo.ExtPrSouRequirementHeadVO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.vo.ExtPrSouRequirementPoolQueryVO;
import com.midea.cloud.srm.pr.division.service.IDivisionCategoryService;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplan.mapper.ExtPrSouRequirementHeadMapper;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.query.querysouprpools.IPrSouRequirementPoolQueryPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.query.querysouprpools.PrSouRequirementPoolQueryContext;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 招标计划池 - 列表查询插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/10
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultPrSouRequirementPoolQueryPlugin implements IPrSouRequirementPoolQueryPlugin {

    @Autowired
    private ExtPrSouRequirementHeadMapper extPrSouRequirementHeadMapper;
    @Autowired
    private QlService qlService;
    @Autowired
    private IDivisionCategoryService divisionCategoryService;
    @Autowired
    private RbacClient rbacClient;

    @Override
    @ApiOperation("校验操作条件/权限")
    public PrSouRequirementPoolQueryContext judgeQuerySouPoolAuth(PrSouRequirementPoolQueryContext context) {
        context.getParam().formatParams();
        return context;
    }

    @Override
    @ApiOperation("执行处理")
    public PrSouRequirementPoolQueryContext executeQuerySouPool(PrSouRequirementPoolQueryContext context) {
        // 1: 查询数据
        if (context.getParam().getPageNum() != null && context.getParam().getPageSize() != null) {
            PageMethod.startPage(context.getParam().getPageNum(), context.getParam().getPageSize());
        }
        List<ExtPrSouRequirementPoolQueryVO> resultList = SouObjectXUtil.convertList(extPrSouRequirementHeadMapper.querySouPrPool(context.getParam()), ExtPrSouRequirementPoolQueryVO.class);
        context.setResult(resultList);
        if (resultList.isEmpty()) { return context; }
        // 2: 查询额外信息
        Set<Long> requirementIds = resultList.stream().map(ExtPrSouRequirementPoolQueryVO::getRequirementHeadId).collect(Collectors.toSet());
        // 2.1: 查询采购申请信息
        Map<Long/* requirementHeadId */, PrRequirementHead> requirementHeadMap = qlService.readByKeys(PrRequirementHead.class.getSimpleName(),
                new ArrayList<>(requirementIds), PrRequirementHead.class)
                .stream().collect(Collectors.toMap(PrRequirementHead::getRequirementHeadId, Function.identity()));
        resultList.forEach(result -> SouObjectXUtil.mergeProperties(requirementHeadMap.get(result.getRequirementHeadId()), result));
        // 2.2: 查询招标计划
        Map<Long/* requirementHeadId */, ExtPrSouRequirementHead> souPrHeadMap = qlService.readByKeys(ExtPrSouRequirementHead.class.getSimpleName(),
                new ArrayList<>(requirementIds), ExtPrSouRequirementHead.class)
                .stream().collect(Collectors.toMap(ExtPrSouRequirementHead::getRequirementHeadId, Function.identity()));
        resultList.forEach(result -> BeanUtils.copyProperties(souPrHeadMap.get(result.getRequirementHeadId()), result,"extPublicEndTime"));
        // 2.3: 查询招标计划工作成员
        Map<Long/* requirementHeadId */, List<ExtPrSouRequirementGroup>> souGroupMap = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementGroup.class)
                .in(ExtPrSouRequirementGroup::getRequirementHeadId, requirementIds), ExtPrSouRequirementGroup.class)
                .stream().collect(Collectors.groupingBy(ExtPrSouRequirementGroup::getRequirementHeadId));
        resultList.forEach(result -> {
            List<ExtPrSouRequirementGroup> groupList = souGroupMap.get(result.getRequirementHeadId());
            if (CollectionUtils.isNotEmpty(groupList)) {
                ExtPrSouRequirementGroup souGroup = groupList.stream().filter(e -> PrSouRequirementGroupTypeEnum.SOU.name().equals(e.getGroupType())).findFirst().orElse(null);
                if (souGroup != null) {
                    result.setSouGroupUserId(souGroup.getUserId());
                    result.setSouGroupUsername(souGroup.getUsername());
                    result.setSouGroupFullName(souGroup.getFullName());
                    result.setSouGroupPhone(souGroup.getPhone());
                    result.setSouGroupDepartmentName(souGroup.getDepartmentName());
                }
                ExtPrSouRequirementGroup techGroup = groupList.stream().filter(e -> PrSouRequirementGroupTypeEnum.TECH.name().equals(e.getGroupType())).findFirst().orElse(null);
                if (techGroup != null) {
                    result.setTechGroupUserId(techGroup.getUserId());
                    result.setTechGroupUsername(techGroup.getUsername());
                    result.setTechGroupFullName(techGroup.getFullName());
                    result.setTechGroupPhone(techGroup.getPhone());
                    result.setTechGroupWorkYear(techGroup.getWorkYear());
                    result.setTechGroupDepartmentName(techGroup.getDepartmentName());
                }
                ExtPrSouRequirementGroup vendorGroup = groupList.stream().filter(e -> PrSouRequirementGroupTypeEnum.VENDOR.name().equals(e.getGroupType())).findFirst().orElse(null);
                if (vendorGroup != null) {
                    result.setVendorGroupUserId(vendorGroup.getUserId());
                    result.setVendorGroupUsername(vendorGroup.getUsername());
                    result.setVendorGroupFullName(vendorGroup.getFullName());
                    result.setVendorGroupPhone(vendorGroup.getPhone());
                    result.setVendorGroupDepartmentName(vendorGroup.getDepartmentName());
                }
            }
        });
        // 2.4: 查询招标计划推荐供应商
        Map<Long/* requirementHeadId */, List<ExtPrSouRequirementVendor>> souVendorMap = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementVendor.class)
                .in(ExtPrSouRequirementVendor::getRequirementHeadId, requirementIds), ExtPrSouRequirementVendor.class)
                .stream().collect(Collectors.groupingBy(ExtPrSouRequirementVendor::getRequirementHeadId));
        resultList.forEach(result -> {
            List<ExtPrSouRequirementVendor> vendorList = souVendorMap.get(result.getRequirementHeadId());
            if (CollectionUtils.isNotEmpty(vendorList)) {
                BeanUtils.copyProperties(vendorList.get(0), result);
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
