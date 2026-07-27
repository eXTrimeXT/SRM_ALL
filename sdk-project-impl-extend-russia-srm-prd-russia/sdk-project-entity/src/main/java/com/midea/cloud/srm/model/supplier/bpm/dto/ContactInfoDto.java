package com.midea.cloud.srm.model.supplier.bpm.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.supplier.info.entity.ContactInfo;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * 备注
 * @author huangbf3
 */
@TableName("scc_sup_contact_info")
@ApiModel(
        description = "联系人信息"
)
@Data
@EqualsAndHashCode(callSuper = true)
public class ContactInfoDto extends ContactInfo {

    private Long socialSecurityCertificateFileId;
}
