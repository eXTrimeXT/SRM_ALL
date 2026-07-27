package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.plugin.event.createsoureq;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.ExtRbacClient;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.entity.PrRequirementHead;
import com.midea.cloud.srm.model.pm.pr.requirement.enums.RequirementApproveStatus;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.rbac.ExtUser;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.dto.ExtPrSouRequirementHeadDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementAttach;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementVendor;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementGroupTypeEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementStatusEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.vo.ExtPrSouRequirementHeadVO;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.service.PrSouRequirementCreateValidService;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.createsoureq.IPrSouRequirementCreateSouReqPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.createsoureq.PrSouRequirementCreateSouReqContext;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 招标计划池 - 创建寻源需求插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/11
 */
@Slf4j
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultPrSouRequirementCreateSouReqPlugin implements IPrSouRequirementCreateSouReqPlugin {

    @Autowired
    private QlService qlService;
    @Autowired
    private QlOpenClient qlOpenClient;
    @Autowired
    private ExtRbacClient extRbacClient;

    @Autowired
    private PrSouRequirementCreateValidService prSouRequirementCreateValidService;

    @Override
    @ApiOperation("校验操作条件/权限")
    public PrSouRequirementCreateSouReqContext judgeCreateSouReqAuth(PrSouRequirementCreateSouReqContext context) {
        AssertUtils.notEmpty(context.getParams(), "请选择需要操作的数据");
        context.setParams(context.getParams().stream().filter(e -> e.getRequirementHeadId() != null).collect(Collectors.toList()));
        AssertUtils.notEmpty(context.getParams(), "请选择需要操作的数据");
        List<ExtPrSouRequirementHead> souPrHeadList = qlService.readByKeys(ExtPrSouRequirementHead.class.getSimpleName(),
                new ArrayList<>(context.getParams().stream().map(ExtPrSouRequirementHeadDTO::getRequirementHeadId).collect(Collectors.toSet())), ExtPrSouRequirementHead.class);
        AssertUtils.notEmpty(context.getParams(), "请选择需要操作的招标计划数据");
        List<PrRequirementHead> prHeadList = qlService.readByKeys(PrRequirementHead.class.getSimpleName(),
                new ArrayList<>(souPrHeadList.stream().map(ExtPrSouRequirementHead::getRequirementHeadId).collect(Collectors.toSet())), PrRequirementHead.class);
        Map<Long, PrRequirementHead> prHeadMap = prHeadList.stream().collect(Collectors.toMap(PrRequirementHead::getRequirementHeadId, Function.identity()));
        boolean isRequirementOK = prHeadList.stream().allMatch(e -> RequirementApproveStatus.APPROVED.equals(e.getAuditStatus()));
        AssertUtils.isTrue(isRequirementOK, "只能操作已审批的招标计划");
        souPrHeadList.forEach(prHead -> AssertUtils.isFalse(PrSouRequirementStatusEnum.CANCEL.name().equals(prHead.getSouReqStatus()),
                "招标计划[{0}]已取消", prHeadMap.get(prHead.getRequirementHeadId()).getRequirementHeadNum()));
        souPrHeadList.forEach(e -> {
            PrRequirementHead prHead = prHeadMap.get(e.getRequirementHeadId());

            AssertUtils.isFalse(PrSouRequirementStatusEnum.CANCEL.name().equals(e.getSouReqStatus()), "招标计划[{0}]已取消", prHead.getRequirementHeadNum());
            AssertUtils.isTrue(Enable.N.equals(e.getHasCreateSouReq()), "招标计划[{0}]已生成寻源需求", prHead.getRequirementHeadNum());
            AssertUtils.isTrue(Enable.Y.equals(e.getHasAssigned()), "招标计划[{0}]未分配完成，不能创建寻源需求", prHead.getRequirementHeadNum());
        });
        String categoryCode = prHeadList.get(0).getCategoryCode();
        boolean isCategoryOK = prHeadList.stream().allMatch(e -> e.getCategoryCode().equals(categoryCode));
        AssertUtils.isTrue(isCategoryOK, "所勾选的招标计划必须为相同品类");

        //合并申请单号校验
        prSouRequirementCreateValidService.requirementCreateValid(context.getParams().stream().map(ExtPrSouRequirementHeadDTO::getRequirementHeadId).collect(Collectors.toList()), "req");

        context.setSouPrHeadList(souPrHeadList);
        context.setPrHeadList(prHeadList);
        return context;
    }

    @Override
    @ApiOperation("前置处理(构造数据)")
    public PrSouRequirementCreateSouReqContext beforeCreateSouReq(PrSouRequirementCreateSouReqContext context) {
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
            // 1.3: 查询招标计划附件
            //noinspection unchecked
            Map<Long/* requirementHeadId */, List<ExtPrSouRequirementAttach>> attachMap = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementAttach.class)
                            .in(ExtPrSouRequirementAttach::getRequirementHeadId, requirementHeadIds)
                            .orderByAsc(ExtPrSouRequirementAttach::getSortIndex), ExtPrSouRequirementAttach.class)
                    .stream().collect(Collectors.groupingBy(ExtPrSouRequirementAttach::getRequirementHeadId));
            souPrHeadList.forEach(pr -> pr.setSouAttachList(attachMap.get(pr.getRequirementHeadId())));
        }

        ExtPrSouRequirementHeadVO prSouVO;
        {
            // 取金额最大的申请单
            souPrHeadList.sort(Comparator.comparing(a -> a.getSouReqHead().getTotalAmountByTenKilo()));
            prSouVO = souPrHeadList.get(souPrHeadList.size() - 1);
        }
        BigDecimal totalAmountByTenKilo = BigDecimal.ZERO;
        StringBuilder totalProjectOverview = new StringBuilder(1000);
        StringBuilder totalTechRequire = new StringBuilder(1000);
        StringBuilder totalPerformanceRequire = new StringBuilder(1000);
        StringBuilder totalVendorQualificationRequire = new StringBuilder(1000);
        {
            int index = 0;
            for (ExtPrSouRequirementHeadVO prHead : souPrHeadList) {
                index++;
                totalAmountByTenKilo = totalAmountByTenKilo.add(prHead.getSouReqHead().getTotalAmountByTenKilo());

                if (index > 1) {
                    totalProjectOverview.append("\n----------\n");
                    totalTechRequire.append("\n----------\n");
                    totalPerformanceRequire.append("\n----------\n");
                    totalVendorQualificationRequire.append("\n----------\n");
                }
                totalProjectOverview.append(prHead.getSouReqHead().getProjectOverview());
                totalTechRequire.append(prHead.getSouReqHead().getTechRequire());
                totalPerformanceRequire.append(prHead.getSouReqHead().getPerformanceRequire());
                totalVendorQualificationRequire.append(prHead.getSouReqHead().getVendorQualificationRequire());
            }
        }
        ExtPrSouRequirementGroup vendorGroup = null;
        ExtPrSouRequirementGroup souGroup = null;
        ExtPrSouRequirementGroup techGroup = null;
        String requirementHeadNosStr;
        String requirementHeadIdsStr;
        ExtUser extUser = null;
        {
            if (CollectionUtils.isNotEmpty(prSouVO.getSouGroupList())) {
                vendorGroup = prSouVO.getSouGroupList().stream().filter(e -> PrSouRequirementGroupTypeEnum.VENDOR.name().equals(e.getGroupType())).findFirst().orElse(null);
                souGroup = prSouVO.getSouGroupList().stream().filter(e -> PrSouRequirementGroupTypeEnum.SOU.name().equals(e.getGroupType())).findFirst().orElse(null);
                techGroup = prSouVO.getSouGroupList().stream().filter(e -> PrSouRequirementGroupTypeEnum.TECH.name().equals(e.getGroupType())).findFirst().orElse(null);
            }
            StringBuffer sb = new StringBuffer();
            souPrHeadList.forEach(e -> sb.append(e.getRequirementHeadNum()).append(","));
            requirementHeadIdsStr = CollUtil.join(souPrHeadList, ",", l -> l.getRequirementHeadId().toString());
            requirementHeadNosStr = sb.toString();
            requirementHeadNosStr = requirementHeadNosStr.substring(0, requirementHeadNosStr.length() - 1);
        }
        //获取用户信息
        if (ObjectUtil.isNotEmpty(vendorGroup) && ObjectUtil.isNotEmpty(vendorGroup.getUserId())) {
            Long userId = vendorGroup.getUserId();
            extUser = extRbacClient.getByUserId(vendorGroup.getUserId());
            log.info("招标计划转寻源需求-查询供应商负责人(入参)：vendorGroup.userId=" + vendorGroup.getUserId() + ", 结果：extUser=" + (extUser != null ? JSON.toJSONString(extUser) : null));
        }
        // 2: 根据详情信息，构造一个寻源申请单
        SouReqHead reqHead = SouReqHead.builder()
                .reqHeadId(IdGenrator.generate())
                //板块
                .orgBuId(prSouVO.getSouReqHead().getOrgBuId())
                .orgBuCode(prSouVO.getSouReqHead().getOrgBuCode())
                .orgBuName(prSouVO.getSouReqHead().getOrgBuName())
                //公司
                .orgId(prSouVO.getOrgId())
                .orgCode(prSouVO.getOrgCode())
                .orgName(prSouVO.getOrgName())
                //需求部门
                .reqDepartment(prSouVO.getCeeaDepartmentName())
                //需求人I
                .reqUserId(prSouVO.getApplyById())
                .reqUserName(prSouVO.getApplyByNickname())
                //供应商负责人
                .responsibilityUserId(vendorGroup != null ? vendorGroup.getUserId() : null)
                .responsibilityUserName(vendorGroup != null ? vendorGroup.getFullName() : null)
                //招标负责人
                .souPersonUserId(souGroup != null ? souGroup.getUserId() : null)
                .souPersonUserName(souGroup != null ? souGroup.getFullName() : null)
                //是否前置交流
                .isPreComm(prSouVO.getSouReqHead().getPrefixTechDiscussion() != null ? prSouVO.getSouReqHead().getPrefixTechDiscussion().name() : Enable.N.name())
                //项目名称
                .projectName(prSouVO.getSouReqHead().getProjectName())
                //公示截止时间
                .publicEndTime(prSouVO.getSouReqHead().getPublicEndTime())
                //预算金额(万元)
                .totalAmountByTenKilo(totalAmountByTenKilo)
                //品类
                .categoryId(prSouVO.getCategoryId())
                .categoryCode(prSouVO.getCategoryCode())
                .categoryName(prSouVO.getCategoryName())
                //规模数量
                .requireQuantity(prSouVO.getSouReqHead().getRequireQuantity())
                //申请单id
                .requirementHeadId(prSouVO.getRequirementHeadId())
                //申请单号
                .requirementHeadNo(prSouVO.getRequirementHeadNum())
                //合并申请单号(逗号隔开字符串)
                .requirementHeadNoList(requirementHeadNosStr)
                //合并单ID
                .requirementHeadIdList(requirementHeadIdsStr)
                //需求来源
                .requireFrom(prSouVO.getSouReqHead().getRequireFrom())
                .vendorQualReq(totalVendorQualificationRequire.toString())
                .technicalReq(totalTechRequire.toString())
                .performanceReq(totalPerformanceRequire.toString())
                .projectScope(totalProjectOverview.toString())
                .projectAddress(prSouVO.getSouReqHead().getProjectAddress())
                //报名联系人（按照品类分工，获取供应商开发职责人）
                .contactName(vendorGroup != null ? vendorGroup.getFullName() : null)
                //联系人手机号
                .phone(ObjectUtil.isNotEmpty(extUser) ? extUser.getPhone() : null)
                //用户表扩展字段EXT_OFFICE_PHONE
                .officePhone(ObjectUtil.isNotEmpty(extUser) ? extUser.getExtOfficePhone() : null)
                .isPublic(prSouVO.getSouReqHead().getNeedPublic().toString())
                .technicalUserId(techGroup != null ? techGroup.getUserId() : null)
                .technicalUserName(techGroup != null ? techGroup.getFullName() : null)
                //技术负责人手机号
                .techPhone(techGroup != null ? techGroup.getPhone() : null)
                .build();
        context.putX("datax", reqHead);
        log.info("招标计划转寻源需求-查询供应商负责人(构造结果)：reqHead=" + JSON.toJSONString(reqHead));
        return context;
    }

    @Override
    @ApiOperation("执行处理")
    public PrSouRequirementCreateSouReqContext executeCreateSouReq(PrSouRequirementCreateSouReqContext context) {
        // 从上下文中拿出构造好的数据
        SouReqHead reqHead = (SouReqHead) context.getX("datax");
        // TODO: 调用寻源接口创建招标单
        List<SouReqHead> reqHeadList = new ArrayList<>();
        reqHeadList.add(reqHead);
        qlOpenClient.create(ContextPath.SOU, MqlType.SOU_REQ_HEAD_BUYER, reqHeadList);
        RecordDTO record = qlOpenClient.read(ContextPath.SOU, MqlType.SOU_REQ_HEAD_BUYER, reqHead.getReqHeadId());
        context.setResult(record);

        return context;
    }

    @Override
    @ApiOperation("后置处理")
    public PrSouRequirementCreateSouReqContext afterCreateSouReq(PrSouRequirementCreateSouReqContext context) {
        // 1: 回写招标计划信息
        if (context.getResult() != null) {
            qlService.updateByWrapper(QlWrappers.update(ExtPrSouRequirementHead.class)
                    .set(ExtPrSouRequirementHead::getHasCreateSouReq, Enable.Y)
                    .set(ExtPrSouRequirementHead::getSouReqId, context.getResult().get("reqHeadId"))
                    .set(ExtPrSouRequirementHead::getSouReqNo, context.getResult().get("reqHeadNo"))
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
