package com.midea.cloud.srm.sou.bid.ipmonitors;

import com.midea.cloud.srm.model.sou.ipaddresss.entity.SccSouIpAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Component
public class IpMonitoryUtils {

    private static IpMonitoryUtils ipMonitoryUtils;

    @Autowired
    private  IpMonitoryCompent ipMonitoryCompent;

    @PostConstruct
    private void inite(){
        ipMonitoryUtils = this;
    }

    public static IpMonitoryUtils instance() {
        return ipMonitoryUtils;
    }

    private IpMonitoryUtils() {

    }

    public void ipMonitory(Long projectId, Long vendorId, String vendorCode, String vendorName, String source) {
        ipMonitoryCompent.ipMonitory(IpMonitoryCompent.buildParam(projectId, vendorId, vendorCode, vendorName, source));
    }
}
