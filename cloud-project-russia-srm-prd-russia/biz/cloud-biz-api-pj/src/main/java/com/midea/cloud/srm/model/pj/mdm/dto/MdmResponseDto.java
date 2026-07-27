package com.midea.cloud.srm.model.pj.mdm.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
public class MdmResponseDto<T> {
/**    否	number			状态码 */
    private Integer code;
/**    否	string			描述信息 */
    private String message;

    private T result;

    @SneakyThrows(value = {Exception.class})
    public static MdmResponseDto buildResp(Class tClass, String json) {
        if(StringUtils.isBlank(json)) {
            return new MdmResponseDto<>();
        }
        try {
            JSONObject jsonObject = JSON.parseObject(json);
            MdmResponseDto mdmResponseDto = jsonObject.toJavaObject(MdmResponseDto.class);
            String resultText = "result";
            if(jsonObject.containsKey(resultText) && CollectionUtils.isNotEmpty(jsonObject.getJSONArray(resultText))) {
                mdmResponseDto.setResult(jsonObject.getJSONArray("result").toJavaList(tClass));
            }
            return mdmResponseDto;
        } catch (Exception e) {
            throw new Exception("MDM接口返回异常：" + json);
        }
    }

    public static void main(String[] args) {
        String json = "{\n" +
                "    \"code\":200,\n" +
                "    \"message\":\"操作成功！\",\n" +
                "    \"result\":[\n" +
                "        {\n" +
                "            \"appNum\":\"10998307\",\n" +
                "            \"serialNum\":\"126112\",\n" +
                "            \"orgCode\":\"CAYAU\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        MdmResponseDto<List<MdmResultDto>> mdmResultDtoMdmResponseDto
                = MdmResponseDto.buildResp(MdmResultDto.class, json);
        log.info(JSON.toJSONString(mdmResultDtoMdmResponseDto));
        log.info(mdmResultDtoMdmResponseDto.getResult().get(0).getOrgCode());
    }
}
