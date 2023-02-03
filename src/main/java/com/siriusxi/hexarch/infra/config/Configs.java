package com.siriusxi.hexarch.infra.config;

import com.siriusxi.hexarch.account.core.domain.Money;
import com.siriusxi.hexarch.account.core.usecase.MoneyTransferProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ConfigurationProperties.class)
public class Configs {

  /**
   * Adds a use-case-specific {@link MoneyTransferProperties} object to the application context. The properties
   * are read from the Spring-Boot-specific {@link ConfigurationProperties} object.
   */
  @Bean
  public MoneyTransferProperties moneyTransferProperties(ConfigurationProperties configurationProperties){
    return new MoneyTransferProperties(Money.of(configurationProperties.getTransferThreshold()));
  }

}
