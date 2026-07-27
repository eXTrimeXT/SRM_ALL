package com.midea.cloud.srm.sou.extfiles.service.mapper;

import com.midea.cloud.srm.model.file.upload.entity.Fileupload;

import java.util.List;
import java.util.Map;

/**
 * @Author: panmq
 * @Date: 2024/03/20/ $
 * @Description:
 */
public interface ExtFileMapper {

    /**
     * listFile
     * @param param
     * @return
     */
    List<Fileupload> listFile(Map<String, Object> param);
}
