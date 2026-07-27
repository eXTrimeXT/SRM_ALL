package com.midea.cloud.srm.sup.info.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.sup.info.dto.ExtManagementAttachDTO;
import com.midea.cloud.srm.model.supplier.info.dto.ManagementAttachRequestDTO;
import com.midea.cloud.srm.model.supplier.info.dto.VendorInformation;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.model.supplier.info.entity.OrgCategory;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * <pre>
 *  VendorInformationPjMapper 接口
 * </pre>
 *
 * @author liangwl23@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/9/27 10:33
 *  修改内容:
 * </pre>
 */
public interface VendorInformationPjMapper extends BaseMapper<VendorInformation> {

    /**
     * 1、查询供应商状态为准、正式、呆滞供应商。
     *       是否黑名单=否
     *       是否时间限制=是
     * @param managementAttachRequestDTO
     * @param authDate
     * @param endDate
     * @return
     */
    List<ExtManagementAttachDTO> listManagementAttachPage(@Param("managementAttachRequestDTO") ManagementAttachRequestDTO managementAttachRequestDTO,@Param("authDate") LocalDate authDate, @Param("endDate") LocalDate endDate);

    /**
     * 获取供应商
     * @return
     */
    List<CompanyInfo> listSluggishSupplier();
    /**
     * 获取供应商
     * @return
     */
    List<OrgCategory> listUnusedOrgCategory();
}
