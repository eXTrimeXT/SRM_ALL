package com.midea.cloud.srm.sou.sourcing.init.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSccSouTechScoreHistory;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouTechScoreLine;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
public interface IExtSccSouTechScoreHistoryService extends IService<ExtSccSouTechScoreHistory> {

    /**
     * 保存历史记录
     * @param techScoreLineList
     * @return
     */
    public List<ExtSccSouTechScoreHistory> saveTechScoreHist(List<ExtSouTechScoreLine> techScoreLineList);
}
