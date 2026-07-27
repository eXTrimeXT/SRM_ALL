package com.midea.cloud.srm.supcooperate.config;

import com.google.common.collect.Lists;
import com.midea.cloud.common.authentication.IPassTokenExtractor;
import com.midea.cloud.common.constants.PermitAllUrl;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.privilege.ResourceManager;
import com.midea.cloud.common.result.ResultCode;
import com.midea.cloud.component.security.CustomUserInfoTokenServices;
import com.midea.cloud.srm.feign.oauth.Oauth2Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
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
import java.util.Map;

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

    @Autowired
    private Oauth2Client oAuth2Client;

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
    public void configure(ResourceServerSecurityConfigurer resources)
            throws Exception {
        resources.expressionHandler(oAuth2WebSecurityExpressionHandler);
        resources.tokenExtractor(new IPassTokenExtractor(PermitAllUrl.permitAllUrl(acquirePermitResource())));

        resources.accessDeniedHandler((request, response, accessException) -> {
            throw new BaseException(ResultCode.SESSION_VALID_ERROR);
        });
        resources.authenticationEntryPoint((request, response, authException) -> {
            throw new BaseException(ResultCode.SESSION_VALID_ERROR);
        });
    }

    @Bean
    public CustomUserInfoTokenServices userInfoTokenServices() {
        CustomUserInfoTokenServices services = new CustomUserInfoTokenServices(
                this.clientId, new CustomUserInfoTokenServices.ITokenService() {
            @Override
            public Map<String, Object> getUserInfoByToken(String accessToken) {
                return oAuth2Client.getUserInfoByToken(accessToken);
            }
        });
        return services;
    }

    @Override
    public String[] acquirePermitResource() {
        List<String> permitUrls = Lists.newArrayList(
                "/sc-anon/**",
                "/registerService/**",
                // 以下代码来自pm模块的代码合并
                "/pr-anon/**",
                "/po-anon/**",
                "/pm-anon/**",
                "/ps-anon/**",
                "/pr/requirementHead/**",
                "/midea-common/build-info",
                "/api-ql/PrSouProjectPlanForBuyer/**",
                "/api-ql/PrSouRequirementPoolForBuyer/**",
                "/api-ql/PrSouRequirementCancelForBuyer/**",
                "/npm/pr/requirement/projectPlan/**",
                "/npm/pr/requirement/sou/**"
        );

        // 以下代码来自pm模块的代码合并

        // APS采购申请
        permitUrls.add("/purchase/aps/**");
        permitUrls.add("/api-ql-doc/**");
        permitUrls.add("/api-ql/**");
        permitUrls.add("/test/**");
        permitUrls.add("/jsoneditor/**");
        permitUrls.add("/treefy.js");

        return permitUrls.toArray(new String[0]);
    }

    @Override
    public String[] acquireForbidenResource() {
        throw new IllegalAccessError();
    }

}
