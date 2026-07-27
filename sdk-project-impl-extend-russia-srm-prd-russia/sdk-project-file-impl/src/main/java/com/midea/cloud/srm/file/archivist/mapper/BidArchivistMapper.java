package com.midea.cloud.srm.file.archivist.mapper;

import com.midea.cloud.srm.model.sou.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouDemand;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/7/13
 */
public interface BidArchivistMapper {

    /**
     * 查询申请单号
     * @param param
     * @return
     */
    List<ExtSouDemand> querySouDemand(Map<String, Object> param);

    /**
     * 查询申请资料附件
     * @param param
     * @return
     */
    List<Long> queryRequirementFileId(Map<String, Object> param);

    /**
     * 查询招标资料提交附件
     * @param param
     * @return
     */
    List<Long> queryDataSubmitFileId(Map<String, Object> param);

    /**
     * 查询投标附件
     * @param param
     * @return
     */
    List<Map<String, Object>> queryOrderFile(Map<String, Object> param);

    /**
     * 查询质疑附件
     * @param param
     * @return
     */
    List<Map<String, Object>> queryQuestionFileId(Map<String, Object> param);

    /**
     * 查询保证金附件
     * @param param
     * @return
     */
    List<Map<String, Object>> queryMarginFileId(Map<String, Object> param);

    /**
     * 查询澄清附件
     * @param param
     * @return
     */
    List<Long> queryAnswerFileId(Map<String, Object> param);

    /**
     * 查询定标申请附件
     * @param param
     * @return
     */
    List<Long> queryCaFileId(Map<String, Object> param);

    /**
     * 查询招标附件
     * @param param
     * @return
     */
    List<Map<String, Object>> querySouFileId(Map<String, Object> param);

    /**
     * 查询考察附件
     * @param param
     * @return
     */
    List<Long> queryInspectFileId(Map<String, Object> param);

    /**
     * 查询中落标附件
     * @param param
     * @return
     */
    List<Long> queryNoticeFileId(Map<String, Object> param);

    /**
     * 查询推荐单附件
     * @param param
     * @return
     */
    List<Long> queryRecomFileId(Map<String, Object> param);

    /**
     * 查询标前交流附件
     * @param param
     * @return
     */
    List<Long> queryPreFileId(Map<String, Object> param);

    /**
     * 查询附件
     * @param param
     * @return
     */
    List<Fileupload>  queryFileuploadByIds(Map<String, Object> param);

    /**
     * 查询招标项目
     * @param projectId
     * @return
     */
    ExtSouProject queryProjectById(@Param("projectId") Long projectId);
}
