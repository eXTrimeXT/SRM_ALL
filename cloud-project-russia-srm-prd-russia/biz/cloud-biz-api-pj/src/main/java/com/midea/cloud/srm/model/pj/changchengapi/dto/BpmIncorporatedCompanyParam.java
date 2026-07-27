package com.midea.cloud.srm.model.pj.changchengapi.dto;

import lombok.Data;

/**
 * 法务-法人公司主数据接口请求参数对象
 * @author huangbf3
 */
@Data
public class BpmIncorporatedCompanyParam {

    /**
     * 当前页
     */
    private Integer page;

    /**
     * 每页显示条数，默认10，最大500
     */
    private Integer size;

    /**
     * 更新时间（年月日时分秒：yyyy-MM-dd HH:mm:ss
     */
    private String updateTime;
}
