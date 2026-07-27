package com.midea.cloud.srm.sou.req.service;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.srm.model.sou.req.SouInviteHead;

/**
 * 邀请供应商头表
 *
 * @author xiaym13 xiaym13@meicloud.com
 * @since 1.0.0 2023-10-13
 */
public interface SouInviteHeadService extends BaseService<SouInviteHead> {
    /**
     * 邀请供应商报名-分页查询
     *
     * @param params
     * @return
     */
    public PageInfo<SouInviteHead> listPage(SouInviteHead params);
}
