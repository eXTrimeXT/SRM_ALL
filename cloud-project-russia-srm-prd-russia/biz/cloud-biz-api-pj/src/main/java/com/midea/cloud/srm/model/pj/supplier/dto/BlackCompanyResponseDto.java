package com.midea.cloud.srm.model.pj.supplier.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author fubiao
 */
@Data
@Slf4j
public class BlackCompanyResponseDto<T> {
    /**    否	number			状态码 */
    private Integer code;
    /**    否	string			描述信息 */
    private String message;
    private T result;

    private static final String ROWS = "rows";

    @SneakyThrows(value = {Exception.class})
    public static BlackCompanyResponseDto buildResp(String json) {
        if(StringUtils.isBlank(json)) {
            return new BlackCompanyResponseDto<>();
        }
        try {
            JSONObject jsonObject = JSON.parseObject(json);
            BlackCompanyResponseDto blackCompanyResponseDto = jsonObject.toJavaObject(BlackCompanyResponseDto.class);
            if(jsonObject.containsKey("result")) {
                BlackCompanyResultDto result = jsonObject.getJSONObject("result").toJavaObject(BlackCompanyResultDto.class);
                result.setRows(jsonObject.getJSONObject("result").getJSONArray(ROWS).toJavaList(BlackCompanyListDto.class));
                blackCompanyResponseDto.setResult(result);
            }
            return blackCompanyResponseDto;
        } catch (Exception e) {
            throw new Exception("SAP接口返回异常：" + json);
        }
    }
}
