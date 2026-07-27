package com.midea.cloud.srm.supcooperate.ext.requirement.pr.controller;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.constants.SequenceCodeConstant;
import com.midea.cloud.common.enums.FileUploadType;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.BigDecimalUtil;
import com.midea.cloud.component.handler.AutoMetaObjContext;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.srm.feign.BaseExtClient;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.base.material.MaterialItem;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.organization.entity.OrganizationUser;
import com.midea.cloud.srm.model.base.organization.entity.Site;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementLine;
import com.midea.cloud.srm.model.pm.pr.requirement.enums.RequirementApproveStatus;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.sou.fixprice.dto.ExtFixPriceHeadDTO;
import com.midea.cloud.srm.model.sou.fixprice.dto.ExtFixPriceQueryDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.dto.ExtPrSouRequirementLineExportRequestDto;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sies.client.SiesClient;
import com.midea.cloud.srm.sies.pojo.SiesImportParam;
import com.midea.cloud.srm.sies.pojo.SiesResponse;
import com.midea.cloud.srm.sies.pojo.SiesResult;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.*;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.mapper.PurchaseRequirementLineMapper;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.mapper.PurchaseRequirementMapper;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.service.EdmattachSyncService;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.service.PurchaseRequirementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zenghx2
 */
@Api(value = "PurchaseRequirementController", tags = {"非招标需求"})
@RestController
@Slf4j
@RequestMapping("/purchaseRequirement")
public class PurchaseRequirementController {

    @Autowired
    private SiesClient siesClient;
    @Autowired
    private PurchaseRequirementMapper purchaseRequirementMapper;
    @Autowired
    private PurchaseRequirementService purchaseRequirementService;
    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    @Autowired
    private PurchaseRequirementLineMapper requirementLineMapper;

    @Autowired
    private EdmattachSyncService edmattachSyncService;

    @Autowired
    private RbacClient rbacClient;
    @Autowired
    public BaseClient baseClient;
    @Autowired
    private QlService qlService;
    @Autowired
    private QlOpenClient qlOpenClient;
    @Autowired
    private BaseExtClient baseExtClient;

    @Autowired
    private FileCenterClient fileCenterClient;

    @ApiOperation(value = "采购需求-物资明细-导入文件模板下载", notes = "采购需求-物资明细-导入文件模板下载")
    @RequestMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response) throws Exception {
        siesClient.downloadTemplate(this.getClass(), response);
    }

    @ApiOperation(value = "excel导入采购需求行", notes = "excel导入采购需求行", httpMethod = "POST")
    @PostMapping("/import")
    public Map<String, Object> importV2(@RequestParam("file") MultipartFile file, Fileupload fileupload, PurchaseRequirementHeadDTO head,
                                        HttpServletRequest request) throws Exception {
        Assert.notNull(file, "文件不能为空");
        Assert.notNull(head.getOrgId(), "请选择业务实体");
        fileupload.putX("head", head);
        SiesResponse importResponse = siesClient.importExcel(this.getClass(), file, fileupload, request);
        SiesResult result = importResponse.getResult();
        if (importResponse.isSuccess()) {
            //返回数据给前端
            SiesImportParam importParam = (SiesImportParam) importResponse.getDataParam();
            Collection<List<?>> data = importParam.getExtData().getAllManualData().values();
            if (CollectionUtils.isNotEmpty(data)) {
                result.put("data", data.iterator().next());
            }
        }
        return result;
    }

    @ApiOperation("选择协议单")
    @PostMapping("/selectWithPriceAgree")
    public PageInfo<PurchaseRequirementLineDTO> selectWithPriceAgree(@RequestBody RequirementSelectionQueryDTO request) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
//        request.setOrgId(getOrgId());
        request.setOrgIds(getOrgIdByBase());
        List<PurchaseRequirementLineDTO> list = purchaseRequirementMapper.selectWithPriceAgree(request);
        return new PageInfo<>(list);
    }

    @ApiOperation("选择定价单")
    @PostMapping("/selectWithFixPrice")
    public PageInfo<PurchaseRequirementLineDTO> selectWithFixPrice(@RequestBody RequirementSelectionQueryDTO request) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
//        request.setOrgId(getOrgId());
        request.setOrgIds(getOrgIdByBase());
        List<PurchaseRequirementLineDTO> list = purchaseRequirementMapper.selectWithFixPrice(request);
        PageInfo<PurchaseRequirementLineDTO> page = new PageInfo<>(list);
        list.stream().forEach(e -> {
            BigDecimal noTaxPrice = e.getUnitPrice();
            BigDecimal taxRate = e.getTaxRate();
            e.setTaxPrice(BigDecimalUtil.mul(noTaxPrice, BigDecimalUtil.div(taxRate, new BigDecimal(100)).add(BigDecimal.ONE)));
        });
        return page;
    }

    @ApiOperation("创建定价订单")
    @PostMapping("/createOrderByFixPrice")
    public Object createOrderByFixPrice(@RequestBody ExtFixPriceHeadDTO extFixPriceHeadDTO) {
        log.info("创建定价订单，param:{}", JSONUtil.toJsonStr(extFixPriceHeadDTO));
        purchaseRequirementService.createOrderByFixPrice(extFixPriceHeadDTO);
        return null;
    }

    private List<Long> getOrgIdByBase(){
        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        List<OrganizationUser> organizationUsers = loginAppUser.getOrganizationUsers();
        List<Long> ids = organizationUsers.stream().map(OrganizationUser::getOrganizationId).collect(Collectors.toList());
        List<Organization> organizationList = baseClient.getOrganizationsByIds(ids);
        Assert.isTrue(!organizationList.isEmpty(), "查询业务实体失败");
        return organizationList.stream().filter(s -> "OU".equals(s.getOrganizationTypeCode()))
                .map(Organization::getOrganizationId).collect(Collectors.toList());
    }

    private Long getOrgId(){
        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        HrUserOrgnizationDto userOrgnizationDto = pjProjectExtClient.getHrUserOrgnizationByUsername(loginAppUser.getUsername());
        Assert.notNull(userOrgnizationDto.getOuOrganization(), "查询业务实体失败");
        return userOrgnizationDto.getOuOrganization().getOrganizationId();
    }

    @ApiOperation(value = "EDM-SRM采购申请调用SRM的接口", notes = "EDM-SRM采购申请调用SRM的接口")
    @PostMapping("/edmGeneratePurchaseRequirement")
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> edmGeneratePurchaseRequirement(@RequestBody Map<String, EdmPrHeadDto> ep){
        EdmPrHeadDto requestInfo = ep.get("data");
        Map<String, Object> reMap = new HashMap<>(4);
        reMap.put("content", requestInfo.getExNo());
        try {
            User user = rbacClient.getUserByUserName(requestInfo.getApplyBy());
            if (user == null) {
                reMap.put("code", "500");
                reMap.put("message", "找不到申请人");
                return reMap;
            }
            AutoMetaObjContext.noOp(AutoMetaObjContext.MODE.FOREVERY);
            //获取用户的组织信息
            HrUserOrgnizationDto organization = pjProjectExtClient.getHrUserOrgnizationByUsername(requestInfo.getApplyBy());
            PurchaseRequirementHeadDTO requirementHeadDTO = new PurchaseRequirementHeadDTO();
            if (organization != null) {
                if (organization.getBuOrganization() != null) {
                    requirementHeadDTO.setExtOrgBuId(organization.getBuOrganization().getOrganizationId().toString());
                    requirementHeadDTO.setExtOrgBuCode(organization.getBuOrganization().getOrganizationCode());
                    requirementHeadDTO.setExtOrgBuName(organization.getBuOrganization().getOrganizationName());
                }
                if (organization.getOuOrganization() != null) {
                    requirementHeadDTO.setOrgId(organization.getOuOrganization().getOrganizationId());
                    requirementHeadDTO.setOrgCode(organization.getOuOrganization().getOrganizationCode());
                    requirementHeadDTO.setOrgName(organization.getOuOrganization().getOrganizationName());
                }
                if (organization.getDepartmentOrganization() != null) {
                    requirementHeadDTO.setCeeaDepartmentId(organization.getDepartmentOrganization().getOrganizationId().toString());
                    requirementHeadDTO.setCeeaDepartmentCode(organization.getDepartmentOrganization().getOrganizationCode());
                    requirementHeadDTO.setCeeaDepartmentName(organization.getDepartmentOrganization().getOrganizationName());

                }
            }
            requirementHeadDTO.setApplyBy(requestInfo.getApplyBy());
            requirementHeadDTO.setApplyById(user.getUserId());
            requirementHeadDTO.setApplyByNickname(user.getNickname());
            requirementHeadDTO.setApplyCode(requestInfo.getApplyBy());
            requirementHeadDTO.setApplyDate(LocalDate.now());
            requirementHeadDTO.setAuditStatus(RequirementApproveStatus.DRAFT);
            requirementHeadDTO.setCeeaAppointReason(requestInfo.getCeeaAppointReason());
            requirementHeadDTO.setCeeaPrType(requestInfo.getApplicationFormType());
            requirementHeadDTO.setCreatedBy(requestInfo.getApplyBy());
            requirementHeadDTO.setCreatedFullName(user.getNickname());
            requirementHeadDTO.setCreatedId(user.getUserId());
            requirementHeadDTO.setCreationDate(new Date());
            requirementHeadDTO.setCreatedByIp("127.0.0.1");
            requirementHeadDTO.setLastUpdatedBy(requestInfo.getApplyBy());
            requirementHeadDTO.setLastUpdatedFullName(user.getNickname());
            requirementHeadDTO.setLastUpdatedId(user.getUserId());
            requirementHeadDTO.setDemandType(requestInfo.getDemandType());
            requirementHeadDTO.setExtBidFlag("N");
            requirementHeadDTO.setNextYearBudgetAmount(new BigDecimal(0));
            requirementHeadDTO.setThisYearBudgetAmount(new BigDecimal(0));
            requirementHeadDTO.setTotalBudget(new BigDecimal(0));
            requirementHeadDTO.setUsedBudget(new BigDecimal(0));
            requirementHeadDTO.setEdmSource(requestInfo.getSource());
            requirementHeadDTO.setEdmExNo(requestInfo.getExNo());
            String num = baseClient.seqGen(SequenceCodeConstant.SEQ_PMP_PR_APPLY_NUM);
            requirementHeadDTO.setRequirementHeadNum(num);
            purchaseRequirementMapper.insert(requirementHeadDTO);
            List<Record> recordList = new ArrayList<>();
            List<EdmPrLineDto> lineList = requestInfo.getLineList();
            List<String> materialCode = requestInfo.getLineList().stream().map(EdmPrLineDto::getCategoryCode).collect(Collectors.toList());
            Map<String, MaterialItem> mMap = baseClient.listMaterialItemsByCodes(materialCode);

            //批量查询组织
            List<com.midea.cloud.srm.model.base.entity.Organization> organizationList  = baseExtClient.listOrganizationByOrgCodes(lineList.stream().map(e -> e.getExtUseDepartmentCode()).distinct().collect(Collectors.toList()));
            Map<String, com.midea.cloud.srm.model.base.entity.Organization> orgMap = new HashMap<>(15);
            if(CollectionUtils.isNotEmpty(organizationList)) {
                orgMap = organizationList.stream().collect(Collectors.toMap(k -> k.getOrganizationCode(), Function.identity(), (k1, k2) -> k2));
            }

            //批量查询地址
            List<Long> organizationIdList = new ArrayList<>(15);
            orgMap.values().stream().forEach(o -> {
                if(!organizationIdList.contains(o.getOrganizationId())) {
                    organizationIdList.add(o.getOrganizationId());
                    if(StringUtils.isNotBlank(o.getParentOrganizationIds())) {
                        organizationIdList.add(Long.valueOf(o.getParentOrganizationIds()));
                    }
                }
            });

            //查询公司地址
            Map<Long,List<Record>> siteGroup = baseExtClient.getOrgAddressBatch(organizationIdList);

            //查询用户
            List<User> userList = rbacClient.listByUserNames(lineList.stream().map(e -> e.getExtUserCode()).distinct().collect(Collectors.toList()));
            Map<String, User> userMap = new HashMap<>(15);
            if(CollectionUtils.isNotEmpty(userList)) {
                userMap = userList.stream().collect(Collectors.toMap(k -> k.getUsername(), Function.identity(), (k1, k2) -> k2));
            }

            Map<String, com.midea.cloud.srm.model.base.entity.Organization> finalOrgMap = orgMap;
            Map<Long, List<Record>> finalSiteGroup = siteGroup;

            Map<String, User> finalUserMap = userMap;
            lineList.forEach(e -> {
                Record record = new Record();
                String mCode = e.getCategoryCode();
                MaterialItem mi = mMap.get(mCode);
                if (mi == null) {
                    throw new BaseException("物料不存在");
                }

                com.midea.cloud.srm.model.base.entity.Organization organ = finalOrgMap.getOrDefault(e.getExtUseDepartmentCode(), new com.midea.cloud.srm.model.base.entity.Organization());
                List<Record> orgAddress = finalSiteGroup.get(organ.getOrganizationId());
                if(CollectionUtils.isEmpty(orgAddress) && StringUtils.isNotBlank(organ.getParentOrganizationIds())) {
                    orgAddress = finalSiteGroup.get(Long.valueOf(organ.getParentOrganizationIds()));
                }
                if (CollectionUtils.isNotEmpty(orgAddress)) {
                    List<Record> defaults = orgAddress.stream().filter(e1 -> YesOrNo.YES.getValue().equals(e1.get("isDefault"))).collect(Collectors.toList());
                    Record addr = CollectionUtils.isNotEmpty(defaults)?defaults.get(0):orgAddress.get(0);
                    //收货人
                    record.put(PurchaseRequirementLineDTO::getExtReceiver, addr.get("receiver"));
                    //收货地址
                    record.put(PurchaseRequirementLineDTO::getReceiveAddress, addr.get("siteName"));
                    //收货人联系方式
                    record.put(PurchaseRequirementLineDTO::getReceiveTelephone, addr.get("receiverPhone"));
                }
                User u = finalUserMap.getOrDefault(e.getExtUserCode(), new User());

                record.put(PurchaseRequirementLineDTO::getRequirementHeadId, requirementHeadDTO.getRequirementHeadId());
                record.put(PurchaseRequirementLineDTO::getRequirementHeadNum, num);
                //品牌
                record.put(PurchaseRequirementLineDTO::getBrand, e.getBrand());
                //品类id
                record.put(PurchaseRequirementLineDTO::getCategoryId, mi.getCategoryId());
                //品列编码
                record.put(PurchaseRequirementLineDTO::getCategoryCode, mi.getCategoryCode());
                //品列名称
                record.put(PurchaseRequirementLineDTO::getCategoryName, mi.getCategoryName());
                //备注信息
                record.put(PurchaseRequirementLineDTO::getComments, e.getComments());
                //需求人列/需求部门
                record.put(PurchaseRequirementLineDTO::getDmandLineRequest, u.getNickname());
                //区域
                record.put(PurchaseRequirementLineDTO::getExtAreaCode, organ.getOrganizationRegion());
                //费用科目
                record.put("extFeeSubject", e.getExtFeeSubject());
                //品类
                record.put(PurchaseRequirementLineDTO::getExtMaterialModel, mi.getMaterialType());
                //预估总价
                record.put(PurchaseRequirementLineDTO::getExtPredictAmount, new BigDecimal(e.getOrderQuantity()).multiply(new BigDecimal(e.getExtPredictPrice())));
                //预估单价
                record.put(PurchaseRequirementLineDTO::getExtPredictPrice, e.getExtPredictPrice());
                //是否商品
                record.put(PurchaseRequirementLineDTO::getExtProductFlag, "N");
                //使用部门编码
                record.put(PurchaseRequirementLineDTO::getExtUseDepartmentCode, e.getExtUseDepartmentCode());
                //使用部门id
                record.put(PurchaseRequirementLineDTO::getExtUseDepartmentId, organ.getOrganizationId());
                //使用部门名称
                record.put(PurchaseRequirementLineDTO::getExtUseDepartmentName, organ.getOrganizationName());
                //用途
                record.put(PurchaseRequirementLineDTO::getExtUseTo, e.getExtUseTo());
                //使用人信息编码
                record.put(PurchaseRequirementLineDTO::getExtUserCode, e.getExtUserCode());
                //使用人信息名称
                record.put(PurchaseRequirementLineDTO::getExtUserName, u.getNickname());
                //使用人联系方式
                record.put("extUserPhone", e.getExtUserPhone());
                //物料编码
                record.put(PurchaseRequirementLineDTO::getMaterialCode, mi.getMaterialCode());
                //物料id
                record.put(PurchaseRequirementLineDTO::getMaterialId, mi.getMaterialId());
                //物料名称
                record.put(PurchaseRequirementLineDTO::getMaterialName, mi.getMaterialName());
                //本次需求时间
                record.put(PurchaseRequirementLineDTO::getRequirementDate, e.getRequirementDate());
                //需求数量
                record.put(PurchaseRequirementLineDTO::getRequirementQuantity, e.getOrderQuantity());
                //基本计量单位
                record.put(PurchaseRequirementLineDTO::getUnit, mi.getUnitName());
                //基本计量单位编码
                record.put(PurchaseRequirementLineDTO::getUnitCode, mi.getUnit());
                record.put(PurchaseRequirementLineDTO::getExtAttachName, e.getExtAttachName());
                log.info("附件的地址" + e.getExtAttachName());
                /* 修改成异步定时同步附件信息---提升edm接口请求响应速度
                    if (StringUtils.isNotBlank(e.getExtAttachName())) {
                    MockMultipartFile multipartFile;
                    String aa = e.getExtAttachName().substring(0, e.getExtAttachName().indexOf("?"));
                    String fileType = aa.substring(aa.lastIndexOf(".") + 1).toLowerCase();
                    try {
                        HttpClient httpClient = HttpClients.createDefault();
                        HttpGet httpGet = new HttpGet(e.getExtAttachName());
                        HttpResponse response = httpClient.execute(httpGet);
                        HttpEntity entity = response.getEntity();
                        InputStream inputStream = entity.getContent(); // 获取输入流来读取文件内容
                        byte[] fileBytes = IOUtils.toByteArray(inputStream); // 将输入流转为字节数组
                        inputStream.close(); // 关闭输入流
                        String originalFilename = UUID.randomUUID().toString();
                        log.info("附件类型信息---***===" + fileType);
                        multipartFile =  new MockMultipartFile(originalFilename, originalFilename + "." + fileType, "application/" + fileType, fileBytes); // 创建MultipartFile对象
                    } catch (IOException ex) {
                        log.info("处理附件===========" + JSONObject.toJSONString(ex));
                        throw new BaseException("附件下载失败" + "---" + ex.getMessage());
                    }
                    String sourceType = "WEB_APP";
                    String uploadType = FileUploadType.DEF.name();
                    String fileModular = "sup";
                    String fileFunction = "vendorBiddingManagement";
                    log.info("附件类型信息===" + multipartFile.getContentType());
                    Fileupload fl = fileCenterClient.feignClientUpload(multipartFile, sourceType, uploadType, fileModular, fileFunction, fileType);
                    log.info("返回的信息===" + JSONObject.toJSONString(fl));
                    record.put(PurchaseRequirementLineDTO::getExtAttachId, fl.getFileuploadId());
                    record.put(PurchaseRequirementLineDTO::getExtAttachName, fl.getFileSourceName());
                }*/
                record.put("tenantId", e.getTenantId());
                record.put("edmOrgId", e.getEdmOrgId());
                record.put("externalId", e.getExternalId());
                recordList.add(record);
            });
            qlService.save("PurchaseRequirementLine", recordList);
            log.info("申请人信息===" + requestInfo.getApplyBy() + "===" + user.getNickname() + "===" + user.getUserId());
            LambdaUpdateWrapper<RequirementLine> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(RequirementLine::getRequirementHeadId, requirementHeadDTO.getRequirementHeadId()).
                    set(RequirementLine::getCreatedBy, requestInfo.getApplyBy()).
                    set(RequirementLine::getCreatedFullName, user.getNickname()).
                    set(RequirementLine::getCreatedId, user.getUserId()).
                    set(RequirementLine::getLastUpdatedBy, requestInfo.getApplyBy()).
                    set(RequirementLine::getLastUpdatedFullName, user.getNickname()).
                    set(RequirementLine::getLastUpdatedId, user.getUserId());
            requirementLineMapper.update(null, updateWrapper);

            //添加异步同步附件任务
            edmattachSyncService.addSyncTask(requirementHeadDTO.getRequirementHeadId());
            reMap.put("code", "00000");
            reMap.put("message", "成功");
        } catch (Exception e) {
            reMap.put("code", "500");
            reMap.put("message", e.getMessage());
            log.error("异常信息日志" , e);
            throw new BaseException(e.getMessage());
        } finally {
            AutoMetaObjContext.manullyRemove();
        }
        return reMap;
    }

    @ApiOperation(value = "采购需求提报明细导出", notes = "采购需求提报明细导出")
    @PostMapping(path = "/exportRequirementLine")
    public void exportRequirementLine(HttpServletResponse response,@RequestBody ExtPrSouRequirementLineExportRequestDto queryParam) throws Exception {
        purchaseRequirementService.exportRequirementLine(queryParam, response);
    }
}
