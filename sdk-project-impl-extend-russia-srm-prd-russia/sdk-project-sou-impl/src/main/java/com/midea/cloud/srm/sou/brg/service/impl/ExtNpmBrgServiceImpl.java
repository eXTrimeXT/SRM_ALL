package com.midea.cloud.srm.sou.brg.service.impl;

import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.srm.sou.brg.service.ExtNpmBrgService;
import com.midea.cloud.srm.sou.souseq.service.IExtSouSeqService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
/**
 * 备注
 * @author huangbf3
 */
@Slf4j
@Service
public class ExtNpmBrgServiceImpl implements ExtNpmBrgService {

    @Autowired
    private IExtSouSeqService extSouSeqService;

    private static final String SEQ_PREFIX_BRG = "BRG_";
    private static final String SEQ_PREFIX = "GW";

    @Override
    public String generateSeq(String invbuCode) {
        String dateStr = DateUtil.format(new Date(), "yyyyMM");
        String yearStr = DateUtil.format(new Date(), "yyyy");
        String seqCode = extSouSeqService.getSerial(StringUtils.join(SEQ_PREFIX_BRG, SEQ_PREFIX), invbuCode, yearStr, 3L);
        return StringUtils.join(SEQ_PREFIX, dateStr, "-", invbuCode, seqCode, "（竞价）");
    }
}
