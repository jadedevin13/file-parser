package org.hicx.fileparser.consumer.type;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hicx.fileparser.type.FileTaskConsumer;
import org.hicx.fileparser.type.interceptor.FileStatInterceptor;
import org.springframework.scheduling.annotation.Async;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Stream;

@Slf4j
@Setter
public class SimpleLocalFileTaskConsumer implements FileTaskConsumer {

  private List<FileStatInterceptor> fileStatInterceptors = Collections.emptyList();

  @Async
  @Override
  public Future<Void> consumeFile(String source, String destination) {
    Path sourcePath = Paths.get(source);
    try (Stream<String> lines = Files.newBufferedReader(sourcePath).lines()) {
      lines.forEach(
          line -> fileStatInterceptors.parallelStream()
              .forEach(fileStatInterceptor -> fileStatInterceptor.processContent(line)));

      Files.move(
          sourcePath,
          sourcePath
              .getParent()
              .resolve(Paths.get(destination, sourcePath.getFileName().toString())),
          StandardCopyOption.REPLACE_EXISTING);

    } catch (FileNotFoundException exc) {
      log.warn("File is not found", exc);
    } catch (IOException exc) {
      log.warn("There was an error reading the file", exc);
    }
    return CompletableFuture.completedFuture(null);
  }
}
