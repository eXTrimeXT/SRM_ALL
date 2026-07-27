package com.midea.cloud.srm.sou.meiql.filecheck.service;

import com.midea.cloud.srm.model.file.oss.DownLoadResultDto;
import com.midea.cloud.srm.model.pj.aihelper.FileCheckDto;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 备注
 * @author bs
 */
public interface FileCheckService {
    /**
     * 备注
     * @param fileCheckDto 参数
     * @return 参数
     * @throws Exception
     */
    List<DownLoadResultDto> findOrderFile(FileCheckDto fileCheckDto) throws Exception;
    /**
     * 备注
     * @param fileCheckDto 参数
     * @return 参数
     * @throws Exception
     */
    List<DownLoadResultDto> findAnswerFile(FileCheckDto fileCheckDto) throws Exception;

    /**
     * 备注
     *
     * @param fileCheckDto 参数
     * @param response
     * @return 参数
     * @throws Exception
     */
    void pageFile(FileCheckDto fileCheckDto, HttpServletResponse response) throws Exception;

}
