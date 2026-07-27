package com.midea.cloud.srm.pr.division.service;

import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pm.pr.division.entity.DivisionCategory;
import com.midea.cloud.srm.model.supcooperate.ext.division.dto.PersonInChargeUserDto;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
*  <pre>
 *  品类分工规则表 服务类
 * </pre>
*
* @author chensl26@meiCloud.com
* @version 1.00.00
*
*  <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020-07-22 08:41:41
 *  修改内容:
 * </pre>
*/
public interface IPjDivisionCategoryService{

    /**
     * 保存品类分工规则
     * @param divisionCategories
     */
    void saveOrUpdateDivisionCategory(List<DivisionCategory> divisionCategories);

    /**
     * 备注
     * @param response 参数
     * @throws IOException 报错
     */
    void importModelDownload(HttpServletResponse response) throws IOException;

    /**
     * 备注
     * @param file 参数
     * @param fileupload 参数
     * @return 返回
     */
    Map<String, Object> importExcelNew(MultipartFile file, Fileupload fileupload);

    /**
     * 批量更新品类分工负责人
     * @param personInChargeUserDto 批量更新品类分工负责人参数
     */
    void batchUpdatePersonInChargeUser(PersonInChargeUserDto personInChargeUserDto);
}
