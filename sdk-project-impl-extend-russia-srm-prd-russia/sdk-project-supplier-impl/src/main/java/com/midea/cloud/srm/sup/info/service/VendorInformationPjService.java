package com.midea.cloud.srm.sup.info.service;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.model.sup.info.dto.ExtManagementAttachDTO;
import com.midea.cloud.srm.model.supplier.info.dto.ManagementAttachRequestDTO;
import com.midea.cloud.srm.model.supplier.info.entity.ManagementAttach;

/**
 * <pre>
 *  VendorInformationPjService 接口
 * </pre>
 *
 * @author liangwl23@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/9/27 10:11
 *  修改内容:
 * </pre>
 */
public interface VendorInformationPjService {

    /**
     * 获取供应商信息
     * @param managementAttachRequestDTO
     * @return
     */
    PageInfo<ExtManagementAttachDTO> listManagementAttachPage(ManagementAttachRequestDTO managementAttachRequestDTO);
}
