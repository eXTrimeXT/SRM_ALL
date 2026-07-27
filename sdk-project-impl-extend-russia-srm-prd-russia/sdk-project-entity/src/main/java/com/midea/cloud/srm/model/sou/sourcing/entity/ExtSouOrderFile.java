package com.midea.cloud.srm.model.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouFileConfigTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel("投标附件标")
@TableName("scc_sou_order_file")
public class ExtSouOrderFile extends BaseEntity<ExtSouOrderFile> {

    @TableId("order_file_id")
    @ApiModelProperty("ID")
    private Long orderFileId;

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源单ID")
    @TableField("project_id")
    private Long projectId;

    /** @see SouOrder#getOrderId */
    @TableField("order_id")
    @ApiModelProperty("报价头ID")
    private Long orderId;

    /** @see SouFileConfig#getSouFileConfigId */
    @TableField("sou_file_config_id")
    @ApiModelProperty("配置文件Id")
    private Long souFileConfigId;

    /** @see SouOrder#getVendorId */
    @TableField("vendor_id")
    @ApiModelProperty("供应商ID")
    private Long vendorId;

    /** @see SouOrder#getRound */
    @TableField("round")
    @ApiModelProperty("轮次")
    private Integer round;

    /** @see SouFileConfig#getFileType */
    @TableField("file_type")
    @ApiModelProperty("附件类型")
    private String fileType;

    @TableField("order_doc_id")
    @ApiModelProperty("文件ID")
    private Long orderDocId;

    @TableField("order_file_name")
    @ApiModelProperty("文件名")
    private String orderFileName;

    @TableField("order_remark")
    @ApiModelProperty("备注")
    private String orderRemark;


    /**
     * 包名
     */
    @ApiModelProperty("包名")
    private String extPackageName;

    /**
     * 包名列表
     */
    @TableField(exist = false)
    private List<String> extPackageNameList;

    /**
     * 签署状态
     */
    @ApiModelProperty("签署状态")
    private String extSignStatus;

    @ApiModelProperty("投标时间")
    private Date extSubmitTime;

    @ApiModelProperty("投标状态")
    private String extOrderStatus;

    public void formattingPackageNameToList() {
        if(StringUtils.isNotBlank(extPackageName)) {
            extPackageNameList = new ArrayList<>(Arrays.asList(extPackageName.split(SrmConstant.SIG_3)));
        } else {
            extPackageNameList = new ArrayList<>();
        }
    }
    public void formattingPackageListToName() {
        if(CollectionUtils.isNotEmpty(extPackageNameList)) {
            extPackageName = extPackageNameList.stream().collect(Collectors.joining(SrmConstant.SIG_3));
        } else {
            extPackageName = "";
        }
    }

    /**
     * 防止前端传入类似此种格式的字符串，杜绝滚雪球式字符长度增长："[\"[\\\"[\\\\\\\"[\\\\\\\\\\\\\\\"[]\\\\\\\\\\\\\\\"]\\\\\\\"]\\\"]\"]"
     */
    public void formattingPackageName() {
        if(StringUtils.isNotBlank(this.extPackageName)) {
            /** 不以双引号开头 */
            if(this.extPackageName.startsWith(SrmConstant.DOUBLE_QUOTATION) && this.extPackageName.endsWith(SrmConstant.DOUBLE_QUOTATION)) {
                this.extPackageName = this.extPackageName.substring(1, this.extPackageName.length()-1);
            }
            /** 不存在"[ 和 ]"组合的字符串 */
            this.extPackageName = this.extPackageName.replaceAll(SrmConstant.BACKSLASH, "").replaceAll(StringUtils.joinWith("", SrmConstant.DOUBLE_QUOTATION, SrmConstant.BRACKET_LEFT_STR), "").replaceAll(StringUtils.joinWith("", SrmConstant.BRACKET_RIGHT_STR, SrmConstant.DOUBLE_QUOTATION), "");
            char[] charArrays = this.extPackageName.toCharArray();
            /** 不允许出现连续的[开头字符串，统计连续[开头个数 */
            Integer countLeft = 0;
            for(char c : charArrays) {
                if(SrmConstant.BRACKET_LEFT == c) {
                    countLeft++;
                } else {
                    break;
                }
            }
            /** 不允许出现连续的]结尾字符串，统计连续]结尾个数 */
            Integer countRight = 0;
            for(int i = charArrays.length-1; i>=0; i--) {
                if(SrmConstant.BRACKET_RIGHT == charArrays[i]) {
                    countRight++;
                } else {
                    break;
                }
            }

            /** 出现连续[开头 且 与连续]结尾 个数匹配的时候，保留一个*/
            if(Integer.compare(countLeft, 1) == 1 && Integer.compare(countLeft, countRight) == 0) {
                Integer start = countLeft-1;
                Integer end = this.extPackageName.length() - countLeft +1;
                this.extPackageName = this.extPackageName.substring(start, end);
            }
        }
    }
}
