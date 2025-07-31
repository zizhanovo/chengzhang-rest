package com.chengzhang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 成章写作编辑器主启动类
 * 
 * @author chengzhang
 * @since 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class ChengzhangApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChengzhangApplication.class, args);
    }

}