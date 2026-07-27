package com.midea.cloud.srm.sou.sourcing.init.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.midea.cloud.component.mphelper.mapper.CustomMapper;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 备注
 * @author huangbf3
 */
@Mapper
public interface ExtSouProjectMapper extends BaseMapper<ExtSouProject> {
    /**
     * 备注
     * @param queryWrapper 参数
     * @return 返回
     */
    @ResultType(ExtSouProject.class)
    @Select("SELECT s.* FROM scc_sou_project s\n" +
            "JOIN scc_npm_sou_plan p ON p.PROJECT_ID = s.PROJECT_ID AND ifnull(p.TECH_END_FIX_TIME, p.TECH_END_TIME) <= NOW() \n" +
            "WHERE ${ew.sqlSegment}")
    public List<ExtSouProject> techBidCount(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);

    /**
     * 备注
     * @param queryWrapper 参数
     * @return 返回
     */
    @ResultType(ExtSouProject.class)
    @Select("SELECT s.* FROM scc_sou_project s\n" +
            "JOIN scc_sou_round r ON r.PROJECT_ID = s.PROJECT_ID AND r.`ROUND` = s.CURRENT_ROUND AND r.ORDER_END_TIME <= NOW() WHERE ${ew.sqlSegment}")
    public List<ExtSouProject> busBidCount(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);

    /**
     * 统计供应商某个品类的投标次数和中标次数
     * @param param
     * @return
     */
    public List<Map<String, Object>> statisticalBidTimes(Map<String, Object> param);
}
