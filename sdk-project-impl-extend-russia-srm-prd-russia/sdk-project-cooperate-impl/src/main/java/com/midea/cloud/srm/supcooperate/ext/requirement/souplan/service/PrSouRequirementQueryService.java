package com.midea.cloud.srm.supcooperate.ext.requirement.souplan.service;

import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.dto.ExtPrSouRequirementForDataSubmit;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * 招标计划 - 查询服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/09/27
 */
public interface PrSouRequirementQueryService {

    /**
     * 获取板块接口人名单excel
     * @throws IOException 报错
     */
    void queryOrgBuInterfacePersonListExcel() throws IOException;


    /**
     * 根据距离截止时间的小时数，查询需要提交资料招标计划，
     * @param minHour 距离截止日期最小小时数
     * @param maxHour 距离截止日期最大小时数
     * @return
     */
    List<ExtPrSouRequirementForDataSubmit> findSouRequirementBySendProfileEndDateFromHour(int minHour,int maxHour);

    /**
     * 获取招标计划负责人
     * @param requirementHeadId
     * @return
     */
    JSONObject getBidFuZeRen(Long requirementHeadId);
}
