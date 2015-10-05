package config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author dbychkov
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "services")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
