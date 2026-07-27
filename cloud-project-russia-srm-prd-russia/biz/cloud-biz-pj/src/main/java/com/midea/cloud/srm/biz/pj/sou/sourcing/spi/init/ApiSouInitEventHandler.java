package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.init;

import com.alibaba.fastjson.TypeReference;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.service.SouInitQueryService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.init.editproject.SouProjectEditPO;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.init.editrequrie.SouRequireEditPO;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.init.editvendor.SouVendorEditPO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.*;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouInitDetailVO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouFileTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouProcessNodeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouSourceFromTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 寻源openAPI - 立项业务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/30
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ApiSouInitEventHandler implements ISouSpiBean {

    @Autowired
    private SouInitQueryService souInitQueryService;
    @Autowired
    private SouProjectDAOImpl souProjectDao;

    @ApiOperation("项目信息保存前的额外处理")
    public void doHandlerBeforeEditProject(ApiSouProjectInfoDTO param, boolean isCopy, String souType) {
    }

    @ApiOperation("项目信息保存后的额外处理")
    public void doHandlerAfterEditProject(ApiSouProjectInfoDTO param, boolean isCopy, String souType, SouProjectEditPO po) {
    }

    @ApiOperation("项目需求保存前的额外处理")
    public void doHandlerBeforeEditRequires(ApiSouRequireInfoDTO param, boolean isCopy, @Nullable Long userId, String souType) {
    }

    @ApiOperation("项目需求保存后的额外处理")
    public void doHandlerAfterEditRequires(ApiSouRequireInfoDTO param, boolean isCopy, @Nullable Long userId, String souType, SouRequireEditPO po) {
    }

    @ApiOperation("邀请供应商保存前的额外处理")
    public void doHandlerBeforeEditVendors(ApiSouVendorInfoDTO param, boolean isCopy, String souType) {
    }

    @ApiOperation("邀请供应商保存后的额外处理")
    public void doHandlerAfterEditVendors(ApiSouVendorInfoDTO param, boolean isCopy, String souType, SouVendorEditPO po) {
    }

    @ApiOperation("评分规则保存前的额外处理")
    public void doHandlerBeforeEditScoreRule(ApiSouInitScoreInfoDTO param, boolean isCopy, String souType) {
    }

    @ApiOperation("评分规则保存后的额外处理")
    public void doHandlerAfterEditScoreRule(ApiSouInitScoreInfoDTO param, boolean isCopy, String souType) {
    }

    @ApiOperation("删除寻源单前的额外处理")
    public void doHandlerBeforeRemoveSou(long projectId, String souType) {
    }

    @ApiOperation("删除寻源单后的额外处理")
    public void doHandlerAfterRemoveSou(long projectId, String souType, ApiSouInitDetailVO initInfo) {
    }

    @ApiOperation("作废寻源单前的额外处理")
    public void doHandlerBeforeCancelSou(ApiSouCancelDTO param, String souType) {
    }

    @ApiOperation("作废寻源单后的额外处理")
    public void doHandlerAfterCancelSou(ApiSouCancelDTO param, String souType) {
    }

    @ApiOperation("复制寻源单--构造立项基本信息")
    public ApiSouProjectInfoDTO doHandlerForCopyProjectInfo(long projectId, String souType) {
        ApiSouInitDetailVO initInfo = souInitQueryService.getSouInitInfo(projectId, souType);
        ApiSouProjectInfoDTO projectInfoDTO = new ApiSouProjectInfoDTO(); {
            // 寻源单
            projectInfoDTO.setProject(SouObjectXUtil.convertTargetObj(initInfo.getProjectInfo(), ApiSouProjectEditDTO.class)); {
                String souName = projectInfoDTO.getProject().getSouName() + " - ";
                int index = 1;
                while (true) {
                    long count = souProjectDao.lambdaQuery()
                            .eq(SouProject::getSouName, souName + index)
                            .eq(SouProject::getSouType, souType)
                            .count();
                    if (count <= 0) {
                        projectInfoDTO.getProject().setSouName(souName + index);
                        break;
                    }
                    index++;
                }
                projectInfoDTO.getProject().setProjectId(null);
                projectInfoDTO.getProject().setSouNo(null);
                projectInfoDTO.getProject().setPriceStartTime(null);
                projectInfoDTO.getProject().setPriceEndTime(null);
                projectInfoDTO.getProject().setSignUpStartTime(null);
                projectInfoDTO.getProject().setSignUpEndTime(null);
                projectInfoDTO.getProject().setOrderStartTime(null);
                projectInfoDTO.getProject().setOrderEndTime(null);
                projectInfoDTO.getProject().setSourceFromType(SouSourceFromTypeEnum.HAND_MAKE.name());
            }
            // 工作小组
            projectInfoDTO.setGroupList(SouObjectXUtil.convertTargetObj(initInfo.getProjectInfo().getGroupList(), new TypeReference<List<ApiSouGroupEditDTO>>() {})); {
                projectInfoDTO.getGroupList().forEach(group -> group.setGroupId(null));
            }
            // 可用币种
            projectInfoDTO.setCurrencyList(SouObjectXUtil.convertTargetObj(initInfo.getProjectInfo().getCurrencyList(), new TypeReference<List<ApiSouCurrencyEditDTO>>() {})); {
                projectInfoDTO.getCurrencyList().forEach(currency -> currency.setSouCurrencyId(null));
            }
            // 外部查看附件
            projectInfoDTO.setOuterFileList(SouObjectXUtil.convertTargetObj(initInfo.getProjectInfo().getSouFileList().stream()
                    .filter(e -> SouFileTypeEnum.OUTER.equals(e.getFileType())).collect(Collectors.toSet()), new TypeReference<List<ApiSouFileEditDTO>>() {})); {
                projectInfoDTO.getOuterFileList().forEach(file -> file.setSouFileId(null));
            }
            // 内部查看附件
            projectInfoDTO.setInnerFileList(SouObjectXUtil.convertTargetObj(initInfo.getProjectInfo().getSouFileList().stream()
                    .filter(e -> SouFileTypeEnum.INNER.equals(e.getFileType())).collect(Collectors.toSet()), new TypeReference<List<ApiSouFileEditDTO>>() {})); {
                projectInfoDTO.getInnerFileList().forEach(file -> file.setSouFileId(null));
            }
            // 供方必须上传附件
            projectInfoDTO.setFileConfigList(SouObjectXUtil.convertTargetObj(initInfo.getProjectInfo().getFileConfigList(), new TypeReference<List<ApiSouFileConfigEditDTO>>() {})); {
                projectInfoDTO.getFileConfigList().forEach(file -> file.setSouFileConfigId(null));
            }
            projectInfoDTO.setTempSave(true);
        }
        return projectInfoDTO;
    }

    @ApiOperation("复制寻源单--构造立项物料需求")
    public ApiSouRequireInfoDTO doHandlerForCopyRequireInfo(long newProjectId, long oldProjectId, String souType) {
        ApiSouInitDetailVO initInfo = souInitQueryService.getSouInitInfo(oldProjectId, souType);
        ApiSouRequireInfoDTO requireInfoDTO = new ApiSouRequireInfoDTO(); {
            requireInfoDTO.setProjectId(newProjectId);
            requireInfoDTO.setItemList(SouObjectXUtil.convertTargetObj(initInfo.getRequireInfo(), new TypeReference<List<ApiSouItemDTO>>() {})); {
                requireInfoDTO.getItemList().forEach(item -> {
                    item.setSouItemId(null);
                    if (item.getLadderList() != null) {
                        item.getLadderList().forEach(ladder -> ladder.setSouItemLadderId(null));
                    }
                });
            }
            requireInfoDTO.setTempSave(true);
        }
        return requireInfoDTO;
    }

    @ApiOperation("复制寻源单--构造立项邀请供应商")
    public ApiSouVendorInfoDTO doHandlerForCopyVendorInfo(long oldProjectId, String souType, long newProjectId,
                                                          Map<Long/* oldSouItemId */, Long/* newSouItemId */> souItemIdMap) {
        ApiSouInitDetailVO initInfo = souInitQueryService.getSouInitInfo(oldProjectId, souType);
        ApiSouVendorInfoDTO vendorInfoDTO = new ApiSouVendorInfoDTO(); {
            vendorInfoDTO.setProjectId(newProjectId);
            vendorInfoDTO.setVendorList(SouObjectXUtil.convertTargetObj(initInfo.getVendorInfo(), new TypeReference<List<ApiSouVendorDTO>>() {})); {
                vendorInfoDTO.getVendorList().forEach(vendor -> {
                    SouObjectXUtil.putXbyLambda(vendor, SouVendor::getSouVendorId, null);
                    vendor.getAuthList().forEach(auth -> {
                        auth.setVendorAuthId(null);
                        auth.setSouItemId(souItemIdMap.get(auth.getSouItemId()));
                    });
                });
            }
            vendorInfoDTO.setTempSave(true);
        }
        return vendorInfoDTO;
    }

    @ApiOperation("复制寻源单--构造立项评分规则")
    public ApiSouInitScoreInfoDTO doHandlerForCopyScoreInfo(long oldProjectId, String souType, long newProjectId) {
        SouProject newProject = souProjectDao.getById(newProjectId);
        ApiSouInitScoreInfoDTO scoreInfoDTO = new ApiSouInitScoreInfoDTO(); {
            scoreInfoDTO.setProjectId(newProjectId);
            scoreInfoDTO.setScoreTemplateId(newProject.getScoreTemplateId());
            scoreInfoDTO.setScoreRuleType(newProject.getScoreRuleType());
            scoreInfoDTO.setTempSave(true);
        }
        return scoreInfoDTO;
    }

    @ApiOperation("复制寻源单后的额外操作")
    public void doHandlerAfterCopySou(long oldProjectId, String souType, long newProjectId) {
    }

    @ApiOperation("自动提交立项审批通过前的额外处理")
    public void doHandlerBeforeAutoSubmitPass(long projectId, SouProcessNodeEnum processNode, String souType) {
    }

    @ApiOperation("自动提交立项审批通过后的额外处理")
    public void doHandlerAfterAutoSubmitPass(long projectId, SouProcessNodeEnum processNode, String souType) {
    }

    @ApiOperation("立项审批提交前的额外处理")
    public void doHandlerBeforeApprovalSubmit(long projectId, String souType) {
    }

    @ApiOperation("立项审批提交后的额外处理")
    public void doHandlerAfterApprovalSubmit(long projectId, String souType) {
    }

    @ApiOperation("立项审批通过前的额外处理")
    public void doHandlerBeforeApprovalPass(long projectId, String souType) {
    }

    @ApiOperation("立项审批通过后的额外处理")
    public void doHandlerAfterApprovalPass(long projectId, String souType) {
    }

    @ApiOperation("立项审批未通过前的额外处理")
    public void doHandlerBeforeApprovalUnPass(ApiSouCreateApprovalUnPassDTO param, String souType) {
    }

    @ApiOperation("立项审批未通过后的额外处理")
    public void doHandlerAfterApprovalUnPass(ApiSouCreateApprovalUnPassDTO param, String souType) {
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
