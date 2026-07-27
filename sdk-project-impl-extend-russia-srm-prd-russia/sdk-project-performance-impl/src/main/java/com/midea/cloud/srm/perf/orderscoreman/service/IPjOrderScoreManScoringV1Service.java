package com.midea.cloud.srm.perf.orderscoreman.service;

import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.perf.orderscoreman.entity.PjOrderScoreManScoringV1;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 *  绩效评分项目评分人表 服务类
 * </pre>
 *
 * @author luxc18@meiCloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020-06-06 15:10:36
 *  修改内容:
 * </pre>
 */
public interface IPjOrderScoreManScoringV1Service extends BaseService<PjOrderScoreManScoringV1> {
    /**
     * 备注
     * @param scoreManScoringV1 参数
     * @return 返回
     */
    List<PjOrderScoreManScoringV1> listScoreManScoringPage(PjOrderScoreManScoringV1 scoreManScoringV1);

    /**
     * 备注
     * @param scoreManScoringV1List 参数
     * @return 返回
     */
    String saveScoreManScoring(List<PjOrderScoreManScoringV1> scoreManScoringV1List);

    /**
     * 备注
     * @param file 参数
     * @param fileupload 参数
     * @return 返回
     * @throws Exception 报错
     */
    Map<String, Object> importScoreManScoringV1Excel(MultipartFile file, Fileupload fileupload) throws Exception;
}
