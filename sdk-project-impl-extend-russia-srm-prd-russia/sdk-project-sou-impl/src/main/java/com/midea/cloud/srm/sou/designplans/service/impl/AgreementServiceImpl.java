package com.midea.cloud.srm.sou.designplans.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.srm.model.sou.designplans.dto.AgreementExcelDto;
import com.midea.cloud.srm.model.sou.designplans.entity.SccSouChDemandAgreement;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.sou.designplans.mapper.AgreementMapper;
import com.midea.cloud.srm.sou.designplans.service.AgreementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: 协议明细实现类
 * @description:
 * @author: 100014337
 * @create: 2023-12-20 19:42
 * @version 1.0
 **/
@Slf4j
@Service
public class AgreementServiceImpl extends BaseServiceImpl<AgreementMapper, SccSouChDemandAgreement> implements AgreementService {

    @Resource
    private AgreementMapper agreementMapper;

    @Override
    public List<SccSouChDemandAgreement> getAgreement(SccSouChDemandAgreement obj) {
        return null;
    }

    /**
     * @describe 协议明细导入逻辑
     * @author: 100014337
     * @date: 2023/12/20
     * @param: file
     * @param: designId
     * @return
     **/
    @Override
    public void storageService(MultipartFile file, String projectCode) throws IOException {
        EasyExcel.read(file.getInputStream(), AgreementExcelDto.class, new ReadListener<AgreementExcelDto>() {
            // 读取数据初始化值
            private static final int BATCH_COUNT = 50;
            private final List<SccSouChDemandAgreement> list = new ArrayList<>();
            @Override
            public void invoke(AgreementExcelDto data, AnalysisContext analysisContext) {
                SccSouChDemandAgreement agreement = SouObjectXUtil.convertTargetObj(data,SccSouChDemandAgreement.class);
                agreement.setProjectCode(projectCode);
                list.add(agreement);
                // 达到BATCH_COUNT了，需要去存储一次数据库，数据库过多在内存，容易OOM
                if (list.size() >= BATCH_COUNT) {
                    try {
                        /**
                         * 插入数据库
                         **/
                        saveOrUpdateBatch(list);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    //存储完成清理list
                    list.clear();
                }
                }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                if (list.size() == 0) {
                    return;
                }
                try {
                    /**
                     * 插入数据库
                     **/
                    saveOrUpdateBatch(list);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                log.info("所有数据解析完成！");
            }
        }).sheet().doRead();
    }
}

