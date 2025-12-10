package org.turismo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableCaching
@EnableJpaRepositories(basePackages = "org.turismo.repository")
@EnableMongoRepositories(basePackages = "org.turismo.repository")
public class TourismSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(TourismSystemApplication.class, args);
    }
}