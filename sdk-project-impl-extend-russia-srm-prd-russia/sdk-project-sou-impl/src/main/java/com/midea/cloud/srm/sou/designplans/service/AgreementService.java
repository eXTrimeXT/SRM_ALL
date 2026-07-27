package com.midea.cloud.srm.sou.designplans.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.sou.designplans.entity.SccSouChDemandAgreement;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @description: 协议明细服务
 * @author: 100014337
 * @create: 2023-12-20 19:38
 * @version 1.0
 **/
public interface AgreementService extends IService<SccSouChDemandAgreement> {

    /**
     * 获取协议明细列表
     * @param obj 参数
     * @return 返回值
     */
    List<SccSouChDemandAgreement> getAgreement(SccSouChDemandAgreement obj);


    /**
     * 协议明细导入
     * @param file 导入文件
     * @param projectCode 提报策划主键
     * @return 返回值
     * @throws IOException
     */
    void storageService(MultipartFile file,String projectCode) throws IOException;

}

