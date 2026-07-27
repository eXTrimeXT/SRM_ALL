package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.signup;

import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.biz.pj.sou.comp.order.service.CompSouOrderEventWebService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.*;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.signup.service.SouSignUpEventService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order.ApiSouOrderQueryHandler;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.order.ApiCompSouOrderDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouVendorAuthEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderItemDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.signup.ApiSouSignUpChangeEndTimeDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.signup.ApiSouSignUpConfirmDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.signup.ApiSouSignUpVendorDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouPublishScopeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 寻源openAPI - 报名业务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/02
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ApiSouSignUpEventHandler implements ISouSpiBean {

    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private SouRoundDAOImpl souRoundDao;
    @Autowired
    private SouOrderDAOImpl souOrderDao;
    @Autowired
    private SouSignUpEventService souSignUpEventService;
    @Autowired
    private SouItemDAOImpl souItemDao;
    @Autowired
    private SouVendorAuthDAOImpl souVendorAuthDao;
    @Autowired
    private SouVendorDAOImpl souVendorDao;
    @Autowired
    private CompSouOrderEventWebService compSouOrderEventWebService;

    @ApiOperation("报名时更新应报价供应商数量")
    public void doHandlerForInviteCountWhileSignUp(long projectId) {
        SouProject project = souProjectDao.getById(projectId);

        int inviteCount;
        {
            Set<Long> currentRoundAuthVendorIds = SouActiveBeanUtils.getActiveBean(project.getSouType(), ApiSouOrderQueryHandler.class)
                    .getAuthedVendors(project.getProjectId(), project.getCurrentRound());
            List<SouOrder> currentRoundCanceledOrders = souOrderDao.lambdaQuery()
                    .eq(SouOrder::getProjectId, project.getProjectId())
                    .eq(SouOrder::getRound, project.getCurrentRound())
                    .eq(SouOrder::getOrderStatus, SouOrderStatusEnum.CANCEL)
                    .list();
            if (currentRoundAuthVendorIds.isEmpty()) {
                inviteCount = 0;
            } else {
                int cancelCount = (int) currentRoundCanceledOrders.stream().map(SouOrder::getOrderStatus).filter(SouOrderStatusEnum.CANCEL::equals).count();
                inviteCount = currentRoundAuthVendorIds.size() - cancelCount;
            }
        }

        souProjectDao.lambdaUpdate()
                .set(SouProject::getInviteCount, inviteCount)
                .eq(SouProject::getProjectId, projectId)
                .update();
        souRoundDao.lambdaUpdate()
                .set(SouRound::getInviteCount, inviteCount)
                .eq(SouRound::getProjectId, projectId)
                .eq(SouRound::getRound, project.getCurrentRound())
                .update();
    }

    @ApiOperation("供应商报名后的额外处理")
    public void doHandlerAfterVendorSignUp(ApiSouSignUpVendorDTO param, String souType) {
        // 1: 如果是邀请场景，则自动确认报名
        if (!param.isTempSave()) {
            SouProject project = souProjectDao.getById(param.getProjectId());
            if (SouPublishScopeEnum.INVITE_TENDER.equals(project.getPublishScope())) {
                ApiSouSignUpConfirmDTO confirmDTO = new ApiSouSignUpConfirmDTO();
                {
                    confirmDTO.setProjectId(param.getProjectId());
                    confirmDTO.setVendorId(param.getVendorId());
                    confirmDTO.setToPass(true);
                }
                souSignUpEventService.confirmSignUp(confirmDTO, souType);
            }
        }
    }

    @ApiOperation("采购商确认/驳回供应商报名前的额外处理")
    public void doHandlerBeforeConfirmSignUp(ApiSouSignUpConfirmDTO param, String souType) {
    }

    @ApiOperation("采购商确认报名后添加报价权限信息")
    public void doHandlerForVendorAuthAfterConfirmSignUp(ApiSouSignUpConfirmDTO param, String souType) {
        if (!param.isToPass()) {
            return;
        }
        // 公开的情况下，才需要添加报价权限信息
        SouProject project = souProjectDao.getById(param.getProjectId());
        /*if (!SouPublishScopeEnum.OPEN_TENDER.equals(project.getPublishScope())) {
            return;
        }*/
        //报价信息初始化
        List<SouItem> itemList = souItemDao.lambdaQuery().eq(SouItem::getProjectId, param.getProjectId()).list();
        ApiCompSouOrderDTO compSouOrderDTO = new ApiCompSouOrderDTO();
        compSouOrderDTO.setVendorId(param.getVendorId());
        compSouOrderDTO.setProjectId(project.getProjectId());
        List<ApiSouOrderItemDTO> apiSouOrderItemDtos = new ArrayList<>();
        for (SouItem souItem : itemList) {
            ApiSouOrderItemDTO apiSouOrderItemDTO = new ApiSouOrderItemDTO();
            apiSouOrderItemDTO.setSouItemId(souItem.getSouItemId());
            apiSouOrderItemDtos.add(apiSouOrderItemDTO);
        }
        compSouOrderDTO.setOrderItemList(apiSouOrderItemDtos);
        compSouOrderEventWebService.initOrder(compSouOrderDTO);
        //报价权限
        List<SouVendorAuth> authList = new ArrayList<>(itemList.size());
        Map<Long/* souItemId */, ApiSouVendorAuthEditDTO> tempAuthMap;
        {
            if (CollectionUtils.isEmpty(param.getAuthList())) {
                tempAuthMap = Collections.emptyMap();
            } else {
                tempAuthMap = param.getAuthList().stream().collect(Collectors.toMap(ApiSouVendorAuthEditDTO::getSouItemId, Function.identity()));
            }
        }
        SouVendor vendor = souVendorDao.lambdaQuery()
                .eq(SouVendor::getProjectId, param.getProjectId())
                .eq(SouVendor::getVendorId, param.getVendorId())
                .one();
        for (SouItem souItem : itemList) {
            SouVendorAuth auth = new SouVendorAuth();
            SouObjectXUtil.mergeProperties(souItem, auth);
            authList.add(auth);

            auth.setVendorAuthId(IdGenrator.generate());
            auth.setVendorId(param.getVendorId());
            auth.setSouVendorId(vendor.getSouVendorId());
            {
                ApiSouVendorAuthEditDTO tempAuth = tempAuthMap.get(souItem.getSouItemId());
                if (tempAuth != null) {
                    auth.setForbidPrice(tempAuth.getForbidPrice() != null ? tempAuth.getForbidPrice() : Enable.N);
                } else {
                    auth.setForbidPrice(Enable.N);
                }
            }
        }
        // 删除旧的权限信息，再新增
        souVendorAuthDao.lambdaUpdate()
                .eq(SouVendorAuth::getProjectId, param.getProjectId())
                .eq(SouVendorAuth::getVendorId, param.getVendorId())
                .remove();
        souVendorAuthDao.saveBatch(authList);
    }

    @ApiOperation("采购商确认/驳回供应商报名后的额外处理")
    public void doHandlerAfterConfirmSignUp(ApiSouSignUpConfirmDTO param, String souType) {
    }

    @ApiOperation("修改报名截止时间前的额外处理")
    public void doHandlerBeforeChangeSignUpEndTime(ApiSouSignUpChangeEndTimeDTO param, String souType) {
    }

    @ApiOperation("修改报名截止时间后的额外处理")
    public void doHandlerAfterChangeSignUpEndTime(ApiSouSignUpChangeEndTimeDTO param, String souType) {
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
