package com.moonstoneid.web3feed.common.config;

import java.sql.SQLException;

import org.h2.tools.Server;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({H2ServerProperties.class})
public class H2Config {

    private int port;

    public int getPort() {
        return port;
    }

    // Start H2 Server as bean to allow multiple DB connections via TCP
    // Connect via JDBC URL: jdbc:h2:tcp://localhost:[port]/./database
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2Server() throws SQLException {
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", Integer.toString(port));
    }

}
