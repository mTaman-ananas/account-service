package com.siriusxi.hexarch.infra.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "hexarch.account")
public class ConfigProperties {

  private long transferThreshold = Long.MAX_VALUE;

}
