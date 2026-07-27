package com.midea.cloud.srm.sou.abnormalregs.service;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/21
 */

import com.midea.cloud.srm.model.sou.abnormalregs.vo.SccNpmSouAbnormalRegVo;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Description: for srm
 *异常登记业务层
 * @author srm
 * @date 2024-08-08
 */
public interface SccNpmSouAbnormalRegService {
    /**
     * UpdateAbandon
     * @param id
     * @param instruction
     */
    public void UpdateAbandon(@RequestParam("id")Long id, @RequestParam("instruction")String instruction);

    /**
     * List
     * @return
     */
    public List<SccNpmSouAbnormalRegVo>List();
}
