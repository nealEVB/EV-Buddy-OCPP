package ocpp;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "ocpp")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class OcppApplication {
    public static void main(String[] args) {
        SpringApplication.run(OcppApplication.class, args);
    }
}