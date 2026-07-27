package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.firstpage;

import com.midea.cloud.srm.model.common.BasePage;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源 - 门户.寻源信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/01/19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SouFirstPageViewDTO extends BasePage {

    /** @see SouProject#getSouNo */
    private String souNo;

    /** @see SouProject#getSouName */
    private String souName;

    /** @see SouProject#getSouType */
    private String souType;

}
