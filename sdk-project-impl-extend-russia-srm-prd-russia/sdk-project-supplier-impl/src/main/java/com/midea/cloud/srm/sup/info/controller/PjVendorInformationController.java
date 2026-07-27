package com.midea.cloud.srm.sup.info.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.srm.model.common.enums.UserType;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.sup.info.dto.ExtManagementAttachDTO;
import com.midea.cloud.srm.model.supplier.info.dto.ManagementAttachRequestDTO;
import com.midea.cloud.srm.sies.client.SiesClient;
import com.midea.cloud.srm.sup.info.service.VendorInformationPjService;
import com.midea.cloud.srm.sup.info.service.impl.VendorInformationPjServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;


/**
 * <pre>
 *  供应商信息
 * </pre>
 *
 * @author liangwl23@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/9/9 19:57
 *  修改内容:
 * </pre>
 * @date 2022/04/20
 */
@Api(value = "PjVendorInformationController", tags = {"供应商信息二开"})
@RestController
@RequestMapping("/pj")
@Slf4j
public class PjVendorInformationController {

    @Autowired
    private VendorInformationPjService vendorInformationPjService;

    @Autowired
    private SiesClient importClient;
    /**
     * 证件到期提醒查询 分页
     * @param
     */
    @ApiOperation(value = "证件到期提醒查询分页", notes = "证件到期提醒查询分页", httpMethod = "POST")
    @PostMapping("/info/vendorInformation/listManagementAttachPageByDTO")
    public PageInfo<ExtManagementAttachDTO> listManagementAttachPageByDTO(@RequestBody ManagementAttachRequestDTO managementAttachRequestDTO) {
        LoginAppUser loginAppUser=AppUserUtil.getLoginAppUser();
        boolean pass = StringUtils.isEmpty(loginAppUser.getUserType())
                || (UserType.VENDOR.name().equals(loginAppUser.getUserType()) && loginAppUser.getCompanyId() == null);
        if (pass) {
            return new PageInfo<ExtManagementAttachDTO>(new ArrayList<>());
        }
        if(UserType.VENDOR.name().equals(loginAppUser.getUserType())){
            managementAttachRequestDTO.setCompanyCode(loginAppUser.getCompanyCode());
        }
        //查询日期到期前15天或者是已过期
        managementAttachRequestDTO.setEndDate(VendorInformationPjServiceImpl.getDateByAddingDays(15));
        log.info("endDate:{}",DateUtil.parseDateToStr(managementAttachRequestDTO.getEndDate(),"yyyy-MM-dd"));
        return vendorInformationPjService.listManagementAttachPage(managementAttachRequestDTO);
    }


    @ApiOperation(value = "绿色通道导入模版下载-(个人)", notes = "绿色通道导入模版下载-(个人)")
    @GetMapping("/info/companyInfo/person/importExcelTemplate")
    public void importExcelTemplate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        importClient.downloadTemplate(this.getClass(), response);
    }

    @ApiOperation(value = "绿色通道导入-(个人)", notes = "绿色通道导入-(个人)")
    @PostMapping("/info/companyInfo/person/importExcel")
    public Map<String, Object> importExcel(@RequestParam("file") MultipartFile file, Fileupload fileupload, HttpServletRequest request) {
        return importClient.importExcel(this.getClass(), file, fileupload, request).getResult();
    }
}
