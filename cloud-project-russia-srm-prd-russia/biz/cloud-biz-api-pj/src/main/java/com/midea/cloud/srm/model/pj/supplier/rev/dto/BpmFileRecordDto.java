package com.midea.cloud.srm.model.pj.supplier.rev.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author huangbf3
 */
@Data
public class BpmFileRecordDto implements Serializable {

    /**
     * 附件名称
     */
    private String fjmc;

    /**
     * 模板下载
     */
    private String mbxz;

    /**
     * 附件上传
     */
    private String fjsc;

    /**
     * 备注
     */
    private String bz;
}
