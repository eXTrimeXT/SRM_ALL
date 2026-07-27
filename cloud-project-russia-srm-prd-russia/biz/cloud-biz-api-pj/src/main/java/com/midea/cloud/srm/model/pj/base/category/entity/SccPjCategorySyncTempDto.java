package com.midea.cloud.srm.model.pj.base.category.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: panmq
 * @Date: 2024/04/30/ $
 * @Description: 品类同步-接口临时表实体类
 */
@Data
@ApiModel("品类同步-接口临时表实体类")
public class SccPjCategorySyncTempDto {

    @ApiModelProperty("主键")
    private Long categorySyncId;
    @ApiModelProperty("层级")
    private Integer categoryLevel;
    @ApiModelProperty("创建人工号")
    private String createUserCode;
    @ApiModelProperty("更新人名字")
    private String updateUserName;
    @ApiModelProperty("来源系统更新时间")
    private Date updateTime;
    @ApiModelProperty("创建人名字")
    private String createUserName;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("品类编码")
    private String categoryCode;
    @ApiModelProperty("品类名称")
    private String categoryName;
    @ApiModelProperty("更新人部门")
    private String updateUserDept;
    @ApiModelProperty("版本")
    private Long version;
    @ApiModelProperty("排序")
    private Integer sortNo;
    @ApiModelProperty("删除标识")
    private String deleteFlag;
    @ApiModelProperty("更新人工号")
    private String updateUserCode;
    @ApiModelProperty("创建时间")
    private Date createTime;
    @ApiModelProperty("父级品类编码")
    private String parentCategoryCode;
    @ApiModelProperty("版本号")
    private Integer categoryState;
    @ApiModelProperty("品类描述")
    private String categoryDescribe;
    @ApiModelProperty("创建人部门")
    private String createUserDept;
    @ApiModelProperty("有效状态")
    private String activeFlag;
    @ApiModelProperty("处理序号")
    private String processSerialNum;
    @ApiModelProperty("处理状态，PENDING：未处理，COMPLETED：处理完成，PROCESSING：处理中，ERROR：处理错误，RETRY：需重试")
    private String processStatus;
    @ApiModelProperty("处理信息")
    private String processMessage;
    @ApiModelProperty("处理时间")
    private Date processDate;
    @ApiModelProperty("处理批次号")
    private Long processGroupId;
    @ApiModelProperty("创建人ID")
    private Long createdId;
    @ApiModelProperty("创建人")
    private String createdBy;
    @ApiModelProperty("创建时间")
    private Date creationDate;
    @ApiModelProperty("创建人IP")
    private String createdByIp;
    @ApiModelProperty("创建人姓名")
    private String createdFullName;
    @ApiModelProperty("最后更新人ID")
    private Long lastUpdatedId;
    @ApiModelProperty("更新人")
    private String lastUpdatedBy;
    @ApiModelProperty("最后更新时间")
    private Date lastUpdateDate;
    @ApiModelProperty("最后更新人IP")
    private String lastUpdatedByIp;
    @ApiModelProperty("最后更新人姓名")
    private String lastUpdatedFullName;
    @ApiModelProperty("租户ID")
    private String tenantId;

}
