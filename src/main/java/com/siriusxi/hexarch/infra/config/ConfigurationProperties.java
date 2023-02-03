package com.siriusxi.hexarch.infra.config;

import lombok.Data;

@Data
@org.springframework.boot.context.properties.ConfigurationProperties(prefix = "hexarch.account")
public class ConfigurationProperties {

  private long transferThreshold = Long.MAX_VALUE;

}
