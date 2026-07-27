package com.midea.cloud.srm.sou.fixprice.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.github.pagehelper.page.PageMethod;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.ObjectUtil;
import com.midea.cloud.meiql.api.component.paging.Page;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseUnit;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPjInqSouOrder;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementLine;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.sou.fixprice.dto.ExtFixPriceInqOrderItemsQueryDTO;
import com.midea.cloud.srm.model.sou.fixprice.dto.ExtFixPriceQueryDTO;
import com.midea.cloud.srm.model.sou.fixprice.dto.ExtFixPriceReqLinesQueryDTO;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceFile;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceHead;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceLine;
import com.midea.cloud.srm.model.sou.fixprice.enums.ExtFixPricePaymentMethodEnum;
import com.midea.cloud.srm.model.sou.fixprice.enums.ExtFixPriceSourceFromTypeEnum;
import com.midea.cloud.srm.model.sou.fixprice.vo.*;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouItem;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouOrderItem;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.pr.requirement.enums.PrRequirementFixPriceStatusEnum;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenQueryWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.fixprice.dao.ExtFixPriceFileDAO;
import com.midea.cloud.srm.sou.fixprice.dao.ExtFixPriceHeadDAO;
import com.midea.cloud.srm.sou.fixprice.dao.ExtFixPriceHeadMapper;
import com.midea.cloud.srm.sou.fixprice.dao.ExtFixPriceLineDAO;
import com.midea.cloud.srm.sou.fixprice.plugin.query.listinqorderitems.ExtFixPriceListInqOrderItemsContext;
import com.midea.cloud.srm.sou.fixprice.plugin.query.listinqorderitems.ExtFixPriceListInqOrderItemsPlugin;
import com.midea.cloud.srm.sou.fixprice.service.ExtFixPriceQueryService;
import com.midea.cloud.srm.sou.inq.ext.dao.ExtPjInqSouOrderDAO;
import com.midea.cloud.srm.sou.inq.init.dao.InqSouItemDAO;
import com.midea.cloud.srm.sou.inq.order.dao.InqSouOrderItemDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProjectDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouVendorDAO;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderDAO;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderItemDAO;
import com.spire.ms.System.Collections.Specialized.CollectionsUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtFixPriceQueryServiceImpl implements ExtFixPriceQueryService {

    @Autowired
    private ExtFixPriceHeadDAO extFixPriceHeadDAO;
    @Autowired
    private ExtFixPriceListInqOrderItemsPlugin extFixPriceListInqOrderItemsPlugin;
    @Autowired
    private QlOpenClient qlOpenClient;
    @Autowired
    private ExtFixPriceLineDAO extFixPriceLineDAO;
    @Autowired
    private ExtFixPriceFileDAO extFixPriceFileDAO;
    @Autowired
    private SouProjectDAO souProjectDAO;
    @Autowired
    private SouOrderItemDAO souOrderItemDAO;
    @Autowired
    private SouOrderDAO souOrderDAO;
    @Autowired
    private ExtPjInqSouOrderDAO extPjInqSouOrderDAO;
    @Autowired
    private InqSouOrderItemDAO inqSouOrderItemDAO;
    @Autowired
    private SouVendorDAO souVendorDAO;
    @Autowired
    private PjProjectExtClient pjProjectExtClient;
    @Autowired
    private ExtFixPriceHeadMapper extFixPriceHeadMapper;
    @Autowired
    private BaseClient baseClient;
    @Autowired
    private RbacClient rbacClient;
    @Autowired
    private InqSouItemDAO inqSouItemDAO;

    /**
     * 定价单列表查询
     */
    @Override
    public List<ExtFixPriceHead> listFixPrices(ExtFixPriceQueryDTO queryParam) {
        queryParam.formatParams();
        if (queryParam.getPageNum() != null && queryParam.getPageSize() != null) {
            PageMethod.startPage(queryParam.getPageNum(), queryParam.getPageSize());
        }
        return extFixPriceHeadMapper.listFixPrices(queryParam);
    }

    /**
     * 查看定价单详情
     */
    @Nullable
    @Override
    public ExtFixPriceHeadVO getFixPrice(long fixPriceHeadId) {
        ExtFixPriceHead priceHead = extFixPriceHeadDAO.getById(fixPriceHeadId);
        if (priceHead == null) { return null; }
        List<ExtFixPriceLine> lineList = extFixPriceLineDAO.lambdaQuery()
                .eq(ExtFixPriceLine::getFixPriceHeadId, fixPriceHeadId)
                .orderByAsc(ExtFixPriceLine::getSortIndex)
                .list();
        List<ExtFixPriceFile> fileList = extFixPriceFileDAO.lambdaQuery()
                .eq(ExtFixPriceFile::getFixPriceHeadId, fixPriceHeadId)
                .orderByAsc(ExtFixPriceFile::getSortIndex)
                .list();

        ExtFixPriceHeadVO vo = new ExtFixPriceHeadVO();
        BeanUtils.copyProperties(priceHead, vo);
        vo.setLineList(lineList);
        vo.setFileList(fileList);

        return vo;
    }

    /**
     * 询比价中标信息列表查询
     */
    @Override
    public List<ExtFixPriceInqOrderItemsQueryVO> listSouInqOrderItems(ExtFixPriceInqOrderItemsQueryDTO queryParam) {
        ExtFixPriceListInqOrderItemsContext context = new ExtFixPriceListInqOrderItemsContext(queryParam);
        // 1: 查询数据
        extFixPriceListInqOrderItemsPlugin.queryData(context);
        // 2: 额外数据填补
        extFixPriceListInqOrderItemsPlugin.extraData(context);

        return context.getResultList();
    }

    /**
     * 近期采购列表查询
     */
    @Override
    @SuppressWarnings("rawtypes")
    public List<ExtFixPriceReqLinesQueryVO> listReqLines(ExtFixPriceReqLinesQueryDTO queryParam) {
        // 查询当前操作人所在公司
        Long createUserOrgOuId = null; {
            // 写入创建人所在公司信息
            HrUserOrgnizationDto userOrgnizationDto = pjProjectExtClient.getHrUserOrgnizationByUsername(AppUserUtil.getLoginAppUser().getCeeaEmpNo());
            if (userOrgnizationDto == null || userOrgnizationDto.getOuOrganization() == null) {
                throw new IllegalArgumentException("查询采购员hr信息失败");
            }
            createUserOrgOuId = userOrgnizationDto.getOuOrganization().getOrganizationId();
        }

        queryParam.formatParams();
        QlOpenQueryWrapper wrapper = QlOpenWrappers.query("PurchaseRequirementLine")
                // 数据已进入需求池
                .eq("extPoolStatus", Enable.Y)
                // 未定价
                .in("fixPriceStatus", Arrays.asList(PrRequirementFixPriceStatusEnum.DRAFT.name(), PrRequirementFixPriceStatusEnum.PRICE_FAIL.name()))
                // 未创建询比价
                .eq("ifCreateInq", Enable.N)
                // 近期采购类型
                .eq("extBuyType", "RECENT_PURCHASE")
                // 创建人所在公司ID
                .eq(createUserOrgOuId != null, RequirementLine::getPurchaseOrganization, createUserOrgOuId)
                .contains(queryParam.getRequirementHeadNum() != null, RequirementLine::getRequirementHeadNum, queryParam.getRequirementHeadNum())
                .contains(queryParam.getMaterialCode() != null, RequirementLine::getMaterialCode, queryParam.getMaterialCode())
                .contains(!Objects.isNull(queryParam.getApplyBy()), RequirementLine::getCeeaPerformUserNickname, queryParam.getApplyBy())
                .eq(RequirementLine::getIfDistributionVendor, Enable.Y)
                .eq(queryParam.getOrgId() != null, RequirementLine::getOrgId, queryParam.getOrgId());

        List<RecordDTO> recordList;
        if (queryParam.getPageSize() != null && queryParam.getPageNum() != null) {
            Page<RecordDTO> page = qlOpenClient.query(ContextPath.SUP_CE, wrapper, (long)queryParam.getPageNum(), (long)queryParam.getPageSize());
            recordList = new com.github.pagehelper.Page<>();
            recordList.addAll(page.getRecords());
            ((com.github.pagehelper.Page) recordList).setTotal(page.getTotal());
            ((com.github.pagehelper.Page) recordList).setPageSize(page.getPageSize());
            ((com.github.pagehelper.Page) recordList).setPageNum(page.getPageNum());
        } else {
            recordList = qlOpenClient.query(ContextPath.SUP_CE, wrapper);
        }

        List<ExtFixPriceReqLinesQueryVO> voList = SouObjectXUtil.convertList(recordList, ExtFixPriceReqLinesQueryVO.class);
        voList.forEach(vo -> {
            vo.setSourceFromType(ExtFixPriceSourceFromTypeEnum.PURCHASE_REQ);
            vo.setExtOrderCount(1);
        });

        // 处理额外的几个头表字段
        if (!voList.isEmpty()) {
            Set<Long> requirementHeadIds = voList.stream().map(ExtFixPriceReqLinesQueryVO::getRequirementHeadId).collect(Collectors.toSet());
            if (!requirementHeadIds.isEmpty()) {
                Map<Long/* requirementHeadId */, RequirementHead> reqHeadMap = SouObjectXUtil.convertList(qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query("PurchaseRequirementHead")
                                .in(RequirementHead::getRequirementHeadId, new ArrayList<>(requirementHeadIds))), RequirementHead.class)
                        .stream().collect(Collectors.toMap(RequirementHead::getRequirementHeadId, Function.identity()));
                voList.forEach(vo -> {
                    RequirementHead reqHead = reqHeadMap.get(vo.getRequirementHeadId());
                    if (reqHead != null) {
                        vo.setCeeaPrType(reqHead.getCeeaPrType());
                        vo.setApplyBy(reqHead.getApplyBy());
                    }
                });
            }
            Set<String> usernames = voList.stream().map(ExtFixPriceReqLinesQueryVO::getApplyBy).filter(Objects::nonNull).collect(Collectors.toSet());
            if (!usernames.isEmpty()) {
                Map<String, User> userMap = rbacClient.getUserMapByNames(usernames);
                voList.forEach(vo -> {
                    User user = userMap.get(vo.getApplyBy());
                    if (user != null) {
                        vo.setApplyByName(user.getNickname());
                    }
                });
            }
        }

        return voList;
    }

    /**
     * 供应商报价明细查询
     */
    @Override
    public List<ExtFixPriceInqOrderItemVO> listSouInqOrderItemsForPriceLine(long orderItemId) {
        SouOrderItem orderItem = souOrderItemDAO.getById(orderItemId);
        if (orderItem == null) { return Collections.emptyList(); }
        SouProject souProject = souProjectDAO.getById(orderItem.getProjectId());
        if (souProject == null) { return Collections.emptyList(); }

        // 查询指定轮次指定物料的所有供应商提交的报价
        List<SouOrderItem> orderItemList = souOrderItemDAO.lambdaQuery()
                .eq(SouOrderItem::getProjectId, orderItem.getProjectId())
                .eq(SouOrderItem::getSouItemId, orderItem.getSouItemId())
                .eq(SouOrderItem::getRound, orderItem.getRound())
                .eq(SouOrderItem::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                .isNotNull(SouOrderItem::getOrderNotaxPrice)
                .ne(SouOrderItem::getOrderNotaxPrice, BigDecimal.ZERO)
                .list();
        if (orderItemList.isEmpty()) { return Collections.emptyList(); }
        Map<Long/* orderId */, SouOrder> orderMap = souOrderDAO.listByIds(orderItemList.stream().map(SouOrderItem::getOrderId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(SouOrder::getOrderId, Function.identity()));
        Map<Long/* orderId */, ExtPjInqSouOrder> inqOrderMap = extPjInqSouOrderDAO.listByIds(orderMap.keySet())
                .stream().collect(Collectors.toMap(ExtPjInqSouOrder::getOrderId, Function.identity()));
        Map<Long/* orderItemId */, InqSouOrderItem> inqOrderItemMap = inqSouOrderItemDAO.listByIds(orderItemList.stream().map(SouOrderItem::getOrderItemId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(InqSouOrderItem::getOrderItemId, Function.identity()));
        Map<Long/* vendorId */, SouVendor> vendorMap = souVendorDAO.list(SouVendor::getProjectId, souProject.getProjectId())
                .stream().collect(Collectors.toMap(SouVendor::getVendorId, Function.identity()));
        Map<Long/* souItemId */, InqSouItem> inqItemMap = inqSouItemDAO.listByIds(orderItemList.stream().map(SouOrderItem::getSouItemId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(InqSouItem::getSouItemId, Function.identity()));

        List<ExtFixPriceInqOrderItemVO> resultList = SouObjectXUtil.convertList(orderItemList, ExtFixPriceInqOrderItemVO.class);
        resultList.removeIf(e -> {
            InqSouItem inqItem = inqItemMap.get(e.getSouItemId());
            return Enable.Y.equals(inqItem.getHasClose());
        });
        resultList.forEach(result -> {
            SouOrder order = orderMap.get(result.getOrderId());
            BeanUtils.copyProperties(order, result);

            ExtPjInqSouOrder inqOrder = inqOrderMap.get(result.getOrderId());
            BeanUtils.copyProperties(inqOrder, result);

            InqSouOrderItem inqOrderItem = inqOrderItemMap.get(result.getOrderItemId());
            BeanUtils.copyProperties(inqOrderItem, result);

            SouVendor vendor = vendorMap.get(result.getVendorId());
            BeanUtils.copyProperties(vendor, result);
        });
        // 处理结果集排序
        resultList.sort((a, b) -> {
            int rs = a.getStandardNotaxPrice().compareTo(b.getStandardNotaxPrice());
            if (rs != 0) { return rs; }
            // 价格相同，进一步比较"到货周期"
            InqSouOrderItem inqA = inqOrderItemMap.get(a.getOrderItemId());
            InqSouOrderItem inqB = inqOrderItemMap.get(b.getOrderItemId());
            if (inqA.getExtLeadTime() < inqB.getExtLeadTime()) {
                return -1;
            } else if (inqA.getExtLeadTime() > inqB.getExtLeadTime()) {
                return 1;
            }
            // 进一步比较"质保期"(越大越好)
            Integer compareWarrantyPeriod = Integer.compare(ObjectUtils.defaultIfNull(inqB.getExtWarrantyPeriod(), SrmConstant.NUM_ZERO), ObjectUtils.defaultIfNull(inqA.getExtWarrantyPeriod(), SrmConstant.NUM_ZERO));
            if(Integer.compare(compareWarrantyPeriod, SrmConstant.NUM_ZERO) != 0) {
                return compareWarrantyPeriod;
            }
            // 最后，根据报价时间进行比较
            return a.getLastUpdateDate().before(b.getLastUpdateDate()) ? -1: 1;
        });
        return resultList;
    }

    @Override
    public List<ExtFixPriceExportVO> exportFixPrices(ExtFixPriceQueryDTO queryParam) {
        queryParam.formatParams();
        List<ExtFixPriceHead> extFixPriceHeads = extFixPriceHeadMapper.listFixPrices(queryParam);
        List<Long> headIds = extFixPriceHeads.stream().map(ExtFixPriceHead::getFixPriceHeadId).collect(Collectors.toList());
        return listFixPriceExportVO(headIds,extFixPriceHeads);
    }

    private List<ExtFixPriceExportVO> listFixPriceExportVO(List<Long> headIds,List<ExtFixPriceHead> extFixPriceHeads){
        List<ExtFixPriceExportVO> extFixPriceExportVOS = new ArrayList<>();
        if(ObjectUtils.isNotEmpty(extFixPriceHeads)){
            Map<Long,List<ExtFixPriceLine>> extLineMap = extFixPriceLineDAO.lambdaQuery()
                    .in(ExtFixPriceLine::getFixPriceHeadId, headIds)
                    .orderByAsc(ExtFixPriceLine::getSortIndex)
                    .list().stream().collect(Collectors.groupingBy(ExtFixPriceLine::getFixPriceHeadId));
            Map<String,DictItemDTO> invoiceTypeMap = getDictItemMap("EXT_SOU_INQ_ORDER_INVOICE_TYPE");
            Map<String,DictItemDTO> applyTypeMap = getDictItemMap("application_form_type");
            Map<String,DictItemDTO> extBuyTypeMap = getDictItemMap("PR_BUY_TYPE");
            Map<String,DictItemDTO> extPriceStatusMap = getDictItemMap("EXT_FIX_PRICE_STATUS");
            Map<String, DictItemDTO> paymentMethodMap = getDictItemMap(ExtFixPricePaymentMethodEnum.getDictCode());
            Map<String,DictItemDTO> paymentProvisionMap = getDictItemMap("PAYMENT_PROVISION");
            List<PurchaseUnit> unitList = qlOpenClient.query(ContextPath.BASE, QlOpenWrappers.query(PurchaseUnit.class)
                    .eq(PurchaseUnit::getEnabled, YesOrNo.YES.getValue()), PurchaseUnit.class);
            Map<String, String> unitMap = unitList.stream()
                    .collect(Collectors.toMap(PurchaseUnit::getUnitCode, PurchaseUnit::getUnitName));
            for (ExtFixPriceHead extFixPriceHead:extFixPriceHeads){
                List<ExtFixPriceLine> extFixPriceLines = extLineMap.get(extFixPriceHead.getFixPriceHeadId());
                if(CollUtil.isNotEmpty(extFixPriceLines)){
                    for (ExtFixPriceLine extFixPriceLine:extFixPriceLines){
                        if(CollUtil.isNotEmpty(extFixPriceLines)){
                            ExtFixPriceExportVO extFixPriceExportVO = ExtFixPriceExportVO.createExtFixPriceExportVO(extFixPriceHead, extFixPriceLine, invoiceTypeMap, applyTypeMap, extBuyTypeMap, paymentMethodMap, extPriceStatusMap);
                            extFixPriceExportVO.setUnit(unitMap.get(extFixPriceExportVO.getUnit()));
                            DictItemDTO dictItemDTO = paymentProvisionMap.get(extFixPriceExportVO.getPaymentTerm());
                            if(dictItemDTO != null){
                                extFixPriceExportVO.setPaymentTerm(dictItemDTO.getDictItemName());
                            }
                            extFixPriceExportVOS.add(extFixPriceExportVO);
                        }
                    }
                }
            }
        }
        return extFixPriceExportVOS;
    }

    @Override
    public List<ExtFixPriceExportVO> exportFixPriceLine(Long extFixPriceId) {
        ExtFixPriceHead extFixPriceHead = extFixPriceHeadMapper.selectById(extFixPriceId);
        if(ObjectUtils.isEmpty(extFixPriceHead)){
            throw new BaseException("定价单不存在");
        }
        return listFixPriceExportVO(Collections.singletonList(extFixPriceId),Collections.singletonList(extFixPriceHead));
    }

    @Override
    public List<ExtFixPriceLine> queryLines(String itemCode, String sourceFromNo) {
        return extFixPriceLineDAO.lambdaQuery().eq(ExtFixPriceLine::getItemCode,itemCode).eq(ExtFixPriceLine::getSourceFromNo,sourceFromNo).list();
    }

    @Override
    public List<ExtFixPriceLine> queryLines(List<SouItem> inqSouItems) {
        if(CollUtil.isNotEmpty(inqSouItems)){
            Long projectId = inqSouItems.get(0).getProjectId();
            SouProject souProject = souProjectDAO.getById(projectId);
            String sourceFromNo = souProject.getSouNo();
            List<String> itemCodes = inqSouItems.stream().map(SouItem::getItemCode).collect(Collectors.toList());
            return extFixPriceLineDAO.lambdaQuery().in(ExtFixPriceLine::getItemCode,itemCodes).eq(ExtFixPriceLine::getSourceFromNo,sourceFromNo).list();
        }
        return Collections.emptyList();
    }

    private Map<String,DictItemDTO> getDictItemMap(String dictCode){
        return baseClient.listAllByDictCode(dictCode).stream().collect(Collectors.toMap(DictItemDTO::getDictItemCode,Function.identity()));
    }

}
