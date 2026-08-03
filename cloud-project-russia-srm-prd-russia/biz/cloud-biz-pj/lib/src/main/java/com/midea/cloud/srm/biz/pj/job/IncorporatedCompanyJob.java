package com.midea.cloud.srm.biz.pj.job;

import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.common.result.ResultCode;
import com.midea.cloud.quartz.bind.Job;
import com.midea.cloud.quartz.handler.ExecuteableJob;
import com.midea.cloud.srm.biz.pj.changchengapi.bpm.service.IBpmIncorporatedCompanyService;
import com.midea.cloud.srm.model.pj.changchengapi.dto.BpmIncorporatedCompanyParam;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * @Description: for srm法务-法人公司主数据拉取
 *
 * @author srm
 * @date 2024-05-17
 */
@Job("incorporatedCompanyJob")
@Slf4j
public class IncorporatedCompanyJob implements ExecuteableJob {

    private static final String SIZE = "size";

    private static final String UPDATE_TIME = "updateTime";

    @Resource
    private IBpmIncorporatedCompanyService iBpmIncorporatedCompanyService;

    /**
     * @param params
     * @return
     */
    @Override
    public BaseResult executeJob(Map<String, String> params) {
        try {
            BpmIncorporatedCompanyParam param = new BpmIncorporatedCompanyParam();
            param.setPage(1);
            if(params.containsKey(SIZE)){
                param.setSize(Integer.valueOf(params.get(SIZE)));
            }else{
                param.setSize(100);
            }
            if(params.containsKey(UPDATE_TIME)){
                param.setUpdateTime(params.get(UPDATE_TIME));
            }else{
                param.setUpdateTime(getDate());
            }
            iBpmIncorporatedCompanyService.pullData(param);
        } catch (Exception e) {
            log.error("执行定时任务【法务-法人公司主数据拉取】异常", e);
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
