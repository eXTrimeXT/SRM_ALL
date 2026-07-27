package com.midea.cloud.srm.sou.sourcing.init.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouOrderFileQueryDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtTechFileDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouOrderFileDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrderFile;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouPlan;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 备注
 * @author huangbf3
 */
public interface IExtSouOrderFileService extends IService<ExtSouOrderFile> {

    /**
     * 查询投标附件
     * @param query
     * @return
     */
    List<ExtSouOrderFile> listOrderFile(ApiExtSouOrderFileQueryDto query);

    /**
     * 技术评分查询招标单技术文件
     * @param projectId
     * @return
     */
    List<ExtSouOrderFile> getScoreTechOrderFile(Long projectId);

    /**
     * 技术评分查询招标单技术文件
     * @param projectId
     * @param orderId
     * @return
     */
    List<ExtSouOrderFile> getScoreTechOrderFile(Long projectId, Long orderId);

    /**
     * 技术评分查询招标单技术文件
     * @param projectId
     * @param orderId
     * @return
     */
    List<ExtSouOrderFile> getBusOrderFile(Long projectId, Long orderId);

    /**
     * 查看技术方案
     * @param projectId
     * @return
     */
    ApiExtTechFileDto getTechPlan(Long projectId);

    /**
     * 下载技术方案
     * @param projectId 参数
     * @param response 参数
     * @throws Exception 报错
     */
    void downloadTechPlan(Long projectId, HttpServletResponse  response) throws Exception;

    /**
     * 下载技术方案
     * @param projectId
     * @return
     * @throws Exception
     */
    Map<String, Object> listDownloadTechPlanFile(Long projectId) throws Exception;;

    /**
     * 下载商务附件
     * @param projectId
     * @param response
     * @return
     * @throws Exception
     */
    void downloadBusinessFile(Long projectId, HttpServletResponse  response) throws Exception;

    /**
     * 查询需要下载商务附件
     * @param projectId
     * @return
     * @throws Exception
     */
    Map<String, Object> listDownloadBusinessFile(Long projectId) throws Exception;

    /**
     * 上传脱敏文件查询接口
     * @param projectId
     * @return
     */
    List<ExtSouOrderFileDto> getSecretFileList(Long projectId);

    /**
     * 提交上传脱敏文件
     * @param techFile
     * @param souType
     * @return
     */
    Long editSecretFile(ApiExtTechFileDto techFile, String souType);
}
