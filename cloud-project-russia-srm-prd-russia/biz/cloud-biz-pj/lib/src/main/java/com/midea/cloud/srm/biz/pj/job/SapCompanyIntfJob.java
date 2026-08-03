package com.midea.cloud.srm.biz.pj.job;

import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.common.result.ResultCode;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.ql.QlQueryWrapper;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.quartz.bind.Job;
import com.midea.cloud.quartz.handler.ExecuteableJob;
import com.midea.cloud.srm.biz.pj.common.OpenClientConstant;
import com.midea.cloud.srm.biz.pj.mdmcompanyintf.service.ISccPjMdmCompanyIntfService;
import com.midea.cloud.srm.model.cm.element.entity.ElemMaintain;
import com.midea.cloud.srm.model.common.enums.ProcessStatusEnum;
import com.midea.cloud.srm.model.pj.mdm.dto.MdmResponseDto;
import com.midea.cloud.srm.model.pj.mdm.dto.MdmResultDto;
import com.midea.cloud.srm.model.pj.sapcreatesupview.dto.SapCreateSupViewDto;
import com.midea.cloud.srm.model.pj.sapcreatesupview.dto.SapCreateSupViewListDto;
import com.midea.cloud.srm.model.pj.sapcreatesupview.dto.SapResponseDto;
import com.midea.cloud.srm.model.pj.sapcreatesupview.dto.SapResultDto;
import com.midea.cloud.srm.model.pj.sapcreatesupview.entity.SapCompanyIntf;
import com.midea.cloud.srm.model.pj.sapcreatesupview.enums.SapCreateSupViewEnum;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenQueryWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenUpdateWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author fubiao
 * 供应商同步SAP
 */
@Job("SapCompanyIntfJob")
@Slf4j
public class SapCompanyIntfJob implements ExecuteableJob {

    @Autowired
    private ISccPjMdmCompanyIntfService iSccPjMdmCompanyIntfService;
    @Autowired
    private QlService qlService;
    public static final String MDM_AKONT = "22020000";
    public static final String MDM_ZUAWA = "012";
    public static final String SAP_FLAG = "S";
    /**
     * @param params
     * @return
     */
    @Override
    public BaseResult executeJob(Map<String, String> params) {
        try {
            Date date = new Date();
            //查询当前时间大于等于15分钟的单据
            Calendar calendar=Calendar.getInstance();
            calendar.add(Calendar.MINUTE,-15);
            //查询条件
            //创建时间小于当前时间-15分钟
            //流程状态不等于COMPLETED
            //重试次数小于等于3
            List<SapCompanyIntf> sapCompanyIntfList = qlService.queryByWrapper(QlWrappers.query(SapCompanyIntf.class)
                    .lt(SapCompanyIntf::getCreationDate,calendar.getTime() )
                    .notEq(SapCompanyIntf::getProcessStatus,ProcessStatusEnum.COMPLETED.getCode())
                    .le(SapCompanyIntf::getAppNum,3)
                    .orderByDesc(SapCompanyIntf::getLastUpdateDate), SapCompanyIntf.class);
            //组装数据
            List<SapCreateSupViewDto> sapCreateSupViewList=new ArrayList();
            SapCreateSupViewEnum[] sapCreateSupViewEnums = SapCreateSupViewEnum.values();
            for(SapCompanyIntf sapCompanyIntf:sapCompanyIntfList) {
                for (SapCreateSupViewEnum sapCreateSupViewEnum : sapCreateSupViewEnums) {
                    SapCreateSupViewDto sapCreateSupViewDto = new SapCreateSupViewDto();
                    sapCreateSupViewDto.setAKONT(MDM_AKONT);
                    sapCreateSupViewDto.setBUKRS(sapCreateSupViewEnum.getName());
                    sapCreateSupViewDto.setLIFNR(sapCompanyIntf.getOrgCode());
                    sapCreateSupViewDto.setZUAWA(MDM_ZUAWA);
                    sapCreateSupViewList.add(sapCreateSupViewDto);
                }
            }
            if(sapCreateSupViewList.size()==0){
                return null;
            }
            SapCreateSupViewListDto sapCreateSupViewListDto=new SapCreateSupViewListDto();
            sapCreateSupViewListDto.setLIST(sapCreateSupViewList);
            SapResponseDto<List<SapResultDto>> sapResponseDto = null;
            sapResponseDto=iSccPjMdmCompanyIntfService.createSupplierToSap(sapCreateSupViewListDto);

            //如果请求接口异常，通过mql修改临时表状态
            //否则更新成功
            List<String>updateList=new ArrayList();
            if (OpenClientConstant.CODE_SUCCESS_INT.compareTo(sapResponseDto.getCode()) != 0) {
                for(SapResultDto sapResultDto:sapResponseDto.getResult()) {
                    if(updateList.size()>0){
                        if(updateList.contains(sapResultDto.getLIFNR())){
                            continue;
                        }
                    }
                    //查询供应商传输次数
                    //查询条件
                    //创建时间小于当前时间-15分钟
                    //流程状态不等于COMPLETED
                    //重试次数小于等于3
                    //报错供应商
                    List<SapCompanyIntf> sapCompanyIntf2List = qlService.queryByWrapper(QlWrappers.query(SapCompanyIntf.class)
                            .lt(SapCompanyIntf::getCreationDate,calendar.getTime() )
                            .notEq(SapCompanyIntf::getProcessStatus,ProcessStatusEnum.COMPLETED.getCode())
                            .le(SapCompanyIntf::getAppNum,3)
                            .eq(SapCompanyIntf::getOrgCode,sapResultDto.getLIFNR())
                            .orderByDesc(SapCompanyIntf::getCreationDate), SapCompanyIntf.class);
                    qlService.updateByWrapper(QlWrappers.update("SapCompanyIntf")
                            .set("processStatus", ProcessStatusEnum.ERROR.getCode())
                            .set("processMessage", sapResponseDto.getMessage())
                            .set("processDate", date)
                            .set("appNum",sapCompanyIntf2List.get(0).getAppNum()+1)
                            .notEq(SapCompanyIntf::getSupState, ProcessStatusEnum.COMPLETED.getCode())
                            .eq(SapCompanyIntf::getOrgCode, sapResultDto.getLIFNR())
                            .eq(SapCompanyIntf::getSapCompanyIntfId, sapCompanyIntf2List.get(0).getSapCompanyIntfId()));
                    updateList.add(sapResultDto.getLIFNR());
                }
            }else{
                for(SapResultDto sapResultDto:sapResponseDto.getResult()) {
                    if(updateList.size()>0){
                        if(updateList.contains(sapResultDto.getLIFNR())){
                            continue;
                        }
                    }
                    //查询条件
                    //创建时间小于当前时间-15分钟
                    //流程状态不等于COMPLETED
                    //重试次数小于等于3
                    //供应商
                    List<SapCompanyIntf> sapCompanyIntf2List = qlService.queryByWrapper(QlWrappers.query(SapCompanyIntf.class)
                            .lt(SapCompanyIntf::getCreationDate, calendar.getTime())
                            .notEq(SapCompanyIntf::getProcessStatus, ProcessStatusEnum.COMPLETED.getCode())
                            .le(SapCompanyIntf::getAppNum, 3)
                            .eq(SapCompanyIntf::getOrgCode, sapResultDto.getLIFNR())
                            .orderByDesc(SapCompanyIntf::getCreationDate), SapCompanyIntf.class);
                    if(SAP_FLAG.equals(sapResultDto.getFLAG())) {
                        qlService.updateByWrapper(QlWrappers.update("SapCompanyIntf")
                                .set("processStatus", ProcessStatusEnum.COMPLETED.getCode())
                                .set("processMessage", sapResultDto.getMSG())
                                .set("processDate", date)
                                .notEq(SapCompanyIntf::getSupState, ProcessStatusEnum.COMPLETED.getCode())
                                .eq(SapCompanyIntf::getOrgCode, sapResultDto.getLIFNR())
                                .eq(SapCompanyIntf::getSapCompanyIntfId, sapCompanyIntf2List.get(0).getSapCompanyIntfId()));
                    }else{
                        qlService.updateByWrapper(QlWrappers.update("SapCompanyIntf")
                                .set("processStatus", ProcessStatusEnum.ERROR.getCode())
                                .set("processMessage", sapResultDto.getMSG())
                                .set("processDate", date)
                                .set("appNum",sapCompanyIntf2List.get(0).getAppNum()+1)
                                .notEq(SapCompanyIntf::getSupState, ProcessStatusEnum.COMPLETED.getCode())
                                .eq(SapCompanyIntf::getOrgCode, sapResultDto.getLIFNR())
                                .eq(SapCompanyIntf::getSapCompanyIntfId, sapCompanyIntf2List.get(0).getSapCompanyIntfId()));
                    }
                    updateList.add(sapResultDto.getLIFNR());
                }
            }
        } catch (Exception e) {
            log.error("用户同步SAP定时任务-执行失败", e);
            return BaseResult.build(ResultCode.UNKNOWN_ERROR,"用户同步SAP定时任务-执行失败！");
        }
        return BaseResult.buildSuccess("用户同步SAP定时任务-执行成功！");
    }
}
