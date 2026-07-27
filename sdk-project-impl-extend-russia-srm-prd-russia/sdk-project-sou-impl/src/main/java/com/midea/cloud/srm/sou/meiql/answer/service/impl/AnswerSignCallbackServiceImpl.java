package com.midea.cloud.srm.sou.meiql.answer.service.impl;

import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.sign.service.ISignCallbackService;
import com.midea.cloud.srm.model.sou.answer.dto.ReplayFileDTO;
import com.midea.cloud.srm.model.sou.enums.BidSignStatusEnum;
import com.midea.cloud.srm.model.sou.enums.TypeEnum;
import com.spire.ms.System.Collections.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class AnswerSignCallbackServiceImpl implements ISignCallbackService {

    @Autowired
    private QlService qlService;

    @Override
    public void complete(Long businessId, String param, List<Fileupload> fileuploads) throws Exception {
        log.info("澄清签章附件签署回调:businessId："+businessId);
        log.info("澄清签章附件签署回调:param："+param);
        log.info("澄清签章附件签署回调:fileuploads："+ JsonUtil.arrayToJsonStr(fileuploads));
        List<ReplayFileDTO> fileDTOList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.ReplayFile.getCode())
                .eq(ReplayFileDTO::getReplayId,businessId).eq(ReplayFileDTO::getSignStatus,BidSignStatusEnum.NOT_SIGN.getCode()).eq(ReplayFileDTO::getIsDelete, YesOrNo.NO.getValue()),ReplayFileDTO.class);
        Map<String,ReplayFileDTO> fileMap = fileDTOList.stream().collect(Collectors.toMap( r -> removeSuffix(r.getFileName()), Function.identity(),(key1, key2)->key1));
        List<ReplayFileDTO> updateList = new ArrayList();
        fileuploads.stream().filter(fileupload -> !SrmConstant.SIGN_EXCLUDE_LIST.contains(fileupload.getFileSourceName())).forEach(f -> {
            if (fileMap.containsKey(removeSuffix(f.getFileSourceName()))) {
                ReplayFileDTO dto = fileMap.get(removeSuffix(f.getFileSourceName()));
                if (null != dto) {
                    dto.setSignTime(new Date());
                    dto.setFileName(f.getFileSourceName());
                    dto.setFileId(f.getFileuploadId());
                    dto.setSignStatus(BidSignStatusEnum.SIGN.getCode());
                    updateList.add(dto);
                }
            }
        });
        if (CollectionUtils.isNotEmpty(updateList)) {
            qlService.update(TypeEnum.ReplayFile.getCode(),updateList);
        }
    }

    public String removeSuffix(String fileName) {
        if (StringUtils.isNotBlank(fileName)) {
            return fileName.substring(0,fileName.lastIndexOf("."));
        }
        return fileName;
    }

}
