package com.midea.cloud.srm.model.pj.sou.car.point.notice.dto.webapi;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.srm.model.common.BasePage;
import com.midea.cloud.srm.model.pj.sou.car.point.notice.entity.PointNotice;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/**
 * <pre>
 *  列表查询参数
 * </pre>
 *
 * @author zhaoming1.kuang@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/10/13 13:52
 *  修改内容:
 * </pre>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PointNoticeSelectWebDTO extends BasePage {

    /** @see PointNotice#getNoticeNo  */
    @ApiModelProperty("定点通知单号")
    private String noticeNo;

    /** @see PointNotice#getPublishStatus  */
    @ApiModelProperty("发布状态（字典：SOU_POINT_NOTICE_PUBLISH_STATUS）")
    private String publishStatus;

    /** @see PointNotice#getAuditStatus */
    @ApiModelProperty("审批状态（字典：SOU_POINT_NOTICE_AUDIT_STATUS）")
    private String auditStatus;

    /** @see PointNotice#getSouNo */
    @ApiModelProperty("询价单号")
    private String souNo;

    /** @see PointNotice#getSouName */
    @ApiModelProperty("询价标题")
    private String souName;

    /** @see PointNotice#getVendorId()  */
    @ApiModelProperty("供应商ID")
    private Long vendorId;

    /**
     * 格式化及转化
     */
    public LambdaQueryWrapper<PointNotice> formatAndConvert() {
        // 1: 入参格式化
        this.formatParams();
        // 2: 转化
        return this.convertLambda();
    }

    /**
     * 入参格式化
     */
    private void formatParams() {
        // 定点会签单号
        noticeNo = StringUtils.trimToNull(noticeNo);
        // 寻源单号
        souNo = StringUtils.trimToNull(souNo);
        //寻源标题
        souName = StringUtils.trimToNull(souName);
    }

    /**
     * 转化
     */
    private LambdaQueryWrapper<PointNotice> convertLambda() {
        LambdaQueryWrapper<PointNotice> lambda = new LambdaQueryWrapper<>();

        // 定点会签单号
        lambda.like(noticeNo != null, PointNotice::getNoticeNo, noticeNo);
        // 寻源单号
        lambda.like(souNo != null, PointNotice::getSouNo, souNo);
        // 寻源标题
        lambda.like(souName != null, PointNotice::getSouName, souName);
        // 发布状态
        lambda.eq(publishStatus != null, PointNotice::getPublishStatus,publishStatus);
        // 审批状态
        lambda.eq(auditStatus != null, PointNotice::getAuditStatus,auditStatus);
        // 供应商ID
        lambda.eq(vendorId != null, PointNotice::getVendorId,vendorId);
        return lambda;
    }
}
