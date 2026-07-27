package com.midea.cloud.srm.sou.meiql.answer.service.impl;

import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.ql.QlUpdateWrapper;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.PjSouClient;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.sou.answer.dto.AnswerVendorDTO;
import com.midea.cloud.srm.model.sou.answer.dto.ReplayDTO;
import com.midea.cloud.srm.model.sou.answer.dto.ReplayFileDTO;
import com.midea.cloud.srm.model.sou.answer.dto.SignReplayDTO;
import com.midea.cloud.srm.model.sou.answer.enums.AnswerConfirmStatusEnum;
import com.midea.cloud.srm.model.sou.enums.BidSignStatusEnum;
import com.midea.cloud.srm.model.sou.enums.TypeEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiExtSignDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiExtSignatoryDto;
import com.midea.cloud.srm.sou.meiql.answer.service.AnswerService;
import com.midea.cloud.srm.sou.meiql.answer.service.AnswerSignService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Service
public class AnswerSignServiceImpl implements AnswerSignService {
    @Autowired
    private PjSouClient pjSouClient;

    @Autowired
    private QlService qlService;

    @Autowired
    private AnswerService answerService;

    /**
     * 契约锁签署
     * @param businessId
     * @param orderType
     * @param title
     * @param fileIds
     * @param signatoryDtos
     * @return
     */
    public String sign(Long businessId, String orderType, String title, List<Long> fileIds, List<ApiExtSignatoryDto> signatoryDtos) {
        ApiExtSignDto signDto = new ApiExtSignDto();
        signDto.setFileIdList(fileIds);
        signDto.setTitle(title);
        signDto.setOrderType(orderType);
        signDto.setOrderId(businessId);
        signDto.setSignatoryList(signatoryDtos);
        return pjSouClient.contractSigningByUrl(signDto.toJsonObject());
    }

    /**
     * 供应商签署
     * @param businessId
     * @param orderType
     * @param title
     * @param fileIds
     * @return
     */
    public String signByCurUser(Long businessId,String orderType,String title,List<Long> fileIds) {
        ApiExtSignatoryDto signatoryDto = new ApiExtSignatoryDto();
        LoginAppUser user = AppUserUtil.getLoginAppUser();
        AssertUtils.notNull(user,"用户信息失效，请重新登录");
        signatoryDto.setTenantName(user.getCompanyName());
        return sign(businessId,orderType,title,fileIds, Arrays.asList(signatoryDto));
    }
    @Override
    public SignReplayDTO sign(ReplayDTO replayDTO) {
        SignReplayDTO result = new SignReplayDTO();
        answerService.checkConfirm(replayDTO.getAnswerVendorId());
        List<Long> fileIds = new ArrayList<>();
        AssertUtils.notEmpty(replayDTO.getSceneFiles(),"签署附件不存在");
        List<String> fileNames = new ArrayList<>();
        for (ReplayFileDTO fileDTO : replayDTO.getSceneFiles()) {
            if ("A".equals(fileDTO.getSignStatus())) {
                fileIds.add(fileDTO.getFileId());
                fileDTO.setSignStatus(BidSignStatusEnum.NOT_SIGN.getCode());
                fileNames.add(fileDTO.getFileName());
            }
        }
        AssertUtils.notEmpty(fileIds,"签署附件不存在");
        List<Serializable> ids = qlService.save(TypeEnum.Replay.getCode(),Arrays.asList(replayDTO));
        AssertUtils.notEmpty(ids,"保存信息异常");
        Long replayId = (Long) ids.get(0);
        String signUrl =  signByCurUser(replayId,"ANSWER_REPLAY",fileNames.stream().collect(Collectors.joining(",")),fileIds);
        List<ReplayFileDTO> replayFileDtos = qlService.queryByWrapper(QlWrappers.query(TypeEnum.ReplayFile.getCode()).eq(ReplayFileDTO::getReplayId,replayId)
                .in(ReplayFileDTO::getFileId,fileIds),ReplayFileDTO.class);
        result.setSignUrl(signUrl);
        replayDTO.setReplayId(replayId);
        replayDTO.setSceneFiles(replayFileDtos);
        result.setReplayDTO(replayDTO);
        return result;
    }

}
