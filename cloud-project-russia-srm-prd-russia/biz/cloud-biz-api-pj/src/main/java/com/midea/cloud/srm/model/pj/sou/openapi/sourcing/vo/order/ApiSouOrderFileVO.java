package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFileConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderFile;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 寻源openAPI - 供应商报价附件
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouOrderFileVO extends SouOrderFile {

    /** @see SouFileConfig#getRequireDocId */
    @ApiModelProperty("文件ID")
    private Long requireDocId;

    /** @see SouFileConfig#getRequireFileName */
    @ApiModelProperty("文件名")
    private String requireFileName;

    /** @see SouFileConfig#getFileRequire */
    @ApiModelProperty("资料要求")
    private String fileRequire;

    /** @see SouFileConfig#getRequireRemark */
    @ApiModelProperty("备注")
    private String requireRemark;

    public static List<ApiSouOrderFileVO> convertApiVO(List<SouFileConfig> fileConfigList,
                                                       List<SouOrderFile> orderFileList) {
        fileConfigList.sort(Comparator.comparing(SouFileConfig::getSortIndex));
        Map<Long/* souFileConfigId */, SouOrderFile> orderFileMap = orderFileList.stream()
                .filter(o-> Objects.nonNull(o.getSouFileConfigId())).collect(Collectors.toMap(SouOrderFile::getSouFileConfigId, Function.identity()));

        List<ApiSouOrderFileVO> voList = new ArrayList<>(fileConfigList.size());
        for (SouFileConfig fileConfig : fileConfigList) {
            ApiSouOrderFileVO vo = new ApiSouOrderFileVO();
            voList.add(vo);

            // 供方必须上传附件ID
            vo.setSouFileConfigId(fileConfig.getSouFileConfigId());
            // 附件类型
            vo.setFileType(fileConfig.getFileType());
            // 要求附件ID
            vo.setRequireDocId(fileConfig.getRequireDocId());
            // 要求附件名称
            vo.setRequireFileName(fileConfig.getRequireFileName());
            // 附件要求
            vo.setFileRequire(fileConfig.getFileRequire());
            // 要求备注
            vo.setRequireRemark(fileConfig.getRequireRemark());

            SouOrderFile orderFile = orderFileMap.get(fileConfig.getSouFileConfigId());
            if (orderFile != null) {
                // 供应商报价附件表ID
                vo.setOrderFileId(orderFile.getOrderFileId());
                // 供应商报价附件ID
                vo.setOrderDocId(orderFile.getOrderDocId());
                // 供应商报价附件名称
                vo.setOrderFileName(orderFile.getOrderFileName());
                // 供应商报价附件备注
                vo.setOrderRemark(orderFile.getOrderRemark());
            }
        }
        orderFileList.forEach(orderFile -> {
            if (orderFile.getSouFileConfigId() == null) {
                ApiSouOrderFileVO vo = new ApiSouOrderFileVO();
                BeanUtils.copyProperties(orderFile, vo);
                voList.add(vo);
            }
        });

        return voList;
    }

}
