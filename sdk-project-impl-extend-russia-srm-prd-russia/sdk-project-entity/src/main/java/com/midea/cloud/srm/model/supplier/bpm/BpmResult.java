package com.midea.cloud.srm.model.supplier.bpm;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.base.monitor.enums.YesOrNo;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 * 备注
 * @author huangbf3
 */
@Slf4j
public class BpmResult implements Serializable {

    /**
     * 生成bpm的json
     * @param processTitle
     * @param mainTable
     * @param mainTableData
     * @param processGroupId
     * @param appId
     * @param createOrgId
     * @param createUser
     * @param itemTable
     * @param itemData
     * @param itemFile
     * @return string json
     */
    public static JSONObject generateBpmJson(String processTitle, String mainTable, Object mainTableData,
                                             String processGroupId, String appId, String createOrgId, String createUser,
                                             List<String> itemTable, List<Object> itemData, Object itemFile) {
        return getJsonObject(processTitle, mainTable, mainTableData, processGroupId, appId, createOrgId, createUser, itemTable, itemData, itemFile, Lists.newArrayList());

    }

    /**
     * 生成bpm的json
     * @param processTitle
     * @param mainTable
     * @param mainTableData
     * @param processGroupId
     * @param appId
     * @param createOrgId
     * @param createUser
     * @param itemTable
     * @param itemData
     * @param itemFile
     * @param mainFile
     * @return string json
     */
    public static JSONObject generateBpmJson(String processTitle, String mainTable, Object mainTableData,
                                             String processGroupId, String appId, String createOrgId, String createUser,
                                             List<String> itemTable, List<Object> itemData, Object itemFile, Object mainFile) {
        return getJsonObject(processTitle, mainTable, mainTableData, processGroupId, appId, createOrgId, createUser, itemTable, itemData, itemFile, mainFile);
    }

    private static JSONObject getJsonObject(String processTitle, String mainTable, Object mainTableData, String processGroupId, String appId, String createOrgId, String createUser, List<String> itemTable, List<Object> itemData, Object itemFile, Object mainFile) {
        JSONObject resultMap = new JSONObject();
        resultMap.put("PROCESSTITLE", processTitle);
        resultMap.put("MAINTABLE", mainTable);
        resultMap.put("MAINTABLEDATA", mainTableData);
        resultMap.put("PROCESSGROUPID", processGroupId);
        resultMap.put("APPID", appId);
        resultMap.put("CREATEORGID", createOrgId);
        resultMap.put("CREATEUSER", createUser);
        resultMap.put("ITEMTABLE", itemTable);
        resultMap.put("ITEMDATA", itemData);
        resultMap.put("ITEMFILE", itemFile);
        resultMap.put("MAINFILE", mainFile);
        log.info("封装的参数==="+JSONObject.toJSONString(resultMap));
        return resultMap;
    }


    public static List<String> getFileField(String str) {
        return Stream.of(str.split(",")).collect(Collectors.toList());
    }

    public static List<Map<String, Object>> getFileList(String addressPath, String fileName, Long fileId) {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>(50);
        map.put("FILE_PATH_BYMOBILE", "");
        map.put("FILE_NAME", fileName);
        map.put("FILE_PATH", String.format(addressPath + "fileSourceName=%s&fileuploadId=%s", fileName, fileId));
        list.add(map);
        return list;
    }

    public static String dealYesOrNo(String yn) {
        if (YesOrNo.Y.name().equalsIgnoreCase(yn)) {
            return "是";
        } else if (YesOrNo.N.name().equalsIgnoreCase(yn)) {
            return "否";
        } else {
            return "";
        }
    }

    public static String sdfDate(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return d == null ? null : sdf.format(d);
    }

    public static String formatDate(Object d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return d == null ? null : sdf.format(d);
    }

    public static String formatLocalDate(LocalDate d) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return d == null ? null : dtf.format(d);
    }

    public static String getDictName(List<DictItem> list ,String code) {
        for (DictItem e : list) {
            if (e.getDictItemCode().equals(code)) {
                return e.getDictItemName();
            }
        }
        return null;
    }


    public static JSONObject resubmitBpmJson(String processInstId,String processTitle, String mainTable, Object mainTableData,
                                             String createUser,
                                             List<String> itemTable, List<Object> itemData, Object itemFile) {
        JSONObject resultMap = new JSONObject();
        resultMap.put("PROCESSINSTID", processInstId);
        resultMap.put("PROCESSTITLE", processTitle);
        resultMap.put("MAINTABLE", mainTable);
        resultMap.put("MAINTABLEDATA", mainTableData);
        resultMap.put("CREATEUSER", createUser);
        resultMap.put("ITEMTABLE", itemTable);
        resultMap.put("ITEMDATA", itemData);
        resultMap.put("ITEMFILE", itemFile);
        log.info("封装的参数==="+JSONObject.toJSONString(resultMap));
        return resultMap;
    }

}
