package com.midea.cloud.srm.sou.agreement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.sou.agreement.dto.AreGroupDto;
import com.midea.cloud.srm.model.sou.agreement.dto.LinePageDto;
import com.midea.cloud.srm.model.sou.agreement.dto.PriceAgreementDTO;
import com.midea.cloud.srm.model.sou.agreement.dto.SccSouJcAgreementDto;
import com.midea.cloud.srm.model.sou.agreement.entity.SccSouJcAgreement;
import com.midea.cloud.srm.model.sou.agreement.entity.SccSouJcAgreementChange;
import com.midea.cloud.srm.model.sou.agreement.entity.SccSouJcAgreementInfo;
import com.midea.cloud.srm.model.sou.agreement.entity.SccSouJcAgreementOrg;
import com.midea.cloud.srm.model.sou.agreement.excel.ExportJcHtHeadLineData;
import com.midea.cloud.srm.model.sou.agreement.excel.ImportExcelJcAgreementLineDto;
import com.midea.cloud.srm.model.sou.agreement.excel.ImportExcelJcAgreementLineEditDto;
import com.midea.cloud.srm.model.sou.agreement.excel.ImportExcelJcAgreementLineTieredDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;
/**
 * 备注
 * @author huangbf3
 */
public interface JcAgreementService extends IService<SccSouJcAgreement> {

    /**
     * 获取列表
     * @param ja 参数
     * @return 列表
     */
    List<SccSouJcAgreement> agreementList(SccSouJcAgreementDto ja);

    /**
     * 获取集采id
     * @param ja 参数
     * @return 列表
     */
    List<Long> agreementIdList(SccSouJcAgreementDto ja);

    /**
     * 备注
     * @param agreementIds 参数
     * @return 返回
     */
    List<AreGroupDto> getAreList(Set<Long> agreementIds);

    /**
     * 备注
     * @param list 参数
     */
   //void checkMaterialIsValid(List<SccSouJcAgreementInfo> list);


    /**
     * 检验
     * @param newArea
     * @param newMaterialCode
     * @param newOrg
     * @param ja
     */
    void checkMa(String[] newArea, String newMaterialCode, List<SccSouJcAgreementOrg> newOrg, SccSouJcAgreement ja);

    /**
     * 变更集采管理
     * @param oldObject 老对象
     * @param newObject 新对象
     * @return 返回
     */
    List<SccSouJcAgreementChange> changeJcAgreement(SccSouJcAgreement oldObject, SccSouJcAgreement newObject);


    /**
     * 导入协议头
     * @param file 导入的文件
     * @param suffix 参数
     * @param agreementType 类型
     * @param suffix
     * @throws IOException 异常
     */
    void importAgreementHead(MultipartFile file, String agreementType, String suffix) throws IOException;

    /**
     * 列表页面导入协议行
     * @param file 导入的文件
     * @param suffix 后缀
     * @throws IOException 报错
     * @param xyType 协议类型
     * @throws IOException
     */
    //void importAgreementLine(MultipartFile file, String suffix, String xyType) throws IOException;

    /**
     *2
     * 编辑页面导入协议行
     * @param file 导入的文件
     * @param agreementId 参数
     * @param suffix 参数
     * @throws IOException 报错
     */
    //void importEditAgreementLine(MultipartFile file, Long agreementId, String suffix) throws IOException;

    /**
     * 备注
     * @param ja
     * @return
     */
    List<LinePageDto> getLinePageList(LinePageDto ja);


    /**
     * 按区域+物料查询协议
     * @param orgIds
     * @param supplyAreas
     * @param materialIds
     * @return
     */
    List<PriceAgreementDTO> getValidPriceList(List<Long> orgIds, List<String> supplyAreas, List<Long> materialIds);

    /**
     * 导入
     * @param lineList 行列表
     * @param lineTieredList 阶梯价列表
     * @param xyType 协议类型
     */
    void importAgreementLine(List<ImportExcelJcAgreementLineDto> lineList, List<ImportExcelJcAgreementLineTieredDto> lineTieredList, String xyType);

    /**
     * 编辑页面导入协议行
     * @param lineList 行列表
     * @param lineTieredList 阶梯价列表
     * @param ja 参数
     * @throws IOException 报错
     */
    void importEditAgreementLine(List<ImportExcelJcAgreementLineEditDto> lineList, List<ImportExcelJcAgreementLineTieredDto> lineTieredList, SccSouJcAgreement ja) throws IOException;

    /**
     * 备注
     * @param agreementId 参数
     * @return 返回
     */
    Integer getMaxLineInfoNum(Long agreementId);

    /**
     * 协议查询报表
     * @param ja 参数
     * @return list
     */
    List<SccSouJcAgreementDto> getJcHeadLinePageList(SccSouJcAgreementDto ja);

    /**
     * 导出集采和合同头加行信息
     * @param ja 参数
     * @return list
     */
    List<ExportJcHtHeadLineData> exportJcHtHeadLineData(SccSouJcAgreementDto ja);

    /**
     * getjcInfoList
     * @param jcAgreementInfo
     * @return
     */
    List<SccSouJcAgreementInfo> getjcInfoList(SccSouJcAgreementInfo jcAgreementInfo);
}
