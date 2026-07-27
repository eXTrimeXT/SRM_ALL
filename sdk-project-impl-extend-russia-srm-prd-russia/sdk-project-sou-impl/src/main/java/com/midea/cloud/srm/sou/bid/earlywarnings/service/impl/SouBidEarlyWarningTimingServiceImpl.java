package com.midea.cloud.srm.sou.bid.earlywarnings.service.impl;

import com.midea.cloud.srm.sou.bid.earlywarnings.service.SouBidEarlyWarningService;
import com.midea.cloud.srm.sou.bid.earlywarnings.service.SouBidEarlyWarningTimingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: for srm 预警定时任务实现类
 *
 * @author srm
 * @date 2024-05-20
 */
@Service
@Slf4j
public class SouBidEarlyWarningTimingServiceImpl implements SouBidEarlyWarningTimingService {

    @Autowired
    private List<SouBidEarlyWarningService> warningServiceList;

    @Override
    public String doWaring() {
        warningServiceList.stream().forEach(service -> {
            service.doWarning();
        });
        return "success";
    }
}
