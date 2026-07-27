package com.midea.cloud.srm.sou.purinq.excelhandler;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.EasyExcelUtil;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.model.extapi.sou.purinq.dto.order.ExtPurInqSouOrderItemImportDTO;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtPurInqSouOrderItemImportConverter {

    @Autowired
    private FileCenterClient fileCenterClient;

    public ExtPurInqSouOrderItemImportListener convert(long projectId, Integer round, long vendorId, boolean isBuyer, MultipartFile file, Fileupload fileupload) {
        ExtPurInqSouOrderItemImportListener listener = new ExtPurInqSouOrderItemImportListener(projectId, round, vendorId);
        // 1: 读取excel数据
        this.readData(file, listener);
        // 2: 检测读取过程中是否记录到错误，如果有，就返回错误信息
        if (listener.isHasError()) {
            fileupload = EasyExcelUtil.uploadErrorFile(fileCenterClient, fileupload, listener.getDtoList(), ExtPurInqSouOrderItemImportDTO.class, file);
            listener.setErrFileDocId(fileupload.getFileuploadId());
            return listener;
        }
        return listener;
    }

    /**
     * 读取excel中的数据
     * @param file
     * @param listener
     */
    private void readData(MultipartFile file, ExtPurInqSouOrderItemImportListener listener) {
        try {
            // 获取输入流
            InputStream inputStream = file.getInputStream();
            // 数据收集器
            ExcelReader excelReader = EasyExcel.read(inputStream, listener).build();
            // 第一个sheet读取类型
            ReadSheet readSheet = EasyExcel.readSheet(0)
                    .head(ExtPurInqSouOrderItemImportDTO.class)
                    .build();
            // 读取数据
            excelReader.read(readSheet);
        } catch (IOException e) {
            throw new BaseException("excel解析出错");
        }
    }

}
