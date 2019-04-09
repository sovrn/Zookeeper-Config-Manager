package com.sovrn;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;

public class ZookeeperConfigManagerApplication {
  private static final Logger LOG = LoggerFactory.getLogger(ZookeeperConfigManagerApplication.class);

  public static void main(String[] args) {

    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
    ctx.register(AppConfig.class);
    ctx.refresh();

    String zookeeper = ctx.getBean(String.class);

    ZKManager zkManager = new ZKManager(zookeeper);


    StringBuilder contentBuilder = new StringBuilder();
    String env = System.getenv("env");
    try (Stream<String> stream = Files.lines( Paths.get("src/main/resources/" + env + "-blackbird.properties"), StandardCharsets.UTF_8))
    {
      stream.forEach(s -> contentBuilder.append(s).append("\n"));
    }
    catch (IOException e)
    {
      LOG.error(e.getMessage());
    }
    String content = contentBuilder.toString();
    byte[] b = content.getBytes();


    try {
      zkManager.createOrUpdate("/blackbird", b);
    } catch (KeeperException | InterruptedException e) {
      LOG.error(e.getMessage());
    }
  }
}
