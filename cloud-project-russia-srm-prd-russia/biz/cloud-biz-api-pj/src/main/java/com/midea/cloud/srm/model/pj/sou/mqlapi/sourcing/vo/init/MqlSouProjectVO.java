package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.init;

import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.init.MqlSouItemVO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.init.MqlSouVendorVO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.process.MqlSouProcessNodeVO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouFileTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangwk12@meicloud.com
 * @since 2023/03/08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouProjectVO extends SouProject {

    @ApiModelProperty("工作小组")
    private List<SouGroup> groupList;

    @ApiModelProperty("内部/外部查看附件")
    private List<SouFile> fileList;

    @ApiModelProperty("供方必须上传配置")
    private List<SouFileConfig> fileConfigList;

    @ApiModelProperty("可用币种")
    private List<SouCurrency> currencyList;

    @ApiModelProperty("物料需求")
    private List<MqlSouItemVO> itemList;

    @ApiModelProperty("邀请供应商信息")
    private List<MqlSouVendorVO> vendorList;

    @ApiModelProperty("流程节点")
    private List<MqlSouProcessNodeVO> processNodeList;

    public static MqlSouProjectVO convertMqlVO(SouProject project,
                                               List<MqlSouProcessNodeVO> processNodeList,
                                               List<SouCurrency> currencyList,
                                               List<SouFile> souFileList,
                                               List<SouFileConfig> fileConfigList,
                                               List<SouGroup> groupList) {
        MqlSouProjectVO vo = SouObjectXUtil.convertTargetObj(project, MqlSouProjectVO.class);
        vo.setProcessNodeList(processNodeList);
        vo.setCurrencyList(currencyList);
        vo.setFileList(souFileList);
        vo.setFileConfigList(fileConfigList);
        vo.setGroupList(groupList);
        return vo;
    }

    public void doVendorView(@Nullable Long vendorId) {
        // 附件
        if (fileList != null) {
            fileList = fileList.stream().filter(e -> e.getFileType().equals(SouFileTypeEnum.OUTER)).collect(Collectors.toList());
        }
        // 工作小组
        groupList = null;
        if (vendorList != null) {
            if (vendorId == null) {
                vendorList = null;
            } else {
                vendorList = (vendorList.stream().filter(v -> vendorId.equals(v.getVendorId())).collect(Collectors.toList()));
            }
        }
    }

}
