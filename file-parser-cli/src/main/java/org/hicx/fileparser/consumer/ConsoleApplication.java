package org.hicx.fileparser.consumer;

import lombok.extern.slf4j.Slf4j;
import org.hicx.fileparser.consumer.type.SimpleLocalFileTaskConsumer;
import org.hicx.fileparser.consumer.type.interceptor.DotCountFileStatInterceptor;
import org.hicx.fileparser.consumer.type.interceptor.ModeWordFileStatInterceptor;
import org.hicx.fileparser.consumer.type.interceptor.WordCountFileStatInterceptor;
import org.hicx.fileparser.property.SimpleLocalFileTaskProperty;
import org.hicx.fileparser.type.FileTaskConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.List;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties({SimpleLocalFileTaskProperty.class})
public class ConsoleApplication implements CommandLineRunner {

  @Autowired private SimpleLocalFileTaskProperty simpleLocalFileTaskProperty;

  public static void main(String[] args) {
    SpringApplication.run(ConsoleApplication.class, args);
  }

  @Override
  public void run(String... args) {}

  @KafkaListener(topics = "file-parser.simple-local-file.text-plain.created")
  public void processMessage(String path) {
    log.info("Processing  simple local file text plain; " + path);
    SimpleLocalFileTaskConsumer simpleLocalFileTaskConsumer = new SimpleLocalFileTaskConsumer();
    WordCountFileStatInterceptor wordCountInterceptor = new WordCountFileStatInterceptor();
    DotCountFileStatInterceptor dotCountInterceptor = new DotCountFileStatInterceptor();
    ModeWordFileStatInterceptor modeWordInterceptor = new ModeWordFileStatInterceptor();
    simpleLocalFileTaskConsumer.setFileStatInterceptors(
            List.of(wordCountInterceptor, dotCountInterceptor, modeWordInterceptor));
    simpleLocalFileTaskConsumer.consumeFile(path, simpleLocalFileTaskProperty.getDestinationPath());
  }
}
