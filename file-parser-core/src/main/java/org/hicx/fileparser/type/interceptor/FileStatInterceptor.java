package org.hicx.fileparser.type.interceptor;

import java.util.Optional;

public interface FileStatInterceptor {
  Optional<Object> getResult();

  void processContent(String line);
}
