package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init;

import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouProject;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouFileTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 寻源openAPI - 项目信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouInitProjectInfoVO extends SouProject {

    @ApiModelProperty("流程配置信息")
    private SouProcessConfig processConfig;
    @ApiModelProperty("流程节点信息")
    private List<ApiSouProcessNodeVO> processNodeList;
    @ApiModelProperty("可用币种")
    private List<SouCurrency> currencyList;
    @ApiModelProperty("内部/外部附件")
    private List<SouFile> souFileList;
    @ApiModelProperty("供应商必须上传附件")
    private List<SouFileConfig> fileConfigList;
    @ApiModelProperty("工作小组")
    private List<SouGroup> groupList;
    @ApiModelProperty("保证金信息")
    private CompSouProject compSouProject;

    public static ApiSouInitProjectInfoVO convertApiVO(SouProject project,
                                                       SouProcessConfig processConfig,
                                                       List<ApiSouProcessNodeVO> processNodeList,
                                                       List<SouCurrency> currencyList,
                                                       List<SouFile> souFileList,
                                                       List<SouFileConfig> fileConfigList,
                                                       List<SouGroup> groupList, CompSouProject compSouProject) {
        ApiSouInitProjectInfoVO vo = SouObjectXUtil.convertTargetObj(project, ApiSouInitProjectInfoVO.class);
        vo.setProcessConfig(processConfig);
        vo.setProcessNodeList(processNodeList);
        vo.setCurrencyList(currencyList);
        vo.setSouFileList(souFileList);
        vo.setFileConfigList(fileConfigList);
        vo.setGroupList(groupList);
        vo.setCompSouProject(compSouProject);
        return vo;
    }

    public void doVendorView() {
        // 附件
        this.souFileList = this.souFileList.stream().filter(e -> e.getFileType().equals(SouFileTypeEnum.OUTER)).collect(Collectors.toList());
        // 工作小组
        this.groupList = null;
    }

}
