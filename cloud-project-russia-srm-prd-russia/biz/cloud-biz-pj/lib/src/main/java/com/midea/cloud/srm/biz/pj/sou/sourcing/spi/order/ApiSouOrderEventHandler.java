package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.*;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order.editorder.SouOrderEditPO;
import com.midea.cloud.srm.feign.supplier.SupplierClient;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderCancelDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderWithdrawDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.*;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.model.supplier.info.entity.ContactInfo;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 寻源openAPI - 报价业务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/30
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ApiSouOrderEventHandler implements ISouSpiBean {

    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private SouVendorDAOImpl souVendorDao;
    @Autowired
    private SupplierClient supplierClient;
    @Autowired
    private SouProcessConfigDAOImpl souProcessConfigDao;
    @Autowired
    private SouVendorAuthDAOImpl souVendorAuthDao;
    @Autowired
    private SouOrderDAOImpl souOrderDao;
    @Autowired
    private SouRoundDAOImpl souRoundDao;

    @ApiOperation("暂存/提交报价单前的额外处理")
    public void doHandlerBeforeEditOrder(ApiSouOrderDTO param, String souType) {
    }

    @ApiOperation("暂存/提交报价单后的额外处理")
    public void doHandlerAfterEditOrder(ApiSouOrderDTO param, String souType, SouOrderEditPO po) {
    }

    @ApiOperation("公开+首次报价时新增供应商信息")
    public void doHandlerForNewVendorWhileOrder(long projectId, long vendorId, String souType) {
        SouProject project = souProjectDao.getById(projectId);
        if (!SouPublishScopeEnum.OPEN_TENDER.equals(project.getPublishScope())) {
            return;
        }
        SouVendor vendor = souVendorDao.lambdaQuery()
                .eq(SouVendor::getProjectId, projectId)
                .eq(SouVendor::getVendorId, vendorId)
                .one();
        if (vendor != null) {
            return;
        }
        // 添加供应商
        vendor = new SouVendor();
        {
            // ID
            vendor.setSouVendorId(IdGenrator.generate());
            // 寻源单ID
            vendor.setProjectId(projectId);
            // 供应商ID
            vendor.setVendorId(vendorId);
            CompanyInfo companyInfo = supplierClient.getCompanyInfo(vendorId);
            AssertUtils.notNull(companyInfo, LocaleHandler.getLocaleMsg("供应商") + "[{0}]" + LocaleHandler.getLocaleMsg("不存在"), vendor.getVendorId());
            // 供应商编码
            vendor.setVendorCode(companyInfo.getCompanyCode());
            // 供应商名称
            vendor.setVendorName(companyInfo.getCompanyName());
            // 加入轮次
            vendor.setJoinRound(project.getCurrentRound());
            // 报名状态/时间
            SouProcessConfig processConfig = souProcessConfigDao.getById(project.getProcessConfigId());
            if (Enable.Y.equals(processConfig.getSignUpManagement())) {
                // 有报名节点
                vendor.setSignUpStatus(SouSignUpStatusEnum.SIGN_UP_DONE);
                vendor.setSignUpTime(new Date());
            } else {
                vendor.setSignUpStatus(SouSignUpStatusEnum.NO_SIGN_UP);
                vendor.setSignUpTime(null);
            }
            // 联系信息
            ContactInfo contactInfo = null;
            {
                List<ContactInfo> contactInfoList = supplierClient.listContactInfoByCompanyId(vendorId);
                if (CollectionUtils.isNotEmpty(contactInfoList)) {
                    for (ContactInfo info : contactInfoList) {
                        if (Enable.Y.name().equals(info.getCeeaDefaultContact())) {
                            contactInfo = info;
                            break;
                        }
                    }
                    if (contactInfo == null) {
                        contactInfo = contactInfoList.get(0);
                    }
                }
            }
            vendor.setLinkmanName(contactInfo != null ? contactInfo.getContactName() : null);
            vendor.setPhone(contactInfo != null ? contactInfo.getCeeaContactMethod() : null);
            vendor.setEmail(contactInfo != null ? contactInfo.getEmail() : null);
            // 排序
            vendor.setSortIndex((int) (souVendorDao.lambdaQuery().eq(SouVendor::getProjectId, projectId).count() + 1));
        }
        // 添加供应商报价权限
        List<SouItem> validItemList = SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderQueryHandler.class)
                .getValidItemsInSpecifiedRound(projectId, project.getCurrentRound());
        List<SouVendorAuth> authList = new ArrayList<>(validItemList.size());
        for (SouItem souItem : validItemList) {
            SouVendorAuth auth = new SouVendorAuth();
            authList.add(auth);

            // ID
            auth.setVendorAuthId(IdGenrator.generate());
            // 供应商表ID
            auth.setSouVendorId(vendor.getSouVendorId());
            // 供应商ID
            auth.setVendorId(vendorId);
            // 是否禁止报价
            auth.setForbidPrice(Enable.N);
            BeanUtils.copyProperties(souItem, auth);
        }

        // 保存数据
        souVendorDao.save(vendor);
        souVendorAuthDao.saveBatch(authList);
    }

    @ApiOperation("暂存/提交报价、作废报价、撤回报价后，更新报价供应商数量")
    public void doHandlerForOrderCountWhileOrder(long projectId) {
        SouProject project = souProjectDao.getById(projectId);
        // 查询当前轮次的应/已报价供应商人数
        int inviteCount, orderCount;
        {
            Set<Long> currentRoundAuthVendorIds = SouActiveBeanUtils.getActiveBean(project.getSouType(), ApiSouOrderQueryHandler.class)
                    .getAuthedVendors(project.getProjectId(), project.getCurrentRound());
            List<SouOrder> currentRoundOrders = souOrderDao.lambdaQuery()
                    .eq(SouOrder::getProjectId, project.getProjectId())
                    .eq(SouOrder::getRound, project.getCurrentRound())
                    .list();
            if (currentRoundAuthVendorIds.isEmpty()) {
                inviteCount = 0;
            } else {
                inviteCount = currentRoundAuthVendorIds.size();
            }
            orderCount = (int) currentRoundOrders.stream().map(SouOrder::getOrderStatus)
                    .filter(e -> SouOrderStatusEnum.SUBMISSION.equals(e) || SouOrderStatusEnum.CANCEL.equals(e))
                    .count();
        }
        // 更新本轮次报价信息
        souRoundDao.lambdaUpdate()
                .set(SouRound::getInviteCount, inviteCount)
                .set(SouRound::getOrderCount, orderCount)
                .eq(SouRound::getProjectId, project.getProjectId())
                .eq(SouRound::getRound, project.getCurrentRound())
                .update();
        souProjectDao.lambdaUpdate()
                .set(SouProject::getInviteCount, inviteCount)
                //.set(SouProject::getProjectStatus, SouProjectStatusEnum.EVALUATING)
                .set(SouProject::getOrderCount, orderCount)
                .eq(SouProject::getProjectId, project.getProjectId())
                .update();
    }

    @ApiOperation("撤回报价前的额外处理")
    public void doHandlerBeforeWithdrawOrder(ApiSouOrderWithdrawDTO param, String souType) {
    }

    @ApiOperation("撤回报价后的额外处理")
    public void doHandlerAfterWithdrawOrder(ApiSouOrderWithdrawDTO param, String souType) {
    }

    @ApiOperation("作废报价前的额外处理")
    public void doHandlerBeforeCancelOrder(ApiSouOrderCancelDTO param, String souType) {
    }

    @ApiOperation("作废报价后的额外处理")
    public void doHandlerAfterCancelOrder(ApiSouOrderCancelDTO param, String souType) {
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
