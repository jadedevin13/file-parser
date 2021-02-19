package org.hicx.fileparser.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.hicx.fileparser.property.SimpleLocalFileTaskProperty;
import org.hicx.fileparser.type.FileTaskProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.io.IOException;
import java.util.Set;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties({SimpleLocalFileTaskProperty.class})
public class ConsoleApplication implements CommandLineRunner {

  @Autowired private FileTaskProducer fileTaskProducer;
  @Autowired private ConfigurableBeanFactory beanFactory;
  @Autowired private SimpleLocalFileTaskProperty simpleLocalFileTaskProperty;

  public static void main(String[] args) {
    SpringApplication.run(ConsoleApplication.class, args);
  }

  @Override
  public void run(String... args) {
    try {
      Set<String> supportedTypes = simpleLocalFileTaskProperty.getSupportedTypes();
      supportedTypes.forEach(
          supportedType -> {
            var cleanTopic = supportedType.replaceAll("/", "-");
            String topicName = "file-parser.simple-local-file." + cleanTopic + ".created";
            beanFactory.registerSingleton(topicName, new NewTopic(topicName, 10, (short) 1));
          });
      fileTaskProducer.produceFileTasks(simpleLocalFileTaskProperty.getSourcePath(), supportedTypes);
    } catch (IOException exc) {
      log.warn("File task producer exception", exc);
    } catch (InterruptedException exc) {
      log.warn("File watcher interrupted", exc);
    }
  }
}
