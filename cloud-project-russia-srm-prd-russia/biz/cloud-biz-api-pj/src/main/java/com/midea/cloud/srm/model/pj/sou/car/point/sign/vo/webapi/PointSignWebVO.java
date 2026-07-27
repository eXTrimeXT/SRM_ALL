package com.midea.cloud.srm.model.pj.sou.car.point.sign.vo.webapi;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.pj.sou.car.point.sign.entity.PointSign;
import com.midea.cloud.srm.model.pj.sou.car.point.sign.entity.PointSignDetail;
import com.midea.cloud.srm.model.pj.sou.car.point.sign.entity.PointSignPriceDetail;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * <pre>
 * 定点会签出参对象
 * </pre>
 *
 * @author zhaoming1.kuang@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/10/10 21:11
 *  修改内容:
 * </pre>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PointSignWebVO extends PointSign {

    /**
     * 被终止的会签类型
     */
    @ApiModelProperty("被终止的会签类型")
    private String endType;

    /**
     * 总体报价结果
     */
    @ApiModelProperty("总体报价结果")
    private List<PointSignPriceDetail> priceDetailList;

    /**
     * 定点明细
     */
    @ApiModelProperty("定点明细")
    private List<PointSignDetail> detailList;

    /**
     * 附件数据
     */
    @ApiModelProperty("附件数据")
    private List<SceneFile> fileUploads;

    /**
     * 终止附件数据
     */
    @ApiModelProperty("终止附件数据")
    private List<SceneFile> endFileUploads;
}
