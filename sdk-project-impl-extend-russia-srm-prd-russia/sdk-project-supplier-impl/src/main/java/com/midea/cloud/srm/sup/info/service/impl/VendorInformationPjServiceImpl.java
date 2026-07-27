package com.midea.cloud.srm.sup.info.service.impl;

import com.alibaba.excel.util.StringUtils;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.sup.info.dto.ExtManagementAttachDTO;
import com.midea.cloud.srm.model.supplier.info.dto.ManagementAttachRequestDTO;
import com.midea.cloud.srm.sup.info.mapper.VendorInformationPjMapper;
import com.midea.cloud.srm.sup.info.service.VendorInformationPjService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

/**
 * <pre>
 *  VendorInformationPjServiceImpl 类
 * </pre>
 *
 * @author liangwl23@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/9/27 10:18
 *  修改内容:
 * </pre>
 */
@Service
public class VendorInformationPjServiceImpl implements VendorInformationPjService {

    @Autowired
    private VendorInformationPjMapper vendorInformationPjMapper;
    @Autowired
    private BaseClient baseClient;

    @Override
    public PageInfo<ExtManagementAttachDTO> listManagementAttachPage(ManagementAttachRequestDTO managementAttachRequestDTO) {
        PageUtil.startPage(managementAttachRequestDTO.getPageNum(), managementAttachRequestDTO.getPageSize());
        List<ExtManagementAttachDTO> attachList = vendorInformationPjMapper.listManagementAttachPage(managementAttachRequestDTO
                ,managementAttachRequestDTO.getAuthDate() != null ? DateUtil.dateToLocalDate(managementAttachRequestDTO.getAuthDate()) : null
                , managementAttachRequestDTO.getEndDate() != null ? DateUtil.dateToLocalDate(managementAttachRequestDTO.getEndDate()) : null);
        if (CollectionUtils.isNotEmpty(attachList)) {
            //资质信息证件类型字典
            List<DictItemDTO> dtoList = baseClient.listAllByDictCode("CERTIFICATE_TYPE");
            Map<String, String> dictMap = new HashMap<>(16);
            if (!CollectionUtils.isEmpty(dtoList) && dtoList.size() > 0) {
                dtoList.forEach(dictItemDTO -> {
                    dictMap.put(dictItemDTO.getDictItemCode(), dictItemDTO.getDictItemName());
                });
            }
            for (ExtManagementAttachDTO extManagementAttachDTO : attachList) {
                if ("资质信息".equals(extManagementAttachDTO.getDocumentType())) {
                    if (StringUtils.isNotBlank(extManagementAttachDTO.getAuthNum())) {
                        String dictValue = dictMap.get(extManagementAttachDTO.getAuthNum());
                        if (StringUtils.isNotBlank(dictValue)) {
                            extManagementAttachDTO.setDocumentType(dictValue);
                        } else {
                            extManagementAttachDTO.setDocumentType(extManagementAttachDTO.getAuthNum());
                        }
                    }
                }
            }
        }
        return new PageInfo<ExtManagementAttachDTO>(attachList);
    }

    public static Date getDateByAddingDays(int daysToAdd) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, daysToAdd);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

}
