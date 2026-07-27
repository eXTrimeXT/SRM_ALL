package com.midea.cloud.srm.sou.agreement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.sou.agreement.dto.AreGroupDto;
import com.midea.cloud.srm.model.sou.agreement.dto.LinePageDto;
import com.midea.cloud.srm.model.sou.agreement.dto.PriceAgreementDTO;
import com.midea.cloud.srm.model.sou.agreement.dto.SccSouJcAgreementDto;
import com.midea.cloud.srm.model.sou.agreement.entity.SccSouJcAgreement;
import com.midea.cloud.srm.model.sou.agreement.entity.SccSouJcAgreementInfo;
import com.midea.cloud.srm.model.sou.agreement.excel.ExportJcHtHeadLineData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;
/**
 * 备注
 * @author huangbf3
 */
@Mapper
public interface JcAgreementMapper extends BaseMapper<SccSouJcAgreement> {

    /**
     * 获取列表
     * @param ja 参数
     * @return 列表
     */
    List<SccSouJcAgreement> agreementList(@Param("ja") SccSouJcAgreementDto ja);

    /**
     * 获取集采id
     * @param ja 参数
     * @return 列表
     */
    List<Long> agreementIdList(@Param("ja") SccSouJcAgreementDto ja);

    /**
     * 备注
     * @param agreementIds 参数
     * @return 返回
     */
    List<AreGroupDto> getAreList(@Param("agreementIds") Set<Long> agreementIds);

    /**
     * 备注
     * @param ja
     * @return
     */
    List<LinePageDto> getLinePageList(@Param("ja") LinePageDto ja);

    /**
     * 备注
     * @param orgIds
     * @param supplyAreas
     * @param materialIds
     * @return
     */
    List<PriceAgreementDTO> getValidPriceList(@Param("orgIds") List<Long> orgIds, @Param("supplyAreas") List<String> supplyAreas, @Param("materialIds") List<Long> materialIds);

    /**
     * chMa
     * @param agreementCode
     * @param newMaterialCode
     * @param effectiveStartDate
     * @param effectiveEndDate
     * @return
     */
    List<LinePageDto> chMa(@Param("agreementCode") String agreementCode, @Param("newMaterialCode") String newMaterialCode, @Param("effectiveStartDate")Date effectiveStartDate, @Param("effectiveEndDate")Date effectiveEndDate);

    /**
     * 备注
     * @param agreementId 参数
     * @return 返回
     */
    Integer getMaxLineInfoNum(@Param("agreementId") Long agreementId);

    /**
     * 协议查询报表
     * @param ja 参数
     * @return list
     */
    List<SccSouJcAgreementDto> getJcHeadLinePageList(@Param("ja") SccSouJcAgreementDto ja);

    /**
     * 导出集采和合同头加行信息
     * @param ja 参数
     * @return list
     */
    List<ExportJcHtHeadLineData> exportJcHtHeadLineData(@Param("ja") SccSouJcAgreementDto ja);

    /**
     * getjcInfoList
     * @param jcAgreementInfo
     * @return
     */
    List<SccSouJcAgreementInfo> getjcInfoList(SccSouJcAgreementInfo jcAgreementInfo);
}
