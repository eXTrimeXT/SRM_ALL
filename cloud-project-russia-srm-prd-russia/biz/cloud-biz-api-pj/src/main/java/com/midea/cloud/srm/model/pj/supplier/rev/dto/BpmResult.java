package com.midea.cloud.srm.model.pj.supplier.rev.dto;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.midea.cloud.srm.model.base.monitor.enums.YesOrNo;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
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
   /* public static JSONObject generateBpmJson(String processTitle, String mainTable, Object mainTableData,
                                         String processGroupId, String appId, String createOrgId, String createUser,
                                         List<String> itemTable, List<Object> itemData, Object itemFile) {
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
        return resultMap;
    }*/

    public static JSONObject generateBpmJson(String processTitle, String mainTable, Object mainTableData,
                                             String processGroupId, String appId, String createOrgId, String createUser,
                                             List<String> itemTable, List<Object> itemData, Object itemFile) {
        return getJsonObject(processTitle, mainTable, mainTableData, processGroupId, appId, createOrgId, createUser, itemTable, itemData, itemFile, Lists.newArrayList());
    }


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
        log.info("封装的参数==="+JSONObject.toJSONString(resultMap));
        return resultMap;
    }

    public static List<String> getFileField(String str) {
        return Stream.of(str.split(",")).collect(Collectors.toList());
    }

    public static List<Map<String, Object>> getFileList(String addressPath, List<Fileupload> list) {
        List<Map<String, Object>> fileList = new ArrayList<>();
        for (Fileupload e : list) {
            Map<String, Object> map = new HashMap<>(50);
            map.put("FILE_PATH_BYMOBILE", "");
            map.put("FILE_NAME", e.getFileSourceName());
            map.put("FILE_PATH", String.format(addressPath + "fileSourceName=%s&fileuploadId=%s", e.getFileSourceName(), e.getFileuploadId()));
            fileList.add(map);
        }
        return fileList;
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
