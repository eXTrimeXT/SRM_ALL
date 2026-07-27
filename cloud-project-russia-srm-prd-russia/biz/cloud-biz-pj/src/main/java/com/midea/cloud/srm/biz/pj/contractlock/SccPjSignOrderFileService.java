package com.midea.cloud.srm.biz.pj.contractlock;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.pj.sign.entity.SccPjSignOrderFile;
import io.swagger.annotations.ApiOperation;


import java.util.Collection;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-17
 */
public interface SccPjSignOrderFileService extends IService<SccPjSignOrderFile>{

    /**
     * save
     * @param sccPjSignOrderFile
     * @return
     */
    @Override
    @ApiOperation(value = "保存签署业务附件关系表")
    boolean save(SccPjSignOrderFile sccPjSignOrderFile);

    /**
     * saveBatch
     * @param entityList
     * @return
     */
    @Override
    @ApiOperation(value = "批量保存签署业务附件关系表")
    boolean saveBatch(Collection<SccPjSignOrderFile> entityList);


}
