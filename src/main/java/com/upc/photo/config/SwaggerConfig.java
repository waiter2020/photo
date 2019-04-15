package com.upc.photo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Author: waiter
 * @Date: 2019/4/14 22:46
 * @Version 1.0
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {


    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
// ⾃自⾏行行修改为⾃自⼰己的包路路径
                .apis(RequestHandlerSelectors.basePackage("com.upc.photo.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("智能云相册")
                .description("智能云相册 API 1.0 操作⽂文档")
//服务条款⽹网址
                .termsOfServiceUrl("http://www.upc.pub/")
                .version("1.0")
                .contact(new Contact("无名码农", "http://www.upc.pub/", "1403976416@qq.com"))
                .build();
    }
}
