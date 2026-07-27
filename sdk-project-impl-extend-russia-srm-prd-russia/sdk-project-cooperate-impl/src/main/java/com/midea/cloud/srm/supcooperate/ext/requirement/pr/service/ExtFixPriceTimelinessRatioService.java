package com.midea.cloud.srm.supcooperate.ext.requirement.pr.service;

import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.ExtFixPriceTimelinessRatioDetail;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.ExtFixPriceTimelinessRatioHead;

import java.util.List;
import java.util.Map;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/7/5
 */
public interface ExtFixPriceTimelinessRatioService {
    /**
     * list
     * @param query
     * @return
     */
    public List<ExtFixPriceTimelinessRatioDetail> list(Map<String,Object> query);

    /**
     * listById
     * @param query
     * @return
     */
    public List<ExtFixPriceTimelinessRatioDetail> listById(Map<String,Object>query);

    /**
     * get
     * @param query
     * @return
     */
    public List<ExtFixPriceTimelinessRatioHead>get(Map<String,Object> query);
}
