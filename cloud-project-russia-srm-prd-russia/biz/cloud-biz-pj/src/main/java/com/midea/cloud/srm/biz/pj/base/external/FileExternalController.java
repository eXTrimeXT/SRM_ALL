package com.midea.cloud.srm.biz.pj.base.external;

import com.midea.cloud.common.utils.redis.RSALockUtil;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

/**
 * <pre>
 *    提供给aps的接口，用于接收aps推送过来的数据
 * </pre>
 * @author xiewx16
 * @date 2023/4/12
 * @apiNote
 */
@Slf4j
@RestController
@RequestMapping("/external/file")
public class FileExternalController {

    @Autowired
    FileCenterClient fileCenterClient;

    @Autowired
    private RSALockUtil rsaLockUtil;

    public static final int SUCCESS_VALUE = 200;

    @RequestMapping({"/download"})
    public void download(HttpServletResponse response2,HttpServletRequest request,Fileupload fileupload) throws Exception {
        String fileKey = rsaLockUtil.encrypt(fileupload.getFileuploadId().toString());
        Response response = fileCenterClient.downloadFileByParam(new Fileupload().setFileKey(fileKey));
        if (response != null&&response.status()==SUCCESS_VALUE) {
            InputStream inputStream = response.body().asInputStream();
            this.wirte(request, response2, inputStream, fileupload.getFileSourceName());
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @RequestMapping({"/downloadByFileKey"})
    public void downloadByFileKey(HttpServletResponse response2,HttpServletRequest request,Fileupload fileupload) throws Exception {
        Response response = fileCenterClient.downloadFileByParam(fileupload);
        if (response != null&&response.status()==SUCCESS_VALUE) {
            InputStream inputStream = response.body().asInputStream();
            this.wirte(request, response2, inputStream, fileupload.getFileSourceName());
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void wirte(HttpServletRequest request, HttpServletResponse response, InputStream is, String fileName) throws IOException {
        fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20").replaceAll("%28", "\\(").replaceAll("%29", "\\)").replaceAll("%25", "\\%");
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            String mimeType = request.getServletContext().getMimeType(fileName);
            response.setHeader("Content-type", mimeType);
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
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
        } catch (Exception var23) {
            log.error("文件下载失败", var23);
        } finally {
            if (bis != null) {
                try {
                    is.close();
                    bis.close();
                } catch (IOException var22) {
                    var22.printStackTrace();
                }

                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException var21) {
                        var21.printStackTrace();
                    }
                }
            }

        }

    }

}
