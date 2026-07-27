package com.midea.cloud.srm.biz.pj.sou.sourcing.select.dao;

import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.select.ApiSouSelectItemQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.select.ApiSouSelectResultVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.select.BiddingSuccessResltVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.ApiSouSelectQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select.ApiSouSelectQueryVO;

import java.util.List;

/**
 * 寻源核心 - 评选
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/20
 */
public interface SouSelectMapper {

    /**
     * 评选信息查询
     * @param queryParam
     * @return
     */
    List<ApiSouSelectItemQueryVO> querySelectList(ApiSouSelectQueryDTO queryParam);

    /**
     * 查询历史数据
     * @param queryParam
     * @return
     */
    List<ApiSouSelectItemQueryVO> queryHisItemList(ApiSouSelectQueryDTO queryParam);

    /**
     * 报价结果
     * @param queryParam
     * @return
     */
    List<ApiSouSelectResultVO> querySelectResultList(ApiSouSelectQueryDTO queryParam);

    /**
     *  备注
     * @param queryParam
     * @return
     */
    List<ApiSouSelectResultVO> queryWinNoticeList(ApiSouSelectQueryDTO queryParam);


    /**
     * 中标通知推送bpm数据查询方法
     * @param queryParam
     * @return
     */
    List<BiddingSuccessResltVO> biddingSuccessResltList(ApiSouSelectQueryDTO queryParam);

}
