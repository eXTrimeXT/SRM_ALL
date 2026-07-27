package com.midea.cloud.srm.sou.meiql.bidnotices.service;

import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDTO;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDetailDTO;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDetailTemplateDTO;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeInternalTemplateDTO;
import com.midea.cloud.srm.model.sou.ca.dto.CaDTO;
import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
public interface BidNoticeService {
    /**
     * 备注
     * @param ca 参数
     * @return 返回
     * @throws Exception
     */
    BidNoticeDTO add(CaDTO ca) throws Exception;

    /**
     * 备注
     * @param projectId 参数
     * @return 返回
     * @throws Exception
     */
    BidNoticeDTO add(Long projectId) throws Exception;

    /**
     * 生成落标附件
     * @param extProjectNo 参数
     * @param vendorName 参数
     * @param souName 参数
     * @return 返回
     */
    Long addLostBidFile(String extProjectNo,String vendorName,String souName);

    /**
     * 备注
     * @param extProjectNo 参数
     * @param vendorName 参数
     * @param souName 参数
     * @param now 参数
     * @param fileName 参数
     * @return 返回
     */
    Long saveFile(String extProjectNo,String vendorName,String souName,String now,String fileName);

    /**
     * 备注
     * @param fileuploadId 参数
     * @param companyName 参数
     * @param now 参数
     * @param fileName 参数
     * @return 返回
     */
    Long signCar(Long fileuploadId,String companyName,String now,String fileName);

    /**
     * 备注
     * @param fileuploadId 参数
     * @param companyName 参数
     * @param now 参数
     * @param fileName 参数
     * @return 返回
     */
    Long signGoup(Long fileuploadId,String companyName,String now,String fileName);

    /**
     * 备注
     * @param fileuploadId 参数
     * @param signPath 参数
     * @param now 参数
     * @param fileName 参数
     * @return 返回
     */
    Long signName(Long fileuploadId,String signPath,String now,String fileName);

    /**
     * 备注
     * @param fileuploadId 参数
     * @param companyName 参数
     * @param now 参数
     * @param fileName 参数
     * @return 返回
     */
    Long signCarWin(Long fileuploadId,String companyName,String now,String fileName);

    /**
     * 备注
     * @param fileuploadId 参数
     * @param companyName 参数
     * @param now 参数
     * @param fileName 参数
     * @return 返回
     */
    Long signGroupWin(Long fileuploadId,String companyName,String now,String fileName);

    /**
     * 备注
     * @param type 参数
     * @param extOrgBuCode 参数
     * @param fileuploadId 参数
     * @param fileName 参数
     * @param now 参数
     * @return 返回
     */
    Long signByType(String type,String extOrgBuCode,Long fileuploadId,String fileName,String now);

    /**
     * 备注
     * @param id 参数
     * @param type 参数
     * @param extOrgBuCode 参数
     */
    void signByType(Long id,String type,String extOrgBuCode);

    /**
     * 合同创建
     * @param internalId
     * @return
     */
    public List<Record> createContract (Long internalId);

    /**
     * 项目合同模板数据
     * @param bidNoticeDetailId
     * @return
     */
    BidNoticeDetailTemplateDTO getBidNoticeDetailInfo(Long bidNoticeDetailId);

    /**
     * 项目合同模板内部通知
     * @param bidNoticeInternalId
     * @return
     * @throws Exception
     */
    BidNoticeInternalTemplateDTO getBidNoticeInternalTemplateInfo(Long bidNoticeInternalId) throws Exception;
}
