package com.midea.cloud.srm.supcooperate.eas.entity;

import lombok.Data;

import java.io.Serializable;
/**
 * 备注
 * @author huangbf3
 */
@Data
public class ReceiveInfo implements Serializable {

    private String deliveryNoteNum;
    private String deliveryNoteLineNum;
    /** 收货数 */
    private String collectNum;

}
