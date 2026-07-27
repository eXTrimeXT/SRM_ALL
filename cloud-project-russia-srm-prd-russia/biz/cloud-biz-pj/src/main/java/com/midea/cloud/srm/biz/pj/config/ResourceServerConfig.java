package com.midea.cloud.srm.biz.pj.config;

import com.google.common.collect.Lists;
import com.midea.cloud.common.authentication.IPassTokenExtractor;
import com.midea.cloud.common.constants.PermitAllUrl;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.privilege.ResourceManager;
import com.midea.cloud.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 资源服务配置
 *
 * @author artifact
 */
@EnableResourceServer
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Slf4j
public class ResourceServerConfig extends ResourceServerConfigurerAdapter implements ResourceManager {

    @Value("${security.oauth2.resource.clientId:unknow}")
    private String clientId;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionAuthenticationStrategy(new SessionFixationProtectionStrategy())
                .and().csrf().disable().exceptionHandling()
                .authenticationEntryPoint(
                        (request, response, authException) -> {
                            authErrorHandler(request);
                        })
                .accessDeniedHandler(
                        (request, response, accessException) -> {
                            authErrorHandler(request);
                        })
                .and().authorizeRequests()
                .antMatchers(PermitAllUrl.permitAllUrl(acquirePermitResource()))
                .permitAll().anyRequest().access("@operatingAuthorizationHandler.auth(authentication, request)").and().httpBasic().disable();
        http.headers().frameOptions().sameOrigin();
    }

    private void authErrorHandler(HttpServletRequest request) {
        log.error("当前操作人没有此功能【{}】的权限，请联系管理员在【角色维护】页面授权", request.getRequestURI());
        throw new BaseException(ResultCode.NEED_PERMISSION, "当前操作人没有此功能【" + request.getRequestURI() + "】的权限，请联系管理员在【角色维护】页面授权");
    }

    @Autowired
    private OAuth2WebSecurityExpressionHandler oAuth2WebSecurityExpressionHandler;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources){
        resources.tokenExtractor(new IPassTokenExtractor());
        resources.expressionHandler(oAuth2WebSecurityExpressionHandler);

        resources.accessDeniedHandler((request, response, accessException) -> {
            throw new BaseException(ResultCode.SESSION_VALID_ERROR);
        });
        resources.authenticationEntryPoint((request, response, authException) -> {
            throw new BaseException(ResultCode.SESSION_VALID_ERROR);
        });
    }

    @Override
    public String[] acquirePermitResource() {
        List<String> permitUrls = Lists.newArrayList(
                "/bpmFlow/**",
                "/external/**",
                "/openClientTest/**",
                "/pj-anon/**",
                "/register/**",
                "/erp/vendor/**",
                "/info/vendorInformation/**",
                "/dim/dim/listOrder",
                "/registerService/**",
                "/info/siteInfo/**",
                /*放开拦截，执行加拦截实现授权 */
                "/buyer/sou/end/**",
                /*MeiQL相关 */
                "/api-ql-doc/**",
                "/test/**",
                "/sou/**",
                "/jsoneditor/**",
                "/treefy.js",
                "/files-anon/**",
                 "/template/**",
                 "/bpmFlow/**"
        );
        return permitUrls.toArray(new String[0]);
    }

    @Override
    public String[] acquireForbidenResource() {
        throw new IllegalAccessError();
    }

}
