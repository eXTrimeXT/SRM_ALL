package com.midea.cloud.srm.model.pj.supplier.rev.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author huangbf3
 */
@Data
public class BpmReviewFormExpDto implements Serializable {

    /**
     * 原因
     */
    private String yy;

    /**
     * 原因描述
     */
    private String yyms;
}
