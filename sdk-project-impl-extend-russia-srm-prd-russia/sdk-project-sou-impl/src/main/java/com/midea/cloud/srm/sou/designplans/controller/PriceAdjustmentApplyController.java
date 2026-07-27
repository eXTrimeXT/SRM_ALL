package com.midea.cloud.srm.sou.designplans.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.EasyExcelUtil;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.model.sou.designplans.dto.AgreementExcelDto;
import com.midea.cloud.srm.model.sou.designplans.dto.PaaAdjustDto;
import com.midea.cloud.srm.model.sou.designplans.entity.*;
import com.midea.cloud.srm.model.sou.designplans.enums.PaaAdjustEnums;
import com.midea.cloud.srm.model.sou.openapi.inq.dto.init.ApiInqSouInitDTO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.sou.constants.NumConstant;
import com.midea.cloud.srm.sou.constants.SouConstant;
import com.midea.cloud.srm.sou.designplans.mapper.AgreementMapper;
import com.midea.cloud.srm.sou.designplans.mapper.PaaAdjustMapper;
import com.midea.cloud.srm.sou.designplans.mapper.PaaMarketSituationMapper;
import com.midea.cloud.srm.sou.designplans.mapper.SccSouChPaaAdjustAttMapper;
import com.midea.cloud.srm.sou.designplans.service.AgreementService;
import com.midea.cloud.srm.sou.designplans.service.LedgerService;
import com.midea.cloud.srm.sou.designplans.service.PaaAdjustService;
import com.midea.cloud.srm.sou.inq.init.controller.WebInqSouInitForBuyerController;
import com.midea.cloud.srm.sou.purfixprice.service.ExtPurFixPriceLineService;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouVendorDAO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author ex_liuxy46
 */
@Api(value = "PriceAdjustmentApplyController", tags = {"调价申请"})
@Slf4j
@RestController
@RequestMapping("/price/adjustment/apply")
public class PriceAdjustmentApplyController {

    private static final String CODE = "tjsq";
    private static final int START_NUM = 10000;
    private static final int END_NUM = 99999;

    @Resource
    private PaaAdjustMapper paaAdjustMapper;

    @Resource
    private AgreementMapper agreementMapper;

    @Resource
    private PaaAdjustService paaAdjustService;


    @Resource
    private PaaMarketSituationMapper paaMarketSituationMapper;

    @Resource
    private AgreementService agreementService;

    @Resource
    private LedgerService ledgerService;

    @Resource
    private ExtPurFixPriceLineService extPurFixPriceLineService;

    @Resource
    private SouVendorDAO souVendorDAO;

    @Resource
    private SccSouChPaaAdjustAttMapper sccSouChPaaAdjustAttMapper;



    @ApiOperation(value = "获取集采台账-调价申请列表", notes = "获取集采台账-调价申请列表", httpMethod = "POST")
    @PostMapping("/getChLedgerPageList")
    public PageInfo<PaaAdjustDto> getChLedgerPageList(@RequestBody SccSouChPaaAdjust adjust) {
        PageUtil.startPage(adjust.getPageNum(), adjust.getPageSize());
        List<PaaAdjustDto> list = paaAdjustService.getAdjustList(adjust);
        list.forEach(e -> {
            LambdaQueryWrapper<SccSouChPaaAdjustAtt> attQuery = new LambdaQueryWrapper<>();
            attQuery.eq(SccSouChPaaAdjustAtt::getAdjustId, e.getAdjustId());
            List<SccSouChPaaAdjustAtt> adjustAttList = sccSouChPaaAdjustAttMapper.selectList(attQuery);
            e.setAdjustAttList(adjustAttList);
        });
        return new PageInfo<>(list);
    }

    @ApiOperation(value = "调价申请-调整-新增或更新", notes = "调价申请-调整-新增或更新")
    @PostMapping("/saveOrUpdatePaaAdjust")
    public PaaAdjustDto saveOrUpdatePaaAdjust(@RequestBody PaaAdjustDto paaAdjustDto) {
        checkThrow(paaAdjustDto == null,"查询参数为空");
        checkThrow(ObjectUtil.isEmpty(paaAdjustDto.getJcId()), "项目ID不能为空");
        SccSouChPaaAdjust adjust = SouObjectXUtil.convertTargetObj(paaAdjustDto, SccSouChPaaAdjust.class);
        SccSouChLedger ledger = new SccSouChLedger();
        ledger.setLedgerId(paaAdjustDto.getJcId());
        adjust.setNum(Optional.of(ledgerService.getLedgerList(ledger).stream()
                .map(SccSouChLedger::getNum)
                .max(Integer::compare).orElse(NumConstant.ONE)).get()+NumConstant.ONE);
        if (adjust.getAdjustId() == null) {
            String adjustCode = randomCode();
            int i = getIsExistCode(adjustCode);
            if (i == 0) {
                adjust.setAdjustCode(adjustCode);
            } else {
                while (true) {
                    String co = randomCode();
                    int cou = getIsExistCode(co);
                    if (cou == 0) {
                        adjust.setAdjustCode(co);
                        break;
                    }
                }
            }
            paaAdjustMapper.insert(adjust);
        } else {
            paaAdjustMapper.updateById(adjust);
        }
        if (CollectionUtils.isNotEmpty(paaAdjustDto.getAdjustAttList())) {
            paaAdjustDto.getAdjustAttList().forEach(e -> {
                e.setAdjustId(adjust.getAdjustId());
                LambdaUpdateWrapper<SccSouChPaaAdjustAtt> attUpdate = new LambdaUpdateWrapper<>();
                attUpdate.eq(SccSouChPaaAdjustAtt::getAdjustId, e.getAdjustId());
                sccSouChPaaAdjustAttMapper.delete(attUpdate);
                e.setAdjustAttId(null);
                sccSouChPaaAdjustAttMapper.insert(e);
            });
        }
        if (PaaAdjustEnums.SUBMIT.getCode().equals(adjust.getStatus())) {
            //询比价调整
            if (NumConstant.ONE_STR.equals(adjust.getAdjustType())) {
                createInquiryComparisonPrices();
            }
            //市场行情调整
            if (NumConstant.TWO_STR.equals(adjust.getAdjustType())) {
                createMarketSituation(adjust);
            }
        }
        return SouObjectXUtil.convertTargetObj(adjust, PaaAdjustDto.class);
    }

    /**
     * 根据id删除调价申请
     *
     * @param  adjustId
     * @return 删除结果
     */
    @ApiOperation(value = "根据id删除调价申请")
    @DeleteMapping("/delete")
    public void delete(@RequestParam Long adjustId) {
        paaAdjustService.removeById(adjustId);
    }

    public int getIsExistCode(String code) {
        LambdaQueryWrapper<SccSouChPaaAdjust> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SccSouChPaaAdjust::getAdjustCode, code);
        return Math.toIntExact(paaAdjustMapper.selectCount(lqw));
    }

    public static String randomCode() {
        return CODE + System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(START_NUM, END_NUM);
    }

    /**
     * 创建询比价
     */
    public void createInquiryComparisonPrices() {
        WebInqSouInitForBuyerController fb = new WebInqSouInitForBuyerController();
        ApiInqSouInitDTO param = new ApiInqSouInitDTO();
        //todo
        fb.editInq(param);
    }

    /**
     * 创建市场行情
     */
    public void createMarketSituation(SccSouChPaaAdjust adjust) {
        SccSouChPaaMarketSituation situation = new SccSouChPaaMarketSituation();
        situation.setAdjustId(adjust.getAdjustId());
        situation.setAdjustCode(adjust.getAdjustCode());
        paaMarketSituationMapper.insert(situation);
    }

    @ApiOperation(value = "下载协议明细模板", notes = "下载协议明细模板")
    @RequestMapping("/downloadAgreementInfoTemplate")
    public void downloadReqTemplate(HttpServletResponse response) {
        try {
            InputStream inputStream = this.getClass().getResourceAsStream("/template/协议头导入模板.xlsx");
            assert inputStream != null;
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            OutputStream outputStream = EasyExcelUtil.getServletOutputStream(response, "协议信息模板");
            workbook.write(outputStream);
        } catch (Exception e) {
            throw new BaseException("下载失败");
        }
    }

    public void checkThrow(Boolean boo, String str) {
        if (boo) {
            throw new BaseException(str);
        }
    }

    @ApiOperation(value = "协议明细导入", notes = "协议明细导入")
    @PostMapping("/importAgreementInfo")
    public void importAgreementInfo(@RequestBody MultipartFile file, @RequestParam("projectCode") String projectCode) throws IOException {
        checkThrow(projectCode == null,"项目id不能为空");
        String suffix = validationExcelFile(file);
        Workbook xwb;
        if (SouConstant.XLSX.equals(suffix)) {
            xwb = new XSSFWorkbook(file.getInputStream());
        } else {
            xwb = new HSSFWorkbook(file.getInputStream());
        }
        Sheet xssfSheet = xwb.getSheetAt(NumConstant.ZERO);
        checkThrow(xssfSheet.getLastRowNum() > 1000, "数据行超过了1000行");
        agreementService.storageService(file,projectCode);
    }

    @ApiOperation(value = "供应商明细查询", notes = "新增协议明细列表", httpMethod = "POST")
    @PostMapping("/souVendorList")
    public PageInfo<ExtSouVendor>  souVendorList(@RequestBody ExtSouVendor vendor) {
        PageUtil.startPage(vendor.getPageNum(), vendor.getPageSize());
        if(ObjectUtil.isNotEmpty(vendor.getVendorId())){
            List<SouVendor> souVendors = souVendorDAO.lambdaQuery().eq(SouVendor::getProjectId, vendor.getProjectId())
                    .eq(SouVendor::getVendorId, vendor.getVendorId()).list();
            return new PageInfo(SouObjectXUtil.convertList(souVendors,ExtSouVendor.class));
        }
        List<SouVendor> souVendors = souVendorDAO.lambdaQuery().eq(SouVendor::getProjectId, vendor.getProjectId()).list();
        return new PageInfo(SouObjectXUtil.convertList(souVendors,ExtSouVendor.class));
    }

    @ApiOperation(value = "拉取协议明细列表查询", notes = "拉取协议明细列表查询", httpMethod = "POST")
    @PostMapping("/getAgreementPageList")
    public PageInfo<SccSouChDemandAgreement> agreement(@RequestBody SccSouChDemandAgreement agreement) {
        return extPurFixPriceLineService.getAgreement(agreement);
    }

    @ApiOperation(value = "协议明细导出", notes = "协议明细导出")
    @GetMapping(path = "/getAgreementUpload", produces = "application/octet-stream;charset=UTF-8")
    public void upload(HttpServletResponse response, @RequestParam("projectCode") String projectCode) throws IOException {
        LambdaQueryWrapper<SccSouChDemandAgreement> infoQuery = new LambdaQueryWrapper<>();
        infoQuery.eq(SccSouChDemandAgreement::getProjectCode, projectCode);
        List<SccSouChDemandAgreement> list = agreementService.list(infoQuery);
        String fileName = new String("协议明细".getBytes(), StandardCharsets.ISO_8859_1);
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), AgreementExcelDto.class).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).sheet().doWrite(SouObjectXUtil.convertList(list,AgreementExcelDto.class));
    }


    public String validationExcelFile(MultipartFile file) {
        checkThrow(file == null,"参数不能为空");
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        checkThrow(!"xlsx".equals(fileSuffix) && !"xls".equals(fileSuffix),"上传的文件格式不正确,只支持xlsx、xls");
        return fileSuffix;
    }

}
