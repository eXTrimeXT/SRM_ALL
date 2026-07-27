package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.tech;

import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.order.MqlSouOrderFileVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFileConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouFileConfigTypeEnum;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * MQL - 供应商的技术标信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/04/03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouTechVendorOrderVO extends SouVendor {

    @ApiModelProperty("公司信息")
    private CompanyInfo companyInfo;

    @ApiModelProperty("技术标附件")
    private List<MqlSouOrderFileVO> techFileList;

    public static MqlSouTechVendorOrderVO convertApiVO(SouVendor vendor,
                                                       CompanyInfo companyInfo,
                                                       List<SouFileConfig> techFileConfigList,
                                                       List<SouOrderFile> orderFileList) {
        MqlSouTechVendorOrderVO vo = new MqlSouTechVendorOrderVO();
        BeanUtils.copyProperties(vendor, vo);
        vo.setCompanyInfo(companyInfo);
        vo.setTechFileList(new ArrayList<>(techFileConfigList.size())); {
            Map<Long/* fileConfigId */, SouOrderFile> orderFileMap = orderFileList.stream()
                    .filter(e -> SouFileConfigTypeEnum.TECH_FILE.equals(e.getFileType()))
                    .collect(Collectors.toMap(SouOrderFile::getSouFileConfigId, Function.identity()));
            for (SouFileConfig fileConfig : techFileConfigList) {
                MqlSouOrderFileVO fileVO = new MqlSouOrderFileVO();
                vo.getTechFileList().add(fileVO);

                // 供方必须上传附件ID
                fileVO.setSouFileConfigId(fileConfig.getSouFileConfigId());
                // 附件类型
                fileVO.setFileType(fileConfig.getFileType());
                // 要求附件ID
                fileVO.setRequireDocId(fileConfig.getRequireDocId());
                // 要求附件名称
                fileVO.setRequireFileName(fileConfig.getRequireFileName());
                // 附件要求
                fileVO.setFileRequire(fileConfig.getFileRequire());
                // 要求备注
                fileVO.setRequireRemark(fileConfig.getRequireRemark());

                SouOrderFile orderFile = orderFileMap.get(fileConfig.getSouFileConfigId());
                if (orderFile != null) {
                    // 供应商报价附件表ID
                    fileVO.setOrderFileId(orderFile.getOrderFileId());
                    // 供应商报价附件ID
                    fileVO.setOrderDocId(orderFile.getOrderDocId());
                    // 供应商报价附件名称
                    fileVO.setOrderFileName(orderFile.getOrderFileName());
                    // 供应商报价附件备注
                    fileVO.setOrderRemark(orderFile.getOrderRemark());
                }
            }

            for (SouOrderFile orderFile : orderFileList) {
                if (!SouFileConfigTypeEnum.TECH_FILE.equals(orderFile.getFileType()) || orderFile.getSouFileConfigId() != null) { continue; }

                MqlSouOrderFileVO fileVO = new MqlSouOrderFileVO();
                vo.getTechFileList().add(fileVO);

                // 附件类型
                fileVO.setFileType(orderFile.getFileType());
                // 供应商报价附件表ID
                fileVO.setOrderFileId(orderFile.getOrderFileId());
                // 供应商报价附件ID
                fileVO.setOrderDocId(orderFile.getOrderDocId());
                // 供应商报价附件名称
                fileVO.setOrderFileName(orderFile.getOrderFileName());
                // 供应商报价附件备注
                fileVO.setOrderRemark(orderFile.getOrderRemark());
            }
        }

        return vo;
    }

}