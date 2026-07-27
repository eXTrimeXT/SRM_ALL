package com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao;

import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendorRecord;
import org.springframework.stereotype.Service;

/**
 * 寻源核心 - 追加供应商记录表
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/24
 */
@Service
public class SouVendorRecordDAOImpl extends BaseServiceImpl<SouVendorRecordMapper, SouVendorRecord> implements BaseService<SouVendorRecord> {
}
