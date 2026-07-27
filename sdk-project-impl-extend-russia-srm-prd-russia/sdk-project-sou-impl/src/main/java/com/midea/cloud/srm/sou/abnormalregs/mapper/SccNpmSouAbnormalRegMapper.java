package com.midea.cloud.srm.sou.abnormalregs.mapper;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/21
 */

import com.midea.cloud.srm.model.sou.abnormalregs.vo.SccNpmSouAbnormalRegVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Description: for srm
 *异常登记持久层
 * @author srm
 * @date 2024-08-08
 */
@Mapper
public interface SccNpmSouAbnormalRegMapper {
    /**
     * UpdateAbandon
     * @param id
     * @param instruction
     */
    @Update("update scc_npm_sou_abnormal_reg set ABANDON_INSTRUCTION=#{instruction} , REG_STATUS= 'ABANDONED' where REG_ID=#{id}")
    public void UpdateAbandon(@RequestParam("id")Long id, @RequestParam("instruction")String instruction);

    /**
     * List
     * @return
     */
    @Select("select * from scc_npm_sou_abnormal_reg")
    public List<SccNpmSouAbnormalRegVo> List();
}
