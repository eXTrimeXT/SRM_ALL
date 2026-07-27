package com.midea.cloud.srm.sou.sourcing.spi.init;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Arrays;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class ApiExtSouWinLossNoticeBidJudgeHandler implements ISouSpiBean {

    @Autowired
    private IExtSouProjectService projectService;

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public ExtSouProject judgeWinLossNoticeBidAuth(Long projectId, String souType) {
        ExtSouProject souProject = projectService.getById(projectId);
        AssertUtils.notNull(souProject, "项目信息不存在！");

        AssertUtils.isFalse(Arrays.asList(SouBiddingProStatusEnum.NOTICE_ING.getCode(), SouBiddingProStatusEnum.ARCHIVE_TODO.getCode(), SouBiddingProStatusEnum.ARCHIVE_DONE.getCode()).contains(souProject.getProjectStatus()), "该招标单已发起中/落标通知，请前往中/落标通知处查看");

        return souProject;
    }

    @ApiOperation("项目信息保存前的额外处理")
    public void doHandlerBeforeWinLossNoticeBidBid(Long projectId, String souType) {
    }

    @ApiOperation("项目信息保存后的额外处理")
    public void doHandlerAfterWinLossNoticeBidBid(Long projectId, String souType, ExtSouProject po) {
    }

}
