package com.midea.cloud.srm.biz.pj.logistics.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author huangbf3
 */
@Data
public class LogisticsInfo implements Serializable {

    private String com;
    private String condition;
    private LogisticsData data;
    private String ischeck;
    private String message;
    private String nu;
    private String state;
    private String status;
}
