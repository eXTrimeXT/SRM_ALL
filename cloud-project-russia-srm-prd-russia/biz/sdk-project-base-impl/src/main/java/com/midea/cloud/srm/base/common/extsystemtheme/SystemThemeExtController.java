package com.midea.cloud.srm.base.common.extsystemtheme;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.FileDownloadUtil;
import com.midea.cloud.common.utils.redis.RSALockUtil;
import com.midea.cloud.srm.base.systemtheme.service.SystemThemeFileService;
import com.midea.cloud.srm.base.systemtheme.service.SystemThemeService;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.model.base.systemtheme.dto.SystemThemeParam;
import com.midea.cloud.srm.model.base.systemtheme.entity.SystemThemeFile;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import feign.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *
 * </pre>
 *
 * @author kuangzm
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/7/3 10:27:09
 *  修改内容:
 * </pre>
 */
@RestController
@RequestMapping("/systemTheme")
@Api(value = "SystemThemeController", tags = "系统主题配置")
public class SystemThemeExtController extends BaseController {

    @Autowired
    private SystemThemeService systemThemeService;

    @Autowired
    private SystemThemeFileService systemThemeFileService;

    @Autowired
    private FileCenterClient fileCenterClient;

    @Autowired
    private RSALockUtil rsaLockUtil;

    @PostMapping("/save")
    @ApiOperation(value = "save", notes = "主题保存")
    public Map<String,List> save(@RequestBody SystemThemeParam param){
        systemThemeService.save(param);
        return systemThemeService.get();
    }


    @GetMapping("/get")
    @ApiOperation(value = "get", notes = "主题明细")
    public Map<String,List> get() {
        return systemThemeService.get();
    }

    @GetMapping("/file/download")
    @ApiOperation(value = "download", notes = "主题下载")
    public void download(HttpServletRequest request, HttpServletResponse response, Long fileUploadId) throws Exception {
        if (null == fileUploadId) {
            throw new BaseException("fileUploadId参数不存在");
        }
        SystemThemeFile file = systemThemeFileService.lambdaQuery().eq(SystemThemeFile::getFileId,fileUploadId).one();
        if (null == file) {
            throw new BaseException("该附件不是主题附件");
        }

        String fileKey = rsaLockUtil.encrypt(fileUploadId.toString());
        
        Response fileResponse = fileCenterClient.downloadFileByParam(new Fileupload().setFileuploadId(fileUploadId).setFileKey(fileKey));
        if (null == fileResponse) {
            throw new BaseException("附件调用异常");
        }
        FileDownloadUtil.wirte(fileResponse.body().asInputStream(), file.getFileName(), request.getServletContext(), response);
    }
}
