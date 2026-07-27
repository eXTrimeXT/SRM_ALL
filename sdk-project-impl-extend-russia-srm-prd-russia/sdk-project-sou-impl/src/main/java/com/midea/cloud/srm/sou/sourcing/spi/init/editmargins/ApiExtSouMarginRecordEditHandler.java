package com.midea.cloud.srm.sou.sourcing.spi.init.editmargins;

import com.alibaba.fastjson.JSON;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.enums.SouMarginRecordTypeEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouMarginRecordDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouMarginDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouMarginRecordDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.ccapipayments.service.CcApiPaymentWithBusinessService;
import com.midea.cloud.srm.sou.sourcing.init.service.ExtSouInitQueryService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouMarginRecordService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouMarginService;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class ApiExtSouMarginRecordEditHandler implements ISouSpiBean {

    @Autowired
    private IExtSouMarginRecordService souMarginRecordService;

    @Autowired
    private CcApiPaymentWithBusinessService ccApiPaymentWithBusinessService;

    @Autowired
    private IExtSouMarginService souMarginService;

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public ExtSouMarginRecordPo formatValidateAndConvert(ApiExtSouMarginRecordDto param, String souType) {
        // 1: 数据格式化及校验
        this.formatAndValidate(param, souType);
        // 2: 数据转换
        return this.convert(param, souType);
    }

    /**
     *  入参格式化及校验
     * @param param 参数
     * @param souType 参数
     */
    protected void formatAndValidate(ApiExtSouMarginRecordDto param, String souType) {

        AssertUtils.isFalse(Objects.isNull(SouMarginRecordTypeEnum.valueOf(param.getType())), "提交类型参数有误！");

        if(CollectionUtils.isEmpty(param.getMarginRecordList()) && Objects.isNull(param.getMarginRecord())) {
            AssertUtils.isTrue(false, "提交明细数据不允许为空！");
        }
    }

    /**
     * 数据转换
     * @param param 参数
     * @param souType 参数
     * @return 返回
     */
    protected ExtSouMarginRecordPo convert(ApiExtSouMarginRecordDto param, String souType) {
        ExtSouMarginRecordPo po = new ExtSouMarginRecordPo();
        po.setMarginRecordList(this.doConvertProject(param, po, souType));
        return po;
    }

    /**
     * 转换得到寻源信息
     * @param param 参数
     * @param souType 参数
     * @return 返回
     */
    protected List<ExtSouMarginRecord> doConvertProject(ApiExtSouMarginRecordDto param, ExtSouMarginRecordPo po, String souType) {
        List<ExtSouMarginRecordDto> extSouMarginRecordList = param.getMarginRecordList();
        if(Objects.isNull(extSouMarginRecordList)) {
            extSouMarginRecordList = new ArrayList<>();
            param.setMarginRecordList(extSouMarginRecordList);
        }
        //支持单个传值
        if(!Objects.isNull(param.getMarginRecord())) {
            extSouMarginRecordList.add(param.getMarginRecord());
        }

        List<Long> marginIdList = extSouMarginRecordList.stream().map(r -> r.getMarginId()).distinct().collect(Collectors.toList());
        List<ExtSouMargin> marginList = souMarginService.listByIds(marginIdList);
        Map<Long, ExtSouMargin> marginMap = marginList.stream().collect(Collectors.toMap(k -> k.getMarginId(), Function.identity(), (k1, k2) -> k2));
        Map<Long, ExtSouMargin> yearMarginMap = souMarginService.queryYearMarginInfo(marginList);
        po.setMarginMap(marginMap);
        po.setYearMarginMap(yearMarginMap);

        extSouMarginRecordList.stream().forEach(record -> {
            record.setType(param.getType());
            /** 记录原单项目ID，年度保证金扣款时使用 */
            record.setOldProjectId(record.getProjectId());
            ExtSouMargin souMargin = marginMap.get(record.getMarginId());
            if(!Objects.isNull(souMargin) && YesOrNo.YES.getValue().equals(souMargin.getYearFlag())) {
                record.setProjectId(SrmConstant.LONG_MINUS_ONE);
                if(SrmConstant.LONG_MINUS_ONE.compareTo(souMargin.getProjectId()) != 0) {
                    record.setMarginId(souMargin.getRelYearMarginId());
                }
            } else {
                if(Objects.isNull(record.getProjectId())) {
                    record.setProjectId(param.getProjectId());
                }
            }
            if(Objects.isNull(record.getRecordId())) {
                record.setRecordId(IdGenrator.generate());
            }
            if(SouMarginRecordTypeEnum.REFUND.getCode().equals(param.getType())) {
                record.setAmount(record.getRefundAmount());
            }
        });

        List<ExtSouMarginRecord> recordList = JSON.parseArray(JSON.toJSONString(extSouMarginRecordList), ExtSouMarginRecord.class);
        return recordList;
    }

    @ApiOperation("项目信息保存前的额外处理")
    public void doHandlerBeforeEditProject(ApiExtSouMarginRecordDto param, String souType) {
    }

    private Long getMarignBestProjectId(ExtSouMarginRecordPo po, Long marginId) {
        if(ObjectUtils.allNotNull(po, po.getMarginMap())) {
            ExtSouMargin margin = po.getMarginMap().get(marginId);
            if(ObjectUtils.allNotNull(margin)) {
                if(SrmConstant.LONG_MINUS_ONE.compareTo(margin.getProjectId()) != 0) {
                    return margin.getProjectId();
                }
                if(SrmConstant.LONG_MINUS_ONE.compareTo(margin.getRelYearMarginId()) != 0) {
                    Long projectId = getYearMarginBestProjectId(po, margin.getRelYearMarginId());
                    if(ObjectUtils.allNotNull(projectId)) {
                        return projectId;
                    }
                    return getYearMarginBestProjectId(po, marginId);
                } else {
                    return getYearMarginBestProjectId(po, marginId);
                }
            } else {
                return getYearMarginBestProjectId(po, marginId);
            }
        }
        return null;
    }

    private Long getYearMarginBestProjectId(ExtSouMarginRecordPo po, Long marginId) {
        if (ObjectUtils.allNotNull(po, po.getYearMarginMap())) {
            ExtSouMargin margin = po.getYearMarginMap().get(marginId);
            if(ObjectUtils.allNotNull(margin)) {
                return margin.getSourceProjectId();
            }
        }
        return null;
    }

    @ApiOperation("项目信息保存后的额外处理")
    public void doHandlerAfterEditProject(ApiExtSouMarginRecordDto param, String souType, ExtSouMarginRecordPo po) {

        if(CollectionUtils.isEmpty(param.getMarginRecordList())) {
            return;
        }
        /** 还原原招标单ID */
        po.getMarginRecordList().stream().forEach(r -> {
            r.setProjectId(getMarignBestProjectId(po, r.getMarginId()));
        });

        ccApiPaymentWithBusinessService.callApiPaymentWithMargin(param.getType(), po.getMarginRecordList());
        //保证金扣款后，重新汇总扣款金额和可退金额
        //扣款金额=多次扣款金额（非失败）的汇总
        //可退金额=缴纳金额-扣款金额
        if(SouMarginRecordTypeEnum.CHARGE.getCode().equals(param.getType()) && CollectionUtils.isNotEmpty(po.getMarginRecordList())) {
            List<Long> marginIdList = po.getMarginRecordList().stream().map(m -> m.getMarginId()).collect(Collectors.toList());
            List<ExtSouMarginRecord> list = souMarginRecordService.lambdaQuery().in(ExtSouMarginRecord::getMarginId, marginIdList).list();
            Map<Long, BigDecimal> map = list.stream().collect(Collectors.groupingBy(ExtSouMarginRecord::getMarginId,
                    Collectors.mapping(ExtSouMarginRecord::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

            List<ExtSouMargin> souMarginList = souMarginService.listByIds(marginIdList);
            for(ExtSouMargin souMargin : souMarginList) {
                souMargin.setChargeAmount(map.get(souMargin.getMarginId()));
                souMargin.setRefundAmount(safeSubtract(souMargin.getPayAmount(),souMargin.getChargeAmount()));
            }
            souMarginService.updateBatchById(souMarginList);
        }

    }

    public static BigDecimal safeSubtract(BigDecimal minuend, BigDecimal subtrahend) {
        if (minuend == null) {
            minuend = BigDecimal.ZERO; // 如果minuend为null，则用0代替
        }
        if (subtrahend == null) {
            subtrahend = BigDecimal.ZERO; // 如果subtrahend为null，则用0代替
        }
        if(subtrahend.compareTo(minuend) >= 1) {
            return BigDecimal.ZERO;
        }
        return minuend.subtract(subtrahend);
    }


}
