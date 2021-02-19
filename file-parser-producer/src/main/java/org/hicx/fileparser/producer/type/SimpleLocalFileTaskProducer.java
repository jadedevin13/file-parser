package org.hicx.fileparser.producer.type;

import lombok.extern.slf4j.Slf4j;
import org.hicx.fileparser.type.FileTaskProducer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;

@Slf4j
@Component
public class SimpleLocalFileTaskProducer implements FileTaskProducer {

  private final KafkaTemplate<String, String> kafkaTemplate;

  public SimpleLocalFileTaskProducer(KafkaTemplate<String, String> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  @Override
  public void produceFileTasks(String source, Set<String> supportedTypes)
      throws IOException, InterruptedException {
    Path sourcePath = Paths.get(source);

    Files.list(sourcePath)
        .parallel()
        .filter(path -> fileSupportedTypes(path, supportedTypes))
        .forEach(this::queueTask);

    FileSystem sourceFileSystem = sourcePath.getFileSystem();
    WatchService watchService = sourceFileSystem.newWatchService();
    WatchKey watchKey = sourcePath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

    while (watchKey.isValid()) {
      WatchKey take = watchService.take();
      take.pollEvents().parallelStream()
          .filter(watchEvent -> fileSupportedTypes(watchEvent, supportedTypes))
          .map(watchEvent -> (sourcePath.resolve((Path) watchEvent.context())))
          .forEach(this::queueTask);
      take.reset();
    }
  }

  private void queueTask(Path path) {
    try {
      var cleanTopic = Files.probeContentType(path).replaceAll("/", "-");
      kafkaTemplate.send(
          "file-parser.simple-local-file." + cleanTopic + ".created",
          path.toAbsolutePath().toString());
    } catch (IOException exc) {
      log.warn("Error occurred in sending event probe content type of " + path, exc);
    }
  }

  private boolean fileSupportedTypes(WatchEvent<?> watchEvent, Set<String> supportedTypes) {
    if (watchEvent.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
      Path contextPath = (Path) watchEvent.context();
      return fileSupportedTypes(contextPath, supportedTypes);
    }
    return false;
  }

  private boolean fileSupportedTypes(Path contextPath, Set<String> supportedTypes) {
    try {
      String contentType = Files.probeContentType(contextPath);
      if (contentType == null) {
        contentType = "unknown";
      }
      log.info(
          "File inside directory created with name "
              + contextPath
              + " and content type of "
              + contentType);
      return supportedTypes.contains(contentType);
    } catch (IOException exc) {
      log.warn("Error occurred during probe content type of " + contextPath, exc);
    }
    return false;
  }
}
