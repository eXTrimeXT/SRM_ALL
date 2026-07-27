package com.midea.cloud.srm.base.pjfile.service;

import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.srm.model.base.pjfile.enitty.FileTransformRecord;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 备注
 * @author luxc18
 */
public interface FileTransformRecordService extends BaseService<FileTransformRecord> {

    /**
     * 附件转换
     * @throws IOException 报错
     */
    void fileTransform() throws IOException;

}
