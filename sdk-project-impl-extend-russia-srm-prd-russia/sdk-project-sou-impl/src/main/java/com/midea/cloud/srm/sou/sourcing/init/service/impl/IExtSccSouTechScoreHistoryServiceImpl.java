package com.midea.cloud.srm.sou.sourcing.init.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSccSouTechScoreHistory;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouTechScoreLine;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSccSouTechScoreHistoryMapper;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSccSouTechScoreHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class IExtSccSouTechScoreHistoryServiceImpl extends ServiceImpl<ExtSccSouTechScoreHistoryMapper, ExtSccSouTechScoreHistory> implements IExtSccSouTechScoreHistoryService {
    @Override
    public List<ExtSccSouTechScoreHistory> saveTechScoreHist(List<ExtSouTechScoreLine> techScoreLineList) {
        if(CollectionUtils.isEmpty(techScoreLineList)) {
            return new ArrayList<>();
        }
        List<ExtSccSouTechScoreHistory> historyList = new ArrayList<>();
        Date submiteDate = new Date();
        String historyNum = DateUtil.format(submiteDate, DateUtil.DATE_FORMAT_14);
        techScoreLineList.stream().forEach(t -> {
            ExtSccSouTechScoreHistory history = new ExtSccSouTechScoreHistory();
            BeanCopyUtil.copyProperties(history, t);
            history.setTechScoreHistoryId(IdGenrator.generate());
            history.setSubmiteDate(submiteDate);
            history.setHistoryNum(historyNum);

            historyList.add(history);
        });
        this.saveBatch(historyList);
        return historyList;
    }
}
