package com.midea.cloud.srm.base.pjfile.controller;

import com.midea.cloud.srm.base.pjfile.service.FileTransformRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <pre>
 *  附件
 * </pre>
 *
 * @author luxc18@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/12/26 11:04
 *  修改内容:
 * </pre>
 */
@RestController
@RequestMapping("/pj/file")
@Api(value = "PjFileController", tags = "二开-附件迁移功能")
public class FileTransformRecordController {

    @Autowired
    private FileTransformRecordService fileTransformRecordService;

    /**
     * 下载并上传附件到SRM
     *
     * @return
     */
    @GetMapping("/fileTransform")
    @ApiOperation(value = "下载并上传附件到SRM", notes = "下载并上传附件到SRM")
    public void fileTransform() throws IOException {
        fileTransformRecordService.fileTransform();
    }
}
