package com.midea.cloud.srm.base.material.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.component.handler.AutoMetaObjContext;
import com.midea.cloud.srm.base.material.mapper.MtPartIntermediaryMapper;
import com.midea.cloud.srm.base.material.service.MtPartIntermediaryService;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.model.base.entity.MtPartIntermediary;
import com.midea.cloud.srm.model.pj.changchengapi.material.MaterialParam;
import com.midea.cloud.srm.model.pj.changchengapi.material.MaterialResultDto;
import com.midea.cloud.srm.model.pj.changchengapi.yangguan.ResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author huangbf3
 * 非生产物料接口
 */
@Service
@Slf4j
public class MtPartIntermediaryServiceImpl extends ServiceImpl<MtPartIntermediaryMapper, MtPartIntermediary> implements MtPartIntermediaryService {

    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    /**
     * 拉取物料数据
     * @param materialParam 请求物料接口参数
     */
    @Override
    public void pullData(MaterialParam materialParam) {
        log.info("materialParam:"+ JSONObject.toJSONString(materialParam));
        ResultDTO<MaterialResultDto> resultDTO = pjProjectExtClient.materialPage(materialParam);
        log.info("resultDTO:"+ JSONObject.toJSONString(resultDTO));
        Integer successCode = 200;
        if(resultDTO.getCode().equals(successCode)){
            MaterialResultDto materialResultDto = resultDTO.getResult();
            saveOrUpdateFromApi(materialResultDto);
            //存在其他页数据继续拉取
            if(materialResultDto.getTotal()>materialParam.getSize()*materialParam.getPage()){
                materialParam.setPage(materialParam.getPage()+1);
                pullData(materialParam);
            }
        }
    }

    /**
     * 新增或更新物料数据
     * @param materialResultDto 接口返回的物料数据
     */
    @Override
    public void saveOrUpdateFromApi(MaterialResultDto materialResultDto) {
        List<MtPartIntermediary> mtPartIntermediaries = getMtPartIntermediaryList(materialResultDto);

        log.info("mtPartIntermediaries:"+ JSONObject.toJSONString(mtPartIntermediaries));
        if(mtPartIntermediaries.size()==0){
            return;
        }
        List<String> itemCodes = mtPartIntermediaries.stream().map(MtPartIntermediary::getItemCode).collect(Collectors.toList());

        List<MtPartIntermediary> dbMtPartIntermediaries = this.lambdaQuery().in(MtPartIntermediary::getItemCode,itemCodes).list();
        log.info("dbMtPartIntermediaries:"+ JSONObject.toJSONString(dbMtPartIntermediaries));

        Map<String,MtPartIntermediary> mtPartIntermediaryMap = dbMtPartIntermediaries.stream().collect(Collectors.toMap(MtPartIntermediary::getItemCode,t->t));

        List<MtPartIntermediary> addMtPartIntermediaries = new ArrayList<>();
        List<MtPartIntermediary> updateMtPartIntermediaries = new ArrayList<>();

        for(MtPartIntermediary mtPartIntermediary:mtPartIntermediaries){
            if(mtPartIntermediaryMap.containsKey(mtPartIntermediary.getItemCode())){
                MtPartIntermediary dbMtPartIntermediary = mtPartIntermediaryMap.get(mtPartIntermediary.getItemCode());
                dbMtPartIntermediary.setCategoryCode(mtPartIntermediary.getCategoryCode());
                dbMtPartIntermediary.setCategoryName(mtPartIntermediary.getCategoryName());
                dbMtPartIntermediary.setItemCode(mtPartIntermediary.getItemCode());
                dbMtPartIntermediary.setItemName(mtPartIntermediary.getItemName());
                dbMtPartIntermediary.setMaterialDescription(mtPartIntermediary.getMaterialDescription());
                dbMtPartIntermediary.setUnit(mtPartIntermediary.getUnit());
                dbMtPartIntermediary.setApplyPerson(mtPartIntermediary.getApplyPerson());
                dbMtPartIntermediary.setApplyTime(DateUtil.format(new Date()));
                dbMtPartIntermediary.setStatus(mtPartIntermediary.getStatus());
                dbMtPartIntermediary.setSpecification(mtPartIntermediary.getMaterialDescription());
                dbMtPartIntermediary.setIsSync("0");
                dbMtPartIntermediary.setLogo(mtPartIntermediary.getLogo());

                dbMtPartIntermediary.setCreatedId(-1L);
                dbMtPartIntermediary.setCreatedBy(mtPartIntermediary.getCreatedBy());
                dbMtPartIntermediary.setCreationDate(mtPartIntermediary.getCreationDate());
                dbMtPartIntermediary.setCreatedFullName(mtPartIntermediary.getCreatedFullName());
                dbMtPartIntermediary.setLastUpdatedId(-1L);
                dbMtPartIntermediary.setLastUpdatedBy(mtPartIntermediary.getLastUpdatedBy());
                dbMtPartIntermediary.setLastUpdateDate(mtPartIntermediary.getLastUpdateDate());
                dbMtPartIntermediary.setLastUpdatedFullName(mtPartIntermediary.getLastUpdatedFullName());
                updateMtPartIntermediaries.add(dbMtPartIntermediary);
            }else{
                mtPartIntermediary.setCreatedId(-1L);
                mtPartIntermediary.setLastUpdatedId(-1L);
                mtPartIntermediary.setId(IdGenrator.generate());
                addMtPartIntermediaries.add(mtPartIntermediary);
            }
        }
        log.info("updateMtPartIntermediaries:"+ JSONObject.toJSONString(updateMtPartIntermediaries));
        log.info("addMtPartIntermediaries:"+ JSONObject.toJSONString(addMtPartIntermediaries));
        try{
            AutoMetaObjContext.noOp(AutoMetaObjContext.MODE.FOREVERY);
            this.saveBatch(addMtPartIntermediaries);
            this.updateBatchById(updateMtPartIntermediaries);
        } finally {
            AutoMetaObjContext.manullyRemove();
        }

    }

    /**
     * 接口物料数据转数据库数据格式
     * @param materialResultDto 接口返回的物料数据
     * @return 数据库表对象数据格式列表数据
     */
    public List<MtPartIntermediary> getMtPartIntermediaryList(MaterialResultDto materialResultDto){

        List<MtPartIntermediary> mtPartIntermediaries = new ArrayList<>();
        if(materialResultDto.getRows()!=null&&materialResultDto.getRows().size()>0){
            for(MaterialResultDto.RowItem rowItem:materialResultDto.getRows()){
                MtPartIntermediary mtPartIntermediary = new MtPartIntermediary();
                String[] materialCategoryCodeArr = rowItem.getMaterialCategoryCode().split("/");
                String[] materialCategoryNameArr = rowItem.getMaterialCategoryName().split("/");
                mtPartIntermediary.setCategoryCode(materialCategoryCodeArr[materialCategoryCodeArr.length-1]);
                mtPartIntermediary.setCategoryName(materialCategoryNameArr[materialCategoryNameArr.length-1]);
                mtPartIntermediary.setItemCode(rowItem.getMaterialCode());
                mtPartIntermediary.setItemName(rowItem.getMaterialName());
                mtPartIntermediary.setMaterialDescription(rowItem.getMaterialDescribe());
                mtPartIntermediary.setSpecification(rowItem.getMaterialDescribe());
                mtPartIntermediary.setUnit(rowItem.getMeasurementName());
                mtPartIntermediary.setApplyPerson(rowItem.getUpdateUserCode()+"-"+rowItem.getUpdateUserName());
                mtPartIntermediary.setApplyTime(DateUtil.format(new Date()));
                if(new Integer(1).equals(rowItem.getMaterialStatus())){
                    mtPartIntermediary.setStatus("停用");
                }else{
                    mtPartIntermediary.setStatus("启用");
                }
                mtPartIntermediary.setIsSync("0");
                mtPartIntermediary.setLogo("MDM");
                String createBy = replaceNullStr(rowItem.getCreateUserName())+"("+replaceNullStr(rowItem.getCreateUserCode())+")";
                String updateBy = replaceNullStr(rowItem.getUpdateUserName())+"("+replaceNullStr(rowItem.getUpdateUserCode())+")";
                createBy = createBy.replace("()","");
                updateBy = updateBy.replace("()","");

                mtPartIntermediary.setCreatedBy(createBy);
                mtPartIntermediary.setCreatedFullName(rowItem.getCreateUserName());
                mtPartIntermediary.setCreationDate(rowItem.getCreateTime());
                mtPartIntermediary.setLastUpdatedBy(updateBy);
                mtPartIntermediary.setLastUpdatedFullName(rowItem.getUpdateUserName());
                mtPartIntermediary.setLastUpdateDate(rowItem.getUpdateTime());

                mtPartIntermediaries.add(mtPartIntermediary);
            }
        }
        return mtPartIntermediaries;
    }

    private String replaceNullStr(String str){
        if(StringUtils.isEmpty(str)) {
            return "";
        } else {
            return str;
        }

    }
}
