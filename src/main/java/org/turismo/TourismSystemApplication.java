package org.turismo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableCaching
@EnableJpaRepositories(basePackages = "com.tourism.repository")
@EnableMongoRepositories(basePackages = "com.tourism.repository")
public class TourismSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(TourismSystemApplication.class, args);
    }
}