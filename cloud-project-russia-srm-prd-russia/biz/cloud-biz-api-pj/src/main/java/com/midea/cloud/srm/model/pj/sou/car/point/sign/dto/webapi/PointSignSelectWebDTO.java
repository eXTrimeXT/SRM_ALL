package com.midea.cloud.srm.model.pj.sou.car.point.sign.dto.webapi;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.srm.model.common.BasePage;
import com.midea.cloud.srm.model.pj.sou.car.point.sign.entity.PointSign;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/**
 * <pre>
 *
 * </pre>
 *
 * @author zhaoming1.kuang@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/10/11 09:08
 *  修改内容:
 * </pre>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PointSignSelectWebDTO extends BasePage {

    /** @see PointSign#getSignNo */
    @ApiModelProperty("定点会签单号")
    private String signNo;

    /** @see PointSign#getSignTitle */
    @ApiModelProperty("标题")
    private String signTitle;

    /** @see PointSign#getStatus */
    @ApiModelProperty("单据状态")
    private String status;

    /** @see PointSign#getType */
    @ApiModelProperty("单据类型")
    private String type;

    /**
     * 格式化及转化
     */
    public LambdaQueryWrapper<PointSign> formatAndConvert() {
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
        signNo = StringUtils.trimToNull(signNo);
        // 标题
        signTitle = StringUtils.trimToNull(signTitle);
    }

    /**
     * 转化
     */
    private LambdaQueryWrapper<PointSign> convertLambda() {
        LambdaQueryWrapper<PointSign> lambda = new LambdaQueryWrapper<>();

        // 定点会签单号
        lambda.like(signNo != null, PointSign::getSignNo, signNo);
        // 标题
        lambda.like(signTitle != null, PointSign::getSignTitle, signTitle);
        // 单据状态
        lambda.eq(status != null, PointSign::getStatus,status);
        // 单据类型
        lambda.eq(type != null, PointSign::getType,type);
        return lambda;
    }
}
