package com.upc.photo.config;

import com.upc.photo.component.OptionsRequestFilter;
import com.upc.photo.handler.JsonLoginSuccessHandler;
import com.upc.photo.handler.JwtRefreshSuccessHandler;
import com.upc.photo.handler.TokenClearLogoutHandler;
import com.upc.photo.service.impl.JwtAuthenticationProvider;
import com.upc.photo.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * @Author: waiter
 * @Date: 2019/4/2 21:30
 * @Version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserService userService;


    public WebSecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                //静态资源访问无需认证
                .antMatchers("/user/add").permitAll()
                //admin开头的请求，需要admin权限
                .antMatchers("/admin/**").hasAnyRole("ADMIN")
                //需登陆才能访问的url
                .anyRequest().authenticated()  //默认其它的请求都需要认证，这里一定要添加
                .and()
                .csrf().disable()  //CRSF禁用，因为不使用session
                .sessionManagement().disable()  //禁用session
                .formLogin().disable() //禁用form登录
                .cors()  //支持跨域
                .and()   //添加header设置，支持跨域和ajax请求
//                .headers().addHeaderWriter(new StaticHeadersWriter(Arrays.asList(
//                new Header("Access-control-Allow-Origin","*"),
//                new Header("Access-Control-Expose-Headers","Authorization"))))
//                .and() //拦截OPTIONS请求，直接返回header
                .addFilterAfter(new OptionsRequestFilter(), CorsFilter.class)
                //添加登录filter
                .apply(new JsonLoginConfigurer<>()).loginSuccessHandler(new JsonLoginSuccessHandler(userService))
                .and()
                //添加token的filter
                .apply(new JwtLoginConfigurer<>()).tokenValidSuccessHandler(new JwtRefreshSuccessHandler(userService)).permissiveRequestUrls("/logout")
                .and()
                //使用默认的logoutFilter
                .logout()
                .logoutUrl("/logout")
                //logout时清除token
                .addLogoutHandler(new TokenClearLogoutHandler(userService))
                //logout成功后返回200
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
                .and()
                .sessionManagement().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider()).authenticationProvider(jwtAuthenticationProvider());
        auth.userDetailsService(userService).passwordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder());
    }


    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }



    @Bean("jwtAuthenticationProvider")
    protected AuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider(userService);
    }

    @Bean("daoAuthenticationProvider")
    protected AuthenticationProvider daoAuthenticationProvider() throws Exception{
        //这里会默认使用BCryptPasswordEncoder比对加密后的密码，注意要跟createUser时保持一致
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setUserDetailsService(userDetailsService());
        return daoProvider;
    }


    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST","HEAD", "OPTIONS","DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.addExposedHeader("Authorization");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}