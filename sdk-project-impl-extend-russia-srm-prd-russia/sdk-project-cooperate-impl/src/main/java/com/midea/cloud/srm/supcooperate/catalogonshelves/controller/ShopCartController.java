package com.midea.cloud.srm.supcooperate.catalogonshelves.controller;

import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.supcooperate.ext.catalogonshelvess.dto.OnShelvesDto;
import com.midea.cloud.srm.model.supcooperate.ext.catalogonshelvess.dto.ShopCartModelDto;
import com.midea.cloud.srm.supcooperate.catalogonshelves.service.IShopCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 购物车二开
 * @author huangbf3
 */
@Api(value = "shopCart/ext/", tags = {"购物车二开"})
@RestController
@RequestMapping("shopCart/ext/")
public class ShopCartController {

    @Autowired
    private IShopCartService iShopCartService;

    @ApiOperation(value = "下载购物车导入模板", notes = "下载购物车导入模板")
    @RequestMapping("/importShopCartModelDownload")
    public void importShopCartModelDownload(HttpServletResponse response) throws IOException {
        iShopCartService.importShopCartModelDownload(response);
    }

    /**
     * 自定义导入文件
     * @param file 导入文件
     */
    @ApiOperation(value = "自定义导入文件", notes = "自定义导入文件")
    @RequestMapping("/importShopCartExcel")
    public Map<String, Object> importShopCartExcel(@RequestParam("file") MultipartFile file, Fileupload fileupload) {
            return iShopCartService.importShopCartExcel(file, fileupload);
    }

    @ApiOperation("增加记录选择物料后反查参考价和最小起订量")
    @PostMapping("/extAdd")
    public OnShelvesDto extAdd(@RequestBody Map<String,Object>query){
        OnShelvesDto list= iShopCartService.extAdd(query);
      return list;
    }
}
