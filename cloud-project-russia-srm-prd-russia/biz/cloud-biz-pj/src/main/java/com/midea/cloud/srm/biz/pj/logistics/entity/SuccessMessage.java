package com.midea.cloud.srm.biz.pj.logistics.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author huangbf3
 */
@Data
public class SuccessMessage implements Serializable {

    private String returnCode;
    private LogisticsInfo message;
}
