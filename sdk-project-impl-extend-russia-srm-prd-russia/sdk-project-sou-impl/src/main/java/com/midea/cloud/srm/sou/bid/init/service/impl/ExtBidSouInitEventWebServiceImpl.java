package com.midea.cloud.srm.sou.bid.init.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.fastjson.JSON;
import com.midea.cloud.common.enums.ImportStatus;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.listener.AnalysisEventListenerImpl;
import com.midea.cloud.common.utils.EasyExcelUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouItemDto;
import com.midea.cloud.srm.sou.bid.init.service.ExtBidSouInitEventWebService;
import com.midea.cloud.srm.sou.bid.init.service.ExtBidSouInitQueryWebService;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouItemMapper;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouPriceTemplateDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouItem;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouItemService;
import com.midea.cloud.srm.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.sou.sourcing.spi.init.editpricetemplates.ApiExtSouPriceTemplateEditHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.editsouitems.ApiExtSouItemEditHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.editsouitems.ExtSouItemEditPO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Slf4j
@Service
public class ExtBidSouInitEventWebServiceImpl implements ExtBidSouInitEventWebService {

    @Autowired
    private ExtBidSouInitQueryWebService extBidSouInitQueryWebService;

    @Autowired
    private IExtSouItemService itemService;

    @Autowired
    private FileCenterClient fileCenterClient;

    @Override
    public Map<String, Object> importPriceExcel(Long projectId, MultipartFile file, Fileupload fileupload, String souType) throws Exception {
        // 检查参数
        EasyExcelUtil.checkParam(file, fileupload);

        ApiExtSouPriceTemplateDto templateDto = extBidSouInitQueryWebService.listPriceTemplate(projectId);
        Map<String, String> colMap = new HashMap<>(50);
        List<List<String>> headList = new ArrayList<>();
        colMap.put("包名", "extPackageName");
        headList.add(Arrays.asList("包名"));
        templateDto.getBuyerAsSelectedList().stream().forEach(t -> {
            colMap.put(t.getColumnName(), t.getColumnCode());
            List<String> titleList = new ArrayList<>();
            titleList.add(t.getColumnName());
            headList.add(titleList);
        });
        colMap.put(ApiExtSouItemDto.ERROR_MSG, ApiExtSouItemDto.ERROR_CHECK);
        List<String> titleList = new ArrayList<>();
        titleList.add(ApiExtSouItemDto.ERROR_MSG);
        headList.add(titleList);

        // 获取输入流
        InputStream inputStream = file.getInputStream();
        // 数据收集器
        ExtAnalysisEventListenerImpl<Map<Integer, Object>> listener = new ExtAnalysisEventListenerImpl<>();
        ExcelReader excelReader = EasyExcel.read(inputStream, listener).build();

        // 第一个sheet读取类型
        ReadSheet readSheet = EasyExcel.readSheet(0).head(headList).build();
        // 开始读取第一个sheet
        excelReader.read(readSheet);
        List<Map<Integer, Object>> list = new ArrayList<>();
        list = listener.getDatas();

        Map<Integer, String> headMap = listener.getHeadMap();

        //校验模板
        checkImportTemplateValid(headList, headMap);

        List<Map<String, Object>> dataList = new ArrayList<>();
        list.stream().forEach(item -> {
            Map<String, Object> data = new HashMap<>(50);
            Boolean isBank = true;
            for (Integer key : item.keySet()) {
                if(colMap.containsKey(headMap.get(key))) {
                    if(!Objects.isNull(item.get(key)) && StringUtils.isNotBlank(MapUtils.getString(item, key))) {
                        isBank = false;
                    }
                    data.put(colMap.get(headMap.get(key)), item.get(key));
                }
            }
            if(!isBank) {
                dataList.add(data);
            }
        });

        if(CollectionUtils.isEmpty(dataList)) {
            throw new BaseException("导入数据为空（或全部是空行），请检查导入文件无误后再进行操作！");
        }

        ApiExtSouItemDto param = new ApiExtSouItemDto();
        param.setProjectId(projectId);
        param.setImportList(dataList);
        param.setTempSave(true);

        Map<String, Object> errorFileupload = getStringObjectMap(file, fileupload, souType, colMap, headList, headMap, param);
        if (errorFileupload != null) {
            return errorFileupload;
        }

        return ImportStatus.importSuccess();
    }

    /**
     * 行业包出来
     * @param file 参数
     * @param fileupload 参数
     * @param souType 参数
     * @param colMap 参数
     * @param headList 参数
     * @param headMap 参数
     * @param param 参数
     * @return 返回
     * @throws Exception
     */
    @Nullable
    private Map<String, Object> getStringObjectMap(MultipartFile file, Fileupload fileupload, String souType, Map<String, String> colMap, List<List<String>> headList, Map<Integer, String> headMap, ApiExtSouItemDto param) throws Exception {
        // 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiExtSouItemEditHandler.class).doHandlerBeforeEditProject(param, souType);

        //行业包转换处理
        ExtSouItemEditPO po = SouActiveBeanUtils.getActiveBean(souType, ApiExtSouItemEditHandler.class).formatValidateAndConvertForImport(param, souType);

        if (param.getImportCheck().get()) {
            itemService.saveOrUpdateBatch(po.getItemList());
        } else {
            List<List<Object>> errorDataList = new ArrayList<>();
            List<Integer> keyList = headMap.keySet().stream().sorted(Comparator.comparingInt(s->s)).collect(Collectors.toList());
            param.getImportList().stream().forEach(data -> {
                List<Object> errorData = new ArrayList<>();
                for(int key : keyList) {
                    errorData.add(data.get(colMap.get(headMap.get(key))));
                }
                errorData.add(data.get(colMap.get(ApiExtSouItemDto.ERROR_MSG)));
                errorDataList.add(errorData);
            });

            Fileupload errorFileupload = uploadFile(fileupload, file, headList, errorDataList);
            return ImportStatus.importError(errorFileupload.getFileuploadId(), errorFileupload.getFileSourceName());

        }
        return null;
    }

    /**
     * 校验导入模板的合法性
     * @param headList
     * @param headMap
     */
    private void checkImportTemplateValid(List<List<String>> headList, Map<Integer, String> headMap) {
        //标题
        List<String> titleList = new ArrayList<>();
        headList.stream().forEach(list -> {
            String title = list.get(0);
            if(!ApiExtSouItemDto.ERROR_MSG.equals(title)) {
                titleList.add(title);
            }
        });

        //导入标题
        List<String> importTitleList = headMap.values().stream().filter(s -> !ApiExtSouItemDto.ERROR_MSG.equals(s)).collect(Collectors.toList());

        titleList.removeAll(importTitleList);

        if(CollectionUtils.isNotEmpty(titleList)) {
            throw new BaseException("导入文件与导入模板格式不匹配，请重新下载模板维护数据再进行导入操作！");
        }

    }

    @Override
    public Fileupload uploadFile(Fileupload fileupload, MultipartFile file, List<List<String>> headList, List<List<Object>> errorList) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream).head(headList).sheet(0).sheetName("sheetName").doWrite(errorList);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        file = new MockMultipartFile(file.getName(), file.getOriginalFilename(), file.getContentType(), inputStream);
        fileupload.setUploadType("DEF");
        return fileCenterClient.feignClientUpload(file, fileupload.getSourceType(), fileupload.getUploadType()
                , fileupload.getFileModular(), fileupload.getFileFunction()
                , fileupload.getFileType());

    }

}
