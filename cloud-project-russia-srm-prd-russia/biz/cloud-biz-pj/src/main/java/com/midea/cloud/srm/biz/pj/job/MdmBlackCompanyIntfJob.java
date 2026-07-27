package com.midea.cloud.srm.biz.pj.job;

import com.midea.cloud.common.enums.ApproveStatusType;
import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.common.result.ResultCode;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.quartz.bind.Job;
import com.midea.cloud.quartz.handler.ExecuteableJob;
import com.midea.cloud.srm.biz.pj.common.OpenClientConstant;
import com.midea.cloud.srm.biz.pj.supplier.service.BlackSupplierQueryService;
import com.midea.cloud.srm.biz.pj.utils.MqlType;
import com.midea.cloud.srm.model.base.black.entity.Black;
import com.midea.cloud.srm.model.base.monitor.enums.YesOrNo;
import com.midea.cloud.srm.model.common.enums.ProcessStatusEnum;
import com.midea.cloud.srm.model.pj.sup.company.entity.AuthenticationScreen;
import com.midea.cloud.srm.model.pj.supplier.dto.*;
import com.midea.cloud.srm.model.pj.supplier.entity.MdmBlackCompanyIntf;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenUpdateWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author fubiao
 * 供应商同步SAP
 */
@Job("MdmBlackCompanyIntfJob")
@Slf4j
public class MdmBlackCompanyIntfJob implements ExecuteableJob {

    @Autowired
    private BlackSupplierQueryService blackSupplierQueryService;
    @Autowired
    private QlService qlService;
    @Autowired
    QlOpenClient qlOpenClient;
    public static final String MDM_SYSADMIN = "sysAdmin";
    public static final String MDM_PAGE = "1";
    public static final String MDM_SIZE = "100";
    /**
     * @param params 参数
     * @return 结果
     */
    @Override
    public BaseResult executeJob(Map<String, String> params) {
        try {
            Date date = new Date();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm_ss");
            //组装数据实体
            BlackCompanyDto blackCompanyDto=new BlackCompanyDto();
            //返回页码总数
            String total="";
            //返回页码总数
            int number;
            //调用MDM结果0失败1成功
            int flag;
            //结果集合
            //1查询scc_pj_sup_blackcompany_intf表，如果为空则查询全量数据，否则只查询当天数据
            List<MdmBlackCompanyIntf> mdmBlackCompanyIntfList = qlService.queryByWrapper(QlWrappers.query(MdmBlackCompanyIntf.class).orderByDesc(MdmBlackCompanyIntf::getProcessDate), MdmBlackCompanyIntf.class);
            //2组装数据
            blackCompanyDto.setSize(MDM_SIZE);
            blackCompanyDto.setPage(MDM_PAGE);
            if(mdmBlackCompanyIntfList!=null && mdmBlackCompanyIntfList.size()>0){
                blackCompanyDto.setStartUpdateTime(sdf.format(mdmBlackCompanyIntfList.get(0).getProcessDate()));
                blackCompanyDto.setEndUpdateTime(sdf.format(date));
            }
            //3调用MDM接口
            BlackCompanyResponseDto<BlackCompanyResultDto> blackCompanyResponseDto;
            blackCompanyResponseDto=blackSupplierQueryService.blackCompanyQuery(blackCompanyDto);
            if (OpenClientConstant.CODE_SUCCESS_INT.compareTo(blackCompanyResponseDto.getCode()) == 0) {
                if(blackCompanyResponseDto.getResult().getRows()==null || blackCompanyResponseDto.getResult().getRows().size()==0){
                    return BaseResult.buildSuccess("用户同步供应商黑名单定时任务-执行成功！");
                }
                //4获取失信名单总数，计算需要调用多少次
                BlackCompanyResultDto blackCompanyResultDto=blackCompanyResponseDto.getResult();
                total = blackCompanyResultDto.getTotal();
                List<BlackCompanyListDto> resultList = new ArrayList<>(blackCompanyResultDto.getRows());
                number = (int) Math.ceil((Double.parseDouble(total) / Double.parseDouble(MDM_SIZE)));
                //5循环调用接口，获取所有数据
                if (number > 1) {
                    for (int i = 1; i < number; i++) {
                        new BlackCompanyResponseDto<>();
                        BlackCompanyResponseDto<BlackCompanyResultDto> blackCompanyBypage;
                        blackCompanyDto.setPage(String.valueOf(i + 1));
                        blackCompanyBypage = blackSupplierQueryService.blackCompanyQuery(blackCompanyDto);
                        resultList.addAll(blackCompanyBypage.getResult().getRows());
                    }
                }
                //6对比现有供应商库供应商，如果不存在则根据重点关注或者黑名单，进行拉黑
                compareBlackCompany(resultList);
                flag=1;
            }else{
                flag=0;
            }
            //7添加执行记录表
            MdmBlackCompanyIntf mdmBlackCompanyIntf=new MdmBlackCompanyIntf();
            mdmBlackCompanyIntf.setProcessDate(date);
            mdmBlackCompanyIntf.setCreatedBy(MDM_SYSADMIN);
            if(flag==0){
                mdmBlackCompanyIntf.setProcessStatus(ProcessStatusEnum.ERROR.getCode());
                mdmBlackCompanyIntf.setProcessMessage(blackCompanyResponseDto.getMessage());
            }else{
                mdmBlackCompanyIntf.setProcessStatus(ProcessStatusEnum.COMPLETED.getCode());
            }
            mdmBlackCompanyIntf.setSynNum(Long.parseLong(total));
            createSynInfolist(mdmBlackCompanyIntf);
        } catch (Exception e) {
            log.error("用户同步MDM供应商黑名单定时任务-执行失败", e);
            return BaseResult.build(ResultCode.UNKNOWN_ERROR,"用户同步MDM供应商黑名单定时任务-执行失败！");
        }
        return BaseResult.buildSuccess("用户同步供应商黑名单定时任务-执行成功！");
    }
    public void compareBlackCompany(List<BlackCompanyListDto> list) {
        Date date = new Date();
        //查询供应商主数据，如果重点关注，则更新供应商库主数据
        //如果是黑名单，则修改供应商库主数据状态，并增加供应商黑名单管理表
        //黑名单列表集合
        List<BlackCompanyListDto>blackList= new ArrayList<>();
        //重点关注列表集合
        List<BlackCompanyListDto>focusList= new ArrayList<>();
        for (BlackCompanyListDto blackCompanyListDto : list) {
            if("重点关注".equals(blackCompanyListDto.getCompanyType())){
                focusList.add(blackCompanyListDto);
            }
            if("禁止合作".equals(blackCompanyListDto.getCompanyType())){
                List<CompanyInfo> authenticationScreens = qlOpenClient.query(ContextPath.SUP,
                        QlOpenWrappers.query("CompanyInfo").eq(CompanyInfo::getCompanyName, blackCompanyListDto.getCompanyName()), CompanyInfo.class);
                if(authenticationScreens!=null && authenticationScreens.size()!=0){
                    if("N".equals(authenticationScreens.get(0).getIsBacklist()) || authenticationScreens.get(0).getIsBacklist()==null){
                        blackCompanyListDto.setCompanyCode(authenticationScreens.get(0).getCompanyCode());
                        blackCompanyListDto.setCompanyType(authenticationScreens.get(0).getCompanyTypeName());
                        blackCompanyListDto.setCompanyCity(authenticationScreens.get(0).getCompanyCity());
                        blackCompanyListDto.setRegisteredCapital(authenticationScreens.get(0).getRegisteredCapital());
                        blackCompanyListDto.setLegalPerson(authenticationScreens.get(0).getLegalPerson());
                        blackCompanyListDto.setCompanyCountry(authenticationScreens.get(0).getCompanyCountry());
                        blackCompanyListDto.setCompanyProvince(authenticationScreens.get(0).getCompanyProvince());
                        blackCompanyListDto.setCompanyCity(authenticationScreens.get(0).getCompanyCity());
                        blackCompanyListDto.setCompanyCreationDate(authenticationScreens.get(0).getCompanyCreationDate());
                        blackList.add(blackCompanyListDto);
                    }
                }
            }
        }
        //重点关注供应商更新供应商库
        for (BlackCompanyListDto companyListDto : focusList) {
            QlOpenUpdateWrapper wrapper = QlOpenWrappers.update("CompanyInfo")
                    .eq(AuthenticationScreen::getCompanyName, companyListDto.getCompanyName())
                    .eq(AuthenticationScreen::getLcCode, companyListDto.getTaxCode())
                    .set(AuthenticationScreen::getFocusFlag, YesOrNo.Y.toString());
            qlOpenClient.update(ContextPath.SUP, wrapper);
        }
        //黑名单供应商更新供应商库
        for (BlackCompanyListDto blackCompanyListDto : blackList) {
            QlOpenUpdateWrapper wrapper = QlOpenWrappers.update("CompanyInfo")
                    .eq(AuthenticationScreen::getCompanyName, blackCompanyListDto.getCompanyName())
                    .eq(AuthenticationScreen::getLcCode, blackCompanyListDto.getTaxCode())
                    .set(AuthenticationScreen::getIsBacklist, YesOrNo.Y.toString())
                    .set(AuthenticationScreen::getBacklistUpdatedBy, MDM_SYSADMIN)
                    .set(AuthenticationScreen::getBacklistUpdatedDate, date)
                    .set(AuthenticationScreen::getBlackListEffectiveDate, date);
            qlOpenClient.update(ContextPath.SUP, wrapper);
        }
        //新增黑名单管理表
        createBlacklist(blackList);
    }
    protected void createBlacklist(List<BlackCompanyListDto> blackCompanyInfo) {
        // 新增黑名单
        Black black = new Black();
        //组装数据
        black=createBlackMain(black);
        qlOpenClient.create(ContextPath.SUP,MqlType.BLACK, MeiQl.toListValue(Collections.singletonList(black), Record.class));
        //获取黑名单单号
        List<Black> blackList=qlOpenClient.query(ContextPath.SUP,
                QlOpenWrappers.query("Black").eq(Black::getBlackId, black.getBlackId()), Black.class);
        for (BlackCompanyListDto blackCompanyListDto : blackCompanyInfo) {
            BlackCompanyMqlDTO blackCompany = new BlackCompanyMqlDTO();
            BeanUtils.copyProperties(blackCompanyListDto,blackCompany);
            blackCompany.setBlackId(black.getBlackId());
            blackCompany.setCompanyName(blackCompanyListDto.getCompanyName());
            blackCompany.setSocialCreditCode(blackCompanyListDto.getTaxCode());
            blackCompany.setDataSource("MDM");
            blackCompany.setBlackCode(blackList.get(0).getBlackCode());
            blackCompany.setReason(blackCompanyListDto.getQuestion());
            blackCompany.setBlackCompanyId(IdGenrator.generate());
            qlOpenClient.create(ContextPath.SUP, MqlType.BLACKSUPPLIER, MeiQl.toListValue(Collections.singletonList(blackCompany), Record.class));
        }
    }
    protected Black createBlackMain(Black black) {
        black.setApproveStatus(ApproveStatusType.APPROVED.getValue());
        black.setIsAllowSourcing(YesOrNo.N.toString());
        black.setIsAllowCreateOrder(YesOrNo.N.toString());
        black.setIsAllowWarehousing(YesOrNo.N.toString());
        black.setIsAllowFinance(YesOrNo.N.toString());
        black.setIsAllowPayment(YesOrNo.N.toString());
        black.setEffectiveTime(new Date());
        black.setBlackId(IdGenrator.generate());
        return black;
    }
    protected void createSynInfolist(MdmBlackCompanyIntf mdmBlackCompanyIntf) {
        List<MdmBlackCompanyIntf> mdmBlackCompanyIntfList = new ArrayList<>();
        mdmBlackCompanyIntfList.add(mdmBlackCompanyIntf);
        qlService.create("MdmBlackCompanyIntf", mdmBlackCompanyIntfList);
    }
}
