package com.midea.cloud.srm.model.pj.sou.openapi.bid.vo.init;

import com.midea.cloud.srm.model.pj.sou.openapi.bid.vo.init.ApiBidSouCurrencyVO;
import com.midea.cloud.srm.model.pj.sou.openapi.bid.vo.init.ApiBidSouProjectVO;
import com.midea.cloud.srm.model.pj.sou.openapi.bid.vo.process.ApiBidSouProcessConfigVO;
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
 * 招投标openAPI - 立项基本信息
 * PS: 参考 {@link ApiSouInitProjectInfoVO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiBidSouInitProjectVO extends ApiBidSouProjectVO {

    @ApiModelProperty("流程配置信息")
    private ApiBidSouProcessConfigVO processConfig;
    @ApiModelProperty("流程节点信息")
    private List<ApiSouProcessNodeVO> processNodeList;
    @ApiModelProperty("可用币种")
    private List<ApiBidSouCurrencyVO> currencyList;
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
