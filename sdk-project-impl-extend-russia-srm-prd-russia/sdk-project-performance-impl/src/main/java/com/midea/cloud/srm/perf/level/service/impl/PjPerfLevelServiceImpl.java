package com.midea.cloud.srm.perf.level.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.result.ResultCode;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.perf.level.entity.PerfLevel;
import com.midea.cloud.srm.perf.common.PerfLevelConst;
import com.midea.cloud.srm.perf.level.mapper.PerfLevelMapper;
import com.midea.cloud.srm.perf.level.service.IPjPerfLevelService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <pre>
 *  绩效等级表 服务实现类
 * </pre>
 *
 * @author luxc18@meiCloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020-06-03 09:26:13
 *  修改内容:
 * </pre>
 */
@Service
public class PjPerfLevelServiceImpl extends BaseServiceImpl<PerfLevelMapper, PerfLevel> implements IPjPerfLevelService {

    @Resource
    private FileCenterClient fileCenterClient;

    @Resource
    private BaseClient baseClient;

    /**
     * 保存或修改绩效等级信息
     *
     * @param perfLevel
     * @return
     * @throws BaseException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String saveOrUpdatePerfLevel(PerfLevel perfLevel) throws BaseException {
        Assert.notNull(perfLevel, ResultCode.MISSING_SERVLET_REQUEST_PARAMETER.getMessage());
        Long levelId = perfLevel.getLevelId();
        //是否修改默认为不是
        boolean isUpdate = false;
        if (null == levelId) {
            //新增
            levelId = IdGenrator.generate();
            perfLevel.setLevelId(levelId);
            perfLevel.setDeleteFlag(Enable.N.toString());
            perfLevel.setVersion(1L);
        } else {
            isUpdate = true;
        }
        //检查绩效等级信息能否保存
        this.checkSaveOrUpdatePerfLevel(perfLevel, isUpdate);

        boolean isUpdateSuccess = false;
        try {
            isUpdateSuccess = super.saveOrUpdate(perfLevel);
        } catch (Exception e) {
            log.error("保存/修改绩效等级信息时报错：", e);
            throw new BaseException(ResultCode.OPERATION_FAILED.getMessage());
        }
        if (!isUpdateSuccess) {
            return ResultCode.OPERATION_FAILED.getMessage();
        }
        return ResultCode.SUCCESS.getMessage();
    }

    /**
     * 当状态为启用时，校验得分区间不能重叠
     *
     * @param perfLevel
     */
    @Override
    public void checkScoreOverlap(PerfLevel perfLevel) {
        if (Enable.Y.equals(perfLevel.getStatus())) {
            List<PerfLevel> dbPerfLevels = new ArrayList<>();
            // 未经暂存
            if (Objects.isNull(perfLevel.getLevelId())) {
                dbPerfLevels = this.list(Wrappers.lambdaQuery(PerfLevel.class)
                        .eq(PerfLevel::getStatus, Enable.Y)
                );
            }
            // 更新操作
            else {
                dbPerfLevels = this.list(Wrappers.lambdaQuery(PerfLevel.class)
                        .eq(PerfLevel::getStatus, Enable.Y)
                        .ne(PerfLevel::getLevelId, perfLevel.getLevelId())
                );
            }
            BigDecimal scoreStart = perfLevel.getScoreStart();
            BigDecimal scoreEnd = perfLevel.getScoreEnd();
            if (CollectionUtils.isNotEmpty(dbPerfLevels)) {
                dbPerfLevels.forEach(dbPerfLevel -> {
                    BigDecimal dbScoreStart = dbPerfLevel.getScoreStart();
                    BigDecimal dbScoreEnd = dbPerfLevel.getScoreEnd();
                    // 左边开始节点在右边区间
                    boolean case1 = (scoreStart.compareTo(dbScoreStart) > 0) && (scoreStart.compareTo(dbScoreEnd) < 0);
                    // 右边开始节点在左边区间
                    boolean case2 = (dbScoreStart.compareTo(scoreStart) > 0) && (dbScoreStart.compareTo(scoreEnd) < 0);
                    // 开始节点相等
                    boolean case3 = (scoreStart.compareTo(dbScoreStart) == 0);
                    // 结束节点相等
                    boolean case4 = (scoreEnd.compareTo(dbScoreEnd) == 0);
                    if (case1 || case2 || case3 || case4) {
                        String levelName = dbPerfLevel.getLevelName();
                        StringBuffer sb = new StringBuffer();
                        sb.append("绩效等级得分重叠，已存在等级名称：[").append(levelName).append("]，已有得分区间：[")
                                .append(dbScoreStart).append("至").append(dbScoreEnd).append("]，请调整后重试。");
                        throw new BaseException(LocaleHandler.getLocaleMsg(sb.toString()));
                    }
                });
            }
        }
    }

    /**
     * Description 检查绩效等级信息能否保存
     *
     * @return
     * @throws Assert
     * @Param perfLevel 绩效等级实体类
     * @Param isUpdate 是否修改，是则为true
     * @Author luxc18@meicloud.com
     * @Date 2020.06.03
     **/
    private void checkSaveOrUpdatePerfLevel(PerfLevel perfLevel, boolean isUpdate) {
        Assert.notNull(perfLevel, ResultCode.MISSING_SERVLET_REQUEST_PARAMETER.getMessage());
        Assert.notNull(perfLevel.getLevelId(), "id不能为空");
        Assert.notNull(perfLevel.getLevelName(), PerfLevelConst.LEVEL_NAME_NOT_NULL);
        Assert.notNull(perfLevel.getLevelDescription(), PerfLevelConst.LEVEL_DESCRIPTION_NOT_NULL);
        Assert.notNull(perfLevel.getScoreStart(), PerfLevelConst.SCORE_START_NOT_NULL);
        Assert.notNull(perfLevel.getScoreEnd(), PerfLevelConst.SCORE_END_NOT_NULL);
        Assert.notNull(perfLevel.getStatus(), PerfLevelConst.STATUS_NOT_NULL);

        // 当状态为启用时，校验得分区间不能重叠
        this.checkScoreOverlap(perfLevel);
    }

}
