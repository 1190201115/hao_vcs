package com.cyh.hao_vcs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement//开启事务注解
public class HaoVcsApplication {

    public static void main(String[] args) {
        SpringApplication.run(HaoVcsApplication.class, args);
    }

}
