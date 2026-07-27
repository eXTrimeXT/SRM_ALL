package com.midea.cloud.srm.sou.req.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.sou.req.SouInviteHead;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 邀请供应商头表
 *
 * @author xiaym13 xiaym13@meicloud.com
 * @since 1.0.0 2023-10-13
 */
@Mapper
public interface SouInviteHeadMapper extends BaseMapper<SouInviteHead> {
    /**
     * 备注
     * @param qDc 参数
     * @return 返回
     */
    List<SouInviteHead> listPage(SouInviteHead qDc);

    /**
     * listCategoryByVendors
     * @param map
     * @return
     */
    List<SouInviteHead> listCategoryByVendors(Map<String, Object> map);
}
