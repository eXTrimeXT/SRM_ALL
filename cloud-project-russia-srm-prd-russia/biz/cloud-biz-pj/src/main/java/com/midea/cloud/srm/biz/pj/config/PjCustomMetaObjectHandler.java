package com.midea.cloud.srm.biz.pj.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.midea.cloud.common.constants.SysConstant;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.IPUtil;
import com.midea.cloud.component.filter.HttpServletHolder;
import com.midea.cloud.component.handler.AutoMetaObjContext;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * <pre>
 *
 * </pre>
 *
 * @author zhangwn7@meicloud.com
 * @version 1.00.00
 * <p>
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Component
@Slf4j
@Primary
public class PjCustomMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        if (AutoMetaObjContext.getAndClean()) {
            log.debug("默认数据库规范字段自动插入");
            LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
            String username = loginAppUser == null ? SysConstant.System.SYSTEM_MANAGER : loginAppUser.getUsername();
            String nickname = loginAppUser == null ? SysConstant.System.SYSTEM_MANAGER : loginAppUser.getNickname();
            Long userId = loginAppUser == null ? SysConstant.System.SYSTEM_ID : loginAppUser.getUserId();
            String remoteIp = null;


            HttpServletRequest request = HttpServletHolder.getRequest();
            remoteIp = request == null ? "127.0.0.1" : IPUtil.getRemoteIpAddr(request);

            this.setFieldValByName("createdId", userId, metaObject);
            this.setFieldValByName("createdBy", username, metaObject);
            this.setFieldValByName("creationDate", new Date(), metaObject);
            this.setFieldValByName("createdByIp", remoteIp, metaObject);
            this.setFieldValByName("createdFullName", nickname, metaObject);
            this.setFieldValByName("lastUpdatedId", userId, metaObject);
            this.setFieldValByName("lastUpdatedBy", username, metaObject);
            this.setFieldValByName("lastUpdateDate", new Date(), metaObject);
            this.setFieldValByName("lastUpdatedByIp", remoteIp, metaObject);
            this.setFieldValByName("lastUpdatedFullName", nickname, metaObject);
            // 中台规范字段
            this.setFieldValByName("createdName", nickname, metaObject);
            this.setFieldValByName("lastUpdateBy", username, metaObject);
            this.setFieldValByName("lastUpdateName", nickname, metaObject);
        }

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (AutoMetaObjContext.getAndClean()) {
            log.debug("默认数据库规范字段自动更新");
            LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
            String username = loginAppUser == null ? SysConstant.System.SYSTEM_MANAGER : loginAppUser.getUsername();
            String nickname = loginAppUser == null ? SysConstant.System.SYSTEM_MANAGER : loginAppUser.getNickname();
            Long userId = loginAppUser == null ? SysConstant.System.SYSTEM_ID : loginAppUser.getUserId();
            String remoteIp = null;


            HttpServletRequest request = HttpServletHolder.getRequest();
            remoteIp = request == null ? "127.0.0.1" : IPUtil.getRemoteIpAddr(request);

            this.setFieldValByName("lastUpdatedId", userId, metaObject);
            this.setFieldValByName("lastUpdatedBy", username, metaObject);
            this.setFieldValByName("lastUpdateDate", new Date(), metaObject);
            this.setFieldValByName("lastUpdatedByIp", remoteIp, metaObject);
            this.setFieldValByName("lastUpdatedFullName", nickname, metaObject);
            // 中台规范字段
            this.setFieldValByName("lastUpdateBy", username, metaObject);
            this.setFieldValByName("lastUpdateName", nickname, metaObject);
        }

    }
}
