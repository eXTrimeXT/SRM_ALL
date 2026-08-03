package com.midea.cloud.srm.base.material.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.base.entity.MtPartIntermediary;
import com.midea.cloud.srm.model.pj.changchengapi.material.MaterialParam;
import com.midea.cloud.srm.model.pj.changchengapi.material.MaterialResultDto;


/**
 * 非生产物料接口
 * @author huangbf3
 */
public interface MtPartIntermediaryService extends IService<MtPartIntermediary> {
    /**
     * 拉取物料
     * @param materialParam 请求物料接口参数
     */
    void pullData(MaterialParam materialParam);

    /**
     * 从获取的物料接口数据新增或更新入库
     * @param materialResultDto 接口返回的物料数据
     */
    void saveOrUpdateFromApi(MaterialResultDto materialResultDto);
}
