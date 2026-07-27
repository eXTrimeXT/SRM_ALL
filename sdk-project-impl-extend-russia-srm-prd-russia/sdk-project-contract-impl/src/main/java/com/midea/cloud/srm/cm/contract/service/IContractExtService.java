package com.midea.cloud.srm.cm.contract.service;

import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.srm.model.contract.dto.ExcelContractMaterialDTO;
import com.midea.cloud.srm.model.pj.base.organization.dto.OrganizationEditDto;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceLine;
import com.midea.cloud.srm.model.sou.purfixprice.entity.ExtPurFixPriceLineContractDTO;
import com.midea.cloud.srm.model.supplier.info.dto.InfoDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

/**
 * @author 100014336 ganyh19
 */
public interface IContractExtService {

    /**
     *
     * 生成补充协议编号
     * @param contractHeadId 合同头ID*
     * @throws InterruptedException 中断异常
     * @return 返回生成的序列号
     */
    String getGenerateExtCode(Long contractHeadId) throws InterruptedException;

    /**
     * 填充公司信息 甲方信息
     * @param contract 合同主表数据实体
     * @param organizationEditDto 公司信息
     */
    void fillCompanyInfo(Record contract, OrganizationEditDto organizationEditDto);

    /**
     * 填充供应商 乙方信息
     * @param infoDTO 供应商信息
     * @param partner partner实体
     */
    void fillSupInfo(Record partner, InfoDTO infoDTO);

    /**
     *  填充伙伴数据
     * @param contract 合同主表实体
     * @param companyId 公司ID
     * @param vendorId 供应商ID
     */
    void fillPartners(Record contract, OrganizationEditDto companyId, InfoDTO vendorId);

    /**
     * Incorporated 甲方数据
     * @param contractPartners
     * @param contract
     */
    void fillIncorporatedPartner(List<Record> contractPartners, Record contract);

    /**
     * 填充基础数据
     * @param contract 合同数据
     * @param organizationEditDto 公司信息
     * @param info 供应商信息
     * @param sourceType 来源类型
     */
    void fillBaseInfo(Record contract, OrganizationEditDto organizationEditDto, InfoDTO info, String sourceType);

    /**
     * 填充集采总数据
     * @param contract 合同头主表实体
     * @param priceLineList 集采定价单返回数据
     */
    void fillTotalInfoForPurFix(Record contract, List<ExtPurFixPriceLineContractDTO> priceLineList);

    /**
     * 填充总数据
     * @param contract 合同头主表实体
     * @param priceLineList 临采定价单数据
     */
    void fillTotalInfo(Record contract, List<ExtFixPriceLine> priceLineList);

    /**
     * 获取供应商信息
     * @param vendorId 供应商ID
     * @return 返回供应商信息
     */
    InfoDTO getVendorInfo(Long vendorId);

    /**
     * 通过临采单数据创建合同
     * @param fixPriceLineIds 临采单ID
     * @return 返回合同ID
     */
    List<Serializable> createContractByFixPriceLine(List<String> fixPriceLineIds);

    /**
     * 通过集采定价单数据创建合同
     * @param fixPriceLineIds 集采定价单ID
     * @return 返回合同ID
     */
    List<Serializable> createContractByJcFixPriceLine(List<String> fixPriceLineIds);

    /**
     * 解析Excel获取物料数据
     * @param file 导入的文件对象
     * @return 返回解析后的物料详情数据
     */
    List<ExcelContractMaterialDTO> readExcelWithContractMaterial(MultipartFile file);


}
