package com.qixiang.qixiang_system_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class QixiangSystemApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(QixiangSystemApiApplication.class, args);
	}

}
