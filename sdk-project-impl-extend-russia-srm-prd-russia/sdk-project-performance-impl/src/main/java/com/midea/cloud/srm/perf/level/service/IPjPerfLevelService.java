package com.midea.cloud.srm.perf.level.service;

import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.perf.level.entity.PerfLevel;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *  <pre>
 *  绩效等级表 服务类
 * </pre>
 *
 * @author luxc18@meiCloud.com
 * @version 1.00.00
 *
 *  <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020-06-03 09:26:13
 *  修改内容:
 * </pre>
 */
public interface IPjPerfLevelService extends BaseService<PerfLevel> {

    /**
     * 保存或修改绩效等级信息
     * @param perfLevel 参数
     * @return 返回
     * @throws BaseException 报错
     */
    String saveOrUpdatePerfLevel(PerfLevel perfLevel) throws BaseException;

    /**
     * 当状态为启用时，校验得分区间不能重叠
     * @param perfLevel
     */
    void checkScoreOverlap(PerfLevel perfLevel);

}
