package com.midea.cloud.srm.sou.sourcing.spi.init.recomm;

import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.enums.SouBidAttachmentTypeEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouRecommVendorInfoDTO;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.meiql.recommvendor.service.SouRecommvendorRiskService;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouProjectMapper;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class ApiExtSouRecommVendorEditHandler implements ISouSpiBean {

    @Autowired
    private ExtSouProjectMapper projectMapper;
    @Autowired
    private BaseClient baseClient;

    @Autowired
    private SouRecommvendorRiskService recommvendorRiskService;

    private static final String SEQ_SOU_BID_NO = "SEQ_SOU_BID_NO";
    private static final String SEQ_SOU_VENDOR_NO = "SEQ_SOU_VENDOR_NO";

    private static final String SEQ_RECOMMVENDOR = "SEQ_RECOMMVENDOR";



    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public ExtSouRecommVendorEditPO formatValidateAndConvert(ApiExtSouRecommVendorInfoDTO param, boolean isCopy, String souType) {
        if (isCopy) {
            param.setIsTempSave(true);
        }

        // 1: 数据格式化及校验
        this.formatAndValidate(param, param.getIsTempSave(), isCopy, souType);
        // 2: 数据转换
        return this.convert(param, param.getIsTempSave(), souType);
    }

    /**
     * 入参格式化及校验
     * @param param 参数
     * @param isTempSave 参数
     * @param isCopy 参数
     * @param souType 参数
     */
    protected void formatAndValidate(ApiExtSouRecommVendorInfoDTO param, boolean isTempSave, boolean isCopy, String souType) {

    }

    /**
     * 数据转换 参数
     * @param param 参数
     * @param isTempSave 参数
     * @param souType 参数
     * @return 返回
     */
    protected ExtSouRecommVendorEditPO convert(ApiExtSouRecommVendorInfoDTO param, boolean isTempSave, String souType) {
        ExtSouRecommVendorEditPO projectEditPo = new ExtSouRecommVendorEditPO();
        projectEditPo.setProject(this.doConvertProject(param, isTempSave, souType));
        projectEditPo.setSouRecommendedVendor(this.doConvertRecommVendor(param, isTempSave, souType));
        projectEditPo.setVendorFileList(this.doConvertSouFile(param, isTempSave, souType));
        projectEditPo.setSouVendor(this.doConvertVendor(param, isTempSave, souType));
        //合并申请单号
        String[] split = org.apache.commons.lang3.ObjectUtils.defaultIfNull(param.getProject().getApplicantNo(), "").split(";");
        List<ExtSouDemand> souDemands = new ArrayList<>();
        for (String appNo : split) {
            if(StringUtils.isBlank(appNo)||"null".equals(appNo)) {
                continue;
            }
            ExtSouDemand extSouDemand = new ExtSouDemand();
            extSouDemand.setProjectId(projectEditPo.getProject().getProjectId());
            extSouDemand.setApplicantNo(appNo);
            extSouDemand.setDemandId(IdGenrator.generate());
            extSouDemand.setStatus(SrmConstant.NUM_ZERO);
            souDemands.add(extSouDemand);
        }
        projectEditPo.setSouDemands(souDemands);
        return projectEditPo;
    }

    @Nullable
    protected List<ExtSouVendor> doConvertVendor(ApiExtSouRecommVendorInfoDTO param, boolean isTempSave, String souType) {

        List<ExtSouVendor> extSouVendorList = new ArrayList<ExtSouVendor>();
        if(CollectionUtils.isNotEmpty(param.getSouVendor())) {

            List<RecommvendorDto> recommvendorDtoList = new ArrayList<>();

            for (ExtSouVendor extSouVendor : param.getSouVendor()) {
                extSouVendor.setProjectId(param.getProject().getProjectId());
                extSouVendor.setExtRecommendNo(param.getProject().getExtRecommendNo());
                if (Objects.isNull(extSouVendor.getVendorId())) {
                    extSouVendor.setVendorId(IdGenrator.generate());
                }
                RecommvendorDto recommvendorDto = new RecommvendorDto();
                BeanCopyUtil.copyProperties(recommvendorDto, extSouVendor);
                recommvendorDtoList.add(recommvendorDto);
            }

            //是否重点关注
            recommvendorRiskService.queryCompanyInfo(recommvendorDtoList);

            //爬虫接口 注册资金 成立时间 GSCP 公司地址 是否失信 是否经营异常 法人 主要人员 主要股东
            recommvendorRiskService.crawler(recommvendorDtoList, false, null);
            //返回供应商数据
            recommvendorDtoList.stream().forEach(vendor -> {
                ExtSouVendor extSouVendor = new ExtSouVendor();
                BeanCopyUtil.copyProperties(extSouVendor, vendor);
                extSouVendorList.add(extSouVendor);
            });
        }
        return extSouVendorList;
    }


    /**
     * 转换得到寻源信息
     * @param projectInfo 参数
     * @param isTempSave 参数
     * @param souType 参数
     * @return 返回
     */
    protected ExtSouProject doConvertProject(ApiExtSouRecommVendorInfoDTO projectInfo, boolean isTempSave, String souType) {
        ExtSouProject project = projectInfo.getProject();
        project.setSouType(souType);
        if (Objects.isNull(project.getProjectId())) {
            project.setProjectId(IdGenrator.generate());
            //SEQ_SOU_BID_NO
            project.setSouNo(baseClient.seqGen(SEQ_RECOMMVENDOR));
            //SEQ_SOU_VENDOR_NO
            project.setExtRecommendNo(project.getSouNo());
        }
        return project;
    }

    /**
     * 转换得到工作小组
     * @param param 参数
     * @param isTempSave 参数
     * @param souType 参数
     * @return 返回
     */
    protected ExtSouRecommendedVendor doConvertRecommVendor(ApiExtSouRecommVendorInfoDTO param, boolean isTempSave, String souType) {
        ExtSouRecommendedVendor extSouRecommendedVendor = param.getSouRecommendedVendor();
        if (ObjectUtils.isEmpty(extSouRecommendedVendor)) {
            return null;
        }
        extSouRecommendedVendor.setProjectId(param.getProject().getProjectId());
        extSouRecommendedVendor.setExtRecommendNo(param.getProject().getExtRecommendNo());
        if (Objects.isNull(extSouRecommendedVendor.getRecommendedVendorId())) {
            extSouRecommendedVendor.setRecommendedVendorId(IdGenrator.generate());
        }
        return extSouRecommendedVendor;
    }

    /**
     * 转换得到招标附件
     * @param projectInfo 参数
     * @param isTempSave 参数
     * @param souType 参数
     * @return 返回
     */
    protected List<ExtSouFile> doConvertSouFile(ApiExtSouRecommVendorInfoDTO projectInfo, boolean isTempSave, String souType) {
        List<ExtSouFile> souFileList = new ArrayList<>();
        AtomicInteger index = new AtomicInteger(1);
        if (CollectionUtils.isNotEmpty(projectInfo.getVendorFileList())) {
            projectInfo.getVendorFileList().stream().forEach(file -> {
                file.setFileType(SouBidAttachmentTypeEnum.VENDOR.getCode());
                file.setProjectId(projectInfo.getProject().getProjectId());
                file.setExtRecommendNo(projectInfo.getProject().getExtRecommendNo());
                if (Objects.isNull(file.getSouFileId())) {
                    file.setSouFileId(IdGenrator.generate());
                }
                file.setSortIndex(index.getAndAdd(1));
                souFileList.add(file);
            });
        }
        return souFileList;
    }

}
