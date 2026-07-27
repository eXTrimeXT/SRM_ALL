package com.midea.cloud.srm.model.sou.designplans.dto;


import com.midea.cloud.srm.model.sou.designplans.entity.SccSouChPaaAdjustAtt;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * @program: master
 * @description:
 * @author: 100014337
 * @create: 2023-12-18 16:24
 * @version 1.0
 **/
@Getter
@Setter
public class PaaAdjustDto {

    private Long adjustId;

    private Long designId;

    private String adjustCode;

    private String adjustName;

    private String status;

    private Long jcId;

    private String jcCode;

    private Integer num;

    private Date executeDateStart;

    private Date executeDateEnd;

    private String adjustType;

    private String createDateEnd;

    private String createdBy;

    private String creationDate;

    private Long createUnitId;

    private String createUnitCode;

    private String createUnitName;

    private String createdFullName;

    /**
     * 调价介绍
     **/
    private String introduce;

    List<SccSouChPaaAdjustAtt> adjustAttList;
}

