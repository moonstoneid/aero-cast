package com.moonstoneid.web3feed.common.config;

import org.springframework.context.annotation.Import;

@Import(value = {EthConfig.class, H2Config.class})
public class BaseAppConfig {

}
