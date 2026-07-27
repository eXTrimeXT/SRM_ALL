package com.midea.cloud.srm.supcooperate.catalogonshelves.service;

import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.supcooperate.ext.catalogonshelvess.dto.OnShelvesDto;
import com.midea.cloud.srm.model.supcooperate.ext.catalogonshelvess.dto.ShopCartModelDto;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 购物车接口定义类
 * @author huangbf3
 */
public interface IShopCartService {

    /**
     * importShopCartModelDownload
     * @param response
     * @throws IOException
     */
    void importShopCartModelDownload(HttpServletResponse response) throws IOException;

    /**
     * importShopCartExcel
     * @param file
     * @param fileupload
     * @return
     */
    Map<String, Object> importShopCartExcel(MultipartFile file, Fileupload fileupload);

    /**
     * extAdd
     * @param query
     * @return
     */
    OnShelvesDto extAdd(Map<String,Object>query);
}
