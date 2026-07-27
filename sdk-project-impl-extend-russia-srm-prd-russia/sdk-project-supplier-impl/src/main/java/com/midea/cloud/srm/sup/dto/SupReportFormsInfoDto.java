package com.midea.cloud.srm.sup.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Data
public class SupReportFormsInfoDto implements Serializable {

    private String zbCode;
    private String supName;
    private Long supId;
    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 品类
     */
    private String pl;
    private String sfBid;
    private String bBidReason;
    private String sfBidder;
    private String bidderMoney;
    private String htStatus;
    private String htGetScore;
    private String htResult;
    private String htDealResult;
    private String tbPerson;
    private String tbTel;
    /**
     * 项目编号
     */
    private String projectCode;

    /**
     * 项目状态
     */
    private String projectStatus;

}
