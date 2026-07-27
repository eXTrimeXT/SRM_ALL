package com.midea.cloud.srm.supcooperate.ext.requirement.souplan.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.midea.cloud.common.utils.EasyExcelUtil;
import com.midea.cloud.component.filter.HttpServletHolder;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.ql.QlQuery;
import com.midea.cloud.meiql.api.spec.ql.QlQueryFieldWrapper;
import com.midea.cloud.meiql.api.spec.ql.QlQueryWrapper;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.entity.PrRequirementHead;
import com.midea.cloud.srm.model.pm.pr.requirement.enums.RequirementApproveStatus;
import com.midea.cloud.srm.model.sou.expert.enums.ExtSouExpertScoreGroupTypeEnum;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.dto.ExtPrSouRequirementForDataSubmit;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementGroupTypeEnum;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.srm.constant.SouConstant;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.entity.PrRequirementHead;
import com.midea.cloud.srm.model.pm.pr.division.entity.DivisionCategory;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.pr.division.service.IDivisionCategoryService;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplan.service.PrSouRequirementQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;

/**
 * 备注
 * @author huangbf3
 */
@Service
public class PrSouRequirementQueryServiceImpl implements PrSouRequirementQueryService {

    @Autowired
    private QlService qlService;

    @Autowired
    private IDivisionCategoryService divisionCategoryService;
    @Autowired
    private RbacClient rbacClient;
    /**
     * 获取板块接口人名单excel
     */
    @Override
    public void queryOrgBuInterfacePersonListExcel() throws IOException {
        try (OutputStream outputStream = EasyExcelUtil.getServletOutputStream(HttpServletHolder.getResponse(), "板块接口人名单.xlsx")) {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template-file/orgbu_interface_person_list.xlsx");
            if (inputStream == null) { return; }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int len;
            byte[] bytes = new byte[1024];
            while ((len = inputStream.read(bytes)) != -1) {
                byteArrayOutputStream.write(bytes, 0, len);
            }
            inputStream.close();
            byteArrayOutputStream.close();
            byteArrayOutputStream.writeTo(outputStream);
        }
    }

    @Override
    public List<ExtPrSouRequirementForDataSubmit> findSouRequirementBySendProfileEndDateFromHour(int minHour,int maxHour) {

        QlQueryWrapper qlWrappers =  QlWrappers.query(MqlType.EXT_PR_SOU_REQUIREMENT_HEAD,"psrh")
                .select(
                        QlQueryFieldWrapper.field("psrh",ExtPrSouRequirementForDataSubmit::getProjectName),
                        QlQueryFieldWrapper.field("prh",ExtPrSouRequirementForDataSubmit::getRequirementHeadNum),
                        QlQueryFieldWrapper.field("psrg",ExtPrSouRequirementForDataSubmit::getUsername),
                        QlQueryFieldWrapper.field("psrh",ExtPrSouRequirementForDataSubmit::getSendSouProfileEndDate)
                )
                //级联采购需求主表
                .innerJoin(MqlType.PURCHASE_REQUIREMENT_HEAD,"prh",sw->{
                    sw.eq(
                            QlQueryFieldWrapper.field("prh", PrRequirementHead::getRequirementHeadId),
                            QlQueryFieldWrapper.field("psrh",ExtPrSouRequirementHead::getRequirementHeadId)
                    );
                })
                //级联招标计划工作成员表
                .innerJoin(MqlType.EXT_PR_SOU_REQUIREMENT_GROUP,"psrg",sw->{
                    sw.eq(
                            QlQueryFieldWrapper.field("psrg", ExtPrSouRequirementGroup::getRequirementHeadId),
                                    QlQueryFieldWrapper.field("psrh",ExtPrSouRequirementHead::getRequirementHeadId))
                            .eq(QlQueryFieldWrapper.field("psrg",ExtPrSouRequirementGroup::getGroupType), PrSouRequirementGroupTypeEnum.SOU);
                })
                //未提交
                .eq(ExtPrSouRequirementHead::getHasSendSouProfile, Enable.N)
                //未到截止时间
                .gt(ExtPrSouRequirementHead::getSendSouProfileEndDate, LocalDate.now())
                //已审批
                .eq(PrRequirementHead::getAuditStatus, RequirementApproveStatus.APPROVED);
        List<ExtPrSouRequirementForDataSubmit> extPrSouRequirementForDataSubmits = qlService.queryByWrapper(
                qlWrappers,
                ExtPrSouRequirementForDataSubmit.class);
        if(CollUtil.isNotEmpty(extPrSouRequirementForDataSubmits)){
            return extPrSouRequirementForDataSubmits.stream().filter(e->{
                long diff = e.getSendSouProfileEndDate().toEpochDay() - LocalDate.now().toEpochDay();
                long min =(minHour/24);
                long max = (maxHour/24);
                return (min<=diff)&&(diff<max);
            }).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public JSONObject getBidFuZeRen(Long requirementHeadId) {
        JSONObject fuzeren = new JSONObject();
        ExtPrSouRequirementHead extPrSouRequirementHead = qlService.readByKey("ExtPrSouRequirementHead",requirementHeadId, ExtPrSouRequirementHead.class);
        //  采购需求的bpm审批，需要增加两个字段：招标负责人、供应商负责人 分别传工号和姓名
        PrRequirementHead prHead = qlService.readByKey(PrRequirementHead.class.getSimpleName(), requirementHeadId, PrRequirementHead.class);
        // 1: 根据品类分工分配人员
        /*List<DivisionCategory> divisionList = divisionCategoryService.lambdaQuery()
                .eq(DivisionCategory::getOrgId, prHead.getOrgId())
                .eq(DivisionCategory::getCategoryId, prHead.getCategoryId())
                .list();*/
        QueryWrapper<DivisionCategory> dcQuery = new QueryWrapper<>();
        dcQuery.eq("ORG_ID", prHead.getOrgId());
        dcQuery.eq("CATEGORY_ID", prHead.getCategoryId());
        //金额   TOTAL_AMOUNT_BY_TEN_KILO
        if (SouConstant.SPECIAL_SOU.equals(extPrSouRequirementHead.getRequireFrom())) {
            dcQuery.eq("IF_MAIN_PERSON", "N");
        } else {
            int te = 10;
            if (extPrSouRequirementHead.getTotalAmountByTenKilo().compareTo(new BigDecimal(te)) <= 0) {
                //小于等于10万
                dcQuery.eq("IF_MAIN_PERSON", "N");
            } else {
                //大于10万
                dcQuery.eq("IF_MAIN_PERSON", "Y");
            }
        }
        List<DivisionCategory> divisionList = divisionCategoryService.list(dcQuery);
        if (!divisionList.isEmpty()) {
            // 主招标负责人
            DivisionCategory zzbfzr = divisionList.stream().filter(e -> "Person in charge of bidding".equals(e.getDuty())).collect(Collectors.toList())
                    .stream().findFirst().orElse(null);
            if (zzbfzr != null) {
                zzbfzr.getPersonInChargeUsername();
                User user =  rbacClient.findByUsername(zzbfzr.getPersonInChargeUsername());
                JSONObject zzbfzrJson = new JSONObject();
                zzbfzrJson.put("isBpmPeople",false);
                zzbfzrJson.put("userId",user.getCeeaEmpNo());
                zzbfzrJson.put("userName",user.getNickname());
                fuzeren.put("zzbfzr",zzbfzrJson);
            }
            // 主供应商负责人
            DivisionCategory zgysfzr = divisionList.stream().filter(e -> "Supplier Leader".equals(e.getDuty())).collect(Collectors.toList())
                    .stream().findFirst().orElse(null);
            if(zgysfzr != null){
                zgysfzr.getPersonInChargeUsername();
                User user =  rbacClient.findByUsername(zgysfzr.getPersonInChargeUsername());
                JSONObject zgysfzrJson = new JSONObject();
                zgysfzrJson.put("isBpmPeople",false);
                zgysfzrJson.put("userId",user.getCeeaEmpNo());
                zgysfzrJson.put("userName",user.getNickname());
                fuzeren.put("zgysfzr",zgysfzrJson);

            }
        }
        return fuzeren;
    }

}
