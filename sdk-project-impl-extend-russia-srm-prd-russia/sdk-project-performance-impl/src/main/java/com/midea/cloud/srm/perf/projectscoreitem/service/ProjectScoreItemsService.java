package com.midea.cloud.srm.perf.projectscoreitem.service;

import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.srm.model.cm.contract.entity.ContractHead;
import com.midea.cloud.srm.model.perf.projectscoreitem.dto.ProjectScoreItemsDTO;
import com.midea.cloud.srm.model.perf.projectscoreitem.dto.ProjectScoreItemsQueryDTO;
import com.midea.cloud.srm.model.perf.projectscoreitem.entity.ProjectScoreItems;
import com.midea.cloud.srm.model.perf.projectscoreitem.entity.ProjectScoreItemsPerson;
import com.midea.cloud.srm.model.perf.projectscoreman.entity.ProjectScoreMan;
import com.midea.cloud.srm.model.perf.projectscoreman.entity.ProjectScoreManDetail;
import com.midea.cloud.srm.model.perf.projectscoreman.entity.ProjectScoreManRejectInfo;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
public interface ProjectScoreItemsService extends BaseService<ProjectScoreItems> {
    /**
     * 备注
     * @param queryDTO 参数
     * @return 返回
     */
    List<ProjectScoreItems> listPage(ProjectScoreItemsQueryDTO queryDTO);

    /**
     * 备注
     * @param projectScoreItemsId 参数
     * @return 返回
     */
    ProjectScoreItemsDTO getDetailById(Long projectScoreItemsId);

    /**
     * 备注
     * @param dto 参数
     * @return 返回
     */
    Long saveOrUpdateDetail(ProjectScoreItemsDTO dto);

    /**
     * 备注
     * @param projectScoreItemsId 参数
     */
    void notifyScorers(Long projectScoreItemsId);

    /**
     * 备注
     * @param dto 参数
     */
    void reject(ProjectScoreItemsDTO dto);

    /**
     * 备注
     * @param projectScoreItemsId 参数
     */
    void calcScore(Long projectScoreItemsId);

    /**
     * 备注
     * @param projectScoreMan 参数
     * @return 返回
     */
    List<ProjectScoreManDetail> listScoreManDetailList(ProjectScoreMan projectScoreMan);

    /**
     *  备注
     * @param contractHead 参数
     * @return 返回
     */
    ProjectScoreItems getInfoByContractNo(ContractHead contractHead);

    /**
     * 获取驳回信息
     * @param projectScoreItemsPerson
     * @return
     */
    List<ProjectScoreManRejectInfo> queryProjectScoreManRejectInfo(ProjectScoreItemsPerson projectScoreItemsPerson);
}
