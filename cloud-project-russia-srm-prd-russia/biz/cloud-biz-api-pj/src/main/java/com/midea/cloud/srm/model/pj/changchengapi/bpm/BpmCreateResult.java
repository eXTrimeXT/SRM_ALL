package com.midea.cloud.srm.model.pj.changchengapi.bpm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author huangbf3
 * BPM审批流创建返回结果
 */
@Data
public class BpmCreateResult {

    @ApiModelProperty("流程实例ID")
    private String processInstId;

    @ApiModelProperty("链接地址")
    private String linkUrl;
}
