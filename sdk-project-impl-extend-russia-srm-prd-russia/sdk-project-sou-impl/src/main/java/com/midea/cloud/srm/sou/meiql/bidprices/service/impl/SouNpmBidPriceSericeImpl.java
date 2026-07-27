package com.midea.cloud.srm.sou.meiql.bidprices.service.impl;

import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.bidprices.dto.BidPriceDto;
import com.midea.cloud.srm.model.sou.ca.dto.CaDTO;
import com.midea.cloud.srm.model.sou.ca.dto.CaSelectionResultDTO;
import com.midea.cloud.srm.model.sou.enums.ExtSouGroupRoleEnum;
import com.midea.cloud.srm.model.sou.enums.TypeEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiExtSouOrderItemDto;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.sou.meiql.bidprices.service.SouNpmBidPriceSerice;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouGroupService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouItemService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderItemService;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Service
@Transactional(rollbackFor = {Exception.class})
@Slf4j
public class SouNpmBidPriceSericeImpl implements SouNpmBidPriceSerice {
    @Autowired
    private QlService qlService;

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private IExtSouItemService itemService;

    @Autowired
    private IExtSouGroupService groupService;

    @Autowired
    private IExtSouOrderItemService orderItemService;

    @Override
    public List<BidPriceDto> generateBidPrice(CaDTO caDTO) {

        //中标结果
        List<CaSelectionResultDTO> caSelectionResults = caDTO.getCaSelectionResults();
        if(!Objects.isNull(caSelectionResults)) {
            caSelectionResults = caSelectionResults.stream().filter(r -> YesOrNo.YES.getValue().equals(r.getIsWin())).collect(Collectors.toList());
        }
        if(CollectionUtils.isEmpty(caSelectionResults)) {
            log.info(MessageFormat.format("定标申请{0}无中标结果", caDTO.getCaNo()));
            return new ArrayList<>();
        }

        //查询招标信息
        ExtSouProject project = projectService.getById(caDTO.getProjectId());
        if(Objects.isNull(project)) {
            log.info(MessageFormat.format("定标申请{0}关联招标单据为空", caDTO.getCaNo()));
            return new ArrayList<>();
        }

        //查询招标报价信息
        List<ExtSouItem> itemList = itemService.lambdaQuery().eq(ExtSouItem::getProjectId, project.getProjectId()).list();
        //按名称分组
        Map<String, List<ExtSouItem>> nameGroup = itemList.stream().collect(Collectors.groupingBy(ExtSouItem::getItemDesc));
        //按包名分组
        Map<String, List<ExtSouItem>> packGroup = itemList.stream().filter(p -> StringUtils.isNotBlank(p.getExtPackageName())).collect(Collectors.groupingBy(ExtSouItem::getExtPackageName));

        //查询供应商报价信息
        List<ExtSouOrderItem> orderItemList = orderItemService.lambdaQuery().eq(ExtSouOrderItem::getProjectId, project.getProjectId())
                .in(ExtSouOrderItem::getVendorId, caSelectionResults.stream().map(r -> r.getVendorId()).distinct().collect(Collectors.toList())).list();
        //记录每个供应商最大轮次
        Map<Long, Integer> vendorMaxRound = new HashMap<>(15);
        orderItemList.stream().forEach(oi -> {
            vendorMaxRound.put(oi.getVendorId(), Math.max(ObjectUtils.defaultIfNull(MapUtils.getInteger(vendorMaxRound, oi.getVendorId()), 1), ObjectUtils.defaultIfNull(oi.getRound(), 1)));
        });

        //取每个供应商最大轮次的报价信息
        orderItemList = orderItemList.stream().filter(oi -> Integer.compare(ObjectUtils.defaultIfNull(MapUtils.getInteger(vendorMaxRound, oi.getVendorId()), 1), ObjectUtils.defaultIfNull(oi.getRound(), 1)) == 0).collect(Collectors.toList());

        //按供应商分组报价信息
        Map<Long, List<ExtSouOrderItem>> vendorGroup = orderItemList.stream().collect(Collectors.groupingBy(ExtSouOrderItem::getVendorId));

        List<BidPriceDto> priceDtoList = new ArrayList<>();
        //根据供应商中标范围生成招标价格库数据

        for(CaSelectionResultDTO resultDTO: caSelectionResults) {
            List<BidPriceDto> buildList = generateBidPriceData(resultDTO, project, vendorGroup.getOrDefault(resultDTO.getVendorId(), new ArrayList<>()), nameGroup, packGroup);
            if(CollectionUtils.isNotEmpty(buildList)) {
                priceDtoList.addAll(buildList);
            }
        }

        //保存数据
        saveOrUpdate(priceDtoList);
        return priceDtoList;
    }

    private void saveOrUpdate(List<BidPriceDto> priceDtoList) {
        if(CollectionUtils.isEmpty(priceDtoList)) {
            return;
        }
        //根据招标单ID查询
        List<BidPriceDto> existList = qlService.query(MqlType.BID_PRICE, MeiQl.newCondition()
                .in(BidPriceDto::getProjectId, priceDtoList.stream().map(BidPriceDto::getProjectId).distinct().collect(Collectors.toList()))
                .eq(BidPriceDto::getParentBidPriceId, -1L), BidPriceDto.class);
        Map<String, BidPriceDto> priceDtoMap = existList.stream().collect(Collectors.toMap(p -> StringUtils.joinWith("_", p.getProjectId(), p.getOrderItemId()), v -> v, (k1, k2)->k2));

        //保存或更新
        List<BidPriceDto> saveList = new ArrayList<>();
        List<BidPriceDto> updateList = new ArrayList<>();

        priceDtoList.stream().forEach(p -> {
            String key = StringUtils.joinWith("_", p.getProjectId(), p.getOrderItemId());
            if(priceDtoMap.containsKey(key)) {
                p.setBidPriceId(priceDtoMap.get(key).getBidPriceId());
                updateList.add(p);
            } else {
                saveList.add(p);
            }
        });

        if(CollectionUtils.isNotEmpty(saveList)) {
            qlService.save(MqlType.BID_PRICE, saveList);
        }
        if(CollectionUtils.isNotEmpty(updateList)) {
            qlService.update(MqlType.BID_PRICE, updateList);
        }
    }

    private List<BidPriceDto> generateBidPriceData(CaSelectionResultDTO resultDTO, ExtSouProject project, List<ExtSouOrderItem> orderItemList, Map<String, List<ExtSouItem>> nameGroup, Map<String, List<ExtSouItem>> packGroup) {
        List<BidPriceDto> priceDtoList = new ArrayList<>();
        if(StringUtils.isBlank(resultDTO.getWinRange())) {
            return priceDtoList;
        }

        //查询招标负责人
        List<ExtSouGroup> souPrincipalList = groupService.lambdaQuery().eq(ExtSouGroup::getProjectId, project.getProjectId()).eq(ExtSouGroup::getGroupRole, ExtSouGroupRoleEnum.PRINCIPAL.getCode()).list();
        ExtSouGroup souPrincipal = CollectionUtils.isNotEmpty(souPrincipalList)?souPrincipalList.get(0):new ExtSouGroup();

        //中标范围---先检索包名，包名没有再检索名称
        List<String> rangeList = new ArrayList<>(Arrays.asList(resultDTO.getWinRange().replaceAll(SrmConstant.SIG_3, SrmConstant.SIG_1).split(SrmConstant.SIG_1)));
        //报价信息
        Map<Long, ExtSouOrderItem> orderItemMap = orderItemList.stream().collect(Collectors.toMap(k -> k.getSouItemId(), v -> v, (k1, k2)->k2));
        for(String range : rangeList) {
            //包名检索
            List<ExtSouItem> itemList = null;
            if(packGroup.containsKey(range)) {
                itemList = packGroup.get(range);
            } else {
                itemList = nameGroup.get(range);
            }
            if(CollectionUtils.isEmpty(itemList)) {
                continue;
            }
            //构造价格库数据
            for(ExtSouItem souItem : itemList) {
                if(!orderItemMap.containsKey(souItem.getSouItemId())) {
                   continue;
                }
                //构造数据
                BidPriceDto priceDto = toPriceDto(resultDTO, project, souItem, orderItemMap.get(souItem.getSouItemId()));
                //招标负责人
                priceDto.setSouPrincipal(souPrincipal.getFullName());
                priceDto.setSouPrincipalUserId(souPrincipal.getUserId());
                priceDto.setSouPrincipalUserName(souPrincipal.getUserName());
                priceDtoList.add(priceDto);
            }
        }

        return priceDtoList;
    }

    private BidPriceDto toPriceDto(CaSelectionResultDTO resultDTO, ExtSouProject project, ExtSouItem souItem, ExtSouOrderItem orderItem) {
        BidPriceDto bidPriceDto = new BidPriceDto();

        ApiExtSouOrderItemDto orderItemDto = new ApiExtSouOrderItemDto();
        BeanCopyUtil.copyProperties(orderItemDto, orderItem);
        orderItemDto.coverItemFields();

        //父ID，当父ID等于-1时，表示头表
        bidPriceDto.setParentBidPriceId(-1L);
        //报价单明细ID
        bidPriceDto.setOrderItemId(orderItem.getOrderItemId());
        //供应商ID
        bidPriceDto.setVendorId(orderItem.getVendorId());
        //供应商编码
        bidPriceDto.setVendorCode(resultDTO.getVendorCode());
        //供应商名称
        bidPriceDto.setVendorName(resultDTO.getVendorName());
        //寻源单ID
        bidPriceDto.setProjectId(project.getProjectId());
        //招标项目编号
        bidPriceDto.setProjectNo(project.getExtProjectNo());
        //寻源名称
        bidPriceDto.setSouName(project.getSouName());

        bidPriceDto.setExtOrgBuId(project.getExtOrgBuId());

        bidPriceDto.setExtOrgBuCode(project.getExtOrgBuCode());

        bidPriceDto.setExtOrgBuName(project.getExtOrgBuName());

        bidPriceDto.setExtOrgOuId(project.getExtOrgOuId());

        bidPriceDto.setExtOrgOuCode(project.getExtOrgOuCode());

        bidPriceDto.setExtOrgOuName(project.getExtOrgOuName());

        //品类ID
        bidPriceDto.setCategoryId(project.getExtCategoryId());
        //品类编码
        bidPriceDto.setCategoryCode(project.getExtCategoryCode());
        //品类名称
        bidPriceDto.setCategoryName(project.getExtCategoryName());
        //物料ID
        bidPriceDto.setItemId(souItem.getItemId());
        //物料编码
        bidPriceDto.setItemCode(souItem.getItemCode());
        //名称
        bidPriceDto.setItemDesc(souItem.getItemDesc());
        //规格/型号
        bidPriceDto.setSpecification(souItem.getCategoryName());
        //品牌
        bidPriceDto.setBrand(souItem.getExtBrand());
        //项目特征
        bidPriceDto.setFeature(souItem.getExtFeature());
        //施工内容
        bidPriceDto.setConstructionItem(souItem.getExtConstructionItem());
        //数量/工程量
        bidPriceDto.setQuantity(souItem.getExtQuantity());
        //未税单价（万元）
        bidPriceDto.setPriceNoTax(orderItemDto.getExtPriceNoTax());
        //未税总价（万元）
        bidPriceDto.setPriceSumNoTax(orderItemDto.getExtPriceSumNoTax());
        //含税单价（万元）
        bidPriceDto.setPriceTax(orderItemDto.getExtPriceTax());
        //含税总价（万元）
        bidPriceDto.setPriceSumTax(orderItemDto.getExtPriceSumTax());
        //发票类型
        bidPriceDto.setInvoiceType(orderItemDto.getExtInvoiceType());
        //税率（%）
        bidPriceDto.setTaxRate(orderItemDto.getExtTaxRate());
        //币种
        bidPriceDto.setCurrency(orderItemDto.getExtCurrency());
        //标段
        bidPriceDto.setBidSection(souItem.getExtBidSection());
        //区域
        bidPriceDto.setRegion(souItem.getExtRegion());
        //单位
        bidPriceDto.setUnit(souItem.getUnit());
        //暂定数量/工程量
        bidPriceDto.setRequireQuantity(souItem.getRequireQuantity());
        //分项
        bidPriceDto.setSubitem(souItem.getExtSubitem());
        //固定未税单价（万元）
        bidPriceDto.setFixedPriceNoTax(orderItemDto.getExtFixedPriceNoTax());
        //暂定未税总价（万元）
        bidPriceDto.setProvPriceSumNoTax(orderItemDto.getExtProvPriceSumNoTax());
        //固定含税单价（万元）
        bidPriceDto.setFixedPriceTax(orderItemDto.getExtFixedPriceTax());
        //暂定含税总价（万元）
        bidPriceDto.setProvPriceSumTax(orderItemDto.getExtProvPriceSumTax());
        //备注
        bidPriceDto.setRemark(souItem.getRemark());
        return bidPriceDto;
    }

    @Override
    public List<BidPriceDto> generateBidPriceById(Long caId) {
        CaDTO ca = qlService.readByKey(TypeEnum.Ca.getCode(), caId, CaDTO.class);
        //招标结果
        List<CaSelectionResultDTO> caSelectionResultDTOList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.CaSelectionResult.getCode())
                .eq(CaSelectionResultDTO::getCaId, caId), CaSelectionResultDTO.class);
        ca.setCaSelectionResults(caSelectionResultDTOList);
        return generateBidPrice(ca);
    }
}
