package com.midea.cloud.srm.biz.pj.file.anon.controller;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.StreamUtils;
import com.midea.cloud.srm.biz.pj.file.anon.service.FileAnonService;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * <pre>
 *
 * </pre>
 *
 * @author wangpr@meiCloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020/11/16
 *  修改内容:
 * </pre>
 */
@RestController
@RequestMapping("/files-anon")
@Slf4j
public class FileAnonController extends BaseController {

    @Autowired
    private BaseClient baseClient;



    @Autowired
    private FileAnonService fileAnonService;

    /**
     * 下载模板
     *
     * @param request request
     * @param response response
     * @param fileDownloadTemplateCode fileDownloadTemplateCode
     * @throws Exception Exception
     */
    @RequestMapping("/file/fileupload/downloadTemplate/{fileDownloadTemplateCode}")
    public void downloadTemplate(HttpServletRequest request, HttpServletResponse response, @PathVariable("fileDownloadTemplateCode") String fileDownloadTemplateCode) throws Exception {
        if (StringUtils.isBlank(fileDownloadTemplateCode)) {
            throw new BaseException("模板编码不能为空");
        }

        List<DictItem> dictItemList = baseClient.listDictItemByDictCode("FILE_DOWNLOAD_TEMPLATE_CODE");
        DictItem dictItem = null;
        for (DictItem tempItem : dictItemList) {
            if (fileDownloadTemplateCode.equals(tempItem.getDictItemCode())) {
                dictItem = tempItem;
                break;
            }
        }
        if (dictItem == null || StringUtils.isBlank(dictItem.getDictItemName())) {
            throw new BaseException("未配置模板路径");
        }
        String path = dictItem.getDictItemName();
        String staticTextOne = "/static/";
        String staticTextTwo = "static/";
        if (!path.startsWith(staticTextOne) && !path.startsWith(staticTextTwo)) {
            throw new BaseException("配置下载路径非法");
        }
        InputStream inputStream = null;
        try {
            inputStream = StreamUtils.getInputStream(path);
            wirte(request, response, inputStream, dictItem.getItemDescription());
        }catch (Exception e){
            throw new BaseException("输出文件流错误");
        }finally {
            if(inputStream != null){
                inputStream.close();
            }
        }


    }


    /**
     * 输出文件流到前端
     *
     * @param request request
     * @param response response
     * @param is is
     * @param fileName fileName
     * @throws IOException
     */
    public void wirte(HttpServletRequest request, HttpServletResponse response, InputStream is, String fileName) throws IOException {
//        替换空格 不然会变为加号
        fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20").replaceAll("%28", "\\(").replaceAll("%29", "\\)").replaceAll("%25", "\\%");
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            String mimeType = request.getServletContext().getMimeType(fileName);
            response.setHeader("Content-type", mimeType);
//            解决axios下载后取不到文件名的问题
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
//            解决axios下载后取不到文件名的问题
            response.setHeader("FileName", fileName);
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            ServletOutputStream out = response.getOutputStream();
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(out);
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (Exception ex) {
            log.error("文件下载失败", ex);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(bis);
            IOUtils.closeQuietly(bos);
        }
    }




    @ApiOperation(value = "智能化文件word转pdf上传", notes = "智能化文件word转pdf上传接口", httpMethod = "POST")
    @PostMapping("/file/wordTransPdf")
    public Fileupload wordTransPdf(@RequestParam Long fileId, @RequestParam String fileName) throws Exception {
        return fileAnonService.wordToPdf(fileId, fileName);
    }



}
