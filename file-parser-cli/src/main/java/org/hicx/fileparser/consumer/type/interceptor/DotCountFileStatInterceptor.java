package org.hicx.fileparser.consumer.type.interceptor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hicx.fileparser.type.interceptor.FileStatInterceptor;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Getter
@Slf4j
public class DotCountFileStatInterceptor implements FileStatInterceptor {

  private Optional<Object> result = Optional.empty();
  private Pattern pattern = Pattern.compile("\\.");

  @Override
  public void processContent(String line) {
    Matcher matcher = pattern.matcher(line);
    int count = 0;
    while (matcher.find()) {
      count++;
    }
    result = Optional.of(count + (int) result.orElse(0));
    log.info("Current processed dot count of " + result.map(res -> ((int) res)));
  }
}
