package com.midea.cloud.srm.biz.pj.job;

import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.quartz.bind.Job;
import com.midea.cloud.quartz.handler.ExecuteableJob;
import com.midea.cloud.srm.biz.pj.changchengapi.eas.service.EasUserService;
import com.midea.cloud.srm.model.common.enums.UserType;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

/**
 * @author huangbf3
 * 用户同步EAS定时任务
 */
@Job("UserEasJob")
@Slf4j
public class UserEasJob implements ExecuteableJob {

    @Autowired
    private EasUserService easUserService;

    /**
     * @param params
     * @return
     */
    @Override
    public BaseResult executeJob(Map<String, String> params) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String str = params.get("selectDate");
            User u = new User();
            u.setUserType(UserType.BUYER.name());
            if (StringUtils.isNotBlank(str)) {
                LocalDate localDate = LocalDate.parse(str, formatter).minusDays(1);
                Date d = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                u.setLastUpdateDate(d);
            } else {
                LocalDate localDate = LocalDate.now().minusDays(1);
                Date d = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                u.setLastUpdateDate(d);
            }
            easUserService.pushRbacUser(u);
        } catch (Exception e) {
            return BaseResult.buildSuccess("用户同步EAS定时任务-执行失败！");
        }
        return BaseResult.buildSuccess("用户同步EAS定时任务-执行成功！");
    }
}
