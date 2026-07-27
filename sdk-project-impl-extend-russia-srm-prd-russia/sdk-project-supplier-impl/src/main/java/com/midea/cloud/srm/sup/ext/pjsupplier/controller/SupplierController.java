package com.midea.cloud.srm.sup.ext.pjsupplier.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.esotericsoftware.minlog.Log;
import com.midea.cloud.common.enums.ApproveStatusType;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.model.base.black.entity.Black;
import com.midea.cloud.srm.model.base.black.entity.BlackCompany;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.sies.client.SiesClient;
import com.midea.cloud.srm.sup.black.service.BlackCompanyService;
import com.midea.cloud.srm.model.sup.black.dto.BlackCompanyInfo;
import com.midea.cloud.srm.model.sup.black.dto.BlackCompanyMqlDTO;
import com.midea.cloud.srm.sup.ext.pjsupplier.service.PjSupplierRiskService;
import com.midea.cloud.srm.sup.ext.pjsupplier.service.PjSupplierService;
import com.midea.cloud.srm.sup.info.service.ICompanyInfoService;
import com.midea.cloud.srm.sup.invite.service.InviteVendorService;
import com.midea.cloud.srm.utils.MqlType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 供应商清单二开
 * @author 100014323
 */
@Api(value = "SupplierController", tags = {"供应商清单对接外部接口二开"})
@RestController
@RequestMapping("/pj/companyInfo")
@Slf4j
public class SupplierController {

    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    @Autowired
    private QlService qlService;

    @Autowired
    private BlackCompanyService blackCompanyService;

    @Autowired
    private SiesClient importClient;

    @Autowired
    private PjSupplierService pjSupplierService;

    @Autowired
    private PjSupplierRiskService pjSupplierRiskService;

    private final String BAN_COOP = "禁止合作";

    /**
     * 查询供应商风险-黑名单
     * @param vendorIdList
     * @return
     */
    @PostMapping("/querySupplierRiskBlacklist")
    @ApiOperation("查询供应商风险-黑名单")
    List<Record> querySupplierRiskBlacklist(@RequestBody List<Long> vendorIdList) {
        try {
            return pjSupplierRiskService.querySupplierRiskBlacklist(vendorIdList);
        } catch (Exception e) {
            log.error("querySupplierRiskBlacklist Exception", e);
            throw new BaseException(e.getMessage());
        }
    }


    /**
     * 阳光诚信系统供应商黑名单
     *
     * @return
     */
    @PostMapping("/queryIfBlackCompany")
    public String queryIfBlackCompany(@RequestBody CompanyInfo companyInfo) {

        if(StringUtils.isBlank(companyInfo.getLcCode()) && StringUtils.isBlank(companyInfo.getIdNumber())) {
            throw new BaseException("社会统一信用编码或者身份证号码不能同时为空");
        }
        LambdaQueryWrapper<BlackCompany> queryWrapper = new LambdaQueryWrapper<>();
        String lcCode = companyInfo.getLcCode();
        if(StringUtils.isBlank(lcCode)) {
            lcCode = companyInfo.getIdNumber();
        }
        // 先查询是否在黑名单已存在
        List<BlackCompany> list = blackCompanyService.list(Wrappers.lambdaQuery(BlackCompany.class)
                .eq(BlackCompany::getSocialCreditCode, lcCode));
        if(CollectionUtils.isNotEmpty(list)){
            return BAN_COOP;
        }

        // 调用外部接口
        BlackCompanyInfo blackCompanyInfo = pjProjectExtClient.blackcompanyInfo(lcCode);
        if (blackCompanyInfo != null) {
            if (BAN_COOP.equals(blackCompanyInfo.getCompanyType())) {
                // 新增黑名单
                Black black = new Black();
                black.setApproveStatus(ApproveStatusType.APPROVED.getValue());
                List<Serializable> idList = qlService.create(MqlType.BLACK, MeiQl.toListValue(Arrays.asList(black), Record.class));
                Long id = (Long) idList.get(0);
                BlackCompanyMqlDTO blackCompany = new BlackCompanyMqlDTO();
                blackCompany.setBlackId(id);
                blackCompany.setCompanyName(blackCompanyInfo.getCompanyName());
                blackCompany.setSocialCreditCode(blackCompanyInfo.getTaxCode());
                blackCompany.setDataSource("阳光诚信");
                blackCompany.setLegalPerson(blackCompanyInfo.getLegalRepresent());
                blackCompany.setShareholder(blackCompanyInfo.getShareholder());
                blackCompany.setReason(blackCompanyInfo.getQuestion());
                qlService.create(MqlType.BLACK_COMPANY, MeiQl.toListValue(Arrays.asList(blackCompany), Record.class));

            }
            return blackCompanyInfo.getCompanyType();
        }
        return "";
    }

    @GetMapping("/getGscpStatus")
    public String getGscpStatus(@RequestParam Long companyId) {
        Assert.isTrue(companyId != null, "供应商id不能为空");
        try {
            String gscpStatus = pjProjectExtClient.importScreening(companyId);
            if (StringUtils.isNotEmpty(gscpStatus)) {
                qlService.updateByWrapper(QlWrappers.update(MqlType.SUPPLIER)
                        .set("gscpStatus", gscpStatus)
                        .eq("companyId", companyId));
            }
            return gscpStatus;
        } catch (Exception e) {
            Log.error("调用接口GSCP状态失败，请重试");
            throw new BaseException("调用接口GSCP状态失败，请重试");
        }
    }

    /**
     * 供应商信息提交后,如果是邀请供应商进来的,推送消息到邀请人的钉钉
     *
     * @param companyId
     * @return
     */
    @GetMapping("/sendDingDingMsg")
    public void sendDingDingMsg(@RequestParam Long companyId) {
        pjSupplierService.sendDingDingMsg(companyId);
    }

    /**
     * 导入模板下载
     *
     * @param response
     * @throws IOException
     */
    @ApiOperation(value = "导入模板下载(新MQL)", notes = "导入模板下载(新MQL)")
    @GetMapping("/importExcelTemplate")
    public void importExcelTemplate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        importClient.downloadTemplate(this.getClass(), response);
    }

    /**
     * 绿色通道导入-(新mql)
     *
     * @param file
     * @param fileupload
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "绿色通道导入-(新mql)", notes = "绿色通道导入-(新mql)")
    @PostMapping("/importExcel")
    public Map<String, Object> importExcel(@RequestParam("file") MultipartFile file, Fileupload fileupload, HttpServletRequest request) {
        return importClient.importExcel(this.getClass(), file, fileupload, request).getResult();
    }

    @GetMapping("/getMdmCodeByCompanyId")
    public void getMdmCodeByCompanyId(@RequestParam Long companyId) {
        pjSupplierService.getMdmCodeByCompanyId(companyId);
    }

}
