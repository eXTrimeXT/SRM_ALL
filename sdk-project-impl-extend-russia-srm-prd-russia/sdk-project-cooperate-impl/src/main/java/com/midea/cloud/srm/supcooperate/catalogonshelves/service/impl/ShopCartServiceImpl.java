package com.midea.cloud.srm.supcooperate.catalogonshelves.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.ServiceException;
import com.midea.cloud.common.enums.ImportStatus;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.listener.AnalysisEventListenerImpl;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.EasyExcelUtil;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryWrapper;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.util.OpenApiUtil;
import com.midea.cloud.srm.feign.BaseExtClient;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.base.material.MaterialItem;
import com.midea.cloud.srm.model.base.material.dto.MaterialItemQueryDto;
import com.midea.cloud.srm.model.base.monitor.enums.YesOrNo;
import com.midea.cloud.srm.model.base.noticetemplate.entity.NoticeTemplate;
import com.midea.cloud.srm.model.base.noticetemplate.enums.NoticeTemplateModeEnum;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.objectx.dto.ConditionDTO;
import com.midea.cloud.srm.model.pj.base.organization.entity.OrgCollectInfo;
import com.midea.cloud.srm.model.pj.base.organization.entity.OrgCompanyAddress;
import com.midea.cloud.srm.model.pj.base.organization.entity.Site;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.pm.pr.catalogonshelves.entity.CatalogOnShelves;
import com.midea.cloud.srm.model.pm.pr.shopcart.entity.ShopCart;
import com.midea.cloud.srm.model.pm.pr.shopcart.enums.ShopCartStatus;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.sou.agreement.enums.AgreementStatusEnums;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.supcooperate.dto.ShopCartDto;
import com.midea.cloud.srm.model.supcooperate.ext.catalogonshelvess.ExtCatalogOnShelvesStatusEnum;
import com.midea.cloud.srm.model.supcooperate.ext.catalogonshelvess.dto.OnShelvesDto;
import com.midea.cloud.srm.model.supcooperate.ext.catalogonshelvess.dto.ShopCartModelDto;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenQueryWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.supcooperate.catalogonshelves.mapper.ExtShopCartMapper;
import com.midea.cloud.srm.supcooperate.catalogonshelves.service.IShopCartService;
import com.midea.cloud.srm.supcooperate.ext.deliverynotes.dto.ExtDeliveryNoteDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 购物车
 * @author huangbf3
 */
@Slf4j
@Service
public class ShopCartServiceImpl implements IShopCartService {
    @Autowired
    private BaseClient baseClient;
    @Autowired
    private BaseExtClient baseExtClient;
    @Autowired
    private RbacClient rbacClient;
    @Autowired
    private PjProjectExtClient pjProjectExtClient;
    @Autowired
    private FileCenterClient fileCenterClient;
    @Autowired
    private QlService qlService;
    @Autowired
    private ExtShopCartMapper extShopCartMapper;
    @Autowired
    private QlOpenClient qlOpenClient;

    private static final int NUM5 = 5;
    public static final String SUP_CE_SHOP_IMPORT = "SUP_CE_SHOP_IMPORT";
    private static final String REGION = "REGION";
    private static final String OU = "OU";
    @Override
    public void importShopCartModelDownload(HttpServletResponse response) throws IOException {
        InputStream inputStream = this.getClass().getResourceAsStream("/template/购物车导入模版.xlsx");
        assert inputStream != null;
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        OutputStream outputStream = EasyExcelUtil.getServletOutputStream(response, "购物车导入模版");
        workbook.write(outputStream);
        /*String fileName = "购物车导入模版";
        List<ShopCartModelDto> shopCartModelDtos = new ArrayList<>();
        ServletOutputStream outputStream = EasyExcelUtil.getServletOutputStream(response, fileName);
        // 指定标颜色的行
        List<Integer> rows = new ArrayList<>();
        // 指定标颜色的列
        List<Integer> columns = Arrays.asList(0,1,2,3,4);
        TitleColorSheetWriteHandler titleColorSheetWriteHandler = new TitleColorSheetWriteHandler(rows, columns, IndexedColors.RED.index);
        EasyExcelUtil.writeExcelWithModel(outputStream, shopCartModelDtos, ShopCartModelDto.class, fileName, titleColorSheetWriteHandler);*/
    }

    @Override
    public Map<String, Object> importShopCartExcel(MultipartFile file, Fileupload fileupload) {
        StopWatch stopWatch = new StopWatch("购物车导入");
        stopWatch.start();
        // 文件校验
        EasyExcelUtil.checkParam(file, fileupload);
        // 读取数据
        List<ShopCartModelDto> shopCartModelDtos = readData(file);

        // 是否有报错标识
        AtomicBoolean errorFlag = new AtomicBoolean(false);
        // 获取数据
        getImportData(shopCartModelDtos, errorFlag);
        if (errorFlag.get()) {
            // 有错误,上传错误文件
            Fileupload errorFileupload = EasyExcelUtil.uploadErrorFile(fileCenterClient, fileupload, shopCartModelDtos, ShopCartModelDto.class, file);
            return ImportStatus.importError(errorFileupload.getFileuploadId(), errorFileupload.getFileSourceName());
        } else {
            // 保存
            try {
                saveImportData(shopCartModelDtos);
            } catch (Exception e) {
                log.error("批量保存购物车失败:{}", e.getMessage());
            }
        }
        stopWatch.stop();

        log.info(stopWatch.prettyPrint());

        return null;
    }

    private void saveImportData(List<ShopCartModelDto> shopCartModelDtos) {

        if(shopCartModelDtos.isEmpty()){
            return;
        }
        List<Record> shopCarts = new ArrayList<>(shopCartModelDtos.size());

        for(ShopCartModelDto shopCartModelDTO: shopCartModelDtos){
            //0 是否商品显示否，1 是否商品显示是
            int flag=1;
            // 验证物资编码是否在“内部商城上下架”版块能找到
            QlQueryWrapper catalogOnShelvesWrapper = QlWrappers.query(MqlType.CATALOG_ON_SHELVES)
                    .eq(ShopCartDto::getStatus, ExtCatalogOnShelvesStatusEnum.ON_SHELVES)
                    .eq(ShopCartDto::getExtPriceLibraryStatus, AgreementStatusEnums.EXECUTING.getCode())
                    .eq(ShopCartDto::getMaterialCode, shopCartModelDTO.getMaterialCode());
            // List<ShopCartDto>catalogOnShelvesList=qlService.queryByWrapper(catalogOnShelvesWrapper, ShopCartDto.class);
            List<Record> recordList = qlService.queryByWrapper(catalogOnShelvesWrapper, Record.class);
            List<ShopCartDto> catalogOnShelvesList=new ArrayList<>();
            catalogOnShelvesList = OpenApiUtil.toListValue(recordList, ShopCartDto.class);
            if(catalogOnShelvesList.isEmpty()){
                flag=0;
            }
            //使用部门在”组织设置“里的收货地址中的区域，是否等于上下架版块的该物资所在的协议的区域
            List<String>orgCodeList=new ArrayList<>();
            orgCodeList.add(shopCartModelDTO.getExtDepartmentcode());
            List<com.midea.cloud.srm.model.base.entity.Organization>orglist=baseExtClient.listOrganizationByOrgCodes(orgCodeList);
            if(orglist.isEmpty()){
                throw new ServiceException(shopCartModelDTO.getExtDepartmentcode()+"该组织编码不存在");
            }
            QlOpenQueryWrapper siteWrapper = QlOpenWrappers.query("Site");
            siteWrapper.eq(true, Site::getOrganizationId, orglist.get(0).getOrganizationId());
            siteWrapper.eq(true, Site::getIsDefault, com.midea.cloud.common.enums.YesOrNo.YES.getValue());
            List<Site> siteList = qlOpenClient.query(ContextPath.BASE, siteWrapper, Site.class);
            //区域
            String region="";
            //公司级实体名称
            String orgCode ="";
            if(flag==1){
                //如果区域没有值
                if(siteList.isEmpty() || siteList.get(0).getAddressRegion().isEmpty())
                {
                    //继续往上找
                    Long orgId=null;
                    if(orglist.get(0).getParentOrganizationIds()!=null){
                        orgId= Long.parseLong(orglist.get(0).getParentOrganizationIds());
                    }
                    while(true) {
                        siteWrapper.eq(true, Site::getOrganizationId, orgId);
                        siteWrapper.eq(true, Site::getIsDefault, com.midea.cloud.common.enums.YesOrNo.YES.getValue());
                        List<Site> siteList2 = qlOpenClient.query(ContextPath.BASE, siteWrapper, Site.class);
                        if(siteList2!=null && !siteList2.isEmpty()){
                            region =siteList2.get(0).getAddressRegion();
                            break;
                        }else {
                            com.midea.cloud.srm.model.base.entity.Organization orglistById=baseExtClient.getOrganizationByOrgId(orgId);
                            if(orglistById==null){
                                break;
                            }
                            if(orglistById.getParentOrganizationIds()!=null){
                                orgId=Long.parseLong(orglistById.getParentOrganizationIds());
                            }else{
                                break;
                            }

                        }
                    }
                }else{
                    region=siteList.get(0).getAddressRegion();
                }
                //找到区域后，判断是否等于上下架版块的该物资所在的协议的区域
                if(region !=null && !"".equals(region)){
                    if(!region.equals(shopCartModelDTO.getExtAreaCode())){
                        flag=0;
                    }
                }else{
                    flag=0;
                }
            }
            //使用部门在组织设置版块中，逐级往上找到公司级，是否等于上下架版块的该物资所在的协议的业务实体
            if(flag==1) {
                Long orgId = Long.parseLong(orglist.get(0).getParentOrganizationIds());
                while (true) {
                    com.midea.cloud.srm.model.base.entity.Organization orglistById = baseExtClient.getOrganizationByOrgId(orgId);
                    if (orglistById != null) {
                        if (!Objects.equals(orglistById.getOrganizationTypeCode(), OU)) {
                            orgId = Long.parseLong(orglistById.getParentOrganizationIds());
                        } else {
                            orgCode = orglistById.getOrganizationCode();
                            //判断是否等于上下架版块的该物资所在的协议的业务实体
                            //循环上下架信息，通过区域和业务实体编码，判断是否属于商品
                            for (ShopCartDto shopCartDto : catalogOnShelvesList) {
                                if (shopCartDto.getExtAreaCode().contains(region) && shopCartDto.getExtOrgCodeList().contains(orgCode)) {
                                    flag = 1;
                                    break;
                                } else {
                                    flag = 0;
                                }
                            }
                            break;
                        }
                    } else {
                        flag = 0;
                        break;
                    }
                }
            }

            if(flag==0){
                shopCartModelDTO.setExtIsGoods(com.midea.cloud.common.enums.YesOrNo.NO.getValue());
            }else{
                shopCartModelDTO.setExtIsGoods(com.midea.cloud.common.enums.YesOrNo.YES.getValue());
                //该物资在上下架版块中同时存在两个协议中，则报错不让导入，提示“该物资同时存在于两个协议中，不允许导入，请人为识别”
                QlQueryWrapper wrapper = QlWrappers.query(MqlType.CATALOG_ON_SHELVES)
                        .eq(ShopCartDto::getStatus, ExtCatalogOnShelvesStatusEnum.ON_SHELVES)
                        .eq(ShopCartDto::getExtPriceLibraryStatus, AgreementStatusEnums.EXECUTING.getCode())
                        .eq(ShopCartDto::getMaterialCode, shopCartModelDTO.getMaterialCode())
                        .eq(ShopCartDto::getExtAreaCode, shopCartModelDTO.getExtAreaCode())
                        .contains(ShopCartDto::getExtOrgCodeList, orgCode);
                List<Record> record2List = qlService.queryByWrapper(wrapper, Record.class);
                List<ShopCartDto> catalogList=new ArrayList<>();
                catalogList = OpenApiUtil.toListValue(record2List, ShopCartDto.class);
                if(catalogList.size()>1){
                    throw new ServiceException(shopCartModelDTO.getMaterialCode()+"该物资同时存在于多个协议中，不允许导入，请人为识别");
                }
            }


            Record shopCart = JSONObject.parseObject(JSONObject.toJSONString(shopCartModelDTO),Record.class);
            shopCart.put(ShopCart::getStatus, ShopCartStatus.DRAFT.getCode());
            //预算单价 等于 参考价
            shopCart.put(ShopCart::getUnitPrice, shopCartModelDTO.getExtReferencePrice());
            shopCarts.add(shopCart);
        }
        qlService.save(MqlType.SHOP_CART,shopCarts);
    }

    @Override
    public OnShelvesDto extAdd(Map<String, Object> query) {
        PageUtil.startPage(1, 1);
        OnShelvesDto list= extShopCartMapper.extAdd(query);
        return list;
    }

    private void getImportData(List<ShopCartModelDto> shopCartModelDtos, AtomicBoolean errorFlag) {
        if (CollectionUtils.isNotEmpty(shopCartModelDtos)) {
            for (ShopCartModelDto shopCartModelDTO : shopCartModelDtos) {
                StringBuilder errorMsg = new StringBuilder();
                if (StringUtils.isEmpty(shopCartModelDTO.getSummaryNickname())) {
                    errorFlag.set(true);
                    errorMsg.append("汇总人不能为空");
                }
                if (StringUtils.isEmpty(shopCartModelDTO.getDeptLeaderUserNickname())) {
                    errorFlag.set(true);
                    errorMsg.append("部门领导不能为空");
                }
                if (shopCartModelDTO.getRequirementDateStr()==null) {
                    errorFlag.set(true);
                    errorMsg.append("需求日期不能为空");
                }else{
                    Instant instant = shopCartModelDTO.getRequirementDateStr().toInstant();
                    ZoneId zoneId = ZoneId.systemDefault();
                    LocalDate localDate = instant.atZone(zoneId).toLocalDate();
                    shopCartModelDTO.setRequirementDate(localDate);
                }
                if (StringUtils.isEmpty(shopCartModelDTO.getMaterialCode())) {
                    errorFlag.set(true);
                    errorMsg.append("物料编码不能为空");
                }
                if (shopCartModelDTO.getRequirementNum()==null) {
                    errorFlag.set(true);
                    errorMsg.append("需求数量不能为空");
                }
                if (StringUtils.isEmpty(shopCartModelDTO.getExtUserPhone())) {
                    errorFlag.set(true);
                    errorMsg.append("使用人联系方式不能为空");
                }
                if (shopCartModelDTO.getExtReferencePrice()==null) {
                    errorFlag.set(true);
                    errorMsg.append("参考价不能为空");
                }
                if (StringUtils.isEmpty(shopCartModelDTO.getExtUseTo())) {
                    errorFlag.set(true);
                    errorMsg.append("用途不能为空");
                }
                if (StringUtils.isEmpty(shopCartModelDTO.getExtDepartmentcode())) {
                    errorFlag.set(true);
                    errorMsg.append("使用部门不能为空");
                }
                if(!Objects.isNull(shopCartModelDTO.getExtBuyTypeComment())&&shopCartModelDTO.getExtBuyTypeComment().length()>300){
                    errorFlag.set(true);
                    errorMsg.append("备注长度不能超过300");
                }
                if (errorMsg.length() > 0) {
                    shopCartModelDTO.setErrorMsg(errorMsg.toString());
                } else {
                    shopCartModelDTO.setErrorMsg(null);
                }
//                Date date =new Date();
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTime(date);
//                Date newDate = calendar.getTime();
//                calendar.add(Calendar.DAY_OF_MONTH, 10);
//                int comparisonResult = shopCartModelDTO.getRequirementDateStr().compareTo(newDate);
//                //如果 需求日期<当前日期+10天
//                if(comparisonResult<0){
//                    //获取钉钉模板编码提示信息
//                    NoticeTemplate noticeTemplate = baseClient.listPageNoticeTemplate(new NoticeTemplate().setNoticeTemplateCode(SUP_CE_SHOP_IMPORT)
//                            .setNoticeTemplateMode(NoticeTemplateModeEnum.MESSAGE.getValue()).setNoticeTemplateValid(com.midea.cloud.common.enums.YesOrNo.YES.getValue())).getList().get(0);
//                    String template = noticeTemplate.getNoticeTemplateContent();
//                    errorFlag.set(true);
//                    errorMsg.append(template);
//                }
            }
            extracted(shopCartModelDtos, errorFlag);

        }
    }

    /**
     * 查询上下架参考价
     * @param materialIdList
     * @param orgId
     * @param extAreaCodeList
     * @return
     */
    private Map<String, Record> queryOnshelvesWithMatrialCodeList(List<Long> materialIdList, Long orgId, List<String> extAreaCodeList) {

        StopWatch stopWatch = new StopWatch("查询上下架");
        stopWatch.start("查询数据库");
        Map<String, Record> onshelesMap = new HashMap<>(15);
        if(CollectionUtils.isEmpty(materialIdList) || ObjectUtils.anyNull(orgId) || CollectionUtils.isEmpty(extAreaCodeList)) {
            return onshelesMap;
        }

        QlQueryWrapper qlQueryWrapper = QlWrappers.query(MqlType.CATALOG_ON_SHELVES)
                .in(CatalogOnShelves::getMaterialId, materialIdList);
        qlQueryWrapper.contains( "extOrgIdList", Objects.toString(orgId));

        /**
         * 协议状态进行中
         */
        qlQueryWrapper.eq("extPriceLibraryStatus","EXECUTING");

        //查询上下架
        List<Record> onshelvesList = qlService.queryByWrapper(qlQueryWrapper, Record.class);
        if(CollectionUtils.isEmpty(onshelvesList)) {
            return onshelesMap;
        }
        stopWatch.stop();

        stopWatch.start("遍历结果集");

        //返回结果的Map onshelesMap  key值拼接规则：物料编码+区域编码
        onshelvesList.stream().filter(onshelves -> StringUtils.isNotBlank(onshelves.getString("extAreaCode"))).forEach(onshelves -> {

            String[] extAreaCodeArrarys = onshelves.getString("extAreaCode").split(SrmConstant.SIG_3);
            Arrays.stream(extAreaCodeArrarys).filter(code -> extAreaCodeList.contains(code)).forEach(code -> {
                String key = StringUtils.joinWith(SrmConstant.UNDER_LINE, onshelves.get(CatalogOnShelves::getMaterialId), code);
                onshelesMap.put(key, onshelves);
            });

        });
        stopWatch.stop();

        log.info(stopWatch.prettyPrint());

        return onshelesMap;
    }

    /**
     * 赋值
     * @param shopCartModelDtos 购物车参数
     * @param errorFlag 是否错误
     */
    private void extracted(List<ShopCartModelDto> shopCartModelDtos, AtomicBoolean errorFlag) {
        if (!errorFlag.get()) {
            List<String> materialCodeList = shopCartModelDtos.stream().map(item -> item.getMaterialCode()).collect(Collectors.toList());

            Map<String,MaterialItem> materialItemMap = baseClient.listMaterialByCodeBatch(materialCodeList)
                    .stream().collect(Collectors.toMap(MaterialItem::getMaterialCode,t->t));

            Map<String,MaterialItem> invalidMaterialMap = getInvalidMaterialMap(materialCodeList);

            Set<String> structSet = materialItemMap.values().stream().map(item->item.getStruct()).collect(Collectors.toSet());

            Set<String> deptLeaderUserNicknameSet = shopCartModelDtos.stream().map(item->item.getDeptLeaderUserNickname()).collect(Collectors.toSet());
            Map<String,User> deptLeaderUserMap = rbacClient.listUsersByUsersParamCode(new ArrayList<>(deptLeaderUserNicknameSet)).stream().collect(Collectors.toMap(User::getUsername, t->t));

            Set<String> summaryNicknameSet = shopCartModelDtos.stream().map(item->item.getSummaryNickname()).collect(Collectors.toSet());
            Map<String,User> summaryUserMap = rbacClient.listUsersByUsersParamCode(new ArrayList<>(summaryNicknameSet)).stream().collect(Collectors.toMap(User::getUsername, t->t));

            LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
            HrUserOrgnizationDto hrUserOrgnizationDto = pjProjectExtClient.getHrUserOrgnizationByUsername(loginAppUser.getUsername());
            List<Long> extSecondCategoryIdList = new ArrayList<>();
            if(structSet.size()>0){
               for(String struct:structSet){
                    String[] structArr = struct.split("-");
                    if(structArr.length>1){
                        extSecondCategoryIdList.add(Long.valueOf(structArr[1]));
                    }
                }
            }
            Map<Long, PurchaseCategory> purchaseCategoryMap = baseClient.listCategoryByIds(extSecondCategoryIdList)
                    .stream().collect(Collectors.toMap(PurchaseCategory::getCategoryId,t->t));

            HrUserOrgnizationDto userOrganization = pjProjectExtClient.getHrUserOrgnizationByUsername(loginAppUser.getUsername());

            Map<String, Organization> depMap = new HashMap<>(15);

            Map<Long, List<Record>> areaCodeMap = new HashMap<>(15);

            Map<Long, Organization> paMap = new HashMap<>(15);

            for (ShopCartModelDto shopCartModelDTO : shopCartModelDtos) {
                StringBuilder errorMsg = new StringBuilder();
                shopCartModelDTO.setCurrencyCode("RMB");
                shopCartModelDTO.setCurrencyName("RMB");
                if (!materialItemMap.containsKey(shopCartModelDTO.getMaterialCode())) {
                    errorFlag.set(true);
                    errorMsg.append("找不到物料");
                }else if(invalidMaterialMap.containsKey(shopCartModelDTO.getMaterialCode())){
                    errorFlag.set(true);
                    errorMsg.append("物资编码已禁用，请在物料查询中选取生效编码");
                } else {
                    MaterialItem materialItem = materialItemMap.get(shopCartModelDTO.getMaterialCode());
                    shopCartModelDTO.setCategoryCode(materialItem.getCategoryCode());
                    shopCartModelDTO.setCategoryId(materialItem.getCategoryId());
                    shopCartModelDTO.setCategoryName(materialItem.getCategoryName());
                    shopCartModelDTO.setSpecification(materialItem.getMaterialType());
                    shopCartModelDTO.setUnit(materialItem.getUnit());
                    shopCartModelDTO.setUnitName(materialItem.getUnitName());
                    shopCartModelDTO.setMaterialId(materialItem.getMaterialId());
                    shopCartModelDTO.setMaterialName(materialItem.getMaterialName());
                    if (StringUtils.isBlank(materialItem.getStruct())) {
                        errorFlag.set(true);
                        errorMsg.append("找不到品类");
                    } else {
                        String[] structArr = materialItem.getStruct().split("-");
                        if (structArr.length < 2) {
                            errorFlag.set(true);
                            errorMsg.append("找不到品类");
                        } else if (!purchaseCategoryMap.containsKey(Long.valueOf(structArr[1]))) {
                            errorFlag.set(true);
                            errorMsg.append("找不到品类");
                        } else {
                            PurchaseCategory purchaseCategory = purchaseCategoryMap.get(Long.valueOf(structArr[1]));
                            shopCartModelDTO.setExtSecondCategoryId(purchaseCategory.getCategoryId());
                            shopCartModelDTO.setExtSecondCategoryCode(purchaseCategory.getCategoryCode());
                            shopCartModelDTO.setExtSecondCategoryName(purchaseCategory.getCategoryName());
                        }
                    }
                }
                shopCartModelDTO.setExtIsGoods("N");
                if (!deptLeaderUserMap.containsKey(shopCartModelDTO.getDeptLeaderUserNickname())) {
                    errorFlag.set(true);
                    errorMsg.append("找不到部门领导");
                } else {
                    User user = deptLeaderUserMap.get(shopCartModelDTO.getDeptLeaderUserNickname());
                    shopCartModelDTO.setDeptLeaderUserId(user.getUserId());
                    shopCartModelDTO.setDeptLeaderUserNickname(user.getNickname());
                }
                if (!summaryUserMap.containsKey(shopCartModelDTO.getSummaryNickname())) {
                    errorFlag.set(true);
                    errorMsg.append("找不到汇总人");
                } else {

                    User summaryUser = summaryUserMap.get(shopCartModelDTO.getSummaryNickname());
                    shopCartModelDTO.setSummaryUserId(summaryUser.getUserId());
                    shopCartModelDTO.setSummaryNickname(summaryUser.getNickname());
                    if (hrUserOrgnizationDto == null || hrUserOrgnizationDto.getOuOrganization() == null) {
                        errorFlag.set(true);
                        errorMsg.append("找不到业务实体");
                    } else if (hrUserOrgnizationDto.getDepartmentOrganization() == null) {
                        errorFlag.set(true);
                        errorMsg.append("找不到部门");
                    } else {
                        Organization ouOrganization = hrUserOrgnizationDto.getOuOrganization();

                        shopCartModelDTO.setOrgCode(ouOrganization.getOrganizationCode());
                        shopCartModelDTO.setOrgId(ouOrganization.getOrganizationId());
                        shopCartModelDTO.setOrgName(ouOrganization.getOrganizationName());

                    }
                }


                Organization dep = null;
                if(depMap.containsKey(shopCartModelDTO.getExtDepartmentcode())) {
                    dep = depMap.get(shopCartModelDTO.getExtDepartmentcode());
                } else {
                    Organization o = new Organization();
                    o.setOrganizationCode(shopCartModelDTO.getExtDepartmentcode());
                    dep = baseClient.getOrganization(o);
                    depMap.put(shopCartModelDTO.getExtDepartmentcode(), dep);
                }


                if (dep == null) {
                    errorFlag.set(true);
                    errorMsg.append("使用部门不存在");
                } else {
                    log.info("用户的组织信息===" + JSONObject.toJSONString(userOrganization));
                    if (userOrganization == null) {
                        errorFlag.set(true);
                        errorMsg.append("当前登录人没有组织信息");
                    } else {
                        //公司
                        Organization ouOrganization = userOrganization.getOuOrganization();
                        if (ouOrganization == null) {
                            errorFlag.set(true);
                            errorMsg.append("当前登录人没有业务实体信息");
                        } else {
                            shopCartModelDTO.setExtCeeaDeptid(dep.getOrganizationId());
                            shopCartModelDTO.setExtDepartment(dep.getOrganizationName());
                            if (StringUtils.isEmpty(dep.getParentOrganizationIds())) {
                                errorFlag.set(true);
                                errorMsg.append("使用部门没有上级");
                            } else {
                                String pId = dep.getParentOrganizationIds();
                                boolean b = true;
                                for (int i = 0; i < NUM5; i++) {
                                    log.info("---" + i + "第N次===" + pId);
                                    Organization pa = null;

                                    if(paMap.containsKey(Long.valueOf(pId))) {
                                        pa = paMap.get(Long.valueOf(pId));
                                    } else {
                                        pa = baseClient.get(Long.valueOf(pId));
                                        paMap.put(Long.valueOf(pId), pa);
                                    }

                                    if (ouOrganization!=null&&pa!=null
                                        &&ouOrganization.getOrganizationId()!=null&&
                                            pa.getOrganizationId()!=null&&
                                            ouOrganization.getOrganizationId().equals(pa.getOrganizationId())) {
                                        b = false;
                                        break;
                                    }
                                    if(pa!=null&&pa.getParentOrganizationIds()!=null) {
                                        pId = pa.getParentOrganizationIds();
                                    } else {
                                        break;
                                    }
                                }
                                if (b) {
                                    errorFlag.set(true);
                                    errorMsg.append("使用部门没在登录人业务实体下");
                                }

                                List<Record> records = null;
                                if(areaCodeMap.containsKey(dep.getOrganizationId())) {
                                    records = areaCodeMap.get(dep.getOrganizationId());
                                } else {
                                    records = baseExtClient.getOrgAddress(dep.getOrganizationId());
                                    areaCodeMap.put(dep.getOrganizationId(), records);
                                }

                                if (records != null && records.size() > 0) {
                                    for (Record record : records) {
                                        if (StringUtils.equals(YesOrNo.Y.name(), record.getString("isDefault"))) {
                                            shopCartModelDTO.setExtAddressId(record.getLong("siteId"));
                                            shopCartModelDTO.setExtAddress(record.getString("siteName"));
                                            shopCartModelDTO.setExtAddressName(record.getString("siteName"));

                                            shopCartModelDTO.setExtReceiver(record.getString("receiver"));
                                            shopCartModelDTO.setExtReceiverContact(record.getString("receiverPhone"));
                                            shopCartModelDTO.setExtAreaCode(record.getString("addressRegion"));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
                if (errorMsg.length() > 0) {
                    shopCartModelDTO.setErrorMsg(errorMsg.toString());
                } else {
                    shopCartModelDTO.setErrorMsg(null);
                }
            }

            if(!errorFlag.get()) {
                List<Long> materialIdList = shopCartModelDtos.stream().map(s -> s.getMaterialId()).distinct().collect(Collectors.toList());
                List<String> extAreaCodeList = shopCartModelDtos.stream().map(s -> s.getExtAreaCode()).distinct().collect(Collectors.toList());
                Map<String, Record> onshelvesMap = queryOnshelvesWithMatrialCodeList(materialIdList, shopCartModelDtos.get(0).getOrgId(), extAreaCodeList);

                shopCartModelDtos.stream().forEach(model -> {
                    model.setExtIsGoods(YesOrNo.N.name());
                    String key = StringUtils.joinWith(SrmConstant.UNDER_LINE, model.getMaterialId(), model.getExtAreaCode());
                    if(onshelvesMap.containsKey(key)) {
                        Record record = onshelvesMap.get(key);
                        BigDecimal extReferencePrice = record.getBigDecimal("extReferencePrice");
                        if(!Objects.isNull(extReferencePrice)) {
                            model.setExtIsGoods(YesOrNo.Y.name());
                            model.setExtReferencePrice(extReferencePrice);
                        }
                        /**
                         * 取记录的最小起订量
                         */
                        BigDecimal orderQuantityMinimum = record.getBigDecimal("orderQuantityMinimum");
                        //判断最小起订量是否大于需求数，如果大于，填错误信息
                        if(orderQuantityMinimum.compareTo(model.getRequirementNum())==1){
                            errorFlag.set(true);
                            model.setErrorMsg("需求数小于最小起订量");
                        }
                    }
                });
            }

            /**
             * 接受批量查询的返回结果
             */
           /*List<ShopCartModelDto>resList=extShopCartMapper.getTempList(tempList);*/
            /**
             * 根据resList的结果 反查赋值
             */
            /**
             * map作用：因为要根据resList的结果回调赋值给原数组
             * 又因为要把resList的记录分别赋值给对应的原集合
             * 如果双重循环，一个个判断是否对应，时间复杂度爆炸
             * 所以考虑使用map做一个哈希，具体来说
             * 把 materialCode+'-'+orgId+'-'+extAreaCode 拼接成一个字符串 作为hash的key
             * extIsGoods记录是否商品
             * extReferencePrice记录协商价
             */
            /**
             * 因为物料编码可能有多个记录，做个计数器 拼接在后面
            /* */
         /*   String s1=shopCartModelDtos.get(0).getExtAreaCode();
            String s2=shopCartModelDtos.get(0).getOrgId().toString();
            List<ShopCartModelDto>res=extShopCartMapper.getList(s1,s2);
            HashMap<String,BigDecimal>prices=new HashMap<>();
            for(ShopCartModelDto shopCartModelDto:res){
                prices.put(shopCartModelDto.getMaterialCode(),shopCartModelDto.getExtReferencePrice());
            }
            for(ShopCartModelDto shopCartModelDto:shopCartModelDtos){
                if(prices.containsKey(shopCartModelDto.getMaterialCode())){
                    shopCartModelDto.setExtIsGoods("Y");
                   shopCartModelDto.setExtReferencePrice(prices.get(shopCartModelDto.getMaterialCode()));
                }
                else{
                    shopCartModelDto.setExtIsGoods("N");
                }
            }*/
            /*HashMap<String,Boolean>org=new HashMap<>();
            HashMap<String,Boolean>area=new HashMap<>();
            HashMap<String,BigDecimal>price=new HashMap<>();*/
            //resList shujuku
            //shopCartModelDtos 导入的数据 区域和组织
         /*   for(ShopCartModelDto shopCartModelDto:resList){
                String materialCode = shopCartModelDto.getMaterialCode();
                Integer cnt=0;
                if(count.containsKey(materialCode)){
                    cnt=count.get(materialCode);
                }
                cnt++;
                count.put(materialCode,cnt);
                String add = cnt.toString();
                materialCode+=add;
                String[]list1;
                if(shopCartModelDto.getExtOrgIdList()!=null) {
                    list1 = shopCartModelDto.getExtOrgIdList().split(",");
                }
                else{
                    list1=new String[]{"null"};
                }
                String[]list2;
                if(shopCartModelDto.getExtAreaCode()!=null) {
                    list2 = shopCartModelDto.getExtAreaCode().split(",");
                }
                else{
                    list2=new String[]{"null"};
                }
                for(String s:list1){
                    String str=materialCode+"-"+s;
                    org.put(str,true);
                }
                for(String s:list2){
                    String str=materialCode+"-"+s;
                    area.put(str,true);
                }
                price.put(materialCode,shopCartModelDto.getExtReferencePrice());
            }
            for(ShopCartModelDto shopCartModelDto:shopCartModelDtos){
                String orgId=shopCartModelDto.getOrgId()!=null?shopCartModelDto.getOrgId().toString():"null";
                String areaCode =shopCartModelDto.getExtAreaCode()!=null?shopCartModelDto.getExtAreaCode():"null";
                String materialCode =shopCartModelDto.getMaterialCode();
                Boolean flag=false;
                if(!count.containsKey(materialCode)){
                    shopCartModelDto.setExtIsGoods("N");
                    continue;
                }
                for(Integer i=1;i<=count.get(materialCode);i++){
                    String temp=materialCode+i.toString();
                    String s1=temp+"-"+orgId;
                    String s2=temp+"-"+areaCode;
                    if(org.containsKey(s1)&&area.containsKey(s2)){
                        shopCartModelDto.setExtIsGoods("Y");
                        shopCartModelDto.setExtReferencePrice(price.get(temp));
                        flag=true;
                        break;
                    }
                }
                if(!flag){
                    shopCartModelDto.setExtIsGoods("N");
                }
            }*/
        }
    }

    private Map<String, MaterialItem> getInvalidMaterialMap(List<String> materialCodeList) {

        MaterialItemQueryDto materialItemQueryDto = new MaterialItemQueryDto();
        materialItemQueryDto.setPageNum(1);
        materialItemQueryDto.setPageSize(materialCodeList.size());
        materialItemQueryDto.setItemStatus("N");
        List<ConditionDTO> conditionDTOS = new ArrayList<>();
        ConditionDTO conditionDTO  = new ConditionDTO();
        conditionDTO.setField("objectCode");
        conditionDTO.setOperator("in");
        conditionDTO.setValue(materialCodeList);
        conditionDTOS.add(conditionDTO);

        materialItemQueryDto.setExtendConditions(conditionDTOS);

        List<MaterialItem> list = baseClient.listPageByCondition(materialItemQueryDto).getList();
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.toMap(m -> m.getMaterialCode(), m -> m, (v1, v2) -> v1));
    }

    private List<ShopCartModelDto> readData(MultipartFile file) {
        List<ShopCartModelDto> shopCartModelDtos;
        try {
            // 获取输入流
            InputStream inputStream = file.getInputStream();
            // 数据收集器
            AnalysisEventListenerImpl<ShopCartModelDto> listener = new AnalysisEventListenerImpl<>();
            ExcelReader excelReader = EasyExcel.read(inputStream, listener).build();
            // 第一个sheet读取类型
            ReadSheet readSheet = EasyExcel.readSheet(0).head(ShopCartModelDto.class).build();
            // 开始读取第一个sheet
            excelReader.read(readSheet);
            shopCartModelDtos = listener.getDatas();
        } catch (IOException e) {
            throw new BaseException("excel解析出错");
        }
        return shopCartModelDtos;
    }

}
