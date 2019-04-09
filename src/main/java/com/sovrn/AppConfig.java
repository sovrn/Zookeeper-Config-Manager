package com.sovrn;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource({"classpath:dev-zookeeper.properties"})
public class AppConfig {
  @Value("${zookeeper.connect.url}")
  private String zookeeper;
  @Bean
  public String getZookeeper() {
    return zookeeper;
  }
  @Bean
  public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }
}