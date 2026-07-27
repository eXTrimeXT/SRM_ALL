package com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.init;

import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.init.ApiCompSouCurrencyVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.init.ApiCompSouProjectVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.process.ApiCompSouProcessConfigVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouInitProjectInfoVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouProcessNodeVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFileConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouGroup;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouFileTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 竞价openAPI - 立项基本信息
 * PS: 参考 {@link ApiSouInitProjectInfoVO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiCompSouInitProjectVO extends ApiCompSouProjectVO {

    @ApiModelProperty("流程配置信息")
    private ApiCompSouProcessConfigVO processConfig;
    @ApiModelProperty("流程节点信息")
    private List<ApiSouProcessNodeVO> processNodeList;
    @ApiModelProperty("可用币种")
    private List<ApiCompSouCurrencyVO> currencyList;
    @ApiModelProperty("内部/外部附件")
    private List<SouFile> souFileList;
    @ApiModelProperty("供应商必须上传附件")
    private List<SouFileConfig> fileConfigList;
    @ApiModelProperty("工作小组")
    private List<SouGroup> groupList;

    public void doVendorView() {
        // 附件
        this.souFileList = this.souFileList.stream().filter(e -> e.getFileType().equals(SouFileTypeEnum.OUTER)).collect(Collectors.toList());
        // 工作小组
        this.groupList = null;
    }

}
