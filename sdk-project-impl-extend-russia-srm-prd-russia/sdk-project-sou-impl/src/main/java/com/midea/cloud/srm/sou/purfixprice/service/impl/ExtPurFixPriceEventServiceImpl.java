package com.midea.cloud.srm.sou.purfixprice.service.impl;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouProject;
import com.midea.cloud.srm.model.sou.fixprice.enums.ExtFixPriceStatusEnum;
import com.midea.cloud.srm.model.sou.purfixprice.dto.ExtPurFixPriceEditDTO;
import com.midea.cloud.srm.model.sou.purfixprice.entity.ExtPurFixPriceFile;
import com.midea.cloud.srm.model.sou.purfixprice.entity.ExtPurFixPriceHead;
import com.midea.cloud.srm.model.sou.purfixprice.entity.ExtPurFixPriceLine;
import com.midea.cloud.srm.model.sou.purfixprice.enums.ExtPurFixPriceStatusEnum;
import com.midea.cloud.srm.sou.purfixprice.dao.ExtPurFixPriceFileDAO;
import com.midea.cloud.srm.sou.purfixprice.dao.ExtPurFixPriceHeadDAO;
import com.midea.cloud.srm.sou.purfixprice.dao.ExtPurFixPriceLineDAO;
import com.midea.cloud.srm.sou.purfixprice.plugin.event.editfixprice.ExtPurFixPriceEditContext;
import com.midea.cloud.srm.sou.purfixprice.plugin.event.editfixprice.ExtPurFixPriceEditPlugin;
import com.midea.cloud.srm.sou.purfixprice.service.ExtPurFixPriceEventService;
import com.midea.cloud.srm.sou.purinq.dao.ExtPurInqSouProjectDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtPurFixPriceEventServiceImpl implements ExtPurFixPriceEventService {

    @Autowired
    private ExtPurFixPriceHeadDAO extPurFixPriceHeadDAO;
    @Autowired
    private ExtPurFixPriceLineDAO extPurFixPriceLineDAO;
    @Autowired
    private ExtPurFixPriceFileDAO extPurFixPriceFileDAO;
    @Autowired
    private ExtPurFixPriceEditPlugin extPurFixPriceEditPlugin;
    @Autowired
    private ExtPurInqSouProjectDAO extPurInqSouProjectDAO;

    /**
     * 编辑定价单
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void editFixPrice(ExtPurFixPriceEditDTO param) {
        ExtPurFixPriceEditContext context = new ExtPurFixPriceEditContext(param);
        // 1: 校验操作条件/权限
        extPurFixPriceEditPlugin.judgeEditAuth(context);
        // 2: 校验处理
        extPurFixPriceEditPlugin.validateAndConvertEdit(context);
        // 3: 保存数据
        if (context.getExistFixPriceHead() != null) {
            extPurFixPriceHeadDAO.updateById(context.getPriceHeadEntity());
        } else {
            extPurFixPriceHeadDAO.save(context.getPriceHeadEntity());
        }
        extPurFixPriceLineDAO.saveOrUpdate(context.getPriceHeadEntity().getPurFixPriceHeadId(), context.getPriceLineListEntity(), ExtPurFixPriceLine::getPurFixPriceHeadId);
        extPurFixPriceFileDAO.saveOrUpdate(context.getPriceHeadEntity().getPurFixPriceHeadId(), context.getPriceFileListEntity(), ExtPurFixPriceFile::getPurFixPriceHeadId);
        // 4: 更新上游集采询比价相关状态
        if (context.getExistFixPriceHead() != null && !context.getExistFixPriceHead().getSouProjectId().equals(context.getPriceHeadEntity().getSouProjectId())) {
            // 需要把原来被选中的清除选中标记
            extPurInqSouProjectDAO.lambdaUpdate()
                    .set(ExtPurInqSouProject::getHasFixPrice, Enable.N)
                    .eq(ExtPurInqSouProject::getProjectId, context.getExistFixPriceHead().getSouProjectId())
                    .update();
        }
        extPurInqSouProjectDAO.lambdaUpdate()
                .set(ExtPurInqSouProject::getHasFixPrice, Enable.Y)
                .eq(ExtPurInqSouProject::getProjectId, context.getPriceHeadEntity().getSouProjectId())
                .update();
    }

    /**
     * 删除定价单
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void removeFixPrice(long purFixPriceHeadId) {
        ExtPurFixPriceHead priceHead = extPurFixPriceHeadDAO.getById(purFixPriceHeadId);
        if (priceHead == null) { return; }
        AssertUtils.isTrue(ExtPurFixPriceStatusEnum.DRAFT.equals(priceHead.getFixPriceStatus()), "非拟定状态禁止删除");

        extPurFixPriceHeadDAO.removeById(purFixPriceHeadId);
        extPurFixPriceLineDAO.lambdaUpdate()
                .eq(ExtPurFixPriceLine::getPurFixPriceHeadId, purFixPriceHeadId)
                .remove();
        extPurFixPriceFileDAO.lambdaUpdate()
                .eq(ExtPurFixPriceFile::getPurFixPriceHeadId, purFixPriceHeadId)
                .remove();
        if (priceHead.getSouProjectId() != null) {
            extPurInqSouProjectDAO.lambdaUpdate()
                    .set(ExtPurInqSouProject::getHasFixPrice, Enable.N)
                    .eq(ExtPurInqSouProject::getProjectId, priceHead.getSouProjectId())
                    .update();
        }
    }

    /**
     * 定价单审批提交
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void callbackAfterApprovalSubmit(long purFixPriceHeadId) {
        ExtPurFixPriceHead fixPrice = extPurFixPriceHeadDAO.getById(purFixPriceHeadId);
        AssertUtils.notNull(fixPrice, "定价单[{0}]不存在", purFixPriceHeadId);
        switch (fixPrice.getFixPriceStatus()) {
            // 拟定
            case DRAFT:
                // 已驳回
            case REJECTED:
                // 已撤回
            case WITHDRAW:
//                AssertUtils.isTrue(Enable.Y.equals(fixPrice.getCanSubmit()), "尚未提交单据");
                break;
            // 审批中
            case SUBMITTED:
                // 幂等处理
                return;
            // 已废弃
            case ABANDONED:
                throw new IllegalArgumentException("单据已废弃");
                // 已审批
            case APPROVED:
                throw new IllegalArgumentException("单据已审批");
            default:
                throw new IllegalArgumentException("无法识别的单据状态");
        }

        extPurFixPriceHeadDAO.lambdaUpdate()
                .set(ExtPurFixPriceHead::getFixPriceStatus, ExtPurFixPriceStatusEnum.SUBMITTED)
                .set(ExtPurFixPriceHead::getApprovalSubmitTime, new Date())
                .eq(ExtPurFixPriceHead::getPurFixPriceHeadId, purFixPriceHeadId)
                .update();
        extPurFixPriceLineDAO.lambdaUpdate()
                .set(ExtPurFixPriceLine::getFixPriceStatus, ExtPurFixPriceStatusEnum.SUBMITTED)
                .eq(ExtPurFixPriceLine::getPurFixPriceHeadId, purFixPriceHeadId)
                .update();
    }

    /**
     * 定价单审批通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void callbackAfterApprovalPass(long purFixPriceHeadId) {
        ExtPurFixPriceHead fixPrice = extPurFixPriceHeadDAO.getById(purFixPriceHeadId);
        AssertUtils.notNull(fixPrice, "定价单[{0}]不存在", purFixPriceHeadId);
        switch (fixPrice.getFixPriceStatus()) {
            // 已审批
            case APPROVED:
                // 幂等处理
                return;
            // 审批中
            case SUBMITTED:
                break;
            // 拟定
            case DRAFT:
                throw new IllegalArgumentException("单据尚未提交");
                // 已驳回
            case REJECTED:
                throw new IllegalArgumentException("单据已驳回");
                // 已撤回
            case WITHDRAW:
                throw new IllegalArgumentException("单据已撤回");
                // 已废弃
            case ABANDONED:
                throw new IllegalArgumentException("单据已废弃");
            default:
                throw new IllegalArgumentException("无法识别的单据状态");
        }

        extPurFixPriceHeadDAO.lambdaUpdate()
                .set(ExtPurFixPriceHead::getFixPriceStatus, ExtPurFixPriceStatusEnum.APPROVED)
                .set(ExtPurFixPriceHead::getApprovalPassTime, new Date())
                .eq(ExtPurFixPriceHead::getPurFixPriceHeadId, purFixPriceHeadId)
                .update();
        extPurFixPriceLineDAO.lambdaUpdate()
                .set(ExtPurFixPriceLine::getFixPriceStatus, ExtPurFixPriceStatusEnum.APPROVED)
                .eq(ExtPurFixPriceLine::getPurFixPriceHeadId, purFixPriceHeadId)
                .update();
    }

    /**
     * 定价单审批未通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void callbackAfterApprovalUnPass(long purFixPriceHeadId, String fixPriceStatus) {
        ExtPurFixPriceHead fixPrice = extPurFixPriceHeadDAO.getById(purFixPriceHeadId);
        AssertUtils.notNull(fixPrice, "定价单[{0}]不存在", purFixPriceHeadId);
        switch (fixPrice.getFixPriceStatus()) {
            // 已驳回
            case REJECTED:
                if (ExtFixPriceStatusEnum.REJECTED.name().equals(fixPriceStatus)) {
                    // 幂等处理
                    return;
                }
                throw new IllegalArgumentException("单据已驳回");
                // 已撤回
            case WITHDRAW:
                if (ExtFixPriceStatusEnum.WITHDRAW.name().equals(fixPriceStatus)) {
                    // 幂等处理
                    return;
                }
                throw new IllegalArgumentException("单据已撤回");
                // 已废弃
            case ABANDONED:
                if (ExtFixPriceStatusEnum.ABANDONED.name().equals(fixPriceStatus)) {
                    // 幂等处理
                    return;
                }
                throw new IllegalArgumentException("单据已废弃");
                // 已审批
            case APPROVED:
                throw new IllegalArgumentException("单据已审批");
                // 审批中
            case SUBMITTED:
                break;
            // 拟定
            case DRAFT:
                throw new IllegalArgumentException("单据尚未提交");
            default:
                throw new IllegalArgumentException("无法识别的单据状态");
        }

        extPurFixPriceHeadDAO.lambdaUpdate()
                .set(ExtPurFixPriceHead::getFixPriceStatus, fixPriceStatus)
                .eq(ExtPurFixPriceHead::getPurFixPriceHeadId, purFixPriceHeadId)
                .update();
        extPurFixPriceLineDAO.lambdaUpdate()
                .set(ExtPurFixPriceLine::getFixPriceStatus, fixPriceStatus)
                .eq(ExtPurFixPriceLine::getPurFixPriceHeadId, purFixPriceHeadId)
                .update();

        if (ExtPurFixPriceStatusEnum.ABANDONED.name().equals(fixPriceStatus)) {
            extPurInqSouProjectDAO.lambdaUpdate()
                    .set(ExtPurInqSouProject::getHasFixPrice, Enable.N)
                    .eq(ExtPurInqSouProject::getProjectId, fixPrice.getSouProjectId())
                    .update();
        }
    }

}
