package com.midea.cloud.srm.sou.sourcing.init.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.midea.cloud.component.mphelper.mapper.CustomMapper;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouTechScoreHeadDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouTechScoreHead;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @description scc_sou_tech_score_head
 * @author panmq
 * @date 2023-10-09
 */
@Mapper
public interface ExtSouTechScoreHeadMapper extends CustomMapper<ExtSouTechScoreHead> {
    /**
     * 备注
     * @param queryWrapper 参数
     * @return 返回
     */
    @Select("SELECT p.project_id,p.sou_no, p.ext_project_no, p.sou_name, p.ext_org_ou_name, p.ext_org_bu_name,p.project_status,\n" +
            "g.full_name, g.user_name, g.user_id, h.tech_score_head_id, h.group_id, h.score_status, h.ext_reject_reason, h.CREATION_DATE FROM scc_sou_project p\n" +
            "JOIN scc_sou_tech_score_head h ON h.project_id = p.project_id\n" +
            "JOIN scc_sou_group g ON g.group_id = h.group_id AND g.ext_eva_flag='Y' and g.score_auth = 'SOU_TECH' where ${ew.sqlSegment} order by p.CREATION_DATE DESC")
    @ResultType(value = ExtSouTechScoreHeadDto.class)
    List<ExtSouTechScoreHeadDto> listExtSouTechScoreHead(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);
}

