package com.midea.cloud.srm.model.sou.purfixprice.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 定价单 - 附件
 * @author huangbf3
 */
@Data
@TableName("scc_npm_sou_purfix_price_file")
@EqualsAndHashCode(callSuper = true)
public class ExtPurFixPriceFile extends BaseEntity<ExtPurFixPriceFile> {

    @TableId("PUR_FIX_PRICE_FILE_ID")
    @ApiModelProperty("ID")
    private Long purFixPriceFileId;

    /** @see ExtPurFixPriceHead#getPurFixPriceHeadId */
    @TableField("PUR_FIX_PRICE_HEAD_ID")
    @ApiModelProperty("定价单ID")
    private Long purFixPriceHeadId;

    @TableField("FILE_ID")
    @ApiModelProperty("文件ID")
    private Long fileId;

    @TableField("FILE_NAME")
    @ApiModelProperty("文件名称")
    private String fileName;

    @TableField("SORT_INDEX")
    private Integer sortIndex;

}
