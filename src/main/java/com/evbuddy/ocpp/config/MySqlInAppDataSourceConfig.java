package com.evbuddy.ocpp.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@Profile("azure")   // <--- only active when profile=azure
public class MySqlInAppDataSourceConfig {

    @Bean
    public DataSource dataSource() {
        String conn = System.getenv("MYSQLCONNSTR_localdb");
        if (conn == null || conn.isEmpty()) {
            throw new IllegalStateException("MYSQLCONNSTR_localdb is not set. "
                    + "Enable MySQL In App or configure the connection string in App Service.");
        }

        // Example: "Database=localdb;Data Source=127.0.0.1:50732;User Id=azure;Password=6#vWHD_$"
        Map<String, String> parts = Arrays.stream(conn.split(";"))
                .map(s -> s.split("=", 2))
                .collect(Collectors.toMap(
                        a -> a[0].trim(),
                        a -> a[1].trim()
                ));

        String dataSource = parts.get("Data Source"); // "127.0.0.1:50732"
        String[] hostPort = dataSource.split(":");
        String host = hostPort[0];
        String port = hostPort[1];

        String db   = parts.get("Database");
        String user = parts.get("User Id");
        String pass = parts.get("Password");

        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + db
                + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(jdbcUrl);
        ds.setUsername(user);
        ds.setPassword(pass);
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return ds;
    }
}
