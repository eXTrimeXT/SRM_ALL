package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.order;

import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.order.MqlSouOrderFileDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFileConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderFile;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * MQL - 供应商报价附件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouOrderFileVO extends MqlSouOrderFileDTO {

    public static List<MqlSouOrderFileVO> convertMqlVO(List<SouFileConfig> fileConfigList,
                                                       List<SouOrderFile> orderFileList) {
        fileConfigList.sort(Comparator.comparing(SouFileConfig::getSortIndex));
        Map<Long/* souFileConfigId */, SouOrderFile> orderFileMap = orderFileList.stream()
                .filter(o-> Objects.nonNull(o.getSouFileConfigId())).collect(Collectors.toMap(SouOrderFile::getSouFileConfigId, Function.identity()));

        List<MqlSouOrderFileVO> voList = new ArrayList<>(fileConfigList.size());
        for (SouFileConfig fileConfig : fileConfigList) {
            MqlSouOrderFileVO vo = new MqlSouOrderFileVO();
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
                MqlSouOrderFileVO vo = new MqlSouOrderFileVO();
                BeanUtils.copyProperties(orderFile, vo);
                voList.add(vo);
            }
        });

        return voList;
    }

}
