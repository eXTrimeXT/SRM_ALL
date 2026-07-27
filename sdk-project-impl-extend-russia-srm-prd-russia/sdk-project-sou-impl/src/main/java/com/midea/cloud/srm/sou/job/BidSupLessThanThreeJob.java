package com.midea.cloud.srm.sou.job;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.common.result.ResultCode;
import com.midea.cloud.quartz.bind.Job;
import com.midea.cloud.quartz.handler.ExecuteableJob;
import com.midea.cloud.srm.feign.PjSouClient;
import com.midea.cloud.srm.model.sou.approve.entity.SouApproveUser;
import com.midea.cloud.srm.model.sou.enums.ExtOrderTypeEnum;
import com.midea.cloud.srm.model.sou.enums.SouBidProccessEnum;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouProjectControlDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrder;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.sou.approve.service.ISouApproveUserService;
import com.midea.cloud.srm.sou.sourcing.init.service.ExtSouInitQueryService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author ex_liuxy46
 */
@Job("bidSupLessThanThreeJob")
@Slf4j
public class BidSupLessThanThreeJob implements ExecuteableJob {

    @Resource
    private PjSouClient pjSouClient;

    @Resource
    private IExtSouProjectService projectService;

    @Resource
    private IExtSouOrderService orderService;

    @Resource
    private ISouApproveUserService approveUserService;

    @Resource
    private ExtSouInitQueryService extSouInitQueryService;


    /**
     * 执行
     * @param params 参数
     * @return 返回
     */
    @Override
    public BaseResult executeJob(Map<String, String> params) {
        int one = 1;
        int three = 3;
        int sixTeen = 16;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<ExtSouProject> allList = new ArrayList<>(sixTeen);
        LambdaQueryWrapper<ExtSouProject> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouProject::getCurrentRound, one);
        //标准招标
        queryWrapper.eq(ExtSouProject::getExtSouProcess, SouBidProccessEnum.STANDARD.getCode());
        //技术投标中(TECH_BID)、技术标已截止(TECH_BID_END)
        queryWrapper.in(ExtSouProject::getProjectStatus, SouBiddingProStatusEnum.TECH_BID.getCode(), SouBiddingProStatusEnum.TECH_BID_END.getCode());
        List<ExtSouProject> souProject = projectService.list(queryWrapper);
        log.info("souProject==={}", JSONObject.toJSONString(souProject));
        souProject.forEach(e -> {
            LambdaQueryWrapper<ExtSouOrder> queryOrderWrapper = new LambdaQueryWrapper<>();
            queryOrderWrapper.eq(ExtSouOrder::getProjectId, e.getProjectId());
            queryOrderWrapper.eq(ExtSouOrder::getOrderStatus, SouOrderStatusEnum.SUBMISSION);
            queryOrderWrapper.eq(ExtSouOrder::getExtOrderType, ExtOrderTypeEnum.TECH.getCode());
            long orderList = orderService.count(queryOrderWrapper);
            if (orderList < three) {
                allList.add(e);
            }
        });
        LambdaQueryWrapper<ExtSouProject> otherQueryWrapper = new LambdaQueryWrapper<>();
        otherQueryWrapper.eq(ExtSouProject::getCurrentRound, one);
        //标准招标
        otherQueryWrapper.ne(ExtSouProject::getExtSouProcess, SouBidProccessEnum.STANDARD.getCode());
        //商务投标中(BUS_BID)，商务标已截止(BUS_BID_END)
        queryWrapper.in(ExtSouProject::getProjectStatus, SouBiddingProStatusEnum.BUS_BID.getCode(), SouBiddingProStatusEnum.BUS_BID_END.getCode());
        List<ExtSouProject> otherSouProject = projectService.list(otherQueryWrapper);
        log.info("otherSouProject==={}", JSONObject.toJSONString(otherSouProject));
        otherSouProject.forEach(e -> {
            LambdaQueryWrapper<ExtSouOrder> otherQueryOrderWrapper = new LambdaQueryWrapper<>();
            otherQueryOrderWrapper.eq(ExtSouOrder::getProjectId, e.getProjectId());
            otherQueryOrderWrapper.eq(ExtSouOrder::getOrderStatus, SouOrderStatusEnum.SUBMISSION);
            otherQueryOrderWrapper.eq(ExtSouOrder::getExtOrderType, ExtOrderTypeEnum.BUS.getCode());
            long orderList = orderService.count(otherQueryOrderWrapper);
            if (orderList < three) {
                allList.add(e);
            }
        });
        log.info("不足三家的数据==={}", JSONObject.toJSONString(allList));
        for (ExtSouProject e : allList) {
            ExtSouProjectControlDto extSouProjectControlDto = extSouInitQueryService.getProjectControl(e.getProjectId());
            Date endTime = extSouProjectControlDto.getOrderEndTime();
            Calendar calendar24 = Calendar.getInstance();
            calendar24.add(Calendar.HOUR_OF_DAY, 24);
            Date before24 = calendar24.getTime();
            Calendar calendar48 = Calendar.getInstance();
            calendar48.add(Calendar.HOUR_OF_DAY, 48);
            Date before48 = calendar48.getTime();
            if (endTime.before(before48) && endTime.after(before24)) {
                String msg = String.format("【%S】【%S】于【%S】投标截止，目前投标供应商不足三家，请知悉！", e.getExtProjectNo(), e.getSouName(), format.format(endTime));
                List<String> userList = new ArrayList<>(sixTeen);
                SouApproveUser approveUser = approveUserService.getNewestApproveUser(e.getProjectId());
                log.info("用户信息==={}==={}", JSONObject.toJSONString(approveUser), format.format(endTime));
                userList.add(approveUser.getUserName());
                pjSouClient.workNotices(msg, userList);
            }
        }
        return BaseResult.build(ResultCode.SUCCESS);
    }
}
