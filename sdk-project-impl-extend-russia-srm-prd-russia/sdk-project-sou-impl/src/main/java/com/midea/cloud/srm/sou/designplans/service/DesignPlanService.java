package com.midea.cloud.srm.sou.designplans.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.sou.designplans.entity.SccSouChDemandYearData;
import com.midea.cloud.srm.model.sou.designplans.entity.SccSouChDesignPlan;
import com.midea.cloud.srm.model.sou.designplans.excel.ImportExcelReqInfoDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiSouInitDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author ex_liuxy46
 */
public interface DesignPlanService extends IService<SccSouChDesignPlan> {

    //List<SccSouChDemandYearData> importReqInfo(MultipartFile file, Long designId, String suffix) throws IOException;

    /**
     * 导入
     * @param file 附件
     * @param designId 提报策划方案id
     * @return 返回
     */
    List<ImportExcelReqInfoDto> importReqInfo(List<ImportExcelReqInfoDto> file, Long designId);

    /**
     * 创建集采询比价
     * @param designId
     * @return
     */
    ApiSouInitDTO createPurInq(long designId);

}
