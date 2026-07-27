package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.control.itemrefresh;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouItemRecordDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouVendorAuthDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouVendorDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderItemDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.control.ApiSouItemRefreshAuthDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.control.ApiSouItemRefreshDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouItemRefreshStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouItemRefreshTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 寻源openAPI - 物料变更执行服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/02
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ApiSouItemRefreshHandler implements ISouSpiBean {

    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private SouItemRecordDAOImpl souItemRecordDao;
    @Autowired
    private SouVendorDAOImpl souVendorDao;
    @Autowired
    private SouOrderItemDAOImpl souOrderItemDao;
    @Autowired
    private SouVendorAuthDAOImpl souVendorAuthDao;

    public SouItemRefreshPO convert(ApiSouItemRefreshDTO param, String souType) {
        SouItemRefreshPO po = new SouItemRefreshPO();

        // 1: 查询最新的刷新记录信息
        SouProject souProject = souProjectDao.getById(param.getProjectId());
        List<SouItemRecord> latestRecordList = this.getLatestRecordList(param);
        // 2: 转换得到需要新增/更新的物料
        this.convertItemList(po, latestRecordList);
        // 3: 转换得到新增/更新的报价权限
        this.convertAuthList(po, param, latestRecordList);
        // 4: 转换得到更新的物料记录
        po.setUpdateRecordList(latestRecordList); {
            latestRecordList.forEach(record -> {
                record.setRefreshStatus(SouItemRefreshStatusEnum.DONE);
                record.setRefreshRound(souProject.getCurrentRound());
            });
        }
        // 5: 更新现有的报价行信息(由于报价行表缓存了物料信息，因此可能涉及到物料信息更新)
        this.convertExistOrderItems(po, param.getProjectId());

        return po;
    }

    protected List<SouItemRecord> getLatestRecordList(ApiSouItemRefreshDTO param) {
        List<SouItemRecord> recordList = souItemRecordDao.lambdaQuery()
                .eq(SouItemRecord::getProjectId, param.getProjectId())
                //批次号倒序排列
                .orderByDesc(SouItemRecord::getBatchNo)
                .list();
        AssertUtils.notEmpty(recordList, "当前无待变更的物料推送记录");
        String latestBatchNo = recordList.get(0).getBatchNo();
        List<SouItemRecord> latestRecordList = recordList.stream().filter(e -> latestBatchNo.equals(e.getBatchNo())).collect(Collectors.toList());
        switch (recordList.get(0).getRefreshStatus()) {
            //未刷新
            case DRAFT:
                //刷新失败
            case FAIL:
                break;
            default:
                throw new IllegalArgumentException("当前无待变更的物料推送记录");
        }
        return latestRecordList;
    }

    protected void convertItemList(SouItemRefreshPO po, List<SouItemRecord> latestRecordList) {
        List<SouItemRecord> existRecordList = latestRecordList.stream()
                .filter(e -> SouItemRefreshTypeEnum.EXIST.equals(e.getRefreshType()) || SouItemRefreshTypeEnum.DELETE.equals(e.getRefreshType()))
                .collect(Collectors.toList());
        if (!existRecordList.isEmpty()) {
            po.setUpdateItemList(new ArrayList<>(existRecordList.size()));
            for (SouItemRecord record : existRecordList) {
                po.getUpdateItemList().add(record.getItemInfo());
            }
        }

        List<SouItemRecord> newRecordList = latestRecordList.stream()
                .filter(e -> SouItemRefreshTypeEnum.NEW.equals(e.getRefreshType()))
                .collect(Collectors.toList());
        if (!newRecordList.isEmpty()) {
            po.setSaveItemList(new ArrayList<>(newRecordList.size()));
            for (SouItemRecord record : newRecordList) {
                po.getSaveItemList().add(record.getItemInfo());
            }
        }
    }

    protected void convertAuthList(SouItemRefreshPO po, ApiSouItemRefreshDTO param, List<SouItemRecord> latestRecordList) {
        List<SouItemRecord> newRecordList = latestRecordList.stream()
                .filter(e -> SouItemRefreshTypeEnum.NEW.equals(e.getRefreshType()))
                .collect(Collectors.toList());
        List<SouVendor> vendorList = souVendorDao.lambdaQuery().eq(SouVendor::getProjectId, param.getProjectId()).list();
        if (!newRecordList.isEmpty()) {
            Map<Long/* recordId */, ApiSouItemRefreshAuthDTO> authMap; {
                if (CollectionUtils.isEmpty(param.getAuthList())) {
                    authMap = Collections.emptyMap();
                } else {
                    authMap = param.getAuthList().stream().collect(Collectors.toMap(ApiSouItemRefreshAuthDTO::getRecordId, Function.identity()));
                }
            }
            po.setSaveAuthList(new ArrayList<>(newRecordList.size()));

            // 暂时只考虑新增物料时对报价权限的处理
            newRecordList.forEach(record -> vendorList.forEach(vendor -> {
                SouVendorAuth auth = new SouVendorAuth();
                po.getSaveAuthList().add(auth);

                // ID
                auth.setVendorAuthId(IdGenrator.generate());
                // 寻源单ID
                auth.setProjectId(param.getProjectId());
                // 物料需求ID
                auth.setSouItemId(record.getItemInfo().getSouItemId());
                // 供应商ID
                auth.setVendorId(vendor.getVendorId());
                // 是否禁止报价
                ApiSouItemRefreshAuthDTO authDTO = authMap.get(record.getRecordId());
                auth.setForbidPrice(authDTO != null ? (authDTO.getForbidPrice() != null ? authDTO.getForbidPrice() : Enable.N) : Enable.N);

                BeanUtils.copyProperties(record.getItemInfo(), auth);
            }));
        }

        // 对现有报价权限的更新
        List<SouVendorAuth> existVendorAuths = souVendorAuthDao.list(SouVendorAuth::getProjectId, param.getProjectId());
        if (po.getUpdateAuthList() == null) {
            po.setUpdateAuthList(new ArrayList<>(existVendorAuths.size()));
        }
        Map<Long/* souItemId */, SouItem> updateItemMap = po.getUpdateItemList().stream()
                .collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));
        for (SouVendorAuth auth : existVendorAuths) {
            SouItem updateItem = updateItemMap.get(auth.getSouItemId());
            if (updateItem == null) { continue; }
            po.getUpdateAuthList().add(auth);

            // 物料ID
            auth.setItemId(updateItem.getItemId());
            // 物料编码
            auth.setItemCode(updateItem.getItemCode());
            // 物料名称
            auth.setItemDesc(updateItem.getItemDesc());
            // 单位
            auth.setUnit(updateItem.getUnit());
            // 品类ID
            auth.setCategoryId(updateItem.getCategoryId());
            // 品类编码
            auth.setCategoryCode(updateItem.getCategoryCode());
            // 品类名称
            auth.setCategoryName(updateItem.getCategoryName());
        }
    }

    protected void convertAuthList(SouItemRefreshPO po, @Nullable List<ApiSouItemRefreshAuthDTO> authList, List<SouItemRecord> latestRecordList) {
    }

    protected void convertExistOrderItems(SouItemRefreshPO po, long projectId) {
        if (CollectionUtils.isEmpty(po.getUpdateItemList())) { return; }
        List<SouOrderItem> orderItemList = souOrderItemDao.list(SouOrderItem::getProjectId, projectId);
        if (po.getUpdateOrderItemList() == null) {
            po.setUpdateOrderItemList(new ArrayList<>(orderItemList.size()));
        }

        Map<Long/* souItemId */, SouItem> updateItemMap = po.getUpdateItemList().stream()
                .collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));
        for (SouOrderItem orderItem : po.getUpdateOrderItemList()) {
            SouItem updateItem = updateItemMap.get(orderItem.getSouItemId());
            if (updateItem == null) { continue; }
            po.getUpdateOrderItemList().add(orderItem);

            // 物料组合
            orderItem.setItemGroup(updateItem.getItemGroup());
            // 是无料号物料
            orderItem.setNoCodeItem(updateItem.getNoCodeItem());
            // 物料ID
            orderItem.setItemId(updateItem.getItemId());
            // 物料编码
            orderItem.setItemCode(updateItem.getItemCode());
            // 物料名称
            orderItem.setItemDesc(updateItem.getItemDesc());
            // 单位
            orderItem.setUnit(updateItem.getUnit());
            // 品类ID
            orderItem.setCategoryId(updateItem.getCategoryId());
            // 品类编码
            orderItem.setCategoryCode(updateItem.getCategoryCode());
            // 品类名称
            orderItem.setCategoryName(updateItem.getCategoryName());
            // 需求数量
            orderItem.setRequireQuantity(updateItem.getRequireQuantity());
        }
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
