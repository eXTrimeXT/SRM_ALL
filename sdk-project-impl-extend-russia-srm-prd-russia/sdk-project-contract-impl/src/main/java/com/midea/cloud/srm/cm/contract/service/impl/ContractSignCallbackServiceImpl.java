package com.midea.cloud.srm.cm.contract.service.impl;


import com.midea.cloud.common.enums.contract.ContractStatus;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.sign.service.ISignCallbackService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 100014323
 */
@Service
@Slf4j
public class ContractSignCallbackServiceImpl implements ISignCallbackService {

    @Autowired
    private QlService qlService;

    @Override
    public void complete(Long businessId, String param, List<Fileupload> fileuploads) throws Exception {
        log.info("电子签章回调:businessId：" + businessId);
        log.info("电子签章回调:fileuploads：" + JsonUtil.arrayToJsonStr(fileuploads));

        List<Record> annexRecords = qlService.queryByWrapper(QlWrappers.query("Annex")
                .eq("contractHeadId", businessId), Record.class);

        Map<String, String> fileSourceNameMap = annexRecords.stream().collect(Collectors.toMap(item -> removeSuffix(item.getString("fileSourceName"))
                , item -> item.getString("fileType"), (k1, k2) -> k2));

        List<Record> stampAnnexRecords = new ArrayList<>();
        for (Fileupload fileupload : fileuploads) {
            String fileName = removeSuffix(fileupload.getFileSourceName());
            if (!fileSourceNameMap.containsKey(fileName)) {
                continue;
            }
            String fileType = fileSourceNameMap.get(fileName);
            Record record = new Record();
            record.put("contractHeadId", businessId);
            record.put("fileSourceName", fileupload.getFileSourceName());
            record.put("fileuploadId", fileupload.getFileuploadId());
            record.put("ceeaUploadTime", new Date());
            record.put("fileType", fileType);
            stampAnnexRecords.add(record);
        }
        // 保存归档附件
        qlService.save("StampAnnex", stampAnnexRecords);
        // 修改合同状态
        qlService.updateByWrapper(QlWrappers.update("ContractHead")
                .eq("contractHeadId", businessId)
                .set("contractStatus", ContractStatus.UN_ARCHIVED.name())
        );

        log.info("-----------------------电子签章进入complete------------------------");
    }

    public String removeSuffix(String fileName) {
        if (StringUtils.isNotBlank(fileName)) {
            return fileName.substring(0, fileName.lastIndexOf("."));
        }
        return fileName;
    }
}
