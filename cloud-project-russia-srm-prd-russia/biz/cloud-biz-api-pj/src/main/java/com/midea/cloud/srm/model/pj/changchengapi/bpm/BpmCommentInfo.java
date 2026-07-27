package com.midea.cloud.srm.model.pj.changchengapi.bpm;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author huangbf3
 */
@Data
@Accessors(chain = true)
@ApiModel("动作")
public class BpmCommentInfo {

    @ApiModelProperty("actionName表示当前用户的办理动作，如同意、不同意、退修改、送领导批示等。")
    private String actionName;

    @ApiModelProperty("commentMsg表示当前用户办理时录入的意见留言")
    private String commentMsg;
}
