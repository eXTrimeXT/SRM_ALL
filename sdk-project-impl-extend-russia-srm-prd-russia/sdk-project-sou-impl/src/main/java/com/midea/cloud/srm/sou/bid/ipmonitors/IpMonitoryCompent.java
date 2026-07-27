package com.midea.cloud.srm.sou.bid.ipmonitors;

import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.model.sou.ipaddresss.entity.SccSouIpAddress;
import com.midea.cloud.srm.sou.bid.init.controller.SccSouIpAddressController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
/**
 * 备注
 * @author huangbf3
 */
@Component
@Slf4j
public class IpMonitoryCompent {

    @Autowired
    private SccSouIpAddressController sccSouIpAddressController;

    public void ipMonitory(SccSouIpAddress ipAddress) {
        try {
            log.info(MessageFormat.format("开始监听IP地址：{0}-{1}-{2}", ipAddress.getBidsId(), ipAddress.getSupplierCode(), ipAddress.getSource()));
            HttpServletRequest  request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
            sccSouIpAddressController.addIpAddress(ipAddress, request);
            log.info(MessageFormat.format("成功监听IP地址：{0}-{1}-{2}", ipAddress.getBidsId(), ipAddress.getSupplierCode(), ipAddress.getSource()));

        } catch (Exception e) {
            log.error("ipMonitory Exception", e);
        }
    }

    public static SccSouIpAddress buildParam(Long projectId, Long vendorId, String vendorCode, String vendorName, String source) {
        SccSouIpAddress ipAddress = new SccSouIpAddress();
        ipAddress.setRowId(IdGenrator.generate());
        ipAddress.setBidsId(projectId);
        ipAddress.setSupplierId(vendorId);
        ipAddress.setSupplierCode(vendorCode);
        ipAddress.setSupplierName(vendorName);
        ipAddress.setSource(source);
        return ipAddress;
    }

}
