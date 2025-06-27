package org.example;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

@Slf4j
@SpringBootApplication
@MapperScan("org.example.mapper")
public class DemoApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext cac = SpringApplication.run(DemoApplication.class, args);
        ConfigurableEnvironment configEnv = cac.getEnvironment();
        log.info("项目启动");
        log.info("访问地址\thttp://127.0.0.1:{}",configEnv.getProperty("server.port","8080"));
        log.info("接口地址\thttp://127.0.0.1:{}/swagger-ui/index.html",configEnv.getProperty("server.port","8080"));
    }
}