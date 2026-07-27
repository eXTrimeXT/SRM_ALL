package com.midea.cloud.srm.sou.inq.ext.excelhandler;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.EasyExcelUtil;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtInqSouOrderItemImportDTO;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
/**
 * 备注
 * @author huangbf3
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtInqSouOrderItemImportConverter {

    @Autowired
    private FileCenterClient fileCenterClient;

    public ExtInqSouOrderItemImportListener convert(long projectId, Integer round, long vendorId, boolean isBuyer, MultipartFile file, Fileupload fileupload) {
        ExtInqSouOrderItemImportListener listener = new ExtInqSouOrderItemImportListener(projectId, round, vendorId);
        // 1: 读取excel数据
        this.readData(file, listener);
        // 2: 检测读取过程中是否记录到错误，如果有，就返回错误信息
        if (listener.isHasError()) {
            fileupload = EasyExcelUtil.uploadErrorFile(fileCenterClient, fileupload, listener.getDtoList(), ExtInqSouOrderItemImportDTO.class, file);
            listener.setErrFileDocId(fileupload.getFileuploadId());
            return listener;
        }
        return listener;
    }

    /**
     * 读取excel中的数据
     * @param file 参数
     * @param listener 参数
     */
    private void readData(MultipartFile file, ExtInqSouOrderItemImportListener listener) {
        try {
            // 获取输入流
            InputStream inputStream = file.getInputStream();
            // 数据收集器
            ExcelReader excelReader = EasyExcel.read(inputStream, listener).build();
            // 第一个sheet读取类型
            ReadSheet readSheet = EasyExcel.readSheet(0)
                    .head(ExtInqSouOrderItemImportDTO.class)
                    .build();
            // 读取数据
            excelReader.read(readSheet);
        } catch (IOException e) {
            throw new BaseException("excel解析出错");
        }
    }

}
