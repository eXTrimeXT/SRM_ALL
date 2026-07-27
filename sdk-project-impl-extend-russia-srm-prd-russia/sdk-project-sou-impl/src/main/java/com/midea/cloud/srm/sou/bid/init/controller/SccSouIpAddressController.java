package com.midea.cloud.srm.sou.bid.init.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.model.sou.ipaddresss.dto.IpAddressDto;
import com.midea.cloud.srm.model.sou.ipaddresss.entity.SccSouIpAddress;
import com.midea.cloud.srm.sou.bid.init.service.SccSouIpAddressService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 备注
 * @author huangbf3
 */
@RestController
@RequestMapping("/bids/ip/address")
@Api(tags = "招标-IP地址管理")
@Slf4j
public class SccSouIpAddressController {

    @Resource
    private SccSouIpAddressService sccSouIpAddressService;

    /**
     * 招标地址
     * @return
     */
    @PostMapping("/ipAddress/zbList")
    public Map<String, Object>  zbIpAddressList(@RequestBody SccSouIpAddress ipAddress) {
        if (ipAddress.getProjectId() != null) {
            ipAddress.setBidsId(ipAddress.getProjectId());
        }
        List<SccSouIpAddress> list = sccSouIpAddressService.list(new QueryWrapper<>(ipAddress));
        dealSameIpForBid(list);
        int num = 0;
        for (SccSouIpAddress e : list) {
            if ("Y".equals(e.getSameIpFlag())) {
                num++;
            }
        }
        Map<String, Object> resultMap = new HashMap<>(50);
        resultMap.put("num", num);
        resultMap.put("ipAddressList", list);
        return resultMap;
    }

    private void dealSameIpForBid(List<SccSouIpAddress> list) {
        log.info("dealSameIpForBid start...");
        //包括最近一个月监控有IP相同的
        if(CollectionUtils.isEmpty(list)) {
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);

        /** 查询最近一个月相同的IP监控 */
        List<SccSouIpAddress> otherIpLastMonthList = sccSouIpAddressService.lambdaQuery().in(SccSouIpAddress::getIp, list.stream().map(SccSouIpAddress::getIp).distinct().collect(Collectors.toList()))
                .in(SccSouIpAddress::getSupplierId, list.stream().map(SccSouIpAddress::getSupplierId).distinct().collect(Collectors.toList()))
                .ge(SccSouIpAddress::getMonitorTime, calendar.getTime())
                .notIn(SccSouIpAddress::getBidsId, list.stream().map(SccSouIpAddress::getBidsId).distinct().collect(Collectors.toList())).list();

        Map<String, Set<Long>> ipMap = new HashMap<>(15);

        list.stream().forEach(ipAddress -> {
            cacheIpMap(ipMap, ipAddress);
        });

        otherIpLastMonthList.stream().forEach(ipAddress -> {
            cacheIpMap(ipMap, ipAddress);
        });

        /** 同一个IP地址出现两个或以上不同供应商时判断重复 */
        list.stream().forEach(ipAddress -> {
            if(ipMap.getOrDefault(ipAddress.getIp(), new HashSet<>()).size() > 1) {
                ipAddress.setSameIpFlag(YesOrNo.YES.getValue());
            } else {
                ipAddress.setSameIpFlag(YesOrNo.NO.getValue());
            }
        });
    }

    private void cacheIpMap(Map<String, Set<Long>> ipMap, SccSouIpAddress sccSouIpAddress) {
        if(!ipMap.containsKey(sccSouIpAddress.getIp())) {
            ipMap.put(sccSouIpAddress.getIp(), new HashSet<>());
        }
        ipMap.get(sccSouIpAddress.getIp()).add(sccSouIpAddress.getSupplierId());
    }

    @PostMapping("/ipAddress/list")
    public PageInfo<SccSouIpAddress> ipAddressList(@RequestBody SccSouIpAddress ipAddress) {
        PageUtil.startPage(ipAddress.getPageNum(), ipAddress.getPageSize());
        LambdaQueryWrapper<SccSouIpAddress> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(ipAddress.getSupplierName())) {
            queryWrapper.like(SccSouIpAddress::getSupplierName, ipAddress.getSupplierName());
        }
        if (StringUtils.isNotBlank(ipAddress.getSource())) {
            queryWrapper.eq(SccSouIpAddress::getSource, ipAddress.getSource());
        }
        if(StringUtils.isNotBlank(ipAddress.getIp())) {
            queryWrapper.like(SccSouIpAddress::getIp, ipAddress.getIp());
        }
        if(StringUtils.isNotBlank(ipAddress.getMonitorTimeFrom())) {
            queryWrapper.ge(SccSouIpAddress::getMonitorTime, ipAddress.getMonitorTimeFrom());
        }
        if(StringUtils.isNotBlank(ipAddress.getMonitorTimeTo())) {
            queryWrapper.le(SccSouIpAddress::getMonitorTime, ipAddress.getMonitorTimeTo());
        }

        queryWrapper.orderByDesc(SccSouIpAddress::getMonitorTime);
        List<SccSouIpAddress> list = sccSouIpAddressService.list(queryWrapper);
        dealSameIp(list);
        return new PageInfo<>(list);
    }

    public void dealSameIp(List<SccSouIpAddress> list) {
        for (SccSouIpAddress ssid1 : list) {
            if (ssid1.getSupplierId() == null) {
                continue;
            }
            for (SccSouIpAddress ssid2 : list) {
                if (!ssid1.getRowId().equals(ssid2.getRowId()) &&
                        ssid1.getIp().equals(ssid2.getIp()) &&
                        !ssid1.getSupplierId().equals(ssid2.getSupplierId())) {
                    ssid2.setSameIpFlag("Y");
                }
            }
        }

    }

    @PostMapping("/ipAddress/save")
    public String addIpAddress(@RequestBody SccSouIpAddress ipAddress, HttpServletRequest request) {
//        List<SccSouIpAddress> ipList = new ArrayList<>();
        String ipStr = getIpAddr(request);
        ipAddress.setIp(ipStr);
        ipAddress.setMonitorTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        return sccSouIpAddressService.save(ipAddress) ? "{\"code\": 1, \"message\": \"操作成功！\"}" : "{\"code\": 0, \"message\": \"操作失败！\"}";
    }

    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        try {
            String unknown = "unknown";
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || unknown.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || unknown.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || unknown.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                String localIp = "127.0.0.1";
                if (localIp.equals(ipAddress)) {
                    // 根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP，多个IP按照','分割
            int num15 = 15;
            String text = ",";
            if (ipAddress != null && ipAddress.length() > num15) {
                if (ipAddress.indexOf(text) > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress = "";
        }
        return ipAddress;
    }

}
