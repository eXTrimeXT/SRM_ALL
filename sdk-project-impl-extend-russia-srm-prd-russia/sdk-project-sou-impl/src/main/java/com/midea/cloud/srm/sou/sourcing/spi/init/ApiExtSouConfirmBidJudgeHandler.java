package com.midea.cloud.srm.sou.sourcing.spi.init;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.srm.model.sou.ca.dto.CaSupplierDTO;
import com.midea.cloud.srm.model.sou.enums.SouBidMarginStatusEnum;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.enums.TypeEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouOrderDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouProjectDto;
import com.midea.cloud.srm.model.sou.req.SouInviteItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouMargin;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.bid.invite.service.ExtSouInviteService;
import com.midea.cloud.srm.sou.sourcing.init.service.ExtSouInitQueryService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class ApiExtSouConfirmBidJudgeHandler implements ISouSpiBean {

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private ExtSouInviteService extSouInviteService;

    @Autowired
    private ExtSouInitQueryService extSouInitQueryService;

    @Autowired
    private QlService qlService;

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public ExtSouProject judgeConfirmBidAuth(Long projectId, String souType) {
        ExtSouProject souProject = projectService.getById(projectId);
        AssertUtils.notNull(souProject, "项目信息不存在！");

        AssertUtils.isFalse(Arrays.asList(SouBiddingProStatusEnum.CONFIRM_BID.getCode(),
                SouBiddingProStatusEnum.WIN_LOSS_NOTICE.getCode(), SouBiddingProStatusEnum.NOTICE_ING.getCode(), SouBiddingProStatusEnum.ARCHIVE_TODO.getCode(), SouBiddingProStatusEnum.ARCHIVE_DONE.getCode()).contains(souProject.getProjectStatus()), "当前招标单已发起定标申请，请前往定标申请查看！");

        if (!SouBiddingProStatusEnum.BUS_BID_OPEN.getCode().equals(souProject.getProjectStatus())) {
            throw new BaseException("当前状态不允许发起定标申请！");
        }
        checkSouMargin(souProject);

        return souProject;
    }

    public void checkSouMargin(ExtSouProject souProject){
        //是否保证金为否，不用校验
        if("N".equals(souProject.getExtEarnestFlag())) {
            return;
        }
        //查询投标行表
        List<ApiExtSouOrderDto> orderItemList = extSouInitQueryService.getExtSouOrder(souProject.getProjectId());
        List<ExtSouMargin> souMarginList = extSouInitQueryService.getSouMargin(souProject.getProjectId());
        //已缴纳供应商集合
        Set<Long> marginSet = souMarginList.stream().filter(s -> SouBidMarginStatusEnum.PAY.getCode().equals(s.getMarginStatus()))
                .map(ExtSouMargin::getVendorId).collect(Collectors.toSet());
        StringBuilder sb = new StringBuilder();
        orderItemList.stream().filter(s -> SouOrderStatusEnum.SUBMISSION.equals(s.getOrderStatus()))
                .forEach(s -> {
                    if(!marginSet.contains(s.getVendorId())) {
                        sb.append(s.getVendorName()+"供应商还没缴纳保证金,不允许参与定标;");
                    }
                });
        if(sb.length() > 0) {
            throw new BaseException(sb.toString());
        }

    }

    @ApiOperation("项目信息保存前的额外处理")
    public void doHandlerBeforeConfirmBid(Long projectId, String souType) {
    }

    @ApiOperation("项目信息保存后的额外处理")
    public void doHandlerAfterConfirmBid(Long projectId, String souType, ExtSouProject po, ExtSouProjectDto projectDto) {
        //更新供应商报名得分
        if (!Objects.isNull(projectDto.getCaId())) {
            List<CaSupplierDTO> supplierDtos = qlService.query(TypeEnum.CaSupplier.getCode(), MeiQl.newCondition().eq(CaSupplierDTO::getCaId, projectDto.getCaId()), CaSupplierDTO.class);
            if (CollectionUtils.isNotEmpty(supplierDtos)) {
                List<SouInviteItem> souInviteItems = new ArrayList<>();
                Set<Long> vendorIdSet = new HashSet<>();
                supplierDtos.stream().forEach(supplier -> {
                    SouInviteItem souInviteItem = new SouInviteItem();
                    souInviteItem.setVendorId(supplier.getVendorId());
                    //技术得分
                    souInviteItem.setTechScore(supplier.getTechScore());
                    //综合得分
                    souInviteItem.setTotalScore(supplier.getCompositeScore());
                    if (!vendorIdSet.contains(souInviteItem.getVendorId())) {
                        vendorIdSet.add(souInviteItem.getVendorId());
                        souInviteItems.add(souInviteItem);
                    }

                });
                extSouInviteService.updateScoreBatch(projectDto, souInviteItems);
            }
        }
    }

}
