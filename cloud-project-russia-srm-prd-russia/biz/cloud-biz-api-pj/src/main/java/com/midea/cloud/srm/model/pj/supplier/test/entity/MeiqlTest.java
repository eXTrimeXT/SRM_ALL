package com.midea.cloud.srm.model.pj.supplier.test.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.meiql.api.annotation.QlMatchType;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author huangbf3
 */
@Data
@TableName("scc_sup_meiql_test")
@ApiModel(description = "meiql测试")
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@QlMatchType("MeiqlTest")
public class MeiqlTest extends BaseEntity{

    @TableId
    @ApiModelProperty("主键ID")
    private Long rowId;

    @ApiModelProperty("test")
    private String test;
}
