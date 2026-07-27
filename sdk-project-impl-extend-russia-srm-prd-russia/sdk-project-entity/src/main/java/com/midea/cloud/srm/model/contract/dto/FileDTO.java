package com.midea.cloud.srm.model.contract.dto;

import lombok.Data;
/**
 * 备注
 * @author huangbf3
 */
@Data
public class FileDTO {
    /** 文件格式 */
    private String docType;
    /** 文件名 */
    private String fileName;
    /** 文件地址 */
    private String fileUrl;
    /** 签章系统文档id */
    private Long signDocumentId;

}