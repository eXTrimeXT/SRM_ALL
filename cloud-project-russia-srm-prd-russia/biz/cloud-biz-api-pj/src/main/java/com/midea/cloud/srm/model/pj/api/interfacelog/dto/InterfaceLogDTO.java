package com.midea.cloud.srm.model.pj.api.interfacelog.dto;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.model.pj.api.interfacelog.entity.InterfaceLog;
import com.midea.cloud.srm.model.pj.api.interfacelog.enums.ApiInfoEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * @author huangbf3
 */
@Data
@ApiModel(description = "接口日志表DTO")
public class InterfaceLogDTO extends InterfaceLog {
    @ApiModelProperty(value = "创建时间开始")
    private Date creationDateBegin;

    @ApiModelProperty(value = "创建时间结束")
    private Date creationDateEnd;

    private static final Integer NUM_TEN_THOUSAND = 10000;
    /**
     * 初始化对象
     */
    public InterfaceLogDTO(){

    }
    /**
     * 初始化对象
     * @param apiInfoEnum 备注
     * @param serviceInfo 备注
     */
    public InterfaceLogDTO(ApiInfoEnum apiInfoEnum,Object serviceInfo){
        super.setBillType(apiInfoEnum.getBillType());
        super.setServiceName(apiInfoEnum.getBillType());
        super.setServiceType(apiInfoEnum.getServiceType());
        super.setTargetSys(apiInfoEnum.getTargetSys());
        super.setType(apiInfoEnum.getInterfaceType());
        super.setStatus("SUCCESS");
        if(serviceInfo!=null){
            if(serviceInfo instanceof String) {
                super.setServiceInfo(formatingServiceInfo((String) serviceInfo));
            } else {
                super.setServiceInfo(formatingServiceInfo(JSONObject.toJSONString(serviceInfo)));
            }
        }
    }

    private static String formatingServiceInfo(String info) {
        if(StringUtils.isBlank(info)) {
            return info;
        }
        if(Integer.compare(info.length(), NUM_TEN_THOUSAND) == 1) {
            return info.substring(0, NUM_TEN_THOUSAND);
        }
        return info;
    }

    /**
     *
     * @param apiInfoEnum
     */
    public InterfaceLogDTO(ApiInfoEnum apiInfoEnum){
        super.setBillType(apiInfoEnum.getBillType());
        super.setServiceName(apiInfoEnum.getBillType());
        super.setServiceType(apiInfoEnum.getServiceType());
        super.setTargetSys(apiInfoEnum.getTargetSys());
        super.setType(apiInfoEnum.getInterfaceType());
        super.setStatus("SUCCESS");
    }

}
