package com.midea.cloud.srm.sou.purfixprice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouItem;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouOrderItem;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouProject;
import com.midea.cloud.srm.model.sou.designplans.entity.SccSouChDemandAgreement;
import com.midea.cloud.srm.model.sou.purfixprice.dto.ExtPurFixPriceLineGroupQueryDTO;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouSelectStatusEnum;
import com.midea.cloud.srm.sou.constants.NumConstant;
import com.midea.cloud.srm.sou.designplans.service.AgreementService;
import com.midea.cloud.srm.sou.purfixprice.dao.ExtPurFixPriceHeadMapper;
import com.midea.cloud.srm.sou.purfixprice.dao.ExtPurFixPriceLineMapper;
import com.midea.cloud.srm.sou.purfixprice.service.ExtPurFixPriceLineService;
import com.midea.cloud.srm.sou.purfixprice.service.ExtPurFixPriceQueryService;
import com.midea.cloud.srm.sou.purinq.dao.ExtPurInqSouItemDAO;
import com.midea.cloud.srm.sou.purinq.dao.ExtPurInqSouOrderItemDAO;
import com.midea.cloud.srm.sou.purinq.dao.ExtPurInqSouProjectDAO;
import com.midea.cloud.srm.sou.purinq.dao.ExtPurInqSouProjectMapper;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouItemDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProjectDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouVendorDAO;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderItemDAO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @program: master
 * @description: 1
 * @author: 100014337
 * @create: 2023-12-22 17:00
 * @version 1.0
 **/
@Service
public class ExtPurFixPriceLineServiceImpl  implements ExtPurFixPriceLineService {

    @Autowired
    private ExtPurFixPriceLineMapper extPurFixPriceLineMapper;

    @Resource
    private AgreementService agreementService;

    @Resource
    private ExtPurFixPriceQueryService extPurFixPriceQueryService;

    @Autowired
    private SouProjectDAO souProjectDAO;
    @Autowired
    private ExtPurFixPriceHeadMapper extPurFixPriceHeadMapper;

    @Autowired
    private SouOrderItemDAO souOrderItemDAO;

    @Autowired
    private ExtPurInqSouOrderItemDAO extPurInqSouOrderItemDAO;

    @Autowired
    private ExtPurInqSouItemDAO extPurInqSouItemDAO;

    @Autowired
    private SouItemDAO souItemDAO;
    @Autowired
    private SouVendorDAO souVendorDAO;

    @Autowired
    private ExtPurInqSouProjectDAO extPurInqSouProjectDAO;

    @Override
    public PageInfo<SccSouChDemandAgreement> getAgreement(SccSouChDemandAgreement agreement) {
        String projectCode = agreement.getProjectCode();
        Long count  = Long.valueOf(agreementService.lambdaQuery().eq(SccSouChDemandAgreement::getProjectCode, projectCode).count());
        if(count > NumConstant.ZERO){
            PageUtil.startPage(agreement.getPageNum(), agreement.getPageSize());
            LambdaQueryWrapper<SccSouChDemandAgreement> infoQuery = new LambdaQueryWrapper<>();
            infoQuery.eq(SccSouChDemandAgreement::getProjectCode,projectCode);
            List<SccSouChDemandAgreement> list = agreementService.list(infoQuery);
            return  new PageInfo(list);
        }
        List<SccSouChDemandAgreement> purInqOrderItems = getPurInqOrderItems(projectCode);
        if(CollectionUtils.isNotEmpty(purInqOrderItems)){
            agreementService.saveOrUpdateBatch(purInqOrderItems);
        }
        PageUtil.startPage(agreement.getPageNum(), agreement.getPageSize());
        LambdaQueryWrapper<SccSouChDemandAgreement> infoQuery = new LambdaQueryWrapper<>();
        infoQuery.eq(SccSouChDemandAgreement::getProjectCode,projectCode);
        List<SccSouChDemandAgreement> list = agreementService.list(infoQuery);
        return  new PageInfo(list);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class})
    public  List<SccSouChDemandAgreement> getPurInqOrderItems(String projectCode){
        List<ExtPurInqSouProject> list = extPurInqSouProjectDAO.lambdaQuery().eq(ExtPurInqSouProject::getDesignProjectCode, projectCode).list();
        Long projectId;
        if(CollectionUtils.isNotEmpty(list)){
            projectId = list.get(NumConstant.ZERO).getProjectId();
        }else{
            return Lists.newArrayList();
        }
        SouProject souProject = souProjectDAO.getById(projectId);
        AssertUtils.notNull(souProject, "寻源单[{0}]不存在", projectId);
        ExtPurFixPriceLineGroupQueryDTO queryParam =new ExtPurFixPriceLineGroupQueryDTO();
        queryParam.setProjectId(projectId);
        List<Long> itemIds = extPurFixPriceHeadMapper.getItemIdsForPage(queryParam);
        if (itemIds.isEmpty()) {
            return Lists.newArrayList();
        }
        List<SouOrderItem> orderItemList = souOrderItemDAO.lambdaQuery()
                .eq(SouOrderItem::getProjectId, queryParam.getProjectId())
                .eq(SouOrderItem::getSelectStatus, SouSelectStatusEnum.WIN)
                .in(SouOrderItem::getItemId, itemIds)
                .list();
        if (orderItemList.isEmpty()) {
            return Lists.newArrayList();
        }
        Map<Long/* orderItemId */, ExtPurInqSouOrderItem> inqOrderItemMap = extPurInqSouOrderItemDAO
                .listByIds(orderItemList.stream().map(SouOrderItem::getOrderItemId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(ExtPurInqSouOrderItem::getOrderItemId, Function.identity()));
        // 2: 查询物料需求信息
        Map<Long/* souItemId */, ExtPurInqSouItem> inqSouItemMap = extPurInqSouItemDAO
                .listByIds(orderItemList.stream().map(SouOrderItem::getSouItemId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(ExtPurInqSouItem::getSouItemId, Function.identity()));
        Map<Long/* souItemId */, SouItem> souItemMap = souItemDAO.listByIds(inqSouItemMap.keySet()).stream().collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));
        // 3: 查询供应商信息
        Map<Long/* vendorId */, SouVendor> vendorMap = souVendorDAO.list(SouVendor::getProjectId, queryParam.getProjectId())
                .stream().collect(Collectors.toMap(SouVendor::getVendorId, Function.identity()));
        // 4: 组装数据
        List<SccSouChDemandAgreement> voList = new ArrayList<>(orderItemList.size());
        for (SouOrderItem orderItem : orderItemList) {
            SccSouChDemandAgreement vo = new SccSouChDemandAgreement();
            voList.add(vo);
            vo.setProjectId(projectId);
            vo.setProjectCode(projectCode);
            ExtPurInqSouItem inqSouItem = inqSouItemMap.get(orderItem.getSouItemId());
            SouItem souItem = souItemMap.get(orderItem.getSouItemId());
            SouVendor vendor = vendorMap.get(orderItem.getVendorId());
            // 物料
            vo.setItemId(orderItem.getItemId());
            vo.setItemCode(orderItem.getItemCode());
            vo.setItemDesc(orderItem.getItemDesc());
            // 品牌
            vo.setBrand(inqSouItem.getBrand());
            // 供货区域
            vo.setArea(inqSouItem.getArea());
            // 规格型号
            vo.setModel(inqSouItem.getModel());
            // 单位
            vo.setUnit(orderItem.getUnit());
            // 备注
            vo.setRemark(souItem.getRemark());
            // 未税单价
            vo.setNotaxPrice(orderItem.getStandardNotaxPrice());
            // 含税单价
            vo.setTaxPrice(orderItem.getStandardTaxPrice());
            // 供应商
            vo.setVendorId(vendor.getVendorId());
            vo.setVendorCode(vendor.getVendorCode());
            vo.setVendorName(vendor.getVendorName());
        }
        return voList;
    }
}

