package com.midea.cloud.srm.model.contract.dto;

import com.midea.cloud.srm.model.contract.dto.FileDTO;
import lombok.Data;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
public class ContractFileDTO {
    /**
     * 创建方式
     */
    private Integer createType;
    /**
     * 附件类型
     */
    private Integer fileType;
    /**
     * 附件类型名称
     */
    private String fileTypeName;
    /**
     * 文件详细信息
     */
    private List<FileDTO> files;
    /**
     * 是否用印
     */
    private Integer isSign;
}