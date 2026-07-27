package com.midea.cloud.srm.sup.report;

import cn.hutool.core.collection.CollUtil;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.ql.QlQueryWrapper;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.SouExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.cm.contract.constants.ContractMqlSchemaType;
import com.midea.cloud.srm.model.cm.perform.entity.PerPlan;
import com.midea.cloud.srm.model.contract.enums.ContractHeadPlanStatusEnums;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouOrderDto;
import com.midea.cloud.srm.sup.dto.SupReportFormsDto;
import com.midea.cloud.srm.sup.dto.SupReportFormsInfoDto;
import com.midea.cloud.srm.sup.mapper.SupReportFormsMapper;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.midea.cloud.srm.model.contract.constant.ContractHeadFieldName.CONTRACT_HEAD_ID_FIELD;

/**
 * @author ex_liuxy46
 */
@Slf4j
@RestController
@RequestMapping("/supplier/report/forms")
public class SupReportFormsController {

    @Resource
    private SouExtClient souExtClient;
    @Resource
    private SupReportFormsMapper supReportFormsMapper;
    @Resource
    private QlService qlService;

    @Resource
    private BaseClient baseClient;

    @ApiOperation(value = "获取供应商报表列表")
    @PostMapping("/getSupRepFormsList")
    public PageInfo<SupReportFormsDto> getSupRepFormsList(@RequestBody SupReportFormsDto supReportForms) {
        List<DictItem> authNumDict = baseClient.listDictItemByDictCode("CERTIFICATE_TYPE");
        if (StringUtils.isNotBlank(supReportForms.getZzLevel())) {
            List<String> levelList = authNumDict.stream().filter(e -> e.getDictItemName().contains(supReportForms.getZzLevel())).map(DictItem::getDictItemCode).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(levelList)) {
                supReportForms.setZzLevel(String.join("|", levelList));
            }
        }
        PageUtil.startPage(supReportForms.getPageNum(), supReportForms.getPageSize());
        List<SupReportFormsDto> list = supReportFormsMapper.getSupRfList(supReportForms);
        log.info("数据的数量===" + list.size());
        list.forEach(e -> {
            if (StringUtils.isNotBlank(e.getZzLevel())) {
                String[] authNums = e.getZzLevel().split(",");
                List<String> zzList = new ArrayList<>();
                for (String authNum : authNums) {
                    for (DictItem dictItem : authNumDict) {
                        if (authNum.equals(dictItem.getDictItemCode())) {
                            zzList.add(dictItem.getDictItemName());
                        }
                    }
                }
                if (CollectionUtils.isNotEmpty(zzList)) {
                    e.setZzLevel(String.join(",", zzList));
                }
            }
        });
        return new PageInfo<>(list);
    }

    @ApiOperation(value = "获取供应商报表列表详情")
    @PostMapping("/getSupRepFormsInfo")
    public List<SupReportFormsInfoDto> getSupRepFormsInfo(@RequestBody SupReportFormsInfoDto fin) {
        if (fin.getSupId() == null) {
            throw new BaseException("缺少供应商id");
        }
        List<SupReportFormsInfoDto> list = supReportFormsMapper.supReportFormsInfoList(fin);
        List<SupReportFormsInfoDto> pshList = supReportFormsMapper.projectScoreHeaderList(fin.getSupId());
        for (SupReportFormsInfoDto e : list) {
            for (SupReportFormsInfoDto supReportFormsInfoDto : pshList) {
                if (e.getZbCode().equals(supReportFormsInfoDto.getZbCode())) {
                    e.setHtGetScore(supReportFormsInfoDto.getHtGetScore());
                    e.setHtResult(supReportFormsInfoDto.getHtResult());
                    e.setHtDealResult(supReportFormsInfoDto.getHtDealResult());
                    e.setHtStatus(supReportFormsInfoDto.getHtStatus());
                }
            }
            try {
                List<ApiExtSouOrderDto> souOrderList = souExtClient.getExtSouOrderInfo(e.getZbCode());
                for (ApiExtSouOrderDto apiExtSouOrderDto : souOrderList) {
                    if (e.getSupId().equals(apiExtSouOrderDto.getVendorId())) {
                        e.setBBidReason(apiExtSouOrderDto.getExtNotjoinReason());
                    }
                }
            } catch (Exception ex) {

            }
            /* 取项目化绩效得分的履约结果状态
            try {
                List<String> chList = supReportFormsMapper.getContractHeadList(e.getSupId(), e.getZbCode());
                if (CollectionUtils.isNotEmpty(chList)) {
                    String contractHeadIdField = CONTRACT_HEAD_ID_FIELD;
                    QlQueryWrapper perPlanWrapper = QlWrappers.query(ContractMqlSchemaType.ContractHead.getType()).select("status", contractHeadIdField).in(contractHeadIdField, chList.get(0));
                    List<PerPlan> perPlans = qlService.queryByWrapper(perPlanWrapper, PerPlan.class);
                    if(CollUtil.isNotEmpty(perPlans)) {
                        String headPlanStatus = getHeadPlanStatus(perPlans.get(0).getStatus());
                        e.setHtStatus(getHeadPlanTypeDesc(headPlanStatus));
                    }
                }
            } catch (Exception ex) {

            }*/
        }
        return list;
    }

    private String getHeadPlanStatus(String planStatus){
        //除了履约中和履约完成其他状态均为未开始
        if(!ContractHeadPlanStatusEnums.IN_PERFORMANCE.getCode().equals(planStatus)
                &&!ContractHeadPlanStatusEnums.COMPLETE_PERFORMANCE.getCode().equals(planStatus)){
            return ContractHeadPlanStatusEnums.NEVER_START.getCode();
        }
        return planStatus;
    }

    private String getHeadPlanTypeDesc(String planType){
        String desc = "";
        List<ContractHeadPlanStatusEnums>  contractHeadPlanStatusEnums = Arrays.stream(ContractHeadPlanStatusEnums.values()).filter(e->e.getCode().equals(planType)).collect(Collectors.toList());
        if(CollUtil.isNotEmpty(contractHeadPlanStatusEnums)){
            desc = contractHeadPlanStatusEnums.get(0).getDesc();
        }
        return desc;
    }
}
