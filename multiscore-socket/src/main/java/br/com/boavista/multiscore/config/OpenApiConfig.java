package br.com.boavista.multiscore.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class OpenApiConfig implements WebMvcConfigurer {
    @Bean
    public OpenAPI customOpenAPI() {
       final License license = new License().name("Apache License Version 2.0").url("https://www.apache.org/licenses/LICENSE-2.0");
        return new OpenAPI().info(new Info()
                .title("Conversational Program")
                .version("1.0")
                .description("Conversational Programs for AS400/Mainframe")
                .license(license)
        );
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("api-docs.json")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
