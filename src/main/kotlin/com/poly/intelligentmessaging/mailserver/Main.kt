package com.poly.intelligentmessaging.mailserver

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import springfox.documentation.swagger2.annotations.EnableSwagger2




@SpringBootApplication
@EnableScheduling
@EnableSwagger2
class Application

fun main() {
    SpringApplication.run(Application::class.java)
}


