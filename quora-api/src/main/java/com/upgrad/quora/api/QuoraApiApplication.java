package com.upgrad.quora.api;

import com.upgrad.quora.service.ServiceConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * A Configuration class that can declare one or more @Bean methods and trigger auto-configuration and component scanning.
 * This class launches a Spring Application from Java main method.
 */
@SpringBootApplication
//@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@Import(ServiceConfiguration.class)
public class QuoraApiApplication {
    public static void main(String[] args) {

        //Sample code Check in Test for Development Branch of Quora Application
        SpringApplication.run(QuoraApiApplication.class, args);
		//Testing the git pull
    }
}

