package com.midea.cloud.srm.model.pj.sou.car.point.notice.dto.webapi;

import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.pj.sou.car.point.notice.entity.PointNotice;
import com.midea.cloud.srm.model.pj.sou.car.point.notice.entity.PointNoticeLink;
import com.midea.cloud.srm.model.pj.sou.car.point.notice.entity.PointNoticeResult;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

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
 *  修改日期: 2022/10/13 14:26
 *  修改内容:
 * </pre>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PointNoticeWebDTO extends PointNotice {

    /**
     * 中标结果
     */
    @ApiModelProperty("中标结果")
    private List<PointNoticeResult> resultList;

    /**
     * 联系人
     */
    @ApiModelProperty("联系人")
    private List<PointNoticeLink> linkList;

    /**
     * 附件数据
     */
    @ApiModelProperty("附件数据")
    private List<SceneFile> fileUploads;
}
