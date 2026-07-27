package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.plugin.event.createvendorrecommend;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.RecommVendorExtClient;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.entity.PrRequirementHead;
import com.midea.cloud.srm.model.pm.pr.requirement.enums.RequirementApproveStatus;
import com.midea.cloud.srm.model.sou.enums.RecommvendorSourceFromEnum;
import com.midea.cloud.srm.model.sou.enums.SouRecommvendorTypeEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouRecommVendorInfoDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouProjectDto;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouFile;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouRecommendedVendor;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.dto.ExtPrSouRequirementHeadDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementAttach;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementVendor;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementGroupTypeEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementStatusEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.vo.ExtPrSouRequirementHeadVO;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.service.PrSouRequirementCreateValidService;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.createvendorrecommend.IPrSouRequirementCreateVendorRecommendPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.createvendorrecommend.PrSouRequirementCreateVendorRecommendContext;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 招标计划池 - 推荐供应商插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/11
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultPrSouRequirementCreateVendorRecommendPlugin implements IPrSouRequirementCreateVendorRecommendPlugin {

    @Autowired
    private QlService qlService;

    @Autowired
    private RecommVendorExtClient recommVendorExtClient;

    @Autowired
    private PrSouRequirementCreateValidService prSouRequirementCreateValidService;

    @Override
    @ApiOperation("校验操作条件/权限")
    public PrSouRequirementCreateVendorRecommendContext judgeVendorRecommendAuth(PrSouRequirementCreateVendorRecommendContext context) {
        AssertUtils.notEmpty(context.getParams(), "请选择需要操作的数据");
        context.setParams(context.getParams().stream().filter(e -> e.getRequirementHeadId() != null).collect(Collectors.toList()));
        AssertUtils.notEmpty(context.getParams(), "请选择需要操作的数据");
        List<ExtPrSouRequirementHead> souPrHeadList = qlService.readByKeys(ExtPrSouRequirementHead.class.getSimpleName(),
                new ArrayList<>(context.getParams().stream().map(ExtPrSouRequirementHeadDTO::getRequirementHeadId).collect(Collectors.toSet())), ExtPrSouRequirementHead.class);
        AssertUtils.notEmpty(context.getParams(), "请选择需要操作的招标计划数据");
        List<PrRequirementHead> prHeadList = qlService.readByKeys(PrRequirementHead.class.getSimpleName(),
                new ArrayList<>(souPrHeadList.stream().map(ExtPrSouRequirementHead::getRequirementHeadId).collect(Collectors.toSet())), PrRequirementHead.class);
        Map<Long, PrRequirementHead> prHeadMap = prHeadList.stream().collect(Collectors.toMap(PrRequirementHead::getRequirementHeadId, Function.identity()));
        boolean isRequirementOk = prHeadList.stream().allMatch(e -> RequirementApproveStatus.APPROVED.equals(e.getAuditStatus()));
        AssertUtils.isTrue(isRequirementOk, "只能操作已审批的招标计划");
        souPrHeadList.forEach(e -> {
            PrRequirementHead prHead = prHeadMap.get(e.getRequirementHeadId());

            AssertUtils.isFalse(PrSouRequirementStatusEnum.CANCEL.name().equals(e.getSouReqStatus()), "招标计划[{0}]已取消", prHead.getRequirementHeadNum());
            AssertUtils.isTrue(Enable.N.equals(e.getHasCreateVendorRecommend()), "招标计划[{0}]已生成推荐供应商", prHead.getRequirementHeadNum());
            AssertUtils.isTrue(Enable.Y.equals(e.getHasAssigned()), "招标计划[{0}]未分配完成，不能创建推荐供应商", prHead.getRequirementHeadNum());
        });
        String categoryCode = prHeadList.get(0).getCategoryCode();
        boolean isCategoryOk = prHeadList.stream().allMatch(e -> e.getCategoryCode().equals(categoryCode));
        AssertUtils.isTrue(isCategoryOk, "所勾选的招标计划必须为相同品类");

        //合并申请单号校验
        prSouRequirementCreateValidService.requirementCreateValid(context.getParams().stream().map(ExtPrSouRequirementHeadDTO::getRequirementHeadId).collect(Collectors.toList()), SouTypeEnum.recomm.name());

        context.setSouPrHeadList(souPrHeadList);
        context.setPrHeadList(prHeadList);
        return context;
    }

    @Override
    @ApiOperation("前置处理(构造数据)")
    public PrSouRequirementCreateVendorRecommendContext beforeVendorRecommend(PrSouRequirementCreateVendorRecommendContext context) {
        Set<Long> requirementHeadIds = context.getSouPrHeadList().stream().map(ExtPrSouRequirementHead::getRequirementHeadId).collect(Collectors.toSet());
        // 1: 查询招标计划信息集合
        List<ExtPrSouRequirementHeadVO> souPrHeadList = new ArrayList<>(context.getPrHeadList().size());
        {
            souPrHeadList = SouObjectXUtil.convertList(context.getPrHeadList(), ExtPrSouRequirementHeadVO.class);

            Map<Long/* requirementHeadId */, ExtPrSouRequirementHead> souPrHeadMap = context.getSouPrHeadList().stream()
                    .collect(Collectors.toMap(ExtPrSouRequirementHead::getRequirementHeadId, Function.identity()));
            souPrHeadList.forEach(pr -> pr.setSouReqHead(souPrHeadMap.get(pr.getRequirementHeadId())));

            // 1.1: 查询招标计划工作小组
            //noinspection unchecked
            Map<Long/* requirementHeadId */, List<ExtPrSouRequirementGroup>> groupMap = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementGroup.class)
                            .in(ExtPrSouRequirementGroup::getRequirementHeadId, requirementHeadIds)
                            .orderByAsc(ExtPrSouRequirementGroup::getSortIndex), ExtPrSouRequirementGroup.class)
                    .stream().collect(Collectors.groupingBy(ExtPrSouRequirementGroup::getRequirementHeadId));
            souPrHeadList.forEach(pr -> pr.setSouGroupList(groupMap.get(pr.getRequirementHeadId())));
            // 1.2: 查询招标计划推荐供应商
            //noinspection unchecked
            Map<Long/* requirementHeadId */, List<ExtPrSouRequirementVendor>> vendorMap = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementVendor.class)
                            .in(ExtPrSouRequirementVendor::getRequirementHeadId, requirementHeadIds)
                            .orderByAsc(ExtPrSouRequirementVendor::getSortIndex), ExtPrSouRequirementVendor.class)
                    .stream().collect(Collectors.groupingBy(ExtPrSouRequirementVendor::getRequirementHeadId));
            souPrHeadList.forEach(pr -> pr.setSouVendorList(vendorMap.get(pr.getRequirementHeadId())));
        }
        souPrHeadList = souPrHeadList.stream().sorted((o1, o2)
                        -> org.apache.commons.lang3.ObjectUtils.defaultIfNull(o1.getSouReqHead().getTotalAmountByTenKilo(), BigDecimal.ZERO)
                        .compareTo(org.apache.commons.lang3.ObjectUtils.defaultIfNull(o2.getSouReqHead().getAfterTotalAmountByTenKilo(), BigDecimal.ZERO)))
                .collect(Collectors.toList());
        // 2: 根据详情信息，构造一个推荐供应商单dto
        ApiExtSouRecommVendorInfoDTO souRecommVendorInfoDTO = new ApiExtSouRecommVendorInfoDTO();

        //推荐单来源
        ExtSouRecommendedVendor recommendedVendor = new ExtSouRecommendedVendor();
        recommendedVendor.setSourceFrom(RecommvendorSourceFromEnum.BID.name());
        recommendedVendor.setRcommendType(SouRecommvendorTypeEnum.RECOMM.getCode());
        souRecommVendorInfoDTO.setSouRecommendedVendor(recommendedVendor);
        //推荐供应商基本信息
        ExtSouProjectDto project = new ExtSouProjectDto();
        //推荐供应商列表
        List<ExtSouVendor> recommVendorList = new ArrayList<>();
        //推荐供应商附件
        extracted(souPrHeadList, souRecommVendorInfoDTO, recommendedVendor, project);
        ApiExtSouRecommVendorInfoDTO o = souRecommVendorInfoDTO;
        context.putX("datax", o);
        return context;
    }

    /**
     * 推荐供应商
     * @param souPrHeadList 参数
     * @param souRecommVendorInfoDTO 参数
     * @param recommendedVendor 参数
     * @param project 参数
     */
    private void extracted(List<ExtPrSouRequirementHeadVO> souPrHeadList, ApiExtSouRecommVendorInfoDTO souRecommVendorInfoDTO, ExtSouRecommendedVendor recommendedVendor, ExtSouProjectDto project) {
        for (ExtPrSouRequirementHeadVO souPrHeadVO : souPrHeadList) {
            //板块，取申请单
            project.setExtOrgBuName(souPrHeadVO.getSouReqHead().getOrgBuName());
            project.setExtOrgBuCode(souPrHeadVO.getSouReqHead().getOrgBuCode());
            project.setExtOrgBuId(souPrHeadVO.getSouReqHead().getOrgBuId());
            //公司名称,取申请单
            project.setExtOrgOuName(souPrHeadVO.getOrgName());
            project.setExtOrgOuCode(souPrHeadVO.getOrgCode());
            project.setExtOrgOuId(souPrHeadVO.getOrgId());
            //需求部门
            project.setExtApplicantDepart(souPrHeadVO.getCeeaDepartmentName());
            //寻源单名号,申请单号带出
            project.setSouNo(souPrHeadVO.getSouReqHead().getSouNo());
            //创建人
            project.setCreatedFullName(souPrHeadVO.getCreatedFullName());
            //创建时间
            project.setCreationDate(souPrHeadVO.getCreationDate());
            //最后更新时间
            project.setLastUpdateDate(souPrHeadVO.getLastUpdateDate());

            //是否公示
            recommendedVendor.setPublishFlag(org.apache.commons.lang3.ObjectUtils.defaultIfNull(souPrHeadVO.getSouReqHead().getNeedPublic(), Enable.N).name());

            ExtPrSouRequirementGroup extPrSouRequirementGroup = souPrHeadVO.getSouGroupList()
                    .stream().filter(e -> e.getGroupType().equals(PrSouRequirementGroupTypeEnum.TECH.name())).findAny().orElse(null);
            if (!ObjectUtils.isEmpty(extPrSouRequirementGroup)) {
                //技术负责人
                project.setExtTechPrincipal(extPrSouRequirementGroup.getFullName());
                //电话
                project.setTel(extPrSouRequirementGroup.getPhone());
            }
            project.setProjectStatus("DRAFT");
            ExtPrSouRequirementGroup souGroup = souPrHeadVO.getSouGroupList()
                    .stream().filter(e -> e.getGroupType().equals(PrSouRequirementGroupTypeEnum.SOU.name())).findAny().orElse(null);
            if (!ObjectUtils.isEmpty(souGroup)) {
                //招标负责人
                project.setExtSouPrincipal(souGroup.getFullName());
            }
            //申请单号,来源单据号，//合并申请单号
            project.setSourceFromNo(souPrHeadVO.getRequirementHeadNum());
            //来源单据ID
            project.setSourceFromId(souPrHeadVO.getRequirementHeadId());
            //需求来源,来源类型
            project.setSourceFromType(souPrHeadVO.getSouReqHead().getRequireFrom());
            //预算（万元）,累加
            if (project.getExtBudget() == null) { project.setExtBudget(BigDecimal.ZERO); }
            if (souPrHeadVO.getSouReqHead().getTotalAmountByTenKilo() != null) {
                project.setExtBudget(project.getExtBudget().add(souPrHeadVO.getSouReqHead().getTotalAmountByTenKilo()));
            }
            //品类ID
            project.setExtCategoryId(souPrHeadVO.getCategoryId());
            //品类编码
            project.setExtCategoryCode(souPrHeadVO.getCategoryCode());
            //品类
            project.setExtCategoryName(souPrHeadVO.getCategoryName());
            //规模数量
            project.setExtScaleQuantity(souPrHeadVO.getSouReqHead().getRequireQuantity().toString());
            //项目名称，取计划
            project.setSouName(souPrHeadVO.getSouReqHead().getProjectName());
            //合并申请单号
            project.setApplicantNo(project.getApplicantNo() + ";" + souPrHeadVO.getRequirementHeadNum());
            //TODO 投标意向金,取寻源需求，无寻源需求为0,招标DTO无对应字段
            project.setExtEarnestAmount(souPrHeadVO.getSouReqHead().getEarnestMoney());
            //TODO 推荐供应商类型，正常创建的为推荐供应商，追加类型为：追加供应商。招标DTO无对应字段
            //TODO 是否公示，招标DTO无对应字段
            souRecommVendorInfoDTO.setProject(project);
            //项目概述及招标范围
            recommendedVendor.setProjectRemark(unionString(recommendedVendor.getProjectRemark(), souPrHeadVO.getSouReqHead().getProjectOverview()));
            //供应商资质要求
            recommendedVendor.setVendorFlairAdjure(unionString(recommendedVendor.getVendorFlairAdjure(), souPrHeadVO.getSouReqHead().getVendorQualificationRequire()));
            //供应商业绩要求
            recommendedVendor.setVendorBizAdjure(unionString(recommendedVendor.getVendorBizAdjure(), souPrHeadVO.getSouReqHead().getPerformanceRequire()));
        }
    }

    protected String unionString(String value, String addValue) {
        if(StringUtils.isBlank(value)) {
            return addValue;
        }
        if(StringUtils.isBlank(addValue)) {
            return value;
        }
        return StringUtils.joinWith("------------", value, addValue);
    }

    @Override
    @ApiOperation("执行处理")
    public PrSouRequirementCreateVendorRecommendContext executeVendorRecommend(PrSouRequirementCreateVendorRecommendContext context) {
        // 从上下文中拿出构造好的数据
        ApiExtSouRecommVendorInfoDTO o = context.getX("datax");
        // TODO: 调用寻源接口创建招标单
        Long projectId = recommVendorExtClient.editRecommVendorInfo(o);
        // 将创建的招标单信息放到上下文
        if (!ObjectUtils.isEmpty(projectId)) {
            ApiExtSouRecommVendorInfoDTO recommendVendorInfo = recommVendorExtClient.getRecommVendorInfo(projectId);
            context.setResult(recommendVendorInfo);
        }
        return context;
    }

    @Override
    @ApiOperation("后置处理")
    public PrSouRequirementCreateVendorRecommendContext afterVendorRecommend(PrSouRequirementCreateVendorRecommendContext context) {
        // 1: 回写招标计划信息
        if (context.getResult() != null) {
            qlService.updateByWrapper(QlWrappers.update(ExtPrSouRequirementHead.class)
                    .set(ExtPrSouRequirementHead::getHasCreateVendorRecommend, Enable.Y)
                    .set(ExtPrSouRequirementHead::getRecommendVendorBillId, context.getResult().getProject().getProjectId())
                    .set(ExtPrSouRequirementHead::getRecommendVendorBillNo, context.getResult().getProject().getExtRecommendNo())
                    .in(ExtPrSouRequirementHead::getRequirementHeadId, context.getSouPrHeadList().stream()
                            .map(ExtPrSouRequirementHead::getRequirementHeadId).collect(Collectors.toList())));
        }

        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
