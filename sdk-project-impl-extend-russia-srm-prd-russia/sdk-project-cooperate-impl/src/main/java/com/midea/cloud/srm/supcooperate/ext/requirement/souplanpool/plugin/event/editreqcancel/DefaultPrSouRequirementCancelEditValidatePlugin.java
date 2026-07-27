package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.plugin.event.editreqcancel;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancel;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancelAttach;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancelLine;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.enums.PrSouRequirementCancelStatusEnum;
import com.midea.cloud.srm.ql.util.MqlCreateUpdateUtils;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.editreqcancel.IPrSouRequirementCancelEditValidatePlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.editreqcancel.PrSouRequirementCancelEditContext;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 招标计划 - 计划取消校验插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/09
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultPrSouRequirementCancelEditValidatePlugin implements IPrSouRequirementCancelEditValidatePlugin {

    @Autowired
    private BaseClient baseClient;
    @Autowired
    private QlService qlService;

    @Override
    @ApiModelProperty("校验及转化计划取消单")
    public PrSouRequirementCancelEditContext validateAndConvertCancel(PrSouRequirementCancelEditContext context) {
        // 1: 数据校验
        ExtPrSouRequirementCancel param = context.getParam(); {
            // 1.1: ID(略)
            // 1.2: 计划取消编号(略)
            // 1.3: 取消状态
            param.setCancelStatus(PrSouRequirementCancelStatusEnum.DRAFT.name());
            // 1.4: 申请部门
            param.setDepartmentId(StringUtils.trimToNull(param.getDepartmentId()));
            param.setDepartmentName(StringUtils.trimToNull(param.getDepartmentName()));
            // 1.5: 申请日期
            param.setApplyDate(LocalDate.now());
            // 1.6: 申请人
            AssertUtils.notNull(param.getApplyById(), "缺少applyById字段");
            param.setApplyBy(StringUtils.trimToNull(param.getApplyBy()));
            AssertUtils.notNull(param.getApplyBy(), "缺少applyBy字段");
            param.setApplyByNickname(StringUtils.trimToNull(param.getApplyByNickname()));
            AssertUtils.notNull(param.getApplyByNickname(), "缺少applyByNickname字段");
            // 1.7: 取消原因
            param.setCancelReason(StringUtils.trimToNull(param.getCancelReason()));
        }
        // 2: 数据转化
        ExtPrSouRequirementCancel entity; {
            if (context.getExistReqCancel() != null) {
                entity = context.getExistReqCancel();
            } else {
                entity = new ExtPrSouRequirementCancel();
            }
            //noinspection unchecked
            SouObjectXUtil.mergePropertiesIgnoreFieldsWithoutExts(param, entity,
                    ExtPrSouRequirementCancel::getRequirementCancelId,
                    ExtPrSouRequirementCancel::getRequirementCancelNo,
                    ExtPrSouRequirementCancel::getCreatedId,
                    ExtPrSouRequirementCancel::getCreatedBy,
                    ExtPrSouRequirementCancel::getCreatedByIp,
                    ExtPrSouRequirementCancel::getCreationDate,
                    ExtPrSouRequirementCancel::getCreatedUserName,
                    ExtPrSouRequirementCancel::getCreatedFullName);
            MqlCreateUpdateUtils.removeExtPropsInMqlRelations(entity, ExtPrSouRequirementCancel.class.getSimpleName());

            if (context.getExistReqCancel() == null) {
                entity.setRequirementCancelId(IdGenrator.generate());
                context.getParam().setGenerateCode(context.getParam().getGenerateCode());
                entity.setRequirementCancelNo(baseClient.seqGen(context.getParam().getGenerateCode() != null ? context.getParam().getGenerateCode() : "SEQ_PR_REQ_SOU_CANCEL"));
            }

            SouObjectXUtil.mergeProperties(entity, param);
        }

        context.setReqCancelEntity(entity);
        return context;
    }

    @Override
    @ApiModelProperty("校验及转化计划取消明细")
    public PrSouRequirementCancelEditContext validateAndConvertCancelLines(PrSouRequirementCancelEditContext context) {
        // 1: 数据校验
        List<ExtPrSouRequirementCancelLine> lineList = context.getParam().getCancelLineList(); {
            if (CollectionUtils.isEmpty(lineList)) {
                AssertUtils.isTrue(context.getParam().getTempSave(), "请选择需要取消的招标计划");
                return context;
            }
            int index = 0;
            for (ExtPrSouRequirementCancelLine line : lineList) {
                index++;

                ExtPrSouRequirementHead souReqHead = qlService.readByKey(ExtPrSouRequirementHead.class.getSimpleName(), line.getRequirementHeadId(), ExtPrSouRequirementHead.class);
                RequirementHead reqHead = qlService.readByKey("PurchaseRequirementHead", line.getRequirementHeadId(), RequirementHead.class);

                // 1.1: ID(略)
                // 1.2: 计划取消ID
                line.setRequirementCancelId(context.getReqCancelEntity().getRequirementCancelId());
                // 1.3: 招标计划ID
                AssertUtils.notNull(line.getRequirementHeadId(), "请选择招标计划");
                // 1.4: 所属板块
                line.setOrgBuId(souReqHead.getOrgBuId());
                line.setOrgBuCode(souReqHead.getOrgBuCode());
                line.setOrgBuName(souReqHead.getOrgBuName());
                // 1.4: 所属公司
                line.setOrgId(reqHead.getOrgId());
                line.setOrgCode(reqHead.getOrgCode());
                line.setOrgName(reqHead.getOrgName());
                // 1.5: 所属部门
                line.setCeeaDepartmentId(reqHead.getCeeaDepartmentId());
                line.setCeeaDepartmentName(reqHead.getCeeaDepartmentName());
                // 1.6: 所属品类
                line.setCategoryId(reqHead.getCategoryId());
                line.setCategoryCode(reqHead.getCategoryCode());
                line.setCategoryName(reqHead.getCategoryName());
                // 1.7: 排序
                line.setSortIndex(index);
            }
        }
        // 2: 数据转化
        List<ExtPrSouRequirementCancelLine> entityList = new ArrayList<>(lineList.size()); {
            for (ExtPrSouRequirementCancelLine line : lineList) {
                ExtPrSouRequirementCancelLine entity;
                ExtPrSouRequirementCancelLine existLine = context.getExistReqCancelLineMap().get(line.getRequirementCancelLineId());
                if (existLine != null) {
                    entity = existLine;
                } else {
                    entity = new ExtPrSouRequirementCancelLine();
                }
                entityList.add(entity);
                //noinspection unchecked
                SouObjectXUtil.mergePropertiesIgnoreFieldsWithoutExts(line, entity,
                        ExtPrSouRequirementCancelLine::getRequirementCancelLineId,
                        ExtPrSouRequirementCancelLine::getCreatedId,
                        ExtPrSouRequirementCancelLine::getCreatedBy,
                        ExtPrSouRequirementCancelLine::getCreatedByIp,
                        ExtPrSouRequirementCancelLine::getCreationDate,
                        ExtPrSouRequirementCancelLine::getCreatedUserName,
                        ExtPrSouRequirementCancelLine::getCreatedFullName);
                MqlCreateUpdateUtils.removeExtPropsInMqlRelations(entity, ExtPrSouRequirementCancelLine.class.getSimpleName());
                if (existLine == null) {
                    entity.setRequirementCancelLineId(IdGenrator.generate());
                }
                SouObjectXUtil.mergeProperties(entity, line);
            }
        }

        context.setReqCancelLineEntityList(entityList);
        return context;
    }

    @Override
    @ApiModelProperty("校验及转化计划取消附件")
    public PrSouRequirementCancelEditContext validateAndConvertCancelAttaches(PrSouRequirementCancelEditContext context) {
        // 1: 数据校验
        List<ExtPrSouRequirementCancelAttach> attachList = context.getParam().getCancelAttachList(); {
            if (CollectionUtils.isEmpty(attachList)) {
                return context;
            }
            int index = 0;
            for (ExtPrSouRequirementCancelAttach attach : attachList) {
                index++;
                // 1.1: ID(略)
                // 1.2: 计划取消ID
                attach.setRequirementCancelId(context.getReqCancelEntity().getRequirementCancelId());
                // 1.3: 文件ID
                AssertUtils.notNull(attach.getFileId(), "附件列表第{0}行请上传附件", index);
                attach.setFileName(StringUtils.trimToNull(attach.getFileName()));
                AssertUtils.notNull(attach.getFileName(), "附件列表第{0}行请上传附件", index);
                AssertUtils.isTrue(attach.getFileName().length() <= 150, "附件列表第{0}行附件名称长度不能超过150", index);
                // 1.4: 上传时间
                attach.setUploadTime(new Date());
                // 1.5: 排序
                attach.setSortIndex(index);
            }
        }
        // 2: 数据转化
        List<ExtPrSouRequirementCancelAttach> entityList = new ArrayList<>(attachList.size()); {
            for (ExtPrSouRequirementCancelAttach attach : attachList) {
                ExtPrSouRequirementCancelAttach entity;
                ExtPrSouRequirementCancelAttach existAttach = context.getExistReqCancelAttachMap().get(attach.getRequirementCancelAttachId());
                if (existAttach != null) {
                    entity = existAttach;
                } else {
                    entity = new ExtPrSouRequirementCancelAttach();
                }
                entityList.add(entity);
                //noinspection unchecked
                SouObjectXUtil.mergePropertiesIgnoreFieldsWithoutExts(attach, entity,
                        ExtPrSouRequirementCancelAttach::getRequirementCancelAttachId,
                        ExtPrSouRequirementCancelAttach::getCreatedId,
                        ExtPrSouRequirementCancelAttach::getCreatedBy,
                        ExtPrSouRequirementCancelAttach::getCreatedByIp,
                        ExtPrSouRequirementCancelAttach::getCreationDate,
                        ExtPrSouRequirementCancelAttach::getCreatedUserName,
                        ExtPrSouRequirementCancelAttach::getCreatedFullName);
                MqlCreateUpdateUtils.removeExtPropsInMqlRelations(entity, ExtPrSouRequirementCancelAttach.class.getSimpleName());
                if (existAttach == null) {
                    entity.setRequirementCancelAttachId(IdGenrator.generate());
                }
                SouObjectXUtil.mergeProperties(entity, attach);
            }
        }

        context.setReqCancelAttachEntityList(entityList);
        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
