package com.midea.cloud.srm.cm.contract.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.EasyExcelUtil;
import com.midea.cloud.common.utils.PdfUtil;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.srm.cm.contract.service.IContractExtService;
import com.midea.cloud.srm.cm.contract.utils.EggClient;
import com.midea.cloud.srm.model.cm.contract.constants.ContractMqlSchemaType;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.contract.dto.ContractHeadExt;
import com.midea.cloud.srm.model.contract.dto.ExcelContractMaterialDTO;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.mideacloud.common.enums.YesOrNo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

/**
 * @author 100014337
 */
@RestController
@RequestMapping("contractHead/ext/")
@Slf4j
public class ContractHeadExtController extends BaseController {

    @Autowired
    private IContractExtService contractExtService;

    @Autowired
    private QlService qlService;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private EggClient eggClient;

    @Autowired
    private QlOpenClient qlOpenClient;

    @PostMapping("/save")
    public List<Serializable> save(@RequestBody List<Record> data){
        List<Serializable> ids = null;
        try {
            ids = qlService.save(ContractMqlSchemaType.ContractHead.getType(),data);
        } catch (Exception e){
            if(ObjectUtil.isNotEmpty(e.getMessage())&&e.getMessage().contains(BaseException.class.getName())){
                throw new BaseException(getErrorMsg(e.getMessage()));
            } else {
                throw e;
            }

        }
        return ids;
    }

    private String getErrorMsg(String message) {
        return message.substring(message.indexOf(BaseException.class.getName())+BaseException.class.getName().length()+1).trim();
    }


    @ApiOperation("生成序列编码")
    @GetMapping("/generateExtContractCode")
    public String generateExtContractCode(@RequestParam("contractHeadId") Long contractHeadId){
        AssertUtils.isTrue(ObjectUtil.isNotEmpty(contractHeadId),"合同ID不能为空");
        try {
            return contractExtService.getGenerateExtCode(contractHeadId);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @ApiOperation("创建pdf")
    @GetMapping("/createExtPdf")
    public void testGeneratePdf(@RequestParam("contractHeadId") Long contractHeadId,HttpServletResponse response){
        ContractHeadExt contractHead = new ContractHeadExt();
        Record record = qlService.readByKey(ContractMqlSchemaType.ContractHead.getType(), contractHeadId,Record.class);
        contractHead.setContractHeadId(record.getLong("contractHeadId"));
        contractHead.setContent(record.getString("extContentFinal"));
        InputStream in = eggClient.upload(contractHead.getContent());
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PdfUtil.removeBlankPdfPages(in, byteArrayOutputStream);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            PdfUtil.addPageCount(byteArrayInputStream, byteArrayOutputStream);
            IOUtils.write(byteArrayOutputStream.toByteArray(),response.getOutputStream());
            PdfUtil.getServletOutputStream(response, "abc");;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @ApiOperation("临采定价生成合同接口")
    @PostMapping("/createTempProcure")
    public List<Serializable> createTempProcure(@RequestBody List<String> fixPriceLineIds){
        return contractExtService.createContractByFixPriceLine(fixPriceLineIds);
    }

    @ApiOperation("集采生成合同接口")
    @PostMapping("/createCentPurchase")
    public List<Serializable> createCentPurchase(@RequestBody List<String> fixPriceLineIds){
        return contractExtService.createContractByJcFixPriceLine(fixPriceLineIds);
    }

    @ApiOperation("分析Excel")
    @PostMapping("/analyzeExcel")
    public List<ExcelContractMaterialDTO> analyzeExcel(MultipartFile file){
        return contractExtService.readExcelWithContractMaterial(file);
    }

    @ApiOperation("下载excel")
    @GetMapping("/downloadMaterialModel")
    public void getMaterialExcelModel(HttpServletResponse response)  {
        try {
            Resource resource = resourceLoader.getResource("classpath:template/合同明细导入模板.xlsx");
//            log.info(resource.getURI().getPath());
            InputStream inputStream = resource.getInputStream();
            AssertUtils.isTrue(ObjectUtil.isNotEmpty(inputStream),"模板不存在");
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            OutputStream outputStream = EasyExcelUtil.getServletOutputStream(response,"合同明细导入模板");
            workbook.write(outputStream);
        } catch (Exception e){
            e.printStackTrace();
            throw new BaseException("下载失败");
        }
    }

    @RequestMapping({"/pdfAddPage"})
    @ApiOperation(
            value = "加页码，去空白页",
            notes = "加页码，去空白页"
    )
    public void pdfAddWatermark(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws Exception {
        Assert.notNull(file, LocaleHandler.getLocaleMsg("文件不能为空！"));
        InputStream inputStream = file.getInputStream();
        ServletOutputStream outputStream = PdfUtil.getServletOutputStream(response, "abc");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfUtil.removeBlankPdfPages(inputStream, byteArrayOutputStream);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        PdfUtil.addPageCount(byteArrayInputStream, byteArrayOutputStream);
        IOUtils.write(byteArrayOutputStream.toByteArray(),outputStream);
    }


    @ApiOperation("校验供应商是否黑名单、是否重点关注对象")
    @GetMapping("/checkVendor")
    public JSONObject checkVendor(Long vendorId){
        Assert.notNull(vendorId,"vendorId不能为空");
        List<RecordDTO> companyInfoList = qlOpenClient.query(ContextPath.SUP, QlOpenWrappers.query(MqlType.SUPPLIER).eq(CompanyInfo::getCompanyId, vendorId));
        RecordDTO companyInfo = null;
        if(!CollectionUtils.isEmpty(companyInfoList)) {
            companyInfo = companyInfoList.get(0);
        }

        if(companyInfo == null){
            throw new BaseException("找不到对应的供应商");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("vendorId", companyInfo.get(CompanyInfo::getCompanyId));
        jsonObject.put("isBlack", companyInfo.get(CompanyInfo::getIsBacklist));
        jsonObject.put("focusFlag", companyInfo.getString("focusFlag"));

        return jsonObject;
    }







}
