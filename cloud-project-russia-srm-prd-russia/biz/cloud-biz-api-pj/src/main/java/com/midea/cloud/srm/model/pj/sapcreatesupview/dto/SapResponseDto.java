package com.midea.cloud.srm.model.pj.sapcreatesupview.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.model.pj.mdm.dto.MdmResultDto;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author fubiao
 */
@Data
@Slf4j
public class SapResponseDto<T> {
    /**    否	number			状态码 */
    private Integer code;
    /**    否	string			描述信息 */
    private String message;
    private T result;

    private static final String RESULT = "RESULT";

    @SneakyThrows(value = {Exception.class})
    public static SapResponseDto buildResp(Class tClass, String json) {
        if(StringUtils.isBlank(json)) {
            return new SapResponseDto<>();
        }
        try {
            JSONObject jsonObject = JSON.parseObject(json);
            SapResponseDto sapResponseDto = jsonObject.toJavaObject(SapResponseDto.class);
            String resultText = "result";
            if(jsonObject.containsKey(resultText) && CollectionUtils.isNotEmpty(jsonObject.getJSONObject(resultText).getJSONArray(RESULT))) {
                sapResponseDto.setResult(jsonObject.getJSONObject(resultText).getJSONArray(RESULT).toJavaList(tClass));
            }
            return sapResponseDto;
        } catch (Exception e) {
            throw new Exception("SAP接口返回异常：" + json);
        }
    }
}
