package com.midea.cloud.srm.model.pj.common;

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
 * @author huangbf3
 */
@Data
@Slf4j
public class CommonResponseDto<T> {
/**    否	number			状态码 */
    private Integer code;
/**    否	string			描述信息 */
    private String msg;

    private T data;

    @SneakyThrows(value = {Exception.class})
    public static CommonResponseDto buildResp(Class tClass, String json) {
        if(StringUtils.isBlank(json)) {
            return new CommonResponseDto<>();
        }
        try {
            JSONObject jsonObject = JSON.parseObject(json);
            CommonResponseDto commonResponseDto = jsonObject.toJavaObject(CommonResponseDto.class);
            String resultText = "data";
            if(jsonObject.containsKey(resultText) && CollectionUtils.isNotEmpty(jsonObject.getJSONArray(resultText))) {
                commonResponseDto.setData(jsonObject.getJSONArray("data").toJavaList(tClass));
            }
            return commonResponseDto;
        } catch (Exception e) {
            throw new Exception("MDM接口返回异常：" + json);
        }
    }
}
