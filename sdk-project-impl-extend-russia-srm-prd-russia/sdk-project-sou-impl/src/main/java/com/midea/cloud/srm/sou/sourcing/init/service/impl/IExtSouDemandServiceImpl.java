package com.midea.cloud.srm.sou.sourcing.init.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouDemand;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouGroup;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouDemandMapper;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouGroupMapper;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouDemandService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouGroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class IExtSouDemandServiceImpl extends ServiceImpl<ExtSouDemandMapper, ExtSouDemand> implements IExtSouDemandService {

    public static final String PACK_NAME_CN = "包";

    @Override
    public List<ExtSouDemand> generatePackName(List<ExtSouDemand> demandList) {
        if(CollectionUtils.isEmpty(demandList)) {
            return demandList;
        }
        List<ExtSouDemand> souDemandList = demandList.stream().sorted(Comparator.comparing(ExtSouDemand::getApplicantNo)).collect(Collectors.toList());
        AtomicInteger index = new AtomicInteger(1);
        souDemandList.stream().forEach(d -> {
            d.setSortIndex(index.getAndAdd(1));
            d.setPackageName(newPackName(PACK_NAME_CN, d.getSortIndex()));
            d.setStatus(SrmConstant.NUM_ZERO);
        });
        return souDemandList;
    }

    /**
     * 拼接包名
     * @param packName
     * @param index
     * @return
     */
    protected String newPackName(String packName, Integer index) {
        return StringUtils.joinWith("", packName, index);
    }
}
