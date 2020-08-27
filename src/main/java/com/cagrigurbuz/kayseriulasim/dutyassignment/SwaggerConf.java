package com.cagrigurbuz.kayseriulasim.dutyassignment;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConf {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.cagrigurbuz.kayseriulasim.dutyassignment"))
                .build()
                .apiInfo(info());
    }
    
    private ApiInfo info() {

        ApiInfo apiInfo = new ApiInfo(
                "Duty assignment web application",
                "Assign the given duties to the employees.",
                "v1",
                "kayseriulasim.cagrigurbuz.com", 
                new Contact("Çağrı Gürbüz", "https://cagrigurbuz.com", "ulasim@cagrigurbuz.com"),
                "Apache License Version 2.0",
                "https://www.apache.org/licence.html"
        );

        return apiInfo;
    }
}