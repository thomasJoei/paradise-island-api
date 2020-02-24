package com.upgrade.paradise.island;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableSwagger2
@ComponentScan(basePackages = {"com.upgrade.paradise.island"})
public class ParadiseIslandApplication {

    public static void main(String[] args) throws Exception {
        // Set UTC TimeZone for the whole application + in memory H2 database (for tests)
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        new SpringApplication(ParadiseIslandApplication.class).run(args);
    }

}
