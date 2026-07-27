package com.midea.cloud.srm.biz.pj.sou.sourcing.signup.service.impl;

import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouFileDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouVendorDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.signup.dao.SouSignUpFileDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.signup.service.SouSignUpEventService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.signup.ApiSouSignUpEventHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.signup.ApiSouSignUpJudgeHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.signup.vendorsignup.ApiSouSignUpVendorEditHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.signup.vendorsignup.SouVendorSignUpPO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.signup.ApiSouSignUpChangeEndTimeDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.signup.ApiSouSignUpConfirmDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.signup.ApiSouSignUpVendorDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouSignUpFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouFileTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouProjectStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouSignUpStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 项目式询价 - 报名事件服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/23
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class SouSignUpEventServiceImpl implements SouSignUpEventService {

    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private SouVendorDAOImpl souVendorDao;
    @Autowired
    private SouSignUpFileDAOImpl souSignUpFileDao;
    @Autowired
    private SouFileDAOImpl souFileDao;

    /**
     * 供应商报名
     *
     * @param param   报名信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void vendorSignUp(ApiSouSignUpVendorDTO param, String souType) {
        // 1: 入参格式化
        param.formatParams();
        // 2: 校验操作条件/权限 AppUserUtil.getLoginAppUser().getCompanyId() --> vendorId
        SouActiveBeanUtils.getActiveBean(souType, ApiSouSignUpJudgeHandler.class)
                .judgeVendorSignUpAuth(param.getProjectId(), param.getVendorId(), souType);
        // 3: 入参校验+数据转换
        SouVendorSignUpPO po = SouActiveBeanUtils.getActiveBean(souType, ApiSouSignUpVendorEditHandler.class).formatValidateAndConvert(param, souType);
        // 4: 保存数据
        souSignUpFileDao.lambdaUpdate()
                .eq(SouSignUpFile::getProjectId, param.getProjectId())
                .eq(SouSignUpFile::getVendorId, param.getVendorId())
                .remove();
        if (CollectionUtils.isNotEmpty(po.getSignUpFileList())) {
            souSignUpFileDao.saveBatch(po.getSignUpFileList());
        }
        List<SouFile> souFileList = po.getBondPayList();
        for(int i = 0 ; i < souFileList.size(); i ++){
            souFileList.get(i).setSouVendorId(AppUserUtil.getLoginAppUser().getCompanyId());
        }
        // 处理附件保存
        souFileDao.lambdaUpdate().eq(SouFile::getProjectId, param.getProjectId()).eq(SouFile::getFileType, SouFileTypeEnum.BOND).remove();
        if (!org.springframework.util.CollectionUtils.isEmpty(souFileList)) {
            souFileDao.saveBatch(souFileList);
        }
        souVendorDao.saveOrUpdate(po.getVendor());
        souProjectDao.lambdaUpdate().set(SouProject::getProjectStatus
                        , SouProjectStatusEnum.ACCEPT_SIGN_UP)
                .set(SouProject::getSignUpStartTime, new Date())
                .eq(SouProject::getProjectId, param.getProjectId())
                //.eq(SouProject::getProjectStatus, SouProjectStatusEnum.DRAFT)
                .update();
        // 5: 行业包额外处理(后置)
        //SouActiveBeanUtils.getActiveBean(souType, ApiSouSignUpEventHandler.class).doHandlerAfterVendorSignUp(param, souType);
        // 6: 刷新应报价供应商数量
        //SouActiveBeanUtils.getActiveBean(souType, ApiSouSignUpEventHandler.class).doHandlerForInviteCountWhileSignUp(param.getProjectId());
    }

    /**
     * 确认/驳回报名
     *
     * @param param   报名确认/驳回信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void confirmSignUp(ApiSouSignUpConfirmDTO param, String souType) {
        // 1: 入参格式化
        param.formatParams();
        // 2: 校验操作条件/权限
        SouActiveBeanUtils.getActiveBean(souType, ApiSouSignUpJudgeHandler.class)
                .judgeConformVendorSignUpAuth(param.getProjectId(), param.getVendorId(), souType);
        // 3: 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouSignUpEventHandler.class).doHandlerBeforeConfirmSignUp(param, souType);
        // 4: 确认/驳回报名
        souVendorDao.lambdaUpdate()
                .set(SouVendor::getSignUpStatus, param.isToPass() ? SouSignUpStatusEnum.SIGN_UP_DONE : SouSignUpStatusEnum.REJECTED)
                .set(!param.isToPass(), SouVendor::getSignUpRejectReason, param.getRejectReason())
                .eq(SouVendor::getProjectId, param.getProjectId())
                .eq(SouVendor::getVendorId, param.getVendorId())
                .update();
        // 5: 报名通过后，自动添加报价权限信息
        SouActiveBeanUtils.getActiveBean(souType, ApiSouSignUpEventHandler.class).doHandlerForVendorAuthAfterConfirmSignUp(param, souType);
        // 6: 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouSignUpEventHandler.class).doHandlerAfterConfirmSignUp(param, souType);
        // 7: 刷新应报价供应商数量
        SouActiveBeanUtils.getActiveBean(souType, ApiSouSignUpEventHandler.class).doHandlerForInviteCountWhileSignUp(param.getProjectId());
    }

    /**
     * 立即截止报名/延长报名时间
     *
     * @param param   修改信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void changeSignUpEndTime(ApiSouSignUpChangeEndTimeDTO param, String souType) {
        // 1: 入参格式化
        param.formatParams();
        // 2: 校验操作条件/权限
        SouActiveBeanUtils.getActiveBean(souType, ApiSouSignUpJudgeHandler.class)
                .judgeChangeSignUpEndTimeAuth(param.getProjectId(), souType);
        // 3: 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouSignUpEventHandler.class).doHandlerBeforeChangeSignUpEndTime(param, souType);
        // 4: 更新报名相关信息
        souProjectDao.lambdaUpdate()
                .set(SouProject::getProjectStatus, param.isStopNow() ? SouProjectStatusEnum.SIGN_UP_END : SouProjectStatusEnum.ACCEPT_SIGN_UP)
                .set(SouProject::getSignUpEndTime, param.isStopNow() ? new Date() : param.getSignUpEndTime())
                .eq(SouProject::getProjectId, param.getProjectId())
                .in(SouProject::getProjectStatus, SouProjectStatusEnum.ACCEPT_SIGN_UP, SouProjectStatusEnum.SIGN_UP_END)
                .update();
        // 5: 更新节点状态
        /*souProcessNodeDAO.lambdaUpdate()
                .set(SouProcessNode::getNodeStatus, param.isStopNow() ? Enable.Y : Enable.N)
                .eq(SouProcessNode::getProjectId, param.getProjectId())
                .eq(SouProcessNode::getProcessNode, SouProcessNodeEnum.signUpManagement)
                .update();*/
        // 6: 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouSignUpEventHandler.class).doHandlerAfterChangeSignUpEndTime(param, souType);
    }

}
