package com.midea.cloud.srm.sou.brg.service;
/**
 * 备注
 * @author huangbf3
 */
public interface ExtNpmBrgService {

    /**
     * 根据板块生成竞价
     * 根据板块生成竞价单号
     * @param invbuCode
     * @return
     */
    public String generateSeq(String invbuCode);
}
