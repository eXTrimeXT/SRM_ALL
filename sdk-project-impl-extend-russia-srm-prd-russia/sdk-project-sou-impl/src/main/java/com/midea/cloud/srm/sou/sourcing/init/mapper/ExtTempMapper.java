package com.midea.cloud.srm.sou.sourcing.init.mapper;

import com.midea.cloud.srm.model.perf.projectscore.entity.ProjectScoreHeader;
import com.midea.cloud.srm.sou.req.vo.ProjectVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/7/18
 */
@Mapper
public interface ExtTempMapper {
    /**
     * list
     * @param list
     * @return
     */
    List<ProjectScoreHeader>list(List<String>list);

    /**
     * listProject
     * @param list
     * @return
     */
    List<ProjectVO>listProject(List<String>list);

    /**
     * 查询项目化绩效
     * @param params
     * @return
     */
    List<ProjectVO> queryScoreInfo(Map<String, Object> params);

}
