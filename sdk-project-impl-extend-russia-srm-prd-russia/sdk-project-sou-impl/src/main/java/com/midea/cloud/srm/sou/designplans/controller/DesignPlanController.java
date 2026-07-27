package com.midea.cloud.srm.sou.designplans.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.enums.ImportStatus;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.listener.AnalysisEventListenerImpl;
import com.midea.cloud.common.utils.*;
import com.midea.cloud.srm.feign.PjSouClient;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.sou.designplans.dto.ProjPlanDto;
import com.midea.cloud.srm.model.sou.designplans.dto.PullQueryDto;
import com.midea.cloud.srm.model.sou.designplans.dto.SupDto;
import com.midea.cloud.srm.model.sou.designplans.entity.*;
import com.midea.cloud.srm.model.sou.designplans.enums.DesignPlanEnums;
import com.midea.cloud.srm.model.sou.designplans.excel.ImportExcelReqInfoDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiSouInitDTO;
import com.midea.cloud.srm.sies.client.SiesClient;
import com.midea.cloud.srm.sou.constants.NumConstant;
import com.midea.cloud.srm.sou.designplans.mapper.*;
import com.midea.cloud.srm.sou.designplans.service.DemandSupService;
import com.midea.cloud.srm.sou.designplans.service.DemandYearDataService;
import com.midea.cloud.srm.sou.designplans.service.DesignPlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 备注
 * @author huangbf3
 */
@Api(value = "DesignPlanController", tags = {"提报策划方案"})
@Slf4j
@RestController
@RequestMapping("/design/plan")
public class DesignPlanController {

    @Resource
    private DesignPlanService designPlanService;

    @Resource
    private DemandYearDataService demandYearDataService;

    @Resource
    private DemandSupService demandSupService;

    @Resource
    private DemandProgrammeMapper demandProgrammeMapper;

    @Resource
    private DemandWorkMapper demandWorkMapper;

    @Resource
    private DemandStrategyMapper demandStrategyMapper;

    @Resource
    private DemandOtherMapper demandOtherMapper;

    @Resource
    private DemandSettingMapper demandSettingMapper;

    @Resource
    private DemandAnalysisMapper demandAnalysisMapper;

    @Resource
    private PjSouClient pjSouClient;

    @Autowired
    private SiesClient importClient;

    @Autowired
    private FileCenterClient fileCenterClient;

    private final static int NUM1=1;
    private final static int NUM2=2;
    private final static int NUM3=3;
    private final static int NUM9=9;

    @ApiOperation(value = "获取提报策划方案列表", notes = "获取提报策划方案列表", httpMethod = "POST")
    @PostMapping("/getChDesignPlanPageList")
    public PageInfo<SccSouChDesignPlan> getChDesignPlanPageList(@RequestBody SccSouChDesignPlan designPlan) {
        PageUtil.startPage(designPlan.getPageNum(), designPlan.getPageSize());
        LambdaQueryWrapper<SccSouChDesignPlan> qw = new LambdaQueryWrapper<>();
        //项目编号
        qw.like(StringUtils.isNotBlank(designPlan.getProjectCode()), SccSouChDesignPlan::getProjectCode, designPlan.getProjectCode());
        //创建人
        qw.like(StringUtils.isNotBlank(designPlan.getCreatedBy()), SccSouChDesignPlan::getCreatedBy, designPlan.getCreatedBy());
        //审核状态
        qw.eq(StringUtils.isNotBlank(designPlan.getStatus()), SccSouChDesignPlan::getStatus, designPlan.getStatus());
        //创建时间从-到
        qw.ge(designPlan.getCreationDate() != null, SccSouChDesignPlan::getCreationDate, designPlan.getCreationDate());
        qw.le(designPlan.getCreateDateEnd() != null, SccSouChDesignPlan::getCreationDate, designPlan.getCreateDateEnd());
        qw.orderByDesc(SccSouChDesignPlan::getCreationDate);
        List<SccSouChDesignPlan> list = designPlanService.list(qw);
        return new PageInfo<>(list);
    }

    public void checkThrow(Boolean boo, String str) {
        if (boo) {
            throw new BaseException(str);
        }
    }

    @ApiOperation(value = "添加或更新基础信息", notes = "添加或更新基础信息", httpMethod = "POST")
    @PostMapping("/savaOrUpdateDesignPlan")
    public SccSouChDesignPlan savaOrUpdateDesignPlan(@RequestBody SccSouChDesignPlan designPlan) {
        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        if (loginAppUser == null) {
            throw new BaseException("没有当前登录人的信息");
        }
        if (designPlan == null) {
            throw new BaseException("参数不能为空");
        }
        checkThrow(StringUtils.isBlank(designPlan.getPhone()), "联系方式不能为空");
        String code;
        if (designPlan.getDesignId() == null) {
            List<SccSouChDesignPlan> dp = designPlanService.lambdaQuery().orderByDesc(SccSouChDesignPlan::getCreationDate).list();
            if (CollectionUtils.isNotEmpty(dp)) {
                code = dp.get(NumConstant.ZERO).getProjectCode().replace("XYDC", "");
            } else {
                code = "0";
            }
            designPlan.setProjectCode("XYDC" + getNum(code));
        }
        long i = Math.toIntExact(designPlanService.lambdaQuery().eq(SccSouChDesignPlan::getProjectId, designPlan.getProjectId())
                .ne(designPlan.getDesignId() != null, SccSouChDesignPlan::getDesignId, designPlan.getDesignId()).count());
        designPlan.setNum(Convert.toInt(i) + 1);
        designPlan.setStatus("DRAFT");
        HrUserOrgnizationDto hrUser = pjSouClient.getHrUserOrgnizationByUsername(loginAppUser.getUsername());
        Assert.notNull(hrUser, "获取hr用户失败");
        Organization orgBu = hrUser.getBuOrganization();
        if (orgBu != null) {
            designPlan.setOrgBuId(orgBu.getOrganizationId());
            designPlan.setOrgBuCode(orgBu.getOrganizationCode());
            designPlan.setOrgBuName(orgBu.getOrganizationName());
        }
        Organization orgOu = hrUser.getOuOrganization();
        if (orgOu != null) {
            designPlan.setOrgId(orgOu.getOrganizationId());
            designPlan.setOrgCode(orgOu.getOrganizationCode());
            designPlan.setOrgName(orgOu.getOrganizationName());
        }
        designPlanService.saveOrUpdate(designPlan);
        return designPlan;
    }

    public static String getNum(String str) {
        int in = Integer.parseInt(str) + 1;
        String a = Integer.toString(in);
        StringBuilder builder = new StringBuilder();
        for (int i = a.length(); i < NumConstant.FIVE; i++) {
            builder.append("0");
        }
        return builder + a;
    }

    @ApiOperation(value = "获取基础信息", notes = "获取基础信息")
    @GetMapping("/getDesignPlanInfo")
    public SccSouChDesignPlan getDesignPlanInfo(@RequestParam Long designId) {
        checkThrow(designId == null, "提报策划方案id不能为空");
        return designPlanService.getById(designId);
    }

    @ApiOperation(value = "拉取订单数据", notes = "拉取订单数据,type:1、上年订单数据。2、上上年订单数据")
    @PostMapping("/pullOrder")
    public List<SccSouChDemandYearData> pullOrder(@RequestBody PullQueryDto pullQuery) {
        checkThrow(pullQuery == null, "查询参数为空");
        Integer type = pullQuery.getType();
        Long designId = pullQuery.getDesignId();
        if (type != NUM1 && type != NUM2) {
            throw new BaseException("拉取类型错误");
        }
        if (designId == null) {
            throw new BaseException("提报策划方案id不能为空");
        }
        if (type == NUM1) {
            if(pullQuery.getLastYearOrderDateStart() == null || pullQuery.getLastYearOrderDateEnd() == null){
                throw new BaseException("上年订单日期不能为空");
            }
        }
        if (type == NUM2) {
            if(pullQuery.getLastLastYearOrderDateStart() == null || pullQuery.getLastLastYearOrderDateEnd() == null){
                throw new BaseException("上上年订单日期不能为空");
            }
        }
        if (type == NUM1) {
            pullQuery.setLastLastYearOrderDateStart(null);
            pullQuery.setLastLastYearOrderDateEnd(null);
        } else {
            pullQuery.setLastYearOrderDateStart(null);
            pullQuery.setLastYearOrderDateEnd(null);
        }
        return demandYearDataService.pullOrder(pullQuery, type, designId);
    }

    @ApiOperation(value = "拉取订单数据", notes = "拉取订单数据,type:1、上年订单数据。2、上上年订单数据。3、合并的数据")
    @PostMapping("/getPullOrder")
    public PageInfo<SccSouChDemandYearData> getPullOrder(@RequestBody SccSouChDemandYearData yearData) {
        checkThrow(yearData == null, "参数为空");
        Long designId = yearData.getDesignId();
        Integer type = yearData.getType();
        if (designId == null) {
            throw new BaseException("提报策划方案id不能为空");
        }
        if (type != NUM1 && type != NUM2 && type != NUM3) {
            throw new BaseException("类型错误");
        }
        PageUtil.startPage(yearData.getPageNum(), yearData.getPageSize());
        List<SccSouChDemandYearData> allList = demandYearDataService.lambdaQuery().
                eq(SccSouChDemandYearData::getDesignId, designId).
                eq(SccSouChDemandYearData::getType, type).list();
        return new PageInfo<>(CollectionUtils.isNotEmpty(allList) ? allList : new ArrayList<>());
    }

    @ApiOperation(value = "删除需求数据", notes = "拉取订单数据,type:1、上年订单数据。2、上上年订单数据。3、合并的数据")
    @PostMapping("/deleteSomePullOrderData")
    public boolean deleteSomePullOrderData(@RequestBody SccSouChDemandYearData yearData) {
        checkThrow(yearData == null || yearData.getYearId() == null, "参数为空");
        assert yearData != null;
        return demandYearDataService.removeById(yearData.getYearId());
    }

    @ApiOperation(value = "合并订单数据", notes = "合并订单数据")
    @GetMapping("/mergeOrderData")
    public List<SccSouChDemandYearData> mergeOrderData(@RequestParam("designId") Long designId) {
        if (designId == null) {
            throw new BaseException("提报策划方案id不能为空");
        }
        SccSouChDesignPlan dp = designPlanService.getById(designId);
        if (DesignPlanEnums.APPROVING.getCode().equals(dp.getStatus()) || DesignPlanEnums.APPROVED.getCode().equals(dp.getStatus())) {
            throw new BaseException("当前状态不能合并数据");
        }
        List<SccSouChDemandYearData> allList = demandYearDataService.lambdaQuery().
                eq(SccSouChDemandYearData::getDesignId, designId).
                in(SccSouChDemandYearData::getType, 1, 2).list();
        List<SccSouChDemandYearData> lastList = allList.stream().filter(e -> e.getType() == 1).collect(Collectors.toList());
        List<SccSouChDemandYearData> lastLastList = allList.stream().filter(e -> e.getType() == 2).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(lastList)) {
            throw new BaseException("上年不存在数据");
        }
        if (CollectionUtils.isEmpty(lastLastList)) {
            throw new BaseException("上上年不存在数据");
        }
        //合并的集合以上一年为准
        List<SccSouChDemandYearData> mergeList = new ArrayList<>(lastList);
        mergeList.forEach(item -> item.setDataSource("上一年"));
        // 根据物资编码合并数据上一年和上上一年的数据，同时后台标记如果数据上一年数据存在，不取值上上一年数据，并且标记来源
        for (SccSouChDemandYearData lastLast : lastLastList) {
            boolean exists = false;
            for (SccSouChDemandYearData mergeData : mergeList) {
                if (mergeData.getMaterialCode().equals(lastLast.getMaterialCode())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                lastLast.setDataSource("上上一年");
                mergeList.add(lastLast);
            }
        }
        //根据供货区域+物料编码+品牌进行合并
        mergeList = this.merge(mergeList);
        mergeList.forEach(e -> {
            e.setYearId(IdGenrator.generate());
            e.setDesignId(designId);
            e.setType(3);
            //清除创建和更新相关字段
            SouCommonUtil.cleanupStandardFields(e);
        });
        //删除已有数据
        demandYearDataService.remove(new LambdaQueryWrapper<SccSouChDemandYearData>()
                .eq(SccSouChDemandYearData::getDesignId, designId)
                .eq(SccSouChDemandYearData::getType, 3));
        //新增
        demandYearDataService.saveBatch(mergeList);
        //保存供应商信息
        this.demandSup(lastList, dp, designId);
        return mergeList;
    }

    /**
     * 合并数据后，需要拉取供应商信息，并且保存到表里
     *
     * @param lastList
     * @param dp
     * @param designId
     */
    private void demandSup(List<SccSouChDemandYearData> lastList, SccSouChDesignPlan dp, Long designId) {
        List<SccSouChDemandSup> chDemandSupList = new ArrayList<>();
        //根据供应商为维度汇总金额
        Set<String> sup = lastList.stream().map(SccSouChDemandYearData::getSupCode).collect(Collectors.toSet());
        List<SccSouChDemandAnalysis> supplyList = new ArrayList<>();
        for (String e : sup) {
            SccSouChDemandAnalysis an = new SccSouChDemandAnalysis();
            an.setType(3);
            an.setDesignId(designId);
            BigDecimal mo = BigDecimal.ZERO;
            for (SccSouChDemandYearData a : lastList) {
                if (e.equals(a.getSupCode())) {
                    an.setVendorId(a.getSupId());
                    an.setVendorCode(a.getSupCode());
                    an.setVendorName(a.getSupName());
                    mo = mo.add(a.getPriceTotal());
                }
            }
            an.setMatMoney(mo);
            supplyList.add(an);
        }
        //首轮按照近一年的供应商供货金额前5名
        if (ObjectUtil.isEmpty(dp.getNum()) || dp.getNum() == 1) {
            this.getTopSup(supplyList, 5, designId, chDemandSupList);
        } else {
            Date lastYear = DateUtil.offset(new Date(), DateField.YEAR, -1);
            //非首轮则是按照上一年shang上一轮的定价协议的供应商，同时新增近一年的金额前三的家
            SccSouChDesignPlan lastChDesignPlan = designPlanService.getOne(new LambdaQueryWrapper<SccSouChDesignPlan>()
                    .eq(SccSouChDesignPlan::getNum, dp.getNum() - 1)
                    .between(SccSouChDesignPlan::getCreationDate, DateUtil.beginOfYear(lastYear), DateUtil.endOfYear(lastYear))
                    .eq(SccSouChDesignPlan::getProjectId, dp.getProjectId()), false);
            if (ObjectUtil.isNotEmpty(lastChDesignPlan)) {
                List<SccSouChDemandSup> lastChDemandSup = demandSupService.list(new LambdaQueryWrapper<SccSouChDemandSup>().eq(SccSouChDemandSup::getDesignId, lastChDesignPlan.getDesignId()));
                lastChDemandSup.forEach(st -> {
                    SccSouChDemandSup chDemandSup = new SccSouChDemandSup();
                    chDemandSup.setDesignId(designId);
                    chDemandSup.setSupId(Convert.toStr(st.getSupId()));
                    chDemandSup.setSupCode(st.getSupCode());
                    chDemandSup.setSupName(st.getSupName());
                    chDemandSupList.add(chDemandSup);
                });
            }
            //近一年的金额前三的家
            this.getTopSup(supplyList, 3, designId, chDemandSupList);
        }
        List<SccSouChDemandSup> existDemandSup = demandSupService.list(new LambdaQueryWrapper<SccSouChDemandSup>().eq(SccSouChDemandSup::getDesignId, designId));
        //为了防止覆盖，所以需要剔除已有的数据，不进行插入
        chDemandSupList.removeIf(a -> existDemandSup.stream().anyMatch(b -> b.getSupId().equals(a.getSupId())));
        //插入
        demandSupService.saveOrUpdateBatch(chDemandSupList);
    }

    private List<SccSouChDemandSup> getTopSup(List<SccSouChDemandAnalysis> supplyList, int i, Long designId, List<SccSouChDemandSup> chDemandSupList) {
        //取前5个
        List<SccSouChDemandAnalysis> topNum = supplyList.stream()
                .sorted((o1, o2) -> o2.getMatMoney().compareTo(o1.getMatMoney()))
                .limit(i)
                .collect(Collectors.toList());
        for (SccSouChDemandAnalysis demandAnalysis : topNum) {
            SccSouChDemandSup chDemandSup = new SccSouChDemandSup();
            chDemandSup.setDesignId(designId);
            chDemandSup.setSupId(Convert.toStr(demandAnalysis.getVendorId()));
            chDemandSup.setSupCode(demandAnalysis.getVendorCode());
            chDemandSup.setSupName(demandAnalysis.getVendorName());
            chDemandSupList.add(chDemandSup);
        }
        return chDemandSupList;
    }

    /**
     * 据供货区域+物料编码+品牌进行合并
     *
     * @param mergeList
     * @return
     */
    private List<SccSouChDemandYearData> merge(List<SccSouChDemandYearData> mergeList) {
        return mergeList.stream()
                .collect(Collectors.groupingBy(data -> ("0".equals(data.getMaterialCode()) ? "" : data.getMaterialCode()) + data.getBrand() + data.getAreaCode()))
                .values().stream()
                .map(dataList -> {
                    SccSouChDemandYearData yd = dataList.get(0);
                    //未税单价
                    BigDecimal wsdj = yd.getPriceTax();
                    //含税单价
                    BigDecimal hsdj = yd.getRatePrice();
                    for (SccSouChDemandYearData e : dataList) {
                        if (wsdj.compareTo(e.getPriceTax()) > 0) {
                            wsdj = e.getPriceTax();
                        }
                        if (hsdj.compareTo(e.getRatePrice()) > 0) {
                            hsdj = e.getRatePrice();
                        }
                    }
                    yd.setHistoryVendorCode(yd.getSupCode());
                    yd.setHistoryPriceTax(wsdj);
                    yd.setHistoryRatePrice(hsdj);
                    return yd;
                })
                .collect(Collectors.toList());
    }

    public static void mergeInfo(List<SccSouChDemandYearData> list, List<SccSouChDemandYearData> mergeList) {
        for (SccSouChDemandYearData a : list) {
            boolean bo = true;
            for (SccSouChDemandYearData b : mergeList) {
                if (boo(a.getAreaCode(), b.getAreaCode()) && boo(a.getMaterialCode(), b.getMaterialCode()) && boo(a.getBrand(), b.getBrand())) {
                    bo = false;
                }
            }
            if (bo) {
                mergeList.add(a);
            }
        }
    }

    public static Boolean boo(String str1, String str2) {
        if (StringUtils.isBlank(str1) || StringUtils.isBlank(str2)) {
            return false;
        }
        return str1.equals(str2);
    }

    @ApiOperation(value = "供应商信息列表", notes = "供应商信息列表")
    @GetMapping("/getReqSupInfoList")
    public List<SccSouChDemandSup> getReqSupInfoList(@RequestParam("designId") Long designId) {
        checkThrow(designId == null, "策划方案id为空");
        return demandSupService.lambdaQuery().eq(SccSouChDemandSup::getDesignId, designId).list();
    }

    @ApiOperation(value = "查询供应商信息列表", notes = "查询供应商信息列表")
    @GetMapping("/getReqSupList")
    public List<SccSouChDemandSup> getReqSupInfoList(@RequestBody SccSouChDemandSup sup) {
        if (ObjectUtil.isEmpty(sup.getSupId())) {
            return demandSupService.lambdaQuery().eq(SccSouChDemandSup::getSupId, sup.getSupId()).list();
        }
        if (StringUtils.isEmpty(sup.getSupCode())) {
            return demandSupService.lambdaQuery().eq(SccSouChDemandSup::getSupCode, sup.getSupCode()).list();
        }
        return demandSupService.lambdaQuery().eq(SccSouChDemandSup::getDemandSupId, sup.getDemandSupId()).list();
    }

    /**
     * 校验供应商信息不能为空
     * @param list
     */
    private void checkVendorInfo(List<SccSouChDemandSup> list) {
        if(CollectionUtils.isNotEmpty(list)) {
            Map<String, List<SccSouChDemandSup>> groupMap = list.stream().collect(Collectors.groupingBy(SccSouChDemandSup::getSupCode));

            List<String> errorVendorNameList = new ArrayList<>();
            for(List<SccSouChDemandSup> subList : groupMap.values()) {
                if(Integer.compare(subList.size(), 1) == 1) {
                    errorVendorNameList.add(subList.get(0).getSupName());
                }
            }

            if(CollectionUtils.isNotEmpty(errorVendorNameList)) {
                throw new BaseException("存在以下重复供应商，不允许重复添加：" + errorVendorNameList.stream().collect(Collectors.joining(SrmConstant.SIG_3)));
            }

        }
    }

    @ApiOperation(value = "添加或更新供应商信息", notes = "添加或更新供应商信息")
    @PostMapping("/saveOrUpdateDemandSup")
    public List<SccSouChDemandSup> saveOrUpdateDemandSup(@RequestBody SupDto sup) {
        checkThrow(sup == null, "参数不能为空");
        List<SccSouChDemandSup> list = sup.getList();
        //校验供应商不允许重复
        checkVendorInfo(list);

        Long designId = sup.getDesignId();
        list.forEach(l -> l.setDesignId(designId));
        checkThrow(designId == null, "策划方案id为空");
        if (CollectionUtils.isEmpty(list)) {
            demandSupService.lambdaQuery().eq(SccSouChDemandSup::getDesignId, designId).clear();
            return new ArrayList<>();
        }
        List<Long> demandSupId = list.stream().filter(s -> !Objects.isNull(s.getDemandSupId())).map(SccSouChDemandSup::getDemandSupId).collect(Collectors.toList());
        demandSupService.remove(new LambdaQueryWrapper<SccSouChDemandSup>()
                .notIn(CollectionUtils.isNotEmpty(demandSupId), SccSouChDemandSup::getDemandSupId, demandSupId)
                .eq(SccSouChDemandSup::getDesignId, designId)
        );
        demandSupService.saveOrUpdateBatch(list);
        return list;
    }


    @ApiOperation(value = "列表-需求信息-项目策略方案", notes = "列表-需求信息-项目策略方案")
    @PostMapping("/getDemandProjPlanList")
    public ProjPlanDto getDemandProjPlanList(@RequestParam("designId") Long designId) {
        checkThrow(designId == null, "策划方案id不能为空");
        ProjPlanDto projPlan = new ProjPlanDto();
        LambdaQueryWrapper<SccSouChDemandProgramme> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SccSouChDemandProgramme::getDesignId, designId);
        SccSouChDemandProgramme demandProgramme = demandProgrammeMapper.selectOne(queryWrapper);
        projPlan.setDemandProgramme(demandProgramme);
        List<SccSouChDemandWork> workList = demandWorkMapper.selectList(new LambdaQueryWrapper<SccSouChDemandWork>().eq(SccSouChDemandWork::getDesignId, designId));
        projPlan.setWorkList(workList);
        //招标策略及目标设定-汽柴油
        List<SccSouChDemandStrategy> strategyList = demandStrategyMapper.selectList(new LambdaQueryWrapper<SccSouChDemandStrategy>().eq(SccSouChDemandStrategy::getDesignId, designId));
        projPlan.setStrategyList(strategyList);
        //招标策略及目标设定-其他
        List<SccSouChDemandOther> otherList = demandOtherMapper.selectList(new LambdaQueryWrapper<SccSouChDemandOther>().eq(SccSouChDemandOther::getDesignId, designId));
        projPlan.setOtherList(otherList);
        //招标策略及目标设定-招标策略及目标设定
        List<SccSouChDemandSetting> settingList = demandSettingMapper.selectList(new LambdaQueryWrapper<SccSouChDemandSetting>().eq(SccSouChDemandSetting::getDesignId, designId));
        projPlan.setSettingList(settingList);
        //获取初始化数据
        List<SccSouChDemandYearData> allList = demandYearDataService.lambdaQuery().
                eq(SccSouChDemandYearData::getDesignId, designId).
                eq(SccSouChDemandYearData::getType, 1).list();
        if (ObjectUtil.isEmpty(allList)) {
            return projPlan;
        }
        //总金额
        BigDecimal allMo = allList.stream().map(SccSouChDemandYearData::getPriceTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (allMo.compareTo(BigDecimal.ZERO) == 0) {
            return projPlan;
        }
        //使用单位金额分析：取值数据2（近一年）拉取的数据，按照金额排序，取值前九家单位名称、及金额，同时剩余归集到其他
        List<SccSouChDemandAnalysis> top9List = this.getByType1(allList, allMo, designId);
        projPlan.setUnitList(top9List);
        //按物流品类分析
        List<SccSouChDemandAnalysis> top20List = this.getByType2(allList, allMo, designId);
        projPlan.setCategoryList(top20List);
        //按供应商分析
        List<SccSouChDemandAnalysis> top4List = this.getByType3(allList, allMo, designId);
        projPlan.setSupplyList(top4List);
        return projPlan;
    }

    private List<SccSouChDemandAnalysis> getByType3(List<SccSouChDemandYearData> allList, BigDecimal allMo, Long designId) {
        Set<String> sup = allList.stream().map(SccSouChDemandYearData::getSupCode).collect(Collectors.toSet());
        List<SccSouChDemandAnalysis> supplyList = new ArrayList<>();
        for (String e : sup) {
            SccSouChDemandAnalysis an = new SccSouChDemandAnalysis();
            an.setType(3);
            an.setDesignId(designId);
            BigDecimal mo = BigDecimal.ZERO;
            for (SccSouChDemandYearData a : allList) {
                if (e.equals(a.getSupCode())) {
                    an.setVendorId(a.getSupId());
                    an.setVendorCode(a.getSupCode());
                    an.setVendorName(a.getSupName());
                    mo = mo.add(a.getPriceTotal());
                }
            }
            an.setMatMoney(mo);
            an.setMatRate(mo.divide(allMo, 2, RoundingMode.HALF_UP));
            supplyList.add(an);
        }
        //取前4个
        return supplyList.stream()
                .sorted((o1, o2) -> o2.getMatMoney().compareTo(o1.getMatMoney()))
                .limit(4)
                .collect(Collectors.toList());
    }

    private List<SccSouChDemandAnalysis> getByType2(List<SccSouChDemandYearData> allList, BigDecimal allMo, Long designId) {
        Set<String> materialCode = allList.stream().map(SccSouChDemandYearData::getMaterialCode).collect(Collectors.toSet());
        List<SccSouChDemandAnalysis> categoryList = new ArrayList<>();
        for (String e : materialCode) {
            SccSouChDemandAnalysis an = new SccSouChDemandAnalysis();
            an.setType(2);
            an.setDesignId(designId);
            BigDecimal mo = BigDecimal.ZERO;
            for (SccSouChDemandYearData a : allList) {
                if (e.equals(a.getMaterialCode())) {
                    an.setMaterialId(a.getMaterialId());
                    an.setMaterialCode(a.getMaterialCode());
                    an.setMaterialName(a.getMaterialName());
                    mo = mo.add(a.getPriceTotal());
                }
            }
            an.setMatMoney(mo);
            an.setMatRate(mo.divide(allMo, 2, RoundingMode.HALF_UP));
            categoryList.add(an);
        }
        //取前20个
        return categoryList.stream()
                .sorted((o1, o2) -> o2.getMatMoney().compareTo(o1.getMatMoney()))
                .limit(20)
                .collect(Collectors.toList());
    }

    private List<SccSouChDemandAnalysis> getByType1(List<SccSouChDemandYearData> allList, BigDecimal allMo, Long designId) {
        Set<String> unitCode = allList.stream().map(SccSouChDemandYearData::getOrganizationCode).collect(Collectors.toSet());
        List<SccSouChDemandAnalysis> unitList = new ArrayList<>();
        for (String e : unitCode) {
            SccSouChDemandAnalysis an = new SccSouChDemandAnalysis();
            an.setType(1);
            an.setDesignId(designId);
            //单位合并金额
            BigDecimal mo = BigDecimal.ZERO;
            for (SccSouChDemandYearData a : allList) {
                if (e.equals(a.getOrganizationCode())) {
                    an.setOrganizationId(a.getOrganizationId());
                    an.setOrganizationCode(a.getOrganizationCode());
                    an.setOrganizationName(a.getOrganizationName());
                    mo = mo.add(a.getPriceTotal());
                }
            }
            an.setMatMoney(mo);
            unitList.add(an);
        }
        //按照金额倒序
        unitList.sort((o1, o2) -> o2.getMatMoney().compareTo(o1.getMatMoney()));
        //取前9个
        List<SccSouChDemandAnalysis> top9List = unitList.stream()
                .limit(9)
                .collect(Collectors.toList());
        // 如果元素个数大于9，则将第10个元素到最后一个元素合并为一条数据
        if (unitList.size() > NUM9) {
            BigDecimal matMoneySum = BigDecimal.ZERO;
            for (int i = NUM9; i < unitList.size(); i++) {
                matMoneySum = matMoneySum.add(unitList.get(i).getMatMoney());
            }
            SccSouChDemandAnalysis lastUnit = new SccSouChDemandAnalysis();
            lastUnit.setOrganizationName("其它");
            lastUnit.setMatMoney(matMoneySum);
            top9List.add(lastUnit);
        }
        ////赋值占比
        top9List.forEach(l -> l.setMatRate(l.getMatMoney().divide(allMo, 2, RoundingMode.HALF_UP)));
        return top9List;
    }

    @ApiOperation(value = "添加或更新-需求信息-项目策略方案", notes = "添加或更新-需求信息-项目策略方案")
    @PostMapping("/saveOrUpdateDemandProjPlan")
    public ProjPlanDto saveOrUpdateDemandProjPlan(@RequestBody ProjPlanDto projPlan) {
        if (projPlan == null) {
            return new ProjPlanDto();
        }
        Long designId = projPlan.getDesignId();
        checkThrow(designId == null, "策划方案id不能为空");
        SccSouChDemandProgramme demandProgramme = projPlan.getDemandProgramme();
        demandProgramme.setDesignId(designId);
        //工作日程
        List<SccSouChDemandWork> workList = projPlan.getWorkList();
        //招标策略及目标设定-汽柴油
        List<SccSouChDemandStrategy> strategyList = projPlan.getStrategyList();
        //招标策略及目标设定-其他
        List<SccSouChDemandOther> otherList = projPlan.getOtherList();
        //招标策略及目标设定-招标策略及目标设定
        List<SccSouChDemandSetting> settingList = projPlan.getSettingList();
        //招标策略及目标设定-使用单位金额分析
        List<SccSouChDemandAnalysis> unitList = projPlan.getUnitList();
        //招标策略及目标设定-按物流品类分析
        List<SccSouChDemandAnalysis> categoryList = projPlan.getCategoryList();
        //供方采购金额分析
        List<SccSouChDemandAnalysis> supplyList = projPlan.getSupplyList();
        if (demandProgramme.getProgrammeId() != null) {
            demandProgrammeMapper.updateById(demandProgramme);
        } else {
            demandProgrammeMapper.insert(demandProgramme);
        }
        if (CollectionUtils.isEmpty(workList)) {
            LambdaQueryWrapper<SccSouChDemandWork> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SccSouChDemandWork::getDesignId, designId);
            demandWorkMapper.delete(queryWrapper);
        } else {
            List<Long> workIds = workList.stream().map(SccSouChDemandWork::getWorkId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(workIds)) {
                LambdaQueryWrapper<SccSouChDemandWork> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(SccSouChDemandWork::getDesignId, designId);
                queryWrapper.notIn(SccSouChDemandWork::getWorkId, workIds);
                demandWorkMapper.delete(queryWrapper);
            }
            for (SccSouChDemandWork e : workList) {
                e.setDesignId(designId);
                if (e.getWorkId() == null) {
                    demandWorkMapper.insert(e);
                } else {
                    demandWorkMapper.updateById(e);
                }
            }
        }

        if (CollectionUtils.isEmpty(strategyList)) {
            LambdaQueryWrapper<SccSouChDemandStrategy> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SccSouChDemandStrategy::getDesignId, designId);
            demandStrategyMapper.delete(queryWrapper);
        } else {
            List<Long> strategyIds = strategyList.stream().map(SccSouChDemandStrategy::getStrategyId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(strategyIds)) {
                LambdaQueryWrapper<SccSouChDemandStrategy> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(SccSouChDemandStrategy::getDesignId, designId);
                queryWrapper.notIn(SccSouChDemandStrategy::getStrategyId, strategyIds);
                demandStrategyMapper.delete(queryWrapper);
            }
            for (SccSouChDemandStrategy e : strategyList) {
                e.setDesignId(designId);
                if (e.getStrategyId() == null) {
                    demandStrategyMapper.insert(e);
                } else {
                    demandStrategyMapper.updateById(e);
                }
            }
        }

        extracted(designId, otherList, settingList, unitList, categoryList);

        LambdaQueryWrapper<SccSouChDemandAnalysis> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SccSouChDemandAnalysis::getDesignId, designId);
        demandAnalysisMapper.delete(queryWrapper);
        if (CollectionUtils.isNotEmpty(unitList)) {
            for (SccSouChDemandAnalysis e : unitList) {
                e.setDesignId(designId);
                e.setType(1);
                demandAnalysisMapper.insert(e);
            }
        }
        if (CollectionUtils.isNotEmpty(categoryList)) {
            for (SccSouChDemandAnalysis e : categoryList) {
                e.setDesignId(designId);
                e.setType(2);
                demandAnalysisMapper.insert(e);
            }
        }
        if (CollectionUtils.isNotEmpty(supplyList)) {
            for (SccSouChDemandAnalysis e : supplyList) {
                e.setDesignId(designId);
                e.setType(3);
                demandAnalysisMapper.insert(e);
            }
        }
        return projPlan;
    }

    /**
     * 添加或更新-需求信息-项目策略方案 拆分
     * @param designId 参数
     * @param otherList 参数
     * @param settingList 参数
     * @param unitList 参数
     * @param categoryList 参数
     */
    private void extracted(Long designId, List<SccSouChDemandOther> otherList, List<SccSouChDemandSetting> settingList, List<SccSouChDemandAnalysis> unitList, List<SccSouChDemandAnalysis> categoryList) {
        if (CollectionUtils.isEmpty(otherList)) {
            LambdaQueryWrapper<SccSouChDemandOther> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SccSouChDemandOther::getDesignId, designId);
            demandOtherMapper.delete(queryWrapper);
        } else {
            List<Long> otherIds = otherList.stream().map(SccSouChDemandOther::getOtherId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(otherIds)) {
                LambdaQueryWrapper<SccSouChDemandOther> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(SccSouChDemandOther::getDesignId, designId);
                queryWrapper.notIn(SccSouChDemandOther::getOtherId, otherIds);
                demandOtherMapper.delete(queryWrapper);
            }
            for (SccSouChDemandOther e : otherList) {
                e.setDesignId(designId);
                if (e.getOtherId() == null) {
                    demandOtherMapper.insert(e);
                } else {
                    demandOtherMapper.updateById(e);
                }
            }
        }
        //招标策略及目标设定
        List<SccSouChDemandSup> existDemandSup = demandSupService.list(new LambdaQueryWrapper<SccSouChDemandSup>().eq(SccSouChDemandSup::getDesignId, designId));
        List<String> supCodeList = existDemandSup.stream().map(SccSouChDemandSup::getSupCode).collect(Collectors.toList());

        List<SccSouChDemandSetting> existsDemandSettingList = demandSettingMapper.selectList(Wrappers.lambdaQuery(SccSouChDemandSetting.class).eq(SccSouChDemandSetting::getDesignId, designId));
        Map<String, SccSouChDemandSetting> existDemandSettingMap = new HashMap<>(15);
        if(CollectionUtils.isNotEmpty(existsDemandSettingList)) {
            List<Long> settingIdList = existsDemandSettingList.stream().map(s -> s.getSettingId()).collect(Collectors.toList());
            existDemandSettingMap = existsDemandSettingList.stream().filter(s -> supCodeList.contains(s.getSupCode())).collect(Collectors.toMap(k -> k.getSupCode(), Function.identity(), (k1, k2) -> k2));
            List<Long> existsSettingIdList = existDemandSettingMap.values().stream().map(k->k.getSettingId()).collect(Collectors.toList());
            settingIdList.removeAll(existsSettingIdList);
            if(CollectionUtils.isNotEmpty(settingIdList)) {
                demandSettingMapper.deleteBatchIds(settingIdList);
            }
        }

        settingList = settingList.stream().filter(a -> supCodeList.contains(a.getSupCode())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(settingList)) {

            for (SccSouChDemandSetting e : settingList) {
                SccSouChDemandSetting existsSccSouChDemandSetting = existDemandSettingMap.get(e.getSupCode());
                e.setDesignId(designId);
                if (existsSccSouChDemandSetting == null) {
                    demandSettingMapper.insert(e);
                } else {
                    e.setSettingId(existsSccSouChDemandSetting.getSettingId());
                    demandSettingMapper.updateById(e);
                }
            }
            List<String> filterSupCodeList = settingList.stream().map(s -> s.getSupCode()).collect(Collectors.toList());
            existDemandSup = existDemandSup.stream().filter(s -> !filterSupCodeList.contains(s.getSupCode())).collect(Collectors.toList());
        }
        //从供应商信息获取数据过去
        if (ObjectUtil.isNotEmpty(existDemandSup)) {
            for (SccSouChDemandSup et : existDemandSup) {
                SccSouChDemandSetting chDemandSetting = new SccSouChDemandSetting();
                chDemandSetting.setDesignId(designId);
                chDemandSetting.setSupId(Convert.toLong(et.getSupId()));
                chDemandSetting.setSupCode(et.getSupCode());
                chDemandSetting.setSupName(et.getSupName());
                //其它待续
                demandSettingMapper.insert(chDemandSetting);
            }
        }

    }

    @ApiOperation(value = "下载需求模板", notes = "下载需求模板")
    @RequestMapping("/downloadReqTemplate")
    public void downloadReqTemplate(HttpServletResponse response) {
        try {
            InputStream inputStream = this.getClass().getResourceAsStream("/template/集采需求导入.xlsx");
            assert inputStream != null;
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            OutputStream outputStream = EasyExcelUtil.getServletOutputStream(response, "需求信息模板");
            workbook.write(outputStream);
        } catch (Exception e) {
            throw new BaseException("下载失败");
        }
    }

    @ApiOperation(value = "需求导入", notes = "需求导入")
    @PostMapping("/importReqInfo")
    public Map<String, Object> importReqInfo(@RequestBody MultipartFile file, @RequestParam("designId") Long designId, Fileupload fileupload) {
        checkThrow(designId == null, "协议id不能为空");
        AnalysisEventListenerImpl<ImportExcelReqInfoDto> listenerLine = new AnalysisEventListenerImpl<>();
        try {
            ExcelReader excelReaderLine = EasyExcel.read(file.getInputStream(), listenerLine).build();
            ReadSheet readSheetLine = EasyExcel.readSheet(0).headRowNumber(4).head(ImportExcelReqInfoDto.class).build();
            excelReaderLine.read(readSheetLine);
            List<ImportExcelReqInfoDto> objList = designPlanService.importReqInfo(listenerLine.getDatas(), designId);
            if (CollectionUtils.isNotEmpty(objList)) {
                log.info("导入失败");
                Fileupload errorFileupload = uploadFile(fileupload, file, ImportExcelReqInfoDto.class, objList);
                return ImportStatus.importError(errorFileupload.getFileuploadId(),errorFileupload.getFileSourceName());
            }
            log.info("导入成功");
            return ImportStatus.importSuccess();
        } catch (Exception e) {
            throw new BaseException(e.getMessage());
        }
    }

    public Fileupload uploadFile(Fileupload fileupload, MultipartFile file, Class<?> clazz, List<ImportExcelReqInfoDto> errorList) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream).head(clazz).sheet(0).sheetName("sheetName").doWrite(errorList);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        file = new MockMultipartFile(file.getName(), file.getOriginalFilename(), file.getContentType(), inputStream);
        fileupload.setUploadType("DEF");
        return fileCenterClient.feignClientUpload(file, fileupload.getSourceType(), fileupload.getUploadType()
                , fileupload.getFileModular(), fileupload.getFileFunction()
                , fileupload.getFileType());

    }


    @ApiOperation("创建集采询比价")
    @PostMapping("/createPurInq/{designId}")
    public ApiSouInitDTO createPurInq(@PathVariable("designId") Long designId) {
        return designPlanService.createPurInq(designId);
    }

    @ApiOperation("创建集采询比价")
    @GetMapping("/delete")
    public void delete(@RequestParam("designId") Long designId) {
        designPlanService.removeById(designId);
    }

}
