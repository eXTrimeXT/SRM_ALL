package com.midea.cloud.srm.biz.pj.supplier.entry.service;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.model.pj.supplier.entry.dto.PjEntryConfigDTO;
import com.midea.cloud.srm.model.supplierauth.entry.dto.EntryCategoryConfigSaveResultDTO;
import com.midea.cloud.srm.model.supplierauth.entry.entity.EntryCategoryConfig;
import com.midea.cloud.srm.model.supplierauth.entry.entity.EntryConfig;

import java.util.List;

/**
 * <pre>
 *  供应商准入流程行表（品类配置） 服务类
 * </pre>
 *
 * @author luxc18@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020-09-15 11:28:48
 *  修改内容:
 * </pre>
 */
public interface IPjEntryConfigService {

    /**
     * 备注
     * @param entryConfigList
     * @return
     */
    List<Long> batchSaveOrUpdateList(List<PjEntryConfigDTO> entryConfigList);

    /**
     * 备注
     * @param entryConfig
     * @return
     */
    PageInfo<PjEntryConfigDTO> listPageByParam(EntryConfig entryConfig);
}
