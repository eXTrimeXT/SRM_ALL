package com.midea.cloud.srm.model.sou.filelink.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <pre>
 *  围串标识别流水
 * </pre>
 *
 * @author huanghb14@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020-03-04 13:39:58
 *  修改内容:
 * </pre>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_sou_word_file_link")
@ApiModel(description = "文件映射表")
public class WordFileLink extends BaseEntity {

    private static final long serialVersionUID = 6390591027354729862L;

    @ApiModelProperty(value = "ID")
    @TableId("WORD_FILE_LINK_ID")
    private Long wordFileLinkId;

    @ApiModelProperty(value = "文件ID")
    @TableField("PDF_FILE_LINK_ID")
    private Long pdfFileLinkId;

    @ApiModelProperty(value = "文件名称")
    @TableField("PDF_FILE_LINK_NAME")
    private String pdfFileLinkName;

}
