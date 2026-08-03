package com.midea.cloud.srm.biz.pj.sou.comp.init.domain;

import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouItem;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.init.ApiCompSouCurrencyEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.init.ApiCompSouProjectEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.init.ApiCompSouRequireInfoDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouRequireInfoDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouInitDetailVO;
import com.midea.cloud.srm.model.pm.pr.requirement.dto.RequirementManageDTO;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 竞价立项
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/13
 */
public interface CompInitDomainService {

    /**
     * 保存项目额外信息
     * @param project 备注
     * @param isTempSave 备注
     * @param isCopy 备注
     */
    void editProjectInfo(ApiCompSouProjectEditDTO project, boolean isTempSave, boolean isCopy);

    /**
     * 保存币种
     * @param project
     * @param currencyList
     */
    void editCurrency(ApiCompSouProjectEditDTO project, List<ApiCompSouCurrencyEditDTO> currencyList);

    /**
     * 保存项目需求信息
     * @param param
     * @param userId 用户ID
     * @param isTempSave 是否保存
     * @param isCopy 是否复制
     */
    void editRequireInfo(ApiCompSouRequireInfoDTO param, @Nullable Long userId, boolean isTempSave, boolean isCopy);

    /**
     * 询价立项 - (公式报价)缓存基材价格
     * @param projectId id
     * @param compItemList 备注
     * @param round 轮次
     */
    void saveCompBaseMaterialPrice(long projectId, @Nullable List<CompSouItem> compItemList, int round);

    /**
     *
     * 删除/废弃寻源单据后，对应的处理寻源需求、采购需求单据信息
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param initInfo 信息
     * @param toDelete 删除
     */
    void handlerRelativeBusinessAfterDeleteOrCancelComp(long projectId, ApiSouInitDetailVO initInfo, boolean toDelete);

    /**
     *
     * 转化来源于需求池的信息，以便后续保存寻源物料需求
     *
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param reqParams 需求池数据
     * @return
     */
    ApiSouRequireInfoDTO handlerSouItemsFromReq(long projectId, List<RequirementManageDTO> reqParams);

}
