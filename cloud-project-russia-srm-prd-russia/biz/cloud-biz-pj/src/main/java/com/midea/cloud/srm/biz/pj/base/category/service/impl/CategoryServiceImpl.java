package com.midea.cloud.srm.biz.pj.base.category.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.biz.pj.base.category.service.ICategoryService;
import com.midea.cloud.srm.biz.pj.utils.MqlType;
import com.midea.cloud.srm.feign.pj.base.BaseExtClient;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.common.enums.ProcessStatusEnum;
import com.midea.cloud.srm.model.pj.base.category.entity.PjPurchaseCategory;
import com.midea.cloud.srm.model.pj.base.category.entity.SccPjCategorySyncTempDto;
import com.midea.cloud.srm.model.pj.changchengapi.dto.CategoryApiParamDTO;
import com.midea.cloud.srm.model.pj.changchengapi.dto.CategoryDTO;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenQueryWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import com.mideacloud.common.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author huangbf3
 */
@Slf4j
@Service
public class CategoryServiceImpl implements ICategoryService {

    @Value("${gwm.appkey}")
    private String appKey;
    @Value("${gwm.secret}")
    private String secret;
    @ApiModelProperty("源系统应用")
    @Value("${gwm.bpm.src-system}")
    private String srcSystem;

    @ApiModelProperty("创建流程")
    @Value("${gwm.url.category}")
    private String categoryUrl;


    @Autowired
    BaseExtClient baseExtClient;

    @Autowired
    QlOpenClient qlOpenClient;

    @Autowired
    private QlService qlService;

    private Integer NUM_TWO_FIVE_SIX = 256;

    @ApiOperation(value = "分页查询物料品类")
    @Override
    public List<CategoryDTO> findCategoryFromApi(CategoryApiParamDTO categoryApiParamDTO, String serialNum, Long processGroupId) {
        String url = categoryUrl;

        Map<String,String> headers = new HashMap<>(50);
        headers.put("SRC-SYSTEM",srcSystem);
        categoryApiParamDTO.getCategoryDTO().setSize(categoryApiParamDTO.getCategoryDTO().getSize());
        categoryApiParamDTO.getCategoryDTO().setPage(categoryApiParamDTO.getCategoryDTO().getPage());

        OpenClient openClient = new OpenClient(appKey,secret);
        String result = openClient.sendHttpPost(url, JSONObject.toJSONString(categoryApiParamDTO.getCategoryDTO()),"application/json",headers);
        JSONObject resultJsn = JSONObject.parseObject(result);

        log.info(resultJsn.getInteger("code")+"");

        List<CategoryDTO> list = new ArrayList<>();
        String resultText = "result";
        String totalText = "total";
        String codeText = "code";
        int successValue = 200;
        if(resultJsn.getInteger(codeText).equals(successValue)){
            JSONArray jsonArray = resultJsn.getJSONObject(resultText).getJSONArray("rows");
            //保存接口临时表
            saveCategoryTemp(jsonArray, serialNum, processGroupId);
            list = JSONObject.parseArray(jsonArray.toJSONString(), CategoryDTO.class) ;
        }
        if(resultJsn.getJSONObject(resultText)!=null&&resultJsn.getJSONObject(resultText).getInteger(totalText)>
                (categoryApiParamDTO.getCategoryDTO().getPage()*categoryApiParamDTO.getCategoryDTO().getSize())){
            categoryApiParamDTO.getCategoryDTO().setPage(categoryApiParamDTO.getCategoryDTO().getPage()+1);
            categoryApiParamDTO.setHaveNextPage(true);
        }else{
            categoryApiParamDTO.setHaveNextPage(false);
        }


        log.info("请求参数:"+JSONObject.toJSONString(categoryApiParamDTO.getCategoryDTO()));
        log.info("返回结果:"+result);

        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateBatch(CategoryApiParamDTO categoryApiParamDTO, String serialNum) {

        Long processGroupId = IdGenrator.generate();

        List<CategoryDTO> categoryDtos = findCategoryFromApi(categoryApiParamDTO, serialNum, processGroupId);

        List<PjPurchaseCategory> purchaseCategories = changeFromApiData(categoryDtos, serialNum, processGroupId);

        try {
            saveOrUpdateBatch(purchaseCategories);
        } catch (Exception e) {
            log.error("saveOrUpdateBatch purchaseCategories Exception", e);

            String msg = StringUtils.joinWith(": ", "保存接口异常", e.getMessage());
            if(Integer.compare(msg.getBytes().length, NUM_TWO_FIVE_SIX) == 1) {
                msg = msg.substring(0, 250);
            }

            updateTempInfo(ProcessStatusEnum.ERROR.getCode(), msg, serialNum, processGroupId);
            throw new BaseException(e.getMessage());
        }

        qlService.updateByWrapper(QlWrappers.update(MqlType.SCC_PJ_CATEGORY_SYNC_TEMP)
                .set(SccPjCategorySyncTempDto::getProcessStatus, ProcessStatusEnum.COMPLETED.getCode())
                .set(SccPjCategorySyncTempDto::getProcessMessage, ProcessStatusEnum.COMPLETED.getName())
                .eq(SccPjCategorySyncTempDto::getProcessSerialNum, serialNum)
                .eq(SccPjCategorySyncTempDto::getProcessGroupId, processGroupId)
                .eq(SccPjCategorySyncTempDto::getProcessStatus, ProcessStatusEnum.PENDING.getCode()));

        if(categoryApiParamDTO.getHaveNextPage()){
            saveOrUpdateBatch(categoryApiParamDTO, serialNum);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void updateTempInfo(String processStatus, String msg, String processSerialNum, Long processGroupId) {
        qlService.updateByWrapper(QlWrappers.update(MqlType.SCC_PJ_CATEGORY_SYNC_TEMP)
                .set(SccPjCategorySyncTempDto::getProcessStatus, processStatus)
                .set(SccPjCategorySyncTempDto::getProcessMessage, msg)
                .eq(SccPjCategorySyncTempDto::getProcessSerialNum, processSerialNum)
                .eq(SccPjCategorySyncTempDto::getProcessGroupId, processGroupId)
                .eq(SccPjCategorySyncTempDto::getProcessStatus, ProcessStatusEnum.PENDING.getCode()));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void saveCategoryTemp(JSONArray categoryRows, String serialNum, Long processGroupId) {
        if(!Objects.isNull(categoryRows) && CollectionUtils.isNotEmpty(categoryRows)) {
            List<SccPjCategorySyncTempDto> tempDtos = JSONArray.parseArray(categoryRows.toJSONString(), SccPjCategorySyncTempDto.class);
            tempDtos.stream().forEach(data -> {
                data.setCategorySyncId(IdGenrator.generate());
                data.setProcessSerialNum(serialNum);
                data.setProcessGroupId(processGroupId);
                data.setProcessStatus(ProcessStatusEnum.PENDING.getCode());
                data.setProcessDate(new Date());
            });

            qlService.create(MqlType.SCC_PJ_CATEGORY_SYNC_TEMP, tempDtos);
        }
    }

    private void saveOrUpdateBatch(List<PjPurchaseCategory> purchaseCategories) {
        if(purchaseCategories==null||purchaseCategories.size()==0){
            return;
        }
        List<String> codes = purchaseCategories.stream().map(PjPurchaseCategory::getCategoryCode).collect(Collectors.toList());
        List<String> parentCodes = purchaseCategories.stream().filter(item->Objects.nonNull(item.getParentCode())).map(PjPurchaseCategory::getParentCode).collect(Collectors.toList());
        codes.addAll(parentCodes);


        QlOpenQueryWrapper wrapper = QlOpenWrappers.query("PurchaseCategory");
        wrapper.in("categoryCode",codes);

        List<PurchaseCategory> addList = new ArrayList();
        for(PjPurchaseCategory pjPurchaseCategory:purchaseCategories){
            PurchaseCategory purchaseCategory = new PurchaseCategory();
            BeanUtils.copyProperties(pjPurchaseCategory,purchaseCategory);
            purchaseCategory.setSupplierCountLimit(100000);
            Integer lastLevelFlagLevel = 4;
            purchaseCategory.setLastLevelFlag(lastLevelFlagLevel.compareTo(purchaseCategory.getLevel())==0?Enable.Y:Enable.N);
            addList.add(purchaseCategory);
        }



        baseExtClient.batchSaveOrUpdate(addList);



    }

    /**
     * 转换接口返回的数据格式
     * @param categoryDtos
     * @return
     */
    private List<PjPurchaseCategory> changeFromApiData(List<CategoryDTO> categoryDtos, String serialNum, Long processGroupId) {
        if(categoryDtos==null&&categoryDtos.size()==0){
            return Arrays.asList();
        }
        QlOpenQueryWrapper qlOpenQueryWrapper = QlOpenWrappers.query("PurchaseCategory");
        qlOpenQueryWrapper.in("categoryCode",categoryDtos.stream().map(CategoryDTO::getCategoryCode).collect(Collectors.toList()));

        List<PjPurchaseCategory> dbCategorys = qlOpenClient.query(ContextPath.BASE,qlOpenQueryWrapper,PjPurchaseCategory.class);
        Map<String,PjPurchaseCategory> pjPurchaseCategoryMap = dbCategorys.stream().collect(Collectors.toMap(PjPurchaseCategory::getCategoryCode,t->t));

        List<PjPurchaseCategory> purchaseCategories = new ArrayList<>();
        List<String> errorCategoryCodeList = new ArrayList<>(16);

        QlOpenQueryWrapper wrapper = QlOpenWrappers.query("PurchaseCategory");
        wrapper.in("categoryCode",categoryDtos.stream().map(c -> c.getParentCategoryCode()).distinct().collect(Collectors.toList()));

        List<PjPurchaseCategory> pjPurchaseCategories = qlOpenClient.query(ContextPath.BASE,wrapper,PjPurchaseCategory.class);
        Map<String, List<PjPurchaseCategory>> parentPurchaseCategoryMap = pjPurchaseCategories.stream().collect(Collectors.groupingBy(c -> c.getCategoryCode()));


        for(CategoryDTO categoryDTO:categoryDtos){
            PjPurchaseCategory pjPurchaseCategory = new PjPurchaseCategory();

            if(pjPurchaseCategoryMap.containsKey(categoryDTO.getCategoryCode())){
                pjPurchaseCategory.setCategoryId(pjPurchaseCategoryMap.get(categoryDTO.getCategoryCode()).getCategoryId());
            }
            pjPurchaseCategory.setCategoryCode(categoryDTO.getCategoryCode());
            pjPurchaseCategory.setCategoryName(categoryDTO.getCategoryName());
            pjPurchaseCategory.setLevel(categoryDTO.getCategoryLevel());
            pjPurchaseCategory.setCreatedBy(categoryDTO.getCreateUserCode());
            pjPurchaseCategory.setCreatedFullName(categoryDTO.getCreateUserName());
            pjPurchaseCategory.setLastUpdatedBy(categoryDTO.getUpdateUserCode());
            pjPurchaseCategory.setLastUpdatedFullName(categoryDTO.getUpdateUserName());
            try {
                pjPurchaseCategory.setCreationDate(DateUtil.parseDate(categoryDTO.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));
                pjPurchaseCategory.setLastUpdateDate(DateUtil.parseDate(categoryDTO.getUpdateTime(),"yyyy-MM-dd HH:mm:ss"));
            } catch (ParseException e) {
                log.error("时间转换错误");
            }
            if(StringUtils.equals(categoryDTO.getParentCategoryCode(),"0")){
                pjPurchaseCategory.setParentId(-1L);
            }else{

                if(parentPurchaseCategoryMap.containsKey(categoryDTO.getParentCategoryCode())) {
                    PjPurchaseCategory parentCategory = parentPurchaseCategoryMap.get(categoryDTO.getParentCategoryCode()).get(0);
                    pjPurchaseCategory.setParentId(parentCategory.getCategoryId());
                    pjPurchaseCategory.setParentName(parentCategory.getCategoryName());
                } else {
                    errorCategoryCodeList.add(categoryDTO.getCategoryCode());
                    continue;
                }

            }
            pjPurchaseCategory.setParentCode(categoryDTO.getParentCategoryCode());
            if("1".equals(categoryDTO.getCategoryState()) || "true".equals(categoryDTO.getDeleteFlag())) {
                pjPurchaseCategory.setEnabled("N");
            } else {
                pjPurchaseCategory.setEnabled("Y");
            }
            purchaseCategories.add(pjPurchaseCategory);
        }

        if(CollectionUtils.isNotEmpty(errorCategoryCodeList)) {
            qlService.updateByWrapper(QlWrappers.update(MqlType.SCC_PJ_CATEGORY_SYNC_TEMP)
                    .set(SccPjCategorySyncTempDto::getProcessStatus, ProcessStatusEnum.ERROR.getCode())
                    .set(SccPjCategorySyncTempDto::getProcessMessage, "缺少父级品类")
                    .eq(SccPjCategorySyncTempDto::getProcessSerialNum, serialNum)
                    .eq(SccPjCategorySyncTempDto::getProcessGroupId, processGroupId)
                    .eq(SccPjCategorySyncTempDto::getProcessStatus, ProcessStatusEnum.PENDING.getCode())
                    .in(SccPjCategorySyncTempDto::getCategoryCode, errorCategoryCodeList));
        }

        return purchaseCategories;
    }


}
