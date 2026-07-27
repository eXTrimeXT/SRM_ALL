package com.midea.cloud.srm.sou.meiql.borrow.dto;

import lombok.Data;

import java.io.Serializable;
/**
 * 备注
 * @author huangbf3
 */
@Data
public class BorrowAttach implements Serializable {

    private Long borrowAttachId;

    private Long borrowId;

    private Long attachId;

    private String attachName;

    private String attachPath;

}
