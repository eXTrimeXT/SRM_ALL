package com.midea.cloud.srm.model.pj.sou.score.vo;

import com.midea.cloud.srm.model.pj.sou.score.entity.SouScoreManage;
import com.midea.cloud.srm.model.pj.sou.score.entity.SouSupplierScoreManage;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("评分管理")
public class SouScoreManageVO extends SouScoreManage {

    /** 供应商管理列表 */
    private List<SouSupplierScoreManage> souSupplierScoreManages;

    public static SouScoreManageVO init(SouScoreManage s) {
        SouScoreManageVO rsp = new SouScoreManageVO();
        BeanUtils.copyProperties(s,rsp);
        return rsp;
    }
}
