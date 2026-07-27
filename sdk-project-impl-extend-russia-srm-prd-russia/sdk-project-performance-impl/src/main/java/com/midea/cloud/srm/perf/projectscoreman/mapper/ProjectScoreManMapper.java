package com.midea.cloud.srm.perf.projectscoreman.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.perf.projectscoreman.entity.ProjectScoreMan;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
public interface ProjectScoreManMapper extends BaseMapper<ProjectScoreMan> {
    /**
     * 备注
     * @param list 参数
     * @return 返回
     */
    List<ProjectScoreMan> listByGroupList(@Param("list") List<ProjectScoreMan> list);

}
