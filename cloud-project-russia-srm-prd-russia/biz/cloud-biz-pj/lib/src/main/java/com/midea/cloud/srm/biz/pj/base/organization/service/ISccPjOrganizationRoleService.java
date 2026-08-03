package com.midea.cloud.srm.biz.pj.base.organization.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.base.organization.dto.BpmSccPjOrganizationRoleDto;
import com.midea.cloud.srm.model.pj.base.organization.entity.SccPjOrganizationRole;
import com.midea.cloud.srm.model.pj.base.organization.entity.SccPjOrganizationRoleUser;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author huangbf3
 * 组织角色 接口类
 */
public interface ISccPjOrganizationRoleService extends IService<SccPjOrganizationRole> {

    /**
     * 手动或自动同步组织角色BPM
     * @param bpmPor 传入数据
     */
    void organizationRoleBpm(BpmSccPjOrganizationRoleDto bpmPor);

    /**
     * 手动或自动同步组织角色EAS
     * @param easPor 传入数据
     * @throws Exception
     */
    void organizationRoleEas(BpmSccPjOrganizationRoleDto easPor) throws Exception;

    /**
     * 备注
     * @param username
     * @return
     */
    SccPjOrganizationRoleUser getParentUserByUsername(String username);

    /**
     * 定标审批_推送中标范围
     * @param zbfw 中标范围
     * @param zbfwcode 单据ID
     * @param caNo 定标审批单号
     */
    void pushZbfwToBpm(String zbfw,String zbfwcode,String caNo);

    /**
     * importExcelTemplate
     * @param response
     * @throws IOException
     */
    void importExcelTemplate(HttpServletResponse response) throws IOException;

    /**
     * importExcel
     * @param file
     * @param fileupload
     * @return
     * @throws IOException
     */
    Map<String,Object> importExcel(MultipartFile file, Fileupload fileupload) throws IOException;
}
