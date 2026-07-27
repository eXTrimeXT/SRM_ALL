package com.midea.cloud.srm.model.pj.sou.openapi.brg.vo.init;

import com.midea.cloud.srm.model.pj.sou.openapi.brg.vo.init.ApiBrgSouCurrencyVO;
import com.midea.cloud.srm.model.pj.sou.openapi.brg.vo.init.ApiBrgSouProjectVO;
import com.midea.cloud.srm.model.pj.sou.openapi.brg.vo.process.ApiBrgSouProcessConfigVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouInitProjectInfoVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouProcessNodeVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouFileTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目式询价 - 立项基本信息
 * PS: 参考 {@link ApiSouInitProjectInfoVO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiBrgSouInitProjectVO extends ApiBrgSouProjectVO {

    @ApiModelProperty("流程配置信息")
    private ApiBrgSouProcessConfigVO processConfig;
    @ApiModelProperty("流程节点信息")
    private List<ApiSouProcessNodeVO> processNodeList;
    @ApiModelProperty("可用币种")
    private List<ApiBrgSouCurrencyVO> currencyList;
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
