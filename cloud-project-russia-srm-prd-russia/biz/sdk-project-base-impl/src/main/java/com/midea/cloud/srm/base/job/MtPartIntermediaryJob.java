package com.midea.cloud.srm.base.job;

import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.common.result.ResultCode;
import com.midea.cloud.quartz.bind.Job;
import com.midea.cloud.quartz.handler.ExecuteableJob;
import com.midea.cloud.srm.base.material.service.MtPartIntermediaryService;
import com.midea.cloud.srm.model.pj.changchengapi.material.MaterialParam;
import dm.jdbc.util.DateUtil;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * @Description: for srm 非生产物料数据拉取
 *
 * @author srm
 * @date 2024-05-18
 */
@Job("mtPartIntermediaryJob")
@Slf4j
public class MtPartIntermediaryJob implements ExecuteableJob {

    @Resource
    private MtPartIntermediaryService mtPartIntermediaryService;

    private static final String SIZE = "size";
    private static final String UPDATE_TIME = "updateTime";

    /**
     * @param params
     * @return
     */
    @Override
    public BaseResult executeJob(Map<String, String> params) {
        try {
            MaterialParam materialParam = new MaterialParam();
            materialParam.setPage(1);
            if(params.containsKey(SIZE)){
                materialParam.setSize(Integer.valueOf(params.get(SIZE)));
            }else{
                materialParam.setSize(100);
            }
            if(params.containsKey(UPDATE_TIME)){
                materialParam.setUpdateTime(params.get(UPDATE_TIME));
            }else{
                materialParam.setUpdateTime(getDate());
            }
            mtPartIntermediaryService.pullData(materialParam);
        } catch (Exception e) {
            log.error("执行定时任务【非生产物料数据拉取】异常", e);
            return BaseResult.build(ResultCode.UNKNOWN_ERROR, e.getMessage());
        }
        return BaseResult.build(ResultCode.SUCCESS);
    }

    /**
     * 获取前一天时间
     * @return
     */
    public static String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        Date start = c.getTime();
        return format.format(start);
    }
    public static void main(String[] args) {
        log.info(getDate());
    }
}
