package com.midea.cloud.srm.sou.purinq.service.impl;

import com.github.pagehelper.page.PageMethod;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.EasyExcelUtil;
import com.midea.cloud.component.filter.HttpServletHolder;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.inq.enums.ExtPurInqSouTypeEnum;
import com.midea.cloud.srm.model.extapi.sou.purinq.dto.order.ExtPurInqOrderItemHisQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouItem;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouItemRound;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouOrder;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouOrderItem;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.init.ApiPurInqSouInitDetailVO;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.order.ApiPurInqSouOrderDetailVO;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.order.ApiPurInqSouOrderItemVO;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.order.ExtPurInqOrderItemHisQueryVO;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.order.ExtPurInqSouOrderVO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.init.ApiSouInitDetailVO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.order.ApiSouOrderFileVO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.sou.purinq.dao.*;
import com.midea.cloud.srm.sou.purinq.service.ExtPurInqSouOrderQueryService;
import com.midea.cloud.srm.sou.sourcing.control.service.SouControlEventService;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouItemDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouItemLadderDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProjectDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouVendorDAO;
import com.midea.cloud.srm.sou.sourcing.init.service.SouInitQueryService;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderDAO;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderFileDAO;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderItemDAO;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderItemLadderDAO;
import com.midea.cloud.srm.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.sou.sourcing.spi.order.ApiSouOrderQueryHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtPurInqSouOrderQueryServiceImpl implements ExtPurInqSouOrderQueryService {

    @Autowired
    private SouControlEventService souControlEventService;
    @Autowired
    private SouProjectDAO souProjectDAO;
    @Autowired
    private SouOrderDAO souOrderDAO;
    @Autowired
    private SouOrderItemDAO souOrderItemDAO;
    @Autowired
    private ExtPurInqSouOrderItemDAO extPurInqSouOrderItemDAO;
    @Autowired
    private SouItemDAO souItemDAO;
    @Autowired
    private ExtPurInqSouItemDAO extPurInqSouItemDAO;
    @Autowired
    private SouOrderItemLadderDAO souOrderItemLadderDAO;
    @Autowired
    private SouInitQueryService souInitQueryService;
    @Autowired
    private ExtPurInqSouOrderDAO extPurInqSouOrderDAO;
    @Autowired
    private SouOrderFileDAO souOrderFileDAO;
    @Autowired
    private SouItemLadderDAO souItemLadderDAO;
    @Autowired
    private ExtPurInqSouOrderMapper extPurInqSouOrderMapper;
    @Autowired
    private ExtPurInqSouItemRoundDAO extPurInqSouItemRoundDAO;
    @Autowired
    private SouVendorDAO souVendorDAO;

    /**
     * 获取上一轮报价
     */
    @Override
    public List<ApiPurInqSouOrderItemVO> getLastOrderItems(long projectId, long vendorId) {
        // 1: 查询询价单信息
        SouProject souProject = souProjectDAO.getById(projectId);
        AssertUtils.notNull(souProject, "询价单[{0}]不存在", projectId);
        AssertUtils.isTrue(ExtPurInqSouTypeEnum.ext_pur_inq.name().equals(souProject.getSouType()), "非询比价单据");
        if (souProject.getCurrentRound() == null || souProject.getCurrentRound() <= 1) { return Collections.emptyList(); }
        // 2: 查询供应商在小于当前轮次的所有有效的报价明细
        Map<Long/* orderItemId */, SouOrderItem> orderItemMap = souOrderItemDAO.lambdaQuery()
                .eq(SouOrderItem::getProjectId, projectId)
                .eq(SouOrderItem::getVendorId, vendorId)
                .eq(SouOrderItem::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                .lt(SouOrderItem::getRound, souProject.getCurrentRound())
                .list().stream().collect(Collectors.toMap(SouOrderItem::getOrderItemId, Function.identity()));
        List<ExtPurInqSouOrderItem> inqOrderItemList = extPurInqSouOrderItemDAO.listByIds(orderItemMap.keySet());
        // 4: 查询物料需求信息
        Map<Long/* souItemId */, SouItem> souItemMap = souItemDAO.list(SouItem::getProjectId, projectId).stream().collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));
        Map<Long/* souItemId */, ExtPurInqSouItem> inqSouItemMap = extPurInqSouItemDAO.list(ExtPurInqSouItem::getProjectId, projectId).stream().collect(Collectors.toMap(ExtPurInqSouItem::getSouItemId, Function.identity()));
        // 5: 查询阶梯报价
        Map<Long/* orderItemId */, List<SouOrderItemLadder>> orderItemLadderMap = souOrderItemLadderDAO.lambdaQuery()
                .in(SouOrderItemLadder::getOrderItemId, orderItemMap.keySet())
                .orderByAsc(SouOrderItemLadder::getSortIndex)
                .list().stream().collect(Collectors.groupingBy(SouOrderItemLadder::getOrderItemId));
        // 6: 组装数据
        List<ApiPurInqSouOrderItemVO> voList = new ArrayList<>(inqOrderItemList.size());
        for (ExtPurInqSouOrderItem inqOrderItem : inqOrderItemList) {
            ApiPurInqSouOrderItemVO vo = new ApiPurInqSouOrderItemVO();
            voList.add(vo);

            SouOrderItem orderItem = orderItemMap.get(inqOrderItem.getOrderItemId());
            SouObjectXUtil.mergeProperties(orderItem, vo);

            SouObjectXUtil.mergeProperties(inqOrderItem, vo);

            SouItem souItem = souItemMap.get(vo.getSouItemId());
            SouObjectXUtil.mergeProperties(souItem, vo);

            ExtPurInqSouItem inqSouItem = inqSouItemMap.get(vo.getSouItemId());
            SouObjectXUtil.mergeProperties(inqSouItem, vo);

            vo.setLadderPriceList(orderItemLadderMap.get(vo.getOrderItemId()));
        }

        return voList;
    }

    /**
     * 查询指定轮次报价信息
     */
    @Override
    public ApiPurInqSouOrderDetailVO getSouOrderInfo(long projectId, long vendorId, @Nullable Integer round) {
        // 0: 刷新数据
        souControlEventService.refreshProjectBySouTime(projectId);
        // 1: 查询立项信息
        ApiPurInqSouInitDetailVO inqSouInitInfo; {
            ApiSouInitDetailVO souInitInfo = souInitQueryService.getSouInitInfo(projectId, ExtPurInqSouTypeEnum.ext_pur_inq.name());
            souInitInfo.doVendorView(vendorId);
            inqSouInitInfo = SouObjectXUtil.convertTargetObj(souInitInfo, ApiPurInqSouInitDetailVO.class);
        }
        round = round != null ? round : inqSouInitInfo.getProjectInfo().getCurrentRound();
        // 2: 查询指定轮次生效的物料信息
        Set<Long> availableSouItemIds = extPurInqSouItemRoundDAO.lambdaQuery()
                .eq(ExtPurInqSouItemRound::getProjectId, projectId)
                .eq(ExtPurInqSouItemRound::getRound, round)
                .eq(ExtPurInqSouItemRound::getCanOrder, Enable.Y)
                .list().stream().map(ExtPurInqSouItemRound::getSouItemId).collect(Collectors.toSet());
        // 3: 查询本轮报价单
        SouOrder souOrder = souOrderDAO.lambdaQuery()
                .eq(SouOrder::getProjectId, projectId)
                .eq(SouOrder::getVendorId, vendorId)
                .eq(SouOrder::getRound, round)
                .one();
        ExtPurInqSouOrder inqSouOrder = null; {
            if (souOrder != null) {
                inqSouOrder = extPurInqSouOrderDAO.getById(souOrder.getOrderId());
            }
        }
        // 4: 查询本轮报价明细
        List<SouOrderItem> orderItemList = Collections.emptyList(); {
            if (souOrder != null) {
                orderItemList = souOrderItemDAO.list(SouOrderItem::getOrderId, souOrder.getOrderId());
            }
        }
        Map<Long/* orderItemId */, ExtPurInqSouOrderItem> inqOrderItemMap = Collections.emptyMap(); {
            if (souOrder != null) {
                inqOrderItemMap = extPurInqSouOrderItemDAO.list(ExtPurInqSouOrderItem::getOrderId, souOrder.getOrderId())
                        .stream().collect(Collectors.toMap(ExtPurInqSouOrderItem::getOrderItemId, Function.identity()));
            }
        }
        // 5: 查询物料需求信息
        List<SouItem> souItemList = SouActiveBeanUtils.getActiveBean(ExtPurInqSouTypeEnum.ext_pur_inq.name(), ApiSouOrderQueryHandler.class)
                .getAvailableItemsForVendor(projectId, round, vendorId);
        Map<Long/* souItemId */, ExtPurInqSouItem> inqSouItemMap = extPurInqSouItemDAO.listByIds(souItemList.stream().map(SouItem::getSouItemId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(ExtPurInqSouItem::getSouItemId, Function.identity()));
        // 6: 查询阶梯报价
        Map<Long/* orderItemId */, List<SouOrderItemLadder>> orderItemLadderMap = Collections.emptyMap(); {
            if (!inqOrderItemMap.isEmpty()) {
                orderItemLadderMap = souOrderItemLadderDAO.lambdaQuery()
                        .in(SouOrderItemLadder::getOrderItemId, inqOrderItemMap.keySet())
                        .orderByAsc(SouOrderItemLadder::getSortIndex)
                        .list().stream().collect(Collectors.groupingBy(SouOrderItemLadder::getOrderItemId));
            }
        }
        // 7: 查询附件
        List<SouOrderFile> orderFileList; {
            if (souOrder != null) {
                orderFileList = souOrderFileDAO.lambdaQuery()
                        .eq(SouOrderFile::getOrderId, souOrder.getOrderId())
                        .list();
            } else {
                orderFileList = Collections.emptyList();
            }
        }
        // 8: 查询阶梯价模板信息
        Map<Long/* souItemId */, List<SouItemLadder>> souItemLadderMap = souItemLadderDAO.list(SouItemLadder::getProjectId, projectId)
                .stream().sorted(Comparator.comparing(SouItemLadder::getSortIndex)).collect(Collectors.groupingBy(SouItemLadder::getSouItemId));
        // 9: 组装数据
        ApiPurInqSouOrderDetailVO vo = new ApiPurInqSouOrderDetailVO(); {
            vo.setInitInfo(inqSouInitInfo);
            if (souOrder != null) {
                ExtPurInqSouOrderVO order = SouObjectXUtil.convertTargetObj(souOrder, ExtPurInqSouOrderVO.class);
                SouObjectXUtil.mergeProperties(inqSouOrder, order);
                vo.setOrder(order);
            }
            if (vo.getOrder() != null) {
                SouObjectXUtil.mergeProperties(inqSouOrder, vo.getOrder());
            }
            if (CollectionUtils.isNotEmpty(orderFileList)) {
                vo.setOrderFileList(SouObjectXUtil.convertList(orderFileList, ApiSouOrderFileVO.class));
            }
            vo.setItemList(SouObjectXUtil.convertList(souItemList, ApiPurInqSouOrderItemVO.class)); {
                // 去掉指定轮次未生效的数据
                vo.getItemList().removeIf(e -> !availableSouItemIds.contains(e.getSouItemId()));

                Map<Long/* souItemId */, SouOrderItem> orderItemMap = orderItemList.stream().collect(Collectors.toMap(SouOrderItem::getSouItemId, Function.identity()));
                for (ApiPurInqSouOrderItemVO orderItem : vo.getItemList()) {
                    SouOrderItem oi = orderItemMap.get(orderItem.getSouItemId());
                    if (oi != null) {
                        SouObjectXUtil.mergeProperties(oi, orderItem);
                    }

                    if (oi != null) {
                        ExtPurInqSouOrderItem ioi = inqOrderItemMap.get(oi.getOrderItemId());
                        if (ioi != null) {
                            SouObjectXUtil.mergeProperties(ioi, orderItem);
                        }
                    }

                    ExtPurInqSouItem inqSouItem = inqSouItemMap.get(orderItem.getSouItemId());
                    SouObjectXUtil.mergeProperties(inqSouItem, orderItem);

                    List<SouOrderItemLadder> orderLadderList = orderItemLadderMap.get(orderItem.getOrderItemId());
                    if (CollectionUtils.isNotEmpty(orderLadderList)) {
                        orderItem.setLadderPriceList(orderLadderList);
                    } else {
                        List<SouItemLadder> souLadderList = souItemLadderMap.get(orderItem.getSouItemId());
                        if (CollectionUtils.isNotEmpty(souLadderList)) {
                            orderItem.setLadderPriceList(SouObjectXUtil.convertList(souLadderList, SouOrderItemLadder.class));
                        }
                    }

                    orderItem.setRound(round);
                }
            }
        }

        return vo;
    }

    /**
     * 供应商历史报价列表查询
     */
    @Override
    public List<ExtPurInqOrderItemHisQueryVO> listVendorOrderHis(ExtPurInqOrderItemHisQueryDTO queryParam) {
        queryParam.formatParams();
        // 1: 查询数据
        if (queryParam.getPageNum() != null && queryParam.getPageSize() != null) {
            PageMethod.startPage(queryParam.getPageNum(), queryParam.getPageSize());
        }
        List<ExtPurInqOrderItemHisQueryVO> voList = SouObjectXUtil.convertList(extPurInqSouOrderMapper.listVendorOrderHis(queryParam), ExtPurInqOrderItemHisQueryVO.class);
        if (voList.isEmpty()) { return voList; }
        // 2: 查询额外信息
        // 2.1: 查询询比价报价明细信息
        Map<Long/* orderItemId */, ExtPurInqSouOrderItem> inqOrderItemMap = extPurInqSouOrderItemDAO.listByIds(voList.stream().map(ExtPurInqOrderItemHisQueryVO::getOrderItemId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(ExtPurInqSouOrderItem::getOrderItemId, Function.identity()));
        voList.forEach(vo -> SouObjectXUtil.mergeProperties(inqOrderItemMap.get(vo.getOrderItemId()), vo));
        // 2.2: 查询物料需求信息
        Set<Long> souItemIds = voList.stream().map(ExtPurInqOrderItemHisQueryVO::getSouItemId).collect(Collectors.toSet());
        Map<Long/* souItemId */, SouItem> souItemMap = souItemDAO.listByIds(souItemIds).stream().collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));
        voList.forEach(vo -> SouObjectXUtil.mergeProperties(souItemMap.get(vo.getSouItemId()), vo));
        // 2.3: 查询询比价物料需求信息
        Map<Long/* souItemId */, ExtPurInqSouItem> inqSouItemMap = extPurInqSouItemDAO.listByIds(souItemIds).stream().collect(Collectors.toMap(ExtPurInqSouItem::getSouItemId, Function.identity()));
        voList.forEach(vo -> SouObjectXUtil.mergeProperties(inqSouItemMap.get(vo.getSouItemId()), vo));
        // 2.4: 查询询比价报价单信息
        Map<Long/* orderId */, ExtPurInqSouOrder> inqOrderMap = extPurInqSouOrderDAO.listByIds(voList.stream().map(ExtPurInqOrderItemHisQueryVO::getOrderId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(ExtPurInqSouOrder::getOrderId, Function.identity()));
        voList.forEach(vo -> SouObjectXUtil.mergeProperties(inqOrderMap.get(vo.getOrderId()), vo));
        // 2.5: 查询询价单信息
        Map<Long/* projectId */, SouProject> souProjectMap = souProjectDAO.listByIds(voList.stream().map(ExtPurInqOrderItemHisQueryVO::getProjectId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(SouProject::getProjectId, Function.identity()));
        voList.forEach(vo -> SouObjectXUtil.mergeProperties(souProjectMap.get(vo.getProjectId()), vo));
        // 2.6: 查询报价单
        Map<Long/* orderId */, SouOrder> orderMap = souOrderDAO.listByIds(inqOrderMap.keySet()).stream().collect(Collectors.toMap(SouOrder::getOrderId, Function.identity()));
        voList.forEach(vo -> SouObjectXUtil.mergeProperties(orderMap.get(vo.getOrderId()), vo));
        // 2.7: 查询供应商
        Map<String/* projectId_vendorId */, SouVendor> vendorMap = souVendorDAO.lambdaQuery()
                .in(SouVendor::getProjectId, souProjectMap.keySet())
                .list().stream().collect(Collectors.toMap(e -> e.getProjectId() + "_" + e.getVendorId(), Function.identity()));
        voList.forEach(vo -> {
            SouVendor vendor = vendorMap.get(vo.getProjectId() + "_" + vo.getVendorId());
            if (vendor != null) {
                vo.setVendorCode(vendor.getVendorCode());
                vo.setVendorName(vendor.getVendorName());
            }
        });

        return voList;
    }

    /**
     * 下载集采报价须知附件
     */
    @Override
    public void getPurOrderNoticeFile() throws IOException {
        try (OutputStream outputStream = getServletOutputStreamForPurInqOrderFile(HttpServletHolder.getResponse(), "集采报价须知.doc")) {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template-file/pur_inq_order_notice_file.doc");
            if (inputStream == null) { return; }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int len;
            byte[] bytes = new byte[1024];
            while ((len = inputStream.read(bytes)) != -1) {
                byteArrayOutputStream.write(bytes, 0, len);
            }
            inputStream.close();
            byteArrayOutputStream.close();
            byteArrayOutputStream.writeTo(outputStream);
        }
    }

    private static ServletOutputStream getServletOutputStreamForPurInqOrderFile(HttpServletResponse response, String fileName) throws IOException {
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.setContentType("application/msword");
        response.setCharacterEncoding("utf8");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        response.setHeader("Pragma", "public");
        response.setHeader("Cache-Control", "no-store");
        response.addHeader("Cache-Control", "max-age=0");
        return response.getOutputStream();
    }

}
