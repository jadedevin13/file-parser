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
public class WordCountFileStatInterceptor implements FileStatInterceptor {

  private Optional<Object> result = Optional.empty();
  private Pattern pattern = Pattern.compile("\\w+(-\\w+)*");

  @Override
  public void processContent(String line) {
    Matcher matcher = pattern.matcher(line);
    int count = 0;
    while (matcher.find()) {
      count++;
    }
    result = Optional.of(count + (int) result.orElse(0));
    log.info("Current processed word count of " + result.map(res -> Integer.toString((int) res)));
  }
}
