package com.midea.cloud.srm.rbac.job;

import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.quartz.bind.Job;
import com.midea.cloud.quartz.handler.ExecuteableJob;
import com.midea.cloud.srm.feign.RbacPjClient;
import com.midea.cloud.srm.feign.RbacSupClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.common.enums.UserType;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.rbac.user.service.ExtUserService;
import com.midea.cloud.srm.rbac.user.service.UserThirdService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author bs
 * 用户同步阳光诚信定时任务
 */
@Job("UserSissJob")
@Slf4j
public class UserSissJob implements ExecuteableJob {
    @Autowired
    private RbacClient rbacClient;
    @Autowired
    private RbacPjClient rbacPjClient;
    @Autowired
    private RbacSupClient rbacSupClient;
    @Autowired
    private UserThirdService userThirdService;

    @Autowired
    private ExtUserService extUserService;

    /**
     * @param params
     * @return
     */
    @Override
    public BaseResult executeJob(Map<String, String> params) {
        try {
            User u = new User();
            u.setUserType(UserType.VENDOR.name());
            u.setPageNum(1);
            u.setPageSize(30);

            List<User> pageUser = rbacClient.listByUser(u);;

            while (CollectionUtils.isNotEmpty(pageUser)) {
                if(u.getPageNum() != 1){
                    pageUser = rbacClient.listByUser(u);
                }
                u.setPageNum(u.getPageNum() + 1);
                extUserService.userPushSiss(pageUser);
            }
        } catch (Exception e) {
            return BaseResult.buildSuccess("用户同步siss定时任务-执行失败！");
        }
        return BaseResult.buildSuccess("用户同步siss定时任务-执行成功！");
    }







}
