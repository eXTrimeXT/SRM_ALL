package com.midea.cloud.srm.biz.pj.sou.sourcing.signup.service.impl;

import com.github.pagehelper.page.PageMethod;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.comp.init.dao.CompSouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.controller.service.SouControlEventService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouFileDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouVendorDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.select.dao.SouSelectMapper;
import com.midea.cloud.srm.biz.pj.sou.sourcing.signup.dao.SouSignUpFileDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.signup.service.SouSignUpQueryService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.signup.ApiSouSignUpJudgeHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.signup.ApiSouSignUpQueryHandler;
import com.midea.cloud.srm.feign.supplier.SupplierClient;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouProject;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.select.ApiSouSelectItemQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.ApiSouSelectQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.signup.ApiSouSignUpQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.signup.ApiSouSignUpQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.signup.ApiSouSignUpVendorVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouSignUpFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouFileTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.quotationSatausStatusEnum;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 项目式询价 - 报名查询服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/23
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class SouSignUpQueryServiceImpl implements SouSignUpQueryService {

    @Autowired
    private SouFileDAOImpl souFileDao;
    @Autowired
    private SouSignUpFileDAOImpl souSignUpFileDao;
    @Autowired
    private SouVendorDAOImpl souVendorDao;
    @Autowired
    private SouControlEventService souControlEventService;
    @Autowired
    private SupplierClient supplierClient;
    @Autowired
    private CompSouProjectDAOImpl compSouProjectDao;

    @Autowired
    private SouSelectMapper souSelectMapper;

    /**
     * 查询供应商报名信息
     *
     * @param queryParam 查询条件
     * @param souType    寻源类型{@link SouTypeEnum}
     */
    @Override
    public List<ApiSouSignUpQueryVO> listVendorSignUp(ApiSouSignUpQueryDTO queryParam, String souType) {
        /* 0: 刷新数据 */
        if (queryParam.getProjectId() != null) {
            souControlEventService.refreshProjectBySouTime(queryParam.getProjectId());
        }
        /* 1: 入参格式化 */
        queryParam.formatParams();
        /* 3: 查询数据 */
        if (queryParam.getPageNum() != null && queryParam.getPageSize() != null) {
            PageMethod.startPage(queryParam.getPageNum(), queryParam.getPageSize());
        }
        List<SouVendor> vendorList = souVendorDao.lambdaQuery()
                /* 寻源单ID */
                .eq(SouVendor::getProjectId, queryParam.getProjectId())
                /* 供应商ID */
                .eq(queryParam.getVendorId() != null, SouVendor::getVendorId, queryParam.getVendorId())
                /* 供应商编码 */
                .like(queryParam.getVendorCode() != null, SouVendor::getVendorCode, queryParam.getVendorCode())
                /* 供应商名称 */
                .like(queryParam.getVendorName() != null, SouVendor::getVendorName, queryParam.getVendorName())
                /* 报名状态 */
                .eq(queryParam.getSignUpStatus() != null, SouVendor::getSignUpStatus, queryParam.getSignUpStatus())
                /* 加入轮次 */
                .eq(queryParam.getJoinRound() != null, SouVendor::getJoinRound, queryParam.getJoinRound())
                .list();
        /* 4: 组装数据 */
        List<ApiSouSignUpQueryVO> voList = ApiSouSignUpQueryVO.convertApiVO(vendorList);
        /* 竞价项目id */
        long  projectId = queryParam.getProjectId();
        for(int i = 0 ; i < voList.size(); i ++){
            /* 供应商id */
               long vendorId = voList.get(i).getVendorId();
            /* 根据竞价项目id和供应商id去查询报价明细表，看是否有报价 */
            ApiSouSelectQueryDTO query = new ApiSouSelectQueryDTO();
            query.setProjectId(projectId);
            query.setVendorId(vendorId);
            List<ApiSouSelectItemQueryVO> souOrderItemList = souSelectMapper.querySelectList(query);
            if(souOrderItemList.size()> 0){
                voList.get(i).setQuotationSataus(quotationSatausStatusEnum.QUOTATION_STATUS_DONE);
            }else{
                voList.get(i).setQuotationSataus(quotationSatausStatusEnum.NO_QUOTATION_STATUS);
            }

        }
        /* 5: 行业包额外处理(后置) */
        return SouActiveBeanUtils.getActiveBean(souType, ApiSouSignUpQueryHandler.class).doHandlerAfterListVendorSignUp(queryParam, souType, voList);
    }

    /**
     * 查询供应商报名详情
     *
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param vendorId  供应商ID
     * @param souType   寻源类型{@link SouTypeEnum}
     */
    @Override
    public ApiSouSignUpVendorVO getVendorSignUpDetail(long projectId, long vendorId, String souType) {
        /* 0: 刷新数据 */
        souControlEventService.refreshProjectBySouTime(projectId);
        /* 1: 校验操作条件/权限 */
        SouVendor vendor = SouActiveBeanUtils.getActiveBean(souType, ApiSouSignUpJudgeHandler.class).judgeGetVendorSignUpDetailAuth(projectId, vendorId, souType);
        /* 2: 查询数据 */
        /* 2.1: 查询报名附件要求 */
        List<SouFile> outerFileList = souFileDao.lambdaQuery()
                .eq(SouFile::getProjectId, projectId)
                .eq(SouFile::getFileType, SouFileTypeEnum.OUTER)
                .orderByAsc(SouFile::getSortIndex)
                .list();
        /* 2.2: 查询 供应商报名附件 */
        List<SouSignUpFile> signUpFileList = souSignUpFileDao.lambdaQuery()
                .eq(SouSignUpFile::getProjectId, projectId)
                .eq(SouSignUpFile::getVendorId, vendorId)
                .list();

        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        String createUser = loginAppUser.getUsername();

            /* 2.3: 查询 保证金附件  供应商报名时查询 是共用一个接口不 */
        List<SouFile> bondFileList  = souFileDao.lambdaQuery()
                    .eq(SouFile::getProjectId, projectId)
                    .eq(SouFile::getFileType, SouFileTypeEnum.BOND)
                /* 各自只能看到自己上传的附件 */
                    .eq(SouFile::getSouVendorId, vendorId)
                    .list();


        /* 2.4: 查询供应商信息 */
        CompanyInfo companyInfo = supplierClient.getCompanyInfo(vendorId);
        AssertUtils.notNull(companyInfo, LocaleHandler.getLocaleMsg("找不到公司信息") + "[{0}]", vendorId);
        /* 查询保证金信息 */
        CompSouProject compSouProject = compSouProjectDao.getById(projectId);
        /* 4: 组装数据 */
        ApiSouSignUpVendorVO vo = ApiSouSignUpVendorVO.convertApiVO(vendor, compSouProject, outerFileList, bondFileList, signUpFileList, companyInfo);
        /* 5: 行业包额外处理(后置) */
        return SouActiveBeanUtils.getActiveBean(souType, ApiSouSignUpQueryHandler.class).doHandlerAfterGetVendorSignUpDetail(projectId, vendorId, souType, vo);
    }



    /**
     * 根据projectId查看招标资料
     * **/
    @Override
    public  List<SouFile> getSignOuter(long projectId) {
        List<SouFile> outerFileList = souFileDao.lambdaQuery()
                .eq(SouFile::getProjectId, projectId)
                .eq(SouFile::getFileType, SouFileTypeEnum.OUTER)
                .list();
        return outerFileList;
    }

}
