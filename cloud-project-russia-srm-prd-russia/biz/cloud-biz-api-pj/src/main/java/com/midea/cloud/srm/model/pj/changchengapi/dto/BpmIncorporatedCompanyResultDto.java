package com.midea.cloud.srm.model.pj.changchengapi.dto;

import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmIncorporatedCompany;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 法务-法人公司主数据接口返回参数对象
 * @author huangbf3
 */
@Data
public class BpmIncorporatedCompanyResultDto<T> {
    /**
     * 总条数
     */
    private Integer total;
    /**
     * 当前页
     */
    private Integer page;
    /**
     * 行数据
     */
    private List<T> rows;
}
