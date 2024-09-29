package org.example.backpro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
public class BackProApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackProApplication.class, args);
    }

}
