package com.midea.cloud.srm.model.pj.sou.openapi.brg.dto.init;

import com.midea.cloud.srm.model.pj.sou.brg.entity.BrgSouProject;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouRequireInfoDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderTypeEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 项目式询价 - 物料需求保存
 * PS: 参考 {@link ApiSouRequireInfoDTO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/21
 */
@SuppressWarnings("ALL")
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiBrgSouRequireInfoDTO extends BaseObjectX {

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源单ID")
    protected Long projectId;

    /** @see SouProject#getOrderType */
    @ApiModelProperty("报价类型")
    protected SouOrderTypeEnum orderType;

    /** @see BrgSouProject#getRequireDesc */
    @ApiModelProperty("需求简述")
    protected String requireDesc;

    @ApiModelProperty("物料需求")
    protected List<ApiBrgSouItemDTO> itemList;

    @ApiModelProperty("true-暂存/false-提交")
    protected boolean isTempSave;

    @ApiModelProperty("报价模板id")
    protected Long quoteTempId;

    @ApiModelProperty("报价模板名称")
    protected String quoteTempName;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (orderType == null) {
            throw new IllegalArgumentException("请选择报价类型");
        }
        requireDesc = StringUtils.trimToNull(requireDesc);
        int length = 255;
        if (requireDesc != null && requireDesc.length() > length) {
            throw new IllegalArgumentException("需求简述的长度不能超过255");
        }
        if (CollectionUtils.isEmpty(itemList)) {
            throw new IllegalArgumentException("请输入物料需求信息");
        }
    }

}
