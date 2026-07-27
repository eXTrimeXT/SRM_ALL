package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode
@ApiModel("电子签章签署文件")
public class ApiExtSignDto extends BaseObjectX {

    @ApiModelProperty("业务单据类型   必填")
    private String orderType;

    @ApiModelProperty("业务单据ID  必填")
    private Long orderId;

    @ApiModelProperty("合同文件名称  必填")
    private String title;

    @ApiModelProperty("需要合成合同文件的附件Id集合  必填")
    private List<Long> fileIdList;

    @ApiModelProperty("签署方信息 必填")
    private List<ApiExtSignatoryDto> signatoryList;

    public JSONObject toJsonObject() {
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(this));
        if(CollectionUtils.isNotEmpty(signatoryList)) {
            JSONArray signatoryJsonArray = jsonObject.getJSONArray("signatoryList");
            signatoryJsonArray.forEach(o->{
                JSONObject object = (JSONObject) o;
                //TenantName
                object.put("TenantName", object.get("tenantName"));
                object.remove("tenantName");
                //ReceiverName
                object.put("ReceiverName", object.get("receiverName"));
                object.remove("receiverName");
            });
            jsonObject.put("SignatoryList", signatoryJsonArray);
            jsonObject.remove("signatoryList");
        }
        return jsonObject;
    }

}
