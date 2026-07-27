package com.midea.cloud.srm.sou.purfixprice.plugin.event.editfixprice;

import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouItem;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouOrderItem;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouProject;
import com.midea.cloud.srm.model.extapi.sou.purinq.enums.ExtPurInqSouProjectStatusEnum;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.purfixprice.dto.ExtPurFixPriceEditDTO;
import com.midea.cloud.srm.model.sou.purfixprice.dto.ExtPurFixPriceQueryDTO;
import com.midea.cloud.srm.model.sou.purfixprice.entity.ExtPurFixPriceFile;
import com.midea.cloud.srm.model.sou.purfixprice.entity.ExtPurFixPriceHead;
import com.midea.cloud.srm.model.sou.purfixprice.entity.ExtPurFixPriceLine;
import com.midea.cloud.srm.model.sou.purfixprice.enums.ExtPurFixPriceStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouSelectStatusEnum;
import com.midea.cloud.srm.sou.purfixprice.dao.ExtPurFixPriceHeadDAO;
import com.midea.cloud.srm.sou.purinq.dao.ExtPurInqSouItemDAO;
import com.midea.cloud.srm.sou.purinq.dao.ExtPurInqSouOrderItemDAO;
import com.midea.cloud.srm.sou.purinq.dao.ExtPurInqSouProjectDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouItemDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProjectDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouVendorDAO;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderItemDAO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtPurFixPriceEditPlugin {

    @Autowired
    private ExtPurFixPriceHeadDAO extPurFixPriceHeadDAO;
    @Autowired
    private ExtPurInqSouProjectDAO extPurInqSouProjectDAO;
    @Autowired
    private SouProjectDAO souProjectDAO;
    @Autowired
    private SouOrderItemDAO souOrderItemDAO;
    @Autowired
    private SouItemDAO souItemDAO;
    @Autowired
    private ExtPurInqSouItemDAO extPurInqSouItemDAO;
    @Autowired
    private ExtPurInqSouOrderItemDAO extPurInqSouOrderItemDAO;
    @Autowired
    private SouVendorDAO souVendorDAO;
    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    public void judgeEditAuth(ExtPurFixPriceEditContext context) {
        if (context.getParam().getPurFixPriceHeadId() != null) {
            ExtPurFixPriceHead priceHead = extPurFixPriceHeadDAO.getById(context.getParam().getPurFixPriceHeadId());
            AssertUtils.notNull(priceHead, "询比价信息[{0}]不存在", context.getParam().getPurFixPriceHeadId());
            switch (priceHead.getFixPriceStatus()) {
                // 拟定
                case DRAFT:
                    // 已驳回
                case REJECTED:
                    // 已撤回
                case WITHDRAW:
                    break;
                // 审批中
                case SUBMITTED:
                    throw new IllegalArgumentException("单据审批中，禁止编辑");
                    // 已废弃
                case ABANDONED:
                    throw new IllegalArgumentException("单据已废弃，禁止编辑");
                    // 已审批
                case APPROVED:
                    throw new IllegalArgumentException("单据已审批，禁止编辑");
                default:
                    throw new IllegalArgumentException("非法的单据状态");
            }

            context.setExistFixPriceHead(priceHead);
        }
    }

    public void validateAndConvertEdit(ExtPurFixPriceEditContext context) {
        this.validateAndConvertProject(context);
        this.validateAndConvertLines(context);
        this.validateAndConvertFiles(context);
    }

    private void validateAndConvertProject(ExtPurFixPriceEditContext context) {
        // 1: 数据校验
        ExtPurFixPriceEditDTO param = context.getParam(); {
            // 1.1: ID
            param.setPurFixPriceHeadId(context.getExistFixPriceHead() != null ? context.getExistFixPriceHead().getPurFixPriceHeadId() : IdGenrator.generate());
            // 1.2: 单据状态
            param.setFixPriceStatus(context.getExistFixPriceHead() != null ? context.getExistFixPriceHead().getFixPriceStatus() : ExtPurFixPriceStatusEnum.DRAFT);
            // 1.3: 寻源单ID
            AssertUtils.isTrue(param.isTempSave() || param.getSouProjectId() != null, "请选择项目");
            ExtPurInqSouProject inqProject = null;
            if (param.getSouProjectId() != null) {
                inqProject = extPurInqSouProjectDAO.getById(param.getSouProjectId());
                AssertUtils.notNull(inqProject, "询价单[{0}]不存在", param.getSouProjectId());
                AssertUtils.isTrue(ExtPurInqSouProjectStatusEnum.PRICE_END.equals(inqProject.getExtProjectStatus()), "询价单尚未结束");
                if (context.getExistFixPriceHead() != null && !context.getExistFixPriceHead().getSouProjectId().equals(param.getSouProjectId())) {
                    // 另外选了新的其他询比价
                    AssertUtils.isTrue(Enable.N.equals(inqProject.getHasFixPrice()), "提报策划[{0}]已定价", inqProject.getDesignProjectName());
                }
            }
            // 1.3: 寻源单号
            SouProject souProject = null;
            if (inqProject != null) {
                souProject = souProjectDAO.getById(inqProject.getProjectId());
                param.setSouNo(souProject.getSouNo());
            } else {
                param.setSouNo(null);
            }
            // 1.4: 项目策划方案ID
            param.setDesignId(inqProject != null ? inqProject.getDesignId() : null);
            // 1.5: 项目策划方案编码
            param.setDesignProjectCode(inqProject != null ? inqProject.getDesignProjectCode() : null);
            // 1.6: 项目策划方案名称
            param.setDesignProjectName(inqProject != null ? inqProject.getDesignProjectName() : null);
            // 1.7: 轮数
            param.setDesignNum(inqProject != null ? inqProject.getDesignNum() : null);
            // 1.8: 供货范围
            param.setDesignArea(inqProject != null ? inqProject.getDesignArea() : null);
            // 1.9: 项目策划联系方式
            param.setDesignCreatePhone(inqProject != null ? inqProject.getDesignCreatePhone() : null);
            // 1.10: 项目策划介绍
            param.setDesignProjIntroduce(inqProject != null ? inqProject.getDesignProjIntroduce() : null);
            // 1.11: 执行时间从
            AssertUtils.isTrue(param.isTempSave() || param.getExecuteTimeFrom() != null, "请选择执行时间从");
            if (param.getExecuteTimeFrom() != null) {
                param.setExecuteTimeFrom(ExtPurFixPriceQueryDTO.getStartTimeOfDate(param.getExecuteTimeFrom()));
            }
            // 1.12: 执行时间从
            AssertUtils.isTrue(param.isTempSave() || param.getExecuteTimeTo() != null, "请选择执行时间到");
            if (param.getExecuteTimeTo() != null) {
                param.setExecuteTimeTo(ExtPurFixPriceQueryDTO.getEndTimeOfDay(param.getExecuteTimeTo()));
                if (param.getExecuteTimeFrom() != null) {
                    AssertUtils.isTrue(param.getExecuteTimeFrom().before(param.getExecuteTimeTo()), "执行时间范围错误");
                }
            }
            // 1.13: 是否可提交
            param.setCanSubmit(param.isTempSave() ? Enable.N : Enable.Y);
            // 1.14: 创建人所属公司
            HrUserOrgnizationDto userOrgnizationDto = pjProjectExtClient.getHrUserOrgnizationByUsername(AppUserUtil.getLoginAppUser().getCeeaEmpNo());
            if (userOrgnizationDto == null || userOrgnizationDto.getOuOrganization() == null || userOrgnizationDto.getBuOrganization() == null) {
                throw new IllegalArgumentException("查询采购员hr信息失败");
            }
            param.setCreateUserOrgOuId(userOrgnizationDto.getOuOrganization().getOrganizationId());
            param.setCreateUserOrgOuCode(userOrgnizationDto.getOuOrganization().getOrganizationCode());
            param.setCreateUserOrgOuName(userOrgnizationDto.getOuOrganization().getOrganizationName());
            param.setCreateUserOrgBuId(userOrgnizationDto.getBuOrganization().getOrganizationId());
            param.setCreateUserOrgBuCode(userOrgnizationDto.getBuOrganization().getOrganizationCode());
            param.setCreateUserOrgBuName(userOrgnizationDto.getBuOrganization().getOrganizationName());
            if (userOrgnizationDto.getDepartmentOrganization() != null) {
                param.setCreateUserDeptId(userOrgnizationDto.getDepartmentOrganization().getOrganizationId());
                param.setCreateUserDeptCode(userOrgnizationDto.getDepartmentOrganization().getOrganizationCode());
                param.setCreateUserDeptName(userOrgnizationDto.getDepartmentOrganization().getOrganizationName());
            }
        }
        // 2: 数据转换
        ExtPurFixPriceHead entity = new ExtPurFixPriceHead(); {
            //noinspection unchecked
            SouObjectXUtil.mergePropertiesIgnoreFieldsWithoutExts(param, entity,
                    ExtPurFixPriceHead::getCreatedId,
                    ExtPurFixPriceHead::getCreatedBy,
                    ExtPurFixPriceHead::getCreatedByIp,
                    ExtPurFixPriceHead::getCreationDate,
                    ExtPurFixPriceHead::getCreatedFullName,
                    ExtPurFixPriceHead::getVersion,
                    ExtPurFixPriceHead::getTenantId);
        }
        context.setPriceHeadEntity(entity);
    }

    private void validateAndConvertLines(ExtPurFixPriceEditContext context) {
        if (context.getParam().getSouProjectId() == null) { return; }
        // 1: 查询询价单中中标供应商的报价信息
        List<SouOrderItem> orderItemList = souOrderItemDAO.lambdaQuery()
                .eq(SouOrderItem::getProjectId, context.getParam().getSouProjectId())
                .eq(SouOrderItem::getSelectStatus, SouSelectStatusEnum.WIN)
                .list();
        Map<Long/* souItemId */, SouItem> souItemMap = souItemDAO.list(SouItem::getProjectId, context.getParam().getSouProjectId())
                .stream().collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));
        Map<Long/* souItemId */, ExtPurInqSouItem> inqSouItemMap = extPurInqSouItemDAO.list(ExtPurInqSouItem::getProjectId, context.getParam().getSouProjectId())
                .stream().collect(Collectors.toMap(ExtPurInqSouItem::getSouItemId, Function.identity()));
        Map<Long/* orderItemId */, ExtPurInqSouOrderItem> inqOrderItemMap = extPurInqSouOrderItemDAO.listByIds(orderItemList.stream().map(SouOrderItem::getOrderItemId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(ExtPurInqSouOrderItem::getOrderItemId, Function.identity()));
        Map<Long/* vendorId */, SouVendor> vendorMap = souVendorDAO.list(SouVendor::getProjectId, context.getParam().getSouProjectId())
                .stream().collect(Collectors.toMap(SouVendor::getVendorId, Function.identity()));
        // 2: 构造数据
        List<ExtPurFixPriceLine> entityList = new ArrayList<>(orderItemList.size()); {
            for (SouOrderItem orderItem : orderItemList) {
                ExtPurFixPriceLine entity = new ExtPurFixPriceLine();
                entityList.add(entity);

                SouItem souItem = souItemMap.get(orderItem.getSouItemId());
                ExtPurInqSouItem inqSouItem = inqSouItemMap.get(orderItem.getSouItemId());
                ExtPurInqSouOrderItem inqOrderItem = inqOrderItemMap.get(orderItem.getOrderItemId());
                SouVendor vendor = vendorMap.get(orderItem.getVendorId());

                // 2.1: ID
                entity.setPurFixPriceLineId(IdGenrator.generate());
                // 2.2: 定价单ID
                entity.setPurFixPriceHeadId(context.getPriceHeadEntity().getPurFixPriceHeadId());
                // 2.3: 轮次
                entity.setRound(orderItem.getRound());
                // 2.4: 物料
                entity.setItemId(orderItem.getItemId());
                entity.setItemCode(orderItem.getItemCode());
                entity.setItemDesc(orderItem.getItemDesc());
                entity.setModel(inqSouItem.getModel());
                entity.setUnit(orderItem.getUnit());
                // 2.5: 需求数量
                entity.setRequireQuantity(orderItem.getRequireQuantity());
                // 2.6: 品牌
                entity.setBrand(inqSouItem.getBrand());
                // 2.7: 供货区域
                entity.setArea(inqSouItem.getArea());
                // 2.8: 备注
                entity.setRemark(souItem.getRemark());
                // 2.9: 未税单价
                entity.setNotaxPrice(orderItem.getStandardNotaxPrice());
                // 2.10: 含税单价
                entity.setTaxPrice(orderItem.getStandardTaxPrice());
                // 2.11: 税率
                entity.setTaxKey(orderItem.getTaxKey());
                entity.setTaxRate(orderItem.getTaxRate());
                // 2.12: 质保期
                entity.setExtWarrantyPeriod(inqOrderItem.getExtWarrantyPeriod());
                // 2.13: 供应商
                entity.setVendorId(orderItem.getVendorId());
                entity.setVendorCode(vendor.getVendorCode());
                entity.setVendorName(vendor.getVendorName());
                // 2.14: 上游寻源相关
                entity.setSouProjectId(orderItem.getProjectId());
                entity.setSouItemId(orderItem.getSouItemId());
                entity.setSouOrderId(orderItem.getOrderId());
                entity.setSouOrderItemId(orderItem.getOrderItemId());
                // 2.15: 单据状态
                entity.setFixPriceStatus(context.getPriceHeadEntity().getFixPriceStatus());
            }
        }
        context.setPriceLineListEntity(entityList);
    }

    private void validateAndConvertFiles(ExtPurFixPriceEditContext context) {
        if (CollectionUtils.isEmpty(context.getParam().getFileList())) { return; }

        // 1: 数据校验
        List<ExtPurFixPriceFile> fileList = context.getParam().getFileList(); {
            int index = 0;
            for (ExtPurFixPriceFile file : fileList) {
                index++;
                // 1.1: ID
                file.setPurFixPriceFileId(IdGenrator.generate());
                // 1.2: 定价单ID
                file.setPurFixPriceHeadId(context.getPriceHeadEntity().getPurFixPriceHeadId());
                // 1.3: 文件ID
                AssertUtils.notNull(file.getFileId(), "请上传附件");
                // 1.4: 文件名称
                file.setFileName(StringUtils.trimToNull(file.getFileName()));
                AssertUtils.notNull(file.getFileName(), "请上传附件");
                AssertUtils.isTrue(file.getFileName().length() <= 150, "附件的名称长度不能超过150");
                // 1.5: 排序
                file.setSortIndex(index);
            }
        }

        context.setPriceFileListEntity(SouObjectXUtil.convertList(fileList, ExtPurFixPriceFile.class));
    }

}
