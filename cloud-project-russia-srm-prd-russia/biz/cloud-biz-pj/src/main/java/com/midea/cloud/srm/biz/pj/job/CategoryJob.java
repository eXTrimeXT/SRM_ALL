package com.midea.cloud.srm.biz.pj.job;

import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.quartz.bind.Job;
import com.midea.cloud.quartz.handler.ExecuteableJob;
import com.midea.cloud.srm.biz.pj.base.category.service.ICategoryService;
import com.midea.cloud.srm.model.pj.changchengapi.dto.CategoryApiParamDTO;
import com.midea.cloud.srm.model.pj.changchengapi.dto.CategoryDTO;
import com.mideacloud.common.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

/**
 * @author huangbf3
 * 品类定时任务
 */
@Job("CategoryJob")
@Slf4j
public class CategoryJob implements ExecuteableJob {
    @Autowired
    private ICategoryService iCategoryService;

    private static final String UPDATE_TIME = "updateTime";

    @Override
    public BaseResult executeJob(Map<String, String> params) {
        CategoryApiParamDTO categoryApiParamDTO = new CategoryApiParamDTO();
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryApiParamDTO.setCategoryDTO(categoryDTO);
        categoryDTO.setSize(1000);
        categoryDTO.setPage(1);
        if(params.containsKey(UPDATE_TIME)){
            categoryDTO.setUpdateTime(params.get("updateTime"));
        }else{
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -2);
            categoryDTO.setUpdateTime(DateUtil.date2Str(calendar.getTime(),"yyyy-MM-dd HH:mm:ss"));
        }
        int length = 4;
        String serialNum = Objects.toString(IdGenrator.generate());
        for(int i=0;i<=length;i++){
            categoryDTO.setCategoryLevel(i);
            categoryDTO.setPage(1);
            categoryDTO.setSize(500);
            iCategoryService.saveOrUpdateBatch(categoryApiParamDTO, serialNum);
        }
        return BaseResult.buildSuccess("品类定时任务-执行成功！");
    }

}
