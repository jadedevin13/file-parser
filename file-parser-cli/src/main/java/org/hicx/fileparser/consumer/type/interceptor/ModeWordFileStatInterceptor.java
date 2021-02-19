package org.hicx.fileparser.consumer.type.interceptor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hicx.fileparser.type.interceptor.FileStatInterceptor;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Slf4j
public class ModeWordFileStatInterceptor implements FileStatInterceptor {

  private Optional<Object> result = Optional.empty();
  private Map<String, Integer> wordCount = new ConcurrentHashMap<>();
  private Pattern pattern = Pattern.compile("\\w+(-\\w+)*");

  @Override
  public void processContent(String line) {
    Matcher matcher = pattern.matcher(line);
    while (matcher.find()) {
      String word = matcher.group();
      wordCount.putIfAbsent(word, 0);
      wordCount.put(word, wordCount.get(word) + 1);
    }
    result = Optional.of(wordCount);
    log.info("Current processed word mode count of " + wordCount);
  }
}
