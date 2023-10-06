package com.moonstoneid.web3feed.common.config;

import java.sql.SQLException;

import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({H2ServerProperties.class})
@Slf4j
public class H2Config {

    // Start H2 Server as bean to allow multiple DB connections via TCP
    // Connect via JDBC URL: jdbc:h2:tcp://localhost:[port]/./[database-name]
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2Server(H2ServerProperties h2ServerProperties) throws SQLException {
        Integer port = h2ServerProperties.getPort();
        log.debug("Starting H2Server at port {}.", port);
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", Integer.toString(port));
    }

}
