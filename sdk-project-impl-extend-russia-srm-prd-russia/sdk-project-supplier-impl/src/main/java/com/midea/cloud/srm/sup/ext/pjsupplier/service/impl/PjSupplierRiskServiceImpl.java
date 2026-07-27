package com.midea.cloud.srm.sup.ext.pjsupplier.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.midea.cloud.common.enums.ApproveStatusType;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.emun.PjSupplierExceptionTypeEmun;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.model.base.black.entity.Black;
import com.midea.cloud.srm.model.base.black.entity.BlackCompany;
import com.midea.cloud.srm.model.sup.black.dto.BlackCompanyInfo;
import com.midea.cloud.srm.model.sup.black.dto.BlackCompanyMqlDTO;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.sup.black.service.BlackCompanyService;
import com.midea.cloud.srm.sup.ext.pjsupplier.service.PjSupplierRiskService;
import com.midea.cloud.srm.utils.MqlType;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * @author luxc18
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
@Api("供应商风险-黑名单")
public class PjSupplierRiskServiceImpl implements PjSupplierRiskService {
    @Autowired
    private QlService qlService;

    @Autowired
    private BlackCompanyService blackCompanyService;

    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    @Override
    public List<Record> querySupplierRiskBlacklist(List<Long> vendorIdList) {
        if (CollectionUtils.isEmpty(vendorIdList)) {
            return new ArrayList<>();
        }
        //查询供应商主数据
        List<Record> companyInfos = qlService.queryByWrapper(QlWrappers.query(MqlType.SUPPLIER)
                .in("companyId", vendorIdList), Record.class);
        if (CollectionUtils.isEmpty(companyInfos)) {
            return new ArrayList<>();
        }

        List<String> lcCodeList = companyInfos.stream().map(r -> {
            String lcCode = r.get(CompanyInfo::getLcCode);
            //支持个人
            if(StringUtils.isBlank(lcCode)) {
                lcCode = r.get(CompanyInfo::getIdNumber);
            }
            return lcCode;
        }).collect(Collectors.toList());

        //查询黑名单表
        List<BlackCompany> list = blackCompanyService.list(Wrappers.lambdaQuery(BlackCompany.class)
                .in(BlackCompany::getSocialCreditCode, lcCodeList));
        //组装数据
        Map<Long, BlackCompany> blackCompanyMap = list.stream().collect(Collectors.toMap(k -> k.getCompanyId(), Function.identity(), (k1, k2) -> k2));

        //返回结果
        List<Record> companyInfoList = new ArrayList<>();
        //需要调用阳光诚信接口
        companyInfos.stream().forEach(companyInfo -> {
            if (blackCompanyMap.containsKey(companyInfo.get(CompanyInfo::getCompanyId))) {
                companyInfo.put(CompanyInfo::getIsBacklist, YesOrNo.YES.getValue());
                companyInfoList.add(companyInfo);
            } else {
                companyInfoList.add(blackInfoFromSunshineCredit(companyInfo));
            }
        });

        return companyInfoList;
    }

    protected Record blackInfoFromSunshineCredit(Record companyInfo) {
        //查询阳光诚信接口
        String lcCode = companyInfo.get(CompanyInfo::getLcCode);
        if(StringUtils.isBlank(lcCode)) {
            lcCode = companyInfo.get(CompanyInfo::getIdNumber);
        }
        BlackCompanyInfo blackCompanyInfo = pjProjectExtClient.blackcompanyInfo(lcCode);
        if (ObjectUtils.allNotNull(blackCompanyInfo)) {
            //区分类型
            switch (ObjectUtils.defaultIfNull(blackCompanyInfo.getCompanyType(), "")) {
                case "禁止合作":
                    //生成黑名单数据
                    createBlacklist(blackCompanyInfo);
                    //添加异常记录
                    addCompanyExcetion(companyInfo.get(CompanyInfo::getCompanyId), PjSupplierExceptionTypeEmun.BLACK);
                    //更新标识
                    companyInfo.put(CompanyInfo::getIsBacklist, YesOrNo.YES.getValue());
                    break;
                case "重点关注":
                    //供应商清单记录是否重点关注为是
                    companyInfo.put("focusFlag", YesOrNo.YES.getValue());
                    qlService.update(MqlType.SUPPLIER, Collections.singletonList(companyInfo));
                    //添加异常记录
                    addCompanyExcetion(companyInfo.get(CompanyInfo::getCompanyId), PjSupplierExceptionTypeEmun.BLACK);
                    //更新标识
                    companyInfo.put(CompanyInfo::getIsBacklist, YesOrNo.NO.getValue());
                    break;
                default:
                    companyInfo.put(CompanyInfo::getIsBacklist, YesOrNo.NO.getValue());
            }
        } else {
            //添加异常记录
            addCompanyExcetion(companyInfo.get(CompanyInfo::getCompanyId), PjSupplierExceptionTypeEmun.BLACK);

            companyInfo.put(CompanyInfo::getIsBacklist, YesOrNo.NO.getValue());
        }
        return companyInfo;
    }


    protected void createBlacklist(BlackCompanyInfo blackCompanyInfo) {
        // 新增黑名单
        Black black = new Black();
        black.setApproveStatus(ApproveStatusType.APPROVED.getValue());
        List<Serializable> idList = qlService.create(MqlType.BLACK, MeiQl.toListValue(Arrays.asList(black), Record.class));
        Long id = (Long) idList.get(0);
        BlackCompanyMqlDTO blackCompany = new BlackCompanyMqlDTO();
        blackCompany.setBlackId(id);
        blackCompany.setCompanyName(blackCompanyInfo.getCompanyName());
        blackCompany.setSocialCreditCode(blackCompanyInfo.getTaxCode());
        blackCompany.setDataSource("阳光诚信");
        blackCompany.setLegalPerson(blackCompanyInfo.getLegalRepresent());
        blackCompany.setShareholder(blackCompanyInfo.getShareholder());
        blackCompany.setReason(blackCompanyInfo.getQuestion());
        qlService.create(MqlType.BLACK_COMPANY, MeiQl.toListValue(Arrays.asList(blackCompany), Record.class));
    }

    protected void addCompanyExcetion(Long companyId, PjSupplierExceptionTypeEmun type) {
        List<Record> companyExceptionInfoList = qlService.query(MqlType.NPM_COMPANY_EXCEPTION_INFO, MeiQl.newCondition()
                .eq("companyId", companyId).eq("exceptionType", type.name()), Record.class);
        if(CollectionUtils.isNotEmpty(companyExceptionInfoList)) {
            return;
        }
        Record companyExceptionInfo = new Record();
        companyExceptionInfo.put("companyId", companyId);
        companyExceptionInfo.put("exceptionType", type.name());

        qlService.save(MqlType.NPM_COMPANY_EXCEPTION_INFO, Collections.singletonList(companyExceptionInfo));
    }
}
