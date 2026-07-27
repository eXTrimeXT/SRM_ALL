package com.midea.cloud.srm.model.pj.sou.score.vo;

import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.pj.sou.score.entity.SouSupplierScoreManage;
import com.midea.cloud.srm.model.pj.sou.score.entity.SouSupplierScoreManageRecord;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("供应商评分管理")
public class SouSupplierScoreManageVO extends SouSupplierScoreManage {

    /** 供应商评分记录 */
    private List<SouSupplierScoreManageRecord> souSupplierScoreManageRecordList;

    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("评分人上传附件")
    private List<SceneFile> sceneFiles = new ArrayList<>();

    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("供应商附件")
    private List<SouOrderFile> souOrderFiles = new ArrayList();



    public static SouSupplierScoreManageVO init(SouSupplierScoreManage souSupplierScoreManage) {
        SouSupplierScoreManageVO rsp = new SouSupplierScoreManageVO();
        BeanUtils.copyProperties(souSupplierScoreManage, rsp);
        return rsp;
    }
}
